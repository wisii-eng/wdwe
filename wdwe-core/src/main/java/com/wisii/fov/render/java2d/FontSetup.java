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
 */package com.wisii.fov.render.java2d;

// FOV
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.fonts.Font;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.fonts.FontTriplet;


/**
 * Sets up the Java2D/AWT fonts. It is similar to
 * com.wisii.fov.render.fonts.FontSetup.
 * Assigns the font (with metrics) to internal names like "F1" and
 * assigns family-style-weight triplets to the fonts.
 */
public class FontSetup
{
    /** logging instance */
    protected static Log log = LogFactory.getLog(FontSetup.class);

    private static int LAST_PREDEFINED_FONT_NUMBER = 21;

    private static final Set HARDCODED_FONT_NAMES;

//    private static Document _document;

    static {
        HARDCODED_FONT_NAMES = new java.util.HashSet();
        HARDCODED_FONT_NAMES.add("any");
        HARDCODED_FONT_NAMES.add("sans-serif");
        HARDCODED_FONT_NAMES.add("serif");
        HARDCODED_FONT_NAMES.add("monospace");
        HARDCODED_FONT_NAMES.add("Helvetica");
        HARDCODED_FONT_NAMES.add("Times");
        HARDCODED_FONT_NAMES.add("Courier");
        HARDCODED_FONT_NAMES.add("Symbol");
        HARDCODED_FONT_NAMES.add("ZapfDingbats");
        HARDCODED_FONT_NAMES.add("Times Roman");
        HARDCODED_FONT_NAMES.add("Times-Roman");
//        HARDCODED_FONT_NAMES.add("Times New Roman");
        HARDCODED_FONT_NAMES.add("Computer-Modern-Typewriter");
    }

