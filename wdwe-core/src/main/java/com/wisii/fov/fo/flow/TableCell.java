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
 *//* $Id: TableCell.java,v 1.3 2007/08/28 09:32:14 hzl Exp $ */

package com.wisii.fov.fo.flow;

import java.util.ArrayList;

import org.apache.xml.serializer.AttributesImplSerializer;
import org.xml.sax.Locator;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.StaticPropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.KeepProperty;
import com.wisii.fov.fo.properties.LengthRangeProperty;

/**
 * Class modelling the fo:table-cell object.
 * @todo check need for all instance variables stored here
 */
public class TableCell extends TableFObj {
    // The value of properties relevant for fo:table-cell.
    private CommonAccessibility commonAccessibility;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonRelativePosition commonRelativePosition;
    private LengthRangeProperty blockProgressionDimension;
    private Numeric columnNumber;
    private int displayAlign;
    private int relativeAlign;
    private int emptyCells;
    private int endsRow;
    private Length height;
    private String id;
    private LengthRangeProperty inlineProgressionDimension;
    private Numeric numberColumnsSpanned;
    private Numeric numberRowsSpanned;
    private int startsRow;
    private Length width;
    private KeepProperty keepTogether;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    // End of property values

    /** used for FO validation */
    private boolean blockItemFound = false;

    /**
     * Offset of content rectangle in inline-progression-direction,
     * relative to table.
     */
    protected int startOffset;

    /**
     * Offset of content rectangle, in block-progression-direction,
     * relative to the row.
     */
    protected int beforeOffset = 0;

    /**
     * Offset of content rectangle, in inline-progression-direction,
     * relative to the column start edge.
     */
    protected int startAdjust = 0;

    /**
     * Adjust to theoretical column width to obtain content width
     * relative to the column start edge.
     */
    protected int widthAdjust = 0;

    /** For collapsed border style */
    protected int borderHeight = 0;

    /** Ypos of cell ??? */
    protected int top;
    //zhangqiang添加，用来记录属性，但单元格为空时，需要添加空block，该空block需要父属性进行初始化
    private PropertyList propertylist;
    /**
     * @param parent FONode that is the parent of this object
     */
    public TableCell(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
    	propertylist = pList;
        commonAccessibility = pList.getAccessibilityProps();
        commonAural = pList.getAuralProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonRelativePosition = pList.getRelativePositionProps();
        blockProgressionDimension = pList.get(PR_BLOCK_PROGRESSION_DIMENSION).getLengthRange();
        displayAlign = pList.get(PR_DISPLAY_ALIGN).getEnum();
        relativeAlign = pList.get(PR_RELATIVE_ALIGN).getEnum();
        emptyCells = pList.get(PR_EMPTY_CELLS).getEnum();
        endsRow = pList.get(PR_ENDS_ROW).getEnum();
        height = pList.get(PR_HEIGHT).getLength();
        id = pList.get(PR_ID).getString();
        inlineProgressionDimension = pList.get(PR_INLINE_PROGRESSION_DIMENSION).getLengthRange();
        columnNumber = pList.get(PR_COLUMN_NUMBER).getNumeric();
        numberColumnsSpanned = pList.get(PR_NUMBER_COLUMNS_SPANNED).getNumeric();
        numberRowsSpanned = pList.get(PR_NUMBER_ROWS_SPANNED).getNumeric();
        startsRow = pList.get(PR_STARTS_ROW).getEnum();
        width = pList.get(PR_WIDTH).getLength();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();

        super.bind(pList);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
        getFOEventHandler().startCell(this);
    }

    /**
     * Make sure content model satisfied, if so then tell the
     * FOEventHandler that we are at the end of the flow.
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (!blockItemFound) {
            if (getUserAgent().validateStrictly()) {
                missingChildElementError("marker* (%block;)+");
            } else if (childNodes != null && childNodes.size() > 0) {
                getLogger().warn("fo:table-cell content that is not "
                        + "enclosed by a fo:block will be dropped/ignored.");
            }
        }
        if ((startsRow() || endsRow())
                && getParent().getNameId() == FO_TABLE_ROW ) {
            getLogger().warn("starts-row/ends-row for fo:table-cells "
                    + "non-applicable for children of an fo:table-row.");
        }
        if(childNodes==null)
		{
			blockItemFound = true;
			Block b = new Block(this);
		
			//需要调用该方法，否则属性会没有默认值
			b.processNode("block", null,new AttributesImplSerializer(), new StaticPropertyList(b, propertylist));
			childNodes = new ArrayList();
			childNodes.add(b);
		}
		propertylist=null;
        getFOEventHandler().endCell(this);    	   
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: marker* (%block;)+
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI) && localName.equals("marker")) {
            if (blockItemFound) {
               nodesOutOfOrderError(loc, "fo:marker", "(%block;)");
            }
        } else if (!isBlockItem(nsURI, localName)) {
            invalidChildError(loc, nsURI, localName);
        } else {
            blockItemFound = true;
        }
    }

    /** @see com.wisii.fov.fo.FObj#generatesReferenceAreas() */
    public boolean generatesReferenceAreas() {
        return true;
    }

    /**
     * Set position relative to table (set by body?)
     *
     * @param offset    new offset
     */
    public void setStartOffset(int offset) {
        startOffset = offset;
    }

    /**
     * @return the Common Border, Padding, and Background Properties.
     */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return this.commonBorderPaddingBackground;
    }

    /**
     * @return the "column-number" property.
     */
    public int getColumnNumber() {
        return columnNumber.getValue();
    }

    /** @return true if "empty-cells" is "show" */
    public boolean showEmptyCells() {
        return (this.emptyCells == EN_SHOW);
    }

    /**
     * @return the "id" property.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the "number-columns-spanned" property.
     */
    public int getNumberColumnsSpanned() {
        return Math.max(numberColumnsSpanned.getValue(), 1);
    }

    /**
     * @return the "number-rows-spanned" property.
     */
    public int getNumberRowsSpanned() {
        return Math.max(numberRowsSpanned.getValue(), 1);
    }

    /**
     * @return the "block-progression-dimension" property.
     */
    public LengthRangeProperty getBlockProgressionDimension() {
        return blockProgressionDimension;
    }

    /**
     * @return the "inLine-progression-dimension" property.
     */
    public LengthRangeProperty getInLineProgressionDimension() {
        return inlineProgressionDimension;
    }

    /**
     * @return the "width" property.
     */
    public Length getWidth() {
        return width;
    }

    /** @return the display-align property. */
    public int getDisplayAlign() {
        return displayAlign;
    }

    /** @return true if the cell starts a row. */
    public boolean startsRow() {
        return (startsRow == EN_TRUE);
    }

    /** @return true if the cell ends a row. */
    public boolean endsRow() {
        return (endsRow == EN_TRUE);
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "table-cell";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public final int getNameId() {
        return FO_TABLE_CELL;
    }

    /**
     * @see com.wisii.fov.fo.FObj#checkId()
     */
    protected void checkId(String id) throws ValidationException
    {}

	/**
	 * @param columnNumber 设置columnNumber成员变量的值
	
	 * 值约束说明
	
	 */
	public void setColumnNumber(Numeric columnNumber)
	{
		this.columnNumber = columnNumber;
	}

	/**
	 * @param numberColumnsSpanned 设置numberColumnsSpanned成员变量的值
	
	 * 值约束说明
	
	 */
	public void setNumberColumnsSpanned(Numeric numberColumnsSpanned)
	{
		this.numberColumnsSpanned = numberColumnsSpanned;
	}
    
}
