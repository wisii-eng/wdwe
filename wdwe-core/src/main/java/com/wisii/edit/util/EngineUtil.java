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
 * EngineUtil.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.util;

import com.wisii.component.setting.WisiiBean;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.cache.database.hsql.HsqldbService;
import com.wisii.edit.tag.components.decorative.WdemsCascadeManager;
import com.wisii.edit.tag.components.decorative.WdemsEditComponentManager;
import com.wisii.edit.tag.components.decorative.WdemsOperationManager;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.components.group.WdemsGroupManager;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.util.WDWEUtil;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-6
 */
public class EngineUtil
{
	private static EnginePanel enginepanel;
	public static enum PrintState
	{
		// 没打印
		UnPrint,
		// 打印服务不可用
		PrintServiceUnUsable,
		// PrinterException if the specified service does not support
		// * 2D printing, or this PrinterJob class does not support
		PrintServiceUnSupport,
		// 其他打印错误，包括打印机卡纸等
		OtherException,
		// 正常打印
		Success;
	}
	private static PrintState printstate = PrintState.UnPrint;
	public static EnginePanel getEnginePanel(WisiiBean wisiibean)
	{
		if (wisiibean == null)
		{
			return null;
		}
		enginepanel = new EnginePanel(wisiibean);
		printstate = PrintState.UnPrint;
		return enginepanel;
	}
	public static void releaseResource()
	{
		if (HsqldbService.isInit())
		{
			// 停止hsql服务
			HsqldbService dbService = HsqldbService.getInstance();
			dbService.stop();
		}
		EditStatusControl.RUNSTATUS = EditStatusControl.STATUS.READ;
		WdemsEditComponentManager.clearDump();
		WdemsCascadeManager.clearDump();
		WdemsOperationManager.clearDump();
		WdemsWarningManager.clearDump();
		WdemsGroupManager.cleanDump();
		enginepanel = null;
		printstate = PrintState.UnPrint;
	}
	public static EnginePanel getEnginepanel()
	{
		return enginepanel;
	}
	public static void print(String xmlstring, String xslstring)
			throws FOVException
	{
		if (xmlstring == null || xmlstring.isEmpty() || xslstring == null
				|| xslstring.isEmpty())
		{
			return;
		}
		WisiiBean bean = new WisiiBean();
		bean.setXml(xmlstring);
		bean.setXsl(xslstring);
		bean.setOutputMode(MimeConstants.MIME_WISII_PRINT);
		// try {
		WDWEUtil.renderTo(bean);
		// } catch (FOVException e) {
		// e.printStackTrace();
		// }
		//
	}
	public static void print(WisiiBean wisiibean) throws FOVException
	{
		if (wisiibean == null)
		{
			return;
		}
		wisiibean.setOutputMode(MimeConstants.MIME_WISII_PRINT);
		// try {
		WDWEUtil.renderTo(wisiibean);
		// } catch (FOVException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
	public static PrintState getPrintstate()
	{
		return printstate;
	}
	public static void setPrintstate(PrintState printstate)
	{
		EngineUtil.printstate = printstate;
	}
	public static void main(String[] args)
	{
		int c=2;
		for(int i=1;i<c;i++)
		{
			EngineThread t=new EngineThread(i);
			t.start();
//		    try
//			{
//				Thread.currentThread().sleep(100);
//			}
//			catch (InterruptedException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
//		WisiiBean bean = new WisiiBean();
//		bean.setXslFile("E:/test/bold.xsl");
//		bean.setXmlFile("E:/test/T004.xml");
//		bean.setOutputMode(MimeConstants.MIME_FLASH);
//		bean.setOutputfilename("E:/test/qq1.swf");
//		bean.setSelectedLayers("1");
//		try
//		{
//			WDWEUtil.renderTo(bean);
//		}
//		catch (FOVException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// EngineUtil.print(bean);
	}
	private static class EngineThread extends Thread
	{
		private int index;
		private EngineThread(int index)
		{
			this.index=index;
		}
		public void run() {
			WisiiBean bean = new WisiiBean();
			bean.setXslFile("E:/test/A00"+index+".xsl");
			bean.setXmlFile("E:/test/A00"+index+".xml");
			bean.setOutputMode(MimeConstants.MIME_FLASH);
//			bean.setPrintbyFlash("true");
			bean.setSelectedLayers("1");
			bean.setOutputfilename("E:/test/qq"+index+".swf");
			try
			{
				WDWEUtil.renderTo(bean);
			}
			catch (FOVException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	}
}
