package com.hanaTI.HanaFuture.domain.welfare.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WelfareType {
    
    CENTRAL("중앙정부", "중앙부처에서 제공하는 복지 서비스"),
    LOCAL("지자체", "지방자치단체에서 제공하는 복지 서비스");
    
    private final String displayName;
    private final String description;
}
