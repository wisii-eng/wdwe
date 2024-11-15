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
 *//* $Id: KnuthInlineBox.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr.inline;

import com.wisii.fov.layoutmgr.inline.AlignmentContext;
import com.wisii.fov.layoutmgr.FootnoteBodyLayoutManager;
import com.wisii.fov.layoutmgr.KnuthBox;
import com.wisii.fov.layoutmgr.Position;

public class KnuthInlineBox extends KnuthBox {

    private FootnoteBodyLayoutManager footnoteBodyLM = null;
    private AlignmentContext alignmentContext = null;

    /**
     * Create a new KnuthBox.
     *
     * @param w    the width of this box
     * @param alignmentContext the alignmentContext for this box
     * @param pos  the Position stored in this box
     * @param bAux is this box auxiliary?
     */
    public KnuthInlineBox(int w, AlignmentContext alignmentContext, Position pos, boolean bAux) {
        super(w, pos, bAux);
        this.alignmentContext = alignmentContext;
    }

    /**
     * @return the alignment context.
     */
    public AlignmentContext getAlignmentContext() {
        return alignmentContext;
    }

    /**
     * @param fblm the FootnoteBodyLM this box must hold a reference to
     */
    public void setFootnoteBodyLM(FootnoteBodyLayoutManager fblm) {
        footnoteBodyLM = fblm;
    }

    /**
     * @return the FootnoteBodyLM this box holds a reference to
     */
    public FootnoteBodyLayoutManager getFootnoteBodyLM() {
        return footnoteBodyLM;
    }

    /**
     * @return true if this box holds a reference to a FootnoteBodyLM
     */
    public boolean isAnchor() {
        return (footnoteBodyLM != null);
    }


    /** @see java.lang.Object#toString() */
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        return sb.toString();
    }
}
