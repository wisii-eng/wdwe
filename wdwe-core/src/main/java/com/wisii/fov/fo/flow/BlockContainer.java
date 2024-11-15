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
 *//* $Id: BlockContainer.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.util.ArrayList;
import org.xml.sax.Locator;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.StaticPropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAbsolutePosition;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonMarginBlock;
import com.wisii.fov.fo.properties.KeepProperty;
import com.wisii.fov.fo.properties.LengthRangeProperty;

/**
 * Class modelling the fo:block-container object.
 */
public class BlockContainer extends FObj {
    // The value of properties relevant for fo:block-container.
    private CommonAbsolutePosition commonAbsolutePosition;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonMarginBlock commonMarginBlock;
    private LengthRangeProperty blockProgressionDimension;
    private int breakAfter;
    private int breakBefore;
    // private ToBeImplementedProperty clip;
    private int displayAlign;
    private Length height;
    private String id;
    private LengthRangeProperty inlineProgressionDimension;
    private int intrusionDisplace;
    private KeepProperty keepTogether;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    private int overflow;
    private Numeric referenceOrientation;
    private int span;
    private Length width;
    private int writingMode;
    private Numeric zIndex;
    // End of property values

    /** used for FO validation */
    private boolean blockItemFound = false;
    //zhangqiang添加，用来记录属性，但单元格为空时，需要添加空block，该空block需要父属性进行初始化
    private PropertyList propertylist;
    /**
     * @param parent FONode that is the parent of this object
     */
    public BlockContainer(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
    	propertylist=pList;
        commonAbsolutePosition = pList.getAbsolutePositionProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonMarginBlock = pList.getMarginBlockProps();
        blockProgressionDimension = pList.get(PR_BLOCK_PROGRESSION_DIMENSION).getLengthRange();
        breakAfter = pList.get(PR_BREAK_AFTER).getEnum();
        breakBefore = pList.get(PR_BREAK_BEFORE).getEnum();
        // clip = pList.get(PR_CLIP);
        displayAlign = pList.get(PR_DISPLAY_ALIGN).getEnum();
        height = pList.get(PR_HEIGHT).getLength();
        id = pList.get(PR_ID).getString();
        inlineProgressionDimension = pList.get(PR_INLINE_PROGRESSION_DIMENSION).getLengthRange();
        intrusionDisplace = pList.get(PR_INTRUSION_DISPLACE).getEnum();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        overflow = pList.get(PR_OVERFLOW).getEnum();
        referenceOrientation = pList.get(PR_REFERENCE_ORIENTATION).getNumeric();
        span = pList.get(PR_SPAN).getEnum();
        width = pList.get(PR_WIDTH).getLength();
        writingMode = pList.get(PR_WRITING_MODE).getEnum();
        zIndex = pList.get(PR_Z_INDEX).getNumeric();
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
        getFOEventHandler().startBlockContainer(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: marker* (%block;)+
     * But: "In addition an fo:block-container that does not generate an
     * absolutely positioned area may have a sequence of zero or more
     * fo:markers as its initial children."
     * @todo - implement above restriction if possible
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

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (!blockItemFound) {
            missingChildElementError("marker* (%block;)+");
        }
        super.endOfNode();
        if(childNodes==null)
		{
			blockItemFound = true;
			Block b = new Block(this);
			// 需要调用该方法，否则属性会没有默认值
			b.bind(new StaticPropertyList(b, propertylist));
			childNodes = new ArrayList();
			childNodes.add(b);
		}
        propertylist=null;
        getFOEventHandler().endBlockContainer(this);
    }

    /**
     * @return true (BlockContainer can generate Reference Areas)
     */
    public boolean generatesReferenceAreas() {
        return true;
    }

    /**
     * @return the Common Absolute Position Properties.
     */
    public CommonAbsolutePosition getCommonAbsolutePosition() {
        return commonAbsolutePosition;
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

    /**
     * @return the "block-progression-dimension" property.
     */
    public LengthRangeProperty getBlockProgressionDimension() {
        return blockProgressionDimension;
    }

    /** @return the display-align property. */
    public int getDisplayAlign() {
        return displayAlign;
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
     * @return the "id" property.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the "inline-progression-dimension" property.
     */
    public LengthRangeProperty getInlineProgressionDimension() {
        return inlineProgressionDimension;
    }

    /**
     * @return the "overflow" property.
     */
    public int getOverflow() {
        return overflow;
    }

    /**
     * @return the "reference-orientation" property.
     */
    public int getReferenceOrientation() {
        return referenceOrientation.getValue();
    }

    /**
     * @return the "span" property.
     */
    public int getSpan() {
        return this.span;
    }

    /**
     * @return the "writing-mode" property.
     */
    public int getWritingMode() {
        return writingMode;
    }

    /**
     * @return the width property
     */
    public Length getWidth() {
        return width;
    }

    /**
     * @return the height property
     */
    public Length getHeight() {
        return height;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "block-container";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_BLOCK_CONTAINER;
    }
}

