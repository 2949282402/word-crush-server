package com.wordcrush.server.controller;

import com.wordcrush.server.common.api.ApiResponse;
import com.wordcrush.server.dto.request.LoginRequest;
import com.wordcrush.server.dto.request.RegisterRequest;
import com.wordcrush.server.dto.response.AvatarUploadResponse;
import com.wordcrush.server.dto.response.UserResponse;
import com.wordcrush.server.service.AvatarStorageService;
import com.wordcrush.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.TimeUnit;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "用户账号接口")
public class UserController {

    private final UserService userService;
    private final AvatarStorageService avatarStorageService;

    @PostMapping("/login")
    @Operation(summary = "登录")
    public ApiResponse<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("success", userService.login(request));
    }

    @GetMapping("/checkToken")
    @Operation(summary = "校验 token")
    public ApiResponse<UserResponse> checkToken(
            @RequestParam @NotBlank(message = "token must not be blank") String token) {
        return ApiResponse.success("success", userService.checkToken(token));
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("register success", userService.register(request));
    }

    @PostMapping("/changePassword")
    @Operation(summary = "修改密码")
    public ApiResponse<Void> changePassword(
            @RequestParam @NotBlank(message = "username must not be blank") String username,
            @RequestParam @NotBlank(message = "oldPassword must not be blank") String oldPassword,
            @RequestParam @NotBlank(message = "newPassword must not be blank") String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
        return ApiResponse.success("password changed");
    }

    @PostMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传头像")
    public ApiResponse<AvatarUploadResponse> uploadAvatar(
            @RequestParam @NotBlank(message = "username must not be blank") String username,
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = avatarStorageService.storeAvatar(username, file);
        return ApiResponse.success("avatar uploaded", new AvatarUploadResponse(
                username,
                avatarUrl,
                avatarStorageService.avatarVersion(username)
        ));
    }

    @GetMapping("/avatar/{username}")
    @Operation(summary = "获取头像")
    public ResponseEntity<Resource> getAvatar(@PathVariable String username) {
        Resource resource = avatarStorageService.loadAvatar(username);
        MediaType mediaType = MediaType.parseMediaType(avatarStorageService.detectContentType(username));
        long avatarVersion = avatarStorageService.avatarVersion(username);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .lastModified(avatarVersion)
                .contentType(mediaType)
                .body(resource);
    }
}
