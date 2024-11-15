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
 * @NullInlineIndicator.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.WdemsActioinHandler.ActionItem;
import com.wisii.edit.tag.util.ComponentStyleUtil;

/**
 * 类功能描述：用于表示空inline的存在及其位置
 * 同时根据该指示器可以为可以为空inline添加必
 * 要的事件处理。
 * 
 * 作者：李晓光
 * 创建日期：009-9-23
 */
@SuppressWarnings("serial")
public class NullInlineIndicator implements WdemsTagComponent{
	private final NullInlineImp imp = new NullInlineImp();
	// 圆直径
	public final static int CIRCLE_DIAMETER = 12;//14;
	private Object result=null;
	public void addActions(final Actions action) {
		imp.addActions(action);
	}
	public JComponent getComponent() {
		return imp;
	}
	public void iniValue(final Object value) {
		// TODO Auto-generated method stub
		
	}
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
 	public void setValue(final Object value) {
		
	}
	public void setLocation(final Point p) {
		imp.setLocation(p);
	}
	public void setMaximumSize(final Dimension maximumSize) {
		imp.setMaximumSize(maximumSize);
	}
	public void showValidationState(final ValidationMessage action) {
		
	}
	
	/* ---------------------------------------------------------------------------------- */
	public static class NullInlineImp extends JButton/*JComponent*/{
		private final RoundButton btn = new RoundButton();
		private final WdemsLabel lab = new WdemsLabel();
		private Shape shape = null;
		private Shape clipShape = null;
		private final Paint borderPaint = Color.BLACK;
		private final BasicStroke stroke = new BasicStroke(.8F);
		private final Color start = new Color(238, 238, 238);
		private final Color end1 = new Color(204, 204, 204);
		//光标进入时，获得焦点时
		private final Color end2 = new Color(155, 151, 53);
		private final Paint DEFAULT_PAINT = getPaint(start, end1);
		private final Paint DEFAULT_PAINT_ENTER = getPaint(start, end2);
		private Paint backPaint = DEFAULT_PAINT;
		NullInlineImp(){
			/*setLayout(new BorderLayout(0, 0));
			add(btn, BorderLayout.NORTH);
			add(lab, BorderLayout.CENTER);*/
			setContentAreaFilled(false);
	        addMouseListener(new MouseAdapter(){

				@Override
				public void mouseEntered(final MouseEvent e) {
					if(isFocusOwner())
						return;
					backPaint = DEFAULT_PAINT_ENTER;
				}
				@Override
				public void mouseExited(final MouseEvent e) {
					if(isFocusOwner())
						return;
					backPaint = DEFAULT_PAINT;
				}
	        });
	        addFocusListener(new FocusAdapter(){
				@Override
				public void focusGained(final FocusEvent e) {
					backPaint = DEFAULT_PAINT_ENTER;
				}

				@Override
				public void focusLost(final FocusEvent e) {
					backPaint = DEFAULT_PAINT;
				}
	        });
	        addComponentListener(new ComponentAdapter(){

				@Override
				public void componentResized(final ComponentEvent e) {
					shape = null;
					clipShape = null;
				}
	        });
		}
		/*@Override
		public Insets getInsets() {
			return new Insets(2, 0, 0, 0);
		}*/
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(CIRCLE_DIAMETER, CIRCLE_DIAMETER * 2);
		}
		public void addActions(final Actions action){
			addActionListener(action);
		}
		
		@Override
		public void setBounds(final int x, final int y, int width, final int height) {
			if(width < CIRCLE_DIAMETER)
				width = CIRCLE_DIAMETER * 2;
			super.setBounds(x, y, width, height);
		}		
		private Line2D createLine(){
			Dimension size = getSize();
			if(size == null)
				return null;
			int centerX = CIRCLE_DIAMETER / 2;
			int y = CIRCLE_DIAMETER;
			return new Line2D.Float(centerX, y, centerX, y * 2);
		}
		private Paint getPaint(final Color... colors){
	    	Point2D start = new Point2D.Float(0, 0);
	    	Point2D end = new Point2D.Float(CIRCLE_DIAMETER / 2, CIRCLE_DIAMETER / 2);
	    	GradientPaint p = new GradientPaint(start, colors[0], end, colors[1]);
	    	return p;
	    }

