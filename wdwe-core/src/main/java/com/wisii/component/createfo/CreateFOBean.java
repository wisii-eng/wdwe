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
 */package com.wisii.component.createfo;

import java.io.InputStream;

import com.wisii.component.startUp.SystemUtil;
public class CreateFOBean
{
	private InputStream xslc = null;//xsl模板流
	//private String xslc=null;  xsl模板字符串
	
	private InputStream xmlc = null;//xml数据流
	//private Strinf xslc=null;  xsl数据字符串
	
	private boolean background = true;//是否显示有背景的数据 true:显示,false:不显示
	
	private boolean editable = false;//数据是否可以编辑 true:可以,false:不可以
	
	private String baseurl = null;//xsl中图片和document()方法引用外部xml文件所对应baseURL
	


	
	public void setBackground(boolean b)
	{
		background = b;
	}
	
	public void setBackground(String b)
	{
		if("yes".equalsIgnoreCase(b))
		{
			background = true;
		}
		else
		{
			background = false;
		}
	}
	
	public void setEditable(boolean b)
	{
		editable = b;
	}
	
	public void setEditable(String b)
	{
		if("yes".equalsIgnoreCase(b))
		{
			editable = true;
		}
		else
		{
			editable = false;
		}
	}
	
	public void setBaseURL(String str)
	{
		baseurl = str;
	}


	
//	//----------pw add------------------	
//	public InputStream getXslc()
//	{
//		InputStream[] copyxslt=SystemUtil.getCopyInputStream(xslc);
//		this.xslc=copyxslt[0];
//		return copyxslt[1];
//	}
//	
//	public InputStream getXmlc()
//	{
//		
//		InputStream[] copyxslt=SystemUtil.getCopyInputStream(xmlc);
//		this.xmlc=copyxslt[0];
//		return copyxslt[1];
//		
//	}
//-------------pw add------------------	
	public boolean getBackground()
	{
		return background;
	}
	
	public boolean getEditable()
	{
		return editable;
	}
	
	public String getBaseURL()
	{
		return baseurl;
	}
	
	public String toString()
	{
		String str = "xslc is " + xslc + '\n' +"xmlc is " + xmlc + '\n'+ "background is " + background + '\n' +
		"editable is " + editable + '\n' + "baseurl is " + baseurl;
		
		return str;
	}

	/**
	 * @return the xslc
	 */
	public InputStream getXslc() {
		return xslc;
	}

	/**
	 * @param xslc the xslc to set
	 */
	public void setXslc(InputStream xslc) {
		this.xslc = xslc;
	}

	/**
	 * @return the xmlc
	 */
	public InputStream getXmlc() {
		return xmlc;
	}

	/**
	 * @param xmlc the xmlc to set
	 */
	public void setXmlc(InputStream xmlc) {
		this.xmlc = xmlc;
	}
}
