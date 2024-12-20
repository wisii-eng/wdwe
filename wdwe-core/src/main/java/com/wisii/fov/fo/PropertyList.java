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
 *//* $Id: PropertyList.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo;

// Java
import org.xml.sax.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.fo.expr.PropertyException;
import com.wisii.fov.fo.properties.CommonAbsolutePosition;
import com.wisii.fov.fo.properties.CommonAccessibility;
import com.wisii.fov.fo.properties.CommonAural;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.properties.CommonFont;
import com.wisii.fov.fo.properties.CommonHyphenation;
import com.wisii.fov.fo.properties.CommonMarginBlock;
import com.wisii.fov.fo.properties.CommonMarginInline;
import com.wisii.fov.fo.properties.CommonRelativePosition;
import com.wisii.fov.fo.properties.CommonTextDecoration;
import com.wisii.fov.fo.properties.Property;
import com.wisii.fov.fo.properties.PropertyMaker;

/**
 * Class containing the collection of properties for a given FObj.
 */
public abstract class PropertyList {

    // writing-mode index
    private int writingMode;

    private static boolean[] inheritableProperty;

    /** reference to the parent FO's propertyList **/
    protected PropertyList parentPropertyList = null;
    private FObj fobj = null;

    private static Log log = LogFactory.getLog(PropertyList.class);

    /**
     * Basic constructor.
     * @param fObjToAttach  the FO this PropertyList should be attached to
     * @param parentPropertyList the PropertyList belonging to the new objects
     * parent
     */
    public PropertyList(FObj fObjToAttach, PropertyList parentPropertyList) {
        this.fobj = fObjToAttach;
        this.parentPropertyList = parentPropertyList;
    }

    /**
     * @return the FObj object to which this propertyList is attached
     */
    public FObj getFObj() {
        return this.fobj;
    }

    /**
     * @return the FObj object attached to the parentPropertyList
     */
    public FObj getParentFObj() {
        if (parentPropertyList != null) {
            return parentPropertyList.getFObj();
        } else {
            return null;
        }
    }

    /**
     * @return the FObj object attached to the parentPropetyList
     */
    public PropertyList getParentPropertyList() {
        return parentPropertyList;
    }

    /**
     * Return the value explicitly specified on this FO.
     * @param propId The id of the property whose value is desired.
     * @return The value if the property is explicitly set or set by
     * a shorthand property, otherwise null.
     * @throws PropertyException ...
     */
    public Property getExplicitOrShorthand(int propId) throws PropertyException {
        /* Handle request for one part of a compound property */
        Property p = getExplicit(propId);
        if (p == null) {
            p = getShorthand(propId);
        }
        return p;
    }

    /**
     * Return the value explicitly specified on this FO.
     * @param propId The ID of the property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
    public abstract Property getExplicit(int propId);

    /**
     * Set an value defined explicitly on this FO.
     * @param propId The ID of the property to set.
     * @param value The value of the property.
     */
    public abstract void putExplicit(int propId, Property value);

    /**
     * Return the value of this property inherited by this FO.
     * Implements the inherited-property-value function.
     * The property must be inheritable!
     * @param propId The ID of the property whose value is desired.
     * @return The inherited value, otherwise null.
     * @throws PropertyException ...
     */
    public Property getInherited(int propId) throws PropertyException {

        if (isInherited(propId)) {
            return getFromParent(propId);
        } else {
            // return the "initial" value
            return makeProperty(propId);
        }
    }

    /**
     * Return the property on the current FlowObject. If it isn't set explicitly,
     * this will try to compute it based on other properties, or if it is
     * inheritable, to return the inherited value. If all else fails, it returns
     * the default value.
     * @param propId The Constants ID of the property whose value is desired.
     * @return the Property corresponding to that name
     * @throws PropertyException ...
     */
    public Property get(int propId) throws PropertyException {
        return get(propId, true, true);
    }

    /**
     * Return the property on the current FlowObject. Depending on the passed flags,
     * this will try to compute it based on other properties, or if it is
     * inheritable, to return the inherited value. If all else fails, it returns
     * the default value.
     * @param propId    the property's id
     * @param bTryInherit   true for inherited properties, or when the inherited
     *                      value is needed
     * @param bTryDefault   true when the default value may be used as a last resort
     * @return the property
     * @throws PropertyException ...
     */
    public Property get(int propId, boolean bTryInherit,
                         boolean bTryDefault) throws PropertyException {

        PropertyMaker propertyMaker = findMaker(propId & Constants.PROPERTY_MASK);
        if (propertyMaker != null) {
            return propertyMaker.get(propId & Constants.COMPOUND_MASK, this,
                                         bTryInherit, bTryDefault);
        }
        return null;
    }

