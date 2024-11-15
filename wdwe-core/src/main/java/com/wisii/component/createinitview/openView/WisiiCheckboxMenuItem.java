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
 * @WisiiCheckboxMenuItem.java
 * 
 */
package com.wisii.component.createinitview.openView;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

/**
 * 作者：李晓光
 * 时间：2009-2-3
 * 类功能描述：用于创建带复选框的菜单项
 */
public class WisiiCheckboxMenuItem extends JCheckBoxMenuItem {
	private Object value;
	public WisiiCheckboxMenuItem(String text, Object value){
		super(text);
		setValue(value);
	}
	public WisiiCheckboxMenuItem(String text, Action action, Object value){
		super(action);
		setText(text);
		setValue(value);
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
