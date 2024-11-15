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
 */package com.wisii.edit.tag.action;

import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.data.MaintainData;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.util.EngineUtil;

public class DeleteButtonAction extends Actions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object doAction(ActionEvent e) {
		if(!getMessageListener().askIfItIs("是否要删除这个数据？"))
			return null;
		
		//得到xpath
		String xpath=getXPath();
	
		//重新加载
		if(MaintainData.delete(xpath))
		{

			reload();
		}

		//TODO:该方法未实现对赋值得节点的值得处理和撤销处理，也未实现整体验证
		
		return null;
	}

	@Override
	public void fireConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean updateXML() {
		// TODO Auto-generated method stub
		return false;
	}
	private void reload() {
		Thread th = new Thread() {
			public void run() {//将控件都remove掉
				WdemsTagManager.Instance.clearCurrentPageComponents();
				 //设置从排的数据进行重排
				
				try {
					 EngineUtil.getEnginepanel().getWisiibean().setXml(MaintainData.Xquery("/"));
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
					EngineUtil.getEnginepanel().start();
				 EditStatusControl.reload();
				
			}
		};
		SwingUtilities.invokeLater(th);
	}

}
