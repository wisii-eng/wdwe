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
 *//* $Id: StringProperty.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;

/**
 * Exists primarily as a container for its Maker inner class, which is
 * extended by many string-based FO property classes.
 */
public class StringProperty extends Property {

    /**
     * Inner class for making instances of StringProperty
     */
    public static class Maker extends PropertyMaker {

        /**
         * @param propId the id of the property for which a Maker should be created
         */
        public Maker(int propId) {
            super(propId);
        }

        /**
         * Make a new StringProperty object
         * @param propertyList not used
         * @param value String value of the new object
         * @param fo not used
         * @return the StringProperty object
         */
        public Property make(PropertyList propertyList, String value,
                             FObj fo) {
            // Work around the fact that most String properties are not
            // specified as actual String literals (with "" or '') since
            // the attribute values themselves are Strings!
            // If the value starts with ' or ", make sure it also ends with
            // this character
            // Otherwise, just take the whole value as the String
            int vlen = value.length() - 1;
            if (vlen > 0) {
                char q1 = value.charAt(0);
                if (q1 == '"' || q1 == '\'') {
                    if (value.charAt(vlen) == q1) {
                        return new StringProperty(value.substring(1, vlen));
                    }
                    log.warn("String-valued property starts with quote"
                                       + " but doesn't end with quote: "
                                       + value);
                    // fall through and use the entire value, including first quote
                }
                String str = checkValueKeywords(value);
                if (str != null) {
                    value = str;
                }
            }
            return new StringProperty(value);
        }

    }    // end String.Maker

    private String str;

    /**
     * @param str String value to place in this object
     */
    public StringProperty(String str) {
        this.str = str;
        // log.debug("Set StringProperty: " + str);
    }

    /**
     * @return the Object equivalent of this property
     */
    public Object getObject() {
        return this.str;
    }

    /**
     * @return the String equivalent of this property
     */
    public String getString() {
        return this.str;
    }

}
