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
 * @WdemsEditComponentManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import com.wisii.edit.util.EngineUtil;

/**
 * 类功能描述：用于控制所用的编辑控件
 * 
 * 1、保存所有当前编辑控件
 * 2、保存所用编辑控件Bound信息。
 * 
 * 作者：李晓光
 * 创建日期：2009-7-30
 */
public class WdemsEditComponentManager {
	private static Map<JComponent, Rectangle> EDITOR_COMPONENTS = new HashMap<JComponent, Rectangle>();
	private static JComponent LAYER_COMPONENT = new EditComponentLayerImp();
	private static int pageWidth = -1;
	private static int pageHeight = - 1;
	private static int offsetX = -1;
	private static int offsetY = -1;
	
	public final static void addEditor(JComponent comp){
		addEditor(comp, comp.getBounds());
	}

	public final static void addEditor(JComponent comp, Rectangle bound){
		if(comp == null || bound == null)
			return;
		
		EDITOR_COMPONENTS.put(comp, bound);
		display();
	}
	public final static void addEditors(Map<JComponent, Rectangle> map){
		if(map == null || map.isEmpty())
			return;

		EDITOR_COMPONENTS.putAll(map);
		display();
	}
	public final static void removeEditor(JComponent comp){
		EDITOR_COMPONENTS.remove(comp);
		LAYER_COMPONENT.remove(comp);
	}
	
	public final static void clearDump(){
		EDITOR_COMPONENTS.clear();
		pageWidth = -1;
		pageHeight = -1;
		LAYER_COMPONENT.removeAll();
	}
	
	public final static JComponent getLayerComponent(){
		return LAYER_COMPONENT;
	}
	public final static void setPageWidth(int width){
		if(pageWidth == width)
			return;
		pageWidth = width;
	}
	public final static void setPageHeight(int height){
		if(pageHeight == height)return;
		pageHeight = height;
	}
	public final static int getComponentCount(){
		return EDITOR_COMPONENTS.size();
	}
	public final static List<JComponent> getJComponents()
	{
		if(EDITOR_COMPONENTS==null||EDITOR_COMPONENTS.isEmpty())
		{
			return null;
		}
		return new ArrayList<JComponent>(EDITOR_COMPONENTS.keySet());
	}
	public final static void display(){
		updateBounds();
		/*updatePolicy();
		updateLocation();*/
	}
	private final static void updateBounds(){
		if(EDITOR_COMPONENTS == null || EDITOR_COMPONENTS.isEmpty())return;
		updateOffset();
		for (Entry<JComponent, Rectangle> entry : EDITOR_COMPONENTS.entrySet()) {
			JComponent comp = entry.getKey();
			comp.setBounds(getOffsetRect(entry.getValue()));
			LAYER_COMPONENT.add(comp);
		}
		Thread t = new Thread(){
			@Override
			public void run() {
				
				EngineUtil.getEnginepanel().updateUI();
//				LAYER_COMPONENT.revalidate();
			}
		};
		SwingUtilities.invokeLater(t);
	}
	public final static void updateLocation(){
		if(EDITOR_COMPONENTS == null || EDITOR_COMPONENTS.isEmpty())return;
		Thread t = new Thread(){
			@Override
			public void run() {
				updateOffset();
				for (Entry<JComponent, Rectangle> entry : EDITOR_COMPONENTS.entrySet()) {
					JComponent comp = entry.getKey();
					comp.setBounds(getOffsetRect(entry.getValue()));
				}
				LAYER_COMPONENT.repaint();
			}
		};
		SwingUtilities.invokeLater(t);
	}

	public final static void updatePolicy(Component comp) {
		if (!(comp instanceof JScrollPane))
			return;
		JScrollPane pane = (JScrollPane)comp;
		comp = pane.getViewport().getView();
		if(!(comp instanceof JTextComponent))
			return;
		
		JTextComponent text = (JTextComponent) comp;
		try {
			String t = text.getText();
			if(t == null || "".equalsIgnoreCase(t)){
				return;
			}
			Rectangle r = text.modelToView(text.getText().length() - 1);
			if (r == null || r.y + r.height > 18){
				pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				return;
			}
		} catch (BadLocationException e) {
			System.err.println("Abstract Render = " + e.getMessage());
		}
		java.awt.Container parent = comp.getParent();
		if (!(parent instanceof JViewport))
			return;
		
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	}

	private final static Rectangle getOffsetRect(Rectangle r){
		Rectangle rect = (Rectangle)r.clone();
		rect.setLocation(r.x + offsetX, r.y + offsetY);
		return rect;
	}
	@SuppressWarnings("serial")
	private static class EditComponentLayerImp extends JComponent{
		EditComponentLayerImp(){
			setLayout(null);
			setOpaque(false);
			addComponentListener(new ComponentAdapter(){
				@Override
				public void componentResized(ComponentEvent e) {
					updateLocation();
				}
			});
		}
	}
	private final static void updateOffset(){
		offsetX = (LAYER_COMPONENT.getWidth() - pageWidth) / 2;
		offsetY = (LAYER_COMPONENT.getHeight() - pageHeight) / 2;
	}
}
