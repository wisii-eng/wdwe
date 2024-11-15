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
 * @WisiiElementMapping.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.fov.wisii;

import java.util.HashMap;
import com.wisii.fov.fo.ElementMapping;
import com.wisii.fov.fo.FONode;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-11-16
 */
public class WisiiElementMapping extends ElementMapping
{
	/** The XSL-FO namespace URI */
	public static final String URI = "http://www.wisii.com/wisii";

	/** Basic constructor; inititializes the namespace URI for the fo: namespace */
	public WisiiElementMapping()
	{
		namespaceURI = URI;
	}

	/** Initializes the collection of valid objects for the fo: namespace */
	protected void initialize()
	{
		if (foObjs == null)
		{
			foObjs = new HashMap();
			foObjs.put("qianzhang", new QianZhangMaker());
		}
	}

	static class QianZhangMaker extends ElementMapping.Maker
	{
		public FONode make(FONode parent)
		{
			return new com.wisii.fov.wisii.QianZhang(parent);
		}
	}

}
