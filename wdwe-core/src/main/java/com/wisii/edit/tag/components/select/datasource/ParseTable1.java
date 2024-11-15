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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * 该类用于处理数据类型为表格1型，他的xml文件格式如下例： <根节点> <searchKey> <第一行标志节点>
 * <单个元素1>节点值，我们主要关心的内容</单个元素1> <单个元素2>节点值，我们主要关心的内容</单个元素2> </第一行标志节点>
 * <第二行标志节点> <单个元素1>节点值，我们主要关心的内容</单个元素1> <单个元素2>节点值，我们主要关心的内容</单个元素2>
 * </第二行标志节点> <。。。。。。> </searchKey> </根节点>
 * 
 * @author liuxiao
 * 
 */
public class ParseTable1 implements ParseData {

	/**
	 * 该类用于解析xml，他需要关心的两个参数root开始解析的节点， 并关心在根节点下出现的searchkey 作为节点的属性，那么这个
	 * 节点就必须是列节点的父节点，也就是说他可以是根节点的属性。
	 * 
	 * @param e
	 *            element的list,会有多个element传进来
	 * @param root
	 *            开始解析的根节点
	 * @param tovi
	 *            向tovi中设置信息
	 */
	public void parse(List<Element> e, String root, TableOrViewInfo tovi) {
		if (e == null)
			return;
		if (root == null || root.isEmpty())
			return;
		List<String[]> datas=new ArrayList<String[]>();
		for (int i = 0; i < e.size(); i++) {
			List<String[]> subdata = parseone(e.get(i), root, tovi);
			if(subdata!=null&&!subdata.isEmpty())
			{
				datas.addAll(subdata);
			}
		}
		tovi.addDatas(datas);
	}

	/**
	 * 解析一个list中的内容
	 * 
	 * @param e
	 * @param root
	 * @param tovi
	 */
	private List<String[]> parseone(Element e, String root, TableOrViewInfo tovi) {
		String searchkey = null;
		// 遍历得到根节点
		Node dd = ParseDataBuilder.getRootElement(e, root);
		// 查找searchkey属性，如果根节点是searchKey节点，则其子节点开始为列节点，如果不是
		// 则再向下找一层子节点为列节点
		searchkey = getSearchKey(dd);
		if (searchkey == null) {
			return dealSearchKeyList(dd.getChildNodes(), tovi);
		} else {
			return dealRowAndColumn(dd.getChildNodes(), tovi, searchkey);
		}

	}

	/**
	 * 查看当前节点是不是具有searchKey的节点
	 * 
	 * @param nn
	 * @return searchKey
	 */
	private String getSearchKey(Node nn) {
		if(ParseDataBuilder.isTextNode(nn)) return null;
			Node src = nn.getAttributes().getNamedItem(
					TableOrViewInfo.COLUMN_searchKey);
			if (src != null)
				return src.getNodeValue();
			else
				return null;
		
	}

	/**
	 * 此方法是用于解析行列值
	 * 
	 * @param nl
	 *            为列节点的list
	 * @param tovi
	 *            键表所用到的的信息
	 */
	private List<String[]> dealRowAndColumn(NodeList nl, TableOrViewInfo tovi,
			String searchKey) {
		if (searchKey == null)
			searchKey = "";
		// 如果没有列信息的时候就把一个有长度的数组付给TableOrViewInfo
		String[] dd = tovi.getColumnType();
		if (dd == null || dd.length < 1) {
			// 所有的列加上一个searchKey列
			String[] cd = new String[ParseDataBuilder.countChildNodes(ParseDataBuilder.getFirstUnTextNode(nl))];
			// 将列信息添加到TableOrViewInfo中
			tovi.setColumnType(cd);
		}
		List<String[]> al = new ArrayList<String[]>();
		// 按列写数据
		for (int i = 0; i < nl.getLength(); i++) {
			if(ParseDataBuilder.isTextNode(nl.item(i))) continue;
			NodeList item = nl.item(i).getChildNodes();
			int len=item.getLength();
			List<String> datas=new ArrayList<String>();
			for (int j = 0; j < len; j++) {
				if(ParseDataBuilder.isTextNode(item.item(j))) continue;
				datas.add(item.item(j).getTextContent());
			}
			String[] rowdatas=new String[datas.size()];
			rowdatas=datas.toArray(rowdatas);
			al.add(rowdatas);
		}
		return al;

	}

	/**
	 * 此方法是用于searchKey及其下面的行列值
	 * 
	 * @param nl
	 *            为列节点的list
	 * @param tovi
	 *            键表所用到的的信息
	 */
	private List<String[]> dealSearchKeyList(NodeList nl, TableOrViewInfo tovi) {
		List<String[]> datas = null;
		for (int i = 0; i < nl.getLength(); i++) {
			Node nn = nl.item(i);
			if (nn.getNodeName() == null || nn.getNodeName().equals("#text"))
				continue;
			String ss = getSearchKey(nn);
			// 如果存在则说明有searchkey的节点
			if (ss != null)
				datas = dealRowAndColumn(nn.getChildNodes(), tovi, ss);
			// 则说明没有searchkey的节点，整个list都是列节点
			else
				datas = dealRowAndColumn(nl, tovi, null);
			break;
		}
		return datas;

	}


	public void parseTempData(String data, String root, TableOrViewInfo tovi) {
		// 先键表
		String[] res = data.split("\\|");
		String[] a = res[0].split("-");
		String[] tablehead = a[0].split(",");
		String[] ct = a[1].split(",");
		tovi.setTablehead(tablehead);
		String[] colnum = res[1].split(",");
		String[] columntype = new String[colnum.length];
		StringBuilder columnString = new StringBuilder();
		for (int i = 0; i < colnum.length; i++) {
			try {
				columntype[i] = ct[i];
			} catch (ArrayIndexOutOfBoundsException e) {
				columntype[i] = "varchar";
			}
			columnString.append(TableOrViewInfo.COLUMN_NAME_FIRST + i + ",");
		}
		tovi.setColumnString(columnString.toString());
		tovi.setColumnType(columntype);
		List<String[]> datas = new ArrayList<String[]>();
		for (int i = 1; i < res.length; i++) {
			datas.add(res[i].split(","));
		}
		tovi.addDatas(datas);
	}
	

}
