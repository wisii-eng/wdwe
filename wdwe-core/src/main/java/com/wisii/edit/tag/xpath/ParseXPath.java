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
 */package com.wisii.edit.tag.xpath;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPath;
import org.apache.xpath.jaxp.JAXPPrefixResolver;


/**
 * 负责解析并返回xpath组成的列表
 * @author 闫舒寰
 * @version 1.0 2009/06/30
 */
public enum ParseXPath {
	
	Instance;
	
	/**
	 * 解析xpath成一个链表
	 * @param xpath
	 * @return
	 */
	public List<XPathNodes> parseXPath(final String xpath) {
		
		if (xpath.equals("") || xpath == null) {
			throw new IllegalArgumentException();
		} else {
			//这里为了容错当xpath最后还有个'/'的时候会报错，xpath规范不允许这样
			StringBuilder sb = new StringBuilder(xpath);
			if (xpath.endsWith("/")) {
				sb.deleteCharAt(sb.length() - 1);
			}
			
			//创建一个自定义个的visitor
			XVisitor xv = new XVisitor();
			
			try {
				XPath xp = new XPath(sb.toString(), null,
						new JAXPPrefixResolver(null),
						org.apache.xpath.XPath.SELECT);
				xp.callVisitors(xp, xv);
				
			} catch (TransformerException e) {
				//TODO 抛出合适的异常
				e.printStackTrace();
			}
			
			return xv.getParsedXPathNodeList();
		}
	}
	
	/**
	 * 把XPath的绝对路径转化成相对路径
	 * @param absoluteXPath 有绝对路径的xpath
	 * @param relativeXPath 有相对路径的xpath
	 * @return 当前相对xpath的绝对路径
	 */
	public String transformXPath(final String absoluteXPath, final String relativeXPath){
		
		
		List<XPathNodes> abList = parseXPath(absoluteXPath);
		List<XPathNodes> reList = parseXPath(relativeXPath);
		
		int abInt = abList.size();
		int reInt = reList.size();
		
		int minInt = 0;
		
		if (abInt <= reInt) {
			minInt = abInt;
		} else {
			minInt = reInt;
		}
		
		for (int i = 0; i < minInt; i++) {
			XPathNodes abNode = abList.get(i);
			XPathNodes reNode = reList.get(i);
			if (abNode.getNode() == null || reNode.getNode() == null) {
				//FIXME 当绝对path中有属性节点的时候，最后一个节点为空的时候则会出现这个问题
			} else {
				if (abNode.getNode().equals(reNode.getNode())) {
					if (abNode.getNumber() != null) {
						reNode.setNumber(abNode.getNumber());
					}
				}
			}
		}
		
		StringBuilder sb = new StringBuilder("/");
		
		for (XPathNodes xNodes : reList) {
			sb.append(xNodes.toString());
			sb.append("/");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
//		System.err.println("abs:" + absoluteXPath + " rel:" + relativeXPath + " sb:" + sb.toString());
		
		return sb.toString();
	}
	
	/**
	 * 获得其父节点的xpath路径
	 * @param xpath
	 * @return
	 */
	public String getParentXPath(final String xpath){
//		List<XPathNodes> xpList = ParseXPath.Instance.parseXPath(xpath);
		
		String[] s = xpath.split("/");
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length - 1; i++) {
			sb.append(s[i]);
			sb.append("/");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
	/**
	 * 获得其叶子节点的位置
	 * @param xpath
	 * @return
	 */
	public String getLastChildNumber(final String xpath) {
		
		List<XPathNodes> xpList = ParseXPath.Instance.parseXPath(xpath);
		
		String temp = null;

		for (XPathNodes xpn : xpList) {
			temp = xpn.getNumber();
		}
		
		return temp;
	}

}
