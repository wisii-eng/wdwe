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
 */package com.wisii.edit.tag.factories;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.select.AbstractWdemsCombox;
import com.wisii.edit.tag.schema.wdems.Select;

/**
 * 这个工厂类负责解析Select控件的有关并生成Swing组件的属性
 * @author 闫舒寰
 * @version 1.0 2009/06/10
 *
 */
public enum SelectFactory implements TagFactory {

	Instance;
	
	public WdemsTagComponent makeComponent(final WdemsComponent wc) {
		
//		System.out.println("inside select factory: " + tagObject);
		
		final Object tagObject = wc.getTagObject();
		
		Select sel = null;
		
		if (!(tagObject instanceof Select))
			return null;
		sel = (Select) tagObject;
		WdemsTagComponent wtc = AbstractWdemsCombox.buildCombox(sel, wc.getTagXPath());
		return wtc;
		
//		return testCombobox();
	}
	
	private static JComboBox jcb = new JComboBox(new String[]{"1", "2", "3"});
	
	private WdemsTagComponent testCombobox(){
		
		WdemsTagComponent wtc = new WdemsTagComponent() {
			private Object result=null;
			public void showValidationState(ValidationMessage vAction) {
				// TODO Auto-generated method stub
				
			}
			
			public void setMaximumSize(final Dimension maximumSize) {
				// TODO Auto-generated method stub
				
			}
			
			public void setLocation(final Point p) {
				// TODO Auto-generated method stub
				
			}
			
			public void iniValue(final Object value) {
				// TODO Auto-generated method stub
				
			}
			
			public Object getValue() {
				return jcb.getSelectedItem();
			}
			public void setValue(Object value) {
				// TODO Auto-generated method stub
				
			}
			public JComponent getComponent() {
				// TODO Auto-generated method stub
				return jcb;
			}
			
			public void addActions(final Actions action) {
				jcb.addActionListener(action);
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
		};
		
		return wtc;
	}

}
