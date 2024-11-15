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
 *//* $Id: BasicLinkLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.datatypes.URISpecification;
import com.wisii.fov.fo.flow.BasicLink;
import com.wisii.fov.layoutmgr.LayoutManager;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.LinkResolver;
import com.wisii.fov.area.PageViewport;

/**
 * LayoutManager for the fo:basic-link formatting object
 */
public class BasicLinkLayoutManager extends InlineLayoutManager {
    private BasicLink fobj;

    /**
     * Create an fo:basic-link layout manager.
     *
     * @param node the formatting object that creates the area
     */
    public BasicLinkLayoutManager(BasicLink node) {
        super(node);
        fobj = node;
    }

    /** @see com.wisii.fov.layoutmgr.inline.InlineLayoutManager#createArea(boolean) */
    protected InlineArea createArea(boolean bInlineParent) {
        InlineArea area = super.createArea(bInlineParent);
        setupBasicLinkArea(parentLM, area);
        return area;
    }

    private void setupBasicLinkArea(LayoutManager parentLM,
                                      InlineArea area) {
         if (fobj.getExternalDestination() != null) {
             area.addTrait(Trait.EXTERNAL_LINK,
                     URISpecification.getURL(fobj.getExternalDestination()));
         } else {
             String idref = fobj.getInternalDestination();
             PageViewport page = getPSLM().getFirstPVWithID(idref);
             if (page != null) {
                 area.addTrait(Trait.INTERNAL_LINK, page.getKey());
             } else {
                 LinkResolver res = new LinkResolver(idref, area);
                 getPSLM().addUnresolvedArea(idref, res);
             }
         }
     }
}

