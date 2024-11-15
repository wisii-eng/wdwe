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
 * @QianZhang.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.fov.wisii;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.flow.AbstractGraphics;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-11-16
 */
public class QianZhang extends AbstractGraphics
{
	private String src = "";
	/**
	 * 初始化过程的描述
	 *
	 * @param       初始化参数说明
	
	 * @exception   {说明在某情况下,将发生什么异常}
	 */
	public QianZhang(FONode parent)
	{
		super(parent);
		// TODO Auto-generated constructor stub
	}
	public void bind(PropertyList pList) throws FOVException {
		super.bind(pList);
		src = pList.get(PR_SRC).getString();
	}
	/* (non-Javadoc)
	 * @see com.wisii.fov.fo.flow.AbstractGraphics#getIntrinsicHeight()
	 */
	@Override
	public int getIntrinsicHeight()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	/* (non-Javadoc)
	 * @see com.wisii.fov.fo.flow.AbstractGraphics#getIntrinsicWidth()
	 */
	@Override
	public int getIntrinsicWidth()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	/* (non-Javadoc)
	 * @see com.wisii.fov.fo.FONode#getLocalName()
	 */
	@Override
	public String getLocalName()
	{
		// TODO Auto-generated method stub
		return "qianzhang";
	}
	   /** @see com.wisii.fov.fo.FONode#getNamespaceURI() */
    public String getNamespaceURI() {
        return WisiiElementMapping.URI;
    }

    /** @see com.wisii.fov.fo.FONode#getNormalNamespacePrefix() */
    public String getNormalNamespacePrefix() {
        return "wisii";
    }
	/**
	 * @返回  src变量的值
	 */
	public String getSrc()
	{
		return src;
	}
    
}
