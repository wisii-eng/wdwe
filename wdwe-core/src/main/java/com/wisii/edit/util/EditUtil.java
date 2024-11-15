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
 * EditUtil.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.util;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import com.wisii.component.mainFramework.commun.CommincateFactory;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.data.MaintainData;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.fov.command.plugin.FOMethod;

/**
 * 类功能说明：
 * 作者：zhangqiang
 * 日期:2013-1-9
 */
public class EditUtil
{
	public static void saveXml()
	{
		String xml = getXml();
		if (xml != null)
		{
			saveXml(xml);
		}
	}
	public static void saveXmlToWisiiBean()
	{
		String xml = getXml();
		if (xml != null)
		{
			WisiiBean wisiibean = EngineUtil.getEnginepanel().getWisiibean();
			wisiibean.setXml(xml);
		}
	}
	private static void saveXml(String xml)
	{
		WisiiBean wisiibean = EngineUtil.getEnginepanel().getWisiibean();
		wisiibean.setXml(xml);
		Map<String, Object> sas = new HashMap();
		sas.put("docid", wisiibean.getDocID());
		sas.put("xml", xml);
		sas.put("edittempid", wisiibean.getEditTemplateId());
		sas.put("userpara", wisiibean.getUserPara());
		sas.put("authorityid", wisiibean.getAuthorityId());
		sas.put("templatepara", wisiibean.getTemplatePara());
		// 【刘晓添加】 用于提交的时候如果不是Java环境则直接提交到网络地址
		String add = wisiibean.getSubmitAdd();
		if (add == null || "null".equalsIgnoreCase(add))
			add = "";
		add = SystemUtil.getURLDecoderdecode(add);
		WdemsDateType out = null;
		if (add.length() > 1)
		{
			if (add.startsWith("http"))
			{
				out = CommincateFactory.makeComm(add).send(
						SystemUtil.SER_SUBMITXML, sas);
			}
			else
				out = CommincateFactory.makeComm(
						CommincateFactory.serverUrl + add).send(
						SystemUtil.SER_SUBMITXML, sas);
		}
		else
		{
			out = CommincateFactory.makeComm(
					CommincateFactory.serverUrl + CommincateFactory.requestUrl)
					.send(SystemUtil.SER_SUBMITXML, sas);
		}
		Object ss = out.getReturnDateType();
		if (ss instanceof LinkedList)
		{
			if (ss != null && ((LinkedList) ss).size() == 1)
			{
				StatusbarMessageHelper.output("提交过程中出现错误，提交未成功",
						((LinkedList) ss).poll().toString(),
						StatusbarMessageHelper.LEVEL.INFO);
				return;
			}
		}
		else if (ss != null && !"".equalsIgnoreCase(ss.toString())
				&& !"sucess".equals(ss.toString()))
		{
			StatusbarMessageHelper.output("提交过程中出现错误，提交未成功", ss.toString(),
					StatusbarMessageHelper.LEVEL.INFO);
			return;
		}
		StatusbarMessageHelper.output("提交成功", wisiibean.getDocID(),
				StatusbarMessageHelper.LEVEL.INFO);
		EditStatusControl.setSubData(true);
	}
	public static String getXml()
	{
		StringBuffer sb = new StringBuffer();
		WisiiBean wisiibean = null;
		try
		{
			wisiibean = EngineUtil.getEnginepanel().getWisiibean();
			String s = MaintainData.Xquery("/");
			String xmlnm = wisiibean.getXmlnm();
			if (xmlnm != null)
			{
				int a = s.indexOf('>');
				StringBuilder as = new StringBuilder(s.substring(0, a));
				as.append(" " + wisiibean.getXmlnm());
				as.append(s.substring(a));
				s = as.toString();
			}
			String xmlhead = wisiibean.getXmlhead();
			if (xmlhead != null && !xmlhead.isEmpty())
			{
				sb.append(xmlhead);
			}
			sb.append(s);
			if (sb.length() == 0)
			{
				return wisiibean.getXmlString();
			}
			else
			{
				return sb.toString();
			}
		}
		catch (Exception e)
		{
			if (wisiibean != null)
			{
				return wisiibean.getXmlString();
			}
			return null;
		}
	}
	public static final long INSTANCEID = new Date().getTime();
}
