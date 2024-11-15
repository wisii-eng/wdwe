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
 */package com.wisii.fov.render;

// Java
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.FilteredImageSource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.wisii.component.setting.PrintRef;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.components.complex.WdemsCompositeComponent;
import com.wisii.edit.tag.components.complex.WdemsScrollPane;
import com.wisii.edit.tag.components.decorative.NullInlineIndicator;
import com.wisii.edit.tag.components.decorative.VirtualButton;
import com.wisii.edit.tag.components.decorative.WdemsCascadeManager;
import com.wisii.edit.tag.components.decorative.WdemsEditComponentManager;
import com.wisii.edit.tag.components.decorative.WdemsOperationManager;
import com.wisii.edit.tag.components.decorative.NullInlineIndicator.NullInlineImp;
import com.wisii.edit.tag.components.group.SelectGroup;
import com.wisii.edit.tag.components.group.WdemsGroupComponent;
import com.wisii.edit.tag.components.group.WdemsGroupManager;
import com.wisii.edit.tag.components.group.WdemsGroupManager.GroupKey;
import com.wisii.edit.tag.components.select.AbstractWdemsCombox;
import com.wisii.edit.tag.factories.NullInlineFactory;
import com.wisii.edit.tag.schema.wdems.Checkbox;
import com.wisii.edit.tag.schema.wdems.Group;
import com.wisii.edit.tag.util.ComponentStyleUtil;
import com.wisii.edit.tag.util.LocationUtil;
import com.wisii.edit.tag.util.ComponentStyleUtil.ComponentType;
import com.wisii.edit.tag.util.LocationUtil.IdInfo;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.BeforeFloat;
import com.wisii.fov.area.Block;
import com.wisii.fov.area.BlockViewport;
import com.wisii.fov.area.BodyRegion;
import com.wisii.fov.area.CTM;
import com.wisii.fov.area.Footnote;
import com.wisii.fov.area.LineArea;
import com.wisii.fov.area.MainReference;
import com.wisii.fov.area.NormalFlow;
import com.wisii.fov.area.OffDocumentItem;
import com.wisii.fov.area.Page;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.RegionReference;
import com.wisii.fov.area.RegionViewport;
import com.wisii.fov.area.Span;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.Container;
import com.wisii.fov.area.inline.ForeignObject;
import com.wisii.fov.area.inline.Image;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.InlineBlockParent;
import com.wisii.fov.area.inline.InlineParent;
import com.wisii.fov.area.inline.Leader;
import com.wisii.fov.area.inline.QianZhangArea;
import com.wisii.fov.area.inline.Space;
import com.wisii.fov.area.inline.SpaceArea;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.area.inline.Viewport;
import com.wisii.fov.area.inline.WordArea;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.properties.WisedocColor;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.layoutmgr.inline.TextLayoutManager;
import com.wisii.fov.render.java2d.FovImageFilter;
import com.wisii.fov.render.java2d.Java2DRenderer;
import com.wisii.fov.traits.BorderProps;

/**
 * Abstract base class for all renderers. The Abstract renderer does all the
 * top level processing of the area tree and adds some abstract methods to
 * handle viewports. This keeps track of the current block and inline position.
 */
public abstract class AbstractRenderer implements Renderer, Configurable, Constants
{
    /** logging instance */
    protected static Log log = LogFactory.getLog("com.wisii.fov.render");

    /** user agent    */
    protected FOUserAgent userAgent = FovFactory.newInstance().newFOUserAgent();

    /** block progression position     */
    protected int currentBPPosition = 0;
    /** inline progression position     */
    protected int currentIPPosition = 0;
    /** the block progression position of the containing block used for absolutely positioned blocks    */
    protected int containingBPPosition = 0;
    /** the inline progression position of the containing block used for absolutely positioned blocks     */
    protected int containingIPPosition = 0;

    protected int parentBlockLength = 0; // TextArea所在的Block的显示区域的宽度
    // add by huangzl.
//    protected float parentBlockRecX = 0.0f; // TextArea的上层节点（Block）的显示区域的左上角的X坐标
//    protected float parentBlockRecY = 0.0f; // TextArea的上层节点（Block）的显示区域的左上角的Y坐标
    protected float parentIndent = 0.0f; // TextArea的缩进值
//    protected boolean isTextAlign = false; // true:缩进类型是text-align    
    // add end.
    protected boolean _multiTextFlag;

    /** the currently active PageViewport */
    protected PageViewport currentPageViewport;

    private Set warnedXMLHandlers;

    //addby 许浩 全局变量，存放当前绘制的ViewPort的显示区域
    protected static Rectangle2D _currentViewportArea;
//    addby zq打印机名称
    protected PrintRef printref;
//    打印信息
    protected Map table;
    
    /* 【添加：START】 by 李晓光  2009-7-6 */
    /*enum ComponentType{
    	//表示非复合控件
    	None,
    	//表示复合控件
    	Composite,
    	//具备复合控件的条件，但不具备权限，故不创建任何控件。
    	Nomal
    }*/
    private final static int WIDTH = 50;
	private ComponentType type = ComponentType.Normal;
    private Set<String> ids = new HashSet<String>();
    private WdemsCompositeComponent compositeComp = null;
    protected int imageWidth = 0;
    protected int imageHeight = 0;
    protected Map<JComponent, Rectangle> editors = new HashMap<JComponent, Rectangle>();
    protected Stack<Area> views = new Stack<Area>();
    protected boolean isOverflow = Boolean.FALSE;
    protected JTextArea textArea = null;
    /* 用于保存当前的FO-Block */
    protected FONode curBlock = null;
/*    private Boolean editing = Boolean.FALSE;
    public Boolean isEditing() {
    	return editing;
    }
    
    public void setEditing(Boolean editing) {
    	this.editing = editing;
    }*/
    /* 【添加：END】 by 李晓光  2009-7-6 */


	/** @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)     */
    public void configure(Configuration conf) throws ConfigurationException
    {
    }

    /** @see com.wisii.fov.render.Renderer#setupFontInfo(FontInfo)     */
    public abstract void setupFontInfo(FontInfo fontInfo);

    /**  @see com.wisii.fov.render.Renderer#setUserAgent(FOUserAgent)     */
    public void setUserAgent(FOUserAgent agent)
    {
    	
        userAgent = agent;
    }

    public FOUserAgent getUserAgent()
    {
        return userAgent;
    }


    /** @see com.wisii.fov.render.Renderer#startRenderer(OutputStream) */
    public void startRenderer(OutputStream outputStream) throws IOException
    {}

    /** @see com.wisii.fov.render.Renderer#stopRenderer() */
    public void stopRenderer() throws IOException
    {}

    /**
     * Check if this renderer supports out of order rendering. If this renderer
     * supports out of order rendering then it means that the pages that are
     * not ready will be prepared and a future page will be rendered.
     * @return   True if the renderer supports out of order rendering
     * @see      com.wisii.fov.render.Renderer
     */
    public boolean supportsOutOfOrder()
    {
        return false;
    }

    /** @see     com.wisii.fov.render.Renderer#processOffDocumentItem(OffDocumentItem)     */
    public void processOffDocumentItem(OffDocumentItem oDI)
    {}

    /** @see com.wisii.fov.render.Renderer#getGraphics2DAdapter() */
    public Graphics2DAdapter getGraphics2DAdapter()
    {
        return null;
    }

