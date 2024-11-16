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
 * @AuthorityParse.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.authority;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wisii.edit.message.StatusbarMessageHelper;

/**
 * 类功能描述：解析权限配置
 *
 * 作者：p.x
 * 创建日期：2009-6-15
 */
public class AuthorityParse {
	//属性名称
	private static String ATTRIBUTE_NAME = "name";
	private static String ATTRIBUTE_AUTHORITY = "authority";
	private static String ATTRIBUTE_ISDEFAULT = "isdefault";
	private static String ATTRIBUTE_ISCHOOSE = "ischoose";
	private static String ATTRIBUTE_TEMPLATEID = "templateid";
	//权限
	private static String TEMPLATE_ALL = "template-all";
	private static String COMPONENT_ALL = "component-all";
	
	private static String ELEMENT_GLOBLE_AUTHORITIES = "globle-authority";
	private static String ELEMENT_TEMPLATE_AUTHORITIES = "template-authorities";
	//标签名称
	private static String ELEMENT_TEMPLATES = "templates";
	private static String ELEMENT_TEMPLATE = "template";
	private static String ELEMENT_COMPONENTS = "components";
	private static String ELEMENT_COMPONENT = "component";
	private static String ELEMENT_ADDAUTHORITY = "add-authority";
	//布尔值
	private static String BOOL_TRUE = "true";
	private static String BOOL_FALSE = "false";
	
	private static String DEFAULT = "default";
	private static String SEPARATOR = ",";
	
	public AuthorityParse(){	}
	
