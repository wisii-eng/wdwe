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
 *//* $Id: Footnote.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAccessibility;

/**
 * Class modelling the fo:footnote object.
 */
public class Footnote extends FObj {
    // The value of properties relevant for fo:footnote.
    private CommonAccessibility commonAccessibility;
    // End of property values

    private Inline footnoteCitation = null;
    private FootnoteBody footnoteBody;

    /**
     * @param parent FONode that is the parent of this object
     */
    public Footnote(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAccessibility = pList.getAccessibilityProps();
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        getFOEventHandler().startFootnote(this);
    }

    /**
     * Make sure content model satisfied, if so then tell the
     * FOEventHandler that we are at the end of the flow.
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        super.endOfNode();
        if (footnoteCitation == null || footnoteBody == null) {
            missingChildElementError("(inline,footnote-body)");
        }
        getFOEventHandler().endFootnote(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: (inline,footnote-body)
     * @todo implement additional constraint: A fo:footnote is not permitted
     *      to have a fo:float, fo:footnote, or fo:marker as a descendant.
     * @todo implement additional constraint: A fo:footnote is not
     *      permitted to have as a descendant a fo:block-container that
     *      generates an absolutely positioned area.
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
            if (FO_URI.equals(nsURI) && localName.equals("inline")) {
                if (footnoteCitation != null) {
                    tooManyNodesError(loc, "fo:inline");
                }
            } else if (FO_URI.equals(nsURI) && localName.equals("footnote-body")) {
                if (footnoteCitation == null) {
                    nodesOutOfOrderError(loc, "fo:inline", "fo:footnote-body");
                } else if (footnoteBody != null) {
                    tooManyNodesError(loc, "fo:footnote-body");
                }
            } else {
                invalidChildError(loc, nsURI, localName);
            }
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     */
    public void addChildNode(FONode child) {
        if (((FObj)child).getNameId() == FO_INLINE) {
            footnoteCitation = (Inline) child;
        } else if (((FObj)child).getNameId() == FO_FOOTNOTE_BODY) {
            footnoteBody = (FootnoteBody) child;
        }
    }

    /**
     * Public accessor for inline FO
     * @return the Inline child
     */
    public Inline getFootnoteCitation() {
        return footnoteCitation;
    }

    /**
     * Public accessor for footnote-body FO
     * @return the FootnoteBody child
     */
    public FootnoteBody getFootnoteBody() {
        return footnoteBody;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "footnote";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_FOOTNOTE;
    }
}

