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
 */package com.wisii.net.zip;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.mainFramework.RunbBSReceiveDate;
import com.wisii.component.mainFramework.commun.CommunicateProxy;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.command.plugin.FOMethod;

/**
 * 
 * 老版本的不带压缩的http
 * 
 * @author liuxiao
 * 
 */

public class HttpCommunicate implements CommunicateProxy {

	private ListListener listener;
	private String _serverUrl;

	public HttpCommunicate(String _serverUrl) {
		this._serverUrl = _serverUrl;
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
		Map facade = new HashMap();
		facade.put("serverType", serverType);
		facade.put("para", params);
		
		return send(facade);
	}
	public WdemsDateType send(Object params) {
		InputStream in = null;
		try {

			String myUrl = _serverUrl;
			String str = FOMethod.generateString(params);

			// 此函数是平台主动向服务段发起的http请求,并接收服务端的响应

			HttpURLConnection httpConnection;

			httpConnection = (HttpURLConnection) new URL(myUrl)
					.openConnection(); // 建立一个HttpURLConnection
			httpConnection.setRequestProperty("user-agent", "Mozilla");
			httpConnection.setRequestProperty(
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
				String outputStr = FOMethod.generateString(stream);
				if (outputStr == null) {
					throw new FOVException("对象序列化失败");
				}

				// byte[] buffer = outputStr.getBytes(SystemUtil.CHARSET); //
				// 平台生成的XML串
				((PrintWriter) outputStream).println(outputStr);
				((PrintWriter) outputStream).flush();
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
		RunbBSReceiveDate client = new RunbBSReceiveDate(stream, listener);
		client.start();

	}

	public void setListern(Object dd) {
	}

	public void close(Object outputStream) {
		// TODO 自动生成方法存根

	}

}
