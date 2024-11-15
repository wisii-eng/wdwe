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
 *//* $Id: RepeatablePageMasterReference.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// XML
import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.Property;

/**
 * A repeatable-page-master-reference formatting object.
 * This handles a reference with a specified number of repeating
 * instances of the referenced page master (may have no limit).
 */
public class RepeatablePageMasterReference extends FObj
    implements SubSequenceSpecifier {

    // The value of properties relevant for fo:repeatable-page-master-reference.
    private String masterReference;
    private Property maximumRepeats;
    // End of property values

    private static final int INFINITE = -1;

    private int numberConsumed = 0;

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public RepeatablePageMasterReference(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        masterReference = pList.get(PR_MASTER_REFERENCE).getString();
        maximumRepeats = pList.get(PR_MAXIMUM_REPEATS);

        if (masterReference == null || masterReference.equals("")) {
            missingPropertyError("master-reference");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        PageSequenceMaster pageSequenceMaster = (PageSequenceMaster) parent;

        if (masterReference == null) {
            missingPropertyError("master-reference");
        } else {
            pageSequenceMaster.addSubsequenceSpecifier(this);
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: empty
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        invalidChildError(loc, nsURI, localName);
    }

    /**
     * @see com.wisii.fov.fo.pagination.SubSequenceSpecifier
     */
    public String getNextPageMasterName(boolean isOddPage,
                                        boolean isFirstPage,
                                        boolean isLastPage,
                                        boolean isEmptyPage) {
        if (getMaximumRepeats() != INFINITE) {
            if (numberConsumed < getMaximumRepeats()) {
                numberConsumed++;
            } else {
                return null;
            }
        }
        return masterReference;
    }

    /** @return the "maximum-repeats" property. */
    public int getMaximumRepeats() {
        if (maximumRepeats.getEnum() == EN_NO_LIMIT) {
            return INFINITE;
        } else {
            int mr = maximumRepeats.getNumeric().getValue();
            if (mr < 0) {
                getLogger().debug("negative maximum-repeats: "
                        + this.maximumRepeats);
                mr = 0;
            }
            return mr;
        }
    }

    /** @see com.wisii.fov.fo.pagination.SubSequenceSpecifier#reset() */
    public void reset() {
        this.numberConsumed = 0;
    }


    /** @see com.wisii.fov.fo.pagination.SubSequenceSpecifier#goToPrevious() */
    public boolean goToPrevious() {
        if (numberConsumed == 0) {
            return false;
        } else {
            numberConsumed--;
            return true;
        }
    }

    /** @see com.wisii.fov.fo.pagination.SubSequenceSpecifier#hasPagePositionLast() */
    public boolean hasPagePositionLast() {
        return false;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "repeatable-page-master-reference";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_REPEATABLE_PAGE_MASTER_REFERENCE;
    }

}
