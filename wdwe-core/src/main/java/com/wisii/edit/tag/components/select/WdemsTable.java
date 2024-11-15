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
 * @WdemsTable.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.wisii.edit.tag.components.select.AbstractWdemsCombox.DisplayStyle;
import com.wisii.edit.tag.components.select.AbstractWdemsCombox.SelectModel;

/**
 * 类功能描述：用于创建下拉表结构控件
 * 
 * 作者：李晓光
 * 创建日期：2009-6-15
 */
@SuppressWarnings("serial")
class WdemsTable extends JTable{
	private final static String COLUMN_NAME = "选择";
	private DisplayStyle style = DisplayStyle.TABLE;
	private SelectModel selectModel = SelectModel.Line;
	WdemsTable(){
		super();
		setModel(new WdemsTableModel());
		initStyle();
	}
	WdemsTable(Vector<Object> rows, Vector<Object> columns){
		super(rows, columns);
		initStyle();
	}
	WdemsTable(DefaultTableModel model){		
		super(model);
		
		initStyle();
	}
	private void initStyle(){
		setShowGrid(false);
		updateSelectModel();
		setRowHeight(getRowHeight() + 10);
		/*getTableHeader().setResizingAllowed(Boolean.FALSE);*/		
		getTableHeader().setReorderingAllowed(Boolean.FALSE);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
	
	public DisplayStyle getStyle() {
		return style;
	}
	public void setStyle(DisplayStyle style) {
		if((style == DisplayStyle.TREE || style == DisplayStyle.CHECKBOX_TREE || style == DisplayStyle.RADIO_TREE)) {
			style = DisplayStyle.TABLE;
		}
		updateColumn(this.style, style);
		this.style = style;
	}
	private void updateColumn(DisplayStyle old, DisplayStyle fresh){
		if(old == DisplayStyle.TABLE){
			if(fresh != DisplayStyle.TABLE){
				DefaultTableModel tableModel = (DefaultTableModel)getModel();
				tableModel.addColumn(COLUMN_NAME);
				TableColumnModel model = getTableHeader().getColumnModel();
				model.moveColumn(model.getColumnCount() - 1, 0);
			}
		}else if(fresh == DisplayStyle.TABLE){
			TableColumnModel model = getTableHeader().getColumnModel();
			model.removeColumn(model.getColumn(0));			
		}
	}
	public SelectModel getSelectModel() {
		return selectModel;
	}
	public void setSelectModel(SelectModel selectModel) {
		if(this.selectModel == selectModel)
			return;
		this.selectModel = selectModel;
		updateSelectModel();
	}
	private void updateSelectModel(){
		if(selectModel == SelectModel.Cell){
			setRowSelectionAllowed(Boolean.TRUE);
			setColumnSelectionAllowed(Boolean.TRUE);
		}else if(selectModel == SelectModel.Column){
			setRowSelectionAllowed(Boolean.FALSE);
			setColumnSelectionAllowed(Boolean.TRUE);
		}else{
			setRowSelectionAllowed(Boolean.TRUE);
			setColumnSelectionAllowed(Boolean.FALSE);
		}
	}
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if(style == DisplayStyle.RADIO_TABLE){
			if(column == 0)
				return new WdemsTableRender<AbstractButton>(new JRadioButton());
		}else if(style == DisplayStyle.CHECKBOX_TABLE){
			if(column == 0)
				return new WdemsTableRender<AbstractButton>(new JCheckBox());
		}else if(style == DisplayStyle.ALL_RADIO_TABLE)
			return new WdemsTableRender<AbstractButton>(new JRadioButton());
		else if(style == DisplayStyle.ALL_CHECKBOX_TABLE)
			return new WdemsTableRender<AbstractButton>(new JCheckBox());
		return new WdemsDefaultRenderer();
	}
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if(style == DisplayStyle.ALL_RADIO_TABLE || style == DisplayStyle.ALL_CHECKBOX_TABLE)
			return new WdemsTableEditor<AbstractButton>();
		else
			return null;
		/*WdemsTableEditor<AbstractButton> editor = new WdemsTableEditor<AbstractButton>();
		if(select != SelectModel.Cell)
			 return editor;
		 else if(column == 0)
			 return editor;
		 else{
			 return super.getCellEditor(row, column);
		 }*/
	}
	private class WdemsTableModel extends DefaultTableModel{
		@SuppressWarnings("unchecked")
		@Override
		public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
			boolean flag = Boolean.FALSE;
			if(style != DisplayStyle.TABLE){
				columnIdentifiers.add(COLUMN_NAME);
				flag = Boolean.TRUE;
			}
			super.setDataVector(dataVector, columnIdentifiers);
			if(flag){
				TableColumnModel model = getTableHeader().getColumnModel();
				model.moveColumn(model.getColumnCount() - 1, 0);
			}
		}
	}
	/**
	 * 如果要求每个单元格均采用单选框、复选框，这包装给定的数据
	 */
	static class DataItem{
		private Object value = "";
		private Boolean select = Boolean.FALSE;
		DataItem(Object value){
			this(value, Boolean.FALSE);
		}
		DataItem(Object value, boolean select){
			this.value = value;
			this.select = select;
		}
		public Boolean isSelect() {
			return select;
		}
		public void setSelect(Boolean select) {
			this.select = select;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return (value == null) ? "" : value + "";
		}
	}
}
