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

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.server.command.AbstractServerCommand;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CMDClearSession extends AbstractServerCommand
{
    public CMDClearSession()
    {
    }

    public String execute(JspWriter out, HttpServletRequest request)
    {
        Class util = SystemUtil.class;
        Field[] field = util.getFields();
        for(int i = 0; i < field.length; i++)
        {
            if(field[i].getName().startsWith("SES_"))
            {
                request.getSession().removeAttribute(field[i].getName());
            }

        }
        return "";
    }
}
