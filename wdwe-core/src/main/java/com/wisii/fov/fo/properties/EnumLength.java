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
 *//* $Id: EnumLength.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.datatypes.PercentBaseContext;

/**
 * A length quantity in XSL which is specified as an enum, such as "auto"
 */
public class EnumLength extends LengthProperty {
    private Property enumProperty;

    public EnumLength(Property enumProperty) {
        this.enumProperty = enumProperty;
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getEnum()
     */
    public int getEnum() {
        return enumProperty.getEnum();
    }

    public boolean isAbsolute() {
        return false;
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getValue()
     */
    public int getValue() {
        log.error("getValue() called on " + enumProperty + " length");
        return 0;
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getValue(PercentBaseContext)
     */
    public int getValue(PercentBaseContext context) {
        log.error("getValue() called on " + enumProperty + " length");
        return 0;
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getNumericValue()
     */
    public double getNumericValue() {
        log.error("getNumericValue() called on " + enumProperty + " number");
        return 0;
    }

    /**
     * @see com.wisii.fov.datatypes.Numeric#getNumericValue()
     */
    public double getNumericValue(PercentBaseContext context) {
        log.error("getNumericValue() called on " + enumProperty + " number");
        return 0;
    }

    /**
     * @see com.wisii.fov.fo.properties.Property#getString()
     */
    public String getString() {
        return enumProperty.toString();
    }

    /**
     * @see com.wisii.fov.fo.properties.Property#getString()
     */
    public Object getObject() {
        return enumProperty.getObject();
    }


}