    /**
     * Return the "nearest" specified value for the given property.
     * Implements the from-nearest-specified-value function.
     * @param propId The ID of the property whose value is desired.
     * @return The computed value if the property is explicitly set on some
     * ancestor of the current FO, else the initial value.
     * @throws PropertyException ...
     */
    public Property getNearestSpecified(int propId) throws PropertyException {
        Property p = null;

        for (PropertyList plist = this; p == null && plist != null;
                plist = plist.parentPropertyList) {
            p = plist.getExplicit(propId);
        }

        if (p == null) {
            // If no explicit setting found, return initial (default) value.
            p = makeProperty(propId);
        }
        return p;
    }

    /**
     * Return the value of this property on the parent of this FO.
     * Implements the from-parent function.
     * @param propId The Constants ID of the property whose value is desired.
     * @return The computed value on the parent or the initial value if this
     * FO is the root or is in a different namespace from its parent.
     * @throws PropertyException ...
     */
    public Property getFromParent(int propId) throws PropertyException {
        if (parentPropertyList != null) {
            return parentPropertyList.get(propId);
        } else {
            return makeProperty(propId);
        }
    }

    /**
     * Set writing mode for this FO.
     * Use that from the nearest ancestor, including self, which generates
     * reference areas, or from root FO if no ancestor found.
     * @throws PropertyException ...
     */
    public void setWritingMode() throws PropertyException {
        FObj p = fobj.findNearestAncestorFObj();
        // If this is a reference area or the root, use the property value.
        if (fobj.generatesReferenceAreas() || p == null) {
            writingMode = get(Constants.PR_WRITING_MODE).getEnum();
        } else {
            // Otherwise get the writing mode value from the parent.
            writingMode = getParentPropertyList().getWritingMode();
        }
    }

    /**
     * Return the "writing-mode" property value.
     * @return the "writing-mode" property value.
     */
    public int getWritingMode() {
        return writingMode;
    }


    /**
     * Uses the stored writingMode.
     * @param lrtb the property ID to return under lrtb writingmode.
     * @param rltb the property ID to return under rltb writingmode.
     * @param tbrl the property ID to return under tbrl writingmode.
     * @return one of the property IDs, depending on the writing mode.
     */
    public int getWritingMode(int lrtb, int rltb, int tbrl) {
        switch (writingMode) {
            case Constants.EN_LR_TB: return lrtb;
            case Constants.EN_RL_TB: return rltb;
            case Constants.EN_TB_RL: return tbrl;
            default:
                //nop
        }
        return -1;
    }

    /**
     * Adds the attributes, passed in by the parser to the PropertyList
     *
     * @param attributes Collection of attributes passed to us from the parser.
     * @throws ValidationException if there is an attribute that does not
     *          map to a property id (strict validation only)
     */
    public void addAttributesToList(Attributes attributes)
                    throws ValidationException {
        /*
         * If column-number/number-columns-spanned are specified, then we
         * need them before all others (possible from-table-column() on any
         * other property further in the list...
         */
        String attributeName = "column-number";
        String attributeValue = attributes.getValue(attributeName);
        convertAttributeToProperty(attributes, attributeName,
            attributeValue);
        attributeName = "number-columns-spanned";
        attributeValue = attributes.getValue(attributeName);
        convertAttributeToProperty(attributes, attributeName,
            attributeValue);

        /*
         * If font-size is set on this FO, must set it first, since
         * other attributes specified in terms of "ems" depend on it.
         */
        attributeName = "font";
        attributeValue = attributes.getValue(attributeName);
        convertAttributeToProperty(attributes, attributeName,
                attributeValue);
        if (attributeValue == null) {
            /*
             * font shorthand wasn't specified, so still need to process
             * explicit font-size
             */
            attributeName = "font-size";
            attributeValue = attributes.getValue(attributeName);
            convertAttributeToProperty(attributes, attributeName,
                    attributeValue);
        }

        String attributeNS;
        FovFactory factory = getFObj().getUserAgent().getFactory();
        for (int i = 0; i < attributes.getLength(); i++) {
            /* convert all attributes with the same namespace as the fo element for this fObj */
            attributeNS = attributes.getURI(i);
            attributeName = attributes.getQName(i);
            attributeValue = attributes.getValue(i);
            if (attributeNS == null || attributeNS.length() == 0) {
                convertAttributeToProperty(attributes, attributeName, attributeValue);
            } else if (!factory.isNamespaceIgnored(attributeNS)) {
                if (factory.getElementMappingRegistry().isKnownNamespace(attributeNS)) {
                    getFObj().addForeignAttribute(attributeNS, attributeName, attributeValue);
                } else {
                    handleInvalidProperty(
                            "Error processing foreign attribute: "
                            + attributeNS + "/@" + attributeName, attributeName);
                }
            }
        }
    }

