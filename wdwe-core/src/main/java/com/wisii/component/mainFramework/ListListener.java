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

import java.util.ArrayList;
import java.util.List;
import com.wisii.fov.render.Renderer;

public abstract class  ListListener 
{
	protected List listen=new ArrayList();
	protected Renderer render;
	private boolean isEnd=false;

	public List getListern()
	{
		return listen;
	}

	public void setListern(List liste)
	{
		this.listen = liste;
	}

	public void addObject(Object obj)
	{
		this.listen.add(obj);
	}
	
	public  abstract void listener();
	public Renderer getRender()
	{
		return render;
	}

	public void setRender(Renderer render)
	{
		this.render = render;
	}
	public void close()
	{
		isEnd=true;
	}
	public boolean getIsEnd()
	{
		return isEnd;
	}
}
