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
 *//* $Id: BookmarkTree.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination.bookmarks;

// Java
import java.util.ArrayList;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.pagination.Root;

/**
 * The fo:bookmark-tree formatting object, first introduced in the
 * XSL 1.1 WD.  Prototype version only, subject to change as XSL 1.1 WD
 * evolves.
 */
public class BookmarkTree extends FObj {
    private ArrayList bookmarks = new ArrayList();

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public BookmarkTree(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     */
    protected void addChildNode(FONode obj) {
        if (obj instanceof Bookmark) {
            bookmarks.add(obj);
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (bookmarks == null) {
           missingChildElementError("(fo:bookmark+)");
        }
        ((Root) parent).setBookmarkTree(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
        XSL/FOV: (bookmark+)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (!(FO_URI.equals(nsURI) &&
            localName.equals("bookmark"))) {
                invalidChildError(loc, nsURI, localName);
        }
    }

    public ArrayList getBookmarks() {
        return bookmarks;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "bookmark-tree";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_BOOKMARK_TREE;
    }
}
