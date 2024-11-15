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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Element;


/**工具栏设置类，主要是对工具栏的设置
 * @author liuxiao
 *
 */
public final class ToolBarSetting
{

	/* 设置工具栏的位置，初始位置为top */
	private String location = "top";

	/* 工具栏下每个按钮对象 */
	private List items;

	/**
	 * 得到toolbar节点并解析
	 * 
	 * @param toolbar
	 */
	public ToolBarSetting(Element toolbar)
	{

		location = toolbar.attributeValue("location");

		if (location == null || location.length() == 0)
		{
			location = "top";
		} else
		{
			location = location.toLowerCase();
		}
		items = new ArrayList();
		for (Iterator i = toolbar.elementIterator(); i.hasNext();)
		{
			ItemSetting is = new ItemSetting();

			Element leaf = (Element) i.next();

			List s = leaf.attributes();

			for (int j = 0; j < s.size(); j++)
			{
				Attribute ss = (Attribute) s.get(j);
				is.setPare(ss.getName(), ss.getValue());

			}
			items.add(is);

		}

	}

	/**
	 * @return location
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * @param location
	 *            要设置的 location
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

	/**
	 * @return items
	 */
	public List getItems()
	{
		return items;
	}

}
