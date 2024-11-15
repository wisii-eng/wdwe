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
 * DictionaryPopMenu.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.tookit;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.floating.WdemsToolManager;
import com.wisii.edit.tag.components.input.Dictionaryable;
import com.wisii.edit.tag.components.select.datasource.DataSource;
import com.wisii.edit.tag.components.select.datasource.SwingDataSource;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-14
 */
public class DictionaryPopMenu extends JPopupMenu {
	public DictionaryPopMenu() {
		init();

	}

	private void init() {
		Component comp = WdemsToolManager.Instance.getFocusComponent();
		if (comp == null || !(comp instanceof Dictionaryable)) {
			return;
		}
		DataSource dataSource = ((Dictionaryable) comp).getDataSource();
		if (dataSource == null) {
			return;
		}
		SwingDataSource swingds = dataSource.getSwingDS();
		if (swingds == null) {
			return;
		}
		List<List<String>> swingdatas = swingds.getData(null, -1, -1, null);
		if (swingdatas == null || swingdatas.isEmpty()) {
			return;
		}
		for (List<String> txts : swingdatas) {
			if (txts.size() > 0) {
				add(new TextMenu(txts.get(0)));
			}
		}

	}

	private static class TextMenu extends JMenuItem {
		private TextMenu(String txt) {
			super(txt);
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Component comp = WdemsToolManager.Instance
							.getFocusComponent();
					if (comp == null || !(comp instanceof WdemsTagComponent))
						return;

					WdemsTagComponent text = (WdemsTagComponent) comp;
					text.setValue(TextMenu.this.getText());
				}
			});
		}

	}
}
