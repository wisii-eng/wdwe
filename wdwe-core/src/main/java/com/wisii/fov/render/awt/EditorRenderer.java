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
 *//*
 * EditorRenderer.java
 *
 * 改版履历:2007.04.20
 *
 * 版本信息:1.0
 *
 * Copyright:WISe Internat Information Co.,Ltd.
 */

package com.wisii.fov.render.awt;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.IOException;
import com.wisii.component.mainFramework.commun.CommincateFactory;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.render.awt.viewer.StatusListener;
import com.wisii.fov.render.java2d.Java2DRenderer;


/**
 * The EditorRenderer outputs the pages generated by the layout engine to a Swing window. This Swing window serves as default
 * viewer for the -wdde switch and as an example of how to embed the EditorRenderer into an WDDE/Swing application.
 */
public class EditorRenderer extends Java2DRenderer implements Pageable
{
    /** The MIME type for AWT-Rendering */
    public static final String MIME_TYPE = MimeConstants.MIME_WISII_WDDE_PREVIEW;

    /** Will be notified when rendering progresses  */
    protected StatusListener _statusListener = null;
    

    /** 构造函数。设置字体信息 */
    public EditorRenderer(StatusListener mainWindow)
    {
    	/**
    	 * liuxiao add tip
    	 * 初始化也会建立fov...很麻烦不知道为什么，之前会先建立fovfactory，
    	 * 不应该在初始化editorRender的时候初始化fov
    	 */
         _statusListener = (StatusListener)mainWindow;
        setupFontInfo();
        if(CommincateFactory.serverUrl!=null)
        this.userAgent.setBaseURL(CommincateFactory.serverUrl+SystemUtil.getConfByName("base.baseurl")+"wisiibase");
        else
        	 this.userAgent.setBaseURL(SystemUtil.getBaseURL()+"wisiibase");
    
    }

    /** @see com.wisii.fov.render.java2d.Java2DRenderer#renderPage(PageViewport pageViewport) */
    public void renderPage(PageViewport pageViewport) throws IOException
 {

		super.renderPage(pageViewport);
		int size = pageViewportList.size();
		if (size == 1) {
			this._statusListener.notifyCurrentPageRendered(size - 1);
		}
	}

    /** @see com.wisii.fov.render.java2d.Java2DRenderer#stopRenderer() */
    public void stopRenderer() throws IOException
    {
    	
        int currentPageNumber =getCurrentPageNumber() + 1;
        int numberOfPage =getNumberOfPages();
        if(currentPageNumber > numberOfPage)
        {
            setCurrentPageNumber(0);
        }
        super.stopRenderer();
        this._statusListener.notifyRendererStopped();
    }

    /** @see java.awt.print.Pageable#getPageFormat(int) */
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
    {
        try
         {
             if (pageIndex >= getNumberOfPages())
             {
                 return null;
             }

             PageFormat pageFormat = new PageFormat();
             Paper paper = new Paper();

             Rectangle2D dim = getPageViewport(pageIndex).getViewArea();
             double width = dim.getWidth();
             double height = dim.getHeight();
             height = this.setPagerHeight(height);//设置打印纸的高度

             // if the width is greater than the height assume lanscape mode
             // and swap the width and height values in the paper format
//             if (width > height)
//             {
//                 paper.setImageableArea(0, 0, height / 1000d, width / 1000d);
//                 paper.setSize(height / 1000d, width / 1000d);
//                 pageFormat.setOrientation(PageFormat.LANDSCAPE);
//             }
//             else
//             {
//                 paper.setImageableArea(0, 0, width / 1000d, height / 1000d);
//                 paper.setSize(width / 1000d, height / 1000d);
//                 pageFormat.setOrientation(PageFormat.PORTRAIT);
//             }

             //应该是对话框的设置
             paper = setStatePaper(width,height,paper);
             pageFormat.setOrientation(this.getOrientation());
             pageFormat.setPaper(paper);
             return pageFormat;
         }
         catch (FOVException fovEx)
         {
             throw new IndexOutOfBoundsException(fovEx.getMessage());
         }
    }



    /** @see java.awt.print.Pageable#getPrintable(int) */
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
    {
        return this;
    }


    /**
     * @return the dimensions of the specified page
     * @param pageNum the page number
     * @exception FOVException If the page is out of range or has not been rendered.
     */
    public Dimension getPageImageSize(int pageNum) throws FOVException
    {
        int bitmapWidth = 0;
        int bitmapHeight = 0;
        Rectangle2D  bounds = getPageViewport(pageNum).getViewArea();

        pageWidth = (int)Math.round(bounds.getWidth() / 1000f);
        pageHeight = (int)Math.round(bounds.getHeight() / 1000f);

        //获取屏幕的DPI显示
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution(); //dpi
        userAgent.setTargetResolution(dpi);
        double scaleX = scaleFactor * (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
                        / (userAgent.getTargetPixelUnitToMillimeter());
        double scaleY = scaleFactor * (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
                        / (userAgent.getTargetPixelUnitToMillimeter());

        bitmapWidth = (int)((pageWidth * scaleX) + 0.5);
        bitmapHeight = (int)((pageHeight * scaleY) + 0.5);
        return new Dimension(bitmapWidth, bitmapHeight);
    }

    /** @see com.wisii.fov.render.Renderer#setupFontInfo(com.wisii.fov.fonts.FontInfo) */
	public void setupFontInfo()
    {
        setupFontInfo(new FontInfo());
    }

    /** @see com.wisii.fov.render.AbstractRenderer */
    public String getMimeType()
    {
        return MIME_TYPE;
    }

	public StatusListener get_statusListener()
	{
		return _statusListener;
	}

	public void set_statusListener(StatusListener listener)
	{
		_statusListener = listener;
	}
}
