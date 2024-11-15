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
 *//* $Id: PageNumberCitationLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.fo.flow.PageNumberCitation;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.Resolvable;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.UnresolvedPageNumber;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.fonts.Font;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.LayoutManager;
import com.wisii.fov.layoutmgr.PositionIterator;
import com.wisii.fov.layoutmgr.TraitSetter;

/**
 * LayoutManager for the fo:page-number-citation formatting object
 */
public class PageNumberCitationLayoutManager extends LeafNodeLayoutManager {

    private PageNumberCitation fobj;
    /** Font for the page-number-citation */
    protected Font font;

    /** Indicates whether the page referred to by the citation has been resolved yet */
    protected boolean resolved = false;

    /**
     * Constructor
     *
     * @param node the formatting object that creates this area
     * @todo better retrieval of font info
     */
    public PageNumberCitationLayoutManager(PageNumberCitation node) {
        super(node);
        fobj = node;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#initialize */
    public void initialize() {
        font = fobj.getCommonFont().getFontState(fobj.getFOEventHandler().getFontInfo(), this);
        setCommonBorderPaddingBackground(fobj.getCommonBorderPaddingBackground());
    }

    /**
     * @see LeafNodeLayoutManager#makeAlignmentContext(LayoutContext)
     */
    protected AlignmentContext makeAlignmentContext(LayoutContext context) {
        return new AlignmentContext(
                font
                , fobj.getLineHeight().getOptimum(this).getLength().getValue(this)
                , fobj.getAlignmentAdjust()
                , fobj.getAlignmentBaseline()
                , fobj.getBaselineShift()
                , fobj.getDominantBaseline()
                , context.getAlignmentContext()
            );
    }

    /** @see com.wisii.fov.layoutmgr.inline.LeafNodeLayoutManager#get(LayoutContext) */
    public InlineArea get(LayoutContext context) {
        curArea = getPageNumberCitationInlineArea(parentLM);
        return curArea;
    }

    /**
     * @see com.wisii.fov.layoutmgr.inline.LeafNodeLayoutManager#addAreas(PositionIterator
     *                                                                      , LayoutContext)
     */
    public void addAreas(PositionIterator posIter, LayoutContext context) {
        super.addAreas(posIter, context);
        if (!resolved) {
            getPSLM().addUnresolvedArea(fobj.getRefId(), (Resolvable) curArea);
        }
    }

    /**
     * if id can be resolved then simply return a word, otherwise
     * return a resolvable area
     */
    private InlineArea getPageNumberCitationInlineArea(LayoutManager parentLM) {
        PageViewport page = getPSLM().getFirstPVWithID(fobj.getRefId());
        TextArea text = null;
        if (page != null) {
            String str = page.getPageNumberString();
            // get page string from parent, build area
            text = new TextArea();
            int width = getStringWidth(str);
            text.addWord(str, 0);
            text.setIPD(width);
            resolved = true;
        } else {
            resolved = false;
            text = new UnresolvedPageNumber(fobj.getRefId(), font);
            String str = "MMM"; // reserve three spaces for page number
            int width = getStringWidth(str);
            text.setIPD(width);
        }
        updateTextAreaTraits(text);

        return text;
    }

    /**
     * Updates the traits for the generated text area.
     * @param text the text area
     */
    protected void updateTextAreaTraits(TextArea text) {
        TraitSetter.setProducerID(text, fobj.getId());
        text.setBPD(font.getAscender() - font.getDescender());
        text.setBaselineOffset(font.getAscender());
        TraitSetter.addFontTraits(text, font);
        text.addTrait(Trait.COLOR, fobj.getColor());
        TraitSetter.addTextDecoration(text, fobj.getTextDecoration());
    }

    /**
     * @param str string to be measured
     * @return width (in millipoints ??) of the string
     */
    protected int getStringWidth(String str) {
        int width = 0;
        for (int count = 0; count < str.length(); count++) {
            width += font.getCharWidth(str.charAt(count));
        }
        return width;
    }

    /** @see com.wisii.fov.layoutmgr.inline.LeafNodeLayoutManager#addId() */
    protected void addId() {
        getPSLM().addIDToPage(fobj.getId());
    }
}

