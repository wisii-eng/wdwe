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
 *//* $Id: TableBody.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

// Java
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.StaticPropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.FixedLength;
import com.wisii.fov.fo.properties.NumberProperty;
import com.wisii.fov.fo.properties.PercentLength;
import com.wisii.fov.fo.properties.TableColLength;
/**
 * Class modelling the fo:table-body object.
 */
public class TableBody extends TableFObj {
    // The value of properties relevant for fo:table-body.
    private CommonAccessibility commonAccessibility;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonRelativePosition commonRelativePosition;
    // End of property values

    private PropertyList savedPropertyList;

    /**
     * used for validation
     */
    protected boolean tableRowsFound = false;
    protected boolean tableCellsFound = false;

    /**
     * used for initial values of column-number property
     */
    protected List pendingSpans;
    protected BitSet usedColumnIndices;
    private int columnIndex = 1;
    protected boolean firstRow = true;

    /**
     * @param parent FONode that is the parent of the object
     */
    public TableBody(FONode parent) {
        super(parent);
    }

    /**
     * @see FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAccessibility = pList.getAccessibilityProps();
        commonAural = pList.getAuralProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonRelativePosition = pList.getRelativePositionProps();
        super.bind(pList);
        //Used by convertCellsToRows()
        savedPropertyList = pList;
    }

    /**
     * @see com.wisii.fov.fo.FONode#processNode()
     */
    public void processNode(String elementName, Locator locator,
                            Attributes attlist, PropertyList pList)
                    throws FOVException {
        if (!inMarker()) {
            if (getTable().columns != null) {
                int cap = getTable().columns.size();
                pendingSpans = new java.util.ArrayList(cap);
                usedColumnIndices = new java.util.BitSet(cap);
            } else {
                pendingSpans = new java.util.ArrayList();
                usedColumnIndices = new java.util.BitSet();
            }
            setNextColumnIndex();
        }
        super.processNode(elementName, locator, attlist, pList);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        getFOEventHandler().startBody(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {

        if (!inMarker()) {
            // clean up
            savedPropertyList = null;
            pendingSpans = null;
            usedColumnIndices = null;
        }

        getFOEventHandler().endBody(this);

        if (!(tableRowsFound || tableCellsFound)) {
            if (getUserAgent().validateStrictly()) {
                missingChildElementError("marker* (table-row+|table-cell+)");
            } else {
                getLogger().error("fo:table-body must not be empty. "
                        + "Expected: marker* (table-row+|table-cell+)");
                getParent().removeChild(this);
            }
        }

       //判断子节点的子节点是null就把子节点删掉
        if(childNodes==null)return;
        for(int i=childNodes.size()-1;i>=0;i--)
//         while(li.hasNext())
        {
        	FONode child=(FONode)childNodes.get(i);
        	if(child.getChildNodes()==null) childNodes.remove(i);
        }
        if(childNodes.size()==0)childNodes=null;
        /*
        if (tableCellsFound) {
            convertCellsToRows();
        }
        */
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: marker* (table-row+|table-cell+)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI)) {
            if (localName.equals("marker")) {
                if (tableRowsFound || tableCellsFound) {
                   nodesOutOfOrderError(loc, "fo:marker", "(table-row+|table-cell+)");
                }
            } else if (localName.equals("table-row")) {
                tableRowsFound = true;
                if (tableCellsFound) {
                    invalidChildError(loc, nsURI, localName, "Either fo:table-rows" +
                      " or fo:table-cells may be children of an " + getName() +
                      " but not both");
                }
            } else if (localName.equals("table-cell")) {
                tableCellsFound = true;
                if (tableRowsFound) {
                    invalidChildError(loc, nsURI, localName,
                            "Either fo:table-rows or fo:table-cells "
                            + "may be children of an "
                            + getName() + " but not both");
                }
            } else {
                invalidChildError(loc, nsURI, localName);
            }
        } else {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     */
    protected void addChildNode(FONode child) throws FOVException {
        if (!inMarker()) {
            if (firstRow && child.getNameId() == FO_TABLE_ROW) {
                firstRow = false;
            }
        }
        super.addChildNode(child);
    }

    /**
     * If table-cells are used as direct children of a table-body|header|footer
     * they are replaced in this method by proper table-rows.
     * @throws FOVException if there's a problem binding the TableRow's
     *         properties.
     */
    private void convertCellsToRows() throws FOVException {
        //getLogger().debug("Converting cells to rows...");
        List cells = new java.util.ArrayList(childNodes);
        childNodes.clear();
        Iterator i = cells.iterator();
        TableRow row = null;
        while (i.hasNext()) {
            TableCell cell = (TableCell) i.next();
            if (cell.startsRow() && (row != null)) {
                childNodes.add(row);
                row = null;
            }
            if (row == null) {
                row = new TableRow(this);
                PropertyList pList = new StaticPropertyList(row,
                        savedPropertyList);
                pList.setWritingMode();
                row.bind(pList);
            }
            row.addReplacedCell(cell);
            if (cell.endsRow()) {
                childNodes.add(row);
                row = null;
            }
        }
        if (row != null) {
            childNodes.add(row);
        }
    }

    /**
     * @return the Common Border, Padding, and Background Properties.
     */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return commonBorderPaddingBackground;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "table-body";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_TABLE_BODY;
    }

    /**
     * @param obj table row in question
     * @return true if the given table row is the first row of this body.
     */
    public boolean isFirst(TableRow obj) {
        return (childNodes == null
                || (!childNodes.isEmpty()
                    && childNodes.get(0) == obj));
    }

    /**
     * @param obj table row in question
     * @return true if the given table row is the first row of this body.
     */
    public boolean isLast(TableRow obj) {
        return (childNodes == null
                || (childNodes.size() > 0
                    && childNodes.get(childNodes.size() - 1) == obj));
    }

    /**
     * Initializes list of pending row-spans; used for correctly
     * assigning initial value for column-number for the
     * cells of following rows
     * (note: not literally mentioned in the Rec, but it is assumed
     *  that, if the first cell in a given row spans two rows, then
     *  the first cell of the following row will have an initial
     *  column-number of 2, since the first column is already
     *  occupied...)
     */
    protected void initPendingSpans(FONode child) {
        if (child.getNameId() == FO_TABLE_ROW) {
            pendingSpans = ((TableRow) child).pendingSpans;
        } else if (pendingSpans == null) {
            if (getTable().columns != null) {
                List tableCols = getTable().columns;
                pendingSpans = new java.util.ArrayList(tableCols.size());
                for (int i = tableCols.size(); --i >= 0;) {
                    pendingSpans.add(null);
                }
            } else {
                pendingSpans = new java.util.ArrayList();
            }
        }
    }

    /**
     * Returns the current column index of the TableBody
     *
     * @return the next column number to use
     */
    protected int getCurrentColumnIndex() {
        return columnIndex;
    }

    /**
     * Sets the current column index to a specific value
     * (used by ColumnNumberPropertyMaker.make() in case the
     *  column-number was explicitly specified on the cell)
     *
     * @param newIndex  the new column index
     */
    protected void setCurrentColumnIndex(int newIndex) {
        columnIndex = newIndex;
    }

    /**
     * Resets the current column index for the TableBody
     *
     */
    protected void resetColumnIndex() {
        columnIndex = 1;
        for (int i = usedColumnIndices.length(); --i >= 0;) {
            usedColumnIndices.clear(i);
        }

        PendingSpan pSpan;
        for (int i = pendingSpans.size(); --i >= 0;) {
            pSpan = (PendingSpan) pendingSpans.get(i);
            if (pSpan != null) {
                pSpan.rowsLeft--;
                if (pSpan.rowsLeft == 0) {
                    pendingSpans.set(i, null);
                } else {
                    usedColumnIndices.set(i);
                }
            }
        }
        if (!firstRow) {
            setNextColumnIndex();
        }
    }

    /**
     * Increases columnIndex to the next available value
     *
     */
    protected void setNextColumnIndex() {
        while (usedColumnIndices.get(columnIndex - 1)) {
            //increment columnIndex
            columnIndex++;
        }
        //if the table has explicit columns, and
        //the index is not assigned to any
        //column, increment further until the next
        //index occupied by a column...
        if (getTable().columns != null) {
            while (columnIndex <= getTable().columns.size()
                    && !getTable().isColumnNumberUsed(columnIndex) ) {
                columnIndex++;
            }
        }
    }

    /**
     * Checks whether the previous cell had 'ends-row="true"'
     *
     * @param currentCell   the cell for which the question is asked
     * @return true if:
     *          a) there is a previous cell, which
     *             had ends-row="true"
     *          b) there is no previous cell (implicit
     *             start of row)
     */
    protected boolean previousCellEndedRow() {
        if (childNodes != null) {
            FONode prevNode = (FONode) childNodes.get(childNodes.size() - 1);
            if (prevNode.getNameId() == FO_TABLE_CELL) {
                return ((TableCell) prevNode).endsRow();
            }
        }
        return true;
    }

    /**
     * Checks whether a given column-number is already in use
     * for the current row;
     *
     * @param   colNr   the column-number to check
     * @return true if column-number is already occupied
     */
    public boolean isColumnNumberUsed(int colNr) {
        return usedColumnIndices.get(colNr - 1);
    }

    /**
     * @see com.wisii.fov.fo.flow.TableFObj#flagColumnIndices(int, int)
     */
    protected void flagColumnIndices(int start, int end) {
        for (int i = start; i < end; i++) {
            usedColumnIndices.set(i);
        }
        setNextColumnIndex();
    }
 // 用来生成供排版用的rowGroups对象
	void generateRowGroup()
	{
		Iterator rowsit = getChildNodes();
		//用来记录跨行的单元格，在第一次包含该单元格的表格行时添加
		List<TableCell> rowspancells = new ArrayList<TableCell>();
		while (rowsit.hasNext())
		{
			TableRow row = (TableRow) rowsit.next();
			Iterator tablecellit = row.getChildNodes();
			Number currentlen = 0;
			List<TableCell> remocells = new ArrayList<TableCell>();
			while (tablecellit.hasNext())
			{

				TableCell tablecell = (TableCell) tablecellit.next();
				int rowspan = tablecell.getNumberRowsSpanned();  
				Length width = (Length) tablecell.getWidth();
				boolean isaddfirst = true;
				//tablecell的父对象设置成第一个包含它的表格行
				String id = tablecell.getId();
				if(rowspan>1||(id!=null&&!id.equals("")))
				{
					boolean iscontains = false;
					for (TableCell oldcell : rowspancells)
					{
						if (oldcell.getId().equals(id))
						{
							remocells.add(tablecell);
							iscontains = true;
							tablecell = oldcell;
							width = (Length) tablecell.getWidth();
							isaddfirst = false;
							break;
						}
					}
					if (!iscontains)
					{
						rowspancells.add(tablecell);
					}
				}
				
				// 如果是百分比长度
				if (width instanceof PercentLength)
				{
					PercentLength perlen = (PercentLength) width;
					double widthfactor = perlen.value();
					if (isaddfirst)
					{
						initTableCell(tablecell, widthfactor, currentlen, false);
					}
					currentlen = currentlen.doubleValue() + widthfactor;
				}
				// 否则是固定长度
				else
				{
					int widthint = ((FixedLength) width).getValue();
					if (isaddfirst)
					{
						initTableCell(tablecell, widthint, currentlen, true);
					}
					currentlen = currentlen.intValue() + widthint;
				}
			}
			if(!remocells.isEmpty())
			{
				for(TableCell cell:remocells)
				{
					row.removeChild(cell);
				}
			}
			row.reinitChildren();
		}
	}

	private void initTableCell(TableCell tablecell, Number width,
			Number currentlen, boolean isfixedlen)
	{
		List columns = getTable().getColumns();
		int columnnumber = 1;
		int numbercolumnspan = 1;
		// 固定长度时
		if (isfixedlen)
		{
			int len = 0;
			int columnsize = columns.size();
			int celllen = currentlen.intValue();
			for (int i = 0; i < columnsize; i++)
			{
				TableColumn column = (TableColumn) columns.get(i);
				int columnwidth = column.getColumnWidth().getValue();

				if (Math.abs(celllen - len)<100)
				{
					columnnumber = i + 1;
					int cellwidth = width.intValue();
					cellwidth = cellwidth - columnwidth;
					for (int j = i + 1; j < columnsize && cellwidth> 100&&cellwidth>-100; j++)
					{
						TableColumn spancolumn = (TableColumn) columns.get(j);
						int spancolumnwidth = spancolumn.getColumnWidth()
								.getValue();
						cellwidth = cellwidth - spancolumnwidth;
						numbercolumnspan++;
					}
					break;
				}
				len = len + columnwidth;
			}
		}
		// 百分比长度时
		else
		{
			double len = 0;
			int columnsize = columns.size();
			double celllen = currentlen.doubleValue();
			for (int i = 0; i < columnsize; i++)
			{
				TableColumn column = (TableColumn) columns.get(i);
				double columnwidth = ((TableColLength) column.getColumnWidth())
						.getTableUnits();
				if (Math.abs(celllen - len) <PercentLength.PRECISION)
				{
					columnnumber = i + 1;
					double cellwidth = width.doubleValue();
					cellwidth = cellwidth - columnwidth;
					for (int j = i + 1; j < columnsize
							&& cellwidth > PercentLength.PRECISION
							&& cellwidth > -PercentLength.PRECISION; j++)
					{
						TableColumn spancolumn = (TableColumn) columns.get(j);
						double spancolumnwidth = ((TableColLength) spancolumn
								.getColumnWidth()).getTableUnits();
						cellwidth = cellwidth - spancolumnwidth;
						numbercolumnspan++;
					}
					break;
				}
				len = len + columnwidth;
			}
		}
		tablecell.setColumnNumber(new NumberProperty(columnnumber));
		tablecell.setNumberColumnsSpanned(new NumberProperty(numbercolumnspan));
	}
}
