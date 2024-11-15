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

/**
 * 解析获得的id的一个实现
 * @author 闫舒寰
 * @version 1.0 2009/06/15
 */
public class WdemsTagIDImpl implements WdemsTagID {
	
	private String name;
	
	private String xpath;
	
	private String authority;
	private String defaultvalue;
	
	/**
	 * 这里接收的是name(path)这种形式的id
	 * @param id
	 */
	public WdemsTagIDImpl(final String id) {
		this.name = id;
		this.praseID(id.trim());
	}
	
	public String getTagName() {
		return name;
	}

	public String getTagXPath() {
		return xpath;
	}
	
	public String getAuthority() {
		return authority;
	}
	
	
	public String getDefaultvalue() {
		return defaultvalue;
	}

	//由name(xpath,authority)这种形式来解析出name、xpath和authority
	private void praseID(final String id){
		
		int i = 0;
		while (id.charAt(i) != '(') {
			i += 1;
		}
		
		this.name = id.substring(0, i);
		
		StringBuilder temp = new StringBuilder(id.substring(i + 1, id.length()));
		
		int bp = temp.indexOf(",");
		
		if (bp == -1) {
			this.xpath = temp.toString().trim();
		} else {
			this.xpath = temp.substring(0, bp).trim();
			String left=temp.substring(bp + 1, temp.length()).trim();
			int bp2=left.indexOf(',');
			if(bp2==-1)
			{
				this.authority=left;
			}
			else
			{
				this.authority = left.substring(0, bp2).trim();
				this.defaultvalue= left.substring(bp2+1, left.length()).trim();
			}
			
		}
	}
}
