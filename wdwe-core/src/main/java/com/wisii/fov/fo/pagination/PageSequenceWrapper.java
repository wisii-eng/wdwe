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
 *//* $Id: PageSequenceWrapper.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * The fo:page-sequence-wrapper formatting object, first introduced
 * in the XSL 1.1 WD.  Prototype version only, subject to change as
 * XSL 1.1 WD evolves.
 */
public class PageSequenceWrapper extends FObj {
    // The value of properties relevant for this FO
    private String id;
    private String indexClass;
    private String indexKey;
    // End of property values

    /**
     * @param parent FONode that is the parent of this object
     */
    public PageSequenceWrapper(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        id = pList.get(PR_ID).getString();
        indexClass = pList.get(PR_INDEX_CLASS).getString();
        indexKey = pList.get(PR_INDEX_KEY).getString();
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
        XSL/FOV: (bookmark+)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (!(FO_URI.equals(nsURI) && (localName.equals("page-sequence") ||
            localName.equals("page-sequence-wrapper")))) {
                invalidChildError(loc, nsURI, localName);
        }
    }

    /** @return the "id" property. */
    public String getId() {
        return id;
    }

    /** @return the "index-class" property. */
    public String getIndexClass() {
        return indexClass;
    }

    /** @return the "index-key" property. */
    public String getIndexKey() {
        return indexKey;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "page-sequence-wrapper";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_PAGE_SEQUENCE_WRAPPER;
    }
}

