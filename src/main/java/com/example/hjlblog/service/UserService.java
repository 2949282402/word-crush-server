package com.example.hjlblog.service;

import com.example.hjlblog.entiy.User;

public interface UserService {
    User getUserById(int id);
    User getUserByName(String name);
    int addUser(User user);
}
