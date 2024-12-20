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
 *//* $Id: NonLeafPosition.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.layoutmgr;

public class NonLeafPosition extends Position {

    private Position subPos;

    public NonLeafPosition(LayoutManager lm, Position sub) {
        super(lm);
        subPos = sub;
    }

    public Position getPosition() {
        return subPos;
    }

    public boolean generatesAreas() {
        return (subPos != null ? subPos.generatesAreas() : false);
    }

    /** @see java.lang.Object#toString() */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("NonLeafPos:").append(getIndex()).append("(");
        sb.append(getShortLMName());
        sb.append(", ");
        if (getPosition() != null) {
            sb.append(getPosition().toString());
        } else {
            sb.append("null");
        }
        sb.append(")");
        return sb.toString();
    }
}

