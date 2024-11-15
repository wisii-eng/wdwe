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

import java.lang.reflect.Constructor;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import com.wisii.component.setting.ItemSetting;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.render.awt.viewer.Command;

public class ComponentModel {

	public static void addItem(ItemSetting item, JToolBar toolbar) {
		if(toolbar==null||item==null)
		{
			return;
		}
		AbstractButton button=getButton(item);
		if(button!=null)
		{
			toolbar.add(button);
		}

	}
	public static AbstractButton getButton(ItemSetting item)
	{
		Command action = getCommand(item);
		if (action == null) {
			return null;
		}
		AbstractButton button = getButton(action, item.getTitle(),
				item.getKey(), true);
		// 设置自定义Icon
		setButtonIcon(button,(Icon) action.getValue(Action.SMALL_ICON));
		return button;
	}

	public static Command getCommand(ItemSetting item) {
		try {
			Class cls = Class
					.forName("com.wisii.component.createinitview.openView."
							+ item.getClassname());
			Class[] types = new Class[] { String.class, String.class};
			Constructor cons = cls.getConstructor(types);

			Object[] args = new Object[] { item.getTitle(), item.getIcon() };
			Command action;

			action = (Command) cons.newInstance(args);
			return action;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 添加按钮 tool ：工具栏 button： 按钮 action： 执行的action name :快捷键与action相对应的名称
	 * keyEvent ：快捷键 RolloverEnabled ：是否有翻转效果
	 */
	@SuppressWarnings("serial")
	private static AbstractButton getButton(Action action, String name,
			String keyEvent, boolean RolloverEnabled) {
		JButton button=new JButton();
		button.setAction(action);
		button.setRolloverEnabled(RolloverEnabled);
		keyEvent = SystemUtil.buildStroke(keyEvent);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(keyEvent), name);
		button.getActionMap().put(name, action);
		return button;
	}

	/**
	 * 设置Button上的Icon button 要设置的Button Icon 自定义的Icon在configMap中的key
	 */
	private static boolean setButtonIcon(AbstractButton button, Icon icon) {
		button.setText("");
		button.setIcon(icon);
		return true;
	}
}
