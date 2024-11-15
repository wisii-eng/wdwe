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

/* $Id: FontInfoFinder.java 787668 2009-06-23 13:18:44Z acumiskey $ */

package com.wisii.font.finder;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisii.font.CustomFont;
import com.wisii.font.EmbedFontInfo;
import com.wisii.font.EncodingMode;
import com.wisii.font.Font;
import com.wisii.font.FontCache;
import com.wisii.font.FontLoader;
import com.wisii.font.FontResolver;
import com.wisii.font.FontTriplet;
import com.wisii.font.MultiByteFont;
import com.wisii.font.truetype.FontFileReader;
import com.wisii.font.truetype.TTFFile;
import com.wisii.font.truetype.TTFFontLoader;
import com.wisii.font.util.FontUtil;


/**
 * Attempts to determine correct FontInfo
 */
public class FontInfoFinder {

    /** logging instance */
    private Log log = LogFactory.getLog(FontInfoFinder.class);

    /**
     * Attempts to determine FontTriplets from a given CustomFont.
     * It seems to be fairly accurate but will probably require some tweaking over time
     *
     * @param customFont CustomFont
     * @param triplet Collection that will take the generated triplets
     */
    private void generateTripletsFromFont(CustomFont customFont, Collection triplets) {
        if (log.isTraceEnabled()) {
            log.trace("Font: " + customFont.getFullName()
                    + ", family: " + customFont.getFamilyNames()
                    + ", PS: " + customFont.getFontName()
                    + ", EmbedName: " + customFont.getEmbedFontName());
        }

        // default style and weight triplet vales (fallback)
        String strippedName = stripQuotes(customFont.getStrippedFontName());
        //String subName = customFont.getFontSubName();
        String fullName = stripQuotes(customFont.getFullName());
        String searchName = fullName.toLowerCase();

        String style = guessStyle(customFont, searchName);
        int weight; //= customFont.getWeight();
        int guessedWeight = FontUtil.guessWeight(searchName);
        //We always take the guessed weight for now since it yield much better results.
        //OpenType's OS/2 usWeightClass value proves to be unreliable.
        weight = guessedWeight;

        //Full Name usually includes style/weight info so don't use these traits
        //If we still want to use these traits, we have to make FontInfo.fontLookup() smarter
        triplets.add(new FontTriplet(fullName, Font.STYLE_NORMAL, Font.WEIGHT_NORMAL));
        if (!fullName.equals(strippedName)) {
            triplets.add(new FontTriplet(strippedName, Font.STYLE_NORMAL, Font.WEIGHT_NORMAL));
        }
        Set familyNames = customFont.getFamilyNames();
        Iterator iter = familyNames.iterator();
        while (iter.hasNext()) {
            String familyName = stripQuotes((String)iter.next());
            if (!fullName.equals(familyName)) {
                /* Heuristic:
                 *   The more similar the family name to the full font name,
                 *   the higher the priority of its triplet.
                 * (Lower values indicate higher priorities.) */
                int priority = fullName.startsWith(familyName)
                    ? fullName.length() - familyName.length()
                    : fullName.length();
                triplets.add(new FontTriplet(familyName, style, weight, priority));
            }
        }
    }

    private final Pattern quotePattern = Pattern.compile("'");

    private String stripQuotes(String name) {
        return quotePattern.matcher(name).replaceAll("");
    }

    private String guessStyle(CustomFont customFont, String fontName) {
        // style
        String style = Font.STYLE_NORMAL;
        if (customFont.getItalicAngle() > 0) {
            style = Font.STYLE_ITALIC;
        } else {
            style = FontUtil.guessStyle(fontName);
        }
        return style;
    }