		@Override
		public boolean contains(final int x, final int y) {
	        // 如果按钮改变了尺寸将重新创建一个Shape
	        if (shape == null) {
	        	Point circle = getCircle();
	            shape = new Ellipse2D.Float(circle.x, circle.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
	        }
	        return shape.contains(x, y);
	    }
	    private Point getCircle(){
	    	int x = 0;//(getWidth() - CIRCLE_DIAMETER) / 2;
	    	int y = 0;//getHeight() - CIRCLE_DIAMETER;
	    	y = y < 0 ? 0 : y;
	    	return new Point(x, y);
	    }
		@Override
		public void paint(final Graphics g) {
			Rectangle r = getVisibleRect();
			g.setClip(r);
			super.paint(g);
		}
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.addRenderingHints(ComponentStyleUtil.getRenderingHints());
			
			if (getModel().isArmed()) {
				//鼠标押下是背景变化
				g2d.setColor(start);
			} else {
				//正常显示是背景颜色
				g2d.setPaint(backPaint);
			}
			Point circle = getCircle();
			if(shape == null)
				shape = new Ellipse2D.Float(circle.x, circle.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
			g2d.fill(shape);
			
			paintLine(g2d);
			
			// 在焦点上画出一个标签
			/*super.paintComponent(g);*/			
		}
		private void paintLine(Graphics2D g2d){
			final Paint borderPaint = new Color(102, 102, 102);
		
			g2d.setPaint(borderPaint);
			g2d.draw(createLine());
		}
		// 画出一个边框
		@Override
		protected void paintBorder(final Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setClip(getVisibleRect());
			g2d.addRenderingHints(ComponentStyleUtil.getRenderingHints());
			g2d.setPaint(borderPaint);
			g2d.setStroke(stroke);
			Point circle = getCircle();
			int x = circle.x;//(getWidth() - CIRCLE_DIAMETER) / 2; 
			x = x < 1 ? 1 : x + 1;
			int y = circle.y;//getHeight() - CIRCLE_DIAMETER;
			y = y < 1 ? 1 : y + 1;
			g2d.drawOval(x, y, CIRCLE_DIAMETER - 2, CIRCLE_DIAMETER - 2);//(1, 1, getSize().width - 2, getSize().height - 2);
		}
	}
	/* ---------------------------------------------------------------------------------- */
	private static class RoundButton extends JButton{
		private Shape shape = null;
		private Shape clipShape = null;
		private final Paint borderPaint = Color.BLACK;
		private final BasicStroke stroke = new BasicStroke(.8F);
		private final Color start = new Color(238, 238, 238);
		private final Color end1 = new Color(204, 204, 204);
		//光标进入时，获得焦点时
		private final Color end2 = new Color(155, 151, 53);
		private final Paint DEFAULT_PAINT = getPaint(start, end1);
		private final Paint DEFAULT_PAINT_ENTER = getPaint(start, end2);
		private Paint backPaint = DEFAULT_PAINT;
		
		RoundButton(){
			setPreferredSize(new Dimension(CIRCLE_DIAMETER, CIRCLE_DIAMETER));
			//不让JButton画背景而允许我们去画一个圆背景
	        setContentAreaFilled(false);
	        addMouseListener(new MouseAdapter(){

				@Override
				public void mouseEntered(final MouseEvent e) {
					if(isFocusOwner())
						return;
					backPaint = DEFAULT_PAINT_ENTER;
				}
				@Override
				public void mouseExited(final MouseEvent e) {
					if(isFocusOwner())
						return;
					backPaint = DEFAULT_PAINT;
				}
	        });
	        addFocusListener(new FocusAdapter(){
				@Override
				public void focusGained(final FocusEvent e) {
					backPaint = DEFAULT_PAINT_ENTER;
				}

				@Override
				public void focusLost(final FocusEvent e) {
					backPaint = DEFAULT_PAINT;
				}
	        });
	        addComponentListener(new ComponentAdapter(){

				@Override
				public void componentResized(final ComponentEvent e) {
					shape = null;
					clipShape = null;
				}
	        });
		}
	    // 画出圆的背景和标签
	    @Override
		protected void paintComponent(final Graphics g) {
	    	Graphics2D g2d = (Graphics2D)g;
	    	g2d.addRenderingHints(ComponentStyleUtil.getRenderingHints());
	    	
	        if (getModel().isArmed()) {
	        	//鼠标押下是背景变化
	        	g2d.setColor(start);
	        } else {
	        	//正常显示是背景颜色
	        	g2d.setPaint(backPaint);
	        }
	        Point circle = getCircle();
	        if(shape == null)
	        	shape = new Ellipse2D.Float(circle.x, circle.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);//(0, 0, getWidth(), getHeight());
	        g2d.fill(shape);
	        
	        if(shape != null ){
	        	if(clipShape == null)
	        		clipShape = new Ellipse2D.Float(circle.x, circle.y, CIRCLE_DIAMETER + 2, CIRCLE_DIAMETER + 2);//(0, 0, getWidth() + 2, getHeight() + 2);
	        	g2d.setClip(clipShape);
	        }
	        
	        // 在焦点上画出一个标签
	        super.paintComponent(g);

	    }
	    private Paint getPaint(final Color... colors){
	    	Point2D start = new Point2D.Float(0, 0);
	    	Point2D end = new Point2D.Float(CIRCLE_DIAMETER / 2, CIRCLE_DIAMETER / 2);
	    	GradientPaint p = new GradientPaint(start, colors[0], end, colors[1]);
	    	return p;
	    }

