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

/* $Id: AFPFont.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.fonts;
import java.util.Map;
import com.wisii.fov.fonts.FontType;
import com.wisii.fov.fonts.Typeface;


/**
 * All implemenations of AFP fonts should extend this base class,
 * the object implements the FontMetrics information.
 * <p/>
 */
public abstract class AFPFont extends Typeface {

    /** The font name */
    protected String _name;

    /**
     * Constructor for the base font requires the name.
     * @param name the name of the font
     */
    public AFPFont(String name) {

        _name = name;

    }

    /**
     * @return the name of the font.
     */
    public String getFontName() {
        return _name;
    }

    /**
     * Returns the type of the font.
     * @return the font type
     */
    public FontType getFontType() {
        return FontType.OTHER;
    }

    /**
     * Indicates if the font has kering information.
     * @return True, if kerning is available.
     */
    public boolean hasKerningInfo() {
        return false;
    }

    /**
     * Returns the kerning map for the font.
     * @return the kerning map
     */
    public Map getKerningInfo() {
        return null;
    }

    /**
     * Returns the character set for a given size
     * @param size the font size
     * @return the character set object
     */
    public abstract CharacterSet getCharacterSet(int size);

     /**
     * Determines whether this font contains a particular character/glyph.
     * @param c character to check
     * @return True if the character is supported, Falso otherwise
     */
    public boolean hasChar(char c) {
        return true;
    }

}