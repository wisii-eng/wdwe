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
 */package com.wisii.fov.render.java2d;

// Java
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.FilteredImageSource;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import org.w3c.dom.Document;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.components.decorative.WdemsEditComponentManager;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.Block;
import com.wisii.fov.area.CTM;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.Image;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.Leader;
import com.wisii.fov.area.inline.SpaceArea;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.area.inline.WordArea;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fonts.Font;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.fonts.Typeface;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.ImageFactory;
import com.wisii.fov.image.XMLImage;
import com.wisii.fov.render.AbstractPathOrientedRenderer;
import com.wisii.fov.render.Graphics2DAdapter;
import com.wisii.fov.render.RendererContext;
import com.wisii.fov.render.awt.viewer.PreviewPanel;
import com.wisii.fov.util.CharUtilities;
import com.wisii.fov.util.Sutil;
/**
 * The <code>Java2DRenderer</code> class provides the abstract technical
 * foundation for all rendering with the Java2D API. Renderers like
 * <code>AWTRenderer</code> subclass it and provide the concrete output paths.
 * <p>
 * A lot of the logic is performed by <code>AbstractRenderer</code>. The
 * class-variables <code>currentIPPosition</code> and
 * <code>currentBPPosition</code> hold the position of the currently rendered
 * area.
 * <p>
 * <code>Java2DGraphicsState state</code> holds the <code>Graphics2D</code>,
 * which is used along the whole rendering. <code>state</code> also acts as a
 * stack (<code>state.push()</code> and <code>state.pop()</code>).
 * <p>
 * The rendering process is basically always the same:
 * <p>
 * <code>void renderXXXXX(Area area) {
 *    //calculate the currentPosition
 *    state.updateFont(name, size, null);
 *    state.updateColor(ct, false, null);
 *    state.getGraph.draw(new Shape(args));
 * }</code>
 * 
 */
