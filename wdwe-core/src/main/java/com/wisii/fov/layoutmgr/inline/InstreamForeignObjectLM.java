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
 *//* $Id: InstreamForeignObjectLM.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.area.Area;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.ForeignObject;
import com.wisii.fov.fo.XMLObj;
import com.wisii.fov.fo.flow.InstreamForeignObject;

/**
 * LayoutManager for the fo:instream-foreign-object formatting object
 */
public class InstreamForeignObjectLM extends AbstractGraphicsLayoutManager {

    private InstreamForeignObject fobj;

    /**
     * Constructor
     * @param node the formatting object that creates this area
     */
    public InstreamForeignObjectLM(InstreamForeignObject node) {
        super(node);
        fobj = node;
    }

    /**
     * Get the inline area created by this element.
     *
     * @return the inline area
     */
    protected Area getChildArea() {
        XMLObj child = (XMLObj) fobj.getChildXMLObj();

        org.w3c.dom.Document doc = child.getDOMDocument();
        String ns = child.getNamespaceURI();

        /* 【添加：START】 by  李晓光 2009-2-3 */
        ForeignObject foreign = new ForeignObject(doc, ns);
        foreign.addTrait(Trait.IMAGE_LAYER, new Integer(fobj.getLayer()));
        return foreign;
        /* 【添加：END】 by  李晓光 2009-2-3 */
        /* 【删除：START】 by  李晓光 2009-2-3 */
        /*return new ForeignObject(doc, ns);*/
        /* 【删除：END】 by  李晓光 2009-2-3 */
    }

}

