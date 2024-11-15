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
 * @WiseSpinner.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.tag.components.graphic;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 类功能描述：值改变后会触发Action事件的Spinner
 * 
 * 作者：zhangqiang 创建日期：2008-12-25
 */
public class WiseSpinner extends JSpinner
{
	private boolean shouldfireAction = true;

	/**
	 * 初始化过程的描述
	 * 
	 * @param 初始化参数说明
	 * 
	 * @exception 说明在某情况下,将发生什么异常}
	 */
	public WiseSpinner()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 初始化过程的描述
	 * 
	 * @param 初始化参数说明
	 * 
	 * @exception 说明在某情况下,将发生什么异常}
	 */
	public WiseSpinner(SpinnerModel model)
	{
		super(model);
		// TODO Auto-generated constructor stub
	}

	public synchronized void addActionListener(ActionListener l)
	{
		listenerList.add(ActionListener.class, l);
	}

	public synchronized void removeActionListener(ActionListener l)
	{
		listenerList.remove(ActionListener.class, l);
	}

	/**
	 * Sends a <code>ChangeEvent</code>, whose source is this
	 * <code>JSpinner</code>, to each <code>ChangeListener</code>. When a
	 * <code>ChangeListener</code> has been added to the spinner, this method
	 * method is called each time a <code>ChangeEvent</code> is received from
	 * the model.
	 * 
	 * @see #addChangeListener
	 * @see #removeChangeListener
	 * @see EventListenerList
	 */
	protected void fireStateChanged()
	{
		ActionEvent e = null;
		if (shouldfireAction)
		{
			int modifiers = 0;
			AWTEvent currentEvent = EventQueue.getCurrentEvent();
			if (currentEvent instanceof InputEvent)
			{
				modifiers = ((InputEvent) currentEvent).getModifiers();
			} else if (currentEvent instanceof ActionEvent)
			{
				modifiers = ((ActionEvent) currentEvent).getModifiers();
			}
			e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "value",
					EventQueue.getMostRecentEventTime(), modifiers);
		}
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ChangeListener.class)
			{
				ChangeEvent changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
			if (shouldfireAction && listeners[i] == ActionListener.class)
			{
				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}

	public void initValue(Object value)
	{
		shouldfireAction = false;
		setValue(value);
		shouldfireAction = true;
	}
}
