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
 *//* $Id: NCnameProperty.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import java.awt.Color;

import com.wisii.fov.fo.properties.Property;
import com.wisii.fov.util.ColorUtil;

/**
 * Class for handling NC Name objects
 */
public class NCnameProperty extends Property {

    private final String ncName;

    /**
     * Constructor
     * @param ncName string representing the ncName
     */
    public NCnameProperty(String ncName) {
        this.ncName = ncName;
    }

    /**
     * If a system color, return the corresponding value.
     *
     * @return Color object corresponding to the NCName
     */
    public Color getColor()  {
        try {
            return ColorUtil.parseColorString(ncName);
        } catch (PropertyException e) {
            //TODO: This should probably print an error message?
            return null;
        }
    }

    /**
     * @return the name as a String (should be specified with quotes!)
     */
    public String getString() {
        return this.ncName;
    }

    /**
     * @return the name as an Object.
     */
    public Object getObject() {
        return this.ncName;
    }

    /**
     * @return ncName for this
     */
    public String getNCname() {
        return this.ncName;
    }

}
