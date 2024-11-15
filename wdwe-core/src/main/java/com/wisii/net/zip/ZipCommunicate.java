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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.command.plugin.FOMethod;

/**
 * @author 李晓光
 * 对序列换后的数据，进行压缩、解压处理。
 * 即经采用Base64编码后，再对数据进行压缩。
 */
public class ZipCommunicate extends AbstractCommunicate {
	
	public ZipCommunicate(String serverUrl) {
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
				String outputStr = FOMethod.generateString(stream);
				if (outputStr == null) {
					throw new FOVException("对象序列化失败");
				}
				if (!(outputStream instanceof PrintWriter))
					return;
				PrintWriter writer = (PrintWriter) outputStream;

				createZipStream(outputStr, writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 产生压缩数据。
	private void createZipStream(String source, PrintWriter writer)
			throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(byteOut);
		ZipOutputStream zip = new ZipOutputStream(data);

		ZipEntry entry = new ZipEntry("abc");
		zip.putNextEntry(entry);
		zip.write(source.getBytes());
		zip.closeEntry();
		zip.close();

		String str = byteOut.toString();
		writer.println(str);
		writer.flush();
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
					// e.printStackTrace();
				}

			}
		}
	}

	// 把接收到的zip数据读出
	private StringBuilder unZipStream(InputStream input)
			throws UnsupportedEncodingException, IOException {
		StringBuilder s = new StringBuilder();
		DataInputStream data = new DataInputStream(input);
		ZipInputStream zip = new ZipInputStream(data);
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(zip));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				s.append(line);
			}

		}
		return s;
	}

	// 创建反序列化对象
	private Object getObject(StringBuilder source) {
		return FOMethod.getObjectByStream(source.toString());
	}
}
