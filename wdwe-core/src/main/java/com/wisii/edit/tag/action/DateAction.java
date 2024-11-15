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
 */package com.wisii.edit.tag.action;

import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.wisii.edit.tag.util.WdemsTagUtil;

/**
 * 日期时间动作
 * @author 闫舒寰
 * @version 1.0 2009/08/10
 */
public class DateAction extends Actions {

	@Override
	public Object doAction(final ActionEvent e) {
		Object value = getTagComponent().getValue();
		return value;
	}

	@Override
	public boolean updateXML() {
		return WdemsTagUtil.updateXML(getXPath(), getValue());
	}
	
	/**
	 * 复写了父类中的getValue方法，直接返回数据库中的date的格式的日期时间
	 */
	@Override
	public Object getValue() {
		Object dateTage = getTagObject();

		String dataFormat = null;

		if (dateTage instanceof com.wisii.edit.tag.schema.wdems.Date) {
			com.wisii.edit.tag.schema.wdems.Date wd = (com.wisii.edit.tag.schema.wdems.Date) dateTage;
			dataFormat = wd.getDataFormat();
		}

		if (dataFormat != null) {
			DateFormat df = new SimpleDateFormat(dataFormat);

			Object value = getTagComponent().getValue();

			return df.format(value);
		} else {
			return null;
		}
	}
}
