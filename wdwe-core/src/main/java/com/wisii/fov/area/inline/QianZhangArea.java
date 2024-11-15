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
 *//**
 * @QianZhangArea.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.fov.area.inline;

import com.wisii.fov.area.Area;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-11-16
 */
public class QianZhangArea extends Area 
{
    private String src = "";
    public QianZhangArea(String s) 
    {
    	src=s;
    }
	/**
	 * @返回  src变量的值
	 */
	public String getSrc()
	{
		return src;
	}
    
}
