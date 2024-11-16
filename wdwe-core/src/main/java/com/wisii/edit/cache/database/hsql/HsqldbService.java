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
 * @HsqlServer.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.cache.database.hsql;
import com.wisii.component.setting.WisiiBean;
import com.wisii.edit.authority.AuthorityHelper;
import com.wisii.edit.data.MaintainData;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.validator.ValidatorFactory;
import com.wisii.edit.view.EnginePanel;

/**
 * 类功能描述：
 *
 * 作者：p.x
 * 创建日期：2009-8-4
 */
public class HsqldbService {
	private static HsqldbService dbService = null;
	private static WisiiBean wisiibean;
	private static boolean isinit=false;
	
	private HsqldbService(){
	}
	public static synchronized HsqldbService getInstance(){
		if(null == dbService){
			dbService = new HsqldbService();
			EnginePanel enginepane=EngineUtil.getEnginepanel();
			wisiibean=enginepane.getWisiibean();
			writeDb(wisiibean);

			// 触发权限读取
			AuthorityHelper.triggerCheckAuthor(wisiibean.getAuthorityId(),
					wisiibean.getEditTemplateId());
			WdemsTagManager.Instance.initialTagMap(wisiibean.getEditString());
			ValidatorFactory.initValidateFrame(wisiibean);
		}
		else
		{
			EnginePanel enginepane=EngineUtil.getEnginepanel();
			WisiiBean newwisiibean=enginepane.getWisiibean();
			if(newwisiibean!=wisiibean)
			{
				writeDb(newwisiibean);
				// 触发权限读取
				AuthorityHelper.triggerCheckAuthor(newwisiibean.getAuthorityId(),
						newwisiibean.getEditTemplateId());
				// 解析编辑标签
				WdemsTagManager.Instance.initialTagMap(newwisiibean.getEditString());
				ValidatorFactory.initValidateFrame(newwisiibean);
			}
			wisiibean=newwisiibean;
			
		}
		isinit=true;
		return dbService;
	}
	
	public static boolean isInit() {
		return isinit;
	}
	private static void writeDb(WisiiBean wb)
	{
		MaintainData.setDocumentbyString(wb.getXmlString());
	}
	
	/**
	 * 关闭Hsqldb的数据库服务
	 *
	 */
	public  void stop(){
		dbService=null;
		wisiibean=null;
		isinit=false;
		MaintainData.stop();
		AuthorityHelper.stop();
	}
}