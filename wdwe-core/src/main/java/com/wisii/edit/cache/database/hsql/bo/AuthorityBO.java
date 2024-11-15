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
 * @AuthorityBO.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.cache.database.hsql.bo;

/**
 * 类功能描述：
 *
 * 作者：p.x
 * 创建日期：2009-7-7
 */
public class AuthorityBO {
	private String edittempid;
	private String authorityid;
	private String authority;
	private String components;
	
	public String getEdittempid() {
		return edittempid;
	}
	public void setEdittempid(String edittempid) {
		this.edittempid = edittempid;
	}
	public String getAuthorityid() {
		return authorityid;
	}
	public void setAuthorityid(String authorityid) {
		this.authorityid = authorityid;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getComponents() {
		return components;
	}
	public void setComponents(String components) {
		this.components = components;
	}
	
}