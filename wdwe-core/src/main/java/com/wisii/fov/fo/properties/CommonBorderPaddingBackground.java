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
 *//* $Id: CommonBorderPaddingBackground.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import java.awt.Color;

import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.ImageFactory;
import com.wisii.fov.util.ColorUtil;

/**
 * Stores all common border and padding properties.
 * See Sec. 7.7 of the XSL-FO Standard.
 */
public class CommonBorderPaddingBackground implements Cloneable {
    /**
     * The "background-attachment" property.
     */
    public int backgroundAttachment;

    /**
     * The "background-color" property.
     */
    public Color backgroundColor;

    /**
     * The "background-image" property.
     */
    public String backgroundImage;

    /**
     * The "background-repeat" property.
     */
    public int backgroundRepeat;

    /**
     * The "background-position-horizontal" property.
     */
    public Length backgroundPositionHorizontal;

    /**
     * The "background-position-vertical" property.
     */
    public Length backgroundPositionVertical;


    private FovImage fovimage;
    /* 【添加：START】 by  李晓光 2009-2-5 */
    private int backgroundLayer = 0;
    public int getBackgroundLayer(){
    	return this.backgroundLayer;
    }
    /* 【添加：END】 by  李晓光 2009-2-5 */

    /** the "before" edge */
    public static final int BEFORE = 0;
    /** the "after" edge */
    public static final int AFTER = 1;
    /** the "start" edge */
    public static final int START = 2;
    /** the "end" edge */
    public static final int END = 3;

    public static class BorderInfo implements Cloneable {
        private int mStyle; // Enum for border style
        private Color mColor; // Border color
        private CondLengthProperty mWidth;

        BorderInfo(int style, CondLengthProperty width, Color color) {
            mStyle = style;
            mWidth = width;
            mColor = color;
        }

        public int getStyle() {
            return this.mStyle;
        }

        public Color getColor() {
            return this.mColor;
        }

        public CondLengthProperty getWidth() {
            return this.mWidth;
        }

        public int getRetainedWidth() {
            if ((mStyle == Constants.EN_NONE)
                    || (mStyle == Constants.EN_HIDDEN)) {
                return 0;
            } else {
                return mWidth.getLengthValue();
            }
        }

        /** @see java.lang.Object#toString() */
        public String toString() {
            StringBuffer sb = new StringBuffer("BorderInfo");
            sb.append(" {");
            sb.append(mStyle);
            sb.append(", ");
            sb.append(mColor);
            sb.append(", ");
            sb.append(mWidth);
            sb.append("}");
            return sb.toString();
        }
    }

    private BorderInfo[] borderInfo = new BorderInfo[4];
    private CondLengthProperty[] padding = new CondLengthProperty[4];

    /**
     * Construct a CommonBorderPaddingBackground object.
     */
    public CommonBorderPaddingBackground() {

    }

