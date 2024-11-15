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
 *//*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.wisii.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: RasterFont.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.fonts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.fov.render.afp.exceptions.FontRuntimeException;

/**
 * A font where each character is stored as an array of pixels (a bitmap). Such
 * fonts are not easily scalable, in contrast to vectored fonts. With this type
 * of font, the font metrics information is held in character set files (one for
 * each size and style). <p/>
 *
 */
public class RasterFont extends AFPFont {

    /** Static logging instance */
    protected static final Log log = LogFactory.getLog("com.wisii.fov.render.afp.fonts");

    private HashMap _characterSets = new HashMap();

    private CharacterSet _characterSet = null;

    /**
     * Constructor for the raster font requires the name, weight and style
     * attribute to be available as this forms the key to the font.
     *
     * @param name
     *            the name of the font
     */
    public RasterFont(String name) {
        super(name);
    }

    public void addCharacterSet(int size, CharacterSet characterSet) {

        _characterSets.put(String.valueOf(size), characterSet);

        _characterSet = characterSet;

    }

    /**
     * Get the character set metrics for the specified point size.
     *
     * @param size the point size
     * @return the character set metrics
     */
    public CharacterSet getCharacterSet(int size) {

        String pointsize = String.valueOf(size / 1000);
        CharacterSet csm = (CharacterSet) _characterSets.get(pointsize);
        if (csm == null) {
            csm = (CharacterSet) _characterSets.get(size + "mpt");
        }
        if (csm == null) {
            // Get char set with nearest font size
            int distance = Integer.MAX_VALUE;
            for (Iterator it = _characterSets.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry)it.next();
                String key = (String)me.getKey();
                if (!key.endsWith("mpt")) {
                    int mpt = Integer.parseInt(key) * 1000;
                    if (Math.abs(size - mpt) < distance) {
                        distance = Math.abs(size - mpt);
                        pointsize = (String)me.getKey();
                        csm = (CharacterSet)me.getValue();
                    }
                }
            }
            if (csm != null) {
                _characterSets.put(size + "mpt", csm);
                String msg = "No " + (size / 1000) + "pt font " + _name
                    + " found, substituted with " + pointsize + "pt font";
                log.warn(msg);
            }
        }
        if (csm == null) {
            String msg = "No font found for font " + _name
                + " with point size " + pointsize;
            log.error(msg);
            throw new FontRuntimeException(msg);
        }
        return csm;

    }

    /**
     * Get the first character in this font.
     */
    public int getFirstChar() {

        Iterator i = _characterSets.values().iterator();
        if (i.hasNext()) {
            CharacterSet csm = (CharacterSet) i.next();
            return csm.getFirstChar();
        } else {
            String msg = "getFirstChar() - No character set found for font:" + _name;
            log.error(msg);
            throw new FontRuntimeException(msg);
        }

    }

    /**
     * Get the last character in this font.
     */
    public int getLastChar() {

        Iterator i = _characterSets.values().iterator();
        if (i.hasNext()) {
            CharacterSet csm = (CharacterSet) i.next();
            return csm.getLastChar();
        } else {
            String msg = "getLastChar() - No character set found for font:" + _name;
            log.error(msg);
            throw new FontRuntimeException(msg);
        }

    }

    /**
     * The ascender is the part of a lowercase letter that extends above the
     * "x-height" (the height of the letter "x"), such as "d", "t", or "h". Also
     * used to denote the part of the letter extending above the x-height.
     *
     * @param size the point size
     */
    public int getAscender(int size) {

        return getCharacterSet(size).getAscender();

    }

    /**
     * Obtains the height of capital letters for the specified point size.
     *
     * @param size the point size
     */
    public int getCapHeight(int size) {

        return getCharacterSet(size).getCapHeight();

    }

    /**
     * The descender is the part of a lowercase letter that extends below the
     * base line, such as "g", "j", or "p". Also used to denote the part of the
     * letter extending below the base line.
     *
     * @param size the point size
     */
    public int getDescender(int size) {

        return getCharacterSet(size).getDescender();

    }

    /**
     * The "x-height" (the height of the letter "x").
     *
     * @param size the point size
     */
    public int getXHeight(int size) {

        return getCharacterSet(size).getXHeight();

    }

    /**
     * Obtain the width of the character for the specified point size.
     */
    public int getWidth(int character, int size) {

        return getCharacterSet(size).width(character);

    }

    /**
     * Get the getWidth (in 1/1000ths of a point size) of all characters in this
     * character set.
     *
     * @param size
     *            the point size
     * @return the widths of all characters
     */
    public int[] getWidths(int size) {

        return getCharacterSet(size).getWidths();

    }

    /**
     * Get the getWidth (in 1/1000ths of a point size) of all characters in this
     * character set.
     *
     * @return the widths of all characters
     */
    public int[] getWidths() {

        return getWidths(1000);

    }

    /**
     * Map a Unicode character to a code point in the font.
     * @param c character to map
     * @return the mapped character
     */
    public char mapChar(char c) {
        return _characterSet.mapChar(c);
    }

    /**
     * Get the encoding of the font.
     * @return the encoding
     */
    public String getEncoding() {
        return _characterSet.getEncoding();
    }

}