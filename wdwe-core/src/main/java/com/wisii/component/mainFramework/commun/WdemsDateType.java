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
 */package com.wisii.component.mainFramework.commun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.wisii.component.setting.WisiiBean;
import com.wisii.fov.command.plugin.FOMethod;

/**
 * 该类用于封装统一客户端传递过来的数据以及服务段客户端传递过来的数据以统一的形式输出
 * 
 * @author liuxiao 20090303
 * 
 * 
 */
public class WdemsDateType {

	private Object inReturnDateType = null;

	public static void main(String[] args) {
		System.out.println((int) (Math.random() * 36));

	}

	public WdemsDateType(Object in) {
		this.inReturnDateType = in;
	}

	public Object getReturnDateType() {
		if (inReturnDateType instanceof InputStream) {
			String line = ""; // 读取流每一行的数据
			
			BufferedReader render = new BufferedReader(new InputStreamReader(
					(InputStream) this.inReturnDateType));

			try {
				while ((line = render.readLine()) != null) {
					if (line.trim().length() > 0) {

						return FOMethod.getObjectByStream(line);
//						return line;
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			} finally {

				if (render != null) {
					try {
						render.close();
					} catch (Exception f) {
						f.printStackTrace();

					}
				}

			}
		} else if (inReturnDateType instanceof LinkedList) {
			while (inReturnDateType == null
					|| ((LinkedList) inReturnDateType).size() < 1) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}

			Object object = ((LinkedList) inReturnDateType).poll();
			
			if (object instanceof Integer
					&& ((Integer) object).intValue() == -1) {
			} else
				return FOMethod.getObjectByStream((String)object);
		}
		else if(inReturnDateType instanceof String)
		{
			return inReturnDateType;
		}
		return null;

	}

	public WisiiBean getWisiiBean(Map para, HttpServletRequest request) {
		//TODO
		/*WisiiBean wb = null;
		try {
			wb = ((WisiiBean) request.getSession().getAttribute(
					SystemUtil.WISIIBEAN));
			type = this.SESSIONT;
		} catch (Exception se) {
			type = this.DATASTORET;
			if (Start.dataStore.size() > 0)
				wb = (WisiiBean) Start.dataStore.get(SystemUtil.WISIIBEAN);
			if (wb == null)

				
				wb = (WisiiBean) para.get("para");
		}
		return wb;
		*/
		return null;
	}

	public void saveWisiiBean(WisiiBean wb, HttpServletRequest request) {
		//TODO
		/*if (type == this.SESSIONT) {
			request.getSession().setAttribute(SystemUtil.WISIIBEAN, wb);
		}
		if (type == this.DATASTORET) {
			Start.dataStore.put(SystemUtil.WISIIBEAN, wb);
		}*/
	}

	public void flush(Object ss) {
		if (inReturnDateType instanceof PrintWriter) {
			
			((PrintWriter) inReturnDateType).println(ss);
		
			((PrintWriter) inReturnDateType).close();
		}
		else if (inReturnDateType instanceof LinkedList) {
			if(ss==null)ss=new Integer(-1);
			((LinkedList) inReturnDateType).add(ss);
			
		}
	}
	public Object getInReturnDateType()
	{
		return inReturnDateType;
	}
}
