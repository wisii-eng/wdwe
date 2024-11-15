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
 *    整体验证xml
 *    Version 1.0
 *    汇智互联
 */

package com.wisii.component.validate.validatexml;

import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;


public class SchemaDTDXml
{
    private static final String NAMESPACE_SCHEMA_LOCATION = "noNamespaceSchemaLocation";
    private static final String NAME_SCHEMA_LOCATION = "SchemaLocation";
    private static final String DTD_LOCATION = "!DOCTYPE";
    private static boolean ifsucceed=true;

    /**
     *   通过javaAPI整体验证xml数据
     *   @param xmlStream InputStream
     *   @param schemaStream InputStream
     *   @return boolean
     */
    public static boolean checkXml(InputStream xmlStream, InputStream schemaStream) //通过javaAPI整体验证xml数据,传入参数为流型
    {
        if(xmlStream == null) //xml为空，直接返回false
        {
            return false;
        }

        StringBuffer xmlstrBuf = new StringBuffer();
        StringBuffer schemastrBuf = null;

        try
        {
            InputStreamReader isr = null;
            isr = new InputStreamReader(xmlStream, "UTF-8");
            int c = 0;
            while((c = isr.read()) != -1)
            {
                xmlstrBuf.append((char)c);
            }
            isr.close();

            if(schemaStream != null)
            {
                schemastrBuf  = new StringBuffer();
                isr = new InputStreamReader(schemaStream, "UTF-8");
                while((c = isr.read()) != -1)
                {
                    schemastrBuf.append((char)c);
                }
                isr.close();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return checkXml(xmlstrBuf.toString(), schemastrBuf.toString()); //转换成字符串，调用checkXml(String xmlString, String schemaString)方法
    }

    /**
     *   通过javaAPI整体验证xml数据
     *   @param xmlStream String
     *   @param schemaString String
     *   @return boolean
     */
    public static boolean checkXml(String xmlString, String schemaString) //传入参数为string型
    {
        long times = 0;
boolean s=true;
        if(SystemUtil.PRINT_RUN_TIME)
        {
            times = System.currentTimeMillis();
//            System.err.println("TIME_整体验证xml数据开始。时间（毫秒）：" + times);
        }

        if(xmlString == null || xmlString.trim().length() == 0) //xml为空，直接返回false
        {
            System.err.println("xml数据为空，验证失败");
            return false;
        }

        InputStream xmlStream = null;
        InputStream schemaStream = null;

        try
        {
            xmlStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            if(schemaString != null && schemaString.trim().length() > 0) //schema存在时
            {
//                if(xmlString.indexOf(NAMESPACE_SCHEMA_LOCATION) != -1 || xmlString.indexOf(NAME_SCHEMA_LOCATION) != -1)
                if(xmlString.indexOf(DTD_LOCATION) != -1)
                {
                    s=validate(xmlStream);
                }
                else
                {
                    schemaStream = new ByteArrayInputStream(schemaString.getBytes("UTF-8"));
                    s=validate(xmlStream, schemaStream);
                }
            }
            else //schema作为路径在xml中时
            {
                if(xmlString.indexOf(NAMESPACE_SCHEMA_LOCATION) != -1 || xmlString.indexOf(NAME_SCHEMA_LOCATION) != -1)
                {
                    String schmePath = getPathName(xmlString);
                    if(schmePath == null || schmePath.trim().length() == 0)
                    {
                        System.err.println("schema file should be specified.");
                        return true;
                    }

                    schemaStream = new BufferedInputStream(new FileInputStream(schmePath));
                    s=validate(xmlStream, schemaStream);
                }
                else if(xmlString.indexOf(DTD_LOCATION) != -1)
                {
                    s=validate(xmlStream);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace(); //发生异常，返回false
            return false;
        }
        finally
        {
            try
            {
                if(xmlStream != null)
                {
                    xmlStream.close();
                }
                if(schemaStream != null)
                {
                    schemaStream.close();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }

            if(SystemUtil.PRINT_RUN_TIME)
            {
                long timeEnd = System.currentTimeMillis();
//                System.err.println("TIME_整体验证xml数据结束。时间（毫秒）：" + timeEnd);
                System.err.println("\n\nTIME_整体验证xml数据。消耗时间（毫秒）：" + (timeEnd - times));
            }
        }

        return s;
    }

    /**
     *   使用Schema通过javaAPI整体验证xml数据
     *   @param in InputStream
     *   @param schema InputStream
     *   @return boolean
     */
    private static boolean validate(InputStream in, InputStream schema) throws Exception
    {
    	
    	ifsucceed=true;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        //设置schema文档属性
        factory.setAttribute(
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute(
            "http://java.sun.com/xml/jaxp/properties/schemaSource", schema);
        //--------------以下同DTD验证------------------//
        DocumentBuilder parser = factory.newDocumentBuilder();
        parser.setErrorHandler(
            new org.xml.sax.ErrorHandler()
        {
            public void fatalError(SAXParseException spe) 
            {
            	ifsucceed=false;
            	StatusbarMessageHelper
    			.output(
    					"整体验证失败",
    					spe.getMessage(),
    					StatusbarMessageHelper.LEVEL.INFO);
            }

            public void error(SAXParseException spe) 
            {
            	ifsucceed=false;
            	StatusbarMessageHelper
    			.output(
    					"整体验证失败",
    					spe.getMessage(),
    					StatusbarMessageHelper.LEVEL.INFO);
            }

            public void warning(SAXParseException err) 
            {
            	ifsucceed=false;
            	StatusbarMessageHelper
    			.output(
    					"整体验证失败",
    					err.getMessage(),
    					StatusbarMessageHelper.LEVEL.INFO);
            }
        });
        parser.parse(in);

        return ifsucceed;
    }

    /**
     *   使用DTD整体验证xml数据
     *   @param in InputStream(含有DTD声明!DOCTYPE)
     *   @param schema InputStream
     *   @return boolean
     */
    private static boolean validate(InputStream in) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        DocumentBuilder parser = factory.newDocumentBuilder();
        parser.setErrorHandler(
            new org.xml.sax.ErrorHandler()
        {
            public void fatalError(SAXParseException spe) throws SAXException
            {
                throw spe;
            }

            public void error(SAXParseException spe) throws SAXParseException
            {
                throw spe;
            }

            public void warning(SAXParseException err) throws SAXParseException
            {
            }
        });
        parser.parse(in);

        return true;
    }

    /**
     *   如果验证文件的名字写在xml中则截取文件名，拼装成相应的schema路径
     *   @param xmlStream InputStream
     *   @return boolean
     */
    private static String getPathName(String xmlStr)
    {
        String schemaFileName = null;
        int locationIndex = xmlStr.indexOf(NAMESPACE_SCHEMA_LOCATION);
        if(locationIndex == -1)
        {
            locationIndex = xmlStr.indexOf(NAME_SCHEMA_LOCATION);
        }

        if(locationIndex != -1)
        {
            xmlStr = xmlStr.substring(locationIndex);
            int firstIndex = xmlStr.indexOf('"');
            xmlStr = xmlStr.substring(firstIndex + 1);
            firstIndex = xmlStr.indexOf('"');
            schemaFileName = xmlStr.substring(0, firstIndex).trim();
        }
        else
        {
            return null;
        }

        if(SystemUtil.getBaseURL() != null && SystemUtil.getBaseURL().trim().length() > 0)
        {
            schemaFileName = SystemUtil.getBaseURL().trim() + SystemUtil.VALIDATEPATH + schemaFileName;
        }
        //        System.out.println("#SchemaDTDXml.getPathName()#  schemaFileName=" + schemaFileName);
        return schemaFileName;
    }
}
