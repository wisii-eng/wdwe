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
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.util.EditUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;
import com.wisii.edit.EditStatusControl.STATUS;

/**
 * 该类用于浮动工具栏的从排按钮的重排事件 该类不从 componentModel中初始化
 * 
 * @author liuxiao
 * 
 */

public class ReloadActionDoit extends WholeValidateActionDoit {

	public ReloadActionDoit(String name, String iconName) {
		super(name, iconName);
	}

	@Override
	public void action(ActionEvent e) {
		if (EditStatusControl.RUNSTATUS == STATUS.READ) {
			return;
		}
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
		process();
	}

	public void process() {
		final EnginePanel enginepanel = EngineUtil.getEnginepanel();
		// 将控件都remove掉
		WdemsTagManager.Instance.clearCurrentPageComponents();
		EditUtil.saveXmlToWisiiBean();
		StatusbarMessageHelper.output("保存成功", "",
				StatusbarMessageHelper.LEVEL.INFO);
		Thread t = new Thread() {
			@Override
			public void run() {
				enginepanel.doreLayout();
			}
		};
		t.start();
		EditStatusControl.reload();

	}

}
