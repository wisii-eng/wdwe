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
 * 
 */
package com.wisii.edit.tag.components.select.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.wisii.edit.tag.components.select.CascadeInfo;

/**
 * 该类用于收集创建表或者视图的信息
 * 
 * @author liuxiao
 * 
 */
public class TableOrViewInfo {
	/* 列名前缀 */
	public static final String COLUMN_NAME_FIRST = "col";
	/* 搜索关键列的列名 */
	public static final String COLUMN_searchKey = "searchkey";
	/* 数据源名称同样也是表名 */
	private String name;
	/* 数据源值为其中的第几列 */
	private String[] valueNm;

	/* 数据源结构 */
	private String struts;
	/* 搜索关键字 为数据单独增加一列 */
	private String searchKey;
	/* 表头 */
	private String[] tablehead;
	/* 列类型 */
	private String[] columnType;
	/* 列字符串 */
	private String columnString;
	/* 列明按顺序对应的属性名 */
	private String[] columbyattr;
	private int datacount = 0;
	// 超过一定条数的下拉列表数据，将会被缓存在文件中
	private static int maxneicuncount = 100;
	// 超过上面的条数时，数据将存储在该文件中
	private File datafile;
	// 否则，直接放到数组中
	private List<List<String>> datas;

	/**
	 * 得到的列名称不包含在系统内部的id(内部用，不对外)
	 * 
	 * @return the columnString
	 */
	public String getColumnString() {
		return columnString;
	}

	/**
	 * @return the columnType
	 */
	public String[] getColumnType() {
		return columnType;
	}

	/**
	 * @param columnType
	 *            the columnType to set
	 */
	public void setColumnType(String[] columnType) {
		this.columnType = columnType;
	}

	/**
	 * @param tablehead
	 *            the tablehead to set
	 */
	public void setTablehead(String[] tablehead) {
		this.tablehead = tablehead;
	}

	/**
	 * @param tablehead
	 *            the tablehead to set
	 */
	public String[] getTablehead() {
		return tablehead;
	}

	public TableOrViewInfo(String name, String valueNm, String struts) {
		this.name = name;
		setValueNm(valueNm);
		this.struts = struts;
	}

	public TableOrViewInfo(String name, String valueNm, String struts,
			String searchKey) {
		this.name = name;
		setValueNm(valueNm);
		this.struts = struts;
		this.searchKey = searchKey;

	}

