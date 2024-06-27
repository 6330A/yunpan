package com.nenu.service.impl;

import com.nenu.mapper.UserMapper;
import com.nenu.pojo.User;
import com.nenu.service.UserService;
import com.nenu.utils.Md5Util;
import com.nenu.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Value("${diskDefaultSpace}")
    long diskDefaultSpace;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    //注册
    @Override
    public void register(String username, String password) {
        //对密码进行加密
        String md5String = Md5Util.getMD5(password);
        System.out.println(diskDefaultSpace);
        userMapper.register(username, md5String, diskDefaultSpace);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void updatePassword(String newPwd) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());
        userMapper.updatePassword(Md5Util.getMD5(newPwd), userId);
    }

    //更新已使用空间
    @Override
    public void updateUsedSpace(long chunkSize) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());
        userMapper.updateUsedSpace(userId, chunkSize);
    }
}
