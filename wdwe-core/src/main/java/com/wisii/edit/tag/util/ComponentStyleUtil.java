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
 * @ComponentStyleUtil.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import com.wisii.edit.tag.util.LocationUtil.IdInfo;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.TextArea;

/**
 * 类功能描述：控制控件的基本样式。
 * 
 * 作者：李晓光
 * 创建日期：2009-9-4
 */
public class ComponentStyleUtil {
	private final static Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
	static{
		getHints();
	}
	
	/* 2009-9-14 15:05:11 */
	public static enum ComponentType{
		/* 单体控件 */
		Single,
		/* 复合控件 */
		Composite,
		/* 不创建任何空间 */
		Normal
	}
	/**
	 * 提供当前编辑控件系统采用的抗锯齿处理。
	 * @return	返回抗锯齿属性集
	 */
	public final static Map<RenderingHints.Key, Object> getRenderingHints(){
		
		return map;
	}
	private final static Map<RenderingHints.Key, Object> getHints(){
		/*Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();*/

		/*map.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);*/
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		return map;
	}
	/**
	 * 获得指定Area的样式
	 * @param inline	指定Area
	 * @return	{@link MutableAttributeSet}	返回指定Area的相关样式。
	 */
	public final static MutableAttributeSet getFontStyles(InlineArea inline){
		MutableAttributeSet set = new SimpleAttributeSet();
		
		inline = LocationUtil.findAreaDown(inline);
		if(!(inline instanceof TextArea))
			return set;
		
		Color color = inline.getForeground();
		color = (color == null) ? Color.BLACK : color;
		StyleConstants.setForeground(set, color);
		
		color = inline.getBackground();
		color = (color == null) ? Color.BLACK : color;
		StyleConstants.setBackground(set, color);
		
		Font font = inline.getAreaFont();
		StyleConstants.setFontFamily(set, font.getFamily());
		StyleConstants.setFontSize(set, font.getSize() / 1000);
		Integer leftIndent = inline.getSpaceStart();
		StyleConstants.setLeftIndent(set, leftIndent);
		Integer rightIndent = inline.getSpaceEnd();
		StyleConstants.setRightIndent(set, rightIndent);
		
		return set;
	}
	public final static void openAAText(){
		System.setProperty("swing.aatext", "true");
	}
	public final static Font getDefaultFont(){
		Object obj  = Toolkit.getDefaultToolkit().getDesktopProperty("win.defaultGUI.font");
		if(obj instanceof Font)
			return (Font)obj;
		
		return new Font("宋体", Font.PLAIN, 12);
	}
	public final static String getDefaultFamily(){
		Font font = getDefaultFont();
		
		return font.getFamily();
	}
	public final static void updateFont(JTextComponent comp, String s){
		if(s == null || comp == null || "".equalsIgnoreCase(s.trim()))
			return;
		Font font = comp.getFont();
		if(font.canDisplayUpTo(s) == -1){
			return;
		}
		String family = getDefaultFamily();
		font = new Font(family, font.getStyle(), font.getSize()); 
		comp.setFont(font);
	}
	public final static void updateFont(JTextComponent comp){
		updateFont(comp, comp.getText());
	}
	/* 2009-9-14 14:59:48 */
	public final static ComponentType getComponentType(IdInfo info,FOUserAgent userAgent){
		Collection<String> ids = info.getEditID();
		if(!hasPermissions(ids,userAgent))
			return ComponentType.Normal;
		int size = ids.size();
		ComponentType type = ComponentType.Normal;
		if(size == 1 && size == info.getInlineCount())
			type = ComponentType.Single;
		else
			type = ComponentType.Composite;
		
		return type;
	}
	 /* 检测所有的具有编辑id的inline，当前用户是否有操作权限，目的是：确定是否产生编辑控件。 */
    public final static boolean hasPermissions(Collection<String> ids,FOUserAgent userAgent){
    	Boolean flag = Boolean.FALSE;
    	for (String id : ids) {
    		if(WdemsTagUtil.hasAuthority(id,userAgent))
    			return Boolean.TRUE;	
    	}
    	return flag;
    }
    
    
}