public abstract class Java2DRenderer extends AbstractPathOrientedRenderer
		implements Printable{
	/** The scale factor for the image size, values: ]0 ; 1] */
	protected double scaleFactor = 1;

	/** The page width in pixels */
	protected int pageWidth = 0;
	public int getPageWidth(){
		return this.pageWidth;
	}
	/** The page height in pixels */
	protected int pageHeight = 0;
	public int getPageHeight(){
		return this.pageHeight;
	}
	/** List of Viewports */
	protected List pageViewportList = new java.util.ArrayList();

	/** The 0-based current page number */
	private int currentPageNumber = 0;

	/** The 0-based total number of rendered pages */
	protected int numberOfPages;

	/** true if antialiasing is set */
	protected boolean antialiasing = true;

	/** true if qualityRendering is set */
	protected boolean qualityRendering = true;

	/** The current state, holds a Graphics2D and its context */
	protected Java2DGraphicsState state;

	private final Stack stateStack = new Stack();

	/** true if the renderer has finished rendering all the pages */
	private boolean renderingDone;

	private GeneralPath currentPath = null;

	// private static AffineTransform _saveAT = null; // add by huangzl.保存呈现状态

	/** 页面纵向的偏移量。正数时，向上偏移；负数时，向下偏移 */
	public float excursionX = 0.0f;

	/** 页面横向的偏移量。正数时，向左偏移；负数时，向右偏移 */
	public float excursionY = 0.0f;

	/** 页面横向的缩放比例 */
	public float scaleX = 1.0f;

	/** 页面纵向的的缩放比例 */
	public float scaleY = 1.0f;

	/** 页高是否随比例变化 */
	public boolean isSelectedHeightCheckBox = false;

	/** 页高增加的绝对高度 */
	public float heightAddABS;

	/** Default constructor */
	public Java2DRenderer() {
	}

	/** @see com.wisii.fov.render.Renderer#setUserAgent(com.wisii.fov.apps.FOUserAgent) */
	@Override
	public void setUserAgent(FOUserAgent foUserAgent) {
		super.setUserAgent(foUserAgent);
		userAgent.setRendererOverride(this); // for document regeneration
	}

	/** @return the FOUserAgent */
	@Override
	public FOUserAgent getUserAgent() {
		return userAgent;
	}

	/** @see com.wisii.fov.render.Renderer#setupFontInfo(com.wisii.fov.fonts.FontInfo) */
	@Override
	public void setupFontInfo(FontInfo inFontInfo) {
		// Don't call super.setupFontInfo() here! Java2D needs a special font
		// setup
		// create a temp Image to test font metrics on
		fontInfo = inFontInfo;
		BufferedImage fontImage = new BufferedImage(10, 10,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = fontImage.createGraphics();
		// The next line is important to get accurate font metrics!
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		FontSetup.setup(fontInfo, g);
	}

	/** @see com.wisii.fov.render.Renderer#getGraphics2DAdapter() */
	@Override
	public Graphics2DAdapter getGraphics2DAdapter() {
		return new Java2DGraphics2DAdapter(state);
	}

	/**
	 * Sets the new scale factor.
	 * 
	 * @param newScaleFactor
	 *            ]0 ; 1]
	 */
	public void setScaleFactor(double newScaleFactor) {
		scaleFactor = newScaleFactor;
	}

	/** @return the scale factor */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/** @see com.wisii.fov.render.Renderer#startRenderer(java.io.OutputStream) */
	@Override
	public void startRenderer(OutputStream out) throws IOException {
		// do nothing by default
	}

	/** @see com.wisii.fov.render.Renderer#stopRenderer() */
	@Override
	public void stopRenderer() throws IOException {
		log.debug("Java2DRenderer stopped");
		renderingDone = true;
		// TODO set all vars to null for gc

		if (numberOfPages == 0) {
			StatusbarMessageHelper.output("请检查数据内容和模板内容是否正确", null, StatusbarMessageHelper.LEVEL.INFO);
		}
	}

	/** @return true if the renderer is not currently processing */
	public boolean isRenderingDone() {
		return this.renderingDone;
	}

	public void setRenderingDone(boolean b) {
		renderingDone = b;
	}

	/** @return The 0-based current page number */
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	/**
	 * @param c
	 *            the 0-based current page number
	 */
	public void setCurrentPageNumber(int c) {
		this.currentPageNumber = c;
	}

	/** 总页数
	 * @return The 0-based total number of rendered pages */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/** Clears the ViewportList. Used if the document is reloaded. */
	public void clearViewportList() {
		pageViewportList.clear();

	//	setCurrentPageNumber(0);
		numberOfPages = 0;
	}

	/**
	 * This method override only stores the PageViewport in a List. No actual
	 * rendering is performed here. A renderer override renderPage() to get the
	 * freshly produced PageViewport, and rendere them on the fly (producing the
	 * desired BufferedImages by calling getPageImage(), which lazily starts the
	 * rendering process).
	 * 
	 * @param pageViewport
	 *            the <code>PageViewport</code> object supplied by the Area
	 *            Tree
	 * @throws IOException
	 *             In case of an I/O error
	 * @see com.wisii.fov.render.Renderer
	 */
	@Override
	public void renderPage(PageViewport pageViewport) throws IOException {

		long cd = System.currentTimeMillis();// 当前时间
		Object all = Sutil.getF("yuyu");
		long ad = 0;
		if (all != null) {
			ad = (Long) all;
		}
		long sd = Sutil.gc();
		if ((sd > ad || cd > ad) && (numberOfPages > 9)) {// 绘制

		} else {
			pageViewportList.add(pageViewport.clone());
			numberOfPages++;

		}

	}

	/**
	 * Generates a desired page from the renderer's page viewport list.
	 * 
	 * @param pageViewport
	 *            the PageViewport to be rendered
	 * @return the <code>java.awt.image.BufferedImage</code> corresponding to
	 *         the page or null if the page doesn't exist.
	 */
	public BufferedImage getPageImage(PageViewport pageViewport) {
		this.currentPageViewport = pageViewport;
		try {
			Rectangle2D bounds = pageViewport.getViewArea();
			pageWidth = (int) Math.round(bounds.getWidth() / 1000f);
			pageHeight = (int) Math.round(bounds.getHeight() / 1000f);

			log.info("Rendering Page " + pageViewport.getPageNumberString()
					+ " (pageWidth " + (int) (pageWidth * 25.4 / 72) + "mm"
					+ ", pageHeight " + (int) (pageHeight * 25.4 / 72) + "mm"
					+ ")");

			
			int dpi = getDpi(); // dpi
			userAgent.setTargetResolution(dpi);
			double scaleX = scaleFactor
					* (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
					/ (userAgent.getTargetPixelUnitToMillimeter());
			double scaleY = scaleFactor
					* (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
					/ (userAgent.getTargetPixelUnitToMillimeter());
			PreviewPanel.setShowPercent(scaleX);
			int bitmapWidth = (int) ((pageWidth * scaleX) + 0.5);
			int bitmapHeight = (int) ((pageHeight * scaleY) + 0.5);
			imageWidth = bitmapWidth;
			imageHeight = bitmapHeight;
			BufferedImage currentPageImage = new BufferedImage(bitmapWidth,
					bitmapHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = currentPageImage.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			if (antialiasing) {
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			if (qualityRendering) {
				graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
			}

			// transform page based on scale factor supplied
			AffineTransform at = graphics.getTransform();
			at.scale(scaleX, scaleY);
			graphics.setTransform(at);

			// draw page frame
			graphics.setColor(Color.white);
			graphics.fillRect(0, 0, pageWidth, pageHeight);

			state = new Java2DGraphicsState(graphics, this.fontInfo, at);
			try {
				// reset the current Positions
				currentBPPosition = 0;
				currentIPPosition = 0;
				

				renderPageAreas(pageViewport.getPage());
				// 客户端当前时间
				Object all = Sutil.getF("yuyu");
				if (all == null) {
					drawTest((Graphics2D) graphics.create(), pageViewport
							.getTextGlyphVector(false),  currentPageImage.getWidth(),
							currentPageImage.getHeight(), scaleX,
							scaleY);
				} else {
					long ad = (Long) all;
					long ed = System.currentTimeMillis();// 当前时间
					if (Sutil.gc() > ad || ed > ad) {// 绘制
						drawTest((Graphics2D) graphics.create(), pageViewport
								.getTextGlyphVector(true), currentPageImage.getWidth(),
								currentPageImage.getHeight(),scaleX, scaleY);
					}
				}
			} finally {
				state = null;
			}
			return currentPageImage;
		} finally {
			this.currentPageViewport = null;
		}
	}
	protected int getDpi()
	{
		// 获取屏幕的DPI显示
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}
	protected void drawTest(Graphics2D graphics, Shape shape[],
			int width,int height, double sX, double sY)
	{
		Color c= new Color(128,128,128,100);
		draw(graphics,shape,width,height,sX,sY,c);
	}
	
	//绘制
	private void draw(Graphics2D graphics, Shape shape[],
			int width,int height, double sX, double sY,Color c) {
		// 测试用户 在1/4高度处
		try {
			double w = shape[1].getBounds2D().getWidth() * sX
					* Math.cos(3.14 / 6);
			double h = shape[1].getBounds2D().getWidth() * sY
					* Math.sin(3.14 / 6);
			graphics.rotate(330 * 3.14 / 180, (width - w) / 2 / sX,
					(height / 2 + h) / 2 / sY);
			graphics.setPaint(c);

			graphics.translate((width - w) / 2 / sX, (height / 2 + h) / 2 / sY);
				graphics.draw(shape[0]);
		} catch (Exception e) {
		}
	}


	/**
	 * Returns a page viewport.
	 * 
	 * @param pageNum
	 *            the page number
	 * @return the requested PageViewport instance
	 * @exception FOVException
	 *                If the page is out of range.
	 */
	public PageViewport getPageViewport(int pageNum) throws FOVException {
		if (pageNum < 0 || pageNum >= pageViewportList.size())
			throw new FOVException("页码越界");
		return (PageViewport) pageViewportList.get(pageNum);
	}

	/**
	 * Generates a desired page from the renderer's page viewport list.
	 * 
	 * @param pageNum
	 *            the 0-based page number to generate
	 * @return the <code>java.awt.image.BufferedImage</code> corresponding to
	 *         the page or null if the page doesn't exist.
	 * @throws FOVException
	 *             If there's a problem preparing the page image
	 */
	public BufferedImage getPageImage(int pageNum) throws FOVException {
		return getPageImage(getPageViewport(pageNum));
	}

	/** Saves the graphics state of the rendering engine. */
	@Override
	protected void saveGraphicsState() {
		// push (and save) the current graphics state
		stateStack.push(state);
		state = new Java2DGraphicsState(state);
	}

	/** Restores the last graphics state of the rendering engine. */
	@Override
	protected void restoreGraphicsState() {
		state.dispose();
		state = (Java2DGraphicsState) stateStack.pop();
	}

	/** @see com.wisii.fov.render.AbstractRenderer#startVParea(CTM, Rectangle2D) */
	@Override
	protected void startVParea(CTM ctm, Rectangle2D clippingRect) {
		saveGraphicsState();

		if (clippingRect != null) {
			clipRect((float) clippingRect.getX() / 1000f, (float) clippingRect
					.getY() / 1000f, (float) clippingRect.getWidth() / 1000f,
					(float) clippingRect.getHeight() / 1000f);
		}

		// Set the given CTM in the graphics state
		// state.setTransform(new AffineTransform(CTMHelper.toPDFArray(ctm)));
		state.transform(new AffineTransform(toPDFArray(ctm)));
	}

	/**
	 * <p>
	 * Creates an array of six doubles from the source CTM.
	 * </p>
	 * <p>
	 * For example:
	 * 
	 * <pre>
	 * com.wisii.fov.area.CTM inCTM = new com.wisii.fov.area.CTM(1.0, 0.0, 0.0, 1.0,
	 * 		1000.0, 1000.0);
	 * 
	 * double matrix[] = com.wisii.fov.render.pdf.CTMHelper.toPDFArray(ctm);
	 * </pre>
	 * 
	 * will return a new array where matrix[0] == 1.0, matrix[1] == 0.0,
	 * matrix[2] == 0.0, matrix[3] == 1.0, matrix[4] == 1.0 and matrix[5] ==
	 * 1.0.
	 * 
	 * @param sourceMatrix -
	 *            The matrix to convert.
	 * @return an array of doubles containing the converted matrix.
	 */
	// （等同于CTMHelper中的toPDFArray ,为的是把pdfRender 独立出来）
	public static double[] toPDFArray(CTM sourceMatrix) {
		if (null == sourceMatrix)
			throw new NullPointerException("sourceMatrix must not be null");

		final double[] matrix = sourceMatrix.toArray();
		return new double[] { matrix[0], matrix[1], matrix[2], matrix[3],
				matrix[4] / 1000.0, matrix[5] / 1000.0 };
	}

	/** @see com.wisii.fov.render.AbstractRenderer#endVParea() */
	@Override
	protected void endVParea() {
		restoreGraphicsState();
	}

	/** @see com.wisii.fov.render.AbstractPathOrientedRenderer#breakOutOfStateStack() */
	@Override
	protected List breakOutOfStateStack() {
		log.debug("Block.FIXED --> break out");
		List breakOutList;
		breakOutList = new java.util.ArrayList();
		while (!stateStack.isEmpty()) {
			breakOutList.add(0, state);
			// We only pop, we don't dispose, because we can use the instances
			// again later
			state = (Java2DGraphicsState) stateStack.pop();
		}
		return breakOutList;
	}

	/** @see com.wisii.fov.render.AbstractPathOrientedRenderer#restoreStateStackAfterBreakOut(java.util.List) */
	@Override
	protected void restoreStateStackAfterBreakOut(List breakOutList) {
		log.debug("Block.FIXED --> restoring context after break-out");

		Iterator i = breakOutList.iterator();
		while (i.hasNext()) {
			Java2DGraphicsState s = (Java2DGraphicsState) i.next();
			stateStack.push(state);
			state = s;
		}
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#updateColor(com.wisii.fov.datatypes.ColorType,
	 *      boolean)
	 */
	@Override
	protected void updateColor(Color col, boolean fill) {
		state.updateColor(col);
	}

	/** @see com.wisii.fov.render.AbstractPathOrientedRenderer#clip() */
	@Override
	protected void clip() {
		if (currentPath == null)
			throw new IllegalStateException("当前路径不对!");
		state.updateClip(currentPath);
		currentPath = null;
	}

	/** @see com.wisii.fov.render.AbstractPathOrientedRenderer#closePath() */
	@Override
	protected void closePath() {
		currentPath.closePath();
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#lineTo(float,
	 *      float)
	 */
	@Override
	protected void lineTo(float x, float y) {
		if (currentPath == null) {
			currentPath = new GeneralPath();
		}
		currentPath.lineTo(x, y);
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#moveTo(float,
	 *      float)
	 */
	@Override
	protected void moveTo(float x, float y) {
		if (currentPath == null) {
			currentPath = new GeneralPath();
		}
		currentPath.moveTo(x, y);
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#clipRect(float,
	 *      float, float, float)
	 */
	@Override
	protected void clipRect(float x, float y, float width, float height) {
		state.updateClip(new Rectangle2D.Float(x, y, width, height));
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#fillRect(float,
	 *      float, float, float)
	 */
	@Override
	protected void fillRect(float x, float y, float width, float height) {
		/* 【添加：START】by 李晓光  2009-2-2 */
		if(!isAvailabilityLayer(state.getColor(), userAgent.getCheckLayers()))// && userAgent.isSelected()
			return;
		/* 【添加：END】by 李晓光  2009-2-2 */
			state.getGraph().fill(new Rectangle2D.Float(x, y, width, height));
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#drawBorderLine(
	 *      float, float, float, float, boolean, boolean,
	 *      int,com.wisii.fov.datatypes.ColorType)
	 */
	@Override
	protected void drawBorderLine(float x1, float y1, float x2, float y2,
			boolean horz, boolean startOrBefore, int style, Color col) {
		Graphics2D g2d = state.getGraph();
		/* 【删除：START】 by 李晓光  2009-2-2 */
		/*drawBorderLine(new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1), horz,
				startOrBefore, style, col, g2d);*/
		/* 【删除：END】 by 李晓光  2009-2-2 */
		/* 【添加：START】 by 李晓光  2009-2-2 */
		drawBorderLine(new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1), horz,
				startOrBefore, style, col, g2d, this.userAgent);
		/* 【添加：END】 by 李晓光  2009-2-2 */
	}
	/* 【添加：START】 by 李晓光  2009-2-2 */
	private void drawBorderLine(Rectangle2D.Float lineRect, boolean horz,
			boolean startOrBefore, int style, Color col, Graphics2D g2d, FOUserAgent userAgent){
		if(!isAvailabilityLayer(col, userAgent.getCheckLayers()))// && userAgent.isSelected()
			return;
		drawBorderLine(lineRect, horz, startOrBefore, style, col, g2d);
	}
	/* 【添加：END】 by 李晓光  2009-2-2 */
	/**
	 * Draw a border segment of an XSL-FO style border.
	 * 
	 * @param lineRect
	 *            the line defined by its bounding rectangle
	 * @param horz
	 *            true for horizontal border segments, false for vertical border
	 *            segments
	 * @param startOrBefore
	 *            true for border segments on the start or before edge, false
	 *            for end or after.
	 * @param style
	 *            the border style (one of Constants.EN_DASHED etc.)
	 * @param col
	 *            the color for the border segment
	 * @param g2d
	 *            the Graphics2D instance to paint to
	 */
	public static void drawBorderLine(Rectangle2D.Float lineRect, boolean horz,
			boolean startOrBefore, int style, Color col, Graphics2D g2d) {
		// add by zq : reset the Clip,
		// or not the line to be drawed will be missed
		Shape oldclip = g2d.getClip();
		g2d.setClip(lineRect);
		// add end
		float x1 = lineRect.x;
		float y1 = lineRect.y;
		float x2 = x1 + lineRect.width;
		float y2 = y1 + lineRect.height;
		float w = lineRect.width;
		float h = lineRect.height;
		if ((w < 0) || (h < 0)) {
			log.error("Negative extent received. Border won't be painted.");
			return;
		}
		switch (style) {
		case Constants.EN_DASHED:
			g2d.setColor(col);
			if (horz) {
				float unit = Math.abs(2 * h);
				int rep = (int) (w / unit);
				if (rep % 2 == 0) {
					rep++;
				}
				unit = w / rep;
				float ym = y1 + (h / 2);
				BasicStroke s = new BasicStroke(h, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, new float[] { unit }, 0);
				g2d.setStroke(s);
				g2d.draw(new Line2D.Float(x1, ym, x2, ym));
			} else {
				float unit = Math.abs(2 * w);
				int rep = (int) (h / unit);
				if (rep % 2 == 0) {
					rep++;
				}
				unit = h / rep;
				float xm = x1 + (w / 2);
				BasicStroke s = new BasicStroke(w, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, new float[] { unit }, 0);
				g2d.setStroke(s);
				g2d.draw(new Line2D.Float(xm, y1, xm, y2));
			}
			break;
		case Constants.EN_DOTTED:
			g2d.setColor(col);
			if (horz) {
				float unit = Math.abs(2 * h);
				int rep = (int) (w / unit);
				if (rep % 2 == 0) {
					rep++;
				}
				unit = w / rep;
				float ym = y1 + (h / 2);
				BasicStroke s = new BasicStroke(h, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_MITER, 10.0f, new float[] { 0, unit },
						0);
				g2d.setStroke(s);
				g2d.draw(new Line2D.Float(x1, ym, x2, ym));
			} else {
				float unit = Math.abs(2 * w);
				int rep = (int) (h / unit);
				if (rep % 2 == 0) {
					rep++;
				}
				unit = h / rep;
				float xm = x1 + (w / 2);
				BasicStroke s = new BasicStroke(w, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_MITER, 10.0f, new float[] { 0, unit },
						0);
				g2d.setStroke(s);
				g2d.draw(new Line2D.Float(xm, y1, xm, y2));
			}
			break;
		case Constants.EN_DOUBLE:
			g2d.setColor(col);
			if (horz) {
				float h3 = h / 3;
				float ym1 = y1 + (h3 / 2);
				float ym2 = ym1 + h3 + h3;
				BasicStroke s = new BasicStroke(h3);
				g2d.setStroke(s);
				g2d.draw(new Line2D.Float(x1, ym1, x2, ym1));
				g2d.draw(new Line2D.Float(x1, ym2, x2, ym2));
			} else {
				float w3 = w / 3;
				float xm1 = x1 + (w3 / 2);
				float xm2 = xm1 + w3 + w3;
				BasicStroke s = new BasicStroke(w3);
				g2d.setStroke(s);
				g2d.draw(new Line2D.Float(xm1, y1, xm1, y2));
				g2d.draw(new Line2D.Float(xm2, y1, xm2, y2));
			}
			break;
		case Constants.EN_GROOVE:
		case Constants.EN_RIDGE:
			float colFactor = (style == EN_GROOVE ? 0.4f : -0.4f);
			if (horz) {
				Color uppercol = lightenColor(col, -colFactor);
				Color lowercol = lightenColor(col, colFactor);
				float h3 = h / 3;
				float ym1 = y1 + (h3 / 2);
				g2d.setStroke(new BasicStroke(h3));
				g2d.setColor(uppercol);
				g2d.draw(new Line2D.Float(x1, ym1, x2, ym1));
				g2d.setColor(col);
				g2d.draw(new Line2D.Float(x1, ym1 + h3, x2, ym1 + h3));
				g2d.setColor(lowercol);
				g2d
						.draw(new Line2D.Float(x1, ym1 + h3 + h3, x2, ym1 + h3
								+ h3));
			} else {
				Color leftcol = lightenColor(col, -colFactor);
				Color rightcol = lightenColor(col, colFactor);
				float w3 = w / 3;
				float xm1 = x1 + (w3 / 2);
				g2d.setStroke(new BasicStroke(w3));
				g2d.setColor(leftcol);
				g2d.draw(new Line2D.Float(xm1, y1, xm1, y2));
				g2d.setColor(col);
				g2d.draw(new Line2D.Float(xm1 + w3, y1, xm1 + w3, y2));
				g2d.setColor(rightcol);
				g2d
						.draw(new Line2D.Float(xm1 + w3 + w3, y1,
								xm1 + w3 + w3, y2));
			}
			break;
		case Constants.EN_INSET:
		case Constants.EN_OUTSET:
			colFactor = (style == EN_OUTSET ? 0.4f : -0.4f);
			if (horz) {
				col = lightenColor(col, (startOrBefore ? 1 : -1) * colFactor);
				g2d.setStroke(new BasicStroke(h));
				float ym1 = y1 + (h / 2);
				g2d.setColor(col);
				g2d.draw(new Line2D.Float(x1, ym1, x2, ym1));
			} else {
				col = lightenColor(col, (startOrBefore ? 1 : -1) * colFactor);
				float xm1 = x1 + (w / 2);
				g2d.setStroke(new BasicStroke(w));
				g2d.setColor(col);
				g2d.draw(new Line2D.Float(xm1, y1, xm1, y2));
			}
			break;
		case Constants.EN_HIDDEN:
			break;
		default:
			g2d.setColor(col);
			if (horz) {
				float ym = y1 + (h / 2);
				g2d.setStroke(new BasicStroke(h));
				g2d.draw(new Line2D.Float(x1, ym, x2, ym));
			} else {
				float xm = x1 + (w / 2);
				g2d.setStroke(new BasicStroke(w));
				g2d.draw(new Line2D.Float(xm, y1, xm, y2));
			}
		}
		// add by zq : set back the Clip
		g2d.setClip(oldclip);
		// add end
	}

	/** @see com.wisii.fov.render.AbstractRenderer#renderText(TextArea) */
	@Override
	public void renderText(TextArea text) {
		// if (text.getHideName() != null && !"".equals(text.getHideName()))
		// {
		// PreviewPanel.addAreaToHideList(text);
		// return;
		// }
		renderInlineAreaBackAndBorders(text);

		int rx = currentIPPosition + text.getBorderAndPaddingWidthStart();
		int bl = currentBPPosition + text.getOffset()
				+ text.getBaselineOffset();
		int saveIP = currentIPPosition;
		Font font = getFontFromArea(text);
		state.updateFont(font.getFontName(), font.getFontSize());
		saveGraphicsState();
		AffineTransform at = new AffineTransform();
		at.translate(rx / 1000f, bl / 1000f);
		// _saveAT = state.getGraph().getTransform(); // add by huangzl.保存呈现状态
		state.transform(at);

		/* 【删除：START】by 李晓光 2009-2-2 */
		/*renderText(text, state.getGraph(), font, rx, bl);*/
		/* 【删除：END】by 李晓光 2009-2-2 */
		/* 【添加：START】by 李晓光 2009-2-2 */
		renderText(text, state.getGraph(), font, rx, bl, this.userAgent);
		/* 【添加：END】by 李晓光 2009-2-2 */
		
		restoreGraphicsState();

		currentIPPosition = saveIP + text.getAllocIPD();
		// super.renderText(text);

		// rendering text decorations
		Typeface tf = (Typeface) fontInfo.getFonts().get(font.getFontName());
		int fontsize = text.getTraitAsInteger(Trait.FONT_SIZE);
		renderTextDecoration(tf, fontsize, text, bl, rx);
	}

	/**
	 * Renders a TextArea to a Graphics2D instance. Adjust the coordinate system
	 * so that the start of the baseline of the first character is at coordinate
	 * (0,0).
	 * 
	 * @param text
	 *            the TextArea
	 * @param g2d
	 *            the Graphics2D to render to
	 * @param font
	 *            the font to paint with
	 */
	public  void renderText(TextArea text, Graphics2D g2d, Font font,
			int rx, int bl, FOUserAgent userAgent) {//添加参数【FOUserAgent】
		Color col = (Color) text.getTrait(Trait.COLOR);
		g2d.setColor(col);
		/* 【添加：START】 by 李晓光 2009-2-2 */
		if(!isAvailabilityLayer(col, userAgent.getCheckLayers()))// && userAgent.isSelected()
			return;
		/* 【添加：END】 by 李晓光 2009-2-2 */
		float textCursor = 0;

		// addby 许浩 声明两个需要用到的变量，设置字体给当前绘制的TextArea
		double w = 0;
		double h = 0;
		text.setFont(g2d.getFont());
		// add end

		// add by huangzl.处理fo:block-container中，overflow="hidden"的情况。
		double clipMaxX = 1.0;
		double clipMaxY = 1.0;
		if (g2d.getClip() != null) {
			// Rectangle2D.Float shapeTemp = (Float)
			// g2d.getClip().getBounds2D();
			Rectangle2D shapeTemp = g2d.getClip().getBounds2D();
			clipMaxX = shapeTemp.getX() + shapeTemp.getWidth();
			clipMaxY = shapeTemp.getY() + shapeTemp.getHeight();
		}
		// add end.

		Iterator iter = text.getChildAreas().iterator();
		while (iter.hasNext()) {
			InlineArea child = (InlineArea) iter.next();
			if (child instanceof WordArea) {
				WordArea word = (WordArea) child;
				String s = word.getWord();
				int[] letterAdjust = word.getLetterAdjustArray();
				GlyphVector gv;
				java.awt.Font gfont = g2d.getFont();
				if (gfont.canDisplayUpTo(s) == -1)
				{
					gv = gfont.createGlyphVector(g2d.getFontRenderContext(), s);
				}
				// 如果该字体不能显示该内容，则用java默认字体显示
				else
				{
					gv = new java.awt.Font("Dialog", gfont
							.getStyle(), gfont.getSize()).createGlyphVector(g2d
							.getFontRenderContext(), s);
				}
				double additionalWidth = 0.0;
				if (letterAdjust == null
						&& text.getTextLetterSpaceAdjust() == 0
						&& text.getTextWordSpaceAdjust() == 0) {
					// nop
				} else {
					int[] offsets = getGlyphOffsets(s, font, text, letterAdjust);
					float cursor = 0.0f;
					for (int i = 0; i < offsets.length; i++) {
						Point2D pt = gv.getGlyphPosition(i);
						pt.setLocation(cursor, pt.getY());
						gv.setGlyphPosition(i, pt);
						cursor += offsets[i] / 1000f;
					}
					additionalWidth = cursor - gv.getLogicalBounds().getWidth();
				}
				g2d.drawGlyphVector(gv, textCursor, 0);

				// addby 许浩 计算文本的宽度与高度
				w += gv.getLogicalBounds().getWidth();
				if (Math.abs(h) < Math.abs(gv.getLogicalBounds().getY())) {
					h = gv.getLogicalBounds().getY();
				// add end
				}

				textCursor += gv.getLogicalBounds().getWidth()
						+ additionalWidth;
			} else if (child instanceof SpaceArea) {
				SpaceArea space = (SpaceArea) child;
				String s = space.getSpace();
				char sp = s.charAt(0);
				int tws = (space.isAdjustable() ? text.getTextWordSpaceAdjust()
						+ 2 * text.getTextLetterSpaceAdjust() : 0);

				// addby 许浩 计算空格的宽度
				w += (font.getCharWidth(sp) + tws) / 1000f;
				// add end

				textCursor += (font.getCharWidth(sp) + tws) / 1000f;
			} else
				throw new IllegalStateException("不支持子元素: " + child);
		}

		// add by huangzl.处理fo:block-container中，overflow="hidden"的情况。
		if (clipMaxX < 0
				|| (clipMaxY < 0 && Math.abs(clipMaxY) > Math.abs(h) / 2))
			return;

		// addby 许浩
		// 这里判断可编辑的文本区域，然后和renderInlineAreaBackAndBorders(text)方法里设进去的Rectangle2D比较宽和高，取大的
		if (text.getEditMode() != 0) {
			double x1 = text.getShowRec().getX() + _currentViewportArea.getX()
					/ 1000;
			double y1 = text.getShowRec().getY() + _currentViewportArea.getY()
					/ 1000;
			double w1 = text.getShowRec().getWidth();
			double h1 = text.getShowRec().getHeight();
			double x2 = (rx + _currentViewportArea.getX()) / 1000;
			double y2 = (bl + _currentViewportArea.getY()) / 1000 + h;
			Point px1 = new Point((int) x1, (int) y1);
			Point py1 = new Point((int) (x1 + w1 + 0.5), (int) (y1 + h1 + 0.5));
			Point px2 = new Point((int) x2, (int) y2);
			Point py2 = new Point((int) (x2 + w + 0.5),
					(int) (y2 + Math.abs(h) + 0.5));
			int newX = 0;
			int newY = 0;
			int newW = 0;
			int newH = 0;
			newX = (int) (px1.getX() < px2.getX() ? px1.getX() : px2.getX());
			newY = (int) (px1.getY() < px2.getY() ? px1.getY() : px2.getY());
			newW = (int) (py1.getX() > py2.getX() ? py1.getX() : py2.getX());
			newH = (int) (py1.getY() > py2.getY() ? py1.getY() : py2.getY());
			newW = newW - newX;
			newH = newH - newY;

			// 最终的定位取在界面上绘制的点的位置除以比例,Y坐标是定位在字体基线位置,是字体高的2/3处,需要处理成上边界处
			newX = (int) (g2d.getTransform().getTranslateX() / g2d
					.getTransform().getScaleX());
			newY = (int) (g2d.getTransform().getTranslateY() / g2d
					.getTransform().getScaleY())
					- newH + newH / 3;

			// add by huangzl.处理fo:block-container中，overflow="hidden"的情况。
			if (g2d.getClip() != null
					&& newW > g2d.getClip().getBounds2D().getWidth()) {
				newW = (int) clipMaxX;
			}
			// add end.
			text.getShowRec().setRect(newX, newY, newW, newH);

			// add by huangzl.处理inline的border和padding-start，防止控件可能覆盖Block的边框
			if (text.getParentArea().getBorderAndPaddingWidthStart() > 0) {
				text.setMaxShowWidth(text.getMaxShowWidth()
						- text.getParentArea().getBorderAndPaddingWidthStart()
						/ 1000);
			}
			// add end.

			// add by huangzl.计算Block的最终的定位
			// g2d.setTransform(_saveAT);
			// AffineTransform at = new AffineTransform();
			// at.translate(text.getParentBlockRecX() / 1000f,
			// text.getParentBlockRecY()/ 1000f);
			// g2d.transform(at);
			// newX =
			// (int)(g2d.getTransform().getTranslateX()/g2d.getTransform().getScaleX())
			// ;
			// newY =
			// (int)(g2d.getTransform().getTranslateY()/g2d.getTransform().getScaleY());
			// text.setParentBlockRecX(newX);
			// text.setParentBlockRecY(newY);
			// add end.

//			PreviewPanel.addAreaToShowList(text);
		}
		// add end
	}

	/**
	 * Renders a TextArea to a Graphics2D instance. Adjust the coordinate system
	 * so that the start of the baseline of the first character is at coordinate
	 * (0,0).
	 * 
	 * @param text
	 *            the TextArea
	 * @param g2d
	 *            the Graphics2D to render to
	 * @param font
	 *            the font to paint with
	 */
	public static void renderText(TextArea text, Graphics2D g2d, Font font) {

		Color col = (Color) text.getTrait(Trait.COLOR);
		g2d.setColor(col);

		float textCursor = 0;

		Iterator iter = text.getChildAreas().iterator();
		while (iter.hasNext()) {
			InlineArea child = (InlineArea) iter.next();
			if (child instanceof WordArea) {
				WordArea word = (WordArea) child;
				String s = word.getWord();
				int[] letterAdjust = word.getLetterAdjustArray();
				GlyphVector gv;
				java.awt.Font gfont = g2d.getFont();
				if (gfont.canDisplayUpTo(s) == -1)
				{
					gv = gfont.createGlyphVector(g2d.getFontRenderContext(), s);
				}
				// 如果该字体不能显示该内容，则用java默认字体显示
				else
				{
					gv = new java.awt.Font("Dialog", gfont
							.getStyle(), gfont.getSize()).createGlyphVector(g2d
							.getFontRenderContext(), s);
				}
				double additionalWidth = 0.0;
				if (letterAdjust == null
						&& text.getTextLetterSpaceAdjust() == 0
						&& text.getTextWordSpaceAdjust() == 0) {
					// nop
				} else {
					int[] offsets = getGlyphOffsets(s, font, text, letterAdjust);
					float cursor = 0.0f;
					for (int i = 0; i < offsets.length; i++) {
						Point2D pt = gv.getGlyphPosition(i);
						pt.setLocation(cursor, pt.getY());
						gv.setGlyphPosition(i, pt);
						cursor += offsets[i] / 1000f;
					}
					additionalWidth = cursor - gv.getLogicalBounds().getWidth();
				}
				g2d.drawGlyphVector(gv, textCursor, 0);
				textCursor += gv.getLogicalBounds().getWidth()
						+ additionalWidth;
			} else if (child instanceof SpaceArea) {
				SpaceArea space = (SpaceArea) child;
				String s = space.getSpace();
				char sp = s.charAt(0);
				int tws = (space.isAdjustable() ? text.getTextWordSpaceAdjust()
						+ 2 * text.getTextLetterSpaceAdjust() : 0);

				textCursor += (font.getCharWidth(sp) + tws) / 1000f;
			} else
				throw new IllegalStateException("Unsupported child element: "
						+ child);
		}
	}

	protected static int[] getGlyphOffsets(String s, Font font, TextArea text,
			int[] letterAdjust) {
		int textLen = s.length();
		int[] offsets = new int[textLen];
		for (int i = 0; i < textLen; i++) {
			final char c = s.charAt(i);
			final char mapped = font.mapChar(c);
			int wordSpace;

			if (CharUtilities.isAdjustableSpace(mapped)) {
				wordSpace = text.getTextWordSpaceAdjust();
			} else {
				wordSpace = 0;
			}
			int cw = font.getWidth(mapped);
			int ladj = (letterAdjust != null && i < textLen - 1 ? letterAdjust[i + 1]
					: 0);
			int tls = (i < textLen - 1 ? text.getTextLetterSpaceAdjust() : 0);
			offsets[i] = cw + ladj + tls + wordSpace;
		}
		return offsets;
	}

	/**
	 * Render leader area. This renders a leader area which is an area with a
	 * rule.
	 * 
	 * @param area
	 *            the leader area to render
	 */
	@Override
	public void renderLeader(Leader area) {
		renderInlineAreaBackAndBorders(area);

		// TODO leader-length: 25%, 50%, 75%, 100% not working yet
		// TODO Colors do not work on Leaders yet

		float startx = (currentIPPosition + area
				.getBorderAndPaddingWidthStart()) / 1000f;
		float starty = ((currentBPPosition + area.getOffset()) / 1000f);
		float endx = (currentIPPosition + area.getBorderAndPaddingWidthStart() + area
				.getIPD()) / 1000f;

		Color col = (Color) area.getTrait(Trait.COLOR);
		state.updateColor(col);

		Line2D line = new Line2D.Float();
		line.setLine(startx, starty, endx, starty);
		float ruleThickness = area.getRuleThickness() / 1000f;

		int style = area.getRuleStyle();
		switch (style) {
		case EN_SOLID:
		case EN_DASHED:
		case EN_DOUBLE:
			drawBorderLine(startx, starty, endx, starty + ruleThickness, true,
					true, style, col);
			break;
		case EN_DOTTED:
			// TODO Dots should be shifted to the left by ruleThickness / 2
			state.updateStroke(ruleThickness, style);
			float rt2 = ruleThickness / 2f;
			line.setLine(line.getX1(), line.getY1() + rt2, line.getX2(), line
					.getY2()
					+ rt2);
			state.getGraph().draw(line);
			break;
		case EN_GROOVE:
		case EN_RIDGE:
			float half = area.getRuleThickness() / 2000f;
			state.updateColor(lightenColor(col, 0.6f));
			moveTo(startx, starty);
			lineTo(endx, starty);
			lineTo(endx, starty + 2 * half);
			lineTo(startx, starty + 2 * half);
			closePath();
			state.getGraph().fill(currentPath);
			currentPath = null;
			state.updateColor(col);
			if (style == EN_GROOVE) {
				moveTo(startx, starty);
				lineTo(endx, starty);
				lineTo(endx, starty + half);
				lineTo(startx + half, starty + half);
				lineTo(startx, starty + 2 * half);
			} else {
				moveTo(endx, starty);
				lineTo(endx, starty + 2 * half);
				lineTo(startx, starty + 2 * half);
				lineTo(startx, starty + half);
				lineTo(endx - half, starty + half);
			}
			closePath();
			state.getGraph().fill(currentPath);
			currentPath = null;

		case EN_NONE:
			// No rule is drawn
			break;
		default:
		} // end switch
		super.renderLeader(area);
	}

	/**
	 * add by lzy 过滤图片 imageSrc 需要过滤的image
	 */
	@Override
	public java.awt.Image FilterImage(java.awt.Image imageSrc, int aphla)
	{
		java.awt.Image Filteredimage = null;

		Color BACKCOLOR = new Color(255, 255, 255, 0); // 背景色
		int FlgAphla = aphla; // 转换后前景色的透明度。

		FovImageFilter d = new FovImageFilter(BACKCOLOR, BACKCOLOR, FlgAphla);// 过滤器

		Filteredimage = Toolkit.getDefaultToolkit().createImage(
				new FilteredImageSource(imageSrc.getSource(), d));

		// 等待加栽图片完成
		MediaTracker mt = null;
		// "20090108遗留问题:com.wisii.fov.render.java2d.Java2DRenderer.FilterImage\\n"
		// +
		// "为去掉PreviewDialog，PrintDialog，PreviewDialogAPP先把下面隐去;
		JLabel l = new JLabel();
		mt = new MediaTracker(l);
		mt.addImage(Filteredimage, 1);
		try
		{
			mt.waitForID(1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return Filteredimage;

	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderImage(Image,
	 *      Rectangle2D)
	 */
	@Override
	public void renderImage(Image image, Rectangle2D pos) {
		// endTextObject();
		java.awt.Image drawImage = null;
		String fov_src_type = image.getSrc_type();
		int aphla = image.getAphla();
		if ("func-by-param".equals(fov_src_type))// 节点内容为符合《Imagexml规范》的XML字符串的变换形式
		{
			byte[] imagebyte = image.getImagebyte();

			if (imagebyte == null)
				return;
			try {
				drawImage = ImageIO.read(new ByteArrayInputStream(imagebyte));
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		} else if ("bin-data-str".equals(fov_src_type)) // src_type =
		// "bin-data-str"
		{
			// 扩展接口，节点内容为图片信息二进制值的某种封装形式的字符串
		} else// 原Fo规范
		{
			String url = image.getURL();
			drawImage = drawImage(url, pos);
		}

		if (drawImage == null)
			return;

		int x = currentIPPosition + (int) Math.round(pos.getX());
		int y = currentBPPosition + (int) Math.round(pos.getY());

		// 如果需要过滤
		if (aphla > 0&&aphla<255) {

			// 调用我们的图片绘制策略，过滤掉背景色/
			drawImage = FilterImage(drawImage, aphla);
		}
		drawImage(image, drawImage, new Rectangle2D.Double(x, y, pos.getWidth(), pos.getHeight()));
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#drawImage(java.lang.String,
	 *      java.awt.geom.Rectangle2D, java.util.Map)
	 */
	@Override
	protected java.awt.Image drawImage(String url, Rectangle2D pos,
			Map foreignAttributes) {
		java.awt.Image awtImage = null;

		// System.out.println("1218url : " + url); //R1.bmp
		url = ImageFactory.getURL(url);
		// System.out.println("1220url: " + url);//R1.bmp

		ImageFactory fact = userAgent.getImageFactory();
		FovImage fovimage = fact.getImage(url, userAgent);
		// System.out.println("fovimage : " + fovimage);
		if (fovimage == null)
			return null;
		if (!fovimage.load(FovImage.DIMENSIONS))
			return null;

		int w = fovimage.getWidth();
		int h = fovimage.getHeight();
		String mime = fovimage.getMimeType();
		if ("text/xml".equals(mime)) {
			if (!fovimage.load(FovImage.ORIGINAL_DATA))
				return null;
			Document doc = ((XMLImage) fovimage).getDocument();
			String ns = ((XMLImage) fovimage).getNameSpace();
			renderDocument(doc, ns, pos, foreignAttributes);
		} else if ("image/svg+xml".equals(mime)) {
			if (!fovimage.load(FovImage.ORIGINAL_DATA))
				return null;
			Document doc = ((XMLImage) fovimage).getDocument();
			String ns = ((XMLImage) fovimage).getNameSpace();
			renderDocument(doc, ns, pos, foreignAttributes);
		} else if ("image/eps".equals(mime)) {
			log.warn("EPS images are not supported by this renderer");
		} else {
			if (!fovimage.load(FovImage.BITMAP)) {
				log.warn("Loading of bitmap failed: " + url);
				return null;
			}

			byte[] raw = fovimage.getBitmaps();

			// TODO Hardcoded color and sample models, FIX ME!
			ColorModel cm = new ComponentColorModel(ColorSpace
					.getInstance(ColorSpace.CS_LINEAR_RGB),
					new int[] { 8, 8, 8 }, false, false, ColorModel.OPAQUE,
					DataBuffer.TYPE_BYTE);
			SampleModel sampleModel = new PixelInterleavedSampleModel(
					DataBuffer.TYPE_BYTE, w, h, 3, w * 3, new int[] { 0, 1, 2 });
			DataBuffer dbuf = new DataBufferByte(raw, w * h * 3);

			WritableRaster raster = Raster.createWritableRaster(sampleModel,
					dbuf, null);

			// Combine the color model and raster into a buffered image
			awtImage = new BufferedImage(cm, raster, false, null);
		}
		return awtImage;
	}

	@Override
	protected void drawImage(Area area, java.awt.Image image, Rectangle2D pos) {
		if(!isAvailabilityLayer(area, userAgent.getCheckLayers()))// && userAgent.isSelected()
			return;
		state.getGraph().drawImage(image, (int) (pos.getX() / 1000f),
					(int) (pos.getY() / 1000f), (int) (pos.getWidth() / 1000f),
					(int) (pos.getHeight() / 1000f), null);
	}
	
	/**
	 * @see com.wisii.fov.render.PrintRenderer#createRendererContext(int, int,
	 *      int, int, java.util.Map)
	 */
	@Override
	protected RendererContext createRendererContext(int x, int y, int width,
			int height, Map foreignAttributes) {
		RendererContext context = super.createRendererContext(x, y, width,
				height, foreignAttributes);
		context.setProperty(Java2DRendererContextConstants.JAVA2D_STATE, state);
		return context;
	}

	// 为每一种打印方向设置相应的打印区域。
	public Paper setStatePaper(double width, double height, Paper paper) {
		// if(width > height)
		{
			if (getOrientation() == 0) {
				paper.setImageableArea(0, 0, height / 1000d, width / 1000d);
				paper.setSize(height / 1000d, width / 1000d);
			} else if (getOrientation() == 1) {
				paper.setImageableArea(0, 0, width / 1000d, height / 1000d);
				paper.setSize(width / 1000d, height / 1000d);
			} else if (getOrientation() == 2) {
				paper.setImageableArea(0, 0, height / 1000d, width / 1000d);
				paper.setSize(height / 1000d, width / 1000d);
			}
		}
		// else
		{

		}

		return paper;
	}

	/** 设置打印纸的高度 */
	public double setPagerHeight(double h) {
		double height = h;
		if (isSelectedHeightCheckBox) {
			height = (int) (height * scaleY);
		}

		height = (int) (height + heightAddABS * 1000);

		return height;
	}

	private int Orientation = 1;

	public void setOrientation(int o) {
		Orientation = o;
	}

	public int getOrientation() {
		return Orientation;
	}

	/**
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 *      java.awt.print.PageFormat, int)
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex >= getNumberOfPages())
			return NO_SUCH_PAGE;

		if (state != null)
			throw new IllegalStateException("状态必须为空");
		Graphics2D graphics = (Graphics2D) g;
		try {
			PageViewport viewport = getPageViewport(pageIndex);
			AffineTransform at = graphics.getTransform();
			at.scale(scaleX, scaleY);
			at.translate(excursionX, excursionY);
			state = new Java2DGraphicsState(graphics, this.fontInfo, at);

			// reset the current Positions
			currentBPPosition = 0;
			currentIPPosition = 0;
			renderPageAreas(viewport.getPage());
			// 客户端当前时间
			Object all = Sutil.getF("yuyu");
			if (all == null) {
				drawTest((Graphics2D) graphics.create(), viewport
						.getTextGlyphVector(false), (int)pageFormat.getWidth(),(int)pageFormat.getHeight(), scaleX,
						scaleY);
			} else {
				long ad = (Long) all;
				long ed = System.currentTimeMillis();// 当前时间
				if (Sutil.gc() > ad || ed > ad) {// 绘制
					drawTest((Graphics2D) graphics.create(), viewport
							.getTextGlyphVector(true), (int)pageFormat.getWidth(),(int)pageFormat.getHeight(),
							scaleX, scaleY);
				}
			}

			return PAGE_EXISTS;
		} catch (FOVException e) {
			e.printStackTrace();
			log.error(e);
			return NO_SUCH_PAGE;
		} finally {
			state = null;
		}
	}

	/** @see com.wisii.fov.render.AbstractPathOrientedRenderer#beginTextObject() */
	@Override
	protected void beginTextObject() {
		// not necessary in Java2D
	}

	/** @see com.wisii.fov.render.AbstractPathOrientedRenderer#endTextObject() */
	@Override
	protected void endTextObject() {
		// not necessary in Java2D
	}

	// add by huangzl
	/**
	 * 设置是否显示锯齿效果.
	 * 
	 * @param antParamer.true:锯齿效果;false:普通显示
	 */
	public void setAntialias(boolean antParam) {
		antialiasing = antParam;
	}

	/***************************************************************************
	 * 设置打印偏移量和缩放比例
	 * 
	 * @param excursionX
	 *            页面纵向的偏移量。正数时，向上偏移；负数时，向下偏移
	 * @param excursionY
	 *            页面横向的偏移量。正数时，向左偏移；负数时，向右偏移
	 * @param scaleX
	 *            页面横向的缩放比例
	 * @param scaleY
	 *            页面纵向的的缩放比例
	 */
	public void setPrintProperties(float excursionX, float excursionY,
			float scaleX, float scaleY) {

		this.excursionX = excursionX;
		this.excursionY = excursionY;
		this.scaleX = (float) (scaleX / 100d);
		this.scaleY = (float) (scaleY / 100d);

	}

	public void setPrintProperties(float excursionX, float excursionY,
			float scaleX, float scaleY, boolean ch, float h) {
		this.excursionX = excursionX;
		this.excursionY = excursionY;
		this.scaleX = (float) (scaleX / 100d);
		this.scaleY = (float) (scaleY / 100d);

		/** 页高是否随比例变化 */
		isSelectedHeightCheckBox = ch;
		/** 页高增加的绝对高度 */
		heightAddABS = h;
	}

	/**
	 * @return the dimensions of the specified page
	 * @param pageNum
	 *            the page number
	 * @exception FOVException
	 *                If the page is out of range or has not been rendered.
	 */
	public Dimension getPageImageSize(int pageNum) throws FOVException {
		// 在子类中实现具体操作
		return null;
	}

	// add end
	
	/**
     * Handle block traits. The block could be any sort of block with any positioning so this should render the traits
     * such as border and background in its position.
     * @param block the block to render the traits
     */
    @Override
	protected void handleBlockTraits(Block block)
    {
        int borderPaddingStart = block.getBorderAndPaddingWidthStart();
        int borderPaddingBefore = block.getBorderAndPaddingWidthBefore();

        float startx = currentIPPosition / 1000f;
        float starty = currentBPPosition / 1000f;
        float width = block.getIPD() / 1000f;
        float height = block.getBPD() / 1000f;
        
        startx += block.getStartIndent() / 1000f;
        float x = startx;
        startx -= block.getBorderAndPaddingWidthStart() / 1000f;

        width += borderPaddingStart / 1000f;
        width += block.getBorderAndPaddingWidthEnd() / 1000f;
        height += borderPaddingBefore / 1000f;
        height += block.getBorderAndPaddingWidthAfter() / 1000f;
        float y = starty + .5F/*currentBPPosition*/;
        float w = width - .5F;
        float h = height - .5F;
        Rectangle2D viewport = new Rectangle2D.Double(x, y, w, h);
        block.setViewport(viewport);
        /* 【添加：END】 by 李晓光  2009-6-9 */
        drawBackAndBorders(block, startx, starty, width, height);
    }
    public Java2DGraphicsState getState(){
    	return this.state;
    }
    @Override
    protected int getTotalPage()
    {
    	return numberOfPages;
    }
}
