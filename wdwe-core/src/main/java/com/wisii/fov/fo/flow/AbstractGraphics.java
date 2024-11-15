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
 *//* $Id: AbstractGraphics.java,v 1.2 2008/01/17 01:48:13 lzy Exp $ */

package com.wisii.fov.fo.flow;

import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonMarginInline;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.KeepProperty;
import com.wisii.fov.fo.properties.LengthRangeProperty;
import com.wisii.fov.fo.properties.SpaceProperty;
import com.wisii.fov.util.ColorUtil;

/**
 * Common base class for instream-foreign-object and external-graphics
 * flow formatting objects.
 */
public abstract class AbstractGraphics extends FObj {

    // The value of properties relevant for fo:instream-foreign-object
    // and external-graphics.
    private CommonAccessibility commonAccessibility;
    private CommonAural commonAural;
    private CommonBorderPaddingBackground commonBorderPaddingBackground;
    private CommonMarginInline commonMarginInline;
    private CommonRelativePosition commonRelativePosition;
    private Length alignmentAdjust;
    private int alignmentBaseline;
    private Length baselineShift;
    private LengthRangeProperty blockProgressionDimension;
    // private ToBeImplementedProperty clip;
    private Length contentHeight;
    private String contentType;
    private Length contentWidth;
    private int displayAlign;
    private int dominantBaseline;
    private Length height;
    private String id;
    private LengthRangeProperty inlineProgressionDimension;
    private KeepProperty keepWithNext;
    private KeepProperty keepWithPrevious;
    private SpaceProperty lineHeight;
    private int overflow;
    private int scaling;
    private int scalingMethod;
    private int textAlign;
    private Length width;
    /* 【添加：START 】 by 李晓光 2009-2-3*/
    private int layer = 0;
    public int getLayer(){
    	return layer;
    }
    /* 【添加：END 】 by 李晓光 2009-2-3*/
    // End of property values

    /**
     * constructs an instream-foreign-object object (called by Maker).
     *
     * @param parent the parent formatting object
     */
    public AbstractGraphics(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAccessibility = pList.getAccessibilityProps();
        commonAural = pList.getAuralProps();
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonMarginInline = pList.getMarginInlineProps();
        commonRelativePosition = pList.getRelativePositionProps();
        alignmentAdjust = pList.get(PR_ALIGNMENT_ADJUST).getLength();
        alignmentBaseline = pList.get(PR_ALIGNMENT_BASELINE).getEnum();
        baselineShift = pList.get(PR_BASELINE_SHIFT).getLength();
        blockProgressionDimension = pList.get(PR_BLOCK_PROGRESSION_DIMENSION).getLengthRange();
        // clip = pList.get(PR_CLIP);
        contentHeight = pList.get(PR_CONTENT_HEIGHT).getLength();
        contentType = pList.get(PR_CONTENT_TYPE).getString();
        contentWidth = pList.get(PR_CONTENT_WIDTH).getLength();
        displayAlign = pList.get(PR_DISPLAY_ALIGN).getEnum();
        dominantBaseline = pList.get(PR_DOMINANT_BASELINE).getEnum();
        height = pList.get(PR_HEIGHT).getLength();
        id = pList.get(PR_ID).getString();
        inlineProgressionDimension = pList.get(PR_INLINE_PROGRESSION_DIMENSION).getLengthRange();
        keepWithNext = pList.get(PR_KEEP_WITH_NEXT).getKeep();
        keepWithPrevious = pList.get(PR_KEEP_WITH_PREVIOUS).getKeep();
        lineHeight = pList.get(PR_LINE_HEIGHT).getSpace();
        overflow = pList.get(PR_OVERFLOW).getEnum();
        scaling = pList.get(PR_SCALING).getEnum();
        scalingMethod = pList.get(PR_SCALING_METHOD).getEnum();
        textAlign = pList.get(PR_TEXT_ALIGN).getEnum();
        width = pList.get(PR_WIDTH).getLength();
        
        /* 【添加：START】 by 李晓光 2009-2-3 */
        layer = pList.get(PR_GRAPHIC_LAYER).getNumber().intValue();
        ColorUtil.addLayer(new Integer(layer));
        FOUserAgent agent = getUserAgent();
        agent.addLayer(layer);
        /* 【添加：END】 by 李晓光 2009-2-3 */
    }

    /**
     * Given the ipd and the content width calculates the
     * required x offset based on the text-align property
     * @param ipd the inline-progression-dimension of the object
     * @param cwidth the calculated content width of the object
     * @return the X offset
     */
    public int computeXOffset (int ipd, int cwidth) {
        int xoffset = 0;
        switch (textAlign) {
            case EN_CENTER:
                xoffset = (ipd - cwidth) / 2;
                break;
            case EN_END:
                xoffset = ipd - cwidth;
                break;
            case EN_START:
                break;
            case EN_JUSTIFY:
            default:
                break;
        }
        return xoffset;
    }

    /**
     * Given the bpd and the content height calculates the
     * required y offset based on the display-align property
     * @param bpd the block-progression-dimension of the object
     * @param cheight the calculated content height of the object
     * @return the Y offset
     */
    public int computeYOffset(int bpd, int cheight) {
        int yoffset = 0;
        switch (displayAlign) {
            case EN_BEFORE:
                break;
            case EN_AFTER:
                yoffset = bpd - cheight;
                break;
            case EN_CENTER:
                yoffset = (bpd - cheight) / 2;
                break;
            case EN_AUTO:
            default:
                break;
        }
        return yoffset;
    }

    /**
     * @return the "id" property.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the Common Border, Padding, and Background Properties.
     */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return commonBorderPaddingBackground;
    }

    /**
     * @return the "line-height" property.
     */
    public SpaceProperty getLineHeight() {
        return lineHeight;
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
     * @return the "height" property.
     */
    public Length getHeight() {
        return height;
    }

    /**
     * @return the "width" property.
     */
    public Length getWidth() {
        return width;
    }

    /**
     * @return the "content-height" property.
     */
    public Length getContentHeight() {
        return contentHeight;
    }

    public void setContentHeight(Length l)
    {
        contentHeight = l;
    }

    /**
     * @return the "content-width" property.
     */
    public Length getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth(Length w)
    {
        contentWidth = w;
    }

    /**
     * @return the "scaling" property.
     */
    public int getScaling() {
        return scaling;
    }

    /**
     * @return the "overflow" property.
     */
    public int getOverflow() {
        return overflow;
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

    /**
     * @return the graphics intrinsic width
     */
    public abstract int getIntrinsicWidth();

    /**
     * @return the graphics intrinsic height
     */
    public abstract int getIntrinsicHeight();
}
