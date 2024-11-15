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

import com.wisii.edit.tag.components.select.Data;
import com.wisii.edit.tag.schema.wdems.Select;
import com.wisii.edit.tag.util.WdemsTagUtil;
import com.wisii.edit.tag.util.WdemsTagUtil.ValidationType;

/**
 * 下拉菜单动作
 * 
 * @author 闫舒寰
 * @version 1.0 2009/06/17
 */
public class SelectAction extends Actions {

	@Override
	public Object doAction(final ActionEvent e) {
		return getTagComponent().getValue();
	}
	
	@Override
	public boolean updateXML() {
		
		Object o = getTagObject();
		
		if (o instanceof Select) {
			Select sel = (Select) o;
			if (sel.getSrc().equals("schema")) {
				Object value = getValue();
				if (value instanceof Data<?>) {
					Data<?> data = (Data<?>) value;
					WdemsTagUtil.updateXML(getXPath(), data.getObject(1));
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean doOnBlurValidation() {
		return WdemsTagUtil.doValidation(ValidationType.onBlur, this);
	}
}
