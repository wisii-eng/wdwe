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
 *//* $Id: MinOptMaxUtil.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr;

import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.properties.LengthRangeProperty;
import com.wisii.fov.traits.MinOptMax;

/**
 * Utilities for MinOptMax and LengthRangeProperty.
 */
public class MinOptMaxUtil {

    /**
     * Restricts a MinOptMax using the values from a LengthRangeProperty.
     * @param mom MinOptMax to restrict
     * @param lr restricting source
     * @param context Percentage evaluation context
     */
    public static void restrict(MinOptMax mom, LengthRangeProperty lr,
                                PercentBaseContext context) {
        if (lr.getEnum() != Constants.EN_AUTO) {
            if (lr.getMinimum(context).getEnum() != Constants.EN_AUTO) {
                int min = lr.getMinimum(context).getLength().getValue(context);
                if (min > mom.min) {
                    mom.min = min;
                    fixAfterMinChanged(mom);
                }
            }
            if (lr.getMaximum(context).getEnum() != Constants.EN_AUTO) {
                int max = lr.getMaximum(context).getLength().getValue(context);
                if (max < mom.max) {
                    mom.max = max;
                    if (mom.max < mom.opt) {
                        mom.opt = mom.max;
                        mom.min = mom.opt;
                    }
                }
            }
            if (lr.getOptimum(context).getEnum() != Constants.EN_AUTO) {
                int opt = lr.getOptimum(context).getLength().getValue(context);
                if (opt > mom.min) {
                    mom.opt = opt;
                    if (mom.opt > mom.max) {
                        mom.max = mom.opt;
                    }
                }
            }
        }
    }

    /**
     * Extend the minimum length to the given length.
     * @param mom the min/opt/max trait
     * @param len the new minimum length
     * @param optToLen if set adjusts the optimum length to be the smaller of the
     * minimum length and the given length
     */
    public static void extendMinimum(MinOptMax mom, int len, boolean optToLen) {
        if (mom.min < len) {
            mom.min = len;
            mom.opt = Math.max(mom.min, mom.opt);
            if (optToLen) {
                mom.opt = Math.min(mom.min, len);
            }
            mom.max = Math.max(mom.opt, mom.max);
        }
    }

    /**
     * After a calculation on a MinOptMax, this can be called to set opt to
     * a new effective value.
     * @param mom MinOptMax to adjust
     */
    public static void fixAfterMinChanged(MinOptMax mom) {
        if (mom.min > mom.opt) {
            mom.opt = mom.min;
            if (mom.opt > mom.max) {
                mom.max = mom.opt;
            }
        }
    }

    /**
     * Converts a LengthRangeProperty to a MinOptMax.
     * @param prop LengthRangeProperty
     * @param context Percentage evaluation context
     * @return the requested MinOptMax instance
     */
    public static MinOptMax toMinOptMax(LengthRangeProperty prop, PercentBaseContext context) {
        MinOptMax mom = new MinOptMax(
                (prop.getMinimum(context).isAuto()
                        ? 0 : prop.getMinimum(context).getLength().getValue(context)),
                (prop.getOptimum(context).isAuto()
                        ? 0 : prop.getOptimum(context).getLength().getValue(context)),
                (prop.getMinimum(context).isAuto()
                        ? Integer.MAX_VALUE
                        : prop.getMaximum(context).getLength().getValue(context)));
        return mom;
    }

}
