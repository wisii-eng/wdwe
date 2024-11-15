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
 * @WdemsLogInfoComponent.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.factories.bar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import com.wisii.edit.message.StatusbarMessageHelper.LEVEL;
/**
 * 类功能描述：状态栏消息展示控件
 * 1、从数据库中读取消息。
 * 2、包装消息为当前控件。
 * 3、根据消息是否为已读，确定图标显示。
 * 
 * 
 * 作者：李晓光
 * 创建日期：2009-6-24
 */
@SuppressWarnings({ "serial" })
public class WdemsLogInfoComponent extends JPopupMenu {
	/** 详细信息的数据类型 */
	public static enum DataStyle{
		/* 纯文本 */
		TEXT_PLAIN("", "text/plain"),
		/* HTML文本 */
		TEXT_HTML("<html>", "text/html"),
		/* RTF文本 */
		TEXT_RTF("", "text/rtf");
		/* 默认值 */
		private String text = "";
		/* 文本类型 */
		private String type = "";
		DataStyle(String text, String type){
			this.text = text;
			this.type = type;
		}
		public String getText(){
			return this.text;
		}
		public String getType(){
			return this.type;
		}
	}
	private Set<LoginfoItem> dataSource = null;
	private DataStyle style = DataStyle.TEXT_PLAIN;
	final JTaskPane pane = new JTaskPane();
	final JScrollPane scrPane = new JScrollPane(pane);
	
	public WdemsLogInfoComponent() {
		this(null);
	}
	public WdemsLogInfoComponent(Set<LoginfoItem> dataSource) {
		init();
		setDataSource(dataSource);
	}
	@Override
	public void show(Component invoker, int x, int y) {
		//让滚动滑块至于最上方
		scrPane.getViewport().setViewPosition(new Point());
		super.show(invoker, x, y);
	}
	private void init(){
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 250));
		add(createTitlePanel(), BorderLayout.NORTH);
	}
	private JComponent createTitlePanel() {
		JLabel lab = new JLabel("日志信息");
		lab.setOpaque(true);
		lab.setBackground(new Color(242, 246, 251));
		return lab;
	}
	private void initComponents(final Set<LoginfoItem> dataSource){
		pane.removeAll();
		if(this.dataSource != null) {
			this.dataSource.clear();
		}
		initAddComponents(dataSource);
		
	}
	private void initAddComponents(final Set<LoginfoItem> dataSource){
		if(dataSource == null || dataSource.isEmpty())
			return;
		if(this.dataSource == null) {
			this.dataSource = new HashSet<LoginfoItem>(dataSource);
		} else {
			this.dataSource.addAll(dataSource);
		}
		
		Thread t = new Thread(){
			@Override
			public void run() {
				for (LoginfoItem item : dataSource) {
					JTaskPaneGroup group = createItem(item);
					pane.add(group, 0);					
				}
				add(scrPane, BorderLayout.CENTER);
				validate();
			}
		};
		
		SwingUtilities.invokeLater(t);
	}
	private JTaskPaneGroup createItem(LoginfoItem item){
		JTaskPaneGroup group = new JTaskPaneGroup();
		group.setTitle(item.getTitle());
		/* 创建的组，在现实时是否打开，True：打开 False：收起 */
		group.setScrollOnExpand(Boolean.TRUE);
		group.setExpanded(Boolean.FALSE);
		try {
			group.setIcon(new ImageIcon(TaskPaneMain.class.getResource("icons/tasks-email.png")));
		} catch (Exception e) {
			System.err.println("WdemsLogInfoComponent.createItem()没有找到icons/tasks-email.png");
		}
		
		JEditorPane text = createEditorPane();
		LookAndFeelTweaks.makeMultilineLabel(text);
		if(style == DataStyle.TEXT_HTML) {
			LookAndFeelTweaks.htmlize(text);
		}
		text.setText(item.getLogInfo());
		group.add(text);
		return group;
	}
	private JEditorPane createEditorPane(){
		JEditorPane text = new JEditorPane(style.getType(), style.getText());
		return text;
	}
	public DataStyle getStyle() {
		return style;
	}
	public void setStyle(DataStyle style) {
		this.style = style;
	}
	public Set<LoginfoItem> getDataSource() {
		return dataSource;
	}
	public void setDataSource(Set<LoginfoItem> dataSource) {
		if(this.dataSource == dataSource)
			return;
		initComponents(dataSource);
		this.dataSource = dataSource;
	}
	public void addItems(LoginfoItem...items){
		if(items == null || items.length == 0)
			return;
		Set<LoginfoItem> temp = new HashSet<LoginfoItem>(items.length);		
		temp.addAll(Arrays.asList(items));
		initAddComponents(temp);
	}
	
	/**
	 * 类功能描述：用于描述一条log信息
	 * @author 李晓光	2009-6-24
	 */
	public static class LoginfoItem {
		/*public static enum LogLevel{
			LEVEL_DEBUG(StatusbarMessageHelper.LEVEL_DEBUG),
			LEVEL_INFO(StatusbarMessageHelper.LEVEL_INFO);
			LogLevel(int value){
				this.value = value;
			}
			private int value = -1;
			public int getValue(){
				return this.value;
			}
		}*/
		/* 数据ID，标识在数据集合中唯一存在，如果不需要向数据中写数据，不需要指定ID */
		private Integer id = -1;
		/* Log的概要信息 */
		private String title = "";
		/* Log的相信信息 */
		private String logInfo = "";
		/* 表示该信息的状态【True表示已读，False表示未读】 */
		private Boolean read = Boolean.FALSE;
		/* LOG等级 */
		private LEVEL level = LEVEL.DEBUG;
		public LoginfoItem(){}
		public LoginfoItem(String title, String logInfo){
			this(title, logInfo, LEVEL.DEBUG);
		}
		public LoginfoItem(String title, String logInfo, LEVEL level){
			this(-1, title, logInfo, Boolean.FALSE, level);
		}
		public LoginfoItem(Integer id, String title, String logInfo, Boolean read, LEVEL level){
			this.id = id;
			this.title = title;
			this.logInfo = logInfo;
			this.read = read;
			this.level = level;
		}
		
		public Boolean isRead() {
			return read;
		}

		public void setRead(Boolean read) {
			this.read = read;
		}

		public String getLogInfo() {
			return logInfo;
		}

		public void setLogInfo(String logInfo) {
			this.logInfo = logInfo;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		public LEVEL getLevel() {
			return level;
		}
		public void setLevel(LEVEL level) {
			this.level = level;
		}
		@Override
		public String toString() {
			final char separator = ',';
			StringBuilder s = new StringBuilder("[id=");
			s.append(getId());
			s.append(separator);
			s.append("title=");
			s.append(getTitle());
			s.append(separator);
			s.append("loginfo=");
			s.append(getLogInfo());
			s.append(separator);
			s.append("read=");
			s.append(isRead());
			s.append(separator);
			s.append("LogLevel=");
			s.append(getLevel());
			s.append("]");
			return s.toString();
		}
	}
}
