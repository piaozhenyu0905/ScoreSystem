package com.system.assessment.mapper;

import com.system.assessment.pojo.Role;
import com.system.assessment.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDaoMapper {

    //提供根据用户名返回用户方法
    User loadUserByUsername(String username);

}
