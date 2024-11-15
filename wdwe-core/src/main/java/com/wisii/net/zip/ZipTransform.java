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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import sun.misc.BASE64Encoder;

import com.wisii.fov.util.BASE64Decoder;

/**
 * @author 李晓光
 * 提供对数据进行压缩【zip】、解压【zip】，串行化、并行化处理。
 */
public class ZipTransform {
	/**
	 * 把对象转化为BASE64的字符串
	 * 
	 * @param obj
	 *            Object 被转化的对象
	 * @return String 由对象转化而来的字符串
	 */
	public static String generateString(Object obj) throws IOException {
		if (obj == null) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		//把数据进行压缩。
		ZipOutputStream zip = new ZipOutputStream(bos);
		ZipEntry entry = new ZipEntry("wisii");
		zip.putNextEntry(entry);
		
		ObjectOutputStream oos = null;
		String outputStr = null;
		BASE64Encoder encoder = new BASE64Encoder();

		try {
			oos = new ObjectOutputStream(zip);
			oos.writeObject(obj);
			outputStr = encoder.encode(bos.toByteArray());
			oos.flush();
			
			//关闭压缩流
			zip.closeEntry();
			zip.close();
			
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (oos != null) {
				oos.close();
			}
		}

		outputStr = outputStr.replaceAll("\r\n", "%0D%0A");
		outputStr = outputStr.replaceAll("\n", "%0A");
		outputStr = outputStr.replaceAll("\r", "%0D");
		return outputStr;
	}

	/**
	 * 把字符串转化为字节流，在从中读出相应的对象
	 * 
	 * @param str
	 *            要转化的字符串
	 * @return Object 要得到的对象
	 */
	public static Object getObjectByStream(String str) {
		Object obj = null;
		try {
			str = str.replaceAll("%0D%0A", "\r\n");
			str = str.replaceAll("%0A", "\n");
			str = str.replaceAll("%0D", "\r");
			byte[] byte_obj = new BASE64Decoder().decodeBuffer(str);
			ByteArrayInputStream in = new ByteArrayInputStream(byte_obj);
			
			//对数据进行解压
			ZipInputStream zip = new ZipInputStream(in);
			zip.getNextEntry();
			
			ObjectInputStream ois = new ObjectInputStream(zip);
			obj = ois.readObject();
			
			//关闭压缩流
			zip.closeEntry();
			zip.close();
			
			ois.close();
		} catch (StreamCorruptedException e) {
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
		return obj;
	}
}
