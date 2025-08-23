package com.example.wordCrushServer.controller;

import com.example.wordCrushServer.dto.ApiResponse;
import com.example.wordCrushServer.dto.LoginRequest;
import com.example.wordCrushServer.entiy.User;
import com.example.wordCrushServer.service.UserService;
import com.example.wordCrushServer.util.tokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String home() {
        return "Hello World";
    }

    @RequestMapping("/api/user/addUser")
    public String addUser() {
        User _user = userService.getUserByName("hejulian");
        if(_user != null) {
            return "已存在用户名：" + _user.getUsername();
        }
        User user = User.builder()
                .username("hejulian")
                .password("password")
                .build();
        int id = userService.addUser(user);
        return "用户插入成功，id为：" + id;
    }

    @RequestMapping("/api/user/findUserByName")
    public String findUserByName(@RequestParam String username) {
        User user = userService.getUserByName(username);
       return user == null ? "暂无此人" : user.toString();
    }

    @RequestMapping("/api/user/login")
    public ApiResponse<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        User user = userService.getUserByName(username);
        if (user == null) {
            return new ApiResponse<>(401,"用户名或密码错误！", null);
        }
        else if (!user.getPassword().equals(password)) {
            return new ApiResponse<>(401, "用户名或密码错误！", null);
        }
        else{
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("username", user.getUsername());
            userInfo.put("uid", String.valueOf(user.getId()));
            userInfo.put("token", tokenUtil.getToken(user));
            return new ApiResponse<>(200, "登录成功！", userInfo);
        }
    }

    @RequestMapping("/api/user/checkToken")
    public ApiResponse<Map<String, String>> checkToken( @RequestParam String token) {
        ApiResponse<Map<String, String>> checkResponse = tokenUtil.checkToken(token);
        if(checkResponse.getCode() == tokenUtil.TOKEN_VERITY_SUCCESS){
            User user = userService.getUserByName(checkResponse.getData().get("username"));
            System.out.println(user);
            if(user != null){
                System.out.println("检测：验证成功！");
                return new ApiResponse<>(200, "检测：验证成功！", checkResponse.getData());
            }
            else{
                System.out.println("检测：参数错误！");
                return new ApiResponse<>(401, "检测：参数错误！", null);
            }
        }
        else{
            System.out.println(checkResponse.getMsg());
            return new ApiResponse<>(401, checkResponse.getMsg(), null);
        }
    }
}
