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
 * 汇智互联版权所有，未经许可，不得使用
 */

/* $Id: com.wisii.edit.tag.components.button CommonButton.java 2010-8-3 上午11:20:04 李晓光 $ */

package com.wisii.edit.tag.components.button;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.schema.wdems.Button;

/**
 * 类说明:普通按钮
 *
 *
 * @author 李晓光
 * @version  3.0, 2010-8-3, 上午11:20:04
 * @since 2.0
 */
public class CommonButton implements WdemsTagComponent{
	private final JButton btn = new JButton();
	@SuppressWarnings("unused")
	private final Button button;
	private Object vlaue = null;
	private Object result=null;
	public CommonButton(final Button button){
		this.button = button;
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setText(button.getTitle());
		final String hint = button.getHint();
		if(hint != null || "".equals(hint)){
			return;
		}
		btn.setToolTipText(hint);
	}
	
	public void addActions(final Actions action) {
		btn.addActionListener(action);
	}

	
	public JComponent getComponent() {
		return btn;
	}


	public Object getValue() {
		return vlaue;
	}

	
	public void iniValue(final Object value) {
		this.vlaue = value;;
	}

	
	public void setLocation(final Point p) {
		btn.setLocation(p);
	}

	
	public void setMaximumSize(final Dimension maximumSize) {
		btn.setMaximumSize(maximumSize);
	}

	
	public void setValue(final Object value) {
		this.vlaue = value;
	}


	public void showValidationState(final ValidationMessage vAction) {
		
	}

	public Object getActionResult() {
		
		return result;
	}

	public void setActionResult(Object result) {
		this.result=result;
		
	}
	@Override
	public void setDefaultValue(String value) {
	}
	@Override
	public boolean canInitDefaultValue() {
		return false;
	}
	@Override
	public void initByDefaultValue() {
	}
}