	private void setValueNm(String vNm) {
		if (vNm == null || vNm.isEmpty())
			vNm = "0";

		this.valueNm = vNm.split(",");
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 得到值的列数组
	 * 
	 * @return the valueNm
	 */
	public String[] getValueNm() {

		return valueNm;
	}

	/**
	 * 得到第一列的值
	 * 
	 * @return the valueNm
	 */
	public int getfirstValueNm() {

		return Integer.parseInt(valueNm[0]);
	}

	/**
	 * @return the searchKey
	 */
	public String getSearchKey() {
		return searchKey;
	}

	/**
	 * @return the columbyattr
	 */
	public String[] getColumbyattr() {
		return columbyattr;
	}

	/**
	 * @param columbyattr
	 *            the columbyattr to set
	 */
	public void setColumbyattr(String[] columbyattr) {
		this.columbyattr = columbyattr;
	}

	/**
	 * @return the struts
	 */
	public String getStruts() {
		return struts;
	}

	/**
	 * @param columnString
	 *            the columnString to set
	 */
	public void setColumnString(String columnString) {
		this.columnString = columnString;
	}
	public void addDatasByList(List<List<String>> datas) 
	{
		if (datas == null || datas.isEmpty()) {
			return;
		}
		datacount = datas.size();
		if (datacount <= maxneicuncount) {
			this.datas = new ArrayList<List<String>>();
			for (List<String> rowdata : datas) {
				List<String> nrowdata = new ArrayList<String>();
				for (String data : rowdata) {
					nrowdata.add(data);
				}
				this.datas.add(nrowdata);
			}
		} else {
			StringBuffer sb = new StringBuffer();
			for (List<String> rowdata : datas) {
				for (String celldata : rowdata) {
					if (celldata == null) {
						celldata = "";
					}
					sb.append(celldata);
					sb.append("!@#,");
				}
				sb.delete(sb.length() - 4, sb.length());
				sb.append('\n');

			}
			if (sb.length() > 0) {

				FileOutputStream out = null;
				try {
					datafile = File.createTempFile(getName(), "wd");
					out = new FileOutputStream(datafile);
					out.write(sb.toString().getBytes("UTF-8"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}
	}
	public void addDatas(List<String[]> datas) {
		if (datas == null || datas.isEmpty()) {
			return;
		}
		datacount = datas.size();
		if (datacount <= maxneicuncount) {
			this.datas = new ArrayList<List<String>>();
			for (String[] rowdata : datas) {
				List<String> nrowdata = new ArrayList<String>();
				for (String data : rowdata) {
					nrowdata.add(data);
				}
				this.datas.add(nrowdata);
			}
		} else {
			StringBuffer sb = new StringBuffer();
			for (String[] rowdata : datas) {
				for (String celldata : rowdata) {
					if (celldata == null) {
						celldata = "";
					}
					sb.append(celldata);
					sb.append("!@#,");
				}
				sb.delete(sb.length() - 4, sb.length());
				sb.append('\n');

			}
			if (sb.length() > 0) {

				FileOutputStream out = null;
				try {
					datafile = File.createTempFile(getName(), "wd");
					out = new FileOutputStream(datafile);
					out.write(sb.toString().getBytes("UTF-8"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}
	}

	public int getDatacount(CascadeInfo cascadeinfo, String cascadeValue,String search,Set<Integer> searchkeys) {
		if ((cascadeinfo == null || cascadeValue == null
				|| cascadeValue.isEmpty())&&search==null) {
			return datacount;
		} else {
			List<List<String>> alldatas = getAllData(cascadeinfo, cascadeValue);
			List<List<String>> datas=alldatas;
			if(search!=null&&!search.isEmpty()){
			datas = new ArrayList<List<String>>();
			for (List<String> rowdatas : alldatas) {
				int index = 1;
				for (String data : rowdatas) {
					if (data == null || data.isEmpty()) {
						continue;
					}
					boolean nendcop = true;
					if (searchkeys != null && !searchkeys.isEmpty()) {
						nendcop = searchkeys.contains(index);
					}
					if (nendcop && data.contains(search)) {
						datas.add(rowdatas);
						break;
					}
					index++;
				}
			}
			}
			return datas.size();
		}
	}

	public List<List<String>> getData(int pagenumber, int pagecount,
			String search, Set<Integer> searchkeys, CascadeInfo cascadeinfo,
			String cascadeValue) {
		if (search == null || search.isEmpty()) {
			if (pagenumber < 1 || pagecount < 1) {
				return getAllData(cascadeinfo, cascadeValue);
			} else {
				return getPageData(pagenumber, pagecount, cascadeinfo,
						cascadeValue);
			}
		} else {
			List<List<String>> alldata = getAllData(cascadeinfo, cascadeValue);
			List<List<String>> datas=alldata;
			if(search!=null&&!search.isEmpty()){
			datas = new ArrayList<List<String>>();
			for (List<String> rowdatas : alldata) {
				int index = 1;
				for (String data : rowdatas) {
					if (data == null || data.isEmpty()) {
						continue;
					}
					boolean nendcop = true;
					if (searchkeys != null && !searchkeys.isEmpty()) {
						nendcop = searchkeys.contains(index);
					}
					if (nendcop && data.contains(search)) {
						datas.add(rowdatas);
						break;
					}
					index++;
				}
			}
			}
			int start = (pagenumber - 1) * pagecount;
			int end = start + pagecount;
			if(end>datas.size())
			{
				end=datas.size();
			}
			return datas.subList(start, end);
		}
	}

	private List<List<String>> getPageData(int pagenumber, int pagecount,
			CascadeInfo cascadeinfo, String cascadeValue) {
		boolean needcas = cascadeinfo != null && cascadeValue != null
				&& !cascadeValue.isEmpty();
		int start = (pagenumber - 1) * pagecount;
		int end = start + pagecount;
		if (needcas) {
			List<List<String>> alldatas = getAllData(cascadeinfo, cascadeValue);
			if (end > alldatas.size()) {
				end = alldatas.size();
			}
			return alldatas.subList(start, end);
		} else {

			if (end > datacount) {
				end = datacount;
			}
			if (datas != null) {
				List<List<String>> newdatas = new ArrayList<List<String>>();
				for (int i = start; i < end; i++) {
					List<String> rowdata = datas.get(i);
					List<String> rowdatas = new ArrayList<String>();
					for (String data : rowdata) {
						rowdatas.add(data);
					}
					newdatas.add(rowdatas);
				}
				return newdatas;
			} else {
				return getDataFromFile(start, end);
			}
		}

	}

	public List<List<String>> getAllData(CascadeInfo cascadeinfo,
			String cascadeValue) {
		List<List<String>> alldatas = datas;
		if (alldatas == null) {
			alldatas = getDataFromFile(0, datacount);
		}

		if (alldatas != null) {
			List<List<String>> newdatas = new ArrayList<List<String>>();
			boolean needcas = cascadeinfo != null && cascadeValue != null
					&& !cascadeValue.isEmpty();
			for (List<String> rowdata : alldatas) {
				if (needcas) {
					if (cascadeValue.equals(rowdata.get(cascadeinfo.getNext()))) {
						newdatas.add(rowdata);
					}
				} else {
					newdatas.add(rowdata);
				}
			}
			return newdatas;
		}
		return null;
	}

	private List<List<String>> getDataFromFile(int start, int end) {
		List<List<String>> datas = new ArrayList<List<String>>();
		if (datafile != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(datafile), "UTF-8"));
				int li = 0;
				String line;
				while ((line = reader.readLine()) != null) {
					if (li < start) {
						li++;
						continue;
					} else if (li >= start && li < end) {
						String[] rowdatas = line.split("!@#,");
						List<String> rdatas = new ArrayList<String>();
						for (String data : rowdatas) {
							rdatas.add(data);
						}
						datas.add(rdatas);
					} else {
						break;
					}
					li++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return datas;
	}

}