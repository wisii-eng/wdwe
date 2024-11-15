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
 *//* $Id: BackgroundPositionShorthandParser.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import java.util.List;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * A shorthand parser for the background-position shorthand
 */

public class BackgroundPositionShorthandParser extends GenericShorthandParser {

    /**
     * @see com.wisii.fov.fo.properties.ShorthandParser#getValueForProperty()
     */
    public Property getValueForProperty(int propId,
                                               Property property,
                                               PropertyMaker maker,
                                               PropertyList propertyList)
                    throws PropertyException {

        int index = -1;
        List propList = property.getList();
        if (propId == Constants.PR_BACKGROUND_POSITION_HORIZONTAL) {
            index = 0;
        } else if (propId == Constants.PR_BACKGROUND_POSITION_VERTICAL) {
            index = 1;
            if (propList.size() == 1) {
                /* only background-position-horizontal specified
                 * through the shorthand:
                 * background-position-vertical=50% (see: XSL-FO 1.0 -- 7.29.2)
                 */
                return maker.make(propertyList, "50%", propertyList.getParentFObj());
            }
        }
        if (index >= 0) {
            return (Property) propList.get(index);
        } // else: invalid index? shouldn't happen...
        return null;
    }
}
