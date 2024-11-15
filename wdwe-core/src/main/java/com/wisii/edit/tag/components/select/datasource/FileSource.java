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
 */package com.wisii.edit.tag.components.select.datasource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * 处理接收到服务端字符串之后进行解析和键表的工作
 * 实现了DispSource接口
 * @author liuxiao
 *
 */
public  class FileSource implements DispSource {
	
	/* 回传的参数 */
	String content;
	/**
	 * 构造
	 * 
	 * @param content
	 */
	public FileSource(String content)
	{
		this.content=content;
	}
	
	/**
	 * 真正处理回传字符串的地方
	 * 
	 * @param tovi
	 *            建表信息
	 */
	public void parseContent(TableOrViewInfo tovi,String struts, String root){
		
		// 得到文件的字符串之后把它弄成element对象。
		Document document = null;
	      try
	       {
	           DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	           document = parser.parse( new InputSource(new StringReader(content)) );
	           List ss=new ArrayList();
	           
	           ss.add(document.getDocumentElement());
	           //调用解析xmlElement的类进行处理
	           ParseDataBuilder.parseFactory(struts).parse(ss, root, tovi);
	       }catch(Exception ex)
	       {             
	    	   	System.out.println(ex.getMessage());
//	            System.out.println("content = "+content);
	       } 
		
	}
	

}
