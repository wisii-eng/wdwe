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
 * SwingDataSource.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.tag.components.select.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.itown.rcp.portal.basicdata.BasicDataQueryUtils;
//import com.itown.rcp.portal.basicdata.BasicDataTable;
//import com.itown.rcp.portal.basicdata.BasicDataType;
import com.wisii.edit.tag.schema.wdems.Swingdatasource;
/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-14
 */
public class SwingDataSource {
	private Swingdatasource datasource;
	private String rootcode;
	public SwingDataSource(Swingdatasource datasource) {
		this.datasource = datasource;
	}

	public String[] getColumns() {
		String columns=datasource.getColumns();
		if(columns!=null&&!columns.isEmpty())
		{
			return columns.split(",");
		}
		return null;

	}

	/*
	 * 获得总条数，search，为空时表示无查询条件
	 */
	public int getDataCount(String search) {
		Map param = getDefaultPara();
		if (search != null && !search.isEmpty()) {
			param.put("condition-code", search);
			param.put("condition-name", search);
		}
		return 0;
//		return (int) BasicDataQueryUtils.getRowCount(param);
	}

	/*
	 * 获得指定类的行数据，columnindexs为null为获取所有列的数据 pagenumber页号（1开始）,pagecount:每页的大小。
	 * search为空时表示无查询条件
	 */
	public List<List<String>> getData(List<Integer> columnindexs,
			int pagenumber, int pagecount, String search) {
//		List<List<String>> list=new ArrayList<List<String>>();
//		for(int i=0;i<pagecount;i++)
//		{
//			List<String> rows=new ArrayList<String>();
//			rows.add("字段1-"+pagenumber+":"+i);
//			rows.add("字段2-"+pagenumber+":"+i);
//			rows.add("字段3-"+pagenumber+":"+i);
//			rows.add("字段4-"+pagenumber+":"+i);
//			rows.add("字段5-"+pagenumber+":"+i);
//			rows.add("字段6-"+pagenumber+":"+i);
//			list.add(rows);
//		}
//		return list;

		Map param = getDefaultPara();
		if (pagenumber > 0) {
			param.put("page-number", "" + pagenumber);
		}
		if (pagecount > 0) {
			param.put("page-count", "" + pagecount);
		}
		if (search != null && !search.isEmpty()) {
			param.put("condition-code", search);
			param.put("condition-name", search);
		}
		return null;
//		BasicDataTable datatable = BasicDataQueryUtils.getBasicDatas(param);
//		if (datatable == null) {
//			return null;
//		} else {
//			rootcode = datatable.getRootCode();
//			List<Map<String, Object>> datas = datatable.getData();
//			getColumns(columnindexs);
//			String[] columns = getColumns(columnindexs);
//			List<List<String>> rowdatas = new ArrayList<List<String>>();
//			for (Map<String, Object> rowdata : datas) {
//				List<String> row = new ArrayList<String>();
//				for (String column : columns) {
//					Object cell = rowdata.get(column);
//					String cellstr = "";
//					if (cell != null) {
//						cellstr = cell.toString();
//					}
//					row.add(cellstr);
//				}
//				rowdatas.add(row);
//			}
//			return rowdatas;
//		}

	}

	private String[] getColumns(List<Integer> columnindexs) {
		String[] columns = getColumns();
		if (columnindexs != null && !columnindexs.isEmpty()) {
			String[] selcolumns = new String[columnindexs.size()];
			int i = 0;
			for (Integer columnindex : columnindexs) {
				selcolumns[i++] = columns[columnindex];
			}
			columns = selcolumns;
		}
		return columns;
	}

	private Map getDefaultPara() {
		Map param = new HashMap();
//		String type = datasource.getType();
//		String callback = datasource.getCallbackclass();
//		BasicDataType bdtype = BasicDataType.table;
//		param.put("basicdata-param", datasource.getDataname());
//		if (callback != null && !callback.isEmpty()) {
//			bdtype = BasicDataType.callback;
//			param.put("basicdata-param", callback);
//		} else if (type != null && type.equalsIgnoreCase("tree")) {
//			bdtype = BasicDataType.tree;
//		}
//		param.put("basicdata-type", bdtype);
		return param;
	}

	public String getRootcode() {
		return rootcode;
	}
    public String getStruts()
    {
    	return datasource.getType();
    }
}
  