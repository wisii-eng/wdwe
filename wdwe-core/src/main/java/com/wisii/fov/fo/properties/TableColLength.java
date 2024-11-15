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
 *//* $Id: TableColLength.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.datatypes.LengthBase;
import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.fo.FObj;

/**
 * A table-column width specification, possibly including some
 * number of proportional "column-units". The absolute size of a
 * column-unit depends on the fixed and proportional sizes of all
 * columns in the table, and on the overall size of the table.
 * It can't be calculated until all columns have been specified and until
 * the actual width of the table is known. Since this can be specified
 * as a percent of its parent containing width, the calculation is done
 * during layout.
 * NOTE: this is only supposed to be allowed if table-layout=fixed.
 */
public class TableColLength extends LengthProperty {
    /**
     * Number of table-column proportional units
     */
    private double tcolUnits;

    /**
     * The column the column-units are defined on.
     */
    private FObj column;

    /**
     * Construct an object with tcolUnits of proportional measure.
     * @param tcolUnits number of table-column proportional units
     * @param column the column the column-units are defined on
     */
    public TableColLength(double tcolUnits, FObj column) {
        this.tcolUnits = tcolUnits;
        this.column = column;
    }

    /**
     * Override the method in Length
     * @return the number of specified proportional table-column units.
     */
    public double getTableUnits() {
        return tcolUnits;
    }

    /**
     * Return false because table-col-units are a relative numeric.
     * @see com.wisii.fov.datatypes.Numeric#isAbsolute()
     */
    public boolean isAbsolute() {
        return false;
    }

    /**
     * Return the value as a numeric value.
     * @see com.wisii.fov.datatypes.Numeric#getNumericValue()
     */
    public double getNumericValue() {
        throw new UnsupportedOperationException(
                "必须以PercentBaseContext作为参数调用 getNumericValue方法");
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getNumericValue(PercentBaseContext)
     */
    public double getNumericValue(PercentBaseContext context) {
        return tcolUnits * context.getBaseLength(LengthBase.CONTAINING_BLOCK_WIDTH, column);
    }

    /**
     * Return the value as a length.
     * @see com.wisii.fov.datatypes.Length#getValue()
     */
    public int getValue() {
        throw new UnsupportedOperationException(
		"必须以PercentBaseContext作为参数调用getValue方法");
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getValue(PercentBaseContext)
     */
    public int getValue(PercentBaseContext context) {
        return (int) (tcolUnits * context.getBaseLength(LengthBase.CONTAINING_BLOCK_WIDTH, column));
    }

    /**
     * Convert this to a String
     * @return the string representation of this
     */
    public String toString() {
        return (Double.toString(tcolUnits) + " table-column-units");
    }

}
