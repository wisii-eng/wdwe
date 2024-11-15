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
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.schema.wdems.Data;
import com.wisii.edit.tag.schema.wdems.Include;
import com.wisii.edit.tag.schema.wdems.Swingdatasource;
import com.wisii.edit.tag.schema.wdems.TableInfo;
import com.wisii.edit.util.EditUtil;
/**
 * 
 * @author liuxiao
 *
 */
public class DataSource {

	/* 拼接规则：等值拼接 */
	public static final String BOND_EQUAL = "eq";
	/* 拼接规则：按顺序横向拼接 */
	public static final String BOND_SQUE = "sq";
	/* 拼接规则：纵向拼接 */
	public static final String BOND_VERT = "vert";


	/*
	 * 数据不是内置的情况下的数据来源,有这个属性的时候则说明为 唯一数据源否则为多数据源，当由此属性的时候就不解析data 节点了
	 */
	private String translateUrl;
	/* 多数据之间的拼接 */
	private String bond;
	/* 用于得到tableinfo标签 */
	private TableInfo tableInfo;
	/* 用于得到data标签 */
	private Data data;
	/* 用于得到include标签 */
	private List<Include> includeList;

	/* 记录建表信息 */
	private TableOrViewInfo tovi;
	//Swing接口方式提供的数据源
	private SwingDataSource swingDS;

	/**
	 * 构造方法用于从标签对象生成DataSource解析器对象
	 * 
	 * @param ds
	 *            标签对象dataSource
	 */
	public DataSource(final com.wisii.edit.tag.schema.wdems.DataSource ds) {
		if (ds == null)
			return;
		// 读取schema的枚举值
		// 创建TableOrViewInfo对象记录建表信息
		//modify by px
		String table = ds.getName() + "_" + EditUtil.INSTANCEID;
		createTable(table, ds.getValueNumber(), ds.getStruts());
		init(ds);

	}

	/**
	 * 构造方法用于从标签对象生成DataSource解析器对象
	 * 
	 * @param ds
	 *            标签对象dataSource
	 * @param searchKey
	 *            搜索关键字
	 */
	public DataSource(final com.wisii.edit.tag.schema.wdems.DataSource ds,
			final String searchKey) {
		if (ds == null)
			return;
		// 创建TableOrViewInfo对象记录建表信息
		//modify by px
		String table = ds.getName() + "_" + EditUtil.INSTANCEID;
		createTable(table, ds.getValueNumber(), ds.getStruts(),searchKey);
		init(ds);

	}

	/**
	 * 在构造中调用此方法
	 * 
	 * @param ds
	 */
	private void init(final com.wisii.edit.tag.schema.wdems.DataSource ds) {
		String swingds=ds.getSwingdatasource();
		if(swingds!=null&&!swingds.isEmpty())
		{
			Object sds=WdemsTagManager.Instance.getWdemsTags(swingds);
			if(sds instanceof Swingdatasource)
			{
				swingDS=new SwingDataSource((Swingdatasource)sds);
				if (getTableInfoOrDataOrInclude(ds.getTableInfoOrDataOrInclude())) {
				parseTableinfo();
				}
				return;
			}
		}
		// 读到基类的datasource子标签对象
		if (getTableInfoOrDataOrInclude(ds.getTableInfoOrDataOrInclude())) {
            
			// 解析tabliinfo
			parseTableinfo();
			setTranslateUrl(ds.getTranslateUrl());
			setBond(ds.getBond());

			dispatchSource(ds.getStruts(), ds.getRoot());
		}
	}

	/**
	 * 用于建立一个将键表或者建立视图的信息都搜集其来的类最后将此类返回
	 * 
	 * @param name
	 * @param valueNm
	 */
	private void createTable(final String name, final String valueNm,
			final String struts) {
		tovi = new TableOrViewInfo(name, valueNm, struts);

	}

	/**
	 * 用于建立一个将键表或者建立视图的信息都搜集其来的类最后将此类返回
	 * 
	 * @param name
	 * @param valueNm
	 * @param searchKey
	 *            通过include引用的时候可能会有次初始化参数，用于搜索关键字
	 */
	private void createTable(final String name, final String valueNm,
			final String struts, final String searchKey) {
		tovi = new TableOrViewInfo(name, valueNm, struts, searchKey);

	}

	/**
	 * 用来处理多数据来源的分发
	 * 
	 * @param struts
	 * @param root
	 */
	private void dispatchSource(final String struts, final String root) {
		if (translateUrl == null || translateUrl.isEmpty()) {
			// 解析data，如果没有Data才会去读取include，否则就不读取include
			if (!parseData(struts, root)) {
				parseInclude();
			}
		} else {
			DispSource dis=ParseDataBuilder.dispFactory(translateUrl);
			if(dis!=null){
			dis.parseContent(tovi,
					struts, root);}
		}
	}

