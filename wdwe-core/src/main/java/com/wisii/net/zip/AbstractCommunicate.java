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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.mainFramework.commun.CommunicateProxy;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.command.plugin.FOMethod;

/**
 * @author 李晓光
 * 抽象类，实现公共的方法
 */
public abstract class AbstractCommunicate implements CommunicateProxy{
	@SuppressWarnings("unused")
	private ListListener listener;
	private String serverUrl;

	public AbstractCommunicate(String serverUrl) {
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
}
