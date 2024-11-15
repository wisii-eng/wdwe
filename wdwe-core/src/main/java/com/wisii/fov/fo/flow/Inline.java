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
 *//* $Id: Inline.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.CharIterator;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.OneCharIterator;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.KeepProperty;
import com.wisii.fov.fo.properties.LengthRangeProperty;

/**
 * Class modelling the fo:inline formatting object.
 */
public class Inline extends InlineLevel {
    // The value of properties relevant for fo:inline.
    // See also superclass InlineLevel
    private CommonRelativePosition commonRelativePosition;
    private Length alignmentAdjust;
    private int alignmentBaseline;
    private Length baselineShift;
    private LengthRangeProperty blockProgressionDimension;
    private int dominantBaseline;
    private Length height;
    private String id;
    private LengthRangeProperty inlineProgressionDimension;
    private KeepProperty keepTogether;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    private Length width;
    private int wrapOption;
    // End of property values

    // used for FO validation
    private boolean blockOrInlineItemFound = false;
    private boolean canHaveBlockLevelChildren = true;

    /**
     * @param parent FONode that is the parent of this object
     */
    public Inline(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        super.bind(pList);
        commonRelativePosition = pList.getRelativePositionProps();
        alignmentAdjust = pList.get(PR_ALIGNMENT_ADJUST).getLength();
        alignmentBaseline = pList.get(PR_ALIGNMENT_BASELINE).getEnum();
        baselineShift = pList.get(PR_BASELINE_SHIFT).getLength();
        blockProgressionDimension = pList.get(PR_BLOCK_PROGRESSION_DIMENSION).getLengthRange();
        dominantBaseline = pList.get(PR_DOMINANT_BASELINE).getEnum();
        height = pList.get(PR_HEIGHT).getLength();
        id = pList.get(PR_ID).getString();
        inlineProgressionDimension = pList.get(PR_INLINE_PROGRESSION_DIMENSION).getLengthRange();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        width = pList.get(PR_WIDTH).getLength();
        wrapOption = pList.get(PR_WRAP_OPTION).getEnum();
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
       /* Check to see if this node can have block-level children.
        * See validateChildNode() below.
        */
       int lvlLeader = findAncestor(FO_LEADER);
       int lvlFootnote = findAncestor(FO_FOOTNOTE);
       int lvlInCntr = findAncestor(FO_INLINE_CONTAINER);

       if (lvlLeader > 0) {
           if (lvlInCntr < 0 ||
               (lvlInCntr > 0 && lvlInCntr > lvlLeader)) {
               canHaveBlockLevelChildren = false;
           }
       } else if (lvlFootnote > 0) {
           if (lvlInCntr < 0 || lvlInCntr > lvlFootnote) {
               canHaveBlockLevelChildren = false;
           }
       }

        checkId(id);

        getFOEventHandler().startInline(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        super.endOfNode();
        getFOEventHandler().endInline(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: marker* (#PCDATA|%inline;|%block;)*
     * Additionally: " An fo:inline that is a descendant of an fo:leader
     *  or fo:footnote may not have block-level children, unless it has a
     *  nearer ancestor that is an fo:inline-container." (paraphrased)
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
            String ruleViolated =
                " An fo:inline that is a descendant of an fo:leader" +
                " or fo:footnote may not have block-level children," +
                " unless it has a nearer ancestor that is an" +
                " fo:inline-container.";
            invalidChildError(loc, nsURI, localName, ruleViolated);
        } else {
            blockOrInlineItemFound = true;
        }
    }

    /**
     * Return the "id" property.
     */
    public String getId() {
        return id;
    }
    public boolean hasId() {
        return id != null && id.length() > 0;
    }
    /**
     * @return the "alignment-adjust" property
     */
    public Length getAlignmentAdjust() {
        return alignmentAdjust;
    }

    /**
     * @return the "alignment-baseline" property
     */
    public int getAlignmentBaseline() {
        return alignmentBaseline;
    }

    /**
     * @return the "baseline-shift" property
     */
    public Length getBaselineShift() {
        return baselineShift;
    }

    /**
     * @return the "dominant-baseline" property
     */
    public int getDominantBaseline() {
        return dominantBaseline;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "inline";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_INLINE;
    }
}
