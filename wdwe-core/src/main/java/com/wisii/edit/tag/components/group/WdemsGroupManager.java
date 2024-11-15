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
 * @WdemsGroupManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wisii.edit.tag.schema.wdems.Group;

/**
 * 类功能描述：用于统一管理组对象。
 * 
 * 作者：李晓光
 * 创建日期：2009-8-21
 */
public class WdemsGroupManager {
	private final static Map<GroupKey, SelectGroup> groups = new ConcurrentHashMap<GroupKey, SelectGroup>();
	private WdemsGroupManager(){}
	
	public final static SelectGroup getGroup(GroupKey key, Group group){
		if(group == null)
			return null;
		if(groups.containsKey(key))
			return groups.get(key);
		SelectGroup value = new WdemsGroup(group);
		groups.put(key, value);
		return value;
	}
	public final static void cleanDump(){
		for (SelectGroup group : groups.values()) {
			group.cleanDump();
		}
	}
	public static class GroupKey {
		private String name = "";
		private String xpath = "";
		public GroupKey(){
			this("");
		}
		public GroupKey(String name){
			this(name, "");
		}
		public GroupKey(String name, String xpath){
			setName(name);
			setXpath(xpath);
		}
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public String getXpath() {
			return xpath;
		}
		public void setXpath(String xpath) {
			this.xpath = xpath;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GroupKey other = (GroupKey) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (xpath == null) {
				if (other.xpath != null)
					return false;
			} else if (!xpath.equals(other.xpath))
				return false;
			return true;
		}
	}
}
