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
 *//* $Id: WrapperLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.fo.flow.Wrapper;
import com.wisii.fov.layoutmgr.LayoutContext;

/**
 * This is the layout manager for the fo:wrapper formatting object.
 */
public class WrapperLayoutManager extends LeafNodeLayoutManager {

    private Wrapper fobj;

    /**
     * Creates a new LM for fo:wrapper.
     * @param node the fo:wrapper
     */
    public WrapperLayoutManager(Wrapper node) {
        super(node);
        fobj = node;
    }

    /** @see com.wisii.fov.layoutmgr.inline.LeafNodeLayoutManager */
    public InlineArea get(LayoutContext context) {
        //Create a zero-width, zero-height dummy area so this node can
        //participate in the ID handling. Otherwise, addId() wouldn't
        //be called.
        InlineArea area = new InlineArea();
        return area;
    }

    /** @see com.wisii.fov.layoutmgr.inline.LeafNodeLayoutManager#addId() */
    protected void addId() {
        getPSLM().addIDToPage(fobj.getId());
    }

}
