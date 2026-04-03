package com.wordcrush.server.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LegacyApiResponse<T>(String status, T message) {

    public static <T> LegacyApiResponse<T> success(T message) {
        return new LegacyApiResponse<>("success", message);
    }

    public static LegacyApiResponse<String> fail(String message) {
        return new LegacyApiResponse<>("fail", message);
    }
}
