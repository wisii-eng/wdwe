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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.component.validate.validatexml.XmlValidate;
import com.wisii.edit.SaxUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
public class ValidatorFactory {

/**
 * 该类用于验证框架的开始
 * 
 */
	/*验证框架map*/
	public static Map<String, BaseValidator> validatorMap;
	/*schema的map*/
	public static Map  schemaMap ;
	/**
	 * 工厂方法用与初始化验证器的工厂，实际为初始化
	 */
	public static void  initValidateFrame(WisiiBean wb)
 {

		if (validatorMap == null || validatorMap.size() < 1)
		{

			validatorMap = new HashMap();

			// validConf=;

			// 调用ValidatorTreebuilder解析器解析验证文件并且将文件中的内容存在validatorMap。
			try
			{
				SaxUtil.getSAXParser().parse(
						SystemUtil.class.getClassLoader().getResourceAsStream(
								"resource/" + "validator.xml"),
						new ValidatorTreebuilder(validatorMap));
			} catch (SAXException e)
			{
				StatusbarMessageHelper.output("解析错误", e.getMessage(),
						StatusbarMessageHelper.LEVEL.INFO);
				e.printStackTrace();
			} catch (IOException e)
			{
				StatusbarMessageHelper.output("无法读取文件", e.getMessage(),
						StatusbarMessageHelper.LEVEL.INFO);
				e.printStackTrace();
			} catch (ParserConfigurationException e)
			{
				StatusbarMessageHelper.output("解析异常", e.getMessage(),
						StatusbarMessageHelper.LEVEL.INFO);
				e.printStackTrace();

			}

		}
		String xsd = wb.getXsdString();
		if (xsd != null && !xsd.isEmpty())
		{
			schemaMap = XmlValidate.getAllDefinedElementsAndAttribute(xsd);
			return;
		}
	}
	
	//外部得到验证器的接口
	public static BaseValidator createValidator(String validName) {
		if(validName==null) return null;
		//是schema验证器，则返回schema验证
		if("schema".equals(validName.toLowerCase()))
		{
			return new SchemaValidator();
		}
		else
		
		//选中验证器逻辑	
		
		return validatorMap.get(validName);
	}
}
