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
import javax.swing.SwingUtilities;
import com.wisii.component.setting.WisiiBean;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.cache.database.hsql.HsqldbService;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.render.awt.viewer.Command;

public class ShiftEditModeDoit extends Command {


	public ShiftEditModeDoit(final String name, String iconName) {
		super(name, iconName);

	}


	/**
	 * 该方法主要实现这个按钮的功能它包括 1.显示请等待遮罩 2.遍历AreaTree中的编辑项 3.得到三个文件并把它们建库
	 */
	@Override
	public void action(final ActionEvent e) {
		String edistr = EngineUtil.getEnginepanel().getWisiibean().getEditString();
		if (edistr == null||edistr.isEmpty()) {
			StatusbarMessageHelper.output("无编辑标签不能编辑", "",
					StatusbarMessageHelper.LEVEL.INFO);
		} else {
			process();
		}
	}

	private void process() {
		Thread th = new Thread() {
			public void run() {
				// 显示遮蔽罩
				EnginePanel enginepanel = EngineUtil.getEnginepanel();
//				enginepanel.openGlass();
				HsqldbService.getInstance();
				// 遍历pageviewport
				EditStatusControl.RUNSTATUS = EditStatusControl.STATUS.WRITE;
				enginepanel.reload();
				enginepanel.getToolbar().enableEditToolbar();
//				enginepanel.closeGlass();
				enginepanel.getToolbar().refreshState();
			
			}
		};
		SwingUtilities.invokeLater(th);
	}

	
	@Override
	public boolean isEnabled() {
		if(!super.isEnabled())
		{
			return false;
		}
		return EditStatusControl.RUNSTATUS != EditStatusControl.STATUS.WRITE;
	}
}