package com.example.demo.utility;


import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;


public class RSAUtil {

//    /**
//     * RSA最大加密明文大小
//     */
//    private static final int MAX_ENCRYPT_BLOCK = 117;
//
//    /**
//     * RSA最大解密密文大小
//     */
//    private static final int MAX_DECRYPT_BLOCK = 128;
    /**
     * 非对称加密密钥算法
     */
    private static final String KEY_ALGORITHM_RSA = "RSA";

    private static final String PUBLIC_KEY_STR;
    private static final String PRIVATE_KEY_STR;

    private static final PublicKey PUBLIC_KEY;

    private static final PrivateKey PRIVATE_KEY;
    /**
     * RSA密钥长度
     * 默认1024位，
     * 密钥长度必须是64的倍数，
     * 范围在512至65536位之间。
     */
    private static final int KEY_SIZE = 1024;

    static {
        //  // 加载加密提供者
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        // 实例化密钥对生成器
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA);
            // 初始化密钥对生成器
            keyPairGen.initialize(KEY_SIZE);
            // 生成密钥对
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PUBLIC_KEY = keyPair.getPublic();
            PRIVATE_KEY = keyPair.getPrivate();
            PUBLIC_KEY_STR = Base64.toBase64String(PUBLIC_KEY.getEncoded());
            PRIVATE_KEY_STR = Base64.toBase64String(PRIVATE_KEY.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    //region  加密


    public static byte[] encrypt(byte[] data) throws Exception {
        return encrypt(data, PUBLIC_KEY_STR);
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  公钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String key) throws Exception {

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            int outputSize = cipher.getOutputSize(data.length);
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
                    : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0, remainSize = 0;
            while ((remainSize = data.length - i * blockSize) > 0) {
                int inputLen = remainSize > blockSize ? blockSize : remainSize;
                cipher.doFinal(data, i * blockSize, inputLen, raw, i * outputSize);
                i++;
            }
            return raw;
        }
        return cipher.doFinal(data);
    }


    public static byte[] decrypt(byte[] data) throws Exception {
        return decrypt(data, PRIVATE_KEY_STR);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  私钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {

        //PKCS8EncodedKeySpec公钥的ASN.1编码 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;
            while (data.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(data, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        }
        return cipher.doFinal(data);
    }

//endregion


    //region  签名


    /**
     * 验签：公钥解密
     *
     * @param data 待解密数据
     * @param publicKeyStr  公钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static boolean verify(byte[] data, String signedStr, String publicKeyStr) throws Exception {
        //X509EncodedKeySpec类表示公钥的ASN.1编码
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(publicKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        // 生成公钥
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);


        //先用MD5生成摘要（长度就不会超过117字节），然后用RSA加密
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.decode(signedStr));
    }


    /**
     * 签名：私钥加密
     *
     * @param data 待加密数据
     * @param key  私钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static String sign(byte[] data, String key) throws Exception {

        //PKCS8EncodedKeySpec该类代表私有密钥的ASN.1编码
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.toBase64String(signature.sign());
    }
//endregion


    /**
     * 取得公钥
     *
     * @return key 公钥
     */
    public static String getPublicKey() {
        return PUBLIC_KEY_STR;
    }

    /**
     * 取得公钥
     *
     * @return 公钥
     */
    public static String getPrivateKey() {
        return PRIVATE_KEY_STR;
    }


    /**
     * key publicKey, value privateKey
     *
     * @return
     */
    public static HashMap<String, String> generatorKeys() {
        HashMap<String, String> hashMap = new HashMap<>();
        // 实例化密钥对生成器
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA);
            // 初始化密钥对生成器
            keyPairGen.initialize(KEY_SIZE);
            // 生成密钥对
            KeyPair keyPair = keyPairGen.generateKeyPair();

            hashMap.put(Base64.toBase64String(PUBLIC_KEY.getEncoded()),
                    Base64.toBase64String(PRIVATE_KEY.getEncoded()));
            return hashMap;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
