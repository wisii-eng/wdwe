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
 * @HardWareInfoGetter.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.fov.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * 类功能描述：
 * 
 * 作者：zhangqiang 创建日期：2009-8-5
 */
public class HardWareInfoGetter
{
	public static String getDiskID()
	{
		return "testdiskid";
	}

	public static String getMac()
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

	public static String getProcessorid()
	{
		return "testprocessorid";
	}

	/* Linux stuff */

	private final static String linuxParseMacAddress(String ipConfigResponse)
			throws ParseException
	{
		String localHost = null;
		try
		{
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex)
		{
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
		}
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;
		while (tokenizer.hasMoreTokens())
		{
			String line = tokenizer.nextToken().trim();
			boolean containsLocalHost = line.indexOf(localHost) >= 0; // see if
			// line
			// contains
			// IP
			// address
			if (containsLocalHost && lastMacAddress != null)
			{
				return lastMacAddress;
			} // see if line contains MAC address
			int macAddressPosition = line.indexOf("HWaddr");
			if (macAddressPosition <= 0)
				continue;
			String macAddressCandidate = line.substring(macAddressPosition + 6)
					.trim();
			if (linuxIsMacAddress(macAddressCandidate))
			{
				lastMacAddress = macAddressCandidate;
				continue;
			}
		}
		ParseException ex = new ParseException(
				"cannot   read   MAC   address   for   " + localHost
						+ "   from   [" + ipConfigResponse + "]", 0);
		ex.printStackTrace();
		throw ex;
	}

	private final static boolean linuxIsMacAddress(String macAddressCandidate)
	{ // TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
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

	private final static boolean windowsIsMacAddress(String macAddressCandidate)
	{ // TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
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
}
