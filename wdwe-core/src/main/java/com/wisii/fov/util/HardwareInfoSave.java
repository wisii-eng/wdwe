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
 * @HardwareInfoSave.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.fov.util;

import java.io.FileOutputStream;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;



/**
 * 类功能描述：硬件信息保存类
 * 
 * 作者：zhangqiang 创建日期：2009-8-5
 */
public class HardwareInfoSave
{
	static boolean save(String filepath, String diskid, String mac,
			String processorid)
	{
		/*
		 * 【20090828 刘晓修改将】 if (filepath == null || filepath.trim().isEmpty() ||
		 * diskid == null 【替换为】 if (filepath == null ||
		 * "".equalsIgnoreCase(filepath) || diskid == null
		 */
		if (filepath == null || "".equalsIgnoreCase(filepath) || diskid == null
				|| mac == null || processorid == null)
		{
			return false;
		}
		try
		{
			FileOutputStream out = new FileOutputStream(filepath.trim());
			BASE64Encoder base64encoder = new BASE64Encoder();
			byte[] datas = base64encoder.encode(
					(diskid + "," + mac + "," + processorid).getBytes())
					.getBytes();
			out.write(datas);
			return true;

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static boolean save(String filepath)
	{
		try
		{
			FileOutputStream out = new FileOutputStream(filepath.trim());
			BASE64Encoder base64encoder = new BASE64Encoder();
			byte[] datas = base64encoder
					.encode(
							(HardWareInfoGetter.getDiskID() + ","
									+ HardWareInfoGetter.getMac() + "," + HardWareInfoGetter
									.getProcessorid()).getBytes()).getBytes();
			out.write(datas);
			return true;

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
