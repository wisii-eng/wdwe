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
 */package com.wisii.edit.validator;

import java.net.URL;
import java.util.ResourceBundle;

import com.wisii.component.startUp.SystemUtil;


public class Resources {
	  
	public static final String VALIDATOR_BUNDLE_NAME="validatorInfo";
	public static final ResourceBundle res = ResourceBundle.getBundle("resource/"+Resources.VALIDATOR_BUNDLE_NAME);
	public static final String MSG_REPLACEMANT="\\[arg\\]";
	
	
	public static String getMessage(String key)
	{
		if(key!=null)
		{
		return res.getString(key);
		}
		return null;
	}
	public static void main (String []args)
	{

		URL url = SystemUtil.class.getClassLoader().getResource(
				Resources.VALIDATOR_BUNDLE_NAME+".properties");
		System.out.println( url.getPath());
		getMessage(null);
		
	}
	

}
