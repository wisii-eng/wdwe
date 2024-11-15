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
 *//* $Id: SinglePageMasterReference.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// XML
import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * A single-page-master-reference formatting object.
 * This is a reference for a single page. It returns the
 * master name only once until reset.
 */
public class SinglePageMasterReference extends FObj
    implements SubSequenceSpecifier {

    // The value of properties relevant for fo:single-page-master-reference.
    private String masterReference;
    // End of property values

    private static final int FIRST = 0;
    private static final int DONE = 1;

    private int state;

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public SinglePageMasterReference(FONode parent) {
        super(parent);
        this.state = FIRST;
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        masterReference = pList.get(PR_MASTER_REFERENCE).getString();

        if (masterReference == null || masterReference.equals("")) {
            missingPropertyError("master-reference");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        PageSequenceMaster pageSequenceMaster = (PageSequenceMaster) parent;
        pageSequenceMaster.addSubsequenceSpecifier(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: empty
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
       invalidChildError(loc, nsURI, localName);
    }

    /** @see com.wisii.fov.fo.pagination.SubSequenceSpecifier */
    public String getNextPageMasterName(boolean isOddPage,
                                        boolean isFirstPage,
                                        boolean isLastPage,
                                        boolean isEmptyPage) {
        if (this.state == FIRST) {
            this.state = DONE;
            return masterReference;
        } else {
            return null;
        }
    }

    /** @see com.wisii.fov.fo.pagination.SubSequenceSpecifier#reset() */
    public void reset() {
        this.state = FIRST;
    }



    /** @see com.wisii.fov.fo.pagination.SubSequenceSpecifier#goToPrevious() */
    public boolean goToPrevious() {
        if (state == FIRST) {
            return false;
        } else {
            this.state = FIRST;
            return true;
        }
    }

    /** @see com.wisii.fov.fo.pagination.SubSequenceSpecifier#hasPagePositionLast() */
    public boolean hasPagePositionLast() {
        return false;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "single-page-master-reference";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_SINGLE_PAGE_MASTER_REFERENCE;
    }
}

