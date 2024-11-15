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
 * WiseToolBar.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import com.wisii.component.createinitview.openView.ComponentModel;
import com.wisii.component.createinitview.openView.JPopupButton;
import com.wisii.component.setting.CustomerSetting;
import com.wisii.component.setting.ItemSetting;
import com.wisii.component.setting.ToolBarSetting;
import com.wisii.fov.render.awt.viewer.Command;
import com.wisii.fov.render.awt.viewer.Translator;

/**
 * 类功能说明：
 * 工具栏，功能为，根据传入的settingid，找到对应配置项，
 * 根据配置项的内容显示工具栏
 * 作者：zhangqiang
 * 日期:2013-1-6
 */
public class WiseToolBar extends JToolBar {
	private String toolbarlocation;
	private String settingid;
	private List<AbstractButton> editbuttons;

	public WiseToolBar(String settingid, double scale) {
		this.settingid = settingid;
		CustomerSetting customerSetting = CustomerSetting.getInstance().init(
				settingid);
		ToolBarSetting toolbarsetting = customerSetting.getToolBarSetting();
		if (toolbarsetting == null) {
			return;
		}
		toolbarlocation = toolbarsetting.getLocation();
		// 设置ToolBar的位置
		if (toolbarlocation.equalsIgnoreCase("left")
				|| toolbarlocation.equalsIgnoreCase("right")) {
			setOrientation(SwingConstants.VERTICAL);
		}
		List ts = toolbarsetting.getItems();
		if (ts == null || ts.isEmpty()) {
			return;
		}
		for (int i = 0; i < ts.size(); i++) {

			ItemSetting ite = (ItemSetting) ts.get(i);
			String type = ite.getType();
			if (type.equalsIgnoreCase("scale")) {
				JComboBox scalebox = new JComboBox();
				scalebox.addItem(Translator.getInstanceof().getString(
						"Menu.Fit.Window"));
				scalebox.addItem(Translator.getInstanceof().getString(
						"Menu.Fit.Width"));
				scalebox.addItem("25%");
				scalebox.addItem("50%");
				scalebox.addItem("75%");
				scalebox.addItem("100%");
				scalebox.addItem("150%");
				scalebox.addItem("200%");
				scalebox.setMaximumSize(new Dimension(65, 24));
				scalebox.setSize(new Dimension(65, 24));
				scalebox.setEditable(true);
				scalebox.setToolTipText(Translator.getInstanceof().getString(
						"Menu.Zoom"));
				scalebox.setSelectedItem(Math.round(scale * 100) + "%");
				Command scalecom = ComponentModel.getCommand(ite);
				if (scalecom != null) {
					scalebox.getActionMap().put(ite.getName(), scalecom);
					scalebox.addActionListener(scalecom);
				}
				add(scalebox);

			} else if (type.equalsIgnoreCase("button")) {
				if (Boolean.parseBoolean(ite.getIsEdit())) {
					if (editbuttons == null) {
						editbuttons = new ArrayList<AbstractButton>();
					}
					AbstractButton editbutton = ComponentModel.getButton(ite);
					if (editbutton != null) {
						editbuttons.add(editbutton);
					}
				} else {
					// 添加按钮
					ComponentModel.addItem(ite, this);
				}

			} else if (type.equalsIgnoreCase("blank")) {
				int dimension = 10;
				try {
					dimension = Integer.parseInt(ite.getSize());
				} catch (NumberFormatException e) {
				}
				addSeparator(new Dimension(dimension, 0));

			} else if (type.equalsIgnoreCase("combox")) {
				if (ite.getName().equals("layer")) {
					JPopupButton comLayer = new JPopupButton(
							JPopupButton.TYPE_WITH_RIGHT_TOGGLE,
							ite.getTitle(), ite.getIcon(), ite.getKey());// 层设置按钮
					comLayer.setMaximumSize(new Dimension(Integer.parseInt(ite
							.getSize()), 27));
					add(comLayer);
				}
			}
		}
		addSeparator();
	}

	public String getToolBarLocation() {
		return toolbarlocation;
	}

	public String getSettingid() {
		return settingid;
	}

	public void enableEditToolbar() {
		if (editbuttons != null) {
			for(AbstractButton editbutton:editbuttons){
			this.add(editbutton);
			
			}
		}
	}

	public void disableEditToolbar() {
		if (editbuttons != null) {
			for (AbstractButton editbutton : editbuttons) {
				this.remove(editbutton);
			}
		}

	}
	public void  refreshState()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<getComponentCount();i++)
				{
					JComponent com = (JComponent) getComponent(i);
					ActionMap actions=com.getActionMap();
					Object[] keys = actions.keys();
					Command command=null;
					if(keys!=null)
					{
						for(Object key:keys)
						{
							Action action=actions.get(key);
							if(action instanceof Command)
							{
								command=(Command) action;
								break;
							}
						}
					}
					if(command!=null)
					{
						com.setEnabled(command.isEnabled());	
					}
		            
				}
				WiseToolBar.this.updateUI();
				
			}
		});
	}
}
