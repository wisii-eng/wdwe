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
 * @(#)MediaPrintableArea.java	1.12 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.wisii.fov.render.print;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

/**
 * Class MediaPrintableArea is a printing attribute used to distinguish
 * the printable and non-printable areas of media.
 * <p>
 * The printable area is specified to be a rectangle, within the overall
 * dimensions of a media.
 * <p>
 * Most printers cannot print on the entire surface of the media, due
 * to printer hardware limitations. This class can be used to query
 * the acceptable values for a supposed print job, and to request an area
 * within the constraints of the printable area to be used in a print job.
 * <p>
 * To query for the printable area, a client must supply a suitable context.
 * Without specifying at the very least the size of the media being used
 * no meaningful value for printable area can be obtained.
 * <p>
 * The attribute is not described in terms of the distance from the edge
 * of the paper, in part to emphasise that this attribute is not independent
 * of a particular media, but must be described within the context of a
 * choice of other attributes. Additionally it is usually more convenient
 * for a client to use the printable area.
 * <p>
 * The hardware's minimum margins is not just a property of the printer,
 * but may be a function of the media size, orientation, media type, and
 * any specified finishings.
 * <code>PrintService</code> provides the method to query the supported
 * values of an attribute in a suitable context :
 * See  {@link javax.print.PrintService#getSupportedAttributeValues(Class,DocFlavor, AttributeSet) <code>PrintService.getSupportedAttributeValues()</code>}
 * <p>
 * The rectangular printable area is defined thus:
 * The (x,y) origin is positioned at the top-left of the paper in portrait
 * mode regardless of the orientation specified in the requesting context.
 * For example a printable area for A4 paper in portrait or landscape
 * orientation will have height > width.
 * <p>
 * A printable area attribute's values are stored
 * internally as integers in units of micrometers (&#181;m), where 1 micrometer
 * = 10<SUP>-6</SUP> meter = 1/1000 millimeter = 1/25400 inch. This permits
 * dimensions to be represented exactly to a precision of 1/1000 mm (= 1
 * &#181;m) or 1/100 inch (= 254 &#181;m). If fractional inches are expressed in

 * negative powers of two, this permits dimensions to be represented exactly to
 * a precision of 1/8 inch (= 3175 &#181;m) but not 1/16 inch (because 1/16 inch

 * does not equal an integral number of &#181;m).
 * <p>
 * <B>IPP Compatibility:</B> MediaPrintableArea is not an IPP attribute.
 */

public final class FOPrintPageSetup
      implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {

    private float x, y, xscale, yscale;
    private int units;

    /**是否在打印纸的height上使用缩放*/
    public boolean isSelectedHeightCheckBox = false;
    /**打印纸的height增加的值*/
    public float heightAddABS = 0.0f;

    private static final long serialVersionUID = -1597171464050795793L;

    /**
     * Value to indicate units of inches (in). It is actually the conversion
     * factor by which to multiply inches to yield &#181;m (25400).
     */
    public static final int INCH = 25400;

    /**
     * Value to indicate units of millimeters (mm). It is actually the
     * conversion factor by which to multiply mm to yield &#181;m (1000).
     */
    public static final int MM = 1000;

    public FOPrintPageSetup(float x, float y, float xscale, float yscale, int units ,boolean ch , float h)
    {
//        if ((x < 0.0) || (y < 0.0) || (xscale <= 0.0) || (yscale <= 0.0) ||
//            (units < 1)) {
//            throw new IllegalArgumentException("0 or negative value argument");
//        }

//        this.x =  x * units + 0.5f;
//        this.y =  y * units + 0.5f;
//        this.xscale = xscale * units + 0.5f;
//        this.yscale = yscale * units + 0.5f;
        this.x = x * units;
        this.y = y * units;
        this.xscale = xscale * units;
        this.yscale = yscale * units;
        isSelectedHeightCheckBox = ch;
        heightAddABS = h;
    }

    /**
      * Constructs a MediaPrintableArea object from floating point values.
      * @param x      printable x
      * @param y      printable y
      * @param w      printable width
      * @param h      printable height
      * @param units  in which the values are expressed.
      *
      * @exception  IllegalArgumentException
      *     Thrown if <CODE>x</CODE> < 0 or <CODE>y</CODE> < 0
      *     or <CODE>w</CODE> <= 0 or <CODE>h</CODE> <= 0 or
      *     <CODE>units</CODE> < 1.
      */
    public FOPrintPageSetup(float x, float y, float xscale, float yscale, int units) {
//        if ((x < 0.0) || (y < 0.0) || (xscale <= 0.0) || (yscale <= 0.0) ||
//            (units < 1)) {
//            throw new IllegalArgumentException("0 or negative value argument");
//        }

//        this.x =  x * units + 0.5f;
//        this.y =  y * units + 0.5f;
//        this.xscale = xscale * units + 0.5f;
//        this.yscale = yscale * units + 0.5f;
        this.x = x * units;
        this.y = y * units;
        this.xscale = xscale * units;
        this.yscale = yscale * units;
    }

    /**
      * Constructs a MediaPrintableArea object from integer values.
      * @param x      printable x
      * @param y      printable y
      * @param w      printable width
      * @param h      printable height
      * @param units  in which the values are expressed.
      *
      * @exception  IllegalArgumentException
      *     Thrown if <CODE>x</CODE> < 0 or <CODE>y</CODE> < 0
      *     or <CODE>w</CODE> <= 0 or <CODE>h</CODE> <= 0 or
      *     <CODE>units</CODE> < 1.
      */
    public FOPrintPageSetup(int x, int y, int xscale, int yscale, int units) {
//        if ((x < 0) || (y < 0) || (xscale <= 0) || (yscale <= 0) ||
//            (units < 1)) {
//            throw new IllegalArgumentException("0 or negative value argument");
//        }
        this.x = x * units;
        this.y = y * units;
        this.xscale = xscale * units;
        this.yscale = yscale * units;

    }

    /**
     * Get the printable area as an array of 4 values in the order
     * x, y, w, h. The values returned are in the given units.
     * @param  units
     *     Unit conversion factor, e.g. {@link #INCH <CODE>INCH</CODE>} or
     *     {@link #MM <CODE>MM</CODE>}.
     *
     * @return printable area as array of x, y, w, h in the specified units.
     *
     * @exception  IllegalArgumentException
     *     (unchecked exception) Thrown if <CODE>units</CODE> < 1.
     */
    public float[] getPrintableArea(int units) {
        return new float[] { getX(units), getY(units),
                             getXscale(units), getYscale(units) };
    }

    /**
     * Get the x location of the origin of the printable area in the
     * specified units.
     * @param  units
     *     Unit conversion factor, e.g. {@link #INCH <CODE>INCH</CODE>} or
     *     {@link #MM <CODE>MM</CODE>}.
     *
     * @return  x location of the origin of the printable area in the
     * specified units.
     *
     * @exception  IllegalArgumentException
     *     (unchecked exception) Thrown if <CODE>units</CODE> < 1.
     */
     public float getX(int units) {
        return convertFromMicrometers(x, units);
     }

    /**
     * Get the y location of the origin of the printable area in the
     * specified units.
     * @param  units
     *     Unit conversion factor, e.g. {@link #INCH <CODE>INCH</CODE>} or
     *     {@link #MM <CODE>MM</CODE>}.
     *
     * @return  y location of the origin of the printable area in the
     * specified units.
     *
     * @exception  IllegalArgumentException
     *     (unchecked exception) Thrown if <CODE>units</CODE> < 1.
     */
     public float getY(int units) {
        return convertFromMicrometers(y, units);
     }

    /**
     * Get the width of the printable area in the specified units.
     * @param  units
     *     Unit conversion factor, e.g. {@link #INCH <CODE>INCH</CODE>} or
     *     {@link #MM <CODE>MM</CODE>}.
     *
     * @return  width of the printable area in the specified units.
     *
     * @exception  IllegalArgumentException
     *     (unchecked exception) Thrown if <CODE>units</CODE> < 1.
     */
     public float getXscale(int units) {
        return convertFromMicrometers(xscale, units);
     }

    /**
     * Get the height of the printable area in the specified units.
     * @param  units
     *     Unit conversion factor, e.g. {@link #INCH <CODE>INCH</CODE>} or
     *     {@link #MM <CODE>MM</CODE>}.
     *
     * @return  height of the printable area in the specified units.
     *
     * @exception  IllegalArgumentException
     *     (unchecked exception) Thrown if <CODE>units</CODE> < 1.
     */
     public float getYscale(int units) {
        return convertFromMicrometers(yscale, units);
     }

     public float getheightAddABS()
     {
         return heightAddABS;
     }

     public boolean isSelectedHeightCheckBox()
     {
         return isSelectedHeightCheckBox;
     }


    /**
     * Returns whether this media margins attribute is equivalent to the passed
     * in object.
     * To be equivalent, all of the following conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class MediaPrintableArea.
     * <LI>
     * The origin and dimensions are the same.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this media margins
     *          attribute, false otherwise.
     */
    public boolean equals(Object object) {
        boolean ret = false;
        if (object instanceof FOPrintPageSetup) {
           FOPrintPageSetup mm = (FOPrintPageSetup)object;
           if (x == mm.x &&  y == mm.y && xscale == mm.xscale && yscale == mm.xscale) {
               ret = true;
           }
        }
        return ret;
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class MediaPrintableArea, the category is
     * class MediaPrintableArea itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link java.lang.Class java.lang.Class}.
     */
    public final Class getCategory() {
        return FOPrintPageSetup.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class MediaPrintableArea,
     * the category name is <CODE>"media-printable-area"</CODE>.
     * <p>This is not an IPP V1.1 attribute.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "media-printable-area";
    }

    /**
     * Returns a string version of this rectangular size attribute in the
     * given units.
     *
     * @param  units
     *     Unit conversion factor, e.g. {@link #INCH <CODE>INCH</CODE>} or
     *     {@link #MM <CODE>MM</CODE>}.
     * @param  unitsName
     *     Units name string, e.g. <CODE>"in"</CODE> or <CODE>"mm"</CODE>. If
     *     null, no units name is appended to the result.
     *
     * @return  String version of this two-dimensional size attribute.
     *
     * @exception  IllegalArgumentException
     *     (unchecked exception) Thrown if <CODE>units</CODE> < 1.
     */
    public String toString(int units, String unitsName) {
        if (unitsName == null) {
            unitsName = "";
        }
        float []vals = getPrintableArea(units);
        String str = "("+vals[0]+","+vals[1]+")->("+vals[2]+","+vals[3]+")";
        return str + unitsName;
    }

    /**
     * Returns a string version of this rectangular size attribute in mm.
     */
    public String toString() {
        return(toString(MM, "mm"));
    }

    /**
     * Returns a hash code value for this attribute.
     */
    public int hashCode() {
        return (int)(x + 37*y + 43*xscale + 47*yscale);
    }

    private static float convertFromMicrometers(float x, int units) {
        if (units < 1) {
            throw new IllegalArgumentException("units is < 1");
        }
        return ((float)x) / ((float)units);
    }
}
