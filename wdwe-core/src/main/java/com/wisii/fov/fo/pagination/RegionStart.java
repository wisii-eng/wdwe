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
 *//* $Id: RegionStart.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// Java
import java.awt.Rectangle;

// FOV
import com.wisii.fov.fo.FONode;
import com.wisii.fov.datatypes.FODimension;
import com.wisii.fov.datatypes.LengthBase;
import com.wisii.fov.datatypes.SimplePercentBaseContext;

/**
 * The fo:region-start element.
 */
public class RegionStart extends RegionSE {
    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public RegionStart(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.pagination.Region#getViewportRectangle(FODimension, SimplePageMaster)
     */
    public Rectangle getViewportRectangle (FODimension reldims, SimplePageMaster spm) {
        /* Special rules apply to resolving extent as values are resolved relative
         * to the page size and reference orientation.
         */
        SimplePercentBaseContext pageWidthContext;
        SimplePercentBaseContext pageHeightContext;
        if (spm.getReferenceOrientation() % 180 == 0) {
            pageWidthContext = new SimplePercentBaseContext(null,
                                                            LengthBase.CUSTOM_BASE,
                                                            spm.getPageWidth().getValue());
            pageHeightContext = new SimplePercentBaseContext(null,
                                                             LengthBase.CUSTOM_BASE,
                                                             spm.getPageHeight().getValue());
        } else {
            // invert width and height since top left are rotated by 90 (cl or ccl)
            pageWidthContext = new SimplePercentBaseContext(null,
                                                            LengthBase.CUSTOM_BASE,
                                                            spm.getPageHeight().getValue());
            pageHeightContext = new SimplePercentBaseContext(null,
                                                             LengthBase.CUSTOM_BASE,
                                                             spm.getPageWidth().getValue());
        }
        SimplePercentBaseContext neighbourContext;
        Rectangle vpRect;
        if (spm.getWritingMode() == EN_LR_TB || spm.getWritingMode() == EN_RL_TB) {
            neighbourContext = pageHeightContext;
            vpRect = new Rectangle(0, 0, getExtent().getValue(pageWidthContext), reldims.bpd);
        } else {
            neighbourContext = pageWidthContext;
            vpRect = new Rectangle(0, 0, reldims.bpd, getExtent().getValue(pageHeightContext));
        }
        adjustIPD(vpRect, spm.getWritingMode(), neighbourContext);
        return vpRect;
    }

    /**
     * @see com.wisii.fov.fo.pagination.Region#getDefaultRegionName()
     */
    protected String getDefaultRegionName() {
        return "xsl-region-start";
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "region-start";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_REGION_START;
    }
}

