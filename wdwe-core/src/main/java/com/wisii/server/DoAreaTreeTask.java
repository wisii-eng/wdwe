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
 */package com.wisii.server;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletResponse;

import com.wisii.fov.command.CMDAreaTreeObj;

//设置AreaTree的任务
public class DoAreaTreeTask implements Callable<Object> {
	
	CMDAreaTreeObj obj;
	HttpServletResponse response;
	Object out;
	Object para;
	Object request;
	Object current;
	
	public DoAreaTreeTask(CMDAreaTreeObj cmd, HttpServletResponse response, Object para, Object request) {
		this.obj = cmd;
		this.response = response;
		this.para = para;
		this.request = request;
	}


	public Object call() throws Exception {
//		boolean b = false;
//		long before = System.currentTimeMillis();
//		b = realExcute(obj, response, para, request);
//		long after = System.currentTimeMillis();
//		System.out.println("page layout: " + (after - before));
		return realExcute(obj, response, para, request);
	}
	//实际运行的方法
	private boolean realExcute(CMDAreaTreeObj cmd, HttpServletResponse response, Object para, Object request){
		
		boolean execute = false;
		try {
			execute = cmd.execute(response.getWriter(), para, request);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			response = null;
		}
		
		return execute;
	}
}