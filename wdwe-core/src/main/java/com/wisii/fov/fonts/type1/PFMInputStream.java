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
 *//* $Id: PFMInputStream.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts.type1;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;

/**
 * This is a helper class for reading PFM files. It defines functions for
 * extracting specific values out of the stream.
 */
public class PFMInputStream extends java.io.FilterInputStream {

    private DataInputStream datain;

    /**
     * Constructs a PFMInputStream based on an InputStream representing the
     * PFM file.
     *
     * @param     in The stream from which to read the PFM file
     */
    public PFMInputStream(InputStream in) {
        super(in);
        datain = new DataInputStream(in);
    }

    /**
     * Parses a one byte value out of the stream.
     *
     * @return The value extracted
     * @throws IOException In case of an I/O problem
     */
    public short readByte() throws IOException {
        short s = datain.readByte();
        // Now, we've got to trick Java into forgetting the sign
        int s1 = (((s & 0xF0) >>> 4) << 4) + (s & 0x0F);
        return (short)s1;
    }

    /**
     * Parses a two byte value out of the stream.
     *
     * @return The value extracted
     * @throws IOException In case of an I/O problem
     */
    public int readShort() throws IOException {
        int i = datain.readShort();

        // Change byte order
        int high = (i & 0xFF00) >>> 8;
        int low = (i & 0x00FF) << 8;
        return low + high;
    }

    /**
     * Parses a four byte value out of the stream.
     *
     * @return The value extracted
     * @throws IOException In case of an I/O problem
     */
    public long readInt() throws IOException {
        int i = datain.readInt();

        // Change byte order
        int i1 = (i & 0xFF000000) >>> 24;
        int i2 = (i & 0x00FF0000) >>> 8;
        int i3 = (i & 0x0000FF00) << 8;
        int i4 = (i & 0x000000FF) << 24;
        return i1 + i2 + i3 + i4;
    }

    /**
     * Parses a zero-terminated string out of the stream.
     *
     * @return The value extracted
     * @throws IOException In case of an I/O problem
     */
    public String readString() throws IOException {
        InputStreamReader reader = new InputStreamReader(in, "ISO-8859-1");
        StringBuffer buf = new StringBuffer();
        int ch = reader.read();
        while (ch != 0) {
            buf.append((char)ch);
            ch = reader.read();
        }
        return buf.toString();
    }

}
