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

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FormattingResults;
import com.wisii.fov.area.AreaTreeHandler;
import com.wisii.fov.fo.ElementMapping.Maker;
import com.wisii.fov.fo.pagination.Root;
import com.wisii.fov.fo.properties.Property;
import com.wisii.fov.image.ImageFactory;
import com.wisii.fov.util.ContentHandlerFactory;
import com.wisii.fov.util.ContentHandlerFactory.ObjectBuiltListener;
import com.wisii.fov.util.ContentHandlerFactory.ObjectSource;

/**
 * SAX Handler that passes parsed data to the various FO objects, where they can
 * be used either to build an FO Tree, or used by Structure Renderers to build
 * other data structures.
 */
public class FOTreeBuilder extends DefaultHandler
{
    /** logging instance */
    protected Log log = LogFactory.getLog(FOTreeBuilder.class);

    /** The registry for ElementMapping instances */
    protected ElementMappingRegistry elementMappingRegistry;

    /** The root of the formatting object tree */
    protected Root rootFObj = null;

    /** Main DefaultHandler that handles the FO namespace. */
    protected MainFOHandler mainFOHandler;

    /** Current delegate ContentHandler to receive the SAX events */
    protected ContentHandler delegate;

    /**
     * The class that handles formatting and rendering to a stream
     * (mark-fov@inomial.com)
     */
    private FOEventHandler foEventHandler;

    /** The SAX locator object managing the line and column counters */
    private Locator locator;

    /** The user agent for this processing run. */
    private FOUserAgent userAgent;

    private boolean used = false;

    private int depth;


    /***  fields add by zkl.  ***/

    private Map ta_conversionMap;

    private Map tableinfoMap;

    private ExtendFOHandler exHander;

    private long times; // 执行时刻（毫秒）
    /***  fields add by zkl, end.  ***/

    /**
     * FOTreeBuilder constructor
     *
     * @param outputFormat
     *            the MIME type of the output format to use (ex.
     *            "application/pdf").
     * @param foUserAgent
     *            in effect for this process
     * @param stream
     *            OutputStream to direct results
     * @throws FOVException
     *             if the FOTreeBuilder cannot be properly created
     */
    public FOTreeBuilder(String outputFormat, FOUserAgent foUserAgent,
                         OutputStream stream) throws FOVException
    {
        this.userAgent = foUserAgent;
        this.elementMappingRegistry = userAgent.getFactory()
                                      .getElementMappingRegistry();
        // This creates either an AreaTreeHandler and ultimately a Renderer, or
        // one of the RTF-, MIF- etc. Handlers.
        foEventHandler = foUserAgent.getRendererFactory().createFOEventHandler(
            foUserAgent, outputFormat, stream);
        foEventHandler.setPropertyListMaker(new PropertyListMaker()
        {
            public PropertyList make(FObj fobj, PropertyList parentPropertyList)
            {
                return new StaticPropertyList(fobj, parentPropertyList);
            }
        });
    }

    /**
     * This method enables to reduce memory consumption of the FO tree slightly.
     * When it returns true no Locator is passed to the FO tree nodes which
     * would copy the information into a SAX LocatorImpl instance.
     *
     * @return true if no context information should be stored on each node in
     *         the FO tree.
     */
    protected boolean isLocatorDisabled()
    {
        // TODO make this configurable through the FOUserAgent so people can
        // optimize memory consumption.
        return false;
    }

