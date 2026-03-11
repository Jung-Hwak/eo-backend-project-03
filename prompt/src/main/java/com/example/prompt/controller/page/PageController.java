package com.example.prompt.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 페이지 라우팅 Controller
 * Thymeleaf 템플릿을 반환하는 역할
 */
@Controller
public class PageController {

    // "http://localhost:8080/" 메인 페이지
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // "http://localhost:8080/chat" 채팅 페이지
    @GetMapping("/chat")
    public String chat() {
        return "ai-chat/chat";
    }

    // "http://localhost:8080/login" 로그인 페이지
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // "http://localhost:8080/signup" 회원가입 페이지
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // "http://localhost:8080/mypage" 마이페이지
    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    // "http://localhost:8080/payment" 결제 페이지
    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    // "http://localhost:8080/reset-password" 비밀번호 찾기 페이지
    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }
}