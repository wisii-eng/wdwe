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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.wisii.component.mainFramework.commun.CommincateFactory;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.util.EngineUtil;

/**该类用于生成用于处理解析数据的类
 * 多个静态工程方法都写在这个类里面
 * 包括 数据来源工厂，数据解析工厂，和数据拼接工厂
 * @author liuxiao
 * 
 */

public class ParseDataBuilder {
	public final static String TABLE1 = "table1";
	public final static String TABLE2 = "table2";
	public final static String TREE = "tree";
	
/**
 * 用于创建实现了ParseData接口的类，该接口用于实际处理xml数据的解析和键表
 * @param struts
 * @return
 */
	public static ParseData parseFactory( String struts)
	{
		if(struts==null) return null;
		if(struts.equalsIgnoreCase(TABLE1))
			return  new ParseTable1();
		if(struts.equalsIgnoreCase(TABLE2))
			return new ParseTable2();
		if(struts.equalsIgnoreCase(TREE))
			return new ParseTree();
		return null;
		
	}
	/**
	 * 用于创建实现了DispSource接口的类
	 * 该方法被DataSource的dispatchSource方法调用
	 * @param translateUrl
	 * @return
	 */
	public static DispSource dispFactory(String translateUrl)
	{if(translateUrl==null||"".equalsIgnoreCase(translateUrl)) return null;
		String dd=send(translateUrl);
//		System.out.println("translateUrl = "+ translateUrl);
		if(dd==null||dd.isEmpty()) return null;
		if(dd.startsWith(SystemUtil.SES_wisselectdatainterface))
			return new ApiSource(dd);
		else return new FileSource(dd);
		
		
	}
	/**
	 * 发送请求到服务端请求数据。
	 * 此方法的传输方法出现问题，需要等整个框架修改完毕之后方能确定
	 * @param translateUrl
	 */
	private static String send(String translateUrl)
	{
	
//		Map map = new HashMap(); // 组成map形式之后发往服务器
//		map.put(SystemUtil.SER_SELECTDATA, translateUrl);

//System.out.println("CommincateFactory.serverUrl = "+CommincateFactory.serverUrl);
		WisiiBean wisiibean=EngineUtil.getEnginepanel().getWisiibean();
		if(translateUrl!=null&&wisiibean!=null){
			String docid=wisiibean.getDocID();
			if(docid!=null&&!docid.isEmpty())
			{
				if(translateUrl.indexOf('?')!=-1){
					translateUrl=translateUrl+"&docid="+docid;
				}
				else{
					translateUrl=translateUrl+"?docid="+docid;
				}
			}
		}
		String url=CommincateFactory.serverUrl==null?"conf/"+translateUrl:CommincateFactory.serverUrl+"/wisiibase/conf/"+translateUrl;
		WdemsDateType in=CommincateFactory.makeSelectDataComm(url).send(SystemUtil.SER_SELECTDATA, translateUrl);
		return (String)in.getReturnDateType();
		//		WdemsDateType input =CommincateFactory.makeComm(CommincateFactory.serverUrl+CommincateFactory.requestUrl).send( SystemUtil.SER_SELECTDATA, translateUrl);
//		Map ss = (Map)input.getReturnDateType();
//		return (String)ss.get("select");
	}
	/**
	 * 通过遍历的方法查找根节点
	 * 
	 * @param e
	 * @param root
	 * @return 返回根节点 
	 */
	public static  Node getRootElement(Node e, String root) {
		if(root==null||root.isEmpty())
			return e;
		if (e.getNodeName().equalsIgnoreCase(root)) {
			return e;
		} else {
			NodeList ss = e.getChildNodes();
			for (int i = 0; i < ss.getLength(); i++) {
				return getRootElement(ss.item(i), root);
			}
		}
		return null;

	}
	/**
	 * 计算该节点的非文本子节点的个数
	 * @param nl 待计算的节点
	 * @return 子节点个数
	 */
	public static  int countChildNodes(Node nl)
	{
		NodeList nnl=nl.getChildNodes();
		int c=0;
		for(int i=0;i<nnl.getLength();i++)
		{
			if(!isTextNode(nnl.item(i)))
				c++;
		}
		return c;
	}
	/**
	 * 得到当前节点组的第一个非文本节点
	 * @param nl 当前节点组
	 * @return 第一个非文本节点
	 */
	public static  Node getFirstUnTextNode(NodeList nl)
	{
		for(int i=0;i<nl.getLength();i++)
		{
			if(!isTextNode(nl.item(i)))
				return nl.item(i);
		}
		return null;
		
	}
	/**
	 * 判断当前节点是否为文本节点
	 * @param n 当前节点
	 * @return true 是文本节点
	 *         false 不是文本节点(是正常节点)
	 */
	public static  boolean isTextNode(Node n)
	{
		if(n.getNodeName().equals("#text"))
			return true;
		return false;
	}
}
