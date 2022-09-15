package com.example.demo.utility;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;



import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;


public class RSAUtil {

    /**
     * 非对称加密密钥算法
     */
    private static final String KEY_ALGORITHM_RSA = "RSA";
    //不能指定RSA否则解密乱码+明文
    private static final String CIPHER = "RSA/ECB/PKCS1Padding";
    /**
     *
     * MD5withRSA  SHA256withRSA
     *  * 数字签名
     *  * 1：MD5withRSA，：将正文通过MD5数字摘要后，将密文 再次通过生成的RSA密钥加密，生成数字签名，
     *  * 将明文与密文以及公钥发送给对方，对方拿到私钥/公钥对数字签名进行解密，然后解密后的，与明文经过MD5加密进行比较
     *  * 如果一致则通过
     *
     *
     *
     *
     *
     *
     *
     *
     *  hmac数字签名 =  rsa_encrypt(hmac(信息) + RSA私钥)
     *  SHA256withRSA数字签名 =  SHA256withRSA_encrypt(信息 + RSA私钥)
     */
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

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
        //RSA最大加密明文大小, RSA最大解密密文大小
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
//            PUBLIC_KEY_STR = Base64.toBase64String(PUBLIC_KEY.getEncoded());
//            PRIVATE_KEY_STR = Base64.toBase64String(PRIVATE_KEY.getEncoded());

            PRIVATE_KEY_STR="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ/OZFB803CogXzkssdQKrlRVncx6xtLGr+vrQeJoO5LfKL9FbkSNdHimbJzY7KXBI+fHz/F6GdlO24r6qEZwMXrDO7dQMwYFPY43Sfa1A90vazjQJm8/66nZM2JNW70WtYJZpSHx5SRqhBO1w0tAd+Lyc/wD/sZf1RF4xxYztzjAgMBAAECgYAYPnEUjuNq/31pi66ds0lQBQl3mtCeuuWreATplFUgYb5eYcPmaF9W4KhNnNjesq+D9HGCtM0dxoteGvaFC0ml35YLbPQkgVJ2n0/A7pTo41NJF9WuEXlNmp96P8K2ndBDMQQ+Fiqf1+AC2JbNM7zzL2g3mJyrwGkbGpuE9IQI0QJBANdDxuTS/CRwL/LGoh3msnOhBDsNKI86dJCdluILJU2DHI5abYbExZPYm1y7R1/ajfidfF30cAfLIis00z1mmJECQQC+C/9uHx1Mvt8gXsp6BmQktEP0DDRe7UAQflmMYBQbo5Jct8yQ9oAwiGDO5Ov8Fp99/3Xb0UAxFOEYaWu5cvgzAkA+OIIB3BYzdhX154IugGMuVulBJFGH7M6KinJ1TeFvYSlc4DhuTuwJCwAFMsCzrRmCNgsfoSrMpeNvd6pjQgdxAkAQm33Lwr5NuZRIAOCSv0I7DuGtTu+4p+TkTBZJNRAsxiOBJLKkrFXRZ+mFyu1wTw3K9er3tZZ1c4ykFHpMb2aPAkBlpa5wuscsItRuysZk8WtPXxmrqdkW+JamHS2zLuKSe3a9hMh6oNsU47jXqEWcyF5iFZONal9+t0I++K5m7Q3A";
            PUBLIC_KEY_STR="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfzmRQfNNwqIF85LLHUCq5UVZ3MesbSxq/r60HiaDuS3yi/RW5EjXR4pmyc2OylwSPnx8/xehnZTtuK+qhGcDF6wzu3UDMGBT2ON0n2tQPdL2s40CZvP+up2TNiTVu9FrWCWaUh8eUkaoQTtcNLQHfi8nP8A/7GX9UReMcWM7c4wIDAQAB";

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
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER);
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
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
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
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        // 生成公钥
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);


        //先用MD5生成摘要（长度就不会超过117字节），然后用RSA加密  SHA256withRSA
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.decodeBase64(signedStr));
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
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
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

//            hashMap.put(Base64.toBase64String(PUBLIC_KEY.getEncoded()),
//                    Base64.toBase64String(PRIVATE_KEY.getEncoded()));
            return hashMap;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
