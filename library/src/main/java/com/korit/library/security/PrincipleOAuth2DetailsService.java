package com.korit.library.security;


import com.korit.library.entity.UserMst;
import com.korit.library.repository.AccountRepository;
import com.korit.library.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipleOAuth2DetailsService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        PrincipalDetails principalDetails = null; // PrincipleDetails에 OAuth2User를 implements해놔서 업캐스팅이 가능하다.

        System.out.println("ClientRegistration >>> " + userRequest.getClientRegistration());
        System.out.println("Attributes >>> " + oAuth2User.getAttributes());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String username = email.substring(0, email.indexOf("@"));
        String provider = userRequest.getClientRegistration().getClientName();

        UserMst userMst = accountRepository.findUserByUsername(username); // username으로 찾겠다

        if(userMst == null) { // 가입이 안되어 있으니 생성해라
            String name = (String) attributes.get("name");
            String password = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()); // 임시 비밀번호

            userMst = UserMst.builder()
                    .username(username)
                    .password(password)
                    .name(name)
                    .email(email)
                    .provider(provider)
                    .build();

            accountRepository.saveUser(userMst);
            accountRepository.saveRole(userMst); // UserMst안에 있는 userId를 들고온다.
            userMst = accountRepository.findUserByUsername(username);

        }else if(userMst.getProvider() == null) { // 일반 회원가입을 했을 경우 usename이 같으면 sns로그인했을 때 바로 등록해주겠다.
            userMst.setProvider(provider);
            accountRepository.setUserProvider(userMst);
        }

        principalDetails = new PrincipalDetails(userMst, attributes);
        return principalDetails;
    }
}
