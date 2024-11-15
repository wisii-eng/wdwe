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
 *//* $Id: PageNumberCitation.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.awt.Color;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonFont;
import com.wisii.fov.fo.properties.CommonMarginInline;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.CommonTextDecoration;
import com.wisii.fov.fo.properties.KeepProperty;
import com.wisii.fov.fo.properties.SpaceProperty;

/**
 * Class modelling the fo:page-number-citation object.
 * This inline fo is replaced with the text for a page number.
 * The page number used is the page that contains the start of the
 * block referenced with the ref-id attribute.
 */
public class PageNumberCitation extends FObj {
    // The value of properties relevant for fo:page-number-citation.
    private CommonAccessibility commonAccessibility;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonFont commonFont;
    private CommonMarginInline commonMarginInline;
    private CommonRelativePosition commonRelativePosition;
    private Length alignmentAdjust;
    private int alignmentBaseline;
    private Length baselineShift;
    private int dominantBaseline;
    private String id;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    // private ToBeImplementedProperty letterSpacing;
    private SpaceProperty lineHeight;
    private String refId;
    private int scoreSpaces;
    private Length textAltitude;
    /** Holds the text decoration values. May be null */
    private CommonTextDecoration textDecoration;
    private Length textDepth;
    // private ToBeImplementedProperty textShadow;
    private int textTransform;
    private int visibility;
    private SpaceProperty wordSpacing;
    private int wrapOption;
    // End of property values

    // Properties which are not explicitely listed but are still applicable
    private Color color;

    /**
     * @param parent FONode that is the parent of this object
     */
    public PageNumberCitation(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAccessibility = pList.getAccessibilityProps();
        commonAural = pList.getAuralProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonFont = pList.getFontProps();
        commonMarginInline = pList.getMarginInlineProps();
        commonRelativePosition = pList.getRelativePositionProps();
        alignmentAdjust = pList.get(PR_ALIGNMENT_ADJUST).getLength();
        alignmentBaseline = pList.get(PR_ALIGNMENT_BASELINE).getEnum();
        baselineShift = pList.get(PR_BASELINE_SHIFT).getLength();
        dominantBaseline = pList.get(PR_DOMINANT_BASELINE).getEnum();
        id = pList.get(PR_ID).getString();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        // letterSpacing = pList.get(PR_LETTER_SPACING);
        lineHeight = pList.get(PR_LINE_HEIGHT).getSpace();
        refId = pList.get(PR_REF_ID).getString();
        scoreSpaces = pList.get(PR_SCORE_SPACES).getEnum();
        textAltitude = pList.get(PR_TEXT_ALTITUDE).getLength();
        textDecoration = pList.getTextDecorationProps();
        textDepth = pList.get(PR_TEXT_DEPTH).getLength();
        // textShadow = pList.get(PR_TEXT_SHADOW);
        textTransform = pList.get(PR_TEXT_TRANSFORM).getEnum();
        visibility = pList.get(PR_VISIBILITY).getEnum();
        wordSpacing = pList.get(PR_WORD_SPACING).getSpace();
        wrapOption = pList.get(PR_WRAP_OPTION).getEnum();

        // implicit properties
        color = pList.get(Constants.PR_COLOR).getColor();
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
        if (refId.equals("")) {
            missingPropertyError("ref-id");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: empty
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
            invalidChildError(loc, nsURI, localName);
    }

    /** @return the Common Font Properties. */
    public CommonFont getCommonFont() {
        return commonFont;
    }

    /** @return the "color" property. */
    public Color getColor() {
        return color;
    }

    /** @return the "text-decoration" property. */
    public CommonTextDecoration getTextDecoration() {
        return textDecoration;
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

    /** @return the Common Border, Padding, and Background Properties. */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return commonBorderPaddingBackground;
    }

    /**
     * @return the "line-height" property
     */
    public SpaceProperty getLineHeight() {
        return lineHeight;
    }

    /** @return the "id" property. */
    public String getId() {
        return id;
    }

    /** @return the "ref-id" property. */
    public String getRefId() {
        return refId;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "page-number-citation";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_PAGE_NUMBER_CITATION;
    }
}
