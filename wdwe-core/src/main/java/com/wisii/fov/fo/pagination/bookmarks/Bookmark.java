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
 *//* $Id $ */

package com.wisii.fov.fo.pagination.bookmarks;

import java.util.ArrayList;
import org.xml.sax.Locator;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonAccessibility;


/**
 * The fo:bookmark formatting object, first introduced in the
 * XSL 1.1 WD.  Prototype version only, subject to change as
 * XSL 1.1 WD evolves.
 */
public class Bookmark extends FObj {
    private BookmarkTitle bookmarkTitle;
    private ArrayList childBookmarks = new ArrayList();

    // The value of properties relevant for this FO
    private CommonAccessibility commonAccessibility;
    private String internalDestination;
    private String externalDestination;
    private boolean bShow = true; // from starting-state property

    /**
     * Create a new bookmark object.
     *
     * @param parent the parent fo node
     */
    public Bookmark(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAccessibility = pList.getAccessibilityProps();
        externalDestination = pList.get(PR_EXTERNAL_DESTINATION).getString();
        internalDestination = pList.get(PR_INTERNAL_DESTINATION).getString();
        bShow = (pList.get(PR_STARTING_STATE).getEnum() == EN_SHOW);

        // per spec, internal takes precedence if both specified
        if (internalDestination.length() > 0) {
            externalDestination = null;
        } else if (externalDestination.length() == 0) {
            // slightly stronger than spec "should be specified"
            attributeError("Missing attribute:  Either external-destination or " +
                "internal-destination must be specified.");
        } else {
            attributeWarning("external-destination property not currently supported");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
        XSL/FOV: (bookmark-title, bookmark*)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
            if (FO_URI.equals(nsURI) && localName.equals("bookmark-title")) {
                if (bookmarkTitle != null) {
                    tooManyNodesError(loc, "fo:bookmark-title");
                }
            } else if (FO_URI.equals(nsURI) && localName.equals("bookmark")) {
                if (bookmarkTitle == null) {
                    nodesOutOfOrderError(loc, "fo:bookmark-title", "fo:bookmark");
                }
            } else {
                invalidChildError(loc, nsURI, localName);
            }
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (bookmarkTitle == null) {
           missingChildElementError("(bookmark-title, bookmark*)");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     */
    protected void addChildNode(FONode obj) {
        if (obj instanceof BookmarkTitle) {
            bookmarkTitle = (BookmarkTitle)obj;
        } else if (obj instanceof Bookmark) {
            childBookmarks.add(obj);
        }
    }

    /**
     * Get the bookmark title for this bookmark
     *
     * @return the bookmark title string or an empty string if not found
     */
    public String getBookmarkTitle() {
        return bookmarkTitle == null ? "" : bookmarkTitle.getTitle();
    }

    public String getInternalDestination() {
        return internalDestination;
    }

    public String getExternalDestination() {
        return externalDestination;
    }

    /**
     * Determines if this fo:bookmark's subitems should be initially displayed
     * or hidden, based on the starting-state property set on this FO.
     *
     * @return true if this bookmark's starting-state is "show", false if "hide".
     */
    public boolean showChildItems() {
        return bShow;
    }

    public ArrayList getChildBookmarks() {
        return childBookmarks;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "bookmark";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_BOOKMARK;
    }
}
