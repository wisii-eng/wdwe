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
 *//* $Id: FontUtil.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts;

/**
 * Font utilities.
 */
public class FontUtil {

    /**
     * Parses an CSS2 (SVG and XSL-FO) font weight (normal, bold, 100-900) to
     * an integer.
     * See http://www.w3.org/TR/REC-CSS2/fonts.html#propdef-font-weight
     * TODO: Implement "lighter" and "bolder".
     * @param text the font weight to parse
     * @return an integer between 100 and 900 (100, 200, 300...)
     */
    public static int parseCSS2FontWeight(String text) {
        int weight = 400;
        try {
            weight = Integer.parseInt(text);
            weight = ((int)weight / 100) * 100;
            weight = Math.max(weight, 100);
            weight = Math.min(weight, 900);
        } catch (NumberFormatException nfe) {
            //weight is no number, so convert symbolic name to number
            if (text.equals("normal")) {
                weight = 400;
            } else if (text.equals("bold")) {
                weight = 700;
            } else {
                throw new IllegalArgumentException(
                    "无效的字体灰度值: '"
                    + text
                    + "'. Use one of: 100, 200, 300, "
                    + "400, 500, 600, 700, 800, 900, "
                    + "normal (=400), bold (=700)");
            }
        }
        return weight;
    }

}
