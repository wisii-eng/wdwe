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
 *//* $Id: WhiteSpaceShorthandParser.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * Shorthand parser for the white-space property;
 * Sets the values for white-space-treament, linefeed-treatment,
 * white-space-collapse and wrap-option
 *
 */
public class WhiteSpaceShorthandParser implements ShorthandParser {

    /**
     * @see com.wisii.fov.fo.properties.ShorthandParser#getValueForProperty()
     */
    public Property getValueForProperty(int propId, Property property,
            PropertyMaker maker, PropertyList propertyList)
            throws PropertyException {
        switch (property.getEnum()) {
        case Constants.EN_PRE:
            switch (propId) {
            case Constants.PR_LINEFEED_TREATMENT:
            case Constants.PR_WHITE_SPACE_TREATMENT:
                return new EnumProperty(Constants.EN_PRESERVE, "PRESERVE");
            case Constants.PR_WHITE_SPACE_COLLAPSE:
                return new EnumProperty(Constants.EN_FALSE, "FALSE");
            case Constants.PR_WRAP_OPTION:
                return new EnumProperty(Constants.EN_NO_WRAP, "NO_WRAP");
            default:
                //nop
            }
        case Constants.EN_NO_WRAP:
            if (propId == Constants.PR_WRAP_OPTION) {
                return new EnumProperty(Constants.EN_NO_WRAP, "NO_WRAP");
            }
        case Constants.EN_NORMAL:
        default:
            //nop
        }
        return null;
    }

}
