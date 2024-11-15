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
 * @WdemsScrollBarUI.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.complex;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

/**
 * 类功能描述：自定义滚动栏UI。 作者：李晓光 创建日期：2009-11-4
 */
class WdemsScrollBarUI extends MetalScrollBarUI {
	private boolean clicked;
	private boolean rollOver;

	public static ComponentUI createUI(JComponent c) {

		return new WdemsScrollBarUI();
	}

	protected TrackListener createTrackListener() {
		return new MiML(this);
	}
	 protected void installDefaults() {
		 super.installDefaults();
		 scrollBarWidth = 5;//((Integer)(UIManager.get( "wdems.scrollBar.width" ))).intValue();    
	 }

	protected JButton createDecreaseButton(int orientation) {
		decreaseButton = new WdemsScrollButton(orientation, scrollBarWidth,
				isFreeStanding);
		return decreaseButton;
	}

	protected JButton createIncreaseButton(int orientation) {
		increaseButton = new WdemsScrollButton(orientation, scrollBarWidth,
				isFreeStanding);
		return increaseButton;
	}

	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		Color thumbColor = UIManager.getColor("ScrollBar.thumb");
		Color thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");

		g.translate(thumbBounds.x, thumbBounds.y);

		g.setColor(thumbColor);
		g.fillRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 1);

		g.setColor((rollOver ? thumbShadow.darker() : thumbShadow));
		g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 1);
		
		/*Icon icDecor = null;
		if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
			icDecor = new ImageIcon("icons/HorizontalThumbIconImage.png");
			;// UIManager.getIcon( "ScrollBar.horizontalThumbIconImage");
		} else {
			icDecor = new ImageIcon("icons/VerticalThumbIconImage.png");
			;// UIManager.getIcon( "ScrollBar.verticalThumbIconImage");
		}

		int w = icDecor.getIconWidth();
		int h = icDecor.getIconHeight();
		int x = (thumbBounds.width - w) / 2;
		int y = (thumbBounds.height - h) / 2;

		if (((scrollbar.getOrientation() == JScrollBar.HORIZONTAL) && (thumbBounds.width >= w))
				|| ((scrollbar.getOrientation() == JScrollBar.VERTICAL) && (thumbBounds.height >= h))) {
			 icDecor.paintIcon( c, g, x, y);
		}*/

		g.translate(-thumbBounds.x, -thumbBounds.y);

		Graphics2D g2D = (Graphics2D) g;
		GradientPaint grad = null;

		Color colA, colB;
		if (clicked) {
			colA = WdemsScrollUtils.getSombra();
			colB = WdemsScrollUtils.getBrillo();
		} else {
			colA = WdemsScrollUtils.getBrillo();
			colB = WdemsScrollUtils.getSombra();
		}
		colA = new Color(100, 100, 238);
		colB = new Color(128, 128, 53);
		if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
			grad = new GradientPaint(thumbBounds.x, thumbBounds.y, colA,
					thumbBounds.x, thumbBounds.height, colB);
		} else {
			grad = new GradientPaint(thumbBounds.x, thumbBounds.y, colA,
					thumbBounds.width, thumbBounds.y, colB);
			/*
			 * ImageIcon icSombra = (ImageIcon)UIManager.getIcon(
			 * "BordeGenSup"); g.drawImage( icSombra.getImage(),
			 * thumbBounds.x,thumbBounds.y+thumbBounds.height,
			 * thumbBounds.width, icSombra.getIconHeight(), null);
			 */
		}

		g2D.setPaint(grad);
		g2D.fill(thumbBounds);
	}

/*	protected Rectangle getTrackBounds() {
		int w = UIManager.getInt("ScrollBar.width");
		if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
			return new Rectangle(trackRect.x, trackRect.y, w, trackRect.height);
		} else {
			return new Rectangle(trackRect.x, trackRect.y, trackRect.width, w);
		}
	}

	protected Rectangle getThumbBounds() {
		int w = UIManager.getInt("ScrollBar.width");
		if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
			return new Rectangle(thumbRect.x, thumbRect.y, thumbRect.width, w);
		} else {

			return new Rectangle(thumbRect.x, thumbRect.y, w, thumbRect.height);
		}
	}*/

	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		Graphics2D g2D = (Graphics2D) g;
		GradientPaint grad = null;

		if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
			grad = new GradientPaint(trackBounds.x, trackBounds.y,
					WdemsScrollUtils.getSombra(), trackBounds.x, trackBounds.y
							+ trackBounds.height, WdemsScrollUtils.getBrillo());
		} else {
			grad = new GradientPaint(trackBounds.x, trackBounds.y,
					WdemsScrollUtils.getSombra(), trackBounds.x
							+ trackBounds.width, trackBounds.y,
					WdemsScrollUtils.getBrillo());
		}

		g2D.setPaint(grad);
		g2D.fill(trackBounds);
	}

	// ///////////////////////////////////

	public class MiML extends MetalScrollBarUI.TrackListener {
		WdemsScrollBarUI papi;

		public MiML(WdemsScrollBarUI papi) {
			this.papi = papi;
		}

		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);

			papi.rollOver = true;
		}

		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);

			papi.rollOver = false;
		}

		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);

			papi.clicked = true;
			scrollbar.repaint();
		}

		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);

			papi.clicked = false;
			scrollbar.repaint();
		}

		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);

			if (papi.rollOver && !thumbRect.contains(e.getX(), e.getY())) {
				rollOver = false;
				scrollbar.repaint();
			} else if (!papi.rollOver && thumbRect.contains(e.getX(), e.getY())) {
				papi.rollOver = true;
				scrollbar.repaint();
			}
		}

		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);

			if (papi.rollOver && !thumbRect.contains(e.getX(), e.getY())) {
				rollOver = false;
				scrollbar.repaint();
			} else if (!papi.rollOver && thumbRect.contains(e.getX(), e.getY())) {
				papi.rollOver = true;
				scrollbar.repaint();
			}
		}
	}
}
