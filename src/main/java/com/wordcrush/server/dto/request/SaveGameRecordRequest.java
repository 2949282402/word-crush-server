package com.wordcrush.server.dto.request;

import java.util.List;

public record SaveGameRecordRequest(
        String username,
        Integer gameType,
        Integer score,
        String time,
        List<String> learnedWords
) {
}