    /**
     * Attempts to determine FontInfo from a given custom font
     * @param fontUrl the font URL
     * @param customFont the custom font
     * @param fontCache font cache (may be null)
     * @return
     */
    private EmbedFontInfo getFontInfoFromCustomFont(
            URL fontUrl, CustomFont customFont, FontCache fontCache) {
        List fontTripletList = new java.util.ArrayList();
        generateTripletsFromFont(customFont, fontTripletList);
        String embedUrl;
        embedUrl = fontUrl.toExternalForm();
        String subFontName = null;
        if (customFont instanceof MultiByteFont) {
            subFontName = ((MultiByteFont)customFont).getTTCName();
        }
        EmbedFontInfo fontInfo = new EmbedFontInfo(null, customFont.isKerningEnabled(),
                fontTripletList, embedUrl, subFontName);
        fontInfo.setPostScriptName(customFont.getFontName());
        if (fontCache != null) {
            fontCache.addFont(fontInfo);
        }
        return fontInfo;
    }

    /**
     * Attempts to determine EmbedFontInfo from a given font file.
     *
     * @param fontUrl font URL. Assumed to be local.
     * @param resolver font resolver used to resolve font
     * @param fontCache font cache (may be null)
     * @return an array of newly created embed font info. Generally, this array
     *         will have only one entry, unless the fontUrl is a TrueType Collection
     */
    public EmbedFontInfo[] find(URL fontUrl, FontResolver resolver, FontCache fontCache) {
        String embedUrl = null;
        embedUrl = fontUrl.toExternalForm();

        long fileLastModified = -1;
        if (fontCache != null) {
            fileLastModified = FontCache.getLastModified(fontUrl);
            // firstly try and fetch it from cache before loading/parsing the font file
            if (fontCache.containsFont(embedUrl)) {
                EmbedFontInfo[] fontInfos = fontCache.getFontInfos(embedUrl, fileLastModified);
                if (fontInfos != null) {
                    return fontInfos;
                }
            // is this a previously failed parsed font?
            } else if (fontCache.isFailedFont(embedUrl, fileLastModified)) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping font file that failed to load previously: " + embedUrl);
                }
                return null;
            }
        }


        // try to determine triplet information from font file
        CustomFont customFont = null;
        if (fontUrl.toExternalForm().endsWith(".ttc")) {
            // Get a list of the TTC Font names
            List ttcNames = null; //List<String>
            String fontFileURI = fontUrl.toExternalForm().trim();
            InputStream in = null;
            try {
                in = FontLoader.openFontUri(resolver, fontFileURI);
                TTFFile ttf = new TTFFile();
                FontFileReader reader = new FontFileReader(in);
                ttcNames = ttf.getTTCnames(reader);
            } catch (Exception e) {
              
                return null;
            } finally {
                IOUtils.closeQuietly(in);
            }

            List/*<EmbedFontInfo>*/ embedFontInfoList
                = new java.util.ArrayList/*<EmbedFontInfo>*/();

            // For each font name ...
            //for (String fontName : ttcNames) {
            Iterator ttcNamesIterator = ttcNames.iterator();
            while (ttcNamesIterator.hasNext()) {
                String fontName = (String)ttcNamesIterator.next();

                if (log.isDebugEnabled()) {
                    log.debug("Loading " + fontName);
                }
                try {
                    TTFFontLoader ttfLoader = new TTFFontLoader(
                            fontFileURI, fontName, true, EncodingMode.AUTO, true, resolver);
                    customFont = ttfLoader.getFont();
                    
                } catch (Exception e) {
                    if (fontCache != null) {
                        fontCache.registerFailedFont(embedUrl, fileLastModified);
                    }
                 
                    continue;
                }
                EmbedFontInfo fi = getFontInfoFromCustomFont(fontUrl, customFont, fontCache);
                if (fi != null) {
                    embedFontInfoList.add(fi);
                }
            }
            return (EmbedFontInfo[])embedFontInfoList.toArray(
                    new EmbedFontInfo[embedFontInfoList.size()]);
        } else {
            // The normal case
            try {
                customFont = FontLoader.loadFont(fontUrl, null, true, EncodingMode.AUTO, resolver);
               
            } catch (Exception e) {
                if (fontCache != null) {
                    fontCache.registerFailedFont(embedUrl, fileLastModified);
                }
           
                return null;
            }
            EmbedFontInfo fi = getFontInfoFromCustomFont(fontUrl, customFont, fontCache);
            if (fi != null) {
                return new EmbedFontInfo[] {fi};
            } else {
                return null;
            }
        }

    }

}
