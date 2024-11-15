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
 * @QianZhangItem.java
 *                     北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.qianzhang;

/**
 * 类功能描述：
 * 
 * 作者：zhangqiang
 * 创建日期：2011-11-16
 */
public final class QianZhangItem
{
	// x坐标位置
	private int x;
	// y坐标位置
	private int y;
	// 签章图片名
	private String src;
	//页码
	private int pageindex;

	public QianZhangItem(int x, int y, String src,int pageindex)
	{
		super();
		this.x = x;
		this.y = y;
		this.src = src;
		this.pageindex=pageindex;
	}

	/**
	 * @返回 x变量的值
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @返回 y变量的值
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * @返回 src变量的值
	 */
	public String getSrc()
	{
		return src;
	}
    
	/**
	 * @返回  pageindex变量的值
	 */
	public int getPageindex()
	{
		return pageindex;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "QianZhangItem [pageindex=" + pageindex + ", src=" + src
				+ ", x=" + x + ", y=" + y + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + pageindex;
		result = prime * result + ((src == null) ? 0 : src.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QianZhangItem other = (QianZhangItem) obj;
		if (pageindex != other.pageindex)
			return false;
		if (src == null)
		{
			if (other.src != null)
				return false;
		} else if (!src.equals(other.src))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	

}
