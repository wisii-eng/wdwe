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
 *//* $Id: PropertyException.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import com.wisii.fov.apps.FOVException;

/**
 * Class for managing exceptions that are raised in Property processing.
 */
public class PropertyException extends FOVException {
    private String propertyName;

    /**
     * Constructor
     * @param detail string containing the detail message
     */
    public PropertyException(String detail) {
        super(detail);
    }

    /**
     * Sets the property context information.
     * @param propInfo the property info instance
     */
    public void setPropertyInfo(PropertyInfo propInfo) {
        setLocator(propInfo.getFO().getLocator());
        propertyName = propInfo.getPropertyMaker().getName();
    }

    /**
     * Sets the name of the property.
     * @param propertyName the property name
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /** @see java.lang.Throwable#getMessage()*/
    public String getMessage() {
        if (propertyName != null) {
            return super.getMessage() + "; property:'" + propertyName + "'";
        } else {
            return super.getMessage();
        }
    }
}
