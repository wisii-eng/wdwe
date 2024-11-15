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
 *//**
 * DictionaryActionDoit.java
 * 北京汇智互联版权所有
 */
package com.wisii.component.createinitview.openView;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import com.wisii.edit.tag.components.floating.WdemsToolManager;
import com.wisii.edit.tag.components.input.Dictionaryable;
import com.wisii.edit.tookit.DictionaryPopMenu;
import com.wisii.fov.render.awt.viewer.Command;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-14
 */
public class DictionaryActionDoit extends Command {
	public DictionaryActionDoit(String name, String iconName) {
		super(name, iconName);
	}

	@Override
	public void action(ActionEvent e) {
		Component comp = WdemsToolManager.Instance.getFocusComponent();
		if (comp == null || !(comp instanceof Dictionaryable)
				|| ((Dictionaryable) comp).getDataSource() == null) {
			return;
		}
		DictionaryPopMenu pop = new DictionaryPopMenu();
		JComponent jcomp = (JComponent) e.getSource();
		pop.show(jcomp, 0, jcomp.getHeight());
	}

	// public boolean isEnabled() {
	// if (!super.isEnabled()) {
	// return false;
	// }
	//
	// }
}
