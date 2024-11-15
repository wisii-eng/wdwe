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
 *//* $Id: EmbedFontInfo.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts;

import java.util.List;

/**
 * FontInfo contains meta information on fonts (where is the metrics file etc.)
 */
public class EmbedFontInfo {

    private String metricsFile, embedFile;
    private boolean kerning;
    private List fontTriplets;

    /**
     * Main constructor
     * @param metricsFile Path to the xml file containing font metrics
     * @param kerning True if kerning should be enabled
     * @param fontTriplets List of font triplets to associate with this font
     * @param embedFile Path to the embeddable font file (may be null)
     */
    public EmbedFontInfo(String metricsFile, boolean kerning,
                    List fontTriplets, String embedFile) {
        this.metricsFile = metricsFile;
        this.embedFile = embedFile;
        this.kerning = kerning;
        this.fontTriplets = fontTriplets;
    }

    /**
     * Returns the path to the metrics file
     * @return the metrics file path
     */
    public String getMetricsFile() {
        return metricsFile;
    }

    /**
     * Returns the path to the embeddable font file
     * @return the font file path
     */
    public String getEmbedFile() {
        return embedFile;
    }

    /**
     * Determines if kerning is enabled
     * @return True if enabled
     */
    public boolean getKerning() {
        return kerning;
    }

    /**
     * Returns the list of font triplets associated with this font.
     * @return List of font triplets
     */
    public List getFontTriplets() {
        return fontTriplets;
    }

}