	/**
	 * <p>解析权限配置信息，权限分别保存到map集合中</p>
	 *
	 */
	public  static void parse(InputStream input,Map globleAuthorityMap,Map templateAuthorityMap){
		Document doc = DocumentHelper.createDocument();	
		
//		File file = new File(path);
		//如果文件存在，读取文件流返回xml文档对象
		if(input!=null){
			SAXReader reader = new SAXReader();	
			try {
				doc = reader.read(input);
				Element root = doc.getRootElement();
				
				//解析公共权限
				for(Iterator globleIt = root.elementIterator(ELEMENT_GLOBLE_AUTHORITIES); 
						globleIt.hasNext();){
					Authority globleAuthority = new Authority();
					Element globleElem = (Element)globleIt.next();
					String attrName = globleElem.attributeValue(ATTRIBUTE_NAME);
					String attrAuthority = globleElem.attributeValue(ATTRIBUTE_AUTHORITY);
					String attrIsDefault = globleElem.attributeValue(ATTRIBUTE_ISDEFAULT);
					String attrIsChoose = globleElem.attributeValue(ATTRIBUTE_ISCHOOSE);
					//判断
					if(DEFAULT.equals(attrName)){
						attrIsDefault = BOOL_TRUE;
					}
					if(attrIsChoose == null){
						attrIsChoose = BOOL_TRUE;
					}else{
						attrIsChoose = BOOL_FALSE;
					}
					if(attrIsDefault == null){
						attrIsDefault = BOOL_FALSE;
					}
					globleAuthority.setName(attrName);
					globleAuthority.setAuthority(attrAuthority);
					globleAuthority.setIsdefault(attrIsDefault);
					globleAuthority.setIschoose(attrIsChoose);
					
					StringBuffer templates = new StringBuffer();
					StringBuffer components = new StringBuffer();
					
//					System.out.println("attrName="+attrName);
//					System.out.println("attrAuthority="+attrAuthority);
//					System.out.println("attrIsDefault="+attrIsDefault);
//					System.out.println("attrIsChoose="+attrIsChoose);
					if(TEMPLATE_ALL.equals(attrAuthority)){
						Element templatesElem = globleElem.element(ELEMENT_TEMPLATES);
//						System.out.println("templates="+templatesElem.getPath());
						//遍历state节点下的operate节点得到文本值
						for(Iterator templateIt = templatesElem.elementIterator(ELEMENT_TEMPLATE); 
							templateIt.hasNext();)
						{
							Element templateElem = (Element)templateIt.next();
							
							templates.append(templateElem.getText());
							if(templateIt.hasNext()){
								templates.append(SEPARATOR);
							}
//							System.out.println("templateElem="+templateElem.getText());
						}
					}else if(COMPONENT_ALL.equals(attrAuthority)){
						Element componentsElem = globleElem.element(ELEMENT_COMPONENTS);
//						System.out.println("componentsElem="+componentsElem.getPath());
						//遍历component节点下的component节点得到文本值
						for(Iterator componentIt = componentsElem.elementIterator(ELEMENT_COMPONENT); 
							componentIt.hasNext();)
						{
							Element componentElem = (Element)componentIt.next();
							
							components.append(componentElem.getText());
							if(componentIt.hasNext()){
								components.append(SEPARATOR);
							}
//							System.out.println("component="+componentElem.getText());
						}
					}
					globleAuthority.setTemplates(templates.toString());
					globleAuthority.setComponents(components.toString());
//					System.out.println("templates="+templates);
//					System.out.println("components="+components);
					
					globleAuthorityMap.put(globleAuthority.getName(), globleAuthority);
				}
				
				//解析模板权限
				for(Iterator templateIt = root.elementIterator(ELEMENT_TEMPLATE_AUTHORITIES); 
					templateIt.hasNext();){
					Element templateElem = (Element)templateIt.next();
					String templateid = templateElem.attributeValue(ATTRIBUTE_TEMPLATEID);
//					System.out.println("templateid="+templateid);
					
					Map authorityMap = new HashMap();
					for(Iterator authorityIt = templateElem.elementIterator(ATTRIBUTE_AUTHORITY); 
						authorityIt.hasNext();)
					{
						Authority templateAuthority = new Authority();
						Element authorityElem = (Element)authorityIt.next();
						String attrName = authorityElem.attributeValue(ATTRIBUTE_NAME);
						String attrIsDefault = authorityElem.attributeValue(ATTRIBUTE_ISDEFAULT);
						String attrIsChoose = authorityElem.attributeValue(ATTRIBUTE_ISCHOOSE);
						//判断
						if(DEFAULT.equals(attrName)){
							attrIsDefault = BOOL_TRUE;
						}
						if(attrIsChoose == null){
							attrIsChoose = BOOL_TRUE;
						}else{
							attrIsChoose = BOOL_FALSE;
						}
						if(attrIsDefault == null){
							attrIsDefault = BOOL_FALSE;
						}
						templateAuthority.setName(attrName);
						templateAuthority.setAuthority("");
						templateAuthority.setIsdefault(attrIsDefault);
						templateAuthority.setIschoose(attrIsChoose);
						
						//System.out.println("attrName="+attrName);
//						System.out.println("attrIsDefault="+attrIsDefault);
//						System.out.println("attrIsChoose="+attrIsChoose);
						
						StringBuffer components = new StringBuffer();
						//遍历authority节点下的component和add-authority节点得到文本值
						for(Iterator componentIt = authorityElem.elementIterator(); 
							componentIt.hasNext();)
						{
							Element componentElem = (Element)componentIt.next();
							if(ELEMENT_ADDAUTHORITY.equals(componentElem.getName())){
								String addName = componentElem.attributeValue(ATTRIBUTE_NAME);
								
								if(authorityMap.containsKey(addName)){
									//如果map中已存在
									Authority tempAuthority = (Authority)authorityMap.get(addName);									
									components.append(tempAuthority.getComponents());
									if(componentIt.hasNext()){
										components.append(SEPARATOR);
									}
								}else{
									//递归查找
									String s = recursion(templateElem,addName);
									components.append(s);
									if(componentIt.hasNext()){
										components.append(SEPARATOR);
									}
								}
							}else{
								components.append(componentElem.getText());
								if(componentIt.hasNext()){
									components.append(SEPARATOR);
								} 
							}							
							//System.out.println("curr="+componentElem.getName());
						}
						//System.out.println("components="+components);
						templateAuthority.setComponents(components.toString());
						
						authorityMap.put(templateAuthority.getName(), templateAuthority);
					}
					templateAuthorityMap.put(templateid, authorityMap);
				}
			} catch (DocumentException e) {
				StatusbarMessageHelper.output("解析模板权限异常",
						e.getMessage() ,	StatusbarMessageHelper.LEVEL.INFO);
			} 
		}
	}
	/**
	 * <p>迭代解析引用节点</p>
	 *
	 * @param node 根节点
	 * @param name 属性
	 * @return 控件字符串
	 */
	public static String recursion(Element node,String name){
		Element elem = (Element)node.selectSingleNode("authority[@name="+name+"]");
		StringBuffer components = new StringBuffer();
		for(Iterator it = elem.elementIterator(); it.hasNext();)	{
			Element tempElem = (Element)it.next();
			if(ELEMENT_ADDAUTHORITY.equals(tempElem.getName())){
				String addName = tempElem.attributeValue(ATTRIBUTE_NAME);
				String s = recursion(node,addName);
				components.append(s);
				if(it.hasNext()){
					components.append(SEPARATOR);
				}
			}else{
				components.append(tempElem.getText());
				if(it.hasNext()){
					components.append(SEPARATOR);
				}
			}
		}
		return components.toString();		
	}
	
	public static void main(String[] args) {
		Map g = new HashMap();
		Map t = new HashMap();
		//AuthorityParse parse = new AuthorityParse(g,t);
		String xmlPath = AuthorityParse.class.getResource("/authority.xml").getPath();
//		AuthorityParse.parse(xmlPath,g,t);
		Set set = g.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			Authority a = (Authority)g.get(key);
//			System.out.println("name="+a.getName());
//			System.out.println("authority="+a.getAuthority());
//			System.out.println("Templates="+a.getTemplates());
//			System.out.println("Components="+a.getComponents());
		}
		set = t.keySet();
		it = set.iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			Map m = (Map)t.get(key);
//			System.out.println("key="+key);
			
			Set tset = m.keySet();
			Iterator tit = tset.iterator();
			while(tit.hasNext()){
				String tkey = (String)tit.next();
				Authority a = (Authority)m.get(tkey);
//				System.out.println("name="+a.getName());
//				System.out.println("Templates="+a.getTemplates());
//				System.out.println("Components="+a.getComponents());
			}
		}
	}
}
