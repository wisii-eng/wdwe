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
 *//* $Id: AbstractGraphicsLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import com.wisii.fov.area.Area;
import com.wisii.fov.area.inline.Viewport;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.LengthBase;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.flow.AbstractGraphics;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.TraitSetter;


/**
 * LayoutManager handling the common tasks for the fo:instream-foreign-object
 * and fo:external-graphics formatting objects
 */
public abstract class AbstractGraphicsLayoutManager extends LeafNodeLayoutManager {

    /** The graphics object this LM deals with */
    protected AbstractGraphics fobj;

    /**
     * Constructor
     * @param node the formatting object that creates this area
     */
    public AbstractGraphicsLayoutManager(AbstractGraphics node) {
        super(node);
        fobj = node;
    }

    /**
     * Get the inline area created by this element.
     *
     * @return the viewport inline area
     */
    private Viewport getInlineArea() {

        // viewport size is determined by block-progression-dimension
        // and inline-progression-dimension

        // if replaced then use height then ignore block-progression-dimension
        //int h = this.propertyList.get("height").getLength().mvalue();

        // use specified line-height then ignore dimension in height direction
        boolean hasLH = false; //propertyList.get("line-height").getSpecifiedValue() != null;

        Length len;

        int bpd = -1;
        int ipd = -1;
        if (hasLH) {
            bpd = fobj.getLineHeight().getOptimum(this).getLength().getValue(this);
        } else {
            // this property does not apply when the line-height applies
            // isn't the block-progression-dimension always in the same
            // direction as the line height?
            len = fobj.getBlockProgressionDimension().getOptimum(this).getLength();
            if (len.getEnum() != EN_AUTO) {
                bpd = len.getValue(this);
            } else {
                len = fobj.getHeight();
                if (len.getEnum() != EN_AUTO) {
                    bpd = len.getValue(this);
                }
            }
        }

        len = fobj.getInlineProgressionDimension().getOptimum(this).getLength();
        if (len.getEnum() != EN_AUTO) {
            ipd = len.getValue(this);
        } else {
            len = fobj.getWidth();
            if (len.getEnum() != EN_AUTO) {
                ipd = len.getValue(this);
            }
        }

        // if auto then use the intrinsic size of the content scaled
        // to the content-height and content-width
        int cwidth = -1;
        int cheight = -1;
        len = fobj.getContentWidth();
        if (len.getEnum() != EN_AUTO) {
            if (len.getEnum() == EN_SCALE_TO_FIT) {
                if (ipd != -1) {
                    cwidth = ipd;
                }
            } else {
                cwidth = len.getValue(this);
            }
        }
        len = fobj.getContentHeight();
        if (len.getEnum() != EN_AUTO) {
            if (len.getEnum() == EN_SCALE_TO_FIT) {
                if (bpd != -1) {
                    cheight = bpd;
                }
            } else {
                cheight = len.getValue(this);
            }
        }

        int scaling = fobj.getScaling();
        if ((scaling == EN_UNIFORM) || (cwidth == -1) || cheight == -1) {
            if (cwidth == -1 && cheight == -1) {
                cwidth = fobj.getIntrinsicWidth();
                cheight = fobj.getIntrinsicHeight();
            } else if (cwidth == -1) {
                if (fobj.getIntrinsicHeight() == 0) {
                    cwidth = 0;
                } else {
                    cwidth = (int)(fobj.getIntrinsicWidth() * (double)cheight
                            / fobj.getIntrinsicHeight());
                }
            } else if (cheight == -1) {
                if (fobj.getIntrinsicWidth() == 0) {
                    cheight = 0;
                } else {
                    cheight = (int)(fobj.getIntrinsicHeight() * (double)cwidth
                            / fobj.getIntrinsicWidth());
                }
            } else {
                // adjust the larger
                if (fobj.getIntrinsicWidth() == 0 || fobj.getIntrinsicHeight() == 0) {
                    cwidth = 0;
                    cheight = 0;
                } else {
                    double rat1 = (double) cwidth / fobj.getIntrinsicWidth();
                    double rat2 = (double) cheight / fobj.getIntrinsicHeight();
                    if (rat1 < rat2) {
                        // reduce cheight
                        cheight = (int)(rat1 * fobj.getIntrinsicHeight());
                    } else if (rat1 > rat2) {
                        cwidth = (int)(rat2 * fobj.getIntrinsicWidth());
                    }
                }
            }
        }

        if (ipd == -1) {
            ipd = cwidth;
        }
        if (bpd == -1) {
            bpd = cheight;
        }

        boolean clip = false;
        if (cwidth > ipd || cheight > bpd) {
            int overflow = fobj.getOverflow();
            if (overflow == EN_HIDDEN) {
                clip = true;
            } else if (overflow == EN_ERROR_IF_OVERFLOW) {
                fobj.getLogger().error("Object overflows the viewport: clipping");
                clip = true;
            }
        }

        int xoffset = fobj.computeXOffset(ipd, cwidth);
        int yoffset = fobj.computeYOffset(bpd, cheight);

        CommonBorderPaddingBackground borderProps = fobj.getCommonBorderPaddingBackground();

        //Determine extra BPD from borders etc.
        int beforeBPD = borderProps.getPadding(CommonBorderPaddingBackground.BEFORE, false, this);
        beforeBPD += borderProps.getBorderWidth(CommonBorderPaddingBackground.BEFORE,
                                             false);
        int afterBPD = borderProps.getPadding(CommonBorderPaddingBackground.AFTER, false, this);
        afterBPD += borderProps.getBorderWidth(CommonBorderPaddingBackground.AFTER, false);

        yoffset += beforeBPD;
        //bpd += beforeBPD;
        //bpd += afterBPD;

        //Determine extra IPD from borders etc.
        int startIPD = borderProps.getPadding(CommonBorderPaddingBackground.START,
                false, this);
        startIPD += borderProps.getBorderWidth(CommonBorderPaddingBackground.START,
                 false);
        int endIPD = borderProps.getPadding(CommonBorderPaddingBackground.END, false, this);
        endIPD += borderProps.getBorderWidth(CommonBorderPaddingBackground.END, false);

        xoffset += startIPD;
        //ipd += startIPD;
        //ipd += endIPD;

        Rectangle2D placement = new Rectangle2D.Float(xoffset, yoffset, cwidth, cheight);

        Area viewportArea = getChildArea();
        TraitSetter.setProducerID(viewportArea, fobj.getId());
        transferForeignAttributes(viewportArea);

        Viewport vp = new Viewport(viewportArea);
        viewportArea.setParentArea(vp);
        TraitSetter.setProducerID(vp, fobj.getId());
        vp.setIPD(ipd);
        vp.setBPD(bpd);
        vp.setContentPosition(placement);
        vp.setClip(clip);
        vp.setOffset(0);

        // Common Border, Padding, and Background Properties
        TraitSetter.addBorders(vp, fobj.getCommonBorderPaddingBackground()
                                , false, false, false, false, this);
        TraitSetter.addPadding(vp, fobj.getCommonBorderPaddingBackground()
                                , false, false, false, false, this);
        TraitSetter.addBackground(vp, fobj.getCommonBorderPaddingBackground(), this);

        return vp;
    }

