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

/* $Id$ */
 
package com.wisii.fov.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSerializer;
import org.apache.xmlgraphics.xmp.schemas.DublinCoreAdapter;
import org.apache.xmlgraphics.xmp.schemas.DublinCoreSchema;
import org.apache.xmlgraphics.xmp.schemas.XMPBasicAdapter;
import org.apache.xmlgraphics.xmp.schemas.XMPBasicSchema;
import org.apache.xmlgraphics.xmp.schemas.pdf.AdobePDFAdapter;
import org.apache.xmlgraphics.xmp.schemas.pdf.AdobePDFSchema;
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFAAdapter;
//import org.apache.xmlgraphics.xmp.schemas.pdf.PDFAOldXMPSchema;
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFAXMPSchema;

import org.xml.sax.SAXException;

/**
 * Special PDFStream for Metadata.
 * @since PDF 1.4
 */
public class PDFMetadata extends PDFStream {
    
    private Metadata xmpMetadata;
    private boolean readOnly = true;

    /** @see com.wisii.fov.pdf.PDFObject#PDFObject() */
    public PDFMetadata(Metadata xmp, boolean readOnly) {
        super();
        if (xmp == null) {
            throw new NullPointerException(
                    "The parameter for the XMP Document must not be null");
        }
        this.xmpMetadata = xmp;
        this.readOnly = readOnly;
    }

    /** @see com.wisii.fov.pdf.AbstractPDFStream#setupFilterList() */
    protected void setupFilterList() {
        if (!getFilterList().isInitialized()) {
            getFilterList().addDefaultFilters(
                getDocumentSafely().getFilterMap(), 
                PDFFilterList.METADATA_FILTER);
        }
        super.setupFilterList();
    }

    /** @see com.wisii.fov.pdf.AbstractPDFStream#allowEncryption() */
    protected boolean allowEncryption() {
        return false; //XMP metadata packet must be scannable by non PDF-compatible readers
    }

    /** @return the XMP metadata */
    public Metadata getMetadata() {
        return this.xmpMetadata;
    }
    
    /**
     * overload the base object method so we don't have to copy
     * byte arrays around so much
     * @see com.wisii.fov.pdf.PDFObject#output(OutputStream)
     */
    protected int output(java.io.OutputStream stream)
                throws java.io.IOException {
        int length = super.output(stream);
        this.xmpMetadata = null; //Release DOM when it's not used anymore
        return length;
    }
    
    /** @see com.wisii.fov.pdf.AbstractPDFStream#outputRawStreamData(java.io.OutputStream) */
    protected void outputRawStreamData(OutputStream out) throws IOException {
        try {
            XMPSerializer.writeXMPPacket(xmpMetadata, out, this.readOnly);
        } catch (TransformerConfigurationException tce) {
            throw new IOException("Error setting up Transformer for XMP stream serialization: " 
                    + tce.getMessage());
        } catch (SAXException saxe) {
            throw new IOException("Error while serializing XMP stream: " 
                    + saxe.getMessage());
        }
    }
    
    /** @see com.wisii.fov.pdf.AbstractPDFStream#buildStreamDict(String) */
    protected String buildStreamDict(String lengthEntry) {
        final String filterEntry = getFilterList().buildFilterDictEntries();
        if (getDocumentSafely().getProfile().getPDFAMode().isPDFA1LevelB() 
                && filterEntry != null && filterEntry.length() > 0) {
            throw new PDFConformanceException(
                    "The Filter key is prohibited when PDF/A-1 is active");
        }
        final StringBuffer sb = new StringBuffer(128);
        sb.append(getObjectID());
        sb.append("<< ");
        sb.append("/Type /Metadata");
        sb.append("\n/Subtype /XML");
        sb.append("\n/Length " + lengthEntry);
        sb.append("\n" + filterEntry);
        sb.append("\n>>\n");
        return sb.toString();
    }

