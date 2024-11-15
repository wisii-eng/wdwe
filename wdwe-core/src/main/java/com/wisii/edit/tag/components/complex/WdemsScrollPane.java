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
 * @WdemsScrollPane.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.complex;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 * 类功能描述：自定义滚动面板。 
 * 作者：李晓光 
 * 创建日期：2009-11-4
 */
@SuppressWarnings("serial")
public class WdemsScrollPane extends JScrollPane {
	static{
		UIManager.put("wdems.scrollBar.width", 5);
		/*UIManager.put("ScrollBar.thumb", Color.GRAY);
		UIManager.put("ScrollBar.thumbShadow", Color.GRAY);*/
	}
	public WdemsScrollPane(Component view){
		super(view);
	}
	@Override
	public Insets getInsets() {
		return new Insets(0, 0,0, 0);
	}
	@Override
	public JScrollBar createHorizontalScrollBar() {
		return new WdemsScrollBar(JScrollBar.HORIZONTAL);
	}
	@Override
	public JScrollBar createVerticalScrollBar(){
		return new WdemsScrollBar(JScrollBar.VERTICAL);
	}
}
