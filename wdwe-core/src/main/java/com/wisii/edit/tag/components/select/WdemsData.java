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
 * @WdemsData.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类功能描述：用于建立数据结构关系。可能利用其前途,来表示表数据结构. 
 * 作者：李晓光 创建日期：2009-7-1
 */
public class WdemsData<T extends Object> {
	List<T> datas = null;
	public WdemsData(){
		
	}
	public WdemsData(List<T> datas){
		this.datas = datas;
	}
	public void addDatas(List<T> datas){
		if(datas == null || datas.isEmpty())
			return;
		if(this.datas == null)
			this.datas = new ArrayList<T>();
		this.datas.addAll(datas);
	}
	public void addDatas(T...datas){
		if(datas == null || datas.length == 0)
			return;
		
		List<T> list = Arrays.asList(datas);
		addDatas(list);
	}
	public void setDatas(List<T> datas){
		this.datas = datas;
	}
	public void setDatas(T...datas){
		if(datas == null){
			this.datas = null;
		}else{
			List<T> list = Arrays.asList(datas);
			this.datas = new ArrayList<T>(list);
		}
	}
	public List<T> getDatas(int...indexes){
		if(datas == null || indexes == null)
			return new ArrayList<T>();
		List<T> all = new ArrayList<T>();
		int length = datas.size();
		for (int i : indexes) {
			if(i < 0 || i >= length)continue;
			all.add(datas.get(i));
		}
		return all;
	}
	public List<T> getDatas(){
		return this.datas;
	}
	public T getData(int index){
		if(datas == null || datas.isEmpty())
			return null;
		return datas.get(index);
	}
	public void clear(){
		if(datas != null)
			datas.clear();
	}
	public int getSize(){
		if(datas == null)
			return 0;
		return datas.size();
	}
	
	@Override
	public String toString() {
		if(datas == null)return "";
		return datas.toString();
	}
}
