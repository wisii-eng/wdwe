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
 */package com.wisii.edit.tag.factories;

import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.datatime.WdemsDateTimeField;
import com.wisii.edit.tag.components.datatime.WdemsDateTimeSpinner;
import com.wisii.edit.tag.schema.wdems.Date;
import com.wisii.fov.render.awt.viewer.date.JDatePicker;
/**
 * 产生日期和时间控件的工厂类
 * @author 闫舒寰
 * @version 1.0 2009/07/09
 */
public enum DateFactory implements TagFactory  {
	
	Instance;
	
	public WdemsTagComponent makeComponent(final WdemsComponent wc) {
		
		final Object tagObject = wc.getTagObject();
		
		Date date = null;
		
		if (!(tagObject instanceof Date)) {
			return null;
		}
		date = (Date) tagObject;
		
		
		
		String type = date.getType();
		WdemsTagComponent wtc = null;
		String format=fomateHandle(wc.getTagXPath(),date.getDataFormat());
		
		if("c".equalsIgnoreCase(type)){
			//选择型
			//TODO 初始化处理
			wtc = new JDatePicker(date.getFormat());
			((JDatePicker)wtc).setDateFormat(format);
		}else if("t".equalsIgnoreCase(type)){
			//手动输入
			wtc = new WdemsDateTimeSpinner(date.getFormat());
			((WdemsDateTimeSpinner)wtc).setLenient(Boolean.FALSE);
			((WdemsDateTimeSpinner)wtc).setDateFormat(format);
		}else{
			wtc = new WdemsDateTimeField(date.getFormat());
			/*((WdemsDateTimeField)wtc).setOverwriteMode(Boolean.TRUE);*/
			/*((WdemsDateTimeField)wtc).setAllowsInvalid(Boolean.FALSE);*/
			((WdemsDateTimeField)wtc).setLenient(Boolean.FALSE);
			((WdemsDateTimeField)wtc).setDateFormat(format);
		}
		return wtc;
	}
	
	// TODO: 该方法暂时写在这个地方，以后要与工厂方法分离成两个类
	//TODO :用于处理date的初始化逻辑
	/**
	 * 用于处理date的初始化逻辑
	 * @param date
	 */
	private String  fomateHandle(String xpath ,String  format){
		
		if (format==null) format="yyyy-MM-dd'T'HH:mm:ss";
		return format;
	}
//	public static void main(String []args)
//	{
//		Date date = new Date();
//		DateFormat dateFormat =new SimpleDateFormat("");
//		   System.out.println(dateFormat.format(date));
//		
//		  
//
//	}

}
