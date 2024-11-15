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
 *//**
 * @LocationUtil.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.util;

import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.wisii.fov.area.Area;
import com.wisii.fov.area.AreaTreeObject;
import com.wisii.fov.area.Block;
import com.wisii.fov.area.CTM;
import com.wisii.fov.area.NormalFlow;
import com.wisii.fov.area.Page;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.InlineBlockParent;
import com.wisii.fov.area.inline.InlineParent;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.flow.Inline;


/**
 * 类功能描述：用于处理控件的位置【Bound】信息，
 * 1、计算单位【mpt->pt】
 * 2、计算缩放【Area-Tree的DPI：72，而实际显示可能不等于72】
 * 3、计算位置，位置的坐标系，是相对于当前页，还是最外层的包含所有页控件的控件。
 * 
 * 作者：李晓光
 * 创建日期：2009-6-8
 */
public final class LocationUtil {
	public final static boolean isNumbers(final String s){
		if(s == null || "".equals(s))
			return Boolean.FALSE;
		
		return s.matches("\\d{1,}");
	}
	/**
	 * 根据制定的Area[Inline-Area、Text-Area]，和放缩比例，获得包含
	 * 指定Area的Viewport的矩形区域。
	 * @param area					指定Area[Inline-Level-Area]
	 * @param scaleX				指定水平放缩系数
	 * @param scaleY				指定竖直放缩系数
	 * @return {@link Rectangle2D} 	返回放缩后的矩形区域
	 */
	public final static Rectangle2D getScaleRectangle(final Area area, final double scaleX, final double scaleY){
		CTM ctm = new CTM();
		ctm = ctm.scale(scaleX, scaleY);
		return getViewport(area, ctm);
	}
	/**
	 * 获得inline的位置信息，目的是确定复选框的位置。
	 * @param area
	 * @param scaleX
	 * @param scaleY
	 * @return
	 */
	public final static Rectangle2D getScaleInlineRectangle(final Area area, final double scaleX, final double scaleY){
		CTM ctm = new CTM();
		ctm = ctm.scale(scaleX, scaleY);
		return getInlineViewport(area, ctm);
	}
	/**
	 * 根据制定的Area[Inline-Area、Text-Area]，和放缩比例，获得包含
	 * 指定Area的Viewport【Block-Viewport】的矩形区域。
	 * @param area
	 * @param scaleX
	 * @param scaleY
	 * @return
	 */
	public final static Rectangle2D getScaleBlockViewportRectangle(final Area area, final double scaleX, final double scaleY){
		CTM ctm = new CTM();
		ctm = ctm.scale(scaleX, scaleY);
		Rectangle2D rect = getBlockViewport(area, Boolean.TRUE);
		if(isNull(rect))
			return rect;
		return getViewport(rect, ctm);		
	}
	/**
	 * 
	 * 把指定的矩形区域根据指定水平、竖直的放缩比率，进行放缩处理。
	 * 【直接修改指定的矩形】
	 * @param source
	 *            指定要放缩的矩形区域
	 * @param scaleX
	 *            指定水平放缩系数
	 * @param scaleY
	 *            指定竖直放缩系数
	 * @return {@link Rectangle2D} 返回放缩后的矩形区域
	 */
	public static Rectangle2D getScaleRectangle(final Rectangle2D source, final double scaleX, final double scaleY) {
		if (isNull(source))
			return source;
		/*double x = source.getX();
		double y = source.getY();
		double width = source.getWidth();
		double height = source.getHeight();

		x *= scaleX;
		y *= scaleY;
		width *= scaleX;
		height *= scaleY;

		source.setRect(x, y, width, height);

		return source;*/
		CTM ctm = new CTM();
		ctm = ctm.scale(scaleX, scaleY);
		return getViewport(source, ctm);
	}	
	/**
	 * 根据制定的Area[Inline-Area、Text-Area]，指定的页
	 * @param area	指定Area[Inline-Level-Area]
	 * @param offsetX	指定x方向上的偏移量
	 * @param offsetY	指定y方向上的偏移量
	 * @return	{@link Rectangle2D}		返回变换后的矩形区域。
	 */
	public final static Rectangle2D getTranslateRectangle(Area area, int offsetX, int offsetY){
		CTM ctm = new CTM(offsetX, offsetY);
		return getViewport(area, ctm);
	}
	/**
	 * 根据制定的Area【Inline-Area】,获得其所占据的区域。
	 * @param area	制定Area
	 * @param offsetX	指定x方向的偏移量
	 * @param offsetY	指定y方向的偏移量
	 * @return	{@link Rectangle2D}		返回变换后的矩形区域。
	 */
	public final static Rectangle2D getTranslateInlineRectangle(Area area, int offsetX, int offsetY){
		CTM ctm = new CTM(offsetX, offsetY);
		return getInlineViewport(area, ctm);
	}
	/**
	 * 根据制定的Area[Inline-Level-Area]、指定的转换矩阵，获得转换后的Block-Viewport区域。
	 * @param area		指定Area[Inline-Level-Area]
	 * @param ctm		指定转换矩阵，如果为Null，则不做任何转换。
	 * @return	{@link Rectangle2D}		返回变换后的矩形区域。
	 */
	public final static Rectangle2D getViewport(Area area, CTM ctm){
		Rectangle2D rect = getBlockViewport(area, Boolean.FALSE);
		if(isNull(rect))
			return rect;
		return getViewport(rect, ctm);
	}
	/**
	 * 获得指定Inline-Area的矩形，并做相应的变换。
	 * @param area	指定Area
	 * @param ctm	指定转换规则。
	 * @return	{@link Rectangle2D}		返回变换后的矩形区域。
	 */
	public final static Rectangle2D getInlineViewport(Area area, CTM ctm){
		Rectangle2D rect = getInlineViewport(area);
		if(isNull(rect))
			return rect;
		return getViewport(rect, ctm);
	}
	/**
	 * 对指定的举行做指定仿射变换
	 * @param rect	指定具体的矩形
	 * @param ctm	指定仿射变换矩阵
	 * @return	{@link Rectangle2D}		返回变换后的矩形区域。
	 */
	public final static Rectangle2D getViewport(Rectangle2D rect, CTM ctm){
		if(isNull(ctm) || isNull(rect))
			return rect;
		return ctm.transform(rect);
	}
	/**
	 * 嵌套Area位置的计算。
	 * 
	 * @param area
	 *            指定当前的Area
	 * @param isNotReference
	 *            指定当前的Area是否是reference Area
	 * @return {@link Double} 返回计算后的偏移量
	 */
	public static double[] getOffsetForNestedArea(final AreaTreeObject area,
			Boolean isNotReference) {
		double offsetX = 0;
		double offsetY = 0;
		if (isNull(area) || (area instanceof Page))
			return new double[] { 0, 0 };
		
		if (((Area)area).isEqualViewport()) {
			if (isNotReference) {
				final Rectangle2D rect = ((Area)area).getViewport();
				offsetX += rect.getX();
				offsetY += rect.getY();
			} 
			if(((Area)area).isTableCell()){
				
				float left = ((Area)area).getBorderStart() / 1000F;
				float top = ((Area)area).getBorderBefore() / 1000F;
								
				offsetX += left;
				offsetY += top;
			}
			
			isNotReference = Boolean.FALSE;
		} else if (((Area)area).isReferrenceArea()) {
			isNotReference = Boolean.TRUE;
		}
		final double[] offset = getOffsetForNestedArea(area.getParentArea(), isNotReference);
		offsetX += offset[0];
		offsetY += offset[1];

		return new double[] { offsetX, offsetY };
	}
	/*public static enum AreaKind{
		Reference,
		Viewport,
		Cell,
		Normal
	}
	public static double[] getOffsetForNestedArea(final AreaTreeObject area, AreaKind kind){
		double offsetX = 0;
		double offsetY = 0;
		if (isNull(area) || (area instanceof Page))
			return new double[] { 0, 0 };
		BlockViewport block = null;
		
		
		return new double[] { offsetX, offsetY };
	}*/
	/**
	 * 判断指定的Area下是否包含了多个不同的ID，如果包含了多个不同的ID
	 * 则要采用复合控件进行编辑。
	 * @param block		指定Area
	 * @return	{@link Boolean}	如果包含多个不同ID：True，否则：False。
	 */
	public final static Boolean isComplexId(Block block){
		return getId(block, new HashSet<String>());
	}
	public final static Set<String> getAllEditID(Block block){
		Set<String> ids = new HashSet<String>();
		getIDCount(block, ids);
	
		return ids;
	}
	public final static <T> IdInfo getAllIDInfo(Block block){
		Set<String> ids = new HashSet<String>();
		int count = getIDCount(block, ids);
		IdInfo info = new IdInfo(ids, count);
		return info;
	}
	public final static IdInfo getAllIDInfo(FONode block){
		Set<String> ids = new HashSet<String>();
		int count = getInlineIDCount(block, ids);
		IdInfo info = new IdInfo(ids, count);
		return info;
	}
	@SuppressWarnings("unchecked")
	public final static TextArea findAreaDown(Area area){
		if(area == null)return null;
		if(area instanceof TextArea)
			return (TextArea)area;
		List list = area.getChildAreas();
		if(list == null || list.isEmpty())
			return null;
		area = (Area)list.get(0);
		return findAreaDown(area);
	}
	public final static Rectangle2D getPageViewportBound(Area area){
		PageViewport page = getPageViewport(area);
		if(page == null)return null;
		return page.getViewArea();
	}
	public final static PageViewport getPageViewport(AreaTreeObject area){
		if(area == null)
			return null;
		if(area instanceof PageViewport)
			return (PageViewport)area;
		return getPageViewport(area.getParentArea());
	}
	//----------------------------------私有方法区------------------------------------------
	@SuppressWarnings("unchecked")
	private final static Boolean getId(Area area, Set<String> ids){
		if(area == null)
			return Boolean.FALSE;
		if(area instanceof InlineParent || area instanceof InlineBlockParent){
			String id = area.getID();
			if(id != "") {
				ids.add(area.getID());
			}
			return (ids.size() > 1);
		}
		List<Area> areas = area.getChildAreas();
		/*if(areas == null || areas.isEmpty())
			return (ids.size() > 1);*/
		for (Area a : areas) {
			if(getId(a, ids)) {
				break;
			}
		}
		return (ids.size() > 1);
	}
	private final static int getIDCount(Area area, Set<String> ids){
		if(area == null)
			return 0;
		int count = 0;
		if(area instanceof InlineParent || area instanceof InlineBlockParent){
			String id = area.getID();
			if(id != "") {
				if(ids.contains(id))
					return count;
				ids.add(id);
			}
			return (count + 1);
		}
		List<Area> areas = area.getChildAreas();
		if(areas == null || areas.isEmpty())
			return count;
		for (Area a : areas) {
			count += getIDCount(a, ids);
		}
		return count;
	}
	private final static int getInlineIDCount(FONode fo, Set<String> ids){
		if(fo == null)
			return 0;
		int count = 0;
		if(fo instanceof Inline){
			String id = ((Inline)fo).getId();
			if(id != "") {
				if(ids.contains(id))
					return count;
				ids.add(id);
			}
			return (count + 1);
		}
		for (ListIterator<FONode> iterator = fo.getChildNodes();iterator!=null&& iterator.hasNext();) {
			count += getInlineIDCount(iterator.next(), ids);			
		}
		
		return count;
	}
	/**
	 * 获得最近的Block-Viewport大小
	 * ※相对于页的BufferedImage的左上顶点。
	 */
	private final static Rectangle2D getBlockViewport(Area area, boolean isReference){
		/*Block block = getNearViewport(area);
		if(block == null){
			StatusbarMessageHelper.output("编辑", "要编辑的数据没有被封装在Block-Container中", StatusbarMessageHelper.LEVEL_INFO);
		}
		Rectangle2D rect = (Rectangle2D)block.getViewport().clone();*/
		Rectangle2D  rect = null;//getNearBlockViewport(area);
		if(isReference)
			rect = getNearViewport(area);
		else
			rect = getNearBlockViewport(area);
		return rect;
	}
	/**
	 * 获得最近的Block-Viewport大小
	 * ※相对于页的BufferedImage的左上顶点。
	 */
	private final static Rectangle2D getInlineViewport(Area area){
		Rectangle2D result = null;
		if (!(area instanceof InlineArea))
			return result;
		result = area.getViewport();
		float left = area.getBorderAndPaddingWidthStart() / 1000F;
		float top = area.getBorderAndPaddingWidthBefore() / 1000F;
		// 现在的FOV对边框的处理是，如果边框的宽度小于1pt，则视边框宽度为1pt。
		left = (left < 1.0F) ? 1.0F : left;
		top = (top < 1.0F) ? 1.0F : top;
		
		double[] offset = getOffsetForNestedArea(area, false);
		double x = result.getX();
		x += offset[0];
		x += left;
		double y = result.getY();
		y += offset[1];
		y += top;
		
		double w = result.getWidth();
		double h = result.getHeight();
		
		result = new Rectangle2D.Double(x, y, w, h); 
		return result;
	}
	private final static Rectangle2D getNearBlockViewport(Area a){
		Area area = searchAllArea(a);
		Rectangle2D view = area.getViewport();
		
		double[] offset = getOffsetForNestedArea(area, false);
		double x = view.getX() + offset[0];
		double y = view.getY() + offset[1];
		double w = view.getWidth() - .5F;
		double h = view.getHeight() - .5F;
		view.setRect(x, y, w, h);

		return view;
	}
	/**
	 * 找到最近的Viewport
	 * @param a	指定Area【Text-Area、Inline-Level-Area】
	 * @return	{@link Block}	返回找到的Block-Area
	 */
	private final static Rectangle2D getNearViewport(Area a){
		AreaTreeObject area = searchAllReferenceArea(a);//searchAllArea(a/*, BlockViewport.class*/);
		Rectangle2D reference = null;
		Rectangle2D parent = null;
		
		if(!((Area)area).isReferrenceArea())
			return null;
		Area block = (Area)area;
		Area parentArea = block;
		reference = block.getViewport();
		if(!(area.getParentArea() instanceof Block)) {
			parent = reference;
		} else if(block.isTableCell()) {
			parent = reference;
		} else {
			parent = ((Block)block.getParentArea()).getViewport();
			parentArea = (Area)block.getParentArea();
		}
		if(reference == null)return null;
		double[] offset = getOffsetForNestedArea(block, false);
		
		double x = reference.getX() + offset[0];
		double y = reference.getY() + offset[1];
		double w = parentArea.getIPD() / 1000F /*+ 0.5*/;//删除调整这样会产生覆盖现象 by 李晓光
		double h = parentArea.getBPD() / 1000F/* + 1*/;//删除调整这样会产生覆盖现象 by 李晓光
		
//		double x = reference.getX() + offset[0];
//		double border = parentArea.getBorderAndPaddingWidthStart() / 1000F;
//		x += border;
//		double y = reference.getY() + offset[1];
//		border = parentArea.getBorderAndPaddingWidthBefore() / 1000F;
//		y += border;
//		double w = parent.getWidth() - 2 * border;
//		double h = parent.getHeight()- 2 * border;
		
		/*if(((Area)area).isTableCell())*/{
			x += parentArea.getBorderAndPaddingWidthStart() / 1000F + 0.5;
			y += parentArea.getBorderAndPaddingWidthBefore() / 1000F + 0.5;
			/*w -= parentArea.getBorderAndPaddingWidthStart() / 1000F;
			w -= parentArea.getBorderAndPaddingWidthEnd() / 1000F;
			h -= parentArea.getBorderAndPaddingWidthBefore() / 1000F;
			h -= parentArea.getBorderAndPaddingWidthAfter() / 1000F;*/
		}
		Rectangle2D result = new Rectangle2D.Double(x, y, w, h);
		
		return result;
	}
	/** 判断当前对象是否为空 */
	private final static boolean isNull(Object o){
		return (o == null);
	}
	/**
	 * 根据指定的Area，向上遍历AreaTree，获得指定类型、及子类型的对象。
	 * 
	 * @param area
	 *            指定Area
	 * @param type
	 *            指定类型
	 * @return {@link Area} 返回指定类型、子类型的对象。
	 */
	@SuppressWarnings("unchecked")
	private final static Area searchAllArea(Area area, final Class type) {
		if (isNull(area) || isNull(type))
			return area;
		if(area instanceof NormalFlow)
			return null;
		area = (Area)area.getParentArea();		
		if (isNull(area))
			return area;
		if (/*type.isAssignableFrom(area.getClass())*/ type == area.getClass())
			return area;
		/*if(area.isReferrenceArea())
			return area;*/
		return searchAllArea(area, type);
	}
	private final static Area searchAllReferenceArea(Area area) {
		if (isNull(area))
			return area;
		/*if(area instanceof NormalFlow)
			return null;*/
		area = (Area)area.getParentArea();		
		if (isNull(area))
			return area;
		/* 【删除】 2009-9-14 15:43:11 */
		if(area.isReferrenceArea())
			return area;
		return searchAllReferenceArea(area);
	}
	private final static Area searchAllArea(Area area) {
		if (isNull(area))
			return area;
		if(area instanceof Block)
			return area;
		/*if(area instanceof NormalFlow)
			return null;*/
		area = (Area)area.getParentArea();		
		if (isNull(area))
			return area;
		/* 【删除】 2009-9-14 15:43:11 */
		/*if(area.isReferrenceArea())
			return area;*/
		return searchAllArea(area);
	}
	
	public final static class IdInfo{
		private Set<String> editID = null;
		private int inlineCount = 0;
		private IdInfo(Set<String> editID, int inlineCound){
			this.editID = editID;
			this.inlineCount = inlineCound;
		}
		
		public Set<String> getEditID() {
			return editID;
		}
		public int getInlineCount() {
			return inlineCount;
		}
	}
	/**
	 * 先把系统dpi下的的尺寸转换为72PT【默认系统的dpi = 96】，再对其进行放缩处理。
	 * @param length	制定系统下dpi下的长度。
	 * @param scale		制定放缩系数。
	 * @return	{@link Double}	返回放缩后的长度值。
	 */
	public final static double getLengthToScale(double length, double scale){
		if(length < 0 || scale < 0)
			return 0;
		length = length * DEFAULT_DPI / SYSTEM_DPI * scale;
		
		return length;
	}
	private final static int DEFAULT_DPI = 72;
	private final static int SYSTEM_DPI;
	static{
		SYSTEM_DPI = Toolkit.getDefaultToolkit().getScreenResolution();
	}
}
