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
 *//* $Id: PageNumberCitationLastLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.fo.flow.PageNumberCitationLast;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.Resolvable;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.UnresolvedPageNumber;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.LayoutManager;

/**
 * LayoutManager for the fo:page-number-citation-last formatting object
 */
public class PageNumberCitationLastLayoutManager extends PageNumberCitationLayoutManager {

    private PageNumberCitationLast fobj;

    /**
     * Constructor
     *
     * @param node the formatting object that creates this area
     * @todo better retrieval of font info
     */
    public PageNumberCitationLastLayoutManager(PageNumberCitationLast node) {
        super(node);
        fobj = node;
    }

    /** @see com.wisii.fov.layoutmgr.inline.LeafNodeLayoutManager#get(LayoutContext) */
    public InlineArea get(LayoutContext context) {
        curArea = getPageNumberCitationLastInlineArea(parentLM);
        return curArea;
    }

    /**
     * if id can be resolved then simply return a word, otherwise
     * return a resolvable area
     */
    private InlineArea getPageNumberCitationLastInlineArea(LayoutManager parentLM) {
        TextArea text = null;
        resolved = false;
        if (!getPSLM().associateLayoutManagerID(fobj.getRefId())) {
            text = new UnresolvedPageNumber(fobj.getRefId(), font, UnresolvedPageNumber.LAST);
            getPSLM().addUnresolvedArea(fobj.getRefId(), (Resolvable)text);
            String str = "MMM"; // reserve three spaces for page number
            int width = getStringWidth(str);
            text.setIPD(width);
        } else {
            PageViewport page = getPSLM().getLastPVWithID(fobj.getRefId());
            String str = page.getPageNumberString();
            // get page string from parent, build area
            text = new TextArea();
            int width = getStringWidth(str);
            text.addWord(str, 0);
            text.setIPD(width);

            resolved = true;
        }

        updateTextAreaTraits(text);

        return text;
    }
}
