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
 *//* $Id: CharacterLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.fo.flow.Character;
import com.wisii.fov.fonts.Font;
import com.wisii.fov.layoutmgr.InlineKnuthSequence;
import com.wisii.fov.layoutmgr.KnuthElement;
import com.wisii.fov.layoutmgr.KnuthGlue;
import com.wisii.fov.layoutmgr.KnuthPenalty;
import com.wisii.fov.layoutmgr.KnuthSequence;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.LeafPosition;
import com.wisii.fov.layoutmgr.Position;
import com.wisii.fov.layoutmgr.TraitSetter;
import com.wisii.fov.area.Trait;
import com.wisii.fov.traits.MinOptMax;
import com.wisii.fov.traits.SpaceVal;
import com.wisii.fov.util.CharUtilities;

import java.util.List;
import java.util.LinkedList;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.layoutmgr.inline.AlignmentContext;

/**
 * LayoutManager for the fo:character formatting object
 */
public class CharacterLayoutManager extends LeafNodeLayoutManager {
    private Character fobj;
    private MinOptMax letterSpaceIPD;
    private int hyphIPD;
    private Font font;
    private CommonBorderPaddingBackground borderProps = null;

    /**
     * Constructor
     *
     * @param node the fo:character formatting object
     */
    public CharacterLayoutManager(Character node) {
        // @todo better null checking of node
        super(node);
        fobj = node;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#initialize */
    public void initialize() {
        font = fobj.getCommonFont().getFontState(fobj.getFOEventHandler().getFontInfo(), this);
        SpaceVal ls = SpaceVal.makeLetterSpacing(fobj.getLetterSpacing());
        letterSpaceIPD = ls.getSpace();
        hyphIPD = font.getCharWidth(fobj.getCommonHyphenation().hyphenationCharacter);
        borderProps = fobj.getCommonBorderPaddingBackground();
        setCommonBorderPaddingBackground(borderProps);
        com.wisii.fov.area.inline.TextArea chArea = getCharacterInlineArea(fobj);
        chArea.setBaselineOffset(font.getAscender());
        setCurrentArea(chArea);
    }

    private com.wisii.fov.area.inline.TextArea getCharacterInlineArea(Character node) {
        com.wisii.fov.area.inline.TextArea text
            = new com.wisii.fov.area.inline.TextArea();
        char ch = node.getCharacter();
        if (CharUtilities.isAnySpace(ch)) {
            text.addSpace(ch, 0, CharUtilities.isAdjustableSpace(ch));
        } else {
            text.addWord(String.valueOf(ch), 0);
        }
        TraitSetter.setProducerID(text, node.getId());
        TraitSetter.addTextDecoration(text, fobj.getTextDecoration());
        return text;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#getNextKnuthElements(LayoutContext, int) */
    public LinkedList getNextKnuthElements(LayoutContext context, int alignment) {
        MinOptMax ipd;
        curArea = get(context);
        KnuthSequence seq = new InlineKnuthSequence();

        if (curArea == null) {
            setFinished(true);
            return null;
        }

        ipd = new MinOptMax(font.getCharWidth(fobj.getCharacter()));

        curArea.setIPD(ipd.opt);
        curArea.setBPD(font.getAscender() - font.getDescender());

        TraitSetter.addFontTraits(curArea, font);
        curArea.addTrait(Trait.COLOR, fobj.getColor());

        // TODO: may need some special handling for fo:character
        alignmentContext = new AlignmentContext(font
                                    , font.getFontSize()
                                    , fobj.getAlignmentAdjust()
                                    , fobj.getAlignmentBaseline()
                                    , fobj.getBaselineShift()
                                    , fobj.getDominantBaseline()
                                    , context.getAlignmentContext());

        addKnuthElementsForBorderPaddingStart(seq);

        // create the AreaInfo object to store the computed values
        areaInfo = new AreaInfo((short) 0, ipd, false, alignmentContext);

        // node is a fo:Character
        if (letterSpaceIPD.min == letterSpaceIPD.max) {
            // constant letter space, only return a box
            seq.add(new KnuthInlineBox(areaInfo.ipdArea.opt, areaInfo.alignmentContext,
                                        notifyPos(new LeafPosition(this, 0)), false));
        } else {
            // adjustable letter space, return a sequence of elements;
            // at the moment the character is supposed to have no letter spaces,
            // but returning this sequence allows us to change only one element
            // if addALetterSpaceTo() is called
            seq.add(new KnuthInlineBox(areaInfo.ipdArea.opt, areaInfo.alignmentContext,
                                        notifyPos(new LeafPosition(this, 0)), false));
            seq.add(new KnuthPenalty(0, KnuthElement.INFINITE, false,
                                            new LeafPosition(this, -1), true));
            seq.add(new KnuthGlue(0, 0, 0,
                                         new LeafPosition(this, -1), true));
            seq.add(new KnuthInlineBox(0, null,
                                        notifyPos(new LeafPosition(this, -1)), true));
        }

        addKnuthElementsForBorderPaddingEnd(seq);

        LinkedList returnList = new LinkedList();
        returnList.add(seq);
        setFinished(true);
        return returnList;
    }

    /** @see InlineLevelLayoutManager#getWordChars(StringBuffer, Position) */
    public void getWordChars(StringBuffer sbChars, Position bp) {
        sbChars.append
            (((com.wisii.fov.area.inline.TextArea) curArea).getText());
    }

    /** @see InlineLevelLayoutManager#hyphenate(Position, HyphContext) */
    public void hyphenate(Position pos, HyphContext hc) {
        if (hc.getNextHyphPoint() == 1) {
            // the character ends a syllable
            areaInfo.bHyphenated = true;
            isSomethingChanged = true;
        } else {
            // hc.getNextHyphPoint() returned -1 (no more hyphenation points)
            // or a number > 1;
            // the character does not end a syllable
        }
        hc.updateOffset(1);
    }

    /** @see InlineLevelLayoutManager#applyChanges(List) */
    public boolean applyChanges(List oldList) {
        setFinished(false);
        if (isSomethingChanged) {
            // there is nothing to do,
            // possible changes have already been applied
            // in the hyphenate() method
            return true;
        } else {
            return false;
        }
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#getChangedKnuthElements(List, int) */
    public LinkedList getChangedKnuthElements(List oldList, int alignment) {
        if (isFinished()) {
            return null;
        }

        LinkedList returnList = new LinkedList();

        addKnuthElementsForBorderPaddingStart(returnList);

        if (letterSpaceIPD.min == letterSpaceIPD.max
            || areaInfo.iLScount == 0) {
            // constant letter space, or no letter space
            returnList.add(new KnuthInlineBox(areaInfo.ipdArea.opt,
                                        areaInfo.alignmentContext,
                                        notifyPos(new LeafPosition(this, 0)), false));
            if (areaInfo.bHyphenated) {
                returnList.add
                    (new KnuthPenalty(hyphIPD, KnuthPenalty.FLAGGED_PENALTY, true,
                                      new LeafPosition(this, -1), false));
            }
        } else {
            // adjustable letter space
            returnList.add
                (new KnuthInlineBox(areaInfo.ipdArea.opt
                              - areaInfo.iLScount * letterSpaceIPD.opt,
                              areaInfo.alignmentContext,
                              notifyPos(new LeafPosition(this, 0)), false));
            returnList.add(new KnuthPenalty(0, KnuthElement.INFINITE, false,
                                            new LeafPosition(this, -1), true));
            returnList.add
                (new KnuthGlue(areaInfo.iLScount * letterSpaceIPD.opt,
                               areaInfo.iLScount * letterSpaceIPD.max - letterSpaceIPD.opt,
                               areaInfo.iLScount * letterSpaceIPD.opt - letterSpaceIPD.min,
                               new LeafPosition(this, -1), true));
            returnList.add(new KnuthInlineBox(0, null,
                                        notifyPos(new LeafPosition(this, -1)), true));
            if (areaInfo.bHyphenated) {
                returnList.add
                    (new KnuthPenalty(hyphIPD, KnuthPenalty.FLAGGED_PENALTY, true,
                                      new LeafPosition(this, -1), false));
            }
        }

        addKnuthElementsForBorderPaddingEnd(returnList);

        setFinished(true);
        return returnList;
    }

    /** @see LeafNodeLayoutManager#addId */
    protected void addId() {
        getPSLM().addIDToPage(fobj.getId());
    }

}

