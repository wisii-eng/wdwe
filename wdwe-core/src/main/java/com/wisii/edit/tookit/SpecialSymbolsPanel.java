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
 *//**
 * @SpecialChar.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.tookit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JWindow;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import com.wisii.edit.tag.components.floating.WdemsToolManager;

/**
 * 类功能描述：
 *
 * 作者：p.x
 * 创建日期：2009-5-31
 */
@SuppressWarnings("serial")
public class SpecialSymbolsPanel extends JPanel implements ActionListener{

	private final JTabbedPane tabbedPane;
	private String symbols = "";
	/*private final JFrame parent;*/
	
	private final SymbolsCanvas pointSC;
	private final SymbolsCanvas specialSC;
	private final SymbolsCanvas mathSC;
	private final SymbolsCanvas unitSC;
	private final SymbolsCanvas numberSC;
	private final SymbolsCanvas pinyinSC;
	
	//字体属性
	private final String fontName = "";
	private final int fontSize = 12;
	private final int fontStyle = Font.PLAIN;
	private Font testFont = null;
	
	//标点符号
	private final int pointSymbols[] = { 
			0x0020,0x002c,0x3001,0x3002,0x002e,0x003b,0xff1a,0x003f,0x0021,0x003a,0x2026,0x2035,
			0x2032,0x0060,0x3005,0x007e,0x2016,0x20c7,0x20c9,0xff50,0xff51,0xff52,0x2022,0xff54,
			0xff55,0xff56,0xff57,0x007c,0x2013,0xfe31,0x2014,0xfe33,0xfe34,0xfe4f,0x0028,0x0029,
			0xfe35,0xfe36,0xfe58,0xfe59,0xfe37,0xfe38,0xfe5d,0xfe5e,0xfe39,0xfe3a,0x3010,0x3011,
			0xfe3b,0xfe3c,0x300a,0x300b,0xfe3d,0xfe3e,0x3008,0x3009,0xfe3f,0xfe40,0x300c,0x300d,
			0xfe41,0xfe42,0x300e,0x300f,0xfe43,0xfe44,0xfe59,0xfe5a,0xfe5b,0xfe5c,0xfe5d,0xfe5e,
			0x2018,0x2019,0x201c,0x201d,0x301d,0x301e,0x02ca,0x02cb};
	//特殊符号
	private final int specialSymbols[] = { 
			0xff03,0xff20,0xff06,0xff0a,0x203b,0x00a7,0x3003,0x2116,0x3013,0x3007,0x25cf,0x25b3,
			0x25b2,0x25ce,0x2606,0x2605,0x25c7,0x25c6,0x25a1,0x25a0,0x25bd,0x25bc,0x32a3,0x2105,
			0x00af,0xfff3,0xff3f,0xfe49,0xfe4a,0xfe4d,0xfe4e,0xfe4b,0xfe4c,0xfe5f,0xfe60,0xfe61,
			0x2040,0x2041,0x2295,0x2296,0x2190,0x2191,0x2192,0x2193,0x2196,0x2197,0x2198,0x2199,
			0x2225,0x2223,0x002f,0x005c,0x2215,0xfe68};
	//数学符号
	private final int mathSymbols[] = { 
			0x2248,0x2261,0x2260,0x003d,0x2264,0x2265,0xff1c,0xff1e,0x226e,0x226f,0x2237,0x00b1,
			0xff0b,0xff0d,0x00d7,0x00f7,0x2215,0x222b,0x222e,0x221d,0x221e,0x2228,0x2229,0x2211,
			0x220f,0x2229,0x222a,0x2208,0x2234,0x2235,0x22a5,0x2225,0x2220,0x2312,0x2299,0x224c,
			0x223d,0x221a,0x2266,0x2267,0x2252,0x2261,0x002b,0x002d,0x003c,0x003e,0xfe66,0xff5e,
			0x221f,0x22bf,0x33d1,0x33d2};
	//单位符号
	private final int unitSymbols[] = { 
			0x00b0,0x2032,0x2033,0x00a5,0x0024,0x3012,0x00a2,0x00a3,0x0025,0x0040,0x2103,0x2108,
			0xfe69,0xfe6a,0x2030,0xfe6b,0x33d5,0x339c,0x339d,0x339e,0x33ce,0x33a1,0x338e,0x338f,
			0x33c4,0x00ba,0x25cb,0x00a4};
	//数字符号
	private final int numberSymbols[] = { 
			0x2160,0x2161,0x2162,0x2163,0x2164,0x2165,0x2166,0x2167,0x2168,0x2169,0x216a,0x216b,
			0x2170,0x2171,0x2172,0x2173,0x2174,0x2175,0x2176,0x2177,0x2178,0x2179,0x2488,0x2489,
			0x248a,0x248b,0x248c,0x248d,0x248e,0x248f,0x2490,0x2491,0x2492,0x2493,0x2494,0x2495,
			0x2496,0x2497,0x2498,0x2499,0x249a,0x249b,0x2474,0x2475,0x2476,0x2477,0x2478,0x2479,
			0x247a,0x247b,0x247c,0x247d,0x247e,0x247f,0x2481,0x2482,0x2483,0x2484,0x2485,0x2486,
			0x2487,0x2460,0x2461,0x2462,0x2463,0x2464,0x2465,0x2466,0x2467,0x2468,0x2469,0x3220,
			0x3221,0x3222,0x3223,0x3224,0x3225,0x3226,0x3227,0x3228,0x3229};
	//拼音符号
	private final int pinyinSymbols[] = { 
			0x0101,0x00e1,0x01ce,0x00e0,0x014d,0x00f3,0x01d2,0x00f2,0x0113,0x00e9,0x011b,0x00e8,
			0x012b,0x00ed,0x01d0,0x00ec,0x016b,0x00fa,0x01d4,0x00f9,0x01d6,0x01d8,0x01da,0x01dc,
			0x00fc,0x00ea,0x0251,0x0000,0x0114,0x0148,0x01f9,0x0261};
	
