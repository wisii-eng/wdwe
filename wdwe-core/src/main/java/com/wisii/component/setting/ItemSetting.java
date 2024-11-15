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
 */package com.wisii.component.setting;
/**
 * @author liuxiao
 * 
 * 这个类主要是各个按钮或组件的设置信息
 */

public final class ItemSetting
{
	/* 元素的名称 */
	private String name;

	/* 没有加载图片的时候显示的字样 */
	private String vname;

	/* 触发item的快捷键，如果这个控件没有快捷键方式则该属性失效 */
	private String key;

	/* Item的鼠标经过时候的显示 */
	private String title;

	/* 图标。如果这个控件不能加载图标则该属性失效 */
	private String icon;// 

	/* 目前只为blank开放这个属性 */
	private String size;

	/* 元素的类型 */
	private String type;
	private String classname;
	//是否是编辑工具栏
	private String isEdit;

	/**
	 * @return the classname
	 */
	public String getClassname()
	{
		return classname;
	}

	/**
	 * @param classname
	 *            the classname to set
	 */
	public void setClassname(String classname)
	{
		this.classname = classname;
	}

	/**
	 * 根据元素名称设置元素值
	 * 
	 * @param item
	 *            元素名
	 * @param value
	 *            元素值
	 */
	public void setPare(String item, String value)
	{
		if (item.equalsIgnoreCase("type"))
		{
			type = value.toLowerCase();
		}
		else if (item.equalsIgnoreCase("name"))
		{
			name = value.toLowerCase();
		}
		else if (item.equalsIgnoreCase("key"))
		{

			key = value;
		}
		else if (item.equalsIgnoreCase("title"))
		{
			title = value;
			vname = value;
		}
		else if (item.equalsIgnoreCase("icon"))
		{
			icon = value;
		}
		else if (item.equalsIgnoreCase("size"))
		{
			size = value;
		}

		else if (item.equalsIgnoreCase("classname"))
		{
			classname = value;
		}
		else if (item.equalsIgnoreCase("isEdit"))
		{
			isEdit = value;
		}

	}


	/**
	 * @return icon
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * @return key
	 */
	public String getKey()
	{
		return key;
	}
	/**
	 * @return name
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * @return size
	 */
	public String getSize()
	{
		return size;
	}

	/**
	 * @return title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @return type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @return vname
	 */
	public String getVname()
	{
		return vname;
	}

	public String getIsEdit() {
		return isEdit;
	}

	public String toString()
	{
		return "name = " + name + ", type = " + type;
	}
}
