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
 * 这个接口想象中是为了解决验证动作执行的问题，以后会在Actions中实现这个接口，action具体的子类实现
 * 具体的方法
 * @author 闫舒寰
 * @version 1.0 2009/07/10
 */
public interface ValidationActions {
	
	/**
	 * 做失焦点验证
	 * @return 验证是否通过
	 */
	public boolean doOnBlurValidation();
	

	/**
	 * 当文档内容改变时所进行的验证
	 * @return 验证是否通过
	 */
	public boolean doOnEditValidation();
	
	/**
	 * 当用户设置结果的时候所引起的验证
	 * @return 验证是否通过
	 */
	public boolean doOnResultValidation();
	

}
