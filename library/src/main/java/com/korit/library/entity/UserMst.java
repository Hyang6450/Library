package com.korit.library.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserMst {
    @ApiModelProperty(hidden = true) //회원가입 할 때 필요없는 데이터
    private int userId;

    @NotBlank
    @ApiModelProperty(name = "username", value = "사용자 이름", example = "abc", required = true)
    private String username;

    @NotBlank
    @ApiModelProperty(name = "password", value = "비밀번호", example = "1234", required = true)
    private String password;

    @NotBlank
    @ApiModelProperty(name = "repassword", value = "비밀번호 확인", example = "1234", required = true)
    private String repassword;

    @NotBlank
    @ApiModelProperty(name = "name", value = "성명", example = "김지향", required = true)
    private String name;

    @NotBlank
    @Email
    @ApiModelProperty(name = "email", value = "이메일", example = "abc@gmail.com", required = true)
    private String email;

    @ApiModelProperty(name = "provider", value = "OAuth데이터출처", example = "Google", required = false)
    private String provider;

    // roledtl과 join
    @ApiModelProperty(hidden = true)
    private List<RoleDtl> roleDtl;

    @ApiModelProperty(hidden = true)
    private LocalDateTime createDate;
    @ApiModelProperty(hidden = true)
    private LocalDateTime updateDate;
}
