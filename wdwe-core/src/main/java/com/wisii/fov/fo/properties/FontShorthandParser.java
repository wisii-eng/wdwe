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
 *//* $Id: FontShorthandParser.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.FOPropertyMapping;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * A shorthand parser for the font shorthand property
 */
public class FontShorthandParser extends GenericShorthandParser {

    /**
     * @see com.wisii.fov.fo.properties.ShorthandParser#getValueForProperty()
     */
    public Property getValueForProperty(int propId,
                                               Property property,
                                               PropertyMaker maker,
                                               PropertyList propertyList)
                    throws PropertyException {

        int index = -1;
        Property newProp;
        switch (propId) {
        case Constants.PR_FONT_SIZE:
            index = 0;
            break;
        case Constants.PR_FONT_FAMILY:
            index = 1;
            break;
        case Constants.PR_LINE_HEIGHT:
            index = 2;
            break;
        case Constants.PR_FONT_STYLE:
            index = 3;
            break;
        case Constants.PR_FONT_VARIANT:
            index = 4;
            break;
        case Constants.PR_FONT_WEIGHT:
            index = 5;
            break;
        default:
            //nop
        }
        newProp = (Property) property.getList().get(index);
        return newProp;
    }
}
