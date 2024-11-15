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
 *//* $Id: Declarations.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// Java
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * Declarations formatting object.
 * A declarations formatting object holds a set of color-profiles
 * and optionally additional non-XSL namespace elements.
 * The color-profiles are held in a hashmap for use with color-profile
 * references.
 */
public class Declarations extends FObj {

    private Map colorProfiles = null;

    /**
     * @param parent FONode that is the parent of this object
     */
    public Declarations(FONode parent) {
        super(parent);
        ((Root) parent).setDeclarations(this);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        // No properties defined for fo:declarations
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL 1.0: (color-profile)+ (and non-XSL NS nodes)
     * FOV/XSL 1.1: (color-profile)* (and non-XSL NS nodes)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
                throws ValidationException {
        if (FO_URI.equals(nsURI)) {
            if (!localName.equals("color-profile")) {
                invalidChildError(loc, nsURI, localName);
            }
        } // anything outside of XSL namespace is OK.
    }

    /**
     * At the end of this element sort out the children into
     * a hashmap of color profiles and a list of extension attachments.
     * @see com.wisii.fov.fo.FONode#endOfNode()
     */
    protected void endOfNode() throws FOVException {
        if (childNodes != null) {
            for (Iterator iter = childNodes.iterator(); iter.hasNext();) {
                FONode node = (FONode)iter.next();
                if (node.getName().equals("fo:color-profile")) {
                    ColorProfile cp = (ColorProfile)node;
                    if (!"".equals(cp.getColorProfileName())) {
                        if (colorProfiles == null) {
                            colorProfiles = new java.util.HashMap();
                        }
                        if (colorProfiles.get(cp.getColorProfileName()) != null) {
                            // duplicate names
                            getLogger().warn("Duplicate fo:color-profile profile name : "
                                    + cp.getColorProfileName());
                        }
                        colorProfiles.put(cp.getColorProfileName(), cp);
                    } else {
                        getLogger().warn("color-profile-name required for color profile");
                    }
                } else {
                    getLogger().debug("Ignoring element " + node.getName()
                            + " inside fo:declarations.");
                }
            }
        }
        childNodes = null;
    }

    /**
     * @see com.wisii.fov.fo.FObj#getName()
     */
    public String getLocalName() {
        return "declarations";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_DECLARATIONS;
    }
}
