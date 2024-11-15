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
 * @WdemsCascadeManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import com.wisii.edit.tag.components.select.AbstractWdemsCombox;

/**
 * 类功能描述：维护用于级联的所用控件
 * 
 * 作者：李晓光
 * 创建日期：2009-10-21
 */
public class WdemsCascadeManager {
	private final static Map<String, JComponent> map = new HashMap<String, JComponent>();
	
	public final static void registerComponents(JComponent... comps){
		for (JComponent c : comps) {
			if(filter(c))
				continue;
			map.put(c.getName(), c);
		}
	}
	public final static void registerComponents(Collection<JComponent> comps){
		for (JComponent c : comps) {
			if(filter(c))
				continue;
			map.put(c.getName(), c);
		}
	}
	public final static <T extends JComponent> T getComponent(String name, Class<T> clazz){
		if(!map.containsKey(name))
			return null;
		JComponent c = map.get(name);
		if(clazz.isAssignableFrom(c.getClass()))
			return clazz.cast(c);
		return null;
	}
	public final static void removeComponent(JComponent comp){
		map.remove(comp.getName());
	}
	private final static boolean filter(JComponent c){
		return !(c instanceof AbstractWdemsCombox);
	}
	public final static void clearDump(){
		map.clear();
	}
	public final static int size(){
		return map.size();
	}
}
