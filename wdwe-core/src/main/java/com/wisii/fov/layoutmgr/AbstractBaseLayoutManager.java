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
 *//* $Id: AbstractBaseLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.fov.datatypes.LengthBase;
import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.fo.FObj;

/**
 * The base class for nearly all LayoutManagers.
 * Provides the functionality for merging the {@link LayoutManager}
 * and the {@link com.wisii.fov.datatypes.PercentBaseContext} interfaces
 * into a common base calls for all higher LayoutManagers.
 */
public abstract class AbstractBaseLayoutManager
    implements LayoutManager, PercentBaseContext {

    /** Indicator if this LM generates reference areas */
    protected boolean generatesReferenceArea = false;
    /** Indicator if this LM generates block areas */
    protected boolean generatesBlockArea = false;
    /** The formatting object for this LM */
    protected FObj fobj = null;

    /**
     * logging instance
     */
    protected static Log log = LogFactory.getLog(LayoutManager.class);

    /**
     * Abstract base layout manager.
     */
    public AbstractBaseLayoutManager() {
    }

    /**
     * Abstract base layout manager.
     *
     * @param fo the formatting object for this layout manager
     */
    public AbstractBaseLayoutManager(FObj fo) {
        fobj = fo;
        setGeneratesReferenceArea(fo.generatesReferenceAreas());
        if (getGeneratesReferenceArea()) {
            setGeneratesBlockArea(true);
        }
    }

    // --------- Property Resolution related functions --------- //

    /**
     * @see com.wisii.fov.datatypes.PercentBaseContext#getBaseLength(int, FObj)
     */
    public int getBaseLength(int lengthBase, FObj fobj) {
        if (fobj == getFObj()) {
            switch (lengthBase) {
            case LengthBase.CONTAINING_BLOCK_WIDTH:
                return getAncestorBlockAreaIPD();
            case LengthBase.CONTAINING_BLOCK_HEIGHT:
                return getAncestorBlockAreaBPD();
            case LengthBase.PARENT_AREA_WIDTH:
                return getParentAreaIPD();
            case LengthBase.CONTAINING_REFAREA_WIDTH:
                return getReferenceAreaIPD();
            default:
                log.error(new Exception("Unknown base type for LengthBase:" + lengthBase));
                return 0;
            }
        } else {
            LayoutManager lm = getParent();
            while (lm != null && fobj != lm.getFObj()) {
                lm = lm.getParent();
            }
            if (lm != null) {
                return lm.getBaseLength(lengthBase, fobj);
            }
        }
        log.error("Cannot find LM to handle given FO for LengthBase.");
        return 0;
    }

    /**
     * Find the first ancestor area that is a block area
     * and returns its IPD.
     * @return the ipd of the ancestor block area
     */
    protected int getAncestorBlockAreaIPD() {
        LayoutManager lm = getParent();
        while (lm != null) {
            if (lm.getGeneratesBlockArea() && !lm.getGeneratesLineArea()) {
                return lm.getContentAreaIPD();
            }
            lm = lm.getParent();
        }
        if (lm == null) {
            log.error("No parent LM found");
        }
        return 0;
    }

    /**
     * Find the first ancestor area that is a block area
     * and returns its BPD.
     * @return the bpd of the ancestor block area
     */
    protected int getAncestorBlockAreaBPD() {
        LayoutManager lm = getParent();
        while (lm != null) {
            if (lm.getGeneratesBlockArea() && !lm.getGeneratesLineArea()) {
                return lm.getContentAreaBPD();
            }
            lm = lm.getParent();
        }
        if (lm == null) {
            log.error("No parent LM found");
        }
        return 0;
    }

    /**
     * Find the parent area and returns its IPD.
     * @return the ipd of the parent area
     */
    protected int getParentAreaIPD() {
        LayoutManager lm = getParent();
        if (lm != null) {
            return lm.getContentAreaIPD();
        }
        log.error("No parent LM found");
        return 0;
    }

    /**
     * Find the parent area and returns its BPD.
     * @return the bpd of the parent area
     */
    protected int getParentAreaBPD() {
        LayoutManager lm = getParent();
        if (lm != null) {
            return lm.getContentAreaBPD();
        }
        log.error("No parent LM found");
        return 0;
    }

    /**
     * Find the first ancestor area that is a reference area
     * and returns its IPD.
     * @return the ipd of the ancestor reference area
     */
    public int getReferenceAreaIPD() {
        LayoutManager lm = getParent();
        while (lm != null) {
            if (lm.getGeneratesReferenceArea()) {
                return lm.getContentAreaIPD();
            }
            lm = lm.getParent();
        }
        if (lm == null) {
            log.error("No parent LM found");
        }
        return 0;
    }

    /**
     * Find the first ancestor area that is a reference area
     * and returns its BPD.
     * @return the bpd of the ancestor reference area
     */
    protected int getReferenceAreaBPD() {
        LayoutManager lm = getParent();
        while (lm != null) {
            if (lm.getGeneratesReferenceArea()) {
                return lm.getContentAreaBPD();
            }
            lm = lm.getParent();
        }
        if (lm == null) {
            log.error("No parent LM found");
        }
        return 0;
    }

    /**
     * Returns the IPD of the content area
     * @return the IPD of the content area
     * @see LayoutManager#getContentAreaIPD
     */
    public int getContentAreaIPD() {
        log.error("getContentAreaIPD called when it should have been overwritten");
        return 0;
    }

    /**
     * Returns the BPD of the content area
     * @return the BPD of the content area
     * @see LayoutManager#getContentAreaBPD
     */
    public int getContentAreaBPD() {
        log.error("getContentAreaBPD called when it should have been overwritten");
        return 0;
    }

    /**
     * @see LayoutManager#getGeneratesReferenceArea
     */
    public boolean getGeneratesReferenceArea() {
        return generatesReferenceArea;
    }

    /**
     * Lets implementing LM set the flag indicating if they
     * generate reference areas.
     * @param generatesReferenceArea if true the areas generates by this LM are
     * reference areas.
     */
    protected void setGeneratesReferenceArea(boolean generatesReferenceArea) {
        this.generatesReferenceArea = generatesReferenceArea;
    }

    /**
     * @see LayoutManager#getGeneratesBlockArea
     */
    public boolean getGeneratesBlockArea() {
        return generatesBlockArea;
    }

    /**
     * Lets implementing LM set the flag indicating if they
     * generate block areas.
     * @param generatesBlockArea if true the areas generates by this LM are block areas.
     */
    protected void setGeneratesBlockArea(boolean generatesBlockArea) {
        this.generatesBlockArea = generatesBlockArea;
    }

    /**
     * @see com.wisii.fov.layoutmgr.LayoutManager#getGeneratesLineArea
     */
    public boolean getGeneratesLineArea() {
        return false;
    }

    /**
     * @see com.wisii.fov.layoutmgr.LayoutManager#getFObj
     */
    public FObj getFObj() {
        return fobj;
    }

}