    /**
     * Creates an XMP document based on the settings on the PDF Document.
     * @param pdfDoc the PDF Document
     * @return the requested XMP metadata
     */
    public static Metadata createXMPFromUserAgent(PDFDocument pdfDoc) {
        Metadata meta = new Metadata();
        
        PDFInfo info = pdfDoc.getInfo();

        //Set creation date if not available, yet
        if (info.getCreationDate() == null) {
            Date d = new Date();
            info.setCreationDate(d);
        }
        
        //Important: Acrobat's preflight check for PDF/A-1b wants the creation date in the Info
        //object and in the XMP metadata to have the same timezone or else it shows a validation
        //error even if the times are essentially equal.

        //Dublin Core
        DublinCoreAdapter dc = DublinCoreSchema.getAdapter(meta);
        if (info.getAuthor() != null) {
            dc.addCreator(info.getAuthor());
        }
        if (info.getTitle() != null) {
            dc.setTitle(info.getTitle());
        }
        if (info.getSubject() != null) {
            dc.addSubject(info.getSubject());
        }
        dc.addDate(info.getCreationDate());

        //PDF/A identification
        PDFAMode pdfaMode = pdfDoc.getProfile().getPDFAMode(); 
        if (pdfaMode.isPDFA1LevelB()) {
            PDFAAdapter pdfa = PDFAXMPSchema.getAdapter(meta);
            //Create the identification a second time with the old namespace to keep 
            //Adobe Acrobat happy
//            PDFAAdapter pdfaOld = PDFAOldXMPSchema.getAdapter(meta);
            pdfa.setPart(1);
//            pdfaOld.setPart(1);
            if (pdfaMode == PDFAMode.PDFA_1A) {
                pdfa.setConformance("A"); //PDF/A-1a
//                pdfaOld.setConformance("A"); //PDF/A-1a
            } else {
                pdfa.setConformance("B"); //PDF/A-1b
//                pdfaOld.setConformance("B"); //PDF/A-1b
            }
        }
        
        //XMP Basic Schema
        XMPBasicAdapter xmpBasic = XMPBasicSchema.getAdapter(meta);
        xmpBasic.setCreateDate(info.getCreationDate());
        PDFProfile profile = pdfDoc.getProfile(); 
        if (profile.isModDateRequired()) {
            xmpBasic.setModifyDate(info.getCreationDate());
        }
        if (info.getCreator() != null) {
            xmpBasic.setCreatorTool(info.getCreator());
        }

        AdobePDFAdapter adobePDF = AdobePDFSchema.getAdapter(meta);
        if (info.getKeywords() != null) {
            adobePDF.setKeywords(info.getKeywords());
        }
        if (info.getProducer() != null) {
            adobePDF.setProducer(info.getProducer());
        }
        adobePDF.setPDFVersion(pdfDoc.getPDFVersionString());
        
        
        return meta;
    }

    /**
     * Updates the values in the Info object from the XMP metadata according to the rules defined
     * in PDF/A-1 (ISO 19005-1:2005)
     * @param meta the metadata
     * @param info the Info object
     */
    public static void updateInfoFromMetadata(Metadata meta, PDFInfo info) {
        DublinCoreAdapter dc = DublinCoreSchema.getAdapter(meta);
        info.setTitle(dc.getTitle());
        String[] creators = dc.getCreators();
        if (creators != null && creators.length > 0) {
            info.setAuthor(creators[0]);
        } else {
            info.setAuthor(null);
        }
        String[] subjects = dc.getSubjects();
        //PDF/A-1 defines dc:subject as "Text" but XMP defines it as "bag Text".
        //We're simply doing the inverse from createXMPFromUserAgent() above.
        if (subjects != null && subjects.length > 0) {
            info.setSubject(subjects[0]);
        } else {
            info.setSubject(null);
        }
        
        AdobePDFAdapter pdf = AdobePDFSchema.getAdapter(meta);
        info.setKeywords(pdf.getKeywords());
        info.setProducer(pdf.getProducer());
        
        XMPBasicAdapter xmpBasic = XMPBasicSchema.getAdapter(meta);
        info.setCreator(xmpBasic.getCreatorTool());
        Date d;
        d = xmpBasic.getCreateDate();
        xmpBasic.setCreateDate(d); //To make Adobe Acrobat happy (bug filed with Adobe)
        //Adobe Acrobat doesn't like it when the xmp:CreateDate has a different timezone
        //than Info/CreationDate
        info.setCreationDate(d);
        d = xmpBasic.getModifyDate();
        if (d != null) { //ModifyDate is only required for PDF/X
            xmpBasic.setModifyDate(d);
            info.setModDate(d);
        }
    }
}
