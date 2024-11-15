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
 */package com.wisii.fov.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KeyArray {

	private List keysSque;

	private Map conversion;

	public KeyArray() {
		keysSque = new ArrayList();

		conversion = new HashMap();
	}

	public KeyArray(Map map) {
		keysSque = new ArrayList();

		conversion = new HashMap();
		Iterator itertor = map.keySet().iterator();
		while (itertor.hasNext()) {
			String key = (String) itertor.next();
			String value = (String) map.get(key);
			add(key,value);
		}
	}
	public void add(Object key, Object value) {
		if (conversion != null) {
			conversion = new HashMap();
		}
		if (keysSque != null) {
			keysSque = new ArrayList();
		}
		if (conversion.get(key) == null) {
			keysSque.add(key);
			conversion.put(key, value);
		} else {
			conversion.put(key, value);
		}

	}
	public int size()
	{
		return keysSque.size();
	}
	public Object get (Object key)
	{
		return conversion.get(key);
	}
	public Map getMap()
	{
		return conversion;
	}

}
