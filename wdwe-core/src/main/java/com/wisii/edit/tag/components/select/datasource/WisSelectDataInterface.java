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
 */package com.wisii.edit.tag.components.select.datasource;
/**
 * 该类为服务器端类，为下拉列表数据处理类的统一接口。
 * 用户的实现的数据类名为transletURL的具体处理类
 * @author liuxiao
 *
 */
public interface WisSelectDataInterface {
	/**
	 * 该方法用于通过程序调用接口，
	 * @param translate  为模板中translateUrl这个属性所定义的值
	 * @return 返回的为参数格式如下：
	 *    每一行的值通过符号|进行分割，每一列之间的值通过","进行分割
	 *    第一列为一个标志字符串"wisselectdatainterface",全为小写
	 *    例如：
	 *    wisselectdatainterface|a,b,d,g,r|e,r,t,h,f|f,f,g,e,4
	 *    这个例子中有三行5列的数据
	 *    
	 */
	public String getDataSource(String translate);

}
