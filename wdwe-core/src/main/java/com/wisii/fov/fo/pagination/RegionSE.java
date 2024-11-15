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
 *//* $Id: RegionSE.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// Java
import java.awt.Rectangle;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;

/**
 * Abstract base class for fo:region-start and fo:region-end.
 */
public abstract class RegionSE extends SideRegion {
    // The value of properties relevant for fo:region-[start|end].
    // End of property values

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    protected RegionSE(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        super.bind(pList);
    }

    /**
     * Adjust the viewport reference rectangle for a region as a function
     * of precedence.
     * If  before and after have precedence = true, the start and end
     * regions only go to the limits of their extents, otherwise
     * they extend in the BPD to the page reference rectangle
     * diminish by extend of start and end if present.
     * @param vpRefRect viewport reference rectangle
     * @param wm writing mode
     * @param siblingContext the context to use to resolve extent on siblings
     */
    protected void adjustIPD(Rectangle vpRefRect, int wm, PercentBaseContext siblingContext) {
        int offset = 0;
        RegionBefore before = (RegionBefore) getSiblingRegion(FO_REGION_BEFORE);
        if (before != null && before.getPrecedence() == EN_TRUE) {
            offset = before.getExtent().getValue(siblingContext);
            vpRefRect.translate(0, offset);
        }
        RegionAfter after = (RegionAfter) getSiblingRegion(FO_REGION_AFTER);
        if (after != null && after.getPrecedence() == EN_TRUE) {
            offset += after.getExtent().getValue(siblingContext);
        }
        if (offset > 0) {
            if (wm == EN_LR_TB || wm == EN_RL_TB) {
                vpRefRect.height -= offset;
            } else {
                vpRefRect.width -= offset;
            }
        }
    }
}

