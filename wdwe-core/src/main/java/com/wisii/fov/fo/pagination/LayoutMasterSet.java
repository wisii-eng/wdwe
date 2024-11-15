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
 *//* $Id: LayoutMasterSet.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.pagination;

// Java
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * The layout-master-set formatting object.
 * This class maintains the set of simple page master and
 * page sequence masters.
 * The masters are stored so that the page sequence can obtain
 * the required page master to create a page.
 * The page sequence masters can be reset as they hold state
 * information for a page sequence.
 */
public class LayoutMasterSet extends FObj {

    private Map simplePageMasters;
    private Map pageSequenceMasters;

    /**
     * @see com.wisii.fov.fo.FONode#FONode(FONode)
     */
    public LayoutMasterSet(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        // No properties in layout-master-set.
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        getRoot().setLayoutMasterSet(this);
        simplePageMasters = new java.util.HashMap();
        pageSequenceMasters = new java.util.HashMap();
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
        if (childNodes == null) {
            missingChildElementError("(simple-page-master|page-sequence-master)+");
        }
        checkRegionNames();
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
        XSL/FOV: (simple-page-master|page-sequence-master)+
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI)) {
            if (!localName.equals("simple-page-master")
                && !localName.equals("page-sequence-master")) {
                    invalidChildError(loc, nsURI, localName);
            }
        } else {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * Section 7.25.7: check to see that if a region-name is a
     * duplicate, that it maps to the same fo region-class.
     * @throws ValidationException if there's a name duplication
     */
    private void checkRegionNames() throws ValidationException {
        // (user-entered) region-name to default region map.
        Map allRegions = new java.util.HashMap();
        for (Iterator spm = simplePageMasters.values().iterator();
                spm.hasNext();) {
            SimplePageMaster simplePageMaster =
                (SimplePageMaster)spm.next();
            Map spmRegions = simplePageMaster.getRegions();
            for (Iterator e = spmRegions.values().iterator();
                    e.hasNext();) {
                Region region = (Region) e.next();
                if (allRegions.containsKey(region.getRegionName())) {
                    String defaultRegionName =
                        (String) allRegions.get(region.getRegionName());
                    if (!defaultRegionName.equals(region.getDefaultRegionName())) {
                        throw new ValidationException("区域名 ("
                                               + region.getRegionName()
                                               + ") 正在映射若干 "
                                               + "页布局类 ("
                                               + defaultRegionName + " 和 "
                                               + region.getDefaultRegionName()
                                               + ")", locator);
                    }
                }
                allRegions.put(region.getRegionName(),
                               region.getDefaultRegionName());
            }
        }
    }

    /**
     * Add a simple page master.
     * The name is checked to throw an error if already added.
     * @param sPM simple-page-master to add
     * @throws ValidationException if there's a problem with name uniqueness
     */
    protected void addSimplePageMaster(SimplePageMaster sPM)
                throws ValidationException {

        // check for duplication of master-name
        String masterName = sPM.getMasterName();
        if (existsName(masterName)) {
            throw new ValidationException("'master-name' ("
               + masterName
               + ") 必须有惟一的 "
               + " page-masters 和 page-sequence-masters", sPM.getLocator());
        }
        this.simplePageMasters.put(masterName, sPM);
    }

    private boolean existsName(String masterName) {
        if (simplePageMasters.containsKey(masterName)
                || pageSequenceMasters.containsKey(masterName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a simple page master by name.
     * This is used by the page sequence to get a page master for
     * creating pages.
     * @param masterName the name of the page master
     * @return the requested simple-page-master
     */
    public SimplePageMaster getSimplePageMaster(String masterName) {
        return (SimplePageMaster)this.simplePageMasters.get(masterName);
    }

    /**
     * Add a page sequence master.
     * The name is checked to throw an error if already added.
     * @param masterName name for the master
     * @param pSM PageSequenceMaster instance
     * @throws ValidationException if there's a problem with name uniqueness
     */
    protected void addPageSequenceMaster(String masterName,
                                        PageSequenceMaster pSM)
                throws ValidationException {
        // check against duplication of master-name
        if (existsName(masterName)) {
            throw new ValidationException("'master-name' ("
               + masterName
               + ") 必须有惟一的 "
               + " page-masters 和 page-sequence-masters", pSM.getLocator());
        }
        this.pageSequenceMasters.put(masterName, pSM);
    }

    /**
     * Get a page sequence master by name.
     * This is used by the page sequence to get a page master for
     * creating pages.
     * @param masterName name of the master
     * @return the requested PageSequenceMaster instance
     */
    public PageSequenceMaster getPageSequenceMaster(String masterName) {
        return (PageSequenceMaster)this.pageSequenceMasters.get(masterName);
    }

    /**
     * Checks whether or not a region name exists in this master set.
     * @param regionName name of the region
     * @return true when the region name specified has a region in this LayoutMasterSet
     */
    public boolean regionNameExists(String regionName) {
        for (Iterator e = simplePageMasters.values().iterator();
                e.hasNext();) {
            if (((SimplePageMaster)e.next()).regionNameExists(regionName)) {
                return true;
            }
        }
        return false;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "layout-master-set";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_LAYOUT_MASTER_SET;
    }
}

