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
 */package com.wisii.component.mainFramework.commun;

/**
 * 该类用于生产连接协议
 * 
 * @author liuxiao
 * 
 */
public class CommincateFactory {
	/**applet的服务根路径*/
	public static String serverUrl;
	public static final String requestUrl="/wisii/jsp/CommServer.jsp"; 
	/**
	 * 该方法用于产生
	 * @param serverUrl
	 * @return
	 */
	public static CommunicateProxy makeComm(String serverUrl) {
		// System.out.println("serverUrl = "+serverUrl);
		if (serverUrl != null && !serverUrl.isEmpty()
				&& serverUrl.startsWith("http")) {
			return new HttpCommunicate(serverUrl);
		} else
			return new CommCommunicate();
	}
	public static CommunicateProxy makeSelectDataComm(String serverUrl) {
		// System.out.println("serverUrl = "+serverUrl);
		if (serverUrl != null && !serverUrl.isEmpty()
				&& serverUrl.startsWith("http")) {
			return new HttpCommunicate(serverUrl);
		} else
			return new FileCommunicate(serverUrl);
	}
}