    /**
     * Sets up the font info object.
     *
     * Adds metrics for basic fonts and useful family-style-weight
     * triplets for lookup.
     *
     * @param fontInfo the font info object to set up
     * @param graphics needed for acces to font metrics
     */
    public static void setup(FontInfo fontInfo, Graphics2D graphics) {
        FontMetricsMapper metric;
        int normal, bold, bolditalic, italic;

        /*
         * available java fonts are:
         * Serif - bold, normal, italic, bold-italic
         * SansSerif - bold, normal, italic, bold-italic
         * MonoSpaced - bold, normal, italic, bold-italic
         */
        normal = java.awt.Font.PLAIN;
        bold = java.awt.Font.BOLD;
        italic = java.awt.Font.ITALIC;
        bolditalic = java.awt.Font.BOLD + java.awt.Font.ITALIC;

        metric = new FontMetricsMapper("SansSerif", normal, graphics);
        // --> goes to  F1
        fontInfo.addMetrics("F1", metric);
        metric = new FontMetricsMapper("SansSerif", italic, graphics);
        // --> goes to  F2
        fontInfo.addMetrics("F2", metric);
        metric = new FontMetricsMapper("SansSerif", bold, graphics);
        // --> goes to  F3
        fontInfo.addMetrics("F3", metric);
        metric = new FontMetricsMapper("SansSerif", bolditalic, graphics);
        // --> goes to  F4
        fontInfo.addMetrics("F4", metric);


        metric = new FontMetricsMapper("Serif", normal, graphics);
        // --> goes to  F5
        fontInfo.addMetrics("F5", metric);
        metric = new FontMetricsMapper("Serif", italic, graphics);
        // --> goes to  F6
        fontInfo.addMetrics("F6", metric);
        metric = new FontMetricsMapper("Serif", bold, graphics);
        // --> goes to  F7
        fontInfo.addMetrics("F7", metric);
        metric = new FontMetricsMapper("Serif", bolditalic, graphics);
        // --> goes to  F8
        fontInfo.addMetrics("F8", metric);


        metric = new FontMetricsMapper("MonoSpaced", normal, graphics);
        // --> goes to  F9
        fontInfo.addMetrics("F9", metric);
        metric = new FontMetricsMapper("MonoSpaced", italic, graphics);
        // --> goes to  F10
        fontInfo.addMetrics("F10", metric);
        metric = new FontMetricsMapper("MonoSpaced", bold, graphics);
        // --> goes to  F11
        fontInfo.addMetrics("F11", metric);
        metric = new FontMetricsMapper("MonoSpaced", bolditalic, graphics);
        // --> goes to  F12
        fontInfo.addMetrics("F12", metric);

        metric = new FontMetricsMapper("Serif", normal, graphics);
        //"Symbol" doesn't seem to work here, but "Serif" does the job just fine. *shrug*
        // --> goes to  F13 and F14
        fontInfo.addMetrics("F13", metric);
        fontInfo.addMetrics("F14", metric);


        // Custom type 1 fonts step 1/2
        // fontInfo.addMetrics("F15", new OMEP());
        // fontInfo.addMetrics("F16", new GaramondLightCondensed());
        // fontInfo.addMetrics("F17", new BauerBodoniBoldItalic());

//    --------------  add by zkl.

        // 宋体
        metric = new FontMetricsMapper("宋体", normal, graphics);
        fontInfo.addMetrics("F18", metric);
        metric = new FontMetricsMapper("宋体", italic, graphics);
        fontInfo.addMetrics("F19", metric);
        metric = new FontMetricsMapper("宋体", bold, graphics);
        fontInfo.addMetrics("F20", metric);
        metric = new FontMetricsMapper("宋体", bolditalic, graphics);
        fontInfo.addMetrics("F21", metric);
//        --------------add by zkl end.
//        --------------add by huangzl.
        // Arial
//        metric = new FontMetricsMapper("Arial", normal, graphics);
//        fontInfo.addMetrics("F22", metric);
//        metric = new FontMetricsMapper("Arial", italic, graphics);
//        fontInfo.addMetrics("F23", metric);
//        metric = new FontMetricsMapper("Arial", bold, graphics);
//        fontInfo.addMetrics("F24", metric);
//        metric = new FontMetricsMapper("Arial", bolditalic, graphics);
//        fontInfo.addMetrics("F25", metric);

        // Courier New
//        metric = new FontMetricsMapper("Courier New", normal, graphics);
//        fontInfo.addMetrics("F26", metric);
//        metric = new FontMetricsMapper("Courier New", italic, graphics);
//        fontInfo.addMetrics("F27", metric);
//        metric = new FontMetricsMapper("Courier New", bold, graphics);
//        fontInfo.addMetrics("F28", metric);
//        metric = new FontMetricsMapper("Courier New", bolditalic, graphics);
//        fontInfo.addMetrics("F29", metric);

        // 仿宋_GB2312
//        metric = new FontMetricsMapper("仿宋_GB2312", normal, graphics);
//        fontInfo.addMetrics("F30", metric);
//        metric = new FontMetricsMapper("仿宋_GB2312", italic, graphics);
//        fontInfo.addMetrics("F31", metric);
//        metric = new FontMetricsMapper("仿宋_GB2312", bold, graphics);
//        fontInfo.addMetrics("F32", metric);
//        metric = new FontMetricsMapper("仿宋_GB2312", bolditalic, graphics);
//        fontInfo.addMetrics("F33", metric);

        // 黑体
//        metric = new FontMetricsMapper("黑体", normal, graphics);
//        fontInfo.addMetrics("F34", metric);
//        metric = new FontMetricsMapper("黑体", italic, graphics);
//        fontInfo.addMetrics("F35", metric);
//        metric = new FontMetricsMapper("黑体", bold, graphics);
//        fontInfo.addMetrics("F36", metric);
//        metric = new FontMetricsMapper("黑体", bolditalic, graphics);
//        fontInfo.addMetrics("F37", metric);

        // 楷体_GB2312
//        metric = new FontMetricsMapper("楷体_GB2312", normal, graphics);
//        fontInfo.addMetrics("F38", metric);
//        metric = new FontMetricsMapper("楷体_GB2312", italic, graphics);
//        fontInfo.addMetrics("F39", metric);
//        metric = new FontMetricsMapper("楷体_GB2312", bold, graphics);
//        fontInfo.addMetrics("F40", metric);
//        metric = new FontMetricsMapper("楷体_GB2312", bolditalic, graphics);
//        fontInfo.addMetrics("F41", metric);
//        --------------add by huangzl end.

//    --------------  mod by zkl.
        /* any is treated as serif */
//        fontInfo.addFontProperties("F5", "any", "normal", Font.NORMAL);
//        fontInfo.addFontProperties("F6", "any", "italic", Font.NORMAL);
//        fontInfo.addFontProperties("F6", "any", "oblique", Font.NORMAL);
//        fontInfo.addFontProperties("F7", "any", "normal", Font.BOLD);
//        fontInfo.addFontProperties("F8", "any", "italic", Font.BOLD);
//        fontInfo.addFontProperties("F8", "any", "oblique", Font.BOLD);
        /* any is treated as 宋体 */
        fontInfo.addFontProperties("F18", "any", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F19", "any", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F19", "any", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F20", "any", "normal", Font.BOLD);
        fontInfo.addFontProperties("F21", "any", "italic", Font.BOLD);
        fontInfo.addFontProperties("F21", "any", "oblique", Font.BOLD);
//        --------------mod by zkl end.

        fontInfo.addFontProperties("F1", "sans-serif", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F2", "sans-serif", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F2", "sans-serif", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F3", "sans-serif", "normal", Font.BOLD);
        fontInfo.addFontProperties("F4", "sans-serif", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F4", "sans-serif", "italic", Font.BOLD);

        fontInfo.addFontProperties("F5", "serif", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F6", "serif", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F6", "serif", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F7", "serif", "normal", Font.BOLD);
        fontInfo.addFontProperties("F8", "serif", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F8", "serif", "italic", Font.BOLD);

        fontInfo.addFontProperties("F9", "monospace", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F10", "monospace", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F10", "monospace", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F11", "monospace", "normal", Font.BOLD);
        fontInfo.addFontProperties("F12", "monospace", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F12", "monospace", "italic", Font.BOLD);

        fontInfo.addFontProperties("F1", "Helvetica", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F2", "Helvetica", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F2", "Helvetica", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F3", "Helvetica", "normal", Font.BOLD);
        fontInfo.addFontProperties("F4", "Helvetica", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F4", "Helvetica", "italic", Font.BOLD);

        fontInfo.addFontProperties("F5", "Times", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F7", "Times", "normal", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times", "italic", Font.BOLD);

        fontInfo.addFontProperties("F9", "Courier", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F10", "Courier", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F10", "Courier", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F11", "Courier", "normal", Font.BOLD);
        fontInfo.addFontProperties("F12", "Courier", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F12", "Courier", "italic", Font.BOLD);

        fontInfo.addFontProperties("F13", "Symbol", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F14", "ZapfDingbats", "normal", Font.NORMAL);

        // Custom type 1 fonts step 2/2
        // fontInfo.addFontProperties("F15", "OMEP", "normal", FontInfo.NORMAL);
        // fontInfo.addFontProperties("F16", "Garamond-LightCondensed", "normal", FontInfo.NORMAL);
        // fontInfo.addFontProperties("F17", "BauerBodoni", "italic", FontInfo.BOLD);

        /* for compatibility with PassiveTex */
        fontInfo.addFontProperties("F5", "Times-Roman", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times-Roman", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times-Roman", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F7", "Times-Roman", "normal", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times-Roman", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times-Roman", "italic", Font.BOLD);
/*
        fontInfo.addFontProperties("F5", "Times New Roman", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times New Roman", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times New Roman", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F7", "Times New Roman", "normal", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times New Roman", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times New Roman", "italic", Font.BOLD);
*/
        fontInfo.addFontProperties("F5", "Times Roman", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times Roman", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F6", "Times Roman", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F7", "Times Roman", "normal", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times Roman", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F8", "Times Roman", "italic", Font.BOLD);

        fontInfo.addFontProperties("F9", "Computer-Modern-Typewriter",
                                   "normal", Font.NORMAL);



        //add by huangzl.
        /*
        fontInfo.addFontProperties("F22", "Arial", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F23", "Arial", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F23", "Arial", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F24", "Arial", "normal", Font.BOLD);
        fontInfo.addFontProperties("F25", "Arial", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F25", "Arial", "italic", Font.BOLD);

        fontInfo.addFontProperties("F26", "Courier New", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F27", "Courier New", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F27", "Courier New", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F28", "Courier New", "normal", Font.BOLD);
        fontInfo.addFontProperties("F29", "Courier New", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F29", "Courier New", "italic", Font.BOLD);

        fontInfo.addFontProperties("F30", "仿宋_GB2312", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F31", "仿宋_GB2312", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F31", "仿宋_GB2312", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F32", "仿宋_GB2312", "normal", Font.BOLD);
        fontInfo.addFontProperties("F33", "仿宋_GB2312", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F33", "仿宋_GB2312", "italic", Font.BOLD);

        fontInfo.addFontProperties("F34", "黑体", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F35", "黑体", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F35", "黑体", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F36", "黑体", "normal", Font.BOLD);
        fontInfo.addFontProperties("F37", "黑体", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F37", "黑体", "italic", Font.BOLD);

        fontInfo.addFontProperties("F38", "楷体_GB2312", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F39", "楷体_GB2312", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F39", "楷体_GB2312", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F40", "楷体_GB2312", "normal", Font.BOLD);
        fontInfo.addFontProperties("F41", "楷体_GB2312", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F41", "楷体_GB2312", "italic", Font.BOLD);
*/
        fontInfo.addFontProperties("F18", "宋体", "normal", Font.NORMAL);
        fontInfo.addFontProperties("F19", "宋体", "oblique", Font.NORMAL);
        fontInfo.addFontProperties("F19", "宋体", "italic", Font.NORMAL);
        fontInfo.addFontProperties("F20", "宋体", "normal", Font.BOLD);
        fontInfo.addFontProperties("F21", "宋体", "oblique", Font.BOLD);
        fontInfo.addFontProperties("F21", "宋体", "italic", Font.BOLD);

        //add end.

        //添加自定义字体
        int p = addCustomFont(fontInfo, graphics,LAST_PREDEFINED_FONT_NUMBER + 1);

        configureInstalledAWTFonts(fontInfo, graphics, p);

    }

    private static void configureInstalledAWTFonts(FontInfo fontInfo, Graphics2D graphics,
            int startNumber) {
        int num = startNumber;
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] allFontFamilies = env.getAvailableFontFamilyNames();
        for (int i = 0; i < allFontFamilies.length; i++) {            String family = allFontFamilies[i];
            if (HARDCODED_FONT_NAMES.contains(family)) {
                continue; //skip
            }

            if (log.isDebugEnabled()) {
                log.debug("Registering: " + family);
            }

            //Java does not give info about what variants of a font is actually supported, so
            //we simply register all the basic variants. If we use GraphicsEnvironment.getAllFonts()
            //we don't get reliable info whether a font is italic or bold or both.
            int fontStyle;
            fontStyle = java.awt.Font.PLAIN;
            registerFontTriplet(fontInfo, family, fontStyle, "F" + num, graphics);
            num++;

            fontStyle = java.awt.Font.ITALIC;
            registerFontTriplet(fontInfo, family, fontStyle, "F" + num, graphics);
            num++;

            fontStyle = java.awt.Font.BOLD;
            registerFontTriplet(fontInfo, family, fontStyle, "F" + num, graphics);
            num++;

            fontStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
            registerFontTriplet(fontInfo, family, fontStyle, "F" + num, graphics);
            num++;

        }
    }

    private static void registerFontTriplet(FontInfo fontInfo, String family, int fontStyle,
            String fontKey, Graphics2D graphics) {
        FontMetricsMapper metric = new FontMetricsMapper(family, fontStyle, graphics);
        fontInfo.addMetrics(fontKey, metric);

        int weight = Font.NORMAL;
        if ((fontStyle & java.awt.Font.BOLD) != 0) {
            weight = Font.BOLD;
        }
        String style = "normal";
        if ((fontStyle & java.awt.Font.ITALIC) != 0) {
            style = "italic";
        }
        FontTriplet triplet = FontInfo.createFontKey(family, style, weight);
        fontInfo.addFontProperties(fontKey, triplet);
    }

    private static int addCustomFont(FontInfo fontInfo, Graphics2D graphics ,int startNumber)
    {
//        if(_document == null)
//        {
//            return startNumber;
//        }

        int normal, bold, bolditalic, italic;
        normal = java.awt.Font.PLAIN;
        bold = java.awt.Font.BOLD;
        italic = java.awt.Font.ITALIC;
        bolditalic = java.awt.Font.BOLD + java.awt.Font.ITALIC;

//        Element font_mappings = (Element)_document.selectSingleNode("/wdde-configuration/font-mappings");
//        if(font_mappings == null)
//        {
//            return startNumber;
//        }

//        List fontsList = font_mappings.selectNodes("font-mapping");
        FontMetricsMapper metric;
//        Element font;
        String fontName;

//        Map fontMap = null;
        
//        String osname = System.getProperty("os.name");
//        String javaversionJava = System.getProperty("java.version");
//        String javavmspecificationvendor = System.getProperty("java.vm.specification.vendor");
//        System.err.println("osname is " + osname);
//        System.err.println("javaversionJava is " + javaversionJava);
//        System.err.println("javavmspecificationvendor is " + javavmspecificationvendor);
        
        
        //判断是否是Client端(windows 系统)
//        if(PreviewDialog.previewSelf != null)
//        {
////            fontMap = PreviewDialog.previewSelf.configMap;
//        }
//        else if(PrintDialog.printDialogself != null)
//        {
////            fontMap = PrintDialog.printDialogself.configMap;
//
//        }
//        else if(PreviewDialogAPP.previewSelf != null)
//        {
////            fontMap = PreviewDialogAPP.previewSelf.configMap;
//        }
//        if(ReadConfig.configres != null)
//        {
//        	fontMap = ReadConfig.configres;
//        }
        
        if(SystemUtil.getConfByName("font.define") == null)
        {
            return startNumber;
        }
        
        String[] allfont = String.valueOf(SystemUtil.getConfByName("font.define")).split(",");

        for(int i=0;i<allfont.length;i++)
        {
            fontName=  String.valueOf(SystemUtil.getConfByName("font."+allfont[i]+".name"));
            if(HARDCODED_FONT_NAMES.contains(fontName) || fontName.equals("宋体"))
            {
                continue; //skip
            }

            metric = new FontMetricsMapper(fontName, normal, graphics);
            fontInfo.addMetrics("F" + startNumber, metric);
            fontInfo.addFontProperties("F" + startNumber, fontName, "normal", Font.NORMAL);
            startNumber++;

            metric = new FontMetricsMapper(fontName, italic, graphics);
            fontInfo.addMetrics("F" + startNumber, metric);
            fontInfo.addFontProperties("F" + startNumber, fontName, "oblique", Font.NORMAL);
            fontInfo.addFontProperties("F" + startNumber, fontName, "italic", Font.NORMAL);
            startNumber++;

            metric = new FontMetricsMapper(fontName, bold, graphics);
            fontInfo.addMetrics("F" + startNumber, metric);
            fontInfo.addFontProperties("F" + startNumber, fontName, "normal", Font.BOLD);
            startNumber++;

            metric = new FontMetricsMapper(fontName, bolditalic, graphics);
            fontInfo.addMetrics("F" + startNumber, metric);
            fontInfo.addFontProperties("F" + startNumber, fontName, "oblique", Font.BOLD);
            fontInfo.addFontProperties("F" + startNumber, fontName, "italic", Font.BOLD);
            startNumber++;
        }


//        for(int i=0;i<fontsList.size();i++)
//        {
//            font = (Element)fontsList.get(i);
//            fontName = font.selectSingleNode("@name").getText();
//
//            if(HARDCODED_FONT_NAMES.contains(fontName) || fontName.equals("宋体"))
//            {
//                continue; //skip
//            }
//
//            metric = new FontMetricsMapper(fontName, normal, graphics);
//            fontInfo.addMetrics("F" + startNumber, metric);
//            fontInfo.addFontProperties("F" + startNumber, fontName, "normal", Font.NORMAL);
//            startNumber++;
//
//            metric = new FontMetricsMapper(fontName, italic, graphics);
//            fontInfo.addMetrics("F" + startNumber, metric);
//            fontInfo.addFontProperties("F" + startNumber, fontName, "oblique", Font.NORMAL);
//            fontInfo.addFontProperties("F" + startNumber, fontName, "italic", Font.NORMAL);
//            startNumber++;
//
//            metric = new FontMetricsMapper(fontName, bold, graphics);
//            fontInfo.addMetrics("F" + startNumber, metric);
//            fontInfo.addFontProperties("F" + startNumber, fontName, "normal", Font.BOLD);
//            startNumber++;
//
//            metric = new FontMetricsMapper(fontName, bolditalic, graphics);
//            fontInfo.addMetrics("F" + startNumber, metric);
//            fontInfo.addFontProperties("F" + startNumber, fontName, "oblique", Font.BOLD);
//            fontInfo.addFontProperties("F" + startNumber, fontName, "italic", Font.BOLD);
//            startNumber++;
//        }
        
        return startNumber;
    }

}

