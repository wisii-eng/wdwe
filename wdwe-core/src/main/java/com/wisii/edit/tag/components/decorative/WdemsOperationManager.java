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
 * @WdemsOperationManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.edit.tag.components.balloontip.BalloonTip;
import com.wisii.edit.tag.components.balloontip.styles.RoundedBalloonStyle;
import com.wisii.edit.tag.components.select.AbstractWdemsCombox;
import com.wisii.edit.tag.util.WdemsTagUtil;


/**
 * 类功能描述：用于控件在控件的上方显示添加、删除按钮，
 * 显示控件的边框。
 * 
 * 1、鼠标进入指定控件显示控件的
 * 边框。同时，在控件的上方弹出一
 * 个操作平台，用于控制添加、删除
 * 节点操作。
 * 2、当鼠标既不在指定的控件内，
 * 也不再操作平台内，则隐藏边框
 * 和控制平台。
 * 
 * 作者：李晓光
 * 创建日期：2009-8-11
 */
@SuppressWarnings("serial")
public class WdemsOperationManager {
	private final static OperationComponentImp imp = new OperationComponentImp();
	private final static boolean isTipStyple = Boolean.TRUE;
	private final static Handler handler = new Handler();
	private static ConcurrentHashMap<Component, List<VirtualButton>> map = new ConcurrentHashMap<Component, List<VirtualButton>>();
	public final static void registerComponent(JComponent comp){
		registerComponent(comp, null);
	}
	public final static void registerComponent(JComponent comp, List<VirtualButton> buttonlist){
		if(comp == null || buttonlist == null || buttonlist.isEmpty()) {
			return;
		}
		JComponent eventComp = getEventComp(comp);
		map.put(eventComp, buttonlist);
		if(!isTipStyple){
			registerListener(eventComp);			
		}else{
			registerEvent(eventComp, comp);
		}
	}
	private final static void registerEvent(JComponent component, JComponent parent){
		BalloonTip old = WdemsTagUtil.getBalloonTip(parent);
		String text = "";
		if(old != null){
			text = old.getText();
			old.closeBalloon();			
		}
		final BalloonTip tip = new BalloonTip(component, text, new RoundedBalloonStyle(5,5,Color.WHITE, Color.BLACK), Boolean.FALSE);
		List<EditButton> editButtons = FloatPanel.getButttons(map.get(component));
		for (EditButton editButton : editButtons) {
			tip.add(editButton);
		}
		tip.add(Box.createHorizontalStrut(5));
		registerListener(component, new EventImp(tip));
	}
	static void registerListener(JComponent comp, final EventImp imp){
		comp.addMouseListener(imp);
		if(!(comp instanceof JTextComponent)){
			comp.addComponentListener(imp);
			return;
		}
		JTextComponent text = (JTextComponent)comp;
		Document doc = text.getDocument();
		doc.addDocumentListener(imp);
		
		Window window = SwingUtilities.getWindowAncestor(WdemsOperationManager.imp);
		if(window != null){
			window.addComponentListener(imp);
		}
		
		java.security.AccessController
		.doPrivileged(new java.security.PrivilegedAction() {
			public Object run() {
				Toolkit.getDefaultToolkit().addAWTEventListener(
						imp,
						AWTEvent.MOUSE_EVENT_MASK
								| AWTEvent.MOUSE_MOTION_EVENT_MASK
								| AWTEvent.MOUSE_WHEEL_EVENT_MASK);
				return null;
			}
		});
		
		WdemsActioinHandler.bindActions(comp, BindType.Float);
	}
	private static class EventImp extends MouseAdapter implements ComponentListener, DocumentListener, AWTEventListener{
		final BalloonTip tip;
		EventImp(final BalloonTip tip){
			this.tip = tip;
			tip.enableClickToHide(Boolean.TRUE);
			hidden();
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			display();
		}

		@Override
		public void mouseExited(MouseEvent e) {
//			hidden();
		}

		
		public void componentHidden(ComponentEvent e) {
			hidden();
		}

		
		public void componentMoved(ComponentEvent e) {
			hidden();
		}

		
		public void componentResized(ComponentEvent e) {
			hidden();
		}

	
		public void componentShown(ComponentEvent e) {}

	
		public void changedUpdate(DocumentEvent e) {
			hidden();
		}

		
		public void insertUpdate(DocumentEvent e) {
			hidden();
		}

		
		public void removeUpdate(DocumentEvent e) {
			hidden();
		}
		
