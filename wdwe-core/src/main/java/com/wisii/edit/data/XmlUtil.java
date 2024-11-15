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
 */package com.wisii.edit.data;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 将字符串转成节点
	 * 
	 * @param e xml字符串
	 * @return 返回xml节点的nodelist  如果不是节点则返回null
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static NodeList returnElement(String e)
			throws ParserConfigurationException, SAXException, IOException {
		// TODO 未实现返回值
		if (e == null || e.equalsIgnoreCase(""))
			return null;

		if (e.startsWith("<")) {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><aaa>" + e
							+ "</aaa>")));

			Element root = doc.getDocumentElement();
			return root.getChildNodes();
			

		}
		return null;
	}
	
	/**
	 * 处理返回的内容为值
	 * 
	 * @param e
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String[] returnElementText(String e)
			throws ParserConfigurationException, SAXException, IOException
	{
		// TODO 未实现返回值
		if (e == null || e.equalsIgnoreCase(""))
			return null;
		if (e.startsWith("<"))
		{
			NodeList a = returnElement(e);
			if (a.getLength() > 0)
			{
				String[] ss = new String[a.getLength()];
				for (int i = 0; i < a.getLength(); i++)
				{
					ss[i] = a.item(i).getTextContent();
				}
				return ss;
			}
		}
		int a = e.indexOf('"');
		StringBuilder bb = new StringBuilder();
		while ((a + 1) > 0)
		{
			e = e.substring(a + 1);
			a = e.indexOf('"');
			bb.append(e.substring(0, a) + ",");
			e = e.substring(a + 1);
			a = e.indexOf('"');
		}
		return bb.toString().split(",");
	}
	

}
