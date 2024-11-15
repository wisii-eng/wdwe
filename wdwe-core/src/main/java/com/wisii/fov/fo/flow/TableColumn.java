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
 *//* $Id: TableColumn.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

// XML
import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.StaticPropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.expr.PropertyException;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.Property;

/**
 * Class modelling the fo:table-column object.
 */
public class TableColumn extends TableFObj {
    // The value of properties relevant for fo:table-column.
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private Numeric columnNumber;
    private Length columnWidth;
    private Numeric numberColumnsRepeated;
    private Numeric numberColumnsSpanned;
    private int visibility;
    // End of property values

    private boolean defaultColumn;
    private StaticPropertyList pList = null;

    /**
     * @param parent FONode that is the parent of this object
     */
    public TableColumn(FONode parent) {
        this(parent, false);
    }

    /**
     * @param parent FONode that is the parent of this object
     * @param defaultColumn true if this table-column has been manually created as a default column
     */
    public TableColumn(FONode parent, boolean defaultColumn) {
        super(parent);
        this.defaultColumn = defaultColumn;
    }


    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        columnNumber = pList.get(PR_COLUMN_NUMBER).getNumeric();
        columnWidth = pList.get(PR_COLUMN_WIDTH).getLength();
        numberColumnsRepeated = pList.get(PR_NUMBER_COLUMNS_REPEATED).getNumeric();
        numberColumnsSpanned = pList.get(PR_NUMBER_COLUMNS_SPANNED).getNumeric();
        visibility = pList.get(PR_VISIBILITY).getEnum();
        super.bind(pList);

        if (numberColumnsRepeated.getValue() <= 0) {
			throw new PropertyException("number-columns-repeated必须大于0, "
                    + "得到的 " + numberColumnsRepeated.getValue());
        }
        if (numberColumnsSpanned.getValue() <= 0) {
			throw new PropertyException("number-columns-repeated必须大于0, "
                    + "得到的 " + numberColumnsSpanned.getValue());
        }
        this.pList = new StaticPropertyList(this, pList);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode()
     */
    protected void startOfNode() throws FOVException {
        getFOEventHandler().startColumn(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        getFOEventHandler().endColumn(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: empty
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
            invalidChildError(loc, nsURI, localName);
    }

    /**
     * @return the Common Border, Padding, and Background Properties.
     */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return commonBorderPaddingBackground;
    }

    /**
     * @return the "column-width" property.
     */
    public Length getColumnWidth() {
        return columnWidth;
    }

    /**
     * add by zhangqiang 行加列的时候，需要计算列宽，计算的列宽通过此方式记录
     * Sets the column width.
     * @param columnWidth the column width
     */
    public void setColumnWidth(Length columnWidth) {
        this.columnWidth = columnWidth;
    }

    /**
	 * @param columnNumber 设置columnNumber成员变量的值
	
	 * 值约束说明
	
	 */
	public void setColumnNumber(Numeric columnNumber)
	{
		this.columnNumber = columnNumber;
	}

	/**
     * @return the "column-number" property.
     */
    public int getColumnNumber() {
        return columnNumber.getValue();
    }

    /** @return value for number-columns-repeated. */
    public int getNumberColumnsRepeated() {
        return numberColumnsRepeated.getValue();
    }

    /** @return value for number-columns-spanned. */
    public int getNumberColumnsSpanned() {
        return numberColumnsSpanned.getValue();
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "table-column";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_TABLE_COLUMN;
    }

    /**
     * Indicates whether this table-column has been created as default column for this table in
     * case no table-columns have been defined. Note that this only used to provide better
     * user feedback (see ColumnSetup).
     * @return true if this table-column has been created as default column
     */
    public boolean isDefaultColumn() {
        return defaultColumn;
    }

    /** @see java.lang.Object#toString() */
    public String toString() {
        StringBuffer sb = new StringBuffer("fo:table-column");
        sb.append(" column-number=").append(getColumnNumber());
        if (getNumberColumnsRepeated() > 1) {
            sb.append(" number-columns-repeated=").append(getNumberColumnsRepeated());
        }
        if (getNumberColumnsSpanned() > 1) {
            sb.append(" number-columns-spanned=").append(getNumberColumnsSpanned());
        }
        sb.append(" column-width=").append(getColumnWidth());
        return sb.toString();
    }

    /**
     * Retrieve a property value through its Id; used by from-table-column() function
     *
     * @param propId    the id for the property to retrieve
     * @return the requested Property
     * @throws PropertyException
     */
    public Property getProperty(int propId) throws PropertyException {
        return this.pList.getInherited(propId);
    }

    /**
     * Clear the reference to the PropertyList (retained for from-table-column())
     *
     */
    protected void releasePropertyList() {
        this.pList = null;
    }
}
