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
 * @DataTable.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 类功能描述：用于表示一个数据表. 
 * 作者：李晓光 创建日期：2009-7-1
 */
public class DataTable extends WdemsData<DataRow> implements Data<DataRow> {
	public DataTable(){}
	public DataTable(List<DataRow> rows){
		super(rows);
	}

	/**
	 * 类功能描述：用于表示一个数据表中的一行中一个单元格数据. 
	 */
	public static class DataCell<T> {
		/* 类型 */
		private String type = "";
		/* 列名称 */
		private String name = "";
		/* 代表的值 */
		private T value = null;
		/* 是否为主键 */
		private Boolean primaryKey = Boolean.FALSE;
		public DataCell(){}
		public DataCell(T value){
			setValue(value);
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public T getValue() {
			return (T)value;
		}
		public void setValue(T value) {
			this.value = value;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		/**
		 * @return the primaryKey
		 */
		public Boolean getPrimaryKey() {
			return primaryKey;
		}
		/**
		 * @param primaryKey the primaryKey to set
		 */
		public void setPrimaryKey(Boolean primaryKey) {
			this.primaryKey = primaryKey;
		}
		@Override
		public String toString() {
			StringBuffer s = new StringBuffer("[");
			s.append("type:");
			s.append(getType());
			s.append(",name:");
			s.append(getName());
			s.append(",value:");
			s.append(getValue());
			s.append("]");
			return s.toString();
		}
	}
	/**
	 * 列索引从1开始
	 */
	public Object getObject(int column) {
		return getObject(1, column);
	}
	/**
	 * 列索引从1开始
	 * 行索引从1开始
	 */
	public Object getObject(int row, int column) {
		if(row <= 0)
			return null;
		DataRow r = getData(row - 1);
		if(r == null || column <= 0 || column > r.getSize())return null;
		
		return r.getData(column - 1).getValue();
	}
	/**
	 * 起始索引1
	 */
	public Collection<DataRow> getCellsOf(int... indexes) {
		List<DataRow> result = new ArrayList<DataRow>();
		if(indexes == null || indexes.length == 0)
			return result;
		indexes = updateIndex(indexes);
		List<DataRow> rows = getDatas();
		for (DataRow row : rows) {
			DataRow r = new DataRow();
			r.addDatas(row.getDatas(indexes));
			result.add(r);
		}
		return rows;
	}
	private int[] updateIndex(int...indexes){
		int count = 0;
		for (int i : indexes) {
			indexes[count++] = --i;
		}
		return indexes;
	}
}
