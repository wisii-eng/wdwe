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
 * @WdemsDateTimeField.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.datatime;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.message.StatusbarMessageHelper.LEVEL;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.util.ComponentStyleUtil;
import com.wisii.edit.tag.util.LocationUtil;

/**
 * 类功能描述：用于录入日期时间的控件。
 * 1、指定日期时间的格式化样式【Java文档中定义了规则】
 * 2、根据制定的日期时间格式化样式，格式化录入的日期、时间样式。
 * 3、返回格式化好的日期事件字符串、Date信息。
 * 4、如果想要获得设置后的日期时间，可向该组件添加ActionListener、PropertyChangeListener监听
 * 5、设置完后回车、失去焦点自动触发相应的事件【通知4中的监听者】。
 * 作者：李晓光
 * 创建日期：2009-6-19
 */
@SuppressWarnings("serial")
public class WdemsDateTimeField extends JFormattedTextField implements WdemsTagComponent {
	private SimpleDateFormat format = null;
	private DateFormatter formatter = null;
	private final static String DEFAULT_PATTERN = "yyyy-MMM-dd-HH:mm:ss";
	private String dateFormat = "";
	private final String DEFAULT_FORMAT;
	private Object result=null;
	public WdemsDateTimeField(){
		this(DEFAULT_PATTERN);
	}
	public WdemsDateTimeField(String pattern){
		this(pattern, Locale.getDefault());
	}
	public WdemsDateTimeField(String pattern, Locale locale){
		super();
		setBackground(new Color(253, 238, 238));
		format = new SimpleDateFormat();
		DEFAULT_FORMAT = format.toPattern();
		format.setDateFormatSymbols(new DateFormatSymbols(locale));
		format.applyPattern(pattern);
		format.setLenient(false);
		formatter = new DateFormatter(format){
			@Override
			public Object stringToValue(String text) throws ParseException {
				Date date =  createDate(text, getPattern());
				return date;
			}
		};
		setFormatter(formatter);
		setFormatterFactory(new DefaultFormatterFactory(formatter));
		setValue(new Date());
	}
	/**
	 * 设置日期时间编辑控件的现实样式。
	 * @param pattern	指定显示样式。
	 */
	public void setPattern(String pattern){
		if(pattern == null || "".equalsIgnoreCase(pattern)) {
			pattern = DEFAULT_FORMAT;
		}
		format.applyPattern(pattern);
		setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	/**
	 * 获得当前的显示样式。
	 * @return {@link String}	返回现实样式。
	 */
	public String getPattern(){
		return format.toPattern();
	}
	public String getDateFormat() {
		if(dateFormat == null || "".equalsIgnoreCase(dateFormat)) {
			this.dateFormat = getPattern();
		}
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * 获得当前控件是否采用复写模式进行编辑。
	 * @return	{@link Boolean}		采用复写模式：True，否则：False。
	 */
	public boolean isOverwriteMode(){
		return formatter.getOverwriteMode();
	}
	/**
	 * 设置当前控件的是否采用复写模式编辑。
	 * @param overwriteMode	指定是否采用复写模式。
	 */
	public void setOverwriteMode(boolean overwriteMode){
		formatter.setOverwriteMode(overwriteMode);
		setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	public void setAllowsInvalid(boolean allowsInvalid){
		formatter.setAllowsInvalid(allowsInvalid);
		setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	public boolean isAllowsInvalid(){
		return formatter.getAllowsInvalid();
	}
	public void setLenient(boolean lenient){
		format.setLenient(lenient);
		setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	public boolean isLenient(){
		return format.isLenient();
	}
	public Object getItem(){
		Date value = getValue();
		if(value == null)
			return "";
		
		return format.format(value);
	}
	/**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created.
     * The listener list is processed in last to
     * first order.
     * @see EventListenerList
     */
    @Override
	protected void fireActionPerformed() {
    	try {
			commitEdit();
			setValue(getValue());
		} catch (ParseException e) {
			
		}
		super.fireActionPerformed();
    }
	/* --------------------WdemsTagComponent接口实现------------------------ */
	
	@Override
	public Date getValue(){
		Object value = super.getValue();
		if(value instanceof Date)
			return (Date)value;
		return null;
	}
	
	public JComponent getComponent() {
		return this;
	}
	public void iniValue(Object value) {
		/* 初始化当前控件，但要求不触发事件 */
		if(value instanceof String){
			value = createDate(value + "", getDateFormat());
		}
		if(!(value instanceof Date)) {
			value = new Date();
		}
		setValue(value);
	}
	private Date createDate(String str, String pttern){
		try {
			SimpleDateFormat format = new SimpleDateFormat(pttern);
			return format.parse(str);
		} catch (ParseException e) {
			return createDefault(str);
		}catch (IllegalArgumentException e){
			StatusbarMessageHelper.output("日期格式不合理", e.getMessage(), LEVEL.INFO);
		}
		return null;
	}
	private Date createDefault(String str){
		if(str == null || "".equalsIgnoreCase(str.trim()))
			return null;
		if(!LocationUtil.isNumbers(str))
			return getValue();
		if(str.trim().length() < 5)
			return getValue();
		SimpleDateFormat format = new SimpleDateFormat();
		 if(str.trim().length() > 8) {
			format.applyPattern("yyyyMMddHHmmss");
		} else if(str.trim().length() > 6) {
			format.applyPattern("yyyyMMd");
		} else if(str.trim().length() > 4) {
			format.applyPattern("yyMMd");
		}
		 try {
			 Date date = format.parse(str);
			return date;
		} catch (ParseException e) {
			StatusbarMessageHelper.output("解析日期", e.getMessage(), LEVEL.INFO);
		}
		return getValue();
		/*return null;*/
	}
	public void showValidationState(ValidationMessage vAction) {
		StatusbarMessageHelper.output("日期时间", vAction.getWrongMessage(), LEVEL.DEBUG);
		boolean b = vAction.getValidationState();
		if (b) {
			WdemsWarningManager.registerAccept(this);
		} else {
			WdemsWarningManager.registerWarning(this);
		}
	}
	
	public void addActions(Actions action) {
		addActionListener(action);
	}
	/* --------------------WdemsTagComponent接口实现------------------------ */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D)g; 
		graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());
		
		super.paintComponent(graphics);
	}
	public static void main(String[] args) {
		JFrame f = new JFrame("JFormattedTextField Sample");
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    Container content = f.getContentPane();
	    content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
	    final WdemsDateTimeField comp = new WdemsDateTimeField();
	   /* comp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				comp.iniValue(comp.getText());
			}
	    });*/
	    JTextField field = new JTextField(9);
//	    System.out.println("comp.getActionListeners().length = " + comp.getActionListeners().length);
	    /*comp.setOverwriteMode(true);
	    comp.setAllowsInvalid(false);*/
	    final JCheckBox box = new JCheckBox();
	    box.setSelected(true);
	    box.setIcon(new ImageIcon(SystemUtil.getImagesPath("warning.png")));
	   /* box.setRolloverIcon(new ImageIcon(SystemUtil.getImagesPath("warning.png")));
	    box.setRolloverSelectedIcon(new ImageIcon(SystemUtil.getImagesPath("gotop.gif")));*/
	    box.setSelectedIcon(new ImageIcon(SystemUtil.getImagesPath("accept.png")));
	    box.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
//				System.out.println(box.isSelected());
			}
	    });
	    content.add(comp);
	    content.add(field);
	    content.add(box);

	  
	    f.setSize(400, 300);
	    f.setVisible(true);
	}
	public Object getActionResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setActionResult(Object result) {
		this.result = result;
	}
	@Override
	public void setDefaultValue(String value) {
	}
	@Override
	public boolean canInitDefaultValue() {
		return false;
	}
	@Override
	public void initByDefaultValue() {
	}
}
