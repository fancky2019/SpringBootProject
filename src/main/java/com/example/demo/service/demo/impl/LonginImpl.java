package com.example.demo.service.demo.impl;

import com.example.demo.model.request.TestRequest;
import com.example.demo.service.demo.LoginService;
import org.apache.commons.codec.digest.DigestUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

//region  登录用户名密码设计
            /*
             password_hash VARCHAR(255) NOT NULL,  -- BCrypt加密的前端哈希值
    Spring Boot 项目，采用 HTTPS 传输 前端SHA256哈希 到后端， 后端BCrypt加密的前端哈希值存储到数据库 的方案，既安全又易于实现。

             */
//endregion

/**
 * MD5          32      32字符十六进制，已不安全
 * SHA-1        40      40字符十六进制，已过时
 * SHA-256      64      64字符十六进制，目前安全
 * SHA-512      128     128字符十六进制，更安全
 * BCrypt       60      60字符Base64，密码存储推荐
 * Argon2       68      变长，密码哈希竞赛获胜者
 *
 *
 * Base64 编码字符集：总共 64 个字符，所以叫 Base64。
 *
 * text
 * A-Z (26个)
 * a-z (26个)
 * 0-9 (10个)
 * +    (1个)
 * /    (1个)
 * =    (填充符，1个):它的作用是在编码数据的末尾进行填充，使编码后的字符串长度是 4 的倍数。
 *
 */
@Service
public class LonginImpl implements LoginService {


    //    private PooledPBEStringEncryptor encryptor;
    @Autowired
    private StringEncryptor encryptor;
    //BCrypt生成复合字符串（算法+成本+盐+哈希）。MD5/SHA生成纯哈希值
    //BCrypt 是一种自适应哈希函数.生成hash值 ，类似MD5 sha256 .md5 32位16进制字符
    //BCrypt是一种专门用于密码哈希的安全算法 类似MD5.BCrypt内部自动加盐
    //BCrypt 生成的是自包含的、带盐的、可调节成本的哈希值，专门为安全存储密码而设计。它生成的是哈希，但比传统哈希函数更安全、更智能。
    // 使用BCrypt，强度12（推荐） 12 是 成本因子（Cost Factor），也叫工作因子（Work Factor）。它决定了BCrypt算法的计算复杂度。
    //迭代次数 = 2^成本因子
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    // 客户端盐（可选，增强安全性）
    private static final String CLIENT_SALT = "your-app-client-salt-v1";

    /**
     * 加密前端哈希值（注册/修改密码时使用）
     * @param frontendHash 前端传过来的SHA256哈希值
     * @return BCrypt加密后的哈希值
     */
    public String encodeFrontendHash(String frontendHash) {
        // 直接对前端哈希值进行BCrypt加密.BCrypt内部自动加盐
        return passwordEncoder.encode(frontendHash);
    }

    /**
     * 带客户端盐的版本（更安全）
     * @param rawPassword 原始密码（仅用于带盐计算）
     * @return BCrypt加密后的哈希值
     */
    public String encodeWithClientSalt(String rawPassword) {
        // 1. 前端应该：SHA256(CLIENT_SALT + rawPassword)
        // 2. 这里为了演示，模拟前端处理
        String frontendHash = calculateFrontendHash(rawPassword);

        // 3. BCrypt加密前端哈希值
        return passwordEncoder.encode(frontendHash);
    }

    /**
     * 验证密码
     * @param frontendHash 前端传来的SHA256哈希值
     * @param storedHash 数据库中存储的BCrypt哈希值
     * @return 是否匹配
     */
    public boolean verifyPassword(String frontendHash, String storedHash) {
        // BCrypt会自动处理比较
        return passwordEncoder.matches(frontendHash, storedHash);
    }

    /**
     * 计算前端哈希值（模拟前端计算）
     */
    private String calculateFrontendHash(String rawPassword) {
        // 前端应该使用：sha256(CLIENT_SALT + password)
        String saltedPassword = CLIENT_SALT + rawPassword;
        // MD5（已过时，不安全）
        String md5Hash = DigestUtils.md5Hex(saltedPassword);
        return DigestUtils.sha256Hex(saltedPassword);
    }

    /**
     * 生成密码重置token
     */
    public String generatePasswordResetToken(String email) {
        String raw = email + System.currentTimeMillis() + UUID.randomUUID();
        return DigestUtils.sha256Hex(raw);
    }

    @Override
    public boolean login(TestRequest request) {
        String saltedPassword = CLIENT_SALT + request.getPassword();
        //  MD5（已过时，不安全）  32 字符
        //32个十六进制字符：f743c3e5caa458a732ec51b9f899ebad
        String md5Hash = DigestUtils.md5Hex(saltedPassword);
        //64个十六进制字符	：dc9346cb547c00b54050403aa11e8bd3f08f8337c475ffa59241c1d037f455e8
        String frontendHash = DigestUtils.sha256Hex(saltedPassword);
//        标准格式：$2a$12$盐值(22字符)哈希值(31字符)
//        总长度：3 + 2 + 22 + 31 = 60字符
        //标识符$
        //算法版本（2a = BCrypt）
        //成本因子（12 = 2^12次迭代）
        //盐值（22字符 = 16字节）:成本因子$后的22字符   Z8c6RR0xxfE7ZVYUKTec8u
        //哈希值（31字节）：最后的31 字符 后31个字符：w2PRJb.Ba0Cqf/6TPbKz8fiXqZrnT2O
        //$2a$12$Z8c6RR0xxfE7ZVYUKTec8uw2PRJb.Ba0Cqf/6TPbKz8fiXqZrnT2O
        String dbStoredHash = passwordEncoder.encode(frontendHash);
        // BCrypt会自动处理比较
        boolean match = passwordEncoder.matches(frontendHash, dbStoredHash);
        return false;
    }


    /**
     * jasypt加密
     * 引入依赖
     *      <dependency>
     *             <groupId>com.github.ulisesbocchio</groupId>
     *             <artifactId>jasypt-spring-boot-starter</artifactId>
     *             <version>2.1.2</version>
     *         </dependency>
     * @param pwd
     * @return
     */
    @Override
    public String encryptedPassword(String pwd) {
        //lkK8NYWG9PaTrDFfbA3cyhJlal/mVKiY8jPV7eQtR0ary+Hc87jT1e13sggAWG5z
        String encryptedPassword = encryptor.encrypt(pwd);

        // 验证解密
        String decrypted = encryptor.decrypt(encryptedPassword);
        return encryptedPassword;
    }
}
