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

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.validate.validatexml.XmlValidate;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.fov.command.plugin.FOMethod;
import com.wisii.fov.server.command.AbstractServerCommand;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description: 获得schema对象进行发送
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
 * @author not attributable
 * @version 1.0
 */
public class CMDSchemaObj extends AbstractServerCommand {
	public CMDSchemaObj() {
	}

	public boolean execute(Object out, Object para, Object request) {
		WdemsDateType wdt = new WdemsDateType(out);
		try {
			WisiiBean wb ;
			try{
			wb = ((WisiiBean) ((HttpServletRequest) request)
					.getSession().getAttribute("wisiibean"));
			}
			catch (Exception e)
			{
				StatusbarMessageHelper.output("未得到session的信息:", null , StatusbarMessageHelper.LEVEL.DEBUG);
				wdt.flush(null);
				return false;
			}
			

			String structdefc = null;
			if (wb != null)
				structdefc = wb.getXsdString();

			Map map = XmlValidate.getAllDefinedElementsAndAttribute(structdefc);
			String validate = FOMethod.generateString(map);

			if (validate == null) {
				System.err.println("fail to get the validate information.");
			} else {

				wdt.flush(validate);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return true;
	}
}
