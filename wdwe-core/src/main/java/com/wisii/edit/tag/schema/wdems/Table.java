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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element ref="{http://www.wisii.com/wdems}include" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tr" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice maxOccurs="unbounded">
 *                   &lt;element name="td" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice maxOccurs="unbounded" minOccurs="0">
 *                             &lt;element ref="{http://www.wisii.com/wdems}button" maxOccurs="unbounded"/>
 *                             &lt;element ref="{http://www.wisii.com/wdems}include" maxOccurs="unbounded" minOccurs="0"/>
 *                             &lt;group ref="{http://www.wisii.com/wdems}uiComponents" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/choice>
 *                           &lt;attribute name="rowspan" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="colspan" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *                 &lt;attribute name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="column" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="row" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "includeOrTr"
})
@XmlRootElement(name = "table")
public class Table {

    @XmlElements({
        @XmlElement(name = "tr", type = Table.Tr.class),
        @XmlElement(name = "include", type = Include.class)
    })
    protected List<Object> includeOrTr;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger column;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger row;

    /**
     * Gets the value of the includeOrTr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeOrTr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeOrTr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Table.Tr }
     * {@link Include }
     * 
     * 
     */
    public List<Object> getIncludeOrTr() {
        if (includeOrTr == null) {
            includeOrTr = new ArrayList<Object>();
        }
        return this.includeOrTr;
    }

    /**
     * Gets the value of the column property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getColumn() {
        return column;
    }

    /**
     * Sets the value of the column property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setColumn(BigInteger value) {
        this.column = value;
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
     * Gets the value of the row property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRow() {
        return row;
    }

    /**
     * Sets the value of the row property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRow(BigInteger value) {
        this.row = value;
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
     *       &lt;choice maxOccurs="unbounded">
     *         &lt;element name="td" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice maxOccurs="unbounded" minOccurs="0">
     *                   &lt;element ref="{http://www.wisii.com/wdems}button" maxOccurs="unbounded"/>
     *                   &lt;element ref="{http://www.wisii.com/wdems}include" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;group ref="{http://www.wisii.com/wdems}uiComponents" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/choice>
     *                 &lt;attribute name="rowspan" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="colspan" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
     *       &lt;attribute name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "td"
    })
    public static class Tr {

        protected List<Table.Tr.Td> td;
        @XmlAttribute
        protected String xpath;
        @XmlAttribute
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger height;

        /**
         * Gets the value of the td property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the td property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTd().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Table.Tr.Td }
         * 
         * 
         */
        public List<Table.Tr.Td> getTd() {
            if (td == null) {
                td = new ArrayList<Table.Tr.Td>();
            }
            return this.td;
        }

        /**
         * Gets the value of the xpath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getXpath() {
            return xpath;
        }

        /**
         * Sets the value of the xpath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setXpath(String value) {
            this.xpath = value;
        }

        /**
         * Gets the value of the height property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getHeight() {
            return height;
        }

        /**
         * Sets the value of the height property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setHeight(BigInteger value) {
            this.height = value;
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
         *       &lt;choice maxOccurs="unbounded" minOccurs="0">
         *         &lt;element ref="{http://www.wisii.com/wdems}button" maxOccurs="unbounded"/>
         *         &lt;element ref="{http://www.wisii.com/wdems}include" maxOccurs="unbounded" minOccurs="0"/>
         *         &lt;group ref="{http://www.wisii.com/wdems}uiComponents" maxOccurs="unbounded" minOccurs="0"/>
         *       &lt;/choice>
         *       &lt;attribute name="rowspan" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *       &lt;attribute name="colspan" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *       &lt;attribute name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        public static class Td {

            @XmlElementRefs({
                @XmlElementRef(name = "group", namespace = "http://www.wisii.com/wdems", type = Group.class),
                @XmlElementRef(name = "button", namespace = "http://www.wisii.com/wdems", type = Button.class),
                @XmlElementRef(name = "input", namespace = "http://www.wisii.com/wdems", type = Input.class),
                @XmlElementRef(name = "date", namespace = "http://www.wisii.com/wdems", type = Date.class),
                @XmlElementRef(name = "select", namespace = "http://www.wisii.com/wdems", type = Select.class),
                @XmlElementRef(name = "include", namespace = "http://www.wisii.com/wdems", type = Include.class)
            })
            @XmlMixed
            protected List<Object> content;
            @XmlAttribute
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger rowspan;
            @XmlAttribute
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger colspan;
            @XmlAttribute
            protected String xpath;
            @XmlAttribute
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger width;

            /**
             * Gets the value of the content property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the content property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getContent().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Group }
             * {@link String }
             * {@link Button }
             * {@link Input }
             * {@link Date }
             * {@link Select }
             * {@link Include }
             * 
             * 
             */
            public List<Object> getContent() {
                if (content == null) {
                    content = new ArrayList<Object>();
                }
                return this.content;
            }

            /**
             * Gets the value of the rowspan property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getRowspan() {
                return rowspan;
            }

            /**
             * Sets the value of the rowspan property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setRowspan(BigInteger value) {
                this.rowspan = value;
            }

            /**
             * Gets the value of the colspan property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getColspan() {
                return colspan;
            }

            /**
             * Sets the value of the colspan property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setColspan(BigInteger value) {
                this.colspan = value;
            }

            /**
             * Gets the value of the xpath property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getXpath() {
                return xpath;
            }

            /**
             * Sets the value of the xpath property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setXpath(String value) {
                this.xpath = value;
            }

            /**
             * Gets the value of the width property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getWidth() {
                return width;
            }

            /**
             * Sets the value of the width property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setWidth(BigInteger value) {
                this.width = value;
            }

        }

    }

}