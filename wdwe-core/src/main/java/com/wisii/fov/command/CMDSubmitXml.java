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

import java.util.Map;

import com.wisii.component.datainterface.SubmitInterface;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.fov.server.command.AbstractServerCommand;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description: 将xmlc数据提交给集成商
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author LIUXIAO
 * @version 1.0
 */
public class CMDSubmitXml extends AbstractServerCommand {
	SubmitInterface sb;
	
	public CMDSubmitXml() {
		String s=SystemUtil.getConfByName("interface.submitinterface");
		if(s!=null&&!"".equalsIgnoreCase(s))
		{
			try {
				sb= (SubmitInterface)Class.forName(s).newInstance();
			} catch (InstantiationException e) {
				StatusbarMessageHelper.output(s+"实例化错误", e.getMessage(),StatusbarMessageHelper.LEVEL.INFO );
			
			} catch (IllegalAccessException e) {
				StatusbarMessageHelper.output(s+"非法访问", e.getMessage(),StatusbarMessageHelper.LEVEL.INFO );
				
			} catch (ClassNotFoundException e) {
				StatusbarMessageHelper.output(s+"不存在", e.getMessage(),StatusbarMessageHelper.LEVEL.INFO );
				
			}
		}
	}

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
		
		WdemsDateType wdt = new WdemsDateType(out);
		if(sb==null) {wdt.flush("interface.submitinterface中配置的类不存在");  return false;}
		
		String sa=(String)((Map)para).get("xml");
		
		String s=sb.submit(sa,(Map)para);
		if(s==null||"".equalsIgnoreCase(s))
		{
			
			wdt.flush(s); 
			return false;
		}
		
		return true;
		
	}
}
