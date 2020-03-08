package org.example.test.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.User;
import org.example.test.mapper.UserMapper;
import org.example.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User queryUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public int paySuccessUpdateUser(User user) {
        return userMapper.updateById(user);
    }
}
