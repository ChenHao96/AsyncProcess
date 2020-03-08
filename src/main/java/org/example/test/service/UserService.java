package org.example.test.service;

import org.example.test.entity.User;

public interface UserService {

    User queryUserById(Integer userId);

    int paySuccessUpdateUser(User user);
}
