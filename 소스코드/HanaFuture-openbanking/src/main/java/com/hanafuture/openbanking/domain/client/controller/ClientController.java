package com.hanafuture.openbanking.domain.client.controller;

import com.hanafuture.openbanking.domain.client.entity.Client;
import com.hanafuture.openbanking.domain.client.repository.ClientRepository;
import com.hanafuture.openbanking.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    
    private final ClientRepository clientRepository;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerClient() {
        try {
            // 하나Future 클라이언트 등록
            String clientSecret = "hanafuture_secret_2024";
            String hashedSecret = sha256Hash(clientSecret);
            
            Client client = Client.builder()
                    .clientId("hanafuture_client")
                    .clientSecret(hashedSecret)
                    .grantType("client_credentials")
                    .clientUseCode("HANAFUTURE_MAIN_001")
                    .build();
            
            // 기존 클라이언트가 있다면 삭제
            clientRepository.deleteById("hanafuture_client");
            
            // 새로 등록
            clientRepository.save(client);
            
            log.info("클라이언트 등록 완료: {}", client.getClientId());
            
            return ResponseEntity.ok(
                    ApiResponse.success("클라이언트가 성공적으로 등록되었습니다.", "hanafuture_client")
            );
            
        } catch (Exception e) {
            log.error("클라이언트 등록 실패", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("클라이언트 등록에 실패했습니다."));
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> listClients() {
        try {
            var clients = clientRepository.findAll();
            return ResponseEntity.ok(
                    ApiResponse.success("클라이언트 목록 조회 성공", clients)
            );
        } catch (Exception e) {
            log.error("클라이언트 목록 조회 실패", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("클라이언트 목록 조회에 실패했습니다."));
        }
    }
    
    private String sha256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}

