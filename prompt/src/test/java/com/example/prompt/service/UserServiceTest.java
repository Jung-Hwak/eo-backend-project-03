package com.example.prompt.service;

import com.example.prompt.domain.PlanEntity;
import com.example.prompt.dto.user.UserDto;
import com.example.prompt.repository.PlanRepository;
import com.example.prompt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @BeforeEach
    public void setUp() {
        planRepository.save(PlanEntity.builder()
                .planName("NORMAL")
                .tokenLimit(10000)
                .aiUse(0)
                .price(0)
                .build());
        log.info("setUp - NORMAL 플랜 저장 완료");
    }

    // UserService Bean 확인
    @Test
    public void testExists() {
        assertNotNull(userService);
        log.info("UserService = {}", userService);
    }

    // 아이디 중복 확인 테스트
    @Test
    public void testIsUseridAvailable() {
        log.info("Testing userid availability");

        boolean available = userService.isUseridAvailable("brand_new_id");
        assertTrue(available);
        log.info("brand_new_id available = {}", available);
    }

    // 회원가입 테스트
    @Test
    public void testSignup() {
        String email = "signup@gmail.com";
        String code = emailService.sendVerificationCode(email);
        emailService.verifyCode(email, code);

        UserDto dto = createSignupDto("signup_user", "회원가입유저", email, "password123!", "password123!");
        log.info("Before signup : dto = {}", dto);

        assertDoesNotThrow(() -> userService.signup(dto));

        assertTrue(userRepository.existsByUserid("signup_user"));
        log.info("After signup : user saved successfully");
    }

    // 회원가입 실패 - 아이디 중복
    @Test
    public void testSignup_duplicateUserid() {
        String email1 = "dup1@gmail.com";
        String code1 = emailService.sendVerificationCode(email1);
        emailService.verifyCode(email1, code1);
        userService.signup(createSignupDto("dup_user", "중복유저", email1, "password123!", "password123!"));

        String email2 = "dup2@gmail.com";
        String code2 = emailService.sendVerificationCode(email2);
        emailService.verifyCode(email2, code2);

        UserDto dto = createSignupDto("dup_user", "중복유저2", email2, "password123!", "password123!");
        log.info("Testing duplicate userid : {}", dto.getUserid());

        assertThrows(IllegalArgumentException.class, () -> userService.signup(dto));
        log.info("Duplicate userid test passed");
    }

    // 회원가입 실패 - 이메일 미인증
    @Test
    public void testSignup_emailNotVerified() {
        UserDto dto = createSignupDto("noVerify_user", "미인증유저", "noverify@gmail.com", "password123!", "password123!");
        log.info("Testing signup without email verification");

        assertThrows(IllegalArgumentException.class, () -> userService.signup(dto));
        log.info("Email not verified test passed");
    }

    // 회원가입 실패 - 비밀번호 불일치
    @Test
    public void testSignup_passwordMismatch() {
        String email = "mismatch@gmail.com";
        String code = emailService.sendVerificationCode(email);
        emailService.verifyCode(email, code);

        UserDto dto = createSignupDto("mismatch_user", "불일치유저", email, "password123!", "different123!");
        log.info("Testing password mismatch");

        assertThrows(IllegalArgumentException.class, () -> userService.signup(dto));
        log.info("Password mismatch test passed");
    }

    // 내 정보 조회 테스트
    @Test
    public void testGetMyInfo() {
        String email = "myinfo@gmail.com";
        String code = emailService.sendVerificationCode(email);
        emailService.verifyCode(email, code);
        userService.signup(createSignupDto("myinfo_user", "내정보유저", email, "password123!", "password123!"));

        Long userId = userRepository.findByUserid("myinfo_user").get().getId();
        log.info("Testing getMyInfo for userId = {}", userId);

        UserDto result = userService.getMyInfo(userId);

        assertNotNull(result);
        assertThat(result.getUserid()).isEqualTo("myinfo_user");
        log.info("getMyInfo result = {}", result);
    }

    // 회원 탈퇴 테스트
    @Test
    public void testWithdraw() {
        String email = "withdraw@gmail.com";
        String code = emailService.sendVerificationCode(email);
        emailService.verifyCode(email, code);
        userService.signup(createSignupDto("withdraw_user", "탈퇴유저", email, "password123!", "password123!"));

        Long userId = userRepository.findByUserid("withdraw_user").get().getId();
        log.info("Before withdraw : active = {}", userRepository.findById(userId).get().isActive());

        userService.withdraw(userId);

        assertFalse(userRepository.findById(userId).get().isActive());
        log.info("After withdraw : active = {}", userRepository.findById(userId).get().isActive());
    }

    // Helper method
    private UserDto createSignupDto(String userid, String username, String email,
                                    String password, String passwordConfirm) {
        return UserDto.builder()
                .userid(userid)
                .username(username)
                .email(email)
                .password(password)
                .passwordConfirm(passwordConfirm)
                .agree(true)
                .build();
    }
}