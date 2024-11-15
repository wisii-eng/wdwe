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

import com.wisii.edit.tag.schema.wdems.Date;
import com.wisii.edit.tag.schema.wdems.Input;
import com.wisii.edit.tag.schema.wdems.Select;
import com.wisii.edit.tag.schema.wdems.Wdems;

public class TestReadWdemsTags {

	public static void main(final String[] args) {
		

//		String path = "C:/Users/Karl/Desktop/WXML/g51/fullMark.xml";
		String path = "jsp\\wisiibase\\xml\\fullMark.xml";
//		String path1 = "C:/Users/Karl/Desktop/WXML/g51/G51_combine.xsl";
		
		Wdems doc = JAXB.unmarshal(new File(path), Wdems.class);

//		System.out.println(doc);
		
		List<Object> wList = doc.getInputOrSelectOrDate();
		System.err.println(wList.size());
		
		Map<String, Object> tagMap = new HashMap<String, Object>();
		
		for (Object object : wList) {
			
			Method m;
			
			Class<?> c = object.getClass();
			
			try {
				m = c.getMethod("getName", null);
				tagMap.put((String)m.invoke(object, null), object);
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
		
		Map<Class<?>, String> type = new HashMap<Class<?>, String>();
		
		type.put(Input.class, "input");
		type.put(Select.class, "select");
		type.put(Date.class, "date");
		
		System.out.println(type.get(tagMap.get("unit").getClass()));
		
//		System.out.println(tagMap.get("unit").getClass());
		
		
	}

}
