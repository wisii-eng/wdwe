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
 *//* $Id: InlineLevel.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.awt.Color;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObjMixed;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonFont;
import com.wisii.fov.fo.properties.CommonMarginInline;
import com.wisii.fov.fo.properties.SpaceProperty;

/**
 * Class modelling the commonalities of several inline-level
 * formatting objects.
 */
public abstract class InlineLevel extends FObjMixed {

    // The value of properties relevant for inline-level FOs.
    protected CommonBorderPaddingBackground commonBorderPaddingBackground;
    protected CommonAccessibility commonAccessibility;
    protected CommonMarginInline commonMarginInline;
    protected CommonAural commonAural;
    protected CommonFont commonFont;
    protected Color color;
    protected SpaceProperty lineHeight;
    protected int visibility;
    // End of property values

    /**
     * @param parent FONode that is the parent of this object
     */
    protected InlineLevel(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonBorderPaddingBackground = pList.getBorderPaddingBackgroundProps();
        commonAccessibility = pList.getAccessibilityProps();
        commonMarginInline = pList.getMarginInlineProps();
        commonAural = pList.getAuralProps();
        commonFont = pList.getFontProps();
        color = pList.get(PR_COLOR).getColor();
        lineHeight = pList.get(PR_LINE_HEIGHT).getSpace();
        visibility = pList.get(PR_VISIBILITY).getEnum();
    }

    /**
     * @return the Common Margin Properties-Inline.
     */
    public CommonMarginInline getCommonMarginInline() {
        return commonMarginInline;
    }

    /**
     * @return the Common Border, Padding, and Background Properties.
     */
    public CommonBorderPaddingBackground getCommonBorderPaddingBackground() {
        return commonBorderPaddingBackground;
    }

    /**
     * @return the Common Font Properties.
     */
    public CommonFont getCommonFont() {
        return commonFont;
    }

    /**
     * @return the "color" property.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the "line-height" property
     */
    public SpaceProperty getLineHeight() {
        return lineHeight;
    }

}

