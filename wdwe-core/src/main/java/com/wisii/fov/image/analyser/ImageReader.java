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
 *//* $Id: ImageReader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.image.analyser;

// Java
import java.io.InputStream;
import java.io.IOException;

// FOV
import com.wisii.fov.image.FovImage;
import com.wisii.fov.apps.FOUserAgent;

/**
 * ImageReader objects read image headers to determine the image size.
 *
 * @author    Pankaj Narula
 * @version   $Id: ImageReader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $
 */
public interface ImageReader {

    /**
     * Verify image type. If the stream does not contain image data expected by
     * the reader it must reset the stream to the start. This is so that the
     * next reader can start reading from the start. The reader must not close
     * the stream unless it can handle the image and it has read the
     * information.
     *
     * @param bis              Image buffered input stream
     * @param uri              URI to the image
     * @param ua               The user agent
     * @return                 <code>true</code> if image type is the handled one
     * @exception IOException  if an I/O error occurs
     */
    FovImage.ImageInfo verifySignature(String uri, InputStream bis,
            FOUserAgent ua)
        throws IOException;

}

