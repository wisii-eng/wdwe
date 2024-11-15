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
 */package com.wisii.fov.layoutmgr.inline;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.wisii.fov.fo.flow.Footnote;
import com.wisii.fov.layoutmgr.AbstractLayoutManager;
import com.wisii.fov.layoutmgr.FootnoteBodyLayoutManager;
import com.wisii.fov.layoutmgr.InlineKnuthSequence;
import com.wisii.fov.layoutmgr.KnuthElement;
import com.wisii.fov.layoutmgr.KnuthSequence;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.Position;

/** Layout manager for fo:footnote. */
public class FootnoteLayoutManager extends AbstractLayoutManager implements InlineLevelLayoutManager
{
    private Footnote footnote;
    private InlineStackingLayoutManager citationLM;
    private FootnoteBodyLayoutManager bodyLM;
    /** Represents the footnote citation **/
    private KnuthElement forcedAnchor;

    /**
     * Create a new footnote layout manager.
     * @param node footnote to create the layout manager for
     */
    public FootnoteLayoutManager(Footnote node) {
        super(node);
        footnote = node;
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#initialize() */
    public void initialize() {
        // create an InlineStackingLM handling the fo:inline child of fo:footnote
        citationLM = new InlineLayoutManager(footnote.getFootnoteCitation());

        // create a FootnoteBodyLM handling the fo:footnote-body child of fo:footnote
        bodyLM = new FootnoteBodyLayoutManager(footnote.getFootnoteBody());
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager */
    public LinkedList getNextKnuthElements(LayoutContext context,
                                           int alignment) {
        // this is the only method that must be implemented:
        // all other methods will never be called, as the returned elements
        // contain Positions created by the citationLM, so its methods will
        // be called instead

        // set the citationLM parent to be this LM's parent
        citationLM.setParent(getParent());
        citationLM.initialize();
        bodyLM.setParent(this);
        bodyLM.initialize();

        // get Knuth elements representing the footnote citation
        LinkedList returnedList = new LinkedList();
        while (!citationLM.isFinished()) {
            LinkedList partialList = citationLM.getNextKnuthElements(context, alignment);
            if (partialList != null) {
                returnedList.addAll(partialList);
            }
        }
        if (returnedList.size() == 0) {
            //Inline part of the footnote is empty. Need to send back an auxiliary
            //zero-width, zero-height inline box so the footnote gets painted.
            KnuthSequence seq = new InlineKnuthSequence();
            //Need to use an aux. box, otherwise, the line height can't be forced to zero height.
            forcedAnchor = new KnuthInlineBox(0, null, null, true);
            seq.add(forcedAnchor);
            returnedList.add(seq);
        }
        setFinished(true);

        addAnchor(returnedList);

        return returnedList;
    }

    private void addAnchor(LinkedList citationList) {
        // find the last box in the sequence, and add a reference
        // to the FootnoteBodyLM
        KnuthInlineBox lastBox = null;
        ListIterator citationIterator = citationList.listIterator(citationList.size());
        while (citationIterator.hasPrevious() && lastBox == null) {
            Object obj = citationIterator.previous();
            if (obj instanceof KnuthElement) {
                KnuthElement element = (KnuthElement)obj;
                if (element instanceof KnuthInlineBox) {
                    lastBox = (KnuthInlineBox) element;
                }
            } else {
                KnuthSequence seq = (KnuthSequence)obj;
                ListIterator nestedIterator = seq.listIterator(seq.size());
                while (nestedIterator.hasPrevious() && lastBox == null) {
                    KnuthElement element = (KnuthElement)nestedIterator.previous();
                    if (element instanceof KnuthInlineBox && !element.isAuxiliary()
                            || element == forcedAnchor) {
                        lastBox = (KnuthInlineBox) element;
                    }
                }
            }
        }
        if (lastBox != null) {
            lastBox.setFootnoteBodyLM(bodyLM);
        } else {
            //throw new IllegalStateException("No anchor box was found for a footnote.");
        }
    }

    /** @see com.wisii.fov.layoutmgr.inline.InlineLevelLayoutManager */
    public List addALetterSpaceTo(List oldList) {
        log.warn("null implementation of addALetterSpaceTo() called!");
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

    /** @see com.wisii.fov.layoutmgr.inline.InlineLevelLayoutManager */
    public void getWordChars(StringBuffer sbChars, Position pos) {
        log.warn("null implementation of getWordChars() called!");
    }

    /** @see com.wisii.fov.layoutmgr.inline.InlineLevelLayoutManager */
    public void hyphenate(Position pos, HyphContext hc) {
        log.warn("null implementation of hyphenate called!");
    }

    /** @see com.wisii.fov.layoutmgr.inline.InlineLevelLayoutManager */
    public boolean applyChanges(List oldList) {
        log.warn("null implementation of applyChanges() called!");
        return false;
    }

    /**
     * @see com.wisii.fov.layoutmgr.LayoutManager#getChangedKnuthElements(java.util.List, int)
     */
    public LinkedList getChangedKnuthElements(List oldList,
                                              int alignment) {
        log.warn("null implementation of getChangeKnuthElement() called!");
        return null;
    }
}
