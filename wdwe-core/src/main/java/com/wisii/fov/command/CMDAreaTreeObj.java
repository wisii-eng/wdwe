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
 */package com.wisii.fov.command;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.wisii.component.createareatree.CreateAreaTree;
import com.wisii.component.createareatree.CreateAreaTreeBean;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.server.command.AbstractServerCommand;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description: 加工areatree对象进行传输
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class CMDAreaTreeObj extends AbstractServerCommand {
	
	
	public CMDAreaTreeObj() {}
	
	
	//外部调用的方法
	public boolean execute(Object out, Object para, Object request) {
		return realExcute(out, para, request);
	}
	public boolean execute(Object out, Object para) {
		return realExcute(out, para);
	}
	
	//实际运行的方法
	private boolean realExcute(Object out, Object para, Object request){
//		System.out.println(Version.getVersion());
		
//		SystemUtil.LICFILEINPUTSTREAN=SystemUtil.class.getClassLoader().getResourceAsStream("license.lic");
//
//		SystemUtil.init();
		Map paraMap = (Map) para;
		if(!paraMap.containsKey("para")){
			System.out.println("参数WisiiBean为null");
			return false;
		}
		WisiiBean wb = (WisiiBean) paraMap.get("para");
// WisiiBean wisiiibean = new WisiiBean();
		
		try {
			HttpSession session = ((HttpServletRequest) request).getSession();
			
			WisiiBean wisiiibean = (WisiiBean) session.getAttribute("wisiibean");
			wb.setXmlFileName(wisiiibean.getXmlFileName());
			wb.setXml(wisiiibean.getXmlString());
			wb.setXslFileName(wisiiibean.getXslFileName());
			wb.setXsl(wisiiibean.getXslString());
			wb.setXsdFileName(wisiiibean.getXsdFileName());
			wb.setXsd(wisiiibean.getXsdString());
			wb.setFoFileName(wisiiibean.getFoFileName());
			wb.setFo(wisiiibean.getFoString());
		} catch (NullPointerException e) {
//			System.out.println("单机版方式");
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
//			System.out.println("单机版方式");
		}
		
// 测试用：
	/*
	 * String
	 * d="FrontServletTask&servlet/ClientfrontServlet?filename=pass_goods_in&awt&docID=0909898789&background=true&percent=200&editable=true&excursionX=0&excursionY=0&scaleX=100&scaleY=100&isSelectedHeightCheckBox=false&heightAddABS=0&orientationRequested=PORTRAIT&userPara=用户参数&printSquence=null";
	 * String[] parac = d.split(SystemUtil.getConfByName("base.devidegroup"));
	 * if (!parac[1].startsWith("servlet=") && !parac[1].startsWith("xml="))
	 * 
	 * parac[1] = d.substring(d.indexOf(SystemUtil
	 * .getConfByName("base.devidegroup")) + 1); wisiiibean = new WisiiBean();
	 * wisiiibean.setServerBaseUrl("D://lx//workspace//TestWdems//jsp/");
	 * wisiiibean.setXmlFileName("pass_goods_in.xml");
	 * wisiiibean.setXslFileName("pass_goods_in.xsl"); try {
	 * wisiiibean.parseOptions(parac);
	 *  } catch (FOVException e) {
	 * 
	 * e.printStackTrace(); }
	 */
		// ------------------------------------------------
		WdemsDateType wdt = new WdemsDateType(out);
		// 统一客户端方式会验证两遍有bug
		
		if(wb.getFoString()!=null )
		{}
		else{
		if (wb.getXmlString() == null) 
		{
			wdt.flush(null); 

		return false;
		
		}

		if (wb.getXslString()==null) 
		{
			wdt.flush(null);
			return false;
		}
		}
		CreateAreaTreeBean areatreebean = new CreateAreaTreeBean();
		if (wb.getFoString() != null) {
			areatreebean.setFoString(wb.getFoString());
		} else {
			
				areatreebean.setStyleString(wb.getXslString());
				areatreebean.setSourceString(wb.getXmlString());
				areatreebean.setXmlSource(wb.getXmlSource());
		}
		
		areatreebean.setOutputMode(wb.getOutputFormat());
		// 【刘晓注掉20090617】
		areatreebean.setTemplateMap(wb.getTemplatePara());
		
		if (wb.getBaseurl() != null) {
			areatreebean.setBaseUrl(wb.getBaseurl().getPath());
		} else
			try {
				wb.setBaseurl(new URL(SystemUtil.getBaseURL()));
			} catch (MalformedURLException e1) {
			
				try {
					wb.setBaseurl(new URL("file:"
							+ SystemUtil.getBaseURL()));
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				}
			}
		areatreebean.setBaseUrl(SystemUtil.getBaseURL());
		try {

			// CreateFO cfo = new CreateFO(fobean);
			// OutputStream[] foout = (OutputStream[])(cfo.execute(null));
			// ByteArrayInputStream foinput = new
			// ByteArrayInputStream(((ByteArrayOutputStream)foout[0]).toByteArray());
			//		
			// CreateAreaTreeBean areatreebean = new CreateAreaTreeBean();
			// areatreebean.setInstream(foinput);
			areatreebean.setOut(out);
			areatreebean.setBaseUrl(SystemUtil.getBaseURL());
			CreateAreaTree areatree = new CreateAreaTree(areatreebean);
			areatree.communproxy = this.communicateProxy;
			//开始执行AreaTree
			areatree.execute(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//实际运行的方法 add by px 2010-03-04
	private boolean realExcute(Object out, Object para){
		Map paraMap = (Map) para;
		if(!paraMap.containsKey("para")){
			System.out.println("参数WisiiBean为null");
			return false;
		}
		WisiiBean wb = (WisiiBean) paraMap.get("para");
		try {
			wb.setXmlFileName(wb.getXmlFileName());
			wb.setXml(wb.getXmlString());
			wb.setXslFileName(wb.getXslFileName());
			wb.setXsl(wb.getXslString());
			wb.setXsdFileName(wb.getXsdFileName());
			wb.setXsd(wb.getXsdString());
			wb.setFoFileName(wb.getFoFileName());
			wb.setFo(wb.getFoString());
		} catch (NullPointerException e) {
//			System.out.println("单机版方式");
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
//			System.out.println("单机版方式");
		}
		//System.out.println("realExcute===="+wb.getXmlString());
		// ------------------------------------------------
		WdemsDateType wdt = new WdemsDateType(out);
		// 统一客户端方式会验证两遍有bug
		if (wb.getXmlString() == null) 
		{
			wdt.flush(null); 

		return false;
		
		}

		if (wb.getXslString()==null) 
		{
			wdt.flush(null);
			return false;
		}

		CreateAreaTreeBean areatreebean = new CreateAreaTreeBean();
		if (wb.getFoString() != null) {
			areatreebean.setFoString(wb.getFoString());
		} else {
			
				areatreebean.setStyleString(wb.getXslString());
				areatreebean.setSourceString(wb.getXmlString());
				areatreebean.setXmlSource(wb.getXmlSource());
		}
		
		areatreebean.setOutputMode(wb.getOutputFormat());
		// 【刘晓注掉20090617】
		areatreebean.setTemplateMap(wb.getTemplatePara());
		
		if (wb.getBaseurl() != null) {
			areatreebean.setBaseUrl(wb.getBaseurl().getPath());
		} else
			try {
				wb.setBaseurl(new URL(SystemUtil.getBaseURL()));
			} catch (MalformedURLException e1) {
			
				try {
					wb.setBaseurl(new URL("file:"
							+ SystemUtil.getBaseURL()));
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				}
			}
		areatreebean.setBaseUrl(SystemUtil.getBaseURL());
		try {

			// CreateFO cfo = new CreateFO(fobean);
			// OutputStream[] foout = (OutputStream[])(cfo.execute(null));
			// ByteArrayInputStream foinput = new
			// ByteArrayInputStream(((ByteArrayOutputStream)foout[0]).toByteArray());
			//		
			// CreateAreaTreeBean areatreebean = new CreateAreaTreeBean();
			// areatreebean.setInstream(foinput);
			areatreebean.setOut(out);
			areatreebean.setBaseUrl(SystemUtil.getBaseURL());
			CreateAreaTree areatree = new CreateAreaTree(areatreebean);
			areatree.communproxy = this.communicateProxy;
			//开始执行AreaTree
			areatree.execute(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
