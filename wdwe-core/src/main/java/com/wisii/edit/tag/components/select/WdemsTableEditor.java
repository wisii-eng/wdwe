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
 * @WdemsTableEditor.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.Color;
import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

import com.wisii.edit.tag.components.select.WdemsTable.DataItem;


/**
 * 类功能描述：用于实现表、Tree中Leaf节点的数据Editor
 * 
 * 作者：李晓光
 * 创建日期：2009-6-15
 */
@SuppressWarnings("serial")
class WdemsTableEditor<T extends AbstractButton> extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
	private T comp = null;
	private Object value = null;
	WdemsTableEditor(){
		
	}
	WdemsTableEditor(T comp){
		this.comp = comp;
	}
	@SuppressWarnings("unchecked")
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if(comp == null)
			comp = (T)(((WdemsTableRender)table.getCellRenderer(row, column)).getComponent());
		this.value = value;
		if(value instanceof DataItem){
			DataItem item = (DataItem)value;
			comp.setSelected(item.isSelect());
			comp.setText(value  + "");
		}else {
			comp.setSelected(isSelected);
		}
		if (isSelected) {
			comp.setBackground(table.getSelectionBackground());
			comp.setForeground(table.getSelectionForeground());
		} else {
			if((row & 1) != 0)
				comp.setBackground(new Color(242, 246, 251));
			else
				comp.setBackground(table.getBackground());
			comp.setForeground(table.getForeground());
		}
		return comp;
	}
	
	@SuppressWarnings("unchecked")
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (comp == null) {
			comp = (T) (((WdemsTableRender) tree.getCellRenderer()).getComponent());
		}
		this.value = value;
		comp.setText(value + "");
		if (value instanceof DataItem) {
			DataItem item = (DataItem) value;
			comp.setSelected(item.isSelect());
		} else {
			comp.setSelected(isSelected);
		}
		if (isSelected) {
			comp.setBackground(UIManager.getColor("Tree.selectionBackground"));
			comp.setForeground(UIManager.getColor("Tree.selectionForeground"));
		} else {
			comp.setBackground(tree.getBackground());
			comp.setForeground(tree.getForeground());
		}
		/*ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				if (stopCellEditing()) {
					fireEditingStopped();
				}
			}
		};
		comp.addItemListener(itemListener);*/
		return comp;
	}
	public Object getCellEditorValue() {
		if(value instanceof DataItem){
			DataItem item = (DataItem)value;
			item.setSelect(comp.isSelected());
		}
		
		return value;
	}
}
