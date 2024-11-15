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
 * @ImageComponent.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * 类功能描述：根据指定的图像的不同创建不同的效果的控件
 * 目的是方便创建不规则控件。
 * 
 * 作者：李晓光
 * 创建日期：2009-9-23
 */
@SuppressWarnings("serial")
public class ImageComponent extends JLabel {
	private final static Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.BLUE);
	private final static Border UNSELECTED_BORDER = BorderFactory.createEmptyBorder();
	private BufferedImage image = null;
	public ImageComponent(BufferedImage image){
		super(new ImageIcon(image));
		this.image = image;
		initComponent();
	}
	private void initComponent(){
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e) {
				/*if(isFocusOwner())
					return;*/
				setBorder(SELECTED_BORDER);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				/*if(isFocusOwner())
					return;*/
				setBorder(UNSELECTED_BORDER);
			}
		});
		/*addFocusListener(new FocusAdapter(){
			@Override
			public void focusGained(FocusEvent e) {
				setBorder(SELECTED_BORDER);
			}
			@Override
			public void focusLost(FocusEvent e) {
				setBorder(UNSELECTED_BORDER);
			}
		});*/
	}
	@Override
	public boolean contains(int x, int y) {
		if(image == null)
			return Boolean.FALSE;
		int left = 0, top = 0;
		left = x - left;
		top = y - top;
		Rectangle r = new Rectangle(0, 0, image.getWidth(this), image.getHeight(this));
		if(!r.contains(x, y)){
			return Boolean.FALSE;
		}
		int color = image.getRGB(left, top);
		return (color >> 24 & 0XFF) > 0;
	}
}
