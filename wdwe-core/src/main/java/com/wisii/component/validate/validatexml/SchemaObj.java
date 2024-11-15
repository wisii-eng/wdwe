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
 *//**
 *    实例为发到客户端的验证数据对象
 *    Version 1.0
 *    汇智互联
 */
package com.wisii.component.validate.validatexml;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.validator.ValidatorFactory;
import com.wisii.fov.render.awt.viewer.Translator;

public class SchemaObj {

	/**
	 * 内置基本数据类型常量
	 */
	private final String XSD_CSTRING = "string"; // 由字符组成的字符串

	private static final String XSD_CBOOLEAN = "boolean"; // 布尔值

	private final String XSD_CDECIMAL = "decimal"; // 各种精度的数字 BigDecimal

	private final String XSD_CFLOAT = "float"; // 单精度浮点型

	private final String XSD_CDOUBLE = "double"; // 双精度浮点型

	// 格式：PYYMMDDTHHMMSS

	private final String XSD_CDATETIME = "dateTime"; // 指定的日期/时间数据

	// 格式：CCYY-MM-DDThh:mm:ss

	private final String XSD_CTIME = "time"; // 指定时间数据 格式 ：hh:mm:ss

	private final String XSD_CDATE = "date"; // 指定月历的日期数据 格式：CCYY-MM-DD

	private final String XSD_CGYEARMONTH = "gYearMonth"; // 定义了日期的一部分——年和月部分(YYYY-MM)

	private final String XSD_CGYEAR = "gYear"; // 定义了日期的年部分(YYYY)

	private final String XSD_CGMONTHDAY = "gMonthDay"; // 定义了日期的一部分——月和日部分(MM-DD)

	private final String XSD_CGDAY = "gDay"; // 定义了日期的部分定义了时间的一部分——日部分(DD)

	private final String XSD_CGMONTH = "gMonth"; // 定义了日期的一部分——月部分(MM)

	private final String XSD_CANYURI = "anyURI"; // URL网址

	private final String XSD_CINTEGER = "integer"; // 由基本类型派生，表示整数

	private final String XSD_CNONPOSITIVEINTEGER = "nonPositiveInteger"; // 源自integer，表示负无穷到等于0的整数

	private final String XSD_CNEGATIVEINTEGER = "negativeInteger"; // 源自nonPositiveInteger，表示负无穷到小于0的整数

	private final String XSD_CLONG = "long"; // 源自integer，表示长整数

	private final String XSD_CINT = "int"; // 由long派生的整数，范围在-2147483648~2147483647

	private final String XSD_CSHORT = "short"; // 源自int的短数据，范围在-32768~32767

	private final String XSD_CBYTE = "byte"; // 源自short位整数，范围在-128~127

	private final String XSD_CNONNEGATIVEINTEGER = "nonNegativeInteger"; // 源自integer，表示等于0到正无穷的整数

	private final String XSD_CUNSIGNEDLONG = "unsignedLong"; // 源自nonNegativeInteger，表示无符号长整数

	private final String XSD_CUNSIGNEDINT = "unsignedInt"; // 由unsignedLong派生的无符号整数，取值范围为0~4294967295

	private final String XSD_CUNSIGNEDSHORT = "unsignedShort"; // 源自unsignedInt的无符号短整数，取值范围0~65535

	private final String XSD_CUNSIGNEDBYTE = "unsignedByte"; // 源自unsignedShort的无符号位整数，取值范围0~255

	private final String XSD_CPOSITIVEINTEGER = "positiveInteger"; // 源自nonNegativeInteger，表示大于0到正无穷的整数

	/**
	 * 属性名常量
	 */
	private static final String XSD_TYPE = "type"; // 在该架构（或由指定的命名空间指示的其他架构）中定义的内置数据类型或简单类型的名称

	private final String XSD_FIXED = "fixed"; // 属性具有的固定值

	/**
	 * 对数据类型的约束
	 */

