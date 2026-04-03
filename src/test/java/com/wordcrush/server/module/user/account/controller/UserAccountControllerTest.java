package com.wordcrush.server.module.user.account.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordcrush.server.config.SecurityConfig;
import com.wordcrush.server.module.user.account.dto.LoginRequest;
import com.wordcrush.server.module.user.account.dto.RegisterRequest;
import com.wordcrush.server.module.user.account.response.UserResponse;
import com.wordcrush.server.module.user.account.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserAccountController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnStandardLoginResponse() throws Exception {
        when(userService.login(any(LoginRequest.class)))
                .thenReturn(new UserResponse("admin", "1", "token-string"));

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("success"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.uid").value("1"));
    }

    @Test
    void shouldReturnRegisterResponse() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(new UserResponse("tom", "2", "new-token"));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("tom", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("register success"))
                .andExpect(jsonPath("$.data.token").value("new-token"));
    }

    @Test
    void shouldReturnCheckTokenResponse() throws Exception {
        when(userService.checkToken("token-string"))
                .thenReturn(new UserResponse("admin", "1", "token-string"));

        mockMvc.perform(get("/api/user/checkToken").param("token", "token-string"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }
}
