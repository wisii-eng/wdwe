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

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXB;

/**
 * 服务端配置类
 * @author 闫舒寰
 * @version 1.0 2009/12/17
 */
public class ServerConfUtil {
	
	
	
	//server配置文件所在地
	private static final String configPath = System.getProperty("user.dir") + "/wdemsSource/resource/ServerConfig.xml";
	//配置文件所生成的类
	private static final ServerConfig sc = JAXB.unmarshal(new File(configPath), ServerConfig.class);
	//CPU个数
	private static final int CPU = Runtime.getRuntime().availableProcessors();
	//运行线程池
	private static Executor runPool;
	
	private static final ServerConfUtil Instance = new ServerConfUtil();
	
	private ServerConfUtil(){
		if (sc.getRunPoolState().equals("cached")) {
			runPool = Executors.newCachedThreadPool();
		} else if (sc.getRunPoolState().equals("fixed")) {
			if (sc.getRunPoolSize() == -1) {
				runPool = Executors.newFixedThreadPool(CPU + 1);
			} else {
				runPool = Executors.newFixedThreadPool(sc.getRunPoolSize());
			}
		}
	}
	
	public static ServerConfUtil getInstance(){
		return Instance;
	}
	
	
	public boolean isConcurrent(){
		return sc.isConcurrency();
	}
	
	
	public Executor getRunPool(){
		return runPool;
	}
	
}
