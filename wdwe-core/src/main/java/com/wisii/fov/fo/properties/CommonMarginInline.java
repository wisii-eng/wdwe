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
 *//* $Id: CommonMarginInline.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * Store all common margin properties for inlines.
 * See Sec. 7.11 of the XSL-FO Standard.
 * Public "structure" allows direct member access.
 */
public class CommonMarginInline {

    /**
     * The "margin-top" property.
     */
    public Length marginTop;

    /**
     * The "margin-bottom" property.
     */
    public Length marginBottom;

    /**
     * The "margin-left" property.
     */
    public Length marginLeft;

    /**
     * The "margin-right" property.
     */
    public Length marginRight;

    /**
     * The "space-start" property.
     */
    public SpaceProperty spaceStart;

    /**
     * The "space-end" property.
     */
    public SpaceProperty spaceEnd;

    /**
     * Create a CommonMarginInline object.
     * @param pList The PropertyList with propery values.
     */
    public CommonMarginInline(PropertyList pList) throws PropertyException {
        marginTop = pList.get(Constants.PR_MARGIN_TOP).getLength();
        marginBottom = pList.get(Constants.PR_MARGIN_BOTTOM).getLength();
        marginLeft = pList.get(Constants.PR_MARGIN_LEFT).getLength();
        marginRight = pList.get(Constants.PR_MARGIN_RIGHT).getLength();

        spaceStart = pList.get(Constants.PR_SPACE_START).getSpace();
        spaceEnd = pList.get(Constants.PR_SPACE_END).getSpace();
    }
}
