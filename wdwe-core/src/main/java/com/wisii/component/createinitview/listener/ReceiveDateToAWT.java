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
import java.util.Set;

import com.wisii.component.mainFramework.ListListener;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.render.awt.EditorRenderer;
import com.wisii.fov.util.ColorUtil;

public class ReceiveDateToAWT extends ListListener
{
	
	public ReceiveDateToAWT(EditorRenderer  r)
	{
		render = r;
	}
	
	public void listener()
	{
		Object obj=listen.get(listen.size() - 1);
//		for (Object obj	 : listen) {
			if(obj instanceof Set){
				ColorUtil.setAllLayers((Set)obj);
				EditorRenderer r0 = ((EditorRenderer)render);
				r0.getUserAgent().setAllLayers((Set)obj);
				
			}
			else{
//		}
		
//		if(currrentpageNum == listen.size()-1) //如果是当前页
//		{
//			//绘制当前页            
//            ((EditorRenderer)render).set_firstPageViewport((PageViewport)(listen.get(currrentpageNum)));            
//            ((EditorRenderer)render).get_statusListener().notifyCurrentPageRendered();
//		}
		try
		{
			
			((EditorRenderer)render).renderPage((PageViewport)(obj));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassCastException e)
		{
			
		}
			}
	}

	
	
}
