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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;

import com.wisii.component.startUp.SystemUtil;

public class AreaBorder extends AbstractBorder
{

	final int NUM = 8;

	Image imagel = new ImageIcon(SystemUtil.getImagesPath("leftborder.gif"))
			.getImage();

	Image imager = new ImageIcon(SystemUtil.getImagesPath("rightborder.gif"))
			.getImage();

	Image imagebl = new ImageIcon(SystemUtil.getImagesPath("leftbottom.gif"))
			.getImage();

	Image imageb = new ImageIcon(SystemUtil.getImagesPath("bottomborder.gif"))
			.getImage();

	Image imagebr = new ImageIcon(SystemUtil.getImagesPath("rightbottom.gif"))
			.getImage();

	Image imagetl = new ImageIcon(SystemUtil.getImagesPath("lefttop.gif"))
			.getImage();

	Image imagetr = new ImageIcon(SystemUtil.getImagesPath("righttop.gif"))
			.getImage();

	Image imaget = new ImageIcon(SystemUtil.getImagesPath("topborder.gif"))
			.getImage();

	Image[] borderImg =
	{ imagetl, imaget, imagetr, imager, imagebr, imageb, imagebl, imagel };

	Insets insets;

	/**
	 * 用于接收一个Image类型的数组作为参数，用于绘制边框
	 * 
	 * @param borderImg
	 */
	public AreaBorder()
	{
	}

	/**
	 * 设置边框内嵌宽度
	 * 
	 * @param insets
	 */
	public void setInsets(Insets insets)
	{
		this.insets = insets;
	}

	/**
	 * 获取边框的内嵌宽度
	 */
	public Insets getBorderInsets(Component c)
	{
		if (this.insets != null)
		{
			return this.insets;
		} else
		{
			return new Insets(this.borderImg[1].getHeight(null),
					this.borderImg[7].getWidth(null), this.borderImg[5]
							.getHeight(null), this.borderImg[3].getWidth(null));
		}
	}

	/**
	 * 要通过图像绘制边框，需要使用TexturePaint类，它实现了Paint接口。
	 * 但是TexturePaint只能接受BufferedImage，而非一般的图像，因此必须经过转化。
	 * BufferedImage是一种特殊的图像格式，允许java2D框架对其进行像素级的读写操作。
	 * 而普通图像是由操作系统控制的，很难对其进行像素级的访问。 java并不允许在这2种图像之间转化，但可以在其中一种图像上绘制另一种图像。
	 * 
	 * @param img
	 * @return
	 */
	public BufferedImage createBufferedImage(Image img, int width, int height)
	{
		BufferedImage bufImg = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufImg.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return bufImg;
	}

	/**
	 * 使用合适的图像创建一个TexturePaint实例，并填充要求的区域。
	 * 
	 * @param g2d
	 * @param img
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void fillTexture(Graphics2D g2d, Image img, int x, int y, int w,
			int h)
	{
		BufferedImage bufImg = this.createBufferedImage(img,
				img.getWidth(null), img.getHeight(null));
		Rectangle rect = new Rectangle(x, y, w, h);
		TexturePaint paint = new TexturePaint(bufImg, rect);
		g2d.setPaint(paint);
		g2d.fillRect(x, y, w, h);
	}

	/**
	 * 首先用红色填充整个边框区域，然后把Graphics强制转换成Graphics2D对象， 以便以后进行高级绘图操作。接着保存对图像高度和宽度的引用。
	 * 最后对每段边框区域调用fillTexture()方法。
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2d = (Graphics2D) g;

		int top = this.borderImg[1].getHeight(null);

		int left = this.borderImg[7].getWidth(null);
		int bottom = this.borderImg[5].getHeight(null);
		int right = this.borderImg[3].getWidth(null);

		int height = c.getHeight();
		int width = c.getWidth();

		this.fillTexture(g2d, this.borderImg[0], x, y, left, top);
		this.fillTexture(g2d, this.borderImg[1], x + left, y, width - left
				- right, top);
		this.fillTexture(g2d, this.borderImg[2], x + width - right, y, right,
				top);
		this.fillTexture(g2d, this.borderImg[3], x + width - right, y + top,
				right, height - top - bottom);
		this.fillTexture(g2d, this.borderImg[4], x + width - right, y + height
				- bottom, right, bottom);
		this.fillTexture(g2d, this.borderImg[5], x + left, y + height - bottom,
				width - left - right, bottom);
		this.fillTexture(g2d, this.borderImg[6], x, y + height - bottom, left,
				bottom);
		this.fillTexture(g2d, this.borderImg[7], x, y + top, left, height - top
				- bottom);
	}
}