package com.nenu;

import lombok.ToString;

import java.sql.SQLOutput;

enum Season implements Info{
    //1. 静态实例的构造 private static final
    SPRING("春天", "春暖花开"),
    SUMMER("夏天", "夏日炎炎"){
        @Override
        public void show() {
            System.out.println("这是一个夏季！");
        }
    },
    AUTUMN("秋天", "秋高气爽"),
    WINTER("冬天", "白雪皑皑");

    //2. 枚举类的成员变量 private final
    private final String SEASON_NAME;
    private final String SEASON_DESC;

    //3.私有化构造器
    private Season(String seasonName, String seasonDesc){
        SEASON_NAME = seasonName;
        SEASON_DESC = seasonDesc;
    }

    //3. 提供实例变量get方法，注意没有set
    public String getSeasonName(){
        return SEASON_NAME;
    }
    public String getSeasonDesc(){
        return SEASON_DESC;
    }

    //4. 重写toString()
    @Override
    public String toString() {
        return "Season{" +
                "SEASON_NAME='" + SEASON_NAME + '\'' +
                ", SEASON_DESC='" + SEASON_DESC + '\'' +
                '}';
    }

    @Override
    public void show(){
        System.out.println("一个季节");
    }
}

interface Info{
    void show();
}