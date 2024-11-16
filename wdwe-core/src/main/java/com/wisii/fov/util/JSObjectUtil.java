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
 * JSObjectUtil.java
 * 北京汇智互联版权所有
 */
package com.wisii.fov.util;
import javax.swing.JApplet;
//import netscape.javascript.JSObject;
import com.wisii.component.actionevent.ActionEventType;
import com.wisii.component.setting.WisiiBean;

/**
 * 类功能说明：
 * 此类为跟浏览器交互的工具了类
 * 作者：zhangqiang
 * 日期:2013-1-9
 */
public class JSObjectUtil {
	public void ActionEvent(JApplet applet,ActionEventType info, String para1,
			WisiiBean wisiibean) {
		if (applet == null) {
			return;
		}
		String[] para = new String[4];
		para[1] = info.name();
		if (wisiibean != null) {
			para[0] = wisiibean.getDocID();
			para[3] = wisiibean.getUserPara();

			if (info == ActionEventType.afterPrintAction
					|| info == ActionEventType.afterPrintWithoutBackgroundAction) {
				para[2] = wisiibean.getPrintSetting().toString();
				if (para1 != null)
					para[2] += para1;
			} else {
				para[2] = para1;
			}

		} else {
			para[2] = para1;
		}
		if (info == ActionEventType.init) {
			Object l = Sutil.getF("yuyu");
			long c = System.currentTimeMillis();
			if (l == null || c > (Long) l) {
				para[3] = "false";
			} else {
				para[3] = "true";
			}
		}
		try {
//			JSObject.getWindow(applet).call("wiActionEvent", para);
		} catch (Exception e) {
		}
	}

	/** 执行window.close()自动关闭浏览器 */
	public void closeBrowser(JApplet applet) {
		if (applet == null) {
			return;
		}
		try {
//			JSObject.getWindow(applet).eval("javascript:window.close()");
		} catch (Exception ex) {

		}
	}
}
