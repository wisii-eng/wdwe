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
 * @WedmsTabelCombox.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.graphic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.components.select.Data;
import com.wisii.edit.tag.schema.wdems.Graphic;
import com.wisii.edit.tag.util.WdemsTagUtil;
import com.wisii.edit.util.EditUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.ImageFactory;

/**
 * 
 * Desc:上传图片控件
 * 
 * @author xieli
 * 
 *         2016-9-29下午09:29:27
 */
@SuppressWarnings("serial")
public class GraphicCom extends JButton implements WdemsTagComponent {

	private Actions action;
	private Object result = null;
	private final Graphic input;
	private String defaultvalue;
	private boolean isedit = false;
	private BufferedImage img;

	public GraphicCom(final Graphic input) {
		this.input = input;
		initialComponentActions();
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(new Color(201, 221, 252));
//		this.setVerticalTextPosition(JButton.BOTTOM);
//		this.setHorizontalTextPosition(JButton.CENTER);
//		this.setHorizontalAlignment(JButton.CENTER);
		final Component c = this;
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				c.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				c.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		WdemsActioinHandler.bindActions(this, BindType.SingleLineInput);
	}

	// 该控件最基本的附加功能
	private void initialComponentActions() {
//		this.setToolTipText("点击上传图片");
	}

	public void addActions(final Actions action) {
		this.action = action;
		this.addActionListener(action);

		if (this.input != null) {

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					String src = input.getSrc();
					try {
						final GraphicUploadDialog dialog = new GraphicUploadDialog(GraphicCom.this);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setTitle("图片上传");
						dialog.setModal(true);
						dialog.setLocationRelativeTo(EngineUtil.getEnginepanel());
						dialog.setVisible(true);
					} catch (Exception e2) {
						// TODO 
						e2.printStackTrace();
					}
				}
			});

			this.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Object oldValue = evt.getOldValue();
					Object newValue = evt.getNewValue();
					if(oldValue instanceof String && newValue instanceof String){
						WdemsTagUtil.updateXML(action.getXPath(), getValue());
					}
					
				}
			});
		}
	}

	public JComponent getComponent() {
		return this;
	}
	String[] a = null;

	public void setValue(final Object value) {
		if (value == null) {
			// TODO 应该报个什么错
		} else {
			edit();
			this.setText(value.toString());
			iniValue(value);
			// 需要主动发action动作，要不不执行
			// this.postActionEvent();
		}
	}

	public Object getValue() {
		return this.getText();
	}
	class Dm implements Data<String>{

		
		@Override
		public Collection<String> getCellsOf(int... indexes) {
			
			return null;
		}

		@Override
		public Object getObject(int column) {
			return a[column-1];
		}

		@Override
		public Object getObject(int row, int column) {
			return a[column-1];
		}
		
	}

	public void iniValue(final Object value) {
		this.removeActionListener(action);
		String src = (String)value;
		Image image = null;
		
		
//		if(src.startsWith(SystemUtil.HTTPSCHEME) )
//        {// 绝对路径
////			JOptionPane.showMessageDialog(null, "111:" + src);
//			try {
//				URL file = new URL(src);
//				img = ImageIO.read(file);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        }else if(src.startsWith(SystemUtil.FILESCHEME)){
//        	try {
//        		File file = new File(src);
//				img = ImageIO.read(file);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        }
//        else
//        {// 相对base url的路径，并且图片必须放到base url路径下的graphics目录中
//        	String url = SystemUtil.getURL(src);
//        	if(url.startsWith(SystemUtil.HTTPSCHEME)){
//        		src = url + File.separator + SystemUtil.GRAPHICSRELATIVEPATH + src;
//        		try {
//    				URL file = new URL(src);
//    				img = ImageIO.read(file);
//    			} catch (IOException e) {
//    				e.printStackTrace();
//    			}
//        	}else{
//        		String property = System.getProperty("user.dir");
//        		src = property + File.separator + SystemUtil.GRAPHICSRELATIVEPATH + src;
//        		try {
//            		File file = new File(src);
//    				img = ImageIO.read(file);
//    			} catch (IOException e) {
//    				e.printStackTrace();
//    			}
//        	}
////            JOptionPane.showMessageDialog(null, "222:" + src);
//        }
//		BufferedImage zoomImage = zoomImage(img, 0.9, 0.9);
//		GraphicCom.this.setIcon(new ImageIcon(zoomImage));
		
		FOUserAgent foUserAgent = EngineUtil.getEnginepanel().getFOUserAgent();
//System.out.println("GraphicCom.iniValue()" + src);
		try {
			image = getImage(src, foUserAgent);
			GraphicCom.this.setIcon(new ImageIcon(image));
//			process();
		} catch (Exception e1) {
			String url = SystemUtil.getURL(src);
			if (url.startsWith(SystemUtil.HTTPSCHEME)) {
				src = url + File.separator + SystemUtil.GRAPHICSRELATIVEPATH + "imageunexist.gif";
				image = getImage(src, foUserAgent);
//				try {
//					URL file = new URL(src);
//					img = ImageIO.read(file);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			} else {
				String property = System.getProperty("user.dir");
				src = property + File.separator + SystemUtil.GRAPHICSRELATIVEPATH
						+ "imageunexist.gif";
				image = getImage(src, foUserAgent);
//				try {
//					File file = new File(src);
//					img = ImageIO.read(file);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
			GraphicCom.this.setIcon(new ImageIcon(image));
//			e1.printStackTrace();
		}
		EngineUtil.getEnginepanel().updateUI();
		// 通过文件的方式获取BufferedImage
//		try {
//			File file = new File(src);
//			img = ImageIO.read(file);
//			GraphicCom.this.setIcon(new ImageIcon(img));
//		} catch (IOException e1) {
			// TODO Auto-generated catch block
//			src = property + File.separator + "graphics" + File.separator + "imageunexist.gif";
//			File file2 = new File(src);
//			try {
//				img = ImageIO.read(file2);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			GraphicCom.this.setIcon(new ImageIcon(img));
//			e1.printStackTrace();
//		}

		
		if (value == null || value.toString().isEmpty()) {
			if (canInitDefaultValue()) {
				this.setBorder(BorderFactory.createLineBorder(Color.yellow));
				this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						edit();
					}
				});
			}
		} else {
			/**
			 * if (!SwingUtilities.isEventDispatchThread()) { SwingUtilities.invokeLater(new Runnable() {
			 * 
			 * public void run() { setText(value.toString()); } }); } else{ this.setText(value.toString()); }
			 */
			// swing控件不会主动判断是否在swing线程中，这里需要强制判断一下
			if ("@null".equals(value)) {
				this.setText("");
			} else {
				this.setText(value.toString());
			}
			isedit = true;
		}
		this.addActionListener(action);
	}

	public void showValidationState(final ValidationMessage vAction) {

		Boolean b = vAction.getValidationState();

		if (b != null) {
			if (b) {
				WdemsWarningManager.registerAccept(this);
			} else {
				WdemsWarningManager.registerWarning(this);
			}
		}
	}

