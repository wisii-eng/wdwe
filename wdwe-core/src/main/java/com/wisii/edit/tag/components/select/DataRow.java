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
 *//**
 * @DataRow.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.util.ArrayList;
import java.util.List;

import com.wisii.edit.tag.components.select.DataTable.DataCell;

/**
 * 类功能描述：用于表示一个数据表中的一行数据.
 *  
 * 作者：李晓光 创建日期：2009-7-1
 */
@SuppressWarnings("unchecked")
public class DataRow extends WdemsData<DataCell> {
	public DataRow() {

	}
	public DataRow(List<DataCell> column) {
		super(column);
	}
	public void addCells(List<Object> datas){
		List<DataCell> cells = new ArrayList<DataCell>();
		for (Object value : datas) {
			if(value instanceof DataCell)
				cells.add((DataCell)value);
			else if(value != null)
				cells.add(new DataCell(value));
		}
		super.addDatas(cells);
	}
}
