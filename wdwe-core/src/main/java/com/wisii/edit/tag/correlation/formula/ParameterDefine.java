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

public class ParameterDefine {
	/*参数类型定义*/
	public static enum TYPE{
		/*数字型*/
		NUMBER,
		/*字符串型*/
		STRING,
		/*正则表达式类型*/
		REGEXP,
		/*布尔型*/
		BOOLEAN

	}
	/*function头字符串定义，用于分发*/
	public static final String [] HEADOFFUNCTION= new String[] {
		/*总头，固定位置 下面所有的枚举值名称必须以这个值为开头，没有这个开头的函数名一律被识别为js方法*/
		"wdemsF",
		/*java方法*/	
	"wdemsF_j_"};
		
	/*function头字符串定义，用于分发*/
	public static final String [] INSOFFUNCTION= new String[] {
		/*总头，固定位置 下面所有的枚举值名称必须以这个值为开头，没有这个开头的函数名一律被识别为js方法*/
		"JSFunction",
		/*java方法*/	
	"JavaFunction"};
		
		
		
	
}
