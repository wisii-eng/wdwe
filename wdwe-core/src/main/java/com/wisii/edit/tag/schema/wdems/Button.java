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
 *///
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.30 at 02:11:32 下午 CST 
//


package com.wisii.edit.tag.schema.wdems;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.wisii.com/wdems}uiCommon"/>
 *       &lt;attGroup ref="{http://www.wisii.com/wdems}commonAttr"/>
 *       &lt;attGroup ref="{http://www.wisii.com/wdems}valAttrGroup"/>
 *       &lt;attribute name="insert" default="after">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="before"/>
 *             &lt;enumeration value="after"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="initValue" default="copy">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="null"/>
 *             &lt;enumeration value="copy"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="add"/>
 *             &lt;enumeration value="delete"/>
 *             &lt;enumeration value="hidden"/>
 *             &lt;enumeration value="conn"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "button")
public class Button {

    @XmlAttribute
    protected String insert;
    @XmlAttribute
    protected String initValue;
    @XmlAttribute(required = true)
    protected String type;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    protected String appearance;
    @XmlAttribute
    protected String width;
    @XmlAttribute
    protected String height;
    @XmlAttribute
    protected String hint;
    @XmlAttribute
    protected String hintType;
    @XmlAttribute
    protected Boolean isReload;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute
    protected String conn;
    @XmlAttribute
    protected String onBlur;
    @XmlAttribute
    protected String onSelected;
    @XmlAttribute
    protected String onKeyPress;
    @XmlAttribute
    protected String onKeyDown;
    @XmlAttribute
    protected String onKeyUp;
    @XmlAttribute
    protected String onEdit;
    @XmlAttribute
    protected String onClick;
    @XmlAttribute
    protected String onResult;
    @XmlAttribute
    protected String nodataxpath;

    /**
     * Gets the value of the insert property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsert() {
        if (insert == null) {
            return "after";
        } else {
            return insert;
        }
    }

    public String getNodataxpath() {
		return nodataxpath;
	}

	public void setNodataxpath(String nodataxpath) {
		this.nodataxpath = nodataxpath;
	}

	/**
     * Sets the value of the insert property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsert(String value) {
        this.insert = value;
    }

    /**
     * Gets the value of the initValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitValue() {
        if (initValue == null) {
            return "copy";
        } else {
            return initValue;
        }
    }

    /**
     * Sets the value of the initValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitValue(String value) {
        this.initValue = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the appearance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppearance() {
        return appearance;
    }

    /**
     * Sets the value of the appearance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppearance(String value) {
        this.appearance = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Gets the value of the hint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHint() {
        return hint;
    }

    /**
     * Sets the value of the hint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHint(String value) {
        this.hint = value;
    }

    /**
     * Gets the value of the hintType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHintType() {
        return hintType;
    }

    /**
     * Sets the value of the hintType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHintType(String value) {
        this.hintType = value;
    }

    /**
     * Gets the value of the isReload property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsReload() {
        if (isReload == null) {
            return false;
        } else {
            return isReload;
        }
    }

    /**
     * Sets the value of the isReload property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsReload(Boolean value) {
        this.isReload = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the conn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConn() {
        return conn;
    }

    /**
     * Sets the value of the conn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConn(String value) {
        this.conn = value;
    }

    /**
     * Gets the value of the onBlur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnBlur() {
        return onBlur;
    }

    /**
     * Sets the value of the onBlur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnBlur(String value) {
        this.onBlur = value;
    }

    /**
     * Gets the value of the onSelected property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnSelected() {
        return onSelected;
    }

    /**
     * Sets the value of the onSelected property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnSelected(String value) {
        this.onSelected = value;
    }

    /**
     * Gets the value of the onKeyPress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnKeyPress() {
        return onKeyPress;
    }

    /**
     * Sets the value of the onKeyPress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnKeyPress(String value) {
        this.onKeyPress = value;
    }

    /**
     * Gets the value of the onKeyDown property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnKeyDown() {
        return onKeyDown;
    }

    /**
     * Sets the value of the onKeyDown property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnKeyDown(String value) {
        this.onKeyDown = value;
    }

    /**
     * Gets the value of the onKeyUp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnKeyUp() {
        return onKeyUp;
    }

    /**
     * Sets the value of the onKeyUp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnKeyUp(String value) {
        this.onKeyUp = value;
    }

    /**
     * Gets the value of the onEdit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnEdit() {
        return onEdit;
    }

    /**
     * Sets the value of the onEdit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnEdit(String value) {
        this.onEdit = value;
    }

    /**
     * Gets the value of the onClick property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnClick() {
        return onClick;
    }

    /**
     * Sets the value of the onClick property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnClick(String value) {
        this.onClick = value;
    }

    /**
     * Gets the value of the onResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnResult() {
        return onResult;
    }

    /**
     * Sets the value of the onResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnResult(String value) {
        this.onResult = value;
    }

}
