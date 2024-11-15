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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import com.wisii.fov.server.command.InnerServerCommand;

/**
 * <p>Title: </p>
 *
 * <p>Description: 返回页数给集成商</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CMDRtrnPage2Oth implements InnerServerCommand
{
    public CMDRtrnPage2Oth()
    {
    }

    /**
     * execute
     *
     * @param out JspWriter
     * @param request HttpServletRequest
     * @return String
     * @todo Implement this com.wisii.fov.server.command.InnerServerCommand
     *   method
     */
    public String execute(JspWriter out, HttpServletRequest request)
    {
        return "";
    }

	public boolean execute(Object out, Object para, Object request) {
		// TODO Auto-generated method stub
		return false;
	}
}
