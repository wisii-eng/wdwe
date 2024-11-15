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
 *//* $Id: Table.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Locator;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.PercentBase;
import com.wisii.fov.datatypes.ValidationPercentBaseContext;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.StaticPropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonMarginBlock;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.FixedLength;
import com.wisii.fov.fo.properties.KeepProperty;
import com.wisii.fov.fo.properties.LengthPairProperty;
import com.wisii.fov.fo.properties.LengthRangeProperty;
import com.wisii.fov.fo.properties.NumberProperty;
import com.wisii.fov.fo.properties.PercentLength;
import com.wisii.fov.fo.properties.TableColLength;

/**
 * Class modelling the fo:table object.
 */
public class Table extends TableFObj {
    // The value of properties relevant for fo:table.
    private CommonAccessibility commonAccessibility;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonMarginBlock commonMarginBlock;
    private CommonRelativePosition commonRelativePosition;
    private LengthRangeProperty blockProgressionDimension;
    private int borderCollapse;
    private LengthPairProperty borderSeparation;
    private int breakAfter;
    private int breakBefore;
    private String id;
    private LengthRangeProperty inlineProgressionDimension;
    private int intrusionDisplace;
    //private Length height;
    private KeepProperty keepTogether;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    private int tableLayout;
    private int tableOmitFooterAtBreak;
    private int tableOmitHeaderAtBreak;
    //private Length width;
    private int writingMode;
    // End of property values

    private static final int MINCOLWIDTH = 10000; // 10pt

    /** collection of columns in this table */
    protected List columns = null;
    private int columnIndex = 1;
    private BitSet usedColumnIndices = new BitSet();
    private TableBody tableHeader = null;
    private TableBody tableFooter = null;

    /** used for validation */
    private boolean tableColumnFound = false;
    private boolean tableHeaderFound = false;
    private boolean tableFooterFound = false;
    private boolean tableBodyFound = false;

