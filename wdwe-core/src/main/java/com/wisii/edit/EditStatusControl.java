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
 */package com.wisii.edit;


/**
 * 该类负责标志当前变及的时候的状态，是一个单例类
 * @author liuxiao
 *
 */
public final class EditStatusControl {

	/*是否重载*/
	private static boolean isReload=true;
	
	/*是否进行整体验证*/
	private static boolean wholeValidated=true;
	
	private static boolean isSubData = false; // 是否已经提交了数据 true:提交了
	
	public static STATUS RUNSTATUS;
	/*状态说明*/
	public static enum STATUS{
		/*读状态*/
		READ,
		/*写状态*/
		WRITE
	
		
	}
	/**
	 * 本类禁止实例化
	 */
	private EditStatusControl()
	{}
	
	/**
	 * 重载完毕调用此方法
	 */
	public synchronized static void reload()
	{
		isReload=true;
	}
	/**
	 * 整体验证完毕调用此方法
	 */
	public synchronized static void wholeValidated()
	{
		wholeValidated=true;
	}
	/**
	 * 数据更新的时候调用此方法
	 */
	public synchronized static void updataData()
	{
		isReload=false;
		wholeValidated=false;
		isSubData=false;
		
	}
	public synchronized static void init()
	{
		isReload=true;
		wholeValidated=true;
		isSubData=true;
		
	}
	/**
	 * 是否已经重排过了
	 * @return 重排过了 true
	 *         未重排  false
	 */
	public synchronized static boolean  isReloaded()
	{
		return isReload;
	}
	/**
	 * 是否已经整体验证过了
	 * @return 验证过了 true
	 *         未验证或验证未通过  false
	 */
	public synchronized static boolean  isWholeValidated()
	{
		return wholeValidated;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the isSubData
	 */
	public synchronized static boolean isSubData() {
		return isSubData;
	}

	/**
	 * @param isSubData the isSubData to set
	 */
	public synchronized static void setSubData(boolean isSubData) {
		EditStatusControl.isSubData = isSubData;
	}


}
