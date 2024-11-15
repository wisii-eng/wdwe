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
 *//* $Id: VerticalAlignShorthandParser.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.PropertyList;

/**
 * A shorthand parser for the vertical-align shorthand. It is used to set
 * values for alignment-baseline, alignment-adjust, baseline-shift
 * and dominant-baseline.
 */
public class VerticalAlignShorthandParser implements ShorthandParser, Constants {

    /**
     * @see ShorthandParser#getValueForProperty(int, Property, PropertyMaker, PropertyList)
     */
    public Property getValueForProperty(int propId,
                                        Property property,
                                        PropertyMaker maker,
                                        PropertyList propertyList) {
        int propVal = property.getEnum();
        switch (propVal) {
            case EN_BASELINE:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_BASELINE, "BASELINE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_BASELINE, "BASELINE"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            case EN_TOP:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_BEFORE_EDGE, "BEFORE_EDGE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_BASELINE, "BASELINE"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            case EN_TEXT_TOP:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_TEXT_BEFORE_EDGE, "TEXT_BEFORE_EDGE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_BASELINE, "BASELINE"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            case EN_MIDDLE:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_MIDDLE, "MIDDLE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_BASELINE, "BASELINE"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            case EN_BOTTOM:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_AFTER_EDGE, "AFTER_EDGE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_BASELINE, "BASELINE"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            case EN_TEXT_BOTTOM:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_TEXT_AFTER_EDGE, "TEXT_AFTER_EDGE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_BASELINE, "BASELINE"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            case EN_SUB:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_BASELINE, "BASELINE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_SUB, "SUB"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            case EN_SUPER:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_BASELINE, "BASELINE");
                    case PR_ALIGNMENT_ADJUST:
                        return new EnumLength(new EnumProperty(EN_AUTO, "AUTO"));
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_SUPER, "SUPER"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
            default:
                switch (propId) {
                    case PR_ALIGNMENT_BASELINE:
                        return new EnumProperty(EN_BASELINE, "BASELINE");
                    case PR_ALIGNMENT_ADJUST:
                        return property;
                    case PR_BASELINE_SHIFT:
                        return new EnumLength(new EnumProperty(EN_BASELINE, "BASELINE"));
                    case PR_DOMINANT_BASELINE:
                        return new EnumProperty(EN_AUTO, "AUTO");
                }
        }
        return null;
    }

}
