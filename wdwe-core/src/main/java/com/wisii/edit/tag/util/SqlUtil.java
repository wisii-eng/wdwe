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
 * @SqlUtil.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.util;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * 类功能描述：用于创建各种操作的Sql信息
 * 
 * 作者：李晓光
 * 创建日期：2009-7-1
 */
public class SqlUtil {
	public static enum Relection{
		And,
		Or
	}
	private final static String SQL_SELECT = "select {0} from {1}";
	private final static String SQL_WHERE = "where {0}";
	private final static String SQL_GROUP_BY = "group by {0}";
	private final static String SQL_ORDER_BY = "order by {0}";
	private final static String SQL_UPDATE = "update {0} set {1}";
	private final static String SQL_INSER = "inser into {0} values({1})";
	private final static String SQL_LIMIT = "limit {0} {1}";
	private final static String SQL_COUNT = "select count(0) from {0}";
	private final static String SQL_LIKE = "LIKE ''%{0}%''";
	private final static String SQL_QUOTATIO = "''{0}''";
	/* 逗号分隔符 */
	private final static String SEPARATOR = ",";
	/* 空格分隔符 */
	private final static String SEPARATOR_SPACE = " ";
	/* 升序 */
	private final static String ASC = "ASC";
	/* 降序 */
	private final static String DESC = "DESC";
	
	/**
	 * 获得数据条目数的sql语句
	 * @param params	指定表
	 * @return	{@link String}
	 */
	public final static String getCouns(String...params){
		if(params == null || params.length == 0)return "";
		
		return getFormatString(SQL_COUNT, params);
	}
	public final static String getQuotatio(String s){
		return getFormatString(SQL_QUOTATIO, s);
	}
	/**
	 * <p>
	 * SELECT [LIMIT n m] [DISTINCT] 
	 * { selectExpression | table.* | * } [, ... ] 
	 * [INTO [CACHED|TEMP|TEXT] newTable] 
	 * FROM tableList 
	 * [WHERE Expression] 
	 * [ORDER BY selectExpression [{ASC | DESC}] [, ...] ] 
	 * [GROUP BY Expression [, ...] ] 
	 * [UNION [ALL] selectStatement]
	 * </p>
	 * @param columns
	 * @param tables
	 * @return
	 */
	public final static String getSQLSelect(List<String> columns, String...tables){
		if(tables == null || tables.length == 0)return "";
		String table = tables[0];
		if(tables.length > 1) {
			table = getString(SEPARATOR, tables);
		}
		String column = "*";
		if(columns == null || columns.size() == 0){
			column = "*";
		}else if(columns.size() > 1) {
			column = getString(SEPARATOR, columns);
		} else if(columns.size() == 1) {
			column = columns.get(0);
		}
			
		return getFormatString(SQL_SELECT, column, table);
	}
	public final static String getSQLSelect(String columns, String tables){
		if(tables == null || tables.length() == 0)
			return "";
		if(columns == null || columns.length() == 0) {
			columns = "*";
		}
		return getFormatString(SQL_SELECT, columns, tables);
	}
	/**
	 * <p>
	 * [WHERE Expression]
	 * </p>
	 * @param conditions
	 * @return
	 */
	public final static String getSQLWhere(String... conditions){
		return getSQLWhere(Relection.Or, conditions);
		/*if(conditions == null || conditions.length == 0)
			return "";
		String params = conditions[0];
		if(params == null || "".equalsIgnoreCase(params.trim()))
			return ""; 
		if(conditions.length > 1) {
			params = getString(" or ", conditions);
		}
		return getFormatString(SQL_WHERE, params);*/
	}
	public final static String getSQLWhere(Relection rel, String... conditions){
		if(conditions == null || conditions.length == 0)
			return "";
		String params = conditions[0];
		if(params == null || "".equalsIgnoreCase(params.trim()))
			return ""; 
		if(conditions.length > 1) {			
			params = getString(" "+ rel.name() + " ", conditions);				
		}
		return getFormatString(SQL_WHERE, params);
	}
	public final static String getSQLLike(String s){
		if(s == null || "".equalsIgnoreCase(s))return "";
		return getFormatString(SQL_LIKE, s);
	}
	/**
	 * <p>
	 * SELECT [LIMIT n m] [DISTINCT] 
	 * { selectExpression | table.* | * } [, ... ] 
	 * </p>
	 * @param params
	 * @return
	 */
	public final static String getSQLLimit(Integer...params){
		if(params == null || params.length < 2)
			return "";
		String[] arr = {params[0] + "", params[1] + ""};
		return getFormatString(SQL_LIMIT, arr);
	}
	/**
	 * <p>
	 * [GROUP BY Expression [, ...] ] 
	 * </p>
	 * @param columns	指定要列名、表达式
	 * @return
	 */
	public  final static String getSQLGroup(String...columns){
		if(columns == null || columns.length == 0)
			return "";
		String params = columns[0];
		if(columns.length > 1) {
			params = getString(SEPARATOR, columns);
		}
		return getFormatString(SQL_GROUP_BY, params);
	}
	/**
	 * <p>
	 * [ORDER BY selectExpression [{ASC | DESC}] [, ...] ] 
	 * </p>
	 * @param columns	指定要排序的列名
	 * @return
	 */
	public final static String getSQLOrderBy(String column){
		return getFormatString(SQL_ORDER_BY, column);
	}
	
	/**
	 * <p>
	 * 	UPDATE table SET column = Expression [, ...]  
	 * 	[WHERE Expression]
	 * </p>
	 * @param table	指定表名
	 * @param values	指定要更新的列及值【column='test'】
	 * @return
	 */
	public final static String getSqlUpdate(String table, String...values){
		if(table == null || "".equals(table))
			return "";
		if(values == null || values.length == 0)
			return "";
		String params = values[0];
		if(values.length > 1) {
			params = getString(SEPARATOR, values);
		}
		return getFormatString(SQL_UPDATE, params);
	}
	/**
	 * <p>
	 * 	INSERT INTO table [ (column [,...] ) ] 
	 * 	{ VALUES(Expression [,...]) | SelectStatement }
	 * </p>
	 * @param table	指定表名、或包含了要更新的所有列名称其间用','分隔【tablename(col1,col2)】
	 * @param values	指定要要更新列的值
	 * @return
	 */
	public final static String getSqlInsert(String table, String...values){
		if(table == null || "".equals(table))
			return "";
		if(values == null || values.length == 0)
			return "";
		String params = values[0];
		if(values.length > 1) {
			params = getString(SEPARATOR, values);
		}
		return getFormatString(SQL_INSER, table, params);
	}
	/** 
	 * 把制定的数据 用指定的分隔符连接起来
	 */
	public final static String getString(String separator, List<String> datas){
		if(separator == null || "".equalsIgnoreCase(separator))
			return "";
		StringBuilder str = new StringBuilder();
		for (String s : datas) {
			if(s == null || "".equals(s)) {
				continue;
			}
			str.append(s);
			str.append(separator);
		}
		if(str.length() > 1) {
			str = str.replace(str.length() - separator.length(), str.length(), "");
		}
		/*str.deleteCharAt(str.length() - 1);*/
		return str.toString();
	}
	/**
	 * 把制定的数据 用指定的分隔符连接起来
	 */
	public final static String getString(String separator, String...datas){
		return getString(separator, Arrays.asList(datas));
	}
	/**
	 * 按指定的样式格式化指定的数据
	 * @param pattern
	 * @param params
	 * @return
	 */
	public final static String getFormatString(String pattern, Object...params){
		return MessageFormat.format(pattern, params);
	}
}