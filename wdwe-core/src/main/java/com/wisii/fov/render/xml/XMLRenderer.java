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
/* $Id: XMLRenderer.java 426576 2006-07-28 15:44:37Z jeremias $ */
package com.wisii.fov.render.xml;

// Java
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import com.wisii.fov.util.QName;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.AreaTreeObject;
import com.wisii.fov.area.BeforeFloat;
import com.wisii.fov.area.Block;
import com.wisii.fov.area.BlockViewport;
import com.wisii.fov.area.BodyRegion;
import com.wisii.fov.area.CTM;
import com.wisii.fov.area.Footnote;
import com.wisii.fov.area.LineArea;
import com.wisii.fov.area.MainReference;
import com.wisii.fov.area.NormalFlow;
import com.wisii.fov.area.OffDocumentExtensionAttachment;
import com.wisii.fov.area.OffDocumentItem;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.RegionReference;
import com.wisii.fov.area.RegionViewport;
import com.wisii.fov.area.Span;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.Trait.Background;
import com.wisii.fov.area.inline.Container;
import com.wisii.fov.area.inline.ForeignObject;
import com.wisii.fov.area.inline.Image;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.InlineBlockParent;
import com.wisii.fov.area.inline.InlineParent;
import com.wisii.fov.area.inline.Leader;
import com.wisii.fov.area.inline.Space;
import com.wisii.fov.area.inline.SpaceArea;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.area.inline.Viewport;
import com.wisii.fov.area.inline.WordArea;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.extensions.ExtensionAttachment;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.fonts.FontSetup;
import com.wisii.fov.fonts.FontTriplet;
import com.wisii.fov.render.PrintRenderer;
import com.wisii.fov.render.Renderer;
import com.wisii.fov.render.RendererContext;
import com.wisii.fov.render.XMLHandler;
import com.wisii.fov.util.ColorUtil;
import com.wisii.fov.util.XMLizable;

/**
 * Renderer that renders areas to XML for debugging purposes.
 * This creates an xml that contains the information of the area
 * tree. It does not output any state or derived information.
 * The output can be used to build a new area tree (@see AreaTreeBuilder)
 * which can be rendered to any renderer.
 */
