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
 */package com.wisii.fov.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.xmlgraphics.util.Service;

/**
 * This class holds references to various XML handlers used by FOV.
 * It also supports automatic discovery of additional XML handlers available through the class path.
 */
public class ContentHandlerFactoryRegistry
{
    /** the logger */
    private static Log log = LogFactory.getLog(ContentHandlerFactoryRegistry.class);

    /** Map from namespace URIs to ContentHandlerFactories */
    private Map factories = new java.util.HashMap();

    /** 默认构造方法：查找所有可用ContentHandler。     */
    public ContentHandlerFactoryRegistry()
    {
        discover();
    }

    /**
     * Add an XML handler. The handler itself is inspected to find out what it supports.
     * @param classname the fully qualified class name
     */
    public void addContentHandlerFactory(String classname)
    {
        try
        {
            ContentHandlerFactory factory = (ContentHandlerFactory)Class.forName(classname).newInstance();
            addContentHandlerFactory(factory);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("找不到: " + classname);
        }
        catch (InstantiationException e)
        {
            throw new IllegalArgumentException(classname + "不能示例说明 "  );
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("不能访问: " + classname);
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException(classname + " 不是一个" + ContentHandlerFactory.class.getName());
        }
    }

    /**
     * Add an ContentHandlerFactory. The instance is inspected to find out what it supports.
     * @param factory the ContentHandlerFactory instance
     */
    public void addContentHandlerFactory(ContentHandlerFactory factory)
    {
        String[] ns = factory.getSupportedNamespaces();
        for (int i = 0; i < ns.length; i++)
        {
            factories.put(ns[i], factory);
        }
    }

    /**
     * Retrieves a ContentHandlerFactory instance of a given namespace URI.
     * @param namespaceURI the namespace to be handled.
     * @return the ContentHandlerFactory or null, if no suitable instance is available.
     */
    public ContentHandlerFactory getFactory(String namespaceURI)
    {
        ContentHandlerFactory factory = (ContentHandlerFactory)factories.get(namespaceURI);
        return factory;
    }

    /** 从 classes\META-INF\services\复件 com.wisii.fov.util.ContentHandlerFactory 文件中读取可用的各ContentHandler类并动态记录它们。    */
    private void discover()
    {
        // add mappings from available services 目前为空。
        Iterator providers = Service.providers(ContentHandlerFactory.class);
        if (providers != null)
        {
            while (providers.hasNext())
            {
                ContentHandlerFactory factory = (ContentHandlerFactory)providers.next();
                try
                {
                    if (log.isDebugEnabled())
                        log.debug("Dynamically adding ContentHandlerFactory: " + factory.getClass().getName());
                    addContentHandlerFactory(factory);
                }
                catch (IllegalArgumentException e)
                {
                    log.error("Error while adding ContentHandlerFactory", e);
                }

            }
        }
    }
}
