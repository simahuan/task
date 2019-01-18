package android.izy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class DateUtils {

	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
	private static final String ISO_TIME_NO_T_FORMAT = "HH:mm:ss";
	public static final String ISO_DATE_TIME_NO_T_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 获得当前日期时间
	 * 
	 * @return
	 */
	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 获得当前日期时间
	 * <p>
	 * 日期时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		SimpleDateFormat df = new SimpleDateFormat(ISO_DATE_TIME_NO_T_FORMAT, Locale.getDefault());
		return df.format(newDate());
	}

	/**
	 * 格式化日期时间
	 * <p>
	 * 日期时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String formatDateTime(Date date) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(ISO_DATE_TIME_NO_T_FORMAT, Locale.getDefault());
			return df.format(date);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 将字符串日期时间转换成java.util.Date类型
	 * <p>
	 * 日期时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param datetime
	 * @return
	 */
	public static Date parseDateTime(String datetime) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(ISO_DATE_TIME_NO_T_FORMAT, Locale.getDefault());
			return df.parse(datetime);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 格式化日期
	 * <p>
	 * 日期格式yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String formatDate(Date date) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault());
			return df.format(date);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 将字符串日期转换成java.util.Date类型
	 * <p>
	 * 日期时间格式yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String date) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault());
			return df.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 格式化时间
	 * <p>
	 * 时间格式HH:mm:ss
	 * 
	 * @return
	 */
	public static String formatTime(Date date) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(ISO_TIME_NO_T_FORMAT, Locale.getDefault());
			return df.format(date);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 将字符串日期转换成java.util.Date类型
	 * <p>
	 * 时间格式 HH:mm:ss
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static Date parseTime(String time) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(ISO_TIME_NO_T_FORMAT, Locale.getDefault());
			return df.parse(time);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 格式化日期时间
	 * 
	 * @param date
	 * @param pattern
	 *            格式化模式，详见{@link SimpleDateFormat}构造器
	 *            <code>SimpleDateFormat(String pattern)</code>
	 * @return
	 */
	public static String format(Date date, String pattern) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
			return format.format(date);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 根据自定义pattern将字符串日期转换成java.util.Date类型
	 * 
	 * @param datetime
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String date, String pattern) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(ISO_DATE_TIME_NO_T_FORMAT, Locale.getDefault());
			df.applyPattern(pattern);
			return df.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 创建一个日期
	 * 
	 * @return
	 */
	public static Date newDate() {
		return new Date();
	}

	/**
	 * 创建一个日期
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public static Date newDate(int year, int month, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return cal.getTime();
	}

	/**
	 * 获取Date的年份
	 * 
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		return now.get(Calendar.YEAR);
	}

	/**
	 * 获取Date的月份，1-12月
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取Date的日
	 * 
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取Date中的小时(24小时)
	 * 
	 * @param date
	 * @return
	 */
	public static int getHour(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取Date中的分钟
	 * 
	 * @param date
	 * @return
	 */
	public static int getMinute(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(date);
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * 获取Date中的秒
	 * 
	 * @param date
	 * @return
	 */
	public static int getSecond(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(date);
		return cal.get(Calendar.SECOND);
	}

	/**
	 * 创建一个日期
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date newDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * 日期加减
	 * 
	 * @param date
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public static Date add(Date date, int year, int month, int dayOfMonth) {
		return add(date, year, month, dayOfMonth, 0, 0, 0);
	}

	/**
	 * 日期加减
	 * 
	 * @param date
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date add(Date date, int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.add(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.add(Calendar.MINUTE, minute);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * 判断原日期是否在目标日期之前
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public static boolean isBefore(Date src, Date dst) {
		return src.before(dst);
	}

	/**
	 * 判断原日期是否在目标日期之后
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public static boolean isAfter(Date src, Date dst) {
		return src.after(dst);
	}

	/**
	 * 判断某个日期是否在某个日期范围
	 * 
	 * @param beginDate
	 *            日期范围开始
	 * @param endDate
	 *            日期范围结束
	 * @param src
	 *            需要判断的日期
	 * @return
	 */
	public static boolean between(Date beginDate, Date endDate, Date src) {
		return beginDate.before(src) && endDate.after(src);
	}

	/**
	 * 计算两个日期相隔的天数
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static long diffDate(Date beginDate, Date endDate) {
		return (beginDate.getTime() - endDate.getTime()) / (24 * 60 * 60 * 1000);
	}

	/**
	 * 秒转为HH:mm:ss
	 * 
	 * @param seconds
	 * @return
	 */
	public static String toTimeString(int seconds) {
		long hours = seconds / 3600, remainder = seconds % 3600, minutes = remainder / 60, secs = remainder % 60;
		return ((hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (secs < 10 ? "0" : "") + secs);
	}

	/**
	 * @param s
	 *            A string representing hours, minutes, seconds, e.g.
	 *            <code>11:23:44</code>
	 * @return The converted number of seconds.
	 */
	public static long fromTimeString(String s) {
		// Handle "00:00:00.000" pattern, drop the milliseconds
		if (s.lastIndexOf(".") != -1)
			s = s.substring(0, s.lastIndexOf("."));
		String[] split = s.split(":");
		if (split.length != 3)
			throw new IllegalArgumentException("Can't parse time string: " + s);
		return (Long.parseLong(split[0]) * 3600) + (Long.parseLong(split[1]) * 60) + (Long.parseLong(split[2]));
	}
}
