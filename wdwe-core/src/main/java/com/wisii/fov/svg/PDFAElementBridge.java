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

/* $Id: PDFAElementBridge.java 426576 2006-07-28 15:44:37Z jeremias $ */
 
package com.wisii.fov.svg;

import java.awt.geom.AffineTransform;

import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;

import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAElement;

/**
 * Bridge class for the &lt;a> element.
 *
 * @author <a href="mailto:keiron@aftexsw.com">Keiron Liddle</a>
 */
public class PDFAElementBridge extends AbstractGraphicsNodeBridge {
    private AffineTransform transform;

    /**
     * Constructs a new bridge for the &lt;a> element.
     */
    public PDFAElementBridge() {
    }

    /**
     * Set the current transform of this element.
     * @param tf the transform
     */
    public void setCurrentTransform(AffineTransform tf) {
        transform = tf;
    }

    /**
     * Returns 'a'.
     * @return the name of this node
     */
    public String getLocalName() {
        return SVG_A_TAG;
    }

    /**
     * Creates a <tt>CompositeGraphicsNode</tt>.
     * @return a new PDFANode
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new PDFANode();
    }

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return node the new graphics node
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        PDFANode aNode = (PDFANode)super.createGraphicsNode(ctx, e);
        aNode.setDestination(((SVGAElement)e).getHref().getBaseVal());
        aNode.setTransform(transform);
        return aNode;
    }

    /**
     * Returns true as the &lt;a> element is a container.
     * @return true if the a element is a container
     */
    public boolean isComposite() {
        return true;
    }

}
