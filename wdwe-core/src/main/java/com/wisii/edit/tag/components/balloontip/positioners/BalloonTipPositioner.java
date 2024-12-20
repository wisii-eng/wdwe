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

import java.awt.Point;
import java.awt.Rectangle;

import com.wisii.edit.tag.components.balloontip.BalloonTip;


/**
 * A BalloonTipPositioner is used to determine the position of a BalloonTip
 * Note: If you change a positioner's settings, the changes may not be visible until the balloon tip is redrawn.
 * @author Tim Molderez
 */
public abstract class BalloonTipPositioner {
	protected BalloonTip balloonTip = null;
	
	/**
	 * Default constructor
	 */
	public BalloonTipPositioner() {}
	
	/**
	 * Retrieve the balloon tip that uses this positioner
	 * @return The balloon tip that uses this positioner
	 */
	public BalloonTip getBalloonTip() {
		return balloonTip;
	}
	
	/**
	 * This method is meant only to be used by BalloonTip!
	 * A BalloonTip must call this method at the end of its construction (or when it's swapping for a new BalloonTipPositioner)
	 * @param balloonTip
	 */
	public void setBalloonTip(final BalloonTip balloonTip) {
		this.balloonTip = balloonTip;
	}
	
	/**
	 * Find the current location of the balloon's tip, relative to the top level container
	 * @return The location of the tip
	 */
	 public abstract Point getTipLocation();

	/**
	 * Determine and set the current location of the balloon tip
	 * @param attached		the balloon tip is attached to this rectangle
	 */
	public abstract void determineAndSetLocation(Rectangle attached);
}