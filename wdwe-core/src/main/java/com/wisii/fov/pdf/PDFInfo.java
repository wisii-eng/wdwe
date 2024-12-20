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

/* $Id: PDFInfo.java 426576 2006-07-28 15:44:37Z jeremias $ */
 
package com.wisii.fov.pdf;

import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * class representing an /Info object
 */
public class PDFInfo extends PDFObject {

    /**
     * the application producing the PDF
     */
    private String producer;

    private String title = null;
    private String author = null;
    private String subject = null;
    private String keywords = null;
    private Date creationDate = null;
    private Date modDate = null;

    /**
     * the name of the application that created the
     * original document before converting to PDF
     */
    private String creator;

    /** @return the producer of the document or null if not set */
    public String getProducer() {
        return this.producer;
    }
    
    /**
     * set the producer string
     *
     * @param producer the producer string
     */
    public void setProducer(String producer) {
        this.producer = producer;
    }

    /** @return the creator of the document or null if not set */
    public String getCreator() {
        return this.creator;
    }
    
    /**
     * set the creator string
     *
     * @param creator the document creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /** @return the title string */
    public String getTitle() {
        return this.title;
    }

    /**
     * set the title string
     *
     * @param t the document title
     */
    public void setTitle(String t) {
        this.title = t;
    }

    /** @return the author of the document or null if not set */
    public String getAuthor() {
        return this.author;
    }
    
    /**
     * set the author string
     *
     * @param a the document author
     */
    public void setAuthor(String a) {
        this.author = a;
    }

    /** @return the subject of the document or null if not set */
    public String getSubject() {
        return this.subject;
    }
    
    /**
     * set the subject string
     *
     * @param s the document subject
     */
    public void setSubject(String s) {
        this.subject = s;
    }

    /** @return the keywords for the document or null if not set */
    public String getKeywords() {
        return this.keywords;
    }
    
    /**
     * set the keywords string
     *
     * @param k the keywords for this document
     */
    public void setKeywords(String k) {
        this.keywords = k;
    }

    /**
     * @return last set creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param date Date to store in the PDF as creation date. Use null to force current system date.
     */
    public void setCreationDate(Date date) {
        creationDate = date;
    }

    /** @return last modification date
     */
    public Date getModDate() {
        return this.modDate;
    }

    /**
     * Sets the date of the last modification.
     * @param date the last modification date or null if there are no modifications
     */
    public void setModDate(Date date) {
        this.modDate = date;
    }

    /**
     * @see com.wisii.fov.pdf.PDFObject#toPDF()
     */
    public byte[] toPDF() {
        PDFProfile profile = getDocumentSafely().getProfile(); 
        ByteArrayOutputStream bout = new ByteArrayOutputStream(128);
        try {
            bout.write(encode(getObjectID()));
            bout.write(encode("<<\n"));
            if (title != null && title.length() > 0) {
                bout.write(encode("/Title "));
                bout.write(encodeText(this.title));
                bout.write(encode("\n"));
            } else {
                profile.verifyTitleAbsent();
            }
            if (author != null) {
                bout.write(encode("/Author "));
                bout.write(encodeText(this.author));
                bout.write(encode("\n"));
            }
            if (subject != null) {
                bout.write(encode("/Subject "));
                bout.write(encodeText(this.subject));
                bout.write(encode("\n"));
            }
            if (keywords != null) {
                bout.write(encode("/Keywords "));
                bout.write(encodeText(this.keywords));
                bout.write(encode("\n"));
            }
    
            if (creator != null) {
                bout.write(encode("/Creator "));
                bout.write(encodeText(this.creator));
                bout.write(encode("\n"));
            }
    
            bout.write(encode("/Producer "));
            bout.write(encodeText(this.producer));
            bout.write(encode("\n"));
    
            // creation date in form (D:YYYYMMDDHHmmSSOHH'mm')
            if (creationDate == null) {
                creationDate = new Date();
            }
            bout.write(encode("/CreationDate "));
            bout.write(encodeString(formatDateTime(creationDate)));
            bout.write(encode("\n"));
            
            if (profile.isModDateRequired() && this.modDate == null) {
                this.modDate = this.creationDate;
            }
            if (this.modDate != null) {
                bout.write(encode("/ModDate "));
                bout.write(encodeString(formatDateTime(modDate)));
                bout.write(encode("\n"));
            }
            if (profile.isPDFXActive()) {
                bout.write(encode("/GTS_PDFXVersion "));
                bout.write(encodeString(profile.getPDFXMode().getName()));
                bout.write(encode("\n"));
            }
            if (profile.isTrappedEntryRequired()) {
                bout.write(encode("/Trapped /False\n"));
            }
            
            bout.write(encode(">>\nendobj\n"));
        } catch (IOException ioe) {
            log.error("Ignored I/O exception", ioe);
        }
        return bout.toByteArray();
    }

}

