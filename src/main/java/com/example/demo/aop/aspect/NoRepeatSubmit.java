package com.example.demo.aop.aspect;

/*
页面提交前从后面获取一个token ,提交时候返回给后端，后端利用这个token 作为存入redis key
redisKey设计：ip&userid&path&param
 */
public @interface NoRepeatSubmit {
}
