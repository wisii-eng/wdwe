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
 */package com.wisii.component.createinitview.openView;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import com.wisii.edit.EditStatusControl;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.util.EditUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;

public class SubDataActionDoit extends WholeValidateActionDoit
 {

	public SubDataActionDoit(String name, String iconName) {
		super(name, iconName);

	}

	public void action(ActionEvent e) {

		Thread th = new Thread() {
			public void run() {
				EditStatusControl.RUNSTATUS = EditStatusControl.STATUS.READ;
				EnginePanel enginepanel = EngineUtil.getEnginepanel();
				if (!EditStatusControl.isSubData()) {
					//判断是否有错误域
					 if(WdemsTagManager.Instance.hasWrongValidationComponents()) 
					 {
						 JOptionPane.showMessageDialog(EngineUtil.getEnginepanel(), "具有验证不通过的内容，请修正错误后再提交", "验证错误提示", JOptionPane.ERROR_MESSAGE);
						 return ; 
					 }
					 //判断是否有整体验证
					 if(!EditStatusControl.isWholeValidated())
					 {
						 if(!wholeValidateHandle())
						 {
							 JOptionPane.showMessageDialog(EngineUtil.getEnginepanel(), "整体验证不通过的内容，请修正错误后再提交", "验证错误提示", JOptionPane.ERROR_MESSAGE);
							 return ;
						 }
					 }
					EditUtil.saveXml();
					enginepanel.doreLayout();
				} else {
					enginepanel.reload();
				}
				EngineUtil.getEnginepanel().getToolbar().disableEditToolbar();
				enginepanel.getToolbar().refreshState();
			}
		};
		th.start();

	}

}
