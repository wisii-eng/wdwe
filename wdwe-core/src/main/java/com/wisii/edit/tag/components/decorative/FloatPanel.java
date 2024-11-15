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
 */
package com.wisii.edit.tag.components.decorative;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.wisii.component.startUp.SystemUtil;

@SuppressWarnings("serial")
public class FloatPanel extends JPanel
{

	Image imagep = new ImageIcon(SystemUtil.getImagesPath("top.gif"))
			.getImage();

	JComponent compont;

	public static int horizontalgrap = 4;//10;

	public static int verticalgrap = 5;

	List<VirtualButton> buttons;

	private final static ImageIcon imageadd = new ImageIcon(SystemUtil
			.getImagesPath("addbutton.gif"));

	private final static ImageIcon imagedelete = new ImageIcon(SystemUtil
			.getImagesPath("delbutton.gif"));

	private final static ImageIcon imagehidden = new ImageIcon(SystemUtil
			.getImagesPath("hiddbutton.gif"));

	public FloatPanel()
	{
		super();
	}

	public FloatPanel(JComponent textfiled)
	{
		super();
		compont = textfiled;
		int width = compont.getWidth();
		FloatPanel.this
				.setPreferredSize(new Dimension(width, 16 + verticalgrap));
		FloatPanel.this.setLayout(null);
	}

	public FloatPanel(JComponent textfiled, List<VirtualButton> buttonlist)
	{
		super();
		compont = textfiled;
		buttons = buttonlist;

		int width = 50;//compont.getWidth();
		int height = 20;//compont.getHeight();
		
		Integer[] lengths = EditPanel.getWidthAndHeight(buttons, width, height);
//		FloatPanel.this.setPreferredSize(new Dimension(lengths[0], lengths[1]));
//		this.setPreferredSize(new Dimension(lengths[0], lengths[1]));
		
		setPreferredSize(new Dimension(50, 20));
		this.setLayout(null);
		int rownumber = lengths[2];
		if (buttons != null)
		{
			for (int i = 0; i < buttons.size(); i++)
			{
				VirtualButton current = buttons.get(i);
				EditButton button = new EditButton(current.getType(), current
						.getPon(), current.getHint(), current.getAction());
				String type = current.getType();
				if (EditButton.add.equals(type))
				{
					button.setIcon(imageadd);
				} else if (EditButton.delete.equals(type))
				{
					button.setIcon(imagedelete);
				} else if (EditButton.hidden.equals(type))
				{
					button.setIcon(imagehidden);
				}
				int positionx = 0;
				int positiony = 0;
				if (i > 0)
				{
					int xnumber = i % rownumber;
					int ynumber = i / rownumber;
					positionx = xnumber * (16 + horizontalgrap);
					positiony = ynumber * (16 + verticalgrap);
				}
				button.setToolTipText(current.getHint());
				button.setBounds(positionx, positiony, 16, 16);
				this.add(button);
			}
		}
	}
	
	// add by 李晓光  2010-8-6
	/**
	 *把VirtualButton转换为能够显示的EditButton
	 * @param buttons
	 * @return
	 */
	public final static List<EditButton> getButttons(List<VirtualButton> buttons){
		if(buttons == null || buttons.isEmpty()){
			return Collections.emptyList();
		}
		List<EditButton> editButtons = new ArrayList<EditButton>(buttons.size());
		for (VirtualButton virtualButton : buttons) {
			EditButton button = new EditButton(virtualButton.getType(), virtualButton
					.getPon(), virtualButton.getHint(), virtualButton.getAction());
			String type = virtualButton.getType();
			if (EditButton.add.equals(type))
			{
				button.setIcon(imageadd);
			} else if (EditButton.delete.equals(type))
			{
				button.setIcon(imagedelete);
			} else if (EditButton.hidden.equals(type))
			{
				button.setIcon(imagehidden);
			}
			button.setToolTipText(virtualButton.getHint());
			button.setPreferredSize(new Dimension(16, 16));
			
			editButtons.add(button);
		}
		
		return editButtons;
	}
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Dimension size = this.getSize();
		g.drawImage(imagep, 0, 0, size.width, size.height, null);
	}
}
