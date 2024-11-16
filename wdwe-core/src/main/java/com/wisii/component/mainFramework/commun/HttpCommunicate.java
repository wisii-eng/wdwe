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
 * 
 */
package com.wisii.component.mainFramework.commun;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.command.plugin.FOMethod;
import com.wisii.io.IOUtil;
import com.wisii.net.zip.GZipTransform;

/**
 * @author liuxiao
 */
public class HttpCommunicate implements CommunicateProxy
{
	@SuppressWarnings("unused")
	private ListListener listener;
	private String serverUrl;
	public HttpCommunicate(String serverUrl)
	{
		this.serverUrl = serverUrl;
	}
	public WdemsDateType send(String serverType, Object params)
	{
		Map<String, Object> facade = new HashMap<String, Object>();
		facade.put("serverType", serverType);
		facade.put("para", params);
		return send(facade);
	}
	public WdemsDateType send(Object params)
	{
		String returnstr = null;
		try
		{
			String myUrl = serverUrl;
			String str = null;
			try {
				str = FOMethod.generateString(params);
			} catch (Exception e) {
				
			}
			// 此函数是平台主动向服务段发起的http请求,并接收服务端的响应
			HttpURLConnection httpConnection;
			httpConnection = (HttpURLConnection) new URL(myUrl)
					.openConnection(); // 建立一个HttpURLConnection
			httpConnection.setRequestProperty("user-agent", "Mozilla");
			httpConnection
					.setRequestProperty(
							"accept",
							"image/gif, image/x-xbitmap, image/jpeg, "
									+ "image/pjpeg, application/x-shockwave-flash, "
									+ "application/vnd.ms-excel, "
									+ "application/vnd.ms-powerpoint, application/msword, */*");
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setAllowUserInteraction(true);
			httpConnection.connect();
			if (str != null && !str.isEmpty())
			{
				// 发送数据给服务端
				OutputStream outputStream = httpConnection.getOutputStream();
				byte[] buffer = str.getBytes(SystemUtil.SYS_CHARSET); // 平台生成的XML串
				outputStream.write(buffer);
				outputStream.flush();
				outputStream.close();
				// 接收服务段的数据
			}else{
				OutputStream outputStream = httpConnection.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outputStream);
				try {
					oos.writeObject(params);
					oos.flush();
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				} finally {
					if (oos != null) {
						oos.close();
					}
				}
				outputStream.flush();
				outputStream.close();
			}
			InputStream in = httpConnection.getInputStream();
			byte[] datas = IOUtil.getDataOfInputStream(in);
			if (datas != null && datas.length > 0)
			{
				returnstr = new String(datas, SystemUtil.SYS_CHARSET);
			}
		}
		catch (Exception ioe)
		{
			ioe.printStackTrace();
		}
		return new WdemsDateType(returnstr);
	}
	public void setListern(Object dd)
	{
	}
	public void close(Object outputStream)
	{
	}
	public void reSendData(Object stream, Object outputStream)
	{
		// System.out.println("output stream = " + outputStream);
		try
		{
			if (stream != null)
			{
				String outputStr = GZipTransform.generateString(stream);
				if (outputStr == null)
				{
					throw new FOVException("对象序列化失败");
				}
				if (!(outputStream instanceof PrintWriter))
					return;
				PrintWriter writer = (PrintWriter) outputStream;
				writer.println(outputStr);
				// System.out.println("zip stream = " + outputStr);
				// writer.append(outputStr);
				writer.flush();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void receiveData(Object stream, ListListener listener)
	{
		MyTread client = new MyTread(stream, listener);
		client.start();
	}
	private class MyTread extends Thread
	{
		public Object stream;
		public ListListener listener;
		public MyTread(Object stream, ListListener listern)
		{
			this.listener = listern;
			this.stream = stream;
		}
		public void run()
		{
			InputStream in = (InputStream) ((WdemsDateType) stream)
					.getInReturnDateType();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			try
			{
				int i = 0;
				while ((line = reader.readLine()) != null)
				{
					if (line.trim().length() > 0)
					{
						Object data = getObject(line);
						listener.addObject(data);
						listener.listener();
					}
					i++;
				}
			}
			catch (IOException ex1)
			{
				ex1.printStackTrace();
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (Exception f)
					{
						f.printStackTrace();
					}
				}
				try
				{
					listener.close();
					listener.getRender().stopRenderer();
				}
				catch (Exception e)
				{
					// TODO 自动生成 catch 块
					// e.printStackTrace();
				}
			}
			// InputStream in = (InputStream) ((WdemsDateType)
			// stream).getInReturnDateType();
			//
			// try {
			//
			// StringBuilder source = unZipStream(in);
			// System.out.println("unzip = " + source);
			// Object data = getObject(source);
			// listener.addObject(data);
			// listener.listener();
			//
			// } catch (IOException ex1) {
			// ex1.printStackTrace();
			// } finally {
			// try {
			// listener.close();
			// System.out.println("start print..................");
			// listener.getRender().stopRenderer();
			// } catch (Exception e) {
			// // TODO 自动生成 catch 块
			// e.printStackTrace();
			// }
			//
			// }
		}
	}
	// 把接收到的zip数据读出
	private StringBuilder unZipStream(InputStream input)
			throws UnsupportedEncodingException, IOException
	{
		StringBuilder s = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		for (String line = reader.readLine(); line != null; line = reader
				.readLine())
		{
			s.append(line);
		}
		reader.close();
		return s;
	}
	// 创建反序列化对象
	private Object getObject(String source)
	{
		return GZipTransform.getObjectByStream(source.toString());
	}
}
