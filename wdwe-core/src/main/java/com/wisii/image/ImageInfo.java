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
 * @ImageInfo.java
 *                 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.Serializable;

/**
 * 类功能描述：
 * 
 * 作者：zhangqiang
 * 创建日期：2012-2-24
 */
public final class ImageInfo implements Serializable
{
	/** image width (in pixels) */
	private int width;
	/** image height (in pixels) */
	private int height;
	/** horizontal bitmap resolution (in dpi) */
	private double dpiHorizontal = Toolkit.getDefaultToolkit()
			.getScreenResolution();
	/** vertical bitmap resolution (in dpi) */
	private double dpiVertical = dpiHorizontal;
	/** MIME type of the image */
	private String mimeType;
	private int imagewidth = -1;
	private int imageheight = -1;
	private Image image;

	/**
	 * @返回 width变量的值
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @param width
	 *            设置width成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * @返回 height变量的值
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * @param height
	 *            设置height成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}

	/**
	 * @返回 dpiHorizontal变量的值
	 */
	public double getDpiHorizontal()
	{
		return dpiHorizontal;
	}

	/**
	 * @param dpiHorizontal
	 *            设置dpiHorizontal成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setDpiHorizontal(double dpiHorizontal)
	{
		this.dpiHorizontal = dpiHorizontal;
	}

	/**
	 * @返回 dpiVertical变量的值
	 */
	public double getDpiVertical()
	{
		return dpiVertical;
	}

	/**
	 * @param dpiVertical
	 *            设置dpiVertical成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setDpiVertical(double dpiVertical)
	{
		this.dpiVertical = dpiVertical;
	}

	/**
	 * @返回 mimeType变量的值
	 */
	public String getMimeType()
	{
		return mimeType;
	}

	/**
	 * @param mimeType
	 *            设置mimeType成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ImageInfo [dpiHorizontal=" + dpiHorizontal + ", dpiVertical="
				+ dpiVertical + ", height=" + height + ", mimeType=" + mimeType
				+ ", width=" + width + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(dpiHorizontal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(dpiVertical);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + height;
		result = prime * result
				+ ((mimeType == null) ? 0 : mimeType.hashCode());
		result = prime * result + width;
		return result;
	}

	/**
	 * @返回 imagewidth变量的值
	 */
	public int getImagewidth()
	{
		if (imagewidth == -1)
		{
			imagewidth = (int) Math.round(width
					* Toolkit.getDefaultToolkit().getScreenResolution()
					/ dpiHorizontal);
		}
		return imagewidth;
	}

	/**
	 * @返回 imageheight变量的值
	 */
	public int getImageheight()
	{
		if (imageheight == -1)
		{
			imageheight = (int) Math.round(height
					* Toolkit.getDefaultToolkit().getScreenResolution()
					/ dpiVertical);
		}
		return imageheight;
	}

	/**
	 * @返回 image变量的值
	 */
	public Image getImage()
	{
		return image;
	}

	/**
	 * @param image
	 *            设置image成员变量的值
	 * 
	 *            值约束说明
	 */
	public void setImage(Image image)
	{
		this.image = image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageInfo other = (ImageInfo) obj;
		if (Double.doubleToLongBits(dpiHorizontal) != Double
				.doubleToLongBits(other.dpiHorizontal))
			return false;
		if (Double.doubleToLongBits(dpiVertical) != Double
				.doubleToLongBits(other.dpiVertical))
			return false;
		if (height != other.height)
			return false;
		if (mimeType == null)
		{
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

}
