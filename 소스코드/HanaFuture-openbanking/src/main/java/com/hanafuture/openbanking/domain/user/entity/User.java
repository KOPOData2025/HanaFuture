package com.hanafuture.openbanking.domain.user.entity;

import com.hanafuture.openbanking.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "auth_user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    
    @Id
    @Column(name = "user_seq_no", length = 50)
    private String userSeqNo;

    @Column(name = "user_ci", length = 255, nullable = false)
    private String userCi; // 주민등록번호 SHA-256 해시
    
    @Column(name = "user_name", length = 255, nullable = false)
    private String userName; // 사용자 이름
    
    @Column(name = "user_email", length = 255, nullable = false)
    private String userEmail; // 사용자 이메일
} 