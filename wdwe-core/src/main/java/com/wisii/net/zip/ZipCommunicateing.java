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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.fov.apps.FOVException;

/**
 * @author 李晓光
 * 与ZipCommunicate的区别是，采用该方式进行压缩是在采用Base64编码之前进行的。
 * 该方式应该比ZipCommunicate效率要好。
 */
public class ZipCommunicateing extends AbstractCommunicate{
	
	public ZipCommunicateing (String serverUrl) {
		super(serverUrl);
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
				String outputStr = ZipTransform.generateString(stream);
				if (outputStr == null) {
					throw new FOVException("对象序列化失败");
				}
				if (!(outputStream instanceof PrintWriter))
					return;
				PrintWriter writer = (PrintWriter) outputStream;
				
				writer.println(outputStr);
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
		return ZipTransform.getObjectByStream(source.toString());
	}
}
