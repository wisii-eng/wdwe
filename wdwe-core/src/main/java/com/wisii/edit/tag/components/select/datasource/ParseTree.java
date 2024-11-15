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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 该类用于处理数据类型为树形型，他的xml文件格式如下例： 
 * <china>
 *	<province   name= "北京市 "   postalcode= "100001 ">
 *		<city   name= "北京市 "   postalcode= "100001 ">
 *			<county   name= "东城区 "   postalcode= "100010 "/>
 *			<county   name= "西城区 "   postalcode= "100032 "/> 
 *		</city>
 *	</province>
 *	<province   name= "天津市 "   postalcode= "300040 ">
 *		<city   name= "天津市 "   postalcode= "300040 ">
 *			<county   name= "和平区 "   postalcode= "300041 "/>
 *			<county   name= "河东区 "   postalcode= "300171 "/> 
 *		</city>
 *	</province>
 * </china>
 *
 * 树形的数据没有searchkey
 * 
 * @author liuxiao
 * 
 */
	public class ParseTree implements ParseData {

		
	/**
	 * 该方法用于解析xml，他需要关心的两个参数root开始解析的节点， 并关心在根节点下出现的searchkey 作为节点的属性，那么这个
	 * 节点就必须是列节点的父节点，也就是说他可以是根节点的属性。
	 * 
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
		List<String[]> datas=new ArrayList<String[]>();
		for (int i = 0; i < e.size(); i++) {
			List<String[]> subdatas=parseone(e.get(i), root, tovi);
			if(subdatas!=null&&!subdatas.isEmpty())
			{
				datas.addAll(subdatas);
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
		
		// 遍历得到根节点
		return createTable(e.getChildNodes(), tovi);

	}

	

	/**
	 * 此方法是用于解析行列值
	 * 
	 * @param nl
	 *            为列节点的list
	 * @param tovi
	 *            键表所用到的的信息
	 */
	private List<String[]> createTable(NodeList nl, TableOrViewInfo tovi) {
		// 如果没有列信息的时候就把一个有长度的数组付给TableOrViewInfo
		String[] dd = tovi.getColumnType();
		if (dd == null || dd.length < 1) {
			// 所有的列加上一个searchKey列
			String[] cd = new String[nl.getLength()];
			// 将列信息添加到TableOrViewInfo中
			tovi.setColumnType(cd);
		}
		return readTree(nl,tovi,null);
	}
	/**
	 * 遍历子节点并插入记录
	 * @param nl
	 * @param tovi
	 */
	private List<String[]> readTree(NodeList nl, TableOrViewInfo tovi,String fatherid)
 {
		if (nl == null || nl.getLength() < 1)
			return null;
		List<String[]> datas = new ArrayList<String[]>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (ParseDataBuilder.isTextNode(node)) {
				continue;
			}

			// 读取其属性
			NamedNodeMap items = node.getAttributes();
			String[] rowdata = new String[items.getLength() + 1];
			// 内循环得到属性值进行拼接
			for (int j = 0; j < items.getLength(); j++) {
				rowdata[j] = items.item(j).getTextContent();

			}
			rowdata[items.getLength()] = fatherid;
			datas.add(rowdata);

			// 得到父节点id
			String fid = items.item(tovi.getfirstValueNm() - 1).getNodeValue();
			// 查找子节点
			List<String[]> subdatas = readTree(node.getChildNodes(), tovi, fid);
			if (subdatas != null && !subdatas.isEmpty()) {
				datas.addAll(subdatas);
			}

		}
		return datas;
	}

	public void parseTempData(String data, String root, TableOrViewInfo tovi) {
		String []res=data.split("\\|");
		String []ct=res[0].split(",");
		
		String[] columntype=new String[ct.length-1];
		StringBuilder columnString=new StringBuilder();
		for(int i=1;i<ct.length;i++)
		{
			try{
			columntype[i-1]=ct[i];
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				columntype[i-1]="varchar";
			}
			columnString.append(TableOrViewInfo.COLUMN_NAME_FIRST+(i-1)+",");
		}
		tovi.setColumnString(columnString.toString());
		tovi.setColumnType(columntype);
		List<String[]> datas = new ArrayList<String[]>();
		for (int i = 1; i < res.length; i++) {
			String[] rowdatas=res[i].split(",");
			String first=rowdatas[0];
			for(int j=0;j<rowdatas.length-1;j++)
			{
				rowdatas[j]=rowdatas[j+1];
			}
			rowdatas[rowdatas.length-1]=first;
			datas.add(rowdatas);
		}
		tovi.addDatas(datas);
	}

	

}
