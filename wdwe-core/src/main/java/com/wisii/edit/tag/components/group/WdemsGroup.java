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
 * @WdemsGroup.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.group;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;
import javax.swing.event.EventListenerList;

import com.wisii.edit.tag.components.group.WdemsGroupComponent.WdemsButtonModel;
import com.wisii.edit.tag.schema.wdems.Group;

/**
 * 类功能描述：用于表示组概念，所有加入组的控件， 要遵循Group标签定义的规则。
 * 
 * 作者：李晓光 创建日期：2009-8-20
 */
public class WdemsGroup implements SelectGroup {
	private Group groupMarker = null;

	/**
	 * The current selection.
	 */
	private final JToggleButton.ToggleButtonModel selection = null;
	/* private final BitSet selections = new BitSet(); */
	private final List<ButtonModel> selections = Collections
			.synchronizedList(new ArrayList<ButtonModel>());
	protected List<AbstractButton> buttons = new Vector<AbstractButton>();
	protected EventListenerList listeners = new EventListenerList();

	public WdemsGroup() {
		this(null);
	}

	public WdemsGroup(Group group) {
		setGroupMarker(group);
	}

	// the list of buttons participating in this group

	/**
	 * Adds the button to the group.
	 * 
	 * @param b
	 *            the button to be added
	 */
	public void add(AbstractButton b) {
		if (b == null)
			return;
		if(!buttons.contains(b))
			buttons.add(b);

		/*
		 * if (b.isSelected()) { if (selection == null) { selection =
		 * (WdemsButtonModel)b.getModel(); } else { b.setSelected(false); } }
		 */
		if (b.isSelected()) {
			int count = selections.size();
			if (isRadio()) {
				if (count >= 1) {
					b.setSelected(false);
				} else {
					selections.add((WdemsButtonModel) b.getModel());
				}
			} else {
				if (count >= getMaxSelected()) {
					b.setSelected(false);
				} else {
					selections.add((WdemsButtonModel) b.getModel());
				}
			}
		}

		((WdemsButtonModel) b.getModel()).setCompGroup(this);
	}

	/**
	 * Removes the button from the group.
	 * 
	 * @param b
	 *            the button to be removed
	 */
	public void remove(AbstractButton b) {
		if (b == null)
			return;
		buttons.remove(b);

		/*
		 * if(b.getModel() == selection) { selection = null; }
		 * b.getModel().setGroup(null);
		 */

		if (selections.contains(b.getModel())) {
			selections.remove(b.getModel());
		}
		b.getModel().setGroup(null);
		((WdemsButtonModel) b.getModel()).setCompGroup(null);

	}

	/**
	 * Returns all the buttons that are participating in this group.
	 * 
	 * @return an <code>Enumeration</code> of the buttons in this group
	 */
	public Iterator<AbstractButton> getSelctionElements() {
		return buttons.iterator();
	}

	/**
	 * Returns the model of the selected button.
	 * 
	 * @return the selected button model
	 */
	public ButtonModel getSelection() {
		if (selections == null || selections.isEmpty())
			return null;

		return selections.get(0);
	}

	public List<ButtonModel> getSelections() {
		return selections;
	}

	/**
	 * Sets the selected value for the <code>ButtonModel</code>. Only one button
	 * in the group may be selected at a time.
	 * 
	 * @param m
	 *            the <code>ButtonModel</code>
	 * @param b
	 *            <code>true</code> if this button is to be selected, otherwise
	 *            <code>false</code>
	 */
	public void setSelected(ButtonModel m, boolean b) {
		/*
		 * if (b && m != null && m != selection) { ButtonModel oldSelection =
		 * selection; selection = m; if (oldSelection != null) {
		 * oldSelection.setSelected(false); } m.setSelected(true); }
		 */

		if (b && m != null && !selections.contains(m)) {
			if (selections.size() >= getMaxSelected()) {
				if (getMaxSelected() == 1) {
					List<ButtonModel> list = new ArrayList<ButtonModel>(
							selections);
					selections.clear();
					for (ButtonModel model : list) {
						model.setSelected(false);
					}
					selections.add(m);
					m.setSelected(true);
				} else {
					selections.remove(m);
					m.setSelected(false);
				}
			} else {
				selections.add(m);
				m.setSelected(true);
			}
		}
		/** 如果允许取消选择，就放开此代码【在设置了组后，如果当前复选框已经被选中，如果再次点击取消选择，放开此代码】 */
		/*if (!b && selections.contains(m)) {
			selections.remove(m);
		}*/
	}

	/**
	 * Returns whether a <code>ButtonModel</code> is selected.
	 * 
	 * @return <code>true</code> if the button is selected, otherwise returns
	 *         <code>false</code>
	 */
	public boolean isSelected(ButtonModel m) {
		/* return (m == selection); */
		return selections.contains(m);
	}

	/**
	 * Returns the number of buttons in the group.
	 * 
	 * @return the button count
	 * @since 1.3
	 */
	public int getButtonCount() {
		if (buttons == null)
			return 0;
		else
			return buttons.size();
	}

	public int getMaxSelected() {
		if(groupMarker == null)
			return 1;
		return groupMarker.getMaxSelected().intValue();
	}

	public int getMinSelected() {
		if(groupMarker == null)
			return 1;
		return groupMarker.getMinSelected().intValue();
	}

	/**
	 * 检查当前的Group对象是否是单选的。
	 * 
	 * @return {@link Boolean} 返回Group是否为单选的。
	 */
	public boolean isRadio() {
		int max = getMaxSelected();
		int min = getMinSelected();
		return (min == 1) && (min == max);
	}

	public Group getGroupMarker() {
		return groupMarker;
	}

	private void setGroupMarker(Group groupMarker) {
		this.groupMarker = groupMarker;
	}

	public void addAction(ActionListener lis) {
		listeners.add(ActionListener.class, lis);
	}

	public void removeAction(ActionListener lis) {
		listeners.remove(ActionListener.class, lis);
	}

	protected void fireActionListener() {
		ActionEvent e = null;
		// Guaranteed to return a non-null array
		Object[] list = listeners.getListenerList();
		long mostRecentEventTime = EventQueue.getMostRecentEventTime();
		int modifiers = 0;
		AWTEvent currentEvent = EventQueue.getCurrentEvent();
		if (currentEvent instanceof InputEvent) {
			modifiers = ((InputEvent) currentEvent).getModifiers();
		} else if (currentEvent instanceof ActionEvent) {
			modifiers = ((ActionEvent) currentEvent).getModifiers();
		}
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = list.length - 2; i >= 0; i -= 2) {
			if (list[i] == ActionListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "",
							mostRecentEventTime, modifiers);
				}
				((ActionListener) list[i + 1]).actionPerformed(e);
			}
		}
	}
	public void cleanDump(){
		buttons.clear();
		clearSelection();
	}
	public void clearSelection() {
		selections.clear();
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (ButtonModel model : selections) {
			s.append("selected = ");
			s.append(model.isSelected());
			s.append(",");
		}
		return s.toString();
	}
}
