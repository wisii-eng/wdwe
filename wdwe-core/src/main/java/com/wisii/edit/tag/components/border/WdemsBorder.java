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
 */package com.wisii.edit.tag.components.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

public class WdemsBorder extends AbstractBorder {
	
	Color color = Color.black;
    
	@Override
	public Insets getBorderInsets(final Component c) {
		// TODO Auto-generated method stub
		return new Insets(20, 20, 20, 20);
	}
	
	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width,
			final int height) {
		// TODO Auto-generated method stub
//		super.paintBorder(c, g, x, y, width, height);
		
		Insets insets = getBorderInsets(c);
		
		System.out.println(insets);
		
		Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(color);
        g.drawLine(0, 0, 0, h-2);
//        g.drawLine(1, 0, w-2, 0);

        g.setColor(color);
        g.drawLine(1, 1, 1, h-3);
//        g.drawLine(2, 1, w-3, 1);

//        g.setColor(color);
//        g.drawLine(0, h-1, w-1, h-1);
//        g.drawLine(w-1, 0, w-1, h-2);
//
//        g.setColor(color);
//        g.drawLine(1, h-2, w-2, h-2);
//        g.drawLine(w-2, 1, w-2, h-3);

        g.translate(-x, -y);
        g.setColor(oldColor);
		
		
		
	}
    
    public static void main(final String[] args) {
		JFrame jf = new JFrame();
		
		jf.setPreferredSize(new Dimension(400, 400));
		
		JLabel jl = new JLabel("hello world");
		
		JPanel jp = new JPanel();
		
		jf.add(jp);
		
		jl.setBorder(new WdemsBorder());
		
		jp.add(jl);
//		jf.getContentPane().add(jl);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}

}
