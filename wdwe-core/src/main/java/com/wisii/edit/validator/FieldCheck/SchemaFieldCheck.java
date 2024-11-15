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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import com.wisii.edit.validator.FieldCheck.CommFieldCheck;
import com.wisii.edit.validator.FieldCheck.SchemaFieldCheck;

public class SchemaFieldCheck {
	/**
     * <p>是否可以被转换成为有效的boolean类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的boolean类型数据
     */
    public static boolean Schema_isBoolean(String value) {
    	return (SchemaFieldCheck.formatBoolean(value) != null);
    }
    /**
     *  转换字符串为Boolean对象
     *
     *@param  value  字符串
     *@return Boolean 转换后的对象
     */
    public static Boolean formatBoolean(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Boolean.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * <p>是否可以被转换成为有效的byte类型数据</p>
     *
     * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的byte类型数据
     */
    public static boolean Schema_isByte(String value) {
        return CommFieldCheck.isByte(value);
    }
    /**
     * <p>是否可以被转换成为有效的Decimal数字类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的Decimal类型数据
     */
    public static boolean Schema_isDecimal(String value) {
    	return (SchemaFieldCheck.formatBigDecimal(value) != null);
    	//Pattern pattern = Pattern.compile("^(\\d+\\.)?\\d+$");
    	//return pattern.matcher(value).matches();
    }
    /**
     *  转换字符串为BigDecimal对象
     *
     *@param  value  字符串
     *@return BigDecimal 转换后的对象
     */
    public static BigDecimal formatBigDecimal(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new BigDecimal(value);
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
    public static boolean Schema_isDouble(String value) {
        return CommFieldCheck.isDouble(value);
    }
    /**
     * <p>是否可以被转换成为有效的float类型数据</p>
     *
     * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的float类型数据
     */
    public static boolean Schema_isFloat(String value) {
        return CommFieldCheck.isFloat(value);
    }
    /**
     * <p>是否可以被转换成为有效的int类型数据</p>
     *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的int类型数据
     */
    public static boolean Schema_isInt(String value) {
        return CommFieldCheck.isInt(value);
    }
    /**
     * <p>是否可以被转换成为有效的Integer类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的Integer类型数据
     */
    public static boolean Schema_isInteger(String value) {
    	return (SchemaFieldCheck.formatBigInteger(value) != null);
    	//Pattern pattern = Pattern.compile("^\\d+$");
    	//return pattern.matcher(value).matches();
    }
    /**
     *  转换字符串为BigInteger对象
     *
     *@param  value  字符串
     *@return BigInteger 转换后的对象.
     */
    public static BigInteger formatBigInteger(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new BigInteger(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * <p>是否可以被转换成为有效的Long类型数据</p>
     *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的long类型数据
     */
    public static boolean Schema_isLong(String value) {
        return CommFieldCheck.isLong(value);
    }
    /**
     * <p>是否可以被转换成为有效的仅包含负值的整数 ( .., -2, -1.)类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的仅包含负值的整数类型数据
     */
    public static boolean Schema_isNegativeInteger(String value) {
    	if (CommFieldCheck.isInt(value)) {
    		int num = Integer.parseInt(value);
    		if(num < 0){
    			return true;
    		}
		}
		return false;		
    }
    /**
     * <p>是否可以被转换成为有效的仅包含非负值的整数 (0, 1, 2, ..)类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的仅包含非负值的整数类型数据
     */
    public static boolean Schema_isNonNegativeInteger(String value) {
    	if (CommFieldCheck.isInt(value)) {
    		int num = Integer.parseInt(value);
    		if(num >= 0){
    			return true;
    		}
		}
		return false;		
    }
    /**
     * <p>是否可以被转换成为有效的仅包含非正值的整数 (.., -2, -1, 0)类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的仅包含非正值的整数类型数据
     */
    public static boolean Schema_isNonPositiveInteger(String value) {
    	if (CommFieldCheck.isInt(value)) {
    		int num = Integer.parseInt(value);
    		if(num <= 0){
    			return true;
    		}
		}
		return false;		
    }
    /**
     * <p>是否可以被转换成为有效的仅包含正值的整数 (1, 2, ..)类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的仅包含正值的整数类型数据
     */
    public static boolean Schema_isPositiveInteger(String value) {
    	if (CommFieldCheck.isInt(value)) {
    		int num = Integer.parseInt(value);
    		if(num > 0){
    			return true;
    		}
		}
		return false;		
    }
    /**
     * <p>是否可以被转换成为有效的short类型数据</p>
     *
	 * @param value 要判断的字符串
	 * @return true -如果字符串value可以被转换成为有效的short类型数据
     */
    public static boolean Schema_isShort(String value) {
        return CommFieldCheck.isShort(value);
    }
    /**
     * <p>是否可以被转换成为有效的无符号的 8 位Byte类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的无符号的 8 位Byte类型数据
     */
    public static boolean Schema_isUnsignedByte(String value) {
    	if(CommFieldCheck.isByte(value)){
    		byte num = Byte.parseByte(value);
    		if(num > 0){
    			return true;
    		}
    	}
        return false;
    }
    /**
     * <p>是否可以被转换成为有效的无符号的 32 位整数类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的无符号的 32 位整数类型数据
     */
    public static boolean Schema_isUnsignedInt(String value) {
    	if(CommFieldCheck.isInt(value)){
    		int num = Integer.parseInt(value);
    		if(num > 0){
    			return true;
    		}
    	}
        return false;
    }
    /**
     * <p>是否可以被转换成为有效的无符号的 64位整数类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的无符号的 64位整数类型数据
     */
    public static boolean Schema_isUnsignedLong(String value) {
    	if(CommFieldCheck.isLong(value)){
    		long num = Long.parseLong(value);
    		if(num > 0){
    			return true;
    		}
    	}
        return false;
    }
    /**
     * <p>是否可以被转换成为有效的无符号的 16位整数类型数据</p>
     *
     * @param value 要判断的字符串
     * @return true -如果字符串value可以被转换成为有效的无符号的 16位整数类型数据
     */
    public static boolean Schema_isUnsignedShort(String value) {
    	if(CommFieldCheck.isShort(value)){
    		short num = Short.parseShort(value);
    		if(num > 0){
    			return true;
    		}
    	}
        return false;
    }
    /**
     * <p>定义可接受值的一个列表</p>
     *
     * @param value 要判断的字符串
     * @param list 列表集合
     * @return true 如果list中包含value
     */
    public static boolean Schema_Enumeration(String value,List list) {
    	if(value == null){
    		return false;
    	}
    	if(list != null){
    		if(list.contains(value)){
        		return true;
        	}
    	}
        return false;
    }
    /**
     * <p>定义所允许的最大的小数位数。必须大于等于0</p>
     *
     * @param value 要判断的字符串
     * @param maxDigits 最大的小数位数
     * @return true 如果value的小数位数大于等于0且小于等于最大的小数位数
     */
    public static boolean Schema_fractionDigits(String value,int maxDigits) {
    	if(maxDigits < 0){
    		return false;
    	}
    	if (!CommFieldCheck.isBlankOrNull(value)) {
			return false;
		}
    	
    	BigDecimal decimal = SchemaFieldCheck.formatBigDecimal(value);
    	if(decimal != null){
    		int len = decimal.scale();
    		if(len <= maxDigits){
    			return true;
    		}
    	}
        return false;
    }
    /**
     * <p>定义所允许的字符或者列表项目的精确数目。必须大于或等于0。</p>
     *
     * @param value 要判断的字符串
     * @param length 精确数目
     * @return true 如果value的长度等于精确数目而且必须大于或等于0
     */
    public static boolean Schema_Length(String value,int length) {
    	if(value == null){
    		return false;
    	}
    	int len = value.length();
    	if(len >= 0 && len == length){
    		return true;
    	}
        return false;
    }
    /**
     * <p>定义所允许的字符或者列表项目的最大数目。必须大于或等于0。</p>
     *
     * @param value 要判断的字符串
     * @param length 最大数目
     * @return true 如果value的长度小于等于精确数目而且必须大于或等于0
     */
    public static boolean Schema_maxLength(String value,int length) {
    	if(value == null){
    		return false;
    	}
    	int len = value.length();
    	if(len >= 0 && len <= length){
    		return true;
    	}
        return false;
    }
    /**
     * <p>定义所允许的字符或者列表项目的最小数目。必须大于或等于0。</p>
     *
     * @param value 要判断的字符串
     * @param length 最小数
     * @return true 如果value的长度大于等于精确数目而且必须大于或等于0
     */
    public static boolean Schema_minLength(String value,int length) {
    	if(value == null){
    		return false;
    	}
    	int len = value.length();
    	if(len >= 0 && len >= length){
    		return true;
    	}
        return false;
    }
    /**
     * <p>定义数值的上限。所允许的值必须小于此值。</p>
     *
     * @param value 要判断的字符串
     * @param max 数值的上限
     * @return true 如果允许的值小于数值的上限
     */
    public static boolean Schema_maxExclusive(String value,String max) {
    	BigDecimal valueDecimal = SchemaFieldCheck.formatBigDecimal(value);
    	BigDecimal maxDecimal = SchemaFieldCheck.formatBigDecimal(max);
    	if(valueDecimal != null && maxDecimal != null){
    		if(valueDecimal.compareTo(maxDecimal) < 0){
        		return true;
        	}
    	}
        return false;
    }
    /**
     * <p>定义数值的上限。所允许的值必须小于或等于此值。</p>
     *
     * @param value 要判断的字符串
     * @param max 数值的上限
     * @return true 如果允许的值小于或等于数值的上限
     */
    public static boolean Schema_maxInclusive(String value,String max) {
    	BigDecimal valueDecimal = SchemaFieldCheck.formatBigDecimal(value);
    	BigDecimal maxDecimal = SchemaFieldCheck.formatBigDecimal(max);
    	if(valueDecimal != null && maxDecimal != null){
    		if(valueDecimal.compareTo(maxDecimal) <= 0){
        		return true;
        	}
    	}
        return false;
    }
    /**
     * <p>定义数值的下限。所允许的值必需大于此值。</p>
     *
     * @param value 要判断的字符串
     * @param min 数值的下限
     * @return true 如果允许的值大于数值的下限
     */
    public static boolean Schema_minExclusive(String value,String min) {
    	BigDecimal valueDecimal = SchemaFieldCheck.formatBigDecimal(value);
    	BigDecimal minDecimal = SchemaFieldCheck.formatBigDecimal(min);
    	if(valueDecimal != null && minDecimal != null){
    		if(valueDecimal.compareTo(minDecimal) > 0){
        		return true;
        	}
    	}
        return false;
    }
    /**
     * <p>定义数值的下限。所允许的值必需大于或等于此值。</p>
     *
     * @param value 要判断的字符串
     * @param min 数值的下限
     * @return true 如果允许的值大于或等于数值的下限
     */
    public static boolean Schema_minInclusive(String value,String min) {
    	BigDecimal valueDecimal = SchemaFieldCheck.formatBigDecimal(value);
    	BigDecimal minDecimal = SchemaFieldCheck.formatBigDecimal(min);
    	if(valueDecimal != null && minDecimal != null){
    		if(valueDecimal.compareTo(minDecimal) >= 0){
        		return true;
        	}
    	}
        return false;
    }
    /**
     * <p>定义可接受的字符的精确序列。</p>
     *
     * @param value 要判断的字符串
     * @param regexp 正则表达式
     * @return true 如果value匹配正则表达式regexp
     */
    public static boolean Schema_Pattern(String value,String regexp) {
    	if (value == null) {
			return false;
		}
    	Pattern p = Pattern.compile(regexp);
    	return p.matcher(value).matches();
    }
    /**
     * <p>定义所允许的阿拉伯数字的精确位数。必须大于0。</p>
     *
     * @param value 要判断的字符串
     * @param length 数字的精确位数
     * @return true 如果value长度大于0并且等于length
     */
    public static boolean Schema_totalDigits(String value,int length) {
    	if (!CommFieldCheck.isBlankOrNull(value)) {
			return false;
		}
    	BigDecimal valueDecimal = SchemaFieldCheck.formatBigDecimal(value);
    	if(valueDecimal != null){
    		int len = valueDecimal.precision();
    		if(len == length && len > 0){
    			return true;
    		}
    	}
        return false;
    }
    /**
     * <p>定义空白字符（换行、回车、空格以及制表符）的处理方式。</p>
     *
     * @param value 要判断的字符串
     * @return true 
     */
    public static boolean Schema_whitespace(String value,String restriction) {
    	
        return false;
    }
}
