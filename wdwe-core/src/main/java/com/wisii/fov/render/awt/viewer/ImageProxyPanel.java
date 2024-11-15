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
 *//* $Id: ImageProxyPanel.java,v 1.10 2007/10/25 02:57:57 lzy Exp $ */

package com.wisii.fov.render.awt.viewer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import javax.swing.JPanel;

import com.wisii.edit.EditStatusControl;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.edit.tag.components.decorative.WdemsCascadeManager;
import com.wisii.edit.tag.components.decorative.WdemsEditComponentManager;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.components.group.WdemsGroupManager;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.render.java2d.Java2DRenderer;



/**
 * Panel used to display a single page of a document.
 * This is basically a lazy-load display panel which
 * gets the size of the image for layout purposes but
 * doesn't get the actual image data until needed.
 * The image data is then accessed via a soft reference,
 * so it will be garbage collected when moving through
 * large documents.
 */
public class ImageProxyPanel extends JPanel {

    /** The reference to the BufferedImage storing the page data */
    private Reference imageRef;

    /** The maximum and preferred size of the panel */
    private Dimension size;

    /** The renderer. Shared with PreviewPanel and PreviewDialog. */
    private final Java2DRenderer renderer;

    /** The page to be rendered. */
    private int page;

    private int x;

    private int y;

    private int width;
    private int height;

    //在刷新之后是否执行鼠标键操作
//    private boolean isDoClicked = false;

    //鼠标键事件
    private MouseEvent event;

    //Panel面板
    private PreviewPanel panel;
    /**
     * Panel constructor. Doesn't allocate anything until needed.
     * @param renderer the AWTRenderer instance to use for painting
     * @param page initial page number to show
     */
    public ImageProxyPanel(Java2DRenderer renderer, int page) {
        this.renderer = renderer;
        this.page = page;
        //add by xh,布局设成空
        this.setLayout(null);
        //add end
        // Allows single panel to appear behind page display.
        // Important for textured L&Fs.
        setOpaque(false);
        try{
//        WdemsActioinHandler.bindActionsWhenWindow(this, BindType.MainFrame);
        }
        catch(Exception e)
        {
        	//e.printStackTrace();
        }
    }

    /**
     * @return the size of the page plus the border.
     */
    @Override
	public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * @return the size of the page plus the border.
     */
    @Override
	public Dimension getPreferredSize() {
        if (size == null) {
            try {
                Insets insets = getInsets();
                size = renderer.getPageImageSize(page);
                size = new Dimension(size.width + insets.left + insets.right,
                                                         size.height + insets.top + insets.bottom);
            } catch (FOVException fovEx) {
                // Arbitary size. Doesn't really matter what's returned here.
                return new Dimension(10, 10);
            }
        }
        return size;
    }

    /**
     * Sets the number of the page to be displayed and refreshes the display.
     * @param pg the page number
     */
    public void setPage(int pg) {
        if (page != pg) {
            page = pg;
            cleanWhenChangePage();
            imageRef = null;
            size=null;
            repaint();
        }
       
    }
    /** 当切换页面时，及时销毁垃圾对象。 */
    private void cleanWhenChangePage(){
    	WdemsWarningManager.clearDump();
    }
    /**
     * Gets the image data and paints it on screen. Will make
     * calls to getPageImage as required.
     * @param graphics
     * @see javax.swing.JComponent#paintComponent(Graphics)
     * @see com.wisii.fov.render.java2d.Java2DRenderer#getPageImage(int)
     */
    @Override
	public synchronized void paintComponent(Graphics graphics) {
        try {
            if (isOpaque()) { //paint background
                graphics.setColor(getBackground());
                graphics.fillRect(0, 0, getWidth(), getHeight());
            }

            super.paintComponent(graphics);

            BufferedImage image = null;
            if (imageRef == null || imageRef.get() == null) {
            	clearBeforePaint();
                image = renderer.getPageImage(page);
                imageRef = new SoftReference(image);
            } else {
                image = (BufferedImage)imageRef.get();
            }
            x = (getWidth() - image.getWidth()) / 2;
            y = (getHeight() - image.getHeight()) / 2;
            width = image.getWidth();
            height = image.getHeight();
            graphics.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);
        } catch (FOVException fovEx) {
            fovEx.printStackTrace();
        }
        
        event = null;
    }
    private void clearBeforePaint(){
    	WdemsEditComponentManager.clearDump();
    	WdemsCascadeManager.clearDump();
    	WdemsGroupManager.cleanDump();
    }
    //鼠标左键点击控件外部的线程
    private class runMouseClcked extends Thread
    {
        private final MouseEvent event;

        public runMouseClcked(MouseEvent e)
        {
            event = e;
        }

        @Override
		public void run()
        {
            if(panel != null)
            {
                panel.mouseMoved(event);
                panel.mouseClicked(event);
            }
        }
    }

	public Reference getImageRef() {
		return imageRef;
	}

    public int getOffsetX()
    {
        return x;
    }

    public int getOffsetY()
    {
        return y;
    }

    public int getImageWidth()
    {
        return width;// 因为imageRef是弱引用，为了防止imageRef.get()返回null，导致空指针异常，所以直接保存width属性
    }

    public int getImageHeight()
    {
        return height;
    }


//    public boolean getDoClicked()
//    {
//        return isDoClicked;
//    }
//
//    public void setDoClicked(boolean b)
//    {
//        isDoClicked = b;
//    }

    public MouseEvent getEvent()
    {
        return event;
    }

    public void setEvent(MouseEvent e)
    {
        event = e;
    }

    public void setPanel(PreviewPanel p)
    {
        panel = p;
    }

    public PreviewPanel getPanel()
    {
        return panel;
    }

}
