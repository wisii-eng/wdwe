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

/* $Id: MapCodedFont.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.modca;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.wisii.fov.render.afp.exceptions.FontRuntimeException;
import com.wisii.fov.render.afp.fonts.AFPFont;
import com.wisii.fov.render.afp.fonts.CharacterSet;
import com.wisii.fov.render.afp.fonts.OutlineFont;
import com.wisii.fov.render.afp.fonts.RasterFont;
import com.wisii.fov.render.afp.tools.BinaryUtils;

/**
 * The Map Coded Font structured field maps a unique coded font resource local
 * ID, which may be embedded one or more times within an object's data and
 * descriptor, to the identifier of a coded font resource object. Additionally,
 * the Map Coded Font structured field specifies a set of resource attributes
 * for the coded font.
 *
 * @author <a href="mailto:pete@townsend.uk.com">Pete Townsend </a>
 */
public class MapCodedFont extends AbstractAFPObject {

    /**
     * The collection of map coded fonts (maximum of 254)
     */
    private ArrayList _fontlist = null;

    /**
     * Constructor for the MapCodedFont
     */
    public MapCodedFont() {

        _fontlist = new ArrayList();

    }

    /**
     * Accessor method to write the AFP datastream for the Map Coded Font
     * @param os The stream to write to
     * @throws java.io.IOException
     */
    public void writeDataStream(OutputStream os)
        throws IOException {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(0x5A);
        baos.write(new byte[] { 0x00, 0x00 });

        // Field identifier for a MapCodedFont
        baos.write(new byte[] { (byte) 0xD3, (byte) 0xAB, (byte) 0x8A });

        // Reserved
        baos.write(new byte[] { 0x00, 0x00, 0x00 });

        for (int i = 0; i < _fontlist.size(); i++) {

            FontDefinition fd = (FontDefinition) _fontlist.get(i);

            // Start of repeating groups (occurs 1 to 254)
            baos.write(0x00);

            if (fd._scale == 0) {
                // Raster Font
                baos.write(0x22); // Length of 34
            } else {
                // Outline Font
                baos.write(0x3A); // Length of 58
            }

            // Font Character Set Name Reference
            baos.write(0x0C);
            baos.write(0x02);
            baos.write((byte) 0x86);
            baos.write(0x00);
            baos.write(fd._characterset);

            // Font Code Page Name Reference
            baos.write(0x0C);
            baos.write(0x02);
            baos.write((byte) 0x85);
            baos.write(0x00);
            baos.write(fd._codepage);

            // Character Rotation
            baos.write(0x04);
            baos.write(0x26);
            baos.write(fd._orientation);
            baos.write(0x00);

            // Resource Local Identifier
            baos.write(0x04);
            baos.write(0x24);
            baos.write(0x05);
            baos.write(fd._fontReferenceKey);

            if (fd._scale != 0) {
                // Outline Font (triplet '1F')
                baos.write(0x14);
                baos.write(0x1F);
                baos.write(0x00);
                baos.write(0x00);

                baos.write(BinaryUtils.convert(fd._scale, 2)); // Height
                baos.write(new byte[] { 0x00, 0x00 }); // Width

                baos.write(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00 });

                baos.write(0x60);

                // Outline Font (triplet '5D')
                baos.write(0x04);
                baos.write(0x5D);
                baos.write(BinaryUtils.convert(fd._scale, 2));
            }

        }

        byte[] data = baos.toByteArray();

        // Set the total record length
        byte[] rl1 = BinaryUtils.convert(data.length - 1, 2);
        data[1] = rl1[0];
        data[2] = rl1[1];

        os.write(data);

    }

    /**
     * Add a font definition on the the map coded font object.
     *
     * @param fontReference
     *            the font number used as the resource identifier
     * @param font
     *            the font
     * @param size
     *            the size of the font
     * @param orientation
     *            the orientation of the font
     */
    public void addFont(byte fontReference, AFPFont font, int size, int orientation)
        throws MaximumSizeExceededException {

        FontDefinition fd = new FontDefinition();

        fd._fontReferenceKey = fontReference;

        switch (orientation) {
            case 90:
                fd._orientation = 0x2D;
                break;
            case 180:
                fd._orientation = 0x5A;
                break;
            case 270:
                fd._orientation = (byte) 0x87;
                break;
            default:
                fd._orientation = 0x00;
                break;
        }

        try {

            if (font instanceof RasterFont) {

                RasterFont raster = (RasterFont) font;
                CharacterSet cs = raster.getCharacterSet(size);
                if (cs == null) {
                    String msg = "Character set not found for font "
                        + font.getFontName() + " with point size " + size;
                    log.error(msg);
                    throw new FontRuntimeException(msg);
                }

                fd._characterset = cs.getNameBytes();

                if (fd._characterset.length != 8) {
                    throw new IllegalArgumentException("The character set "
                        + new String(fd._characterset,
                        AFPConstants.EBCIDIC_ENCODING)
                        + " must have a fixed length of 8 characters.");
                }

                fd._codepage = cs.getCodePage().getBytes(
                    AFPConstants.EBCIDIC_ENCODING);

                if (fd._codepage.length != 8) {
                    throw new IllegalArgumentException("The code page "
                        + new String(fd._codepage,
                        AFPConstants.EBCIDIC_ENCODING)
                        + " must have a fixed length of 8 characters.");
                }

            } else if (font instanceof OutlineFont) {

                OutlineFont outline = (OutlineFont) font;
                CharacterSet cs = outline.getCharacterSet();
                fd._characterset = cs.getNameBytes();

                // There are approximately 72 points to 1 inch or 20 1440ths per point.

                fd._scale = ((size / 1000) * 20);

                fd._codepage = cs.getCodePage().getBytes(
                    AFPConstants.EBCIDIC_ENCODING);

                if (fd._codepage.length != 8) {
                    throw new IllegalArgumentException("The code page "
                        + new String(fd._codepage,
                        AFPConstants.EBCIDIC_ENCODING)
                        + " must have a fixed length of 8 characters.");
                }

            } else {
                String msg = "Font of type " + font.getClass().getName()
                    + " not recognized.";
                log.error(msg);
                throw new FontRuntimeException(msg);
            }

            if (_fontlist.size() > 253) {

                // Throw an exception if the size is exceeded
                throw new MaximumSizeExceededException();

            } else {

                _fontlist.add(fd);

            }

        } catch (UnsupportedEncodingException ex) {

            throw new FontRuntimeException("Failed to create font "
                + " due to a UnsupportedEncodingException", ex);

        }

    }

    /**
     * Private utility class used as a container for font attributes
     */
    private class FontDefinition {

        /**
         * The code page of the font
         */
        private byte[] _codepage;

        /**
         * The character set of the font
         */
        private byte[] _characterset;

        /**
         * The font reference key
         */
        private byte _fontReferenceKey;

        /**
         * The orientation of the font
         */
        private byte _orientation;

        /**
         * The scale (only specified for outline fonts)
         */
        private int _scale = 0;

    }

}