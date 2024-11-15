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
 * 
 */
package com.wisii.component.createinitview.openView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.apps.FOUserAgent;


public class WisedocPopPanel extends JPopupMenu {
	private MyPanel center = new MyPanel();
	private JButton btnOK = new JButton("确定");
	private JButton btnCancle = new JButton("取消");
	private JCheckBox selectAll = new JCheckBox("全选");
	private final static String LAB_TEXT = "<html><u color='red'>反选</u><html>";
	private JLabel antiElection = new JLabel(LAB_TEXT);
	private JPanel south = new JPanel();
	private JPanel north = new JPanel();
	private JPopupButton pop = null;
	private Set layers = new HashSet();
	public WisedocPopPanel(JPopupButton pop){
		this.pop = pop;
  		this.setLayout(new BorderLayout());
		this.add(north, BorderLayout.NORTH);
		this.add(createListPanel(center), BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
		
		north.setLayout(new GridLayout(1, 2));
		north.add(selectAll);
		north.add(antiElection);
		antiElection.setBorder(BorderFactory.createEmptyBorder(0, 5, 6, 0));
		south.setLayout(new FlowLayout(FlowLayout.TRAILING));
		south.add(btnOK);
		south.add(btnCancle);
		initButtonAction();
	}
	private void initButtonAction(){
		ActionListener lis = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				Object source = e.getSource();
				if(source == btnOK){
					updateViewUI();
				}
				WisedocPopPanel.this.setVisible(false);
				
			}
		};
		btnOK.addActionListener(lis);
		btnCancle.addActionListener(lis);
		ActionListener l = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				layers.clear();
				int count = center.getComponentCount();
				for (int i = 0; i < count; i++) {
					Component comp = center.getComponent(i);
					if(!(comp instanceof MyCheckBox))
						continue;
					MyCheckBox box = (MyCheckBox)comp;
					if(source == selectAll){
						box.setSelected(selectAll.isSelected());
						if(selectAll.isSelected()){
							layers.add(box.getValue());
						}
					}/*else if(source == antiElection){
						box.setSelected(!box.isSelected());
						if(box.isSelected())
							layers.add(box.getValue());
					}*/
				}
			}
		};
		selectAll.addActionListener(l);
		/*antiElection.addActionListener(l);*/
		antiElection.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				layers.clear();
				int count = center.getComponentCount();
				for (int i = 0; i < count; i++) {
					Component comp = center.getComponent(i);
					if(!(comp instanceof MyCheckBox))
						continue;
					MyCheckBox box = (MyCheckBox)comp;
						box.setSelected(!box.isSelected());
					if(box.isSelected())
						layers.add(box.getValue());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
	}
	public void setVisible(boolean b) {
		super.setVisible(b);
		upateButtonUI(pop.getLeftButton());
	}

	private void upateButtonUI(AbstractButton btn)
 {
		FOUserAgent agent = EngineUtil.getEnginepanel().getFOUserAgent();
		Set alllayers = agent.getAllLayers();
		if (alllayers != null && alllayers.equals(layers))// ColorUtil.getAllLayers().size()
			// == layers.size()
			btn.setSelected(false);
		else {

			btn.setSelected(true);
		}
	}
	private void updateViewUI(){
		EnginePanel panel=EngineUtil.getEnginepanel();
		FOUserAgent agent = panel.getFOUserAgent();
		agent.setSelectLayers(layers);
		agent.setViewNoBack(true);
		/* 重新加载当前页 */
		panel.reload();

	}
	private AbstractAction crearBoxAction(){
		AbstractAction action = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if(!(source instanceof MyCheckBox))
					return;
				MyCheckBox item = (MyCheckBox)source;
				if(item.isSelected())
					layers.add(item.getValue());
				else
					layers.remove(item.getValue());
			}
		};
		return action;
	}
	private JScrollPane createListPanel(JPanel center){
		FOUserAgent agent = EngineUtil.getEnginepanel().getFOUserAgent();
		Set set = agent.getAllLayers();
		Integer[] arr = (Integer[])set.toArray(new Integer[0]);
		List list = Arrays.asList(arr);
		Collections.sort(list);
		JScrollPane scrPane = new JScrollPane(center);
		center.setLayout(new GridLayout(0, 1));
		AbstractAction a = crearBoxAction();
		MyCheckBox box = null;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Integer layer = (Integer) iterator.next();
			box = new MyCheckBox(layer);
			box.addActionListener(a);
			center.add(box);
			initBox(box);
		}
		return scrPane;
	}
	private void initBox(MyCheckBox box){
		Set set = EngineUtil.getEnginepanel().getFOUserAgent().getCheckLayers(); 
		
		if(set != layers)
			layers = set;
		if(set == null)
			return;
		
		boolean flag = set.contains(box.getValue());
		box.setSelected(flag);
	}
	private class MyPanel extends JPanel implements Scrollable{
		public Dimension getPreferredScrollableViewportSize() {
			return new Dimension(10, 120);
		}

		public int getScrollableBlockIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			switch (orientation) {
			case SwingConstants.VERTICAL:
				return visibleRect.height;
			case SwingConstants.HORIZONTAL:
				return visibleRect.width;
			default:
				throw new IllegalArgumentException("Invalid orientation: "
						+ orientation);
			}
		}

		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		public int getScrollableUnitIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			switch (orientation) {
			case SwingConstants.VERTICAL:
				return visibleRect.height;
			case SwingConstants.HORIZONTAL:
				return visibleRect.width;
			default:
				throw new IllegalArgumentException("Invalid orientation: "
						+ orientation);
			}
		}
	}
	private class MyCheckBox extends JCheckBox{
		private Integer value = new Integer(-1);
		private final static String SUFFIX = "层";
		public MyCheckBox(Integer value){
			this.value = value;
			setText(value + SUFFIX);
		}
		public Integer getValue(){
			return value;
		}
	}
}
