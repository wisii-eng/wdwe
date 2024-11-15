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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.wisii.edit.message.StatusbarMessageHelper;

public class Validator  implements BaseValidator{
	/*验证器名称*/
	private String name;
	/*类名*/
	private String classname;
	/*方法名*/
	private String method;
	/*参数类名*/
	private String[] methodparams;
	/*报错信息名称*/
	private String msg;
	private String error;
	
	public Validator()
	{
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * @param classname
	 *            the classname to set
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * 
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @param methodparams
	 *            the methodparams to set
	 */
	public void setMethodparams(String[] methodparams) {
		this.methodparams = methodparams;
	}
	
	/**
	 * 那个参数如何传进来
	 * 
	 * @param  注：当前与的值默认为集合的第一个参数。 
	 */
	public boolean validate(List args)
	{
//		这个里面实现调用验证逻辑
		Class cls=null;
		try {
			cls = Class.forName(this.classname);
		} catch (ClassNotFoundException e) {
			
			StatusbarMessageHelper.output("验证器"+this.classname+"找不到", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
			error="验证器"+this.classname+"找不到";
			return false;
		}
		
		 Class[] types = new Class[methodparams.length];
		 for(int i=0;i<methodparams.length;i++)
		 {
			 try {
			 types[i]=Class.forName(methodparams[i]);
			 
		 } catch (ClassNotFoundException e) {
				
				StatusbarMessageHelper.output("验证器"+this.classname+"找不到参数"+methodparams[i]+"类", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
				error="验证器"+this.classname+"找不到参数"+methodparams[i]+"类";
				return false;
		 }
		 }
		   
		 Method med;
		try {
			med = cls.getMethod(this.method, types);
			
			Boolean dd=(Boolean) med.invoke(cls.newInstance(), args.toArray());
			return dd;
		} catch (SecurityException e) {
			
			StatusbarMessageHelper.output("调用方法"+this.method+"出现错误", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
			
		} catch (NoSuchMethodException e) {
			StatusbarMessageHelper.output("找不到方法"+this.method+"出现错误", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);

		} catch (IllegalArgumentException e) {
		
			StatusbarMessageHelper.output("参数错误"+this.classname+"."+this.method+":"+args, e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);

		} catch (IllegalAccessException e) {
			
			StatusbarMessageHelper.output("接入错误"+this.classname+"."+this.method, e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);

		} catch (InvocationTargetException e) {
			
			StatusbarMessageHelper.output("调用错误"+this.classname+"."+this.method, e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);

		} catch (InstantiationException e) {
			
			StatusbarMessageHelper.output("实例化错误"+this.classname, e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);

		}
		error="验证器错误";
		return false;
	}
	/**
	 * 当验证出错，则需要调用这个方法返回报错信息 从资源文件中得到信息内容
	 * 此时的信息中可能带有[arg]这种匹配字符。
	 * @return 错误信息
	 */
	public String getError() {
		if (error==null)
		error=Resources.getMessage(msg);
		return error;
	
	}


}
