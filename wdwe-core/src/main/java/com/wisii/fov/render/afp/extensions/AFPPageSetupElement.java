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
 *//*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.wisii.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: AFPPageSetupElement.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.extensions;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.ValidationException;

/**
 * Extension element for fox:ps-page-setup-code.
 */
public class AFPPageSetupElement extends AbstractAFPExtensionObject {

    /**
     * Main constructor
     * @param parent parent FO node
     */
    public AFPPageSetupElement(FONode parent) {
        super(parent, "page");
    }

    /** @see com.wisii.fov.fo.FONode#startOfNode() */
    protected void startOfNode() throws FOVException {
        super.startOfNode();
        if (parent.getNameId() != Constants.FO_SIMPLE_PAGE_MASTER) {
            throw new ValidationException(getName() + " must be a child of fo:simple-page-master.");
        }
    }

}