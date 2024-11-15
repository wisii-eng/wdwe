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
 *//* $Id: PercentLength.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.datatypes.PercentBase;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * a percent specified length quantity in XSL
 */
public class PercentLength extends LengthProperty {

    /**
     * The percentage itself, expressed as a decimal value, e.g. for 95%, set
     * the value to .95
     */
    private double factor;

    /**
     * A PercentBase implementation that contains the base length to which the
     * {@link #factor} should be applied to compute the actual length
     */
    private PercentBase lbase = null;

    private double resolvedValue;
    //最小精度
    public static double PRECISION = 0.00001;

    /**
     * Main constructor. Construct an object based on a factor (the percent,
     * as a factor) and an object which has a method to return the Length which
     * provides the "base" for the actual length that is modeled.
     * @param factor the percentage factor, expressed as a decimal (e.g. use
     * .95 to represent 95%)
     * @param lbase base property to which the factor should be applied
     */
    public PercentLength(double factor, PercentBase lbase) {
        this.factor = factor;
        this.lbase = lbase;
    }

    /**
     * @return the base
     */
    public PercentBase getBaseLength() {
        return this.lbase;
    }

    /**
     *
     * @return the factor
     * TODO: Should this really exists?
     */
    public double value() {
        return factor;
    }

    /**
     * Return false because percent-length are always relative.
     * @see com.wisii.fov.datatypes.Numeric#isAbsolute()
     */
    public boolean isAbsolute() {
        return false;
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getNumericValue()
     */
    public double getNumericValue() {
        return getNumericValue(null);
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getNumericValue(PercentBaseContext)
     */
    public double getNumericValue(PercentBaseContext context) {
        try {
            resolvedValue = factor * lbase.getBaseLength(context);
            return resolvedValue;
        } catch (PropertyException exc) {
            log.error(exc);
            return 0;
        }
    }

    /**
     * Return the length of this PercentLength.
     * @see com.wisii.fov.datatypes.Length#getValue()
     */
    public int getValue() {
        return (int) getNumericValue();
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getValue(PercentBaseContext)
     */
    public int getValue(PercentBaseContext context) {
        return (int) getNumericValue(context);
    }

    /**
     * @return the String equivalent of this
     */
    public String toString() {
        // TODO: What about the base value?
        return (new Double(factor * 100.0).toString()) + "%";
    }

}
