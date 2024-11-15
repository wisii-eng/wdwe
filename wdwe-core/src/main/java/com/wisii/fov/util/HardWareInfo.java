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
 * @HardWareInfo.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.fov.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * 类功能描述：硬件信息获取类
 *
 * 作者：zhangqiang
 * 创建日期：2009-7-31
 */
 final class HardWareInfo
{

	static String getDiskSerialID()
	{
		return "testdiskid";
	}

	static String getMac()
	{
		String os = System.getProperty("os.name");
		try
		{
			if (os.startsWith("Windows"))
			{
				return windowsParseMacAddress(windowsRunIpConfigCommand());

			}

			else if (os.startsWith("Linux"))

			{
				return linuxParseMacAddress(linuxRunIfConfigCommand());
			} else
			{
				throw new IOException("unknown   operating   system:   " + os);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return "cannot get";
		}
	}

	static String getPrecessorID()
	{
		return "testprocessorid";
	}

	/* Linux stuff */

	private final static String linuxParseMacAddress(String ipConfigResponse)
			throws ParseException
	{
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		while (tokenizer.hasMoreTokens())
		{
			String line = tokenizer.nextToken().trim();
			int macAddressPosition = line.indexOf("HWaddr");
			if (macAddressPosition < 0)
				continue;
			String macAddressCandidate = line.substring(macAddressPosition + 6)
					.trim();
			if (macAddressCandidate != null && !"".equals(macAddressCandidate))
			{
				return macAddressCandidate;
			}
		}
		ParseException ex = new ParseException(
				"cannot   read   MAC   address   for   " + ipConfigResponse
						+ "   from   [" + ipConfigResponse + "]", 0);
		ex.printStackTrace();
		throw ex;
	}
	private final static String linuxRunIfConfigCommand() throws IOException
	{
		Process p = Runtime.getRuntime().exec("/sbin/ifconfig");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
		StringBuffer buffer = new StringBuffer();
		for (;;)
		{
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}

	/* Windows stuff */

	private final static String windowsParseMacAddress(String ipConfigResponse)
			throws ParseException
	{
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		while (tokenizer.hasMoreTokens())
		{
			String line = tokenizer.nextToken().trim();
			if (line.startsWith("Physical Address"))
			{
				int macAddressPosition = line.indexOf(":");
				if (macAddressPosition <= 0)
					continue;
				String macAddressCandidate = line.substring(
						macAddressPosition + 1).trim();
				if (macAddressCandidate != null
						&& !"".equals(macAddressCandidate))
				{
					return macAddressCandidate;
				}
			}
		}
		ParseException ex = new ParseException(
				"cannot   read   MAC   address   from   [" + ipConfigResponse
						+ "]", 0);
		ex.printStackTrace();
		throw ex;
	}
	private final static String windowsRunIpConfigCommand() throws IOException
	{
		Process p = Runtime.getRuntime().exec("ipconfig   /all");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
		StringBuffer buffer = new StringBuffer();
		for (;;)
		{
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}


	/**
	 * {方法的功能/动作描述}
	 * 
	 * @param
	 * @return
	 * @exception
	 */
//	private static native String getMac0();
//
//	private static native String getPrecessorID0();
//
//	private static native String getDiskSerialID0();
}
