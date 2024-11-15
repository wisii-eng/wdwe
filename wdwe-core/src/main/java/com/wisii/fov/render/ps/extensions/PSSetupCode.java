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

/* $Id: PSSetupCode.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.ps.extensions;

import java.io.Serializable;

import com.wisii.fov.fo.extensions.ExtensionAttachment;
import com.wisii.fov.util.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This is the pass-through value object for the PostScript extension.
 */
public class PSSetupCode implements ExtensionAttachment, Serializable, XMLizable {

    /** The category URI for this extension attachment. */
    public static final String CATEGORY = "apache:fov:extensions:postscript";
    
    private String name;
    private String content;

    /**
     * No-argument contructor.
     */
    public PSSetupCode() {
        //nop
    }
    
    /**
     * Default constructor.
     * @param name the name of the setup code object, may be null
     * @param content the content of the setup code object
     */
    public PSSetupCode(String name, String content) {
        this.name = name;
        this.content = content;
    }
    
    /** @return the content */
    public String getContent() {
        return content;
    }
    
    /**
     * Sets the content for the setup code object.
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /** @return the name */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the setup code object.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @see com.wisii.fov.fo.extensions.ExtensionAttachment#getCategory() */
    public String getCategory() {
        return CATEGORY;
    }
    
    /** @see java.lang.Object#toString() */
    public String toString() {
        return "PSSetupCode(name=" + getName() + ")";
    }

    private static final String ATT_NAME = "name";
    private static final String ELEMENT = "ps-setup-code";
    
    /** @see com.wisii.fov.util.XMLizable#toSAX(org.xml.sax.ContentHandler) */
    public void toSAX(ContentHandler handler) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        if (name != null && name.length() > 0) {
            atts.addAttribute(null, ATT_NAME, ATT_NAME, "CDATA", name);
        }
        handler.startElement(CATEGORY, ELEMENT, ELEMENT, atts);
        if (content != null && content.length() > 0) {
            char[] chars = content.toCharArray();
            handler.characters(chars, 0, chars.length);
        }
        handler.endElement(CATEGORY, ELEMENT, ELEMENT);
    }
    
}
