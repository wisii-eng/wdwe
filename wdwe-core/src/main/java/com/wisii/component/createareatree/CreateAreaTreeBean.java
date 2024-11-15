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
 */package com.wisii.component.createareatree;

import java.io.InputStream;
import java.util.Map;
import javax.xml.transform.Source;
import com.wisii.component.mainFramework.commun.CommunicateProxy;

public class CreateAreaTreeBean
{	
	private InputStream instream = null;//Fo InputStream
	public String  foString = null;//Fo InputStream
	private Map configMap = null;
	
	public CommunicateProxy cp;
	

	
	/** 向客户端发送PageViewport数据的OutputStream */
    private Object out;
    
    
    public InputStream sourcefile = null;  // either FO or XML/XSLT usage
    public String sourcedir =null; // Use to save the absolute path of the file.
    public InputStream stylesheet = null; // for XML/XSLT usage
    public String styledir = null;

    private Map templateMap;
	private String baseUrl = null;//xsl中图片和document()方法引用外部xml文件所对应baseURL
    public String sourceString = null;
    public String styleString = null;
//	private boolean background = true;//是否显示有背景的数据 true:显示,false:不显示
    //【刘晓注掉20090617】
//	private boolean editable = false;//数据是否可以编辑 true:可以,false:不可以
	
	private  String outputMode;
	public Source xmlsource;
	public String getOutputMode()
	{
		return outputMode;
	}

	public void setOutputMode(String outputMode)
	{
		this.outputMode = outputMode;
	}

//xml

	public String getSourcedir()
	{
		return sourcedir;
	}

	public void setSourcedir(String sourcedir)
	{
		this.sourcedir = sourcedir;
	}

	public InputStream getSourcefile()
	{
		return sourcefile;
	}

	public void setSourcefile(InputStream sourcefile)
	{
		this.sourcefile = sourcefile;
	}

	public String getSourceString()
	{
		return sourceString;
	}

	public void setSourceString(String sourceString)
	{
		this.sourceString = sourceString;
	}
//xsl
	public String getStyledir()
	{
		return styledir;
	}

	public void setStyledir(String styledir)
	{
		this.styledir = styledir;
	}

	public String getStyleString()
	{
		return styleString;
	}

	public void setStyleString(String styleString)
	{
		this.styleString = styleString;
	}

	public InputStream getStylesheet()
	{
		return stylesheet;
	}

	public void setStylesheet(InputStream stylesheet)
	{
		this.stylesheet = stylesheet;
	}

	

//	public boolean isBackground()
//	{
//		return background;
//	}
//
//	public void setBackground(boolean background)
//	{
//		this.background = background;
//	}

	public CreateAreaTreeBean()
	{
		
	}

	public InputStream getInstream()
	{
		return instream;
	}

	public void setInstream(InputStream instream)
	{
		this.instream = instream;
	}

	public Map getConfigMap()
	{
		return configMap;
	}

	public void setConfigMap(Map configMap)
	{
		this.configMap = configMap;
	}

	public Object getOut()
	{
		return out;
	}

	public void setOut(Object out)
	{
		this.out = out;
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the foString
	 */
	public String getFoString() {
		return foString;
	}

	/**
	 * @param foString the foString to set
	 */
	public void setFoString(String foString) {
		this.foString = foString;
	}

	/**
	 * @return the templateMap
	 */
	public Map getTemplateMap() {
		return templateMap;
	}

	/**
	 * @param templateMap the templateMap to set
	 */
	public void setTemplateMap(Map templateMap) {
		this.templateMap = templateMap;
	}

	public final Source getXmlSource()
	{
		return xmlsource;
	}

	public final void setXmlSource(Source xmlsource)
	{
		this.xmlsource = xmlsource;
	}

}
