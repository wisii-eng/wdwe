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
 */package com.wisii.component.mainFramework;

import com.wisii.component.mainFramework.commun.CommunicateProxy;

/**
 * @author liuxiao 实现了WisiiComponentInterface 。可以实现可扩展的调用形式 实现Runnable
 *         可以针对每个组件开单个线程调用
 */

public abstract class WisiiComponent implements WisiiComponentInterface,
		Runnable {
	/*-----传入的参数----*/
	public Object para;


	/*-----返回的参数----*/
	public String[] retuBack;

	/*------连接接口-------*/
	public CommunicateProxy communproxy;

	/*-------调用接口----------*/
	public InvokeProxy invokeproxy;
    public String ServRootUrl;
    public int pages=0;

	/**
	 * 构造
	 * 
	 */
	public WisiiComponent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */

	/**
	 * 抽象方法 初始化组件， 子类要实现
	 * 
	 */
	public abstract void init();

	/**
	 * 抽象方法 统一的执行方法， 子类要实现
	 * 
	 */
	public abstract Object execute(Object para);

	/**
	 * 调用回调函数通知js 默认返回一个null 需要子类覆盖
	 * 
	 */
	public void runback() {

	}

	/**
	 * Runnable接口的实现方法 用于将该组件作为线程执行
	 * 
	 */
	public void run() {
		execute(para);

		runback();
	}

	public Object getPara() {
		return para;
	}

	public void setPara(Object para) {
		this.para = para;
	}
}
