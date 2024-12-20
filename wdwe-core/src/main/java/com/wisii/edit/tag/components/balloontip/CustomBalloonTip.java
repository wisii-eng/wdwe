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

package com.wisii.edit.tag.components.balloontip;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.wisii.edit.tag.components.balloontip.positioners.BalloonTipPositioner;
import com.wisii.edit.tag.components.balloontip.styles.BalloonTipStyle;



/**
 * Provides the same functionality as a BalloonTip, but you can add a certain offset, 
 * which can come in handy if attached to custom components.
 * Also, if the attached component is part of a JScrollPane, the balloon tip can be set such that it will
 * only be visible if the chosen offset point is visible. 
 * @author Tim Molderez
 */
public class CustomBalloonTip extends BalloonTip {
	
	// A rectangular shape within the custom component; the balloon tip will attach to this rectangle
	protected Rectangle offset = new Rectangle(0,0,1,1);
	// If the custom component is located in a viewport, we'll need it to determine when the balloon tip should hide itself
	private JViewport viewport = null;
	
	// If the viewport changes, so should the balloon tip
	private ComponentAdapter viewportListenerListener = new ComponentAdapter() {
		public void componentMoved(ComponentEvent e) {
			if (attachedComponent.isShowing()) {
				refreshLocation();
			}
		}
		public void componentResized(ComponentEvent e) {
			if (attachedComponent.isShowing()) {
				refreshLocation();
			}
		}
	};
	
	/**
	 * @see com.wisii.edit.tag.components.balloontip.BalloonTip#BalloonTip(JComponent, String, BalloonTipStyle, Orientation, AttachLocation, int, int, boolean)
	 * @param attachedComponent	The custom component to attach the balloon tip to
	 * @param offset			Specifies a rectangle within the attached component; the balloon tip will attach to this rectangle.
	 * 							Do note that the coordinates should be relative to the attached component's top left corner.
	 */
	public CustomBalloonTip(JComponent attachedComponent, String text, Rectangle offset, BalloonTipStyle style, Orientation alignment, AttachLocation attachLocation, int horizontalOffset, int verticalOffset, boolean useCloseButton) {
		super(attachedComponent, text, style, alignment, attachLocation, horizontalOffset, verticalOffset, useCloseButton);
		this.offset = offset;
		refreshLocation();
	}
	
	/**
	 * @see com.wisii.edit.tag.components.balloontip.BalloonTip#BalloonTip(JComponent, String, BalloonTipStyle, BalloonTipPositioner, boolean)
	 * @param attachedComponent	The custom component to attach the balloon tip to
	 * @param offset			Specifies a rectangle within the attached component; the balloon tip will attach to this rectangle.
	 * 							Do note that the coordinates should be relative to the attached component's top left corner.
	 */
	public CustomBalloonTip(JComponent attachedComponent, String text, Rectangle offset, BalloonTipStyle style, BalloonTipPositioner positioner, boolean useCloseButton) {
		super(attachedComponent, text, style, positioner, useCloseButton);
		this.offset = offset;
		refreshLocation();
	}
	
	/**
	 * Set the offset within the attached component
	 * @param offset
	 */
	public void setOffset(Rectangle offset) {
		this.offset = offset;
		refreshLocation();
	}

	public void closeBalloon() {
		if (viewport != null) {
			viewport.removeComponentListener(viewportListenerListener);
		}
		super.closeBalloon();
	}
	
	/**
	 * Sets up the balloon tip such that it will only be shown if
	 * the table cell we're attached to is visible within this viewport.
	 * This is very useful if, for example, the JTable with this balloon tip is inside a JScrollpane.
	 * (You can also remove the viewport by calling setViewport(null).)
	 * @param viewport
	 */
	public void setViewport(JViewport viewport) {
		this.viewport = viewport;
		viewport.addComponentListener(viewportListenerListener);
	}
	
	/**
	 * Retrieve the viewport that this balloon tip is monitoring, such that the balloon tip will hide itself 
	 * once the balloon's tip is outside of this viewport.
	 * @return The viewport this balloon tip is located in
	 */
	public JViewport getViewport() {
		return viewport;
	}
	
	public void refreshLocation() {
		Point location = SwingUtilities.convertPoint(attachedComponent, getLocation(), this);
		try {
			positioner.determineAndSetLocation(new Rectangle(location.x + offset.x, location.y + offset.y, offset.width, offset.height));

			if (viewport != null) {
				// Determine whether the point that visually connects the balloon and the table cell still is visible...
				Rectangle view = new Rectangle(SwingUtilities.convertPoint(viewport, viewport.getLocation(), getTopLevelContainer()), viewport.getSize());
				Point tipLocation = positioner.getTipLocation();
				if (tipLocation.y >= view.y-1 // -1 because we still want to allow balloons that are attached to the very top...
						&& tipLocation.y <= (view.y + view.height)
						&& (tipLocation.x) >= view.x
						&& (tipLocation.x) <= (view.x + view.width)) {
					setVisible(true);
				} else {
					setVisible(false);
				}
			}
		} catch (NullPointerException exc) {}
	}
}