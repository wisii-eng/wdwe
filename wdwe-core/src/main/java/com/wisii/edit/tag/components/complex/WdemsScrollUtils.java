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
 * @WdemsScrollUtils.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.complex;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * 类功能描述：自定义滚动条用工具类，主要提供滚动条样式信息。 
 * 作者：李晓光 
 * 创建日期：2009-11-4
 */
class WdemsScrollUtils {
	protected static Color rollColor;

	static Color getSombra() {
		return getColorAlfa(getColorTercio(MetalLookAndFeel.getControlDarkShadow(), Color.black), 64);
	}

	static ColorUIResource getColorTercio(Color a, Color b) {
		return new ColorUIResource(propInt(a.getRed(), b.getRed(), 3), propInt(a.getGreen(), b.getGreen(), 3), propInt(a.getBlue(), b.getBlue(), 3));
	}

	static Color getColorAlfa(Color col, int alfa) {
		return new Color(col.getRed(), col.getGreen(), col.getBlue(), alfa);
	}


	static Color getBrillo() {
		return getColorAlfa(getColorTercio(MetalLookAndFeel
				.getControlHighlight(), Color.white), 64);
	}

	static Color getRolloverColor() {
		if (rollColor == null) {
			rollColor = getColorAlfa(UIManager.getColor("Button.focus"), 40);
		}

		return rollColor;
	}
	private static int propInt(int a, int b, int prop) {
		return b + ((a - b) / prop);
	}
}
