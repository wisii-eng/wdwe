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

import java.util.LinkedList;

import com.wisii.fov.server.command.AbstractServerCommand;
import com.wisii.fov.server.command.CommandHelp;

public class VisualServlet extends Thread {

	String serverType;
	Object params;
	LinkedList tlinkList;

	public VisualServlet(String _serverType,Object params) {
		
		this.serverType=_serverType;
		this.params=params;
		

	}

	public void run() 
	{
		

//		System.out.print("serverType-------------------------"+serverType);
		tlinkList=new LinkedList();
		AbstractServerCommand command;
		try
		{
			command = CommandHelp.getCommand(serverType);
		
			command.communicateProxy=CommincateFactory.makeComm(null);

		if (command.execute(tlinkList, params,null))
		{

			CommincateFactory.makeComm(null).close(tlinkList);
			
		}
		}
		catch (Exception e)
		{
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}


	}

	
}