		public void eventDispatched(AWTEvent ev) {
			switch (ev.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                Component src = (Component)ev.getSource();
                if (isIn(src)) {
					return;
				}
                if (!(src instanceof JComponent)){
                	hidden();
                	((MouseEvent)ev).consume();
                }
                break;

            case MouseEvent.MOUSE_MOVED:
            	if(!tip.isShowing()) {
					return;
				}
            	Rectangle r = new Rectangle(0, 0, tip.getWidth(), tip.getHeight());
            	MouseEvent evt = (MouseEvent)ev;
            	JComponent component = tip.getAttachedComponent();
            	Rectangle r0 = new Rectangle(0, 0, component.getWidth(), component.getHeight());
            	
            	Point p0 = SwingUtilities.convertPoint(evt.getComponent(), evt.getPoint(), component);
            	Point p = SwingUtilities.convertPoint(evt.getComponent(), evt.getPoint(), tip);            	
            	if(r.outcode(p) != 0 && r0.outcode(p0) != 0) {
            		hidden();
				}
                break;
            case MouseEvent.MOUSE_DRAGGED:
            	if (isIn((Component)ev.getSource()) || imp.getComponentCount() > 0) {
					return;
				}
            	hidden();
                break;
            case MouseEvent.MOUSE_WHEEL:
                if (isIn((Component)ev.getSource())) {
					return;
				}
                hidden();
                break;
            }
		}
		boolean isIn(Component src) {
			for (Component c = src; c != null; c = c.getParent()) {
				if (c instanceof Applet || c instanceof Window) {
					break;
				} else if (c instanceof BalloonTip) {
					return true;
				}
			}
			return false;
		}
		private void display(){
			tip.refreshLocation();
			tip.setVisible(Boolean.TRUE);
		}
		private void hidden(){
			tip.setVisible(Boolean.FALSE);
		}
	}
	public final static JComponent getComponent(){
		return imp;
	}
	public final static void distroy(){
		imp.setVisible(Boolean.FALSE);
	}
	public final static void clearDump(){
		map.clear();
		imp.removeAll();
		imp.repaint();
		WdemsTagUtil.clear();
	}
	
	public final static void showOptionPopup(ActionEvent e){
		JComponent comp = (JComponent)e.getSource();
		
		FloatComponent floating = new FloatComponent(comp);
		FloatPanel panel = new FloatPanel(comp, map.get(comp));
		floating.setCenter(panel);
		imp.removeAll();
		imp.add(floating);
		
		updateUI(floating);
	}
	public final static void hideOptionPopup(ActionEvent e){
		cancel();
	}
	private final static void updateUI(final FloatComponent floating){
		Thread t = new Thread(){
			@Override
			public void run() {
				imp.validate();					
				imp.repaint(imp.getBounds());
			}
		};
		SwingUtilities.invokeLater(t);
	}
	
	private final static JComponent getEventComp(JComponent comp){
		if(comp instanceof AbstractWdemsCombox) {
			return ((AbstractWdemsCombox) comp).getEditor();
		} else if(comp instanceof JComboBox){
			JComboBox box = (JComboBox)comp;
			if(box.isEditable()){
				return (JComponent)box.getEditor().getEditorComponent();
			}else{
				return (JComponent)box.getRenderer().getListCellRendererComponent(new JList(), null, 0, false, false);
			}
		}
		return comp;
	}
	static void registerListener(JComponent comp){
		comp.addMouseListener(handler);
		if(!(comp instanceof JTextComponent)){
			comp.addComponentListener(handler);
			return;
		}
		JTextComponent text = (JTextComponent)comp;
		Document doc = text.getDocument();
		doc.addDocumentListener(handler);
		
		WdemsActioinHandler.bindActions(comp, BindType.Float);
	}
	static void cancel(){
		imp.removeAll();
		imp.repaint(imp.getBounds());
	 }
	static boolean isIn(Component src) {
		for (Component c = src; c != null; c = c.getParent()) {
			if (c instanceof Applet || c instanceof Window) {
				break;
			} else if (c instanceof FloatComponent) {
				return true;
			}
		}
		return false;
	}
	private final static class OperationComponentImp extends JComponent{
		OperationComponentImp(){
			setLayout(null);
			setOpaque(false);
		}
		@Override
		public boolean contains(int x, int y) {
			if(getComponentCount() == 0) {
				return Boolean.FALSE;
			}
			Component comp = getComponent(0);
			if(!(comp instanceof FloatComponent)) {
				return Boolean.FALSE;
			}
			Point p = SwingUtilities.convertPoint(this, x, y, comp);
			return getComponent(0).contains(p.x, p.y);
		}
	}
		
	private final static class FloatComponent extends JPanel{
		private final Color color = new Color(82, 147, 227);
		private final int height = 23;
		private Border border = new AreaBorder();
		private final JPanel center = new JPanel(new BorderLayout());
		private final Box south = Box.createHorizontalBox();
		private Window window = null;
		
		FloatComponent(JComponent comp){
			this.setOpaque(Boolean.FALSE);
			
			//设置该控件的边框。
//			setBorder(border);
			
			setBounds(getRect(comp));
			setLayout(new BorderLayout(0, 0));
			Dimension dim = comp.getSize();
			//用于透明显示底层的控件
			south.setPreferredSize(new Dimension(dim));
			south.setOpaque(Boolean.FALSE);
			
			center.setBackground(color);
			
			add(south, BorderLayout.SOUTH);
			add(center, BorderLayout.CENTER);
			grabWindow();
			handler.setComponent(this);
		}
		
