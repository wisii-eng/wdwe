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

import java.util.List;

import org.w3c.dom.Element;
/**
 * 该接口直接用于解析xml数据逻辑
 * @author liuxiao
 *
 */
public interface ParseData {
	/*一次写数据库的条数*/
	public static final int ITEMS_WRITE_DB_ONCE=100;
	public void parse(List<Element> e,String root,TableOrViewInfo tovi);
	public void parseTempData(String data ,String root,TableOrViewInfo tovi);

	
}
