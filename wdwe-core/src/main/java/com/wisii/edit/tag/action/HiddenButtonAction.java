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
 */
package com.wisii.edit.tag.action;

import java.awt.event.ActionEvent;
import org.w3c.dom.Element;

public class HiddenButtonAction extends Actions
{

	@Override
	public Object doAction(ActionEvent e)
	{
		// 需要传给Element，用于初始化Tree
		Element element = null;
		EditXmlNodeDialog dia = new EditXmlNodeDialog(element);
		// 对话框选择确定退出时才有返回值
		if (dia.showDialog() == 1)
		{
			return dia.getResult();
		}
		return null;
	}

	@Override
	public void fireConnection()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean updateXML()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
