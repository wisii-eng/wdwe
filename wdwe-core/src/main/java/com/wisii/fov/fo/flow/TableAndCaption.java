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
 *//* $Id: TableAndCaption.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

// XML
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
 * Class modelling the fo:table-and-caption property.
 * @todo needs implementation
 */
public class TableAndCaption extends FObj {
    // The value of properties relevant for fo:table-and-caption.
    private CommonAccessibility commonAccessibility;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonMarginBlock commonMarginBlock;
    private CommonRelativePosition commonRelativePosition;
    private int breakAfter;
    private int breakBefore;
    private int captionSide;
    private String id;
    private int intrusionDisplace;
    private KeepProperty keepTogether;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    private int textAlign;
    // End of property values

    static boolean notImplementedWarningGiven = false;

    /** used for FO validation */
    private boolean tableCaptionFound = false;
    private boolean tableFound = false;

    /**
     * @param parent FONode that is the parent of this object
     */
    public TableAndCaption(FONode parent) {
        super(parent);

        if (!notImplementedWarningGiven) {
            getLogger().warn("fo:table-and-caption is not yet implemented.");
            notImplementedWarningGiven = true;
        }
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
        captionSide = pList.get(PR_CAPTION_SIDE).getEnum();
        id = pList.get(PR_ID).getString();
        intrusionDisplace = pList.get(PR_INTRUSION_DISPLACE).getEnum();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        textAlign = pList.get(PR_TEXT_ALIGN).getEnum();
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
    }

    /**
     * Make sure content model satisfied, if so then tell the
     * FOEventHandler that we are at the end of the flow.
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (!tableFound) {
            missingChildElementError("marker* table-caption? table");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: marker* table-caption? table
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {

        if (FO_URI.equals(nsURI) && localName.equals("marker")) {
            if (tableCaptionFound) {
                nodesOutOfOrderError(loc, "fo:marker", "fo:table-caption");
            } else if (tableFound) {
                nodesOutOfOrderError(loc, "fo:marker", "fo:table");
            }
        } else if (FO_URI.equals(nsURI) && localName.equals("table-caption")) {
            if (tableCaptionFound) {
                tooManyNodesError(loc, "fo:table-caption");
            } else if (tableFound) {
                nodesOutOfOrderError(loc, "fo:table-caption", "fo:table");
            } else {
                tableCaptionFound = true;
            }
        } else if (FO_URI.equals(nsURI) && localName.equals("table")) {
            if (tableFound) {
                tooManyNodesError(loc, "fo:table");
            } else {
                tableFound = true;
            }
        } else {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /** @return the "id" property. */
    public String getId() {
        return id;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "table-and-caption";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_TABLE_AND_CAPTION;
    }
}

