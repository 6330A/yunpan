package com.nenu.mapper;

import com.nenu.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where user_id = #{id}")
    User findById(Integer id);

    //根据用户名查找
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);

    //注册用户
    @Insert("insert into user (username, password, create_time, update_time, disk_space, used_space, user_type)" +
    " values (#{username}, #{password}, now(), now(), #{diskSpace}, 0, 0)")
    void register(String username, String password, long diskSpace);

    //更新用户信息
    @Update("update user set nickname=#{nickname}, update_time=now() where user_id=#{userId}")
    void update(User user);

    @Update("update user set password=#{newPwdMd5}, update_time=now() where user_id=#{userId}")
    void updatePassword(String newPwdMd5, Integer userId);

    //更新用户空间
    @Update("update user set used_space = used_space + #{chunkSize} where user_id = #{userId}")
    void updateUsedSpace(Integer userId, long chunkSize);
}
