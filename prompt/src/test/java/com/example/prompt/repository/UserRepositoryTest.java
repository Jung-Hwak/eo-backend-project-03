package com.example.prompt.repository;

import com.example.prompt.domain.PlanEntity;
import com.example.prompt.domain.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    private PlanEntity normalPlan;

    @BeforeEach
    public void setUp() {
        normalPlan = planRepository.save(PlanEntity.builder()
                .planName("NORMAL")
                .tokenLimit(10000)
                .aiUse(0)
                .price(0)
                .build());
        log.info("setUp - normalPlan = {}", normalPlan);
    }

    // 리파지토리 빈확인
    @Test
    public void testExists(){
        assertNotNull(userRepository);
        log.info("UserRepository = {}", userRepository);
    }

    // 저장 테스트
    @Test
    public void testSave(){
        PlanEntity plan = planRepository.findByPlanName("NORMAL")
                .orElseThrow(() -> new RuntimeException("NORMAL 플랜 없음"));

        UserEntity user = createUser("test_user", "테스트유저", "test@naver.com", plan);
        log.info("Before save : user = {}", user);

        UserEntity savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertThat(savedUser.getUserid()).isEqualTo("test_user");
        log.info("After save : user = {}", savedUser);

    }
    // 아이디로 조회 테스트
    @Test
    public void testFindByUserid() {
        PlanEntity plan = planRepository.findByPlanName("NORMAL")
                .orElseThrow(() -> new RuntimeException("NORMAL 플랜 없음"));

        userRepository.save(createUser("find_user", "찾기유저", "find@naver.com", plan));

        Optional<UserEntity> result = userRepository.findByUserid("find_user");

        assertTrue(result.isPresent());
        assertThat(result.get().getUserid()).isEqualTo("find_user");
        log.info("findByUserid result = {}", result.get());
    }

    // 이메일로 조회 테스트
    @Test
    public void testFindByEmail() {
        PlanEntity plan = planRepository.findByPlanName("NORMAL")
                .orElseThrow(() -> new RuntimeException("NORMAL 플랜 없음"));

        userRepository.save(createUser("email_user", "이메일유저", "email@naver.com", plan));

        Optional<UserEntity> result = userRepository.findByEmail("email@naver.com");

        assertTrue(result.isPresent());
        assertThat(result.get().getEmail()).isEqualTo("email@naver.com");
        log.info("findByEmail result = {}", result.get());
    }

    // 아이디 중복 확인 테스트
    @Test
    public void testExistsByUserid() {
        PlanEntity plan = planRepository.findByPlanName("NORMAL")
                .orElseThrow(() -> new RuntimeException("NORMAL 플랜 없음"));

        userRepository.save(createUser("exists_user", "존재유저", "exists@naver.com", plan));

        assertTrue(userRepository.existsByUserid("exists_user"));
        assertFalse(userRepository.existsByUserid("no_such_user"));
        log.info("existsByUserid test passed");
    }

    // 이메일 중복 확인 테스트
    @Test
    public void testExistsByEmail() {
        PlanEntity plan = planRepository.findByPlanName("NORMAL")
                .orElseThrow(() -> new RuntimeException("NORMAL 플랜 없음"));

        userRepository.save(createUser("email2_user", "이메일2유저", "email2@naver.com", plan));

        assertTrue(userRepository.existsByEmail("email2@naver.com"));
        assertFalse(userRepository.existsByEmail("no@naver.com"));
        log.info("existsByEmail test passed");
    }

    // 탈퇴 테스트 (active = false)
    @Test
    public void testDeactivate() {
        PlanEntity plan = planRepository.findByPlanName("NORMAL")
                .orElseThrow(() -> new RuntimeException("NORMAL 플랜 없음"));

        UserEntity user = userRepository.save(createUser("deact_user", "탈퇴유저", "deact@naver.com", plan));
        log.info("Before deactivate : active = {}", user.isActive());

        user.setActive(false);
        UserEntity updated = userRepository.save(user);

        assertFalse(updated.isActive());
        log.info("After deactivate : active = {}", updated.isActive());
    }



    // Helper method
    private UserEntity createUser(String userid, String username, String email, PlanEntity plan) {
        return UserEntity.builder()
                .userid(userid)
                .username(username)
                .password("password123!")
                .email(email)
                .plan(plan)
                .build();
    }


}