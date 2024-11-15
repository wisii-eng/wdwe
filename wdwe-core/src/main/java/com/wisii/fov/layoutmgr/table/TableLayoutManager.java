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
 *//* $Id: TableLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.table;

import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.flow.Table;
import com.wisii.fov.fo.flow.TableColumn;
import com.wisii.fov.fo.properties.TableColLength;
import com.wisii.fov.layoutmgr.BlockStackingLayoutManager;
import com.wisii.fov.layoutmgr.ConditionalElementListener;
import com.wisii.fov.layoutmgr.KnuthElement;
import com.wisii.fov.layoutmgr.KnuthGlue;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.ListElement;
import com.wisii.fov.layoutmgr.PositionIterator;
import com.wisii.fov.layoutmgr.Position;
import com.wisii.fov.layoutmgr.RelSide;
import com.wisii.fov.layoutmgr.TraitSetter;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.Block;
import com.wisii.fov.traits.MinOptMax;
import com.wisii.fov.traits.SpaceVal;
import java.util.Iterator;
import java.util.LinkedList;
import com.wisii.fov.datatypes.LengthBase;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;

/**
 * LayoutManager for a table FO.
 * A table consists of oldColumns, table header, table footer and multiple
 * table bodies.
 * The header, footer and body add the areas created from the table cells.
 * The table then creates areas for the oldColumns, bodies and rows
 * the render background.
 */