    /**
     * Default table-column used when no columns are specified. It is used
     * to handle inheritance (especially visibility) and defaults properly. */
    private TableColumn defaultColumn;
    //zhangqiang 添加，如果没有列信息时，行加单元格方式，需要用他来初始化列
    private PropertyList propertylist;
    /**
     * @param parent FONode that is the parent of this object
     */
    public Table(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
    	propertylist =  pList;
        commonAccessibility = pList.getAccessibilityProps();
        commonAural = pList.getAuralProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonMarginBlock = pList.getMarginBlockProps();
        commonRelativePosition = pList.getRelativePositionProps();
        blockProgressionDimension = pList.get(PR_BLOCK_PROGRESSION_DIMENSION).getLengthRange();
        borderCollapse = pList.get(PR_BORDER_COLLAPSE).getEnum();
        borderSeparation = pList.get(PR_BORDER_SEPARATION).getLengthPair();
        breakAfter = pList.get(PR_BREAK_AFTER).getEnum();
        breakBefore = pList.get(PR_BREAK_BEFORE).getEnum();
        id = pList.get(PR_ID).getString();
        inlineProgressionDimension = pList.get(PR_INLINE_PROGRESSION_DIMENSION).getLengthRange();
        intrusionDisplace = pList.get(PR_INTRUSION_DISPLACE).getEnum();
        //height = pList.get(PR_HEIGHT).getLength();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        tableLayout = pList.get(PR_TABLE_LAYOUT).getEnum();
        tableOmitFooterAtBreak = pList.get(PR_TABLE_OMIT_FOOTER_AT_BREAK).getEnum();
        tableOmitHeaderAtBreak = pList.get(PR_TABLE_OMIT_HEADER_AT_BREAK).getEnum();
        //width = pList.get(PR_WIDTH).getLength();
        writingMode = pList.get(PR_WRITING_MODE).getEnum();
        super.bind(pList);

        //Create default column in case no table-columns will be defined.
        defaultColumn = new TableColumn(this, true);
        PropertyList colPList = new StaticPropertyList(defaultColumn, pList);
        colPList.setWritingMode();
        defaultColumn.bind(colPList);

        if (borderCollapse != EN_SEPARATE) {
            //TODO Remove once the collapsing border is at least marginally working.
            borderCollapse = EN_SEPARATE;
            log.debug("A table has been forced to use the separate border model"
                    + " (border-collapse=\"separate\") as the collapsing border model"
                    + " is not implemented, yet.");
        }
        if (tableLayout == EN_AUTO) {
            attributeWarning("table-layout=\"auto\" is currently not supported by FOV");
        }
        if (!isSeparateBorderModel() && getCommonBorderPaddingBackground().hasPadding(
                ValidationPercentBaseContext.getPseudoContextForValidationPurposes())) {
            //See "17.6.2 The collapsing border model" in CSS2
            attributeWarning("In collapsing border model a table does not have padding"
                    + " (see http://www.w3.org/TR/REC-CSS2/tables.html#collapsing-borders)"
                    + ", but a non-zero value for padding was found. The padding will be ignored.");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
        getFOEventHandler().startTable(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: (marker*,table-column*,table-header?,table-footer?,table-body+)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI)) {
            if (localName.equals("marker")) {
                if (tableColumnFound || tableHeaderFound || tableFooterFound
                        || tableBodyFound) {
                   nodesOutOfOrderError(loc, "fo:marker",
                       "(table-column*,table-header?,table-footer?,table-body+)");
                }
            } else if (localName.equals("table-column")) {
                tableColumnFound = true;
                if (tableHeaderFound || tableFooterFound || tableBodyFound) {
                    nodesOutOfOrderError(loc, "fo:table-column",
                        "(table-header?,table-footer?,table-body+)");
                }
            } else if (localName.equals("table-header")) {
                if (tableHeaderFound) {
                    tooManyNodesError(loc, "table-header");
                } else {
                    tableHeaderFound = true;
                    if (tableFooterFound || tableBodyFound) {
                        nodesOutOfOrderError(loc, "fo:table-header",
                            "(table-footer?,table-body+)");
                    }
                }
            } else if (localName.equals("table-footer")) {
                if (tableFooterFound) {
                    tooManyNodesError(loc, "table-footer");
                } else {
                    tableFooterFound = true;
                    if (tableBodyFound) {
                        nodesOutOfOrderError(loc, "fo:table-footer",
                            "(table-body+)");
                    }
                }
            } else if (localName.equals("table-body")) {
                tableBodyFound = true;
            } else {
                invalidChildError(loc, nsURI, localName);
            }
        } else {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {

         if (!tableBodyFound) {
           missingChildElementError(
                   "(marker*,table-column*,table-header?,table-footer?"
                       + ",table-body+)");
        }
        if (!inMarker()) {
            if (columns != null && !columns.isEmpty()) {
                for (int i = columns.size(); --i >= 0;) {
                    TableColumn col = (TableColumn) columns.get(i);
                    if (col != null) {
                        col.releasePropertyList();
                    }
                }
            }
        }
        if(childNodes==null)return;
        for(int i=childNodes.size()-1;i>=0;i--)

		{
			FONode child = (FONode) childNodes.get(i);
			if (child.getChildNodes() == null)
			{
				childNodes.remove(i);

			}
		}
       if(childNodes.size()==0)
       {
    	   childNodes=null;
       }
        tableBodyFound =childNodes!=null;;
        if(tableHeaderFound&&tableHeader.getChildNodes()==null)
        {
        	tableHeaderFound=false;
        	tableHeader=null;
        }
        if(tableFooterFound&&tableFooter.getChildNodes()==null)
        {
        	tableFooterFound=false;
        	tableFooter=null;
        }
        //如果无列信息，证明是行加单元格方式，此时将这种模式转换成行加列方式
        if((columns==null||columns.isEmpty())&&(tableBodyFound||tableHeaderFound||tableFooterFound))
        {
        	reinitTable();
        }
        getFOEventHandler().endTable(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     */
    protected void addChildNode(FONode child) throws FOVException {
        if ("fo:table-column".equals(child.getName())) {
            if (columns == null) {
                columns = new java.util.ArrayList();
            }
            if (!inMarker()) {
                addColumnNode((TableColumn) child);
            } else {
                columns.add((TableColumn) child);
            }
        } else {
            if ("fo:table-footer".equals(child.getName())) {
                tableFooter = (TableBody) child;
            } else if ("fo:table-header".equals(child.getName())) {
                tableHeader = (TableBody) child;
            } else {
                // add bodies
                super.addChildNode(child);
            }
        }
    }

    /**
     * Adds a column to the columns List, and updates the columnIndex
     * used for determining initial values for column-number
     *
     * @param col   the column to add
     * @throws FOVException
     */
    private void addColumnNode(TableColumn col) {
        int colNumber = col.getColumnNumber();
        int colRepeat = col.getNumberColumnsRepeated();
        if (columns.size() < colNumber) {
            //add nulls for non-occupied indices between
            //the last column up to and including the current one
            while (columns.size() < colNumber) {
                columns.add(null);
            }
        }
        //replace the null-value with the actual column
        columns.set(colNumber - 1, col);
        if (colRepeat > 1) {
            //in case column is repeated:
            //for the time being, add the same column
            //(colRepeat - 1) times to the columns list
            //TODO: need to force the column-number (?)
            for (int i = colRepeat - 1; --i >= 0;) {
                columns.add(col);
            }
        }
        //flag column indices used by this column
        int startIndex = columnIndex - 1;
        int endIndex = startIndex + colRepeat;
        flagColumnIndices(startIndex, endIndex);
    }

    /** @return true of table-layout="auto" */
    public boolean isAutoLayout() {
        return (tableLayout != EN_FIXED);
    }

    /** @return the default table column */
    public TableColumn getDefaultColumn() {
        return this.defaultColumn;
    }

    /** @return the list of table-column elements. */
    public List getColumns() {
        return columns;
    }

    /**
     * @param index index of the table-body element.
     * @return the requested table-body element
     */
    public TableBody getBody(int index) {
        return (TableBody) childNodes.get(index);
    }

    /** @return the body for the table-header. */
    public TableBody getTableHeader() {
        return tableHeader;
    }

    /** @return the body for the table-footer. */
    public TableBody getTableFooter() {
        return tableFooter;
    }

    /** @return true if the table-header should be omitted at breaks */
    public boolean omitHeaderAtBreak() {
        return (this.tableOmitHeaderAtBreak == EN_TRUE);
    }

    /** @return true if the table-footer should be omitted at breaks */
    public boolean omitFooterAtBreak() {
        return (this.tableOmitFooterAtBreak == EN_TRUE);
    }

    /**
     * @return the "inline-progression-dimension" property.
     */
    public LengthRangeProperty getInlineProgressionDimension() {
        return inlineProgressionDimension;
    }

    /**
     * @return the "block-progression-dimension" property.
     */
    public LengthRangeProperty getBlockProgressionDimension() {
        return blockProgressionDimension;
    }

    /**
     * @return the Common Margin Properties-Block.
     */
    public CommonMarginBlock getCommonMarginBlock() {
        return commonMarginBlock;
    }

    /**
     * @return the Common Border, Padding, and Background Properties.
     */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return commonBorderPaddingBackground;
    }

    /** @return the "break-after" property. */
    public int getBreakAfter() {
        return breakAfter;
    }

    /** @return the "break-before" property. */
    public int getBreakBefore() {
        return breakBefore;
    }

    /** @return the "keep-with-next" property.  */
    public KeepProperty getKeepWithNext() {
        return keepWithNext;
    }

    /** @return the "keep-with-previous" property.  */
    public KeepProperty getKeepWithPrevious() {
        return keepWithPrevious;
    }

    /** @return the "keep-together" property.  */
    public KeepProperty getKeepTogether() {
        return keepTogether;
    }

    /**
     * Convenience method to check if a keep-together constraint is specified.
     * @return true if keep-together is active.
     */
    public boolean mustKeepTogether() {
        return !getKeepTogether().getWithinPage().isAuto()
                || !getKeepTogether().getWithinColumn().isAuto();
    }

    /** @return the "border-collapse" property. */
    public int getBorderCollapse() {
        return borderCollapse;
    }

    /** @return true if the separate border model is active */
    public boolean isSeparateBorderModel() {
        return (getBorderCollapse() == EN_SEPARATE);
    }

    /** @return the "border-separation" property. */
    public LengthPairProperty getBorderSeparation() {
        return borderSeparation;
    }

    /**
     * @return the "id" property.
     */
    public String getId() {
        return id;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "table";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_TABLE;
    }

    /**
     * Returns the current column index of the Table
     *
     * @return the next column number to use
     */
    public int getCurrentColumnIndex() {
        return columnIndex;
    }

    /**
     * Checks if a certain column-number is already occupied
     *
     * @param colNr the column-number to check
     * @return true if column-number is already in use
     */
    public boolean isColumnNumberUsed(int colNr) {
        return usedColumnIndices.get(colNr - 1);
    }

    /**
     * Sets the current column index of the given Table
     * (used by ColumnNumberPropertyMaker.make() in case the column-number
     * was explicitly specified)
     *
     * @param   newIndex    the new value for column index
     */
    public void setCurrentColumnIndex(int newIndex) {
        columnIndex = newIndex;
    }

    /**
     * @see com.wisii.fov.fo.flow.TableFObj#flagColumnIndices(int, int)
     */
    protected void flagColumnIndices(int start, int end) {
        for (int i = start; i < end; i++) {
            usedColumnIndices.set(i);
        }
        //set index for the next column to use
        while (usedColumnIndices.get(columnIndex - 1)) {
            columnIndex++;
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#clone(FONode, boolean)
     */
    public FONode clone(FONode parent, boolean removeChildren)
        throws FOVException {
        FObj fobj = (FObj) super.clone(parent, removeChildren);
        if (removeChildren) {
            Table t = (Table) fobj;
            t.columns = null;
            t.tableHeader = null;
            t.tableFooter = null;
        }
        return fobj;
    }
    //zhangqiang添加begin，用来处理行加列方式时计算得到列信息
    /**
	 * 该方法在表格的大小，跨行，跨列等属性变化时调用
	 */
	private void reinitTable()
	{
		// 重新生成列信息
		reinitColumn();
		// 根据列信息生成单元格的跨行信息
		reinitTablePart();

	}
    private Iterator getChildren()
	{
		List children = new ArrayList();
		if (tableHeader != null)
		{
			children.add(tableHeader);
		}
		if (childNodes != null && !childNodes.isEmpty())
		{
			children.addAll(childNodes);
		}
		if (tableFooter != null)
		{
			children.add(tableFooter);
		}
		return children.iterator();
	}
	/*
	 * 重新生成列信息，算法思路为遍历每一行中的单元格， 得到宽度信息，和已有宽度信息比较，已有宽度信息中不包括该
	 * 宽度时，则将该宽度信息添加到已有宽度信息的合适位置（保证宽度是从小到大排列的）
	 */
	private void reinitColumn()
	{
		
		Iterator tablebodyit = getChildren();
		List<Number> sortlengths = new LinkedList<Number>();
		PercentBase percentbase = null;
		//用来记录跨行的单元格，在第一次包含该单元格的表格行时添加
		List<TableCell> rowspancells = new ArrayList<TableCell>();
		while (tablebodyit.hasNext())
		{
			TableBody tablebody = (TableBody) tablebodyit
					.next();
			Iterator rowsit = tablebody.getChildNodes();
			while (rowsit.hasNext())
			{
				TableRow row = (TableRow) rowsit.next();
				Iterator tablecellit = row.getChildNodes();
				Number currentlen = 0;
				while (tablecellit.hasNext())
				{
					TableCell tablecell = (TableCell) tablecellit
							.next();
					int rowspan = tablecell.getNumberRowsSpanned();  
					Length width = (Length) tablecell.getWidth();
					String id = tablecell.getId();
					if(rowspan>1||(id!=null&&!id.equals("")))
					{
						boolean iscontains = false;
						for (TableCell oldcell : rowspancells)
						{
							if (oldcell.getId().equals(id))
							{
								iscontains = true;
								tablecell = oldcell;
								width = (Length) tablecell.getWidth();
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
						if (percentbase == null)
						{
							percentbase = perlen.getBaseLength();
						}
						currentlen = currentlen.doubleValue()
								+ perlen.value();
						addlenassort(sortlengths, currentlen);
					}
					// 否则是固定长度
					else
					{
						currentlen = currentlen.intValue()
								+ ((FixedLength) width).getValue();
						addlenassort(sortlengths, currentlen);
					}
				}
			}
		}
		columns = new ArrayList<TableColumn>();
		int size = sortlengths.size();
		// 如果percentbase不为null，证明单元格长度是百分比长度
		if (percentbase != null)
		{
			double oldlen = 0;
			
			for (int i = 0; i < size; i++)
			{
				double len = (Double) sortlengths.get(i);
				double width = len - oldlen;
				TableColumn column = new TableColumn(this);
				try
				{
					column.bind(new StaticPropertyList(column, propertylist));
				} catch (FOVException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				column.setColumnWidth(new TableColLength(width,column));
				column.setColumnNumber(new NumberProperty(i + 1));
				columns.add(column);
				oldlen = len;
			}

		}
		// 是固定长度时
		else
		{
			int oldlen = 0;
			for (int i = 0; i < size; i++)
			{
				int len = (Integer) sortlengths.get(i);
				int width = len - oldlen;
				TableColumn column = new TableColumn(this);
				try
				{
					column.bind(new StaticPropertyList(column, propertylist));
				} catch (FOVException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				column.setColumnWidth(new FixedLength(width));
				column.setColumnNumber(new NumberProperty(i + 1));
				columns.add(column);
				oldlen = len;
			}
		}
       //（表格中多加了一个列宽为0的列），这样在行中的单元格全部跨行时，添加一个指向该列的单元，
		//从而使得排版正确(因为现在的排版程序假定行中至少得包含一个不跨行的单元格)
		TableColumn column = new TableColumn(this);
		try
		{
			column.bind(new StaticPropertyList(column, propertylist));
		} catch (FOVException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		column.setColumnWidth(new FixedLength(0));
		column.setColumnNumber(new NumberProperty(size + 1));
		columns.add(column);
	}

	private void addlenassort(List<Number> sortlengths, Number currentlen)
	{
		// 如果长度列表中已包含该长度，则直接返回
		if (sortlengths.contains(currentlen))
		{
			return;
		}
		int size = sortlengths.size();
		int index = 0;
		// 否则，插入找到第一个小于当前长度的位置，新长度插入到该位置之后
		for (int i = size - 1; i >= 0; i--)
		{
			Number number = sortlengths.get(i);
			if (number.doubleValue() < currentlen.doubleValue())
			{
				index = i + 1;
				break;
			}
		}
		sortlengths.add(index, currentlen);
	}

	private void reinitTablePart()
	{
		Iterator childrenit = getChildren();
		TableBody lastbody = null;
		while (childrenit.hasNext())
		{
			TableBody tablebody = (TableBody) childrenit.next();
			tablebody.generateRowGroup();
			lastbody = tablebody;
		}
	}
    //zhangqiang添加ｅｎｄ　
}
