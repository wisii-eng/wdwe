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

import java.io.Serializable;
import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.mainFramework.commun.CommunicateProxy;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.area.PageViewport;

public class PageViewPortListener extends ListListener implements Serializable
{	
	private static final long serialVersionUID = 2147894690410749413L;

	/** 向客户端发送PageViewport数据的JspWriter */
  
    
    /** user agent    */
    protected FOUserAgent _userAgent;
    private CommunicateProxy  commPro;
    public Object out;
    
	public PageViewPortListener(CommunicateProxy cm)
	{
		commPro = cm;
	}
	
	//发送数据
	public void listener()
	{
		try
		{
			//发送当前页面的PageViewport数据到客户端
			PageViewport page = (PageViewport)listen.get(listen.size() - 1);
			
			commPro.reSendData(page, out);
			
//	        String outputStr = generateString(page);
//	        if(outputStr == null)
//	        {
//	            throw new FOVException("PageViewport对象序列化失败");
//	        }
//	        
//	        if(_writer != null)
//	        {
//	        	_writer.println(outputStr);
//		        _writer.flush();
//	        }
//            
//	        //发送动态表的信息到客户端
//	        Map tableinfo = _userAgent.getTableinfo();
//	        commPro.reSendData(tableinfo, out);
	        
//	        outputStr = generateString(tableinfo);
//	        if(outputStr != null && _writer != null)
//	        {
//	            _writer.println(outputStr);
//	            _writer.flush();
//	        }
	        
	        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
    /** 设置向客户端发送数据的writer */
    public void setOutputStream(Object mOutputStream)
    {
        this.out = mOutputStream;
    }
    
    public void setUserAgent(FOUserAgent agent)
    {
        _userAgent = agent;
    }
    
//    /**
//     * 把对象转化为BASE64的字符串
//     * @param obj Object 被转化的对象
//     * @return String 由对象转化而来的字符串
//     */
//    public static String generateString(Object obj) throws IOException
//    {
//        if(obj == null)
//        {
//            return null;
//        }
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = null;
//        String outputStr = null;
//        BASE64Encoder encoder = new BASE64Encoder();
//
//        try
//        {
//            oos = new ObjectOutputStream(bos);
//            oos.writeObject(obj);
//            outputStr = encoder.encode(bos.toByteArray());
//            oos.flush();
//            oos.close();
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//        finally
//        {
//            if(oos != null)
//            {
//                oos.close();
//            }
//        }
//
//        outputStr =  outputStr.replaceAll("\r\n", "%0D%0A");
//        outputStr =  outputStr.replaceAll("\n", "%0A");
//        outputStr =  outputStr.replaceAll("\r", "%0D");
//        return outputStr;
//    }

}
