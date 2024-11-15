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
 */package com.wisii.component.createinitview.listener;

import java.io.IOException;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.render.Renderer;
import com.wisii.fov.render.print.PrintRenderer;

public class ReceiveDateToPrint extends ListListener
{
	

	
	public ReceiveDateToPrint(PrintRenderer p)
	{
		this.render = p;
	}
	
	public void listener()
	{
		try
		{
			this.render.renderPage((PageViewport)listen.get(listen.size()-1));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (FOVException e)
		{
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
	}


}
