package com.yaude.common.util;

import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
public class PasswordUtil {

	/**
	 * JAVA6支持以下任意一種算法 PBEWITHMD5ANDDES PBEWITHMD5ANDTRIPLEDES
	 * PBEWITHSHAANDDESEDE PBEWITHSHA1ANDRC2_40 PBKDF2WITHHMACSHA1
	 * */

	/**
	 * 定義使用的算法為:PBEWITHMD5andDES算法
	 */
	public static final String ALGORITHM = "PBEWithMD5AndDES";//加密算法
	public static final String Salt = "63293188";//密鑰

	/**
	 * 定義迭代次數為1000次
	 */
	private static final int ITERATIONCOUNT = 1000;

	/**
	 * 獲取加密算法中使用的鹽值,解密中使用的鹽值必須與加密中使用的相同才能完成操作. 鹽長度必須為8字節
	 * 
	 * @return byte[] 鹽值
	 * */
	public static byte[] getSalt() throws Exception {
		// 實例化安全隨機數
		SecureRandom random = new SecureRandom();
		// 產出鹽
		return random.generateSeed(8);
	}

	public static byte[] getStaticSalt() {
		// 產出鹽
		return Salt.getBytes();
	}

	/**
	 * 根據PBE密碼生成一把密鑰
	 * 
	 * @param password
	 *            生成密鑰時所使用的密碼
	 * @return Key PBE算法密鑰
	 * */
	private static Key getPBEKey(String password) {
		// 實例化使用的算法
		SecretKeyFactory keyFactory;
		SecretKey secretKey = null;
		try {
			keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			// 設置PBE密鑰參數
			PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
			// 生成密鑰
			secretKey = keyFactory.generateSecret(keySpec);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return secretKey;
	}

	/**
	 * 加密明文字符串
	 * 
	 * @param plaintext
	 *            待加密的明文字符串
	 * @param password
	 *            生成密鑰時所使用的密碼
	 * @param salt
	 *            鹽值
	 * @return 加密后的密文字符串
	 * @throws Exception
	 */
	public static String encrypt(String plaintext, String password, String salt) {

		Key key = getPBEKey(password);
		byte[] encipheredData = null;
		PBEParameterSpec parameterSpec = new PBEParameterSpec(salt.getBytes(), ITERATIONCOUNT);
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);

			cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
			//update-begin-author:sccott date:20180815 for:中文作為用戶名時，加密的密碼windows和linux會得到不同的結果 gitee/issues/IZUD7
			encipheredData = cipher.doFinal(plaintext.getBytes("utf-8"));
			//update-end-author:sccott date:20180815 for:中文作為用戶名時，加密的密碼windows和linux會得到不同的結果 gitee/issues/IZUD7
		} catch (Exception e) {
		}
		return bytesToHexString(encipheredData);
	}

	/**
	 * 解密密文字符串
	 * 
	 * @param ciphertext
	 *            待解密的密文字符串
	 * @param password
	 *            生成密鑰時所使用的密碼(如需解密,該參數需要與加密時使用的一致)
	 * @param salt
	 *            鹽值(如需解密,該參數需要與加密時使用的一致)
	 * @return 解密后的明文字符串
	 * @throws Exception
	 */
	public static String decrypt(String ciphertext, String password, String salt) {

		Key key = getPBEKey(password);
		byte[] passDec = null;
		PBEParameterSpec parameterSpec = new PBEParameterSpec(salt.getBytes(), ITERATIONCOUNT);
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);

			cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

			passDec = cipher.doFinal(hexStringToBytes(ciphertext));
		}

		catch (Exception e) {
			// TODO: handle exception
		}
		return new String(passDec);
	}

	/**
	 * 將字節數組轉換為十六進制字符串
	 * 
	 * @param src
	 *            字節數組
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 將十六進制字符串轉換為字節數組
	 * 
	 * @param hexString
	 *            十六進制字符串
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}


}