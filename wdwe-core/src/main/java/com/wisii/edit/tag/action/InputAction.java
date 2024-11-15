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
 */package com.wisii.edit.tag.action;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import com.wisii.edit.tag.util.WdemsTagUtil;

/**
 * input动作类
 * @author 闫舒寰
 * @version 1.0 2009/06/16
 */
public class InputAction extends Actions {
	

	public InputAction() {
	}
	
	
	@Override
	public Object doAction(final ActionEvent e) {
		Object value = getTagComponent().getValue();
		return value;
	}
	
	@Override
	public boolean updateXML() {
		boolean updateXML = WdemsTagUtil.updateXML(getXPath(), getValue());
		getMessageListener().refresh();//刷新一下是为了显示加有显示条件的控件。
		return updateXML;
	}
	

	/************下面的代码都是为了测试而存在的******************/
	@Action
	public void action(){
		if (actionEvent.getSource() instanceof JTextField) {
			JTextField tf = (JTextField) actionEvent.getSource();
//			System.out.println("input word: " + tf.getText());
		}
//		System.out.println("xpath: " + getXPath());
	}
	
	@Validation(id = 2, value = "ppl")
	public void validate(){
		int i = 0;
		for (int j = 0; j < 1000000; j++) {
			i += j;
		}
//		System.out.println("hihi" + i);
	}
}
