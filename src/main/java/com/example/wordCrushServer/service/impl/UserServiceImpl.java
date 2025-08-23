package com.example.wordCrushServer.service.impl;

import com.example.wordCrushServer.entiy.User;
import com.example.wordCrushServer.mapper.UserMapper;
import com.example.wordCrushServer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }

    @Override
    public User getUserByName(String name) {
        return userMapper.getUserByName(name);
    }

    @Override
    public int addUser(User user) {
        return userMapper.insertUser(user);
    }
}
