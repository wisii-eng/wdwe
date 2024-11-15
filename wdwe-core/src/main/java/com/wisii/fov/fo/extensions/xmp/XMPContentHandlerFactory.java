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

import com.wisii.fov.util.ContentHandlerFactory;
import org.apache.xmlgraphics.xmp.XMPConstants;
import org.apache.xmlgraphics.xmp.XMPHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * ContentHandlerFactory for the XMP root element.
 */
public class XMPContentHandlerFactory implements ContentHandlerFactory {

    private static final String[] NAMESPACES = new String[] 
                                         {XMPConstants.XMP_NAMESPACE, XMPConstants.RDF_NAMESPACE};

    /** @see com.wisii.fov.util.ContentHandlerFactory#getSupportedNamespaces() */
    public String[] getSupportedNamespaces() {
        return NAMESPACES;
    }

    /** @see com.wisii.fov.util.ContentHandlerFactory#createContentHandler() */
    public ContentHandler createContentHandler() throws SAXException {
        return new FOVXMPHandler();
    }

    /**
     * Local subclass of XMPHandler that implements ObjectSource for FOV integration.
     */
    private class FOVXMPHandler extends XMPHandler implements ObjectSource {

        private ObjectBuiltListener obListener;
        
        public Object getObject() {
            return getMetadata();
        }

        /** @see com.wisii.fov.util.ContentHandlerFactory.ObjectSource */
        public void setObjectBuiltListener(ObjectBuiltListener listener) {
            this.obListener = listener;
        }
        
        /** @see org.xml.sax.helpers.DefaultHandler#endDocument() */
        public void endDocument() throws SAXException {
            if (obListener != null) {
                obListener.notifyObjectBuilt(getObject());
            }
        }
        
    }
    
}
