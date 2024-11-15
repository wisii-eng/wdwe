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
 * @UpDownArrowIcon.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * 类功能描述：用来提供按列排序的图标
 * 1、在点击表的列头时，如果当前列支持排序。
 * 2、根据升序、降序、决定采用哪种图标。
 * 作者：李晓光
 * 创建日期：2009-6-29
 */
public enum UpDownArrowIcon implements Icon {
	Down(IconStyle.DOWN),
	Up(IconStyle.UP)
	;
	static enum IconStyle{
		UP, DOWN		
	}
	private final static int size = 12;
	private IconStyle style = IconStyle.DOWN;
	UpDownArrowIcon(IconStyle style){
		this.style = style;
	}
	public int getIconHeight() {
		return size;
	}	
	public int getIconWidth() {
		return size;
	}
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int centerX = x + size / 2;
        int minX = x + 1;
        int maxX = (x + size) - 2;
        int minY = y + 1;
        int maxY = (y + size) - 2;
        Color color = (Color) UIManager.get("controlDkShadow");
        if (style == IconStyle.UP) {
            g.setColor(Color.WHITE);
            g.drawLine(minX, maxY, maxX, maxY);
            g.drawLine(maxX, maxY, centerX, minY);
            g.setColor(color);
            g.drawLine(minX, maxY, centerX, minY);
        } else {
            g.setColor(color);
            g.drawLine(minX, minY, maxX, minY);
            g.drawLine(minX, minY, centerX, maxY);
            g.setColor(Color.WHITE);
            g.drawLine(maxX, minY, centerX, maxY);
        }
	}

}
