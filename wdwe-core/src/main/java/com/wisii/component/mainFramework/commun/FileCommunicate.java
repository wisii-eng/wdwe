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
 * FileCommunicate.java
 * 北京汇智互联版权所有
 */
package com.wisii.component.mainFramework.commun;

import java.io.FileInputStream;
import com.wisii.component.mainFramework.ListListener;
import com.wisii.io.IOUtil;

/**
 * 类功能说明：
 * 作者：zhangqiang
 * 日期:2016-4-13
 */
public class FileCommunicate implements CommunicateProxy
{
	private String serverUrl;
	public FileCommunicate(String serverUrl)
	{
		this.serverUrl = serverUrl;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wisii.component.mainFramework.commun.CommunicateProxy#send(java.lang
	 * .String, java.lang.Object)
	 */
	@Override
	public WdemsDateType send(String serverType, Object params)
	{
		return send(params);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wisii.component.mainFramework.commun.CommunicateProxy#send(java.lang
	 * .Object)
	 */
	@Override
	public WdemsDateType send(Object params)
	{
		String returnstr = null;
		try
		{
			FileInputStream in = new FileInputStream(serverUrl);
			byte[] data = IOUtil.getDataOfInputStream(in);
			in.close();
			if (data != null && data.length > 0)
			{
				returnstr = new String(data, "utf-8");
			}
		}
		catch (Exception e)
		{
		}
		return new WdemsDateType(returnstr);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wisii.component.mainFramework.commun.CommunicateProxy#reSendData(
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	public void reSendData(Object stream, Object outputStream)
	{
		// TODO Auto-generated method stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wisii.component.mainFramework.commun.CommunicateProxy#setListern(
	 * java.lang.Object)
	 */
	@Override
	public void setListern(Object dd)
	{
		// TODO Auto-generated method stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wisii.component.mainFramework.commun.CommunicateProxy#close(java.
	 * lang.Object)
	 */
	@Override
	public void close(Object outputStream)
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void receiveData(Object stream, ListListener li)
	{
		// TODO Auto-generated method stub
	}
}
