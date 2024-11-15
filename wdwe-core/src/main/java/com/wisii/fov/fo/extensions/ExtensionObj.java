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
 *//* $Id: ExtensionObj.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.extensions;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FOEventHandler;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;


/**
 * Base class for pdf bookmark extension objects.
 */
public abstract class ExtensionObj extends FObj {

    /**
     * Create a new extension object.
     *
     * @param parent the parent formatting object
     */
    public ExtensionObj(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FONode#processNode
     */
    public void processNode(String elementName, Locator locator,
                            Attributes attlist, PropertyList pList)
        throws FOVException
    {
        // Empty
    }

    /**
     * Create a default property list for this element.
     */
    protected PropertyList createPropertyList(PropertyList parent,
                FOEventHandler foEventHandler) throws FOVException {
        return null;
    }
}

