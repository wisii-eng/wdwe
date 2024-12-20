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
 */package com.wisii.fov.render.print;

import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.render.AbstractRendererMaker;
import com.wisii.fov.render.Renderer;

/**
 * RendererMaker for the Print Renderer.
 */
public class PrintRendererMaker extends AbstractRendererMaker
{

    private static final String[] MIMES = new String[] {MimeConstants.MIME_WISII_PRINT};

    /**@see com.wisii.fov.render.AbstractRendererMaker */
    public Renderer makeRenderer(FOUserAgent ua)
    {
        return new PrintRenderer();
    }

    /** @see com.wisii.fov.render.AbstractRendererMaker#needsOutputStream() */
    public boolean needsOutputStream()
    {
        return false;
    }

    /** @see com.wisii.fov.render.AbstractRendererMaker#getSupportedMimeTypes() */
    public String[] getSupportedMimeTypes()
    {
        return MIMES;
    }

}