public class XMLRenderer extends PrintRenderer
{
	/** XML MIME type */
	public static final String XML_MIME_TYPE = MimeConstants.MIME_WISII_AREA_TREE;
	/** Main namespace in use. */
	public static final String NS = "";
	/** CDATA type */
	public static final String CDATA = "CDATA";
	/** An empty Attributes object used when no attributes are needed. */
	public static final Attributes EMPTY_ATTS = new AttributesImpl();
	private boolean startedSequence = false;
	private RendererContext context;
	private boolean compactFormat = false;
	/**
	 * If not null, the XMLRenderer will mimic another renderer by using its
	 * font setup.
	 */
	protected Renderer mimic;
	/** ContentHandler that the generated XML is written to */
	protected ContentHandler handler;
	/** AttributesImpl instance that can be used during XML generation. */
	protected AttributesImpl atts = new AttributesImpl();
	/** The OutputStream to write the generated XML to. */
	protected OutputStream out;
	/**
	 * A list of ExtensionAttachements received through processOffDocumentItem()
	 */
	protected List extensionAttachments;
	private int totalpage = 0;
	/**
	 * Creates a new XML renderer.
	 */
	public XMLRenderer()
	{
		context = new RendererContext(this, XML_MIME_TYPE);
	}
	/**
	 * Configure the XML renderer.
	 * Get the configuration to be used for fonts etc.
	 * 
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
	 */
	public void configure(Configuration cfg) throws ConfigurationException
	{
		super.configure(cfg);
		// Font configuration
		List cfgFonts = FontSetup.buildFontListFromConfiguration(cfg);
		if (this.fontList == null)
		{
			this.fontList = cfgFonts;
		}
		else
		{
			this.fontList.addAll(cfgFonts);
		}
	}
	/**
	 * @see com.wisii.fov.render.Renderer#setUserAgent(FOUserAgent)
	 */
	public void setUserAgent(FOUserAgent agent)
	{
		super.setUserAgent(agent);
		XMLHandler xmlHandler = new XMLXMLHandler();
		userAgent.getXMLHandlerRegistry().addXMLHandler(xmlHandler);
		Boolean b = (Boolean) userAgent.getRendererOptions().get(
				"compact-format");
		if (b != null)
		{
			setCompactFormat(b.booleanValue());
		}
	}
	/**
	 * Call this method to make the XMLRenderer mimic a different renderer by
	 * using its font
	 * setup. This is useful when working with the intermediate format parser.
	 * 
	 * @param renderer the renderer to mimic
	 */
	public void mimicRenderer(Renderer renderer)
	{
		this.mimic = renderer;
	}
	/** @see com.wisii.fov.render.PrintRenderer#setupFontInfo(com.wisii.fov.fonts.FontInfo) */
	public void setupFontInfo(FontInfo inFontInfo)
	{
		// if (mimic != null) {
		// mimic.setupFontInfo(inFontInfo);
		// } else {
		// super.setupFontInfo(inFontInfo);
		// }
		BufferedImage fontImage = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = fontImage.createGraphics();
		// The next line is important to get accurate font metrics!
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		com.wisii.fov.render.java2d.FontSetup.setup(inFontInfo, g);
	}
	/**
	 * Sets an outside TransformerHandler to use instead of the default one
	 * create in this class in startRenderer().
	 * 
	 * @param handler Overriding TransformerHandler
	 */
	public void setContentHandler(ContentHandler handler)
	{
		this.handler = handler;
	}
	public void setCompactFormat(boolean compact)
	{
		this.compactFormat = compact;
	}
	private boolean isDetailedFormat()
	{
		return !this.compactFormat;
	}
	/**
	 * Handles SAXExceptions.
	 * 
	 * @param saxe the SAXException to handle
	 */
	protected void handleSAXException(SAXException saxe)
	{
		throw new RuntimeException(saxe.getMessage());
	}
	/**
	 * Writes a comment to the generated XML.
	 * 
	 * @param comment the comment
	 */
	protected void comment(String comment)
	{
		if (handler instanceof LexicalHandler)
		{
			try
			{
				((LexicalHandler) handler).comment(comment.toCharArray(), 0,
						comment.length());
			}
			catch (SAXException saxe)
			{
				handleSAXException(saxe);
			}
		}
	}
	/**
	 * Starts a new element (without attributes).
	 * 
	 * @param tagName tag name of the element
	 */
	protected void startElement(String tagName)
	{
		startElement(tagName, EMPTY_ATTS);
	}
	/**
	 * Starts a new element.
	 * 
	 * @param tagName tag name of the element
	 * @param atts attributes to add
	 */
	protected void startElement(String tagName, Attributes atts)
	{
		try
		{
			handler.startElement(NS, tagName, tagName, atts);
		}
		catch (SAXException saxe)
		{
			handleSAXException(saxe);
		}
	}
	/**
	 * Ends an element.
	 * 
	 * @param tagName tag name of the element
	 */
	protected void endElement(String tagName)
	{
		try
		{
			handler.endElement(NS, tagName, tagName);
		}
		catch (SAXException saxe)
		{
			handleSAXException(saxe);
		}
	}
	/**
	 * Sends plain text to the XML
	 * 
	 * @param text the text
	 */
	protected void characters(String text)
	{
		try
		{
			char[] ca = text.toCharArray();
			handler.characters(ca, 0, ca.length);
		}
		catch (SAXException saxe)
		{
			handleSAXException(saxe);
		}
	}
	/**
	 * Adds a new attribute to the protected member variable "atts".
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 */
	protected void addAttribute(String name, String value)
	{
		atts.addAttribute(NS, name, name, CDATA, value);
	}
	/**
	 * Adds a new attribute to the protected member variable "atts".
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 */
	protected void addAttribute(QName name, String value)
	{
		atts.addAttribute(name.getNamespaceURI(), name.getLocalName(),
				name.getQName(), CDATA, value);
	}
	/**
	 * Adds a new attribute to the protected member variable "atts".
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 */
	protected void addAttribute(String name, int value)
	{
		addAttribute(name, Integer.toString(value));
	}
	/**
	 * Adds a new attribute to the protected member variable "atts".
	 * 
	 * @param name name of the attribute
	 * @param rect a Rectangle2D to format and use as attribute value
	 */
	protected void addAttribute(String name, Rectangle2D rect)
	{
		addAttribute(name, createString(rect));
	}
	/**
	 * Adds the general Area attributes.
	 * 
	 * @param area Area to extract attributes from
	 */
	protected void addAreaAttributes(Area area)
	{
		addAttribute("ipd", area.getIPD());
		addAttribute("bpd", area.getBPD());
		if (isDetailedFormat())
		{
			if (area.getIPD() != 0)
			{
				addAttribute("ipda", area.getAllocIPD());
			}
			if (area.getBPD() != 0)
			{
				addAttribute("bpda", area.getAllocBPD());
			}
			addAttribute(
					"bap",
					area.getBorderAndPaddingWidthStart() + " "
							+ area.getBorderAndPaddingWidthEnd() + " "
							+ area.getBorderAndPaddingWidthBefore() + " "
							+ area.getBorderAndPaddingWidthAfter());
		}
	}
	/**
	 * Adds attributes from traits of an Area.
	 * 
	 * @param area Area to extract traits from
	 */
	protected void addTraitAttributes(Area area)
	{
		Map traitMap = area.getTraits();
		if (traitMap != null)
		{
			Iterator iter = traitMap.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry traitEntry = (Map.Entry) iter.next();
				Object key = traitEntry.getKey();
				String name = Trait.getTraitName(key);
				Class clazz = Trait.getTraitClass(key);
				if ("break-before".equals(name) || "break-after".equals(name))
				{
					continue;
				}
				Object value = traitEntry.getValue();
				if (key == Trait.FONT)
				{
					FontTriplet triplet = (FontTriplet) value;
					addAttribute("font-name", triplet.getName());
					addAttribute("font-style", triplet.getStyle());
					addAttribute("font-weight", triplet.getWeight());
				}
				else if (clazz.equals(Background.class))
				{
					Background bkg = (Background) value;
					// TODO Remove the following line (makes changes in the test
					// checks necessary)
					addAttribute(name, bkg.toString());
					if (bkg.getColor() != null)
					{
						addAttribute("bkg-color", bkg.getColor().toString());
					}
					if (bkg.getURL() != null)
					{
						addAttribute("bkg-img", bkg.getURL());
						String repString;
						int repeat = bkg.getRepeat();
						switch (repeat)
						{
						case Constants.EN_REPEAT:
							repString = "repeat";
							break;
						case Constants.EN_REPEATX:
							repString = "repeat-x";
							break;
						case Constants.EN_REPEATY:
							repString = "repeat-y";
							break;
						case Constants.EN_NOREPEAT:
							repString = "no-repeat";
							break;
						default:
							throw new IllegalStateException(
									"Illegal value for repeat encountered: "
											+ repeat);
						}
						addAttribute("bkg-repeat", repString);
						addAttribute("bkg-horz-offset", bkg.getHoriz());
						addAttribute("bkg-vert-offset", bkg.getVertical());
					}
				}
				else if (clazz.equals(Color.class))
				{
					Color c = (Color) value;
					addAttribute(name, ColorUtil.colorTOsRGBString(c));
				}
				else if (key == Trait.START_INDENT || key == Trait.END_INDENT)
				{
					if (((Integer) value).intValue() != 0)
					{
						addAttribute(name, value.toString());
					}
				}
				else
				{
					addAttribute(name, value.toString());
				}
			}
		}
		transferForeignObjects(area);
	}
	private void transferForeignObjects(AreaTreeObject ato)
	{
		Map prefixes = new java.util.HashMap();
		Iterator iter = ato.getForeignAttributes().entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			QName qname = (QName) entry.getKey();
			prefixes.put(qname.getPrefix(), qname.getNamespaceURI());
			addAttribute(qname, (String) entry.getValue());
		}
		// Namespace declarations
		iter = prefixes.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String qn = "xmlns:" + (String) entry.getKey();
			atts.addAttribute("", (String) entry.getKey(), qn, CDATA,
					(String) entry.getValue());
		}
	}
	private String createString(Rectangle2D rect)
	{
		return "" + (int) rect.getX() + " " + (int) rect.getY() + " "
				+ (int) rect.getWidth() + " " + (int) rect.getHeight();
	}
	private void handleDocumentExtensionAttachments()
	{
		if (extensionAttachments != null && extensionAttachments.size() > 0)
		{
			handleExtensionAttachments(extensionAttachments);
			extensionAttachments.clear();
		}
	}
	/** @see com.wisii.fov.render.AbstractRenderer#processOffDocumentItem() */
	public void processOffDocumentItem(OffDocumentItem oDI)
	{
		if (oDI instanceof OffDocumentExtensionAttachment)
		{
			ExtensionAttachment attachment = ((OffDocumentExtensionAttachment) oDI)
					.getAttachment();
			if (extensionAttachments == null)
			{
				extensionAttachments = new java.util.ArrayList();
			}
			extensionAttachments.add(attachment);
		}
		else
		{
			String warn = "Ignoring OffDocumentItem: " + oDI;
			log.warn(warn);
		}
	}
	/**
	 * @see com.wisii.fov.render.Renderer#startRenderer(OutputStream)
	 */
	public void startRenderer(OutputStream outputStream) throws IOException
	{
		log.debug("Rendering areas to Area Tree XML");
		if (this.handler == null)
		{
			SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory
					.newInstance();
			try
			{
				TransformerHandler transformerHandler = factory
						.newTransformerHandler();
				this.handler = transformerHandler;
				StreamResult res = new StreamResult(outputStream);
				transformerHandler.setResult(res);
			}
			catch (TransformerConfigurationException tce)
			{
				throw new RuntimeException(tce.getMessage());
			}
			this.out = outputStream;
		}
		try
		{
			handler.startDocument();
		}
		catch (SAXException saxe)
		{
			handleSAXException(saxe);
		}
		if (userAgent.getProducer() != null)
		{
			comment("Produced by " + userAgent.getProducer());
		}
		startElement("areaTree");
	}
	/**
	 * @see com.wisii.fov.render.Renderer#stopRenderer()
	 */
	public void stopRenderer() throws IOException
	{
		if (startedSequence)
		{
			endElement("pageSequence");
		}
		endElement("areaTree");
		try
		{
			handler.endDocument();
		}
		catch (SAXException saxe)
		{
			handleSAXException(saxe);
		}
		if (this.out != null)
		{
			this.out.flush();
		}
		log.debug("Written out Area Tree XML");
	}
	/**
	 * @see com.wisii.fov.render.Renderer#renderPage(PageViewport)
	 */
	public void renderPage(PageViewport page) throws IOException, FOVException
	{
		totalpage++;
		atts.clear();
		addAttribute("bounds", page.getViewArea());
		addAttribute("key", page.getKey());
		addAttribute("nr", page.getPageNumber());
		addAttribute("formatted-nr", page.getPageNumberString());
		addAttribute("simple-page-master-name", page.getSimplePageMasterName());
		if (page.isBlank())
		{
			addAttribute("blank", "true");
		}
		transferForeignObjects(page);
		startElement("pageViewport", atts);
		startElement("page");
		handlePageExtensionAttachments(page);
		super.renderPage(page);
		endElement("page");
		endElement("pageViewport");
	}
	private void handleExtensionAttachments(List attachments)
	{
		if (attachments != null && attachments.size() > 0)
		{
			startElement("extension-attachments");
			Iterator i = attachments.iterator();
			while (i.hasNext())
			{
				ExtensionAttachment attachment = (ExtensionAttachment) i.next();
				if (attachment instanceof XMLizable)
				{
					try
					{
						((XMLizable) attachment).toSAX(this.handler);
					}
					catch (SAXException e)
					{
						log.error(
								"Error while serializing Extension Attachment",
								e);
					}
				}
				else
				{
					String warn = "Ignoring non-XMLizable ExtensionAttachment: "
							+ attachment;
					log.warn(warn);
				}
			}
			endElement("extension-attachments");
		}
	}
	private void handlePageExtensionAttachments(PageViewport page)
	{
		handleExtensionAttachments(page.getExtensionAttachments());
	}
	/**
	 * @see com.wisii.fov.render.Renderer#startPageSequence(LineArea)
	 */
	public void startPageSequence(LineArea seqTitle)
	{
		handleDocumentExtensionAttachments();
		if (startedSequence)
		{
			endElement("pageSequence");
		}
		startedSequence = true;
		startElement("pageSequence");
		if (seqTitle != null)
		{
			startElement("title");
			List children = seqTitle.getInlineAreas();
			for (int count = 0; count < children.size(); count++)
			{
				InlineArea inline = (InlineArea) children.get(count);
				renderInlineArea(inline);
			}
			endElement("title");
		}
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderRegionViewport(RegionViewport)
	 */
	protected void renderRegionViewport(RegionViewport port)
	{
		if (port != null)
		{
			atts.clear();
			addAreaAttributes(port);
			addTraitAttributes(port);
			addAttribute("rect", port.getViewArea());
			if (port.isClip())
			{
				addAttribute("clipped", "true");
			}
			startElement("regionViewport", atts);
			RegionReference region = port.getRegionReference();
			atts.clear();
			addAreaAttributes(region);
			addTraitAttributes(region);
			addAttribute("name", region.getRegionName());
			addAttribute("ctm", region.getCTM().toString());
			if (region.getRegionClass() == FO_REGION_BEFORE)
			{
				startElement("regionBefore", atts);
				renderRegion(region);
				endElement("regionBefore");
			}
			else if (region.getRegionClass() == FO_REGION_START)
			{
				startElement("regionStart", atts);
				renderRegion(region);
				endElement("regionStart");
			}
			else if (region.getRegionClass() == FO_REGION_BODY)
			{
				BodyRegion body = (BodyRegion) region;
				if (body.getColumnCount() != 1)
				{
					addAttribute("columnGap", body.getColumnGap());
					addAttribute("columnCount", body.getColumnCount());
				}
				startElement("regionBody", atts);
				renderBodyRegion(body);
				endElement("regionBody");
			}
			else if (region.getRegionClass() == FO_REGION_END)
			{
				startElement("regionEnd", atts);
				renderRegion(region);
				endElement("regionEnd");
			}
			else if (region.getRegionClass() == FO_REGION_AFTER)
			{
				startElement("regionAfter", atts);
				renderRegion(region);
				endElement("regionAfter");
			}
			endElement("regionViewport");
		}
	}
	/** @see com.wisii.fov.render.AbstractRenderer */
	protected void startVParea(CTM ctm, Rectangle2D clippingRect)
	{
		// only necessary for graphical output
	}
	/** @see com.wisii.fov.render.AbstractRenderer#endVParea() */
	protected void endVParea()
	{
		// only necessary for graphical output
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderInlineAreaBackAndBorders(com.wisii.fov.area.inline.InlineArea)
	 */
	protected void renderInlineAreaBackAndBorders(InlineArea area)
	{
		// only necessary for graphical output
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderBeforeFloat(BeforeFloat)
	 */
	protected void renderBeforeFloat(BeforeFloat bf)
	{
		startElement("beforeFloat");
		super.renderBeforeFloat(bf);
		endElement("beforeFloat");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderFootnote(Footnote)
	 */
	protected void renderFootnote(Footnote footnote)
	{
		startElement("footnote");
		super.renderFootnote(footnote);
		endElement("footnote");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderMainReference(MainReference)
	 */
	protected void renderMainReference(MainReference mr)
	{
		atts.clear();
		addAreaAttributes(mr);
		addTraitAttributes(mr);
		if (mr.getColumnCount() != 1)
		{
			addAttribute("columnGap", mr.getColumnGap());
		}
		startElement("mainReference", atts);
		Span span = null;
		List spans = mr.getSpans();
		for (int count = 0; count < spans.size(); count++)
		{
			span = (Span) spans.get(count);
			atts.clear();
			if (span.getColumnCount() != 1)
			{
				addAttribute("columnCount", span.getColumnCount());
			}
			addAreaAttributes(span);
			addTraitAttributes(span);
			startElement("span", atts);
			for (int c = 0; c < span.getColumnCount(); c++)
			{
				NormalFlow flow = (NormalFlow) span.getNormalFlow(c);
				renderFlow(flow);
			}
			endElement("span");
		}
		endElement("mainReference");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderFlow(NormalFlow)
	 */
	protected void renderFlow(NormalFlow flow)
	{
		// the normal flow reference area contains stacked blocks
		atts.clear();
		addAreaAttributes(flow);
		addTraitAttributes(flow);
		startElement("flow", atts);
		super.renderFlow(flow);
		endElement("flow");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderBlock(Block)
	 */
	protected void renderBlock(Block block)
	{
		atts.clear();
		addAreaAttributes(block);
		addTraitAttributes(block);
		int positioning = block.getPositioning();
		if (block instanceof BlockViewport)
		{
			BlockViewport bvp = (BlockViewport) block;
			boolean abspos = false;
			if (bvp.getPositioning() == Block.ABSOLUTE
					|| bvp.getPositioning() == Block.FIXED)
			{
				abspos = true;
			}
			if (abspos)
			{
				addAttribute("left-position", bvp.getXOffset());
				addAttribute("top-position", bvp.getYOffset());
			}
			addAttribute("ctm", bvp.getCTM().toString());
			if (bvp.getClip())
			{
				addAttribute("clipped", "true");
			}
		}
		else
		{
			if (block.getXOffset() != 0)
			{
				addAttribute("left-offset", block.getXOffset());
			}
			if (block.getYOffset() != 0)
			{
				addAttribute("top-offset", block.getYOffset());
			}
		}
		switch (positioning)
		{
		case Block.RELATIVE:
			addAttribute("positioning", "relative");
			break;
		case Block.ABSOLUTE:
			addAttribute("positioning", "absolute");
			break;
		case Block.FIXED:
			addAttribute("positioning", "fixed");
			break;
		default: // nop
		}
		startElement("block", atts);
		super.renderBlock(block);
		endElement("block");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderLineArea(LineArea)
	 */
	protected void renderLineArea(LineArea line)
	{
		atts.clear();
		addAreaAttributes(line);
		addTraitAttributes(line);
		startElement("lineArea", atts);
		super.renderLineArea(line);
		endElement("lineArea");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderViewport(Viewport)
	 */
	protected void renderViewport(Viewport viewport)
	{
		atts.clear();
		addAreaAttributes(viewport);
		addTraitAttributes(viewport);
		addAttribute("offset", viewport.getOffset());
		addAttribute("pos", viewport.getContentPosition());
		if (viewport.getClip())
		{
			addAttribute("clip", "true");
		}
		startElement("viewport", atts);
		super.renderViewport(viewport);
		endElement("viewport");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer
	 */
	public void renderImage(Image image, Rectangle2D pos)
	{
		atts.clear();
		addAreaAttributes(image);
		addTraitAttributes(image);
		addAttribute("url", image.getURL());
		// addAttribute("pos", pos);
		startElement("image", atts);
		endElement("image");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderContainer(Container)
	 */
	public void renderContainer(Container cont)
	{
		startElement("container");
		super.renderContainer(cont);
		endElement("container");
	}
	/**
	 * Renders an fo:foreing-object.
	 * 
	 * @param fo the foreign object
	 * @param pos the position of the foreign object
	 * @see com.wisii.fov.render.AbstractRenderer#renderForeignObject(ForeignObject,
	 *      Rectangle2D)
	 */
	public void renderForeignObject(ForeignObject fo, Rectangle2D pos)
	{
		atts.clear();
		addAreaAttributes(fo);
		addTraitAttributes(fo);
		String ns = fo.getNameSpace();
		addAttribute("ns", ns);
		startElement("foreignObject", atts);
		Document doc = fo.getDocument();
		context.setProperty(XMLXMLHandler.HANDLER, handler);
		renderXML(context, doc, ns);
		endElement("foreignObject");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderInlineSpace(Space)
	 */
	protected void renderInlineSpace(Space space)
	{
		atts.clear();
		addAreaAttributes(space);
		addTraitAttributes(space);
		addAttribute("offset", space.getOffset());
		startElement("space", atts);
		endElement("space");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderText(TextArea)
	 */
	protected void renderText(TextArea text)
	{
		atts.clear();
		if (text.getTextWordSpaceAdjust() != 0)
		{
			addAttribute("twsadjust", text.getTextWordSpaceAdjust());
		}
		if (text.getTextLetterSpaceAdjust() != 0)
		{
			addAttribute("tlsadjust", text.getTextLetterSpaceAdjust());
		}
		addAttribute("offset", text.getOffset());
		addAttribute("baseline", text.getBaselineOffset());
		addAreaAttributes(text);
		addTraitAttributes(text);
		startElement("text", atts);
		super.renderText(text);
		endElement("text");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderWord(WordArea)
	 */
	protected void renderWord(WordArea word)
	{
		atts.clear();
		addAttribute("offset", word.getOffset());
		int[] letterAdjust = word.getLetterAdjustArray();
		if (letterAdjust != null)
		{
			StringBuffer sb = new StringBuffer(64);
			boolean nonZeroFound = false;
			for (int i = 0, c = letterAdjust.length; i < c; i++)
			{
				if (i > 0)
				{
					sb.append(' ');
				}
				sb.append(letterAdjust[i]);
				nonZeroFound |= (letterAdjust[i] != 0);
			}
			if (nonZeroFound)
			{
				addAttribute("letter-adjust", sb.toString());
			}
		}
		startElement("word", atts);
		characters(word.getWord());
		endElement("word");
		super.renderWord(word);
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderSpace(SpaceArea)
	 */
	protected void renderSpace(SpaceArea space)
	{
		atts.clear();
		addAttribute("offset", space.getOffset());
		if (!space.isAdjustable())
		{
			addAttribute("adj", "false"); // default is true
		}
		startElement("space", atts);
		characters(space.getSpace());
		endElement("space");
		super.renderSpace(space);
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderInlineParent(InlineParent)
	 */
	protected void renderInlineParent(InlineParent ip)
	{
		atts.clear();
		addAreaAttributes(ip);
		addTraitAttributes(ip);
		addAttribute("offset", ip.getOffset());
		startElement("inlineparent", atts);
		super.renderInlineParent(ip);
		endElement("inlineparent");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderInlineBlockParent(InlineBlockParent)
	 */
	protected void renderInlineBlockParent(InlineBlockParent ibp)
	{
		atts.clear();
		addAreaAttributes(ibp);
		addTraitAttributes(ibp);
		addAttribute("offset", ibp.getOffset());
		startElement("inlineblockparent", atts);
		super.renderInlineBlockParent(ibp);
		endElement("inlineblockparent");
	}
	/**
	 * @see com.wisii.fov.render.AbstractRenderer#renderLeader(Leader)
	 */
	protected void renderLeader(Leader area)
	{
		atts.clear();
		addAreaAttributes(area);
		addTraitAttributes(area);
		addAttribute("offset", area.getOffset());
		addAttribute("ruleStyle", area.getRuleStyleAsString());
		addAttribute("ruleThickness", area.getRuleThickness());
		startElement("leader", atts);
		endElement("leader");
		super.renderLeader(area);
	}
	/** @see com.wisii.fov.render.AbstractRenderer#getMimeType() */
	public String getMimeType()
	{
		return XML_MIME_TYPE;
	}
	@Override
	protected int getTotalPage()
	{
		return totalpage;
	}
}
