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
 *//* $Id: LengthBase.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.datatypes;

import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;
import com.wisii.fov.fo.flow.Character;
import com.wisii.fov.fo.flow.ExternalGraphic;
import com.wisii.fov.fo.flow.InstreamForeignObject;

/**
 * Models a length which can be used as a factor in a percentage length
 * calculation
 */
public class LengthBase implements PercentBase {
    // Standard kinds of percent-based length
    /** constant for a custom percent-based length */
    public static final int CUSTOM_BASE = 0;
    /** constant for a font-size percent-based length */
    public static final int FONTSIZE = 1;
    /** constant for an inh font-size percent-based length */
    public static final int INH_FONTSIZE = 2;
    /** constant for a containing box percent-based length */
    public static final int PARENT_AREA_WIDTH = 3;
    /** constant for a containing refarea percent-based length */
    public static final int CONTAINING_REFAREA_WIDTH = 4;
    /** constant for a containing block percent-based length */
    public static final int CONTAINING_BLOCK_WIDTH = 5;
    /** constant for a containing block percent-based length */
    public static final int CONTAINING_BLOCK_HEIGHT = 6;
    /** constant for a image intrinsic percent-based length */
    public static final int IMAGE_INTRINSIC_WIDTH = 7;
    /** constant for a image intrinsic percent-based length */
    public static final int IMAGE_INTRINSIC_HEIGHT = 8;
    /** constant for a image background position horizontal percent-based length */
    public static final int IMAGE_BACKGROUND_POSITION_HORIZONTAL = 9;
    /** constant for a image background position vertical percent-based length */
    public static final int IMAGE_BACKGROUND_POSITION_VERTICAL = 10;
    /** constant for a table-unit-based length */
    public static final int TABLE_UNITS = 11;
    /** constant for a alignment adjust percent-based length */
    public static final int ALIGNMENT_ADJUST = 12;

    /**
     * The FO for which this property is to be calculated.
     */
    protected /* final */ FObj fobj;

    /**
     * One of the defined types of LengthBase
     */
    private /* final */ int baseType;

    /** For percentages based on other length properties */
    private Length baseLength;

    /**
     * Constructor
     * @param parentFO parent FO for this
     * @param plist property list for this
     * @param baseType a constant defining the type of teh percent base
     * @throws PropertyException In case an problem occurs while evaluating values
     */
    public LengthBase(FObj parentFO, PropertyList plist, int baseType) throws PropertyException {
        this.fobj = plist.getFObj();
        this.baseType = baseType;
        switch (baseType) {
        case FONTSIZE:
            this.baseLength = plist.get(Constants.PR_FONT_SIZE).getLength();
            break;
        case INH_FONTSIZE:
            this.baseLength = plist.getInherited(Constants.PR_FONT_SIZE).getLength();
            break;
        default:
            // TODO: pacify CheckStyle
            // throw new RuntimeException();
            break;
        }
    }

    /**
     * @return the dimension of this object (always 1)
     */
    public int getDimension() {
        return 1;
    }

    /**
     * @return the base value of this object (always 1.0)
     */
    public double getBaseValue() {
        return 1.0;
    }

    /**
     * @see com.wisii.fov.datatypes.PercentBase#getBaseLength(PercentBaseContext)
     */
    public int getBaseLength(PercentBaseContext context) throws PropertyException {
        int baseLen = 0;
        if (context != null) {
            if (baseType == FONTSIZE || baseType == INH_FONTSIZE) {
                return baseLength.getValue(context);
            }
            baseLen =  context.getBaseLength(baseType,  fobj);
        } else {
            fobj.getLogger().error("getBaseLength called without context");
        }
        return baseLen;
    }

}

