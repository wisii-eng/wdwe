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
 *//* $Id: BidiOverride.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.awt.Color;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObjMixed;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonFont;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.SpaceProperty;
import org.xml.sax.Locator;

/**
 * fo:bidi-override element.
 */
public class BidiOverride extends FObjMixed {

    // used for FO validation
    private boolean blockOrInlineItemFound = false;
    private boolean canHaveBlockLevelChildren = true;

    // The value of properties relevant for fo:bidi-override.
    private CommonAural commonAural;
    private CommonFont commonFont;
    private CommonRelativePosition commonRelativePosition;
    private Color prColor;
    // private ToBeImplementedProperty prDirection;
    // private ToBeImplementedProperty prLetterSpacing;
    private SpaceProperty lineHeight;
    // private ToBeImplementedProperty prScoreSpaces;
    // private ToBeImplementedProperty prUnicodeBidi;
    private SpaceProperty prWordSpacing;
    // End of property values

    /**
     * @param parent FONode that is the parent of this object
     */
    public BidiOverride(FONode parent) {
        super(parent);

       /* Check to see if this node can have block-level children.
        * See validateChildNode() below.
        */
       int lvlLeader = findAncestor(FO_LEADER);
       int lvlInCntr = findAncestor(FO_INLINE_CONTAINER);
       int lvlInline = findAncestor(FO_INLINE);
       int lvlFootnote = findAncestor(FO_FOOTNOTE);

       if (lvlLeader > 0) {
           if (lvlInCntr < 0 || (lvlInCntr > 0 && lvlInCntr > lvlLeader)) {
               canHaveBlockLevelChildren = false;
           }
       } else if (lvlInline > 0 && lvlFootnote == (lvlInline + 1)) {
           if (lvlInCntr < 0 || (lvlInCntr > 0 && lvlInCntr > lvlInline)) {
               canHaveBlockLevelChildren = false;
           }
       }

    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAural = pList.getAuralProps();
        commonFont = pList.getFontProps();
        commonRelativePosition = pList.getRelativePositionProps();
        prColor = pList.get(PR_COLOR).getColor();
        // prDirection = pList.get(PR_DIRECTION);
        // prLetterSpacing = pList.get(PR_LETTER_SPACING);
        lineHeight = pList.get(PR_LINE_HEIGHT).getSpace();
        // prScoreSpaces = pList.get(PR_SCORE_SPACES);
        // prUnicodeBidi = pList.get(PR_UNICODE_BIDI);
        prWordSpacing = pList.get(PR_WORD_SPACING).getSpace();
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: marker* (#PCDATA|%inline;|%block;)*
     * Additionally: "An fo:bidi-override that is a descendant of an fo:leader
     *  or of the fo:inline child of an fo:footnote may not have block-level
     *  children, unless it has a nearer ancestor that is an
     *  fo:inline-container."
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI) && localName.equals("marker")) {
            if (blockOrInlineItemFound) {
               nodesOutOfOrderError(loc, "fo:marker",
                    "(#PCDATA|%inline;|%block;)");
            }
        } else if (!isBlockOrInlineItem(nsURI, localName)) {
            invalidChildError(loc, nsURI, localName);
        } else if (!canHaveBlockLevelChildren && isBlockItem(nsURI, localName)) {
            String ruleViolated = "An fo:bidi-override"
                + " that is a descendant of an fo:leader or of the fo:inline child"
                + " of an fo:footnote may not have block-level children, unless it"
                + " has a nearer ancestor that is an fo:inline-container.";
            invalidChildError(loc, nsURI, localName, ruleViolated);
        } else {
            blockOrInlineItemFound = true;
        }
    }

    /**
     * @return the "line-height" property.
     */
    public SpaceProperty getLineHeight() {
        return lineHeight;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "bidi-override";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_BIDI_OVERRIDE;
    }
}
