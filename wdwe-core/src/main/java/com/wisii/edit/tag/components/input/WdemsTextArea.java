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
 */package com.wisii.edit.tag.components.input;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextArea;

import com.wisii.edit.tag.util.ComponentStyleUtil;

public class WdemsTextArea extends JTextArea {
		/**
		 * 
		 */
		private final MultiLineInput multiLineInput;
		private MultiLineInput muliline;
		WdemsTextArea(MultiLineInput multiLineInput, MultiLineInput muliline)
		{
			this.multiLineInput = multiLineInput;
			this.muliline=muliline;
		}
		
		public MultiLineInput getMuliline() {
			return muliline;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			Graphics2D graphics = (Graphics2D)g; 
			graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());
			
			super.paintComponent(graphics);
			
			
			//下面是添加背景提示：
			Color original = g.getColor();

			if (this.multiLineInput.input != null) {
				String s = this.multiLineInput.input.getHint();
				if (this.getDocument().getLength() == 0 && s != null && !"".equals(s)) {
					Graphics2D g2d = (Graphics2D) g;
//					g2d.setColor(Color.red);
//					g2d.fillRect(getX(), getY(), getWidth(), getHeight());
					Rectangle2D r = getFont().getStringBounds(s, graphics.getFontRenderContext());
					Rectangle bound = getBounds();
					Insets inset = this.getBorder().getBorderInsets(this);
					LineMetrics line = getFont().getLineMetrics(s, graphics.getFontRenderContext());
					
					int x = inset.left;
					int y = (int)((bound.height - getHeight() - inset.top - inset.bottom) / 2 + line.getAscent());
					y = (int)(inset.top + line.getAscent() + line.getLeading());
					
					g2d.setColor(Color.gray);
					g2d.drawString(s, x, y);	
				}
			}
			
//			System.out.println("x:" + getX() + " y:" + getAlignmentY() + " width:" + getWidth() + " heigth:" + getHeight());
			g.setColor(original);
			
		}
	}