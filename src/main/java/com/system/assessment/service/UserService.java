package com.system.assessment.service;

import com.system.assessment.pojo.User;
import com.system.assessment.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public interface UserService {

    public void updateNewUser(UserVO userVO);

    public ArrayList<String> deleteUsers(DeleteUserVO deleteUserVO);

    public Integer findRole(Integer id);

    public ImportVO uploadFile(MultipartFile file);

    public List<String> findDepartment();

    public User findUserByName(String name);

    public Integer deleteUser(Integer userId);

    public List<UserVO> findBasicInfos(UserInfoSelectVO userInfoSelectVO);

    public Integer updateUserInfo(UserVO user);

    public Integer addUsersByExcel(List<User> users);

    public Integer addUser(User user);

    public User findBasicInfoBySelfId(Integer userId);

    public Integer updatePassword(PasswordUpdateVO passwordUpdateVO, Integer userId);

    public String findPassword(@Param("userId")Integer userId);
}
