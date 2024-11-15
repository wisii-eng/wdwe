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
 */package com.wisii.edit.data;

import java.sql.SQLException;

/**
 * 该类用来处理撤销事件
 * @author IBM
 *
 */
public class UnDoUtil {
	/**
	 * 该方法用来记录撤销事件
	 * @return
	 * @throws SQLException 
	 */
	public static boolean recordUndo(int oper ,String xpath, String shortName ,String content)
	{
		//需要实现undo的代码		
		return true;
		
	
	}
	/**
	 * 该方法用来处理撤销先得到该ID，自动取出比这个ID值大的所有数据项，依次对他们进行update的操作
	 * 注：该方法目前仅支持 数据的更改，不支持结构改变之后的问题
	 * @param id  会传递进来撤销列表项的ID，然后进行撤销
	 * @return
	 * @throws Exception 
	 */
	public static boolean undo(int id)
	{
	
		//需要实现undo的代码	
		return false;
	
	}
}
