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

/* $Id$ */

package com.wisii.fov.fo.extensions.xmp;

import com.wisii.fov.fo.FONode;
import org.apache.xmlgraphics.xmp.XMPConstants;

/**
 * Represents the top-level "xmpmeta" element used by XMP metadata.
 */
public class XMPMetaElement extends AbstractMetadataElement {

    /**
     * Main constructor.
     * @param parent the parent formatting object
     */
    public XMPMetaElement(FONode parent) {
        super(parent);
    }
    
    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "xmpmeta";
    }

    /** @see com.wisii.fov.fo.FONode#getNormalNamespacePrefix() */
    public String getNormalNamespacePrefix() {
        return "x";
    }

    /** @see com.wisii.fov.fo.FONode#getNamespaceURI() */
    public String getNamespaceURI() {
        return XMPConstants.XMP_NAMESPACE;
    }

}
