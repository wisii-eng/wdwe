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
 *//* $Id: GIFReader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.image.io;

// Java
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import com.wisii.image.ImageInfo;


/**
 * ImageReader object for GIF image type.
 *
 * @author    Pankaj Narula
 * @version   $Id: GIFReader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $
 */
public class GIFReader implements ImageReader {

    private static final int GIF_SIG_LENGTH = 10;

    /** @see com.wisii.image.io.fov.image.analyser.ImageReader */
    public ImageInfo verifySignature(InputStream bis) throws IOException {
        byte[] header = getDefaultHeader(bis);
        boolean supported = ((header[0] == 'G')
                && (header[1] == 'I')
                && (header[2] == 'F')
                && (header[3] == '8')
                && (header[4] == '7' || header[4] == '9')
                && (header[5] == 'a'));
        if (supported) {
            ImageInfo info = getDimension(header);
            info.setMimeType(getMimeType());
            return info;
        } else {
            return null;
        }
    }

    /**
     * Returns the MIME type supported by this implementation.
     *
     * @return   The MIME type
     */
    public String getMimeType() {
        return "image/gif";
    }

    private ImageInfo getDimension(byte[] header) {
        ImageInfo info = new ImageInfo();
        // little endian notation
        int byte1 = header[6] & 0xff;
        int byte2 = header[7] & 0xff;
        info.setWidth(((byte2 << 8) | byte1) & 0xffff);

        byte1 = header[8] & 0xff;
        byte2 = header[9] & 0xff;
        info.setHeight(((byte2 << 8) | byte1) & 0xffff);
        int flags = header[9] & 0xff;
        int bitsPerPixel = ((flags >> 4) & 0x07) + 1;
//        System.out.println("bitsPerPixel:"+bitsPerPixel);
        int sc = bitsPerPixel*Toolkit.getDefaultToolkit().getScreenResolution();
        info.setDpiHorizontal(sc);
        info.setDpiVertical(sc);
        return info;
    }

    private byte[] getDefaultHeader(InputStream imageStream)
                throws IOException {
        byte[] header = new byte[GIF_SIG_LENGTH+1];
        try {
            imageStream.mark(GIF_SIG_LENGTH + 1);
            imageStream.read(header);
            imageStream.reset();
        } catch (IOException ex) {
            try {
                imageStream.reset();
            } catch (IOException exbis) {
                // throw the original exception, not this one
            }
            throw ex;
        }
        return header;
    }

}

