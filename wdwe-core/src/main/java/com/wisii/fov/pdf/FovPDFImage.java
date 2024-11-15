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

/* $Id: FovPDFImage.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.pdf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.fov.pdf.PDFConformanceException;
import com.wisii.fov.pdf.PDFFilterList;
import com.wisii.fov.pdf.PDFICCBasedColorSpace;
import com.wisii.fov.pdf.PDFImage;
import com.wisii.fov.pdf.PDFFilter;
import com.wisii.fov.pdf.PDFICCStream;
import com.wisii.fov.pdf.PDFColor;
import com.wisii.fov.pdf.PDFDocument;
import com.wisii.fov.pdf.DCTFilter;
import com.wisii.fov.pdf.CCFFilter;
import com.wisii.fov.pdf.PDFDeviceColorSpace;
import com.wisii.fov.pdf.PDFXObject;
import com.wisii.fov.pdf.BitmapImage;
import com.wisii.fov.util.ColorProfileUtil;

import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.EPSImage;
import com.wisii.fov.image.TIFFImage;

import java.io.IOException;
import java.io.OutputStream;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;

/**
 * PDFImage implementation for the PDF renderer.
 */
public class FovPDFImage implements PDFImage {

    /** logging instance */
    private static Log log = LogFactory.getLog(FovPDFImage.class);

    private FovImage fovImage;
    private PDFICCStream pdfICCStream = null;
    private PDFFilter pdfFilter = null;
    private String maskRef;
    private String softMaskRef;
    private boolean isPS = false;
    private boolean isCCF = false;
    private boolean isDCT = false;
    private String key;

