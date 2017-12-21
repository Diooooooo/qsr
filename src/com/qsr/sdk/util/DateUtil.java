package com.qsr.sdk.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {

	private static long mills_per_second = 1000;
	private static long mills_per_minute = 60 * mills_per_second;
	private static long mills_per_hour = 60 * mills_per_minute;
	private static long mills_per_day = 24 * mills_per_hour;

	private static long mills_per_day_2 = TimeUnit.DAYS.toMillis(1);

	private static String format_default = "yyyy-MM-dd HH:mm:ss";

	private static String format_iso8601 = "yyyy-MM-ddTHH:mm:ss";

	// RFC 822 Date Format
	private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

	// ISO 8601 format
	private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	// Alternate ISO 8601 format without fractional seconds
	private static final String ALTERNATIVE_ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * Formats Date to GMT string.
	 */
	public static String formatRfc822Date(Date date) {
		return getRfc822DateFormat().format(date);
	}

	/**
	 * Parses a GMT-format string.
	 */
	public static Date parseRfc822Date(String dateString) throws ParseException {
		return getRfc822DateFormat().parse(dateString);
	}

	private static DateFormat getRfc822DateFormat() {
		SimpleDateFormat rfc822DateFormat = new SimpleDateFormat(
				RFC822_DATE_FORMAT, Locale.US);
		rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));

		return rfc822DateFormat;
	}

	public static String formatIso8601Date(Date date) {
		return getIso8601DateFormat().format(date);
	}

	public static String formatAlternativeIso8601Date(Date date) {
		return getAlternativeIso8601DateFormat().format(date);
	}

	/**
	 * Parse a date string in the format of ISO 8601.
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date parseIso8601Date(String dateString)
			throws ParseException {
		try {
			return getIso8601DateFormat().parse(dateString);
		} catch (ParseException e) {
			return getAlternativeIso8601DateFormat().parse(dateString);
		}
	}

	private static DateFormat getIso8601DateFormat() {
		SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT,
				Locale.US);
		df.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return df;
	}

	private static DateFormat getAlternativeIso8601DateFormat() {
		SimpleDateFormat df = new SimpleDateFormat(
				ALTERNATIVE_ISO8601_DATE_FORMAT, Locale.US);
		df.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return df;
	}

	public static void setDefaultFormat(String format) {
		format_default = format;
	}

	public static String format(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
//		dateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return dateFormat.format(date);
	}

	public static String format(Date date) {
		return format(date, format_default);
	}

	public static String format(String format) {
		return format(new Date(), format);
	}

	public static String format() {
		return format(new Date(), format_default);
	}

	public static String formatIso8061(Date date) {
		return format(new Date(), format_iso8601);
	}

	// static public long utilNextDay() {
	//
	// Calendar.getInstance();
	//
	// // new Cal
	// // Date d=new Date();
	// // d.get
	// }

	static public Date getSpanDays(Date d, int days) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d);
		// c.setTimeInMillis(millis);
		c1.add(Calendar.DATE, days);

		Calendar c2 = Calendar.getInstance();
		c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
				c1.get(Calendar.DATE));
		return c2.getTime();
	}

	public static long now2nextDayMilli(Date date, int days) {
		Date nextDay = DateUtils.addDays(date, 1);
		nextDay = DateUtils.truncate(nextDay, Calendar.DATE);
		return nextDay.getTime() - date.getTime();
	}

	public static long now2nextDayMilli() {
		return now2nextDayMilli(new Date(), 1);
	}

	public static Date getDay(Date date) {
		long time = date.getTime();

		time = time / mills_per_day_2 * mills_per_day_2;
		return new Date(time);

	}

	public static long getDayTime() {

		return getDayTime(System.currentTimeMillis());

	}

	public static long getDayTime(long time) {

		time = time / mills_per_day_2 * mills_per_day_2;
		return time;

	}

	public static Date getYesterday() {
		Date date = new Date();
		date = DateUtils.addDays(date, -1);
		return getDay(date);
	}

	public static Date getToday() {
		return getDay(new Date());
	}

	private static long getMilliseconds(int days, int hours, int minutes,
			int seconds) {
		long milliseconds = 0;
		milliseconds += days * mills_per_day;
		milliseconds += hours * mills_per_hour;
		milliseconds += minutes * mills_per_minute;
		milliseconds += seconds * mills_per_second;
		return milliseconds;

	}

	public static Date floor(Date date, int days, int hours, int minutes,
			int seconds) {
		return floor(date, getMilliseconds(days, hours, minutes, seconds));
	}

	public static long floor(long time, int days, int hours, int minutes,
			int seconds) {

		return floor(time, getMilliseconds(days, hours, minutes, seconds));
	}

	public static long floor(long time, long milliseconds) {
		long result = time / milliseconds;
		return result * milliseconds;
	}

	public static Date floor(Date date, long milliseconds) {
		return new Date(floor(date.getTime(), milliseconds)
				- getMilliseconds(0, 8, 0, 0));
	}
}
