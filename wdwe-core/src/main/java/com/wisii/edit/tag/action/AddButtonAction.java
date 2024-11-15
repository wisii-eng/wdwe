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
import com.wisii.edit.tag.schema.wdems.Button;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;

/**该类为添加按纽的事件
 * @author IBM
 *
 */
public class AddButtonAction extends Actions {

	 
	@Override
	/**
	 * @author liuxiao
	 */
	public Object doAction(ActionEvent e) {
		
		Button aa= (Button)getTagObject();
		//得到xpath
		String xpath=getXPath();
		//显示太阳花
		EnginePanel enginepanel=EngineUtil.getEnginepanel();
		//判断为前插还是后插
		
		String insert=aa.getInsert();
		String nodata = aa.getNodataxpath();
		boolean  insertDone=false;
		if(ButtonActionFactory.INSERT_BEFORE.equalsIgnoreCase(insert))
		{
			insertDone= MaintainData.addBefore( xpath,nodata);
		}
		if(ButtonActionFactory.INSERT_AFTER.equalsIgnoreCase(insert))
		{
			insertDone=  MaintainData.addAfter(xpath,nodata);
		}

		//关闭遮罩 
		//重新加载
		if(insertDone)
		{
			reload();
			
		}
		
		//TODO:该方法未实现对赋值得节点的值得处理和撤销处理，也未实现整体验证
		return null;
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
	@Override
	public void fireConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean updateXML() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
