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
 *//* $Id: ColorUtil.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.util;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.expr.PropertyException;
import com.wisii.fov.fo.properties.WisedocColor;

/**
 * Generic Color helper class.
 * <p>
 * This class supports parsing string values into color values and creating
 * color values for strings. It provides a list of standard color names.
 * <p>
 * TODO: Add support for color Profiles.
 */
public final class ColorUtil {

    /**
     * keeps all the predefined and parsed colors.
     * <p>
     * This map is used to predefine given colors, as well as speeding up
     * parsing of already parsed colors.
     */
    private static Map colorMap = null;

    static {
        initializeColorMap();
    }
    /* 【添加：START】 by 李晓光  2009-2-3 */
    /* 用来记录所有用户在FO中使用过的层号【Color的层分量上】 */
    private static Set ALL_LAYER = null;
    static{
    	ALL_LAYER = Collections.synchronizedSet(new HashSet());
    	ALL_LAYER.add(Constants.BACKGROUND_LAYER);
    }
    public static Set getAllLayers(){
    	return new HashSet(ALL_LAYER);
    }
    public static void addLayer(Object layer){
    	ALL_LAYER.add(layer);
    }
    public static void setAllLayers(Set allLayers){
    	ALL_LAYER = allLayers;
    }
    /* 【添加：END】 by 李晓光  2009-2-3 */
    /**
     * Private constructor since this is an utility class.
     */
    private ColorUtil() {
    }

    /**
     * Creates a color from a given string.
     * <p>
     * This function supports a wide variety of inputs.
     * <ul>
     * <li>#RGB (hex 0..f)</li>
     * <li>#RGBA (hex 0..f)</li>
     * <li>#RRGGBB (hex 00..ff)</li>
     * <li>#RRGGBBAA (hex 00..ff)</li>
     * <li>rgb(r,g,b) (0..255 or 0%..100%)</li>
     * <li>java.awt.Color[r=r,g=g,b=b] (0..255)</li>
     * <li>system-color(colorname)</li>
     * <li>transparent</li>
     * <li>colorname</li>
     * </ul>
     *
     * @param value
     *            the string to parse.
     * @return a Color representing the string if possible
     * @throws PropertyException
     *             if the string is not parsable or does not follow any of the
     *             given formats.
     */
    public static Color parseColorString(String value) throws PropertyException {
        if (value == null) {
            return null;
        }

        Color parsedColor = (Color) colorMap.get(value.toLowerCase());

        if (parsedColor == null) {
            if (value.startsWith("#")) {
                parsedColor = parseWithHash(value);
            } else if (value.startsWith("rgb(")) {
                parsedColor = parseAsRGB(value);
            } else if (value.startsWith("url(")) {
                throw new PropertyException(
					"从URL(的颜色开始未支持!");
            } else if (value.startsWith("java.awt.Color")) {
                parsedColor = parseAsJavaAWTColor(value);
            } else if (value.startsWith("system-color(")) {
                parsedColor = parseAsSystemColor(value);
            }

            if (parsedColor == null) {
                throw new PropertyException("未知颜色: " + value);
            }

            colorMap.put(value, parsedColor);
        }
        /* 【删除：START】 by 李晓光2009-2-2 */
        // TODO: Check if this is really necessary
        /*return new Color(parsedColor.getRed(), parsedColor.getGreen(),
                parsedColor.getBlue(), parsedColor.getAlpha());*/
        /* 【删除：END】 by 李晓光2009-2-2 */
        return parsedColor;
    }

    /**
     * Tries to parse a color given with the system-color() function.
     *
     * @param value
     *            the complete line
     * @return a color if possible
     * @throws PropertyException
     *             if the format is wrong.
     */
    private static Color parseAsSystemColor(String value)
            throws PropertyException {
        int poss = value.indexOf("(");
        int pose = value.indexOf(")");
        if (poss != -1 && pose != -1) {
            value = value.substring(poss + 1, pose);
        } else {
            throw new PropertyException("未知颜色格式: " + value
                    + ". 必须是 system-color(x)");
        }
        return (Color) colorMap.get(value);
    }