    /**
     * Validates a property name.
     * @param propertyName  the property name to check
     * @return true if the base property name and the subproperty name (if any)
     *           can be correctly mapped to an id
     * @throws ValidationException in case the property name
     *          is invalid for the FO namespace
     */
    protected boolean isValidPropertyName(String propertyName)
                throws ValidationException {

        int propId = FOPropertyMapping.getPropertyId(
                        findBasePropertyName(propertyName));
        int subpropId = FOPropertyMapping.getSubPropertyId(
                        findSubPropertyName(propertyName));

        if (propId == -1
                || (subpropId == -1
                        && findSubPropertyName(propertyName) != null)) {
            StringBuffer errorMessage = new StringBuffer().append(
                        "Invalid property name \'").append(propertyName);
            handleInvalidProperty(errorMessage.toString(), propertyName);
            return false;
        }
        return true;
    }

    /**
     *
     * @param attributes Collection of attributes
     * @param attributeName Attribute name to convert
     * @param attributeValue Attribute value to assign to property
     * @throws ValidationException in case the property name is invalid
     *          for the FO namespace
     */
    private void convertAttributeToProperty(Attributes attributes,
                                            String attributeName,
                                            String attributeValue)
                    throws ValidationException {

        if (attributeValue != null) {

            if (!isValidPropertyName(attributeName)) {
                //will log an error or throw an exception
                return;
            }
            FObj parentFO = fobj.findNearestAncestorFObj();


            /* Handle "compound" properties, ex. space-before.minimum */
            String basePropertyName = findBasePropertyName(attributeName);
            String subPropertyName = findSubPropertyName(attributeName);

            int propId = FOPropertyMapping.getPropertyId(basePropertyName);
            int subpropId = FOPropertyMapping.getSubPropertyId(subPropertyName);

            PropertyMaker propertyMaker = findMaker(propId);
            if (propertyMaker == null) {
                log.warn("No PropertyMaker registered for " + attributeName
                        + ". Ignoring property.");
                return;
            }

            try {
                Property prop = null;
                if (subPropertyName == null) { // base attribute only found
                    /* Do nothing if the base property has already been created.
                     * This is e.g. the case when a compound attribute was
                     * specified before the base attribute; in these cases
                     * the base attribute was already created in
                     * findBaseProperty()
                     */
                    if (getExplicit(propId) != null) {
                        return;
                    }
                    prop = propertyMaker.make(this, attributeValue, parentFO);
                } else { // e.g. "leader-length.maximum"
                    Property baseProperty =
                        findBaseProperty(attributes, parentFO, propId,
                                basePropertyName, propertyMaker);
                    prop = propertyMaker.make(baseProperty, subpropId,
                            this, attributeValue, parentFO);
                }
                if (prop != null) {
                    putExplicit(propId, prop);
                }
            } catch (PropertyException e) {
                log.error("Ignoring property: "
                        + attributeName + "=\"" + attributeValue + "\"");
            }
        }
    }

    private Property findBaseProperty(Attributes attributes,
                                      FObj parentFO,
                                      int propId,
                                      String basePropertyName,
                                      PropertyMaker propertyMaker)
            throws PropertyException {

        /* If the baseProperty has already been created, return it
         * e.g. <fo:leader xxxx="120pt" xxxx.maximum="200pt"... />
         */

        Property baseProperty = getExplicit(propId);

        if (baseProperty != null) {
            return baseProperty;
        }

        /* Otherwise If it is specified later in this list of Attributes, create it now
         * e.g. <fo:leader xxxx.maximum="200pt" xxxx="200pt"... />
         */
        String basePropertyValue = attributes.getValue(basePropertyName);

        if (basePropertyValue != null && propertyMaker != null) {
            baseProperty = propertyMaker.make(this, basePropertyValue,
                                              parentFO);
            return baseProperty;
        }

        return null;  // could not find base property
    }

    /**
     * @param message ...
     * @param propName ...
     * @throws ValidationException ...
     */
    protected void handleInvalidProperty(String message, String propName)
                    throws ValidationException {
        if (!propName.startsWith("xmlns")) {
            if (fobj.getUserAgent().validateStrictly()) {
                fobj.attributeError(message);
            } else {
                log.error(message + " Property ignored.");
            }
        }
    }

