package com.korit.library.repository;

import com.korit.library.entity.UserMst;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountRepository {
    public UserMst findUserByUserId(int userId);
    public UserMst findUserByUsername(String username);

    public int saveUser(UserMst userMst);
    public int saveRole(UserMst userMst);

    public int setUserProvider(UserMst user);

}
