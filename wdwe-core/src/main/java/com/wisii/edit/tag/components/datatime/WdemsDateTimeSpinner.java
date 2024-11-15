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
 * @WdemsDateTimeSpinner.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.datatime;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.message.StatusbarMessageHelper.LEVEL;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
/**
 * 类功能描述：以Spinner的方式编辑日期、时间。
 * 1、指定日期时间的格式化样式【Java文档中定义了规则】
 * 2、根据制定的日期时间格式化样式，格式化录入的日期、时间样式。
 * 3、返回格式化好的日期事件字符串、Date信息。
 * 
 * 作者：李晓光
 * 创建日期：2009-6-19
 */
@SuppressWarnings("serial")
public class WdemsDateTimeSpinner extends JSpinner implements WdemsTagComponent {
	private SimpleDateFormat format = null;
	private DateFormatter formatter = null;
	private JFormattedTextField text = null;
	private String dateFormat = "";
	private final String DEFAULT_FORMAT = new SimpleDateFormat().toPattern();
	private Object result=null;
	public WdemsDateTimeSpinner(){
		this("");
	}
	public WdemsDateTimeSpinner(String pattern){
		this(pattern, new SpinnerDateModel());
	}
	public WdemsDateTimeSpinner(String pattern, SpinnerDateModel model){
		super(model);
		
		/*setBorder(BorderFactory.createLineBorder(Color.GRAY));*/
		setBorder(null);
		format = getFormat();
		formatter = getFormatter();
		text = getTextField();
		text.setBackground(new Color(253, 238, 238));
		text.setBorder(null);
		setPattern(pattern);

		addChangeListener(new ListenerImp());
		/*text.addActionListener(new ListenerImp());*/
	}
	public String getPattern(){
		return format.toPattern();
	}
	public void setPattern(String pattern){
		/*if(pattern == null || "".equals(pattern))
			throw new NullPointerException(this.getClass().getSimpleName() + ": 格式化样式不能为空。");*/
		if(pattern == null || "".equalsIgnoreCase(pattern)) {
			pattern = DEFAULT_FORMAT;
		}
		format.applyPattern(pattern);
		text.setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		if(dateFormat == null || "".equalsIgnoreCase(dateFormat)) {
			this.dateFormat = getPattern();
		}
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public SimpleDateFormat getFormat(){
		DateEditor editor = (DateEditor)getEditor();
		return editor.getFormat();
	}
	public DateFormatter getFormatter(){
		DateEditor editor = (DateEditor)getEditor();
		return (DateFormatter)editor.getTextField().getFormatter();
	}
	public JFormattedTextField getTextField(){
		DateEditor editor = (DateEditor)getEditor();
		
		return editor.getTextField();
	}
	public boolean isLenient(){
		return format.isLenient();
	}
	public boolean isAllowsInvalid(){
		return formatter.getAllowsInvalid();
	}
	/**
	 * 获得当前控件是否采用复写模式进行编辑。
	 * @return	{@link Boolean}		采用复写模式：True，否则：False。
	 */
	public boolean isOverwriteMode(){
		return formatter.getOverwriteMode();
	}
	public void setLenient(boolean lenient){
		format.setLenient(lenient);
		text.setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	public void setAllowsInvalid(boolean allowsInvalid){
		formatter.setAllowsInvalid(allowsInvalid);
		text.setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	/**
	 * 设置当前控件的是否采用复写模式编辑。
	 * @param overwriteMode	指定是否采用复写模式。
	 */
	public void setOverwriteMode(boolean overwriteMode){
		formatter.setOverwriteMode(overwriteMode);
		text.setFormatterFactory(new DefaultFormatterFactory(formatter));
	}
	/* --------------------WdemsTagComponent接口实现------------------------ */
	public void addActions(Actions action) {
		listenerList.add(ActionListener.class, action);
	}
	public void removeActionListener(ActionListener action) {
		listenerList.remove(ActionListener.class, action);
	}
	public JComponent getComponent() {
		return this;
	}
	public void iniValue(Object value) {
		if(value instanceof String){
			value = createDate(value.toString());
		}
		if(!(value instanceof Date)) {
			value = new Date();
		}
		setValue(value);
	}

	private Date createDate(String str){
		try {
			SimpleDateFormat format = new SimpleDateFormat(getDateFormat());
			return format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e){
			StatusbarMessageHelper.output("日期格式不合理", e.getMessage(), LEVEL.INFO);
		}
		return null;
	}
	public void showValidationState(ValidationMessage vAction) {
		StatusbarMessageHelper.output(vAction.getWrongMessage(), "", LEVEL.DEBUG);
	}
	
	/**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the <code>event</code> 
     * parameter.
     *
     * @param event  the <code>ActionEvent</code> object
     * @see EventListenerList
     */
    protected void fireActionPerformed() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = null;
        long mostRecentEventTime = EventQueue.getMostRecentEventTime();
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent)currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent)currentEvent).getModifiers();
        }
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                if (e == null) {
                      e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                          "",
                                          mostRecentEventTime,
                                          modifiers);
                }
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }          
        }
    }
    private class ListenerImp implements ChangeListener, ActionListener{
		public void stateChanged(ChangeEvent e) {
			fireActionPerformed();
		}

		public void actionPerformed(ActionEvent e) {
//			System.out.println("enter ");
			fireActionPerformed();
		}
	}   
	/* --------------------WdemsTagComponent接口实现------------------------ */
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
