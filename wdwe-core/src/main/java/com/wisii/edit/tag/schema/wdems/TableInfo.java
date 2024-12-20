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

import java.util.ArrayList;
import java.util.List;
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
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="column" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="attrName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="type" default="varChar">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="integer"/>
 *                       &lt;enumeration value="double"/>
 *                       &lt;enumeration value="float"/>
 *                       &lt;enumeration value="varChar"/>
 *                       &lt;enumeration value="date"/>
 *                       &lt;enumeration value="time"/>
 *                       &lt;enumeration value="timestamp"/>
 *                       &lt;enumeration value="datetime"/>
 *                       &lt;enumeration value="numeric"/>
 *                       &lt;enumeration value="boolean"/>
 *                       &lt;enumeration value="tinyint"/>
 *                       &lt;enumeration value="smalltnt"/>
 *                       &lt;enumeration value="bigint"/>
 *                       &lt;enumeration value="real"/>
 *                       &lt;enumeration value="binary"/>
 *                       &lt;enumeration value="varBinary"/>
 *                       &lt;enumeration value="longVarBinary"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "column"
})
@XmlRootElement(name = "tableInfo")
public class TableInfo {

    protected List<TableInfo.Column> column;

    /**
     * Gets the value of the column property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the column property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TableInfo.Column }
     * 
     * 
     */
    public List<TableInfo.Column> getColumn() {
        if (column == null) {
            column = new ArrayList<TableInfo.Column>();
        }
        return this.column;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="attrName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="type" default="varChar">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="integer"/>
     *             &lt;enumeration value="double"/>
     *             &lt;enumeration value="float"/>
     *             &lt;enumeration value="varChar"/>
     *             &lt;enumeration value="date"/>
     *             &lt;enumeration value="time"/>
     *             &lt;enumeration value="timestamp"/>
     *             &lt;enumeration value="datetime"/>
     *             &lt;enumeration value="numeric"/>
     *             &lt;enumeration value="boolean"/>
     *             &lt;enumeration value="tinyint"/>
     *             &lt;enumeration value="smalltnt"/>
     *             &lt;enumeration value="bigint"/>
     *             &lt;enumeration value="real"/>
     *             &lt;enumeration value="binary"/>
     *             &lt;enumeration value="varBinary"/>
     *             &lt;enumeration value="longVarBinary"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Column {

        @XmlAttribute
        protected String attrName;
        @XmlAttribute
        protected String type;
        @XmlAttribute
        protected String name;

        /**
         * Gets the value of the attrName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAttrName() {
            return attrName;
        }

        /**
         * Sets the value of the attrName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAttrName(String value) {
            this.attrName = value;
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
            if (type == null) {
                return "varChar";
            } else {
                return type;
            }
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

    }

}
