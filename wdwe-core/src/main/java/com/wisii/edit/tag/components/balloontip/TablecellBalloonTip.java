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
 * Balloontip - Balloon tips for Java Swing applications
 * Copyright 2007, 2008 Bernhard Pauler, Tim Molderez
 * 
 * This file is part of Balloontip.
 * 
 * Balloontip is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Balloontip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Balloontip.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wisii.edit.tag.components.balloontip;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import com.wisii.edit.tag.components.balloontip.positioners.BalloonTipPositioner;
import com.wisii.edit.tag.components.balloontip.styles.BalloonTipStyle;


/**
 * Provides the same functionality as a BalloonTip, but can attach itself to a specific JTable's cell.
 * @author Tim Molderez
 */
public class TablecellBalloonTip extends CustomBalloonTip {
	
	private int row;
	private int column;
	
	// If someone messes with the table's columns, this will probably screw up the balloon tip's position, so we'll just close it
	private TableColumnModelListener columnListener = new TableColumnModelListener()  {
		public void columnAdded(TableColumnModelEvent e) {closeBalloon();}
		public void columnMarginChanged(ChangeEvent e) {setCellPosition(row, column);}
		public void columnMoved(TableColumnModelEvent e) {closeBalloon();}
		public void columnRemoved(TableColumnModelEvent e) {closeBalloon();}
		public void columnSelectionChanged(ListSelectionEvent e) {}
	};
	
	// This class is needed when the table hasn't been set up yet during the constructor of this balloon tip
	private class ConstructorHelper implements AncestorListener {
		public void ancestorAdded(AncestorEvent event) {
			((JTable)attachedComponent).getColumnModel().addColumnModelListener(columnListener);
			// Don't forget to reposition yourself! During the constructor the table wasn't set up yet, so you couldn't determine the table cell's position then...
			setCellPosition(row, column);
			// Remove yourself
			((JTable)attachedComponent).removeAncestorListener(this);
		}
		public void ancestorMoved(AncestorEvent event) {}
		public void ancestorRemoved(AncestorEvent event) {}
	};

	/**
	 * @see com.wisii.edit.tag.components.balloontip.BalloonTip#BalloonTip(JComponent, String, BalloonTipStyle, Orientation, AttachLocation, int, int, boolean)
	 * @param table		The table to attach the balloon tip to
	 * @param row		Which row is the balloon tip attached to
	 * @param column	Which column is the balloon tip attached to
	 */
	public TablecellBalloonTip(JTable table, String text, int row, int column, BalloonTipStyle style, Orientation alignment, AttachLocation attachLocation, int horizontalOffset, int verticalOffset, boolean useCloseButton) {
		super(table, text, ((JTable)table).getCellRect(row, column, true), style, alignment, attachLocation, horizontalOffset, verticalOffset, useCloseButton);
		
		this.row = row;
		this.column = column;
		
		// We can only add the columnListener if we're sure the table has already been properly set up (which is the case if our super-constructor was able to determine the top level container...)
		if (getTopLevelContainer() != null) {
			table.getColumnModel().addColumnModelListener(columnListener);
		} else {
			table.addAncestorListener(new ConstructorHelper());
		}
	}
	
	/**
	 * @see com.wisii.edit.tag.components.balloontip.BalloonTip#BalloonTip(JComponent, String, BalloonTipStyle, BalloonTipPositioner, boolean)
	 * @param table		The table to attach the balloon tip to
	 * @param row		Which row is the balloon tip attached to
	 * @param column	Which column is the balloon tip attached to
	 */
	public TablecellBalloonTip(JTable table, String text, int row, int column, BalloonTipStyle style, BalloonTipPositioner positioner, boolean useCloseButton) {
		super(table, text, ((JTable)table).getCellRect(row, column, true), style, positioner, useCloseButton);
		
		this.row = row;
		this.column = column;
		
		// We can only add the columnListener if we're sure the table has already been properly set up (which is the case if our super-constructor was able to determine the top level container...)
		if (getTopLevelContainer() != null) {
			table.getColumnModel().addColumnModelListener(columnListener);
		} else {
			table.addAncestorListener(new ConstructorHelper());
		}
	}

	/**
	 * Sets the table cell the balloon tip should attach to
	 * @param row
	 * @param column
	 */
	public void setCellPosition(int row, int column) {
		offset = ((JTable)attachedComponent).getCellRect(row, column, true);
		refreshLocation();
	}

	public void closeBalloon() {
		super.closeBalloon();
		((JTable)attachedComponent).getColumnModel().removeColumnModelListener(columnListener);
	}
}