package com.quant.user.interfaces.rest;

import com.quant.common.response.Result;
import com.quant.user.application.service.UserService;
import com.quant.user.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * User REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(Result::success)
                .orElse(Result.error(404, "User not found"));
    }

    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return Result.success(created);
    }

    @PutMapping
    public Result<User> updateUser(@RequestBody User user) {
        User updated = userService.updateUser(user);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
