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
 *//*
 * FOMethod.java
 *
 * 改版履历:2007.04.20
 *
 * 版本信息:1.0
 *
 * Copyright:WISe Internat Information Co.,Ltd.
 */

package com.wisii.fov.command.plugin;

import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import com.alibaba.fastjson2.JSON;
import com.wisii.component.setting.WisiiBean;
import com.wisii.fov.util.BASE64Decoder;
import com.wisii.fov.util.Sutil;

/**
 * 提供各个服务中使用的公共方法
 */
public class FOMethod {
	public static final String XMLDAT = "xml";
	private static final String XSLDAT = "xsl";
	private static final String XSDDAT = "xsd";
	public static final String XMLBAK = "xmlbak";

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
		return JSON.toJSONString(obj);
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = null;
//		String outputStr = null;
//		BASE64Encoder encoder = new BASE64Encoder();
//
//		try {
//			oos = new ObjectOutputStream(bos);
//			oos.writeObject(obj);
//			outputStr = encoder.encode(bos.toByteArray());
//			oos.flush();
//			oos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			if (oos != null) {
//				oos.close();
//			}
//		}
//
//		outputStr = outputStr.replaceAll("\r\n", "%0D%0A");
//		outputStr = outputStr.replaceAll("\n", "%0A");
//		outputStr = outputStr.replaceAll("\r", "%0D");
//		return outputStr;
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
			ObjectInputStream ois = new ObjectInputStream((in));
			obj = ois.readObject();
			ois.close();
		} catch (StreamCorruptedException e) {
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
		return obj;
	}

	/**
	 * 流转成字符串
	 * 
	 * @param inputStream
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String trunString(InputStream inputStream, String charest)
			throws UnsupportedEncodingException {
		byte[] iXMLData = null;

		BufferedInputStream input = null; // 输入流,用于接收请求的数据
		byte[] buffer0 = new byte[1024]; // 数据缓冲区
		int count = 0; // 每个缓冲区的实际数据长度
		ByteArrayOutputStream streamXML = new ByteArrayOutputStream(); // 请求数据存放对象

		try {

			input = new BufferedInputStream(inputStream);
			while ((count = input.read(buffer0)) != -1) {
				streamXML.write(buffer0, 0, count);
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			if (input != null) {
				try {
					input.close();
				} catch (Exception f) {
					f.printStackTrace();

				}
			}
		}
		iXMLData = streamXML.toByteArray();

		return new String(iXMLData, charest);

	}

	/**
	 * 流转成字符串
	 * 
	 * @param stream
	 *            需要读的流
	 * @param charest
	 *            按照编码读取流
	 * @return 流字符串表示
	 * @throws IOException
	 */
	public static String readInputStream(InputStream stream, String charest)
			throws IOException {
		String line = null;
		StringBuffer sb = new StringBuffer();

		InputStreamReader read;

		read = new InputStreamReader(stream, charest);

		BufferedReader reader = new BufferedReader(read);

		boolean ss = false;
		while ((line = reader.readLine()) != null) {
			sb.append(line + '\n');
		}

		return sb.toString();
	}

	/**
	 * 流转成字符串
	 * 
	 * @param stream
	 *            需要读的流
	 * @return 流字符串表示
	 * @throws IOException
	 */
	public static String readInputStream(InputStream stream) throws IOException {

		BufferedInputStream input = null; // 输入流,用于接收请求的数据
		input = new BufferedInputStream(stream);
		byte[] iXMLData = null;
		String chre = null;

		int a = 0;
		int count = 0; // 每个缓冲区的实际数据长度
		ByteArrayOutputStream streamXML = new ByteArrayOutputStream(); // 请求数据存放对象
		while ((count = input.read()) != -1) {
			if (a == 0) {
				// 刚开始的时候

				// 读第一个字符如果不是60则要去掉三个
				if (count != 60) {
					input.read();
					input.read();
					if (count == 239) {
						chre = "UTF-8";
					}
				} else
					streamXML.write(count);
				if (chre == null) {
					byte[] b = new byte[50];
					ByteArrayOutputStream streamXML2 = new ByteArrayOutputStream(); // 请求数据存放对象
					input.read(b, 0, 50);
					streamXML2.write(b, 0, 50);
					streamXML.write(b, 0, 50);
					String line = new String(streamXML2.toByteArray(), "UTF-8");
					if (line.indexOf("?xml") > -1
							&& line.indexOf("version") > 1) {

						String str;
						str = line.substring(line.indexOf("encoding") + 8);
						String[] suh = null;
						if (str.indexOf("'") > 0) {
							suh = str.split("'");
						}
						if (str.indexOf("\"") > 0) {
							suh = str.split("\"");
						}
						if (suh != null && suh.length > 2) {

							chre = suh[1].trim();

						}

					}

				}
				a++;
			} else
				// 其他
				streamXML.write(count);

		}

		iXMLData = streamXML.toByteArray();
		if (chre == null)
			chre = "UTF-8";
		String dd = new String(iXMLData, chre);
		return dd;

	}

	/**
	 * 读取文件
	 * 
	 * @param filename
	 *            文件名
	 * @return 返回的文件流
	 * @throws IOException
	 *             如果连接接错误则会抛出
	 */
	public static InputStream readFile(String filename) throws IOException {
		InputStream fis = null;
		try {
			// byte[] b = new byte[10];
			fis = new FileInputStream(filename);
		} catch (Exception e) {
			if (filename.startsWith("http")) {
				URL effURL = null;
				try {
					effURL = new URL(filename);
				} catch (MalformedURLException e1) {

					e1.printStackTrace();
				}

				URLConnection connection = effURL.openConnection();
//				connection.setAllowUserInteraction(false);
//				connection.setDoInput(true);
				connection.connect();
//				Sutil.sc(connection.getDate());
				fis=connection.getInputStream();
//				fis = new StreamSource(connection.getInputStream(), filename)
//						.getInputStream();

			}

		}
		return fis;

	}

	/**
	 * 读取文件,转成字符串
	 * 
	 * @param filename
	 *            文件名
	 * @param charset
	 *            字符串编码
	 * @return 返回转换成功的子夫串
	 * @throws IOException
	 */
	public static String readFile(String filename, String charset)
			throws IOException {
		return readInputStream(readFile(filename));
	}

	/**
	 * 读取字符串转成流
	 * 
	 * @param info
	 *            String
	 * @param charset
	 *            字符串的编码
	 * @return 流
	 */
	public static InputStream readString(String info, String charset) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(info.getBytes(charset));

		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return is;
	}

	/**
	 * 从文件中读出流
	 * 
	 * @param filename
	 *            文件名
	 * @param charset
	 *            字符串编码
	 * @return 返回转换成功的子夫串
	 * @throws IOException
	 */
	public static String readFile(File file, String charset) throws IOException {

		return readInputStream(new FileInputStream(file));
	}

	/**
	 * 得到打印服务
	 * 
	 * @param printname
	 * @return
	 */

	public static PrintService getPrinter(String printname) {

		if (printname == null)
			printname = "";
		// 得到打印服务名
		PrintService[] PRINTSERVICE = PrinterJob.lookupPrintServices();

		if (!printname.equals("")) {
			// 全名匹配，如果匹配上就直接返回了
			for (int i = 0; i < PRINTSERVICE.length; i++) {
				if (PRINTSERVICE[i].getName().equalsIgnoreCase(printname)) {

					return PRINTSERVICE[i];
				}
			}
		}
		// 得到客户端名称
		String clientname = System.getenv("CLIENTNAME");
		// 如果不是远程登录,就返回默认的打印服务
		if (clientname == null || clientname.equalsIgnoreCase("Console")) {
			return PrintServiceLookup.lookupDefaultPrintService();
		} else
		// 远程登录，优先查找匹配的打印机名称和客户端名称。
		{
			PrintService choosePsver = null;
			for (int i = 0; i < PRINTSERVICE.length; i++) {
				String ss = PRINTSERVICE[i].getName();
				if (ss.indexOf(" " + printname + " (") > 0
						&& ss.indexOf(" " + clientname + ")") > 0) {
					return PRINTSERVICE[i];
				}
				if (ss.indexOf(" " + clientname + " (") > 0
						&& choosePsver == null) {
					choosePsver = PRINTSERVICE[i];
				}
			}
			// 没有查找到，则使用
			if (choosePsver != null) {
				PrintService sss = PrintServiceLookup
						.lookupDefaultPrintService();
				if (sss.getName().indexOf(" " + clientname + ")") > 0) {
					return sss;
				} else
					return choosePsver;
			}

		}

		return PrintServiceLookup.lookupDefaultPrintService();

	}


	/**
	 *保存wdwe文件
	 * 
	 * @param wdwefile
	 *            的路径及文件全名
	 * 
	 * 
	 * @throws Exception
	 */
	public static boolean SaveWdwe(String wdwefile, WisiiBean wb, String newxml)
			throws Exception {

		try {
			List entry = readEntry(wdwefile);
			FileOutputStream fileOut = new FileOutputStream(wdwefile);// 建立输出文件流
			CheckedOutputStream checkedOut = new CheckedOutputStream(fileOut,
					new CRC32());// 建立冗余验证流
			ZipOutputStream zipOut = new ZipOutputStream(
					new BufferedOutputStream(checkedOut));// 建立Zip流
			for (int i = 0; i < entry.size(); i++) {
				String en = (String) entry.get(i);

				if (en.endsWith(XMLDAT)) {
					zipOut.putNextEntry(new ZipEntry(en));
					zipOut.write(newxml.getBytes("UTF-8"));
				} else if (en.endsWith(XSLDAT)) {
					zipOut.putNextEntry(new ZipEntry(en));
					zipOut
							.write((wb.getXslString() + (wb.getEditString() == null ? ""
									: wb.getEditString())).getBytes("UTF-8"));

				} else if (en.endsWith(XSDDAT)) {
					zipOut.putNextEntry(new ZipEntry(en));
					zipOut.write(wb.getXsdString().getBytes("UTF-8"));
				}

			}

			zipOut.putNextEntry(new ZipEntry(XMLBAK));
			zipOut.write((wb.getXmlString())
					.getBytes("UTF-8"));

			zipOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}

	public static List readEntry(String wdwefile) throws IOException {
		List entry = new ArrayList();

		ZipFile zfile = new ZipFile(wdwefile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			entry.add(ze.getName());
		}
		zfile.close();
		return entry;
	}

	public static String readEntry(String wdwefile,String filter) throws IOException {
		
		
		ZipFile zfile = new ZipFile(wdwefile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			
			if(ze.getName().endsWith(filter))
				return ze.getName();
		}
		zfile.close();
		return null;
	}

	public static void main(String[] args) {
		String aa = "C://test//test.wdwe";
		WisiiBean wb = new WisiiBean();
		wb.setXml("中文测试有");
		wb.setXsl("中文测试有");
		wb.setXsd("中文测试有");
		try {
			// SaveWdwe(aa,wb);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
