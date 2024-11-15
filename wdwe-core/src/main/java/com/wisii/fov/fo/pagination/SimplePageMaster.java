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
 *//* $Id: SimplePageMaster.java,v 1.2 2008/03/03 08:43:14 lzy Exp $ */

package com.wisii.fov.fo.pagination;

// Java
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.properties.CommonMarginBlock;

/**
 * A simple-page-master formatting object.
 * This creates a simple page from the specified regions
 * and attributes.
 */
public class SimplePageMaster extends FObj
{
    // The value of properties relevant for fo:simple-page-master.
    private CommonMarginBlock commonMarginBlock;
    private String masterName;
    private Length pageHeight;
    private Length pageWidth;
    private Numeric referenceOrientation;
    private int writingMode;

    /* 【添加】 by 李晓光 2008-03-02 */
    private String mediaUsage;
    // End of property values

    /**
     * Page regions (regionClass, Region)
     */
    private Map regions;

    // used for node validation
    private boolean hasRegionBody = false;
    private boolean hasRegionBefore = false;
    private boolean hasRegionAfter = false;
    private boolean hasRegionStart = false;
    private boolean hasRegionEnd = false;

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public SimplePageMaster(FONode parent)
    {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind
     */
    public void bind(PropertyList pList) throws FOVException
    {
        commonMarginBlock = pList.getMarginBlockProps();
        masterName = pList.get(PR_MASTER_NAME).getString();
        pageHeight = pList.get(PR_PAGE_HEIGHT).getLength();
        pageWidth = pList.get(PR_PAGE_WIDTH).getLength();
        referenceOrientation = pList.get(PR_REFERENCE_ORIENTATION).getNumeric();
        writingMode = pList.getWritingMode();
        /* 【添加：Start】 by 李晓光 2008-03-02 */
        mediaUsage = pList.get(PR_MEDIA_USAGE).getString();
//        System.err.println("mediaUsage = " + mediaUsage);
        /* 【添加：End】 by 李晓光 2008-03-02 */
        if(masterName == null || masterName.equals(""))
        {
            missingPropertyError("master-name");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException
    {
        LayoutMasterSet layoutMasterSet = (LayoutMasterSet)parent;

        if(masterName == null)
        {
            missingPropertyError("master-name");
        }
        else
        {
            layoutMasterSet.addSimplePageMaster(this);
        }

        //Well, there are only 5 regions so we can save a bit of memory here
        regions = new HashMap(5);
    }

    /**
     * Make sure content model satisfied.
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException
    {
        if(!hasRegionBody)
        {
            missingChildElementError(
                "(region-body, region-before?, region-after?, region-start?, region-end?)");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
     * XSL Content Model: (region-body,region-before?,region-after?,region-start?,region-end?)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName) throws ValidationException
    {
        if(FO_URI.equals(nsURI) && localName.equals("region-body"))
        {
            if(hasRegionBody)
            {
                tooManyNodesError(loc, "fo:region-body");
            }
            else
            {
                hasRegionBody = true;
            }
        }
        else if(FO_URI.equals(nsURI) && localName.equals("region-before"))
        {
            if(!hasRegionBody)
            {
                nodesOutOfOrderError(loc, "fo:region-body", "fo:region-before");
            }
            else if(hasRegionBefore)
            {
                tooManyNodesError(loc, "fo:region-before");
            }
            else if(hasRegionAfter)
            {
                nodesOutOfOrderError(loc, "fo:region-before", "fo:region-after");
            }
            else if(hasRegionStart)
            {
                nodesOutOfOrderError(loc, "fo:region-before", "fo:region-start");
            }
            else if(hasRegionEnd)
            {
                nodesOutOfOrderError(loc, "fo:region-before", "fo:region-end");
            }
            else
            {
                hasRegionBody = true;
            }
        }
        else if(FO_URI.equals(nsURI) && localName.equals("region-after"))
        {
            if(!hasRegionBody)
            {
                nodesOutOfOrderError(loc, "fo:region-body", "fo:region-after");
            }
            else if(hasRegionAfter)
            {
                tooManyNodesError(loc, "fo:region-after");
            }
            else if(hasRegionStart)
            {
                nodesOutOfOrderError(loc, "fo:region-after", "fo:region-start");
            }
            else if(hasRegionEnd)
            {
                nodesOutOfOrderError(loc, "fo:region-after", "fo:region-end");
            }
            else
            {
                hasRegionAfter = true;
            }
        }
        else if(FO_URI.equals(nsURI) && localName.equals("region-start"))
        {
            if(!hasRegionBody)
            {
                nodesOutOfOrderError(loc, "fo:region-body", "fo:region-start");
            }
            else if(hasRegionStart)
            {
                tooManyNodesError(loc, "fo:region-start");
            }
            else if(hasRegionEnd)
            {
                nodesOutOfOrderError(loc, "fo:region-start", "fo:region-end");
            }
            else
            {
                hasRegionStart = true;
            }
        }
        else if(FO_URI.equals(nsURI) && localName.equals("region-end"))
        {
            if(!hasRegionBody)
            {
                nodesOutOfOrderError(loc, "fo:region-body", "fo:region-end");
            }
            else if(hasRegionEnd)
            {
                tooManyNodesError(loc, "fo:region-end");
            }
            else
            {
                hasRegionEnd = true;
            }
        }
        else
        {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * @see com.wisii.fov.fo.FObj#generatesReferenceAreas()
     */
    public boolean generatesReferenceAreas()
    {
        return true;
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     */
    protected void addChildNode(FONode child) throws FOVException
    {
        if(child instanceof Region)
        {
            addRegion((Region)child);
        }
        else
        {
            super.addChildNode(child);
        }
    }

    /**
     * Adds a region to this simple-page-master.
     * @param region region to add
     */
    protected void addRegion(Region region)
    {
        String key = String.valueOf(region.getNameId());
        regions.put(key, region);
    }

    /**
     * Returns the region for a given region class.
     * @param regionId Constants ID of the FO representing the region
     * @return the region, null if it doesn't exist
     */
    public Region getRegion(int regionId)
    {
        return(Region)regions.get(String.valueOf(regionId));
    }

    /**
     * Returns a Map of regions associated with this simple-page-master
     * @return the regions
     */
    public Map getRegions()
    {
        return regions;
    }

    /**
     * Indicates if a region with a given name exists in this
     * simple-page-master.
     * @param regionName name of the region to lookup
     * @return True if a region with this name exists
     */
    protected boolean regionNameExists(String regionName)
    {
        for(Iterator regenum = regions.values().iterator();
                               regenum.hasNext(); )
        {
            Region r = (Region)regenum.next();
            if(r.getRegionName().equals(regionName))
            {
                return true;
            }
        }
        return false;
    }

    /** @return the Common Margin Properties-Block. */
    public CommonMarginBlock getCommonMarginBlock()
    {
        return commonMarginBlock;
    }

    /** @return "master-name" property. */
    public String getMasterName()
    {
        return masterName;
    }

    /** @return the "page-width" property. */
    public Length getPageWidth()
    {
        return pageWidth;
    }

    /** @return the "page-height" property. */
    public Length getPageHeight()
    {
        return pageHeight;
    }

    /** @return the "writing-mode" property. */
    public int getWritingMode()
    {
        return writingMode;
    }

    /** @return the "reference-orientation" property. */
    public int getReferenceOrientation()
    {
        return referenceOrientation.getValue();
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName()
    {
        return "simple-page-master";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId()
    {
        return FO_SIMPLE_PAGE_MASTER;
    }

    /* 【添加：Start】 by 李晓光 2008-03-02 */
    public String getMediaUsage()
    {
        return mediaUsage;
    }
    /* 【添加：End】 by 李晓光 2008-03-02 */
}
