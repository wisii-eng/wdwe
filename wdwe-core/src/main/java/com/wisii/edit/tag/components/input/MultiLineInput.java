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
 */package com.wisii.edit.tag.components.input;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.components.floating.WdemsToolManager;
import com.wisii.edit.tag.components.select.datasource.DataSource;
import com.wisii.edit.tag.schema.wdems.Input;
import com.wisii.edit.tag.util.WdemsTagUtil;

/**
 * 输入控件
 * @author 闫舒寰
 * @version 1.0 2009/07/10
 */
public class MultiLineInput implements WdemsTagComponent,Dictionaryable {
	
	private Actions action;
	private Object result=null;
	//实际的多行控件
	JTextArea jta;
	//把多行控件加上滚动条
	JScrollPane jsp;
	
	final Input input;
	private String defaultvalue;
	private boolean isedit = false;
	public MultiLineInput(final Input input){
		this.input = input;
		
		if (jta == null) {
			jta = new WdemsTextArea(this, this);
			jta.setDocument(new WdemsPlainDocument(jta));
			jsp = new JScrollPane(jta);
			jsp.setBorder(BorderFactory.createEmptyBorder());
		}
		
		if (input.getHint() != null) {
//			jta.setToolTipText(input.getHint());
			
			//为显示提示添加快捷键，目前是alt+enter组成的快捷键
			Action act = new AbstractAction() {
				public void actionPerformed(final ActionEvent e) {
					setValue(input.getHint());
//					setText(input.getHint());
				}
			};
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_MASK);
			ActionMap am = jta.getActionMap();
			am.put("hint", act);
			jta.getInputMap().put(ks, "hint");
//			jta.setInputMap(JComponent.WHEN_FOCUSED, jta.getInputMap());
		}
		jta.setBorder(BorderFactory.createLineBorder(Color.black));
		
		if (input.isWrap()) {
			jta.setLineWrap(true);
		} else {
			jta.setLineWrap(false);
		}
		
		jta.setWrapStyleWord(true);
		
		jta.setDragEnabled(true);
		
		jta.setBorder(BorderFactory.createEmptyBorder());
		jta.setBackground(new Color(253,238,238));
		
		WdemsActioinHandler.bindActions(jta, BindType.MultiLineInput);
	}

	public void addActions(final Actions action) {
		
		this.action = action;
		
		jta.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
//				System.out.println("hihi");
				action.doOnResultValidation();
			}
		});
		
		jta.addFocusListener(new FocusListener() {
			
			Object lostValue;
			Object gainedValue;
			
			/**
			 * 这里的问题就是当在插入特殊字符的时候需要失去一次焦点，然后设置好了之后再得到焦点
			 * 
			 */
			
			public void focusLost(final FocusEvent e) {
				
//				WdemsToolManager.Instance.setFocusComponent(null);
				this.lostValue = action.getValue();
				//如果值有改变的时候则发送实际的事件
				if (!this.lostValue.equals(this.gainedValue)) {
					//根据失焦点时和得焦点时数据是否一致来判断是否需要进行失焦点验证
					action.doOnBlurValidation();
					this.lostValue = this.gainedValue;
				}
			}
			
			public void focusGained(final FocusEvent e) {
				WdemsToolManager.Instance.setFocusComponent((Component) e.getSource());
				//获得焦点时记录当前值
//				System.err.println("current:" + action.getValue() + " orign:" + this.gainedValue);
				
				if (this.lostValue == null) {
					//第一次进入的时候
					this.gainedValue = action.getValue();
					return;
				}
				if (this.lostValue.equals(action.getValue())) {
					this.gainedValue = action.getValue();
				}
			}
		});
		
		String onEdit = input.getOnEdit();
		
		if (onEdit != null) {
			jta.getDocument().addDocumentListener(new DocumentListener() {
				
				public void removeUpdate(final DocumentEvent e) {
					System.out.println("update");
					action.doOnEditValidation();
				}
				
				public void insertUpdate(final DocumentEvent e) {
					System.out.println("insert");
					action.doOnEditValidation();
				}
				
				public void changedUpdate(final DocumentEvent e) {
					System.out.println("change");
					action.doOnEditValidation();
				}
			});
		}
		
		final String filter = input.getFilter();
		
		if (filter != null) {
			//过滤特定字符
			final Pattern p = Pattern.compile(filter);
			
			jta.addKeyListener(new KeyListener() {
				
				public void keyTyped(final KeyEvent e) {
					char ch = e.getKeyChar();
					
					if (WdemsTagUtil.hasSpecialChar(ch))
						return;
					
					Matcher m = p.matcher(new StringBuilder().append(ch).toString());
					if (m.matches()) {
						e.consume();
						
//						jta.getDocument().
						
						StatusbarMessageHelper.output("输入字符\"" + e.getKeyChar() + "\"被过滤，" + input.getFilterMsg(), "被过滤字符样式为：" + filter, StatusbarMessageHelper.LEVEL.INFO);
					}
					else
					{
						edit();
					}
				}
				
				public void keyReleased(final KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				public void keyPressed(final KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
	}

	public void setValue(final Object value) {
		if (value == null) {
			//TODO 应该报个什么错
		} else {
			jta.setText(value.toString());
			//这里必须要再发送一个消息，激发该对象所对应的事件
			this.action.doOnResultValidation();
			edit();
//			jta.dis
		}
	}

	public JComponent getComponent() {
		return jsp;
	}

	public Object getValue() {
		return jta.getText();
	}

	public void iniValue(final Object value) {
		if (value == null||value.toString().isEmpty()){
			if(canInitDefaultValue())
			{
				jta.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						edit();
					}
				});
				jsp.setBorder(BorderFactory.createLineBorder(Color.yellow));
			}
			return;
		}
		jta.setText(value.toString());
	}

	public void showValidationState(final ValidationMessage vAction) {
		
		Boolean b = vAction.getValidationState();
		
		if (b != null) {
			if (b) {
				WdemsWarningManager.registerAccept(jta);
			} else {
				WdemsWarningManager.registerWarning(jta);
			}
		}
	}

	public void setLocation(final Point p) {
		jta.setLocation(p);
	}

	public void setMaximumSize(final Dimension maximumSize) {
		jta.setMaximumSize(maximumSize);
	}
	
	public void setFont(final Font font){
		jta.setFont(font);
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
		if (value == null) {
			return;
		}
		value = value.trim();
		if (value.isEmpty()) {
			return;
		}
		this.defaultvalue = value;
	}

	@Override
	public boolean canInitDefaultValue() {
		return !isedit && defaultvalue != null;
	}

	@Override
	public void initByDefaultValue() {
		if (canInitDefaultValue()) {
			setValue(defaultvalue);
			defaultvalue = null;
		}
	}

	private void edit() {
		isedit = true;
		jsp.setBorder(BorderFactory.createEmptyBorder());
	}
	@Override
	public DataSource getDataSource() {
		String dic=input.getDictionary();
		if(dic==null||dic.isEmpty())
		{
			return null;
		}
		return WdemsTagManager.Instance.getDataSource(dic);
	}
}
