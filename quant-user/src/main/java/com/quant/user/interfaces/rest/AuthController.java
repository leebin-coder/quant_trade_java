package com.quant.user.interfaces.rest;

import com.quant.common.response.Result;
import com.quant.user.application.dto.LoginRequest;
import com.quant.user.application.dto.LoginResponse;
import com.quant.user.application.dto.SendCodeRequest;
import com.quant.user.application.dto.SendCodeResponse;
import com.quant.user.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller
 */
@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Send verification code
     */
    @PostMapping("/send-code")
    public Result<SendCodeResponse> sendCode(@Valid @RequestBody SendCodeRequest request) {
        log.info("Send verification code request for phone: {}", request.getPhone());
        SendCodeResponse response = authService.sendVerificationCode(request);
        return Result.success(response);
    }

    /**
     * Login with verification code
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for phone: {}", request.getPhone());
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
}
