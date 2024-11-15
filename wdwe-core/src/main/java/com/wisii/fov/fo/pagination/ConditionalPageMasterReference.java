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
 *//* $Id: ConditionalPageMasterReference.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// XML
import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * A conditional-page-master-reference formatting object.
 * This is a reference to a page master with a set of conditions.
 * The conditions must be satisfied for the referenced master to
 * be used.
 * This element is must be the child of a repeatable-page-master-alternatives
 * element.
 */
public class ConditionalPageMasterReference extends FObj {
    // The value of properties relevant for fo:conditional-page-master-reference.
    private String masterReference;
    private int pagePosition;
    private int oddOrEven;
    private int blankOrNotBlank;
    // End of property values

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public ConditionalPageMasterReference(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        masterReference = pList.get(PR_MASTER_REFERENCE).getString();
        pagePosition = pList.get(PR_PAGE_POSITION).getEnum();
        oddOrEven = pList.get(PR_ODD_OR_EVEN).getEnum();
        blankOrNotBlank = pList.get(PR_BLANK_OR_NOT_BLANK).getEnum();

        if (masterReference == null || masterReference.equals("")) {
            missingPropertyError("master-reference");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        getConcreteParent().addConditionalPageMasterReference(this);
    }

    private RepeatablePageMasterAlternatives getConcreteParent() {
        return (RepeatablePageMasterAlternatives) parent;
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
     * Check if the conditions for this reference are met.
     * checks the page number and emptyness to determine if this
     * matches.
     * @param isOddPage True if page number odd
     * @param isFirstPage True if page is first page
     * @param isLastPage True if page is last page
     * @param isBlankPage True if page is blank
     * @return True if the conditions for this reference are met
     */
    protected boolean isValid(boolean isOddPage,
                              boolean isFirstPage,
                              boolean isLastPage,
                              boolean isBlankPage) {
        // page-position
        if (isFirstPage) {
            if (pagePosition == EN_REST) {
                return false;
            } else if (pagePosition == EN_LAST) {
                return false;
            }
        } else if (isLastPage) {
            if (pagePosition == EN_REST) {
                return false;
            } else if (pagePosition == EN_FIRST) {
                return false;
            }
        } else {
            if (pagePosition == EN_FIRST) {
                return false;
            } else if (pagePosition == EN_LAST) {
                return false;
            }
        }

        // odd-or-even
        if (isOddPage) {
            if (oddOrEven == EN_EVEN) {
              return false;
            }
        } else {
            if (oddOrEven == EN_ODD) {
              return false;
            }
        }

        // blank-or-not-blank
        if (isBlankPage) {
            if (blankOrNotBlank == EN_NOT_BLANK) {
                return false;
            }
        } else {
            if (blankOrNotBlank == EN_BLANK) {
                return false;
            }
        }
        return true;
    }

    /** @return the "master-reference" property. */
    public String getMasterReference() {
        return masterReference;
    }

    /** @return the page-position property value */
    public int getPagePosition() {
        return this.pagePosition;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "conditional-page-master-reference";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_CONDITIONAL_PAGE_MASTER_REFERENCE;
    }
}
