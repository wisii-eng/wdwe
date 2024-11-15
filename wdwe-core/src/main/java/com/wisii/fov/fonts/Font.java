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
 */package com.wisii.fov.fonts;

import java.util.Map;

/**
 * This class holds font state information and provides access to the font
 * metrics.
 */
public class Font {

    /** Default fallback key */
    public static final FontTriplet DEFAULT_FONT = new FontTriplet("宋体", "normal", 400);
    /** Normal font weight */
    public static final int NORMAL = 400;
    /** Bold font weight */
    public static final int BOLD = 700;

    private final String fontName;
    private final FontTriplet triplet;
    private final int fontSize;

    /**
     * normal or small-caps font
     */
    //private int fontVariant;

    private final FontMetrics metric;

    /**
     * Main constructor
     * @param key key of the font
     * @param triplet the font triplet that was used to lookup this font (may be null)
     * @param met font metrics
     * @param fontSize font size
     */
    public Font(final String key, final FontTriplet triplet, final FontMetrics met, final int fontSize) {
        this.fontName = key;
        this.triplet = triplet;
        this.metric = met;
        this.fontSize = fontSize;
    }

    /**
     * Returns the font's ascender.
     * @return the ascender
     */
    public int getAscender() {
        return metric.getAscender(fontSize) / 1000;
    }

    /**
     * Returns the font's CapHeight.
     * @return the capital height
     */
    public int getCapHeight() {
        return metric.getCapHeight(fontSize) / 1000;
    }

    /**
     * Returns the font's Descender.
     * @return the descender
     */
    public int getDescender() {
        return metric.getDescender(fontSize) / 1000;
    }

    /**
     * Returns the font's name.
     * @return the font name
     */
    public String getFontName() {
        return fontName;
    }

    /** @return the font triplet that selected this font */
    public FontTriplet getFontTriplet() {
        return this.triplet;
    }

    /**
     * Returns the font size
     * @return the font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Returns the XHeight
     * @return the XHeight
     */
    public int getXHeight() {
        return metric.getXHeight(fontSize) / 1000;
    }

    /** @return true if the font has kerning info */
    public boolean hasKerning() {
        return metric.hasKerningInfo();
    }

    /**
     * Returns the font's kerning table
     * @return the kerning table
     */
    public Map getKerning() {
        if (metric.hasKerningInfo()) {
            return metric.getKerningInfo();
        } else {
            return java.util.Collections.EMPTY_MAP;
        }
    }

    /**
     * Returns the amount of kerning between two characters.
     * @param ch1 first character
     * @param ch2 second character
     * @return the distance to adjust for kerning, 0 if there's no kerning
     */
    public int getKernValue(final char ch1, final char ch2) {
        Map kernPair = (Map)getKerning().get(new Integer(ch1));
        if (kernPair != null) {
            Integer width = (Integer)kernPair.get(new Integer(ch2));
            if (width != null) {
                return width.intValue();
            }
        }
        return 0;
    }

    /**
     * Returns the width of a character
     * @param charnum character to look up
     * @return width of the character
     */
    public int getWidth(final int charnum) {
    	return (metric.getWidth(charnum, fontSize) / 1000);
    	
    	
//    	/**  mod by zkl. 处理字体为粗体时计算上的误差. 2007-06-20. */
//    	
//        int width=(metric.getWidth(charnum, fontSize) / 1000);
////        System.out.println("(metric.getWidth(charnum, fontSize) / 1000)="+(metric.getWidth(charnum, fontSize) / 1000));
//
////    	if(this.triplet.getWeight()==BOLD){
////    		String fontName=triplet.getName();
////    		if("仿宋_GB2312".equals(fontName)
////    				||"黑体".equals(fontName)
////    				||"楷体_GB2312".equals(fontName)
////    				||"宋体".equals(fontName)){
////    			width+=55*(fontSize/1000);
////    		}else if("Arial".equals(fontName)){
////    			width+=35*(fontSize/1000);
////    		}else if("Times New Roman".equals(fontName)){
////    			width+=10*(fontSize/1000);
////    		}
////    	}
////    	 System.out.println("width="+width);
//        return width;

           }

