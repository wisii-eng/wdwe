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
 */package com.wisii.fov.render.awt.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.cli.Renderable;
import com.wisii.fov.area.PageViewport;

import com.wisii.fov.render.awt.AWTRenderer;
import java.awt.Toolkit;
import javax.swing.BorderFactory;


/**
 * <p>Holds a scrollpane with the rendered page(s) and handles actions performed
 * to alter the display of the page.
 * </p>
 * <p>Use PreviewPanelAPP when you want to embed a preview in your own application
 * with your own controls. Use PreviewDialog when you want to use the standard
 * Fov controls.
 * </p>
 * <p>In order to embed a PreviewPanelAPP in your own app, create your own renderer,
 * and your own agent. In order to support reloads, you may also implement your
 * own Renderable extension or the default InputHandler. Setting the Renderable
 * to null works fine though.
 * Then call setPreviewDialogDisplayed(false) to hide the
 * default dialog. Finally create a preview panel with the agent, renderable and
 * renderer and add it to your gui:
 * </p>
 * <pre>
 * AWTRenderer renderer = new AWTRenderer();
 * FOUserAgent agent = new FOUserAgent();
 * agent.setRendererOverride(renderer);
 * renderer.setPreviewDialogDisplayed(false);
 * renderer.setUserAgent(agent);
 * PreviewPanelAPP = new PreviewPanelAPP(agent, null, renderer);
 * PreviewPanelAPP = new PreviewPanelAPP(ua);
 * myGui.add(PreviewPanelAPP);
 * </pre>
 *
 * In order to set options and display a page do:
 * <pre>
 * renderer.clearViewportList();
 * // build report xml here
 * reload(); // optional if setting changed
 * </pre>
 *
 * If you wan't to change settings, don't call reload. A good example is
 * to set the page to fill the screen and set the scrolling mode:
 * <pre>
 * double scale = PreviewPanelAPP.getScaleToFitWindow();
 * PreviewPanelAPP.setScaleFactor(scale);
 * PreviewPanelAPP.setDisplayMode(PreviewPanelAPP.CONTINUOUS);
 * </pre>
 */
public class PreviewPanelAPP extends JPanel
{
    /** Constant for setting single page display. */
    public static final int SINGLE = 1;
    /** Constant for setting continuous page display. */
    public static final int CONTINUOUS = 2;
    /** Constant for displaying even/odd pages side by side in continuous form. */
    public static final int CONT_FACING = 3;

    /** The number of pixels left empty at the top bottom and sides of the page. */
    private static final int BORDER_SPACING = 10;

	/** The number of pixels left empty at the sides of the page. */
    private static final int BORDER_SPACING_FOR_WIDTH = 20;

    /** The main display area */
    private JScrollPane previewArea;

    /** The AWT renderer - often shared with PreviewDialog */
    private AWTRenderer renderer;

    /** The FOUserAgent associated with this panel - often shared with PreviewDialog */
    protected FOUserAgent foUserAgent;
    /**
     * Renderable instance that can be used to reload and re-render a document after
     * modifications.
     */
    protected Renderable renderable;
    /** The number of the page which is currently selected */
    private int currentPage = 0;

    /** The index of the first page displayed on screen. */
    private int firstPage = 0;

    /** The number of pages concurrently displayed on screen. */
    private int pageRange = 1;

    /** The display mode. One of SINGLE, CONTINUOUS or CONT_FACING. */
    private int displayMode = SINGLE;

    /** The component(s) that hold the rendered page(s) */
    private ImageProxyPanel[] pagePanels = null;

    /**
     * Panel showing the page panels in a grid. Usually the dimensions
     * of the grid are 1x1, nx1 or nx2.
     */
    private JPanel gridPanel = null;

    /** Asynchronous reloader thread, used when reload() method is called. */
    private Reloader reloader;

    /**
     * Allows any mouse drag on the page area to scroll the display window.
     */
    private ViewportScroller scroller;

