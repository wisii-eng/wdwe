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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import com.wisii.component.validate.validatexml.SchemaObj;
import com.wisii.edit.tag.components.decorative.WdemsCascadeManager;
import com.wisii.edit.tag.components.decorative.WdemsEditComponentManager;
import com.wisii.edit.tag.components.decorative.WdemsOperationManager;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.components.group.WdemsGroupManager;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.render.java2d.Java2DRenderer;

public class PreviewPanel extends JPanel implements EditAreaInterface {
	/** Constant for setting single page display. */
	public static final int SINGLE = 1;

	/** Constant for setting continuous page display. */
	public static final int CONTINUOUS = 2;

	/** Constant for displaying even/odd pages side by side in continuous form. */
	public static final int CONT_FACING = 3;

	// add by xuhao
	public static final String EDITTYPE_NAME = "editType";

	public static final String XPATH_NAME = "xpath";

	// add end

	/** The number of pixels left empty at the top bottom and sides of the page. */
	private static final int BORDER_SPACING = 10;

	// add by huangzl
	/** The number of pixels left empty at the sides of the page. */
	private static final int BORDER_SPACING_FOR_WIDTH = 20;

	// add end.

	/** The main display area */
	private final JScrollPane previewArea;

	/** The AWT renderer - often shared with PreviewDialog */
	private  Java2DRenderer renderer;

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
	 * Panel showing the page panels in a grid. Usually the dimensions of the
	 * grid are 1x1, nx1 or nx2.
	 */
	private JPanel gridPanel = null;
	private JLayeredPane layerPane = null;

	/** Asynchronous reloader thread, used when reload() method is called. */
	private Reloader reloader;

	/**
	 * Allows any mouse drag on the page area to scroll the display window.
	 */
	private final ViewportScroller scroller;


	// 存放当前界面的显示比例。1.0表示100%
	public static double _showPercent;

	private SchemaObj _checkInfo = null;



	// 是否可编辑的总控开关
	private boolean _editFlag = false;

	// 内部重绘标识
	private boolean reloadFlag = false;



	// 高亮显示的Color
	private Color highLightColor = new Color(184, 207, 229, 100);
	private final Integer DEFAULT_LAYER = JLayeredPane.DEFAULT_LAYER;
	private final Integer EDITOR_LAYER = (JLayeredPane.PALETTE_LAYER - 50);
	private final Integer WARN_LAYER = JLayeredPane.PALETTE_LAYER;
	private final Integer OPERATION_LAYER = (JLayeredPane.PALETTE_LAYER + 50);
	

	// 编辑的数据项是否验证通过
	// private boolean checkpass = true;

	/**
	 * Creates a new PreviewPanel instance.
	 * 
	 * @param foUserAgent
	 *            the user agent
	 * @param renderable
	 *            the Renderable instance that is used to reload/re-render a
	 *            document after modifications.
	 * @param renderer
	 *            the AWT Renderer instance to paint with
	 */
	public PreviewPanel(Java2DRenderer renderer)
	{
		super(new GridLayout(1, 1));
		this.renderer = renderer;
		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(0, 1)); // rows, cols
		layerPane = new JLayeredPane();
		layerPane.setLayout(new OverlayLayout(layerPane));
		layerPane.add(gridPanel, DEFAULT_LAYER);

		previewArea = new JScrollPane(layerPane);
		previewArea.getViewport().setBackground(Color.gray);

		// FIXME should add scroll wheel support here at some point.
		scroller = new ViewportScroller(previewArea.getViewport());
		previewArea.addMouseListener(scroller);
		previewArea.addMouseMotionListener(scroller);
		previewArea.addComponentListener(scroller);

		previewArea.setMinimumSize(new Dimension(50, 50));
		add(previewArea);

