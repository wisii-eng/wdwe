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
 *//* $Id: LabelEndFunction.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.LengthBase;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.flow.ListItem;
import com.wisii.fov.fo.properties.PercentLength;
import com.wisii.fov.fo.properties.Property;

/**
 * Class modelling the label-end Property Value function. See Sec. 5.10.4 of the
 * XSL-FO spec.
 */
public class LabelEndFunction extends FunctionBase {

    /**
     * @return 0 (the number of arguments required for the label-end function)
     */
    public int nbArgs() {
        return 0;
    }

    /**
     *
     * @param args array of arguments for the function (none are needed, but
     * required for the Function interface)
     * @param pInfo PropertyInfo object for the function
     * @return the calculated label-end value for the list
     * @throws PropertyException if called from outside of an fo:list-item
     */
    public Property eval(Property[] args,
                         PropertyInfo pInfo) throws PropertyException {

        Length distance = pInfo.getPropertyList().get(
                Constants.PR_PROVISIONAL_DISTANCE_BETWEEN_STARTS).getLength();
        Length separation = pInfo.getPropertyList().getNearestSpecified(
                Constants.PR_PROVISIONAL_LABEL_SEPARATION).getLength();

        PropertyList pList = pInfo.getPropertyList();
        while (pList != null && !(pList.getFObj() instanceof ListItem)) {
            pList = pList.getParentPropertyList();
        }
        if (pList == null) {
			throw new PropertyException("从fo:list-item的外部调用label-end()");
        }
        Length startIndent = pList.get(Constants.PR_START_INDENT).getLength();

        LengthBase base = new LengthBase(pList.getFObj(), pInfo.getPropertyList(),
                                         LengthBase.CONTAINING_REFAREA_WIDTH);
        PercentLength refWidth = new PercentLength(1.0, base);

        Numeric labelEnd = distance;
        labelEnd = NumericOp.addition(labelEnd, startIndent);
        //TODO add start-intrusion-adjustment
        labelEnd = NumericOp.subtraction(labelEnd, separation);

        labelEnd = NumericOp.subtraction(refWidth, labelEnd);

        return (Property) labelEnd;
    }

}
