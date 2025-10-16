package com.hanafuture.openbanking.domain.client.repository;

import com.hanafuture.openbanking.domain.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
} 