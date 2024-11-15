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

import java.util.ArrayList;
import java.util.List;

/**
 * 这里主要负责解析从FOAreaTree中带过来的id。
 * @author 闫舒寰
 * @version 1.0 2009/06/11
 */
public enum WdemsTagIDFactory {
	
	Instance;
	
	private final List<WdemsTagID> wdemsTagIDs = new ArrayList<WdemsTagID>();
	
	/**
	 * 解析传进来的编辑标签的id
	 * @param id
	 * @return
	 */
	public List<WdemsTagID> parseWdemsTagID(final String id){
		parseTagId(id);
		return wdemsTagIDs;
	}
	
	private static final String startTag = "wdems-start:";
	private static final String endTag = ":wdems-end";
	
	private void parseTagId(final String id) {
		
		StringBuilder sb = new StringBuilder(id);
		
		int first = sb.indexOf(startTag);
		int last = sb.indexOf(endTag, first);
		
		CharSequence info = sb.subSequence(first + startTag.length(), last);

		List<Integer> point = new ArrayList<Integer>();

		for (int i = 0; i < info.length(); i++) {
			if (info.charAt(i) == ')') {
				point.add(i);
			}
		}
		
		wdemsTagIDs.clear();
		
		for (int i = 0; i < point.size(); i++) {
			if (i == 0) {
				wdemsTagIDs.add(new WdemsTagIDImpl((info.subSequence(0, point.get(i))).toString()));
			} else {
				//原来是+1，但是+1后代有","，现在改成+2
				wdemsTagIDs.add(new WdemsTagIDImpl((info.subSequence(point.get(i - 1) + 2, point.get(i))).toString()));
			}
		}
	}
}