		distroy();
		addWarningLayer();
		addEditors();
		addOperations();
	}
	private void addWarningLayer() {
		 layerPane.add(WdemsWarningManager.getComponent(), WARN_LAYER);
	}
	private void addEditors(){
		layerPane.add(WdemsEditComponentManager.getLayerComponent(), EDITOR_LAYER);
	}
	private void addOperations(){
		layerPane.add(WdemsOperationManager.getComponent(), OPERATION_LAYER);
	}
	private void distroy(){
		WdemsEditComponentManager.clearDump();
		WdemsCascadeManager.clearDump();
		WdemsOperationManager.clearDump();
		WdemsWarningManager.clearDump();
		WdemsGroupManager.cleanDump();
	}

	/**
	 * @return the currently visible page
	 */
	public int getPage() {
		return currentPage;
	}

	/**
	 * Selects the given page, displays it on screen and notifies listeners
	 * about the change in selection.
	 * 
	 * @param number
	 *            the page number
	 */
	public void setPage(int number) {
		if (displayMode == CONTINUOUS || displayMode == CONT_FACING) {
			// FIXME Should scroll so page is visible
			currentPage = number;
		} else { // single page mode
			currentPage = number;
			firstPage = currentPage;
		}
		showPage();
	}

	public void setCurrentPage(int number) {
		if (displayMode == CONTINUOUS || displayMode == CONT_FACING) {
			// FIXME Should scroll so page is visible
			currentPage = number;
		} else { // single page mode
			currentPage = number;
			firstPage = currentPage;
		}
	}

	/**
	 * Sets the display mode.
	 * 
	 * @param mode
	 *            One of SINGLE, CONTINUOUS or CONT_FACING.
	 */
	public void setDisplayMode(int mode) {
		if (mode != displayMode) {
			displayMode = mode;
			gridPanel.setLayout(new GridLayout(0,
					displayMode == CONT_FACING ? 2 : 1));
			reload();
		}
	}

	/**
	 * Returns the display mode.
	 * 
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
	 * Allows any mouse drag on the page area to scroll the display window.
	 */
	private class ViewportScroller implements MouseListener,
			MouseMotionListener, ComponentListener {
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
			if (viewport == null)
				return;
			int x = e.getX();
			int y = e.getY();
			int xmove = x - startPosX;
			int ymove = y - startPosY;
			int viewWidth = viewport.getExtentSize().width;
			int viewHeight = viewport.getExtentSize().height;
			int imageWidth = viewport.getViewSize().width;
			int imageHeight = viewport.getViewSize().height;

			Point viewPoint = viewport.getViewPosition();
			int viewX = Math.max(0, Math.min(imageWidth - viewWidth,
					viewPoint.x - xmove));
			int viewY = Math.max(0, Math.min(imageHeight - viewHeight,
					viewPoint.y - ymove));

			viewport.setViewPosition(new Point(viewX, viewY));

			startPosX = x;
			startPosY = y;
		}

		public void mouseMoved(MouseEvent e) {
			PreviewPanel.this.mouseMoved(e);
		}

		// ***** MouseListener *****

		public void mousePressed(MouseEvent e) {
			startPosX = e.getX();
			startPosY = e.getY();
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			PreviewPanel.this.mouseClicked(e);
		}

		public void mouseReleased(MouseEvent e) {
		}

		// ***** ComponentListener *****

		public void componentResized(ComponentEvent e) {
//			_previrePanel.componentResized(e);
		}

		public void componentMoved(ComponentEvent e) {
		}

		public void componentShown(ComponentEvent e) {
		}

		public void componentHidden(ComponentEvent e) {
		}
	}

	/**
	 * This class is used to reload document in a thread safe way.
	 */
	private class Reloader extends Thread {

		@Override
		public void run() {
			pagePanels = null;

			int savedCurrentPage = currentPage;
			currentPage = 0;

			/*gridPanel.removeAll();*/
			clear();
			
			switch (displayMode) {
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
				pagePanels[pg].setBorder(new EmptyBorder(BORDER_SPACING,
						BORDER_SPACING, BORDER_SPACING, BORDER_SPACING));

				gridPanel.add(pagePanels[pg]);
			}
			
			setPage(savedCurrentPage);
		}
	}
	private void clear(){
		Integer[] layers = {OPERATION_LAYER, EDITOR_LAYER, DEFAULT_LAYER};
		for (Integer layer : layers) {
			Component[] comps = layerPane.getComponentsInLayer(layer);
			for (Component c : comps) {
				if(c instanceof Container){
					((Container)c).removeAll();
				}
			}
		}
		WdemsOperationManager.clearDump();
	}
	/**
	 * Scales page image
	 * 
	 * @param scale
	 *            [0;1]
	 */
	public void setScaleFactor(double scale) {
		renderer.setScaleFactor(scale);
		reload();
	}

	/**
	 * Returns the scale factor required in order to fit either the current page
	 * within the current window or to fit two adjacent pages within the display
	 * if the displaymode is continuous.
	 * 
	 * @return the requested scale factor
	 * @throws FOVException
	 *             in case of an error while fetching the PageViewport
	 */
	public double getScaleToFitWindow() throws FOVException {
		Dimension extents = previewArea.getViewport().getExtentSize();
		return getScaleToFit(extents.getWidth() - 2 * BORDER_SPACING, extents
				.getHeight()
				- 2 * BORDER_SPACING);
	}

	/**
	 * As getScaleToFitWindow, but ignoring the Y axis.
	 * 
	 * @return the requested scale factor
	 * @throws FOVException
	 *             in case of an error while fetching the PageViewport
	 */
	public double getScaleToFitWidth() throws FOVException {
		Dimension extents = previewArea.getViewport().getExtentSize();
		return getScaleToFit(extents.getWidth() - 2 * BORDER_SPACING_FOR_WIDTH,
				Double.MAX_VALUE);
	}

	/**
	 * Returns the scale factor required in order to fit either the current page
	 * or two adjacent pages within a window of the given height and width,
	 * depending on the display mode. In order to ignore either dimension, just
	 * specify it as Double.MAX_VALUE.
	 * 
	 * @param viewWidth
	 *            width of the view
	 * @param viewHeight
	 *            height of the view
	 * @return the requested scale factor
	 * @throws FOVException
	 *             in case of an error while fetching the PageViewport
	 */
	public double getScaleToFit(double viewWidth, double viewHeight) {
		PageViewport pageViewport;
		try {
			pageViewport = renderer.getPageViewport(currentPage);
			Rectangle2D pageSize = pageViewport.getViewArea();
			double widthScale = viewWidth / (pageSize.getWidth() / 1000f);
			double heightScale = viewHeight / (pageSize.getHeight() / 1000f);
			return Math.min(displayMode == CONT_FACING ? widthScale / 2
					: widthScale, heightScale);
		} catch (FOVException e) {
			return 1;
		}
	
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
			for (int pg = firstPage; pg < firstPage + pageRange; pg++)
			{
				if (pagePanels!=null&&pagePanels[pg - firstPage] != null)
				{
					pagePanels[pg - firstPage].setPage(pg);
				}
			}
			if (reloadFlag)// 若是内部重绘,则将_editFlag设成true
			{
				_editFlag = true;
				reloadFlag = false;
			}
			revalidate();
			EnginePanel enginepanel=EngineUtil.getEnginepanel();
			enginepanel.closeGlass();
			
		}
	}

	// add by huangzl
	/**
	 * reset the param about display of the previewPanel.
	 */
	public void reInint() {
		this.firstPage = 0;
		this.currentPage=0;
		this.pageRange = 1;
		this.pagePanels = null;
		this.reloader = null;
		this.displayMode = SINGLE;
		this.gridPanel.removeAll();
		this.renderer.setRenderingDone(false);
	}
	public void init(Java2DRenderer renderer) {
		this.renderer=renderer;
		this.firstPage = 0;
		this.currentPage=0;
		this.pageRange = 1;
		this.pagePanels = null;
		this.reloader = null;
		this.displayMode = SINGLE;
		this.gridPanel.removeAll();
		this.renderer.setRenderingDone(false);
	}
	/**
	 * return the grid panel.
	 */
	public JPanel getGridPanel() {
		return this.gridPanel;
	}

	// add end

	/**
	 * 这个方法是判断当前鼠标的位置是否进入了显示区域，并在界面上显示对应变化
	 * 
	 * @param e
	 */
	public void mouseMoved(MouseEvent e) {

	}

	/**
	 * 鼠标点击事件的处理
	 * 
	 * @param e
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * 取消编辑的方法,隐藏控件并回复状态
	 */
	public void cnacelEdit() {

	}

	/**
	 * 完成编辑的方法,在这里进行数据校验和状态改变等操作
	 */
	public boolean editComplete() {
			return true;
	}

	/**
	 * 鼠标右键按下事件,进行右键菜单的弹出
	 */
	public void rightMouseClick(MouseEvent e) {
	}

	

	/**
	 * 获取显示比例
	 * 
	 * @return
	 */
	public static double getShowPercent() {
		return _showPercent;
	}

	/**
	 * 设置显示比例
	 * 
	 * @param percent
	 */
	public static void setShowPercent(double percent) {
		_showPercent = percent;
	}

	public SchemaObj getCheckInfo() {
		return _checkInfo;
	}

	public void setCheckInfo(SchemaObj checkInfo) {
		this._checkInfo = checkInfo;
	}

	public boolean isEditMode() {
		return _editFlag;
	}


	// 设置高亮显示的Color
	public void setHighLightColor(Color c) {
		this.highLightColor = c;
	}

	// 获取高亮显示的Color
	public Color getHighLightColor() {
		return highLightColor;
	}
}
