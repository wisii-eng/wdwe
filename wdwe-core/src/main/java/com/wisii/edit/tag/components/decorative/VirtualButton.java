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
 */
package com.wisii.edit.tag.components.decorative;

import javax.swing.Action;

/**
 * 
 * @author 钟亚军
 * 
 */
public interface VirtualButton
{

	/**
	 * 
	 * 获得button的类型
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public String getType();

	/**
	 * 
	 * 返回按钮将要执行的动作是前插还是后插，只有按钮类型为添加时，这个才有效
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public String getPon();

	/**
	 * 
	 * 获得将要操作的节点的xpath
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public String getHint();

	/**
	 * 
	 * 获得按钮的动作
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public Action getAction();
}
