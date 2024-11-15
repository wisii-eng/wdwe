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
 *//* $Id: LengthProperty.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * Superclass for properties wrapping a Length value.
 */
public abstract class LengthProperty extends Property
    implements Length, Numeric {

    /**
     * Inner class for making instances of LengthProperty
     */
    public static class Maker extends PropertyMaker {

        /**
         * @param propId the id of the property for which a Maker should be created
         */
        public Maker(int propId) {
            super(propId);
        }

        /**
         * @see com.wisii.fov.fo.properties.PropertyMaker#convertProperty(
         *          com.wisii.fov.fo.properties.Property, com.wisii.fov.fo.PropertyList,
         *          com.wisii.fov.fo.FObj)
         */
        public Property convertProperty(Property p,
                                        PropertyList propertyList,
                                        FObj fo) throws PropertyException {
            if (p instanceof EnumProperty) {
                return new EnumLength(p);
            }
            if (p instanceof LengthProperty) {
                return p;
            }
            if (p instanceof NumberProperty) {
                //Assume pixels (like in HTML) when there's no unit
                return new FixedLength(p.getNumeric().getNumericValue(), "px");
            }
            Length val = p.getLength();
            if (val != null) {
                return (Property) val;
            }
            /* always null ?? */
            return convertPropertyDatatype(p, propertyList, fo);
        }

    }

    /**
     * Return the number of table units which are included in this
     * length specification.
     * This will always be 0 unless the property specification used
     * the proportional-column-width() function (only only table
     * column FOs).
     * <p>If this value is not 0, the actual value of the Length cannot
     * be known without looking at all of the columns in the table to
     * determine the value of a "table-unit".
     * @return The number of table units which are included in this
     * length specification.
     */
    public double getTableUnits() {
        return 0.0;
    }

    /**
     * @return the numeric dimension. Length always a dimension of 1.
     */
    public int getDimension() {
        return 1;
    }

    /**
     * @return this.length cast as a Numeric
     */
    public Numeric getNumeric() {
        return this;
    }

    /**
     * @return this.length
     */
    public Length getLength() {
        return this;
    }

    /**
     * @return this.length cast as an Object
     */
    public Object getObject() {
        return this;
    }

}

