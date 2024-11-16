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
 * 
 */
package com.wisii.component.mainFramework;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;

/**
 * @author liuxiao 远程调用装饰类
 */

public class RemoteInvoke implements InvokeProxy, Remote
{

	// 主机IP
	String rmiip;

	// 实例在主机上注册的名字
	String rminame;

	// 主机rmi的段口号 默认为 1099
	String rmiport;

	public Object execute(Object para)
	{
		try
		{
			if (System.getSecurityManager() == null)
			{
				System.setSecurityManager(new RMISecurityManager());
			}

			// 如果要从另一台启动了RMI注册服务的机器上查找WisiiComponentInterface实例
			WisiiComponentInterface wisiiComp = (WisiiComponentInterface) Naming
					.lookup("rmi://" + rmiip + ":" + rmiport + "/" + rminame);

			// 返回远程实例
			return wisiiComp;

		}
		catch (Exception e)
		{
			System.out.println("WisiiComponentInterfaceRmi exception: " + e);
		}
		return null;
	}

	public void init(){}
	public RemoteInvoke(String ip, String port, String name)
	{
		this.rmiip = ip;
		this.rminame = name;
		this.rmiport = port;
	}
}