    /**
     * SAX Handler for locator
     *
     * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
     */
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }

    /** @return a Locator instance if it is available and not disabled */
    protected Locator getEffectiveLocator()
    {
        return(isLocatorDisabled() ? null : this.locator);
    }

    /**
     * SAX Handler for characters
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] data, int start, int length) throws SAXException
    {
    	
        delegate.characters(data, start, length);
    }

    /**
     * SAX Handler for the start of the document
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException
    {
        if(SystemUtil.PRINT_RUN_TIME)
        {
            times = System.currentTimeMillis();
            System.err.println("TIME_startDocument方法调用开始。时间（毫秒）：" + times);
        }

        if(used)
        {
            throw new IllegalStateException("FOTreeBuilder(和这个FOV类)实例不能重复使用."
                                            + " 请提供一个新的实例.");
        }
        used = true;
        rootFObj = null; // allows FOTreeBuilder to be reused
        if(log.isDebugEnabled())
        {
            log.debug("Building formatting object tree");
        }
        foEventHandler.startDocument();
        this.mainFOHandler = new MainFOHandler();
        this.exHander = new ExtendFOHandler(); // add by zkl,2007-04-16.
        this.mainFOHandler.startDocument();
        this.delegate = this.mainFOHandler;
    }

    /**
     * SAX Handler for the end of the document
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
        this.delegate.endDocument();
        this.exHander.endDocument(); // add by zkl.04-18.
        rootFObj = null;
        if(log.isDebugEnabled())
        {
            log.debug("Parsing of document complete");
        }
        foEventHandler.endDocument();

        // Notify the image factory that this user agent has expired.
        ImageFactory imageFactory = userAgent.getFactory().getImageFactory();
        imageFactory.removeContext(this.userAgent);

    }


    /**
     * SAX Handler for the start of an element
     *
     * @see org.xml.sax.ContentHandler#startElement(String, String, String,
     *      Attributes)
     */
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes attlist) throws SAXException
    {
        this.depth++;

        if(SystemUtil.PRINT_RUN_TIME && "page-sequence".equals(localName))
        {
            times = System.currentTimeMillis();
//            System.err.println("TIME_endElement(PageSequence),创建FONode开始。时间（毫秒）：" + times);
        }
        /*
         * 当fo文件中的元素名称不以“fov：”开头时，用this$ExtendFOHandler中的
         * 逻辑来处理标签。
         * mod by zkl.
         */
        if(rawName.startsWith("fov:"))
        {
            exHander.startElement(namespaceURI, localName, rawName, attlist);
        }
        else
        {

            delegate.startElement(namespaceURI, localName, rawName, attlist);
        }
    }

    /**
     * SAX Handler for the end of an element
     *
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String uri, String localName, String rawName) throws SAXException
    {

        if(SystemUtil.PRINT_RUN_TIME && "page-sequence".equals(localName))
        {
            long timesEnd = System.currentTimeMillis();
//            System.err.println("TIME_endElement(PageSequence),创建FONode结束。时间（毫秒）：" + timesEnd);
            System.err.println("TIME_endElement(PageSequence),创建FONode。消耗时间（毫秒）：" + (timesEnd - times));
        }

        if(rawName.startsWith("fov:"))
        { //add by zkl.用this$ExtendFOHandler处理。
            exHander.endElement(uri, localName, rawName);
        }
        else
        {
            this.delegate.endElement(uri, localName, rawName);
            this.depth--;
            if(depth == 0)
            {
                if(delegate != mainFOHandler)
                {
                    // Return from sub-handler back to main handler
                    delegate.endDocument();
                    delegate = mainFOHandler;
                    delegate.endElement(uri, localName, rawName);
                }
            }
        }
    }

    /**
     * Finds the Maker used to create node objects of a particular type
     *
     * @param namespaceURI
     *            URI for the namespace of the element
     * @param localName
     *            name of the Element
     * @return the ElementMapping.Maker that can create an FO object for this
     *         element
     * @throws FOVException
     *             if a Maker could not be found for a bound namespace.
     */
    private Maker findFOMaker(String namespaceURI, String localName) throws FOVException
    {
        return elementMappingRegistry.findFOMaker(namespaceURI, localName,
                                                  locator);
    }

    /** @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException) */
    public void warning(SAXParseException e)
    {
        log.warn(e.toString());
    }

    /** @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException) */
    public void error(SAXParseException e)
    {
    	e.printStackTrace();
        log.error(e.toString());
    }

    /** @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException) */
    public void fatalError(SAXParseException e) throws SAXException
    {
    	e.printStackTrace();
        log.error(e.toString());
        throw e;
    }

    /**
     * Provides access to the underlying FOEventHandler object.
     *
     * @return the FOEventHandler object
     */
    public FOEventHandler getEventHandler()
    {
        return foEventHandler;
    }

    /**
     * Returns the results of the rendering process. Information includes the
     * total number of pages generated and the number of pages per
     * page-sequence.
     *
     * @return the results of the rendering process.
     */
    public FormattingResults getResults()
    {
        if(getEventHandler() instanceof AreaTreeHandler)
        {
            return((AreaTreeHandler)getEventHandler()).getResults();
        }
        else
        {
            return null; // No formatting results available for output
        }
        // formats no involving the layout engine.
    }

    /**
     * add by zkl.
     */
    public Map getConversionMap()
    {
        return ta_conversionMap;
    }

    /** Main DefaultHandler implementation which builds the FO tree. */
    private class MainFOHandler extends DefaultHandler
    {
        /** Current formatting object being handled */
        protected FONode currentFObj = null;

        /** Current propertyList for the node being handled. */
        protected PropertyList currentPropertyList;

        /** Current marker nesting-depth */
        private int nestedMarkerDepth = 0;
        /**
         * SAX Handler for the start of an element
         *
         * @see org.xml.sax.ContentHandler#startElement(String, String, String,
         *      Attributes)
         */
        public void startElement(String namespaceURI, String localName,
                                 String rawName, Attributes attlist) throws SAXException
        {
            /* the node found in the FO document */
            FONode foNode;
            PropertyList propertyList = null;

            // Check to ensure first node encountered is an fo:root
            if(rootFObj == null)
            {
                if(!namespaceURI.equals(FOElementMapping.URI)
                   || !localName.equals("root"))
                {
                    throw new ValidationException("错误:第一个元素必须是fo:root 格式化对象. "
                                                  + "创建 "
                                                  + FONode.getNodeString(namespaceURI, localName)
                                                  + " 代替." + " 请确定您提供了一个正确的 XSL-FO文件.");
                }
            }
            else
            { // check that incoming node is valid for currentFObj
                if(namespaceURI.equals(FOElementMapping.URI))
                {
                    // currently no fox: elements to validate
                    // || namespaceURI.equals(ExtensionElementMapping.URI) */) {
                   try
                   {
                       currentFObj.validateChildNode(locator, namespaceURI,
                                                     localName);
                   }
                   catch(ValidationException e)
                   {
                       throw e;
                   }
                }
            }

            ElementMapping.Maker fobjMaker = findFOMaker(namespaceURI,
                                                         localName);

            try
            {
                foNode = fobjMaker.make(currentFObj);
                if(rootFObj == null)
                {
                    rootFObj = (Root)foNode;
                    rootFObj.setFOEventHandler(foEventHandler);

                }
                propertyList = foNode.createPropertyList(currentPropertyList,
                                                         foEventHandler);


                foNode.processNode(localName, getEffectiveLocator(), attlist,
                                   propertyList);
                if(foNode.getNameId() == Constants.FO_MARKER)
                {
                    if(foEventHandler.inMarker())
                    {
                        nestedMarkerDepth++;
                    }
                    else
                    {
                        foEventHandler.switchMarkerContext(true);
                    }
                }
                foNode.startOfNode();
            }
            catch(IllegalArgumentException e)
            {
                throw new SAXException(e);
            }

            ContentHandlerFactory chFactory = foNode.getContentHandlerFactory();
            if(chFactory != null)
            {
                ContentHandler subHandler = chFactory.createContentHandler();
                if(subHandler instanceof ObjectSource
                   && foNode instanceof ObjectBuiltListener)
                {
                    ((ObjectSource)subHandler)
                        .setObjectBuiltListener((ObjectBuiltListener)foNode);
                }

                subHandler.startDocument();
                subHandler.startElement(namespaceURI, localName, rawName,
                                        attlist);
                depth = 1;
                delegate = subHandler;
            }

            if(currentFObj != null)
            {
                currentFObj.addChildNode(foNode);
            }

            currentFObj = foNode;
            if(propertyList != null && !foEventHandler.inMarker())
            {
                currentPropertyList = propertyList;
            }

            // add by xuhao
            if(currentPropertyList != null)
            {
                Property p1 = currentPropertyList.get(Constants.PR_EDITMODE);
                Property p2 = currentPropertyList.get(Constants.PR_HIDENAME);
                if(p1 != null && p2 != null)
                {
                    int editMode = p1.getNumber().intValue();
                    String hideName = p2.getString();
                    if(editMode != 0 && hideName != null && !"".equals(hideName))
                    {
                        //满足隐藏项的条件
                        currentFObj.addCharacters(new char[0], 0, 0,
                                                  currentPropertyList, getEffectiveLocator());
                    }
                }
            }
            // add end
        }

        /**
         * SAX Handler for the end of an element
         *
         * @see org.xml.sax.ContentHandler#endElement(String, String, String)
         */
        public void endElement(String uri, String localName, String rawName) throws SAXException
        {

            if(currentFObj == null)
            {
                throw new IllegalStateException("如果没有当前元素则调用endElement()方法.");
            }
            else if(!currentFObj.getLocalName().equals(localName)
                    || !currentFObj.getNamespaceURI().equals(uri))
            {
                log.warn("Mismatch: " + currentFObj.getLocalName() + " ("
                         + currentFObj.getNamespaceURI() + ") vs. " + localName
                         + " (" + uri + ")");
            }

            currentFObj.endOfNode();

            if(currentPropertyList != null
               && currentPropertyList.getFObj() == currentFObj
               && !foEventHandler.inMarker())
            {
                currentPropertyList = currentPropertyList
                                      .getParentPropertyList();
            }

            if(currentFObj.getNameId() == Constants.FO_MARKER)
            {
                if(nestedMarkerDepth == 0)
                {
                    foEventHandler.switchMarkerContext(false);
                }
                else
                {
                    nestedMarkerDepth--;
                }
            }

            if(currentFObj.getParent() == null)
            {
                log.debug("endElement for top-level " + currentFObj.getName());
            }
            currentFObj = currentFObj.getParent();
        }

        /**
         * SAX Handler for characters
         *
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] data, int start, int length) throws FOVException
		{
			if (currentFObj != null)
			{
				currentFObj.addCharacters(data, start, start + length,
						currentPropertyList, getEffectiveLocator());
			}
		}

        public void endDocument() throws SAXException
        {
            currentFObj = null;
        }
    }


    /**
     * SAX Handler,to handle self-defined element in fo-file.
     * @author zkl
     * 2007-04-16.
     * 在fo文件中，在<fo:root>元素下定义了两个新的元素：<tableinfo>
     * 和<traslatetable>.其中<tableinfo>定义了动态表的信息。其格式为：
     * <tableinfo startname="" rowcount="" />; <translatetable>定义了
     * 内部转换表的信息，其格式为：
     * <translatetable1>
     *     <translate key="" value="" />
     *     ...
     * </translatetable1>
     * 本类将以上两个元素中的内容解析出来，存储在userAgent类的tableinfo和
     * translatetable属性中。
     */

    private class ExtendFOHandler extends DefaultHandler
    {

        private String transTableName;


        public void startElement(String namespaceURI, String localName,
                                 String rawName, Attributes attlist) throws SAXException
        {

            if(localName.startsWith("translatetable"))
            {
                transTableName = rawName;

                if(ta_conversionMap == null)
                {
                    ta_conversionMap = new HashMap();
                }
                ta_conversionMap.put(rawName, new HashMap());

            }
            else if(localName.equals("translate"))
            {
                Map currTable = (Map)ta_conversionMap.get(transTableName);
                if(attlist.getValue("fov_key") != null)
                {
                    currTable.put(attlist.getValue("fov_key"), attlist
                                  .getValue("fov_value"));
                }

            }
            else if(localName.equals("tableinfo"))
            {
                if(tableinfoMap == null)
                {
                    tableinfoMap = new HashMap();
                }
                tableinfoMap.put(attlist.getValue("fov_startname"), attlist.getValue("fov_rowcount"));

            }

        }

        public void endElement(String uri, String localName, String rawName)
        {
            if(localName.startsWith("translatetable"))
            {
                userAgent.setTranlatetable(ta_conversionMap);
            }
            if(localName.equals("tableinfo"))
            {
                userAgent.setTableinfo(tableinfoMap);
            }

        }

        public void characters(char[] data, int start, int length)
        {

        }


        /**
         * 将解析的结果传递到userAgent对象。
         */
        public void endDocument()
        {
            userAgent.setTranlatetable(ta_conversionMap);
        }

    }

}
