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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.wisii.component.startUp.SystemUtil;

/**
 * 主要是用来读取配置文件的信息，并把他组装成对象
 * 
 * @author liuxiao
 * 
 * 
 */

public class CustomerSetting
{

	/* 工具栏设置标示 */
	private static String settingId = "default";

	/* setting的根节点 */
	private ToolBarSetting toolbarsetting;

	private boolean needReParse = true;
	// 单例
	private static CustomerSetting instance = null;

	public static synchronized CustomerSetting getInstance()
	{
		if (instance == null)
		{
			instance = new CustomerSetting();
		}
		return instance;
	}

	private CustomerSetting()
	{
		// regiestSetting();
	}

	public CustomerSetting init(String id)
	{
		if (id != null && id.length() > 0)
		{
			if (id.equals(settingId))
			{
				needReParse = false;
			} else
			{
				settingId = id;
				needReParse = true;
			}
		} else
		{
			if(settingId==null||!settingId.equals("default"))
			{
				settingId = "default";
				needReParse=true;
			}
		}

		regiestSetting();
		return instance;
	}

	private void regiestSetting()
	{

		if (toolbarsetting == null || needReParse)
		{
			// 文件路径需要修改
			if (needReParse)
			{
				toolbarsetting = null;
			}
			String settingString = readFileByLines(SystemUtil.class
					.getClassLoader().getResourceAsStream(
							"resource/" + "CustomerButton.xml"));
			if (settingString != null)
			{
				try
				{
					Document document = DocumentHelper.parseText(settingString);
					Element root = document.getRootElement();
					if (root != null)
					{
						Iterator i = root.elementIterator();
						if (i.hasNext())
						{
							Element leaf = (Element) i.next();
							toolbarsetting = new ToolBarSetting(leaf);
						}
					}
				} catch (Exception e)
				{
					System.out
							.println("解析按钮配置文件时出现如下错误：settingId=" + settingId);
					e.printStackTrace();
				}
			}

		}
	}

	public boolean haveThisId(String dd)
	{
		if (dd.indexOf("<setting") != -1&&dd.indexOf('"'+settingId+'"') != -1)
		{
			return true;
		}
		return false;
	}

	public ToolBarSetting getToolBarSetting()
	{	
		return toolbarsetting;
	}

	private String readFileByLines(InputStream input)
	{

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(input,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		String tempString = null;
		StringBuffer sb = new StringBuffer();
		try
		{
			while ((tempString = reader.readLine()) != null)
			{

				// 分离一个setting
				if (haveThisId(tempString))
				{
					sb.append(tempString);
					while ((tempString = reader.readLine()) != null)
					{

						sb.append(tempString);
						if (tempString.indexOf("</setting>") != -1)
							// 分离结束
							return sb.toString();
					}
				}

			}
		} catch (IOException e)
		{

			e.printStackTrace();
		} finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				} catch (IOException e)
				{

					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
