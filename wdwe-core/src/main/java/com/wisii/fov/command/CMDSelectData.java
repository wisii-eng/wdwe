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
 */package com.wisii.fov.command;

import java.util.HashMap;
import java.util.Map;

import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.edit.tag.components.select.datasource.SelectDataFactory;
import com.wisii.fov.command.plugin.FOMethod;
import com.wisii.fov.server.command.AbstractServerCommand;

public class CMDSelectData extends AbstractServerCommand {

	/**
	 * execute
	 * 
	 * @param out
	 *            JspWriter
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 * @todo Implement this com.wisii.fov.server.command.InnerServerCommand
	 *       method
	 */
	public boolean execute(Object out, Object para, Object request) {

		try {
			// 初始化流
//System.out.println("CMDSelectData---execute");
			WdemsDateType wdt = new WdemsDateType(out);
			// 得到参数
			String translaturl = (String) para;
			// 读取完数据
			String ss = SelectDataFactory.create(translaturl).getDataSource(
					translaturl);
			// 发送数据
			Map map=new HashMap ();
			map.put("select", ss);
			
			wdt.flush(FOMethod.generateString(map));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

}