		void setCenter(JComponent center){
			Border b = BorderFactory.createEmptyBorder(2, 2, 2, 2);
			border = BorderFactory.createCompoundBorder(border, b);
			this.center.setBorder(border);
			
			this.center.add(center, BorderLayout.CENTER);
		}
		@Override
		public boolean contains(int x, int y) {
			Point p = SwingUtilities.convertPoint(this, x, y, south);
			return super.contains(x, y) && !(south.contains(p.x, p.y));
		}
		private Rectangle getRect(JComponent comp){
			//数值来自其边框的宽度
			int step = 4;
			Rectangle r = comp.getBounds();
			
			/*Point p = SwingUtilities.convertPoint(comp, r.x, r.y, imp);*/
			Point p = SwingUtilities.convertPoint(comp, 0, 0, imp);
			r.setLocation(p.x - step / 2, p.y - step * 2 - height);
			r.setSize(/*r.width*/60 + 2 * step, r.height + 2 * step + height);
			return r;
		}
		 @SuppressWarnings("unchecked")
		void grabWindow() {
			// A grab needs to be added
			java.security.AccessController
					.doPrivileged(new java.security.PrivilegedAction() {
						public Object run() {
							Toolkit.getDefaultToolkit().addAWTEventListener(
									handler,
									AWTEvent.MOUSE_EVENT_MASK
											| AWTEvent.MOUSE_MOTION_EVENT_MASK
											| AWTEvent.MOUSE_WHEEL_EVENT_MASK);
							return null;
						}
					});
			
			if(window == null) {
				window = SwingUtilities.getWindowAncestor(imp);
			}
			if (window != null) {
				window.addComponentListener(handler);
				window.addWindowListener(handler);
			}
		}
		
	}
	
	//事件监听
	private final static class Handler extends MouseAdapter implements AWTEventListener, ComponentListener, WindowListener, DocumentListener {
		private Component comp = null;
		void setComponent(Component comp){
			this.comp = comp;
		}
		
		public void changedUpdate(DocumentEvent e) {
			cancel();
		}

		public void insertUpdate(DocumentEvent e) {
			cancel();
		}

		public void removeUpdate(DocumentEvent e) {
			cancel();
		}
		public void eventDispatched(AWTEvent ev) {
			switch (ev.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                Component src = (Component)ev.getSource();
                if (isIn(src)) {
					return;
				}
                if (!(src instanceof JComponent)){
                	cancel();
                	((MouseEvent)ev).consume();
                }
                break;

            case MouseEvent.MOUSE_MOVED:
            	if(!comp.isShowing()) {
					return;
				}
            	Rectangle r = new Rectangle(0, 0, comp.getWidth(), comp.getHeight());
            	MouseEvent evt = (MouseEvent)ev;
            	Point p = SwingUtilities.convertPoint(evt.getComponent(), evt.getPoint(), comp);
            	if(r.outcode(p) != 0) {
					cancel();
				}
                break;
            case MouseEvent.MOUSE_DRAGGED:
            	if (isIn((Component)ev.getSource()) || imp.getComponentCount() > 0) {
					return;
				}
            	cancel();
                break;
            case MouseEvent.MOUSE_WHEEL:
                if (isIn((Component)ev.getSource())) {
					return;
				}
                cancel();
                break;
            }
		}
		public void componentHidden(ComponentEvent e) {
			cancel();
		}
		public void componentMoved(ComponentEvent e) {
			cancel();
		}
		public void componentResized(ComponentEvent e) {
			cancel();
		}
		public void componentShown(ComponentEvent e) {
			cancel();
		}
		public void windowClosed(WindowEvent e) {
			cancel();
		}
		public void windowClosing(WindowEvent e) {
			cancel();
		}
		public void windowDeactivated(WindowEvent e) {
			cancel();
		}
		public void windowIconified(WindowEvent e) {
			cancel();
		}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			Point p = e.getPoint();			
			
			if(comp != null){
				Point p0 = SwingUtilities.convertPoint(e.getComponent(), p, comp);
				Rectangle r = new Rectangle(0, 0, comp.getWidth(), comp.getHeight());
				if(r.contains(p0)) {
					return;
				}
			}
			
			Component source = e.getComponent();
			
			SwingUtilities.convertPointToScreen(p, source);
			
			JComponent comp = (JComponent)source;
			
			FloatComponent floating = new FloatComponent(comp);
			FloatPanel panel = new FloatPanel(comp, map.get(comp));
			floating.setCenter(panel);
			imp.removeAll();
			imp.add(floating);
			
			updateUI(floating);
		}
	}
}
