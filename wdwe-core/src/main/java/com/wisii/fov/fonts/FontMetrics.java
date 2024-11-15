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
 *//* $Id: FontMetrics.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts;

import java.util.Map;


/**
 * Main interface for access to font metrics.
 */
public interface FontMetrics {

    /**
     * Returns the font name.
     * @return the font name
     */
    String getFontName();


    /**
     * Returns the type of the font.
     * @return the font type
     */
    FontType getFontType();


    /**
     * Returns the maximum ascent of the font described by this
     * FontMetrics object. Note: This is not the same as getAscender().
     * @param size font size
     * @return ascent in milliponts
     */
    int getMaxAscent(int size);

    /**
     * Returns the ascent of the font described by this
     * FontMetrics object. It returns the nominal ascent within the em box.
     * @param size font size
     * @return ascent in milliponts
     */
    int getAscender(int size);

    /**
     * Returns the size of a capital letter measured from the font's baseline.
     * @param size font size
     * @return height of capital characters
     */
    int getCapHeight(int size);


    /**
     * Returns the descent of the font described by this
     * FontMetrics object.
     * @param size font size
     * @return descent in milliponts
     */
    int getDescender(int size);


    /**
     * Determines the typical font height of this
     * FontMetrics object
     * @param size font size
     * @return font height in millipoints
     */
    int getXHeight(int size);

    /**
     * Return the width (in 1/1000ths of point size) of the character at
     * code point i.
     * @param i code point index
     * @param size font size
     * @return the width of the character
     */
    int getWidth(int i, int size);

    /**
     * Return the array of widths.
     * <p>
     * This is used to get an array for inserting in an output format.
     * It should not be used for lookup.
     * @return an array of widths
     */
    int[] getWidths();

    /**
     * Indicates if the font has kering information.
     * @return True, if kerning is available.
     */
    boolean hasKerningInfo();

    /**
     * Returns the kerning map for the font.
     * @return the kerning map
     */
    Map getKerningInfo();

}
