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

import java.util.HashMap;
import java.util.Map;

import com.wisii.edit.tag.TagType;
import com.wisii.edit.tag.WdemsComponent;


/**
 * 为控件创建action的工厂
 * @author 闫舒寰
 * @version 1.0 2009/0616
 */
public enum ActionFactory {
	
	Instance;
	
	//factory type
	static Map<TagType, WdemsActionFactory> aType = new HashMap<TagType, WdemsActionFactory>();
	
	static {
		aType.put(TagType.input, InputActionFactory.Instance);
		aType.put(TagType.select, SelectActionFactory.Instance);
		aType.put(TagType.date, DateActionFactory.Instance);
		aType.put(TagType.checkbox, CheckboxActionFactory.Instance);
		aType.put(TagType.button, ButtonActionFactory.Instance);
		aType.put(TagType.popupbrowser, PopupBrowserActionFactory.Instance);
		aType.put(TagType.graphic, GraphicActionFactory.Instance);
		
	}
	
	public Actions getAction(final WdemsComponent wc){
		return aType.get(TagType.getActionType(wc.getTagObject())).makeAction(wc);
	}
	
}
