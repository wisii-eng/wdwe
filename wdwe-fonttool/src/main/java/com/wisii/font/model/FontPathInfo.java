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
 */
/**
 * @fontInfo.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.font.model;


/**
 * 类功能描述：
 * 字体路径信息类，记录字体的各种字形的路径信息
 * 作者：zhangqiang
 * 创建日期：2011-10-19
 */
public final class FontPathInfo
{
	//字体名称
	private String name;
	//正常字体所在路径
	private PathItemInfo normalfontpath;
	//斜体字体路径
	private PathItemInfo italicfontpath;
	//粗体路径
	private PathItemInfo blodfontpath;
	//斜粗体字体路径
	private PathItemInfo italicboldfontpath;

	/**
	 * @返回 name变量的值
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            设置name成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @返回 normalfontpath变量的值
	 */
	public PathItemInfo getNormalfontpath()
	{
		return normalfontpath;
	}

	/**
	 * @param normalfontpath
	 *            设置normalfontpath成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setNormalfontpath(PathItemInfo normalfontpath)
	{
		this.normalfontpath = normalfontpath;
	}

	/**
	 * @返回 italicfontpath变量的值
	 */
	public PathItemInfo getItalicfontpath()
	{
		return italicfontpath;
	}

	/**
	 * @param italicfontpath
	 *            设置italicfontpath成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setItalicfontpath(PathItemInfo italicfontpath)
	{
		this.italicfontpath = italicfontpath;
	}

	/**
	 * @返回 blodfontpath变量的值
	 */
	public PathItemInfo getBlodfontpath()
	{
		return blodfontpath;
	}

	/**
	 * @param blodfontpath
	 *            设置blodfontpath成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setBlodfontpath(PathItemInfo blodfontpath)
	{
		this.blodfontpath = blodfontpath;
	}

	/**
	 * @返回 italicboldfontpath变量的值
	 */
	public PathItemInfo getItalicboldfontpath()
	{
		return italicboldfontpath;
	}

	/**
	 * @param italicboldfontpath
	 *            设置italicboldfontpath成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setItalicboldfontpath(PathItemInfo italicboldfontpath)
	{
		this.italicboldfontpath = italicboldfontpath;
	}

}
