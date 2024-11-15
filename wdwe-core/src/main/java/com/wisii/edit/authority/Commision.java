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
 * @Commision.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.authority;
import com.wisii.edit.cache.database.hsql.bo.AuthorityBO;
import com.wisii.fov.apps.FOUserAgent;
/**
 * 类功能描述：
 *
 * 作者：p.x
 * 创建日期：2009-6-15
 */
public class Commision {
	public static boolean isCommision(String authorityName,
			FOUserAgent useragent) {
		AuthorityBO authority = AuthorityHelper.getAuthority();
		if (authority != null) {
			String authstr = authority.getAuthority();
			if ("all".equals(authstr)) {
				return true;
			} else if (authstr == null || authstr.isEmpty()) {
				return false;
			} else if ("true".equals(authstr)) {
				String ss = authority.getComponents();
				if (ss != null) {
					String[] components = ss.split(",");
					for (int i = 0; i < components.length; i++) {
						if (components[i].equals(authorityName)) {
							return true;
						}
					}
					return false;
				} else {
					return false;
				}
			} else if ("false".equals(authstr)) {
				String ss = authority.getComponents();
				if (ss != null) {
					String[] components = ss.split(",");
					for (int i = 0; i < components.length; i++) {
						if (components[i].equals(authorityName)) {
							return false;
						}
					}
					return true;
				} else {
					return true;
				}
			}
		}
		return false;
	}
}
