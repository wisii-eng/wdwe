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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.wisii.edit.tag.factories.bar.TaskPaneMain;

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
public class WdemsWarningComponentManager extends JComponent {
	private Map<JComponent, Icon> warnings = new HashMap<JComponent, Icon>();
	/* 缺省图 */
    private Icon defaultIcon = null;
    
    public WdemsWarningComponentManager() {
        loadImages();
    }
    public WdemsWarningComponentManager(Icon defaultIcon){
    	setDefaultIcon(defaultIcon);
    }
    public void addWarning(JComponent comp) {
    	if(warnings.containsKey(comp))
    		return;
    	addWarning(comp, null);
    }
    public void addWarning(JComponent comp, Icon image){
    	warnings.put(comp, image);
    	repaintBadge(comp);
    }
    public void removeWarning(JComponent comp) {
    	if(!warnings.containsKey(comp))
    		return;
    	warnings.remove(comp);
    	repaintBadge(comp);
    }
    public int getWaringComponentCount(){
    	return warnings.size();
    }
    
    public Icon getDefaultIcon() {
		return defaultIcon;
	}

	public void setDefaultIcon(Icon defaultIcon) {
		this.defaultIcon = defaultIcon;
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
            
            /*Point p = invalid.getLocationOnScreen();
            SwingUtilities.convertPointFromScreen(p, this);*/
            
            /*int x = p.x - warningIcon.getWidth() / 2;
            int y = (int) (p.y + invalid.getHeight() - warningIcon.getHeight() / 1.5);*/
            int x = getOffsetX(invalid, icon);
            int y = getOffsetY(invalid, icon);
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
    	repaint(x, y, icon.getIconWidth(), icon.getIconHeight());
    }
    private int getOffsetX(JComponent comp, Icon icon){
    	Point p = getPoint(comp);
    	Insets inset = comp.getInsets();
    	int offset = p.x + comp.getWidth() - icon.getIconWidth() - inset.right;
    	return offset;
    }
    private int getOffsetY(JComponent comp, Icon icon){
    	Point p = getPoint(comp);
    	Insets inset = comp.getInsets();
    	int offset = p.y + comp.getHeight() - icon.getIconHeight() - inset.bottom;
    	return offset;
    }
    private Point getPoint(JComponent comp){
    	Point p = comp.getLocationOnScreen();
    	SwingUtilities.convertPointFromScreen(p, this);
    	return p;
    }
    private Icon getIcon(JComponent comp){
    	Icon icon = warnings.get(comp);
    	if(icon == null)
    		icon = defaultIcon;
    	return icon;
    }
    private void loadImages() {
    	defaultIcon = new ImageIcon(TaskPaneMain.class.getResource("icons/dialog-warning.png"));
    }
}
