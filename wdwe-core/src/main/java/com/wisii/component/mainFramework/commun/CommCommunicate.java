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
 */package com.wisii.component.mainFramework.commun;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.mainFramework.RunCSReceiveDate;

/**
 * 
 */

/**
 * @author liuxiao
 * 
 */

public class CommCommunicate implements CommunicateProxy
{

	// ListListener listener;
	List buffer = new ArrayList();
	
	public WdemsDateType send(String serverType, Object params)
	{

		
		VisualServlet client = new VisualServlet(serverType,params);
		client.start();
		while (client.tlinkList==null)
		{
		try
		{
			Thread.sleep(10);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		}

		return new WdemsDateType(client.tlinkList);

	}

	public void reSendData(Object object, Object outputStream)
	{
		((LinkedList) outputStream).offer(object);
	}


	public void receiveData(Object stream,ListListener listern)
	{
		
		RunCSReceiveDate client = new RunCSReceiveDate((WdemsDateType)stream,listern);
		client.start();

	}

	public void close(Object outputStream)
	{
		LinkedList list = (LinkedList) outputStream;
		list.add(new Integer(-1));
	}

	public void setListern(Object dd){}
	public WdemsDateType send(Object params){
			return null;}
}
