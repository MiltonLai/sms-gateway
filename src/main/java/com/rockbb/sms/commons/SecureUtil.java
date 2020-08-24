package com.rockbb.sms.commons;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;

public class SecureUtil {
    private static Logger logger = LoggerFactory.getLogger(SecureUtil.class);
    private static final char[] digits64 = gen64char();
    public static final char[] digits32 = gen32char();

    /**
     * java.util.UUID的简单包装
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * MD5编码
     */
    public static String md5(String s) {
        return DigestUtils.md5Hex(s);
    }

    /**
     * 随机生成salt进行口令sha256加密
     * @return {salt, hash}
     */
    public static String[] saltHash(String password) {
        String salt = uuid().substring(0, 8);
        return new String[]{salt, saltHash(salt, password)};
    }

    /**
     * 加salt进行口令sha256加密, 输出64位hex
     */
    public static String saltHash(String salt, String password) {
        char c = salt.charAt(0); c = (char)((c % 3) + 1);
        int count = 1 << c;
        for (int i = 0; i < count; i++) {
            salt = salt + password;
            salt = DigestUtils.sha256Hex(salt);
        }
        return salt;
    }

    public static final char[] captchars = {
            '3' , '3' , '4' , '6' , '6' , '7' ,
            '8' , '9' , 'a' , 'b' , 'c' , 'd' ,
            'e' , 'f' , 'g' , 'h' , 'j' , 'k' ,
            'l' , 'm' , 'n' , 'p' , 'q' , 'r' ,
            'f' , 't' , 'u' , 'v' , 'w' , 'x' ,
            'y' , 'q'
    };

    /**
     * 去掉0, 1, i, o等易混淆的字母, 生成校验码字符串
     *
     * @param i
     * @param length
     * @return
     */
    public static String toCaptchaString(long i, int length)
    {
        char[] buf = new char[64];
        int charPos = 64;
        int radix = 1 << 5;
        long mask = radix - 1;
        do {
            buf[--charPos] = captchars[(int)(i & mask)];
            i >>>= 5;
        } while (i != 0);

        if (length == 0)
            return new String(buf, charPos, (64 - charPos));
        else
        {
            while(charPos > 64 - length)
            {
                buf[--charPos] = 'c';
            }
            return new String(buf, 64 - length, length);
        }
    }

    //AES-128-ECB 解密
    public static String aesDecrypt(String base64encrypted, String encryptToken) {
        try {
            byte[] raw = encryptToken.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            //先用base64解密
            byte[] encrypted = Base64.decodeBase64(base64encrypted);
            byte[] original = cipher.doFinal(encrypted);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    //AES-128-ECB 加密
    public static String aesEncrypt(String jsonString, String encryptToken) {
        try {
            byte[] raw = encryptToken.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(jsonString.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将整数压缩为字符串
     *
     * @param i positive integer
     * @param shift 1~6
     * @return
     */
    public static String shiftCompress(long i, int shift) {
        if (shift > 6) {
            shift = 6;
        } else if (shift < 1) {
            shift = 1;
        }
        char[] digits = (shift > 5) ? digits64 : digits32;
        char[] buf = new char[64];
        int pos = 64;
        int radix = 1 << shift;
        long mask = radix - 1;
        do {
            buf[--pos] = digits[(int) (i & mask)];
            i >>>= shift;
        } while (i != 0 && pos >= 0);
        return new String(buf, pos, (64 - pos));
    }

    private static char[] gen64char() {
        char[] chars = new char[64];
        char start = '0';
        int pos = 0;
        chars[pos++] = '-';
        chars[pos++] = '.';
        for (int i = 0; i < 10; i++) chars[pos++] = (char) (start + i);
        start = 'A';
        for (int i = 0; i < 26; i++) chars[pos++] = (char) (start + i);
        start = 'a';
        for (int i = 0; i < 26; i++) chars[pos++] = (char) (start + i);
        return chars;
    }

    private static char[] gen32char() {
        char[] chars = new char[32];
        char start = '0';
        int pos = 0;
        for (int i = 0; i < 10; i++) chars[pos++] = (char) (start + i);
        start = 'A';
        for (int i = 0; i < 26 && pos < 32; i++) chars[pos++] = (char) (start + i);
        return chars;
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        /*String[] s = saltHash("123123");
        System.out.println(s[0] + " " + s[1]);
        for (int i = 0; i < 100; i++ ) {
            System.out.println(saltHash(s[0], "jackrock"));
            System.out.println(saltHash(s[0], "cc"));
            System.out.println(saltHash(s[0], "bbb"));
        }*/
        for (int i = 0; i < 100000; i++) {
            //System.out.println(sn());
        }
        System.out.println("time: " + (System.currentTimeMillis() - t1));
    }
}
