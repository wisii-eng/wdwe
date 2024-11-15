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
 *//* $Id: FilledArea.java,v 1.1 2007/04/12 06:41:18 cvsuser Exp $ */

package com.wisii.fov.area.inline;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Filled area.
 * This inline area contains some inline areas.
 * When the renderer gets the child areas to render
 * the inline areas are repeated to fill the ipd of
 * this inline parent.
 * This extends InlineParent so that the renderer will render
 * this as a normal inline parent.
 */
public class FilledArea extends InlineParent {
    private int unitWidth;

    /**
     * Create a new filled area.
     */
    public FilledArea() {
    }

    /**
     * Set the offset of the descendant TextAreas,
     * instead of the offset of the FilledArea itself.
     *
     * @param v the offset
     */
    /*
    public void setOffset(int v) {
        setChildOffset(inlines.listIterator(), v);
    }
    */

    private void setChildOffset(ListIterator childrenIterator, int v) {
        while (childrenIterator.hasNext()) {
            InlineArea child = (InlineArea) childrenIterator.next();
            if (child instanceof InlineParent) {
                setChildOffset(((InlineParent) child).getChildAreas().listIterator(), v);
            } else if (child instanceof com.wisii.fov.area.inline.Viewport) {
                // nothing
            } else {
                child.setOffset(v);
            }
        }
    }

    /**
     * Set the unit width for the areas to fill the full width.
     *
     * @param w the unit width
     */
    public void setUnitWidth(int w) {
        unitWidth = w;
    }

    /**
     * Return the unit width for the areas to fill the full width.
     *
     * @return the unit width
     */
    public int getUnitWidth() {
        return unitWidth;
    }

    /**
     * @see com.wisii.fov.area.Area#getBPD
     */
    public int getBPD() {
        int bpd = 0;
        for (Iterator childAreaIt = getChildAreas().iterator(); childAreaIt.hasNext();) {
            InlineArea area = (InlineArea)childAreaIt.next();
            if (bpd < area.getBPD()) {
                bpd = area.getBPD();
            }
        }
        return bpd;
    }

    /**
     * Get the child areas for this filled area.
     * This copies the references of the inline areas so that
     * it fills the total width of the area a whole number of times
     * for the unit width.
     *
     * @return the list of child areas copied to fill the width
     */
    public List getChildAreas() {
        int units = (int)(getIPD() / unitWidth);
        List newList = new ArrayList();
        for (int count = 0; count < units; count++) {
            newList.addAll(inlines);
        }
        return newList;
    }
}

