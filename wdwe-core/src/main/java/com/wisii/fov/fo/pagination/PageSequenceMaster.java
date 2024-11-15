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
 *//* $Id: PageSequenceMaster.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// Java
import java.util.List;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * The page-sequence-master formatting object.
 * This class handles a list of subsequence specifiers
 * which are simple or complex references to page-masters.
 */
public class PageSequenceMaster extends FObj {
    // The value of properties relevant for fo:page-sequence-master.
    private String masterName;
    // End of property values

    private LayoutMasterSet layoutMasterSet;
    private List subSequenceSpecifiers;
    private SubSequenceSpecifier currentSubSequence;
    private int currentSubSequenceNumber;

    // The terminology may be confusing. A 'page-sequence-master' consists
    // of a sequence of what the XSL spec refers to as
    // 'sub-sequence-specifiers'. These are, in fact, simple or complex
    // references to page-masters. So the methods use the former
    // terminology ('sub-sequence-specifiers', or SSS),
    // but the actual FO's are MasterReferences.

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public PageSequenceMaster(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        masterName = pList.get(PR_MASTER_NAME).getString();

        if (masterName == null || masterName.equals("")) {
            missingPropertyError("master-name");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode()
     */
    protected void startOfNode() throws FOVException {
        subSequenceSpecifiers = new java.util.ArrayList();
        layoutMasterSet = parent.getRoot().getLayoutMasterSet();
        layoutMasterSet.addPageSequenceMaster(masterName, this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode()
     */
    protected void endOfNode() throws FOVException {
        if (childNodes == null) {
            missingChildElementError("(single-page-master-reference|"
                    + "repeatable-page-master-reference|repeatable-page-master-alternatives)+");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL/FOV: (single-page-master-reference|repeatable-page-master-reference|
     *     repeatable-page-master-alternatives)+
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI)) {
            if (!localName.equals("single-page-master-reference")
                && !localName.equals("repeatable-page-master-reference")
                && !localName.equals("repeatable-page-master-alternatives")) {
                    invalidChildError(loc, nsURI, localName);
            }
        } else {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * Adds a new suqsequence specifier to the page sequence master.
     * @param pageMasterReference the subsequence to add
     */
    protected void addSubsequenceSpecifier(SubSequenceSpecifier pageMasterReference) {
        subSequenceSpecifiers.add(pageMasterReference);
    }

    /**
     * Returns the next subsequence specifier
     * @return a subsequence specifier
     */
    private SubSequenceSpecifier getNextSubSequence() {
        currentSubSequenceNumber++;
        if (currentSubSequenceNumber >= 0
                && currentSubSequenceNumber < subSequenceSpecifiers.size()) {
            return (SubSequenceSpecifier)subSequenceSpecifiers
              .get(currentSubSequenceNumber);
        }
        return null;
    }

    /**
     * Resets the subsequence specifiers subsystem.
     */
    public void reset() {
        currentSubSequenceNumber = -1;
        currentSubSequence = null;
        for (int i = 0; i < subSequenceSpecifiers.size(); i++) {
            ((SubSequenceSpecifier)subSequenceSpecifiers.get(i)).reset();
        }
    }

    /**
     * Used to set the "cursor position" for the page masters to the previous item.
     * @return true if there is a previous item, false if the current one was the first one.
     */
    public boolean goToPreviousSimplePageMaster() {
        if (currentSubSequence != null) {
            boolean success = currentSubSequence.goToPrevious();
            if (!success) {
                if (currentSubSequenceNumber > 0) {
                    currentSubSequenceNumber--;
                    currentSubSequence = (SubSequenceSpecifier)subSequenceSpecifiers
                        .get(currentSubSequenceNumber);
                } else {
                    currentSubSequence = null;
                }
            }
        }
        return (currentSubSequence != null);
    }

    /** @return true if the page-sequence-master has a page-master with page-position="last" */
    public boolean hasPagePositionLast() {
        if (currentSubSequence != null) {
            return currentSubSequence.hasPagePositionLast();
        } else {
            return false;
        }
    }

    /**
     * Returns the next simple-page-master.
     * @param isOddPage True if the next page number is odd
     * @param isFirstPage True if the next page is the first
     * @param isLastPage True if the next page is the last
     * @param isBlankPage True if the next page is blank
     * @return the requested page master
     * @throws FOVException if there's a problem determining the next page master
     */
    public SimplePageMaster getNextSimplePageMaster(boolean isOddPage,
                                                    boolean isFirstPage,
                                                    boolean isLastPage,
                                                    boolean isBlankPage)
                                                      throws FOVException {
        if (currentSubSequence == null) {
            currentSubSequence = getNextSubSequence();
            if (currentSubSequence == null) {
                throw new FOVException("在page-sequence-master中没有后续 '"
                                       + masterName + "'");
            }
        }
        String pageMasterName = currentSubSequence
            .getNextPageMasterName(isOddPage, isFirstPage, isLastPage, isBlankPage);
        boolean canRecover = true;
        while (pageMasterName == null) {
            SubSequenceSpecifier nextSubSequence = getNextSubSequence();
            if (nextSubSequence == null) {
                if (!canRecover) {
                    throw new FOVException("在 page-sequence-master中没有后续  '"
                                           + masterName
                                           + "', 不能恢复");
                }
                getLogger().warn("在 page-sequence-master中没有后续 '"
                                 + masterName
                                 + "', 使用先前的subsequence");
                currentSubSequence.reset();
                canRecover = false;
            } else {
                currentSubSequence = nextSubSequence;
            }
            pageMasterName = currentSubSequence
                .getNextPageMasterName(isOddPage, isFirstPage, isLastPage, isBlankPage);
        }
        SimplePageMaster pageMaster = this.layoutMasterSet
            .getSimplePageMaster(pageMasterName);
        if (pageMaster == null) {
            throw new FOVException("没有与simple-page-master一致的 '"+ pageMasterName + "' 在 page-sequence-master中 '"
                                   + masterName + "'");
        }
        return pageMaster;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "page-sequence-master";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_PAGE_SEQUENCE_MASTER;
    }
}

