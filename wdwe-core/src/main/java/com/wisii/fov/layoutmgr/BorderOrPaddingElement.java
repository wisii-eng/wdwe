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
 *//* $Id: BorderOrPaddingElement.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr;

import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.fo.properties.CondLengthProperty;
import com.wisii.fov.traits.MinOptMax;

/**
 * This class represents an unresolved border or padding element.
 */
public abstract class BorderOrPaddingElement extends UnresolvedListElementWithLength {

    /**
     * Main constructor
     * @param position the Position instance needed by the addAreas stage of the LMs.
     * @param side the side to which this space element applies.
     * @param condLength the length-conditional property for a border or padding specification
     * @param isFirst true if this is a padding- or border-before of the first area generated.
     * @param isLast true if this is a padding- or border-after of the last area generated.
     * @param context the property evaluation context
     */
    public BorderOrPaddingElement(Position position, CondLengthProperty condLength,
            RelSide side,
            boolean isFirst, boolean isLast, PercentBaseContext context) {
        super(position,
                new MinOptMax(condLength.getLength().getValue(context)), side,
                        condLength.isDiscard(), isFirst, isLast);
    }

    /** @see com.wisii.fov.layoutmgr.UnresolvedListElementWithLength */
    public abstract void notifyLayoutManager(MinOptMax effectiveLength);

}
