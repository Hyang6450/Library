package com.korit.library.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 기존것을 버리고 만든거 쓴다라는 어노테이션
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().disable();
        http.authorizeRequests()
                .antMatchers("/mypage/**", "/security/**") //이 요청주소로 들어오면 인증(authenticated())을 거쳐라
                .authenticated()
                .anyRequest() // 다른 모든 요청주소들은 모든 권한(permitAll())을 줘라
                .permitAll()
                .and()
                .formLogin() // form을 통한 로그인을 하겠다
                .loginPage("/account/login") // 로그인 페이지 get 요청
                .loginProcessingUrl("/account/login") // 로그인 인증 post 요청
                .defaultSuccessUrl("/index");
    }
}
