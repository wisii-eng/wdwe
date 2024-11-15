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
 * @WdemsScrollButton.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.complex;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalScrollButton;

/**
 * 类功能描述：自定义滚动条用滚动按钮 
 * 作者：李晓光 
 * 创建日期：2009-11-4
 */
@SuppressWarnings("serial")
class WdemsScrollButton extends MetalScrollButton {
	public WdemsScrollButton(int direction, int width, boolean freeStanding) {
		super(direction, width + 1, freeStanding);
	}

	public void paint(Graphics g) {
		Rectangle rec = new Rectangle(0, 0, getWidth(), getHeight());
		Graphics2D g2D = (Graphics2D) g;
		GradientPaint grad = null;

		if (getDirection() == SwingConstants.EAST
				|| getDirection() == SwingConstants.WEST) {
			if (getModel().isPressed() || getModel().isSelected()) {
				grad = new GradientPaint(0, 0, WdemsScrollUtils.getSombra(), 0,
						rec.height, WdemsScrollUtils.getBrillo());
			} else {
				grad = new GradientPaint(0, 0, WdemsScrollUtils.getBrillo(), 0,
						rec.height, WdemsScrollUtils.getSombra());
			}
		} else {
			if (getModel().isPressed() || getModel().isSelected()) {
				grad = new GradientPaint(0, 0, WdemsScrollUtils.getSombra(),
						rec.width, 0, WdemsScrollUtils.getBrillo());
			} else {
				grad = new GradientPaint(0, 0, WdemsScrollUtils.getBrillo(),
						rec.width, 0, WdemsScrollUtils.getSombra());
			}
		}

		g2D.setColor(MetalLookAndFeel.getControl());
		g2D.fillRect(rec.x, rec.y, rec.width, rec.height);

		g2D.setPaint(grad);
		g2D.fillRect(rec.x, rec.y, rec.width, rec.height);

		if (getModel().isRollover()) {
			g2D.setColor(WdemsScrollUtils.getRolloverColor());
			g2D.fillRect(rec.x, rec.y, rec.width, rec.height);
		}

		g2D.setColor(MetalLookAndFeel.getControlDarkShadow());
		g2D.drawRect(rec.x, rec.y, rec.width - 1, rec.height - 1);

		Icon icon = null;
		switch (getDirection()) {
		case SwingConstants.EAST:
			icon = UIManager.getIcon("ScrollBar.eastButtonIcon");
			break;
		case SwingConstants.WEST:
			icon = UIManager.getIcon("ScrollBar.westButtonIcon");
			break;
		case SwingConstants.NORTH:
			icon = UIManager.getIcon("ScrollBar.northButtonIcon");
			break;
		case SwingConstants.SOUTH:
			icon = UIManager.getIcon("ScrollBar.southButtonIcon");
			break;
		}
		/* icon = new ImageIcon("icons/Frame.png"); */
		/* icon.paintIcon( this, g2D, rec.x, rec.y); */
	}
}
