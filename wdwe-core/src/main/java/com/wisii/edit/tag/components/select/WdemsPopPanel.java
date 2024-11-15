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
 * @WdemsPopPanel.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
/**
 * 类功能描述：用于封装弹出的控件
 * 
 * 作者：李晓光
 * 创建日期：2009-6-15
 */
@SuppressWarnings("serial")
class WdemsPopPanel extends JPopupMenu {
	//最小宽度，最小宽度以控制面板的大小作为最小宽度
	private int minwidth=75;
	WdemsPopPanel(){
		setPreferredSize(new Dimension(400, 300));
		setLayout(new BorderLayout());
		/*add(createTitlePanel(), BorderLayout.NORTH);*/
		/*add(createContent(), BorderLayout.CENTER);*/
		/*add(createBtnPanel(), BorderLayout.SOUTH);*/
	}
	public void setMainComp(JComponent comp){
		add(comp, BorderLayout.CENTER);
	}
	public void setControlComp(JComponent comp){
		minwidth=comp.getPreferredSize().width;
		if(minwidth<75)
		{
			minwidth=75;
		}
		add(comp, BorderLayout.SOUTH);
	}
	
	public int getMinwidth() {
		return minwidth;
	}
	public void addActionListener(ActionListener lis){
		listenerList.add(ActionListener.class, lis);
	}
	public void removeActionListener(ActionListener lis){
		listenerList.remove(ActionListener.class, lis);
	}
	public void fireActionPerformed() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent)currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent)currentEvent).getModifiers();
        }
        ActionEvent e =
            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "",
                            EventQueue.getMostRecentEventTime(), modifiers);
                            
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }          
        }
    }
	public JPanel createBtnPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		JButton btnOK = new JButton("确定");		
		JButton btnCancel = new JButton("取消");
		btnOK.setActionCommand(COMMAND);
		Insets inset = new Insets(0, 0, 0, 0);
		btnOK.setMargin(inset);
		btnCancel.setMargin(inset);
		panel.add(btnOK);
		panel.add(btnCancel);
		addLis(btnOK, btnCancel);
		return panel;
	}

	private void addLis(AbstractButton... buttons) {
		ActionListener lis = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WdemsPopPanel.this.setVisible(false);
				if(COMMAND.equalsIgnoreCase(e.getActionCommand())) {
					fireActionPerformed();
				}
			}
		};
		for (AbstractButton btn : buttons) {
			btn.addActionListener(lis);
		}
	}
	private final static String COMMAND = "OK";
}
