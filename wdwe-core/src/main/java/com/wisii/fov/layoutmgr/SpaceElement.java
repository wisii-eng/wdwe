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
 *//* $Id: SpaceElement.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr;

import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.properties.SpaceProperty;
import com.wisii.fov.traits.MinOptMax;

/**
 * This class represents an unresolved space element.
 */
public class SpaceElement extends UnresolvedListElementWithLength {

    private int precedence;

    /**
     * Main constructor
     * @param position the Position instance needed by the addAreas stage of the LMs.
     * @param space the space property
     * @param side the side to which this space element applies.
     * @param isFirst true if this is a space-before of the first area generated.
     * @param isLast true if this is a space-after of the last area generated.
     * @param context the property evaluation context
     */
    public SpaceElement(Position position, SpaceProperty space, RelSide side,
            boolean isFirst, boolean isLast,
            PercentBaseContext context) {
        super(position,
                MinOptMaxUtil.toMinOptMax(
                        space.getSpace().getLengthRange(),
                context), side, space.isDiscard(), isFirst, isLast);
        int en = space.getSpace().getPrecedence().getEnum();
        if (en == Constants.EN_FORCE) {
            this.precedence = Integer.MAX_VALUE;
        } else {
            this.precedence = space.getSpace().getPrecedence().getNumber().intValue();
        }
    }

    /** @return true if the space is forcing. */
    public boolean isForcing() {
        return this.precedence == Integer.MAX_VALUE;
    }

    /** @return the precedence of the space */
    public int getPrecedence() {
        return this.precedence;
    }

    /** @see com.wisii.fov.layoutmgr.UnresolvedListElementWithLength */
    public void notifyLayoutManager(MinOptMax effectiveLength) {
        LayoutManager lm = getOriginatingLayoutManager();
        if (lm instanceof ConditionalElementListener) {
            ((ConditionalElementListener)lm).notifySpace(
                    getSide(), effectiveLength);
        } else {
            log.warn("Cannot notify LM. It does not implement ConditionalElementListener:"
                    + lm.getClass().getName());
        }
    }

    /** @see java.lang.Object#toString() */
    public String toString() {
        StringBuffer sb = new StringBuffer("Space[");
        sb.append(super.toString());
        sb.append(", precedence=");
        if (isForcing()) {
            sb.append("forcing");
        } else {
            sb.append(getPrecedence());
        }
        sb.append("]");
        return sb.toString();
    }

}
