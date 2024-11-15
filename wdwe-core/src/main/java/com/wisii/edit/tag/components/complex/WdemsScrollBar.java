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
 * @WdemsScrollBar.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.complex;

import javax.swing.JScrollBar;

/**
 * 类功能描述：自定义滚动栏。 
 * 作者：李晓光 
 * 创建日期：2009-11-4
 */
@SuppressWarnings("serial")
class WdemsScrollBar extends JScrollBar {
	WdemsScrollBar(int orientation){
		super(orientation);
	}
	@Override
	public void updateUI() {
		setUI(new WdemsScrollBarUI());
	}
}
