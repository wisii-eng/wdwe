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

/* $Id: AFPElement.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.extensions;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.fo.XMLObj;

/**
 * This class extends the com.wisii.fov.extensions.ExtensionObj class. The
 * object faciliates extraction of elements from formatted objects based on
 * the static list as defined in the AFPElementMapping implementation.
 * <p/>
 */
public class AFPElement extends AbstractAFPExtensionObject {

    /**
     * Constructs an AFP object (called by Maker).
     *
     * @param parent the parent formatting object
     * @param name the name of the afp element
     */
    public AFPElement(FONode parent, String name) {
        super(parent, name);
    }

    /** @see com.wisii.fov.fo.FONode#getNamespaceURI() */
    public String getNamespaceURI() {
        return AFPElementMapping.NAMESPACE;
    }

    /** @see com.wisii.fov.fo.FONode#getNormalNamespacePrefix() */
    public String getNormalNamespacePrefix() {
        return "afp";
    }

    /** @see com.wisii.fov.fo.FONode#startOfNode() */
    protected void startOfNode() throws FOVException {
        super.startOfNode();
        //if (!AFPElementMapping.NAMESPACE.equals(parent.getNamespaceURI())
        //    || !AFPElementMapping.PAGE.equals(parent.getLocalName())) {
        //    throw new ValidationException(getName() + " must be a child of afp:page.");
        //}
        if (parent.getNameId() != Constants.FO_SIMPLE_PAGE_MASTER) {
            throw new ValidationException(getName() + " must be a child of fo:simple-page-master.");
        }
    }

}
