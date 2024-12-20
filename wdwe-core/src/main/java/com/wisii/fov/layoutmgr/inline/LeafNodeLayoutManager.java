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
 *//* $Id: LeafNodeLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import java.util.LinkedList;
import java.util.List;

import com.wisii.fov.area.Area;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.layoutmgr.AbstractLayoutManager;
import com.wisii.fov.layoutmgr.InlineKnuthSequence;
import com.wisii.fov.layoutmgr.KnuthGlue;
import com.wisii.fov.layoutmgr.KnuthPenalty;
import com.wisii.fov.layoutmgr.KnuthSequence;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.LeafPosition;
import com.wisii.fov.layoutmgr.Position;
import com.wisii.fov.layoutmgr.PositionIterator;
import com.wisii.fov.layoutmgr.TraitSetter;
import com.wisii.fov.traits.MinOptMax;


/**
 * Base LayoutManager for leaf-node FObj, ie: ones which have no children.
 * These are all inline objects. Most of them cannot be split (Text is
 * an exception to this rule.)
 * This class can be extended to handle the creation and adding of the
 * inline area.
 */
public abstract class LeafNodeLayoutManager extends AbstractLayoutManager
                                   implements InlineLevelLayoutManager {
    /**
     * The inline area that this leafnode will add.
     */
    protected InlineArea curArea = null;
    /** Any border, padding and background properties applying to this area */
    protected CommonBorderPaddingBackground commonBorderPaddingBackground = null;
    /** The alignment context applying to this area */
    protected AlignmentContext alignmentContext = null;

    private MinOptMax ipd;

    /** Flag to indicate if something was changed as part of the getChangeKnuthElements sequence */
    protected boolean isSomethingChanged = false;
    /** Our area info for the Knuth elements */
    protected AreaInfo areaInfo = null;

    /**
     * Store information about the inline area
     */
    protected class AreaInfo {
        protected short iLScount;
        protected MinOptMax ipdArea;
        protected boolean bHyphenated;
        protected AlignmentContext alignmentContext;

        public AreaInfo(short iLS, MinOptMax ipd, boolean bHyph,
                        AlignmentContext alignmentContext) {
            iLScount = iLS;
            ipdArea = ipd;
            bHyphenated = bHyph;
            this.alignmentContext = alignmentContext;
        }

    }


    /**
     * Create a Leaf node layout mananger.
     * @param node the FObj to attach to this LM.
     */
    public LeafNodeLayoutManager(FObj node) {
        super(node);
    }

    /**
     * Create a Leaf node layout mananger.
     */
    public LeafNodeLayoutManager() {
    }

    /**
     * get the inline area.
     * @param context the context used to create the area
     * @return the current inline area for this layout manager
     */
    public InlineArea get(LayoutContext context) {
        return curArea;
    }

    /**
     * Check if this inline area is resolved due to changes in
     * page or ipd.
     * Currently not used.
     * @return true if the area is resolved when adding
     */
    public boolean resolved() {
        return false;
    }

    /**
     * Set the current inline area.
     * @param ia the inline area to set for this layout manager
     */
    public void setCurrentArea(InlineArea ia) {
        curArea = ia;
    }

    /**
     * This is a leaf-node, so this method is never called.
     * @param childArea the childArea to add
     */
    public void addChildArea(Area childArea) {
    }

    /**
     * This is a leaf-node, so this method is never called.
     * @param childArea the childArea to get the parent for
     * @return the parent area
     */
    public Area getParentArea(Area childArea) {
        return null;
    }

    /**
     * Set the border and padding properties of the inline area.
     * @param commonBorderPaddingBackground the alignment adjust property
     */
    protected void setCommonBorderPaddingBackground(
            CommonBorderPaddingBackground commonBorderPaddingBackground) {
        this.commonBorderPaddingBackground = commonBorderPaddingBackground;
    }

    /**
     * Get the allocation ipd of the inline area.
     * This method may be overridden to handle percentage values.
     * @param refIPD the ipd of the parent reference area
     * @return the min/opt/max ipd of the inline area
     */
    protected MinOptMax getAllocationIPD(int refIPD) {
        return new MinOptMax(curArea.getIPD());
    }

    /**
     * Add the area for this layout manager.
     * This adds the single inline area to the parent.
     * @param posIter the position iterator
     * @param context the layout context for adding the area
     */
    public void addAreas(PositionIterator posIter, LayoutContext context) {
        addId();

        InlineArea area = getEffectiveArea();
        if (area.getAllocIPD() > 0 || area.getAllocBPD() > 0) {
            offsetArea(area, context);
            widthAdjustArea(area, context);
            if (commonBorderPaddingBackground != null) {
                // Add border and padding to area
                TraitSetter.setBorderPaddingTraits(area,
                                                   commonBorderPaddingBackground,
                                                   false, false, this);
                TraitSetter.addBackground(area, commonBorderPaddingBackground, this);
            }
            parentLM.addChildArea(area);
        }

        while (posIter.hasNext()) {
            posIter.next();
        }
    }

    /**
     * @return the effective area to be added to the area tree. Normally, this is simply "curArea"
     * but in the case of page-number(-citation) curArea is cloned, updated and returned.
     */
    protected InlineArea getEffectiveArea() {
        return curArea;
    }

    /**
     * This method is called by addAreas() so IDs can be added to a page for FOs that
     * support the 'id' property.
     */
    protected void addId() {
        // Do nothing here, overriden in subclasses that have an 'id' property.
    }

    /**
     * Offset this area.
     * Offset the inline area in the bpd direction when adding the
     * inline area.
     * This is used for vertical alignment.
     * Subclasses should override this if necessary.
     * @param area the inline area to be updated
     * @param context the layout context used for adding the area
     */
    protected void offsetArea(InlineArea area, LayoutContext context) {
        area.setOffset(alignmentContext.getOffset());
    }

    /**
     * Creates a new alignment context or returns the current
     * alignment context.
     * This is used for vertical alignment.
     * Subclasses should override this if necessary.
     * @param context the layout context used
     * @return the appropriate alignment context
     */
    protected AlignmentContext makeAlignmentContext(LayoutContext context) {
        return context.getAlignmentContext();
    }

    /**
     * Adjust the width of the area when adding.
     * This uses the min/opt/max values to adjust the with
     * of the inline area by a percentage.
     * @param area the inline area to be updated
     * @param context the layout context for adding this area
     */
    protected void widthAdjustArea(InlineArea area, LayoutContext context) {
        double dAdjust = context.getIPDAdjust();
        int width = areaInfo.ipdArea.opt;
        if (dAdjust < 0) {
            width = (int) (width + dAdjust * (areaInfo.ipdArea.opt
                                             - areaInfo.ipdArea.min));
        } else if (dAdjust > 0) {
            width = (int) (width + dAdjust * (areaInfo.ipdArea.max
                                             - areaInfo.ipdArea.opt));
        }
        area.setIPD(width);
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#getNextKnuthElements(LayoutContext, int) */
    public LinkedList getNextKnuthElements(LayoutContext context, int alignment) {
        curArea = get(context);

        if (curArea == null) {
            setFinished(true);
            return null;
        }

        alignmentContext = makeAlignmentContext(context);

        MinOptMax ipd = getAllocationIPD(context.getRefIPD());

        // create the AreaInfo object to store the computed values
        areaInfo = new AreaInfo((short) 0, ipd, false, alignmentContext);

        // node is a fo:ExternalGraphic, fo:InstreamForeignObject,
        // fo:PageNumber or fo:PageNumberCitation
        KnuthSequence seq = new InlineKnuthSequence();

        addKnuthElementsForBorderPaddingStart(seq);

        seq.add(new KnuthInlineBox(areaInfo.ipdArea.opt, alignmentContext,
                                    notifyPos(new LeafPosition(this, 0)), false));

        addKnuthElementsForBorderPaddingEnd(seq);

        LinkedList returnList = new LinkedList();

        returnList.add(seq);
        setFinished(true);
        return returnList;
    }

    /** @see InlineLevelLayoutManager#addALetterSpaceTo(List) */
    public List addALetterSpaceTo(List oldList) {
        // return the unchanged elements
        return oldList;
    }

    /**
     * Remove the word space represented by the given elements
     *
     * @param oldList the elements representing the word space
     */
    public void removeWordSpace(List oldList) {
        // do nothing
        log.warn(this.getClass().getName() + " should not receive a call to removeWordSpace(list)");
    }

    /** @see InlineLevelLayoutManager#getWordChars(StringBuffer, Position) */
    public void getWordChars(StringBuffer sbChars, Position pos) {
    }

    /** @see InlineLevelLayoutManager#hyphenate(Position, HyphContext) */
    public void hyphenate(Position pos, HyphContext hc) {
    }

    /** @see InlineLevelLayoutManager#applyChanges(List) */
    public boolean applyChanges(List oldList) {
        setFinished(false);
        return false;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#getChangedKnuthElements(List, int) */
    public LinkedList getChangedKnuthElements(List oldList,
                                              int alignment) {
        if (isFinished()) {
            return null;
        }

        LinkedList returnList = new LinkedList();

        addKnuthElementsForBorderPaddingStart(returnList);

        // fobj is a fo:ExternalGraphic, fo:InstreamForeignObject,
        // fo:PageNumber or fo:PageNumberCitation
        returnList.add(new KnuthInlineBox(areaInfo.ipdArea.opt, areaInfo.alignmentContext,
                                          notifyPos(new LeafPosition(this, 0)), true));

        addKnuthElementsForBorderPaddingEnd(returnList);

        setFinished(true);
        return returnList;
    }

    /**
     * Creates Knuth elements for start border padding and adds them to the return list.
     * @param returnList return list to add the additional elements to
     */
    protected void addKnuthElementsForBorderPaddingStart(List returnList) {
        //Border and Padding (start)
        if (commonBorderPaddingBackground != null) {
            int ipStart = commonBorderPaddingBackground.getBorderStartWidth(false)
                         + commonBorderPaddingBackground.getPaddingStart(false, this);
            if (ipStart > 0) {
                // Add a non breakable glue
                returnList.add(new KnuthPenalty(0, KnuthPenalty.INFINITE,
                                                false, new LeafPosition(this, -1), true));
                returnList.add(new KnuthGlue(ipStart, 0, 0, new LeafPosition(this, -1), true));
            }
        }
    }

    /**
     * Creates Knuth elements for end border padding and adds them to the return list.
     * @param returnList return list to add the additional elements to
     */
    protected void addKnuthElementsForBorderPaddingEnd(List returnList) {
        //Border and Padding (after)
        if (commonBorderPaddingBackground != null) {
            int ipEnd = commonBorderPaddingBackground.getBorderEndWidth(false)
                        + commonBorderPaddingBackground.getPaddingEnd(false, this);
            if (ipEnd > 0) {
                // Add a non breakable glue
                returnList.add(new KnuthPenalty(0, KnuthPenalty.INFINITE,
                                                false, new LeafPosition(this, -1), true));
                returnList.add(new KnuthGlue(ipEnd, 0, 0, new LeafPosition(this, -1), true));
            }
        }
    }

}

