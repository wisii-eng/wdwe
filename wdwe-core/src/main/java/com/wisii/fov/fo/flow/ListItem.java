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
 *//* $Id: ListItem.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonMarginBlock;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.KeepProperty;

/**
 * Class modelling the fo:list-item object.
 */
public class ListItem extends FObj {
    // The value of properties relevant for fo:list-item.
    private CommonAccessibility commonAccessibility;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonMarginBlock commonMarginBlock;
    private CommonRelativePosition commonRelativePosition;
    private int breakAfter;
    private int breakBefore;
    private String id;
    private int intrusionDisplace;
    private KeepProperty keepTogether;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    private int relativeAlign;
    // End of property values

    private ListItemLabel label = null;
    private ListItemBody body = null;

    /**
     * @param parent FONode that is the parent of this object
     */
    public ListItem(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAccessibility = pList.getAccessibilityProps();
        commonAural = pList.getAuralProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonMarginBlock = pList.getMarginBlockProps();
        commonRelativePosition = pList.getRelativePositionProps();
        breakAfter = pList.get(PR_BREAK_AFTER).getEnum();
        breakBefore = pList.get(PR_BREAK_BEFORE).getEnum();
        id = pList.get(PR_ID).getString();
        intrusionDisplace = pList.get(PR_INTRUSION_DISPLACE).getEnum();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        relativeAlign = pList.get(PR_RELATIVE_ALIGN).getEnum();
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
        getFOEventHandler().startListItem(this);
    }

    /**
     * Make sure content model satisfied, if so then tell the
     * FOEventHandler that we are at the end of the flow.
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (label == null || body == null) {
            missingChildElementError("marker* (list-item-label,list-item-body)");
        }
        getFOEventHandler().endListItem(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: marker* (list-item-label,list-item-body)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI) && localName.equals("marker")) {
            if (label != null) {
                nodesOutOfOrderError(loc, "fo:marker", "fo:list-item-label");
            }
        } else if (FO_URI.equals(nsURI) && localName.equals("list-item-label")) {
            if (label != null) {
                tooManyNodesError(loc, "fo:list-item-label");
            }
        } else if (FO_URI.equals(nsURI) && localName.equals("list-item-body")) {
            if (label == null) {
                nodesOutOfOrderError(loc, "fo:list-item-label", "fo:list-item-body");
            } else if (body != null) {
                tooManyNodesError(loc, "fo:list-item-body");
            }
        } else {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     * @todo see if can/should rely on base class for this
     *    (i.e., add to childNodes instead)
     */
    public void addChildNode(FONode child) {
        int nameId = ((FObj)child).getNameId();

        if (nameId == FO_LIST_ITEM_LABEL) {
            label = (ListItemLabel) child;
        } else if (nameId == FO_LIST_ITEM_BODY) {
            body = (ListItemBody) child;
        } else if (nameId == FO_MARKER) {
            addMarker((Marker) child);
        }
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
     * @return the "break-after" property.
     */
    public int getBreakAfter() {
        return breakAfter;
    }

    /**
     * @return the "break-before" property.
     */
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
     * @return the label of the list item
     */
    public ListItemLabel getLabel() {
        return label;
    }

    /**
     * @return the body of the list item
     */
    public ListItemBody getBody() {
        return body;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "list-item";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_LIST_ITEM;
    }
}

