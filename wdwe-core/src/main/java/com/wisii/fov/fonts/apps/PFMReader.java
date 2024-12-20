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
 *//* $Id: PFMReader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fonts.apps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wisii.Version;
import com.wisii.fov.fonts.type1.PFMFile;
import com.wisii.fov.util.CommandLineLogger;

/**
 * A tool which reads PFM files from Adobe Type 1 fonts and creates
 * XML font metrics file for use in FOV.
 */
public class PFMReader extends AbstractFontReader {

    /**
     * Main constructor.
     */
    public PFMReader() {
        super();
    }

    private static void displayUsage() {
        System.out.println(
            "java " + PFMReader.class.getName() + " [options] metricfile.pfm xmlfile.xml");
        System.out.println();
        System.out.println("where options can be:");
        System.out.println("-d  Debug mode");
        System.out.println("-q  Quiet mode");
        System.out.println("-fn <fontname>");
        System.out.println("    default is to use the fontname in the .pfm file, but");
        System.out.println("    you can override that name to make sure that the");
        System.out.println("    embedded font is used (if you're embedding fonts)");
        System.out.println("    instead of installed fonts when viewing documents ");
        System.out.println("    with Acrobat Reader.");
    }


    /**
     * The main method for the PFM reader tool.
     *
     * @param  args Command-line arguments: [options] metricfile.pfm xmlfile.xml
     * where options can be:
     * -fn <fontname>
     * default is to use the fontname in the .pfm file, but you can override
     * that name to make sure that the embedded font is used instead of installed
     * fonts when viewing documents with Acrobat Reader.
     * -cn <classname>
     * default is to use the fontname
     * -ef <path to the Type1 .pfb fontfile>
     * will add the possibility to embed the font. When running fov, fov will look
     * for this file to embed it
     * -er <path to Type1 fontfile relative to org/apache/fov/render/pdf/fonts>
     * you can also include the fontfile in the fov.jar file when building fov.
     * You can use both -ef and -er. The file specified in -ef will be searched first,
     * then the -er file.
     */
    public static void main(String[] args) {
        String embFile = null;
        String embResource = null;
        String className = null;
        String fontName = null;

        Map options = new java.util.HashMap();
        String[] arguments = parseArguments(options, args);

        // Enable the simple command line logging when no other logger is
        // defined.
        LogFactory logFactory = LogFactory.getFactory();
        if (System.getProperty("com.wisii.commons.logging.Log") == null) {
            logFactory.setAttribute("com.wisii.commons.logging.Log",
                                            CommandLineLogger.class.getName());
        }

        determineLogLevel(options);

        PFMReader app = new PFMReader();

        log.info("PFM Reader for Apache FOV " + Version.getVersion() + "\n");

        if (options.get("-ef") != null) {
            embFile = (String)options.get("-ef");
        }

        if (options.get("-er") != null) {
            embResource = (String)options.get("-er");
        }

        if (options.get("-fn") != null) {
            fontName = (String)options.get("-fn");
        }

        if (options.get("-cn") != null) {
            className = (String)options.get("-cn");
        }

        if (arguments.length != 2 || options.get("-h") != null
            || options.get("-help") != null || options.get("--help") != null) {
            displayUsage();
        } else {
            try {
                log.info("Parsing font...");
                PFMFile pfm = app.loadPFM(arguments[0]);
                if (pfm != null) {
                    app.preview(pfm);

                    Document doc = app.constructFontXML(pfm,
                            fontName, className, embResource, embFile);

                    app.writeFontXML(doc, arguments[1]);
                }
                log.info("XML font metrics file successfullly created.");
            } catch (Exception e) {
                log.error("Error while building XML font metrics file", e);
                //System.exit(-1);//del by huangzl
            }
        }
    }

    /**
     * Read a PFM file and returns it as an object.
     *
     * @param   filename The filename of the PFM file.
     * @return  The PFM as an object.
     * @throws IOException In case of an I/O problem
     */
    public PFMFile loadPFM(String filename) throws IOException {
        log.info("Reading " + filename + "...");
        log.info("");
        InputStream in = new java.io.FileInputStream(filename);
        try {
            PFMFile pfm = new PFMFile();
            pfm.load(in);
            return pfm;
        } finally {
            in.close();
        }
    }

    /**
     * Displays a preview of the PFM file on the console.
     *
     * @param   pfm The PFM file to preview.
     */
    public void preview(PFMFile pfm) {
        if (log != null & log.isInfoEnabled()) {
            log.info("Font: " + pfm.getWindowsName());
            log.info("Name: " + pfm.getPostscriptName());
            log.info("CharSet: " + pfm.getCharSetName());
            log.info("CapHeight: " + pfm.getCapHeight());
            log.info("XHeight: " + pfm.getXHeight());
            log.info("LowerCaseAscent: " + pfm.getLowerCaseAscent());
            log.info("LowerCaseDescent: " + pfm.getLowerCaseDescent());
            log.info("Having widths for " + (pfm.getLastChar() - pfm.getFirstChar())
                        + " characters (" + pfm.getFirstChar()
                        + "-" + pfm.getLastChar() + ").");
            log.info("for example: Char " + pfm.getFirstChar()
                        + " has a width of " + pfm.getCharWidth(pfm.getFirstChar()));
            log.info("");
       }
    }

