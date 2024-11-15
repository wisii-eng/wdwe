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
 * @WdemsRedoUndoManager.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.decorative;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * 类功能描述：用于处理JTextComponent控件撤销、重做。
 * 
 * 
 * 
 * 作者：李晓光 创建日期：2009年8月31日
 */
@SuppressWarnings("serial")
public class WdemsRedoUndoManager {
	private final static int LIMIT = 10;

	/**
	 * 为指定的控件【文本】，添加撤销、重做处理。
	 * 
	 * @param comp
	 *            指定文本控件
	 */
	public final static void registerComponent(JTextComponent comp) {
		bindActions(comp, LIMIT);
	}

	/**
	 * 为指定的控件【文本】，添加撤销、重做处理。
	 * 
	 * @param comp
	 *            指定文本控件。
	 * @param limit
	 *            指定做大撤销次数。
	 */
	public final static void registerComponent(JTextComponent comp, int limit) {
		if (limit < 0) {
			limit = LIMIT;
		}
		bindActions(comp, limit);
	}

	/**
	 * 去除指定控件的撤销处理。
	 * @param comp	指定文本控件
	 */
	public final static void removeComponent(JTextComponent comp) {
		removeActions(comp);
	}

	private final static void removeActions(JTextComponent comp) {
		if (comp == null)
			return;
		AbstractDocument documnet = (AbstractDocument) comp.getDocument();
		UndoableEditListener[] listeners = documnet.getUndoableEditListeners();
		for (UndoableEditListener lis : listeners) {
			documnet.removeUndoableEditListener(lis);
		}
	}

	private final static void bindActions(JTextComponent comp, int limit) {
		if (comp == null)
			return;
		UndoManager manager = new UndoManager();
		manager.setLimit(limit);
		comp.getDocument().addUndoableEditListener(manager);

		InputMap input = comp.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap action = comp.getActionMap();

		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK),
				UIAction.redo);
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK),
				UIAction.undo);

		action.put(UIAction.redo, new UIAction(UIAction.redo, manager));
		action.put(UIAction.undo, new UIAction(UIAction.undo, manager));
	}

	private static class UIAction extends AbstractAction {
		private final static String redo = "redo";
		private final static String undo = "undo";
		private String actionName = "";
		private UndoManager manager = null;

		UIAction(String name, UndoManager manager) {
			super(name);
			actionName = name;
			this.manager = manager;
		}

		public void actionPerformed(ActionEvent e) {
			if (redo == actionName) {
				if (manager.canRedo()) {
					manager.redo();
				}
			} else if (undo == actionName) {
				if (manager.canUndo()) {
					manager.undo();
				}
			}
		}
	}
}
