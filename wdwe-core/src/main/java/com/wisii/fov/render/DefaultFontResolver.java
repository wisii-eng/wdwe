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
 *//* $Id: DefaultFontResolver.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.render;

import javax.xml.transform.Source;

import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.fonts.FontResolver;

/**
 * Default FontResolver implementation which uses the FOUserAgent to resolve font URIs.
 */
public class DefaultFontResolver implements FontResolver {

    private FOUserAgent userAgent;

    /**
     * Main constructor.
     * @param userAgent the user agent
     */
    public DefaultFontResolver(FOUserAgent userAgent) {
        this.userAgent = userAgent;
    }

    /** @see com.wisii.fov.fonts.FontResolver#resolve(java.lang.String) */
    public Source resolve(String href) {
        return userAgent.resolveURI(href, userAgent.getFontBaseURL());
    }

}
