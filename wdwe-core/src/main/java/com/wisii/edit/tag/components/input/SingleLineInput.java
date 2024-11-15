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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
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
import com.wisii.edit.tag.components.formula.FormulaButton;
import com.wisii.edit.tag.components.select.datasource.DataSource;
import com.wisii.edit.tag.schema.wdems.Input;
import com.wisii.edit.tag.util.ComponentStyleUtil;
import com.wisii.edit.tag.util.WdemsTagUtil;

/**
 * 单行的输入域
 * @author 闫舒寰
 * @version 1.0 2009/07/10
 */
@SuppressWarnings("serial")
public class SingleLineInput extends JTextField implements WdemsTagComponent,Dictionaryable {

	private Actions action;
	private Object result = null;
	private final Input input;
	private String defaultvalue;
	private boolean isedit = false;
	// private final BalloonTip bt;

	public SingleLineInput(final Input input) {
		this.input = input;
		setDocument(new WdemsPlainDocument(this));
		this.setBackground(new Color(253, 238, 238));
		initialComponentActions();
		this.setBorder(BorderFactory.createEmptyBorder());
		WdemsActioinHandler.bindActions(this, BindType.SingleLineInput);
	}

	// 该控件最基本的附加功能
	private void initialComponentActions() {
		// 添加控件的提示方式
		if (this.input.getHint() != null) {
			// this.setToolTipText(this.input.getHint());
			//
			// //为显示提示添加快捷键，目前是alt+enter组成的快捷键，这个是简单输入域所特有的
			Action act = new AbstractAction() {
				public void actionPerformed(final ActionEvent e) {
					setValue(input.getHint());
					// setText(input.getHint());
				}
			};
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
					InputEvent.ALT_MASK);
			ActionMap am = this.getActionMap();
			am.put("hint", act);
			this.getInputMap().put(ks, "hint");
			// this.setInputMap(JComponent.WHEN_FOCUSED, this.getInputMap());
		}
		
		if(this.input.getConn() != null){
			this.setBackground(new Color(255, 255, 133));
			this.setToolTipText("右键计算");
			this.setEditable(false);
		}
		// 添加控件基本的过滤器
		final String filter = input.getFilter();