    /**
     * Generates the font metrics file from the PFM file.
     *
     * @param pfm The PFM file to generate the font metrics from.
     * @param fontName name of the font
     * @param className class name for the font
     * @param resource path to the font as embedded resource
     * @param file path to the font as file
     * @return  The DOM document representing the font metrics file.
     */
    public org.w3c.dom.Document constructFontXML(PFMFile pfm,
            String fontName, String className, String resource, String file) {
        log.info("Creating xml font file...");
        log.info("");

        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            doc = factory.newDocumentBuilder().newDocument();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            log.error("Can't create DOM implementation", e);
            return null;
        }
        Element root = doc.createElement("font-metrics");
        doc.appendChild(root);
        root.setAttribute("type", "TYPE1");

        Element el = doc.createElement("font-name");
        root.appendChild(el);
        el.appendChild(doc.createTextNode(pfm.getPostscriptName()));

        String s = pfm.getPostscriptName();
        int pos = s.indexOf("-");
        if (pos >= 0) {
            char[] sb = new char[s.length() - 1];
            s.getChars(0, pos, sb, 0);
            s.getChars(pos + 1, s.length(), sb, pos);
            s = new String(sb);
        }

        el = doc.createElement("embed");
        root.appendChild(el);
        if (file != null) {
            el.setAttribute("file", file);
        }
        if (resource != null) {
            el.setAttribute("class", resource);
        }

        el = doc.createElement("encoding");
        root.appendChild(el);
        el.appendChild(doc.createTextNode(pfm.getCharSetName() + "Encoding"));

        el = doc.createElement("cap-height");
        root.appendChild(el);
        Integer value = new Integer(pfm.getCapHeight());
        el.appendChild(doc.createTextNode(value.toString()));

        el = doc.createElement("x-height");
        root.appendChild(el);
        value = new Integer(pfm.getXHeight());
        el.appendChild(doc.createTextNode(value.toString()));

        el = doc.createElement("ascender");
        root.appendChild(el);
        value = new Integer(pfm.getLowerCaseAscent());
        el.appendChild(doc.createTextNode(value.toString()));

        el = doc.createElement("descender");
        root.appendChild(el);
        value = new Integer(-pfm.getLowerCaseDescent());
        el.appendChild(doc.createTextNode(value.toString()));

        Element bbox = doc.createElement("bbox");
        root.appendChild(bbox);
        int[] bb = pfm.getFontBBox();
        final String[] names = {"left", "bottom", "right", "top"};
        for (int i = 0; i < names.length; i++) {
            el = doc.createElement(names[i]);
            bbox.appendChild(el);
            value = new Integer(bb[i]);
            el.appendChild(doc.createTextNode(value.toString()));
        }

        el = doc.createElement("flags");
        root.appendChild(el);
        value = new Integer(pfm.getFlags());
        el.appendChild(doc.createTextNode(value.toString()));

        el = doc.createElement("stemv");
        root.appendChild(el);
        value = new Integer(pfm.getStemV());
        el.appendChild(doc.createTextNode(value.toString()));

        el = doc.createElement("italicangle");
        root.appendChild(el);
        value = new Integer(pfm.getItalicAngle());
        el.appendChild(doc.createTextNode(value.toString()));

        el = doc.createElement("first-char");
        root.appendChild(el);
        value = new Integer(pfm.getFirstChar());
        el.appendChild(doc.createTextNode(value.toString()));

        el = doc.createElement("last-char");
        root.appendChild(el);
        value = new Integer(pfm.getLastChar());
        el.appendChild(doc.createTextNode(value.toString()));

        Element widths = doc.createElement("widths");
        root.appendChild(widths);

        for (short i = pfm.getFirstChar(); i <= pfm.getLastChar(); i++) {
            el = doc.createElement("char");
            widths.appendChild(el);
            el.setAttribute("idx", Integer.toString(i));
            el.setAttribute("wdt",
                            new Integer(pfm.getCharWidth(i)).toString());
        }


        // Get kerning
        Iterator iter = pfm.getKerning().keySet().iterator();
        while (iter.hasNext()) {
            Integer kpx1 = (Integer)iter.next();
            el = doc.createElement("kerning");
            el.setAttribute("kpx1", kpx1.toString());
            root.appendChild(el);
            Element el2 = null;

            Map h2 = (Map)pfm.getKerning().get(kpx1);
            Iterator enum2 = h2.keySet().iterator();
            while (enum2.hasNext()) {
                Integer kpx2 = (Integer)enum2.next();
                el2 = doc.createElement("pair");
                el2.setAttribute("kpx2", kpx2.toString());
                Integer val = (Integer)h2.get(kpx2);
                el2.setAttribute("kern", val.toString());
                el.appendChild(el2);
            }
        }
        return doc;
    }
}




