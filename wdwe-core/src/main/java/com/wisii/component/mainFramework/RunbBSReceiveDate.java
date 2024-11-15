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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.fov.command.plugin.FOMethod;

public class RunbBSReceiveDate extends Thread
{
	public Object stream;
	public ListListener listener;

	public RunbBSReceiveDate(Object stream,ListListener listern)
	{
		this.listener=listern;
		this.stream=stream;
	}
	public void run()
	{
		
		InputStream in = (InputStream)((WdemsDateType) stream).getInReturnDateType();
	
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;

		try
		{
			int i=0;
			while ((line = reader.readLine()) != null)
			{
				
				if (line.trim().length() > 0)
				{
					Object data = FOMethod.getObjectByStream(line);
					
					listener.addObject(data);
					listener.listener();
					
				}
				i++;
			}

		}
		catch (IOException ex1)
		{
			ex1.printStackTrace();
		}
		finally
		{

			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (Exception f)
				{
					f.printStackTrace();

				}
			}
			
			 try
	         {
				 listener.close();
				 listener.getRender().stopRenderer();
	         } 
	         catch (Exception e)
	         {
	       	  // TODO 自动生成 catch 块
	       	 // e.printStackTrace();
	         }

		}
		

		
	}
	
	

}