		if (filter != null) {
			// 过滤特定字符
			final Pattern p = Pattern.compile(filter);

			this.addKeyListener(new KeyListener() {

				public void keyTyped(final KeyEvent e) {
					char ch = e.getKeyChar();

					if (WdemsTagUtil.hasSpecialChar(ch))
						return;

					if (ch == KeyEvent.VK_ENTER) {
						// 在单行的时候，当用户输入回车的时候则是提交，不需要在正则表达式中明确显示
					} else {
						Matcher m = p.matcher(new StringBuilder().append(ch)
								.toString());
						if (m.matches()) {
							e.consume();
							StatusbarMessageHelper.output(
									"输入字符\"" + e.getKeyChar() + "\"被过滤，"
											+ input.getFilterMsg(), "被过滤字符样式为："
											+ filter,
									StatusbarMessageHelper.LEVEL.INFO);
						}
						else
						{
							edit();
						}
					}
				}

				public void keyReleased(final KeyEvent e) {
				}

				public void keyPressed(final KeyEvent e) {
				}
			});
		}
	}

	public void addActions(final Actions action) {
		this.action = action;
		this.addActionListener(action);

		if (this.input != null) {

			// 这里无论如何都要添加失焦点验证更新数据
			this.addFocusListener(new FocusListener() {

				Object lostValue;
				Object gainedValue;

				/**
				 * 这里的问题就是当在插入特殊字符的时候需要失去一次焦点，然后设置好了之后再得到焦点
				 * 
				 */
				public void focusLost(final FocusEvent e) {
//					WdemsToolManager.Instance.setFocusComponent(null);
					this.lostValue = action.getValue();
					
					// 如果值有改变的时候则发送实际的事件

					// System.err.println("lost:" + this.lostValue + " gained:"
					// + this.gainedValue);
//					if (!this.lostValue.equals(this.gainedValue)) {
						// 根据失焦点时和得焦点时数据是否一致来判断是否需要进行失焦点验证
						action.doOnBlurValidation();
						this.lostValue = this.gainedValue;
//					}
				}

				public void focusGained(final FocusEvent e) {
					WdemsToolManager.Instance.setFocusComponent((Component) e.getSource());
					// 获得焦点时记录当前值
					// System.err.println("current:" + action.getValue() +
					// " orign:" + this.gainedValue);

					if (this.lostValue == null) {
						// 第一次进入的时候
						this.gainedValue = action.getValue();
						return;
					}
					if (this.lostValue.equals(action.getValue())) {
						this.gainedValue = action.getValue();
					}
				}
			});
			if(this.input.getConn() != null){
				
				this.addMouseListener(new MouseAdapter() {
					Object lostValue;
					Object gainedValue;
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getButton() == MouseEvent.BUTTON3){//右键点击
							lostValue = action.getValue();
							action.fireConnection();
							action.getMessageListener().refresh();
							lostValue = this.gainedValue;
						}
						
					}
				});
			}
			String onEdit = input.getOnEdit();

			if (onEdit != null) {
				this.getDocument().addDocumentListener(new DocumentListener() {

					public void removeUpdate(final DocumentEvent e) {
						action.doOnEditValidation();
					}

					public void insertUpdate(final DocumentEvent e) {
						action.doOnEditValidation();
					}

					public void changedUpdate(final DocumentEvent e) {
						action.doOnEditValidation();
					}
				});
			}
		}
	}

	public JComponent getComponent() {
		return this;
	}

	public void setValue(final Object value) {
		if (value == null) {
			// TODO 应该报个什么错
		} else {
			edit();
			this.setText(value.toString());
			// 需要主动发action动作，要不不执行
			// this.postActionEvent();
			this.fireActionPerformed();
		}
	}

	public Object getValue() {
		return this.getText();
	}

	public void iniValue(final Object value) {
		this.removeActionListener(action);
		if (value == null||value.toString().isEmpty()) {
			if(canInitDefaultValue())
			{
				this.setBorder(BorderFactory.createLineBorder(Color.yellow));
				this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						edit();
					}
				});
			}
		} else {
			/**
			 * if (!SwingUtilities.isEventDispatchThread()) {
			 * SwingUtilities.invokeLater(new Runnable() {
			 * 
			 * public void run() { setText(value.toString()); } }); } else{
			 * this.setText(value.toString()); }
			 */
			// swing控件不会主动判断是否在swing线程中，这里需要强制判断一下
			if("@null".equals(value)){//为了处理公式里equals(#{a},#{b})?#{c}:@null的关联更新，如果不显示把值设置为空
				this.setText("");
			}else{
				this.setText(value.toString());
			}
			isedit=true;
		}
		this.addActionListener(action);
		
	}

	public void showValidationState(final ValidationMessage vAction) {

		Boolean b = vAction.getValidationState();

		if (b != null) {
			if (b) {
				WdemsWarningManager.registerAccept(this);
			} else {
				WdemsWarningManager.registerWarning(this);
			}
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());

		super.paintComponent(graphics);

		// 下面是添加背景提示：
		Color original = g.getColor();

		String s = this.input.getHint();
		if (this.getDocument().getLength() == 0 && s != null && !"".equals(s)) {

			Graphics2D g2d = (Graphics2D) g;
			Rectangle2D r = getFont().getStringBounds(s,
					graphics.getFontRenderContext());
			Rectangle bound = getBounds();
			Insets inset = this.getBorder().getBorderInsets(this);
			LineMetrics line = getFont().getLineMetrics(s,
					graphics.getFontRenderContext());

			int x = inset.left;
			int y = (int) ((bound.height - r.getHeight() - inset.top - inset.bottom) / 2 + line
					.getAscent());

			g2d.setColor(Color.gray);
			g2d.drawString(s, x, y);

		}
		// this.set

		// System.out.println("x:" + getX() + " y:" + getAlignmentY() +
		// " width:" + getWidth() + " heigth:" + getHeight());
		g.setColor(original);

	}

	public Object getActionResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
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
		this.setBorder(BorderFactory.createEmptyBorder());
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
