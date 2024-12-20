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
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.wisii.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: PCLRenderer.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.pcl;

//Java
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.print.DocFlavor;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.w3c.dom.Document;

import com.wisii.component.setting.PrintRef;
import com.wisii.component.setting.WisiiBean;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.Block;
import com.wisii.fov.area.BlockViewport;
import com.wisii.fov.area.CTM;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.AbstractTextArea;
import com.wisii.fov.area.inline.ForeignObject;
import com.wisii.fov.area.inline.Image;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.SpaceArea;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.area.inline.Viewport;
import com.wisii.fov.area.inline.WordArea;
import com.wisii.fov.fo.extensions.ExtensionElementMapping;
import com.wisii.fov.fonts.Font;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.image.EPSImage;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.ImageFactory;
import com.wisii.fov.image.XMLImage;
import com.wisii.fov.render.Graphics2DAdapter;
import com.wisii.fov.render.Graphics2DImagePainter;
import com.wisii.fov.render.PrintRenderer;
import com.wisii.fov.render.RendererContext;
import com.wisii.fov.render.RendererContextConstants;
import com.wisii.fov.render.java2d.FontMetricsMapper;
import com.wisii.fov.render.java2d.FontSetup;
import com.wisii.fov.render.java2d.Java2DRenderer;
import com.wisii.fov.render.pcl.extensions.PCLElementMapping;
import com.wisii.fov.traits.BorderProps;
import com.wisii.fov.util.PrintUtil;
import com.wisii.fov.util.QName;
import com.wisii.fov.util.Sutil;
import com.wisii.fov.util.UnitConv;

/**
 * Renderer for the PCL 5 printer language. It also uses HP GL/2 for certain graphic elements.
 */
public class PCLRenderer extends PrintRenderer
{

	/** logging instance */
	private static Log log = LogFactory.getLog(PCLRenderer.class);

	/** The MIME type for PCL */
	public static final String MIME_TYPE = MimeConstants.MIME_PCL_ALT;

	private static final QName CONV_MODE = new QName(
			ExtensionElementMapping.URI, null, "conversion-mode");
	private static final QName SRC_TRANSPARENCY = new QName(
			ExtensionElementMapping.URI, null, "source-transparency");

	/** The OutputStream to write the PCL stream to */
	protected OutputStream out;

	/** The PCL generator */
	protected PCLGenerator gen;
	private boolean ioTrouble = false;

	private Stack graphicContextStack = new Stack();
	private GraphicContext graphicContext = new GraphicContext();

	private PCLPageDefinition currentPageDefinition;
	private int currentPrintDirection = 0;
	private GeneralPath currentPath = null;
	private java.awt.Color currentFillColor = null;

	/**
	 * Controls whether appearance is more important than speed. False can cause
	 * some FO feature
	 * to be ignored (like the advanced borders).
	 */
	private boolean qualityBeforeSpeed = false;

	/**
	 * Controls whether all text should be painted as text. This is a fallback
	 * setting in case
	 * the mixture of native and bitmapped text does not provide the necessary
	 * quality.
	 */
	private boolean allTextAsBitmaps = false;

	// /**
	// * Create the PCL renderer
	// */
	// public PCLRenderer()
	// {
	// System.out.println("Enter PCL Render");
	// }
	//
	// /**
	// *
	// * @param printerName String 指定打印机名称
	// * @param table List 指定转换表 mediaUsage中设置的可能值的转换表
	// */
	// public PCLRenderer(String printerName, List table)
	// {
	// this.printerName = printerName;
	// this.table = table;
	// }

	/**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
	 */
	public void configure(Configuration cfg) throws ConfigurationException
	{
		super.configure(cfg);
		String rendering = cfg.getChild("rendering").getValue(null);
		if ("quality".equalsIgnoreCase(rendering))
		{
			this.qualityBeforeSpeed = true;
		} else if ("speed".equalsIgnoreCase(rendering))
		{
			this.qualityBeforeSpeed = false;
		} else if (rendering != null)
		{
			throw new ConfigurationException(
					"Valid values for 'rendering' are 'quality' and 'speed'. Value found: "
							+ rendering);
		}
		String textRendering = cfg.getChild("text-rendering").getValue(null);
		if ("bitmap".equalsIgnoreCase(textRendering))
		{
			this.allTextAsBitmaps = true;
		} else if ("auto".equalsIgnoreCase(textRendering))
		{
			this.allTextAsBitmaps = false;
		} else if (textRendering != null)
		{
			throw new ConfigurationException(
					"Valid values for 'text-rendering' are 'auto' and 'bitmap'. Value found: "
							+ textRendering);
		}
	}

