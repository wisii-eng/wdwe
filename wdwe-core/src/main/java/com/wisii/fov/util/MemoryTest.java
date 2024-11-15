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
 */package com.wisii.fov.util;

public class MemoryTest {

	// for statistics gathering
	private Runtime runtime;

	// heap memory allocated (for statistics)
	private long initialMemory;

	// time used in rendering (for statistics)
	private long startTime;


	public void start() {
		runtime = Runtime.getRuntime();
		initialMemory = runtime.totalMemory() - runtime.freeMemory();
		startTime = System.currentTimeMillis();
		System.out.println("-------------Strat-------------------");
	}

	public void end() {

		long memoryNow = runtime.totalMemory() - runtime.freeMemory();
		long memoryUsed = (memoryNow - initialMemory) / 1024L;
		long timeUsed = System.currentTimeMillis() - startTime;
		
		System.out.println("Initial heap size: " + (initialMemory / 1024L) + "Kb");
		System.out.println("Current heap size: " + (memoryNow / 1024L) + "Kb");
		System.out.println("Total memory used: " + memoryUsed + "Kb");
		System.out.println("Total time used: " + timeUsed + "ms");
		System.out.println("-------------end-------------------");
	
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	

	}

}
