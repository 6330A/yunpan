package com.nenu.controller;

import com.mysql.cj.util.StringUtils;
import com.nenu.pojo.Result;
import com.nenu.pojo.User;
import com.nenu.service.UserService;
import com.nenu.utils.JwtUtil;
import com.nenu.utils.Md5Util;
import com.nenu.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping
    public User findById(Integer id){
        return userService.findById(id);
    }

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){
        User u = userService.findByUserName(username);
        if(u != null){
            return Result.error("用户名已被占用");
        }else{
            userService.register(username, password);
            return Result.success();
        }
    }

    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){
        User u = userService.findByUserName(username);
        if(u == null){
            return Result.error("用户未注册");
        }

        if(u.getPassword().equals(Md5Util.getMD5(password))){
            System.out.println("login...");
            //生成令牌jwt
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", u.getUserId());
            claims.put("username", u.getUsername());
            String token = JwtUtil.genToken(claims);

            //token令牌存入redis
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token, token, 1, TimeUnit.DAYS);

            return Result.success(token);
        }

        return Result.error("密码错误");
    }

    //获取用户信息
    @GetMapping("/userInfo")
    public Result<User> getUserInfo(){
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = String.valueOf(claims.get("username"));
        System.out.println("userinfo...");
        User user = userService.findByUserName(username);
        return Result.success(user);//这里应该只传递部分信息
    }

    //更新用户基本信息，这里暂时仅限昵称
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        System.out.println("update...");
        userService.update(user);
        return Result.success();
    }

    //更新密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params, @RequestHeader("Authorization") String token){
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        String reNewPwd = params.get("reNewPwd");

        if(StringUtils.isEmptyOrWhitespaceOnly(oldPwd) || StringUtils.isEmptyOrWhitespaceOnly(newPwd) || StringUtils.isEmptyOrWhitespaceOnly(reNewPwd)){
            return Result.error("缺少必要参数");
        }

        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = String.valueOf(claims.get("username"));
        User loginUser = userService.findByUserName(username);

        if(!loginUser.getPassword().equals(Md5Util.getMD5(oldPwd))){
            return Result.error("原密码填写错误");
        }

        if(!newPwd.equals(reNewPwd)){
            return Result.error("两次填写的新密码不一致");
        }

        userService.updatePassword(newPwd);

        //删除原来的token,这是简洁写法
        stringRedisTemplate.delete(token);

        return Result.success();
    }
}
