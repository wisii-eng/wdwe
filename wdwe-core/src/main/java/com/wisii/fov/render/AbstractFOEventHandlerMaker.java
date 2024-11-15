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
 *//* $Id: AbstractFOEventHandlerMaker.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.render;

import java.io.OutputStream;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.fo.FOEventHandler;

/**
 * Base class for factory classes which instantiate FOEventHandlers and provide information
 * about them.
 */
public abstract class AbstractFOEventHandlerMaker {

    /**
     * Instantiates a new FOEventHandler.
     * @param ua the user agent
     * @param out OutputStream for the FOEventHandler to use
     * @return the newly instantiated FOEventHandler
     * @throws FOVException if a problem occurs while creating the event handler
     */
    public abstract FOEventHandler makeFOEventHandler(FOUserAgent ua, OutputStream out)
            throws FOVException;

    /**
     * @return Indicates whether this renderer requires an OutputStream to work with.
     */
    public abstract boolean needsOutputStream();

    /**
     * @return an array of MIME types the renderer supports.
     */
    public abstract String[] getSupportedMimeTypes();

    /**
     * Indicates whether a specific MIME type is supported by this renderer.
     * @param mimeType the MIME type (ex. "application/rtf")
     * @return true if the MIME type is supported
     */
    public boolean isMimeTypeSupported(String mimeType) {
        String[] mimes = getSupportedMimeTypes();
        for (int i = 0; i < mimes.length; i++) {
            if (mimes[i].equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

}