	private final String XSD_FRACTIONDIGITS = "fractionDigits"; // 指定了允许的小数位数的最多位数，必须大于等于0

	private final String XSD_LENGTH = "length"; // 指定了允许的字符或列表项的个数，必须大于等于0

	private final String XSD_MAXLENGTH = "maxLength"; // 指定了所允许的字符或列表项的最多个数。必须大于等于0

	private final String XSD_MINLENGTH = "minLength"; // 指定了所允许的字符或列表的最少个数。必须等于大于0个

	private final String XSD_MAXEXCLUSIVE = "maxExclusive"; // 指定了数值的上限（数值要比这个值小）

	private final String XSD_MAXINCLUSIVE = "maxInclusive"; // 指定了数值上限（数值必须小于等于这个值）

	private final String XSD_MINEXCLUSIVE = "minExclusive"; // 指定了数值的下限

	// （数值要比这个值大）

	private final String XSD_MININCLUSIVE = "minInclusive"; // 指定了数值的下限（数值必须大于等于这个值）

	private final String XSD_PATTERN = "pattern"; // 定义了符合要求的字符的确切排列顺序

	private final String XSD_TOTALDIGITS = "totalDigits"; // 指定了所允许的字符的确切个数，必须大于0






	/**
	 * 检查叶子节点或属性的数据是否符合schema规则
	 * 
	 * @param path
	 *            String
	 * @param data
	 *            String
	 * @return String
	 */
	public String checkXmlData(String path, String data) {
		
		if(ValidatorFactory.schemaMap==null) return null;
		int left = path.indexOf('[');
		int right = path.indexOf(']');
		while (left != -1 && right != -1) {
			String m = path.substring(0, left);
			String n = path.substring(right + 1);
			path = m.concat(n);
			left = path.indexOf('[');
			right = path.indexOf(']');
		}

		if ((path != null) && (data != null)) // path,data不空才做判断
		{
			Map map_path = (Map) ValidatorFactory.schemaMap.get(path); // 根据路径得到约束信息

			if ((map_path) != null)
				return checkType(map_path, data); // 检查type类型
			else
				return null;
		} else
			return Translator.getInstanceof().getString("xmlcheck.NoPathORData");
	}

