package com.korit.library.service;

import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.AccountRepository;
import com.korit.library.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public UserDto registerUser(UserDto userDto){
        userDto.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword())); // DB에 저장되기 전에 암호화 시키는 것
        accountRepository.saveUser(userDto);
        accountRepository.saveRole(userDto);
        return userDto;
    }

    public void duplicateUsername(String username) {
        UserDto user = accountRepository.findUserByUsername(username);
        if (user != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("username", "이미 존재하는 사용자이름입니다.");

            throw new CustomValidationException(errorMap);
        }
    }

    public void compareToPassword(String password, String repassword) {
        if(!password.equals(repassword)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("repassword", "비밀번호가 일치하지 않습니다.");

            throw new CustomValidationException(errorMap);
        }
    }

    public UserDto getUser(int userId) {
        return accountRepository.findUserByUserId(userId);
    }
}
