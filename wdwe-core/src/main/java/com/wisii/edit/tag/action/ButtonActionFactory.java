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
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.schema.wdems.Button;

/**
 * 按钮动作工厂类
 * 
 * @author 闫舒寰
 * @version 1.0 2009/08/05
 */
public enum ButtonActionFactory implements WdemsActionFactory {

	Instance;

	private static final String ADD_BUTTON_TYPE = "add";
	private static final String DELETE_BUTTON_TYPE = "delete";
	private static final String HIDDEN_BUTTON_TYPE = "hidden";
	private static final String CONN_BUTTON_TYPE = "conn";
	// add by 李晓光 2010-8-3
	private static final String COMMON_BUTTON_TYPE = "common";
	
	static final String INSERT_BEFORE="before";
	static final String INSERT_AFTER="after";

	/**
	 * 该方法生产按钮的action 如果标签中的type为空，则该方法返回null
	 * 
	 * @author liuxiao
	 * @param wc
	 *            传入的标签对象
	 * @return 按钮的action
	 */
	public Actions makeAction(final WdemsComponent wc) {

		final Button bt = (Button) wc.getTagObject();

		final String type = bt.getType();
		if (type == null || type.isEmpty())
			return null;
		if (ADD_BUTTON_TYPE.equalsIgnoreCase(type)) {
			return new AddButtonAction();
		}
		if (DELETE_BUTTON_TYPE.equalsIgnoreCase(type)) {
			return new DeleteButtonAction();
		}
		if (HIDDEN_BUTTON_TYPE.equalsIgnoreCase(type)) {
			return new HiddenButtonAction();
		}
		// add by 李晓光  2010-8-3
		if(COMMON_BUTTON_TYPE.equalsIgnoreCase(type)){
			return new CommonAction(bt.getOnClick());
		}
		if (CONN_BUTTON_TYPE.equalsIgnoreCase(type)) {
			return new Actions() {
				
				@Override
				public boolean updateXML() {
					// TODO Auto-generated method stub
					return true;
				}
				
				@Override
				public Object doAction(final ActionEvent e) {
					return new Object();
//					fireConnection();
//					return null;
				}
			};
		}
		
		return null;

	}
	@SuppressWarnings("serial")
	private class CommonAction extends Actions{
		String clazz = "";
		String method = "";
		CommonAction(final String clazz){
			this.clazz = clazz;
		}
		@Override
		public Object doAction(final ActionEvent e) {
			final Object object = getInstance();
			Object ac=null;
			if(object instanceof CommButtonPerformed){
				final CommButtonPerformed listener =(CommButtonPerformed) object;
				ac=listener.actionPerformed(e);
			}
			return ac;
		}

		@Override
		public boolean updateXML() {
			return Boolean.TRUE;
		}
		private Method getMethod(final Object object){
			if(object == null){
				return null;
			}
			try {
				final Method method = object.getClass().getDeclaredMethod(this.method, ActionListener.class);
				return method;
			} catch (final Exception e) {
				return null;
			}
		}
		private Object getInstance(){
			if(clazz == null || clazz.isEmpty()){
				return null;
			}
			try {
				final Class cl = Class.forName(this.clazz);
				
				return cl.newInstance();
			} catch (final Exception e) {
				return null;
			}
		}
	}
}
