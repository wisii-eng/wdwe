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
 *//* $Id: NumericProperty.java,v 1.2 2008/01/17 01:48:28 lzy Exp $ */

package com.wisii.fov.fo.expr;

import java.awt.Color;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.properties.Property;

/**
 * A numeric property which hold the final absolute result of an expression
 * calculations.
 */
public class NumericProperty extends Property implements Numeric, Length {
    private double value;
    private int dim;

    /**
     * Construct a Numeric object by specifying one or more components,
     * including absolute length, percent length, table units.
     * @param value The value of the numeric.
     * @param dim The dimension of the value. 0 for a Number, 1 for a Length
     * (any type), >1, <0 if Lengths have been multiplied or divided.
     */
    public NumericProperty(double value, int dim) {
        this.value = value;
        this.dim = dim;
    }

    /**
     * Return the dimension.
     * @see Numeric#getDimension()
     */
    public int getDimension() {
        return dim;
    }

    /**
     * Return the value.
     * @see Numeric#getNumericValue()
     */
    public double getNumericValue() {
        return value;
    }

    /**
     * @see Numeric#getNumericValue(PercentBaseContext)
     */
    public double getNumericValue(PercentBaseContext context) {
        return value;
    }

    /**
     * Return true of the numeric is absolute.
     * @see Numeric#isAbsolute()
     */
    public boolean isAbsolute() {
        return true;
    }

    /** @see com.wisii.fov.fo.properties.Property#getNumeric() */
    public Numeric getNumeric() {
        return this;
    }

    /** @see com.wisii.fov.fo.properties.Property#getNumber() */
    public Number getNumber() {
        return new Double(value);
    }

    /** @see com.wisii.fov.datatypes.Numeric#getValue() */
    public int getValue() {
        return (int) value;
    }

    /** @see com.wisii.fov.datatypes.Numeric#getValue(PercentBaseContext) */
    public int getValue(PercentBaseContext context) {
        return (int) value;
    }

    /** @see com.wisii.fov.fo.properties.Property#getLength() */
    public Length getLength() {
        if (dim == 1) {
            return this;
        }
        log.error("Can't create length with dimension " + dim);
        return null;
    }

    /** @see com.wisii.fov.fo.properties.Property#getColor() */
    public Color getColor() {
        // TODO:  try converting to numeric number and then to color
        return null;
    }

    /** @see com.wisii.fov.fo.properties.Property#getObject() */
    public Object getObject() {
        return this;
    }

    /** @see java.lang.Object#toString() */
    public String toString() {
        if (dim == 1) {
            return (int) value + "mpt";
        } else {
            return value + "^" + dim;
        }
    }
}
