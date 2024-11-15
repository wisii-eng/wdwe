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
 *//* $Id: LineHeightPropertyMaker.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * A maker which calculates the line-height property.
 * This property maker is special because line-height inherits the specified
 * value, instead of the computed value.
 * So when a line-height is create based on an attribute, the specified value
 * is stored in the property and in compute() the stored specified value of
 * the nearest specified is used to recalculate the line-height.
 */

public class LineHeightPropertyMaker extends SpaceProperty.Maker {
    /**
     * Create a maker for line-height.
     * @param propId the is for linehight.
     */
    public LineHeightPropertyMaker(int propId) {
        super(propId);
    }

    /**
     * @see PropertyMaker#make(PropertyList, String, FObj)
     * @throws PropertyException
     */
    public Property make(PropertyList propertyList, String value, FObj fo)
            throws PropertyException {
        /* if value was specified as a number/length/percentage then
         * conditionality and precedence components are overridden
         */
        Property p = super.make(propertyList, value, fo);
        p.getSpace().setConditionality(
                new EnumProperty(Constants.EN_RETAIN, "RETAIN"), true);
        p.getSpace().setPrecedence(
                new EnumProperty(Constants.EN_FORCE, "FORCE"), true);
        return p;
    }

    /**
     * Recalculate the line-height value based on the nearest specified
     * value.
     * @see PropertyMaker#compute(PropertyList)
     */
    protected Property compute(PropertyList propertyList) throws PropertyException {
        // recalculate based on last specified value
        // Climb up propertylist and find last spec'd value
        Property specProp = propertyList.getNearestSpecified(propId);
        if (specProp != null) {
            String specVal = specProp.getSpecifiedValue();
            if (specVal != null) {
                return make(propertyList, specVal,
                            propertyList.getParentFObj());
            }
        }
        return null;
    }

    /**
     * @see SpaceProperty.Maker#convertProperty(Property, PropertyList, FObj)
     */
    public Property convertProperty(Property p,
            PropertyList propertyList,
            FObj fo) throws PropertyException {
        Numeric numval = p.getNumeric();
        if (numval != null && numval.getDimension() == 0) {
            p = new PercentLength(numval.getNumericValue(), getPercentBase(fo, propertyList));
            Property spaceProp = super.convertProperty(p, propertyList, fo);
            spaceProp.setSpecifiedValue(String.valueOf(numval.getNumericValue()));
            return spaceProp;
        }
        return super.convertProperty(p, propertyList, fo);
    }
}
