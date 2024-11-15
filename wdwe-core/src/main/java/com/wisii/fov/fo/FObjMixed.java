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
 *//* $Id: FObjMixed.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;

/**
 * Base class for representation of mixed content formatting objects
 * (i.e., those that can contain both child FO's and text nodes/PCDATA).
 * It should not be instantiated directly.
 */
public abstract class FObjMixed extends FObj {

    /** Represents accumulated, pending FO text. See flushText(). */
    protected FOText ft = null;

    /** Used for white-space handling; start CharIterator at node ... */
    protected FONode currentTextNode;

    /**
     * @param parent FONode that is the parent of this object
     */
    protected FObjMixed(FONode parent) {
        super(parent);
    }

    /** @see com.wisii.fov.fo.FONode */
    protected void addCharacters(char[] data, int start, int end,
                                 PropertyList pList,
                                 Locator locator) throws FOVException {
        if (ft == null) {
            ft = new FOText(this);
            ft.setLocator(locator);
            if (!inMarker()) {
                ft.bind(pList);
            }
        }
        ft.addCharacters(data, start, end, null, null);
    }

    /** @see com.wisii.fov.fo.FONode#endOfNode() */
    protected void endOfNode() throws FOVException {
        flushText();
        if (!inMarker()
                || getNameId() == FO_MARKER) {
            getFOEventHandler().whiteSpaceHandler
                .handleWhiteSpace(this, currentTextNode);
        }
        super.endOfNode();
    }

    /**
     * Handles white-space for the node that is passed in,
     * starting at its current text-node
     * (used by RetrieveMarker to trigger 'end-of-node' white-space
     *  handling)
     * @param fobj  the node for which to handle white-space
     */
    protected static void handleWhiteSpaceFor(FObjMixed fobj) {
        fobj.getFOEventHandler().getXMLWhiteSpaceHandler()
            .handleWhiteSpace(fobj, fobj.currentTextNode);
    }

    /**
     * Adds accumulated text as one FOText instance.
     * Makes sure that nested calls to itself do nothing.
     * @throws FOVException if there is a problem during processing
     */
    protected void flushText() throws FOVException {
       if (ft != null) {
            FOText lft = ft;
            ft = null;
            if (getNameId() == FO_BLOCK) {
                lft.createBlockPointers((com.wisii.fov.fo.flow.Block) this);
            } else if (getNameId() != FO_MARKER
                    && getNameId() != FO_TITLE
                    && getNameId() != FO_BOOKMARK_TITLE) {
                FONode fo = parent;
                int foNameId = fo.getNameId();
                while (foNameId != FO_BLOCK
                        && foNameId != FO_MARKER
                        && foNameId != FO_TITLE
                        && foNameId != FO_BOOKMARK_TITLE
                        && foNameId != FO_PAGE_SEQUENCE) {
                    fo = fo.getParent();
                    foNameId = fo.getNameId();
                }
                if (foNameId == FO_BLOCK) {
                    lft.createBlockPointers((com.wisii.fov.fo.flow.Block) fo);
                } else if (foNameId == FO_PAGE_SEQUENCE) {
                    log.error("Could not create block pointers."
                            + " FOText w/o Block ancestor.");
                }
            }
            lft.endOfNode();
            addChildNode(lft);
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     */
    protected void addChildNode(FONode child) throws FOVException {
        flushText();
        if (!inMarker()
                || getNameId() == FO_MARKER) {
            if (child instanceof FOText || child.getNameId() == FO_CHARACTER) {
                if (currentTextNode == null) {
                    currentTextNode = child;
                }
            } else {
                // handle white-space for all text up to here
                getFOEventHandler().whiteSpaceHandler
                    .handleWhiteSpace(this, currentTextNode, child);
                currentTextNode = null;
            }
        }
        super.addChildNode(child);
    }

    /**
     * @return iterator for this object
     */
    public CharIterator charIterator() {
        return new RecursiveCharIterator(this);
    }

}
