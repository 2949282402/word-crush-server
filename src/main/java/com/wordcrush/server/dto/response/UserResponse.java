package com.wordcrush.server.dto.response;

public record UserResponse(
        String username,
        String uid,
        String token
) {
}
