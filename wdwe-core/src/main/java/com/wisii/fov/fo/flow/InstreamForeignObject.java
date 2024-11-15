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
 *//* $Id: InstreamForeignObject.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.awt.geom.Point2D;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.XMLObj;
import org.xml.sax.Locator;

/**
 * The instream-foreign-object flow formatting object.
 * This is an atomic inline object that contains
 * xml data.
 */
public class InstreamForeignObject extends AbstractGraphics {

    // The value of properties relevant for fo:instream-foreign-object.
    // All property values contained in AbstractGraphics
    // End of property values

    //Additional value
    private Point2D intrinsicDimensions;

    /**
     * constructs an instream-foreign-object object (called by Maker).
     *
     * @param parent the parent formatting object
     */
    public InstreamForeignObject(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(getId());
    }

    /**
     * Make sure content model satisfied, if so then tell the
     * FOEventHandler that we are at the end of the flow.
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (childNodes == null || childNodes.size() != 1) {
            missingChildElementError("one (1) non-XSL namespace child");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: one (1) non-XSL namespace child
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI)) {
            invalidChildError(loc, nsURI, localName);
        } else if (childNodes != null) {
            tooManyNodesError(loc, "child element");
        }
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "instream-foreign-object";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_INSTREAM_FOREIGN_OBJECT;
    }

    /**
     * Preloads the image so the intrinsic size is available.
     */
    private void prepareIntrinsicSize() {
        if (intrinsicDimensions == null) {
            XMLObj child = (XMLObj)childNodes.get(0);
            Point2D csize = new Point2D.Float(-1, -1);
            intrinsicDimensions = child.getDimension(csize);
            if (intrinsicDimensions == null) {
                getLogger().error("Intrinsic dimensions of "
                        + " instream-foreign-object could not be determined");
            }
        }
    }

    /**
     * @see com.wisii.fov.fo.flow.AbstractGraphics#getIntrinsicWidth()
     */
    public int getIntrinsicWidth() {
        prepareIntrinsicSize();
        if (intrinsicDimensions != null) {
            return (int)(intrinsicDimensions.getX() * 1000);
        } else {
            return 0;
        }
    }

    /**
     * @see com.wisii.fov.fo.flow.AbstractGraphics#getIntrinsicHeight()
     */
    public int getIntrinsicHeight() {
        prepareIntrinsicSize();
        if (intrinsicDimensions != null) {
            return (int)(intrinsicDimensions.getY() * 1000);
        } else {
            return 0;
        }
    }

    /** @see com.wisii.fov.fo.FONode#addChildNode(com.wisii.fov.fo.FONode) */
    protected void addChildNode(FONode child) throws FOVException {
        super.addChildNode(child);
    }

    /** @return the XMLObj child node of the instream-foreign-object. */
    public XMLObj getChildXMLObj() {
        return (XMLObj) childNodes.get(0);
    }

}
