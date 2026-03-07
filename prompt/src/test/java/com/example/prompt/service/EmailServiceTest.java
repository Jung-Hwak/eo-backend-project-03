package com.example.prompt.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    // EmailService Bean 확인
    @Test
    public void testExists() {
        assertNotNull(emailService);
        log.info("EmailService = {}", emailService);
    }

    // 인증번호 발송 테스트
    @Test
    public void testSendVerificationCode() {
        String email = "test@gmail.com";
        log.info("Before send : email = {}", email);

        assertDoesNotThrow(() -> emailService.sendVerificationCode(email));

        log.info("After send : verification code sent to {}", email);
    }

    // 인증번호 검증 성공 테스트
    @Test
    public void testVerifyCode_success() {
        String email = "verify@gmail.com";
        String code = emailService.sendVerificationCode(email);
        log.info("Sent code = {}", code);

        boolean result = emailService.verifyCode(email, code);

        assertTrue(result);
        log.info("Verify result = {}", result);
    }

    // 인증번호 검증 실패 테스트 (틀린 코드)
    @Test
    public void testVerifyCode_fail() {
        String email = "fail@gmail.com";
        emailService.sendVerificationCode(email);
        log.info("Testing wrong code for : {}", email);

        boolean result = emailService.verifyCode(email, "000000");

        assertFalse(result);
        log.info("Wrong code verify result = {}", result);
    }

    // 인증 완료 여부 확인 테스트
    @Test
    public void testIsVerified() {
        String email = "isverified@gmail.com";
        String code = emailService.sendVerificationCode(email);

        // 인증 전
        assertFalse(emailService.isVerified(email));
        log.info("Before verify : isVerified = {}", emailService.isVerified(email));

        // 인증 후
        emailService.verifyCode(email, code);
        assertTrue(emailService.isVerified(email));
        log.info("After verify : isVerified = {}", emailService.isVerified(email));
    }

    // 인증 초기화 테스트
    @Test
    public void testClearVerified() {
        String email = "clear@gmail.com";
        String code = emailService.sendVerificationCode(email);
        emailService.verifyCode(email, code);

        assertTrue(emailService.isVerified(email));
        log.info("Before clear : isVerified = {}", emailService.isVerified(email));

        emailService.clearVerified(email);

        assertFalse(emailService.isVerified(email));
        log.info("After clear : isVerified = {}", emailService.isVerified(email));
    }
}