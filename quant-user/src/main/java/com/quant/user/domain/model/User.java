package com.quant.user.domain.model;

import com.quant.common.domain.AggregateRoot;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User Aggregate Root
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends AggregateRoot {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String mobile;
    private String password;
    private UserStatus status;

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        LOCKED
    }
}
