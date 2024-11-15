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
 * @WdemsDefaultRenderer.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 类功能描述：表的缺省Render，主要是为了统一样式。
 * 
 * 作者：李晓光 创建日期：2009-6-15
 */
@SuppressWarnings("serial")
class WdemsDefaultRenderer extends DefaultTableCellRenderer {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	/* 定义奇数行的背景色 */
	private final static Color BACK_GROUND = new Color(242, 246, 251);
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			if((row & 1) != 0)
				setBackground(BACK_GROUND);
			else
				setBackground(table.getBackground());
			setForeground(table.getForeground());
		}

		setFont(table.getFont());

		if (hasFocus) {
			Border border = null;
			if (isSelected) {
				border = UIManager
						.getBorder("Table.focusSelectedCellHighlightBorder");
			}
			if (border == null) {
				border = UIManager.getBorder("Table.focusCellHighlightBorder");
			}
			setBorder(border);

			if (!isSelected && table.isCellEditable(row, column)) {
				Color col;
				col = UIManager.getColor("Table.focusCellForeground");
				if (col != null) {
					super.setForeground(col);
				}
				col = UIManager.getColor("Table.focusCellBackground");
				if (col != null) {
					super.setBackground(col);
				}
			}
		} else {
			setBorder(getNoFocusBorder());
		}

		setValue(value);

		return this;
	}

	private static Border getNoFocusBorder() {
		if (System.getSecurityManager() != null) {
			return SAFE_NO_FOCUS_BORDER;
		} else {
			return noFocusBorder;
		}
	}
}
