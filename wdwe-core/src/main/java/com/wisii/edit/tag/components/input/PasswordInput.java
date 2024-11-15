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
 */package com.wisii.edit.tag.components.input;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPasswordField;

import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.schema.wdems.Input;

/**
 * 密码输入框
 * @author 闫舒寰
 * @version 1.0 2009/07/10
 */
public class PasswordInput extends JPasswordField implements WdemsTagComponent {

	private final JPasswordField component;
	private Object result = null;

	public PasswordInput(final Input input) {
		component = this;
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(new Color(253, 238, 238));
		if (input.getHint() != null) {
			component.setToolTipText(input.getHint());
		}
	}

	public void addActions(final Actions action) {
		component.addActionListener(action);
	}

	public JComponent getComponent() {
		return component;
	}

	public Object getValue() {
		return component.getText();
	}

	public void iniValue(final Object value) {
		component.setText(value.toString());
	}

	public void setValue(final Object value) {
		component.setText(value.toString());
	}

	public void showValidationState(final ValidationMessage vAction) {
		component.setBackground(Color.red);
	}

	public Object getActionResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setActionResult(Object result) {
		this.result = result;
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
