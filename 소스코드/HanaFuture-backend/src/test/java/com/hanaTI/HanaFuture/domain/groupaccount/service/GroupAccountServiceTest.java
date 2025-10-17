package com.hanaTI.HanaFuture.domain.groupaccount.service;

import com.hanaTI.HanaFuture.domain.groupaccount.dto.request.GroupAccountRequest;
import com.hanaTI.HanaFuture.domain.groupaccount.dto.response.GroupAccountResponse;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccount;
import com.hanaTI.HanaFuture.domain.groupaccount.entity.GroupAccountPurpose;
import com.hanaTI.HanaFuture.domain.groupaccount.repository.GroupAccountRepository;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import com.hanaTI.HanaFuture.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("모임통장 서비스 테스트")
class GroupAccountServiceTest {

    @Mock
    private GroupAccountRepository groupAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupAccountService groupAccountService;

    private User testUser;
    private GroupAccount testGroupAccount;
    private GroupAccountRequest testRequest;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("테스트 사용자")
                .build();

        // 테스트 모임통장 생성
        testGroupAccount = GroupAccount.builder()
                .id(1L)
                .name("가족 여행 적금")
                .description("제주도 여행을 위한 모임통장")
                .purpose(GroupAccountPurpose.TRAVEL)
                .targetAmount(new BigDecimal("5000000"))
                .targetDate(LocalDate.now().plusMonths(12))
                .creator(testUser)
                .build();

        // 테스트 요청 생성
        testRequest = GroupAccountRequest.builder()
                .name("가족 여행 적금")
                .description("제주도 여행을 위한 모임통장")
                .purpose(GroupAccountPurpose.TRAVEL)
                .targetAmount(new BigDecimal("5000000"))
                .targetDate(LocalDate.now().plusMonths(12))
                .bankCode("081")
                .bankName("하나은행")
                .build();
    }

    @Test
    @DisplayName("모임통장 생성 성공")
    void createGroupAccount_Success() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(testUser));
        given(groupAccountRepository.save(any(GroupAccount.class))).willReturn(testGroupAccount);

        // when
        GroupAccountResponse response = groupAccountService.createGroupAccount(testRequest, testUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("가족 여행 적금");
        assertThat(response.getPurpose()).isEqualTo(GroupAccountPurpose.TRAVEL);
        assertThat(response.getTargetAmount()).isEqualTo(new BigDecimal("5000000"));

        verify(userRepository).findById(testUser.getId());
        verify(groupAccountRepository).save(any(GroupAccount.class));
    }

    @Test
    @DisplayName("모임통장 상세 조회 성공")
    void getGroupAccountDetail_Success() {
        // given
        given(groupAccountRepository.findById(anyLong())).willReturn(Optional.of(testGroupAccount));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(testUser));

        // when
        GroupAccountResponse response = groupAccountService.getGroupAccountDetail(testGroupAccount.getId(), testUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testGroupAccount.getId());
        assertThat(response.getName()).isEqualTo(testGroupAccount.getName());

        verify(groupAccountRepository).findById(testGroupAccount.getId());
        verify(userRepository).findById(testUser.getId());
    }
}
