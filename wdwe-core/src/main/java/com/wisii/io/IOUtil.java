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
 * @IOUtil.java
 *              北京汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 类功能描述：
 * 流读取相关的工具功能类
 * 作者：zhangqiang
 * 创建日期：2012-12-17
 */
public class IOUtil
{
	public static byte[] getDataOfInputStream(InputStream in)
			throws IOException
	{
		if (in == null)
		{
			return null;
		}
		byte[] reads = new byte[512];
		int read = -1;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((read = in.read(reads)) != -1)
		{
			out.write(reads, 0, read);
		}
		return out.toByteArray();
	}
	public static byte[] getDataOfUrl(String urlstr) throws IOException
	{
		File file = new File(urlstr);
		InputStream in = null;
		if (file.exists())
		{
			try
			{
				in = new FileInputStream(file);
			}
			catch (FileNotFoundException e)
			{
				throw new IOException("读取数据文件时出错", e);
			}
		}
		else
		{
			try
			{
				URL url = new URL(urlstr);
				in = url.openStream();
			}
			catch (MalformedURLException e)
			{
				throw new IOException("URL:" + urlstr + "格式不正确", e);
			}
			catch (IOException e)
			{
				throw new IOException("建立:" + urlstr + "连接时发生错误,确认网络是否可用", e);
			}
		}
		byte[] datas = getDataOfInputStream(in);
		try
		{
			in.close();
		}
		catch (Exception e)
		{
		}
		return datas;
	}
	public static boolean writedataToFile(byte[] datas, File file)
	{
		if (datas == null || datas.length == 0 || file == null)
		{
			return false;
		}
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			out.write(datas);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
