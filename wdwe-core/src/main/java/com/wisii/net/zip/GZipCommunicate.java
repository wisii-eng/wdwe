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
package com.wisii.net.zip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.mainFramework.commun.CommunicateProxy;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.command.plugin.FOMethod;

/**
 * @author 李晓光
 * 在编码前，对数据进行GZIP压缩。
 */
public class GZipCommunicate implements CommunicateProxy{
	
	@SuppressWarnings("unused")
	private ListListener listener;
	private String serverUrl;

	public GZipCommunicate(String serverUrl) {
		this.serverUrl = serverUrl;
	}


	/**
	 * 服务器返回数据的数据流
	 * 
	 * @param url
	 *            请求服务的ip地址
	 * @param serverType
	 *            请求服务的类型
	 * @param params
	 *            需要传给服务器的参数
	 */
	public WdemsDateType send(String serverType, Object params) {
		Map<String, Object> facade = new HashMap<String, Object>();
		facade.put("serverType", serverType);
		facade.put("para", params);

		return send(facade);
	}

	public WdemsDateType send(Object params) {
		InputStream in = null;
		try {

			String myUrl = serverUrl;
			String str = FOMethod.generateString(params);

			// 此函数是平台主动向服务段发起的http请求,并接收服务端的响应

			HttpURLConnection httpConnection;

			httpConnection = (HttpURLConnection) new URL(myUrl)
					.openConnection(); // 建立一个HttpURLConnection
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setAllowUserInteraction(true);
			httpConnection.connect();
			// 发送数据给服务端
			OutputStream outputStream = httpConnection.getOutputStream();

			byte[] buffer = str.getBytes(SystemUtil.SYS_CHARSET); // 平台生成的XML串
			outputStream.write(buffer);
			outputStream.flush();
			outputStream.close();
			// 接收服务段的数据

			in = httpConnection.getInputStream();

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

		return new WdemsDateType(in);
	}
	
	public void setListern(Object dd) {
	}

	public void close(Object outputStream) {
		
	}
	/**
	 * 用于数据的返回
	 * 
	 * @param stream
	 * @param outputStream
	 *            输出流的引用
	 */
	public void reSendData(Object stream, Object outputStream) {
		try {
			if (stream != null) {
				String outputStr = GZipTransform.generateString(stream);
				if (outputStr == null) {
					throw new FOVException("对象序列化失败");
				}
				if (!(outputStream instanceof StringWriter))
					return;
				StringWriter writer = (StringWriter) outputStream;
				
//				writer.println(outputStr);
				
				writer.append(outputStr);
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 字节流转字符串
	 * 
	 * @param stream
	 * @param listener
	 * 
	 */

	public void receiveData(Object stream, ListListener listener) {
		MyTread client = new MyTread(stream, listener);
		client.start();

	}

	private class MyTread extends Thread {
		public Object stream;
		public ListListener listener;

		public MyTread(Object stream, ListListener listern) {
			this.listener = listern;
			this.stream = stream;
		}

		public void run() {

			InputStream in = (InputStream) ((WdemsDateType) stream).getInReturnDateType();
			
			try {

				StringBuilder source = unZipStream(in);
				Object data = getObject(source);
				listener.addObject(data);
				listener.listener();

			} catch (IOException ex1) {
				ex1.printStackTrace();
			} finally {
				try {
					listener.close();
					listener.getRender().stopRenderer();
				} catch (Exception e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}

			}
		}
	}

	// 把接收到的zip数据读出
	private StringBuilder unZipStream(InputStream input) throws UnsupportedEncodingException, IOException {
		StringBuilder s = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			s.append(line);
		}
		return s;
	}

	// 创建反序列化对象
	private Object getObject(StringBuilder source) {
		return GZipTransform.getObjectByStream(source.toString());
	}
}
