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
 *//* $Id: Title.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// XML
import org.xml.sax.Locator;

import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.flow.InlineLevel;

/**
 * Class modelling the fo:title object.
 */
public class Title extends InlineLevel {
    // The value of properties relevant for fo:title.
    // See superclass InlineLevel
    // End of property values

    /**
     * @param parent FONode that is the parent of this object
     */
    public Title(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
        XSL/FOV: (#PCDATA|%inline;)*
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (!isInlineItem(nsURI, localName)) {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * @see com.wisii.fov.fo.FObj#getName()
     */
    public String getLocalName() {
        return "title";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_TITLE;
    }
}

