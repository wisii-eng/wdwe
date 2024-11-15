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
 */package com.wisii.edit.tag.action;


/**
 * 处理验证信息的时候的信息通过这个接口读取
 * @author 闫舒寰啊
 * @version 1.0 2009/07/09
 */
public interface ValidationMessage {
	
	/**
	 * 用于返回验证错误信息
	 * @return 验证错误信息
	 */
	public String getWrongMessage();
	
	/**
	 * 当验证错误的时候显示的图标
	 * @return
	 */
	public Object getWrongIco();
	
	/**
	 * 当验证正确的时候显示的图标
	 * @return
	 */
	public Object getRightIco();
	
	/**
	 * 返回控件当前进行的验证的验证状态
	 * @return 返回true的时候则当前控件验证正确，若返回false的时候则当前控件验证不正确，当返回为null的时候则代表没有验证状态
	 */
	public Boolean getValidationState();
	
	/**
	 * 获得该控件最终的验证状态，对控件所有的验证状态进行比较
	 * @return 返回true的时候则该控件通过了所有的验证，返回false则该控件至少有一个验证没有通过，当返回为null的时候则代表没有验证状态
	 */
	public Boolean getFinalValidationState();

}
