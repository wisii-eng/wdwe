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
 */package com.wisii.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.xml.bind.JAXB;

import org.apache.avalon.framework.configuration.Configuration;
import org.xml.sax.SAXException;

import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.apps.MimeConstants;

public class Server {
	
	private final int MAX_SOCKET;
	private final int MAX_RUN;
	//接收socket线程池
	private final Executor socketPool;
	//运行接口线程池
	private final Executor runPool;
	/*刘晓添加*/
	public static FovFactory factory=null;
	public static FOUserAgent foUserAgent = null;
	public static  File userConfigFile = null;
	public static Configuration conf=null;
	//public static PDFRenderer _renderer=null;
	/*------------------------------------------*/
	//模板地址
	private final String xslt;
	//生成文件的计数器
	private static int l;
	//server端口
	private final int serverPort;
	//server配置文件所在地
	private static final String configPath = System.getProperty("user.dir") + "/test/com/wisii/server/ServerConfig.xml";
	
	private final String pdfName;
	
	//读配置文件
	public Server() {
		
		File xml = new File(configPath);
		ServerConfig sc = JAXB.unmarshal(xml, ServerConfig.class);
		
		serverPort = sc.getServerPort();
		xslt = sc.getXSLTPath();
		MAX_SOCKET = sc.getSocketPoolSize();
		MAX_RUN = sc.getRunPoolSize();
		
		socketPool = Executors.newFixedThreadPool(MAX_SOCKET);
		runPool = Executors.newFixedThreadPool(MAX_RUN);
		
		pdfName = sc.getPdfPath().trim();
	}
	
	public void start(){
		try {
			ServerSocket s = new ServerSocket(serverPort);
			System.out.println("Server start... at port: " + s.getLocalPort());
			while (true) {
				Socket incoming = s.accept();
				FutureTask<Boolean> task = new FutureTask<Boolean>(new DealTask(incoming));
				socketPool.execute(task);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class DealTask implements Callable<Boolean>{
		
		private final Socket s;
		
		public DealTask(final Socket s) {
			this.s = s;
		}

		public Boolean call() throws Exception {
			handleRequest(s);
			return true;
		}
	}
	
	private void handleRequest(final Socket connection){
		try {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String userInput;
				while ((userInput = in.readLine()) != null) {
					sb.append(userInput);
				}
				runPool.execute(new FutureTask<Boolean>(new RunTask(sb.toString())));
			} finally {
				in.close();
				connection.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int pdfCount;
	
	//调用耗时接口线程
	private class RunTask implements Callable<Boolean> {
		
		private final String s;
		
		
		public RunTask(final String xml) {
			s = xml;
		}
	
		public Boolean call() throws Exception {

//			System.out.println("Start generating pdf....");
			long currentTimeMillis = System.currentTimeMillis();
		//	DriectBackPrintTask.print(s, xslt, pdfName + l++ + ".pdf");
			long currentTimeMillis1 = System.currentTimeMillis();
			System.out.println(++pdfCount + " pdf " + "start: " + currentTimeMillis + " end: " + currentTimeMillis1);
//			System.out.println(currentTimeMillis1 - currentTimeMillis);
			
			return null;
		}
	}
	
	public static void main(final String[] args) {

		
		factory=FovFactory.newInstance();
		foUserAgent = factory.newFOUserAgent();
		
		userConfigFile = new File(System.getProperty("user.dir") + "/wdemsSource/resource/renderconfig.xml");
		//_renderer = new PDFRenderer();
		try {
			factory.setUserConfig(userConfigFile);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conf=foUserAgent.getUserRendererConfig(MimeConstants.MIME_PDF);
		
//		String [] a=new String[]{"xml=C:\\Projects\\Job\\WdemsWeb\\WdemsVss\\jsp\\wisiibase\\xml\\sino.xml", 
//				"xsl=C:\\Projects\\Job\\WdemsWeb\\WdemsVss\\jsp\\wisiibase\\template\\aaaliuliu.xsl","pdf=a.pdf"};
//		for(int i =0;i<5;i++)
//		{
//			DriectBackPrintTask.main(a);
//		}
		new Server().start();
	}
}
