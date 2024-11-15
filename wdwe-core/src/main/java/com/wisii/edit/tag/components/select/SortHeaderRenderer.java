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
 * @SortHeaderRenderer.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * 类功能描述：如果列可排序，则Table采用该Render。
 * 1、该Render提够了设置图标的处理
 * 作者：李晓光
 * 创建日期：2009-6-29
 */
@SuppressWarnings("serial")
class SortHeaderRenderer extends DefaultTableCellRenderer {
	 public Component getTableCellRendererComponent(JTable table,
             Object obj, boolean flag, boolean flag1, int row, int column) {
         if (table != null) {
             JTableHeader header = table.getTableHeader();
             if (header != null) {
                 setForeground(header.getForeground());
                 setBackground(header.getBackground());
                 setFont(header.getFont());
             }
         }
         setText(obj != null ? obj.toString() : "");
         int k = table.convertColumnIndexToModel(column);
        /* if (k == sortColumn) {
             setIcon(ascending ? SortManager.upIcon : SortManager.downIcon);
         } else {
             setIcon(null);
         }*/
         setBorder(UIManager.getBorder("TableHeader.cellBorder"));
         return this;
     }
}
