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
 *//* $Id: ListBlockLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.list;

import com.wisii.fov.fo.flow.ListBlock;
import com.wisii.fov.layoutmgr.BlockLevelLayoutManager;
import com.wisii.fov.layoutmgr.BlockStackingLayoutManager;
import com.wisii.fov.layoutmgr.ConditionalElementListener;
import com.wisii.fov.layoutmgr.LayoutManager;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.PositionIterator;
import com.wisii.fov.layoutmgr.Position;
import com.wisii.fov.layoutmgr.NonLeafPosition;
import com.wisii.fov.layoutmgr.RelSide;
import com.wisii.fov.layoutmgr.TraitSetter;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.Block;
import com.wisii.fov.traits.MinOptMax;
import com.wisii.fov.traits.SpaceVal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * LayoutManager for a list-block FO.
 * A list block contains list items which are stacked within
 * the list block area..
 */
public class ListBlockLayoutManager extends BlockStackingLayoutManager
                implements ConditionalElementListener {

    private Block curBlockArea;

    private boolean discardBorderBefore;
    private boolean discardBorderAfter;
    private boolean discardPaddingBefore;
    private boolean discardPaddingAfter;
    private MinOptMax effSpaceBefore;
    private MinOptMax effSpaceAfter;

    private static class StackingIter extends PositionIterator {
        StackingIter(Iterator parentIter) {
            super(parentIter);
        }

        protected LayoutManager getLM(Object nextObj) {
            return ((Position) nextObj).getLM();
        }

        protected Position getPos(Object nextObj) {
            return ((Position) nextObj);
        }
    }

    /*
    private class SectionPosition extends LeafPosition {
        protected List list;
        protected SectionPosition(LayoutManager lm, int pos, List l) {
            super(lm, pos);
            list = l;
        }
    }*/

    /**
     * Create a new list block layout manager.
     * @param node list-block to create the layout manager for
     */
    public ListBlockLayoutManager(ListBlock node) {
        super(node);
    }

    /**
     * Convenience method.
     * @return the ListBlock node
     */
    protected ListBlock getListBlockFO() {
        return (ListBlock)fobj;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#initialize() */
    public void initialize() {
        foSpaceBefore = new SpaceVal(
                getListBlockFO().getCommonMarginBlock().spaceBefore, this).getSpace();
        foSpaceAfter = new SpaceVal(
                getListBlockFO().getCommonMarginBlock().spaceAfter, this).getSpace();
        startIndent = getListBlockFO().getCommonMarginBlock().startIndent.getValue(this);
        endIndent = getListBlockFO().getCommonMarginBlock().endIndent.getValue(this);
    }

    private void resetSpaces() {
        this.discardBorderBefore = false;
        this.discardBorderAfter = false;
        this.discardPaddingBefore = false;
        this.discardPaddingAfter = false;
        this.effSpaceBefore = null;
        this.effSpaceAfter = null;
    }

    /** @see com.wisii.fov.layoutmgr.BlockStackingLayoutManager */
    public LinkedList getNextKnuthElements(LayoutContext context, int alignment) {
        resetSpaces();
        return super.getNextKnuthElements(context, alignment);
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#getChangedKnuthElements(java.util.List, int) */
    public LinkedList getChangedKnuthElements(List oldList, int alignment) {
        //log.debug("LBLM.getChangedKnuthElements>");
        return super.getChangedKnuthElements(oldList, alignment);
    }

    /**
     * The table area is a reference area that contains areas for
     * columns, bodies, rows and the contents are in cells.
     *
     * @param parentIter the position iterator
     * @param layoutContext the layout context for adding areas
     */
    public void addAreas(PositionIterator parentIter,
                         LayoutContext layoutContext) {
        getParentArea(null);

        // if this will create the first block area in a page
        // and display-align is after or center, add space before
        if (layoutContext.getSpaceBefore() > 0) {
            addBlockSpacing(0.0, new MinOptMax(layoutContext.getSpaceBefore()));
        }

        getPSLM().addIDToPage(getListBlockFO().getId());

        // the list block contains areas stacked from each list item

        LayoutManager childLM = null;
        LayoutContext lc = new LayoutContext(0);
        LayoutManager firstLM = null;
        LayoutManager lastLM = null;
        Position firstPos = null;
        Position lastPos = null;

        // "unwrap" the NonLeafPositions stored in parentIter
        // and put them in a new list;
        LinkedList positionList = new LinkedList();
        Position pos;
        while (parentIter.hasNext()) {
            pos = (Position)parentIter.next();
            if (pos.getIndex() >= 0) {
                if (firstPos == null) {
                    firstPos = pos;
                }
                lastPos = pos;
            }
            if (pos instanceof NonLeafPosition
                    && (pos.getPosition() != null)
                    && ((NonLeafPosition) pos).getPosition().getLM() != this) {
                // pos was created by a child of this ListBlockLM
                positionList.add(((NonLeafPosition) pos).getPosition());
                lastLM = ((NonLeafPosition) pos).getPosition().getLM();
                if (firstLM == null) {
                    firstLM = lastLM;
                }
            }
        }

        if (markers != null) {
            getCurrentPV().addMarkers(markers, true, isFirst(firstPos), isLast(lastPos));
        }

        StackingIter childPosIter = new StackingIter(positionList.listIterator());
        while ((childLM = childPosIter.getNextChildLM()) != null) {
            // Add the block areas to Area
            // set the space adjustment ratio
            lc.setSpaceAdjust(layoutContext.getSpaceAdjust());
            lc.setFlags(LayoutContext.FIRST_AREA, childLM == firstLM);
            lc.setFlags(LayoutContext.LAST_AREA, childLM == lastLM);
            lc.setStackLimit(layoutContext.getStackLimit());
            childLM.addAreas(childPosIter, lc);
        }

        if (markers != null) {
            getCurrentPV().addMarkers(markers, false, isFirst(firstPos), isLast(lastPos));
        }

        // We are done with this area add the background
        TraitSetter.addBackground(curBlockArea,
                getListBlockFO().getCommonBorderPaddingBackground(),
                this);
        TraitSetter.addSpaceBeforeAfter(curBlockArea, layoutContext.getSpaceAdjust(),
                effSpaceBefore, effSpaceAfter);

        flush();

        curBlockArea = null;
        resetSpaces();

        getPSLM().notifyEndOfLayout(((ListBlock)getFObj()).getId());
    }

    /**
     * Return an Area which can contain the passed childArea. The childArea
     * may not yet have any content, but it has essential traits set.
     * In general, if the LayoutManager already has an Area it simply returns
     * it. Otherwise, it makes a new Area of the appropriate class.
     * It gets a parent area for its area by calling its parent LM.
     * Finally, based on the dimensions of the parent area, it initializes
     * its own area. This includes setting the content IPD and the maximum
     * BPD.
     *
     * @param childArea the child area
     * @return the parent area of the child
     */
    public Area getParentArea(Area childArea) {
        if (curBlockArea == null) {
            curBlockArea = new Block();

            // Set up dimensions
            // Must get dimensions from parent area
            /*Area parentArea =*/ parentLM.getParentArea(curBlockArea);

            // set traits
            TraitSetter.setProducerID(curBlockArea, getListBlockFO().getId());
            TraitSetter.addBorders(curBlockArea,
                    getListBlockFO().getCommonBorderPaddingBackground(),
                    discardBorderBefore, discardBorderAfter, false, false, this);
            TraitSetter.addPadding(curBlockArea,
                    getListBlockFO().getCommonBorderPaddingBackground(),
                    discardPaddingBefore, discardPaddingAfter, false, false, this);
            TraitSetter.addMargins(curBlockArea,
                    getListBlockFO().getCommonBorderPaddingBackground(),
                    getListBlockFO().getCommonMarginBlock(),
                    this);
            TraitSetter.addBreaks(curBlockArea,
                    getListBlockFO().getBreakBefore(),
                    getListBlockFO().getBreakAfter());

            int contentIPD = referenceIPD - getIPIndents();
            curBlockArea.setIPD(contentIPD);

            setCurrentArea(curBlockArea);
        }
        return curBlockArea;
    }

    /**
     * Add the child area to this layout manager.
     *
     * @param childArea the child area to add
     */
    public void addChildArea(Area childArea) {
        if (curBlockArea != null) {
            curBlockArea.addBlock((Block) childArea);
            /* 【添加：START】by 李晓光2009-6-9 */
            childArea.setParentArea(curBlockArea);
    		/* 【添加：END】by 李晓光 2009-6-9 */
        }
    }

    /**
     * Reset the position of this layout manager.
     *
     * @param resetPos the position to reset to
     */
    public void resetPosition(Position resetPos) {
        if (resetPos == null) {
            reset(null);
        } else {
            //TODO Something to put here?
        }
    }

    /** @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager#mustKeepTogether() */
    public boolean mustKeepTogether() {
        //TODO Keeps will have to be more sophisticated sooner or later
        return ((BlockLevelLayoutManager)getParent()).mustKeepTogether()
                || !getListBlockFO().getKeepTogether().getWithinPage().isAuto()
                || !getListBlockFO().getKeepTogether().getWithinColumn().isAuto();
    }

    /** @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager#mustKeepWithPrevious() */
    public boolean mustKeepWithPrevious() {
        return !getListBlockFO().getKeepWithPrevious().getWithinPage().isAuto()
            || !getListBlockFO().getKeepWithPrevious().getWithinColumn().isAuto();
    }

    /** @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager#mustKeepWithNext() */
    public boolean mustKeepWithNext() {
        return !getListBlockFO().getKeepWithNext().getWithinPage().isAuto()
                || !getListBlockFO().getKeepWithNext().getWithinColumn().isAuto();
    }

    /** @see com.wisii.fov.layoutmgr.ConditionalElementListener */
    public void notifySpace(RelSide side, MinOptMax effectiveLength) {
        if (RelSide.BEFORE == side) {
            if (log.isDebugEnabled()) {
                log.debug(this + ": Space " + side + ", "
                        + this.effSpaceBefore + "-> " + effectiveLength);
            }
            this.effSpaceBefore = effectiveLength;
        } else {
            if (log.isDebugEnabled()) {
                log.debug(this + ": Space " + side + ", "
                        + this.effSpaceAfter + "-> " + effectiveLength);
            }
            this.effSpaceAfter = effectiveLength;
        }
    }

    /** @see com.wisii.fov.layoutmgr.ConditionalElementListener */
    public void notifyBorder(RelSide side, MinOptMax effectiveLength) {
        if (effectiveLength == null) {
            if (RelSide.BEFORE == side) {
                this.discardBorderBefore = true;
            } else {
                this.discardBorderAfter = true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(this + ": Border " + side + " -> " + effectiveLength);
        }
    }

    /** @see com.wisii.fov.layoutmgr.ConditionalElementListener */
    public void notifyPadding(RelSide side, MinOptMax effectiveLength) {
        if (effectiveLength == null) {
            if (RelSide.BEFORE == side) {
                this.discardPaddingBefore = true;
            } else {
                this.discardPaddingAfter = true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(this + ": Padding " + side + " -> " + effectiveLength);
        }
    }

}

