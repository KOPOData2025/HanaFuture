package com.hanaTI.HanaFuture.domain.groupaccount.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum GroupAccountRole {
    ADMIN("관리자", Set.of(
            GroupAccountPermission.VIEW_ACCOUNT,
            GroupAccountPermission.MANAGE_MEMBERS,
            GroupAccountPermission.MANAGE_SETTINGS,
            GroupAccountPermission.WITHDRAW_MONEY,
            GroupAccountPermission.VIEW_TRANSACTIONS,
            GroupAccountPermission.DELETE_ACCOUNT
    )),
    MANAGER("매니저", Set.of(
            GroupAccountPermission.VIEW_ACCOUNT,
            GroupAccountPermission.MANAGE_MEMBERS,
            GroupAccountPermission.VIEW_TRANSACTIONS,
            GroupAccountPermission.WITHDRAW_MONEY
    )),
    MEMBER("멤버", Set.of(
            GroupAccountPermission.VIEW_ACCOUNT,
            GroupAccountPermission.VIEW_TRANSACTIONS
    )),
    VIEWER("조회자", Set.of(
            GroupAccountPermission.VIEW_ACCOUNT
    ));

    private final String description;
    private final Set<GroupAccountPermission> permissions;

    public boolean hasPermission(GroupAccountPermission permission) {
        return permissions.contains(permission);
    }
}
