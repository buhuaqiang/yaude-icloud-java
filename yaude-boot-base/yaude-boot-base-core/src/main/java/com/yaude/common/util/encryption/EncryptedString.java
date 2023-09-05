package com.yaude.common.util.encryption;


import lombok.Data;

@Data
public class  EncryptedString {

    public static  String key = "1234567890adbcde";//長度為16個字符

    public static  String iv  = "1234567890hjlkew";//長度為16個字符
}
