package android.izy.util;

import java.util.regex.Pattern;

/**
 * 常用验证工具类
 * 
 * @author yangyp
 * @version 1.0, 2014-7-7 上午11:38:28
 */
public class ValidatorUtils {

	/**
	 * 判断是否为 empty 或 <code>null</code>.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 判断是否为 empty 或 <code>null</code>.
	 * 
	 * @param array
	 * @return <code>true</code> if the array is empty or <code>null</code>
	 */
	public static boolean isEmpty(String... array) {
		boolean result = true;
		if (array == null || array.length == 0) {
			result = true;
		} else {
			for (String str : array) {
				result &= isEmpty(str);
			}
		}
		return result;
	}

	/**
	 * 判断是否不为 empty 或 <code>null</code>.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 判断是否不为 empty 或 <code>null</code>.
	 * 
	 * @param array
	 *            字符串列表
	 * @return true/false
	 */
	public static boolean isNotEmpty(String... array) {
		return !isEmpty(array);
	}

	/**
	 * 判断是否文件名
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isFileName(String fileName) {
		return Pattern.matches("[^\\/:*?\"<>|]+", fileName);
	}

	/**
	 * 判断是否是数字（整数与小数）
	 * 
	 * @param phonenumber
	 * @return
	 */
	public static boolean isNumber(String str) {
		// 判断小数点后一位的数字的正则表达式
		return Pattern.matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?", str);
	}

	/**
	 * 判断是否是固定电话
	 * 
	 * @param phonenumber
	 * @return
	 */
	public static boolean isPhone(String phonenumber) {
		return Pattern.matches("0\\d{2,3}-\\d{7,8}", phonenumber);
	}

	/**
	 * 判断是否是手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobilePhone(String mobiles) {
		// Pattern p = Pattern.compile("(13|15|18)\\d{9}");
		return Pattern.matches("(\\+?86)?1[0-9]{10}", mobiles);
	}

	/**
	 * 验证邮箱是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		return Pattern.matches("[a-zA-Z0-9-_]+@[a-zA-Z0-9]+.[A-Za-z]{2,3}(.[A-Za-z]{2})?", email);
	}

	/**
	 * 判断日期格式:yyyy-mm-dd
	 * 
	 * @param sDate
	 * @return
	 */
	public static boolean isValidDate(String date) {
		String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
		String datePattern2 = "^((\\d{2}(([02468][048])|([13579][26]))" + "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
				+ "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
				+ "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		return Pattern.matches(datePattern1, date) && Pattern.matches(datePattern2, date);
	}

}
