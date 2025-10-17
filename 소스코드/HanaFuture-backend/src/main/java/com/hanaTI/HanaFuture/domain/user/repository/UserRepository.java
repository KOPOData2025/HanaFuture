package com.hanaTI.HanaFuture.domain.user.repository;

import com.hanaTI.HanaFuture.domain.user.entity.SocialProvider;
import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByRefreshToken(String refreshToken);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByProviderAndProviderId(SocialProvider provider, String providerId);
    
    boolean existsByProviderAndProviderId(SocialProvider provider, String providerId);
    
    /**
     * 이름과 전화번호로 사용자 검색
     */
    Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);
    
    /**
     * 이름으로 사용자 검색
     */
    Optional<User> findByName(String name);
}
