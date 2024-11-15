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
 */package com.wisii.edit.tag;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.tag.schema.wdems.Wdems;
import com.wisii.edit.tag.util.WdemsTagUtil;

/**
 * 该类是用于读取模板中的或者传输过来的Wdems标签的的，主要是用于把标签映射成实际的Java对象
 * 
 * @author 闫舒寰
 * @version 1.0 2009/06/10
 * 
 */
public enum WdemsTagSource {

	Instance;

	private Map<String, Object> tagMap;

	/**
	 * 根据名字取得当前读取到标签的对象
	 * 
	 * @param name
	 *            标签的名字
	 * @return 标签的对象
	 */
	public Object getWdemsTag(final String name) {
		return tagMap.get(name);
	}
	
	/**
	 * 返回整个标签的map
	 * @return
	 */
	public Map<String, Object> getWdemsTagMap(){
		return new HashMap<String, Object>(tagMap);
	}
	
	//该String作为xml的标准头文行，不能做任何更改。
	private static final String xmlh = new String("<?xml version=\"1.0\" encoding=\""
		+ SystemUtil.FILE_CHARSET + "\"?>");

	/**
	 * 用于读取编辑标签，初始化
	 * @param s
	 */
	public void initialTagMap(final String s) {
		
		if (s == null || s.equals("")) {
			throw new IllegalArgumentException("no edit tag information");
		}

		StringBuilder inputString;
		
		//处理xml文件头，有可能xml文件在xslt中读取出来，没有xml标准头，这种情况就需要补上
		if (!s.contains("<?xml")) {
			inputString = new StringBuilder();
			inputString.append(xmlh);
			inputString.append(s);
		} else {
			inputString = new StringBuilder(s);
		}
		
		//读取xml文件产生标签对象
		Wdems doc = JAXB.unmarshal(WdemsTagUtil.StringToInputStream(inputString.toString()), Wdems.class);

		//把标签对象按照名字放到一个map中
		tagMap = new HashMap<String, Object>();
		List<Object> wList = doc.getInputOrSelectOrDate();
		
		for (Object object : wList) {
			Class<?> c = object.getClass();
			try {
				Method m = c.getMethod("getName", /* (Class<?>) */null);
				tagMap.put((String) m.invoke(object, /* (Class<?>) */null), object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
