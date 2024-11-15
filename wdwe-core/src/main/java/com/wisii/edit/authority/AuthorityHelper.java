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
 * @AuthorityHelper.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.authority;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.cache.database.hsql.bo.AuthorityBO;

/**
 * 类功能描述：
 * 
 * 作者：p.x 创建日期：2009-6-15
 */
public class AuthorityHelper {
	public static final  String defaultID = "default";
	private static AuthorityBO authority;
	/**
	 * 类功能描述：该类用于实现触发查询权限的功能。得到的权限信息将存放到数据库中。
	 *  
	 *  这个方法重要启动新线程来触发权限的读取工作。
	 *  
	 * 作者：
	 * 创建日期：2009-6-15
	 */
	public static void triggerCheckAuthor(String authorityid,String edittempid) 
	{
		Map globleAuthorityMap = new HashMap();
		Map templateAuthorityMap = new HashMap();
		String ss=null;
		loadResoure(globleAuthorityMap, templateAuthorityMap);
		if(defaultID.equals(authorityid)){
			edittempid = defaultID;
			ss=getAuthority(defaultID, defaultID,globleAuthorityMap,templateAuthorityMap);
		}
		else
		{
			ss = getAuthority(authorityid, edittempid,globleAuthorityMap, templateAuthorityMap);
		}
		authority=new AuthorityBO();
		if(ss != null){
			String[] authoritystr = ss.split("\\|");
			if(authoritystr.length==4){
				authority.setEdittempid(authoritystr[0].trim());
				authority.setAuthorityid(authoritystr[1].trim());
				authority.setAuthority(authoritystr[2].trim());
				authority.setComponents(authoritystr[3].trim());
			}
		}
	}
	private static void  loadResoure(Map globleAuthorityMap,Map templateAuthorityMap)
	{
		AuthorityParse.parse(SystemUtil.class.getClassLoader().getResourceAsStream("resource/authority.xml"),	globleAuthorityMap,templateAuthorityMap);
	}
	private static String getAuthority(String authorityid,String edittempid,Map globleAuthorityMap,Map templateAuthorityMap)
	{
			StringBuilder sb=new StringBuilder();
			if(authorityid!=null&&!authorityid.isEmpty())
			{
				if("default".equals(authorityid)){
					return edittempid+" | "+authorityid+" | all | ";
				}
				String[] authoityids=authorityid.split(",");
				for(String aid:authoityids)
				{
					if(globleAuthorityMap.containsKey(aid))
					{
						Authority authority = (Authority)globleAuthorityMap.get(aid);
	                    sb.append(authority.getComponents()+",");				
					}
				}
			}
			if(sb.length()==0){
				return edittempid+" | "+authorityid+" | | ";
			}
			else{
			return edittempid+" | "+authorityid+" | true | "+sb.substring(0, sb.length()-1);
			}
			/*String validate = null;
			boolean isgloble = false;
			//如果模板存在
			if(templateAuthorityMap.containsKey(edittempid)){
				//取出模板map
				Map authorityMap = (Map)templateAuthorityMap.get(edittempid);
				//如果权限存在
				if(authorityMap.containsKey(authorityid)){
					//取出权限,读取此权限
					Authority authority = (Authority)authorityMap.get(authorityid);
					validate = edittempid+" | "+authorityid+" | "+authority.getIschoose()+
						" | "+authority.getComponents();
				}else{
					//在全局列表中查找
					isgloble = true;
				}				
			}else{
				//不存在，在全局列表中查找
				isgloble = true;
			}
			
			//在全局列表中查找
			if(isgloble){
				//如果在全局列表中
				if(globleAuthorityMap.containsKey(authorityid)){
					//取出权限,读取此权限
					Authority authority = (Authority)globleAuthorityMap.get(authorityid);
					if("all".equals(authority.getAuthority())){
						//如果用户拥有当前模板的全部权限，all
						validate = edittempid+" | "+authorityid+" | all | ";
					}else if("template-all".equals(authority.getAuthority())){
						//如果用户拥有当前模板的全部权限，template-all
						String templates = authority.getTemplates();
						String[] template =  templates.split(",");
						boolean isexist = false;
						for(int i=0;i<template.length;i++){
							if(edittempid.equals(template[i])){
								isexist = true;
							}
						}
						if(isexist){
							validate = edittempid+" | "+authorityid+" | all | ";
						}else{
							validate = edittempid+" | "+authorityid+" | | ";
						}						
					}else if("component-all".equals(authority.getAuthority())){
						//
						validate = edittempid+" | "+authorityid+" | "+authority.getIschoose()+
						" | "+authority.getComponents();
					}
					
				}else{
					//查找默认配置
					String key = null;
					boolean isdefault = false;
					
					Set set = globleAuthorityMap.keySet();
					Iterator it = set.iterator();
					while(it.hasNext()){
						key = (String)it.next();
						Authority authority = (Authority)globleAuthorityMap.get(key);
						if("default".equals(authority.getName()) || "true".equals(authority.getIsdefault())){
							isdefault = true;
							break;
						}						
					}
					//如果有默认配置
					if(isdefault){
						//采用默认配置
						Authority authority = (Authority)globleAuthorityMap.get(key);
						validate = edittempid+" | "+authorityid+" | "+authority.getIschoose()+
						" | "+authority.getComponents();
					}else{
						//默认为全部不可编辑
						validate = edittempid+" | "+authorityid+" | | ";
					}
				}
			}*/			
	}
	public static AuthorityBO getAuthority() {
		return authority;
	}
	public static void stop()
	{
		authority=null;
	}
}
