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
 * StatusBar.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.view;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import com.wisii.edit.tag.factories.bar.TaskPaneMain;
import com.wisii.edit.tag.factories.bar.WdemsLogInfoComponent;
import com.wisii.edit.tag.factories.bar.WdemsLogInfoComponent.LoginfoItem;
import com.wisii.fov.render.awt.viewer.Translator;

/**
 * 类功能说明：
 * 状态消息栏
 * 作者：zhangqiang
 * 日期:2013-1-6
 */
public class StatusBar extends JPanel {
	private JLabel processStatus;
	private JLabel infoStatus;
	private static JLabel logInfos = null;
	private static WdemsLogInfoComponent logPop = null;

	public StatusBar() {
		processStatus = new JLabel(Translator.getInstanceof().getString(
				"Status.processStatus"));

		infoStatus = new JLabel();
		logInfos = new JLabel(new ImageIcon(
				TaskPaneMain.class.getResource("icons/tasks-email.png")),
				JLabel.LEADING);
		logInfos.setText("");
		logInfos.setPreferredSize(new Dimension(500, 18));
		logInfos.setHorizontalTextPosition(JLabel.LEADING);
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 0, 1, 5));
		processStatus.setPreferredSize(new Dimension(80, 21));
		processStatus.setMinimumSize(new Dimension(80, 21));

		infoStatus.setPreferredSize(new Dimension(80, 21));
		infoStatus.setMinimumSize(new Dimension(80, 21));
		processStatus.setHorizontalAlignment(SwingConstants.CENTER);
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 18));

		add(logInfos);

		add(separator);

		Dimension min = new Dimension(0, 18);
		Dimension pref = new Dimension(5, 18);
		Dimension max = new Dimension(Integer.MAX_VALUE / 2, 18);
		add(new Box.Filler(min, pref, max));
		JSeparator separator0 = new JSeparator(JSeparator.VERTICAL);
		separator0.setPreferredSize(new Dimension(2, 18));
		add(separator0);
		add(processStatus);
		add(infoStatus);

		final Set<LoginfoItem> items = new HashSet<LoginfoItem>();

		logPop = new WdemsLogInfoComponent(items);
		final Dimension dim = logPop.getPreferredSize();
		logInfos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				logPop.show(logInfos, 0, -dim.height);
			}
		});
	}
	public void setInfoStatus(String info)
	{
		infoStatus.setText(info);
	}
	public void setProcessStatus(String process)
	{
		processStatus.setText(process);
	}
	public static void addLogItems(final LoginfoItem... items)
	{
		if (logPop==null||items == null || items.length == 0)
			return;

		updataLog(items);
		logPop.addItems(items);
		
	}
	private final static void updataLog(final LoginfoItem... items) {
		Thread t = new Thread() {
			@Override
			public void run() {
				if (logPop == null || items == null || items.length == 0)
					return;
				for (LoginfoItem item : items) {
					logInfos.setText(item.getTitle());
				}
			}
		};
		SwingUtilities.invokeLater(t);
	}
}