    /**
     * @see com.wisii.fov.layoutmgr.LayoutManager#getNextKnuthElements(LayoutContext, int)
     */
    public LinkedList getNextKnuthElements(LayoutContext context,
                                           int alignment) {
        Viewport areaCurrent = getInlineArea();
        setCurrentArea(areaCurrent);
        return super.getNextKnuthElements(context, alignment);
    }

    /**
     * @see LeafNodeLayoutManager#makeAlignmentContext(LayoutContext)
     */
    protected AlignmentContext makeAlignmentContext(LayoutContext context) {
        return new AlignmentContext(
                get(context).getAllocBPD()
                , fobj.getAlignmentAdjust()
                , fobj.getAlignmentBaseline()
                , fobj.getBaselineShift()
                , fobj.getDominantBaseline()
                , context.getAlignmentContext()
            );
    }

    /**
     * @see com.wisii.fov.layoutmgr.inline.LeafNodeLayoutManager#addId()
     */
    protected void addId() {
        getPSLM().addIDToPage(fobj.getId());
    }

    /**
     * Returns the image of foreign object area to be put into
     * the viewport.
     * @return the appropriate area
     */
    abstract Area getChildArea();

    // --------- Property Resolution related functions --------- //

    /**
     * @see com.wisii.fov.datatypes.PercentBaseContext#getBaseLength(int, FObj)
     */
    public int getBaseLength(int lengthBase, FObj fobj) {
        switch (lengthBase) {
        case LengthBase.IMAGE_INTRINSIC_WIDTH:
            return getIntrinsicWidth();
        case LengthBase.IMAGE_INTRINSIC_HEIGHT:
            return getIntrinsicHeight();
        case LengthBase.ALIGNMENT_ADJUST:
            return get(null).getBPD();
        default: // Delegate to super class
            return super.getBaseLength(lengthBase, fobj);
        }
    }

    /**
     * Returns the intrinsic width of the e-g.
     * @return the width of the element
     */
    protected int getIntrinsicWidth() {
        return fobj.getIntrinsicWidth();
    }

    /**
     * Returns the intrinsic height of the e-g.
     * @return the height of the element
     */
    protected int getIntrinsicHeight() {
        return fobj.getIntrinsicHeight();
    }

}

