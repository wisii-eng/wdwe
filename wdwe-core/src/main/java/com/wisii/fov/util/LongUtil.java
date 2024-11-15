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

import java.io.IOException;
import java.util.Date;

public class LongUtil
{
	public static void main(String[] args) throws IOException
	{

		// FileWriter fw = new FileWriter(new File("C:/Test", "1008.a"));

		long currTime = new Date().getTime();
		byte[] s = long2bytes(109);

	}

	public static byte[] long2bytes(long a)
	{

		byte[] datas = new byte[8];
		if (a == 0)
			return datas;
		datas[7] = (byte) (0xffl & a);
		datas[6] = (byte) ((0xff00l & a) >> 8);
		datas[5] = (byte) ((0xff0000l & a) >> 16);
		datas[4] = (byte) ((0xff000000l & a) >> 24);
		datas[3] = (byte) ((0xff00000000l & a) >> 32);
		datas[2] = (byte) ((0xff0000000000l & a) >> 40);
		datas[1] = (byte) ((0xff000000000000l & a) >> 48);
		datas[0] = (byte) ((0xff00000000000000l & a) >> 56);

		long l = 0;

		l = datas[7] & 0xffl;

		l |= ((long) datas[6] << 8) & 0xff00l;
		l |= ((long) datas[5] << 16) & 0xff0000l;

		l |= ((long) datas[4] << 24) & 0xff000000l;

		l |= ((long) datas[3] << 32) & 0xff00000000l;

		l |= ((long) datas[2] << 40) & 0xff0000000000l;

		l |= ((long) datas[1] << 48) & 0xff000000000000l;

		l |= ((long) datas[0] << 56) & 0xff00000000000000l;
		return datas;
	}
}
