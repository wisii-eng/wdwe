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

package com.wisii.edit.tag.components.balloontip.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.wisii.edit.tag.components.balloontip.BalloonTip;


/**
 * This class provides timed balloon tips
 * @author Tim
 */
public class TimingUtils {
	
	/*
	 * Disallow instantiating this class
	 */
	private TimingUtils() {};
	
	/**
	 * Displays a balloon tip for a certain time.
	 * @param balloon			the BalloonTip
	 * @param time				show the balloon for this amount of milliseconds
	 */
	public static void showTimedBalloon(final BalloonTip balloon, Integer time) {
		balloon.setVisible(true);
		Timer timer = new Timer(0, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				balloon.closeBalloon();
			}
		});
		timer.setRepeats(false);
		timer.setInitialDelay(time);
		timer.start();
	}
}
