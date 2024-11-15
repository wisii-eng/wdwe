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
 *///* $Id: InlineBlockParent.java,v 1.1 2007/04/12 06:41:18 cvsuser Exp $ */

package com.wisii.fov.area.inline;

import com.wisii.fov.area.Area;
import com.wisii.fov.area.Block;


/**
 * Inline block parent area.
 * This is an inline area that can have one block area as a child
 */
public class InlineBlockParent extends InlineArea {

    /**
     * The list of inline areas added to this inline parent.
     */
    protected Block child = null;

    /**
     * Create a new inline block parent to add areas to.
     */
    public InlineBlockParent() {
    }

    /**
     * Override generic Area method.
     *
     * @param childArea the child area to add
     */
    public void addChildArea(Area childArea) {
        if (child != null) {
            throw new IllegalStateException("InlineBlockParent 只能有一个子区域.");
        }
        if (childArea instanceof Block) {
            child = (Block) childArea;
            /* 【添加：START】 by 李晓光 2009-6-9 */
            childArea.setParentArea(this);
            /* 【添加：END】 by 李晓光 2009-6-9 */
            //Update extents from the child
            setIPD(childArea.getAllocIPD());
            setBPD(childArea.getAllocBPD());
        } else {
            throw new IllegalArgumentException(" InlineBlockParent 的子区域必须是"
                    + " 块");
        }
    }

    /**
     * Get the child areas for this inline parent.
     *
     * @return the list of child areas
     */
    public Block getChildArea() {
        return child;
    }

}
