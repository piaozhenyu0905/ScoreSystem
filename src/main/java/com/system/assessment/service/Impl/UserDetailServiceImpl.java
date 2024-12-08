package com.system.assessment.service.Impl;

import com.system.assessment.mapper.UserDaoMapper;
import com.system.assessment.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;

//自定义 UserDetailService 实现
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDaoMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(username);
        if (ObjectUtils.isEmpty(user)) throw new RuntimeException("用户名不存在!");
        user.setRole(user.getRole());
        return user;
    }
}
