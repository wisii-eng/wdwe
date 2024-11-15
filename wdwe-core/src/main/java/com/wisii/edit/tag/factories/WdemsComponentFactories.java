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

import java.util.HashMap;
import java.util.Map;

import com.wisii.edit.tag.TagType;
import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.components.WdemsTagComponent;

/**
 * 用户统一经过该工厂类来取得WdemsComponent
 * 根据不同种类的tag找到不同的factory
 * @author 闫舒寰
 * @version 1.0 2009/06/10
 */
public enum WdemsComponentFactories {
	
	Instance;
	
	//factory type
	static Map<TagType, TagFactory> fType = new HashMap<TagType, TagFactory>();
	
	static {
		fType.put(TagType.input, InputFactory.Instance);
		fType.put(TagType.select, SelectFactory.Instance);
		fType.put(TagType.date, DateFactory.Instance);
		fType.put(TagType.checkbox, CheckboxFactory.Instance);
		fType.put(TagType.button, ButtonFactory.Instance);
		fType.put(TagType.Group, GroupFactory.Instance);
		fType.put(TagType.popupbrowser, PopupBrowserFactory.Instance);
		fType.put(TagType.graphic, GraphicFactory.Instance);
	}
	
	/**
	 * 根据WdemsComponent对象返回Java控件对象
	 * @param wc
	 * @return
	 */
	public WdemsTagComponent getWdemsComponent(final WdemsComponent wc){
		
		TagType actionType = TagType.getActionType(wc.getTagObject());
		
		if (actionType != null) {
			return fType.get(actionType).makeComponent(wc);
		} else {
			//不是所属类别
			return null;
		}
		
	}
}
