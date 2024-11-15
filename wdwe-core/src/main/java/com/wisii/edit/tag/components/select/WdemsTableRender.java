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
 * @WsemsCheckBoxRender.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import com.wisii.edit.tag.components.select.WdemsTable.DataItem;

/**
 * 类功能描述：用于实现表、Tree中Leaf节点的数据Render
 * 
 * 作者：李晓光 创建日期：2009-6-15
 */
class WdemsTableRender<T extends AbstractButton> implements TableCellRenderer,
		TreeCellRenderer {
	/* 定义奇数行的背景色 */
	private final static Color BACK_GROUND = new Color(242, 246, 251);

	/* 定义Render控件 */
	T comp = null;

	WdemsTableRender(T comp) {
		if (comp == null)
			throw new NullPointerException("指定的控件为空！");
		comp.setHorizontalAlignment(AbstractButton.CENTER);
		this.comp = comp;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof DataItem) {
			comp.setText(value + "");
			DataItem item = (DataItem) value;
			comp.setSelected(item.isSelect());
		} else {
			comp.setSelected(isSelected);
		}
		if (isSelected) {
			comp.setBackground(table.getSelectionBackground());
			comp.setForeground(table.getSelectionForeground());
		} else {
			if ((row & 1) != 0) {
				comp.setBackground(BACK_GROUND);
			} else {
				comp.setBackground(table.getBackground());
			}
			comp.setForeground(table.getForeground());
		}
		return comp;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (!leaf
				&& tree.getSelectionModel().getSelectionMode() == TreeSelectionModel.SINGLE_TREE_SELECTION)
			return new DefaultTreeCellRenderer().getTreeCellRendererComponent(
					tree, value, selected, expanded, leaf, row, hasFocus);
		String text = tree.convertValueToText(value, selected, expanded, leaf,
				row, hasFocus);
		comp.setText(text);
		if (value instanceof DataItem) {
			DataItem item = (DataItem) value;
			comp.setSelected(item.isSelect());
		} else {
			comp.setSelected(selected);
		}
		  if (selected) {
			comp.setForeground(UIManager.getColor("Tree.selectionForeground"));
			comp.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			comp.setForeground(tree.getForeground());
			comp.setBackground(tree.getBackground());
		}
		 /*updateIcon(tree, leaf, expanded);*/
		return comp;
	}

	private void updateIcon(JTree tree, Boolean leaf, Boolean expanded) {
		Icon leafIcon = UIManager.getIcon("Tree.leafIcon");
		Icon opneIcon = UIManager.getIcon("Tree.closedIcon");
		Icon closedIcon = UIManager.getIcon("Tree.openIcon");
		if (!tree.isEnabled()) {
			comp.setEnabled(false);
			if (leaf) {
				comp.setDisabledIcon(leafIcon);
			} else if (expanded) {
				comp.setDisabledIcon(opneIcon);
			} else {
				comp.setDisabledIcon(closedIcon);
			}
		} else {
			comp.setEnabled(true);
			if (leaf) {
				comp.setIcon(leafIcon);
			} else if (expanded) {
				comp.setIcon(opneIcon);
			} else {
				comp.setIcon(closedIcon);
			}
		}
	}
	public T getComponent() {
		return comp;
	}
}
