package com.hanafuture.openbanking.domain.account.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalAccountDetailRequest {
    private String userCi;
    private String accountNum;
} 