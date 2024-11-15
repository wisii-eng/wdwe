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
 *//* $Id: MultiCase.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.properties.CommonAccessibility;

/**
 * Class modelling the fo:multi-case object.
 * @todo implement validateChildNode()
 */
public class MultiCase extends FObj {
    // The value of properties relevant for fo:multi-case.
    private CommonAccessibility commonAccessibility;
    private String id;
    private int startingState;
    // private ToBeImplementedProperty caseName;
    // private ToBeImplementedProperty caseTitle;
    // End of property values

    static boolean notImplementedWarningGiven = false;

    /**
     * @param parent FONode that is the parent of this object
     */
    public MultiCase(FONode parent) {
        super(parent);

        if (!notImplementedWarningGiven) {
            getLogger().warn("fo:multi-case is not yet implemented.");
            notImplementedWarningGiven = true;
        }
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonAccessibility = pList.getAccessibilityProps();
        id = pList.get(PR_ID).getString();
        startingState = pList.get(PR_STARTING_STATE).getEnum();
        // caseName = pList.get(PR_CASE_NAME);
        // caseTitle = pList.get(PR_CASE_TITLE);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        checkId(id);
    }

    /**
     * Return the "starting-state" property.
     */
    public int getStartingState() {
        return startingState;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "multi-case";
    }

    /**
     * @see com.wisii.fov.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_MULTI_CASE;
    }
}
