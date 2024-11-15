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
 * TestFrame.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.view;

import javax.swing.JFrame;

import com.wisii.component.setting.WisiiBean;
import com.wisii.edit.util.EngineUtil;
import com.wisii.fov.apps.MimeConstants;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-6
 */
public class TestFrame extends JFrame{
	public TestFrame(WisiiBean wisiibean)
	{
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(EngineUtil.getEnginePanel(wisiibean));
		setSize(800, 600);
		setVisible(true);
		EngineUtil.getEnginepanel().start();
	}
	private static WisiiBean getWisiiBean()
	{
		WisiiBean bean=new WisiiBean();
		bean.setXslFile("d:/test/a.xsl");
		bean.setXmlFile("d:/test/demo.xml");
		bean.setAuthorityId("default");
		bean.setOutputMode(MimeConstants.MIME_WISII_WDDE_PREVIEW);
		return bean;
	}
	public static void main(String[] args) {
		try{
		new TestFrame(getWisiiBean());
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

}
