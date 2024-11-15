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
 *//* $Id: StaticContent.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// XML
import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.ValidationException;

/**
 * Class modelling the fo:static-content object.
 */
public class StaticContent extends Flow {

    /**
     * @param parent FONode that is the parent of this object
     */
    public StaticContent(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode()
     */
    protected void startOfNode() throws FOVException {
        if (getFlowName() == null || getFlowName().equals("")) {
            throw new ValidationException("一个'flow-name'必须的 "
                                   + getName() + ".", locator);
        }
        getFOEventHandler().startFlow(this);
    }

    /**
     * Make sure content model satisfied, if so then tell the
     * FOEventHandler that we are at the end of the flow.
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (childNodes == null && getUserAgent().validateStrictly()) {
            missingChildElementError("(%block;)+");
        }
        getFOEventHandler().endFlow(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: (%block;)+
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (!isBlockItem(nsURI, localName)) {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /** @see com.wisii.fov.fo.FObj#getLocalName() */
    public String getLocalName() {
        return "static-content";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_STATIC_CONTENT;
    }
}
