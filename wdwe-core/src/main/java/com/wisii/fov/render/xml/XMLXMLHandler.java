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

/* $Id: XMLXMLHandler.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.xml;

import com.wisii.fov.render.Renderer;
import com.wisii.fov.render.XMLHandler;
import com.wisii.fov.render.RendererContext;
import com.wisii.fov.util.DOM2SAX;

import org.xml.sax.ContentHandler;

/**
 * XML handler for the XML renderer.
 */
public class XMLXMLHandler implements XMLHandler {

    /** Key for getting the TransformerHandler from the RendererContext */
    public static final String HANDLER = "handler";

    /** @see com.wisii.fov.render.XMLHandler */
    public void handleXML(RendererContext context,
                org.w3c.dom.Document doc, String ns) throws Exception {
        ContentHandler handler = (ContentHandler) context.getProperty(HANDLER);

        new DOM2SAX(handler).writeDocument(doc, true);
    }

    /** @see com.wisii.fov.render.XMLHandler#supportsRenderer(com.wisii.fov.render.Renderer) */
    public boolean supportsRenderer(Renderer renderer) {
        return (renderer instanceof XMLRenderer);
    }

    /** @see com.wisii.fov.render.XMLHandler#getNamespace() */
    public String getNamespace() {
        return null; //Handle all XML content
    }

}

