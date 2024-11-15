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
 *//* $Id: PNGReader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.image.io;

// Java
import java.io.IOException;
import java.io.InputStream;

import com.wisii.image.ImageInfo;

/**
 * ImageReader object for PNG image type.
 * 
 * @author Pankaj Narula
 * @version $Id: PNGReader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $
 */
public class PNGReader implements ImageReader
{

	private static final int PNG_SIG_LENGTH = 24;

	/** @see com.wisii.image.io.fov.image.analyser.ImageReader */
	public ImageInfo verifySignature(InputStream bis) throws IOException
	{
		byte[] header = getDefaultHeader(bis);
		boolean supported = ((header[0] == (byte) 0x89)
				&& (header[1] == (byte) 0x50) && (header[2] == (byte) 0x4e)
				&& (header[3] == (byte) 0x47) && (header[4] == (byte) 0x0d)
				&& (header[5] == (byte) 0x0a) && (header[6] == (byte) 0x1a) && (header[7] == (byte) 0x0a));

		if (supported)
		{
			ImageInfo info = getDimension(header);
			info.setMimeType(getMimeType());
			return info;
		} else
		{
			return null;
		}
	}

	/**
	 * Returns the MIME type supported by this implementation.
	 * 
	 * @return The MIME type
	 */
	public String getMimeType()
	{
		return "image/png";
	}

	private ImageInfo getDimension(byte[] header)
	{
		ImageInfo info = new ImageInfo();

		// png is always big endian
		int byte1 = header[16] & 0xff;
		int byte2 = header[17] & 0xff;
		int byte3 = header[18] & 0xff;
		int byte4 = header[19] & 0xff;
		long l = (long) ((byte1 << 24) | (byte2 << 16) | (byte3 << 8) | (byte4));
		info.setWidth((int) l);

		byte1 = header[20] & 0xff;
		byte2 = header[21] & 0xff;
		byte3 = header[22] & 0xff;
		byte4 = header[23] & 0xff;
		l = (long) ((byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4);
		info.setHeight((int) l);
		return info;
	}

	private byte[] getDefaultHeader(InputStream imageStream) throws IOException
	{
		byte[] header = new byte[PNG_SIG_LENGTH];
		try
		{
			imageStream.mark(PNG_SIG_LENGTH + 1);
			imageStream.read(header);
			imageStream.reset();
		} catch (IOException ex)
		{
			try
			{
				imageStream.reset();
			} catch (IOException exbis)
			{
				// throw the original exception, not this one
			}
			throw ex;
		}
		return header;
	}

}
