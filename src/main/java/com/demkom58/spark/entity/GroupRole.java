package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GroupRole {
    NONE(
            "Guest",
            false, false, false, false,
            false, false, false, false,
            false, false
    ),
    READ(
            "Reader",
            true, false, false, false,
            false, false, false, false,
            false, false
    ),
    USER(
            "User",
            true, true, false, false,
            false, false, false, false,
            false, false
    ),
    MODERATOR(
            "Moderator",
            true, true, true, true,
            false, false, false, false,
            false, false
    ),
    SUPER_MODERATOR(
            "Super Moderator",
            true, true, true, true,
            true, false, true, true,
            true, false
    ),
    ADMIN(
            "Administrator",
            true, true, true, true,
            true, true, true, true,
            true, true
    );

    private final String name;

    private final boolean read;
    private final boolean upload;

    private final boolean uploadFree;
    private final boolean validate;

    private final boolean makeModer;
    private final boolean makeSuperModer;

    private final boolean shareCategory;
    private final boolean createCategory;
    private final boolean removeCategory;

    private final boolean removeGroup;
}
