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
 *//* $Id: RetrieveMarker.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import java.util.HashMap;
import java.util.Iterator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FOPropertyMapping;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.FObjMixed;
import com.wisii.fov.fo.FOText;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.StaticPropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.expr.PropertyException;
import com.wisii.fov.fo.properties.Property;
import com.wisii.fov.fo.properties.PropertyMaker;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;



/**
 * The retrieve-marker formatting object.
 * This will create a layout manager that will retrieve
 * a marker based on the information.
 */
public class RetrieveMarker extends FObjMixed {
    // The value of properties relevant for fo:retrieve-marker.
    private String retrieveClassName;
    private int retrievePosition;
    private int retrieveBoundary;
    // End of property values

    private PropertyList propertyList;

    /**
     * Create a retrieve marker object.
     *
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public RetrieveMarker(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        if (findAncestor(FO_STATIC_CONTENT) < 0) {
            invalidChildError(locator, FO_URI, "retrieve-marker",
                "An fo:retrieve-marker is permitted only as the " +
                " descendant of an fo:static-content.");
        }

        retrieveClassName = pList.get(PR_RETRIEVE_CLASS_NAME).getString();
        retrievePosition = pList.get(PR_RETRIEVE_POSITION).getEnum();
        retrieveBoundary = pList.get(PR_RETRIEVE_BOUNDARY).getEnum();

        if (retrieveClassName == null || retrieveClassName.equals("")) {
            missingPropertyError("retrieve-class-name");
        }

        propertyList = pList.getParentPropertyList();
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: empty
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
            invalidChildError(loc, nsURI, localName);
    }

    /**
     * @return the "retrieve-class-name" property.
     */
    public String getRetrieveClassName() {
        return retrieveClassName;
    }

    /**
     * @return the "retrieve-position" property (enum value).
     */
    public int getRetrievePosition() {
        return retrievePosition;
    }

    /**
     * @return the "retrieve-boundary" property (enum value).
     */
    public int getRetrieveBoundary() {
        return retrieveBoundary;
    }

    private PropertyList createPropertyListFor(FObj fo, PropertyList parent) {
        return getFOEventHandler().getPropertyListMaker().make(fo, parent);
    }

    private void cloneSingleNode(FONode child, FONode newParent,
                            Marker marker, PropertyList parentPropertyList)
        throws FOVException {

        if (child != null) {
            FONode newChild = child.clone(newParent, true);
            if (child instanceof FObj) {
                Marker.MarkerPropertyList pList;
                PropertyList newPropertyList = createPropertyListFor(
                            (FObj) newChild, parentPropertyList);

                pList = marker.getPropertyListFor(child);
                newChild.processNode(
                        child.getLocalName(),
                        getLocator(),
                        pList,
                        newPropertyList);
                if (newChild.getNameId() == FO_TABLE) {
                    Table t = (Table) child;
                    cloneSubtree(t.getColumns().listIterator(),
                            newChild, marker, newPropertyList);
                    cloneSingleNode(t.getTableHeader(),
                            newChild, marker, newPropertyList);
                    cloneSingleNode(t.getTableFooter(),
                            newChild, marker, newPropertyList);
                }
                cloneSubtree(child.getChildNodes(), newChild,
                        marker, newPropertyList);
                if (newChild instanceof FObjMixed) {
                    handleWhiteSpaceFor((FObjMixed) newChild);
                }
            } else if (child instanceof FOText) {
                FOText ft = (FOText) newChild;
                ft.bind(parentPropertyList);
            }
            addChildTo(newChild, (FObj) newParent);
        }
    }

    /**
     * Clone the FO nodes in the parent iterator,
     * attach the new nodes to the new parent,
     * and map the new nodes to the existing property lists.
     * FOText nodes are also in the new map, with a null value.
     * Clone the subtree by a recursive call to this method.
     * @param parentIter the iterator over the children of the old parent
     * @param newParent the new parent for the cloned nodes
     * @param marker the marker that contains the old property list mapping
     * @param descPLists the map of the new nodes to property lists
     */
    private void cloneSubtree(Iterator parentIter, FONode newParent,
                              Marker marker, PropertyList parentPropertyList)
        throws FOVException {
        if (parentIter != null) {
            FONode child;
            while (parentIter.hasNext()) {
                child = (FONode) parentIter.next();
                cloneSingleNode(child, newParent,
                        marker, parentPropertyList);
            }
        }
    }

    private void cloneFromMarker(Marker marker)
        throws FOVException {
        // clean up remnants from a possible earlier layout
        if (childNodes != null) {
            currentTextNode = null;
            childNodes.removeAll(childNodes);
        }
        cloneSubtree(marker.getChildNodes(), this,
                        marker, propertyList);
    }

    /**
     * Clone the subtree of the given marker
     *
     * @param marker the marker that is to be cloned
     */
    public void bindMarker(Marker marker) {
        if (marker.getChildNodes() != null) {
            try {
                cloneFromMarker(marker);
            } catch (FOVException exc) {
                log.error("fo:retrieve-marker unable to clone "
                        + "subtree of fo:marker (marker-class-name="
                        + marker.getMarkerClassName() + ")", exc);
                return;
            }
        } else if (log.isInfoEnabled()) {
            log.info("Empty marker retrieved...");
        }
        return;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "retrieve-marker";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_RETRIEVE_MARKER;
    }
}
