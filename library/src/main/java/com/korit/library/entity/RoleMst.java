package com.korit.library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RoleMst {
    private int roleId;
    private String roleName;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

}
