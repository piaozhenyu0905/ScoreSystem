package com.system.assessment.controller;

import com.github.pagehelper.PageInfo;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.User;
import com.system.assessment.service.UserService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")  // 统一为所有方法设置路径前缀
@Slf4j
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    public UserService userService;

    @ApiOperation("用户管理-查看个人信息")
    @RequestMapping(value = "/basicInfo",method = RequestMethod.GET)
    public ResponseResult findBasicInfoBySelfId(){
        Integer userId = AuthenticationUtil.getUserId();
        User userInfo = userService.findBasicInfoBySelfId(userId);
        return ResponseResult.success(userInfo);
    }

    @ApiOperation("用户管理-管理员查看用户信息")
    @RequestMapping(value = "/basicInfos",method = RequestMethod.POST)
    public ResponseResult findBasicInfos(@RequestBody UserInfoSelectVO userInfoSelectVO){

        List<UserVO> basicInfos = userService.findBasicInfos(userInfoSelectVO);
        PageInfo<UserVO> pageInfo = new PageInfo<>(basicInfos);
        long total = pageInfo.getTotal(); // 获取总记录数
        DataListResult dataListResult = new DataListResult<>(total, basicInfos);
        return ResponseResult.success(dataListResult);
    }

    @ApiOperation("用户管理-管理员批量删除用户")
    @DeleteMapping(value = "/delete/users")
    public ResponseResult deleteUsers(@RequestBody DeleteUserVO deleteUserVO){
        Integer selfId = AuthenticationUtil.getUserId();
        List<Integer> ids = deleteUserVO.getIds();
        if(ids == null || ids.size() == 0){
            return ResponseResult.error(401,"请选择要删除的用户!");
        }

        for(int index = 0; index < ids.size(); index++){
            Integer id = ids.get(index);
            if(selfId.equals(id)){
                return ResponseResult.error(401,"管理员无法删除自己!请重新选择要删除的用户!");
            }
        }

        ArrayList<String> errorList = userService.deleteUsers(deleteUserVO);
        if(errorList.size() == 0 ){
            return ResponseResult.success();
        }else {
            String message = "";
            for (int i = 0; i < errorList.size(); i++){
                String name = errorList.get(i);
                if(i != 0){
                    message = message + "、" + name;
                }else {
                    message = name;
                }

            }
            message = message + "删除失败!请重新尝试!";
            return ResponseResult.error(401,message);
        }

    }

    @ApiOperation("用户管理-管理员删除用户")
    @DeleteMapping(value = "/delete/user")
    public ResponseResult deleteUser(@RequestParam("id") Integer userId){
        Integer selfId = AuthenticationUtil.getUserId();
        if(selfId.equals(userId)){
            return ResponseResult.error(401,"管理员无法删除自己!");
        }

        Integer result = userService.deleteUser(userId);
        return ResponseResult.success();
    }


    @ApiOperation("返回所有部门")
    @RequestMapping(value = "/departments",method = RequestMethod.POST)
    public ResponseResult findDepartment(){

        List<String> departments = userService.findDepartment();
        return ResponseResult.success(departments);

    }

    @ApiOperation("用户管理-修改密码")
    @RequestMapping(value = "/update/password",method = RequestMethod.POST)
    public ResponseResult updatePassword(@RequestBody PasswordUpdateVO passwordUpdateVO){
        Integer userId = AuthenticationUtil.getUserId();
        String password = userService.findPassword(userId);
        if(!password.equals(passwordUpdateVO.getOldPassword())){
            return ResponseResult.error(401,"旧密码输入错误!");
        }
        Integer result = userService.updatePassword(passwordUpdateVO, userId);
        if(result == 1){
            return ResponseResult.success();
        }else {
            return ResponseResult.error(401,"密码修改失败!");
        }
    }

    @ApiOperation("用户管理-修改个人信息")
    @RequestMapping(value = "/update/selfInfo",method = RequestMethod.POST)
    public ResponseResult updateUserInfo(@RequestBody UserVO user){

        //修改的别人的
        if(!user.getId().equals(AuthenticationUtil.getUserId())){
            //先判断自身是否是超级管理员
            Integer role = userService.findRole(AuthenticationUtil.getUserId());
            if(!role.equals(Role.superAdmin.getCode())){
                return ResponseResult.error(401,"编辑失败,您不具有该权限!");
            }
            //如果自身是超级管理员就可修改
            Integer result = userService.updateUserInfo(user);
            if(result == 1){
                return ResponseResult.success();
            }else {
                return ResponseResult.error(401,"信息修改失败!");
            }
        }else {
            //如果修改的是自己的,先判断自身是否是超级管理员，如果是，不允许修改自己的角色
            Integer role = userService.findRole(AuthenticationUtil.getUserId());
            if(role.equals(Role.superAdmin.getCode())){
                //修改的是自己的权限，则不允许
                if(!user.getRole().equals(Role.superAdmin.getCode())){
                    return ResponseResult.error(401,"超级管理员不允许修改自己的角色，如需更改请联系数据库管理员!");
                }
            }
            Integer userId = AuthenticationUtil.getUserId();
            if(userId.equals(user.getSupervisor1()) || userId.equals(user.getSupervisor2()) || userId.equals(user.getSupervisor3())){
                return ResponseResult.error(401,"主管更新失败,用户与主管不能为同一人!");
            }
            if(userId.equals(user.getHr())){
                return ResponseResult.error(401,"HRBP更新失败,用户与HRBP不能为同一人!");
            }
            if(userId.equals(user.getFirstAdmin()) || userId.equals(user.getSecondAdmin()) || userId.equals(user.getSuperAdmin())){
                return ResponseResult.error(401,"管理员更新失败,用户与管理员不能为同一人!");
            }

            Integer result = userService.updateUserInfo(user);
            if(result == 1){
                return ResponseResult.success();
            }else {
                return ResponseResult.error(401,"信息修改失败!");
            }
        }

    }


    @ApiOperation("用户管理-导入用户")
    @PostMapping("/upload")
    public ResponseResult<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseResult.error(401, "该文件为空");
        }
        List<String> errorList = userService.uploadFile(file);
        if(errorList == null){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "用户信息表导入错误!");
        }else if(errorList.size() == 0){
            return ResponseResult.success();
        }else {
            String error = String.join(",", errorList) + "导入失败!";
            return ResponseResult.error(401, error);
        }
    }

}
