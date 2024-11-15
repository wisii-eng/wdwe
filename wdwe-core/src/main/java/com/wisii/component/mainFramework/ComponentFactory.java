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
 */package com.wisii.component.mainFramework;

public class ComponentFactory
{

	/**
	 * 类的实例
	 * 
	 * 
	 */
	public WisiiComponent wico;

	/**
	 * 工厂方法
	 * @param type 
	 */
	public WisiiComponent createWisiiComponent(String type)
	{
		String classname = "com.wisii.component.create" + type.toLowerCase()
				+ ".Create" + type;
		Class c;
		try
		{
			c = Class.forName(classname);

			return (WisiiComponent) c.newInstance();

		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