	/**
	 * 检查叶子节点或属性的数据是否符合type
	 * 
	 * @param map_path
	 *            Map
	 * @param data
	 *            String
	 * @return String
	 */
	private String checkType(Map map_path, String data) {

		if (map_path.get("nillable") != null) {
			if (map_path.get("nillable").equals("false") && data.equals(""))
				return Translator.getInstanceof().getString("xmlcheck.nillable");
		}

		data = data.trim();

		if ((map_path.get(XSD_TYPE) != null)) // type不空，检查type否则调用comPare(map_path,
		// data)检查约束
		{
			if (map_path.get(XSD_TYPE).equals(XSD_CINTEGER))
				return checkNAN(map_path, data, Translator.getInstanceof()
						.getString("xmlcheck.integer"));
			else if (map_path.get(XSD_TYPE).equals(XSD_CNONPOSITIVEINTEGER)) // 表示负无穷到等于0的整数
			{
				if (data.indexOf("-") == -1) {
					if (data.equals("0") || data.equals("+0"))
						return checkNAN(map_path, data, Translator.getInstanceof()
								.getString("xmlcheck.nonPositiveInteger"));
					else
						return Translator.getInstanceof()
								.getString("xmlcheck.nonPositiveInteger");

				} else
					return checkNAN(map_path, data, Translator.getInstanceof()
							.getString("xmlcheck.nonPositiveInteger"));

			} else if (map_path.get(XSD_TYPE).equals(XSD_CDECIMAL)) {
				try {
					new BigDecimal(data);

				} catch (NumberFormatException e) {
					return Translator.getInstanceof().getString("xmlcheck.bigdecimal");
				}
			} else if (map_path.get(XSD_TYPE).equals(XSD_CNEGATIVEINTEGER)) // 表示负无穷到小于0的整数
			{
				if ((data.indexOf("-") == -1) || (data.equals("-0")))
					return Translator.getInstanceof().getString("xmlcheck.negativeInteger");
				else
					return checkNAN(map_path, data, Translator.getInstanceof()
							.getString("xmlcheck.negativeInteger"));

			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CNONNEGATIVEINTEGER)) // 表示等于0到正无穷的整数
			{
				if (data.indexOf("-") >= 0) {
					if (data.equals("-0"))
						return checkNAN(map_path, data, Translator.getInstanceof()
								.getString("xmlcheck.nonNegativeInteger"));
					else
						return Translator.getInstanceof()
								.getString("xmlcheck.nonNegativeInteger");

				} else
					return checkNAN(map_path, data, Translator.getInstanceof()
							.getString("xmlcheck.nonNegativeInteger"));

			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CPOSITIVEINTEGER)) // 表示大于0到正无穷的整数
			{
				if ((data.indexOf("-") >= 0) || (data.equals("0"))
						|| (data.equals("+0")))
					return Translator.getInstanceof().getString("xmlcheck.positiveInteger");
				else
					return checkNAN(map_path, data, Translator.getInstanceof()
							.getString("xmlcheck.positiveInteger"));

			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CLONG)) // 判断长整数
			{
				try {
					Long.parseLong(data);
				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.long");
				}
			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CINT)) // 判断int
			{
				try {
					Integer.parseInt(data);

				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.int");
				}
			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CSHORT)) // 判断short
			{
				try {
					Short.parseShort(data);

				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.short");
				}
			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CBYTE)) // 判断type
			{
				try {
					Byte.parseByte(data);

				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.byte");
				}
			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CUNSIGNEDLONG)) // 判断无符号长整数(整数部分超出能处理的范围)
			{
			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CUNSIGNEDINT)) // 判断无符号整数
			{
				try {

					if ((Double.parseDouble(data) < 0)
							|| (Double.parseDouble(data) > 4294967295.0)
							|| (data.indexOf(".") > -1))
						return Translator.getInstanceof().getString("xmlcheck.unsingedint");

				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.unsingedint");
				}
			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CUNSIGNEDSHORT)) // 判断无符号短整数
			{
				try {

					if ((Double.parseDouble(data) < 0)
							|| (Double.parseDouble(data) > 65535)
							|| (data.indexOf(".") > -1))
						return Translator.getInstanceof().getString("xmlcheck.unsingedshort");

				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.unsingedshort");
				}
			}

			else if (map_path.get(XSD_TYPE).equals(XSD_CUNSIGNEDBYTE)) // 判断无符号位整数
			{
				try {

					if ((Double.parseDouble(data) < 0)
							|| (Double.parseDouble(data) > 255)
							|| (data.indexOf(".") > -1))
						return Translator.getInstanceof().getString("xmlcheck.unsingedbyte");
				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.unsingedbyte");
				}
			}

			else if (XSD_CSTRING.equals(map_path.get("type"))) {
			} // 判断string型

			else if (XSD_CBOOLEAN.equals(map_path.get("type"))) // 判断boolean型
			{
				if (!(data.equalsIgnoreCase("true") || data
						.equalsIgnoreCase("false")))
					return Translator.getInstanceof().getString("xmlcheck.boolean");
			} else if (XSD_CFLOAT.equals(map_path.get("type"))) // 判断float型
			{
				try {
					if (Float.isInfinite(Float.parseFloat(data)))
						return Translator.getInstanceof().getString("xmlcheck.float");
					else if (data.indexOf(".") > -1) {
						if (data.length() > 40)
							return Translator.getInstanceof().getString("xmlcheck.float");

					} else if (data.length() > 39)
						return Translator.getInstanceof().getString("xmlcheck.float");

				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.float");
				}
			}

			else if (XSD_CDOUBLE.equals(map_path.get("type"))) // 判断double型
			{
				try {
					if (Double.isInfinite(Double.parseDouble(data)))
						return Translator.getInstanceof().getString("xmlcheck.double");
					else if (data.indexOf(".") > -1) {
						if (data.length() > 310)
							return Translator.getInstanceof().getString("xmlcheck.double");

					} else if (data.length() > 309)
						return Translator.getInstanceof().getString("xmlcheck.double");

				} catch (Exception e) {
					return Translator.getInstanceof().getString("xmlcheck.double");
				}
			}

			else if (XSD_CTIME.equals(map_path.get("type"))) // 判断time型
			{
				if ((data.length() != 8) && (data.length() != 10)
						&& (data.length() != 11) && (data.length() != 12))
					return Translator.getInstanceof().getString("xmlcheck.time");
				SimpleDateFormat bartDateFormat = new SimpleDateFormat(
						"hh:mm:ss");
				try {
					bartDateFormat.parse(data);
					int h = (bartDateFormat.getCalendar())
							.get(Calendar.HOUR_OF_DAY);
					if (h == 0) {
						h = Integer.parseInt(data.substring(0, 2));
					}
					int mm = (bartDateFormat.getCalendar())
							.get(Calendar.MINUTE);
					int s = (bartDateFormat.getCalendar()).get(Calendar.SECOND);
					if (!(h == Integer.parseInt(data.substring(0, 2))
							&& mm == Integer.parseInt(data.substring(3, 5)) && s == Integer
							.parseInt(data.substring(6, 8))))
						return Translator.getInstanceof().getString("xmlcheck.time");

				} catch (Exception e) {
					e.printStackTrace(); // 格式化时出错，输入类型不正确
					return Translator.getInstanceof().getString("xmlcheck.time");
				}
			}

			else if (XSD_CDATETIME.equals(map_path.get("type"))) // 判断datatime型
			{
				if ((data.length() != 19) && (data.length() != 21)
						&& (data.length() != 22) && (data.length() != 23))
					return Translator.getInstanceof().getString("xmlcheck.dateTime");
				SimpleDateFormat bartDateFormat1 = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss");
				try {
					bartDateFormat1.parse(data);
					int y = (bartDateFormat1.getCalendar()).get(Calendar.YEAR);
					int m = (bartDateFormat1.getCalendar()).get(Calendar.MONTH) + 1;
					int d = (bartDateFormat1.getCalendar()).get(Calendar.DATE);
					int h = (bartDateFormat1.getCalendar())
							.get(Calendar.HOUR_OF_DAY);
					int mm = (bartDateFormat1.getCalendar())
							.get(Calendar.MINUTE);
					int s = (bartDateFormat1.getCalendar())
							.get(Calendar.SECOND);
					if (!(y == Integer.parseInt(data.substring(0, 4))
							&& m == Integer.parseInt(data.substring(5, 7))
							&& d == Integer.parseInt(data.substring(8, 10))
							&& h == Integer.parseInt(data.substring(11, 13))
							&& mm == Integer.parseInt(data.substring(14, 16)) && s == Integer
							.parseInt(data.substring(17, 19))))
						return Translator.getInstanceof().getString("xmlcheck.dateTime");

				} catch (Exception e) {
					e.printStackTrace();
					return Translator.getInstanceof().getString("xmlcheck.dateTime");
				}
			}

			else if (XSD_CDATE.equals(map_path.get("type"))) // 判断date型
			{
				SimpleDateFormat bartDateFormat1 = new SimpleDateFormat(
						"yyyy-MM-dd");
				if (data.length() == 10) // 判断yyyy-MM-dd型
				{
					try {
						bartDateFormat1.parse(data);
						int y = (bartDateFormat1.getCalendar())
								.get(Calendar.YEAR);
						int m = (bartDateFormat1.getCalendar())
								.get(Calendar.MONTH) + 1;
						int d = (bartDateFormat1.getCalendar())
								.get(Calendar.DATE);
						if (!(y == Integer.parseInt(data.substring(0, 4))
								&& m == Integer.parseInt(data.substring(5, 7)) && d == Integer
								.parseInt(data.substring(8))))
							// return Translator.getInstanceof().getString("xmlcheck.date");
							return "日期不合法，请重新输入！";
					} catch (Exception e) {
						// e.printStackTrace();
						return Translator.getInstanceof().getString("xmlcheck.date");
					}
				} else
					return Translator.getInstanceof().getString("xmlcheck.date");
			}

			else if (XSD_CGYEARMONTH.equals(map_path.get("type"))) // 判断gyearmonth型
			{
				if (data.length() != 7)
					return Translator.getInstanceof().getString("xmlcheck.gYearMonth");
				SimpleDateFormat bartDateFormat = new SimpleDateFormat(
						"yyyy-MM");
				try {
					bartDateFormat.parse(data);
					int y = (bartDateFormat.getCalendar()).get(Calendar.YEAR);
					int m = (bartDateFormat.getCalendar()).get(Calendar.MONTH) + 1;
					if (!(y == Integer.parseInt(data.substring(0, 4)) && m == Integer
							.parseInt(data.substring(5, 7))))
						return Translator.getInstanceof().getString("xmlcheck.gYearMonth");

				} catch (Exception e) {
					e.printStackTrace();
					return Translator.getInstanceof().getString("xmlcheck.gYearMonth");
				}
			}

			else if (XSD_CGYEAR.equals(map_path.get("type"))) // 判断gyear型
			{
				if (data.length() != 4)
					return Translator.getInstanceof().getString("xmlcheck.gYear");
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy");
				try {
					bartDateFormat.parse(data);
					int y = (bartDateFormat.getCalendar()).get(Calendar.YEAR);
					if (y != Integer.parseInt(data))
						return Translator.getInstanceof().getString("xmlcheck.gYear");

				} catch (Exception e) {
					e.printStackTrace();
					return Translator.getInstanceof().getString("xmlcheck.gYear");
				}
			}

			else if (XSD_CGMONTH.equals(map_path.get("type"))) // 判断gmonth型
			{
				if (data.length() != 2)
					return Translator.getInstanceof().getString("xmlcheck.gMonth");
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("MM");
				try {
					bartDateFormat.parse(data);
					int m = (bartDateFormat.getCalendar()).get(Calendar.MONTH) + 1;
					if (m != Integer.parseInt(data))
						return Translator.getInstanceof().getString("xmlcheck.gMonth");

				} catch (Exception e) {
					e.printStackTrace();
					return Translator.getInstanceof().getString("xmlcheck.gMonth");
				}
			}

			else if (XSD_CGDAY.equals(map_path.get("type"))) // 判断gday型
			{
				if (data.length() != 2)
					return Translator.getInstanceof().getString("xmlcheck.gDay");
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("dd");
				try {
					bartDateFormat.parse(data);
					int d = (bartDateFormat.getCalendar()).get(Calendar.DATE);
					if (d != Integer.parseInt(data))
						return Translator.getInstanceof().getString("xmlcheck.gDay");

				} catch (Exception e) {
					e.printStackTrace();
					return Translator.getInstanceof().getString("xmlcheck.gDay");
				}
			}

			else if (XSD_CGMONTHDAY.equals(map_path.get("type"))) // 判断gmonthday型
			{
				if (data.length() != 5)
					return Translator.getInstanceof().getString("xmlcheck.gMonthDay");
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("MM-dd");
				try {
					bartDateFormat.parse(data);
					int m = (bartDateFormat.getCalendar()).get(Calendar.MONTH) + 1;
					int d = (bartDateFormat.getCalendar()).get(Calendar.DATE);
					if (!(d == Integer.parseInt(data.substring(0, 2)) && m == Integer
							.parseInt(data.substring(3, 5))))
						return Translator.getInstanceof().getString("xmlcheck.gMonthDay");

				} catch (Exception e) {
					e.printStackTrace();
					return Translator.getInstanceof().getString("xmlcheck.gMonthDay");
				}
			}

			else if (XSD_CANYURI.equals(map_path.get("type"))) // 判断URL型
			{
				try {
					new URL(data);

				} catch (Exception e) {
					e.printStackTrace();
					return Translator.getInstanceof().getString("xmlcheck.anyURI");
				}
			}

		}
		return comPare(map_path, data);

	}

	/**
	 * 对无穷进行判断
	 * 
	 * @param data
	 *            String
	 * @return String
	 */
	private String checkNAN(Map map_path, String data, String st) {
		int p = data.indexOf("-");
		int i = data.indexOf("+");

		if (p != -1) {
			Matcher m = Pattern.compile("[-]?[0-9]*").matcher(data);
			if (!m.matches())
				return st;
			else
				return comPare(map_path, data);

		}

		else if (i != -1) {
			String s = data.substring(i + 1);
			int j = s.indexOf("+");
			if ((i != 0) || (j != -1))
				return st;
			else {
				Matcher m = Pattern.compile("[0-9+]*").matcher(data);
				if (!m.matches())
					return st;
				else
					return comPare(map_path, data);

			}
		}

		else {
			Matcher m = Pattern.compile("[0-9]*").matcher(data);
			if (!m.matches())
				return st;
			else
				return comPare(map_path, data);
		}
	}

	/**
	 * 检查叶子节点或属性的数据是否符合约束
	 * 
	 * @param map_path
	 *            Map
	 * @param data
	 *            String
	 * @return String
	 */
	private String comPare(Map map_path, String data) {

		String base = (String) map_path.get("base");

		if (ValidatorFactory.schemaMap.get(base) != null) {
			map_path = (Map) ValidatorFactory.schemaMap.get(base);
		}

		if (map_path.get(XSD_FRACTIONDIGITS) != null) // 对小数位数进行判断，若大于规定的小数位数则返回error
		{
			try {
				Double.parseDouble(data);
				if (Integer.parseInt((String) map_path.get(XSD_FRACTIONDIGITS)) < (data
						.length()
						- data.indexOf(".") - 1))
					return Translator.getInstanceof().getString("xmlcheck.fractionDigits")
							+ Integer.parseInt((String) map_path
									.get(XSD_FRACTIONDIGITS));
			} catch (Exception e) {
				return Translator.getInstanceof().getString("xmlcheck.fractionDigits")
						+ Integer.parseInt((String) map_path
								.get(XSD_FRACTIONDIGITS));
			}
		}

		if ("string".equals(map_path.get("base"))
				&& map_path.containsKey("appinfo")) {
			if ("BYTE".equals(map_path.get("appinfo")))
			{
				try {
					if (map_path.get(XSD_LENGTH) != null) // 对长度进行判断，若不等于规定的长度，则返回error
					{
						if (Integer.parseInt((String) map_path.get(XSD_LENGTH)) != data
								.getBytes("GBK").length)
							return "字节数错误: "
									+ Translator.getInstanceof().getString("xmlcheck.length")
									+ Integer.parseInt((String) map_path
											.get(XSD_LENGTH));
					}

					if (map_path.get(XSD_MAXLENGTH) != null) // 对最大长度进行判断，若大于规定的长度，则返回error
					{
						if (data.getBytes("GBK").length > Integer
								.parseInt((String) map_path.get(XSD_MAXLENGTH)))
							return "字节数错误: "
									+ Translator.getInstanceof()
											.getString("xmlcheck.maxLength")
									+ Integer.parseInt((String) map_path
											.get(XSD_MAXLENGTH));
					}

					if (map_path.get(XSD_MINLENGTH) != null) // 对最小长度进行判断，若小于规定的长度，则返回error
					{
						if (data.getBytes("GBK").length < Integer
								.parseInt((String) map_path.get(XSD_MINLENGTH)))
							return "字节数错误: "
									+ Translator.getInstanceof()
											.getString("xmlcheck.minLength")
									+ Integer.parseInt((String) map_path
											.get(XSD_MINLENGTH));
					}
				} catch (UnsupportedEncodingException ex) {
					return "系统字符集不支持GBK格式";
				}
			} else {

			}
		} else {
			if (map_path.get(XSD_LENGTH) != null) // 对长度进行判断，若不等于规定的长度，则返回error
			{
				if (Integer.parseInt((String) map_path.get(XSD_LENGTH)) != data
						.length())
					return Translator.getInstanceof().getString("xmlcheck.length")
							+ Integer.parseInt((String) map_path
									.get(XSD_LENGTH));
			}

			if (map_path.get(XSD_MAXLENGTH) != null) // 对最大长度进行判断，若大于规定的长度，则返回error
			{
				if (data.length() > Integer.parseInt((String) map_path
						.get(XSD_MAXLENGTH)))
					return Translator.getInstanceof().getString("xmlcheck.maxLength")
							+ Integer.parseInt((String) map_path
									.get(XSD_MAXLENGTH));
			}

			if (map_path.get(XSD_MINLENGTH) != null) // 对最小长度进行判断，若小于规定的长度，则返回error
			{
				if (data.length() < Integer.parseInt((String) map_path
						.get(XSD_MINLENGTH)))
					return Translator.getInstanceof().getString("xmlcheck.minLength")
							+ Integer.parseInt((String) map_path
									.get(XSD_MINLENGTH));
			}
		}
		//

		if (map_path.get(XSD_MAXEXCLUSIVE) != null) // 判断数值的上限，若参数的值大于等于规定的值，则返回error
		{
			try {
				if (Double.parseDouble(data) >= Double
						.parseDouble((String) map_path.get(XSD_MAXEXCLUSIVE)))
					return Translator.getInstanceof().getString("xmlcheck.maxExclusive")
							+ Double.parseDouble((String) map_path
									.get(XSD_MAXEXCLUSIVE));
			} catch (Exception e) {
				return Translator.getInstanceof().getString("xmlcheck.maxExclusive")
						+ Double.parseDouble((String) map_path
								.get(XSD_MAXEXCLUSIVE));
			}
		}

		if ((map_path.get(XSD_MAXINCLUSIVE) != null)) // 判断数值的上限，若参数的值大于规定的值，则返回error
		{
			try {
				if ((Double.parseDouble(data) > Double
						.parseDouble((String) map_path.get(XSD_MAXINCLUSIVE))))
					return Translator.getInstanceof().getString("xmlcheck.maxInclusive")
							+ Double.parseDouble((String) map_path
									.get(XSD_MAXINCLUSIVE));
			} catch (Exception e) {
				return Translator.getInstanceof().getString("xmlcheck.maxInclusive")
						+ Double.parseDouble((String) map_path
								.get(XSD_MAXINCLUSIVE));
			}

		}

		if ((map_path.get(XSD_MINEXCLUSIVE) != null)) // 判断数值的下限，若参数的值小于等于规定的值，则返回error
		{
			try {
				if ((Double.parseDouble(data) <= Double
						.parseDouble((String) map_path.get(XSD_MINEXCLUSIVE))))
					return Translator.getInstanceof().getString("xmlcheck.minExclusive")
							+ Double.parseDouble((String) map_path
									.get(XSD_MINEXCLUSIVE));

			} catch (Exception e) {
				return Translator.getInstanceof().getString("xmlcheck.minExclusive")
						+ Double.parseDouble((String) map_path
								.get(XSD_MINEXCLUSIVE));
			}

		}

		if ((map_path.get(XSD_MININCLUSIVE) != null)) // 判断数值的下限，若参数的值小于规定的值，则返回error
		{
			try {
				if ((Double.parseDouble(data) < Double
						.parseDouble((String) map_path.get(XSD_MININCLUSIVE))))
					return Translator.getInstanceof().getString("xmlcheck.minInclusive")
							+ Double.parseDouble((String) map_path
									.get(XSD_MININCLUSIVE));
			} catch (Exception e) {
				return Translator.getInstanceof().getString("xmlcheck.minInclusive")
						+ Double.parseDouble((String) map_path
								.get(XSD_MININCLUSIVE));
			}

		}

		if (map_path.get(XSD_PATTERN) != null) // 判断参数是否符合正则表达式，不符合则返回error
		{
			Matcher m = Pattern.compile(
					String.valueOf(map_path.get(XSD_PATTERN))).matcher(data);
			if (!m.matches())
				return Translator.getInstanceof().getString("xmlcheck.pattern")
						+ map_path.get(XSD_PATTERN);
		}

		if (map_path.get(XSD_TOTALDIGITS) != null) // 判断参数传递过来的字符的个数是否等于约束的字符个数，若不相等，返回error
		{

			char[] s = data.toCharArray();
			int c = 0;
			for (int i = 0; i < s.length; i++) {
				try {
					new Integer(s[i] + "");
					c++;
				} catch (NumberFormatException e) {
				}
			}

			if (c > Integer.parseInt((String) map_path.get(XSD_TOTALDIGITS)))
				return Translator.getInstanceof().getString("xmlcheck.totalDigits")
						+ Integer.parseInt((String) map_path
								.get(XSD_TOTALDIGITS));
		}

		if ((map_path.get(XSD_FIXED) != null)
				&& !((String) map_path.get(XSD_FIXED)).equals(data))
			return Translator.getInstanceof().getString("xmlcheck.fixes")
					+ (String) map_path.get(XSD_FIXED);
		if (("isEnumerationType").equals(map_path.get("type"))
				|| isEnumerationType(map_path)) {

			String allEnumValue = "";
			Iterator iter = map_path.entrySet().iterator();
			String key;
			String value;
			while (iter.hasNext()) {
				Map.Entry pairs = (Entry) iter.next();
				key = (String) pairs.getKey();
				if (key.startsWith("enumeration_")) {
					if ("".equals(allEnumValue)) {
						allEnumValue = (String) pairs.getValue();
					} else {
						allEnumValue = allEnumValue + ", " + pairs.getValue();
					}
				}
			}

			if (!map_path.containsKey("enumeration_" + data))
				return Translator.getInstanceof().getString("xmlcheck.enumeration")
						+ allEnumValue;
		}
		return null;
	}

	private static boolean isEnumerationType(Map typeInfo) {

		Iterator iter = typeInfo.keySet().iterator();
		String key;
		while (iter.hasNext()) {
			key = (String) iter.next();
			if (key != null) {
				if (key.startsWith("enumeration_"))
					return true;
			}
		}

		return false;
	}

	/**
	 * 参数path指定的叶节点数据是枚举类型时得到枚举类型的List,否则返回null
	 * 
	 * @param path
	 *            String
	 * @return List
	 */
	public static  List<String> getEnum(String path) {
		ArrayList<String> arr = null;
		if( ValidatorFactory.schemaMap==null)
		{
			StatusbarMessageHelper.output("没有schema文件", "", StatusbarMessageHelper.LEVEL.INFO);
			return new ArrayList();
		}
		if (path != null) {
			Map m = (Map) ValidatorFactory.schemaMap.get(path); // 路径表达式所对应的信息哈希表。
			if (m == null)
				return arr;
			if (("isEnumerationType").equals(m.get("type"))
					|| isEnumerationType(m))
			// //存在type健值对，也存在enumeration键值对。
			{
				arr = new ArrayList<String>();
				Iterator iter = m.entrySet().iterator();
				String key;
				while (iter.hasNext()) {
					Map.Entry pairs = (Entry) iter.next();
					key = (String) pairs.getKey();
					if (key.startsWith("enumeration_")) {
						arr.add((String) pairs.getValue());
					}
				}
			} else if (XSD_CBOOLEAN.equals(m.get(XSD_TYPE))) {
				arr = new ArrayList();
				arr.add("true");
				arr.add("false");
			}
		}
		return arr; // 当List键值对不存在时，返回null，否则返回此数组.
	}
}
