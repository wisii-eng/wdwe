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

import java.util.List;

import com.wisii.component.validate.validatexml.SchemaObj;

public class SchemaValidator implements BaseValidator {

	String  error;
	public String getError() {
		// 验证器返回错误信息
		return error;
	}

	public boolean validate(List paras) {
		// 验证期调用逻辑
		
		error=new SchemaObj().checkXmlData(((ValidationPara)paras.get(0)).getXpath(),
				((ValidationPara)paras.get(0)).getReg());
		if(error==null) return true;
		
		return false;
	}

}
