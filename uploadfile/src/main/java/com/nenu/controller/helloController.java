package com.nenu.controller;

import com.nenu.pojo.Result;
import org.springframework.web.bind.annotation.*;
@CrossOrigin
@RestController
public class helloController {
    @GetMapping("/hello")
    public Result sayHello() {
        System.out.println("你好!!");
        return Result.success();
    }
}