public class TableLayoutManager extends BlockStackingLayoutManager
                implements ConditionalElementListener {

    private TableContentLayoutManager contentLM;
    private ColumnSetup columns = null;

    private Block curBlockArea;

    private double tableUnits;
    private boolean autoLayout = true;

    private boolean discardBorderBefore;
    private boolean discardBorderAfter;
    private boolean discardPaddingBefore;
    private boolean discardPaddingAfter;
    private MinOptMax effSpaceBefore;
    private MinOptMax effSpaceAfter;

    private int halfBorderSeparationBPD;
    private int halfBorderSeparationIPD;

    /**
     * Create a new table layout manager.
     * @param node the table FO
     */
    public TableLayoutManager(Table node) {
        super(node);
        this.columns = new ColumnSetup(node);
    }

    /** @return the table FO */
    public Table getTable() {
        return (Table)this.fobj;
    }

    /**
     * @return the column setup for this table.
     */
    public ColumnSetup getColumns() {
        return this.columns;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#initialize() */
    public void initialize() {
        foSpaceBefore = new SpaceVal(
                getTable().getCommonMarginBlock().spaceBefore, this).getSpace();
        foSpaceAfter = new SpaceVal(
                getTable().getCommonMarginBlock().spaceAfter, this).getSpace();
        startIndent = getTable().getCommonMarginBlock().startIndent.getValue(this);
        endIndent = getTable().getCommonMarginBlock().endIndent.getValue(this);

        if (getTable().isSeparateBorderModel()) {
            this.halfBorderSeparationBPD = getTable().getBorderSeparation().getBPD().getLength()
                    .getValue(this) / 2;
            this.halfBorderSeparationIPD = getTable().getBorderSeparation().getIPD().getLength()
                    .getValue(this) / 2;
        } else {
            this.halfBorderSeparationBPD = 0;
            this.halfBorderSeparationIPD = 0;
        }

        if (!getTable().isAutoLayout()
                && getTable().getInlineProgressionDimension().getOptimum(this).getEnum()
                    != EN_AUTO) {
            autoLayout = false;
        }
    }

    private void resetSpaces() {
        this.discardBorderBefore = false;
        this.discardBorderAfter = false;
        this.discardPaddingBefore = false;
        this.discardPaddingAfter = false;
        this.effSpaceBefore = null;
        this.effSpaceAfter = null;
    }

    /** @return half the value of border-separation.block-progression-dimension. */
    public int getHalfBorderSeparationBPD() {
        return halfBorderSeparationBPD;
    }

    /** @return half the value of border-separation.inline-progression-dimension. */
    public int getHalfBorderSeparationIPD() {
        return halfBorderSeparationIPD;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager */
    public LinkedList getNextKnuthElements(LayoutContext context, int alignment) {

        LinkedList returnList = new LinkedList();

        if (!breakBeforeServed) {
            try {
                if (addKnuthElementsForBreakBefore(returnList, context)) {
                    return returnList;
                }
            } finally {
                breakBeforeServed = true;
            }
        }

        referenceIPD = context.getRefIPD();

        if (getTable().getInlineProgressionDimension().getOptimum(this).getEnum() != EN_AUTO) {
            int contentIPD = getTable().getInlineProgressionDimension().getOptimum(this)
                    .getLength().getValue(this);
            updateContentAreaIPDwithOverconstrainedAdjust(contentIPD);
        } else {
            if (!getTable().isAutoLayout()) {
                log.info("table-layout=\"fixed\" and width=\"auto\", "
                        + "but auto-layout not supported "
                        + "=> assuming width=\"100%\"");
            }
            updateContentAreaIPDwithOverconstrainedAdjust();
        }

        int availableIPD = referenceIPD - getIPIndents();
        if (getContentAreaIPD() > availableIPD) {
            log.warn(FONode.decorateWithContextInfo(
                    "The extent in inline-progression-direction (width) of a table is"
                    + " bigger than the available space ("
                    + getContentAreaIPD() + "mpt > " + context.getRefIPD() + "mpt)",
                    getTable()));
        }

        // either works out table of column widths or if proportional-column-width function
        // is used works out total factor, so that value of single unit can be computed.
        int sumCols = 0;
        float factors = 0;
        for (Iterator i = columns.iterator(); i.hasNext();) {
            TableColumn column = (TableColumn) i.next();
            if (column != null) {
                Length width = column.getColumnWidth();
                sumCols += width.getValue(this);
                if (width instanceof TableColLength) {
                    factors += ((TableColLength) width).getTableUnits();
                }
            }
        }
        // sets TABLE_UNITS in case where one or more oldColumns is defined using
        // proportional-column-width
        if (sumCols < getContentAreaIPD()) {
            if (tableUnits == 0.0) {
                this.tableUnits = (getContentAreaIPD() - sumCols) / factors;
            }
        }

        if (!firstVisibleMarkServed) {
            addKnuthElementsForSpaceBefore(returnList, alignment);
        }

        if (getTable().isSeparateBorderModel()) {
            addKnuthElementsForBorderPaddingBefore(returnList, !firstVisibleMarkServed);
            firstVisibleMarkServed = true;
        }

        //Spaces, border and padding to be repeated at each break
        addPendingMarks(context);

        LinkedList returnedList = null;
        LinkedList contentList = new LinkedList();
        //Position returnPosition = new NonLeafPosition(this, null);
        //Body prevLM = null;

        LayoutContext childLC = new LayoutContext(0);
        /*
        childLC.setStackLimit(
              MinOptMax.subtract(context.getStackLimit(),
                                 stackSize));*/
        childLC.setRefIPD(context.getRefIPD());
        childLC.copyPendingMarksFrom(context);

        if (contentLM == null) {
            contentLM = new TableContentLayoutManager(this);
        }
        returnedList = contentLM.getNextKnuthElements(childLC, alignment);
        if (childLC.isKeepWithNextPending()) {
            log.debug("TableContentLM signals pending keep-with-next");
            context.setFlags(LayoutContext.KEEP_WITH_NEXT_PENDING);
        }
        if (childLC.isKeepWithPreviousPending()) {
            log.debug("TableContentLM signals pending keep-with-previous");
            context.setFlags(LayoutContext.KEEP_WITH_PREVIOUS_PENDING);
        }

        //Set index values on elements coming from the content LM
        Iterator iter = returnedList.iterator();
        while (iter.hasNext()) {
            ListElement el = (ListElement)iter.next();
            notifyPos(el.getPosition());
        }
        log.debug(returnedList);

        if (returnedList.size() == 1
                && ((ListElement)returnedList.getFirst()).isForcedBreak()) {
            // a descendant of this block has break-before
            if (returnList.size() == 0) {
                // the first child (or its first child ...) has
                // break-before;
                // all this block, including space before, will be put in
                // the
                // following page
                //FIX ME
                //bSpaceBeforeServed = false;
            }
            contentList.addAll(returnedList);

            // "wrap" the Position inside each element
            // moving the elements from contentList to returnList
            returnedList = new LinkedList();
            wrapPositionElements(contentList, returnList);

            return returnList;
        } else {
            /*
            if (prevLM != null) {
                // there is a block handled by prevLM
                // before the one handled by curLM
                if (mustKeepTogether()
                        || prevLM.mustKeepWithNext()
                        || curLM.mustKeepWithPrevious()) {
                    // add an infinite penalty to forbid a break between
                    // blocks
                    contentList.add(new KnuthPenalty(0,
                            KnuthElement.INFINITE, false,
                            new Position(this), false));
                } else if (!((KnuthElement) contentList.getLast()).isGlue()) {
                    // add a null penalty to allow a break between blocks
                    contentList.add(new KnuthPenalty(0, 0, false,
                            new Position(this), false));
                } else {
                    // the last element in contentList is a glue;
                    // it is a feasible breakpoint, there is no need to add
                    // a penalty
                }
            }*/
            contentList.addAll(returnedList);
            if (returnedList.size() > 0) {
                if (((ListElement)returnedList.getLast()).isForcedBreak()) {
                    // a descendant of this block has break-after
                    if (false /*curLM.isFinished()*/) {
                        // there is no other content in this block;
                        // it's useless to add space after before a page break
                        setFinished(true);
                    }

                    returnedList = new LinkedList();
                    wrapPositionElements(contentList, returnList);

                    return returnList;
                }
            }
        }
        wrapPositionElements(contentList, returnList);
        if (getTable().isSeparateBorderModel()) {
            addKnuthElementsForBorderPaddingAfter(returnList, true);
        }
        addKnuthElementsForSpaceAfter(returnList, alignment);
        addKnuthElementsForBreakAfter(returnList, context);
        setFinished(true);
        resetSpaces();
        return returnList;
    }

    /**
     * The table area is a reference area that contains areas for
     * oldColumns, bodies, rows and the contents are in cells.
     *
     * @param parentIter the position iterator
     * @param layoutContext the layout context for adding areas
     */
    public void addAreas(PositionIterator parentIter,
                         LayoutContext layoutContext) {
        getParentArea(null);
        getPSLM().addIDToPage(getTable().getId());

        // add space before, in order to implement display-align = "center" or "after"
        if (layoutContext.getSpaceBefore() != 0) {
            addBlockSpacing(0.0, new MinOptMax(layoutContext.getSpaceBefore()));
        }

        int startXOffset = getTable().getCommonMarginBlock().startIndent.getValue(this);

        // add column, body then row areas

        int tableHeight = 0;
        //Body childLM;
        LayoutContext lc = new LayoutContext(0);


        lc.setRefIPD(getContentAreaIPD());
        contentLM.setStartXOffset(startXOffset);
        contentLM.addAreas(parentIter, lc);
        tableHeight += contentLM.getUsedBPD();

        curBlockArea.setBPD(tableHeight);

        if (getTable().isSeparateBorderModel()) {
            TraitSetter.addBorders(curBlockArea,
                    getTable().getCommonBorderPaddingBackground(),
                    discardBorderBefore, discardBorderAfter, false, false, this);
            TraitSetter.addPadding(curBlockArea,
                    getTable().getCommonBorderPaddingBackground(),
                    discardPaddingBefore, discardPaddingAfter, false, false, this);
        }
        TraitSetter.addBackground(curBlockArea,
                getTable().getCommonBorderPaddingBackground(),
                this);
        TraitSetter.addMargins(curBlockArea,
                getTable().getCommonBorderPaddingBackground(),
                startIndent, endIndent,
                this);
        TraitSetter.addBreaks(curBlockArea,
                getTable().getBreakBefore(), getTable().getBreakAfter());
        TraitSetter.addSpaceBeforeAfter(curBlockArea, layoutContext.getSpaceAdjust(),
                effSpaceBefore, effSpaceAfter);

        flush();

        resetSpaces();
        curBlockArea = null;

        getPSLM().notifyEndOfLayout(((Table)getFObj()).getId());
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

            TraitSetter.setProducerID(curBlockArea, getTable().getId());

            curBlockArea.setIPD(getContentAreaIPD());

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
            /* 【添加：START】 by 李晓光 2009-6-9  */
            childArea.setParentArea(curBlockArea);
            /* 【添加：END】 by 李晓光 2009-6-9  */
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
        }
    }

    /** @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager */
    public int negotiateBPDAdjustment(int adj, KnuthElement lastElement) {
        // TODO Auto-generated method stub
        return 0;
    }

    /** @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager */
    public void discardSpace(KnuthGlue spaceGlue) {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager#mustKeepTogether()
     */
    public boolean mustKeepTogether() {
        //TODO Keeps will have to be more sophisticated sooner or later
        return super.mustKeepTogether()
                || !getTable().getKeepTogether().getWithinPage().isAuto()
                || !getTable().getKeepTogether().getWithinColumn().isAuto();
    }

    /**
     * @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager#mustKeepWithPrevious()
     */
    public boolean mustKeepWithPrevious() {
        return !getTable().getKeepWithPrevious().getWithinPage().isAuto()
                || !getTable().getKeepWithPrevious().getWithinColumn().isAuto();
    }

    /**
     * @see com.wisii.fov.layoutmgr.BlockLevelLayoutManager#mustKeepWithNext()
     */
    public boolean mustKeepWithNext() {
        return !getTable().getKeepWithNext().getWithinPage().isAuto()
                || !getTable().getKeepWithNext().getWithinColumn().isAuto();
    }

    // --------- Property Resolution related functions --------- //

    /**
     * @see com.wisii.fov.datatypes.PercentBaseContext#getBaseLength(int, FObj)
     */
    public int getBaseLength(int lengthBase, FObj fobj) {
        // Special handler for TableColumn width specifications
        if (fobj instanceof TableColumn && fobj.getParent() == getFObj()) {
            switch (lengthBase) {
            case LengthBase.CONTAINING_BLOCK_WIDTH:
                return getContentAreaIPD();
            default:
                log.error("Unknown base type for LengthBase.");
                return 0;
            }
        } else {
            switch (lengthBase) {
            case LengthBase.TABLE_UNITS:
                return (int)this.tableUnits;
            default:
                return super.getBaseLength(lengthBase, fobj);
            }
        }
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
