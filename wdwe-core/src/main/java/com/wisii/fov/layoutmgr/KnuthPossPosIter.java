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
 *//* $Id: KnuthPossPosIter.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr;

import java.util.List;

public class KnuthPossPosIter extends PositionIterator {

    private int iterCount;

    /**
     * Main constructor
     * @param elementList List of Knuth elements
     * @param startPos starting position
     * @param endPos ending position
     */
    public KnuthPossPosIter(List elementList, int startPos, int endPos) {
        super(elementList.listIterator(startPos));
        iterCount = endPos - startPos;
    }

    /**
     * Auxiliary constructor
     * @param elementList List of Knuth elements
     */
    public KnuthPossPosIter(List elementList) {
        this(elementList, 0, elementList.size());
    }

    // Check position < endPos

    /**
     * @see com.wisii.fov.layoutmgr.PositionIterator#checkNext()
     */
    protected boolean checkNext() {
        if (iterCount > 0) {
            return super.checkNext();
        } else {
            endIter();
            return false;
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
        --iterCount;
        return super.next();
    }

    public ListElement getKE() {
        return (ListElement) peekNext();
    }

    protected LayoutManager getLM(Object nextObj) {
        return ((ListElement) nextObj).getLayoutManager();
    }

    protected Position getPos(Object nextObj) {
        return ((ListElement) nextObj).getPosition();
    }
}
