package com.nenu.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    @NotNull
    private Integer userId;                //用户id
    private String username;                //用户名
    @JsonIgnore
    private String password;                //md5密码

    @NotEmpty
    @Pattern(regexp = "^\\S{1,10}$")
    private String nickname;                //昵称
    private String userAvatar;             //头像
    private LocalDateTime createTime;     //创建时间
    private LocalDateTime updateTime;     //更新时间
    private Long diskSpace;                //网盘空间
    private Long usedSpace;                //已用空间
    private Integer userType;              //用户类型 0-用户 1-管理员
}

