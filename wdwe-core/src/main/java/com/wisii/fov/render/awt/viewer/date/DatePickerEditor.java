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
 * @DatePickerEditor.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.fov.render.awt.viewer.date;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;

import javax.swing.ComboBoxEditor;
import javax.swing.SwingConstants;

import com.wisii.edit.tag.components.datatime.WdemsDateTimeField;

/**
 * 类功能描述：用于下列日期控件的Editor。
 * 1、格式化展示。
 * 作者：李晓光
 * 创建日期：2009-6-19
 */
class DatePickerEditor implements ComboBoxEditor{
	private WdemsDateTimeField editor = new WdemsDateTimeField();
	
	DatePickerEditor(){
		editor.setBorder(null);
		editor.setHorizontalAlignment(SwingConstants.CENTER);
		editor.addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e) {
				try {
					editor.commitEdit();
				} catch (ParseException ex) {
					ex.printStackTrace();
				}				
			}
		});
	}

	public void setPattern(String pattern){
		editor.setPattern(pattern);
	}
	public Component getEditorComponent() {
		return editor;
	}

	public void selectAll() {
		editor.selectAll();
		/*editor.requestFocus();*/
	}

	public void setItem(Object value) {
		editor.iniValue(value);
	}
	public Object getItem() {
		/*return editor.getValue();*/
		
		return editor.getItem();
	}
	public void addActionListener(ActionListener l) {
		editor.addActionListener(l);
	}
	public void removeActionListener(ActionListener l) {
		editor.removeActionListener(l);
	}
}
