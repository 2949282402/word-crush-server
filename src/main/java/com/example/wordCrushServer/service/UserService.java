package com.example.wordCrushServer.service;

import com.example.wordCrushServer.entiy.User;

public interface UserService {
    User getUserById(int id);
    User getUserByName(String name);
    int addUser(User user);
}
