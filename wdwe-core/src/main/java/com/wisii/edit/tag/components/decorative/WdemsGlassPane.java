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
 * @WdemsGlassPane.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import com.wisii.component.startUp.SystemUtil;

/**
 * 类功能描述：用于当前JFrame的GlassPanel
 * 1、屏蔽鼠标事件。
 * 2、屏蔽键盘事件。
 * 3、但透明效果。
 * 4、添加表示程序正在运行的图片。
 * 
 * 
 * 作者：李晓光
 * 创建日期：2009-7-6
 */
@SuppressWarnings("serial")
public class WdemsGlassPane extends JLabel {
//	private final static Icon icon = new ImageIcon(SystemUtil.getImagesPath("flower_small.gif"));
//	private static WdemsGlassPane pane = null;
//	public final static WdemsGlassPane getGlassPane(){
//		if(pane == null)
//			pane = new WdemsGlassPane();
//		return pane;
//	}
	public WdemsGlassPane(){
		this(new ImageIcon(SystemUtil.getImagesPath("flower_small.gif")));
	}
	private WdemsGlassPane(Icon icon){
		init();
		setIcon(icon);
	}
//	public void close(){
//		this.setVisible(Boolean.FALSE);
//	}
//	public void open(){
//		this.setVisible(Boolean.TRUE);
//	}
	
	private void init(){
		setOpaque(false);
		setFocusTraversalKeysEnabled(false);
		setBackground(new Color(128, 128, 128, 128));
		setHorizontalAlignment(JLabel.CENTER);
	}
}
