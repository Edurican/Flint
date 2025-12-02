package com.edurican.flint.core.support.error;

public enum ErrorCode {

    // Don't use (0 ~ 1000)
    E500,
    E400,

    // Common (1000)
    COMMON_1000,

    // User (2000)
    U2000,

    // Follow (5000)
    ALREADY_FOLLOWING,
    CANNOT_FOLLOW_SELF,
    NOT_FOLLOWING,

    // comment (6000)
    COMMENT_NOT_FOUND,
    INVALID_CONTENT,
    COMMENT_DEPTH_EXCEEDED,
}