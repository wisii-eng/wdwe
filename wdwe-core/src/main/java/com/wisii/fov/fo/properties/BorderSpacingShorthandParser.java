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
 *//* $Id: BorderSpacingShorthandParser.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import java.util.List;

import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * Shorthand parser for the "border-spacing" shorthand property.
 */
public class BorderSpacingShorthandParser extends GenericShorthandParser {

    protected Property convertValueForProperty(int propId, Property property,
            PropertyMaker maker, PropertyList propertyList)
            throws PropertyException {
        List lst = property.getList();
        if (lst != null) {
            if (lst.size() == 1) {
                Property len = (Property)lst.get(0);
                return new LengthPairProperty(len);
            } else if (lst.size() == 2) {
                Property ipd = (Property)lst.get(0);
                Property bpd = (Property)lst.get(1);
                return new LengthPairProperty(ipd, bpd);
            }
        }
		throw new PropertyException("输入数组的长度不为1或者2");
    }
}
