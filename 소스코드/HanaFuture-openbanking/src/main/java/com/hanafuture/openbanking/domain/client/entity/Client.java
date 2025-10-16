package com.hanafuture.openbanking.domain.client.entity;

import com.hanafuture.openbanking.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "auth_client")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client extends BaseEntity {
    
    @Id
    @Column(name = "client_id", length = 255, nullable = false)
    private String clientId;
    
    @Column(name = "client_secret", length = 255, nullable = false)
    private String clientSecret;
    
    @Column(name = "grant_type", length = 255)
    private String grantType;
    
    @Column(name = "client_use_code", length = 256, nullable = false)
    private String clientUseCode;
} 