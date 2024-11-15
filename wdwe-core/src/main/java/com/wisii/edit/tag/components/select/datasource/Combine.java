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
 * 该类用于将多个include引入的数据源进行拼接
 * 其主要关心的是：
 * 1.当前数据源名称
 * 2.每一个include的searchkey
 * 3.拼接
 * @author liuxiao
 *
 */
public interface Combine {
	/**
	 * 该方法用于拼接
	 * @param tlist 需要拼接的include所代表的TableOrViewInfo
	 * @param tovi 当前数据源的TableOrViewInfo
	 */
	public void combine(List <TableOrViewInfo> tlist ,TableOrViewInfo tovi);

}
