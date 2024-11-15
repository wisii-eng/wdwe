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
package com.wisii.edit.tag.components.select.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.wisii.component.startUp.SystemUtil;

/**
 * 该类为服务端类，是WisSelectDataInterface接口的一个固定实现用于读取select数据文件返回客户端
 * @author liuxiao
 *
 */
public class FileSelectData implements WisSelectDataInterface {

	
	public String getDataSource(String translate) {
		String strAbsPath =null;
		InputStreamReader myReader=null;
		try {
		 strAbsPath = SystemUtil.CONFRELATIVEPATH+translate;
		myReader=new InputStreamReader(new FileInputStream(strAbsPath),SystemUtil.FILE_CHARSET);
		} catch (FileNotFoundException e) {
		
			if (strAbsPath.startsWith("file:")) {
				strAbsPath = strAbsPath.substring(6);
				strAbsPath = File.separator+strAbsPath;
				System.out.println("strAbsPath="+strAbsPath);
				try {
					myReader=new InputStreamReader(new FileInputStream(strAbsPath),SystemUtil.FILE_CHARSET);
				} catch (UnsupportedEncodingException e1) {
					System.out.println("读取文件错误"+strAbsPath);
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					System.out.println("文件没有找到"+strAbsPath);
					e1.printStackTrace();
				}
			}
		
	} catch (UnsupportedEncodingException e1) {
		System.out.println("读取文件错误"+strAbsPath);
		e1.printStackTrace();
		return null;
	} 
		
		BufferedReader myBufferedReader = new BufferedReader(myReader);
		String myString = null;
		StringBuilder resultString = new StringBuilder();
		boolean s=true;
			try {
				while((myString = myBufferedReader.readLine())!= null) {
					if(s)
					{
						s=false;
						if(!myString.startsWith("<"))
						{
							int b=myString.indexOf('<');
							myString=myString.substring(b);
						}
					}
//				System.out.println (myString);
				resultString.append(myString).append("\r\n");
				}
				myBufferedReader.close();
				myReader.close();
				return resultString.toString();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
		return null;
	}
	public static void main (String [] args)
	{
		new FileSelectData().getDataSource("");
	}

}
