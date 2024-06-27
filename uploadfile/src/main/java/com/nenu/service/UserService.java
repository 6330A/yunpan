package com.nenu.service;

import com.nenu.pojo.User;

public interface UserService {
    public User findById(Integer id);

    //查询用户名是否存在
    User findByUserName(String username);

    //注册
    void register(String username, String password);

    //更新用户信息
    void update(User user);

    //更新密码
    void updatePassword(String newPwd);

    //更新已使用空间
    void updateUsedSpace(long chunkSize);
}
