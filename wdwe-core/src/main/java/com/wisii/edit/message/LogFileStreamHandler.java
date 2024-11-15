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
 * @LogFileStreamHandler.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.message;

/**
 * 类功能描述：
 *
 * 作者：p.x
 * 创建日期：2009-7-20
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/** *//**
 * 自定义日志文件处理器
 * @author Administrator
 */
public class LogFileStreamHandler extends StreamHandler{
	//输出流
	private MeteredStream msOut;
	//是否添加的玩家末尾
	private boolean append;
	//希望写入的日志路径
	private String fileUrl;
	
	private File logfile;
	/** 
	 * 初始化自定义文件流处理器
	 * @param fileUrl 文件路径， 可以是个目录或希望的日志名称，如果是个目录则日志为“未命名” 
	 * 指定日志名称时不需要包括日期，程序会自动生成日志文件的生成日期编号
	 * @param append 是否将日志写入已存在的日志文件中
	 * @throws java.lang.Exception
	 */
	public LogFileStreamHandler(String fileUrl, boolean append) throws Exception {
		super();
		this.fileUrl = fileUrl;
		this.append = append;
		openWriteFiles();
	}

	/** 
	 * 获得将要写入的文件
	 */
	private synchronized void openWriteFiles() throws Exception {
		if (fileUrl == null) {
			throw new IllegalArgumentException("文件路径不能为null");
		}
		super.close();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String trace = sdf.format(new Date().getTime());

		File file = new File(fileUrl);
		if (fileUrl.endsWith("/") || fileUrl.endsWith("\\")) {
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		if (file.isDirectory()) {
			logfile = new File(fileUrl + File.separator + "wdems" + "_" + trace
					+ ".log");
		} else {
			logfile = new File(fileUrl + "_" + trace + ".log");
		}
		openFile(logfile, append);	
	}

	/**
	 * 打开需要写入的文件
	 * 
	 * @param file
	 *            需要打开的文件
	 * @param append
	 *            是否将内容添加到文件末尾
	 */
	private void openFile(File file, boolean append) throws Exception {
//		System.out.println("***opend = true " + file.toString());
		int len = 0;
		if (append) {
			len = (int) file.length();
		}
		FileOutputStream fout = new FileOutputStream(file.toString(), append);
		BufferedOutputStream bout = new BufferedOutputStream(fout);
		msOut = new MeteredStream(bout, len);
		setOutputStream(msOut);
	}
	
	/** 
	 * 发布日志信息
	 */
	public synchronized void publish(LogRecord record) {
		super.publish(record);
		super.flush();
	}

	/** 
	 * 抄自FileHandler的实现，用于跟踪写入文件的字节数
	 * 这样以便提高效率
	 */
	private class MeteredStream extends OutputStream {

		private OutputStream out;
		//记录当前写入字节数
		private int written;

		MeteredStream(OutputStream out, int written) {
			this.out = out;
			this.written = written;
		}

		public void write(int b) throws IOException {
			out.write(b);
			written++;
		}

		public void write(byte buff[]) throws IOException {
			out.write(buff);
			written += buff.length;
		}

		public void write(byte buff[], int off, int len) throws IOException {
			out.write(buff, off, len);
			written += len;
		}

		public void flush() throws IOException {
			out.flush();
		}

		public void close() throws IOException {
			out.close();
		}
	}
}

