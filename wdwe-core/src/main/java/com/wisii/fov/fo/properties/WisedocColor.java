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
 * @WisedocColor.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.fov.fo.properties;

import java.awt.Color;

/**
 * 类功能描述：包含层属性的color类
 * 
 * 作者：李晓光 创建日期：2009-2-2
 */

public class WisedocColor extends Color
{
	private int _layer = 0;
	private boolean _issetcolor = false;
	private boolean _issetlayer = false;

	public WisedocColor(Color color)
	{
		super(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
		_issetcolor = true;
	}

	public WisedocColor(int layer)
	{
		super(Color.black.getRGB());
		_layer = layer;
		_issetlayer = true;
	}

	public WisedocColor(Color color, int layer)
	{
		super(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
		if(layer > -1){
		_layer = layer;
		}
	}

	/**
	 * 
	 * 返回层，0表示没设置层，最多支持10层
	 * 
	 * @param
	 * @return
	 * @exception
	 */
	public int getLayer()
	{
		return _layer;
	}

	public boolean isSetColor()
	{
		return _issetcolor;
	}

	public boolean isSetLayer()
	{
		return _issetlayer;
	}
	public boolean equals(Object obj)
	{
		if (!(obj instanceof WisedocColor))
		{
			return false;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		WisedocColor wcolor = (WisedocColor) obj;
		return _layer == wcolor._layer;
	}
	 public String toString() {
	        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + ",a=" + getAlpha() + ",layer=" + getLayer() + "]";
	    }
}
