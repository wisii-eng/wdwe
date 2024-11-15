/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package com.wisii.edit.validator.FieldCheck;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class CommFieldCheck{
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * <p>判空，输入域为null；值长度小于1；空格</p>
	 *
	 * @param value 要判断的字符串.
	 * @return false - 如果字符串value为null、空格、长度小于1
	 */
	public static boolean isBlankOrNull(String value) {
		return ((value != null) && (value.trim().length() >0));
	}
	/**
	 * <p>是否可以被转换成为有效的byte类型数据</p>
	 *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的byte类型数据
	 */
	public static boolean isByte(String value) {
		return (CommFieldCheck.formatByte(value) != null);
	}

	/**
	 *  转换字符串为Byte对象
	 *
	 *@param  value  字符串
	 *@return Byte 转换后的对象
	 */
	public static Byte formatByte(String value) {
		if (value == null) {
			return null;
		}

		try {
			return Byte.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	/**
	 * <p>是否可以被转换成为有效的double类型数据</p>
	 *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的double类型数据
	 */
	public static boolean isDouble(String value) {
		return (CommFieldCheck.formatDouble(value) != null);
	}
	/**
	 *  转换字符串为Double对象
	 *
	 *@param  value  字符串
	 *@return Double 转换后的对象
	 */
	public static Double formatDouble(String value) {
		if (value == null) {
			return null;
		}

		try {
			return Double.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}
	/**
	 * <p>是否可以被转换成为有效的float类型数据</p>
	 *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的float类型数据
	 */
	public static boolean isFloat(String value) {
		return (CommFieldCheck.formatFloat(value) != null);
	}
	/**
	 *  转换字符串为Float对象
	 *
	 *@param  value  字符串
	 *@return Float 转换后的对象.
	 */
	public static Float formatFloat(String value) {
		if (value == null) {
			return null;
		}

		try {
			return Float.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}
	/**
	 * <p>是否可以被转换成为有效的int类型数据</p>
	 *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的int类型数据
	 */
	public static boolean isInt(String value) {
		return (CommFieldCheck.formatInt(value) != null);
	}
	/**
	 *  转换字符串为Integer对象
	 *
	 *@param  value  字符串
	 *@return Integer 转换后的对象.
	 */
	public static Integer formatInt(String value) {
		if (value == null) {
			return null;
		}

		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}
	/**
	 * <p>是否可以被转换成为有效的long类型数据</p>
	 *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的long类型数据
	 */
	public static boolean isLong(String value) {
		return (CommFieldCheck.formatLong(value) != null);
	}
	/**
	 *  转换字符串为Long对象
	 *
	 *@param  value  字符串
	 *@return Long 转换后的对象.
	 */
	public static Long formatLong(String value) {
		if (value == null) {
			return null;
		}

		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}
	/**
	 * <p>是否可以被转换成为有效的short类型数据</p>
	 *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的short类型数据
	 */
	public static boolean isShort(String value) {
		return (CommFieldCheck.formatShort(value) != null);
	}
	/**
	 *  转换字符串为Short对象
	 *
	 *@param  value  字符串
	 *@return Short 转换后的对象.
	 */
	public static Short formatShort(String value) {
		if (value == null) {
			return null;
		}

		try {
			return Short.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}
	/**
	 * <p>当前域的值是否符合正则表达式</p>
	 *
	 * @param value 当前域的值
	 * @param regexp 正则表达式
	 * @return true 如果当前域的值符合正则表达式
	 */
	public static boolean matchRegexp(String value, String regexp) {
		if (!CommFieldCheck.isBlankOrNull(regexp) || value==null) {
			return false;
		}
		return Pattern.matches( regexp , value);
	}
	/**
	 * <p>当前域的值是否小于等于最大长度</p>
	 *
	 * @param value 当前域的值
	 * @param max 最大长度
	 * @return true 如果当前域的值小于等于最大长度
	 */
	public static boolean maxLength(String value, String max) {
		int m = Integer.parseInt(max);
		return (value.length() <= m);
	}

	/**
	 * <p>当前域的值是否大于等于最小长度</p>
	 *
	 * @param value 当前域的值
	 * @param min 最小长度
	 * @return true 如果当前域的值大于等于最小长度
	 */
	public static boolean minLength(String value, String min) {
		int m = Integer.parseInt(min);
		return (value.length() >= m);
	}
	/**
	 * <p>定义数值的上下限。所允许的值必需在两个值之间</p>
	 *
	 * @param value 数值
	 * @param min 数值上限
	 * @param max 数值下限
	 * @return true 如果所允许的值在两个值之间
	 */
	public static boolean isInRangeWithByte(String value, String min, String max) {
		Byte bValue = formatByte(value);
		Byte bMin = formatByte(min);
		Byte bMax = formatByte(max);
		return ((bValue >= bMin) && (bValue <= bMax));
	}
	/**
	 * <p>定义数值的上下限。所允许的值必需在两个值之间</p>
	 *
	 * @param value 数值
	 * @param min 数值上限
	 * @param max 数值下限
	 * @return true 如果所允许的值在两个值之间
	 */
	public static boolean isInRangeWithInt(String value, String min, String max) {
		Integer intValue = formatInt(value);
		Integer intMin = formatInt(min);
		Integer intMax = formatInt(max);
		return ((intValue >= intMin) && (intValue <= intMax));
	}
	/**
	 * <p>定义数值的上下限。所允许的值必需在两个值之间</p>
	 *
	 * @param value 数值
	 * @param min 数值上限
	 * @param max 数值下限
	 * @return true 如果所允许的值在两个值之间
	 */
	public static boolean isInRangeWithLong(String value, String min, String max) {
		Long longValue = formatLong(value);
		Long longMin = formatLong(min);
		Long longMax = formatLong(max);
		return ((longValue >= longMin) && (longValue <= longMax));
	}
	/**
	 * <p>定义数值的上下限。所允许的值必需在两个值之间</p>
	 *
	 * @param value 数值
	 * @param min 数值上限
	 * @param max 数值下限
	 * @return true 如果所允许的值在两个值之间
	 */
	public static boolean isInRangeWithFloat(String value, String min, String max) {
		Float floatValue = formatFloat(value);
		Float floatMin = formatFloat(min);
		Float floatMax = formatFloat(max);
		return ((floatValue >= floatMin) && (floatValue <= floatMax));
	}
	/**
	 * <p>定义数值的上下限。所允许的值必需在两个值之间</p>
	 *
	 * @param value 数值
	 * @param min 数值上限
	 * @param max 数值下限
	 * @return true 如果所允许的值在两个值之间
	 */
	public static boolean isInRangeWithDouble(String value, String min, String max) {
		Double dValue = formatDouble(value);
		Double dMin = formatDouble(min);
		Double dMax = formatDouble(max);
		return ((dValue >= dMin) && (dValue <= dMax));
	}
	/**
	 * <p>定义数值的上下限。所允许的值必需在两个值之间</p>
	 *
	 * @param value 数值
	 * @param min 数值上限
	 * @param max 数值下限
	 * @return true 如果所允许的值在两个值之间
	 */
	public static boolean isInRangeWithShort(String value, String min, String max) {
		Short sValue = formatShort(value);
		Short sMin = formatShort(min);
		Short sMax = formatShort(max);
		return ((sValue >= sMin) && (sValue <= sMax));
	}
	/**
	 * <p>当前域的值是否小于等于最大值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最大值
	 * @return true 如果当前域的值小于等于最大值
	 */
	public static boolean maxValueWithInt(String value, String max) {
		Integer intValue = formatInt(value);
		Integer intMax = formatInt(max);
		return (intValue <= intMax);
	}
	/**
	 * <p>当前域的值是否小于等于最大值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最大值
	 * @return true 如果当前域的值小于等于最大值
	 */
	public static boolean maxValueWithByte(String value, String max) {
		Byte bValue = formatByte(value);
		Byte bMax = formatByte(max);
		return (bValue <= bMax);
	}
	/**
	 * <p>当前域的值是否小于等于最大值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最大值
	 * @return true 如果当前域的值小于等于最大值
	 */
	public static boolean maxValueWithLong(String value, String max) {
		Long lValue = formatLong(value);
		Long lMax = formatLong(max);
		return (lValue <= lMax);
	}
	/**
	 * <p>当前域的值是否小于等于最大值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最大值
	 * @return true 如果当前域的值小于等于最大值
	 */
	public static boolean maxValueWithShort(String value, String max) {
		Short sValue = formatShort(value);
		Short sMax = formatShort(max);
		return (sValue <= sMax);
	}
	/**
	 * <p>当前域的值是否小于等于最大值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最大值
	 * @return true 如果当前域的值小于等于最大值
	 */
	public static boolean maxValueWithFloat(String value, String max) {
		Float fValue = formatFloat(value);
		Float fMax = formatFloat(max);
		return (fValue <= fMax);
	}
	/**
	 * <p>当前域的值是否小于等于最大值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最大值
	 * @return true 如果当前域的值小于等于最大值
	 */
	public static boolean maxValueWithDouble(String value, String max) {
		Double dValue = formatDouble(value);
		Double dMax = formatDouble(max);
		return (dValue <= dMax);
	}
	/**
	 * <p>当前域的值是否大于等于最小值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最小值
	 * @return true 如果当前域的值大于等于最小值
	 */
	public static boolean minValueWithInt(String value, String min) {
		Integer intValue = formatInt(value);
		Integer intMin = formatInt(min);
		return (intValue >= intMin);
	}
	/**
	 * <p>当前域的值是否大于等于最小值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最小值
	 * @return true 如果当前域的值大于等于最小值
	 */
	public static boolean minValueWithLong(String value, String min) {
		Long lValue = formatLong(value);
		Long lMin = formatLong(min);
		return (lValue >= lMin);
	}
	/**
	 * <p>当前域的值是否大于等于最小值</p>
	 *
	 * @param value 当前域的值
	 * @param max 最小值
	 * @return true 如果当前域的值大于等于最小值
	 */
	public static boolean minValueWithShort(String value, String min) {
		Short sValue = formatShort(value);
		Short sMin = formatShort(min);
		return (sValue >= sMin);
	}
	/**
	 * <p>当前域的值是否大于等于最小值</p>
	 *
	  * @param value 当前域的值
	 * @param max 最小值
	 * @return true 如果当前域的值大于等于最小值
	 */
	public static boolean minValueWithByte(String value, String min) {
		Byte bValue = formatByte(value);
		Byte bMin = formatByte(min);
		return (bValue >= bMin);
	}
	/**
	 * <p>当前域的值是否大于等于最小值</p>
	 *
	  * @param value 当前域的值
	 * @param max 最小值
	 * @return true 如果当前域的值大于等于最小值
	 */
	public static boolean minValueWithFloat(String value, String min) {
		Float fValue = formatFloat(value);
		Float fMin = formatFloat(min);
		return (fValue >= fMin);
	}
	/**
	 * <p>当前域的值是否大于等于最小值</p>
	 *
	  * @param value 当前域的值
	 * @param max 最小值
	 * @return true 如果当前域的值大于等于最小值
	 */
	public static boolean minValueWithDouble(String value, String min) {
		Double dValue = formatDouble(value);
		Double dMin = formatDouble(min);
		return (dValue >= dMin);
	}
	/**
	 * <p>当前日期是否在某日之前</p>
	 *
	 * @param date 当前日期对象
	 * @param when 日期字符串
	 * @return true 如果当前日期是否在某日之前，否则返回false
	 */
	public static boolean isBeforeDate(Date date,String when){
		return CommFieldCheck.isBeforeDate(date, when, DEFAULT_DATE_FORMAT);
	}
	/**
	 * <p>当前日期是否在某日之前</p>
	 *
	 * @param date 当前日期对象
	 * @param when 日期字符串
	 * @param dateFormat 日期格式字符串
	 * @return true 如果当前日期是否在某日之前，否则返回false
	 */
	public static boolean isBeforeDate(Date date,String when,String dateFormat){
		if (!CommFieldCheck.isBlankOrNull(when) || !CommFieldCheck.isBlankOrNull(dateFormat)) {
			return false;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		Date whenDate = null;
		try {
			whenDate = formatter.parse(when);
		} catch (ParseException e) {
			return false;
		}
		if (date.before(whenDate)) {
			return false;
		}
		return true;
	}
	/**
	 * <p>当前日期是否在某日之后</p>
	 *
	 * @param date 当前日期对象
	 * @param when 日期字符串
	 * @return true 如果当前日期是否在某日之后，否则返回false
	 */
	public static boolean isAfterDate(Date date,String when){
		return CommFieldCheck.isAfterDate(date, when, DEFAULT_DATE_FORMAT);
	}
	/**
	 * <p>当前日期是否在某日之后</p>
	 *
	 * @param date 当前日期对象
	 * @param when 日期字符串
	 * @param dateFormat 日期格式字符串
	 * @return true 如果当前日期是否在某日之后，否则返回false
	 */
	public static boolean isAfterDate(Date date,String when,String dateFormat){

		if (!CommFieldCheck.isBlankOrNull(when) || !CommFieldCheck.isBlankOrNull(dateFormat)) {
			return false;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		Date whenDate = null;
		try {
			whenDate = formatter.parse(when);
		} catch (ParseException e) {
			return false;
		}
		if (date.after(whenDate)) {
			return false;
		}
		return true;
	}
	/**
	 * <p>当前日期是否在有效的日起范围内</p>
	 *
	 * @param date 当前日期对象
	 * @param minDate 起始日期字符串
	 * @param maxDate 结束日期字符串
	 * @return true 如果当前日期是否在某日之后，否则返回false
	 */
	public static boolean isInRangeOfDate(Date date,String minDate,String maxDate){
		return CommFieldCheck.isInRangeOfDate(date, minDate, maxDate, DEFAULT_DATE_FORMAT);
	}
	/**
	 * <p>当前日期是否在有效的日起范围内</p>
	 *
	 * @param date 当前日期对象
	 * @param minDate 起始日期字符串
	 * @param maxDate 结束日期字符串
	 * @param dateFormat 日期格式字符串
	 * @return true 如果当前日期是否在某日之后，否则返回false
	 */
	public static boolean isInRangeOfDate(Date date,String minDate,String maxDate,String dateFormat){
		if (!CommFieldCheck.isBlankOrNull(minDate) || !CommFieldCheck.isBlankOrNull(maxDate)
				||!CommFieldCheck.isBlankOrNull(dateFormat)) {
			return false;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		Date min = null;
		Date max = null;
		try {
			min = formatter.parse(minDate);
			max=formatter.parse(maxDate);
		} catch (ParseException e) {
			return false;
		}
		if (date.equals(min) && date.equals(max)) {
			return true;
		}
		if (date.after(min) && date.before(max)) {
			return true;
		}
		return false;
	}

}
