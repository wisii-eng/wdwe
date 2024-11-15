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

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsComponent;
/**
 * 该方法用于解析公式但是如果参数为一组的函数必须做为单个Java方法才能使用，其他均不能使用多参数
 * @author liuxiao
 *
 */
// FIXME 如果将来有时间可以写一个函数解析程序，目前的程序对一个参数是一组数据的这种函数支持不好，并且不同类型的函数嵌套的时候支持不好

public enum FunctionFactory {
	Instance;
	public String dispatch(String formulaExpression, List<CorrelationPara> paras,WdemsComponent wc) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
//		System.out.println("formulaExpression = "+ formulaExpression);
		if(formulaExpression==null)
			{StatusbarMessageHelper.output("传入的公式为null", null, StatusbarMessageHelper.LEVEL.INFO);
			return null;}
	
		
		int a=formulaExpression.indexOf(ParameterDefine.HEADOFFUNCTION[0]);
		if(a>-1)
		{
			String  b=formulaExpression.substring(a);
			int c=b.indexOf(')');
			String bc=b.substring(0, c+1);
			for(int i=1; i<ParameterDefine.HEADOFFUNCTION.length;i++)
			{String abc="";
				if(bc.startsWith(ParameterDefine.HEADOFFUNCTION[i]))
					 abc= ((Function)Class.forName("com.wisii.edit.tag.correlation.formula."+ParameterDefine.INSOFFUNCTION[i]).newInstance()).compute(bc, paras,wc);
				if (abc==null) abc="";
				formulaExpression=formulaExpression.replace(bc, abc);
				
			}
			if(formulaExpression.indexOf(ParameterDefine.HEADOFFUNCTION[0])>-1)
				dispatch(formulaExpression,paras,wc);
			
		}
		if(formulaExpression==null|"".equalsIgnoreCase(formulaExpression))
			return null;
		return new CommonFunction().compute(formulaExpression, paras,wc);
	
	}
	public static void main (String[] args)
	{
		String ss="wdemsF_j_setDisable(a)wdemsF_j_setDisable(a)wdemsF_j_setDisable(a)wdemsF_j_setDisable(a)wdemsF_j_setDisable(a)wdemsF_j_setDisable(a)";
		String formulaName="wdemsF_j_setDisable(a)";
		
			ss=ss.replace(formulaName, null);
		System.out.println(ss);
		
	}
}