		@Override
		public boolean contains(final int x, final int y) {
	        // 如果按钮改变了尺寸将重新创建一个Shape
	        if (shape == null) {
	        	Point circle = getCircle();
	            shape = new Ellipse2D.Float(circle.x, circle.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
	        }
	        return shape.contains(x, y);
	    }
		 // 画出一个边框
	    @Override
		protected void paintBorder(final Graphics g) {
	    	Graphics2D g2d = (Graphics2D)g;
	    	g2d.setClip(getVisibleRect());
	    	g2d.addRenderingHints(ComponentStyleUtil.getRenderingHints());
	    	g2d.setPaint(borderPaint);
	    	g2d.setStroke(stroke);
	    	Point circle = getCircle();
	    	int x = circle.x;//(getWidth() - CIRCLE_DIAMETER) / 2; 
	    	x = x < 1 ? 1 : x + 1;
	    	int y = circle.y;//getHeight() - CIRCLE_DIAMETER;
	    	y = y < 1 ? 1 : y + 1;
	    	g2d.drawOval(x, y, CIRCLE_DIAMETER - 2, CIRCLE_DIAMETER - 2);//(1, 1, getSize().width - 2, getSize().height - 2);
	    }
	    private Point getCircle(){
	    	int x = 0;//(getWidth() - CIRCLE_DIAMETER) / 2;
	    	int y = 0;//getHeight() - CIRCLE_DIAMETER;
	    	y = y < 0 ? 0 : y;
	    	return new Point(x, y);
	    }
	}
	/* ---------------------------------------------------------------------------------- */
	private static class WdemsLabel extends JLabel{
		private final Paint borderPaint = new Color(102, 102, 102);//Color.BLACK;
		@Override
		protected void paintComponent(final Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			Line2D line = createLine();
			if(line == null)
				return;
			g2d.addRenderingHints(ComponentStyleUtil.getRenderingHints());
			g2d.setPaint(borderPaint);
			g2d.draw(line);
		}
		private Line2D createLine(){
			Dimension size = getSize();
			if(size == null)
				return null;
			int centerX = CIRCLE_DIAMETER / 2;//size.width / 2;
			return new Line2D.Float(centerX, 0, centerX, size.height);
		}
	}
	/* ---------------------------------------------------------------------------------- */
	public static void main0(final String[] args) {
		final JFrame fr = new JFrame();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setLayout(new FlowLayout());
		fr.setSize(400, 300);
		
		ImageIcon icon = new ImageIcon(SystemUtil.getImagesPath("accept.png"));
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = image.createGraphics();

		g2d.setColor(Color.BLACK);
		
		g2d./*drawOval*/setClip(new Ellipse2D.Float(0, 0, icon.getIconWidth(), icon.getIconHeight()));
		g2d.drawImage(icon.getImage(), 0, 0, fr);
		g2d.dispose();
		
		ImageComponent comp = new ImageComponent(image);
		comp.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseEntered(final MouseEvent e) {
				System.out.println("------------------------------------");
			}
			@Override
			public void mouseExited(final MouseEvent e) {
				System.out.println("=====================================");
			}
		});
		
		WdemsTagComponent tag =new NullInlineIndicator();
		tag.addActions(new Actions(){

			@SuppressWarnings("static-access")
			@Override
			public Object doAction(final ActionEvent e) {
				JColorChooser ch = new JColorChooser();
				ch.showDialog(fr, "", null);
				return null;
			}

			@Override
			public boolean updateXML() {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		JButton btn = new JButton("test");
		
		try {
			Action a = WdemsActioinHandler.createAction("com.wisii.edit.tag.components.action.Test", "customer");
			if(a != null){
				WdemsActioinHandler.bindActions(btn, ActionItem.newInstance("ctrl T", "customer", a));
			}
			/*WdemsActioinHandler.bindActions(btn, ActionItem.newInstance("ctrl R", "pageup", a));*/
			Action b = WdemsActioinHandler.createAction("com.wisii.edit.tag.components.action.Test", "customer");
			if(b != null){
				WdemsActioinHandler.bindActions(btn, ActionItem.newInstance("ctrl R", "pageup", b));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*WdemsTestUtil.UITest(btn);*/
		fr.getContentPane().add(tag.getComponent());
		fr.getContentPane().add(new JButton("Test"));
		fr.getContentPane().add(new JCheckBox("SB"));
		fr.getContentPane().add(comp);
		fr.getContentPane().add(btn);
		fr.setVisible(true);
	}
	public Object getActionResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setActionResult(Object result) {
		this.result = result;
	}
	@Override
	public void setDefaultValue(String value) {
	}
	@Override
	public boolean canInitDefaultValue() {
		return false;
	}
	@Override
	public void initByDefaultValue() {
	}
}
