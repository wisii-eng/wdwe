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
 *//* $Id: PFBData.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts.type1;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Class that represents the contents of a PFB file.
 *
 * @see PFBParser
 */
public class PFBData {

    /**
     * Raw format, no special file structure
     */
    public static final int PFB_RAW = 0;

    /**
     * PC format
     */
    public static final int PFB_PC  = 1;

    /**
     * MAC Format (unsupported, yet)
     */
    public static final int PFB_MAC = 2;

    private int pfbFormat; //One of the PFB_* constants
    private byte[] headerSegment;
    private byte[] encryptedSegment;
    private byte[] trailerSegment;


    /**
     * Sets the PFB format the font was loaded with.
     * @param format one of the PFB_* constants
     */
    public void setPFBFormat(int format) {
        switch (format) {
            case PFB_RAW:
            case PFB_PC:
                this.pfbFormat = format;
                break;
            case PFB_MAC:
                throw new UnsupportedOperationException("Mac 格式化没有执行完");
            default:
                throw new IllegalArgumentException("无效的PFB格式化值 " + format);
        }
    }


    /**
     * Returns the format the font was loaded with.
     * @return int one of the PFB_* constants
     */
    public int getPFBFormat() {
        return this.pfbFormat;
    }

    /**
     * Sets the header segment of the font file.
     * @param headerSeg the header segment
     */
    public void setHeaderSegment(byte[] headerSeg) {
        this.headerSegment = headerSeg;
    }

    /**
     * Sets the encrypted segment of the font file.
     * @param encryptedSeg the encrypted segment
     */
    public void setEncryptedSegment(byte[] encryptedSeg) {
        this.encryptedSegment = encryptedSeg;
    }

    /**
     * Sets the trailer segment of the font file.
     * @param trailerSeg the trailer segment
     */
    public void setTrailerSegment(byte[] trailerSeg) {
        this.trailerSegment = trailerSeg;
    }

    /**
     * Returns the full length of the raw font file.
     * @return int the raw file length
     */
    public int getLength() {
        return getLength1() + getLength2() + getLength3();
    }


    /**
     * Returns the Length1 (length of the header segment).
     * @return int Length1
     */
    public int getLength1() {
        return this.headerSegment.length;
    }


    /**
     * Returns the Length2 (length of the encrypted segment).
     * @return int Length2
     */
    public int getLength2() {
        return this.encryptedSegment.length;
    }


    /**
     * Returns the Length3 (length of the trailer segment).
     * @return int Length3
     */
    public int getLength3() {
        return this.trailerSegment.length;
    }


    /**
     * Writes the PFB file in raw format to an OutputStream.
     * @param out the OutputStream to write to
     * @throws IOException In case of an I/O problem
     */
    public void outputAllParts(OutputStream out) throws IOException {
        out.write(this.headerSegment);
        out.write(this.encryptedSegment);
        out.write(this.trailerSegment);
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "PFB: format=" + getPFBFormat()
                + " len1=" + getLength1()
                + " len2=" + getLength2()
                + " len3=" + getLength3();
    }

}
