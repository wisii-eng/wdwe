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

import com.wisii.component.actionevent.ActionEventType;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.render.awt.viewer.Command;
import com.wisii.fov.render.awt.viewer.GoToPageDialog;
import com.wisii.fov.render.awt.viewer.Translator;

public class GotoActionDoit extends Command
{

	
	public GotoActionDoit(String name, String iconName) {
		super(name, iconName);
	}

	
	
	
	 public void action(ActionEvent e) {
			GoToPageDialog dialog = new GoToPageDialog(Translator.getInstanceof()
							.getString("Menu.Go.to.Page"));
			dialog.setVisible(true);
			int currentPage = dialog.getPageNumber();
			if (currentPage < 1) {
				return;
			}
			EngineUtil.getEnginepanel().goToPage(currentPage);
	    }

}
