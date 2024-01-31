package com.cos.jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

//security 에 UsernamePasswordAuthenticationFilter 가 있음
//login 요청 시 username, password 전송 (post)
//UsernamePasswordAuthenticationFilter 필터 동작
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtAuthenticationFilter : 로그인 시도중");
        try {
            //BufferedReader br = request.getReader();
            //
            //String input = null;
            //while ((input = br.readLine()) != null) {
            //    log.info(input);
            //}

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            log.info("user### {}", user);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            log.info("authenticationToken : {}", authenticationToken);
            //PrincipalDetailsService.loadUserByUsername() 실행
            log.info("0000");
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            log.info("1111");
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info("2222");
            //로그인이 정상적으로 되었다는뜻
            log.info("principalDetails.getUser().getUsername() : {}", principalDetails.getUser().getUsername());
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("======================================");
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        log.info("successfulAuthentication 실행됨: 인증완료");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("cos")
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos"));
        log.info("jwt token : {}", jwtToken);
        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
