package com.security.config;

import com.security.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)//@prePostEnabled => @Secured
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll()
        );

        http.formLogin(f -> f
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
        );

        //구글 로그인 후 후처리 필요
        // 1.코드받기(인증)
        // 2.엑세스토큰(권한)
        // 3.사용자프로필 정보 가져옴
        // 4-1.3토대로 회원가입 자동 진행
        // 4-2.이메일, 전화번호, 이름, 아이디 ,, 쇼핑몰 -> 집주소, 백화점몰 -> vip 등급 등 추가적인 요소가 필요하면 자동x
        http.oauth2Login(auth -> auth.loginPage("/loginForm")
                .userInfoEndpoint(info -> info.userService(principalOauth2UserService)));
        return http.build();
    }
}
