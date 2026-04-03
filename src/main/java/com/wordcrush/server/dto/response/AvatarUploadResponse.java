package com.wordcrush.server.dto.response;

public record AvatarUploadResponse(
        String username,
        String avatarUrl,
        Long avatarVersion
) {
}
