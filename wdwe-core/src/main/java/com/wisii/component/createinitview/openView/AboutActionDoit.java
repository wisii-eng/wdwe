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
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import com.wisii.edit.tag.util.WdemsTagUtil;
import com.wisii.fov.render.awt.viewer.Command;
import com.wisii.fov.util.HardWareInfoGet;

/**
 * 系统说明菜单按钮动作
 * @author 刘晓
 *
 */
public class AboutActionDoit extends Command {

	public AboutActionDoit(final String name, final String iconName) {
		super(name, iconName);
		//初始化下拉菜单
		initialMenu();
	}
	
	//弹出式下拉菜单
	JPopupMenu pop;
	
	//弹出式下拉菜单和动作
	private void initialMenu(){
		pop = new JPopupMenu();
		
		final JMenuItem about = new JMenuItem("关于");
		
		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				WdemsTagUtil.getWdemsVersion(about);
			}
		});
		
		pop.add(about);
		
//		pop.addSeparator();
		
		JMenuItem regedit = new JMenuItem("注册");
		regedit.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				// TODO 这里写注册动作
				new HardWareInfoGet();
			}
		});
		pop.add(regedit);
	}

	@Override
	public void action(final ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JComponent) {
			JComponent jc = (JComponent) source;
			pop.show(jc, 0, jc.getHeight());
		}

	}

}
