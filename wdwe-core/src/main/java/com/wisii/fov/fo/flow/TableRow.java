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
 *//* $Id: TableRow.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xml.serializer.AttributesImplSerializer;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
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
import com.wisii.fov.fo.properties.NumberProperty;

/**
 * Class modelling the fo:table-row object.
 */
public class TableRow extends TableFObj {
    // The value of properties relevant for fo:table-row.
    private CommonAccessibility commonAccessibility;
    private LengthRangeProperty blockProgressionDimension;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonRelativePosition commonRelativePosition;
    private int breakAfter;
    private int breakBefore;
    private Length height;
    private String id;
    private KeepProperty keepTogether;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    private int visibility;
    // End of property values

    private boolean setup = false;

    protected List pendingSpans;
    protected BitSet usedColumnIndices;
    private int columnIndex = 1;
    private PropertyList propertylist;

    /**
     * @param parent FONode that is the parent of this object
     */
    public TableRow(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
    	propertylist = pList;
        commonAccessibility = pList.getAccessibilityProps();
        blockProgressionDimension
            = pList.get(PR_BLOCK_PROGRESSION_DIMENSION).getLengthRange();
        commonAural = pList.getAuralProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonRelativePosition = pList.getRelativePositionProps();
        breakAfter = pList.get(PR_BREAK_AFTER).getEnum();
        breakBefore = pList.get(PR_BREAK_BEFORE).getEnum();
        id = pList.get(PR_ID).getString();
        height = pList.get(PR_HEIGHT).getLength();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        visibility = pList.get(PR_VISIBILITY).getEnum();
        super.bind(pList);
    }

    /**
     * Adds a cell to this row (skips marker handling done by
     * FObj.addChildNode().
     * Used by TableBody during the row building process when only cells are
     * used as direct children of a table-body/header/footer.
     * @param cell cell to add.
     */
    protected void addReplacedCell(TableCell cell) {
        if (childNodes == null) {
            childNodes = new java.util.ArrayList();
        }
        childNodes.add(cell);
    }

    /**
     * @see com.wisii.fov.fo.FONode#processNode(String, Locator,
     *                                  Attributes, PropertyList)
     */
    public void processNode(String elementName, Locator locator,
            Attributes attlist, PropertyList pList) throws FOVException {
        if (!inMarker()) {
            TableBody body = (TableBody) parent;
            body.resetColumnIndex();
            pendingSpans = body.pendingSpans;
            usedColumnIndices = body.usedColumnIndices;
            while (usedColumnIndices.get(columnIndex - 1)) {
                columnIndex++;
            }
        }
        super.processNode(elementName, locator, attlist, pList);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
        getFOEventHandler().startRow(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (childNodes == null) {
//        	return;
            missingChildElementError("(table-cell+)");
        }
        if (!inMarker()) {
            pendingSpans = null;
            usedColumnIndices = null;
        }
        getFOEventHandler().endRow(this);
      
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: (table-cell+)
     */
    protected void validateChildNode(Locator loc, String nsURI,
                                     String localName)
        throws ValidationException {
        if (!(FO_URI.equals(nsURI) && localName.equals("table-cell"))) {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * @return the "id" property.
     */
    public String getId() {
        return id;
    }

    /** @return the "break-after" property. */
    public int getBreakAfter() {
        return breakAfter;
    }

    /** @return the "break-before" property. */
    public int getBreakBefore() {
        return breakBefore;
    }

    /** @return the "keep-with-previous" property. */
    public KeepProperty getKeepWithPrevious() {
        return keepWithPrevious;
    }

    /** @return the "keep-with-next" property. */
    public KeepProperty getKeepWithNext() {
        return keepWithNext;
    }

    /** @return the "keep-together" property. */
    public KeepProperty getKeepTogether() {
        return keepTogether;
    }

    /**
     * Convenience method to check if a keep-together
     * constraint is specified.
     * @return true if keep-together is active.
     */
    public boolean mustKeepTogether() {
        return !getKeepTogether().getWithinPage().isAuto()
                || !getKeepTogether().getWithinColumn().isAuto();
    }

    /**
     * Convenience method to check if a keep-with-next
     * constraint is specified.
     * @return true if keep-with-next is active.
     */
    public boolean mustKeepWithNext() {
        return !getKeepWithNext().getWithinPage().isAuto()
                || !getKeepWithNext().getWithinColumn().isAuto();
    }

    /**
     * Convenience method to check if a keep-with-previous
     * constraint is specified.
     * @return true if keep-with-previous is active.
     */
    public boolean mustKeepWithPrevious() {
        return !getKeepWithPrevious().getWithinPage().isAuto()
                || !getKeepWithPrevious().getWithinColumn().isAuto();
    }

    /**
     * @return the "block-progression-dimension" property.
     */
    public LengthRangeProperty getBlockProgressionDimension() {
        return blockProgressionDimension;
    }

    /**
     * @return the "height" property.
     */
    public Length getHeight() {
        return height;
    }

    /**
     * @return the Common Border, Padding, and Background Properties.
     */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return commonBorderPaddingBackground;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "table-row";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_TABLE_ROW;
    }

    /**
     * Returns the current column index of the TableRow
     *
     * @return the next column number to use
     */
    public int getCurrentColumnIndex() {
        return columnIndex;
    }

    /**
     * Sets the current column index to a specific value
     * in case a column-number was explicitly specified
     * (used by ColumnNumberPropertyMaker.make())
     *
     * @param newIndex  new value for column index
     */
    public void setCurrentColumnIndex(int newIndex) {
        columnIndex = newIndex;
    }

    /**
     * Checks whether a given column-number is already in use
     * for the current row (used by TableCell.bind());
     *
     * @param colNr the column-number to check
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
        // update columnIndex for the next cell
        while (usedColumnIndices.get(columnIndex - 1)) {
            columnIndex++;
        }
    }
    void reinitChildren()
    {
    	boolean isneedaddnullcell = true;
    	if(childNodes!=null&&!childNodes.isEmpty())
    	{
    		int size = childNodes.size();
    		for (int i=0;i<size;i++)
			{
    			TableCell tablecell = (TableCell) childNodes.get(i);
				if (tablecell.getNumberRowsSpanned() == 1)
				{
					isneedaddnullcell = false;
					break;
				}
			}
			
    	}
    	if (isneedaddnullcell)
		{
			Map<Integer, Object> atts = new HashMap<Integer, Object>();
			TableCell tablecell = new TableCell(this);
			try
			{
				tablecell.processNode("tablecell", null,new AttributesImplSerializer(), new StaticPropertyList(tablecell, propertylist));
				propertylist = null;
			} catch (FOVException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tablecell.setColumnNumber(new NumberProperty(((Table) this.getParent()
					.getParent()).getColumns().size() + 1));
			if(childNodes==null)
			{
				childNodes = new ArrayList();
			}
			childNodes.add(tablecell);
		}
    }
}
