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
 */package com.wisii.fov.fo;

import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.DOMImplementation;
import org.xml.sax.Locator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.xmlgraphics.util.Service;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.fo.ElementMapping.Maker;

/**
 * This class keeps track of all configured ElementMapping implementations which are responsible
 * for properly handling all kinds of different XML namespaces.
 */
public class ElementMappingRegistry
{
    /** logging instance */
    protected Log log = LogFactory.getLog(ElementMappingRegistry.class);

    /** Table mapping element names to the makers of objects representing formatting objects.     */
    protected Map fobjTable = new java.util.HashMap();

    /** Map of mapped namespaces and their associated ElementMapping instances.     */
    protected Map namespaces = new java.util.HashMap();

    /**
     * Main constructor. Adds all default element mapping as well as detects ElementMapping through the Service discovery.
     * @param factory the Fov Factory
     */
    public ElementMappingRegistry(FovFactory factory)
    {
        // Add standard element mappings
        setupDefaultMappings();
    }

//add by huangzl
	private void setupDefaultMappings()
	{
		addElementMapping("com.wisii.fov.fo.FOElementMapping");
		addElementMapping("com.wisii.fov.fo.extensions.svg.SVGElementMapping");
		addElementMapping("com.wisii.fov.fo.extensions.svg.BatikExtensionElementMapping");
		addElementMapping("com.wisii.fov.fo.extensions.ExtensionElementMapping");
		addElementMapping("com.wisii.fov.fo.extensions.OldExtensionElementMapping");
		addElementMapping("com.wisii.fov.wisii.WisiiElementMapping");
	}
//add end

    /**
     * Add the element mapping with the given class name.
     * @param mappingClassName the class name representing the element mapping.
     * @throws IllegalArgumentException if there was not such element mapping.
     */
    public void addElementMapping(String mappingClassName) throws IllegalArgumentException
    {
        try
        {
            ElementMapping mapping = (ElementMapping)Class.forName(mappingClassName).newInstance();
            addElementMapping(mapping);
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("找不到" + mappingClassName);
//            throw new IllegalArgumentException("找不到" + mappingClassName);
        }
        catch (InstantiationException e)
        {
            System.err.println("不能示例说明" + mappingClassName);
//            throw new IllegalArgumentException("不能示例说明 " + mappingClassName);
        }
        catch (IllegalAccessException e)
        {
            System.err.println("不能访问" + mappingClassName);
//            throw new IllegalArgumentException("不能访问" + mappingClassName);
        }
        catch (ClassCastException e)
        {
            System.err.println("不是映射元素" + mappingClassName);
//            throw new IllegalArgumentException(mappingClassName + " 不是映射元素");
        }
    }

    /**
     * Add the element mapping.
     * @param mapping the element mapping instance
     */
    public void addElementMapping(ElementMapping mapping)
    {
        this.fobjTable.put(mapping.getNamespaceURI(), mapping.getTable());
        this.namespaces.put(mapping.getNamespaceURI().intern(), mapping);
    }

    /**
     * Finds the Maker used to create node objects of a particular type
     * @param namespaceURI URI for the namespace of the element
     * @param localName name of the Element
     * @param locator the Locator instance for context information
     * @return the ElementMapping.Maker that can create an FO object for this element
     * @throws FOVException if a Maker could not be found for a bound namespace.
     */
    public Maker findFOMaker(String namespaceURI, String localName, Locator locator) throws FOVException
    {
        Map table = (Map)fobjTable.get(namespaceURI);
        Maker fobjMaker = null;
        if (table != null)
        {
            fobjMaker = (ElementMapping.Maker)table.get(localName);
            // try default
            if (fobjMaker == null)
                fobjMaker = (ElementMapping.Maker)table.get(ElementMapping.DEFAULT);
        }

        if (fobjMaker == null)
        {
            if (namespaces.containsKey(namespaceURI.intern()))
            {
                  throw new FOVException(FONode.errorText(locator) + "没有找到定义的映射元素 "
                      + FONode.getNodeString(namespaceURI, localName), locator);
            }
            else
            {
                log.warn("Unknown formatting object " + namespaceURI + "^" + localName);
                fobjMaker = new UnknownXMLObj.Maker(namespaceURI);
            }
        }
        return fobjMaker;
    }

    /**
     * Tries to determine the DOMImplementation that is used to handled a particular namespace.
     * The method may return null for namespaces that don't result in a DOM. It is mostly used
     * in namespaces occurring in foreign objects.
     * @param namespaceURI the namespace URI
     * @return the handling DOMImplementation, or null if not applicable
     */
    public DOMImplementation getDOMImplementationForNamespace(String namespaceURI)
    {
        ElementMapping mapping = (ElementMapping)this.namespaces.get(namespaceURI);
        if (mapping == null)
            return null;
        else
            return mapping.getDOMImplementation();
    }

    /**
     * Indicates whether a namespace is known to FOV.
     * @param namespaceURI the namespace URI
     * @return true if the namespace is known.
     */
    public boolean isKnownNamespace(String namespaceURI)
    {
        return this.namespaces.containsKey(namespaceURI);
    }
}
