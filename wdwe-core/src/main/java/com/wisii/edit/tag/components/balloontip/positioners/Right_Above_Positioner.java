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
 * Balloontip - Balloon tips for Java Swing applications
 * Copyright 2007, 2008 Bernhard Pauler, Tim Molderez
 * 
 * This file is part of Balloontip.
 * 
 * Balloontip is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Balloontip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Balloontip.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wisii.edit.tag.components.balloontip.positioners;

import java.awt.Rectangle;

/**
 * This class positions a balloon tip above the component it's attached to, with the tip on the right
 * @author Tim Molderez
 */
public class Right_Above_Positioner extends BasicBalloonTipPositioner {
	public Right_Above_Positioner(int hO, int vO) {
		super(hO, vO);
	}

	public void determineAndSetLocation(Rectangle attached) {
		// First calculate the location, without applying any correction tricks
		int balloonWidth = balloonTip.getPreferredSize().width;
		int balloonHeight = balloonTip.getPreferredSize().height;
		flipX = true;
		flipY = false;
		
		hOffset = balloonWidth - preferredHorizontalOffset;
		if (fixedAttachLocation) {
			x = new Float(attached.x + attached.width * attachLocationX).intValue() - hOffset;
			y = new Float(attached.y + attached.height * attachLocationY).intValue() - balloonHeight;
		} else {
			x = attached.x + attached.width - balloonWidth;
			y = attached.y - balloonHeight;
		}
		// Apply orientation correction
		if (orientationCorrection) {
			// Check collision with the top of the window
			if (y < 0) {
				flipY = true;
				if (fixedAttachLocation) {
					y += balloonHeight;
				} else {
					y = attached.y + attached.height;
				} 
			}
			
			// Check collision with the right side of the window
			if (x + balloonWidth > balloonTip.getTopLevelContainer().getWidth()) {
				flipX = false;
				hOffset = balloonWidth - hOffset;
				if (fixedAttachLocation) {
					x += (balloonWidth - 2*hOffset);
				} else {
					x = attached.x;
				}
			}
		}
		
		// Apply offset correction
		if (offsetCorrection) {
			applyOffsetCorrection();
		}
		
		// Finally set the balloon tip's location
		if (flipX) {
			balloonTip.getStyle().setHorizontalOffset(balloonWidth - hOffset);
		} else {
			balloonTip.getStyle().setHorizontalOffset(hOffset);
		}
		balloonTip.getStyle().flip(flipX, flipY);
		balloonTip.setBounds(x, y, balloonTip.getPreferredSize().width, balloonTip.getPreferredSize().height);
		balloonTip.revalidate(); // Revalidate is needed in case the balloon gets flipped; validate wouldn't do in that case.
		if (hOffset != preferredHorizontalOffset) {
			balloonTip.repaint(); // In certain cases, when the horizontal offset changes, it doesn't get redrawn properly without a repaint...
		}
	}
}
