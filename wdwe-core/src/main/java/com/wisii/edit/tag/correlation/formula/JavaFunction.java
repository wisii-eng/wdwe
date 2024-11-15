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
 */package com.wisii.edit.tag.correlation.formula;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsComponent;

public class JavaFunction implements Function {

	public String compute(String formulaName, List<CorrelationPara> paras,WdemsComponent wc) {
		//读取控件必要元素
		//id的xpath
		String idxpath=wc.getWdemsTagID().getTagXPath();
		//控件的action执行结果仅限commonButton
		Object result=wc.getWdemsTagComponent().getActionResult();
		//控件ID所对应域的值
		Object value=wc.getWdemsTagComponent().getValue();
		Map WdemsComponentmap=new HashMap();
		WdemsComponentmap.put("idxpath", idxpath);
		WdemsComponentmap.put("result", result);
		WdemsComponentmap.put("value", value);
		
//		System.out.println("formulaName = "+ formulaName);
		// 这个里面实现调用验证逻辑
		Class cls = null;
		try {
			cls = Class.forName("expand.connExpand");
		} catch (ClassNotFoundException e) {

			StatusbarMessageHelper.output("expand.connExpand找不到", e
					.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);

			return "";
		}
		try {
		// 开始解析方法名
		int s=formulaName.indexOf('(');
		String bc=formulaName.substring(0, s);
		Method med;
		Class[] types =null;
		Object[] args=null;
		if (paras != null && paras.size() > 0) {
			types = new Class[paras.size()+1];
			args=new Object[paras.size()+1];

			for (int i = 0; i < paras.size(); i++) {

				// liuxiao 写死为String型的
				
					types[i+1] = Class.forName("java.util.List");
				
				args[i+1]=paras.get(i).getValue();

			}
		
			types[0] = Class.forName("java.util.Map");
			
			args[0]=WdemsComponentmap;
		}
		
	
		
			med = cls.getMethod(bc, types);
		
		

//		System.out.println(med.getName()+"s-------------------------"+med.getParameterTypes().toString()+"---------"+args);
		String dd = (String) med.invoke(cls.newInstance(), args);
		return dd;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
