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
 *//* $Id: EnumProperty.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * Superclass for properties that wrap an enumeration value
 */
public class EnumProperty extends Property {

    /**
     * Inner class for creating EnumProperty instances
     */
    public static class Maker extends PropertyMaker {

        /**
         * @param propId the id of the property for which a Maker should be created
         */
        public Maker(int propId) {
            super(propId);
        }

        /**
         * Called by subclass if no match found.
         * @param value string containing the value to be checked
         * @return null (indicates that an appropriate match was not found)
         */
        public Property checkEnumValues(String value) {
            //log.error("Unknown enumerated value for property '"
            //                       + getPropName() + "': " + value);
            return super.checkEnumValues(value);
        }

        public Property convertProperty(Property p,
                                        PropertyList propertyList,
                                        FObj fo) throws PropertyException {
            if (p instanceof EnumProperty) {
                return p;
            } else {
                return super.convertProperty(p, propertyList, fo);
            }
        }
    }

    private int value;
    private String text;

    /**
     * @param explicitValue enumerated value to be set for this property
     * @param text the string value of the enum.
     */
    public EnumProperty(int explicitValue, String text) {
        this.value = explicitValue;
        this.text = text;
    }

    /**
     * @return this.value
     */
    public int getEnum() {
        return this.value;
    }

    /**
     * @return this.value cast as an Object
     */
    public Object getObject() {
        return text;
    }

}

