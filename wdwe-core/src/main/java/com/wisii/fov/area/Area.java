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
 */package com.wisii.fov.area;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisii.fov.fonts.FontTriplet;
import com.wisii.fov.traits.BorderProps;

// If the area appears more than once in the output or if the area has external data it is cached to keep track of it
// and to minimize rendered output renderers can render the output once and display it for every occurence this should
// also extend to all outputs (including PDFGraphics2D) and all types of renderers

/** Base object for all areas. */
public class Area extends AreaTreeObject implements Serializable
{
	// stacking directions
	/** Stacking left to right     */
	public static final int LR = 0;
	/** Stacking right to left     */
	public static final int RL = 1;
	/** Stacking top to bottom     */
	public static final int TB = 2;
	/** Stacking bottom to top     */
	public static final int BT = 3;

	// orientations for reference areas
	/** Normal orientation     */
	public static final int ORIENT_0 = 0;
	/** Rotated 90 degrees clockwise     */
	public static final int ORIENT_90 = 1;
	/** Rotate 180 degrees     */
	public static final int ORIENT_180 = 2;
   /** Rotated 270 degrees clockwise     */
	public static final int ORIENT_270 = 3;

	// area class values
	/** Normal class     */
	public static final int CLASS_NORMAL = 0;
	/** Fixed position class     */
	public static final int CLASS_FIXED = 1;
	/** Absolute position class     */
	public static final int CLASS_ABSOLUTE = 2;
	/** Before float class     */
	public static final int CLASS_BEFORE_FLOAT = 3;
	/** Footnote class     */
	public static final int CLASS_FOOTNOTE = 4;
	/** Side float class     */
	public static final int CLASS_SIDE_FLOAT = 5;

	// IMPORTANT: make sure this is the maximum + 1
	/** Maximum class count     */
	public static final int CLASS_MAX = CLASS_SIDE_FLOAT + 1;
	private int areaClass = CLASS_NORMAL;
	/** the area's inline-progression-dimension */
	protected int ipd;
	/** the area's block-progression-dimension */
	protected int bpd;

	/** Traits for this area stored in a HashMap     */
	protected Map props = null;

	/** logging instance     */
	protected static Log log = LogFactory.getLog(Area.class);

	/**
	 * Get the area class of this area.
	 * @return the area class
	 */
	public int getAreaClass()
	{
		return areaClass;
	}

	/**
	 * Set the area class of this area.
	 * @param areaClass the area class
	 */
	public void setAreaClass(int areaClass)
	{
		this.areaClass = areaClass;
	}

	/**
	 * Set the inline progression dimension of content rectangle for this area.
	 * @param i the new inline progression dimension
	 * @see <a href="http://www.w3.org/TR/xsl/slice4.html#area-common">ipd</a>
	 */
	public void setIPD(int i)
	{
		ipd = i;
	}

	/**
	 * Get the inline progression dimension of the content rectangle for this area.
	 * @return the inline progression dimension
	 * @see <a href="http://www.w3.org/TR/xsl/slice4.html#area-common">ipd</a>
	 */
	public int getIPD()
	{
		return ipd;
	}

	/**
	 * Set the block progression dimension of the content rectangle for this area.
	 * @param b the new block progression dimension
	 * @see <a href="http://www.w3.org/TR/xsl/slice4.html#area-common">bpd</a>
	 */
	public void setBPD(int b)
	{
		bpd = b;
	}

	/**
	 * Get the block progression dimension of the content rectangle for this area.
	 * @return the block progression dimension
	 * @see <a href="http://www.w3.org/TR/xsl/slice4.html#area-common">bpd</a>
	 */
	public int getBPD()
	{
		return bpd;
	}

	/**
	 * Get the allocation inline progression dimension of this area.
	 * This adds the content, borders and the padding to find the total allocated IPD.
	 * @return the total IPD allocation for this area
	 */
	public int getAllocIPD()
	{
		return getBorderAndPaddingWidthStart() + getIPD() + getBorderAndPaddingWidthEnd();
	}

	/**
	 * Get the allocation block progression dimension of this area.
	 * This adds the content, borders, padding and spaces to find the total allocated BPD.
	 * @return the total BPD allocation for this area
	 */
	public int getAllocBPD()
	{
		return getSpaceBefore() + getBorderAndPaddingWidthBefore() + getBPD() + getBorderAndPaddingWidthAfter() + getSpaceAfter();
	}

	/**
	 * Return the sum of region border- and padding-before
	 * @return width in millipoints
	 */
	public int getBorderAndPaddingWidthBefore()
	{
		int margin = 0;
		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_BEFORE);
		if (bps != null) {
			margin = bps.width;
		}

