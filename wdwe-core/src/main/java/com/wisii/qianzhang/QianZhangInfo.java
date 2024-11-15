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
 * @QianZhangInfo.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.qianzhang;

import java.util.ArrayList;
import java.util.List;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-11-16
 */
public class QianZhangInfo
{
	private List<QianZhangItem> items;

	public void addQianZhangItem(QianZhangItem item)
	{
		if (item != null)
		{
			if (items == null)
			{
				items = new ArrayList<QianZhangItem>();
			}
			items.add(item);
		}
	}

	/**
	 * @返回  items变量的值
	 */
	public List<QianZhangItem> getItems()
	{
		return items;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "QianZhangInfo [items=" + items + "]";
	}
	
}