//	@Override
//	protected void paintComponent(final Graphics g) {
//		Graphics2D graphics = (Graphics2D) g;
//		graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());
//
//		super.paintComponent(graphics);
//
//		// 下面是添加背景提示：
//		Color original = g.getColor();
//
////		String s = this.input.getHint();
//		String s = "点击上传图片";
////		if (this.getDocument().getLength() == 0 && s != null && !"".equals(s)) {
//
//			Graphics2D g2d = (Graphics2D) g;
//			
//			Rectangle2D r = getFont().getStringBounds(s, graphics.getFontRenderContext());
//			Rectangle bound = getBounds();
//			Insets inset = this.getBorder().getBorderInsets(this);
//			LineMetrics line = getFont().getLineMetrics(s, graphics.getFontRenderContext());
//
//			int x = inset.left;
//			int y = (int) ((bound.height - r.getHeight() - inset.top - inset.bottom) / 2 + line.getAscent());
//
//			g2d.setColor(Color.gray);
//			g2d.drawString(s, x, y);
////			g2d.drawImage(img,null,x,y);
//
////		}
//		// this.set
//
//		// System.out.println("x:" + getX() + " y:" + getAlignmentY() +
//		// " width:" + getWidth() + " heigth:" + getHeight());
//		g.setColor(original);
//
//	}

	public Object getActionResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setActionResult(Object result) {
		this.result = result;
	}

	@Override
	public void setDefaultValue(String value) {
		if (value == null) {
			return;
		}
		value = value.trim();
		if (value.isEmpty()) {
			return;
		}
		this.defaultvalue = value;
	}

	@Override
	public boolean canInitDefaultValue() {
		return !isedit && defaultvalue != null;
	}

	@Override
	public void initByDefaultValue() {
		if (canInitDefaultValue()) {
			setValue(defaultvalue);
			defaultvalue = null;
		}
	}
	private void edit() {
		isedit = true;
		this.setBorder(BorderFactory.createEmptyBorder());
	}
	protected java.awt.Image getImage(String url, FOUserAgent userAgent)
	{
		java.awt.Image awtImage = null;
		url = ImageFactory.getURL(url);
		ImageFactory fact = userAgent.getImageFactory();
		FovImage fovimage = fact.getImage(url, userAgent);
		if (fovimage == null)
			return null;
		if (!fovimage.load(FovImage.DIMENSIONS))
			return null;
		int w = fovimage.getWidth();
		int h = fovimage.getHeight();
		String mime = fovimage.getMimeType();
		if ("text/xml".equals(mime))
		{
			System.err.println("text/xml images are not supported by this renderer");
		}
		else if ("image/svg+xml".equals(mime))
		{
			System.err.println("image/svg+xml images are not supported by this renderer");
		}
		else if ("image/eps".equals(mime))
		{
			System.err.println("EPS images are not supported by this renderer");
		}
		else
		{
			if (!fovimage.load(FovImage.BITMAP))
			{
				System.err.println("Loading of bitmap failed: " + url);
				return null;
			}
			byte[] raw = fovimage.getBitmaps();
			ColorModel cm = new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
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
	public void process() {
		final EnginePanel enginepanel = EngineUtil.getEnginepanel();
		// 将控件都remove掉
		WdemsTagManager.Instance.clearCurrentPageComponents();
		EditUtil.saveXmlToWisiiBean();
		Thread t = new Thread() {
			@Override
			public void run() {
				enginepanel.doreLayout();
			}
		};
		t.start();
		EditStatusControl.reload();

	}
}
