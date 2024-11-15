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
 */package com.wisii.edit.tag;

/**
 * 这里放着有关解析后的id，目前可能有tagName和XPath，以后有可能进行扩充
 * 该接口是解析id属性的一个逻辑抽象
 * 
 * 目前这个逻辑模型是name(path)这种，这里提供返回name和path的方法。
 * 针对id中有多个属性的情况，每次返回一个name(path)这种组合，
 * 一个这个组合生成一个WdemsTagID对象。
 * 
 * 以后要是添加authority这种情况，则以这种形式出现name(path,authority)
 * 
 * @author 闫舒寰
 * @version 1.0 2009/06/15
 */
public interface WdemsTagID {
	
	/**
	 * 获得标签名称
	 */
	public String getTagName();
	
	/**
	 * 获得标签的xpath
	 */
	public String getTagXPath();
	
	/**
	 * 获得标签的权限信息
	 */
	public String getAuthority();
	/*
	 * 获得标签的默认值信息
	 */
	public String getDefaultvalue() ;
}
