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
 *//* $Id: BorderWidthPropertyMaker.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * This subclass of LengthProperty.Maker handles the special treatment of
 * border width described in 7.7.20.
 */
public class BorderWidthPropertyMaker extends LengthProperty.Maker {
    int borderStyleId = 0;

    /**
     * Create a length property which check the value of the border-*-style
     * property and return a length of 0 when the style is "none".
     * @param propId the border-*-width of the property.
     */
    public BorderWidthPropertyMaker(int propId) {
        super(propId);
    }

    /**
     * Set the propId of the style property for the same side.
     * @param borderStyleId
     */
    public void setBorderStyleId(int borderStyleId) {
        this.borderStyleId = borderStyleId;
    }

    /**
     * Check the value of the style property and return a length of 0 when
     * the style is NONE.
     * @see com.wisii.fov.fo.properties.PropertyMaker#get(int, PropertyList, boolean, boolean)
     */

    public Property get(int subpropId, PropertyList propertyList,
                        boolean bTryInherit, boolean bTryDefault)
        throws PropertyException
    {
        Property p = super.get(subpropId, propertyList,
                               bTryInherit, bTryDefault);

        // Calculate the values as described in 7.7.20.
        Property style = propertyList.get(borderStyleId);
        if (style.getEnum() == Constants.EN_NONE) {
            return new FixedLength(0);
        }
        return p;
    }
}
