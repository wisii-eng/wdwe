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
 * @PathItemInfo.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.font.model;

import java.io.File;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-10-25
 */
public final class PathItemInfo
{
	private String metrics_url;
	private String embed_url;

	public PathItemInfo( String embedUrl,String ttcname, File rootpath)
	{
		int index = embedUrl.lastIndexOf(File.separator);
		if (index == -1)
		{
			index = embedUrl.lastIndexOf('/');
		}
		String filename = embedUrl.substring(index + 1);
		index = filename.lastIndexOf('.');
		if (index != -1)
		{
			filename = filename.substring(0, index);
		}
		if (ttcname != null)
		{
			filename = filename + "_" + ttcname;
		}
		metrics_url = rootpath.getAbsolutePath() + File.separator + filename
				+ ".xml";
		embed_url = embedUrl;
	}

	/**
	 * @返回 metrics_url变量的值
	 */
	public String getMetrics_url()
	{
		return metrics_url;
	}

	/**
	 * @返回 embed_url变量的值
	 */
	public String getEmbed_url()
	{
		return embed_url;
	}

}
