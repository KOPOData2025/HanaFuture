package com.hanafuture.openbanking.domain.user.service;

import com.hanafuture.openbanking.domain.account.entity.Account;
import com.hanafuture.openbanking.domain.account.repository.AccountRepository;
import com.hanafuture.openbanking.domain.bank.entity.BankCode;
import com.hanafuture.openbanking.domain.bank.repository.BankCodeRepository;
import com.hanafuture.openbanking.domain.user.dto.request.UserRegisterRequest;
import com.hanafuture.openbanking.domain.user.dto.response.UserRegisterResponse;
import com.hanafuture.openbanking.domain.user.dto.response.UserResponse;
import com.hanafuture.openbanking.domain.user.entity.User;
import com.hanafuture.openbanking.domain.user.repository.UserRepository;
import com.hanafuture.openbanking.global.util.UserGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BankCodeRepository bankCodeRepository;

    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest requestDTO){
        // 1. 사용자 조회 또는 신규 생성
        User user = userRepository.findByUserCi(requestDTO.getUserCi())
                .orElseGet(() -> {
                    User newUser = requestDTO.toEntity();
                    newUser.setUserSeqNo(UserGeneratorUtil.generateUserSeqNo());
                    return userRepository.save(newUser);
                });

        // 2. 은행 코드 조회
        BankCode bankCode = bankCodeRepository.findById(requestDTO.getBankCodeStd())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 은행 코드입니다."));

        // 3. 핀테크 이용번호 생성
        String fintechUseNum = UserGeneratorUtil.generateFintechUseNum();

        // 4. 계좌 등록
        Account account = Account.builder()
                .fintechUseNum(fintechUseNum)
                .user(user)
                .bankCode(bankCode)
                .accountNum(requestDTO.getRegisterAccountNum())
                .build();
        accountRepository.save(account);

        return new UserRegisterResponse(user.getUserSeqNo(), fintechUseNum, account.getAccountNum());
    }

    public UserResponse getUserByUserCi(String userCi) {
        User user = userRepository.findByUserCi(userCi).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return UserResponse.of(user);
    }

} 