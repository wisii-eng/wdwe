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
 */package com.wisii.edit.tag;

import java.util.HashMap;
import java.util.Map;

import com.wisii.edit.tag.schema.wdems.Button;
import com.wisii.edit.tag.schema.wdems.Checkbox;
import com.wisii.edit.tag.schema.wdems.Date;
import com.wisii.edit.tag.schema.wdems.Graphic;
import com.wisii.edit.tag.schema.wdems.Input;
import com.wisii.edit.tag.schema.wdems.PopupBrowser;
import com.wisii.edit.tag.schema.wdems.Select;

/**
 * 这里为标签元素定义了分类
 * @author 闫舒寰
 * @version 1.0 2009/06/16
 */
public enum TagType {
	
	input,
	select,
	date,
	Group,
	checkbox,
	button, 
	popupbrowser,
	graphic;
	
	static Map<Class<? extends Object>, TagType> actionTypeMap = new HashMap<Class<? extends Object>, TagType>();
	
	static {
		actionTypeMap.put(Input.class, TagType.input);
		actionTypeMap.put(Select.class, TagType.select);
		actionTypeMap.put(Date.class, TagType.date);
		
		actionTypeMap.put(Checkbox.class, TagType.checkbox);
		actionTypeMap.put(com.wisii.edit.tag.schema.wdems.Group.class, TagType.Group);
		actionTypeMap.put(Button.class, TagType.button);
		actionTypeMap.put(PopupBrowser.class, TagType.popupbrowser);
		actionTypeMap.put(Graphic.class, TagType.graphic);
	}
	
	
	public static TagType getActionType(final Object tagObject){
		return actionTypeMap.get(tagObject.getClass());
	}

}
