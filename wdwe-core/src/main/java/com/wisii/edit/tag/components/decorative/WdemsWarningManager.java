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
 * @WdemsWarningComponentManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.awt.AlphaComposite;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.wisii.component.startUp.SystemUtil;
/**
 * 类功能描述：用于为控件添加警告标示，如：指定的控件数据通过验证，可以为添加一个表示
 * 数据合法的小图标，如果数据部合理，可以添加一个红叉的小图标。
 * 1、绘制位置，由指定的JComponent位置计算而来。
 * 2、绘制JLayerPane上。
 * 3、图标是否要做成半透明的？
 * 4、。
 * 
 * 
 * 作者：李晓光
 * 创建日期：2009-7-6
 */
@SuppressWarnings("serial")
public class WdemsWarningManager {
	/* 默认表示警告的图标 */
	private final static Icon WARNING_ICON = new ImageIcon(SystemUtil.getImagesPath("warning.png"));
	/* 默认表示验证通过的图标 */
	private final static Icon ACCEPT_ICON = new ImageIcon(SystemUtil.getImagesPath("accept.png"));
	/* 处理实现类 */
	private final static WarningComponentImp imp = new WarningComponentImp();
	public final static void registerWarning(JComponent comp){
		imp.addWarning(comp);
	}
	public final static void registerAccept(JComponent comp){
		imp.addAccept(comp);
	}
	public final static void registerWarning(JComponent comp, Icon icon){
		imp.addMarker(comp, icon);
	}
	public final static void registerAccept(JComponent comp, Icon icon){
		imp.addMarker(comp, icon);
	}
	public final static void removeRegister(JComponent comp){
		imp.removeMarker(comp);
	}
	public final static void setDefaultWarning(Icon icon){
		imp.setWarningIcon(icon);
	}
	public final static void setDefaultAccept(Icon icon){
		imp.setAcceptIcon(icon);
	}
	public final static void clearDump(){
		imp.clear();
	}
	public final static JComponent getComponent(){
		return imp;
	}
	private static class WarningComponentImp extends JComponent{
		private static Map<JComponent, Icon> warnings = new ConcurrentHashMap<JComponent, Icon>();
	    private Icon warningIcon = WARNING_ICON;
	    private Icon acceptIcon = ACCEPT_ICON;
	    
	    public void addWarning(JComponent comp) {	    	
	    	addMarker(comp, WARNING_ICON);
	    }
	    public void addAccept(JComponent comp){
	    	addMarker(comp, acceptIcon);
	    }
	    public void addMarker(JComponent comp, Icon icon){
	    	warnings.put(comp, icon);
	    	repaintBadge(comp);
	    }
	    public void removeMarker(JComponent comp) {
	    	if(!warnings.containsKey(comp))
	    		return;
	    	warnings.remove(comp);
	    	repaintBadge(comp);
	    }
	    public int getWaringComponentCount(){
	    	return warnings.size();
	    }
	    public Icon getWarningIcon() {
			return warningIcon;
		}
	    public Icon getAcceptIcon() {
	    	return acceptIcon;
	    }
		public void setWarningIcon(Icon warningIcon) {
			this.warningIcon = warningIcon;
		}
		public void setAcceptIcon(Icon acceptIcon) {
			this.acceptIcon = acceptIcon;
		}
		public void clear(){
			this.removeAll();
			warnings.clear();
		}
		@Override
		public boolean contains(int x, int y) {
			return Boolean.FALSE;
		}
		@Override
	    protected void paintComponent(Graphics g) {
			for (JComponent invalid : warnings.keySet()) {
	        	Icon icon = getIcon(invalid);
	        	if(icon == null)
	        		continue;
	        	
	            if (invalid.getParent() instanceof JViewport) {
	                JViewport viewport = (JViewport) invalid.getParent();
	                // the parent of the viewport is a JScrollPane
	                invalid = (JComponent) viewport.getParent();
	            }
	            
//	            Point p = invalid.getLocationOnScreen();
//	            SwingUtilities.convertPointFromScreen(p, this);
	            
//	            int x = p.x - warningIcon.getWidth() / 2;
//	            int y = (int) (p.y + invalid.getHeight() - warningIcon.getHeight() / 1.5);
	            int x = getOffsetX(invalid, icon);
	            int y = getOffsetY(invalid, icon);
	            if(x == -1 || y == -1)
		    		continue;
	            if (g.getClipBounds().intersects(x, y,
	            		icon.getIconWidth(), icon.getIconHeight())) {
	            	Graphics2D g2d = (Graphics2D)g;
	            	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
	            	icon.paintIcon(this, g2d, x, y);
	            }
	        }
	    }
	    private void repaintBadge(JComponent comp) {
	    	/*Point p = comp.getLocationOnScreen();
	    	SwingUtilities.convertPointFromScreen(p, this);*/
	    	
	    	/*int x = p.x - warningIcon.getWidth() / 2;
	        int y = (int) (p.y + field.getHeight() - warningIcon.getHeight() / 1.5);*/
	    	Icon icon = getIcon(comp);
	    	if(icon == null)
	    		return;
	    	
	    	int x = getOffsetX(comp, icon);//p.x + comp.getWidth() - defaultIcon.getIconWidth() - inset.right;
	    	int y = getOffsetY(comp, icon);//p.y + comp.getHeight() - defaultIcon.getIconHeight() - inset.bottom;
	    	if(x == -1 || y == -1)
	    		return;
	    	/*JLabel lab = new JLabel(icon);
	    	lab.setBorder(BorderFactory.createLineBorder(Color.BLUE));
	    	lab.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
	    	this.add(lab);*/
	    	repaint(x, y, icon.getIconWidth(), icon.getIconHeight());
	    }
	    private int getOffsetX(JComponent comp, Icon icon){
	    	Point p = getPoint(comp);
	    	if(p == null)return -1;
	    	Insets inset = comp.getInsets();
	    	int offset = p.x + comp.getWidth() - icon.getIconWidth() - inset.right;
	    	return offset;
	    }
	    private int getOffsetY(JComponent comp, Icon icon){
	    	Point p = getPoint(comp);
	    	if(p == null)return -1;
	    	Insets inset = comp.getInsets();
	    	int offset = p.y + comp.getHeight() - icon.getIconHeight() - inset.bottom;
	    	return offset;
	    }
	    private Point getPoint(JComponent comp){
	    	Point p = null;
	    	try {
	    		p = comp.getLocationOnScreen();
			} catch (Exception e) {
				/*warnings.remove(comp);*/
				return null;
			}
	    	SwingUtilities.convertPointFromScreen(p, this);
	    	return p;
	    }
	    private Icon getIcon(JComponent comp){
	    	Icon icon = warnings.get(comp);
	    	if(icon == null)
	    		icon = warningIcon;
	    	return icon;
	    }
	}
}