		Integer padWidth = (Integer)getTrait(Trait.PADDING_BEFORE);
		if (padWidth != null) {
			margin += padWidth.intValue();
		}

		return margin;
	}

	/**
	 * Return the sum of region border- and padding-after
	 * @return width in millipoints
	 */
	public int getBorderAndPaddingWidthAfter()
	{
		int margin = 0;

		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_AFTER);
		if (bps != null) {
			margin = bps.width;
		}

		Integer padWidth = (Integer)getTrait(Trait.PADDING_AFTER);
		if (padWidth != null) {
			margin += padWidth.intValue();
		}

		return margin;
	}

	/**
	 * Return the sum of region border- and padding-start
	 * @return width in millipoints
	 */
	public int getBorderAndPaddingWidthStart()
	{
		int margin = 0;
		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_START);
		if (bps != null) {
			margin = bps.width;
		}

		Integer padWidth = (Integer)getTrait(Trait.PADDING_START);
		if (padWidth != null) {
			margin += padWidth.intValue();
		}

		return margin;
	}
	/* 【添加：START】 by 李晓光  2009-8-20 */
	public int getPaddingStart(){
		int margin = 0;
		Integer padWidth = (Integer)getTrait(Trait.PADDING_START);
		if (padWidth != null) {
			margin += padWidth.intValue();
		}

		return margin;
	}
	public int getPaddingBefore(){
		int margin = 0;
		Integer padWidth = (Integer)getTrait(Trait.PADDING_BEFORE);
		if (padWidth != null) {
			margin += padWidth.intValue();
		}

		return margin;
	}
	/* 【添加：END】 by 李晓光  2009-8-20 */
	/**
	 * Return the sum of region border- and padding-end
	 * @return width in millipoints
	 */
	public int getBorderAndPaddingWidthEnd()
	{
		int margin = 0;
		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_END);
		if (bps != null) {
			margin = bps.width;
		}

		Integer padWidth = (Integer)getTrait(Trait.PADDING_END);
		if (padWidth != null) {
			margin += padWidth.intValue();
		}

		return margin;
	}

	/**
	 * Returns the space before
	 * @return width in millipoints
	 */
	public int getSpaceBefore()
	{
		int margin = 0;
		Integer space = (Integer)getTrait(Trait.SPACE_BEFORE);
		if (space != null) {
			margin = space.intValue();
		}
		return margin;
	}

	/**
	 * Returns the space after
	 * @return width in millipoints
	 */
	public int getSpaceAfter()
	{
		int margin = 0;
		Integer space = (Integer)getTrait(Trait.SPACE_AFTER);
		if (space != null) {
			margin = space.intValue();
		}
		return margin;
	}

	/**
	 * Returns the space start
	 * @return width in millipoints
	 */
	public int getSpaceStart()
	{
		int margin = 0;
		Integer space = (Integer)getTrait(Trait.SPACE_START);
		if (space != null) {
			margin = space.intValue();
		}
		return margin;
	}

	/**
	 * Returns the space end
	 * @return width in millipoints
	 */
	public int getSpaceEnd()
	{
		int margin = 0;
		Integer space = (Integer)getTrait(Trait.SPACE_END);
		if (space != null) {
			margin = space.intValue();
		}
		return margin;
	}

	/**
	 * Add a child to this area.
	 * The default is to do nothing. Subclasses must override to do something if they can have child areas.
	 * @param child the child area to add
	 */
	public void addChildArea(Area child)
	{
	}

	/**
	 * Add a trait property to this area.
	 * @param prop the Trait to add
	 */
	public void addTrait(Trait prop)
	{
		if (props == null) {
			props = new java.util.HashMap(20);
		}
		props.put(prop.getPropType(), prop.getData());
	}

	/**
	 * Add a trait to this area.
	 * @param traitCode the trait key
	 * @param prop the value of the trait
	 */
	public void addTrait(Object traitCode, Object prop)
	{
		if (props == null) {
			props = new java.util.HashMap(20);
		}
		props.put(traitCode, prop);
	}

	/**
	 * Get the map of all traits on this area.
	 * @return the map of traits
	 */
	public Map getTraits()
	{
		return this.props;
	}

	/** @return true if the area has traits */
	public boolean hasTraits()
	{
		return (this.props != null);
	}

	/**
	 * Get a trait from this area.
	 * @param oTraitCode the trait key
	 * @return the trait value
	 */
	public Object getTrait(Object oTraitCode)
	{
		return (props != null ? props.get(oTraitCode) : null);
	}

	/**
	 * Checks whether a certain trait is set on this area.
	 * @param oTraitCode the trait key
	 * @return true if the trait is set
	 */
	public boolean hasTrait(Object oTraitCode)
	{
		return (getTrait(oTraitCode) != null);
	}

	/**
	 * Get a boolean trait from this area.
	 * @param oTraitCode the trait key
	 * @return the trait value
	 */
	public boolean getBooleanTrait(Object oTraitCode)
	{
		final Object obj = getTrait(oTraitCode);
		if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue();
		else
			return false;
	}

	/**
	 * Get a trait from this area as an integer.
	 * @param oTraitCode the trait key
	 * @return the trait value
	 */
	public int getTraitAsInteger(Object oTraitCode)
	{
		final Object obj = getTrait(oTraitCode);
		if (obj instanceof Integer)
			return ((Integer)obj).intValue();
		else
			throw new IllegalArgumentException("Trait " + oTraitCode.getClass().getName() + " 不能转化成整数类型");
	}
	/* 【添加：START】 by 李晓光 2009-6-8 */
	public Integer getBorderStart(){
		int margin = 0;
		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_START);
		if (bps != null) {
			margin = bps.width;
		}
		return margin;
	}
	public Integer getBorderEnd(){
		int margin = 0;
		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_END);
		if (bps != null) {
			margin = bps.width;
		}
		return margin;
	}
	public Integer getBorderBefore(){
		int margin = 0;
		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_BEFORE);
		if (bps != null) {
			margin = bps.width;
		}
		return margin;
	}
	public Integer getBorderAfter(){
		int margin = 0;
		BorderProps bps = (BorderProps)getTrait(Trait.BORDER_AFTER);
		if (bps != null) {
			margin = bps.width;
		}
		return margin;
	}
	public Rectangle2D getViewport(){
		if(this.viewport == null)return null;
		
		return (Rectangle2D)this.viewport.clone();
	}
	private Rectangle2D viewport = null;
    public void setViewport(Rectangle2D viewport){
    	this.viewport = viewport;
    }
    public void clearViewport(){
    	this.viewport = null;
    }
	public boolean isTableCell(){
		return getBoolean(Trait.TABLE_CELL);
	}
	public boolean isReferrenceArea(){
		return getBoolean(Trait.IS_REFERENCE_AREA);
	}
	public boolean isViewportArea(){
		return getBoolean(Trait.IS_VIEWPORT_AREA);
	}
	public boolean isEqualViewport(){
		return isViewportArea() || isTableCell();
	}
	private Boolean getBoolean(Integer key){
		final Object obj = getTrait(key);
		if(obj instanceof Boolean)
			return (Boolean)obj;
		return Boolean.FALSE;
	}
	public Color getForeground(){
		final Object obj = getTrait(Trait.COLOR);
		if(obj instanceof Color)
			return (Color)obj;
		return null;
	}
	public Color getBackground(){
		final Object obj = getTrait(Trait.BACKGROUND);
		if(obj instanceof Color)
			return (Color)obj;
		return null;
	}
	public FontTriplet getFontTriplet(){
		final Object obj = getTrait(Trait.FONT);
		if(obj instanceof FontTriplet)
			return (FontTriplet)obj;
		return null;
	}
	public Integer getFontSize(){
		final Object obj = getTrait(Trait.FONT_SIZE);
		if(obj instanceof Integer)
			return (Integer)obj;
		return null;
	}
	public Font	getAreaFont(){
		FontTriplet trip = getFontTriplet();
		Integer size = getFontSize();
		String name = trip.getName();
		String style = trip.getStyle();
		return new Font(name, Font.PLAIN, size);
	}	
	/**
	 * 获得当前Area的Id属性值。
	 * ※ID属性的来源是在FO-Element中设置
	 */
	public String getID(){
		String id = (String)getTrait(Trait.PROD_ID);
		return (id == null) ? "" : id;
	}
	
	/* 把AreaTree建立为可双向遍历的Tree */
	/*public List getChildAreas() {
		return Collections.EMPTY_LIST;
	}
	protected Area parentArea = null;
	public Area getParentArea() {
		return parentArea;
	}

	public void setParentArea(final Area parent) {
		this.parentArea = parent;
	}*/
	/* 【添加：END】 by 李晓光 2009-6-8 */
	/** @see java.lang.Object#toString() */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" {ipd=").append(Integer.toString(getIPD()));
		sb.append(", bpd=").append(Integer.toString(getBPD()));
		sb.append("}");
		sb.append(" {aipd=").append(Integer.toString(getAllocIPD()));
		sb.append(" {abpd=").append(Integer.toString(getAllocBPD()));
		return sb.toString();
	}
}

