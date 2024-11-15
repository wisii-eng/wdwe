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
 *    SAXConversionMap.java
 *    version 1.0
 *    汇智互联
 */
package com.wisii.fov.util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author zkl.2007-04-23. 用来将一个外部的转换表解析成map格式。 外部xml文件的格式如下： <map> <pair>
 *         <code>  </code> <name> </name> </pair> ...(任意多个<pair>元素) </map>
 * 
 * 解析出来的conversionMap结构： code (key) -- name(value).
 */
public class SAXConversionList extends DefaultHandler {

	private List conversionList; // 存储转换表解析结果。

	private String code; // 存储<code>的文本内容。

	private String name; // 存储<name>的文本内容。

	private int elementType = 0; // 1:当前解析到<code>元素; 2:当前解析到<name>元素；0:其他.

	public void startDocument() {
		conversionList = new ArrayList();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (localName.equals("code")) {
			elementType = 1;
		} else if (localName.equals("name")) {
			elementType = 2;
		} else {
			elementType = 0;
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String text = new String(ch, start, length).trim();

		if (text.length() > 0) {

			if (elementType == 1) {
				code = text;
			} else if (elementType == 2) {
				name = text;
			}
		}
		if (text.length() < 1 && elementType == 2)
			name = text;
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("name")) {

			conversionList.add(new CodeToName(code, name));
			/**
			 * 20090328 刘晓 添加置空操作
			 */
			code = "";
			name = "";
			/** -------------------------------------* */
		}
	}

	public static List getConversionList(String xmlFilePath)
			throws TransformerException {
		SAXConversionList handler = new SAXConversionList();
		Source src = new StreamSource(new File(xmlFilePath));
		Result res = new SAXResult(handler);
		TransformerFactory TransFactory = TransformerFactory.newInstance();
		Transformer transformer = TransFactory.newTransformer();
		transformer.transform(src, res);
		return handler.conversionList;
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws TransformerException
	 */
	public static List getConversionList(InputStream inputStream)
			throws TransformerException {
		SAXConversionList handler = new SAXConversionList();
		Source src = new StreamSource(inputStream);
		Result res = new SAXResult(handler);
		TransformerFactory TransFactory = TransformerFactory.newInstance();
		Transformer transformer = TransFactory.newTransformer();
		transformer.transform(src, res);
		return handler.conversionList;
	}

	/**
	 * test.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		List conversionMap = null;
		try {
			conversionMap = SAXConversionList
					.getConversionList("xml/xml/dest.xml");
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		if (conversionMap == null) {
			System.out.println("null!");
			return;
		}
		System.out.println("size: " + conversionMap.size());
		// Iterator iter=conversionMap.entrySet().iterator();
		// while(iter.hasNext()){
		// Map.Entry pairs=(Map.Entry)iter.next();
		// System.out.println(pairs.getKey()+" -- "+pairs.getValue());
		// }
	}

	public static List getList(Map map) {
		List list = new ArrayList();
		Iterator itertor = map.keySet().iterator();
		while (itertor.hasNext()) {
			String key = (String) itertor.next();
			String value = (String) map.get(key);
			list.add(new CodeToName(key, value));
		}
		return list;
	}
	public static Map getMap(List list) {
		Map map = new HashMap();
		for(int i=0;i<list.size();i++)
		{
			CodeToName cn=(CodeToName)list.get(i);
			map.put(cn.getCode(),cn.getName() );
		}
		return map;
	}

}

