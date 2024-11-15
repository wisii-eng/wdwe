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
 * 该类用于等值的横向拼接 拼接后的列为按照list的顺序横向增长 
 * 例如 ： 表一为两列： col1，col2，
 * 表二为三列: col1,col2,col3
 * 拼接完后为： col1,col2,col3,col4,col5 
 * 他们的顺序为：表1， 表1， 表2， 表2， 表2
 * 
 * 按值的拼接为：所有值之间按顺序对应相等 
 * 例如： 
 * 		表a为： col1，col2，col3 , valueNm=1，2 
 * 		表b为： col1，col2，col3 , valueNm=2，3 
 * 在拼接的时候为 a.col1=b.col2 and a.col2=b.col3 
 * 
 * 多个表的时候就都跟第1个表进行拼接
 * 
 * @author liuxiao
 * 
 */
public class ValueComb implements Combine {

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
		List<Integer> valuenames = getInts(tlist.get(0).getValueNm());
		if (datas != null && !datas.isEmpty() && tlist.size() > 0
				&& valuenames != null && valuenames.size() > 0) {
			int size = tlist.size();
			for (int i = 1; i < size; i++) {
				TableOrViewInfo t = tlist.get(i);
				List<List<String>> newdatas = t.getAllData(null, null);
				addDatas(datas, newdatas, valuenames, getInts(t.getValueNm()));
			}
		}
		tovi.addDatasByList(datas);
	}

	private void addDatas(List<List<String>> datas,
			List<List<String>> newdatas, List<Integer> valuenames,
			List<Integer> newvaluenames) {
		if (newdatas == null || newdatas.isEmpty() || newvaluenames == null
				|| newvaluenames.size() != valuenames.size()) {
			return;
		}

		for (List<String> rowdata : datas) {
			if (rowdata == null || rowdata.isEmpty()) {
				continue;
			}
			for (List<String> nrowdata : newdatas) {
				if (nrowdata == null || nrowdata.isEmpty()) {
					continue;
				}
				if (isequal(rowdata, nrowdata, valuenames)) {
					rowdata.addAll(nrowdata);
					break;
				}
			}
		}

	}

	private boolean isequal(List<String> rowdata, List<String> nrowdata,
			List<Integer> valuenames) {
		for (Integer valuename : valuenames) {
			   if(valuename<1||valuename>rowdata.size()||valuename>nrowdata.size())
			   {
				   return false;
			   }
				String v1 = rowdata.get(valuename-1);
				String v2 = nrowdata.get(valuename-1);
				if ((v1 == null && v2 != null) || !v1.equals(v2)) {
					return false;
				}
		}
		return true;
	}

	private List<Integer> getInts(String[] valuenames) {
		if (valuenames == null || valuenames.length == 0) {
			return null;
		}
		List<Integer> vns = new ArrayList<Integer>();
		for (String valuename : valuenames) {
			try {
				Integer vb = Integer.parseInt(valuename);
				vns.add(vb);
			} catch (Exception e) {

			}
		}
		return vns;
	}

}
