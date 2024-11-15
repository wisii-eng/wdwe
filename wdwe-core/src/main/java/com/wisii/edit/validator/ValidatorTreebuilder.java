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

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.validator.FieldCheck.CommFieldCheck;

public class ValidatorTreebuilder extends DefaultHandler{
	//节点对象集合
	private Map ValidatorRegister;
	//属性名称
	private static String ATTRIBUTE_NAME = "name";
	private static String ATTRIBUTE_CLASSNAME = "classname";
	private static String ATTRIBUTE_METHOD = "method";
	private static String ATTRIBUTE_METHODPARAMS = "methodparams";
	private static String ATTRIBUTE_MSG = "msg";
	
	public ValidatorTreebuilder(Map validators)
	{
		this.ValidatorRegister = validators;
	}
	/**
	 * <p> 开始解析文档 </p>
	 */
	public void startDocument() throws SAXException{
		StatusbarMessageHelper.output("开始解析验证配置文件","" , 
				StatusbarMessageHelper.LEVEL.DEBUG);
    }
	/**
	 * <p>文档解析结束 </p>
	 */
    public void endDocument() throws SAXException{        
    	StatusbarMessageHelper.output("完成解析验证配置文件","" , 
				StatusbarMessageHelper.LEVEL.DEBUG);
    }
    /**
	 * <p>
	 * 在每个元素的开始标签处所做的处理，把每个标签的属性封装到Validator对象，
	 * 然后把Validator对象保存到map中，key-为name属性，value-Validator对象
	 * </p>
	 */
	public void startElement(String uri, String localName, String name,Attributes attributes) 
		throws SAXException {
		//System.out.println("--------startElement--------");    
        //System.out.println("Element="+name); 
        
        String nodeName = attributes.getValue(ATTRIBUTE_NAME);
        
        //如果属性name不为null或者空字符串，继续解析其它属性，否则结束当前节点解析
        if(CommFieldCheck.isBlankOrNull(nodeName)){
        	
        	//如果map中不存在重复name属性值，继续解析其它属性，否则结束当前节点解析
            if(!ValidatorRegister.containsKey(nodeName)){
            	String className = attributes.getValue(ATTRIBUTE_CLASSNAME);
                String method = attributes.getValue(ATTRIBUTE_METHOD);
                
                //如果className和method属性值不为null或者空字符串，继续解析其它属性，否则结束当前节点解析
                if(CommFieldCheck.isBlankOrNull(className) || CommFieldCheck.isBlankOrNull(method)){
//                	for(int i=0;i<attributes.getLength();i++){
//                        System.out.println(attributes.getQName(i)+"="+attributes.getValue(i));
//                    }
                	String methodparams = attributes.getValue(ATTRIBUTE_METHODPARAMS);
                	if(methodparams == null){
                		methodparams = "";
                    }
                	String[] params = methodparams.split(",");
                	
                    String msg = attributes.getValue(ATTRIBUTE_MSG);
                    if(msg == null){
                    	msg = "";
                    }
                    //节点对象
                    Validator validator = new Validator();
                    validator.setName(nodeName);
                    validator.setClassname(className);
                    validator.setMethod(method);
                    validator.setMethodparams(params);
                    validator.setMsg(msg);
                	ValidatorRegister.put(nodeName, validator);
                }
            }
        }
	}
	/**
	 * <p>元素解析结束 </p>
	 */
	public void endElement(String uri, String localName, String name)
		throws SAXException {
		//System.out.println("--------endElement--------"); 
	}
	/**
	 * <p>字符处理 </p>
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}
	
}
