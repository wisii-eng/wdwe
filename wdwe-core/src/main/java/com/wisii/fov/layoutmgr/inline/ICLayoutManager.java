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
 *//* $Id: ICLayoutManager.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

// Java
import java.util.List;

// FOV
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.fo.flow.InlineContainer;
/**
 * This creates a single inline container area after
 * laying out the child block areas. All footnotes, floats
 * and id areas are maintained for later retrieval.
 */
public class ICLayoutManager extends LeafNodeLayoutManager {
    private InlineContainer fobj;
    private List childrenLM;

    public ICLayoutManager(InlineContainer node, List childLM) {
        super(node);
        fobj = node;
        childrenLM = childLM;
    }

    public InlineArea get(int index) {
        return null;
    }

    protected void addId() {
        getPSLM().addIDToPage(fobj.getId());
    }
}
