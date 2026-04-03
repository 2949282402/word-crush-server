package com.wordcrush.server.controller;

import com.wordcrush.server.common.api.LegacyApiResponse;
import com.wordcrush.server.common.exception.BusinessException;
import com.wordcrush.server.dto.request.DeleteGameRecordRequest;
import com.wordcrush.server.dto.request.RankingRequest;
import com.wordcrush.server.dto.request.SaveGameRecordRequest;
import com.wordcrush.server.dto.request.UsernameRequest;
import com.wordcrush.server.dto.response.GameRecordResponse;
import com.wordcrush.server.dto.response.RankingItemResponse;
import com.wordcrush.server.service.GameRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "GameRecord", description = "排行榜与游戏记录接口")
public class
GameRecordController {

    private final GameRecordService gameRecordService;

    @PostMapping("/getTopNRecord")
    @Operation(summary = "获取排行榜")
    public LegacyApiResponse<List<RankingItemResponse>> getTopNRecord(@RequestBody RankingRequest request) {
        return executeLegacy(() -> gameRecordService.getTopRankings(request));
    }

    @PostMapping("/addGameRecord")
    @Operation(summary = "新增游戏记录")
    public LegacyApiResponse<String> addGameRecord(@RequestBody SaveGameRecordRequest request) {
        return executeLegacy(() -> {
            gameRecordService.addGameRecord(request);
            return "ok";
        });
    }

    @PostMapping("/deleteGameRecord")
    @Operation(summary = "删除游戏记录")
    public LegacyApiResponse<String> deleteGameRecord(@RequestBody DeleteGameRecordRequest request) {
        return executeLegacy(() -> {
            gameRecordService.deleteGameRecord(request);
            return "deleted";
        });
    }

    @PostMapping("/getAllGameRecord")
    @Operation(summary = "获取用户全部游戏记录")
    public LegacyApiResponse<List<GameRecordResponse>> getAllGameRecord(@RequestBody UsernameRequest request) {
        return executeLegacy(() -> gameRecordService.getAllGameRecords(request));
    }

    private <T> LegacyApiResponse<T> executeLegacy(Supplier<T> supplier) {
        try {
            return LegacyApiResponse.success(supplier.get());
        } catch (BusinessException exception) {
            return failResponse(exception.getMessage());
        } catch (Exception exception) {
            log.error("Legacy endpoint error", exception);
            return failResponse("internal server error");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> LegacyApiResponse<T> failResponse(String message) {
        return (LegacyApiResponse<T>) LegacyApiResponse.fail(message);
    }
}
