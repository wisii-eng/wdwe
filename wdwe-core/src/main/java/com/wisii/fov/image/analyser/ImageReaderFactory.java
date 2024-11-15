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
 *//* $Id: ImageReaderFactory.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.image.analyser;

// Java
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

// FOV
import com.wisii.fov.image.FovImage;
import com.wisii.fov.apps.FOUserAgent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Factory for ImageReader objects.
 *
 * @author    Pankaj Narula
 * @version   $Id: ImageReaderFactory.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $
 */
public class ImageReaderFactory {

    private static ArrayList formats = new ArrayList();

    protected static Log log = LogFactory.getLog(ImageReaderFactory.class);

    static {
        registerFormat(new JPEGReader());
        registerFormat(new BMPReader());
        registerFormat(new GIFReader());
        registerFormat(new PNGReader());
        registerFormat(new TIFFReader());
        registerFormat(new EPSReader());
        registerFormat(new EMFReader());
        // the xml parser through batik closes the stream when finished
        // so there is a workaround in the SVGReader
        registerFormat(new SVGReader());
        registerFormat(new XMLReader());
    }

    /**
     * Registers a new ImageReader.
     *
     * @param reader  An ImageReader instance
     */
    public static void registerFormat(ImageReader reader) {
        formats.add(reader);
    }

    /**
     * ImageReader maker.
     *
     * @param uri  URI to the image
     * @param in   image input stream
     * @param ua   user agent
     * @return     An ImageInfo object describing the image
     */
    public static FovImage.ImageInfo make(String uri, InputStream in,
            FOUserAgent ua) {

        ImageReader reader;
        try {
            for (int count = 0; count < formats.size(); count++) {
                reader = (ImageReader) formats.get(count);
                FovImage.ImageInfo info = reader.verifySignature(uri, in, ua);
                if (info != null) {
                    return info;
                }
            }
        } catch (IOException ex) {
            log.error("Error while recovering Image Informations ("
                + uri + ")", ex);
        }
        return null;
    }

}

