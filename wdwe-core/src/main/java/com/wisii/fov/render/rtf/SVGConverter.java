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

/* $Id: SVGConverter.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.rtf;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.fov.image.XMLImage;

/**
 * Helper class for converting SVG to bitmap images.
 */
public final class SVGConverter {

    /** logger instance */
    private static Log log = LogFactory.getLog(SVGConverter.class);

    /**
     * Constructor is private, because it's just a utility class.
     */
    private SVGConverter() {
    }
    
    /**
     * Converts a SVG image to a JPEG bitmap.
     * @param image the SVG image
     * @return a byte array containing the JPEG image
     */
    public static byte[] convertToJPEG(XMLImage image) {
        JPEGTranscoder transcoder = new JPEGTranscoder();
        /* TODO Disabled to avoid side-effect due to the mixing of source and target resolutions
         * This should be reenabled when it has been determined how exactly to handle this
        transcoder.addTranscodingHint(ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 
                new Float(25.4f / 300)); //300dpi should be enough for now.
        */
        transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(0.9f));
        TranscoderInput input = new TranscoderInput(image.getDocument());
        ByteArrayOutputStream baout = new ByteArrayOutputStream(16384);
        TranscoderOutput output = new TranscoderOutput(baout);
        try {
            transcoder.transcode(input, output);
            return baout.toByteArray();
        } catch (TranscoderException e) {
            log.error(e);
            return null;
        }
    }
    
}
