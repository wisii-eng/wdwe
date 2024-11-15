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
 */package com.wisii.fov.server.command;

import com.wisii.component.startUp.SystemUtil;

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
public class CommandHelp
{
    public CommandHelp()
    {
    }
    public static AbstractServerCommand getCommand(String serverType) throws Exception
    {
    	AbstractServerCommand command= null;

        try
        {
            command = (AbstractServerCommand)Class.forName(SystemUtil.CLASSPATH + serverType).newInstance();
        }
        catch(Exception ex)
        {
            throw new Exception("服务类型"+serverType+"错误！");
        }
        return command;
    }
}
