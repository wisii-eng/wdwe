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
 */
package com.wisii.component.createrender;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TcpIpPrint {
	Socket client;

	BufferedReader socketReader;

	String line = null;

	OutputStream socketWriter;

	String ip = "127.0.0.1";

	int port = 9100;

	public TcpIpPrint(String ss) {
		if (ss != null && !ss.equals("")) {
			String[] as = ss.split(":");
			ip = as[0];
			if (as.length > 1) {
				port = Integer.parseInt(as[1]);
			}
		}

	}

	public String card_status(BufferedInputStream input) {
		try {

			// 创建一个socket
			client = new Socket(ip, port);
			// 创建输入输出数据流
			socketReader = new BufferedReader(new InputStreamReader(client
					.getInputStream()));
			socketWriter = client.getOutputStream();
			// 发送数据

			int count = 0; // 每个缓冲区的实际数据长度
			byte[] buffer0 = new byte[1024]; // 数据缓冲区
			while ((count = input.read(buffer0)) != -1) {
				socketWriter.write(buffer0, 0, count);
			}

			socketWriter.flush();
			// 设置联接超时的时间
			client.setSoTimeout(500000);
			// // 接收数据
			line = socketReader.readLine();
			// 关数据流
			socketWriter.close();
			socketReader.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
			line = "系统忙，请稍后再试";
		}
		return (line);
	}

	public static void main(String arg[]) {
		TcpIpPrint ss = new TcpIpPrint("");

		try {
			ss.card_status(new BufferedInputStream(new FileInputStream(
					"D:/lx/testbak/Mypcl.ps")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

//////////////////////////////////////
