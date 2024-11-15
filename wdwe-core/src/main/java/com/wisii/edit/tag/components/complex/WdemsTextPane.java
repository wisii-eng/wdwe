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
 */package com.wisii.edit.tag.components.complex;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;

@SuppressWarnings("serial")
public class WdemsTextPane extends JTextPane{
	private List<JComponent> jcomps;
	@Override
	public boolean getScrollableTracksViewportHeight() {
		
		if (getParent() instanceof JViewport) {
			JViewport port = (JViewport) getParent();
			TextUI ui = getUI();
			int h = port.getHeight();
			Dimension min = ui.getMinimumSize(this);
			if (h + 5 >= min.height) {
				Dimension max = ui.getMaximumSize(this);
				if (h + 10 <= max.height) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public void insertComponent(Component c) {
		// TODO Auto-generated method stub
		super.insertComponent(c);
		if(jcomps==null)
		{
			jcomps=new ArrayList<JComponent>();
		}
		jcomps.add((JComponent) c);
	}
	public List<JComponent> getJcomps() {
		return jcomps;
	}
	
}