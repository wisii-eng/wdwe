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
 */
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
 */

/* $Id: FontAdder.java 815383 2009-09-15 16:15:11Z maxberger $ */

package com.wisii.font;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.wisii.font.finder.FontInfoFinder;


/**
 * Adds a list of fonts to a given font info list 
 */
public class FontAdder {
    private FontResolver resolver;

    /**
     * Main constructor
     * @param manager a font manager
     * @param resolver a font resolver
     * @param listener a font event handler
     */
    public FontAdder( FontResolver resolver) {
        this.resolver = resolver;
    }
    
    /**
     * Iterates over font url list adding to font info list
     * @param fontURLList font file list
     * @param fontInfoList a configured font info list
     */
    public void add(List/*<URL>*/ fontURLList, List/*<EmbedFontInfo>*/ fontInfoList) {
        FontInfoFinder finder = new FontInfoFinder();

        for (Iterator iter = fontURLList.iterator(); iter.hasNext();) {
            URL fontUrl = (URL)iter.next();
            EmbedFontInfo[] embedFontInfos = finder.find(fontUrl, resolver, null);
            if (embedFontInfos == null) {
                continue;
            }
            for (int i = 0, c = embedFontInfos.length; i < c; i++) {
                EmbedFontInfo fontInfo = embedFontInfos[i];
                if (fontInfo != null) {
                    fontInfoList.add(fontInfo);
                }
            }
        }
    }
}
