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
 *//* $Id: TTFMtxEntry.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts.truetype;

import java.util.List;

/**
 * This class represents a TrueType Mtx Entry.
 */
class TTFMtxEntry {

    private int wx;
    private int lsb;
    private String name = "";
    private int index;
    private List unicodeIndex = new java.util.ArrayList();
    private int[] boundingBox = new int[4];
    private long offset;
    private byte found = 0;

    /**
     * Returns a String representation of this object.
     *
     * @param t TTFFile to use for unit conversion
     * @return String String representation
     */
    public String toString(TTFFile t) {
        return "Glyph " + name + " index: " + getIndexAsString() + " bbox ["
             + t.convertTTFUnit2PDFUnit(boundingBox[0]) + " "
             + t.convertTTFUnit2PDFUnit(boundingBox[1]) + " "
             + t.convertTTFUnit2PDFUnit(boundingBox[2]) + " "
             + t.convertTTFUnit2PDFUnit(boundingBox[3]) + "] wx: "
             + t.convertTTFUnit2PDFUnit(wx);
    }

    /**
     * Returns the boundingBox.
     * @return int[]
     */
    public int[] getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the boundingBox.
     * @param boundingBox The boundingBox to set
     */
    public void setBoundingBox(int[] boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * Returns the found.
     * @return byte
     */
    public byte getFound() {
        return found;
    }

    /**
     * Returns the index.
     * @return int
     */
    public int getIndex() {
        return index;
    }

    /**
     * Determines whether this index represents a reserved character.
     * @return True if it is reserved
     */
    public boolean isIndexReserved() {
        return (getIndex() >= 32768) && (getIndex() <= 65535);
    }

    /**
     * Returns a String representation of the index taking into account if
     * the index is in the reserved range.
     * @return index as String
     */
    public String getIndexAsString() {
        if (isIndexReserved()) {
            return Integer.toString(getIndex()) + " (reserved)";
        } else {
            return Integer.toString(getIndex());
        }
    }

    /**
     * Returns the lsb.
     * @return int
     */
    public int getLsb() {
        return lsb;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the offset.
     * @return long
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Returns the unicodeIndex.
     * @return List
     */
    public List getUnicodeIndex() {
        return unicodeIndex;
    }

    /**
     * Returns the wx.
     * @return int
     */
    public int getWx() {
        return wx;
    }

    /**
     * Sets the found.
     * @param found The found to set
     */
    public void setFound(byte found) {
        this.found = found;
    }

    /**
     * Sets the index.
     * @param index The index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Sets the lsb.
     * @param lsb The lsb to set
     */
    public void setLsb(int lsb) {
        this.lsb = lsb;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the offset.
     * @param offset The offset to set
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * Sets the wx.
     * @param wx The wx to set
     */
    public void setWx(int wx) {
        this.wx = wx;
    }


}
