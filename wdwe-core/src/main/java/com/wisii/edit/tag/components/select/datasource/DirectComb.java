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

import java.util.ArrayList;
import java.util.List;


/**
 * 直接拼接，多张表的时候都接在第一张表的后面
 * 
 * @author liuxiao
 * 
 */
public class DirectComb implements Combine {

	/**
	 * 视图定义如下：
	 * 
	 * CREATE VIEW V_REGION_SALES AS SELECT A1.region_name as REGION,
	 * SUM(A2.Sales) as SALES FROM Geography A1, Store_Information A2 WHERE
	 * A1.store_name = A2.store_name order by id
	 */
	public void combine(List<TableOrViewInfo> tlist, TableOrViewInfo tovi) {

		if (tlist == null || tlist.isEmpty() || tovi == null) {
			return;
		}
		List<List<String>> datas = new ArrayList<List<String>>();

		for (TableOrViewInfo t : tlist) {
			List<List<String>> newdatas = t.getAllData(null, null);
			if (newdatas != null) {
				datas.addAll(newdatas);
			}
		}
		tovi.addDatasByList(datas);
	}

}
