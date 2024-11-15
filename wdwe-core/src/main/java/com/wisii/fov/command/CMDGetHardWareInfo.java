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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.fov.command.plugin.FOMethod;
import com.wisii.fov.server.command.AbstractServerCommand;
import com.wisii.fov.util.HardWareInfoGetter;

public class CMDGetHardWareInfo extends AbstractServerCommand {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CMDGetHardWareInfo().execute(null, null, null);

	}
	public boolean execute(Object out, Object para, Object request) {
		
		try {
		WdemsDateType wdt = new WdemsDateType(out);
				String a=HardWareInfoGetter.getDiskID();
				String b=HardWareInfoGetter.getMac();
				String c=HardWareInfoGetter.getProcessorid();
				// 发送数据
				Map <String ,String>  map=new HashMap<String ,String> ();
				map.put("a", a);
				map.put("b", b);
				map.put("c", c);
			
					wdt.flush(FOMethod.generateString(map));
				} catch (IOException e) {
					
					e.printStackTrace();
				}
		return true;
	}

}