	/**
	 * {方法的功能/动作描述}
	 *
	 * @param      {引入参数名}   {引入参数说明}
	 * @return      {返回参数名}   {返回参数说明}
	 * @exception   {说明在某情况下,将发生什么异常}
	 */
	public SpecialSymbolsPanel(){
		super(new GridLayout(1, 1));
		testFont = new Font( fontName, fontStyle, fontSize );
		
		pointSC = new SymbolsCanvas(pointSymbols);
		specialSC = new SymbolsCanvas(specialSymbols);
		mathSC = new SymbolsCanvas(mathSymbols);
		unitSC = new SymbolsCanvas(unitSymbols);
		numberSC = new SymbolsCanvas(numberSymbols);
		pinyinSC = new SymbolsCanvas(pinyinSymbols);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("标点符号", pointSC);
		tabbedPane.addTab("特殊符号",  specialSC);
		tabbedPane.addTab("数学符号",  mathSC);
		tabbedPane.addTab("单位符号",  unitSC);
		tabbedPane.addTab("数字符号",  numberSC);
		tabbedPane.addTab("拼音",  pinyinSC);

		tabbedPane.setPreferredSize(new Dimension(420, 300));
		//The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				distroy();
			}
		});
		
		this.add(tabbedPane);
	}
	public void distroy(){
		SymbolsCanvas[] canvas = {pointSC, specialSC, mathSC, unitSC, numberSC, pinyinSC};
		for (SymbolsCanvas canv : canvas) {
			canv.hiddenshowZoomed();
		}
	}
	/**
	 * {方法的功能/动作描述}
	 *
	 * @param      {引入参数名}   {引入参数说明}
	 * @return      {返回参数名}   {返回参数说明}
	 * @exception   {说明在某情况下,将发生什么异常}
	 */
	public void actionPerformed(ActionEvent e) {

	}
	public String getSymbols() {
		return symbols;
	}
	public void setSymbols(String symbols) {
		this.symbols = symbols;
	}

	/// Inner panel that holds the actual drawing area and its routines
	private class SymbolsCanvas extends JPanel implements MouseListener, MouseMotionListener {
		//网格横、纵个数
		private int numCharAcross, numCharDown;
		//背景和字体更新状态
		private boolean updateBackBuffer = true;
		private boolean updateFontMetrics = true;
		//符号数组
		private final int theSymbols[];
		//最大网格宽和高
		private int maxAscent, maxDescent;
		//单个网格的宽和高
		private int gridWidth = 0, gridHeight = 0;
		//画布坐标
		private int canvasInset_X = 5;
		private int canvasInset_Y = 5;
		//背景图
		private BufferedImage backBuffer = null;
		//放大
		private final JWindow zoomWindow;
		private BufferedImage zoomImage = null;
		private int mouseOverCharX = -1, mouseOverCharY = -1;
		private int currMouseOverChar = -1, prevZoomChar = -1;
		private final float ZOOM = 2.0f;
		private boolean nowZooming = false;
	//	private boolean firstTime = true;
		//异常id
		private final int CANT_FIT_DRAW = 1;
		//光标
//		private final Cursor blankCursor;

		public SymbolsCanvas(int symbols[]) {
			this.addMouseListener( this );
			this.addMouseMotionListener( this );
			this.setForeground( Color.black );
			this.setBackground( Color.white );
			
			theSymbols = symbols;
		
			zoomWindow = new JWindow();
			zoomWindow.addMouseListener(new MouseAdapter(){
				
				@Override
				public void mouseClicked(MouseEvent e) {
					Component comp = WdemsToolManager.Instance
							.getFocusComponent();
					if (comp==null||!(comp instanceof JTextComponent))
						return;

					JTextComponent text = (JTextComponent) comp;

					if (!text.isEditable())
						return;

					char charArray[] = Character.toChars(currMouseOverChar);
					String symbols = new String(charArray);
					text.replaceSelection(symbols);
					//text.requestFocus();

				}
			});
			zoomWindow.pack();
		}
		public void hiddenshowZoomed(){
			if(zoomWindow != null) {
				zoomWindow.setVisible(false);
			}
		}
		//返回是否第一次
		public boolean firstTime() { return true; }
		//刷新
		public void refresh() {
			updateBackBuffer = true;
			repaint();
		}

		//设置显示的字体参数
		private void setParams( Graphics2D g2 ) {
			g2.setFont( testFont );
		}

		//绘制网格线
		private void drawGrid( Graphics2D g2 ) {
			int totalGridWidth = numCharAcross * gridWidth;
			int totalGridHeight = numCharDown * gridHeight;

			g2.setColor( Color.black );
			for ( int i = 0; i < numCharDown + 1; i++ ) {
				g2.drawLine( canvasInset_X, i * gridHeight + canvasInset_Y,
						canvasInset_X + totalGridWidth, i * gridHeight + canvasInset_Y );
			}
			for ( int i = 0; i < numCharAcross + 1; i++ ) {
				g2.drawLine( i * gridWidth + canvasInset_X, canvasInset_Y,
						i * gridWidth + canvasInset_X, canvasInset_Y + totalGridHeight );
			}
		}

		//绘制特殊字符
		public void modeSpecificDrawChar( Graphics2D g2, int charCode,
				int baseX, int baseY ) {
			GlyphVector gv;
			char charArray[] = Character.toChars( charCode );

			FontRenderContext frc = g2.getFontRenderContext();
			AffineTransform oldTX = g2.getTransform();

			/// Create GlyphVector to measure the exact visual advance
			/// Using that number, adjust the position of the character drawn
			gv = testFont.createGlyphVector( frc, charArray );
			Rectangle2D r2d2 = gv.getPixelBounds(frc, 0, 0);
			int shiftedX = baseX;
			// getPixelBounds returns a result in device space.
			// we need to convert back to user space to be able to
			// calculate the shift as baseX is in user space.
			try {
				double pt[] = new double[4];
				pt[0] = r2d2.getX();
				pt[1] = r2d2.getY();
				pt[2] = r2d2.getX()+r2d2.getWidth();
				pt[3] = r2d2.getY()+r2d2.getHeight();
				oldTX.inverseTransform(pt,0,pt,0,2);
				shiftedX = baseX - (int) ( pt[2] / 2 + pt[0] );
			} catch (NoninvertibleTransformException e) {	}

			/// ABP - keep track of old tform, restore it later
			g2.translate( shiftedX, baseY ); 
			AffineTransform at = new AffineTransform();
			g2.transform( at );

			if ( testFont.canDisplay( charCode )) {
				g2.setColor( Color.black );
			} else {
				g2.setColor( Color.lightGray );
			}
			g2.drawString( new String( charArray ), 0, 0 );

			/// ABP - restore old tform
			g2.setTransform ( oldTX );
		}

		///根据字体计算网格大小和坐标
		private void calcFontMetrics( Graphics2D g2d, int w, int h ) {
			FontMetrics fm;
			Graphics2D g2 = (Graphics2D)g2d.create();

			/// ABP
			AffineTransform at = new AffineTransform();
			g2.setFont( g2.getFont().deriveFont( at ) );
			fm = g2.getFontMetrics();

			maxAscent = fm.getMaxAscent();
			maxDescent = fm.getMaxDescent();

			/// Give slight extra room for each character
			maxAscent += 3;
			maxDescent += 3;
			gridWidth = fm.getMaxAdvance() + 6;
			gridHeight = maxAscent + maxDescent;
			numCharAcross = ( w - 10 ) / gridWidth;
			numCharDown = ( h - 10 ) / gridHeight;

			canvasInset_X = ( w - numCharAcross * gridWidth ) / 2;
			canvasInset_Y = ( h - numCharDown * gridHeight ) / 2;
			if ( numCharDown == 0 || numCharAcross == 0 )
				throw new CannotDrawException( CANT_FIT_DRAW );
		}

		//绘制文本
		private void drawText(Graphics g, int w, int h) {
			Graphics2D g2;

			// / Create back buffer when not printing, and its Graphics2D
			// / Then set drawing parameters for that Graphics2D object
			if (backBuffer == null) {
				backBuffer = (BufferedImage) this.createImage(w, h);
				g2 = backBuffer.createGraphics();
				g2.setColor(Color.white);
				g2.fillRect(0, 0, w, h);
				g2.setColor(Color.black);
				setParams(g2);
				if (updateFontMetrics) {
					calcFontMetrics(g2, w, h);
					updateFontMetrics = false;
				}

				drawGrid(g2);
				int currDraw = 0;
				int charlen = theSymbols.length;
				for (int i = 0; i < numCharDown && currDraw < charlen; i++) {
					for (int j = 0; j < numCharAcross && currDraw < charlen; j++, currDraw++) {
						int gridLocX = j * gridWidth + canvasInset_X;
						int gridLocY = i * gridHeight + canvasInset_Y;

						modeSpecificDrawChar(g2, theSymbols[currDraw], gridLocX
								+ gridWidth / 2, gridLocY + maxAscent);
					}
				}
				g2.dispose();
			}
			g.drawImage(backBuffer, 0, 0, this);

		}

		//绘制界面
		@Override
		public void paintComponent( Graphics g ) {
			if ( updateBackBuffer ) {
				Dimension d = this.getSize();
				try {
					drawText( g, d.width, d.height );
				}
				catch ( CannotDrawException e ) {
					super.paintComponent(g);
					return;
				}
			}
			else {
				/// Screen refresh
				g.drawImage( backBuffer, 0, 0, this );
			}
			updateBackBuffer = false;
		}

		//判断当前光标选择的字符
		private boolean checkMouseLoc( MouseEvent e ) {
			if ( gridWidth != 0 && gridHeight != 0 ){
				int charLocX = ( e.getX() - canvasInset_X ) / gridWidth;
				int charLocY = ( e.getY() - canvasInset_Y ) / gridHeight;
				int charlen = theSymbols.length;
				/// Check to make sure the mouse click location is within drawn area
				if ( charLocX >= 0 && charLocY >= 0 &&	
						charLocX < numCharAcross && charLocY < numCharDown ) {
					
					int i = (charLocY+1) * numCharAcross - (numCharAcross-(charLocX+1));
					i--;
					if(i>=0 && i<charlen){
						currMouseOverChar = theSymbols[i];
					} else
						return false;
					mouseOverCharX = charLocX;
					mouseOverCharY = charLocY;
					return true;
				}
			}
			return false;
		}

		//显示放大效果
		public void showZoomed() {
			Font backup = testFont;
			Point canvasLoc = this.getLocationOnScreen();

			// / Calculate the zoom area's location and size...
			int dialogOffsetX = (int) (gridWidth * (ZOOM - 1) / 2);
			int dialogOffsetY = (int) (gridHeight * (ZOOM - 1) / 2);
			int zoomAreaX = mouseOverCharX * gridWidth + canvasInset_X
					- dialogOffsetX;
			int zoomAreaY = mouseOverCharY * gridHeight + canvasInset_Y
					- dialogOffsetY;
			int zoomAreaWidth = (int) (gridWidth * ZOOM);
			int zoomAreaHeight = (int) (gridHeight * ZOOM);

			// / Position and set size of zoom window as needed
			zoomWindow.setLocation(canvasLoc.x + zoomAreaX, canvasLoc.y
					+ zoomAreaY);
			if (!nowZooming) {
				if (zoomWindow.getWarningString() != null) {
					// / If this is not opened as a "secure" window,
					// / it has a banner below the zoom dialog which makes it
					// look really BAD
					// / So enlarge it by a bit
					zoomWindow.setSize(zoomAreaWidth + 1, zoomAreaHeight + 20);
				} else {
					zoomWindow.setSize(zoomAreaWidth + 1, zoomAreaHeight + 1);
				}
			}

			// / Prepare zoomed image
			zoomImage = (BufferedImage) zoomWindow.createImage(
					zoomAreaWidth + 1, zoomAreaHeight + 1);
			Graphics2D g2 = zoomImage.createGraphics();
			testFont = testFont.deriveFont(fontSize * ZOOM);
			setParams(g2);
			g2.setColor(Color.black);
			g2.drawRect(0, 0, zoomAreaWidth, zoomAreaHeight);
			modeSpecificDrawChar(g2, currMouseOverChar, zoomAreaWidth / 2,
					(int) (maxAscent * ZOOM));
			g2.dispose();
			zoomWindow.setVisible(true);
			zoomWindow.getGraphics().drawImage(zoomImage, 0, 0, this);
			nowZooming = true;
			prevZoomChar = currMouseOverChar;
			testFont = backup;
			if (firstTime()) {
				refresh();
			}
		}

		//光标离开
		public void mouseExited( MouseEvent e ) {}

		//光标移动
		public void mouseMoved( MouseEvent e ) {
			if ( checkMouseLoc( e ) ) {
				if ( !nowZooming || currMouseOverChar != prevZoomChar){
					showZoomed();
					/*this.setCursor( blankCursor );*/
				}
			}else{
				if(nowZooming){
					nowZooming = false;
					this.setCursor( Cursor.getDefaultCursor() );
				}
				zoomWindow.setVisible(false);
			}
		}

		//鼠标点击并释放
		public void mouseClicked( MouseEvent e ) {
			if ( checkMouseLoc( e )) {
				//nowZooming = false;
				char charArray[] = Character.toChars( currMouseOverChar );
				symbols = new String(charArray);
			}
		}
		
		public void mouseEntered( MouseEvent e ) {}
		public void mouseDragged(MouseEvent e) {}
		public void mousePressed( MouseEvent e ) {}
		public void mouseReleased( MouseEvent e ) {}
	}

	private final class CannotDrawException extends RuntimeException {
		// Error ID
		public final int id;

		public CannotDrawException( int i ) {
			id = i;
		}
	}
}
