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
 */package com.wisii.edit.message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.LoggerFactory;
import com.wisii.edit.tag.factories.bar.WdemsLogInfoComponent.LoginfoItem;
import com.wisii.edit.tag.util.SqlUtil;
import com.wisii.edit.view.StatusBar;

/**
 * 该类用于提供一个静态的接口供写状态栏信息的事件调用，从而可以在状态栏里面显示多条信息 状态栏的描述： 一个 panel在
 * 主面板的底部有一个小长条panel挨着显示页码的panel, panel里面显示最近的一条信息。 单击panel上的小三角，状态栏变大，显示未读的信息。
 * 状态信息会自动记录到数据库中。
 * 
 * 
 * 
 * @author liuxiao
 * 
 */
public class StatusbarMessageHelper {

	
	
	/*打印信息级别*/
	public static enum LEVEL{
		/*调试*/
		DEBUG,
		/*信息*/
		INFO,
	
		
	}
	private final static String TABLE_NAME = "StatusbarMessage";
	private final static String[] COLUMNS = {"MSGID", "MSGNAME", "MSGC", "ISREAD", "LEVEL"};

	public static void output(String msgName, String msgC, LEVEL level) {
		String ss=SystemUtil.getConfByName("log.level");
		if(ss!=null&&level.name().toLowerCase().equals(ss.toLowerCase()))
		{
		StatusBar.addLogItems(createItem(msgName, msgC, level));
//		Logger fileLogger = LoggerFactory.getFileLogger();
//        fileLogger.log(Level.INFO, msgName+":"+msgC);
		}
	}
	public final static Set<LoginfoItem> getAllLog(ResultSet set) throws SQLException{
		Set<LoginfoItem> items = new HashSet<LoginfoItem>();
		for (; set.next() ; ) {
			items.add(createItem(set));
		}
		return items;
	}
	private final static LoginfoItem createItem(ResultSet set) throws SQLException{
		LoginfoItem item = new LoginfoItem();
		int i = 0;
		for (String column : COLUMNS) {
			switch (i++) {
			case 0:
				item.setId(set.getInt(column));
				break;
			case 1:
				item.setTitle(set.getString(column));
				break;
			case 2:
				item.setLogInfo(set.getString(column));
				break;
			case 3:
				item.setRead(set.getBoolean(column));
				break;
			case 4:
				item.setLevel(LEVEL.valueOf(set.getString(column)));
				break;
			default:
				break;
			}
		}
		return item;
	}
	private final static LoginfoItem createItem(String title, String logInfo, LEVEL level) {
		LoginfoItem item = new LoginfoItem(title, logInfo, level);
		return item;
	}
}
