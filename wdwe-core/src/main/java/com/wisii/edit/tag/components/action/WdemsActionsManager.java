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
 * @WdemsActionsManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.action;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

/**
 * 类功能描述：用于管理和维护按钮、快捷键用Action。
 * 
 * 作者：李晓光
 * 创建日期：2009-9-27
 */
public class WdemsActionsManager {
	private final static Map<String, Action> actions = new HashMap<String, Action>();
	private WdemsActionsManager(){}
	
	static{
		initialize();
	}
	private final static void initialize(){
		
	}
	public final static void addAction(String key, Action action){
		actions.put(key, action);
	}
	public final static void addActions(Map<String, Action> map){
		actions.putAll(map);
	}
	public final static Action getAction(String key){
		return actions.get(key);
	}
	public final static int getSize(){
		return actions.size();
	}
	public final static void clear(){
		if(actions.isEmpty())
			return;
		actions.clear();
	}
	public final static boolean isEmpty(){
		return actions.isEmpty();
	}
}