    /**
     * Creates a new PDFImage from a FovImage
     * @param image Image
     * @param key XObject key
     */
    public FovPDFImage(FovImage image, String key) {
        fovImage = image;
        this.key = key;
        isPS = (fovImage instanceof EPSImage);
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getKey()
     */
    public String getKey() {
        // key to look up XObject
        return this.key;
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#setup(PDFDocument)
     */
    public void setup(PDFDocument doc) {
        if ("image/jpeg".equals(fovImage.getMimeType())) {
            pdfFilter = new DCTFilter();
            pdfFilter.setApplied(true);
            isDCT = true;

        } else if ("image/tiff".equals(fovImage.getMimeType())
                    && fovImage instanceof TIFFImage) {
            TIFFImage tiffImage = (TIFFImage) fovImage;
            if (tiffImage.getStripCount() == 1) {
                int comp = tiffImage.getCompression();
                if (comp == 1) {
                    // Nothing to do
                } else if (comp == 3) {
                    pdfFilter = new CCFFilter();
                    pdfFilter.setApplied(true);
                    isCCF = true;
                } else if (comp == 4) {
                    pdfFilter = new CCFFilter();
                    pdfFilter.setApplied(true);
                    ((CCFFilter)pdfFilter).setDecodeParms("<< /K -1 /Columns " 
                        + tiffImage.getWidth() + " >>");
                    isCCF = true;
                } else if (comp == 6) {
                    pdfFilter = new DCTFilter();
                    pdfFilter.setApplied(true);
                    isDCT = true;
                }
            }
        }
        if (isPS || isDCT || isCCF) {
            fovImage.load(FovImage.ORIGINAL_DATA);
        } else {
            fovImage.load(FovImage.BITMAP);
        }
        ICC_Profile prof = fovImage.getICCProfile();
        PDFDeviceColorSpace pdfCS = toPDFColorSpace(fovImage.getColorSpace());
        if (prof != null) {
            boolean defaultsRGB = ColorProfileUtil.isDefaultsRGB(prof);
            String desc = ColorProfileUtil.getICCProfileDescription(prof);
            if (log.isDebugEnabled()) {
                log.debug("Image returns ICC profile: " + desc + ", default sRGB=" + defaultsRGB);
            }
            PDFICCBasedColorSpace cs = doc.getResources().getICCColorSpaceByProfileName(desc);
            if (!defaultsRGB) {
                if (cs == null) {
                    pdfICCStream = doc.getFactory().makePDFICCStream();
                    pdfICCStream.setColorSpace(prof, pdfCS);
                    cs = doc.getFactory().makeICCBasedColorSpace(null, null, pdfICCStream);
                } else {
                    pdfICCStream = cs.getICCStream();
                }
            } else {
                if (cs == null && "sRGB".equals(desc)) {
                    //It's the default sRGB profile which we mapped to DefaultRGB in PDFRenderer
                    cs = doc.getResources().getColorSpace("DefaultRGB");
                }
                pdfICCStream = cs.getICCStream();
            }
        }
        //Handle transparency mask if applicable
        if (fovImage.hasSoftMask()) {
            doc.getProfile().verifyTransparencyAllowed(fovImage.getOriginalURI());
            //TODO Implement code to combine image with background color if transparency is not
            //allowed (need BufferedImage support for that)
            byte [] softMask = fovImage.getSoftMask();
            if (softMask == null) {
                return;
            }
            BitmapImage fovimg = new BitmapImage
                ("Mask:" + key, fovImage.getWidth(), fovImage.getHeight(), 
                 softMask, null);
            fovimg.setColorSpace(new PDFDeviceColorSpace(PDFDeviceColorSpace.DEVICE_GRAY));
            PDFXObject xobj = doc.addImage(null, fovimg);
            softMaskRef = xobj.referencePDF();
        }
        if (doc.getProfile().getPDFAMode().isPDFA1LevelB()) {
            if (pdfCS != null
                    && pdfCS.getColorSpace() != PDFDeviceColorSpace.DEVICE_RGB 
                    && pdfCS.getColorSpace() != PDFDeviceColorSpace.DEVICE_GRAY
                    && prof == null) {
                //See PDF/A-1, ISO 19005:1:2005(E), 6.2.3.3
                //FOV is currently restricted to DeviceRGB if PDF/A-1 is active.
                throw new PDFConformanceException(
                        "PDF/A-1 does not allow mixing DeviceRGB and DeviceCMYK: " 
                                + fovImage.getOriginalURI());
            }
        }
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getWidth()
     */
    public int getWidth() {
        return fovImage.getWidth();
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getHeight()
     */
    public int getHeight() {
        return fovImage.getHeight();
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getColorSpace()
     */
    public PDFDeviceColorSpace getColorSpace() {
        // DeviceGray, DeviceRGB, or DeviceCMYK
        if (isCCF || isDCT || isPS) {
            return toPDFColorSpace(fovImage.getColorSpace());
        } else {
            return toPDFColorSpace(ColorSpace.getInstance(ColorSpace.CS_sRGB));
        }
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getBitsPerPixel()
     */
    public int getBitsPerPixel() {
        if (isCCF) {
            return fovImage.getBitsPerPixel();
        } else {
            return 8; //TODO This is suboptimal, handling everything as RGB
            //The image wrappers can mostly only return RGB bitmaps right now. This should
            //be improved so the renderers can deal directly with RenderedImage instances.
        }
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#isTransparent()
     */
    public boolean isTransparent() {
        return fovImage.isTransparent();
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getTransparentColor()
     */
    public PDFColor getTransparentColor() {
        return new PDFColor(fovImage.getTransparentColor().getRed(),
                            fovImage.getTransparentColor().getGreen(),
                            fovImage.getTransparentColor().getBlue());
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getMask()
     */
    public String getMask() {
        return maskRef;
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getSoftMask()
     */
    public String getSoftMask() {
        return softMaskRef;
    }

    /** @return true for CMYK images generated by Adobe Photoshop */
    public boolean isInverted() {
        return fovImage.isInverted();
    }
    
    /**
     * @see com.wisii.fov.pdf.PDFImage#isPS()
     */
    public boolean isPS() {
        return isPS;
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getPDFFilter()
     */
    public PDFFilter getPDFFilter() {
        return pdfFilter;
    }
    
    /**
     * @see com.wisii.fov.pdf.PDFImage#outputContents(OutputStream)
     */
    public void outputContents(OutputStream out) throws IOException {
        if (isPS) {
            outputPostScriptContents(out);
        } else {
            if (fovImage.getBitmapsSize() > 0) {
                out.write(fovImage.getBitmaps());
            } else {
                out.write(fovImage.getRessourceBytes());
            }
        }
    }

    /**
     * Serializes an EPS image to an OutputStream.
     * @param out OutputStream to write to
     * @throws IOException in case of an I/O problem
     */
    protected void outputPostScriptContents(OutputStream out) throws IOException {
        EPSImage epsImage = (EPSImage) fovImage;
        int[] bbox = epsImage.getBBox();
        int bboxw = bbox[2] - bbox[0];
        int bboxh = bbox[3] - bbox[1];

        // delegate the stream work to PDFStream
        //PDFStream imgStream = new PDFStream(0);

        StringBuffer preamble = new StringBuffer();
        preamble.append("%%BeginDocument: " + epsImage.getDocName() + "\n");

        preamble.append("userdict begin                 % Push userdict on dict stack\n");
        preamble.append("/PreEPS_state save def\n");
        preamble.append("/dict_stack countdictstack def\n");
        preamble.append("/ops_count count 1 sub def\n");
        preamble.append("/showpage {} def\n");


        preamble.append((double)(1f / (double) bboxw) + " "
                      + (double)(1f / (double) bboxh) + " scale\n");
        preamble.append(-bbox[0] + " " + (-bbox[1]) + " translate\n");
        preamble.append(bbox[0] + " " + bbox[1] + " "
                        + bboxw + " " + bboxh + " rectclip\n");
        preamble.append("newpath\n");

        StringBuffer post = new StringBuffer();
        post.append("%%EndDocument\n");
        post.append("count ops_count sub {pop} repeat\n");
        post.append("countdictstack dict_stack sub {end} repeat\n");
        post.append("PreEPS_state restore\n");
        post.append("end % userdict\n");

        //Write Preamble
        out.write(PDFDocument.encode(preamble.toString()));
        //Write EPS contents
        out.write(((EPSImage)fovImage).getEPSImage());
        //Writing trailer
        out.write(PDFDocument.encode(post.toString()));
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getICCStream()
     */
    public PDFICCStream getICCStream() {
        return pdfICCStream;
    }

    /**
     * Converts a ColorSpace object to a PDFColorSpace object.
     * @param cs ColorSpace instance
     * @return PDFColorSpace new converted object
     */
    public static PDFDeviceColorSpace toPDFColorSpace(ColorSpace cs) {
        if (cs == null) {
            return null;
        }

        PDFDeviceColorSpace pdfCS = new PDFDeviceColorSpace(0);
        switch(cs.getType()) {
            case ColorSpace.TYPE_CMYK:
                pdfCS.setColorSpace(PDFDeviceColorSpace.DEVICE_CMYK);
            break;
            case ColorSpace.TYPE_RGB:
                pdfCS.setColorSpace(PDFDeviceColorSpace.DEVICE_RGB);
            break;
            case ColorSpace.TYPE_GRAY:
                pdfCS.setColorSpace(PDFDeviceColorSpace.DEVICE_GRAY);
            break;
        }
        return pdfCS;
    }

    /**
     * @see com.wisii.fov.pdf.PDFImage#getFilterHint()
     */
    public String getFilterHint() {
        if (isPS) {
            return PDFFilterList.CONTENT_FILTER;
        } else if (isDCT) {
            return PDFFilterList.JPEG_FILTER;
        } else if (isCCF) {
            return PDFFilterList.TIFF_FILTER;
        } else {
            return PDFFilterList.IMAGE_FILTER;
        }
    }

}

