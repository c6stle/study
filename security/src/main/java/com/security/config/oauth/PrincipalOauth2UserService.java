package com.security.config.oauth;

import com.security.config.auth.PrincipalDetails;
import com.security.config.oauth.provider.FacebookUserInfo;
import com.security.config.oauth.provider.GoogleUserInfo;
import com.security.config.oauth.provider.NaverUserInfo;
import com.security.config.oauth.provider.OAuth2UserInfo;
import com.security.model.User;
import com.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //registrationId 로 어떤 OAuth 로 로그인했는지 알 수 있음
        log.info("userRequest = {}", userRequest.getClientRegistration());
        log.info("userRequest = {}", userRequest.getAccessToken());

        OAuth2User oauth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if ("google".equals(userRequest.getClientRegistration().getRegistrationId())) {
            log.info("google login request");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        } else if ("facebook".equals(userRequest.getClientRegistration().getRegistrationId())) {
            log.info("facebook login request");
            oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
        } else if ("naver".equals(userRequest.getClientRegistration().getRegistrationId())){
            log.info("naver login request");
            oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        } else {
            log.info("소셜 로그인은 구글, 페이스북, 네이버 로그인만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            log.info("자동 회원가입 진행");
            userEntity = User.builder()
                    .username(username)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        } else {
            log.info("소셜 로그인 이력이 있는 회원");
        }

        //구글로그인버튼 -> 구글로그인창 -> 로그인완료 -> code 리턴(OAuth-Client 라이브러리) -> AccessToken 요청
        //userRequest 정보 -> 회원프로필 받기(loadUser) -> 회원프로필을 구글로부터 받아옴
        log.info("getAttributes = " + oauth2User.getAttributes());
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
