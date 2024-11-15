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
 *//* $Id: BlockKnuthSequence.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr;

import java.util.List;


/**
 * Represents a list of block level Knuth elements.
 */
public class BlockKnuthSequence extends KnuthSequence {

    private boolean isClosed = false;

    /**
     * Creates a new and empty list.
     */
    public BlockKnuthSequence() {
        super();
    }

    /**
     * Creates a new list from an existing list.
     * @param list The list from which to create the new list.
     */
    public BlockKnuthSequence(List list) {
        super(list);
    }

    /* (non-Javadoc)
     * @see com.wisii.fov.layoutmgr.KnuthList#isInlineSequence()
     */
    public boolean isInlineSequence() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.wisii.fov.layoutmgr.KnuthList#canAppendSequence(com.wisii.fov.layoutmgr.KnuthSequence)
     */
    public boolean canAppendSequence(KnuthSequence sequence) {
        return !sequence.isInlineSequence() && !isClosed;
    }

    /* (non-Javadoc)
     * @see com.wisii.fov.layoutmgr.KnuthList#appendSequence(com.wisii.fov.layoutmgr.KnuthSequence,)
     */
    public boolean appendSequence(KnuthSequence sequence) {
        // log.debug("Cannot append a sequence without a BreakElement");
        return false;
    }

    /* (non-Javadoc)
     * @see KnuthList#appendSequence(KnuthSequence, boolean, BreakElement)
     */
    public boolean appendSequence(KnuthSequence sequence, boolean keepTogether,
                                  BreakElement breakElement) {
        if (!canAppendSequence(sequence)) {
            return false;
        }
        if (keepTogether) {
            breakElement.setPenaltyValue(KnuthElement.INFINITE);
            add(breakElement);
        } else if (!((ListElement) getLast()).isGlue()) {
            breakElement.setPenaltyValue(0);
            add(breakElement);
        }
        addAll(sequence);
        return true;
    }

    /* (non-Javadoc)
     * @see com.wisii.fov.layoutmgr.KnuthSequence#endSequence()
     */
    public KnuthSequence endSequence() {
        isClosed = true;
        return this;
    }

}
