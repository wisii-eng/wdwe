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
 *//* $Id: CustomFont.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts;

import java.util.Map;
import javax.xml.transform.Source;

/**
 * Abstract base class for custom fonts loaded from files, for example.
 */
public abstract class CustomFont extends Typeface
            implements FontDescriptor, MutableFont {

    private String fontName = null;
    private String embedFileName = null;
    private String embedResourceName = null;
    private FontResolver resolver = null;

    private int capHeight = 0;
    private int xHeight = 0;
    private int ascender = 0;
    private int descender = 0;
    private int[] fontBBox = {0, 0, 0, 0};
    private int flags = 4;
    private int stemV = 0;
    private int italicAngle = 0;
    private int missingWidth = 0;
    private FontType fontType = FontType.TYPE1;
    private int firstChar = 0;
    private int lastChar = 255;

    private Map kerning;

    private boolean useKerning = true;


    /**
     * @see com.wisii.fov.fonts.FontMetrics#getFontName()
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * Returns an URI representing an embeddable font file. The URI will often
     * be a filename or an URL.
     * @return URI to an embeddable font file or null if not available.
     */
    public String getEmbedFileName() {
        return embedFileName;
    }

    /**
     * Returns a Source representing an embeddable font file.
     * @return Source for an embeddable font file or null if not available.
     */
    public Source getEmbedFileSource() {
        if (resolver != null && embedFileName != null) {
            return resolver.resolve(embedFileName);
        } else {
            return null;
        }
    }

    /**
     * Returns the lookup name to an embeddable font file available as a
     * resource.
     * (todo) Remove this method, this should be done using a resource: URI.
     * @return the lookup name
     */
    public String getEmbedResourceName() {
        return embedResourceName;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getAscender()
     */
    public int getAscender() {
        return ascender;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getDescender()
     */
    public int getDescender() {
        return descender;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getCapHeight()
     */
    public int getCapHeight() {
        return capHeight;
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getAscender(int)
     */
    public int getAscender(int size) {
        return size * ascender;
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getDescender(int)
     */
    public int getDescender(int size) {
        return size * descender;
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getCapHeight(int)
     */
    public int getCapHeight(int size) {
        return size * capHeight;
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getXHeight(int)
     */
    public int getXHeight(int size) {
        return size * xHeight;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getFontBBox()
     */
    public int[] getFontBBox() {
        return fontBBox;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getFlags()
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getStemV()
     */
    public int getStemV() {
        return stemV;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getItalicAngle()
     */
    public int getItalicAngle() {
        return italicAngle;
    }

    /**
     * Returns the width to be used when no width is available.
     * @return a character width
     */
    public int getMissingWidth() {
        return missingWidth;
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getFontType()
     */
    public FontType getFontType() {
        return fontType;
    }

    /**
     * Returns the index of the first character defined in this font.
     * @return the index of the first character
     */
    public int getFirstChar() {
        return 0;
        // return firstChar;
        /**(todo) Why is this hardcoded??? This code was in SingleByteFont.java */
    }

    /**
     * Returns the index of the last character defined in this font.
     * @return the index of the last character
     */
    public int getLastChar() {
        return lastChar;
    }

    /**
     * Used to determine if kerning is enabled.
     * @return True if kerning is enabled.
     */
    public boolean isKerningEnabled() {
        return useKerning;
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#hasKerningInfo()
     */
    public final boolean hasKerningInfo() {
        return (isKerningEnabled() && (kerning != null) && !kerning.isEmpty());
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getKerningInfo()
     */
    public final Map getKerningInfo() {
        if (hasKerningInfo()) {
            return kerning;
        } else {
            return java.util.Collections.EMPTY_MAP;
        }
    }


    /* ---- MutableFont interface ---- */

    /**
     * @see com.wisii.fov.fonts.MutableFont#setFontName(String)
     */
    public void setFontName(String name) {
        this.fontName = name;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setEmbedFileName(String)
     */
    public void setEmbedFileName(String path) {
        this.embedFileName = path;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setEmbedResourceName(String)
     */
    public void setEmbedResourceName(String name) {
        this.embedResourceName = name;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setCapHeight(int)
     */
    public void setCapHeight(int capHeight) {
        this.capHeight = capHeight;
    }

    /**
     * Returns the XHeight value of the font.
     * @param xHeight the XHeight value
     */
    public void setXHeight(int xHeight) {
        this.xHeight = xHeight;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setAscender(int)
     */
    public void setAscender(int ascender) {
        this.ascender = ascender;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setDescender(int)
     */
    public void setDescender(int descender) {
        this.descender = descender;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setFontBBox(int[])
     */
    public void setFontBBox(int[] bbox) {
        this.fontBBox = bbox;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setFlags(int)
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setStemV(int)
     */
    public void setStemV(int stemV) {
        this.stemV = stemV;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setItalicAngle(int)
     */
    public void setItalicAngle(int italicAngle) {
        this.italicAngle = italicAngle;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setMissingWidth(int)
     */
    public void setMissingWidth(int width) {
        this.missingWidth = width;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setFontType(FontType)
     */
    public void setFontType(FontType fontType) {
        this.fontType = fontType;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setFirstChar(int)
     */
    public void setFirstChar(int index) {
        this.firstChar = index;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setLastChar(int)
     */
    public void setLastChar(int index) {
        this.lastChar = index;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#setKerningEnabled(boolean)
     */
    public void setKerningEnabled(boolean enabled) {
        this.useKerning = enabled;
    }

    /**
     * Sets the font resolver. Needed for URI resolution.
     * @param resolver the font resolver
     */
    public void setResolver(FontResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * @see com.wisii.fov.fonts.MutableFont#putKerningEntry(Integer, Map)
     */
    public void putKerningEntry(Integer key, Map value) {
        if (kerning == null) {
            kerning = new java.util.HashMap();
        }
        this.kerning.put(key, value);
    }

}
