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

/* $Id: PSExtensionElementMapping.java 426576 2006-07-28 15:44:37Z jeremias $ */
 
package com.wisii.fov.render.ps.extensions;

import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.ElementMapping;

/**
 * This class provides the element mapping for the PostScript-specific extensions.
 */
public class PSExtensionElementMapping extends ElementMapping {

    /** Namespace for the extension */
    public static final String NAMESPACE = "http://xmlgraphics.apache.org/fov/postscript"; 

    /** Main constructor */
    public PSExtensionElementMapping() {
        this.namespaceURI = NAMESPACE;
    }

    /** @see com.wisii.fov.fo.ElementMapping#initialize() */
    protected void initialize() {
        if (foObjs == null) {
            foObjs = new java.util.HashMap();
            foObjs.put("ps-setup-code", new PSSetupCodeMaker());
            foObjs.put("ps-page-setup-code", new PSPageSetupCodeMaker());
        }
    }

    static class PSSetupCodeMaker extends ElementMapping.Maker {
        public FONode make(FONode parent) {
            return new PSSetupCodeElement(parent);
        }
    }

    static class PSPageSetupCodeMaker extends ElementMapping.Maker {
        public FONode make(FONode parent) {
            return new PSPageSetupCodeElement(parent);
        }
    }

}
