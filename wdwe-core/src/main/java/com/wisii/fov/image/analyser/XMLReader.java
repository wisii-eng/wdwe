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
 *//* $Id: XMLReader.java,v 1.3 2007/09/29 06:08:00 hzl Exp $ */

package com.wisii.fov.image.analyser;

// Java
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

// XML
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// FOV
import com.wisii.fov.image.FovImage;
import com.wisii.fov.apps.FOUserAgent;

// Commons-Logging
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** ImageReader object for XML document image type. */
public class XMLReader implements ImageReader {

    /**
     * logging instance
     */
    private Log log = LogFactory.getLog(XMLReader.class);

    private static Map converters = new java.util.HashMap();

    /**
     * Registers a Converter implementation with XMLReader.
     *
     * @param ns    The namespace to associate with this converter
     * @param conv  The actual Converter implementation
     */
    public static void setConverter(String ns, Converter conv) {
        converters.put(ns, conv);
    }

    /** @see com.wisii.fov.image.analyser.ImageReader */
    public FovImage.ImageInfo verifySignature(String uri, InputStream fis,
            FOUserAgent ua)
        throws IOException {
        FovImage.ImageInfo info = loadImage(uri, fis, ua);
        info.originalURI = uri;
        if (info != null) {
            IOUtils.closeQuietly(fis);
        }
        return info;
    }

    /**
     * Returns the MIME type supported by this implementation.
     *
     * @return   The MIME type
     */
    public String getMimeType() {
        return "text/xml";
    }

    /**
     * Creates an ImageInfo object from an XML image read from a stream.
     *
     * (todo) This means the external svg document will be loaded twice. Possibly need
     * a slightly different design for the image stuff.
     *
     * @param uri  The URI to the image
     * @param bis  The InputStream
     * @param ua   The user agent
     * @return     An ImageInfo object describing the image
     */
    protected FovImage.ImageInfo loadImage(String uri, InputStream bis,
            FOUserAgent ua) {
        return createDocument(bis, ua);
    }

    /**
     * Creates an ImageInfo object from an XML image read from a stream.
     *
     * @param is  The InputStream
     * @param ua  The user agent
     * @return    An ImageInfo object describing the image
     */
    public FovImage.ImageInfo createDocument(InputStream is, FOUserAgent ua) {
        Document doc = null;
        FovImage.ImageInfo info = new FovImage.ImageInfo();
        info.mimeType = getMimeType();

        try {
            int length = is.available();
            is.mark(length);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            doc = dbf.newDocumentBuilder().parse(is);
            info.data = doc;

            Element root = doc.getDocumentElement();
            log.debug("XML image namespace: " + root.getAttribute("xmlns"));
            String ns = root.getAttribute("xmlns");
            info.str = ns;

            Converter conv = (Converter) converters.get(ns);
            if (conv != null) {
                FovImage.ImageInfo i = conv.convert(doc);
                if (i != null) {
                    info = i;
                }
            }
        } catch (Exception e) {
            log.warn("Error while constructing image from XML", e);
            try {
                is.reset();
            } catch (IOException ioe) {
                // throw the original exception, not this one
            }
            return null;
        }
        return info;
    }

    /**
     * This interface is to be implemented for XML to image converters.
     */
    public static interface Converter {

        /**
         * This method is called for a DOM document to be converted into an
         * ImageInfo object.
         *
         * @param doc   The DOM document to convert
         * @return      An ImageInfo object describing the image
         */
        FovImage.ImageInfo convert(Document doc);
    }

}

