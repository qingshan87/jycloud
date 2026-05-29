package com.jycloud.user.controller;


import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.util.AddressUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final AddressUtil addressUtil;

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Object getUser(
            @Parameter(description = "用户ID", required = true, example = "1001")
            @PathVariable Long id) {
        return id;
    }


    @Operation(summary = "根据ip查询地址")
    @GetMapping("/ip")
    public ServerResponse test(@RequestParam String ip, HttpServletRequest request) {

        String region = addressUtil.getRegion(ip);

        return ServerResponse.successData(region);
    }
}