    /** @see com.wisii.fov.render.Renderer#getImageAdapter() */
    public ImageAdapter getImageAdapter()
    {
        return null;
    }

    /** @return the current PageViewport or null, if none is active */
    protected PageViewport getCurrentPageViewport()
    {
        return this.currentPageViewport;
    }

    /**
     * Prepare a page for rendering. This is called if the renderer supports out of order rendering. The renderer should
     * prepare the page so that a page further on in the set of pages can be rendered. The body of the page should not
     * be rendered. The page will be rendered at a later time by the call to render page.
     * @see com.wisii.fov.render.Renderer#preparePage(PageViewport)
     */
    public void preparePage(PageViewport page)
    {}

    /**
     * Utility method to convert a page sequence title to a string. Some
     * renderers may only be able to use a string title. A title is a sequence
     * of inline areas that this method attempts to convert to an equivalent string.
     * @param title  The Title to convert
     * @return       An expanded string representing the title
     */
    protected String convertTitleToString(LineArea title)
    {
        List children = title.getInlineAreas();
        String str = convertToString(children);
        return str.trim();
    }

    private String convertToString(List children)
    {
        StringBuffer sb = new StringBuffer();
        for(int count = 0; count < children.size(); count++)
        {
            InlineArea inline = (InlineArea)children.get(count);
            //if (inline instanceof Character)
            //    sb.append(((Character) inline).getChar());
            /*else*/if(inline instanceof TextArea)
            {
                sb.append(((TextArea)inline).getText());
            }
            else if(inline instanceof InlineParent)
            {
                sb.append(convertToString(((InlineParent)inline).getChildAreas()));
            }
            else
            {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /** @see com.wisii.fov.render.Renderer#startPageSequence(LineArea) */
    public void startPageSequence(LineArea seqTitle)
    {
        //do nothing
    }

    // normally this would be overriden to create a page in the output
    /** @see com.wisii.fov.render.Renderer#renderPage(PageViewport) */
    public void renderPage(PageViewport page) throws IOException, FOVException
    {
        this.currentPageViewport = page;
        try
        {
            Page p = page.getPage();
            renderPageAreas(p);
        }
        finally
        {
            this.currentPageViewport = null;
        }
    }

    /**
     * Renders page areas.
     * @param page  The page whos page areas are to be rendered
     */
    protected void renderPageAreas(Page page)
    {
        /* Spec does not appear to specify whether fo:region-body should appear above or below side regions in cases of
         * overlap.  FOV decision is to have fo:region-body on top, hence it is rendered last here. */
        //增加给_currentViewportArea赋值的语句
        RegionViewport viewport;
        viewport = page.getRegionViewport(FO_REGION_BEFORE);
        if(viewport != null)
        {
            _currentViewportArea = viewport.getViewArea();
        }
        renderRegionViewport(viewport);

        viewport = page.getRegionViewport(FO_REGION_START);
        if(viewport != null)
        {
            _currentViewportArea = viewport.getViewArea();
        }
        renderRegionViewport(viewport);

        viewport = page.getRegionViewport(FO_REGION_END);
        if(viewport != null)
        {
            _currentViewportArea = viewport.getViewArea();
        }
        renderRegionViewport(viewport);

        viewport = page.getRegionViewport(FO_REGION_AFTER);
        if(viewport != null)
        {
            _currentViewportArea = viewport.getViewArea();
        }
        renderRegionViewport(viewport);

        viewport = page.getRegionViewport(FO_REGION_BODY);
        if(viewport != null)
        {
            _currentViewportArea = viewport.getViewArea();
        }
        renderRegionViewport(viewport);
    }

    /**
     * Renders a region viewport. <p>
     * The region may clip the area and it establishes a position from where the region is placed.</p>
     * @param port  The region viewport to be rendered
     */
    protected void renderRegionViewport(RegionViewport port)
    {
        if(port != null)
        {
            Rectangle2D view = port.getViewArea();
            // The CTM will transform coordinates relative to
            // this region-reference area into page coords, so
            // set origin for the region to 0,0.
            currentBPPosition = 0;
            currentIPPosition = 0;

            RegionReference regionReference = port.getRegionReference();
            handleRegionTraits(port);
            startVParea(regionReference.getCTM(), port.isClip() ? view : null);
            // do after starting viewport area
            if(regionReference.getRegionClass() == FO_REGION_BODY)
            {
                renderBodyRegion((BodyRegion)regionReference);
            }
            else
            {
                renderRegion(regionReference);
            }
            endVParea();
        }
    }

    /**
     * Establishes a new viewport area.
     * @param ctm the coordinate transformation matrix to use
     * @param clippingRect the clipping rectangle if the viewport should be clipping, null if no clipping is performed.
     */
    protected abstract void startVParea(CTM ctm, Rectangle2D clippingRect);

    /** Signals exit from a viewport area. Subclasses can restore transformation matrices valid before the viewport area was started.     */
    protected abstract void endVParea();

    /**
     * Handle the traits for a region. This is used to draw the traits for the given page region.
     * (See Sect. 6.4.1.2 of XSL-FO spec.)
     * @param rv the RegionViewport whose region is to be drawn
     */
    protected void handleRegionTraits(RegionViewport rv)
    {
        // draw border and background
    }

    /**
     * Renders a region reference area.
     * @param region  The region reference area
     */
    protected void renderRegion(RegionReference region)
    {
        List blocks = region.getBlocks();
        renderBlocks(null, blocks);
    }

    /**
     * Renders a body region area.
     * @param region  The body region
     */
    protected void renderBodyRegion(BodyRegion region)
    {
        BeforeFloat bf = region.getBeforeFloat();
        if(bf != null)
        {
            renderBeforeFloat(bf);
        }
        MainReference mr = region.getMainReference();
        if(mr != null)
        {
            renderMainReference(mr);
        }
        Footnote foot = region.getFootnote();
        if(foot != null)
        {
            renderFootnote(foot);
        }
    }

    /**
     * Renders a before float area.
     * @param bf  The before float area
     */
    protected void renderBeforeFloat(BeforeFloat bf)
    {
        List blocks = bf.getChildAreas();
        if(blocks != null)
        {
            renderBlocks(null, blocks);
            Block sep = bf.getSeparator();
            if(sep != null)
            {
                renderBlock(sep);
            }
        }
    }

    /**
     * Renders a footnote
     * @param footnote  The footnote
     */
    protected void renderFootnote(Footnote footnote)
    {
        currentBPPosition += footnote.getTop();
        List blocks = footnote.getChildAreas();
        if(blocks != null)
        {
            Block sep = footnote.getSeparator();
            if(sep != null)
            {
                renderBlock(sep);
            }
            renderBlocks(null, blocks);
        }
    }

    /**
     * Renders the main reference area.
     * <p>
     * The main reference area contains a list of spans that are stacked on the page.
     * The spans contain a list of normal flow reference areas that are positioned into columns.
     * </p>
     * @param mr  The main reference area
     */
    protected void renderMainReference(MainReference mr)
    {
        int saveIPPos = currentIPPosition;

        Span span = null;
        List spans = mr.getSpans();
        int saveBPPos = currentBPPosition;
        int saveSpanBPPos = saveBPPos;
        for(int count = 0; count < spans.size(); count++)
        {
            span = (Span)spans.get(count);
            for(int c = 0; c < span.getColumnCount(); c++)
            {
                NormalFlow flow = span.getNormalFlow(c);
                if(flow != null)
                {
                    currentBPPosition = saveSpanBPPos;
                    renderFlow(flow);
                    currentIPPosition += flow.getIPD();
                    currentIPPosition += mr.getColumnGap();
                }
            }
            currentIPPosition = saveIPPos;
            currentBPPosition = saveSpanBPPos + span.getHeight();
            saveSpanBPPos = currentBPPosition;
        }
        currentBPPosition = saveBPPos;
    }

    /**
     * Renders a flow reference area.
     * @param flow  The flow reference area
     */
    protected void renderFlow(NormalFlow flow)
    {
        // the normal flow reference area contains stacked blocks
        List blocks = flow.getChildAreas();
        if(blocks != null)
        {
            renderBlocks(null, blocks);
        }
    }

    /**
     * Handle block traits. This method is called when the correct ip and bp posiiton is set. This should be overridden
     * to draw border and background traits for the block area.
     * @param block the block area
     */
    protected void handleBlockTraits(Block block)
    {
        // draw border and background
    }

    /**
     * Renders a block viewport.
     * @param bv        The block viewport
     * @param children  The children to render within the block viewport
     */
    protected void renderBlockViewport(BlockViewport bv, List children)
    {
    
        // clip and position viewport if necessary
        if(bv.getPositioning() == Block.ABSOLUTE)
        {
            // save positions
            int saveIP = currentIPPosition;
            int saveBP = currentBPPosition;

            Rectangle2D clippingRect = null;
            if(bv.getClip())
            {
                clippingRect = new Rectangle(saveIP, saveBP, bv.getIPD(), bv.getBPD());
            }

            CTM ctm = bv.getCTM();
            currentIPPosition = 0;
            currentBPPosition = 0;

            startVParea(ctm, clippingRect);
            
            handleBlockTraits(bv);
            renderBlocks(bv, children);
            endVParea();

            // clip if necessary
            currentIPPosition = saveIP;
            currentBPPosition = saveBP;
        }
        else
        {
            // save position and offset
            int saveIP = currentIPPosition;
            int saveBP = currentBPPosition;

            handleBlockTraits(bv);
            renderBlocks(bv, children);

            currentIPPosition = saveIP;
            currentBPPosition = saveBP + bv.getAllocBPD();
        }
    }

    /**
     * Renders a list of block areas.
     * @param parent  the parent block if the parent is a block, otherwise a null value.
     * @param blocks  The block areas
     */
    @SuppressWarnings("serial")
	protected void renderBlocks(Block parent, List blocks)
    {
        int saveIP = currentIPPosition;
        // Calculate the position of the content rectangle.
        if(parent != null && !Boolean.TRUE.equals(parent.getTrait(Trait.IS_VIEWPORT_AREA)))
        {
            currentBPPosition += parent.getBorderAndPaddingWidthBefore();
        }

        // the position of the containing block is used for absolutely positioned areas
        int contBP = currentBPPosition;
        int contIP = currentIPPosition;
        containingBPPosition = currentBPPosition;
        containingIPPosition = currentIPPosition;

        // add by xuhao
        if(parent != null)
        {
            parentBlockLength = parent.getIPD(); // mod by huangzl.只获取内容的宽度,不需要borders 和 padding的宽度，否则控件可能覆盖Block的边框。
       }
        // add end
        /* 【添加：START】 by 李晓光	2009-7-6 */
        /*if(EditStatusControl.RUNSTATUS == EditStatusControl.STATUS.WRITE && parent != null && !parent.isReferrenceArea())*/ 
        if(canUpdate(parent)){
			update(parent);
		}
        if(EditStatusControl.RUNSTATUS == EditStatusControl.STATUS.WRITE && parent != null && parent.isEqualViewport()){
        	views.push(parent);
        	isOverflow = parent.isOverflow();
        	if(isOverflow){
        		textArea = new JTextArea(){
        			@Override
        			protected void paintComponent(Graphics g) {
        				Graphics2D graphics = (Graphics2D)g;
        				graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());
        				
            			super.paintComponent(graphics);
        			}
        		};
        		textArea.setEditable(Boolean.FALSE);
        	}
        }
        
        if(blocks!=null){
        /* 【添加：END】 by 李晓光	2009-7-6 */
        for(int count = 0; count < blocks.size(); count++)
        {
            Object obj = blocks.get(count);
            if(obj instanceof Block)
            {
                currentIPPosition = contIP;
                containingBPPosition = contBP;
                containingIPPosition = contIP;
                renderBlock((Block)obj);
                containingBPPosition = contBP;
                containingIPPosition = contIP;
            }
            else
            {
                // a line area is rendered from the top left position
                // of the line, each inline object is offset from there
                LineArea line = (LineArea)obj;
                currentIPPosition = contIP + parent.getStartIndent() + line.getStartIndent();
                // mod.因为parentBlockLength的值为Block内容的宽度，即getIPD()，而不是getAllocIPD，所以不用考虑start-indent的情况
//                float startIndent = line.getStartIndent();
//                if(startIndent > 0.0)
//                {
//                    isTextAlign = true;
//                    parentIndent = startIndent; // add by huangzl.处理属性：text-align
//                }
                parentIndent = line.getStartIndent();
                // mod end.
                renderLineArea(line);
                //InlineArea child = (InlineArea) line.getInlineAreas().get(0);
                currentBPPosition += line.getAllocBPD();
            }
            currentIPPosition = saveIP;
        }
        }
        /* 【添加：START】 by 李晓光	2009-7-6 */
       /* if(EditStatusControl.RUNSTATUS == EditStatusControl.STATUS.WRITE && parent != null && !parent.isReferrenceArea())*/ 
        if(canUpdate(parent)){
			reset();
        }
        if(EditStatusControl.RUNSTATUS != EditStatusControl.STATUS.WRITE)
        	return;
        if(!views.isEmpty() && views.peek() == parent){//parent != null && parent.isEqualViewport()
        	updateRect(parent);
        	WdemsEditComponentManager.addEditors(editors);
        	WdemsCascadeManager.registerComponents(editors.keySet());
        	editors.clear();
        	views.pop();
        	isOverflow = Boolean.FALSE;
        	textArea = null;
        }else if(views.isEmpty()){
        	WdemsEditComponentManager.addEditors(editors);
        	WdemsCascadeManager.registerComponents(editors.keySet());
        	editors.clear();
        }
        
        /* 【添加：END】 by 李晓光	2009-7-6 */
    }
    /* 调整最后一个Block的高度，以占满剩余高度 */
    private void updateRect(Block block){
    	//处理溢出的Block
    	processOverflow(block);
    	
    	JComponent last = null;
    	int minY = 0;
    	for (Map.Entry<JComponent, Rectangle> entry : editors.entrySet()) {
			if(entry.getValue().y > minY){
				last = entry.getKey();
				minY = entry.getValue().y;
			}
		}
    	if(!(last instanceof JScrollPane))
    		return;
    	
    	Rectangle2D rect = block.getViewport().getBounds();
    	Java2DRenderer render = (Java2DRenderer)this;
    	AffineTransform at = render.getState().getTransform();
    	
    	double[] offset = LocationUtil.getOffsetForNestedArea(block, false);
    	
    	double y = rect.getY() + offset[1];
    	y *= at.getScaleY();
    	double h = /*rect.getHeight()*/block.getViewport().getHeight() * at.getScaleY();
    	double border = block.getBorderAfter();
    	border += block.getBorderBefore();
    	if(border != .0F){
    		border /= 1000F;
        	border *= at.getScaleY();
    	}
    	
    	Rectangle r = editors.get(last);
    	double different = r.getY() - y;
    	different = Math.max(0, different);
    	different = h - different - r.getHeight();
    	different -= border;
    	different = Math.max(0, different);
    	
    	if(different <= 0)
    		return;
    	
    	r.setRect(r.x, r.y, r.width, r.height + different);
    }

    /* 溢出处理 */
    private void processOverflow(Block block){
    	if(!block.isOverflow() || editors.isEmpty())
    		return;
    	Rectangle rect = block.getViewport().getBounds();
    	JPanel panel = new JPanel();
    	panel.setOpaque(false);
		/*panel.setBorder(BorderFactory.createLineBorder(Color.red));*/
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		int x = Short.MAX_VALUE;
		int y = Short.MAX_VALUE;
		List<JComponent> comps = new ArrayList<JComponent>();
		for (JComponent c : editors.keySet()) {
			c.setBounds(editors.get(c));
			comps.add(c);
		}
		Comparator<JComponent> able = new Comparator<JComponent>(){
			public int compare(JComponent o1, JComponent o2) {
				return (o1.getBounds().y - o2.getBounds().y );
			}
		};
		Collections.sort(comps, able);
		x = comps.get(0).getBounds().x;
		y = comps.get(0).getBounds().y;
		for (JComponent c : comps) {
			panel.add(c);
		}
		Java2DRenderer render = (Java2DRenderer) this;
		AffineTransform at = render.getState().getTransform();
		Rectangle r = new Rectangle(x, y, (int) (rect.width * at
				.getScaleX()), (int) (rect.height * at.getScaleY()));
		editors.clear();
		JScrollPane pane = new WdemsScrollPane(panel);
		pane.setBorder(null);
		editors.put(pane, r);
    }
    
    protected void drawTestBorder(Rectangle2D.Float r, Color c){
    	Java2DRenderer render = (Java2DRenderer)this;
    	BorderProps border = getBorderStyle(c);
    	render.drawBorders(r, border, border, border, border);
    }
    private BorderProps getBorderStyle(Color c){
    	return new BorderProps(Constants.EN_SOLID, 1000, c, BorderProps.SEPARATE);
    }
    /* 【添加：START】 by 李晓光	2009-7-6 */
    /* 判断Render指定的Block是否要重新判断其创建编辑控件的类型。 */
    private boolean canUpdate(Block block){
    	/* 该处理已经方式已经过时，现正删除。 */
    	/*if(false){
    		if((block == null) || (EditStatusControl.RUNSTATUS != EditStatusControl.STATUS.WRITE) || block.isReferrenceArea() || block.isEqualViewport())
    			return Boolean.FALSE;
    		
    		if(curBlock == null){
    			curBlock = block.getSource();
    			return Boolean.TRUE;
    		}else if(curBlock != block.getSource()){
    			curBlock = block.getSource();
    			return Boolean.TRUE;
    		}else
    			return Boolean.FALSE;
    		
    	}*/
    	if(block == null)
    		return Boolean.FALSE;
    	List<Area> areas = block.getChildAreas();
    	return (block != null) && (EditStatusControl.RUNSTATUS == EditStatusControl.STATUS.WRITE)
    	&& (areas != null) && (areas.get(0) instanceof LineArea)
    	&& !block.isReferrenceArea() && !block.isEqualViewport();
    	/*boolean flag = (EditStatusControl.RUNSTATUS == EditStatusControl.STATUS.WRITE);
    	if(!flag)
    		return flag;
    	flag &= (areas != null);
    	if(!flag)
    		return flag;
    	flag &= (areas.get(0) instanceof LineArea);
    	if(!flag)
    		return flag;
    	flag &= !block.isReferrenceArea();
    	if(!flag)
    		return flag;
    	flag &= !block.isEqualViewport();
    	
    	return flag;*/
    }
    private void update(Block block){
    	 /*ids = LocationUtil.getAllEditID(block);*/
    	 IdInfo info = LocationUtil.getAllIDInfo(block);
    	 ids = info.getEditID();
    	 type = ComponentStyleUtil.getComponentType(info,userAgent);
    }

    /* 2009-9-14 14:45:13 */
   
    private void reset(){
    	type = ComponentType.Normal;
    	compositeComp = null;
    	ids.clear();
    }
    /* 【添加：END】 by 李晓光	2009-7-6 */
    
    /**
     * Renders a block area.
     * @param block  The block area
     */
    protected void renderBlock(Block block)
    {
        List children = block.getChildAreas();
        if(block instanceof BlockViewport)
        {
//            if(children != null)
//            {
                renderBlockViewport((BlockViewport)block, children);
//            }
//            else
//            {
//                handleBlockTraits(block);
//                // simply move position
//                currentBPPosition += block.getAllocBPD();
//            }
        }
        else
        {
            // save position and offset
            int saveIP = currentIPPosition;
            int saveBP = currentBPPosition;
            if(block.getPositioning() == Block.ABSOLUTE)
            {
                currentIPPosition += block.getXOffset();
                currentBPPosition += block.getYOffset();
                currentBPPosition += block.getSpaceBefore();

                handleBlockTraits(block);
                if(children != null)
                {
                    renderBlocks(block, children);
                }
                // absolute blocks do not effect the layout
                currentBPPosition = saveBP;
            }
            else
            {
                // relative blocks are offset
                currentIPPosition += block.getXOffset();
                currentBPPosition += block.getYOffset();
                currentBPPosition += block.getSpaceBefore();

                handleBlockTraits(block);

                if(children != null)
                {
                    renderBlocks(block, children);
                }

                // stacked and relative blocks effect stacking
                currentIPPosition = saveIP;
                currentBPPosition = saveBP + block.getAllocBPD();
            }
        }
    }

    /**
     * Renders a line area. <p>
     * A line area may have grouped styling for its children such as underline, background.</p>
     * @param line  The line area
     */
    protected void renderLineArea(LineArea line)
    {
        List children = line.getInlineAreas();
        int saveBP = currentBPPosition;
        currentBPPosition += line.getSpaceBefore();

        //add by xuhao
        int textCount = getInlineCount(children);
        if(textCount > 1)
        {
            _multiTextFlag = true;
        }
        else
        {
            _multiTextFlag = false;
        }
        //add end

        for(int count = 0; count < children.size(); count++)
        {
            InlineArea inline = (InlineArea)children.get(count);
            renderInlineArea(inline);
        }
        currentBPPosition = saveBP;
    }

    private int getInlineCount(List children)
    {
        int count = 0;
        for(int i = 0; i < children.size(); i++)
        {
            Object obj = children.get(i);
            if(obj instanceof InlineParent)
            {
                count++;
            }
        }
        return count;
    }
    /**
     * Render the given InlineArea.
     * @param inlineArea inline area text to render
     */
    protected void renderInlineArea(InlineArea inlineArea)
    {
    	/*if(inlineArea.getID() != ""){
    		Java2DRenderer render = (Java2DRenderer)this;
    		AffineTransform at = render.getState().getTransform();    		
    		Rectangle2D r = LocationUtil.getScaleRectangle(inlineArea, at.getScaleX(), at.getScaleY());
    		List<WdemsComponent> comp = (List<WdemsComponent>)WdemsTagManager.Instance.getWdemsComponent(inlineArea.getID());
    		JComponent c = comp.get(0).getWdemsTagComponent().getComponent();    
    		ImageProxyPanel.EDITOR_COMPONENTS.put(c, r.getBounds());
    		return;
    	}*/
    	if((EditStatusControl.RUNSTATUS==EditStatusControl.STATUS.WRITE) && createComonent(inlineArea)){
    		/*currentIPPosition += inlineArea.getAllocIPD();*/
    		return;
    	}
    	
        if(inlineArea instanceof TextArea)
        {
            renderText((TextArea)inlineArea);
        }
        //else if (inlineArea instanceof Character)
        //renderCharacter((Character) inlineArea);
        else if(inlineArea instanceof WordArea)
        {
            renderWord((WordArea)inlineArea);
        }
        else if(inlineArea instanceof SpaceArea)
        {
            renderSpace((SpaceArea)inlineArea);
        }
        else if(inlineArea instanceof InlineParent)
        {
            renderInlineParent((InlineParent)inlineArea);
        }
        else if(inlineArea instanceof InlineBlockParent)
        {
            renderInlineBlockParent((InlineBlockParent)inlineArea);
        }
        else if(inlineArea instanceof Space)
        {
            renderInlineSpace((Space)inlineArea);
        }
        else if(inlineArea instanceof Viewport)
        {
            renderViewport((Viewport)inlineArea);
        }
        else if(inlineArea instanceof Leader)
        {
            renderLeader((Leader)inlineArea);
        }
    }
    /* 【添加：START】 by 李晓光	2009-7-6 */
    private boolean createComonent(InlineArea inline){
    	if(type == ComponentType.Normal){
    		if(isOverflow){
    			return createStaticContent(inline);
    		}
    		return Boolean.FALSE;
    	}
    	String id = inline.getID();
    	/*if(id != null && id.equals(idBefore)){
    		return Boolean.TRUE;
    	}
    	idBefore = id;*/
    	WdemsEditComponentManager.setPageWidth(imageWidth);
    	WdemsEditComponentManager.setPageHeight(imageHeight);
    	
    	if(type == ComponentType.Composite){
    		if(id != "" && !ids.contains(id))
    			return Boolean.TRUE;
    		ids.remove(id);
    		return createCompositeComponent(inline);
    	}else if(id != ""){
    		/*if(id != "" && !ids.contains(id))
    			return Boolean.TRUE;
    		ids.remove(inline.getID());*/
			return buildComponent(inline);
    	}else
    		return Boolean.FALSE;
    }
    @SuppressWarnings("serial")
	private Boolean createStaticContent(InlineArea inline){
    	Java2DRenderer render = (Java2DRenderer)this;
    	AffineTransform at = render.getState().getTransform();
    	String text = getContent(inline);
		text = filterSpace(text);
		/*JTextField c = new JTextField(text){
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D graphics = (Graphics2D)g;
				graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());
				
    			super.paintComponent(graphics);
			}
		};*/
		/*c.setEditable(Boolean.FALSE);*/
		textArea.append(text);
		/** 放大处理 */
		zoomComponent(textArea, inline);
		Rectangle2D r = LocationUtil.getScaleRectangle(inline, at.getScaleX(), at.getScaleY()); 
		editors.put(textArea, r.getBounds());
		return Boolean.TRUE;
    }
    @SuppressWarnings("serial")
	private Boolean createCompositeComponent(InlineArea inline){
    	Java2DRenderer render = (Java2DRenderer)this;
    	AffineTransform at = render.getState().getTransform();
    	LineArea line = getLineArea(inline);
    	Dimension dim = new Dimension((int)(inline.getIPD() * at.getScaleX() / 1000F), (int)(line.getAllocBPD() * at.getScaleY() / 1000F));
    	JComponent c = null;
    	if(inline.getID() != ""){
    		List<WdemsComponent> comps = WdemsTagManager.Instance.getWdemsComponent(inline.getID(),userAgent);
    		if(comps.size() == 0)
    			return Boolean.FALSE;
    		final List<VirtualButton> options = new ArrayList<VirtualButton>();
    		c = filterList(comps, options);//comps.get(0).getWdemsTagComponent().getComponent();
    		if(c == null && isNullInline(inline)){
    			c = NullInlineFactory.Instance.makeComponent(null).getComponent();
    			c.setAlignmentY(.0F);
    		}
    		registerOptions(c, options);
    		
    		addLis(c);
    		/** 放大处理 */
    		zoomComponent(c, inline);
    		if(c.isPreferredSizeSet())
    			c.setPreferredSize(null);
    		Dimension d = c.getPreferredSize();
    		if(c instanceof JTextComponent){
    			JTextComponent text = (JTextComponent)c;
    			String t = text.getText();
    			if(t == null || "".equals(t)){
    				if(d.width < WIDTH) {
						d = new Dimension(WIDTH, d.height);
					} else {
					}
    			}else{
    				
    			}
    		}else if(c instanceof AbstractWdemsCombox || c instanceof JComboBox){
    			int height =  (int)LocationUtil.getLengthToScale(d.height, at.getScaleY());
    			d.setSize(dim.width + height, height);
    		}else if(c instanceof JCheckBox){
    			processGroup(comps);
    		}
    		if(c instanceof JScrollPane){
    			
    		}else{
    			c.setMinimumSize(d);
    			c.setPreferredSize(d);
        		c.setMaximumSize(d);
    		}
    		c.setAlignmentY(0.75F);
    	}else {
    		
    		String text = getContent(inline);
    		text = filterSpace(text);
    		c = new JLabel(text){
    			@Override
    			protected void paintComponent(Graphics g) {
    				Graphics2D graphics = (Graphics2D)g;
    				graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());
    				
        			super.paintComponent(graphics);
    			}
    		};
    		
    		/** 删除，如果设置最小值，滚动条会闪烁。 */
    		c.setMinimumSize(dim);
    		/*c.setPreferredSize(dim);*/
    		/** 放大处理 */
    		zoomComponent(c, inline);
    		c.setAlignmentY(0.75F);
    	}
    	
    	if(compositeComp == null){
    		compositeComp = new WdemsCompositeComponent();
    		Rectangle2D r = LocationUtil.getScaleRectangle(inline, at.getScaleX(), at.getScaleY()); 
    		
    		JScrollPane pane = new WdemsScrollPane(compositeComp.getComponent());    		
    		pane.setBorder(null);
    		editors.put(pane, r.getBounds());
    	}
    	
    	
    	compositeComp.addComponent(c);
    	    	    	
    	return Boolean.TRUE;
    }
    private boolean isNullInline(InlineArea area){
    	List<Area> areas = area.getChildAreas();
    	
    	return ((area.getClass() == InlineParent.class) && areas.isEmpty());
    }
    private JComponent filterList(final List<WdemsComponent> source, final List<VirtualButton> options){
    	JComponent comp = null;
    	for (Iterator<WdemsComponent> iterator = source.listIterator(); iterator.hasNext();) {
			WdemsComponent c = iterator.next();
			if (c.getWdemsTagComponent() instanceof VirtualButton) {
				VirtualButton vb = (VirtualButton) c.getWdemsTagComponent();
				if(options != null){
					options.add(vb);
					iterator.remove();
				}
			}else{
				comp = c.getWdemsTagComponent().getComponent();
			}	
		}
		
    	return comp;
    }
    private void registerOptions(final JComponent c, final List<VirtualButton> options){
    	WdemsOperationManager.registerComponent(c, options);
    }
    private String filterSpace(String s){
    	StringBuilder builder = new StringBuilder();
    	boolean isChinese = Boolean.FALSE;
    	for (char c : s.toCharArray()) {
    		if(isChinese && c == ' '){
    			isChinese = Boolean.FALSE;
    			continue;
    		}
    		if(TextLayoutManager.isChineseCharacters(c) || TextLayoutManager.isPunctuation(c))
    			isChinese = Boolean.TRUE;
    		else
    			isChinese = Boolean.FALSE;
			builder.append(c);
		}
    	return builder.toString().trim();
    }
    private boolean buildComponent(InlineArea inline){
    	Java2DRenderer render = (Java2DRenderer)this;
    	
		AffineTransform at = render.getState().getTransform();    		
		
		List<WdemsComponent> comp = WdemsTagManager.Instance.getWdemsComponent(inline.getID(),userAgent);
		if(comp.size() == 0)
			return Boolean.FALSE;
		final List<VirtualButton> options = new ArrayList<VirtualButton>();
		JComponent c = filterList(comp, options);//comp.get(0).getWdemsTagComponent().getComponent();
		Boolean flag = Boolean.FALSE;
		if(c == null && options.size() > 0){
			c = NullInlineFactory.Instance.makeComponent(null).getComponent();
			flag = Boolean.TRUE;
		}
		if(c == null && isNullInline(inline)){
			c = NullInlineFactory.Instance.makeComponent(null).getComponent();
		}
		if(c == null){
			return Boolean.FALSE;
		}
		registerOptions(c, options);
		zoomComponent(c, inline);
		Rectangle2D r = null;
		if(c instanceof JCheckBox){
			r = LocationUtil.getScaleInlineRectangle(inline, at.getScaleX(), at.getScaleY());
			
			processGroup(comp);			
		}else  {
			r = LocationUtil.getScaleRectangle(inline, at.getScaleX(), at.getScaleY());
			r = getSingleDime(c, r, inline, at);
		}
		
		if(!editors.containsKey(c))
			editors.put(c, r.getBounds());
		if(flag){
			return Boolean.FALSE;			
		}
		return Boolean.TRUE;
    }
    private Rectangle2D getSingleDime(JComponent c, Rectangle2D rect, InlineArea inline, AffineTransform at){
    	Dimension pre = c.getPreferredSize();
    	if((c instanceof JComboBox) || (c instanceof AbstractWdemsCombox))
    		return isCombox(c, rect, inline);
    	else if(c instanceof JTextField){    		
    		double x = rect.getX() + inline.getOffset() / 1000F;    		
    		double w = Math.min(pre.width, rect.getWidth()/*LocationUtil.getLengthToScale(inline.getAllocIPD() / 1000F, at.getScaleX())*/);
    		double h = pre.height;
    		
    		/*rect.setRect(x, rect.getY(), w, h);*/
    		return rect;
    	}else if(c instanceof JTextComponent){
    		return rect;
    	}else if(c instanceof NullInlineImp){
    		double offset = NullInlineIndicator.CIRCLE_DIAMETER / 2;
    		rect.setRect(rect.getX() - offset, rect.getY() - offset * 2, rect.getWidth(), rect.getHeight() + offset);
    		
    		return rect;
    	}else
    		return rect;
    }
    private Rectangle2D isCombox(JComponent c, Rectangle2D rect, InlineArea inline){
    	if(!(c instanceof JComboBox) && !(c instanceof AbstractWdemsCombox))
    		return rect;
    	Java2DRenderer render = (Java2DRenderer)this;
		AffineTransform at = render.getState().getTransform(); 
		Rectangle r = rect.getBounds();
    	Dimension dim = c.getPreferredSize();
    	
    	double height = LocationUtil.getLengthToScale(dim.height, at.getScaleY());
    	
    	double width = 0;
    	
    	//单体控件在创建是取TextArea的宽度，这样更精确。
    	List<Area> areas = inline.getChildAreas();
    	for (Area text : areas) {
    		width += text.getAllocIPD() * at.getScaleX() / 1000F;
    		height = text.getAllocBPD() * at.getScaleY() / 1000F;
		}
    	
    	int x = r.x + (int)(inline.getOffset() / 1000F);
    	int y = r.y;
    	double h = LocationUtil.getLengthToScale(dim.height, at.getScaleY());
    	height = Math.min(rect.getHeight(), height);
    	if(height > h){
    		height = h;
    	}
    	if(width == 0)
    		width = height;
    	return new Rectangle2D.Double(x, y, width + height, height);
    }
    /* 
     * 如果返回的控件是复选框，则其制定的Group可能是在ID中指定，也有可能是在Checkbox标签中的Group-Reference中指定
     * 也有可能没有组的限制。
     * 该方法提供了处理组的处理。 
     */
    private void processGroup(List<WdemsComponent> comps){
    	JComponent c = comps.get(0).getWdemsTagComponent().getComponent();
    	if(!(c instanceof JCheckBox))
    		return;
    	SelectGroup group = null;
		if(comps.size() > 1) {
			WdemsComponent temp = findGroupTag(comps);
			if(temp != null){
				group = getGroup(temp);
			}
		} else {
			group = getGroup(comps.get(0));
		}
		if(group != null) {
			group.add((WdemsGroupComponent)c);
		}
    }
    private WdemsComponent findGroupTag(List<WdemsComponent> comps){
    	for (WdemsComponent comp : comps) {
    		Object o = comp.getTagObject();
			if(o instanceof Group)
				return comp;
		}
    	return null;
    }
    private SelectGroup getGroup(WdemsComponent comp){
    	Object o = comp.getTagObject();
    	
    	Group group = null;
    	GroupKey key = new GroupKey();
    	if(o instanceof Checkbox){
    		Checkbox box = (Checkbox)o;
    		String name = box.getGroupReference();
    		key.setName(name);
    		group = (Group)WdemsTagManager.Instance.getWdemsTags(name);
    	}else if(o instanceof Group){
    		group = (Group)o;    	
    		key.setName(comp.getTagName());
    		key.setXpath(comp.getTagXPath());
    	}else
    		return null;
    	
    	if(group == null)
    		return null;
    	
    	return WdemsGroupManager.getGroup(key, group);
    }
    private void addLis(JComponent comp){
    	if(!(comp instanceof JTextComponent))
    		return;
    	final JTextComponent c = (JTextComponent)comp;
//    	c.addPropertyChangeListener(new PropertyChangeListener(){
//			public void propertyChange(PropertyChangeEvent evt) {
//				String name = evt.getPropertyName();
//				if(!"font".equalsIgnoreCase(name))
//					return;
//				
//				updateSize(c);
//			}
//    	});
    	c.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				updateSize(c);
			}

			public void removeUpdate(DocumentEvent e) {
				updateSize(c);
			}
    	});
    }
    private void updateSize(JComponent c){
    	if(c.isPreferredSizeSet()){
    		c.setPreferredSize(null);
    	}
    	Dimension dim = c.getPreferredSize();
    	if(dim.width < WIDTH /*&& c.getText().length() == 0*/) {
    		dim = new Dimension(WIDTH, dim.height);
    	}
    	c.setMaximumSize(dim);
    	c.setPreferredSize(dim);			
    	updatePolicy(c);
    }
    
    private void zoomComponent(JComponent c, InlineArea inline){
    	MutableAttributeSet set = ComponentStyleUtil.getFontStyles(inline);
    	
    	Java2DRenderer render = (Java2DRenderer)this;
    	AffineTransform at = render.getState().getTransform();
    	int fontSize = StyleConstants.getFontSize(set);
    	fontSize = (int)Math.ceil(fontSize);
    	int style = Font.PLAIN;
    	if(StyleConstants.isBold(set)){
    		style &= Font.BOLD;
    	}
    	if(StyleConstants.isItalic(set)){
    		style &= Font.ITALIC;
    	}
    	
    	String content = getContent(inline);
    	String hint = (c.getToolTipText() == null) ? "" :c.getToolTipText();
    	Font font = new Font(StyleConstants.getFontFamily(set), style, fontSize);
    	if(font.canDisplayUpTo(content + hint) != -1){
    		String name = ComponentStyleUtil.getDefaultFamily();
    		font = new Font(name, style, fontSize);
    	}
    	float scale = (float)(fontSize * at.getScaleX());
    	if(isNullInline(inline)){
    		scale = fontSize;
    	}
    	//用该种做法，字体变粗时，光标定位不准确
    	AffineTransform a = AffineTransform.getScaleInstance(at.getScaleX(), at.getScaleY());
		font = font.deriveFont(scale);
		
		if(c instanceof JScrollPane){
			JScrollPane pane = (JScrollPane)c;
			c = (JComponent)pane.getViewport().getView(); 
		}
		c.setFont(font);
    }
    
    private void updatePolicy(final JComponent comp){
    	Thread t = new Thread(){
    		@Override
    		public void run() {
    			JScrollPane pane = getPane(comp);
    			if(pane == null)return;
    			WdemsEditComponentManager.updatePolicy(pane);
    		}
    	};
    	SwingUtilities.invokeLater(t);
    }
    private JScrollPane getPane(java.awt.Container comp){
    	if(comp == null)
    		return null;
    	if(comp instanceof JScrollPane)
    		return (JScrollPane)comp;
    	return getPane(comp.getParent());
    }
    @SuppressWarnings("unchecked")
	private String getContent(Area inline){
    	StringBuilder s = new StringBuilder();
    	if(inline == null)return s.toString();
    	if(inline instanceof TextArea){
    		TextArea text = (TextArea)inline;
    		s.append(text.getText());
    		return s.toString();
    	}
    	List<Area> areas = inline.getChildAreas();
    	if(areas == null || areas.isEmpty())
    		return s.toString();
    	for (Area area : areas) {
    		s.append(getContent(area));
    	}
    	return s.toString();
    }
    private LineArea getLineArea(Area inline){
    	if(inline == null)return null;
    	if(inline instanceof LineArea)
    		return (LineArea)inline;
    	return getLineArea((Area)inline.getParentArea());
    }
    /* 【添加：END】 by 李晓光	2009-7-6 */
    /**
     * Render the given Character.
     * @param ch the character to render
     * @deprecated Only TextArea should be used. This method will be removed eventually.
     */
    @Deprecated
	protected void renderCharacter(Character ch)
    {
        currentIPPosition += 0;//ch.getAllocIPD();
    }

    /**
     * Common method to render the background and borders for any inline area.
     * The all borders and padding are drawn outside the specified area.
     * @param area the inline area for which the background, border and padding is to be rendered
     */
    protected abstract void renderInlineAreaBackAndBorders(InlineArea area);

    /**
     * Render the given Space.
     * @param space the space to render
     */
    protected void renderInlineSpace(Space space)
    {
        space.setBPD(0);
        renderInlineAreaBackAndBorders(space);
        // an inline space moves the inline progression position for the current block by the width or height of the
        // space it may also have styling (only on this object) that needs handling
        currentIPPosition += space.getAllocIPD();
    }

    /**
     * Render the given Leader.
     * @param area the leader to render
     */
    protected void renderLeader(Leader area)
    {
        currentIPPosition += area.getAllocIPD();
    }

    /**
     * Render the given TextArea.
     * @param text the text to render
     */
    protected void renderText(TextArea text)
    {
        int saveIP = currentIPPosition;
        int saveBP = currentBPPosition;
        Iterator iter = text.getChildAreas().iterator();
        while(iter.hasNext())
        {
            renderInlineArea((InlineArea)iter.next());
        }
        currentIPPosition = saveIP + text.getAllocIPD();
    }

    /**
     * Render the given WordArea.
     * @param word the word to render
     */
    protected void renderWord(WordArea word)
    {
        currentIPPosition += word.getAllocIPD();
    }

    /**
     * Render the given SpaceArea.
     * @param space the space to render
     */
    protected void renderSpace(SpaceArea space)
    {
        currentIPPosition += space.getAllocIPD();
    }

    /**
     * Render the given InlineParent.
     * @param ip the inline parent to render
     */
    protected void renderInlineParent(InlineParent ip)
    {
        renderInlineAreaBackAndBorders(ip);
        int saveIP = currentIPPosition;
        int saveBP = currentBPPosition;
        currentIPPosition += ip.getBorderAndPaddingWidthStart();
        currentBPPosition += ip.getOffset();
        Iterator iter = ip.getChildAreas().iterator();
        while(iter.hasNext())
        {
            InlineArea area = (InlineArea)iter.next();

            // add by xuhao
            if(area instanceof TextArea && ((TextArea)area).getEditMode() != 0)
            {
                // mod by huangzl.保存上层节点的相关属性
                area.setParentArea(ip); // 从InlineParent得到边框的宽度
                if(_multiTextFlag)
                {
                    ((TextArea)area).setMultiInLine(true);
                }
                else
                {
                    ((TextArea)area).setMaxShowWidth(parentBlockLength / 1000);
//                    ((TextArea)area).setParentBlockRecX(parentBlockRecX);
//                    ((TextArea)area).setParentBlockRecY(parentBlockRecY);
                    ((TextArea)area).setIndent(parentIndent / 1000);
//                    ((TextArea)area).setTextAlign(isTextAlign);// del.因为parentBlockLength的值为Block内容的宽度，即getIPD()，而不是getAllocIPD，所以不用考虑start-indent的情况
                }
                // mod end.
            }
            // add end

            renderInlineArea(area);
        }
        currentIPPosition = saveIP + ip.getAllocIPD();
        currentBPPosition = saveBP;
    }

    /**
     * Render the given InlineBlockParent.
     * @param ibp the inline block parent to render
     */
    protected void renderInlineBlockParent(InlineBlockParent ibp)
    {
        renderInlineAreaBackAndBorders(ibp);
        currentIPPosition += ibp.getBorderAndPaddingWidthStart();
        // For inline content the BP position is updated by the enclosing line area
        int saveBP = currentBPPosition;
        currentBPPosition += ibp.getOffset();
        renderBlock(ibp.getChildArea());
        currentBPPosition = saveBP;
    }

    /**
     * Render the given Viewport.
     * @param viewport the viewport to render
     */
    protected void renderViewport(Viewport viewport)
    {
        Area content = viewport.getContent();
        int saveBP = currentBPPosition;
        currentBPPosition += viewport.getOffset();
        Rectangle2D contpos = viewport.getContentPosition();
        if(content instanceof Image)
        {
            renderImage((Image)content, contpos);
        }
        else if(content instanceof Container)
        {
            renderContainer((Container)content);
        }
        else if(content instanceof ForeignObject)
        {
            renderForeignObject((ForeignObject)content, contpos);
        }
        else if(content instanceof QianZhangArea)
		{
        	//记录签章位置
			float x = currentIPPosition / 1000f;
			float y = (currentBPPosition + viewport.getOffset()) / 1000f;
			float width = viewport.getIPD() / 1000f;
			float height = viewport.getBPD() / 1000f;
			float borderPaddingStart = viewport.getBorderAndPaddingWidthStart() / 1000f;
			float borderPaddingBefore = viewport
					.getBorderAndPaddingWidthBefore() / 1000f;
			float bpwidth = borderPaddingStart
					+ (viewport.getBorderAndPaddingWidthEnd() / 1000f);
			float bpheight = borderPaddingBefore
					+ (viewport.getBorderAndPaddingWidthAfter() / 1000f);

			Rectangle2D rect = new Rectangle();
			rect.setRect(x, y - borderPaddingBefore, width + bpwidth, height
					+ bpheight);
			viewport.setViewport(rect);
			renderQianZhang((QianZhangArea) content, contpos);
		}
        currentIPPosition += viewport.getAllocIPD();
        currentBPPosition = saveBP;
    }

    /**
     * Renders an image area.
     * @param image  The image
     * @param pos    The target position of the image
     * (todo) Make renderImage() protected
     */
    public void renderImage(Image image, Rectangle2D pos)
    {
        // Default: do nothing.
        // Some renderers (ex. Text) don't support images.
    }
    //绘制签章对象，目前该方法只在PDFRenderer中实现，原来定位签章的位置
    public void renderQianZhang(QianZhangArea qianzhang,Rectangle2D pos)
    {
    	
    }
    /**
     * Tells the renderer to render an inline container.
     * @param cont  The inline container area
     */
    protected void renderContainer(Container cont)
    {
        int saveIP = currentIPPosition;
        int saveBP = currentBPPosition;

        List blocks = cont.getBlocks();
        renderBlocks(null, blocks);
        currentIPPosition = saveIP;
        currentBPPosition = saveBP;
    }

    /**
     * Renders a foreign object area.
     * @param fo   The foreign object area
     * @param pos  The target position of the foreign object
     * (todo) Make renderForeignObject() protected
     */
    public void renderForeignObject(ForeignObject fo, Rectangle2D pos)
    {
        // Default: do nothing.
        // Some renderers (ex. Text) don't support foreign objects.
    }

    /**
     * Returns the configuration subtree for a specific renderer.
     * @param cfg the renderer configuration
     * @param namespace the namespace (i.e. the XMLHandler) for which the configuration should be returned
     * @return the requested configuration subtree, null if there's no configuration
     */
    public static Configuration getHandlerConfig(Configuration cfg, String namespace)
    {
        if(cfg == null || namespace == null)
			return null;

        Configuration handlerConfig = null;

        Configuration[] children = cfg.getChildren("xml-handler");
        for(int i = 0; i < children.length; ++i)
        {
            try
            {
                if(children[i].getAttribute("namespace").equals(namespace))
                {
                    handlerConfig = children[i];
                    break;
                }
            }
            catch(ConfigurationException e)
            {
                // silently pass over configurations without namespace
            }
        }
        if(log.isDebugEnabled())
        {
            log.debug((handlerConfig == null ? "No" : "") + "XML handler configuration found for namespace " + namespace);
        }
        return handlerConfig;
    }

    /**
     * Render the xml document with the given xml namespace.
     * The Render Context is by the handle to render into the current rendering target.
     * @param ctx rendering context
     * @param doc DOM Document containing the source document
     * @param namespace Namespace URI of the document
     */
    public void renderXML(RendererContext ctx, Document doc, String namespace)
    {
//chg by huangzl.
        XMLHandler handler = userAgent.getXMLHandlerRegistry().getXMLHandler(this, namespace);
//        XMLHandler handler = new com.wisii.fov.render.java2d.Java2DSVGHandler();
        if(handler != null)
        {
            try
            {
                //Optional XML handler configuration
//del by huagnzl.因为getUserRendererConfig()方法返回的总是null
//                Configuration cfg = userAgent.getUserRendererConfig(getMimeType());
//                if (cfg != null)
//                {
//                    cfg = getHandlerConfig(cfg, namespace);
//                    if (cfg != null)
//                    {
//                        ctx.setProperty(RendererContextConstants.HANDLER_CONFIGURATION, cfg);
//                    }
//                }
                handler.handleXML(ctx, doc, namespace);
            }
            catch(Throwable t)
            {
                // could not handle document
                log.error("Some XML content will be ignored. " + "Could not render XML", t);
            }
        }
        else
        {
            if(warnedXMLHandlers == null)
            {
                warnedXMLHandlers = new java.util.HashSet();
            }
            if(!warnedXMLHandlers.contains(namespace))
            {
                // no handler found for document
                warnedXMLHandlers.add(namespace);
                log.warn("Some XML content will be ignored. " + "No handler defined for XML: " + namespace);
            }
        }
    }

    /**
     * Get the MIME type of the renderer.
     * @return   The MIME type of the renderer
     */
    public String getMimeType()
    {
        return null;
    }

    /*默认实现 Render接口*/
    public void setupPrinterInfo(Map PrinterList, PrintRef printref)
    {
        this.printref = printref;
        table = PrinterList;
    }


    /**add by lzy 过滤图片
     * imageSrc 需要过滤的image
     * */
    public java.awt.Image FilterImage(java.awt.Image imageSrc, int aphla)
    {
        java.awt.Image Filteredimage = null;

        Color BACKCOLOR = new Color(255, 255, 255, 0); //背景色
        Color FLGCOLOR = new Color(0, 0, 255, 255); //前景色
        int FlgAphla = aphla; //转换后前景色的透明度。
        int BACKAPLHA = 0; //转换后背景色的透明度。

        FovImageFilter d = new FovImageFilter(BACKCOLOR, BACKCOLOR, FlgAphla); //过滤器

        Filteredimage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
            imageSrc.getSource(), d));

        //等待加栽图片完成
        MediaTracker mt = null;

       
            mt = new MediaTracker(new JLabel());
        
       

        mt.addImage(Filteredimage, 1);
        try
        {
            mt.waitForID(1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return Filteredimage;
    }
       
    /* 【添加：START】by 李晓光 2009-2-2 */
    /**
     * 如果默认中有当前颜色的所在层则显示
     */
	protected  boolean isAvailabilityLayer(Color color, Set layers){
		if(layers == null)
			return true;
		if(layers.isEmpty())
			return false;
		
		int layer = 0;
		if((color instanceof WisedocColor)){
			WisedocColor c = (WisedocColor)color;
			layer = c.getLayer();
		}
		
		return layers.contains(new Integer(layer));
	}
	/**
     * 如果默认中有当前图像的所在层则显示
     */
	protected boolean isAvailabilityLayer(Area image, Set layers){
		if(layers == null)
			return true;
		if(layers.isEmpty() )
			return false;
		
		Integer layer = (Integer)image.getTrait(Trait.IMAGE_LAYER);
		if(layer == null) {
			layer = new Integer(0);
		}
		return layers.contains(layer);
	}
	/* 【添加：END】by 李晓光 2009-2-2 */
	/* (non-Javadoc)
	 * @see com.wisii.fov.render.Renderer#getResultInfo()
	 */
	public RenderResult getResultInfo()
	{
		RenderResult result=new RenderResult(getTotalPage(),getInfo());
		return result;
	}

	protected abstract int getTotalPage();
	protected  Object getInfo()
	{
		return null;
	}
}
