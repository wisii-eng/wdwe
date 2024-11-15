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
 *//* $Id: KeepProperty.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.datatypes.CompoundDatatype;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * Superclass for properties that wrap Keep values
 */
public class KeepProperty extends Property implements CompoundDatatype {
    private Property withinLine;
    private Property withinColumn;
    private Property withinPage;

    /**
     * Inner class for creating instances of KeepProperty
     */
    public static class Maker extends CompoundPropertyMaker {

        /**
         * @param propId the id of the property for which a Maker should be created
         */
        public Maker(int propId) {
            super(propId);
        }

        /**
         * Create a new empty instance of KeepProperty.
         * @return the new instance.
         */
        public Property makeNewProperty() {
            return new KeepProperty();
        }

        /**
         * @see CompoundPropertyMaker#convertProperty
         */
        public Property convertProperty(Property p, PropertyList propertyList, FObj fo)
            throws PropertyException
        {
            if (p instanceof KeepProperty) {
                return p;
            }
            return super.convertProperty(p, propertyList, fo);
        }
    }

    /**
     * @see com.wisii.fov.datatypes.CompoundDatatype#setComponent(int, Property, boolean)
     */
    public void setComponent(int cmpId, Property cmpnValue,
                             boolean bIsDefault) {
        if (cmpId == CP_WITHIN_LINE) {
            setWithinLine(cmpnValue, bIsDefault);
        } else if (cmpId == CP_WITHIN_COLUMN) {
            setWithinColumn(cmpnValue, bIsDefault);
        } else if (cmpId == CP_WITHIN_PAGE) {
            setWithinPage(cmpnValue, bIsDefault);
        }
    }

    /**
     * @see com.wisii.fov.datatypes.CompoundDatatype#getComponent(int)
     */
    public Property getComponent(int cmpId) {
        if (cmpId == CP_WITHIN_LINE) {
            return getWithinLine();
        } else if (cmpId == CP_WITHIN_COLUMN) {
            return getWithinColumn();
        } else if (cmpId == CP_WITHIN_PAGE) {
            return getWithinPage();
        } else {
            return null;
        }
    }

    /**
     * @param withinLine withinLine property to set
     * @param bIsDefault not used (??)
     */
    public void setWithinLine(Property withinLine, boolean bIsDefault) {
        this.withinLine = withinLine;
    }

    /**
     * @param withinColumn withinColumn property to set
     * @param bIsDefault not used (??)
     */
    protected void setWithinColumn(Property withinColumn,
                                   boolean bIsDefault) {
        this.withinColumn = withinColumn;
    }

    /**
     * @param withinPage withinPage property to set
     * @param bIsDefault not used (??)
     */
    public void setWithinPage(Property withinPage, boolean bIsDefault) {
        this.withinPage = withinPage;
    }

    /**
     * @return the withinLine property
     */
    public Property getWithinLine() {
        return this.withinLine;
    }

    /**
     * @return the withinColumn property
     */
    public Property getWithinColumn() {
        return this.withinColumn;
    }

    /**
     * @return the withinPage property
     */
    public Property getWithinPage() {
        return this.withinPage;
    }

    /**
     * Not sure what to do here. There isn't really a meaningful single value.
     * @return String representation
     */
    public String toString() {
        return "Keep[" +
            "withinLine:" + getWithinLine().getObject() +
            ", withinColumn:" + getWithinColumn().getObject() +
            ", withinPage:" + getWithinPage().getObject() + "]";
    }

    /**
     * @return this.keep
     */
    public KeepProperty getKeep() {
        return this;
    }

    /**
     * @return this.keep cast as Object
     */
    public Object getObject() {
        return this;
    }

}
