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
 *//* $Id: OldExtensionElementMapping.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.extensions;

import java.util.HashMap;

import com.wisii.fov.fo.ElementMapping;
import com.wisii.fov.fo.UnknownXMLObj;

/**
 * Element mapping for the old FOV extension namespace.
 */
public class OldExtensionElementMapping extends ElementMapping {

    /** The old FOV extension namespace URI (FOV 0.20.5 and earlier) */
    public static final String URI = "http://xml.apache.org/fov/extensions";

    /**
     * Constructor.
     */
    public OldExtensionElementMapping() {
        namespaceURI = URI;
    }

    /**
     * Initialize the data structures.
     */
    protected void initialize() {
        if (foObjs == null) {
            foObjs = new HashMap();
            foObjs.put("outline", new UnknownXMLObj.Maker(URI));
            foObjs.put("label", new UnknownXMLObj.Maker(URI));
        }
    }
}
