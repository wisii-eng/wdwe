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
 */package com.wisii.component.createrender;

import java.util.List;

import com.wisii.component.setting.PrintSetting;
import com.wisii.component.setting.WisiiBean;


public class CreateRenderBean
{
	/* 【添加：START】 by 李晓光2009-12-21 */
	//主要用于描述打印类型，在不预览直接打印时，排版分为后台、前台处理两种情况，
	//针对这两种情况，出现了后台直接打印、前台打印的效果【服务端打印、客户端打印】。
	public static enum PrintType{
		//常规打印。
		NORMAL,
		//后台打印【服务端打印】
		BACK_PRINT,
		//前台打印【客户端打印】
		FRONT_PRINT,
	}
	private PrintType printType = PrintType.NORMAL;
	
	public PrintType getPrintType() {
		return printType;
	}

	public void setPrintType(PrintType printType) {
		this.printType = printType;
	}
	/* 【添加：END】 by 李晓光2009-12-21 */
	private List pageViewPortList = null; //输入的pageViewPort列表
	
	private String renderName = null;//标识要生成的render
	
//	页面的打印信息列表 格式:"AWT,Microsoft Office Document Image Writer,0#AWT,Microsoft Office Document Image Writer,0#PCL,\\\\192.168.0.10\\HP LaserJet 6P,0"
	private PrintSetting  printSetting = null;
	private WisiiBean  wisiibean;
	
//	private String printerName;
//	public String getPrinterName()
//	{
//		return printerName;
//	}
//
//	public void setPrinterName(String printerName)
//	{
//		this.printerName = printerName;
//	}

	/**
	 * @return the wisiibean
	 */
	public WisiiBean getWisiibean() {
		return wisiibean;
	}

	/**
	 * @param wisiibean the wisiibean to set
	 */
	public void setWisiibean(WisiiBean wisiibean) {
		this.wisiibean = wisiibean;
	}

	public List getPageViewPortList()
	{
		return pageViewPortList;
	}

	public void setPageViewPortList(List pageViewPortList)
	{
		this.pageViewPortList = pageViewPortList;
	}

	public String getRenderName()
	{
		return renderName;
	}

	public void setRenderName(String renderName)
	{
		this.renderName = renderName;
	}

//	public List getPrinterList()
//	{
//		if(printSetting == null )
//			{
//				return null;
//			}
//	       Map mp=printSetting.getPrintRefMap();
//	       
//	       ArrayList PrinterList = new ArrayList(); 
//	       PrinterList.add(new PrinterInfo("AWT", "", "0"));
//	       
//		if (mp != null) {
//			Iterator it = ((Collection) mp.values()).iterator();
//			while (it.hasNext()) {
//				PrintRef rf = (PrintRef) it.next();
//				 PrinterInfo pf= new PrinterInfo(rf.getOutputMode() , rf.getPrinter(), rf.getCommonLine());
//				 PrinterList.add(pf);
//			}
//		}
//			
//
//			return PrinterList;
//		
//	}
//	public List getPrintList()
//	{
//		if(printSetting == null || "".equals(printSetting))
//		{
//			return null;
//		}
//        String[] rowDate = printSetting.split("#");
//        ArrayList PrinterList = new ArrayList();
//        
//        	PrinterList.add(new PrinterInfo("AWT", "", "0"));
//        
//        for(int i = 0; i < rowDate.length ; i++)
//        {
//            String[] columDate = rowDate[i].split(",");
//
//            PrinterInfo pf;
//            if(columDate.length == 2)
//            {
//                pf = new PrinterInfo(columDate[0] , columDate[1], "");
//            }
//            else
//            {
//                pf = new PrinterInfo(columDate[0] , columDate[1], columDate[2]);
//            }
//
//            PrinterList.add(pf);
//        }
//		
//		return PrinterList;
//	}

	/**
	 * @return the printSetting
	 */
	public PrintSetting getPrintSetting() {
		return printSetting;
	}

	public void setPrintSetting(PrintSetting tprintSetting) {
		
		printSetting=tprintSetting ;
		
	}	
}
