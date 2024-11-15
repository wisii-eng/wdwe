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

import org.xml.sax.Locator;

import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * The fo:bookmark-title formatting object, first introduced in the
 * XSL 1.1 WD.  Prototype version only, subject to change as XSL 1.1 WD
 * evolves.
 */
public class BookmarkTitle extends FObj {
    private String title = "";

    /**
     * Create a new BookmarkTitle object.
     *
     * @param parent the fo node parent
     */
    public BookmarkTitle(FONode parent) {
        super(parent);
    }

    /**
     * Add the characters to this BookmarkTitle.
     * The text data inside the BookmarkTitle xml element
     * is used for the BookmarkTitle string.
     *
     * @param data the character data
     * @param start the start position in the data array
     * @param end the end position in the character array
     * @param locator location in fo source file.
     */
    protected void addCharacters(char data[], int start, int end,
                                 PropertyList pList,
                                 Locator locator) {
        title += new String(data, start, end - start);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
        XSL/FOV: empty
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
            invalidChildError(loc, nsURI, localName);
    }

    /**
     * Get the title for this BookmarkTitle.
     *
     * @return the bookmark title
     */
    public String getTitle() {
        return title;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "bookmark-title";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_BOOKMARK_TITLE;
    }
}
