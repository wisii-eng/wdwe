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
 */package com.wisii.edit.tag.schema;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.w3c.dom.Element;

import com.wisii.edit.tag.schema.wdems.Data;
import com.wisii.edit.tag.schema.wdems.DataSource;
import com.wisii.edit.tag.schema.wdems.Wdems;

public class TestTag {
	
	private Map<String, Object> tagMap;
	
	/**
	 * 根据名字取得当前读取到标签的对象
	 * @param name 标签的名字
	 * @return 标签的对象
	 */
	public Object getWdemsTag(final String name){
		if (tagMap == null) {
			initialTagMap();
		}
		
		return tagMap.get(name);
	}
	
	
	private void initialTagMap(){

		tagMap = new HashMap<String, Object>();
		
//		String path = "C:/Users/Karl/Desktop/WXML/g51/fullMark.xml";
		String path = "jsp\\wisiibase\\xml\\fullMark.xml";
//		String path1 = "C:/Users/Karl/Desktop/WXML/g51/G51_combine.xsl";
		
		Wdems doc = JAXB.unmarshal(new File(path), Wdems.class);
		
		List<Object> wList = doc.getInputOrSelectOrDate();
//		System.err.println(wList.size());
		
		for (Object object : wList) {
			
			Class<?> c = object.getClass();
			
			try {
				Method m = c.getMethod("getName", /*(Class<?>)*/null);
				tagMap.put((String)m.invoke(object, /*(Class<?>)*/null), object);
//				System.out.println(m.invoke(object, null));
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			System.out.println(object.getClass());
		}
	
	}
	
	
	public static void main(final String[] args) {
		
		TestTag tt = new TestTag();
		
		DataSource ds = (DataSource) tt.getWdemsTag("aa");
		
		List<Object> dList = ds.getTableInfoOrDataOrInclude();
		
		for (Object object : dList) {
			if (object instanceof Data) {
				Data dd = (Data) object;
				List<Element> eList = dd.getAny();
				for (Element e : eList) {
					System.out.println(e.getChildNodes().item(0));
				}
			}
		}
		
		System.out.println();
	}


}
