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
 *//* $Id: LazyFont.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.fov.apps.FOVException;
import org.xml.sax.InputSource;

/**
 * This class is used to defer the loading of a font until it is really used.
 */
public class LazyFont extends Typeface implements FontDescriptor {

    private static Log log = LogFactory.getLog(LazyFont.class);

    private String metricsFileName = null;
    private String fontEmbedPath = null;
    private boolean useKerning = false;

    private boolean isMetricsLoaded = false;
    private Typeface realFont = null;
    private FontDescriptor realFontDescriptor = null;

    private FontResolver resolver = null;

    /**
     * Main constructor
     * @param fontEmbedPath path to embeddable file (may be null)
     * @param metricsFileName path to the metrics XML file
     * @param useKerning True, if kerning should be enabled
     * @param resolver the font resolver to handle font URIs
     */
    public LazyFont(String fontEmbedPath, String metricsFileName
                    , boolean useKerning, FontResolver resolver) {
        this.metricsFileName = metricsFileName;
        this.fontEmbedPath = fontEmbedPath;
        this.useKerning = useKerning;
        this.resolver = resolver;
    }

    private void load(boolean fail) {
        if (!isMetricsLoaded) {
            try {
                /**@todo Possible thread problem here */
                FontReader reader = null;
                if (resolver != null) {
                    Source source = resolver.resolve(metricsFileName);
                    if (source == null) {
                        String err = "Cannot load font: failed to create Source from metrics file "
                            + metricsFileName;
                        if (fail) {
                            throw new RuntimeException(err);
                        } else {
                            log.error(err);
                        }
                        return;
                    }
                    InputStream in = null;
                    if (source instanceof StreamSource) {
                        in = ((StreamSource) source).getInputStream();
                    }
                    if (in == null && source.getSystemId() != null) {
                        in = new java.net.URL(source.getSystemId()).openStream();
                    }
                    if (in == null) {
                        String err = "Cannot load font: failed to create InputStream from"
                            + " Source for metrics file " + metricsFileName;
                        if (fail) {
                            throw new RuntimeException(err);
                        } else {
                            log.error(err);
                        }
                        return;
                    }
                    InputSource src = new InputSource(in);
                    src.setSystemId(source.getSystemId());
                    reader = new FontReader(src);
                } else {
                    reader
                        = new FontReader(new InputSource(new URL(metricsFileName).openStream()));
                }
                reader.setKerningEnabled(useKerning);
                reader.setFontEmbedPath(fontEmbedPath);
                reader.setResolver(resolver);
                realFont = reader.getFont();
                if (realFont instanceof FontDescriptor) {
                    realFontDescriptor = (FontDescriptor) realFont;
                }
                // log.debug("Metrics " + metricsFileName + " loaded.");
            } catch (FOVException fovex) {
                log.error("Failed to read font metrics file " + metricsFileName, fovex);
                if (fail) {
                    throw new RuntimeException(fovex.getMessage());
                }
            } catch (IOException ioex) {
                log.error("Failed to read font metrics file " + metricsFileName, ioex);
                if (fail) {
                    throw new RuntimeException(ioex.getMessage());
                }
            }
            isMetricsLoaded = true;
        }
    }

    /**
     * Gets the real font.
     * @return the real font
     */
    public Typeface getRealFont() {
        load(false);
        return realFont;
    }

    // ---- Font ----
    /**
     * @see com.wisii.fov.fonts.Typeface#getEncoding()
     */
    public String getEncoding() {
        load(true);
        return realFont.getEncoding();
    }

    /**
     * @see com.wisii.fov.fonts.Typeface#mapChar(char)
     */
    public char mapChar(char c) {
        load(true);
        return realFont.mapChar(c);
    }

    /**
     * @see com.wisii.fov.fonts.Typeface#hasChar(char)
     */
    public boolean hasChar(char c) {
        load(true);
        return realFont.hasChar(c);
    }

    /**
     * @see com.wisii.fov.fonts.Typeface#isMultiByte()
     */
    public boolean isMultiByte() {
        load(true);
        return realFont.isMultiByte();
    }

    // ---- FontMetrics interface ----
    /**
     * @see com.wisii.fov.fonts.FontMetrics#getFontName()
     */
    public String getFontName() {
        load(true);
        return realFont.getFontName();
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getMaxAscent(int)
     */
    public int getMaxAscent(int size) {
        load(true);
        return realFont.getMaxAscent(size);
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getAscender(int)
     */
    public int getAscender(int size) {
        load(true);
        return realFont.getAscender(size);
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getCapHeight(int)
     */
    public int getCapHeight(int size) {
        load(true);
        return realFont.getCapHeight(size);
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getDescender(int)
     */
    public int getDescender(int size) {
        load(true);
        return realFont.getDescender(size);
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getXHeight(int)
     */
    public int getXHeight(int size) {
        load(true);
        return realFont.getXHeight(size);
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getWidth(int, int)
     */
    public int getWidth(int i, int size) {
        load(true);
        return realFont.getWidth(i, size);
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getWidths()
     */
    public int[] getWidths() {
        load(true);
        return realFont.getWidths();
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#hasKerningInfo()
     */
    public boolean hasKerningInfo() {
        load(true);
        return realFont.hasKerningInfo();
    }

    /**
     * @see com.wisii.fov.fonts.FontMetrics#getKerningInfo()
     */
    public Map getKerningInfo() {
        load(true);
        return realFont.getKerningInfo();
    }

    // ---- FontDescriptor interface ----
    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getCapHeight()
     */
    public int getCapHeight() {
        load(true);
        return realFontDescriptor.getCapHeight();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getDescender()
     */
    public int getDescender() {
        load(true);
        return realFontDescriptor.getDescender();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getAscender()
     */
    public int getAscender() {
        load(true);
        return realFontDescriptor.getAscender();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getFlags()
     */
    public int getFlags() {
        load(true);
        return realFontDescriptor.getFlags();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getFontBBox()
     */
    public int[] getFontBBox() {
        load(true);
        return realFontDescriptor.getFontBBox();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getItalicAngle()
     */
    public int getItalicAngle() {
        load(true);
        return realFontDescriptor.getItalicAngle();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getStemV()
     */
    public int getStemV() {
        load(true);
        return realFontDescriptor.getStemV();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#getFontType()
     */
    public FontType getFontType() {
        load(true);
        return realFontDescriptor.getFontType();
    }

    /**
     * @see com.wisii.fov.fonts.FontDescriptor#isEmbeddable()
     */
    public boolean isEmbeddable() {
        load(true);
        return realFontDescriptor.isEmbeddable();
    }

}

