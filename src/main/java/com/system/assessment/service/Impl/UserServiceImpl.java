package com.system.assessment.service.Impl;

import com.github.pagehelper.PageHelper;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.User;
import com.system.assessment.service.ExcelService;
import com.system.assessment.service.UserService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    public UserMapper userMapper;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public UserService userService;

    @Autowired
    public ExcelService excelService;

    @Override
    public ArrayList<String> deleteUsers(DeleteUserVO deleteUserVO) {
        ArrayList<String> errorList = new ArrayList<>();
        for(int index = 0; index < deleteUserVO.getIds().size(); index++){
            Integer id = deleteUserVO.getIds().get(index);
            try {
                userService.deleteUser(id);
            }catch (Exception e){
                User basicInfoBySelfId = findBasicInfoBySelfId(id);
                String name = basicInfoBySelfId.getName();
                errorList.add(name);
                log.error(name +"删除失败!");
            }
        }

        return errorList;
    }

    @Override
    public Integer findRole(Integer id) {
        return userMapper.findRole(id);
    }

    public Boolean RoleIsValued(String role){
        if(role == null || role.equals("")){
            return false;
        }
        if(!role.equals("超级管理员") && !role.equals("一级管理员") && !role.equals("二级管理员") && !role.equals("普通用户")){
            return false;
        }
        return true;
    };

    public Boolean LxyzIsValued(String lxyz){
        if(lxyz == null || lxyz.equals("")){
            return false;
        }
        if(!lxyz.equals("IP") && !lxyz.equals("LP") && !lxyz.equals("中坚") && !lxyz.equals("精英") && !lxyz.equals("成长")){
            return false;
        }
        return true;
    };

    public Boolean BusinessIsValued(String business){
        if(business == null || business.equals("")){
            return false;
        }
        if(!business.equals("研发") && !business.equals("非研发")){
            return false;
        }
        return true;
    };


    @Override
    public List<String> uploadFile(MultipartFile file) {
        try {
            // 解析 Excel 文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个工作表
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnIndexMap = new HashMap<>(); // 存储每个列名和它的索引

            // 动态获取表头的列索引
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                if (header.contains("\n")) {
                    // 使用 split 提取 \n 前的部分
                    String[] parts = header.split("\n");
                    columnIndexMap.put(parts[0].trim(), cell.getColumnIndex());
                } else {
                    // 如果没有 \n，直接添加原字符串
                    columnIndexMap.put(header, cell.getColumnIndex());
                }
            }

            //验证用户传入的表格是否符合用户导入的格式
            if(columnIndexMap.get("姓名") == null || columnIndexMap.get("系统角色") == null){
                log.error("用户导入的文件内容有误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入的文件内容有误!");
            }

            List<SupervisorVO> supervisorList = new ArrayList<>();
            List<String> errorList = new ArrayList<>();

            // 假设第一行为表头，跳过第一行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String name = getCellValue(row, columnIndexMap.get("姓名"));
                    try {
                        excelService.addUserExcel(row, columnIndexMap, supervisorList);
                    }catch (Exception e){
                        log.error(name + "导入失败!");
                        errorList.add(name);
                    }
                }
            }

            //进行主管id的导设置
            for (int index = 0; index < supervisorList.size(); index++ ){
                SupervisorVO supervisorVO = supervisorList.get(index);
                Integer id = supervisorVO.getId();

                String supervisorName1 = supervisorVO.getSupervisorName1();
                String supervisorName2 = supervisorVO.getSupervisorName2();
                String supervisorName3 = supervisorVO.getSupervisorName3();
                String hrName = supervisorVO.getHrName();
                String firstAdminName = supervisorVO.getFirstAdminName();
                String secondAdminName = supervisorVO.getSecondAdminName();
                String superAdminName = supervisorVO.getSuperAdminName();

                String supervisorWorkNum1 = supervisorVO.getSupervisorWorkNum1();
                String supervisorWorkNum2 = supervisorVO.getSupervisorWorkNum2();
                String supervisorWorkNum3 = supervisorVO.getSupervisorWorkNum3();
                String hrWorkNum = supervisorVO.getHrWorkNum();
                String firstAdminWorkNum = supervisorVO.getFirstAdminWorkNum();
                String secondAdminWorkNum = supervisorVO.getSecondAdminWorkNum();
                String superAdminWorkNum = supervisorVO.getSuperAdminWorkNum();


                Integer supervisor1 = userMapper.findIdByNameAndWorkNum(supervisorName1, supervisorWorkNum1);
                if(supervisor1 != null && supervisor1 != 0){
                    userMapper.updateSupervisor1ById(id, supervisor1);
                }

                Integer supervisor2 = userMapper.findIdByNameAndWorkNum(supervisorName2, supervisorWorkNum2);
                if(supervisor2 != null && supervisor2 != 0){
                    userMapper.updateSupervisor2ById(id, supervisor2);
                }

                Integer supervisor3 = userMapper.findIdByNameAndWorkNum(supervisorName3, supervisorWorkNum3);
                if(supervisor3 != null && supervisor3 != 0){
                    userMapper.updateSupervisor3ById(id, supervisor3);
                }

                Integer hr = userMapper.findIdByNameAndWorkNum(hrName, hrWorkNum);
                if(hr != null && hr != 0){
                    userMapper.updateHrById(id, hr);
                }

                Integer firstAdmin = userMapper.findIdByNameAndWorkNum(firstAdminName, firstAdminWorkNum);
                if(firstAdmin != null && firstAdmin != 0){
                    userMapper.updateFirstAdminById(id, firstAdmin);
                }

                Integer secondAdmin = userMapper.findIdByNameAndWorkNum(secondAdminName, secondAdminWorkNum);
                if(secondAdmin != null && secondAdmin != 0){
                    userMapper.updateSecondAdminById(id, secondAdmin);
                }

                Integer superAdmin = userMapper.findIdByNameAndWorkNum(superAdminName, superAdminWorkNum);
                if(superAdmin != null && superAdmin != 0){
                    userMapper.updateSuperAdminById(id, superAdmin);
                }

            }

            workbook.close();
            return errorList;

        } catch (IOException e) {
            log.error("用户导入数据有误!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入数据有误");
        }catch (IllegalStateException e){
            log.error("文件格式或内容不符合要求!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "文件格式或内容不符合要求");
        }

    }

    @Override
    public List<String> findDepartment() {
        return userMapper.findDepartment();
    }

    @Override
    public User findUserByName(String name) {
        return userMapper.findUserByName(name);
    }

    @Override
    @Transactional
    public Integer deleteUser(Integer userId) {
        Integer role = findRole(AuthenticationUtil.getUserId());
        if(!role.equals(Role.superAdmin.getCode())){
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "您暂无删除权限!");
        }

        //1.将跟该用户有关的从关系矩阵中删除
        relationshipMapper.deleteRelationshipById(userId);
        //2.若该用户是某人的主管或管理员，则删去这种关系
        userMapper.updateSupervisorAndAdmin(userId);
        //3.若该用户时某人的HRBP，则删除这种关系
        userMapper.updateHRBP(userId);
        return userMapper.deleteUser(userId);
    }

    @Override
    public List<UserVO> findBasicInfos(UserInfoSelectVO userInfoSelectVO) {
        if(userInfoSelectVO.getPageNum() != null && userInfoSelectVO.getPageSize() != null){
            PageHelper.startPage(userInfoSelectVO.getPageNum(), userInfoSelectVO.getPageSize());
        }
        return userMapper.findBasicInfos(userInfoSelectVO);
    }

    @Override
    public Integer updateUserInfo(UserVO user) {
        return userMapper.updateUserInfo(user);
    }

    @Override
    public Integer addUsersByExcel(List<User> users) {
        return userMapper.addUsersByExcel(users);
    }

    @Override
    public Integer addUser(User user) {
        return userMapper.addUser(user);
    }

    @Override
    public User findBasicInfoBySelfId(Integer userId) {
        return userMapper.findBasicInfoBySelfId(userId);
    }

    @Override
    public Integer updatePassword(PasswordUpdateVO passwordUpdateVO, Integer userId) {
        return userMapper.updatePassword(passwordUpdateVO.getNewPassword(), userId);
    }

    @Override
    public String findPassword(Integer userId) {
        return userMapper.findPassword(userId);
    }


    // 获取单元格的字符串值，并去除前后空格
    private String getCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null || row == null) return "";

        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";

        // 根据单元格类型处理返回值
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim(); // 返回字符串值
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // 如果是日期，返回日期字符串
                } else {
                    DecimalFormat df = new DecimalFormat("0.##########"); // 格式化数字，避免科学计数法
                    return df.format(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim(); // 优先尝试返回公式结果的字符串
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue()); // 如果失败，返回公式计算的数值结果
                }
            case BLANK:
                return ""; // 空白单元格返回空字符串
            default:
                return ""; // 未支持的类型返回空字符串
        }
    }

    // 获取单元格的 LocalDate 值，支持数值型和字符串型的日期格式
    private LocalDate getLocalDateValue(Row row, Integer columnIndex) {
        if (columnIndex == null) return null;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            // 数值类型直接读取 LocalDate
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            // 字符串类型解析
            String dateStr = cell.getStringCellValue().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        }
        return null;
    }

    // 将角色名称转换为中文描述
    public String En2Zn(String role) {
        switch (role) {
            case "超级管理员":
                return Role.superAdmin.getDescription();
            case "一级管理员":
                return Role.firstAdmin.getDescription();
            case "二级管理员":
                return Role.SecondAdmin.getDescription();
            default:
                return Role.normal.getDescription();
        }
    }


}