	/**
	 * @see com.wisii.fov.render.Renderer#setupFontInfo(com.wisii.fov.fonts.FontInfo)
	 */
	public void setupFontInfo(FontInfo inFontInfo)
	{
		// Don't call super.setupFontInfo() here!
		// The PCLRenderer uses the Java2D FontSetup which needs a special font
		// setup
		// create a temp Image to test font metrics on
		fontInfo = inFontInfo;
		BufferedImage fontImage = new BufferedImage(10, 10,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = fontImage.createGraphics();
		// The next line is important to get accurate font metrics!
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		FontSetup.setup(fontInfo, g);
	}

	/**
	 * Central exception handler for I/O exceptions.
	 * 
	 * @param ioe
	 *            IOException to handle
	 */
	protected void handleIOTrouble(IOException ioe)
	{
		if (!ioTrouble)
		{
			log.error("Error while writing to target file", ioe);
			ioTrouble = true;
		}
	}

	/** @see com.wisii.fov.render.Renderer#getGraphics2DAdapter() */
	public Graphics2DAdapter getGraphics2DAdapter()
	{
		return new PCLGraphics2DAdapter();
	}

	/**
	 * @return the GraphicContext used to track coordinate system transformations
	 */
	public GraphicContext getGraphicContext()
	{
		return this.graphicContext;
	}

	public String getPrinterName()
	{
		if (printref != null)
		{
			return printref.getPrinter();
		}
		return null;
	}

	/** @return the target resolution */
	protected int getResolution()
	{
		int resolution = (int) Math.round(userAgent.getTargetResolution());
		if (resolution <= 300)
		{
			return 300;
		} else
		{
			return 600;
		}
	}

	/**
	 * Sets the current font (NOTE: Hard-coded font mappings ATM!)
	 * 
	 * @param name
	 *            the font name (internal F* names for now)
	 * @param size
	 *            the font size
	 * @param text
	 *            the text to be rendered (used to determine if there are
	 *            non-printable chars)
	 * @return true if the font can be mapped to PCL
	 * @throws IOException
	 *             if an I/O problem occurs
	 */
	public boolean setFont(String name, float size, String text)
			throws IOException
	{
		byte[] encoded = text.getBytes("ISO-8859-1");
		for (int i = 0, c = encoded.length; i < c; i++)
		{
			if (encoded[i] == 0x3F && text.charAt(i) != '?')
			{
				return false;
			}
		}
		// System.out.println("size = " + size);
		// System.out.println("text = " + text);
		int fontcode = 0;
		if (name.length() > 1 && name.charAt(0) == 'F')
		{
			try
			{
				// System.out.println("Name = " + name);
				fontcode = Integer.parseInt(name.substring(1));
				// System.out.println("fontcode = " + fontcode);
			} catch (Exception e)
			{
				log.error(e);
			}
		}
		// Note "(ON" selects ISO 8859-1 symbol set as used by PCLGenerator
		String formattedSize = gen.formatDouble2(size / 1000);
		switch (fontcode)
		{
		case 1: // F1 = Helvetica

			// gen.writeCommand("(8U");
			// gen.writeCommand("(s1p" + formattedSize + "v0s0b24580T");
			// Arial is more common among PCL5 printers than Helvetica - so use
			// Arial

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v0s0b16602T");
			break;
		case 2: // F2 = Helvetica Oblique

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v1s0b16602T");
			break;
		case 3: // F3 = Helvetica Bold

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v0s3b16602T");
			break;
		case 4: // F4 = Helvetica Bold Oblique

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v1s3b16602T");
			break;
		case 5: // F5 = Times Roman

			// gen.writeCommand("(8U");
			// gen.writeCommand("(s1p" + formattedSize + "v0s0b25093T");
			// Times New is more common among PCL5 printers than Times - so use
			// Times New

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v0s0b16901T");
			break;
		case 6: // F6 = Times Italic

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v1s0b16901T");
			break;
		case 7: // F7 = Times Bold

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v0s3b16901T");
			break;
		case 8: // F8 = Times Bold Italic

			gen.writeCommand("(0N");
			gen.writeCommand("(s1p" + formattedSize + "v1s3b16901T");
			break;
		case 9: // F9 = Courier

			gen.writeCommand("(0N");
			gen.writeCommand("(s0p"
					+ gen.formatDouble2(120.01f / (size / 1000.00f))
					+ "h0s0b4099T");
			break;
		case 10: // F10 = Courier Oblique

			gen.writeCommand("(0N");
			gen.writeCommand("(s0p"
					+ gen.formatDouble2(120.01f / (size / 1000.00f))
					+ "h1s0b4099T");
			break;
		case 11: // F11 = Courier Bold

			gen.writeCommand("(0N");
			gen.writeCommand("(s0p"
					+ gen.formatDouble2(120.01f / (size / 1000.00f))
					+ "h0s3b4099T");
			break;
		case 12: // F12 = Courier Bold Oblique

			gen.writeCommand("(0N");
			gen.writeCommand("(s0p"
					+ gen.formatDouble2(120.01f / (size / 1000.00f))
					+ "h1s3b4099T");
			break;
		case 13: // F13 = Symbol

			return false;
			// gen.writeCommand("(19M");
			// gen.writeCommand("(s1p" + formattedSize + "v0s0b16686T");
			// ECMA Latin 1 Symbol Set in Times Roman???
			// gen.writeCommand("(9U");
			// gen.writeCommand("(s1p" + formattedSize + "v0s0b25093T");
			// break;
		case 14: // F14 = Zapf Dingbats

			return false;
			// gen.writeCommand("(14L");
			// gen.writeCommand("(s1p" + formattedSize + "v0s0b45101T");
			// break;
		default:

			// gen.writeCommand("(0N");
			// gen.writeCommand("(s" + formattedSize + "V");
			// gen.writeCommand("(0N");
			// gen.writeCommand("(s0p" + gen.formatDouble2(120.01f / (size /
			// 1000.00f))
			// + "h0s3b4099T");
			gen.writeCommand("(0N");
			gen.writeCommand("(s" + formattedSize + "V");

			return false; // mod by lizhenyou
		}
		return true;
	}

	/** @see com.wisii.fov.render.Renderer#startRenderer(java.io.OutputStream) */
	public void startRenderer(OutputStream outputStream) throws IOException
	{
		log.debug("Rendering areas to PCL...");
		if (outputStream == null)
		{
			out = new ByteArrayOutputStream();
		} else
		{
			out = outputStream;
		}
		this.gen = new PCLGenerator(out, getResolution());

		gen.universalEndOfLanguage();
		gen.writeText("@PJL COMMENT Produced by " + userAgent.getProducer()
				+ "\n");
		if (userAgent.getWisiibean().getPrintSetting().getJobName() != null)
		{
			gen.writeText("@PJL JOB NAME = \""
					+ userAgent.getWisiibean().getPrintSetting().getJobName()
					+ "\"\n");
		}
		gen.writeText("@PJL SET RESOLUTION = " + getResolution() + "\n");
		gen.writeText("@PJL ENTER LANGUAGE = PCL\n");
		gen.resetPrinter();
		gen.setUnitOfMeasure(getResolution());
		gen.setRasterGraphicsResolution(getResolution());
	}

	/** @see com.wisii.fov.render.Renderer#stopRenderer() */
	public void stopRenderer() throws IOException
	{
		gen.separateJobs();
		gen.resetPrinter();
		gen.universalEndOfLanguage();
		out.flush();
		out.close();
		printPCL(userAgent.getWisiibean());
	}

	public void printPCL(WisiiBean wb)
	{
		String pname = getPrinterName();
		if (pname != null && !pname.trim().equals(""))
		{
			PrintUtil.print(pname, new ByteArrayInputStream(
					((ByteArrayOutputStream) out).toByteArray()),
					DocFlavor.INPUT_STREAM.PCL, wb.getPrintSetting()
							.getJobName());
		}
	}

	/** @see com.wisii.fov.render.AbstractRenderer */
	public String getMimeType()
	{
		return MIME_TYPE;
	}

	private static int i = 0;

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderPage(com.wisii.fov.area.PageViewport)
	 */
	public void renderPage(PageViewport page) throws IOException, FOVException
	{
		saveGraphicsState();
		// Paper source
		String paperSource = page.getForeignAttributeValue(new QName(
				PCLElementMapping.NAMESPACE, null, "paper-source"));

		paperSource = getPaperEntry(page.getMediaUsage());
		// System.out.println("paperSource------这个是什么？"+paperSource);
		// paperSource="5";
		// System.out.println("paper================ Source = " + paperSource);
		if (paperSource != null && !paperSource.isEmpty())
		{
			String[] cmds = paperSource.split(";");
			// if(paperSource.)
			try
			{
				int ps = Integer.parseInt(cmds[0]);
				gen.selectPaperSource(ps);
			} catch (Exception e)
			{

			}
			if (cmds.length > 1)
			{
				try
				{
					int dp = Integer.parseInt(cmds[1]);
					gen.selectDoublePrint(dp);
				} catch (Exception e)
				{

				}
			}
			if (cmds.length > 2)
			{
				gen.selectcmd(cmds[2]);
			}
		}

		// Page size
		final long pagewidth = Math.round(page.getViewArea().getWidth());
		final long pageheight = Math.round(page.getViewArea().getHeight());
		selectPageFormat(pagewidth, pageheight);

		super.renderPage(page);
		Object all = Sutil.getF("yuyu");
		if (all == null)
		{
			int width = (int) pagewidth;
			int height = (int) pageheight;
			drawTest(page.getTextGlyphVector(false), width, height);
		} else
		{
			long ad = (Long) all;
			long ed = System.currentTimeMillis();// 当前时间
			if (Sutil.gc() > ad || ed > ad)
			{// 绘制
				int width = (int) pagewidth;
				int height = (int) pageheight;
				drawTest(page.getTextGlyphVector(false), width, height);
			}
		}
		// Eject page
		gen.formFeed();
		restoreGraphicsState();

	}

	private void drawTest(Shape shape[], int width, int height)
	{
		Color c =  new Color(128,128,128,100);
		draw(shape, width, height, c);
	}

	// 绘制
	private void draw(final Shape shape[], final int width, final int height,
			final Color c)
	{
		// 测试用户 在1/4高度处
		try
		{
			final int w = shape[1].getBounds().width*1000;
			final int h = shape[1].getBounds().height*1000;
			final int x = (width - w) / 2;
			final int y = (height  - h) / 2;
			Graphics2DImagePainter painter = new Graphics2DImagePainter()
			{

				public void paint(Graphics2D g2d, Rectangle2D area)
				{
					g2d.translate(0, h);
					g2d.scale(1000, 1000);
					g2d.draw(shape[0]);
					

				}

				public Dimension getImageSize()
				{
					return new Dimension(w, h);
				}
			};
			Graphics2DAdapter g2a = getGraphics2DAdapter();
			RendererContext rc = createRendererContext(x, y, w, h, null);
			Map atts = new java.util.HashMap();
			atts.put(CONV_MODE, "bitmap"); // bitmap JPEG
			atts.put(SRC_TRANSPARENCY, "true"); // true
			rc.setProperty(RendererContextConstants.FOREIGN_ATTRIBUTES,
					atts);
			g2a.paintImage(painter, rc, x, y, w, h);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void selectPageFormat(long pagewidth, long pageheight)
			throws IOException
	{
		this.currentPageDefinition = PCLPageDefinition.getPageDefinition(
				pagewidth, pageheight, 1000);

		if (this.currentPageDefinition == null)
		{
			this.currentPageDefinition = PCLPageDefinition
					.getDefaultPageDefinition();
			log.warn("Paper type could not be determined. Falling back to: "
					+ this.currentPageDefinition.getName());
		}
		log.debug("page size: " + currentPageDefinition.getPhysicalPageSize());
		log
				.debug("logical page: "
						+ currentPageDefinition.getLogicalPageRect());
		if (this.currentPageDefinition.isLandscapeFormat())
		{
			gen.writeCommand("&l1O"); // Orientation
		} else
		{
			gen.writeCommand("&l0O"); // Orientation
		}
		gen.selectPageSize(this.currentPageDefinition.getSelector());
		gen.clearHorizontalMargins();
		gen.setTopMargin(0);
	}

	/** Saves the current graphics state on the stack. */
	protected void saveGraphicsState()
	{
		graphicContextStack.push(graphicContext);
		graphicContext = (GraphicContext) graphicContext.clone();
	}

	/** Restores the last graphics state from the stack. */
	protected void restoreGraphicsState()
	{
		graphicContext = (GraphicContext) graphicContextStack.pop();
	}

	/**
	 * Clip an area. write a clipping operation given coordinates in the current
	 * transform. Coordinates are in points.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param width
	 *            the width of the area
	 * @param height
	 *            the height of the area
	 */
	protected void clipRect(float x, float y, float width, float height)
	{
		// PCL cannot clip (only HP GL/2 can)
	}

	private Point2D transformedPoint(float x, float y)
	{
		return transformedPoint(Math.round(x), Math.round(y));
	}

	private Point2D transformedPoint(int x, int y)
	{
		AffineTransform at = graphicContext.getTransform();
		if (log.isTraceEnabled())
		{
			log.trace("Current transform: " + at);
		}
		Point2D.Float orgPoint = new Point2D.Float(x, y);
		Point2D.Float transPoint = new Point2D.Float();
		at.transform(orgPoint, transPoint);
		// At this point we have the absolute position in FOV's coordinate
		// system

		// Now get PCL coordinates taking the current print direction and the
		// logical page
		// into account.
		Dimension pageSize = currentPageDefinition.getPhysicalPageSize();
		Rectangle logRect = currentPageDefinition.getLogicalPageRect();
		switch (currentPrintDirection)
		{
		case 0:
			transPoint.x -= logRect.x;
			transPoint.y -= logRect.y;
			break;
		case 90:
			float ty = transPoint.x;
			transPoint.x = pageSize.height - transPoint.y;
			transPoint.y = ty;
			transPoint.x -= logRect.y;
			transPoint.y -= logRect.x;
			break;
		case 180:
			transPoint.x = pageSize.width - transPoint.x;
			transPoint.y = pageSize.height - transPoint.y;
			transPoint.x -= pageSize.width - logRect.x - logRect.width;
			transPoint.y -= pageSize.height - logRect.y - logRect.height;

			// The next line is odd and is probably necessary due to the default
			// value of the
			// Text Length command: "1/2 inch less than maximum text length"
			// I wonder why this isn't necessary for the 90 degree rotation.
			// *shrug*
			transPoint.y -= UnitConv.in2mpt(0.5);
			break;
		case 270:
			float tx = transPoint.y;
			transPoint.y = pageSize.width - transPoint.x;
			transPoint.x = tx;
			transPoint.x -= pageSize.height - logRect.y - logRect.height;
			transPoint.y -= pageSize.width - logRect.x - logRect.width;
			break;
		default:
			throw new IllegalStateException("Illegal print direction: "
					+ currentPrintDirection);
		}
		return transPoint;
	}

	private void changePrintDirection()
	{
		AffineTransform at = graphicContext.getTransform();
		int newDir;
		try
		{
			if (at.getScaleX() == 0 && at.getScaleY() == 0
					&& at.getShearX() == 1 && at.getShearY() == -1)
			{
				newDir = 90;
			} else if (at.getScaleX() == -1 && at.getScaleY() == -1
					&& at.getShearX() == 0 && at.getShearY() == 0)
			{
				newDir = 180;
			} else if (at.getScaleX() == 0 && at.getScaleY() == 0
					&& at.getShearX() == -1 && at.getShearY() == 1)
			{
				newDir = 270;
			} else
			{
				newDir = 0;
			}
			if (newDir != this.currentPrintDirection)
			{
				this.currentPrintDirection = newDir;
				gen.changePrintDirection(this.currentPrintDirection);
			}
		} catch (IOException ioe)
		{
			handleIOTrouble(ioe);
		}
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#startVParea(CTM, Rectangle2D)
	 */
	protected void startVParea(CTM ctm, Rectangle2D clippingRect)
	{
		saveGraphicsState();
		AffineTransform at = new AffineTransform(ctm.toArray());
		graphicContext.transform(at);
		changePrintDirection();
		if (log.isDebugEnabled())
		{
			log.debug("startVPArea: " + at + " --> "
					+ graphicContext.getTransform());
		}
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#endVParea()
	 */
	protected void endVParea()
	{
		restoreGraphicsState();
		changePrintDirection();
		if (log.isDebugEnabled())
		{
			log.debug("endVPArea() --> " + graphicContext.getTransform());
		}
	}

	/**
	 * Handle block traits.
	 * The block could be any sort of block with any positioning
	 * so this should render the traits such as border and background
	 * in its position.
	 * 
	 * @param block
	 *            the block to render the traits
	 */
	protected void handleBlockTraits(Block block)
	{
		int borderPaddingStart = block.getBorderAndPaddingWidthStart();
		int borderPaddingBefore = block.getBorderAndPaddingWidthBefore();

		float startx = currentIPPosition / 1000f;
		float starty = currentBPPosition / 1000f;
		float width = block.getIPD() / 1000f;
		float height = block.getBPD() / 1000f;

		startx += block.getStartIndent() / 1000f;
		startx -= block.getBorderAndPaddingWidthStart() / 1000f;

		width += borderPaddingStart / 1000f;
		width += block.getBorderAndPaddingWidthEnd() / 1000f;
		height += borderPaddingBefore / 1000f;
		height += block.getBorderAndPaddingWidthAfter() / 1000f;

		drawBackAndBorders(block, startx, starty, width, height);
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderText(TextArea)
	 */
	protected void renderText(final TextArea text)
	{
		renderInlineAreaBackAndBorders(text);

		String fontname = getInternalFontNameForArea(text);
		int fontsize = text.getTraitAsInteger(Trait.FONT_SIZE);

		// Determine position
		int saveIP = currentIPPosition;
		int rx = currentIPPosition + text.getBorderAndPaddingWidthStart();
		int bl = currentBPPosition + text.getOffset()
				+ text.getBaselineOffset();

		try
		{
			final Color col = (Color) text.getTrait(Trait.COLOR);
			boolean pclFont = allTextAsBitmaps ? false : setFont(fontname,
					fontsize, text.getText());

			if (pclFont)
			{
				// this.currentFill = col;
				if (col != null)
				{
					// useColor(ct);
					gen.setTransparencyMode(true, false);
					gen.selectGrayscale(col);
				}

				saveGraphicsState();
				graphicContext.translate(rx, bl);
				setCursorPos(0, 0);
				gen.setTransparencyMode(true, true);

				super.renderText(text); // Updates IPD and renders words and
										// spaces
				restoreGraphicsState();
			} else
			{
				// Use Java2D to paint different fonts via bitmap
				final Font font = getFontFromArea(text);
				final int baseline = text.getBaselineOffset();

				// for cursive fonts, so the text isn't clipped
				int extraWidth = font.getFontSize() / 3;
				final FontMetricsMapper mapper = (FontMetricsMapper) fontInfo
						.getMetricsFor(font.getFontName());
				int maxAscent = mapper.getMaxAscent(font.getFontSize()) / 1000;
				final int additionalBPD = maxAscent - baseline;

				Graphics2DAdapter g2a = getGraphics2DAdapter();
				final Rectangle paintRect = new Rectangle(rx, currentBPPosition
						+ text.getOffset() - additionalBPD, text.getIPD()
						+ extraWidth, text.getBPD() + additionalBPD);
				RendererContext rc = createRendererContext(paintRect.x,
						paintRect.y, paintRect.width, paintRect.height, null);
				Map atts = new java.util.HashMap();
				atts.put(CONV_MODE, "bitmap"); // bitmap JPEG
				atts.put(SRC_TRANSPARENCY, "true"); // true
				rc.setProperty(RendererContextConstants.FOREIGN_ATTRIBUTES,
						atts);

				Graphics2DImagePainter painter = new Graphics2DImagePainter()
				{

					public void paint(Graphics2D g2d, Rectangle2D area)
					{
						g2d.setFont(mapper.getFont(font.getFontSize()));
						g2d.translate(0, baseline + additionalBPD);
						g2d.scale(1000, 1000);
						g2d.setColor(col);
						Java2DRenderer.renderText(text, g2d, font);
					}

					public Dimension getImageSize()
					{
						return paintRect.getSize();
					}

				};
				g2a.paintImage(painter, rc, paintRect.x, paintRect.y,
						paintRect.width, paintRect.height);
				currentIPPosition = saveIP + text.getAllocIPD();
			}

			// renderTextDecoration(tf, fontsize, area, bl, rx);
		} catch (IOException ioe)
		{
			handleIOTrouble(ioe);
		}
	}

	/**
	 * Sets the current cursor position. The coordinates are transformed to the
	 * absolute position
	 * on the logical PCL page and then passed on to the PCLGenerator.
	 * 
	 * @param x
	 *            the x coordinate (in millipoints)
	 * @param y
	 *            the y coordinate (in millipoints)
	 */
	void setCursorPos(float x, float y)
	{
		try
		{
			Point2D transPoint = transformedPoint(x, y);
			gen.setCursorPos(transPoint.getX(), transPoint.getY());
		} catch (IOException ioe)
		{
			handleIOTrouble(ioe);
		}
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#clip()
	 */
	protected void clip()
	{
		if (currentPath == null)
		{
			throw new IllegalStateException("No current path available!");
		}
		// TODO Find a good way to do clipping. PCL itself cannot clip.
		currentPath = null;
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#closePath()
	 */
	protected void closePath()
	{
		currentPath.closePath();
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#lineTo(float,
	 *      float)
	 */
	protected void lineTo(float x, float y)
	{
		if (currentPath == null)
		{
			currentPath = new GeneralPath();
		}
		currentPath.lineTo(x, y);
	}

	/**
	 * @see com.wisii.fov.render.AbstractPathOrientedRenderer#moveTo(float,
	 *      float)
	 */
	protected void moveTo(float x, float y)
	{
		if (currentPath == null)
		{
			currentPath = new GeneralPath();
		}
		currentPath.moveTo(x, y);
	}

	/**
	 * Fill a rectangular area.
	 * 
	 * @param x
	 *            the x coordinate (in pt)
	 * @param y
	 *            the y coordinate (in pt)
	 * @param width
	 *            the width of the rectangle
	 * @param height
	 *            the height of the rectangle
	 */
	protected void fillRect(float x, float y, float width, float height)
	{
		try
		{
			setCursorPos(x * 1000, y * 1000);
			gen.fillRect((int) (width * 1000), (int) (height * 1000),
					this.currentFillColor);
		} catch (IOException ioe)
		{
			handleIOTrouble(ioe);
		}
	}

	/**
	 * Sets the new current fill color.
	 * 
	 * @param color
	 *            the color
	 */
	protected void updateFillColor(java.awt.Color color)
	{
		this.currentFillColor = color;
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderWord(com.wisii.fov.area.inline.WordArea)
	 */
	protected void renderWord(WordArea word)
	{
		// Font font = getFontFromArea(word.getParentArea());

		String s = word.getWord();

		try
		{
			gen.writeText(s);
		} catch (IOException ioe)
		{
			handleIOTrouble(ioe);
		}

		super.renderWord(word);
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderSpace(com.wisii.fov.area.inline.SpaceArea)
	 */
	protected void renderSpace(SpaceArea space)
	{
		AbstractTextArea textArea = (AbstractTextArea) space.getParentArea();
		String s = space.getSpace();
		char sp = s.charAt(0);
		Font font = getFontFromArea(textArea);

		int tws = (space.isAdjustable() ? textArea.getTextWordSpaceAdjust() + 2
				* textArea.getTextLetterSpaceAdjust() : 0);

		double dx = (font.getCharWidth(sp) + tws) / 100f;
		try
		{
			gen.writeCommand("&a+" + gen.formatDouble2(dx) + "H");
		} catch (IOException ioe)
		{
			handleIOTrouble(ioe);
		}
		super.renderSpace(space);
	}

	/**
	 * Render an inline viewport.
	 * This renders an inline viewport by clipping if necessary.
	 * 
	 * @param viewport
	 *            the viewport to handle
	 * @todo Copied from AbstractPathOrientedRenderer
	 */
	public void renderViewport(Viewport viewport)
	{

		float x = currentIPPosition / 1000f;
		float y = (currentBPPosition + viewport.getOffset()) / 1000f;
		float width = viewport.getIPD() / 1000f;
		float height = viewport.getBPD() / 1000f;
		// TODO: Calculate the border rect correctly.
		float borderPaddingStart = viewport.getBorderAndPaddingWidthStart() / 1000f;
		float borderPaddingBefore = viewport.getBorderAndPaddingWidthBefore() / 1000f;
		float bpwidth = borderPaddingStart
				+ (viewport.getBorderAndPaddingWidthEnd() / 1000f);
		float bpheight = borderPaddingBefore
				+ (viewport.getBorderAndPaddingWidthAfter() / 1000f);

		drawBackAndBorders(viewport, x, y, width + bpwidth, height + bpheight);

		if (viewport.getClip())
		{
			saveGraphicsState();

			clipRect(x + borderPaddingStart, y + borderPaddingBefore, width,
					height);
		}
		super.renderViewport(viewport);

		if (viewport.getClip())
		{
			restoreGraphicsState();
		}
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderBlockViewport(BlockViewport,
	 *      List)
	 */
	protected void renderBlockViewport(BlockViewport bv, List children)
	{
		// clip and position viewport if necessary

		// save positions
		int saveIP = currentIPPosition;
		int saveBP = currentBPPosition;
		// String saveFontName = currentFontName;

		CTM ctm = bv.getCTM();
		int borderPaddingStart = bv.getBorderAndPaddingWidthStart();
		int borderPaddingBefore = bv.getBorderAndPaddingWidthBefore();
		float x, y;
		x = (float) (bv.getXOffset() + containingIPPosition) / 1000f;
		y = (float) (bv.getYOffset() + containingBPPosition) / 1000f;
		// This is the content-rect
		float width = (float) bv.getIPD() / 1000f;
		float height = (float) bv.getBPD() / 1000f;

		if (bv.getPositioning() == Block.ABSOLUTE
				|| bv.getPositioning() == Block.FIXED)
		{

			currentIPPosition = bv.getXOffset();
			currentBPPosition = bv.getYOffset();

			// For FIXED, we need to break out of the current viewports to the
			// one established by the page. We save the state stack for
			// restoration
			// after the block-container has been painted. See below.
			List breakOutList = null;
			if (bv.getPositioning() == Block.FIXED)
			{
				breakOutList = breakOutOfStateStack();
			}

			CTM tempctm = new CTM(containingIPPosition, containingBPPosition);
			ctm = tempctm.multiply(ctm);

			// Adjust for spaces (from margin or indirectly by start-indent etc.
			x += bv.getSpaceStart() / 1000f;
			currentIPPosition += bv.getSpaceStart();

			y += bv.getSpaceBefore() / 1000f;
			currentBPPosition += bv.getSpaceBefore();

			float bpwidth = (borderPaddingStart + bv
					.getBorderAndPaddingWidthEnd()) / 1000f;
			float bpheight = (borderPaddingBefore + bv
					.getBorderAndPaddingWidthAfter()) / 1000f;

			drawBackAndBorders(bv, x, y, width + bpwidth, height + bpheight);

			// Now adjust for border/padding
			currentIPPosition += borderPaddingStart;
			currentBPPosition += borderPaddingBefore;

			Rectangle2D clippingRect = null;
			if (bv.getClip())
			{
				clippingRect = new Rectangle(currentIPPosition,
						currentBPPosition, bv.getIPD(), bv.getBPD());
			}

			startVParea(ctm, clippingRect);
			currentIPPosition = 0;
			currentBPPosition = 0;
			renderBlocks(bv, children);
			endVParea();

			if (breakOutList != null)
			{
				restoreStateStackAfterBreakOut(breakOutList);
			}

			currentIPPosition = saveIP;
			currentBPPosition = saveBP;
		} else
		{

			currentBPPosition += bv.getSpaceBefore();

			// borders and background in the old coordinate system
			handleBlockTraits(bv);

			// Advance to start of content area
			currentIPPosition += bv.getStartIndent();

			CTM tempctm = new CTM(containingIPPosition, currentBPPosition);
			ctm = tempctm.multiply(ctm);

			// Now adjust for border/padding
			currentBPPosition += borderPaddingBefore;

			Rectangle2D clippingRect = null;
			if (bv.getClip())
			{
				clippingRect = new Rectangle(currentIPPosition,
						currentBPPosition, bv.getIPD(), bv.getBPD());
			}

			startVParea(ctm, clippingRect);
			currentIPPosition = 0;
			currentBPPosition = 0;
			renderBlocks(bv, children);
			endVParea();

			currentIPPosition = saveIP;
			currentBPPosition = saveBP;

			currentBPPosition += (int) (bv.getAllocBPD());
		}
		// currentFontName = saveFontName;
	}

	private List breakOutOfStateStack()
	{
		log.debug("Block.FIXED --> break out");
		List breakOutList = new java.util.ArrayList();
		while (!this.graphicContextStack.empty())
		{
			breakOutList.add(0, this.graphicContext);
			restoreGraphicsState();
		}
		return breakOutList;
	}

	private void restoreStateStackAfterBreakOut(List breakOutList)
	{
		log.debug("Block.FIXED --> restoring context after break-out");
		for (int i = 0, c = breakOutList.size(); i < c; i++)
		{
			saveGraphicsState();
			this.graphicContext = (GraphicContext) breakOutList.get(i);
		}
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderImage(Image,
	 *      Rectangle2D)
	 */
	public void renderImage(Image image, Rectangle2D pos)
	{
		drawImage(image.getURL(), pos, image.getForeignAttributes());
	}

	/**
	 * Draw an image at the indicated location.
	 * 
	 * @param url
	 *            the URI/URL of the image
	 * @param pos
	 *            the position of the image
	 * @param foreignAttributes
	 *            an optional Map with foreign attributes, may be null
	 */
	protected void drawImage(String url, Rectangle2D pos, Map foreignAttributes)
	{
		url = ImageFactory.getURL(url);
		ImageFactory fact = userAgent.getFactory().getImageFactory();
		FovImage fovimage = fact.getImage(url, userAgent);
		// System.out.println("fovimage = " + fovimage);
		if (fovimage == null)
		{
			return;
		}
		if (!fovimage.load(FovImage.DIMENSIONS))
		{
			return;
		}
		String mime = fovimage.getMimeType();
		if ("text/xml".equals(mime))
		{
			if (!fovimage.load(FovImage.ORIGINAL_DATA))
			{
				return;
			}
			Document doc = ((XMLImage) fovimage).getDocument();
			String ns = ((XMLImage) fovimage).getNameSpace();

			renderDocument(doc, ns, pos, foreignAttributes);
		} else if ("image/svg+xml".equals(mime))
		{
			if (!fovimage.load(FovImage.ORIGINAL_DATA))
			{
				return;
			}
			Document doc = ((XMLImage) fovimage).getDocument();
			String ns = ((XMLImage) fovimage).getNameSpace();

			renderDocument(doc, ns, pos, foreignAttributes);
		} else if (fovimage instanceof EPSImage)
		{
			log.warn("EPS images are not supported by this renderer");
		} else
		{
			if (!fovimage.load(FovImage.BITMAP))
			{
				log.error("Bitmap image could not be processed: " + fovimage);
				return;
			}
			byte[] imgmap = fovimage.getBitmaps();

			ColorModel cm = new ComponentColorModel(ColorSpace
					.getInstance(ColorSpace.CS_LINEAR_RGB), new int[]
			{ 8, 8, 8 }, false, false, ColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
			int imgw = fovimage.getWidth();
			int imgh = fovimage.getHeight();
			SampleModel sampleModel = new PixelInterleavedSampleModel(
					DataBuffer.TYPE_BYTE, imgw, imgh, 3, imgw * 3, new int[]
					{ 0, 1, 2 });
			DataBuffer dbuf = new DataBufferByte(imgmap, imgw * imgh * 3);

			WritableRaster raster = Raster.createWritableRaster(sampleModel,
					dbuf, null);

			// Combine the color model and raster into a buffered image
			RenderedImage img = new BufferedImage(cm, raster, false, null);
			// RenderedImage img = new BufferedImage(imgw, imgh,
			// BufferedImage.TYPE_BYTE_GRAY);

			try
			{
				setCursorPos(this.currentIPPosition + (int) pos.getX(),
						this.currentBPPosition + (int) pos.getY());
				gen.paintBitmap(img, new Dimension((int) pos.getWidth(),
						(int) pos.getHeight()), false);
			} catch (IOException ioe)
			{
				handleIOTrouble(ioe);
			}
		}
	}

	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderForeignObject(ForeignObject,
	 *      Rectangle2D)
	 */
	public void renderForeignObject(ForeignObject fo, Rectangle2D pos)
	{
		Document doc = fo.getDocument();
		String ns = fo.getNameSpace();
		renderDocument(doc, ns, pos, fo.getForeignAttributes());
	}

	/**
	 * Common method to render the background and borders for any inline area.
	 * The all borders and padding are drawn outside the specified area.
	 * 
	 * @param area
	 *            the inline area for which the background, border and padding
	 *            is to be
	 *            rendered
	 * @todo Copied from AbstractPathOrientedRenderer
	 */
	protected void renderInlineAreaBackAndBorders(InlineArea area)
	{
		float x = currentIPPosition / 1000f;
		float y = (currentBPPosition + area.getOffset()) / 1000f;
		float width = area.getIPD() / 1000f;
		float height = area.getBPD() / 1000f;
		float borderPaddingStart = area.getBorderAndPaddingWidthStart() / 1000f;
		float borderPaddingBefore = area.getBorderAndPaddingWidthBefore() / 1000f;
		float bpwidth = borderPaddingStart
				+ (area.getBorderAndPaddingWidthEnd() / 1000f);
		float bpheight = borderPaddingBefore
				+ (area.getBorderAndPaddingWidthAfter() / 1000f);

		if (height != 0.0f || bpheight != 0.0f && bpwidth != 0.0f)
		{
			drawBackAndBorders(area, x, y - borderPaddingBefore, width
					+ bpwidth, height + bpheight);
		}
	}

	/**
	 * Draw the background and borders. This draws the background and border
	 * traits for an area given the position.
	 * 
	 * @param area
	 *            the area whose traits are used
	 * @param startx
	 *            the start x position
	 * @param starty
	 *            the start y position
	 * @param width
	 *            the width of the area
	 * @param height
	 *            the height of the area
	 */
	protected void drawBackAndBorders(Area area, float startx, float starty,
			float width, float height)
	{
		BorderProps bpsBefore = (BorderProps) area
				.getTrait(Trait.BORDER_BEFORE);
		BorderProps bpsAfter = (BorderProps) area.getTrait(Trait.BORDER_AFTER);
		BorderProps bpsStart = (BorderProps) area.getTrait(Trait.BORDER_START);
		BorderProps bpsEnd = (BorderProps) area.getTrait(Trait.BORDER_END);

		// draw background
		Trait.Background back;
		back = (Trait.Background) area.getTrait(Trait.BACKGROUND);
		if (back != null)
		{

			// Calculate padding rectangle
			float sx = startx;
			float sy = starty;
			float paddRectWidth = width;
			float paddRectHeight = height;

			if (bpsStart != null)
			{
				sx += bpsStart.width / 1000f;
				paddRectWidth -= bpsStart.width / 1000f;
			}
			if (bpsBefore != null)
			{
				sy += bpsBefore.width / 1000f;
				paddRectHeight -= bpsBefore.width / 1000f;
			}
			if (bpsEnd != null)
			{
				paddRectWidth -= bpsEnd.width / 1000f;
			}
			if (bpsAfter != null)
			{
				paddRectHeight -= bpsAfter.width / 1000f;
			}

			if (back.getColor() != null)
			{
				updateFillColor(back.getColor());
				fillRect(sx, sy, paddRectWidth, paddRectHeight);
			}

			// background image
			if (back.getFovImage() != null)
			{
				FovImage fovimage = back.getFovImage();
				if (fovimage != null && fovimage.load(FovImage.DIMENSIONS))
				{
					saveGraphicsState();
					clipRect(sx, sy, paddRectWidth, paddRectHeight);
					int horzCount = (int) ((paddRectWidth * 1000 / fovimage
							.getIntrinsicWidth()) + 1.0f);
					int vertCount = (int) ((paddRectHeight * 1000 / fovimage
							.getIntrinsicHeight()) + 1.0f);
					if (back.getRepeat() == EN_NOREPEAT)
					{
						horzCount = 1;
						vertCount = 1;
					} else if (back.getRepeat() == EN_REPEATX)
					{
						vertCount = 1;
					} else if (back.getRepeat() == EN_REPEATY)
					{
						horzCount = 1;
					}
					// change from points to millipoints
					sx *= 1000;
					sy *= 1000;
					if (horzCount == 1)
					{
						sx += back.getHoriz();
					}
					if (vertCount == 1)
					{
						sy += back.getVertical();
					}
					for (int x = 0; x < horzCount; x++)
					{
						for (int y = 0; y < vertCount; y++)
						{
							// place once
							Rectangle2D pos;
							// Image positions are relative to the currentIP/BP
							pos = new Rectangle2D.Float(sx - currentIPPosition
									+ (x * fovimage.getIntrinsicWidth()), sy
									- currentBPPosition
									+ (y * fovimage.getIntrinsicHeight()),
									fovimage.getIntrinsicWidth(), fovimage
											.getIntrinsicHeight());
							drawImage(back.getURL(), pos, null);
						}
					}
					restoreGraphicsState();
				} else
				{
					log.warn("Can't find background image: " + back.getURL());
				}
			}
		}

		Rectangle2D.Float borderRect = new Rectangle2D.Float(startx, starty,
				width, height);
		drawBorders(borderRect, bpsBefore, bpsAfter, bpsStart, bpsEnd);
	}

	/**
	 * Draws borders.
	 * 
	 * @param borderRect
	 *            the border rectangle
	 * @param bpsBefore
	 *            the border specification on the before side
	 * @param bpsAfter
	 *            the border specification on the after side
	 * @param bpsStart
	 *            the border specification on the start side
	 * @param bpsEnd
	 *            the border specification on the end side
	 */
	protected void drawBorders(Rectangle2D.Float borderRect,
			final BorderProps bpsBefore, final BorderProps bpsAfter,
			final BorderProps bpsStart, final BorderProps bpsEnd)
	{
		if (bpsBefore == null && bpsAfter == null && bpsStart == null
				&& bpsEnd == null)
		{
			return; // no borders to paint
		}
		if (qualityBeforeSpeed)
		{
			drawQualityBorders(borderRect, bpsBefore, bpsAfter, bpsStart,
					bpsEnd);
		} else
		{
			drawFastBorders(borderRect, bpsBefore, bpsAfter, bpsStart, bpsEnd);
		}
	}

	/**
	 * Draws borders. Borders are drawn as shaded rectangles with no clipping.
	 * 
	 * @param borderRect
	 *            the border rectangle
	 * @param bpsBefore
	 *            the border specification on the before side
	 * @param bpsAfter
	 *            the border specification on the after side
	 * @param bpsStart
	 *            the border specification on the start side
	 * @param bpsEnd
	 *            the border specification on the end side
	 */
	protected void drawFastBorders(Rectangle2D.Float borderRect,
			final BorderProps bpsBefore, final BorderProps bpsAfter,
			final BorderProps bpsStart, final BorderProps bpsEnd)
	{
		float startx = borderRect.x;
		float starty = borderRect.y;
		float width = borderRect.width;
		float height = borderRect.height;
		if (bpsBefore != null)
		{
			float borderWidth = bpsBefore.width / 1000f;
			updateFillColor(bpsBefore.color);
			fillRect(startx, starty, width, borderWidth);
		}
		if (bpsAfter != null)
		{
			float borderWidth = bpsAfter.width / 1000f;
			updateFillColor(bpsAfter.color);
			fillRect(startx, (starty + height - borderWidth), width,
					borderWidth);
		}
		if (bpsStart != null)
		{
			float borderWidth = bpsStart.width / 1000f;
			updateFillColor(bpsStart.color);
			fillRect(startx, starty, borderWidth, height);
		}
		if (bpsEnd != null)
		{
			float borderWidth = bpsEnd.width / 1000f;
			updateFillColor(bpsEnd.color);
			fillRect((startx + width - borderWidth), starty, borderWidth,
					height);
		}
	}

	/**
	 * Draws borders. Borders are drawn in-memory and painted as a bitmap.
	 * 
	 * @param borderRect
	 *            the border rectangle
	 * @param bpsBefore
	 *            the border specification on the before side
	 * @param bpsAfter
	 *            the border specification on the after side
	 * @param bpsStart
	 *            the border specification on the start side
	 * @param bpsEnd
	 *            the border specification on the end side
	 */
	protected void drawQualityBorders(Rectangle2D.Float borderRect,
			final BorderProps bpsBefore, final BorderProps bpsAfter,
			final BorderProps bpsStart, final BorderProps bpsEnd)
	{
		Graphics2DAdapter g2a = getGraphics2DAdapter();
		final Rectangle.Float effBorderRect = new Rectangle2D.Float(
				borderRect.x - (currentIPPosition / 1000f), borderRect.y
						- (currentBPPosition / 1000f), borderRect.width,
				borderRect.height);
		final Rectangle paintRect = new Rectangle((int) Math
				.round(borderRect.x * 1000f), (int) Math
				.round(borderRect.y * 1000f), (int) Math
				.floor(borderRect.width * 1000f) + 1, (int) Math
				.floor(borderRect.height * 1000f) + 1);
		// Add one pixel wide safety margin around the paint area
		int pixelWidth = (int) Math.round(UnitConv.in2mpt(1)
				/ userAgent.getTargetResolution());
		final int xoffset = (int) Math.round(-effBorderRect.x * 1000f)
				+ pixelWidth;
		final int yoffset = pixelWidth;
		paintRect.x += xoffset;
		paintRect.y += yoffset;
		paintRect.width += 2 * pixelWidth;
		paintRect.height += 2 * pixelWidth;

		RendererContext rc = createRendererContext(paintRect.x, paintRect.y,
				paintRect.width, paintRect.height, null);
		Map atts = new java.util.HashMap();
		atts.put(CONV_MODE, "bitmap"); // bitmap--> JPEG
		atts.put(SRC_TRANSPARENCY, "true");
		rc.setProperty(RendererContextConstants.FOREIGN_ATTRIBUTES, atts);

		Graphics2DImagePainter painter = new Graphics2DImagePainter()
		{

			public void paint(Graphics2D g2d, Rectangle2D area)
			{
				g2d.translate(xoffset, yoffset);
				g2d.scale(1000, 1000);
				float startx = effBorderRect.x;
				float starty = effBorderRect.y;
				float width = effBorderRect.width;
				float height = effBorderRect.height;
				boolean[] b = new boolean[]
				{ (bpsBefore != null), (bpsEnd != null), (bpsAfter != null),
						(bpsStart != null) };
				if (!b[0] && !b[1] && !b[2] && !b[3])
				{
					return;
				}
				float[] bw = new float[]
				{ (b[0] ? bpsBefore.width / 1000f : 0.0f),
						(b[1] ? bpsEnd.width / 1000f : 0.0f),
						(b[2] ? bpsAfter.width / 1000f : 0.0f),
						(b[3] ? bpsStart.width / 1000f : 0.0f) };
				float[] clipw = new float[]
				{ BorderProps.getClippedWidth(bpsBefore) / 1000f,
						BorderProps.getClippedWidth(bpsEnd) / 1000f,
						BorderProps.getClippedWidth(bpsAfter) / 1000f,
						BorderProps.getClippedWidth(bpsStart) / 1000f };
				starty += clipw[0];
				height -= clipw[0];
				height -= clipw[2];
				startx += clipw[3];
				width -= clipw[3];
				width -= clipw[1];

				boolean[] slant = new boolean[]
				{ (b[3] && b[0]), (b[0] && b[1]), (b[1] && b[2]),
						(b[2] && b[3]) };
				if (bpsBefore != null)
				{
					// endTextObject();

					float sx1 = startx;
					float sx2 = (slant[0] ? sx1 + bw[3] - clipw[3] : sx1);
					float ex1 = startx + width;
					float ex2 = (slant[1] ? ex1 - bw[1] + clipw[1] : ex1);
					float outery = starty - clipw[0];
					float clipy = outery + clipw[0];
					float innery = outery + bw[0];

					// saveGraphicsState();
					Graphics2D g = (Graphics2D) g2d.create();
					moveTo(sx1, clipy);
					float sx1a = sx1;
					float ex1a = ex1;
					if (bpsBefore.mode == BorderProps.COLLAPSE_OUTER)
					{
						if (bpsStart != null
								&& bpsStart.mode == BorderProps.COLLAPSE_OUTER)
						{
							sx1a -= clipw[3];
						}
						if (bpsEnd != null
								&& bpsEnd.mode == BorderProps.COLLAPSE_OUTER)
						{
							ex1a += clipw[1];
						}
						lineTo(sx1a, outery);
						lineTo(ex1a, outery);
					}
					lineTo(ex1, clipy);
					lineTo(ex2, innery);
					lineTo(sx2, innery);
					closePath();
					// clip();
					g.clip(currentPath);
					currentPath = null;
					Rectangle2D.Float lineRect = new Rectangle2D.Float(sx1a,
							outery, ex1a - sx1a, innery - outery);
					Java2DRenderer.drawBorderLine(lineRect, true, true,
							bpsBefore.style, bpsBefore.color, g);
					// restoreGraphicsState();
				}
				if (bpsEnd != null)
				{
					// endTextObject();

					float sy1 = starty;
					float sy2 = (slant[1] ? sy1 + bw[0] - clipw[0] : sy1);
					float ey1 = starty + height;
					float ey2 = (slant[2] ? ey1 - bw[2] + clipw[2] : ey1);
					float outerx = startx + width + clipw[1];
					float clipx = outerx - clipw[1];
					float innerx = outerx - bw[1];

					// saveGraphicsState();
					Graphics2D g = (Graphics2D) g2d.create();
					moveTo(clipx, sy1);
					float sy1a = sy1;
					float ey1a = ey1;
					if (bpsEnd.mode == BorderProps.COLLAPSE_OUTER)
					{
						if (bpsBefore != null
								&& bpsBefore.mode == BorderProps.COLLAPSE_OUTER)
						{
							sy1a -= clipw[0];
						}
						if (bpsAfter != null
								&& bpsAfter.mode == BorderProps.COLLAPSE_OUTER)
						{
							ey1a += clipw[2];
						}
						lineTo(outerx, sy1a);
						lineTo(outerx, ey1a);
					}
					lineTo(clipx, ey1);
					lineTo(innerx, ey2);
					lineTo(innerx, sy2);
					closePath();
					// clip();
					g.setClip(currentPath);
					currentPath = null;
					Rectangle2D.Float lineRect = new Rectangle2D.Float(innerx,
							sy1a, outerx - innerx, ey1a - sy1a);
					Java2DRenderer.drawBorderLine(lineRect, false, false,
							bpsEnd.style, bpsEnd.color, g);
					// restoreGraphicsState();
				}
				if (bpsAfter != null)
				{
					// endTextObject();

					float sx1 = startx;
					float sx2 = (slant[3] ? sx1 + bw[3] - clipw[3] : sx1);
					float ex1 = startx + width;
					float ex2 = (slant[2] ? ex1 - bw[1] + clipw[1] : ex1);
					float outery = starty + height + clipw[2];
					float clipy = outery - clipw[2];
					float innery = outery - bw[2];

					// saveGraphicsState();
					Graphics2D g = (Graphics2D) g2d.create();
					moveTo(ex1, clipy);
					float sx1a = sx1;
					float ex1a = ex1;
					if (bpsAfter.mode == BorderProps.COLLAPSE_OUTER)
					{
						if (bpsStart != null
								&& bpsStart.mode == BorderProps.COLLAPSE_OUTER)
						{
							sx1a -= clipw[3];
						}
						if (bpsEnd != null
								&& bpsEnd.mode == BorderProps.COLLAPSE_OUTER)
						{
							ex1a += clipw[1];
						}
						lineTo(ex1a, outery);
						lineTo(sx1a, outery);
					}
					lineTo(sx1, clipy);
					lineTo(sx2, innery);
					lineTo(ex2, innery);
					closePath();
					// clip();
					g.setClip(currentPath);
					currentPath = null;
					Rectangle2D.Float lineRect = new Rectangle2D.Float(sx1a,
							innery, ex1a - sx1a, outery - innery);
					Java2DRenderer.drawBorderLine(lineRect, true, false,
							bpsAfter.style, bpsAfter.color, g);
					// restoreGraphicsState();
				}
				if (bpsStart != null)
				{
					// endTextObject();

					float sy1 = starty;
					float sy2 = (slant[0] ? sy1 + bw[0] - clipw[0] : sy1);
					float ey1 = sy1 + height;
					float ey2 = (slant[3] ? ey1 - bw[2] + clipw[2] : ey1);
					float outerx = startx - clipw[3];
					float clipx = outerx + clipw[3];
					float innerx = outerx + bw[3];

					// saveGraphicsState();
					Graphics2D g = (Graphics2D) g2d.create();
					moveTo(clipx, ey1);
					float sy1a = sy1;
					float ey1a = ey1;
					if (bpsStart.mode == BorderProps.COLLAPSE_OUTER)
					{
						if (bpsBefore != null
								&& bpsBefore.mode == BorderProps.COLLAPSE_OUTER)
						{
							sy1a -= clipw[0];
						}
						if (bpsAfter != null
								&& bpsAfter.mode == BorderProps.COLLAPSE_OUTER)
						{
							ey1a += clipw[2];
						}
						lineTo(outerx, ey1a);
						lineTo(outerx, sy1a);
					}
					lineTo(clipx, sy1);
					lineTo(innerx, sy2);
					lineTo(innerx, ey2);
					closePath();
					// clip();
					g.setClip(currentPath);
					currentPath = null;
					Rectangle2D.Float lineRect = new Rectangle2D.Float(outerx,
							sy1a, innerx - outerx, ey1a - sy1a);
					Java2DRenderer.drawBorderLine(lineRect, false, false,
							bpsStart.style, bpsStart.color, g);
					// restoreGraphicsState();
				}
			}

			public Dimension getImageSize()
			{
				return paintRect.getSize();
			}

		};
		try
		{
			g2a.paintImage(painter, rc, paintRect.x - xoffset, paintRect.y,
					paintRect.width, paintRect.height);
		} catch (IOException ioe)
		{
			handleIOTrouble(ioe);
		}
	}

	private String getPaperEntry(String mediaUsage)
	{
		if (mediaUsage == null || mediaUsage == "")
		{
			mediaUsage = "0";
		}
		// 如果以“#”开始，则证明直接是打印控制命令，无需去对照表中找
		if (mediaUsage.startsWith("#"))
		{
			if (mediaUsage.length() == 1)
			{
				return null;
			} else
			{
				return mediaUsage.substring(1);
			}
		}
		if (table == null)
		{
			return "";
		}
		PrintRef info = (PrintRef) table.get(mediaUsage);
		if (info == null)
		{
			return "";
		}
		return info.getCommonLine();
	}

	@Override
	protected int getTotalPage()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
