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

/* $Id: PDFICCStream.java 426576 2006-07-28 15:44:37Z jeremias $ */
 
package com.wisii.fov.pdf;

import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Special PDFStream for ICC profiles (color profiles).
 */
public class PDFICCStream extends PDFStream {
    
    private ICC_Profile cp;
    private PDFDeviceColorSpace pdfColorSpace;

    /**
     * @see com.wisii.fov.pdf.PDFObject#PDFObject()
     */
    public PDFICCStream() {
        super();
        cp = null;
    }

    /**
     * Sets the color space to encode in PDF.
     * @param icc the ICC profile
     * @param alt the PDF color space
     */
    public void setColorSpace(ICC_Profile icc, PDFDeviceColorSpace alt) {
        this.cp = icc;
        pdfColorSpace = alt;
    }

    /** @return the ICC profile */
    public ICC_Profile getICCProfile() {
        return this.cp;
    }
    
    /**
     * overload the base object method so we don't have to copy
     * byte arrays around so much
     * @see com.wisii.fov.pdf.PDFObject#output(OutputStream)
     */
    protected int output(java.io.OutputStream stream)
                throws java.io.IOException {
        int length = super.output(stream);
        this.cp = null; //Free ICC stream when it's not used anymore
        return length;
    }
    
    /**
     * @see com.wisii.fov.pdf.PDFStream#outputRawStreamData(OutputStream)
     */
    protected void outputRawStreamData(OutputStream out) throws IOException {
        cp.write(out);
    }
    
    /**
     * @see com.wisii.fov.pdf.AbstractPDFStream#buildStreamDict(String)
     */
    protected String buildStreamDict(String lengthEntry) {
        final String filterEntry = getFilterList().buildFilterDictEntries();
        final StringBuffer sb = new StringBuffer(128);
        sb.append(getObjectID());
        sb.append("<< ");
        sb.append("/N " + cp.getNumComponents());

        if (pdfColorSpace != null) {
            sb.append("\n/Alternate /" + pdfColorSpace.getName() + " ");
        }

        sb.append("\n/Length " + lengthEntry);
        sb.append("\n" + filterEntry);
        sb.append("\n>>\n");
        return sb.toString();
    }

}
