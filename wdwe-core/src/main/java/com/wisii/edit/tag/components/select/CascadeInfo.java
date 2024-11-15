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
 * @CascadeInfo.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类功能描述：用于表示一个数据表. 
 * 作者：李晓光 
 * 创建日期：2009-10-21
 */
public class CascadeInfo {
	private final static transient String regex = "\\w{1,}\\(\\d{1,},\\d{1,}\\)";
	private final static transient Pattern PATTERN = Pattern.compile(regex);
	private String name = "";
	private int previous = -1;
	private int next = -1;
	private boolean valid = Boolean.TRUE;

	public CascadeInfo(String name, int previous, int next) {
		this.name = name;
		this.previous = previous;
		this.next = next;
	}

	/**
	 * 如：city(1,3)
	 * 
	 * @param s
	 */
	public CascadeInfo(String s) {
		process(s);
	}

	public String getName() {
		return name;
	}

	public int getPrevious() {
		return previous;
	}

	public int getNext() {
		return next;
	}

	public boolean isValid() {
		return valid;
	}

	public void process(String s) {
		if (!isValid(s)) {
			 valid = Boolean.FALSE;
			return;
		}
		Pattern p = Pattern.compile("\\w{1,}");
		Matcher m = p.matcher(s);
		if (m.find())
			this.name = m.group();

		p = Pattern.compile("\\d{1,}");
		m = p.matcher(s);
		List<Integer> arr = new ArrayList<Integer>();
		while (m.find()) {
			String temp = m.group();
			arr.add(Integer.parseInt(temp));
		}
		previous = arr.get(arr.size() - 2);
		next = arr.get(arr.size() - 1);
		valid = Boolean.TRUE;
	}

	private boolean isValid(String s) {
		if (s == null || "".equals(s))
			return Boolean.FALSE;
		Matcher m = PATTERN.matcher(s);
		return m.matches();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[name:");
		s.append(name);
		s.append(",previous:");
		s.append(previous);
		s.append(",next:");
		s.append(next);
		s.append("]");

		return s.toString();
	}
}