    /**
     * Tries to parse the standard java.awt.Color toString output.
     *
     * @param value
     *            the complete line
     * @return a color if possible
     * @throws PropertyException
     *             if the format is wrong.
     * @see java.awt.Color#toString()
     */
    private static Color parseAsJavaAWTColor(String value)
            throws PropertyException {
        float red = 0.0f, green = 0.0f, blue = 0.0f;
        int poss = value.indexOf("[");
        int pose = value.indexOf("]");
        try {
            if (poss != -1 && pose != -1) {
                value = value.substring(poss + 1, pose);
                StringTokenizer st = new StringTokenizer(value, ",");
                if (st.hasMoreTokens()) {
                    String str = st.nextToken().trim();
                    red = Float.parseFloat(str.substring(2)) / 255f;
                }
                if (st.hasMoreTokens()) {
                    String str = st.nextToken().trim();
                    green = Float.parseFloat(str.substring(2)) / 255f;
                }
                if (st.hasMoreTokens()) {
                    String str = st.nextToken().trim();
                    blue = Float.parseFloat(str.substring(2)) / 255f;
                } else {
                    throw new NumberFormatException();
                }
                if ((red < 0.0 || red > 1.0) || (green < 0.0 || green > 1.0)
                        || (blue < 0.0 || blue > 1.0)) {
                    throw new PropertyException("颜色值超出范围");
                }
            } else {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            throw new PropertyException("未知格式化颜色: " + value);
        }
        return new Color(red, green, blue);
    }

    /**
     * Parse a color given with the rgb() function.
     *
     * @param value
     *            the complete line
     * @return a color if possible
     * @throws PropertyException
     *             if the format is wrong.
     */
    private static Color parseAsRGB(String value) throws PropertyException {
        Color parsedColor;
        int poss = value.indexOf("(");
        int pose = value.indexOf(")");
        if (poss != -1 && pose != -1) {
            value = value.substring(poss + 1, pose);
            StringTokenizer st = new StringTokenizer(value, ",");
            try {
                float red = 0.0f, green = 0.0f, blue = 0.0f, alpha = 1.0F;//不透明
                int layer = 0;
                if (st.hasMoreTokens()) {
                    String str = st.nextToken().trim();
                    if (str.endsWith("%")) {
                        red = Float.parseFloat(str.substring(0,
                                str.length() - 1)) / 100.0f;
                    } else {
                        red = Float.parseFloat(str) / 255f;
                    }
                }
                if (st.hasMoreTokens()) {
                    String str = st.nextToken().trim();
                    if (str.endsWith("%")) {
                        green = Float.parseFloat(str.substring(0,
                                str.length() - 1)) / 100.0f;
                    } else {
                        green = Float.parseFloat(str) / 255f;
                    }
                }
                if (st.hasMoreTokens()) {
                    String str = st.nextToken().trim();
                    if (str.endsWith("%")) {
                        blue = Float.parseFloat(str.substring(0,
                                str.length() - 1)) / 100.0f;
                    } else {
                        blue = Float.parseFloat(str) / 255f;
                    }
                }
                /* 【添加：START】by 李晓光2009-2-2 用于处理透明度分量*/
                if(st.hasMoreTokens()){
                	String str = st.nextToken().trim();
                	try {
                		if (str.endsWith("%")) {
                            alpha = Float.parseFloat(str.substring(0,
                                    str.length() - 1)) / 100.0f;
                            //设置成0时，透明度为调整为1，原来生成的模板均是透明度默认均是0，
                            //为了能显示原来生成的模板，做此特殊处理
                        	if(alpha==0)
                        	{
                        		alpha = 1.0F; 
                        	}
                        } else {
                        	alpha = Float.parseFloat(str) / 255f;
                        	//设置成0时，透明度为调整为1，原来生成的模板均是透明度默认均是0，
                            //为了能显示原来生成的模板，做此特殊处理
                        	if(alpha==0)
                        	{
                        		alpha = 1.0F; 
                        	}
                        }
					} catch (Exception e) {
						alpha = 1.0F;
					}
                }
                if(st.hasMoreTokens()){
                	String str = st.nextToken().trim();
                	if(isNumbers(str))
                		layer = Integer.parseInt(str);
                }
                /* 【添加：END】by 李晓光2009-2-2 */
                if ((red < 0.0 || red > 1.0) || (green < 0.0 || green > 1.0)
                        || (blue < 0.0 || blue > 1.0) || (alpha < 0.0 || alpha > 1.0)) {//【添加】(alpha < 0.0 || alpha > 1.0) by 李晓光2009-2-2
                    throw new PropertyException("颜色值超出范围");
                }
                parsedColor = new Color(red, green, blue,alpha);
               /* 【添加：START】by 李晓光2009-2-2  创建带有层属性的WisedocColor*/
//                System.out.println(layer);
                ALL_LAYER.add(new Integer(layer));
                parsedColor = new WisedocColor(parsedColor, layer);
               /* 【添加：END】by 李晓光2009-2-2  */
            } catch (Exception e) {
                throw new PropertyException(
                        "rgb()参数值必须是[0..255]或[0%..100%]同时层的参数必须是正整数");
            }
        } else {
            throw new PropertyException("未知格式化颜色: " + value
                    + ". 必须是 rgb(r,g,b)");
        }
        return parsedColor;
    }
    /* 【添加：START】by 李晓光2009-2-2  */
    /**
	 * 判断一个指定字符串是否有数字组成
	 * 
	 * @param txt
	 *            指定要检查的字符串
	 * @return 由数字组成：True 否则：False
	 */
	public static final boolean isNumbers(String txt)
	{
		if (txt == null || txt.length() == 0)
			return false;

		return txt.matches("\\d{1,}");
	}
	/* 【添加：END】by 李晓光2009-2-2  */
    /**
     * parse a color given in the #.... format.
     *
     * @param value
     *            the complete line
     * @return a color if possible
     * @throws PropertyException
     *             if the format is wrong.
     */
    private static Color parseWithHash(String value) throws PropertyException {
        Color parsedColor = null;
        try {
            int len = value.length();
            if ((len >= 4) && (len <= 5)) {
                // note: divide by 15 so F = FF = 1 and so on
                float red = Integer.parseInt(value.substring(1, 2), 16) / 15f;
                float green = Integer.parseInt(value.substring(2, 3), 16) / 15f;
                float blue = Integer.parseInt(value.substring(3, 4), 16) / 15f;
                float alpha = 1.0f;
                if (len == 5) {
                    alpha = Integer.parseInt(value.substring(4), 16) / 15f;
                }
                parsedColor = new Color(red, green, blue, alpha);
            } else if ((len == 7) || (len == 9)) {
                int red = Integer.parseInt(value.substring(1, 3), 16);
                int green = Integer.parseInt(value.substring(3, 5), 16);
                int blue = Integer.parseInt(value.substring(5, 7), 16);
                int alpha = 255;
                if (len == 9) {
                    alpha = Integer.parseInt(value.substring(7), 16);
                }
                parsedColor = new Color(red, green, blue, alpha);
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new PropertyException("未知格式化颜色: " + value
                    + ". 必须是 #RGB. #RGBA, #RRGGBB, or #RRGGBBAA");
        }
        return parsedColor;
    }

    /**
     * Creates a re-parsable string representation of the given color.
     * <p>
     * First, the color will be converted into the sRGB colorspace. It will then
     * be printed as #rrggbb, or as #rrrggbbaa if an alpha value is present.
     *
     * @param color
     *            the color to represent.
     * @return a re-parsable string representadion.
     */
    public static String colorTOsRGBString(Color color) {
        StringBuffer sbuf = new StringBuffer(10);
        sbuf.append('#');
        String s = Integer.toHexString(color.getRed());
        if (s.length() == 1) {
            sbuf.append('0');
        }
        sbuf.append(s);
        s = Integer.toHexString(color.getGreen());
        if (s.length() == 1) {
            sbuf.append('0');
        }
        sbuf.append(s);
        s = Integer.toHexString(color.getBlue());
        if (s.length() == 1) {
            sbuf.append('0');
        }
        sbuf.append(s);
        if (color.getAlpha() != 255) {
            s = Integer.toHexString(color.getAlpha());
            if (s.length() == 1) {
                sbuf.append('0');
            }
            sbuf.append(s);
        }
        return sbuf.toString();

    }

    /**
     * Initializes the colorMap with some predefined values.
     */
    private static void initializeColorMap() {
        colorMap = Collections.synchronizedMap(new java.util.HashMap());

        colorMap.put("aliceblue", new Color(240, 248, 255));
        colorMap.put("antiquewhite", new Color(250, 235, 215));
        colorMap.put("aqua", new Color(0, 255, 255));
        colorMap.put("aquamarine", new Color(127, 255, 212));
        colorMap.put("azure", new Color(240, 255, 255));
        colorMap.put("beige", new Color(245, 245, 220));
        colorMap.put("bisque", new Color(255, 228, 196));
        colorMap.put("black", new Color(0, 0, 0));
        colorMap.put("blanchedalmond", new Color(255, 235, 205));
        colorMap.put("blue", new Color(0, 0, 255));
        colorMap.put("blueviolet", new Color(138, 43, 226));
        colorMap.put("brown", new Color(165, 42, 42));
        colorMap.put("burlywood", new Color(222, 184, 135));
        colorMap.put("cadetblue", new Color(95, 158, 160));
        colorMap.put("chartreuse", new Color(127, 255, 0));
        colorMap.put("chocolate", new Color(210, 105, 30));
        colorMap.put("coral", new Color(255, 127, 80));
        colorMap.put("cornflowerblue", new Color(100, 149, 237));
        colorMap.put("cornsilk", new Color(255, 248, 220));
        colorMap.put("crimson", new Color(220, 20, 60));
        colorMap.put("cyan", new Color(0, 255, 255));
        colorMap.put("darkblue", new Color(0, 0, 139));
        colorMap.put("darkcyan", new Color(0, 139, 139));
        colorMap.put("darkgoldenrod", new Color(184, 134, 11));
        colorMap.put("darkgray", new Color(169, 169, 169));
        colorMap.put("darkgreen", new Color(0, 100, 0));
        colorMap.put("darkgrey", new Color(169, 169, 169));
        colorMap.put("darkkhaki", new Color(189, 183, 107));
        colorMap.put("darkmagenta", new Color(139, 0, 139));
        colorMap.put("darkolivegreen", new Color(85, 107, 47));
        colorMap.put("darkorange", new Color(255, 140, 0));
        colorMap.put("darkorchid", new Color(153, 50, 204));
        colorMap.put("darkred", new Color(139, 0, 0));
        colorMap.put("darksalmon", new Color(233, 150, 122));
        colorMap.put("darkseagreen", new Color(143, 188, 143));
        colorMap.put("darkslateblue", new Color(72, 61, 139));
        colorMap.put("darkslategray", new Color(47, 79, 79));
        colorMap.put("darkslategrey", new Color(47, 79, 79));
        colorMap.put("darkturquoise", new Color(0, 206, 209));
        colorMap.put("darkviolet", new Color(148, 0, 211));
        colorMap.put("deeppink", new Color(255, 20, 147));
        colorMap.put("deepskyblue", new Color(0, 191, 255));
        colorMap.put("dimgray", new Color(105, 105, 105));
        colorMap.put("dimgrey", new Color(105, 105, 105));
        colorMap.put("dodgerblue", new Color(30, 144, 255));
        colorMap.put("firebrick", new Color(178, 34, 34));
        colorMap.put("floralwhite", new Color(255, 250, 240));
        colorMap.put("forestgreen", new Color(34, 139, 34));
        colorMap.put("fuchsia", new Color(255, 0, 255));
        colorMap.put("gainsboro", new Color(220, 220, 220));
        colorMap.put("ghostwhite", new Color(248, 248, 255));
        colorMap.put("gold", new Color(255, 215, 0));
        colorMap.put("goldenrod", new Color(218, 165, 32));
        colorMap.put("gray", new Color(128, 128, 128));
        colorMap.put("green", new Color(0, 128, 0));
        colorMap.put("greenyellow", new Color(173, 255, 47));
        colorMap.put("grey", new Color(128, 128, 128));
        colorMap.put("honeydew", new Color(240, 255, 240));
        colorMap.put("hotpink", new Color(255, 105, 180));
        colorMap.put("indianred", new Color(205, 92, 92));
        colorMap.put("indigo", new Color(75, 0, 130));
        colorMap.put("ivory", new Color(255, 255, 240));
        colorMap.put("khaki", new Color(240, 230, 140));
        colorMap.put("lavender", new Color(230, 230, 250));
        colorMap.put("lavenderblush", new Color(255, 240, 245));
        colorMap.put("lawngreen", new Color(124, 252, 0));
        colorMap.put("lemonchiffon", new Color(255, 250, 205));
        colorMap.put("lightblue", new Color(173, 216, 230));
        colorMap.put("lightcoral", new Color(240, 128, 128));
        colorMap.put("lightcyan", new Color(224, 255, 255));
        colorMap.put("lightgoldenrodyellow", new Color(250, 250, 210));
        colorMap.put("lightgray", new Color(211, 211, 211));
        colorMap.put("lightgreen", new Color(144, 238, 144));
        colorMap.put("lightgrey", new Color(211, 211, 211));
        colorMap.put("lightpink", new Color(255, 182, 193));
        colorMap.put("lightsalmon", new Color(255, 160, 122));
        colorMap.put("lightseagreen", new Color(32, 178, 170));
        colorMap.put("lightskyblue", new Color(135, 206, 250));
        colorMap.put("lightslategray", new Color(119, 136, 153));
        colorMap.put("lightslategrey", new Color(119, 136, 153));
        colorMap.put("lightsteelblue", new Color(176, 196, 222));
        colorMap.put("lightyellow", new Color(255, 255, 224));
        colorMap.put("lime", new Color(0, 255, 0));
        colorMap.put("limegreen", new Color(50, 205, 50));
        colorMap.put("linen", new Color(250, 240, 230));
        colorMap.put("magenta", new Color(255, 0, 255));
        colorMap.put("maroon", new Color(128, 0, 0));
        colorMap.put("mediumaquamarine", new Color(102, 205, 170));
        colorMap.put("mediumblue", new Color(0, 0, 205));
        colorMap.put("mediumorchid", new Color(186, 85, 211));
        colorMap.put("mediumpurple", new Color(147, 112, 219));
        colorMap.put("mediumseagreen", new Color(60, 179, 113));
        colorMap.put("mediumslateblue", new Color(123, 104, 238));
        colorMap.put("mediumspringgreen", new Color(0, 250, 154));
        colorMap.put("mediumturquoise", new Color(72, 209, 204));
        colorMap.put("mediumvioletred", new Color(199, 21, 133));
        colorMap.put("midnightblue", new Color(25, 25, 112));
        colorMap.put("mintcream", new Color(245, 255, 250));
        colorMap.put("mistyrose", new Color(255, 228, 225));
        colorMap.put("moccasin", new Color(255, 228, 181));
        colorMap.put("navajowhite", new Color(255, 222, 173));
        colorMap.put("navy", new Color(0, 0, 128));
        colorMap.put("oldlace", new Color(253, 245, 230));
        colorMap.put("olive", new Color(128, 128, 0));
        colorMap.put("olivedrab", new Color(107, 142, 35));
        colorMap.put("orange", new Color(255, 165, 0));
        colorMap.put("orangered", new Color(255, 69, 0));
        colorMap.put("orchid", new Color(218, 112, 214));
        colorMap.put("palegoldenrod", new Color(238, 232, 170));
        colorMap.put("palegreen", new Color(152, 251, 152));
        colorMap.put("paleturquoise", new Color(175, 238, 238));
        colorMap.put("palevioletred", new Color(219, 112, 147));
        colorMap.put("papayawhip", new Color(255, 239, 213));
        colorMap.put("peachpuff", new Color(255, 218, 185));
        colorMap.put("peru", new Color(205, 133, 63));
        colorMap.put("pink", new Color(255, 192, 203));
        colorMap.put("plum ", new Color(221, 160, 221));
        colorMap.put("plum", new Color(221, 160, 221));
        colorMap.put("powderblue", new Color(176, 224, 230));
        colorMap.put("purple", new Color(128, 0, 128));
        colorMap.put("red", new Color(255, 0, 0));
        colorMap.put("rosybrown", new Color(188, 143, 143));
        colorMap.put("royalblue", new Color(65, 105, 225));
        colorMap.put("saddlebrown", new Color(139, 69, 19));
        colorMap.put("salmon", new Color(250, 128, 114));
        colorMap.put("sandybrown", new Color(244, 164, 96));
        colorMap.put("seagreen", new Color(46, 139, 87));
        colorMap.put("seashell", new Color(255, 245, 238));
        colorMap.put("sienna", new Color(160, 82, 45));
        colorMap.put("silver", new Color(192, 192, 192));
        colorMap.put("skyblue", new Color(135, 206, 235));
        colorMap.put("slateblue", new Color(106, 90, 205));
        colorMap.put("slategray", new Color(112, 128, 144));
        colorMap.put("slategrey", new Color(112, 128, 144));
        colorMap.put("snow", new Color(255, 250, 250));
        colorMap.put("springgreen", new Color(0, 255, 127));
        colorMap.put("steelblue", new Color(70, 130, 180));
        colorMap.put("tan", new Color(210, 180, 140));
        colorMap.put("teal", new Color(0, 128, 128));
        colorMap.put("thistle", new Color(216, 191, 216));
        colorMap.put("tomato", new Color(255, 99, 71));
        colorMap.put("turquoise", new Color(64, 224, 208));
        colorMap.put("violet", new Color(238, 130, 238));
        colorMap.put("wheat", new Color(245, 222, 179));
        colorMap.put("white", new Color(255, 255, 255));
        colorMap.put("whitesmoke", new Color(245, 245, 245));
        colorMap.put("yellow", new Color(255, 255, 0));
        colorMap.put("yellowgreen", new Color(154, 205, 50));

        colorMap.put("transparent", new Color(0, 0, 0, 0));
    }

}