    /**
     * Construct a CommonBorderPaddingBackground object.
     * @param pList The PropertyList to get properties from.
     * @param fobj The FO to create this instance for.
     * @throws PropertyException if there's an error while binding the properties
     */
    public CommonBorderPaddingBackground(PropertyList pList, FObj fobj) throws PropertyException {
    	/* 【添加：START】 by  李晓光 2009-2-5 */
    	backgroundLayer = pList.get(Constants.PR_BACKGROUNDGRAPHIC_LAYER).getNumber().intValue();
    	ColorUtil.addLayer(new Integer(backgroundLayer));
    	  FOUserAgent agent = fobj.getUserAgent();
    	  agent.addLayer(new Integer(backgroundLayer));
    	/* 【添加：END】 by  李晓光 2009-2-5 */
    	
        backgroundAttachment = pList.get(Constants.PR_BACKGROUND_ATTACHMENT).getEnum();
        backgroundColor = pList.get(Constants.PR_BACKGROUND_COLOR).getColor();
        if (backgroundColor.getAlpha() == 0) {
            backgroundColor = null;
        }

        backgroundImage = pList.get(Constants.PR_BACKGROUND_IMAGE).getString();
        if (backgroundImage == null || "none".equals(backgroundImage)) {
            backgroundImage = null;
        } else {
            backgroundRepeat = pList.get(Constants.PR_BACKGROUND_REPEAT).getEnum();
            backgroundPositionHorizontal = pList.get(
                    Constants.PR_BACKGROUND_POSITION_HORIZONTAL).getLength();
            backgroundPositionVertical = pList.get(
                    Constants.PR_BACKGROUND_POSITION_VERTICAL).getLength();

            //Additional processing: preload image
            String url = ImageFactory.getURL(backgroundImage);
            FOUserAgent userAgent = fobj.getUserAgent();
            ImageFactory fact = userAgent.getFactory().getImageFactory();
            fovimage = fact.getImage(url, userAgent);
            if (fovimage == null) {
                fobj.getLogger().error("Background image not available: " + backgroundImage);
            } else {
                // load dimensions
                if (!fovimage.load(FovImage.DIMENSIONS)) {
                    fobj.getLogger().error("Cannot read background image dimensions: "
                            + backgroundImage);
                }
            }
            //TODO Report to caller so he can decide to throw an exception
        }

        initBorderInfo(pList, BEFORE,
                Constants.PR_BORDER_BEFORE_COLOR,
                Constants.PR_BORDER_BEFORE_STYLE,
                Constants.PR_BORDER_BEFORE_WIDTH,
                Constants.PR_PADDING_BEFORE);
        initBorderInfo(pList, AFTER,
                Constants.PR_BORDER_AFTER_COLOR,
                Constants.PR_BORDER_AFTER_STYLE,
                Constants.PR_BORDER_AFTER_WIDTH,
                Constants.PR_PADDING_AFTER);
        initBorderInfo(pList, START,
                Constants.PR_BORDER_START_COLOR,
                Constants.PR_BORDER_START_STYLE,
                Constants.PR_BORDER_START_WIDTH,
                Constants.PR_PADDING_START);
        initBorderInfo(pList, END,
                Constants.PR_BORDER_END_COLOR,
                Constants.PR_BORDER_END_STYLE,
                Constants.PR_BORDER_END_WIDTH,
                Constants.PR_PADDING_END);

    }

    private void initBorderInfo(PropertyList pList, int side,
                    int colorProp, int styleProp, int widthProp, int paddingProp)
                throws PropertyException {
        padding[side] = pList.get(paddingProp).getCondLength();
        // If style = none, force width to 0, don't get Color (spec 7.7.20)
        int style = pList.get(styleProp).getEnum();
        if (style != Constants.EN_NONE) {
            setBorderInfo(new BorderInfo(style,
                pList.get(widthProp).getCondLength(),
                pList.get(colorProp).getColor()), side);
        }
    }

    /**
     * Sets a border.
     * @param info the border information
     * @param side the side to apply the info to
     */
    public void setBorderInfo(BorderInfo info, int side) {
        this.borderInfo[side] = info;
    }

    /**
     * @param side the side to retrieve
     * @return the border info for a side
     */
    public BorderInfo getBorderInfo(int side) {
        return this.borderInfo[side];
    }

    /**
     * Set padding.
     * @param source the padding info to copy from
     */
    public void setPadding(CommonBorderPaddingBackground source) {
        this.padding = source.padding;
    }

    /**
     * @return the background image as a preloaded FovImage, null if there is
     *     no background image.
     */
    public FovImage getFovImage() {
        return this.fovimage;
    }

    public int getBorderStartWidth(boolean bDiscard) {
        return getBorderWidth(START, bDiscard);
    }

    public int getBorderEndWidth(boolean bDiscard) {
        return getBorderWidth(END, bDiscard);
    }

    public int getBorderBeforeWidth(boolean bDiscard) {
        return getBorderWidth(BEFORE, bDiscard);
    }

    public int getBorderAfterWidth(boolean bDiscard) {
        return getBorderWidth(AFTER, bDiscard);
    }

    public int getPaddingStart(boolean bDiscard, PercentBaseContext context) {
        return getPadding(START, bDiscard, context);
    }

    public int getPaddingEnd(boolean bDiscard, PercentBaseContext context) {
        return getPadding(END, bDiscard, context);
    }

    public int getPaddingBefore(boolean bDiscard, PercentBaseContext context) {
        return getPadding(BEFORE, bDiscard, context);
    }

