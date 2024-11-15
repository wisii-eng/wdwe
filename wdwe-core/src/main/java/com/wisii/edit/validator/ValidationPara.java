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
 */package com.wisii.edit.validator;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.data.MaintainData;
public class ValidationPara {
	/* 该值所对应的xpath */
	String xpath;
	/* 该值 */
	String reg;

	/**
	 * 构造函数，用于创建参数对象
	 * 
	 * @param xpath
	 *            该值所对应的xpath
	 * @param value
	 *            该值
	 */
	public ValidationPara(String xpath, String value) {
		this.xpath = xpath;
		this.reg = value;
		fill();
	}

	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}

	/**
	 * @param xpath
	 *            the xpath to set
	 */
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	/**
	 * @return the reg
	 */
	public String getReg() {
		return reg;
	}

	/**
	 * @param reg
	 *            the reg to set
	 */
	public void setReg(String reg) {
		this.reg = reg;
	}

	/**
	 * 
	 * 根据xpath的值得到xml的元素的值返回程序进行引用。这个方法仅用于传入xpath没有传入值 ，但又不是schema验证的时候
	 */
	private void fill() 
	{
		//该方法根据xpath得到其内部的值。
		if(reg==null&&xpath!=null)
		{
			try {
				String []ss=MaintainData.queryValue(xpath);
				if(ss!=null)						
					reg=ss[0];
			} catch (Exception e) {
				StatusbarMessageHelper.output("未得到"+xpath+"的值:", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
	
			}
		}
	}

}