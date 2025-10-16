package com.hana.hanabank.domain.user.repository;

import com.hana.hanabank.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByUserCi(String userCi);
    
    Optional<User> findByUserNum(String userNum);
    
    boolean existsByUserCi(String userCi);
    
    void deleteByUserCi(String userCi);
}
