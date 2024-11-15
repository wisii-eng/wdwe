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

/**
 * 该类用于浮动工具栏的从排按钮的重排事件 该类不从 componentModel中初始化
 * 
 * @author liuxiao
 * 
 */

public class ExitEditActionDoit extends WholeValidateActionDoit {
	/**
	 * 
	 * @param name
	 * @param iconName
	 * @param viewe
	 */
	public ExitEditActionDoit(String name, String iconName) {
		super(name, iconName);

	}

	/**
	 * action的具体逻辑
	 */
	@Override
	public void action(ActionEvent e) {
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
		 //判断它是否要提交
		askIfSubmit();
		process();
	}
	/**
	 * 询问用户是否提交，如果选择是则提交 liuxiao
	 */
	private void askIfSubmit() {

		if (EditStatusControl.isSubData())
			return;
		// TODO: 该方法需要编写
		int result = JOptionPane.NO_OPTION;
		result = JOptionPane.showConfirmDialog(EngineUtil.getEnginepanel(),
				"是否将保存数据？", "修改确认", JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) // 点击是
		{

			EditUtil.saveXml();
			EngineUtil.getEnginepanel().doreLayout();
			StatusbarMessageHelper.output("保存成功", "",
					StatusbarMessageHelper.LEVEL.INFO);
			return;

		} 
	}
	private void process() {
		// 设置为读状态
		EditStatusControl.RUNSTATUS = EditStatusControl.STATUS.READ;
		final EnginePanel  panel=EngineUtil.getEnginepanel();
		// 如果更新过则走重载流程
		if (!EditStatusControl.isReloaded()) {
			panel.reload();
		} else {
		
			Thread t = new Thread() {
				@Override
				public void run() {
					panel.doreLayout();
				}
			};
			t.start();
			
		}
		EngineUtil.getEnginepanel().getToolbar().disableEditToolbar();
	}

}
