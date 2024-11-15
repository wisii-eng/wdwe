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

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.wisii.edit.tag.action.WdemsAction;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.util.WdemsTagUtil;
import com.wisii.edit.tag.util.WdemsTagUtil.ValidationType;

/**
 * 标签类对象的实现
 * @author 闫舒寰
 * @version 1.0 2009/06/10
 * 
 */
public class WdemsComponentImpl implements WdemsComponent {
	
	//1、获得tagID
	private final WdemsTagID wTagID;
	
	//2、由tagID中的名字获得tagObject
	private final Object tagObject;
	
	//3、由tagObject和tagID一起生成控件组件
	private WdemsTagComponent wTagComponent;

	//4、由1，2，3一起生成控件的动作
	private WdemsAction wAction;
	
	//该组件的关联属性，在各个工厂创建控件的时候会把该控件的关联属性设置到这里，若该属性为空，则该控件没有相关联的其他控件
	private String conn;
	
	//当前控件的验证状态，默认是验证通过（true），这里根据不同的验证类型记录不同验证的不同结果
	private Map<ValidationType, Boolean> vStates;
	
	//当前控件在fo中的id的值
	private String tagID;
	
	public boolean getValidateState() {
		if (vStates == null) {
			return true;
		} else {
			Collection<Boolean> vSet = vStates.values();
			boolean vs = true;
			
			for (Boolean b : vSet) {
				if (!b) {
					vs = false;
				}
			}
			return vs;
		}
	}
	
	public Boolean getValidateState(final ValidationType vType) {
		
		if (vStates == null) {
			return null;
		}
		
		Boolean b = vStates.get(vType);
		
		if (b == null) {
			return null;
		} else {
			return b;
		}
	}

	public void setValidateState(final ValidationType validationType, final boolean validateState) {
		if (vStates == null) {
			vStates = new HashMap<ValidationType, Boolean>();
		}
		vStates.put(validationType, validateState);
	}
	
	public String getTagID() {
		return this.tagID;
	}

	public void setTagID(final String tagID) {
		this.tagID = tagID;
	}

	/**
	 * 初始化WdemsComponet控件
	 * @param tagID
	 * @param tagObj
	 */
	public WdemsComponentImpl(final WdemsTagID tagID, final Object tagObj) {
		this.wTagID = tagID;
		this.tagObject = tagObj;
	}
	
	public String getTagName() {
		return wTagID.getTagName();
	}

	public String getTagXPath() {
		return wTagID.getTagXPath();
	}

	public WdemsAction getWdemsAction() {
		return this.wAction;
	}

	public WdemsTagComponent getWdemsTagComponent() {
		return this.wTagComponent;
	}

	public void setWdemsAction(final WdemsAction wdemsAction) {
		this.wAction = wdemsAction;
		wTagComponent.addActions(wdemsAction.getAction());
	}

	public void setWdemsTagComponent(final WdemsTagComponent wtc) {
		this.wTagComponent = wtc;
	}

	public String getXPath() {
		return wTagID.getTagXPath();
	}

	public Object getTagObject() {
		return tagObject;
	}

	public WdemsTagID getWdemsTagID() {
		return wTagID;
	}

//	public JComponent getWdemsComponent() {
//		return wTagComponent.getComponent();
//	}
	
	public String getConnWith() {
		return this.conn;
	}

	public void setBackIniValue() {
		getWdemsTagComponent().iniValue(WdemsTagUtil.getValue(getTagXPath()));
	}

	public void setConnWith(final String connName) {
		this.conn = connName;
	}

	/*******************以下是为设置控件属性所提供的方法*****************/
	public void setLocation(final Point p) {
		wTagComponent.setLocation(p);
	}
	
	public void setMaximumSize(final Dimension maximumSize) {
		wTagComponent.setMaximumSize(maximumSize);
	}

}
