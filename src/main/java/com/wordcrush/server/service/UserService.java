package com.wordcrush.server.service;

import com.wordcrush.server.common.exception.BusinessException;
import com.wordcrush.server.domain.entity.UserAccount;
import com.wordcrush.server.domain.repository.UserAccountRepository;
import com.wordcrush.server.dto.request.LoginRequest;
import com.wordcrush.server.dto.request.RegisterRequest;
import com.wordcrush.server.dto.response.UserResponse;
import com.wordcrush.server.security.TokenService;
import com.wordcrush.server.security.TokenSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        UserAccount user = userAccountRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(401, "invalid username or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "invalid username or password");
        }
        return toUserResponse(user, tokenService.issueToken(user));
    }

    @Transactional(readOnly = true)
    public UserResponse checkToken(String token) {
        TokenSession session = tokenService.requireValidSession(token);
        UserAccount user = userAccountRepository.findById(session.userId())
                .orElseThrow(() -> new BusinessException(404, "user not found"));
        return toUserResponse(user, session);
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.username().trim();
        if (userAccountRepository.existsByUsername(username)) {
            throw new BusinessException(409, "username already exists");
        }
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(1);
        userAccountRepository.save(user);
        return toUserResponse(user, tokenService.issueToken(user));
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "user not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException(400, "old password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        tokenService.revokeUserTokens(user.getId());
    }

    private UserResponse toUserResponse(UserAccount user, TokenSession session) {
        return new UserResponse(user.getUsername(), String.valueOf(user.getId()), session.token());
    }
}
