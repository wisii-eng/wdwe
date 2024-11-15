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


	/**
	 * 该类用于直接的横向拼接 拼接后的列为按照list的顺序横向增长 
	 * 例如 ： 表一为两列： col1，col2，
	 * 表二为三列: col1,col2,col3
	 * 拼接完后为： col1,col2,col3,col4,col5 
	 * 他们的顺序为：表1， 表1， 表2， 表2， 表2
	 * 
	 * 他们都按照插入值的顺序拼接 
	 * 表：K_01 
	 *   a       b 
	 * Name_03 t03 
	 * Name_04 t04 
	 * Name_05 t05 
	 * Name_06 t06
	 * 
	 * 表：K_02
	 *   c         d 
	 *  Name_01 t1.03 
	 *  Name_02 t1.04 
	 *  Name_03 t1.05 
	 *  Name_04 t1.06
	 * 
	 * 拼接后：K_c 
	 * a        b     c      d 
	 * Name_03 t03 Name_01 t1.03 
	 * Name_04 t04 Name_02 t1.04 
	 * Name_05 t05 Name_03 t1.05 
	 * Name_06 t06 Name_04 t1.06
	 * 
	 * @author liuxiao
	 * 
	 */
public class ParallelComb implements Combine {

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
		List<List<String>> datas = tlist.get(0).getAllData(null, null);
		if (datas != null && !datas.isEmpty() && tlist.size() > 0) {
			int size = tlist.size();
			for (int i = 1; i < size; i++) {
				TableOrViewInfo t = tlist.get(i);
				List<List<String>> newdatas = t.getAllData(null, null);
				addDatas(datas, newdatas);
			}
		}
		tovi.addDatasByList(datas);
	}

	private void addDatas(List<List<String>> datas,
			List<List<String>> newdatas) {
		if (newdatas == null || newdatas.isEmpty() ) {
			return;
		}
        
		for (int i=0;i<datas.size();i++) {
			List<String> rowdata=datas.get(i);
			if (rowdata == null || rowdata.isEmpty()) {
				continue;
			}
			if(i>=newdatas.size())
			{
				break;
			}
			rowdata.addAll(newdatas.get(i));
			
		}

	}

}