	/**
	 * 用来处理tableInfo的信息并最终将解析出来的东系都写到tovi中
	 */
	private void parseTableinfo() {
		if (tableInfo == null)
			return;
		List<TableInfo.Column> ss = tableInfo.getColumn();
		// 用于记录表头信息
		String[] tablehead = new String[ss.size()];
		// 用于记录每一列的类型
		String[] column = new String[ss.size()];

		String[] attrnames = new String[ss.size()];
		for (int i = 0; i < tablehead.length; i++) {
			// 记录表头名称，表头名称不用与列匹配，只要在显示的时候安顺序显示在表头即可。
			String ds = ss.get(i).getName();
			if (ds != null && !ds.isEmpty()) {
				tablehead[i] = ds;
			}
			ds = ss.get(i).getType();
			// 记录类型，如果没有类型但是有一列则填上varchar保持列的相匹配
			if (ds != null && !ds.isEmpty()) {
				column[i] = ds;
			} else
				column[i] = "VARCHAR";
			// 记录表头名称，表头名称不用与列匹配，只要在显示的时候安顺序显示在表头即可。
			String attr = ss.get(i).getAttrName();
			if (attr != null && !"".equalsIgnoreCase(attr)) {
				attrnames[i] = attr;
			}
		}

		tovi.setColumnType(column);
		tovi.setTablehead(tablehead);
		tovi.setColumbyattr(attrnames);

	}

	/**
	 * 该方法用于处理传入的list 将他们解析成tableinfo, include, data属性 其中tableinfo
	 * 最多出现一次，如果要包含多个，请使用data 或者include。data最多只能出现一次并且不能跟include共存
	 * 
	 * @param tdi
	 */
	private boolean getTableInfoOrDataOrInclude(final List tdi) {
		if (tdi == null && tdi.size() < 1)
			return false;
		for (int i = 0; i < tdi.size(); i++) {
			Object ss = tdi.get(i);
			if (ss instanceof TableInfo) {
				setTableInfo((TableInfo) ss);
				continue;
			} else if (ss instanceof Data) {
				setData((Data) ss);
				continue;
			} else if (ss instanceof Include) {
				addInclude((Include) ss);
			}
		}
		return true;
	}

	/**
	 * 用于解析Data对象
	 * 
	 * @return 当Data对象不为空的时候返回true,否则返回 false
	 */
	private boolean parseData(final String struts, final String root) {
		if (data == null)
			return false;
		ParseDataBuilder.parseFactory(struts).parse(data.getAny(), root, tovi);
		return true;
	}

	/**
	 * 该方法用于处理多个include的情况
	 */
	private void parseInclude() {
		List tv = new ArrayList();
		for (int i = 0; i < includeList.size(); i++) {
			tv.add(new DataSource(askDataSource(includeList.get(i).getName()),
					includeList.get(i).getSearchKey()).getTableOrViewInfo());

		}
		combine(tv, bond);

	}

	/**
	 * 该方法用于将多个include联合起来
	 * 
	 * @param tv
	 */
	private void combine(final List<TableOrViewInfo> tv, final String bond) {
		if (bond == null || bond.isEmpty()) {
			StatusbarMessageHelper.output("没有bond的信息", tovi.getName(),
					StatusbarMessageHelper.LEVEL.DEBUG);
			return;
		}
		Combine cm = null;
		if (bond.equals(BOND_EQUAL))
			cm = new ValueComb();
		else if (bond.equals(BOND_SQUE))
			cm = new ParallelComb();
		else if (bond.equals(BOND_VERT))
			cm = new DirectComb();
		cm.combine(tv, tovi);
	}

	private com.wisii.edit.tag.schema.wdems.DataSource askDataSource(
			final String name) {
		Object ss = WdemsTagManager.Instance.getWdemsTags(name);
		if (ss instanceof com.wisii.edit.tag.schema.wdems.DataSource)
			return (com.wisii.edit.tag.schema.wdems.DataSource) ss;
		else
			StatusbarMessageHelper.output("未找到正确的dataSource:", "请查看辨析文件名名称为："
					+ name + "的元素", StatusbarMessageHelper.LEVEL.INFO);
		return null;
	}

	/**
	 * @param data
	 *            the data to add
	 */
	private void setData(final Data data) {
		this.data = data;

	}

	/**
	 * @param include
	 *            the include to add
	 */
	private void addInclude(final Include include) {
		if (include == null)
			return;
		if (includeList == null)
			includeList = new ArrayList();
		includeList.add(include);

	}

	/**
	 * @param translateUrl
	 *            the translateUrl to set
	 */
	public void setTranslateUrl(final String translateUrl) {
		this.translateUrl = translateUrl;
	}

	/**
	 * @param bond
	 *            the bond to set
	 */
	public void setBond(final String bond) {
		this.bond = bond;
	}

	/**
	 * @param tableInfo
	 *            the tableInfo to set
	 */
	public void setTableInfo(final TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}

	public TableOrViewInfo getTableOrViewInfo() {
		return this.tovi;
	}

	public SwingDataSource getSwingDS() {
		return swingDS;
	}
	/*
	 * 是否是Swing接口方式提供的数据源
	 */
    public boolean isSwingDS()
    {
    	return swingDS!=null;
    }

}
