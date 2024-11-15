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

import java.util.List;
/**
 * 该类为公式接口对外的bean类
 * @author liuxiao
 *
 */
public class CorrelationPara {
	/*参数名*/
	private String name;
	/*参数的值s*/
	private List <String> value;
	/*参数的类型*/
	private ParameterDefine.TYPE type=ParameterDefine.TYPE.NUMBER;
	/*参数对应的xpath*/
	private String xpath;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the type
	 */
	public ParameterDefine.TYPE getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ParameterDefine.TYPE type) {
		this.type = type;
	}
	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}
	/**
	 * @param xpath the xpath to set
	 */
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(List<String> value) {
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public List<String> getValue() {
		return value;
	}

}
