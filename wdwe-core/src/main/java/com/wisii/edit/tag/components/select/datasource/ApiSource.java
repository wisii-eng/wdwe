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


import com.wisii.component.startUp.SystemUtil;

/**
 * 该类实现了DispSource用于处理数据来源为用户自定义来，此时回传的字符串形式为|和，分割的几种情况，参见
 * WisSelectDataInterface接口
 * @author liuxiao
 *
 */
public class ApiSource implements DispSource {

	/* 回传的参数 */
	String content;

	/**
	 * 构造
	 * 
	 * @param content
	 */
	public ApiSource(String content) {
		this.content = content;
	}
	
	/**
	 * 真正处理回传字符串的地方
	 * @param tovi  建表信息
	 */
	public void parseContent(TableOrViewInfo tovi,String struts, String root) {

		content=content.substring(SystemUtil.SES_wisselectdatainterface.length());
		/*调用解析类真正解析*/
		 ParseDataBuilder.parseFactory(struts).parseTempData(content, root, tovi);
		 
	}
	
}