    public int getPaddingAfter(boolean bDiscard, PercentBaseContext context) {
        return getPadding(AFTER, bDiscard, context);
    }

    public int getBorderWidth(int side, boolean bDiscard) {
        if ((borderInfo[side] == null)
                || (borderInfo[side].mStyle == Constants.EN_NONE)
                || (borderInfo[side].mStyle == Constants.EN_HIDDEN)
                || (bDiscard && borderInfo[side].mWidth.isDiscard())) {
            return 0;
        } else {
            return borderInfo[side].mWidth.getLengthValue();
        }
    }

    public Color getBorderColor(int side) {
        if (borderInfo[side] != null) {
            return borderInfo[side].mColor;
        } else {
            return null;
        }
    }

    public int getBorderStyle(int side) {
        if (borderInfo[side] != null) {
            return borderInfo[side].mStyle;
        } else {
            return Constants.EN_NONE;
        }
    }

    public int getPadding(int side, boolean bDiscard, PercentBaseContext context) {
        if ((padding[side] == null) || (bDiscard && padding[side].isDiscard())) {
            return 0;
        } else {
            return padding[side].getLengthValue(context);
        }
    }

    /**
     * Returns the CondLengthProperty for the padding on one side.
     * @param side the side
     * @return the requested CondLengthProperty
     */
    public CondLengthProperty getPaddingLengthProperty(int side) {
        return padding[side];
    }

    /**
     * Return all the border and padding width in the inline progression
     * dimension.
     * @param bDiscard the discard flag.
     * @param context for percentage evaluation.
     * @return all the padding and border width.
     */
    public int getIPPaddingAndBorder(boolean bDiscard, PercentBaseContext context) {
        return getPaddingStart(bDiscard, context)
            + getPaddingEnd(bDiscard, context)
            + getBorderStartWidth(bDiscard)
            + getBorderEndWidth(bDiscard);
    }

    /**
     * Return all the border and padding height in the block progression
     * dimension.
     * @param bDiscard the discard flag.
     * @param context for percentage evaluation
     * @return all the padding and border height.
     */
    public int getBPPaddingAndBorder(boolean bDiscard, PercentBaseContext context) {
        return getPaddingBefore(bDiscard, context) + getPaddingAfter(bDiscard, context)
               + getBorderBeforeWidth(bDiscard) + getBorderAfterWidth(bDiscard);
    }

    /** @see java.lang.Object#toString() */
    public String toString() {
        return "CommonBordersAndPadding (Before, After, Start, End):\n"
            + "Borders: (" + getBorderBeforeWidth(false) + ", " + getBorderAfterWidth(false) + ", "
            + getBorderStartWidth(false) + ", " + getBorderEndWidth(false) + ")\n"
            + "Border Colors: (" + getBorderColor(BEFORE) + ", " + getBorderColor(AFTER) + ", "
            + getBorderColor(START) + ", " + getBorderColor(END) + ")\n"
            + "Padding: (" + getPaddingBefore(false, null) + ", " + getPaddingAfter(false, null)
            + ", " + getPaddingStart(false, null) + ", " + getPaddingEnd(false, null) + ")\n";
    }

    /**
     * @return true if there is any kind of background to be painted
     */
    public boolean hasBackground() {
        return ((backgroundColor != null || getFovImage() != null));
    }

    /** @return true if border is non-zero. */
    public boolean hasBorder() {
        return ((getBorderBeforeWidth(false) + getBorderAfterWidth(false)
                + getBorderStartWidth(false) + getBorderEndWidth(false)) > 0);
    }

    /**
     * @param context for percentage based evaluation.
     * @return true if padding is non-zero.
     */
    public boolean hasPadding(PercentBaseContext context) {
        return ((getPaddingBefore(false, context) + getPaddingAfter(false, context)
                + getPaddingStart(false, context) + getPaddingEnd(false, context)) > 0);
    }

    /** @return true if there are any borders defined. */
    public boolean hasBorderInfo() {
        return (borderInfo[BEFORE] != null || borderInfo[AFTER] != null
                || borderInfo[START] != null || borderInfo[END] != null);
    }
}
