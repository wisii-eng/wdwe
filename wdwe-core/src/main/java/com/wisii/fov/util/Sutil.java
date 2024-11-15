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
 */package com.wisii.fov.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.wisii.component.startUp.SystemUtil;

public final class Sutil
{
	private static Map<String, Object> funcmap = null;
	private static long c = System.currentTimeMillis();
	static
	{
		init();
	}
	private static synchronized void init()
	{
		if (funcmap != null && !funcmap.isEmpty())
		{
			return;
		}
		funcmap = new HashMap<String, Object>();
		funcmap.put("yuyu", 3131313131313l);
	}
	private static InputStream getIns()
	{
		return SystemUtil.class.getClassLoader().getResourceAsStream(
				"license.lic");
	}
	public static Object getF(String s)
	{
		if(getIns() == null) return null;
		init();
		if (funcmap != null)
		{
			return funcmap.get(s);
		}
		return null;
	}
	public static void sc(long l)
	{
		if (l > c)
		{
			c = l;
		}
	}
	public static long gc()
	{
		return c;
	}
}
