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
 */package com.wisii.edit.tag.components.button;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Action;
import javax.swing.JComponent;

import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.decorative.VirtualButton;
import com.wisii.edit.tag.schema.wdems.Button;

/**
 * 虚按钮类 ，目前这个按钮仅仅用于提供基本的信息，以后或许会有实体的按钮出现。
 * @author 闫舒寰
 * @version 1.0 2009/08/05
 */
public class WdemsButton implements WdemsTagComponent, VirtualButton{
	
	private final Button button;
	
	private Actions action;
	private Object result=null;
	public WdemsButton(final Button button) {
		this.button = button;
	}

	public void addActions(final Actions action) {
		this.action = action;
	}

	public JComponent getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void iniValue(final Object value) {
		// TODO Auto-generated method stub
		
	}

	public void setLocation(final Point p) {
		// TODO Auto-generated method stub
		
	}

	public void setMaximumSize(final Dimension maximumSize) {
		// TODO Auto-generated method stub
		
	}

	public void setValue(final Object value) {
		// TODO Auto-generated method stub
		
	}

	public void showValidationState(final ValidationMessage vAction) {
		// TODO Auto-generated method stub
		
	}

	public Action getAction() {
		return this.action;
	}

	public String getHint() {
		return button.getHint();
	}

	public String getPon() {
		return button.getInsert();
	}

	public String getType() {
		return button.getType();
	}

	/**
	 * @return the result
	 */
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
