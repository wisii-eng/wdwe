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
 *//* $Id: LeaderLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.FilledArea;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.Space;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.fo.flow.Leader;
import com.wisii.fov.fonts.Font;
import com.wisii.fov.layoutmgr.InlineKnuthSequence;
import com.wisii.fov.layoutmgr.KnuthElement;
import com.wisii.fov.layoutmgr.KnuthGlue;
import com.wisii.fov.layoutmgr.KnuthPenalty;
import com.wisii.fov.layoutmgr.KnuthPossPosIter;
import com.wisii.fov.layoutmgr.KnuthSequence;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.LeafPosition;
import com.wisii.fov.layoutmgr.Position;
import com.wisii.fov.layoutmgr.PositionIterator;
import com.wisii.fov.layoutmgr.TraitSetter;
import com.wisii.fov.traits.MinOptMax;

import java.util.List;
import java.util.LinkedList;
import com.wisii.fov.fo.FObj;

/**
 * LayoutManager for the fo:leader formatting object
 */
public class LeaderLayoutManager extends LeafNodeLayoutManager {
    private Leader fobj;
    private Font font = null;

    private LinkedList contentList = null;
    private ContentLayoutManager clm = null;

    private int contentAreaIPD = 0;

    /**
     * Constructor
     *
     * @param node the formatting object that creates this area
     */
    public LeaderLayoutManager(Leader node) {
        super(node);
        fobj = node;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#initialize */
    public void initialize() {
        font = fobj.getCommonFont().getFontState(fobj.getFOEventHandler().getFontInfo(), this);
        // the property leader-alignment does not affect vertical positioning
        // (see section 7.21.1 in the XSL Recommendation)
        // setAlignment(node.getLeaderAlignment());
        setCommonBorderPaddingBackground(fobj.getCommonBorderPaddingBackground());
    }

    /**
     * Return the inline area for this leader.
     * @param context the layout context
     * @return the inline area
     */
    public InlineArea get(LayoutContext context) {
        return getLeaderInlineArea(context);
    }

    /**
     * Return the allocated IPD for this area.
     * @param refIPD the IPD of the reference area
     * @return the allocated IPD
     */
    protected MinOptMax getAllocationIPD(int refIPD) {
        return getLeaderAllocIPD(refIPD);
    }

    private MinOptMax getLeaderAllocIPD(int ipd) {
        // length of the leader
        int borderPaddingWidth = 0;
        if (commonBorderPaddingBackground != null) {
            borderPaddingWidth = commonBorderPaddingBackground.getIPPaddingAndBorder(false, this);
        }
        setContentAreaIPD(ipd - borderPaddingWidth);
        int opt = fobj.getLeaderLength().getOptimum(this).getLength().getValue(this)
                    - borderPaddingWidth;
        int min = fobj.getLeaderLength().getMinimum(this).getLength().getValue(this)
                    - borderPaddingWidth;
        int max = fobj.getLeaderLength().getMaximum(this).getLength().getValue(this)
                    - borderPaddingWidth;
        return new MinOptMax(min, opt, max);
    }

    private InlineArea getLeaderInlineArea(LayoutContext context) {
        InlineArea leaderArea = null;

        if (fobj.getLeaderPattern() == EN_RULE) {
            if (fobj.getRuleStyle() != EN_NONE) {
                com.wisii.fov.area.inline.Leader leader
                    = new com.wisii.fov.area.inline.Leader();
                leader.setRuleStyle(fobj.getRuleStyle());
                leader.setRuleThickness(fobj.getRuleThickness().getValue(this));
                leader.setBPD(fobj.getRuleThickness().getValue(this));
                leaderArea = leader;
            } else {
                leaderArea = new Space();
                leaderArea.setBPD(1);
            }
            leaderArea.addTrait(Trait.COLOR, fobj.getColor());
        } else if (fobj.getLeaderPattern() == EN_SPACE) {
            leaderArea = new Space();
            leaderArea.setBPD(1);
        } else if (fobj.getLeaderPattern() == EN_DOTS) {
            TextArea t = new TextArea();
            char dot = '.'; // userAgent.getLeaderDotCharacter();

            int width = font.getCharWidth(dot);
            t.addWord("" + dot, 0);
            t.setIPD(width);
            t.setBPD(width);
            t.setBaselineOffset(width);
            TraitSetter.addFontTraits(t, font);
            t.addTrait(Trait.COLOR, fobj.getColor());
            Space spacer = null;
            if (fobj.getLeaderPatternWidth().getValue(this) > width) {
                spacer = new Space();
                spacer.setIPD(fobj.getLeaderPatternWidth().getValue(this) - width);
                width = fobj.getLeaderPatternWidth().getValue(this);
            }
            FilledArea fa = new FilledArea();
            fa.setUnitWidth(width);
            fa.addChildArea(t);
            if (spacer != null) {
                fa.addChildArea(spacer);
            }
            fa.setBPD(t.getBPD());

            leaderArea = fa;
        } else if (fobj.getLeaderPattern() == EN_USECONTENT) {
            if (fobj.getChildNodes() == null) {
                fobj.getLogger().error("Leader use-content with no content");
                return null;
            }

            // child FOs are assigned to the InlineStackingLM
            fobjIter = null;

            // get breaks then add areas to FilledArea
            FilledArea fa = new FilledArea();

            clm = new ContentLayoutManager(fa, this);
            addChildLM(clm);

            InlineLayoutManager lm;
            lm = new InlineLayoutManager(fobj);
            clm.addChildLM(lm);
            lm.initialize();

            LayoutContext childContext = new LayoutContext(0);
            childContext.setAlignmentContext(context.getAlignmentContext());
            contentList = clm.getNextKnuthElements(childContext, 0);
            int width = clm.getStackingSize();
            Space spacer = null;
            if (fobj.getLeaderPatternWidth().getValue(this) > width) {
                spacer = new Space();
                spacer.setIPD(fobj.getLeaderPatternWidth().getValue(this) - width);
                width = fobj.getLeaderPatternWidth().getValue(this);
            }
            fa.setUnitWidth(width);
            if (spacer != null) {
                fa.addChildArea(spacer);
            }
            leaderArea = fa;
        }
        TraitSetter.setProducerID(leaderArea, fobj.getId());
        return leaderArea;
     }

    /** @see LeafNodeLayoutManager#addAreas(PositionIterator, LayoutContext) */
    public void addAreas(PositionIterator posIter, LayoutContext context) {
        if (fobj.getLeaderPattern() != EN_USECONTENT) {
            // use LeafNodeLayoutManager.addAreas()
            super.addAreas(posIter, context);
        } else {
            addId();

            widthAdjustArea(curArea, context);

            if (commonBorderPaddingBackground != null) {
                // Add border and padding to area
                TraitSetter.setBorderPaddingTraits(curArea,
                                                   commonBorderPaddingBackground,
                                                   false, false, this);
                TraitSetter.addBackground(curArea, commonBorderPaddingBackground, this);
            }

            // add content areas
            KnuthPossPosIter contentIter = new KnuthPossPosIter(contentList, 0, contentList.size());
            clm.addAreas(contentIter, context);

            parentLM.addChildArea(curArea);

            while (posIter.hasNext()) {
                posIter.next();
            }
        }
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#getNextKnuthElements(LayoutContext, int) */
    public LinkedList getNextKnuthElements(LayoutContext context,
                                           int alignment) {
        MinOptMax ipd;
        curArea = get(context);
        KnuthSequence seq = new InlineKnuthSequence();

        if (curArea == null) {
            setFinished(true);
            return null;
        }

        alignmentContext = new AlignmentContext(curArea.getBPD()
                                    , fobj.getAlignmentAdjust()
                                    , fobj.getAlignmentBaseline()
                                    , fobj.getBaselineShift()
                                    , fobj.getDominantBaseline()
                                    , context.getAlignmentContext());

        ipd = getAllocationIPD(context.getRefIPD());
        if (fobj.getLeaderPattern() == EN_USECONTENT && curArea instanceof FilledArea) {
            // If we have user supplied content make it fit if we can
            int unitWidth = ((FilledArea)curArea).getUnitWidth();
            if (ipd.opt < unitWidth && ipd.max >= unitWidth) {
                ipd.opt = unitWidth;
            }
        }

        // create the AreaInfo object to store the computed values
        areaInfo = new AreaInfo((short) 0, ipd, false, context.getAlignmentContext());

        addKnuthElementsForBorderPaddingStart(seq);

        // node is a fo:Leader
        seq.add(new KnuthInlineBox(0, alignmentContext,
                                    new LeafPosition(this, -1), true));
        seq.add(new KnuthPenalty(0, KnuthElement.INFINITE, false,
                                        new LeafPosition(this, -1), true));
        if (alignment == EN_JUSTIFY || alignment == 0) {
            seq.add
                (new KnuthGlue(areaInfo.ipdArea.opt,
                               areaInfo.ipdArea.max - areaInfo.ipdArea.opt,
                               areaInfo.ipdArea.opt - areaInfo.ipdArea.min,
                               new LeafPosition(this, 0), false));
        } else {
            seq.add
                (new KnuthGlue(areaInfo.ipdArea.opt,
                               0,
                               0,
                               new LeafPosition(this, 0), false));
        }
        seq.add(new KnuthInlineBox(0, alignmentContext,
                                    new LeafPosition(this, -1), true));

        addKnuthElementsForBorderPaddingEnd(seq);

        LinkedList returnList = new LinkedList();
        returnList.add(seq);
        setFinished(true);
        return returnList;
    }

    /** @see InlineLevelLayoutManager#hyphenate(Position, HyphContext) */
    public void hyphenate(Position pos, HyphContext hc) {
        // use the AbstractLayoutManager.hyphenate() null implementation
        super.hyphenate(pos, hc);
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

        returnList.add(new KnuthInlineBox(0, areaInfo.alignmentContext,
                                    new LeafPosition(this, -1), true));
        returnList.add(new KnuthPenalty(0, KnuthElement.INFINITE, false,
                                        new LeafPosition(this, -1), true));
        if (alignment == EN_JUSTIFY || alignment == 0) {
            returnList.add
                (new KnuthGlue(areaInfo.ipdArea.opt,
                               areaInfo.ipdArea.max - areaInfo.ipdArea.opt,
                               areaInfo.ipdArea.opt - areaInfo.ipdArea.min,
                               new LeafPosition(this, 0), false));
        } else {
            returnList.add
                (new KnuthGlue(areaInfo.ipdArea.opt,
                               0,
                               0,
                               new LeafPosition(this, 0), false));
        }
        returnList.add(new KnuthInlineBox(0, areaInfo.alignmentContext,
                                    new LeafPosition(this, -1), true));

        addKnuthElementsForBorderPaddingEnd(returnList);

        setFinished(true);
        return returnList;
    }

    /** @see LeafNodeLayoutManager#addId */
    protected void addId() {
        getPSLM().addIDToPage(fobj.getId());
    }

    /**
     * @see com.wisii.fov.datatypes.PercentBaseContext#getBaseLength(int, FObj)
     */
    public int getBaseLength(int lengthBase, FObj fobj) {
        return getParent().getBaseLength(lengthBase, getParent().getFObj());
    }

    /**
     * Returns the IPD of the content area
     * @return the IPD of the content area
     */
    public int getContentAreaIPD() {
        return contentAreaIPD;
    }

    private void setContentAreaIPD(int contentAreaIPD) {
        this.contentAreaIPD = contentAreaIPD;
    }

}
