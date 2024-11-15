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
 */
/**
 * @RenderConfigUtil.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.font.util;

import java.io.IOException;
import java.util.List;
import com.wisii.font.finder.FontFileFinder;
import com.wisii.font.model.FontPathInfo;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-10-25
 */
public final class FontConfigUtil
{
	public static boolean configPDFFont(String metricsrooturl, String renderConfigpath)
	{
		try
		{
			List fontpaths = new FontFileFinder().find();
			List<FontPathInfo> fpinfoes = FontPathInfoUtil.getFontPathInfos(fontpaths, metricsrooturl);
			return RenderConfigCreater.createRenderConfig(fpinfoes, renderConfigpath);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public static void main(String[] args)
	{
		if(args==null||args.length<2)
		{
			 System.out.println(
		                "java " + FontConfigUtil.class.getName() + " renderfilepath renderconfigpath");
	         return;
		}
		if(configPDFFont(args[0],args[1]/*"C:\\testfont","C:\\testfont\\renderconfig.xml"*/))
		{
			System.out.println("配置字体成功");
		}
		else
		{
			System.out.println("配置字体不成功");
		}
	}
}
