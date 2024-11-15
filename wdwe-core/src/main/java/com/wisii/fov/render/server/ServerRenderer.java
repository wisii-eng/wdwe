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
 * ServerRenderer.java
 *
 * 改版履历:2007.04.20
 *
 * 版本信息:1.0
 *
 * Copyright:Wise Internat Information Co.,Ltd.
 */

package com.wisii.fov.render.server;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import com.wisii.component.mainFramework.commun.CommunicateProxy;
import com.wisii.component.setting.PrintRef;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.LineArea;
import com.wisii.fov.area.OffDocumentItem;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.render.Graphics2DAdapter;
import com.wisii.fov.render.ImageAdapter;
import com.wisii.fov.render.RenderResult;
import com.wisii.fov.render.Renderer;
import com.wisii.fov.render.java2d.FontSetup;

/**
 * ServerRenderer缓存layout引擎创建的PageViewport数据列表；render结束后，把pageViewport列表发送到客户端。
 * */
public class ServerRenderer implements Renderer
{
	/** user agent */
	protected FOUserAgent _userAgent = FovFactory.newInstance()
			.newFOUserAgent();
	/** List of Viewports */
	protected List _pageViewportList = new java.util.ArrayList();
	/** The 0-based total number of rendered pages */
	private int _numberOfPages;
	/** 向客户端发送PageViewport数据的JspWriter */
	private Object _writer;
	/** 输出模式 */
	private String _outputMode = MimeConstants.MIME_WISII_WDDE_PREVIEW;
	// liuxiao added 20080731 start
	private CommunicateProxy communicateProxy;
	public void setCommunicateProxy(CommunicateProxy cm)
	{
		communicateProxy = cm;
	}
	// liuxiao added 20080731 end
	/**
	 * @return the adapter for painting Java2D images (or null if not
	 *         supported)
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public Graphics2DAdapter getGraphics2DAdapter()
	{
		return null;
	}
	/**
	 * @return the adapter for painting RenderedImages (or null if not
	 *         supported)
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public ImageAdapter getImageAdapter()
	{
		return null;
	}
	/**
	 * Get the MIME type of the renderer.
	 * 
	 * @return The MIME type of the renderer, may return null if not
	 *         applicable.
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public String getMimeType()
	{
		return "Wise Doc Data Editor ServerRenderer";
	}
	/**
	 * This is called if the renderer supports out of order rendering.
	 * 
	 * @param page The page viewport to use
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void preparePage(PageViewport page)
	{
	}
	/**
	 * Tells the renderer to process an item not explicitly placed on the
	 * document (e.g., PDF bookmarks).
	 * 
	 * @param ext The extension element to be rendered
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void processOffDocumentItem(OffDocumentItem ext)
	{
	}
	/**
	 * Tells the renderer to render a particular page.
	 * 
	 * @param page The page to be rendered
	 * @throws IOException if an I/O error occurs
	 * @throws FOVException if a FOV interal error occurs.
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void renderPage(PageViewport page) throws IOException, FOVException
	{
		PageViewport pp = (PageViewport) page.clone();
		pp.setKey(page.getKey());
		_pageViewportList.add(pp);
		this._numberOfPages++;
		if (_outputMode.equals(MimeConstants.MIME_WISII_WDDE_PREVIEW)
				&& communicateProxy != null)
		{
			if (SystemUtil.PRINT_RUN_TIME)
			{
				System.err
						.println("TIME_解析xml数据结束(当前显示的页面_ServerRenderer)。时间（毫秒）："
								+ System.currentTimeMillis());
			}
			communicateProxy.reSendData(
					_pageViewportList.get(_numberOfPages - 1), _writer);
		}
	}
	/**
	 * Signals the end of the rendering phase.
	 * 
	 * @throws IOException If an I/O error occurs
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void stopRenderer() throws IOException
	{
		if (communicateProxy != null)
		{
			communicateProxy
					.reSendData(this._userAgent.getAllLayers(), _writer);
		}
		if (SystemUtil.PRINT_RUN_TIME)
		{
			System.err
					.println("TIME_解析xml数据结束(全部的页面_ServerRenderer)。    时间（毫秒）："
							+ System.currentTimeMillis());
		}
		if (_outputMode.equalsIgnoreCase(MimeConstants.MIME_WISII_WDDE_PREVIEW))
		{
		}
		else if (_outputMode.equalsIgnoreCase(MimeConstants.MIME_WISII_PAGENUM)
				&& communicateProxy != null)
		{
			// 返回总页数
			communicateProxy.reSendData(new Integer(_numberOfPages), _writer);
			clearViewportList();
		}
		else if (communicateProxy != null)
		{
			for (int count = 0; count < _numberOfPages; count++)
			{
				communicateProxy.reSendData(_pageViewportList.get(count),
						_writer);
			}
			clearViewportList();
		}
	}
	/**
	 * Set the User Agent.
	 * 
	 * @param agent The User Agent
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void setUserAgent(FOUserAgent agent)
	{
		_userAgent = agent;
		_userAgent.setRendererOverride(this);
	}
	/**
	 * Set up the given FontInfo.
	 * 
	 * @param fontInfo The font information
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void setupFontInfo(FontInfo fontInfo)
	{
		BufferedImage fontImage = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = fontImage.createGraphics();
		// The next line is important to get accurate font metrics!
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		FontSetup.setup(fontInfo, g);
	}
	/**
	 * Tells the renderer that a new page sequence starts.
	 * 
	 * @param seqTitle The title of the page sequence
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void startPageSequence(LineArea seqTitle)
	{
	}
	/**
	 * Initiates the rendering phase.
	 * 
	 * @param outputStream The OutputStream to use for output
	 * @throws IOException If an I/O error occurs
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public void startRenderer(OutputStream outputStream) throws IOException
	{
	}
	/**
	 * Reports if out of order rendering is supported.
	 * 
	 * @return True if this renderer supports out of order rendering.
	 * @todo Implement this com.wisii.fov.render.Renderer method
	 */
	public boolean supportsOutOfOrder()
	{
		return false;
	}
	/** 设置输出模式 */
	public void setOutputMode(String mode)
	{
		if (MimeConstants.MIME_WISII_AWT_PREVIEW.equals(mode))
			_outputMode = MimeConstants.MIME_WISII_WDDE_PREVIEW;
		else
		{
			this._outputMode = mode;
		}
	}
	/** 设置向客户端发送数据的writer */
	public void setWriter(Object out)
	{
		this._writer = out;
	}
	/** Clears the ViewportList. Used if the document is reloaded. */
	public void clearViewportList()
	{
		_pageViewportList.clear();
		_numberOfPages = 0;
	}
	public void setupPrinterInfo(Map PrinterList, PrintRef PrinterName)
	{
		// TODO 自动生成方法存根
	}
	@Override
	public FOUserAgent getUserAgent()
	{
		return _userAgent;
	}
	public List getPageViewportList()
	{
		return _pageViewportList;
	}
	protected int getTotalPage()
	{
		return _numberOfPages;
	}
	@Override
	public RenderResult getResultInfo()
	{
		return new RenderResult(_numberOfPages,null);
	}
}
