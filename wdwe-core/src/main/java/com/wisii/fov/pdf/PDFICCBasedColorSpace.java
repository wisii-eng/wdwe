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

/**
 * Represents an ICCBased color space in PDF.
 */
public class PDFICCBasedColorSpace extends PDFObject implements PDFColorSpace {

    private PDFICCStream iccStream;
    private String explicitName;
    
    /**
     * Constructs a the ICCBased color space with an explicit name (ex. "DefaultRGB").
     * @param explicitName an explicit name or null if a name should be generated
     * @param iccStream the ICC stream to associate with this color space
     */
    public PDFICCBasedColorSpace(String explicitName, PDFICCStream iccStream) {
        this.explicitName = explicitName;
        this.iccStream = iccStream;
    }
    
    /**
     * Constructs a the ICCBased color space.
     * @param iccStream the ICC stream to associate with this color space
     */
    public PDFICCBasedColorSpace(PDFICCStream iccStream) {
        this(null, iccStream);
    }
    
    /** @return the ICC stream associated with this color space */
    public PDFICCStream getICCStream() {
        return this.iccStream;
    }
    
    /** @see com.wisii.fov.pdf.PDFColorSpace#getNumComponents() */
    public int getNumComponents() {
        return iccStream.getICCProfile().getNumComponents();
    }

    /** @see com.wisii.fov.pdf.PDFColorSpace#getName() */
    public String getName() {
        if (explicitName != null) {
            return explicitName;
        } else {
            return "ICC" + iccStream.getObjectNumber();
        }
    }

    /** @see com.wisii.fov.pdf.PDFColorSpace#isDeviceColorSpace() */
    public boolean isDeviceColorSpace() {
        return false;
    }

    /** @see com.wisii.fov.pdf.PDFColorSpace#isRGBColorSpace() */
    public boolean isRGBColorSpace() {
        return getNumComponents() == 3;
    }

    /** @see com.wisii.fov.pdf.PDFColorSpace#isCMYKColorSpace() */
    public boolean isCMYKColorSpace() {
        return getNumComponents() == 4;
    }

    /** @see com.wisii.fov.pdf.PDFColorSpace#isGrayColorSpace() */
    public boolean isGrayColorSpace() {
        return getNumComponents() == 1;
    }

    /** @see com.wisii.fov.pdf.PDFObject#toPDFString() */
    protected String toPDFString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getObjectID());
        sb.append("[/ICCBased ").append(getICCStream().referencePDF()).append("]");
        sb.append("\nendobj\n");
        return sb.toString();
    }

}
