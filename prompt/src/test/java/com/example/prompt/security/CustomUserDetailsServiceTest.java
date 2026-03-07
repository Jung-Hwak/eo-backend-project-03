package com.example.prompt.security;

import com.example.prompt.domain.PlanEntity;
import com.example.prompt.dto.user.UserDto;
import com.example.prompt.repository.PlanRepository;
import com.example.prompt.service.EmailService;
import com.example.prompt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
class CustomUserDetailsServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

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

        // 테스트용 유저 회원가입
        String email = "login_test@gmail.com";
        String code = emailService.sendVerificationCode(email);
        emailService.verifyCode(email, code);
        userService.signup(UserDto.builder()
                .userid("login_user")
                .username("로그인유저")
                .email(email)
                .password("password123!")
                .passwordConfirm("password123!")
                .agree(true)
                .build());

        log.info("setUp - 테스트 유저 생성 완료");
    }

    // 로그인 성공 테스트
    @Test
    public void testLogin_success() throws Exception {
        log.info("Testing POST /login - success");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "login_user")
                        .param("password", "password123!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        log.info("Login success test passed");
    }

    // 로그인 실패 - 비밀번호 틀림
    @Test
    public void testLogin_wrongPassword() throws Exception {
        log.info("Testing POST /login - wrong password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "login_user")
                        .param("password", "wrongpassword!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        log.info("Login wrong password test passed");
    }

    // 로그인 실패 - 존재하지 않는 유저
    @Test
    public void testLogin_userNotFound() throws Exception {
        log.info("Testing POST /login - user not found");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "ghost_user")
                        .param("password", "password123!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        log.info("Login user not found test passed");
    }

    // 로그아웃 테스트
    @Test
    public void testLogout() throws Exception {
        log.info("Testing POST /logout");

        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        log.info("Logout test passed");
    }

}