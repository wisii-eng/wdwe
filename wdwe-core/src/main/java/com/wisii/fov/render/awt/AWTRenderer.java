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
 */package com.wisii.fov.render.awt;

// Java
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.IOException;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.cli.Renderable;
import com.wisii.fov.render.awt.viewer.StatusListener;
import com.wisii.fov.render.java2d.Java2DRenderer;

/**
 * The AWTRender outputs the pages generated by the layout engine to a Swing window. This Swing window serves as default
 * viewer for the -awt switch and as an example of how to embed the AWTRenderer into an AWT/Swing application.
 */
public class AWTRenderer extends Java2DRenderer implements Pageable
{
    /** The MIME type for AWT-Rendering */
    public static final String MIME_TYPE = MimeConstants.MIME_WISII_AWT_PREVIEW;

    /** flag for debugging */
    public boolean debug;

    /** Renderable instance that can be used to reload and re-render a document after modifications.     */
    protected Renderable renderable;

    /** Will be notified when rendering progresses  */
    protected StatusListener statusListener = null;

    /** Creates a new AWTRenderer instance.     */
    public AWTRenderer()
    {
        this(false);
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a new AWTRenderer instance.
     * @param previewAsMainWindow true if the preview dialog created by the renderer should be the main window of the application.
     */
    public AWTRenderer(boolean previewAsMainWindow)
    {
//        this.previewAsMainWindow = previewAsMainWindow;
    }

    /**
     * A Renderable instance can be set so the Preview Dialog can enable the "Reload" button
     * which causes the current document to be reprocessed and redisplayed.
     * @param renderable the Renderable instance.
     */
    public void setRenderable(Renderable renderable)
    {
        this.renderable = renderable;
    }

    /** @see com.wisii.fov.render.Renderer#renderPage(com.wisii.fov.area.PageViewport)   */
    public void renderPage(PageViewport pageViewport) throws IOException
    {
        super.renderPage(pageViewport);
        if (statusListener != null)
            statusListener.notifyPageRendered();
    }

    /** @see com.wisii.fov.render.Renderer#stopRenderer() */
    public void stopRenderer() throws IOException
    {
        super.stopRenderer();
        if (statusListener != null)
            statusListener.notifyRendererStopped(); // Refreshes view of page
    }

    /**
     * @return the dimensions of the specified page
     * @param pageNum the page number
     * @exception FOVException If the page is out of range or has not been rendered.
     */
    public Dimension getPageImageSize(int pageNum) throws FOVException
    {
        Rectangle2D bounds = getPageViewport(pageNum).getViewArea();
        pageWidth = (int) Math.round(bounds.getWidth() / 1000f);
        pageHeight = (int) Math.round(bounds.getHeight() / 1000f);

        //获取屏幕的DPI显示
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution(); //dpi
        userAgent.setTargetResolution(dpi);

        double scaleX = scaleFactor * (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION) / userAgent.getTargetPixelUnitToMillimeter();
        double scaleY = scaleFactor * (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION) / userAgent.getTargetPixelUnitToMillimeter();
        int bitmapWidth = (int) ((pageWidth * scaleX) + 0.5);
        int bitmapHeight = (int) ((pageHeight * scaleY) + 0.5);
        return new Dimension(bitmapWidth, bitmapHeight);
    }

    /** @see java.awt.print.Pageable#getPageFormat(int) */
        public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
        {
            try
            {
                if (pageIndex >= getNumberOfPages()) {
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
                if (width > height) {
                    paper.setImageableArea(0, 0, height / 1000d, width / 1000d);
                    paper.setSize(height / 1000d, width / 1000d);
                    pageFormat.setOrientation(PageFormat.LANDSCAPE);
                } else {
                    paper.setImageableArea(0, 0, width / 1000d, height / 1000d);
                    paper.setSize(width / 1000d, height / 1000d);
                    pageFormat.setOrientation(PageFormat.PORTRAIT);
                }

                //public static final int LANDSCAPE 0 横向
                // public static final int PORTRAIT 1  纵向
                // public static final int REVERSE_LANDSCAPE 2 横向反打

                paper = setStatePaper(width,height,paper);
                pageFormat.setOrientation(this.getOrientation());

                pageFormat.setPaper(paper);
                return pageFormat;
            } catch (FOVException fovEx) {
                throw new IndexOutOfBoundsException(fovEx.getMessage());
            }
    }

    /** @see java.awt.print.Pageable#getPrintable(int) */
    public Printable getPrintable(int pageIndex)
            throws IndexOutOfBoundsException {
        return this;
    }

    /** @see com.wisii.fov.render.Renderer */
    public boolean supportsOutOfOrder() {
        return false; // TODO true?
    }

    /** @see com.wisii.fov.render.AbstractRenderer */
    public String getMimeType() {
        return MIME_TYPE;
    }

    /**
     * Draws the background and borders and adds a basic debug view // TODO
     * implement visual-debugging as standalone
     *
     * @see com.wisii.fov.render.java2d.Java2DRenderer#drawBackAndBorders(com.wisii.fov.area.Area,
     * float, float, float, float)
     *
     * @param area the area to get the traits from
     * @param startx the start x position
     * @param starty the start y position
     * @param width the width of the area
     * @param height the height of the area
     */
    protected void drawBackAndBorders(Area area, float startx, float starty,
            float width, float height) {

        if (debug) {
            debugBackAndBorders(area, startx, starty, width, height);
        }

        super.drawBackAndBorders(area, startx, starty, width, height);
    }

    /** Draws a thin border around every area to help debugging */
    private void debugBackAndBorders(Area area, float startx, float starty,
            float width, float height) {

        // saves the graphics state in a stack
        saveGraphicsState();

        Color col = new Color(0.7f, 0.7f, 0.7f);
        state.updateColor(col);
        state.updateStroke(0.4f, EN_SOLID);
        state.getGraph().draw(
                new Rectangle2D.Float(startx, starty, width, height));

        // restores the last graphics state from the stack
        restoreGraphicsState();
    }

    /** @return the StatusListener. */
    public StatusListener getStatusListener() {
        return statusListener;
    }

    /**
     * Sets a StatusListener this renderer uses to notify about events.
     * @param statusListener The StatusListener to set.
     */
    public void setStatusListener(StatusListener statusListener)
    {
        this.statusListener = statusListener;
    }

  private void jbInit() throws Exception
  {
  }
}
