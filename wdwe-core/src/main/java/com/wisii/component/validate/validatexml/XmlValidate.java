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
 */package com.wisii.component.validate.validatexml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.wisii.component.startUp.SystemUtil;

/**
 * 本类的功能主要是以SchemaTreeBuilder类作为Handler对Schema文件进行解析。
 *
 * @author zkl
 *
 */


public class XmlValidate {

	public static Map getAllDefinedElementsAndAttributeByFile(String xmlSchemaFile){
		
		try {
			
			return getAllDefinedElementsAndAttribute(new FileInputStream(new File(xmlSchemaFile)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Map getAllDefinedElementsAndAttribute(String xsd){
		
		try {
			
			return getAllDefinedElementsAndAttribute(new ByteArrayInputStream(xsd.getBytes(SystemUtil.FILE_CHARSET)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
/**
 * 主要调用此方法生成Map
 * @param xmlSchemaInput
 * @return
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws IOException
 */
	public static Map getAllDefinedElementsAndAttribute(InputStream xmlSchemaInput) throws ParserConfigurationException, SAXException, IOException{
//		MemoryTest ss=new MemoryTest();
//		ss.start();
		SAXParserFactory spfactory=SAXParserFactory.newInstance();
//		ss.end();
		spfactory.setFeature("http://xml.org/sax/features/namespaces", true);
//		ss.end();
		SAXParser parser=spfactory.newSAXParser();
//		ss.end();
		SchemaTreeBuilder treebuilder=new SchemaTreeBuilder();
//		ss.end();
		parser.parse(xmlSchemaInput, treebuilder);
//		ss.end();
		return treebuilder.getAllElementMap();
	}


	public static Map getAllDefinedElementsAndAttribute(String schemaStr,String xmlc) {
		InputStream schemaInput = null;
		if(schemaStr==null || schemaStr.trim().length() == 0){
			schemaInput=readXSDFromXML(xmlc);
		}
        else
        {
            if(schemaStr != null)
            {
                schemaStr = schemaStr.trim();
                try
                {
                    schemaInput = new ByteArrayInputStream(schemaStr.getBytes("UTF-8"));
                }
                catch(IOException io)
                {
                    io.printStackTrace();
                }
            }
		}

		if(schemaInput==null){
			System.err.println("#XmlValidate.getAllDefinedElementsAndAttribute()#  "
					+"ErrorMsg: can not read schemaFile, please make sure that the schemaFile is in correct path!");
			return null;
		}
		try {
			return getAllDefinedElementsAndAttribute(schemaInput);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				schemaInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static InputStream readXSDFromXML(String xmlStr) {
		String schemaFileName="";
		int locationIndex=xmlStr.indexOf("noNamespaceSchemaLocation");
		if(locationIndex==-1){
			locationIndex=xmlStr.indexOf("SchemaLocation");
		}
		if(locationIndex!=-1){
			xmlStr=xmlStr.substring(locationIndex);
			int firstIndex=xmlStr.indexOf('"');
			xmlStr=xmlStr.substring(firstIndex+1);
			firstIndex=xmlStr.indexOf('"');
			schemaFileName=xmlStr.substring(0,firstIndex).trim();
		}
        else
        {
            return null;
        }

        if(SystemUtil.getBaseURL() != null && SystemUtil.getBaseURL().trim().length() > 0)
        {
            schemaFileName = SystemUtil.getBaseURL().trim() + SystemUtil.VALIDATEPATH + schemaFileName;
        }
//        System.out.println("#XmlValidate.readXSDFromXML()#  schemaFileName=" + schemaFileName);

		try {
			return new FileInputStream(schemaFileName);
		} catch (FileNotFoundException e) {
			return null;
		}

	}



	public static void main(String[] args){
		Map m = null;
		m = getAllDefinedElementsAndAttribute("test.xsd");
		System.out.println(m);
	}

}
