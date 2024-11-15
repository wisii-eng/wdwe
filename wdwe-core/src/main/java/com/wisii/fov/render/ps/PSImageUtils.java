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

/* $Id: PSImageUtils.java 426576 2006-07-28 15:44:37Z jeremias $ */
 
package com.wisii.fov.render.ps;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.fov.image.EPSImage;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.JpegImage;
import org.apache.xmlgraphics.ps.PSGenerator;

/**
 * Utility code for rendering images in PostScript. 
 */
public class PSImageUtils extends org.apache.xmlgraphics.ps.PSImageUtils {

    /** logging instance */
    protected static Log log = LogFactory.getLog(PSImageUtils.class);

    /**
     * Renders a bitmap image to PostScript.
     * @param img image to render
     * @param x x position
     * @param y y position
     * @param w width
     * @param h height
     * @param gen PS generator
     * @throws IOException In case of an I/O problem while rendering the image
     */
    public static void renderBitmapImage(FovImage img, 
                float x, float y, float w, float h, PSGenerator gen)
                    throws IOException {
        if (img instanceof JpegImage) {
            if (!img.load(FovImage.ORIGINAL_DATA)) {
                gen.commentln("%JPEG image could not be processed: " + img);
                return;
            }
        } else {
            if (!img.load(FovImage.BITMAP)) {
                gen.commentln("%Bitmap image could not be processed: " + img);
                return;
            }
        }
        byte[] imgmap;
        if (img.getBitmapsSize() > 0) {
            imgmap = img.getBitmaps();
        } else {
            imgmap = img.getRessourceBytes();
        }
        
        String imgName = img.getMimeType() + " " + img.getOriginalURI();
        Dimension imgDim = new Dimension(img.getWidth(), img.getHeight());
        Rectangle2D targetRect = new Rectangle2D.Double(x, y, w, h);
        boolean isJPEG = (img instanceof JpegImage);
        writeImage(imgmap, imgDim, imgName, targetRect, isJPEG, 
                img.getColorSpace(), gen);
    }

    public static void renderEPS(EPSImage img, 
            float x, float y, float w, float h,
            PSGenerator gen) {
        try {
            if (!img.load(FovImage.ORIGINAL_DATA)) {
                gen.commentln("%EPS image could not be processed: " + img);
                return;
            }
            int[] bbox = img.getBBox();
            int bboxw = bbox[2] - bbox[0];
            int bboxh = bbox[3] - bbox[1];
            String name = img.getDocName();
            if (name == null || name.length() == 0) {
                name = img.getOriginalURI();
            }
            renderEPS(img.getEPSImage(), name,
                x, y, w, h,
                bbox[0], bbox[1], bboxw, bboxh, gen);

        } catch (Exception e) {
            log.error("PSRenderer.renderImageArea(): Error rendering bitmap ("
                                   + e.getMessage() + ")", e);
        }
    }

}