    /**
     * Map a java character (unicode) to a font character.
     * Default uses CodePointMapping.
     * @param c character to map
     * @return the mapped character
     */
    public char mapChar(char c) {

        if (metric instanceof com.wisii.fov.fonts.Typeface) {
            return ((com.wisii.fov.fonts.Typeface)metric).mapChar(c);
        }

        // Use default CodePointMapping
        char d = CodePointMapping.getMapping("WinAnsiEncoding").mapChar(c);
        if (d != 0) {
            c = d;
        } else {
            c = '#';
        }

        return c;
    }

    /**
     * Determines whether this font contains a particular character/glyph.
     * @param c character to check
     * @return True if the character is supported, Falso otherwise
     */
    public boolean hasChar(final char c) {
        if (metric instanceof com.wisii.fov.fonts.Typeface) {
            return ((com.wisii.fov.fonts.Typeface)metric).hasChar(c);
        } else {
            // Use default CodePointMapping
            return (CodePointMapping.getMapping("WinAnsiEncoding").mapChar(c) > 0);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append('(');
        /*
        sbuf.append(fontFamily);
        sbuf.append(',');*/
        sbuf.append(fontName);
        sbuf.append(',');
        sbuf.append(fontSize);
        /*
        sbuf.append(',');
        sbuf.append(fontStyle);
        sbuf.append(',');
        sbuf.append(fontWeight);*/
        sbuf.append(')');
        return sbuf.toString();
    }

    /**
     * Helper method for getting the width of a unicode char
     * from the current fontstate.
     * This also performs some guessing on widths on various
     * versions of space that might not exists in the font.
     * @param c character to inspect
     * @return the width of the character
     */
    public int getCharWidth(final char c) {
        int width;
        if ((c == '\n') || (c == '\r') || (c == '\t') || (c == '\u00A0')) {
            width = getCharWidth(' ');
        } else {
            if (hasChar(c)) {
                width = getWidth(mapChar(c));
            } else {
                width = -1;
            }
            if (width <= 0) {
                // Estimate the width of spaces not represented in
                // the font
                int em = getFontSize(); //http://en.wikipedia.org/wiki/Em_(typography)
                int en = em / 2; //http://en.wikipedia.org/wiki/En_(typography)

                if (c == ' ') {
                    width = em;
                } else if (c == '\u2000') {
                    width = en;
                } else if (c == '\u2001') {
                    width = em;
                } else if (c == '\u2002') {
                    width = em / 2;
                } else if (c == '\u2003') {
                    width = getFontSize();
                } else if (c == '\u2004') {
                    width = em / 3;
                } else if (c == '\u2005') {
                    width = em / 4;
                } else if (c == '\u2006') {
                    width = em / 6;
                } else if (c == '\u2007') {
                    width = getCharWidth('0');
                } else if (c == '\u2008') {
                    width = getCharWidth('.');
                } else if (c == '\u2009') {
                    width = em / 5;
                } else if (c == '\u200A') {
                    width = em / 10;
                } else if (c == '\u200B') {
                    width = 0;
                } else if (c == '\u202F') {
                    width = getCharWidth(' ') / 2;
                } else if (c == '\u3000') {
                    width = getCharWidth(' ') * 2;
                } else {
                    //Will be internally replaced by "#" if not found
                    width = getWidth(mapChar(c));
                }
            }
        }

//        System.out.println("#fov.fonts.Font.getCharWidth()#  width="+width);

        return width;
    }

    /**
     * Calculates the word width.
     * @param word text to get width for
     * @return the width of the text
     */
    public int getWordWidth(final String word) {
        if (word == null) {
            return 0;
        }
        int wordLength = word.length();
        int width = 0;
        char[] characters = new char[wordLength];
        word.getChars(0, wordLength, characters, 0);
        for (int i = 0; i < wordLength; i++) {
            width += getCharWidth(characters[i]);
        }

//        System.out.println("#fov.fonts.Font.getWordWidth()#  width="+width);
        return width;
    }

}


