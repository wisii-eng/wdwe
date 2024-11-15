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
 *//* $Id: SideRegion.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;

/**
 * Common base class for side regions (before, after, start, end).
 */
public abstract class SideRegion extends Region {

    private Length extent;

    /** @see com.wisii.fov.fo.FONode#FONode(FONode) */
    protected SideRegion(FONode parent) {
        super(parent);
    }

    /** @see com.wisii.fov.fo.FObj#bind(PropertyList) */
    public void bind(PropertyList pList) throws FOVException {
        super.bind(pList);
        extent = pList.get(PR_EXTENT).getLength();
    }

    /** @return the "extent" property. */
    public Length getExtent() {
        return extent;
    }

}
