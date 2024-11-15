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
 * @KeyManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.action.schema;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.xml.bind.JAXB;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.WdemsActioinHandler.ActionItem;
import com.wisii.edit.tag.components.action.schema.TagSystemKeys.All;
import com.wisii.edit.tag.components.action.schema.TagSystemKeys.BaseUrl;

/**
 * 类功能描述：用于管理快捷键配置文件中的所有配置信息。
 * 
 * 作者：李晓光 创建日期：2009-9-29
 */
public class KeyManager {
	public enum BindType {
		SingleLineInput(TagSystemKeys.SingleLineInput.class), 
		MultiLineInput(TagSystemKeys.MultiLineInput.class), 
		Select(TagSystemKeys.Select.class), 
		Date(TagSystemKeys.Date.class), 
		Checkbox(TagSystemKeys.Checkbox.class), 
		Float(TagSystemKeys.Float.class),
		MainFrame(TagSystemKeys.MainFrame.class);
		
		private Class<?> clazz = null;
		
		private BindType(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		public Class<?> getType() {
			return this.clazz;
		}
	}
	private static BaseUrl GLOBAL_URL = null;
	private final static String CONFIGURE_FILE = "wdemsKeys.xml";//".\\jsp\\WEB-INF\\classes\\com\\wisii\\edit\\tag\\components\\action\\schema\\wdemsKeys.xml";
	
	private final static TagSystemKeys ALL_ELEMENTS = getElement();
	private final static Map<String, String> BASEURL_MAP = new HashMap<String, String>();
	private final static Collection<ActionItem> ALL_ELEMENT_ITEMS = new HashSet<ActionItem>();
	static {
	
		processBaseUrl();
		ALL_ELEMENT_ITEMS.addAll(buildElementAllItems());
	}

	private static TagSystemKeys getElement()
	{
		try{
		InputStream is=SystemUtil.class.getClassLoader().getResourceAsStream("resource/" + CONFIGURE_FILE);
		if(is==null) System.out.println("请检查"+CONFIGURE_FILE+"是否正确");
		return JAXB.unmarshal(is, TagSystemKeys.class);
		}
		catch(Exception e)
		{
			System.out.println("请检查"+CONFIGURE_FILE+"是否正确");
			return null;
		}
		
	}
	public final static Collection<ActionItem> buildActionItems(BindType type) {
		Collection<ActionItem> items = new HashSet<ActionItem>();
		if(type == null)
			return items;
			
		switch (type) {
		case SingleLineInput:
			items = buildSingleLineItems();
			break;
		case MultiLineInput:
			items = buildMultiLineItems();
			break;
		case Select:

			items = buildSelectItems();
			break;
		case Date:
			items = buildDateItems();
			break;
		case Checkbox:
			items = buildCheckboxItems();
			break;
		case Float:
			items= buildFloatItems();
			break;
		case MainFrame:
			items = buildMainframeItems();
			break;
		default:
			break;
		}
		if(!items.isEmpty() && !ALL_ELEMENT_ITEMS.isEmpty()){
			items.addAll(ALL_ELEMENT_ITEMS);
		}
		return items;
	}

	private final static Collection<ActionItem> buildSingleLineItems() {
		TagSystemKeys.SingleLineInput single = ALL_ELEMENTS.getSingleLineInput();
		if(single == null)
			return new HashSet<ActionItem>();
		return buildActionItems(single.getBaseUrl(), single.getKey());
	}

	private final static Collection<ActionItem> buildMultiLineItems() {
		TagSystemKeys.MultiLineInput multi = ALL_ELEMENTS.getMultiLineInput();
		if(multi == null)
			return new HashSet<ActionItem>();
		return buildActionItems(multi.getBaseUrl(), multi.getKey());
	}

	private final static Collection<ActionItem> buildSelectItems() {
		TagSystemKeys.Select select = ALL_ELEMENTS.getSelect();
//		System.out.println("enter buildSelectItems");
		if(select == null)
			return new HashSet<ActionItem>();
		return buildActionItems(select.getBaseUrl(), select.getKey());
	}

	private final static Collection<ActionItem> buildDateItems() {
		TagSystemKeys.Date date = ALL_ELEMENTS.getDate();
		if(date == null)
			return new HashSet<ActionItem>();
		return buildActionItems(date.getBaseUrl(), date.getKey());
	}

	private final static Collection<ActionItem> buildCheckboxItems() {
		TagSystemKeys.Checkbox box = ALL_ELEMENTS.getCheckbox();
		if(box == null)
			return new HashSet<ActionItem>();
		return buildActionItems(box.getBaseUrl(), box.getKey());
	}
	private final static Collection<ActionItem> buildMainframeItems() {
		TagSystemKeys.MainFrame main = ALL_ELEMENTS.getMainFrame();
		if(main == null)
			return new HashSet<ActionItem>();
		return buildActionItems(main.getBaseUrl(), main.getKey());
	}
	private final static Collection<ActionItem> buildFloatItems() {
		TagSystemKeys.Float $float = ALL_ELEMENTS.getFloat();
		if($float == null)
			return new HashSet<ActionItem>();
		return buildActionItems($float.getBaseUrl(), $float.getKey());
	}

	private final static Collection<ActionItem> buildElementAllItems() {
		All all = ALL_ELEMENTS.getAll();
		if(all == null)
			return new HashSet<ActionItem>();
		return buildActionItems(all.getBaseUrl(), all.getKey());
	}

	private final static ActionItem createItemForKey(Key key, String baseUrl) {
		String stroke = key.getContent();
		String name = key.getName();
		String function = key.getFunction();
		Action action = null;
		try {
			action = WdemsActioinHandler.createAction(baseUrl, function);
		} catch (ClassNotFoundException e) {
			return null;
		}
		return ActionItem.newInstance(stroke, name, action);
	}

	private final static Collection<ActionItem> buildActionItems(String baseUrl, List<Key> keys) {
		Collection<ActionItem> items = new HashSet<ActionItem>();
		ActionItem item = null;
		String url = baseUrl;
		for (Key key : keys) {
			url = getBaseUrl(key.getBaseUrl(), baseUrl);
			item = createItemForKey(key, url);
			if (item == null)
				continue;
			items.add(item);
		}
//		System.out.println("enter buildActionItems");
		return items;
	}

	private final static void processBaseUrl() {
		List<TagSystemKeys.BaseUrl> baseUrls = ALL_ELEMENTS.getBaseUrl();
		for (BaseUrl url : baseUrls) {
			BASEURL_MAP.put(url.getName(), url.getContent());
			if (!url.isGlobal())
				continue;
			if (GLOBAL_URL == null) {
				GLOBAL_URL = url;
			}
		}
	}

	private final static String getBaseUrl(String baseUrl, String parent) {
		baseUrl = (baseUrl == null) ? "" : baseUrl;
		parent = (parent == null) ? "" : parent;
		String temp = baseUrl;
		if (!"".equalsIgnoreCase(baseUrl)){
			temp = getBaseUrlFromMap(baseUrl);
		}else if(!"".equalsIgnoreCase(parent)){
			temp = getBaseUrlFromMap(parent);
		}else if(GLOBAL_URL != null){
				temp = GLOBAL_URL.getContent();
		}else
			StatusbarMessageHelper.output("请正确指定Base-Url", "您指定的base-url: 【" + baseUrl + "】 【" + parent + "】", StatusbarMessageHelper.LEVEL.INFO);

		return temp;
	}

	private final static String getBaseUrlFromMap(String baseUrl) {
		String temp = BASEURL_MAP.get(baseUrl);
		if (temp != null)
			return temp;
		else
			return baseUrl;
	}
}
