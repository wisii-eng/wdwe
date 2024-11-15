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
 *//* $Id: AbstractGenericSVGHandler.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.render;

// Java
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

// DOM
import org.w3c.dom.Document;

// Batik
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.gvt.GraphicsNode;

// FOV
import com.wisii.fov.render.Graphics2DAdapter;
import com.wisii.fov.render.Graphics2DImagePainter;
import com.wisii.fov.render.RendererContextConstants;
import com.wisii.fov.render.XMLHandler;
import com.wisii.fov.render.RendererContext;
import com.wisii.fov.render.RendererContext.RendererContextWrapper;
import com.wisii.fov.svg.SVGUserAgent;

// Commons-Logging
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic XML handler for SVG. Uses Apache Batik for SVG processing and simply paints to
 * a Graphics2DAdapter and thus ultimatively to Graphics2D interface that is presented.
 * <p>
 * To use this class, subclass it and implement the missing methods (supportsRenderer, for example).
 */
public abstract class AbstractGenericSVGHandler implements XMLHandler, RendererContextConstants {

    /** logging instance */
    private static Log log = LogFactory.getLog(AbstractGenericSVGHandler.class);

    /** @see com.wisii.fov.render.XMLHandler */
    public void handleXML(RendererContext context,
                Document doc, String ns) throws Exception {

        if (SVGDOMImplementation.SVG_NAMESPACE_URI.equals(ns)) {
            renderSVGDocument(context, doc);
        }
    }

    /**
     * Render the SVG document.
     * @param context the renderer context
     * @param doc the SVG document
     * @throws IOException In case of an I/O error while painting the image
     */
    protected void renderSVGDocument(final RendererContext context,
            final Document doc) throws IOException {
        final RendererContextWrapper wrappedContext = RendererContext.wrapRendererContext(context);
        int x = wrappedContext.getCurrentXPosition();
        int y = wrappedContext.getCurrentYPosition();

        //Prepare
        SVGUserAgent ua = new SVGUserAgent(
                context.getUserAgent().getSourcePixelUnitToMillimeter(),
                new AffineTransform());
        GVTBuilder builder = new GVTBuilder();
        final BridgeContext ctx = new BridgeContext(ua);

        //Build the GVT tree
        final GraphicsNode root;
        try {
            root = builder.build(ctx, doc);
        } catch (Exception e) {
            log.error("SVG graphic could not be built: " + e.getMessage(), e);
            return;
        }

        //Create the painter
        Graphics2DImagePainter painter = new Graphics2DImagePainter() {

            public void paint(Graphics2D g2d, Rectangle2D area) {
                // If no viewbox is defined in the svg file, a viewbox of 100x100 is
                // assumed, as defined in SVGUserAgent.getViewportSize()
                float iw = (float) ctx.getDocumentSize().getWidth();
                float ih = (float) ctx.getDocumentSize().getHeight();
                float w = (float) area.getWidth();
                float h = (float) area.getHeight();
                g2d.scale(w / iw, h / ih);

                root.paint(g2d);
            }

            public Dimension getImageSize() {
                return new Dimension(wrappedContext.getWidth(), wrappedContext.getHeight());
            }

        };

        //Let the painter paint the SVG on the Graphics2D instance
        Graphics2DAdapter adapter = context.getRenderer().getGraphics2DAdapter();
        adapter.paintImage(painter, context,
                x, y, wrappedContext.getWidth(), wrappedContext.getHeight());
    }

    /** @see com.wisii.fov.render.XMLHandler#getNamespace() */
    public String getNamespace() {
        return SVGDOMImplementation.SVG_NAMESPACE_URI;
    }

}

