package android.izy.util;

import java.security.MessageDigest;

import android.util.Base64;

public abstract class DigestUtils {

	/**
	 * 为字符串生成md5摘要.
	 * 
	 * @param input
	 *            输入字符串
	 * @return 生成的摘要内容
	 */
	public static final String MD5(String input) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(input.getBytes());
			return toHexString(messageDigest.digest());
		} catch (Exception e) {
			return input;
		}
	}

	public static String MD5ToBase64(String input) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(input.getBytes());
			return Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
		} catch (Exception e) {
			return input;
		}
	}

	public static String SHA(String input) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA");
			messageDigest.update(input.getBytes());
			return toHexString(messageDigest.digest());
		} catch (Exception e) {
			return input;
		}
	}

	public static String toHexString(byte[] input) {
		StringBuilder sb = new StringBuilder(input.length * 2);
		for (byte b : input) {
			// sb.append(String.format("%02x", b));
			// sb.append(Integer.toHexString(b & 0xff));
			sb.append(Character.forDigit(b >>> 4 & 0xf, 16)).append(Character.forDigit(b & 0xf, 16));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(String.format("%x", 10));
		System.out.println(0xf);
	}
}
