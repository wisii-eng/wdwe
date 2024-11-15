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
 */package com.wisii.edit.tag.components.select.datasource;


/**
 * 该类为服务器端类，处理在服务端的读取下拉列表数据的请求
 * @author liuxiao
 *
 */
public class SelectDataFactory {
/**
 * 工厂类的工厂方法
 * 
 * @param trabsurl
 */
	public static WisSelectDataInterface create(String trabsurl)
	{
		if(trabsurl==null)return null;
		WisSelectDataInterface inte= null;

        try
        {
//        	System.out.println("WisSelectDataInterface---create 1");
        	inte = (WisSelectDataInterface)Class.forName(trabsurl).newInstance();
        	
        }
        catch(Exception ex)
        {
//        	 System.out.println("WisSelectDataInterface---create 2");
          inte= new FileSelectData();
         
        }
        return inte;

		
	}
}