    /**
     * Creates a new PreviewPanelAPP instance.
     * @param foUserAgent the user agent
     * @param renderable the Renderable instance that is used to reload/re-render a document
     *                   after modifications.
     * @param renderer the AWT Renderer instance to paint with
     */
    public PreviewPanelAPP(FOUserAgent foUserAgent, Renderable renderable, AWTRenderer renderer)
    {
        super(new GridLayout(1, 1));
        this.renderable = renderable;
        this.renderer = renderer;
        this.foUserAgent = foUserAgent;

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, 1)); // rows, cols

        previewArea = new JScrollPane(gridPanel);
        previewArea.getViewport().setBackground(Color.gray);

        // FIXME should add scroll wheel support here at some point.
        scroller = new ViewportScroller(previewArea.getViewport());
        previewArea.addMouseListener(scroller);
        previewArea.addMouseMotionListener(scroller);
        previewArea.setMinimumSize(new Dimension(50, 50));
        add(previewArea);
    }

    /**
     * @return the currently visible page
     */
    public int getPage()
    {
        return currentPage;
    }

    /**
     * Selects the given page, displays it on screen and notifies
     * listeners about the change in selection.
     * @param number the page number
     */
    public void setPage(int number)
    {
        if (displayMode == CONTINUOUS || displayMode == CONT_FACING)
        {
            // FIXME Should scroll so page is visible
            currentPage = number;
        }
        else
        { // single page mode
            currentPage = number;
            firstPage = currentPage;
        }
        showPage();
    }

    /**
     * Sets the display mode.
     * @param mode One of SINGLE, CONTINUOUS or CONT_FACING.
     */
    public void setDisplayMode(int mode)
    {
        if (mode != displayMode)
        {
            displayMode = mode;
            gridPanel.setLayout(new GridLayout(0, displayMode == CONT_FACING ? 2 : 1));
            reload();
        }
    }

    /**
     * Returns the display mode.
     * @return mode One of SINGLE, CONTINUOUS or CONT_FACING.
     */
    public int getDisplayMode() {
        return displayMode;
    }

    /**
     * Reloads and reformats document.
     */
    public synchronized void reload() {
        if (reloader == null || !reloader.isAlive()) {
            reloader = new Reloader();
            reloader.start();
        }
    }

    /**
     * Allows a (yet) simple visual debug of the document.
     */
    void debug() {
        renderer.debug = !renderer.debug;
        reload();
    }

    /**
     * Allows any mouse drag on the page area to scroll the display window.
     */
    private class ViewportScroller implements MouseListener, MouseMotionListener {
        /** The viewport to be scrolled */
        private final JViewport viewport;
        /** Starting position of a mouse drag - X co-ordinate */
        private int startPosX = 0;
        /** Starting position of a mouse drag - Y co-ordinate */
        private int startPosY = 0;

        ViewportScroller(JViewport vp) {
            viewport = vp;
        }

        // ***** MouseMotionListener *****

        public synchronized void mouseDragged(MouseEvent e) {
            if (viewport == null) {
                return;
            }
            int x = e.getX();
            int y = e.getY();
            int xmove = x - startPosX;
            int ymove = y - startPosY;
            int viewWidth = viewport.getExtentSize().width;
            int viewHeight = viewport.getExtentSize().height;
            int imageWidth = viewport.getViewSize().width;
            int imageHeight = viewport.getViewSize().height;

            Point viewPoint = viewport.getViewPosition();
            int viewX = Math.max(0, Math.min(imageWidth - viewWidth, viewPoint.x - xmove));
            int viewY = Math.max(0, Math.min(imageHeight - viewHeight, viewPoint.y - ymove));

            viewport.setViewPosition(new Point(viewX, viewY));

            startPosX = x;
            startPosY = y;
        }

        public void mouseMoved(MouseEvent e) { }

        // ***** MouseListener *****

        public void mousePressed(MouseEvent e) {
            startPosX = e.getX();
            startPosY = e.getY();
        }

        public void mouseExited(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) {   }
    }

    /**
     * This class is used to reload document in a thread safe way.
     */
    private class Reloader extends Thread {

        public void run() {
            if (!renderer.isRenderingDone()) {
                // do not allow the reloading while FOV is still rendering
                JOptionPane.showMessageDialog(previewArea,
                        "Cannot perform the requested operation until "
                                + "all page are rendered. Please wait",
                        "Please wait ", 1 /* INFORMATION_MESSAGE */);
                return;
            }

            pagePanels = null;

            int savedCurrentPage = currentPage;
            currentPage = 0;

            gridPanel.removeAll();
            switch(displayMode) {
                case CONT_FACING:
                    // This page intentionally left blank
                    // Makes 0th/1st page on rhs
                    gridPanel.add(new JLabel(""));
                case CONTINUOUS:
                    currentPage = 0;
                    firstPage = 0;
                    pageRange = renderer.getNumberOfPages();
                    break;
                case SINGLE:
                default:
                    currentPage = 0;
                    firstPage = 0;
                    pageRange = 1;
                    break;
            }

            pagePanels = new ImageProxyPanel[pageRange];
            for (int pg = 0; pg < pageRange; pg++) {
                pagePanels[pg] = new ImageProxyPanel(renderer, pg + firstPage);
                pagePanels[pg].setBorder(new EmptyBorder(BORDER_SPACING, BORDER_SPACING, BORDER_SPACING, BORDER_SPACING));
                gridPanel.add(pagePanels[pg]);
            }

            setPage(savedCurrentPage);
        }
    }

    /**
     * Scales page image
     * @param scale [0;1]
     */
    public void setScaleFactor(double scale) {
        renderer.setScaleFactor(scale);
        reload();
    }

    /**
     * Returns the scale factor required in order to fit either the current
     * page within the current window or to fit two adjacent pages within
     * the display if the displaymode is continuous.
     * @return the requested scale factor
     * @throws FOVException in case of an error while fetching the PageViewport
     */
    public double getScaleToFitWindow() throws FOVException {
        Dimension extents = previewArea.getViewport().getExtentSize();
        //获取屏幕的DPI显示
        double dpi = Toolkit.getDefaultToolkit().getScreenResolution(); //dpi
        double scale = SystemUtil.DEFAULT_TARGET_RESOLUTION / dpi; //72.0/96.0;
        return getScaleToFit(extents.getWidth()*scale - 2 * BORDER_SPACING,
                    extents.getHeight()*scale - 2 * BORDER_SPACING);
    }

    /**
     * As getScaleToFitWindow, but ignoring the Y axis.
     * @return the requested scale factor
     * @throws FOVException in case of an error while fetching the PageViewport
     */
    public double getScaleToFitWidth() throws FOVException {
        Dimension extents = previewArea.getViewport().getExtentSize();
        double dpi = Toolkit.getDefaultToolkit().getScreenResolution(); //dpi
        double scale = SystemUtil.DEFAULT_TARGET_RESOLUTION / dpi; //72.0/96.0;
        return getScaleToFit(extents.getWidth()*scale - 2 * BORDER_SPACING_FOR_WIDTH, Double.MAX_VALUE);
    }

    /**
     * Returns the scale factor required in order to fit either the current page or
     * two adjacent pages within a window of the given height and width, depending
     * on the display mode. In order to ignore either dimension,
     * just specify it as Double.MAX_VALUE.
     * @param viewWidth width of the view
     * @param viewHeight height of the view
     * @return the requested scale factor
     * @throws FOVException in case of an error while fetching the PageViewport
     */
    public double getScaleToFit(double viewWidth, double viewHeight) throws FOVException {
        PageViewport pageViewport = renderer.getPageViewport(currentPage);
        Rectangle2D pageSize = pageViewport.getViewArea();
        double widthScale = viewWidth / (pageSize.getWidth() / 1000f);
        double heightScale = viewHeight / (pageSize.getHeight() / 1000f);
        return Math.min(displayMode == CONT_FACING ? widthScale / 2 : widthScale, heightScale);
    }

    /** Starts rendering process and shows the current page. */
    public synchronized void showPage() {
        ShowPageImage viewer = new ShowPageImage();

        if (SwingUtilities.isEventDispatchThread()) {
            viewer.run();
        } else {
            SwingUtilities.invokeLater(viewer);
        }
    }

    /** This class is used to render the page image in a thread safe way. */
    private class ShowPageImage implements Runnable {

        /**
         * The run method that does the actual rendering of the viewed page
         */
        public void run() {
            for (int pg = firstPage; pg < firstPage + pageRange; pg++) {
                pagePanels[pg - firstPage].setPage(pg);
            }
            revalidate();
        }
    }

		/**
		* reset the param about display of the PreviewPanelAPP.
		*/
		public void reInint()
		{
			this.firstPage = 0;
			this.pageRange = 1;
			this.pagePanels = null;
			this.reloader = null;
			this.displayMode = SINGLE;
		}

		/**
		 * return the grid panel.
		 */
		public JPanel getGridPanel()
		{
			return this.gridPanel;
		}
}