    /**
     * Finds the first or base part (up to any period) of an attribute name.
     * For example, if input is "space-before.minimum", should return
     * "space-before".
     * @param attributeName String to be atomized
     * @return the base portion of the attribute
     */
    protected static String findBasePropertyName(String attributeName) {
        int separatorCharIndex = attributeName.indexOf('.');
        String basePropertyName = attributeName;
        if (separatorCharIndex > -1) {
            basePropertyName = attributeName.substring(0, separatorCharIndex);
        }
        return basePropertyName;
    }

    /**
     * Finds the second or sub part (portion past any period) of an attribute
     * name. For example, if input is "space-before.minimum", should return
     * "minimum".
     * @param attributeName String to be atomized
     * @return the sub portion of the attribute
     */
    protected static String findSubPropertyName(String attributeName) {
        int separatorCharIndex = attributeName.indexOf('.');
        String subpropertyName = null;
        if (separatorCharIndex > -1) {
            subpropertyName = attributeName.substring(separatorCharIndex + 1);
        }
        return subpropertyName;
    }

    /**
     * @param propId ID of property
     * @return new Property object
     * @throws PropertyException if there's a problem while processing the property
     */
    private Property getShorthand(int propId) throws PropertyException {
        PropertyMaker propertyMaker = findMaker(propId);

        if (propertyMaker != null) {
            return propertyMaker.getShorthand(this);
        } else {
            //log.error("no Maker for " + propertyName);
            return null;
        }
    }

    /**
     * @param propID ID of property
     * @return new Property object
     * @throws PropertyException if there's a problem while processing the property
     */
    private Property makeProperty(int propId) throws PropertyException {
        PropertyMaker propertyMaker = findMaker(propId);
        if (propertyMaker != null) {
            return propertyMaker.make(this);
        } else {
            //log.error("property " + propertyName
            //                       + " ignored");
        }
        return null;
    }

    /**
     * @param propId ID of property
     * @return isInherited value from the requested Property.Maker
     */
    private boolean isInherited(int propId) {
        if (inheritableProperty == null) {
            inheritableProperty = new boolean[Constants.PROPERTY_COUNT + 1];
            PropertyMaker maker = null;
            for (int prop = 1; prop <= Constants.PROPERTY_COUNT; prop++) {
                maker = findMaker(prop);
                inheritableProperty[prop] = (maker != null && maker.isInherited());
            }
        }

        return inheritableProperty[propId];
    }

    /**
     * @param propId Id of property
     * @return the Property.Maker for this property
     */
    private PropertyMaker findMaker(int propId) {

        if (propId < 1 || propId > Constants.PROPERTY_COUNT) {
            return null;
        } else {
            return FObj.getPropertyMakerFor(propId);
        }
    }

    /**
     * Constructs a BorderAndPadding object.
     * @return a BorderAndPadding object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonBorderPaddingBackground getBorderPaddingBackgroundProps()
                throws PropertyException {
        return new CommonBorderPaddingBackground(this, getFObj());
    }

    /**
     * Constructs a CommonHyphenation object.
     * @return the CommonHyphenation object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonHyphenation getHyphenationProps() throws PropertyException {
        return new CommonHyphenation(this);
    }

    /**
     * Constructs a CommonMarginBlock object.
     * @return the CommonMarginBlock object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonMarginBlock getMarginBlockProps() throws PropertyException {
        return new CommonMarginBlock(this);
    }

    /**
     * Constructs a CommonMarginInline object.
     * @return the CommonMarginInline object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonMarginInline getMarginInlineProps() throws PropertyException {
        return new CommonMarginInline(this);
    }

    /**
     * Constructs a CommonAccessibility object.
     * @return the CommonAccessibility object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonAccessibility getAccessibilityProps() throws PropertyException {
        return new CommonAccessibility(this);
    }

    /**
     * Constructs a CommonAural object.
     * @return the CommonAural object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonAural getAuralProps() throws PropertyException {
        CommonAural props = new CommonAural(this);
        return props;
    }

    /**
     * Constructs a RelativePositionProps objects.
     * @return a RelativePositionProps object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonRelativePosition getRelativePositionProps() throws PropertyException {
        return new CommonRelativePosition(this);
    }

    /**
     * Constructs a CommonAbsolutePosition object.
     * @return the CommonAbsolutePosition object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonAbsolutePosition getAbsolutePositionProps() throws PropertyException {
        return new CommonAbsolutePosition(this);
    }


    /**
     * Constructs a CommonFont object.
     * @return A CommonFont object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonFont getFontProps() throws PropertyException {
        return new CommonFont(this);
    }

    /**
     * Constructs a CommonTextDecoration object.
     * @return a CommonTextDecoration object
     * @throws PropertyException if there's a problem while processing the properties
     */
    public CommonTextDecoration getTextDecorationProps() throws PropertyException {
        return CommonTextDecoration.createFromPropertyList(this);
    }
}

