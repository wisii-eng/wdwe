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
 * @WdemsCheckbox.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.checkbox;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.components.group.WdemsGroupComponent;
import com.wisii.edit.tag.schema.wdems.Checkbox;

/**
 * 类功能描述：复选框控件。
 * 
 * 作者：李晓光 创建日期：2009-7-29
 */
public class WdemsCheckbox implements WdemsTagComponent {
	private final JCheckBox box = new WdemsGroupComponent();
	private Checkbox check = null;
	private Object result=null;
	public WdemsCheckbox(final Checkbox check){
		this.check = check;
		
		init();
		initStyle();
	}
	private void init(){
		box.setSelected(check.isChecked());
		box.setHorizontalAlignment(JCheckBox.CENTER);
		WdemsActioinHandler.bindActions(box, BindType.Checkbox);
//		if (check != null && check.getHint() != null) {
//			box.setToolTipText(check.getHint());
//			box.setText(check.getHint());
//		}
	}
	private void initStyle(){
		//TODO 根据标签的配置，初始化复选框样式。
	}
	public String getGroupName(){
		return (check.getGroupReference()) == null ? "" : (check.getGroupReference());
	}
	public void addActions(final Actions action) {
		box.addActionListener(action);
	}

	public JComponent getComponent() {
		return box;
	}

	public Object getValue() {
		if(check == null)return "";
		if(box.isSelected())
			return check.getSelectValue();
		return check.getUnselectedValue();
	}
	public void setValue(final Object value) {
		if(value instanceof Boolean){
			box.setSelected((Boolean)value);
		}else if(value instanceof String){
			iniValue(value);			
		} else {
			new IllegalArgumentException(value + "");
		}
	}
	public void setSelectIcon(final Icon icon) {
		box.setSelectedIcon(icon);
	}

	public void setUnSelectIcon(final Icon icon) {
		box.setIcon(icon);
	}

	public void setText(final String text) {
		box.setText(text);
	}

	public void iniValue(final Object value) {
		if(!(value instanceof String))return;
		String s = value.toString();
		
		if(s.equalsIgnoreCase(check.getSelectValue())){
			box.setSelected(Boolean.TRUE);
		}else{
			box.setSelected(Boolean.FALSE);
		}
	}
	
	public void setLocation(final Point p) {
		box.setLocation(p);
	}

	public void setMaximumSize(final Dimension maximumSize) {
		box.setMaximumSize(maximumSize);
	}

	public void showValidationState(final ValidationMessage action) {
		if (action.getValidationState()) {
			WdemsWarningManager.registerAccept(box);
		} else {
			WdemsWarningManager.registerWarning(box);
		}
	}
	public Object getActionResult() {
		return result;
	}

	/**
	 * @param result the result to set
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
