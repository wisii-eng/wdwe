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
 *//* $Id: LinkResolver.java,v 1.1 2007/04/12 06:41:18 cvsuser Exp $ */

package com.wisii.fov.area;

// Java
import java.util.List;
import java.io.Serializable;

// FOV
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.Resolvable;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.Area;

/**
 * Link resolving for resolving internal links.
 */
public class LinkResolver implements Resolvable, Serializable {
    private boolean resolved = false;
    private String idRef;
    private Area area;

    /**
     * Create a new link resolver.
     *
     * @param id the id to resolve
     * @param a the area that will have the link attribute
     */
    public LinkResolver(String id, Area a) {
        idRef = id;
        area = a;
    }

    /**
     * @return true if this link is resolved
     */
    public boolean isResolved() {
        return resolved;
    }

    /**
     * Get the references for this link.
     *
     * @return the String array of references.
     */
    public String[] getIDRefs() {
        return new String[] {idRef};
    }

    /**
     * Resolve by adding an internal link.
     *
     * @see com.wisii.fov.area.Resolvable#resolveIDRef(String, List)
     */
    public void resolveIDRef(String id, List pages) {
        if (idRef.equals(id) && pages != null) {
            resolved = true;
            PageViewport page = (PageViewport)pages.get(0);
            area.addTrait(Trait.INTERNAL_LINK, page.getKey());
        }
    }
}
