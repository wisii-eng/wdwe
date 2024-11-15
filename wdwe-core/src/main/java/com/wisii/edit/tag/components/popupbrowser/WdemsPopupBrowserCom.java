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
 * @WedmsTabelCombox.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.popupbrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.wisii.component.startUp.Start;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.edit.tag.components.decorative.WdemsWarningManager;
import com.wisii.edit.tag.components.floating.WdemsToolManager;
import com.wisii.edit.tag.components.input.Dictionaryable;
import com.wisii.edit.tag.components.input.WdemsPlainDocument;
import com.wisii.edit.tag.components.select.Data;
import com.wisii.edit.tag.components.select.datasource.DataSource;
import com.wisii.edit.tag.schema.wdems.PopupBrowser;
import com.wisii.edit.tag.util.ComponentStyleUtil;

/**
 * 
 * Desc:弹网页控件
 * 
 * @author xieli
 * 
 *         2016-9-29下午09:29:27
 */
@SuppressWarnings("serial")
public class WdemsPopupBrowserCom extends JTextField implements WdemsTagComponent, Dictionaryable {

	private Actions action;
	private Object result = null;
	private final PopupBrowser input;
	private String defaultvalue;
	private boolean isedit = false;
	private DataSource dataSource;

	// private final BalloonTip bt;

	public WdemsPopupBrowserCom(final PopupBrowser input) {
		this.input = input;
		Object value = this.getValue();
		setDocument(new WdemsPlainDocument(this));
		initialComponentActions();
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(new Color(201, 221, 252));
		this.setEditable(false);
		final Component c = this;
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				c.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				c.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		setDataSource(WdemsTagManager.Instance.getDataSource(input.getSrc()));
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
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_MASK);
			ActionMap am = this.getActionMap();
			am.put("hint", act);
			this.getInputMap().put(ks, "hint");
			// this.setInputMap(JComponent.WHEN_FOCUSED, this.getInputMap());
		}

	}

	public void addActions(final Actions action) {
		this.action = action;
		this.addActionListener(action);

		if (this.input != null) {

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					String src = input.getSrc();
					try {
						Start.callBrowser(WdemsPopupBrowserCom.this,new String[]{src});
//						final JDialog dialog = new JDialog( getFrame(WdemsPopupBrowserCom.this) );
//						dialog.setTitle("模拟网页回调，可添加多项数据");
//						dialog.setModal(true);
//						dialog.setLayout(new BorderLayout());
//						dialog.add(new PopPanel(dialog), BorderLayout.CENTER);
//						dialog.setSize(400, 200);
//						dialog.setLocationRelativeTo(WdemsPopupBrowserCom.this);
//						dialog.setVisible(true);
					} catch (Exception e2) {
						e2.printStackTrace();
						JOptionPane.showMessageDialog(null, "请检查地址：" + src + "是否连接正常！");
					}
				}
			});

			// 这里无论如何都要添加失焦点验证更新数据
			this.addFocusListener(new FocusListener() {

				Object lostValue;
				Object gainedValue;

				/**
				 * 这里的问题就是当在插入特殊字符的时候需要失去一次焦点，然后设置好了之后再得到焦点
				 * 
				 */
				public void focusLost(final FocusEvent e) {
					// WdemsToolManager.Instance.setFocusComponent(null);
					this.lostValue = action.getValue();

					// 如果值有改变的时候则发送实际的事件

					// System.err.println("lost:" + this.lostValue + " gained:"
					// + this.gainedValue);
					// if (!this.lostValue.equals(this.gainedValue)) {
					// 根据失焦点时和得焦点时数据是否一致来判断是否需要进行失焦点验证
					action.doOnBlurValidation();
					this.lostValue = this.gainedValue;
					// }
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
	String[] a = null;

	public void setValue(final Object value) {
		try {
			if (value == null) {
				// TODO 应该报个什么错
			} else {
				edit();
				String s = (String)value;
				a = s.split(SystemUtil.getConfByName("base.popupbrowser.separator"));
				String conn = input.getConn();
				
				if(conn != null || !"".equals(conn)){
					this.action.fireConnection();
				}
				// 需要主动发action动作，要不不执行
				// this.postActionEvent();
//				this.fireActionPerformed();
			}
		} catch (Exception e) {
			System.err.println("WdemsPopupBrowserCom.setValue()模板设置更新节点跟网页传过来的参数个数不匹配");
			StatusbarMessageHelper.output("警告:" + "模板设置更新节点跟网页传过来的参数个数不匹配", "",
					StatusbarMessageHelper.LEVEL.INFO);
			
		}
	}
	class Dm implements Data<String>{

		
		@Override
		public Collection<String> getCellsOf(int... indexes) {
			
			return null;
		}

		@Override
		public Object getObject(int column) {
			return a[column-1];
		}

		@Override
		public Object getObject(int row, int column) {
			return a[column-1];
		}
		
	}
	public Object getValue() {
		return new Dm();
	}

	public void iniValue(final Object value) {
		this.removeActionListener(action);
		if (value == null || value.toString().isEmpty()) {
			if (canInitDefaultValue()) {
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
			 * if (!SwingUtilities.isEventDispatchThread()) { SwingUtilities.invokeLater(new Runnable() {
			 * 
			 * public void run() { setText(value.toString()); } }); } else{ this.setText(value.toString()); }
			 */
			// swing控件不会主动判断是否在swing线程中，这里需要强制判断一下
			if ("@null".equals(value)) {
				this.setText("");
			} else {
				this.setText(value.toString());
			}
			isedit = true;
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
			Rectangle2D r = getFont().getStringBounds(s, graphics.getFontRenderContext());
			Rectangle bound = getBounds();
			Insets inset = this.getBorder().getBorderInsets(this);
			LineMetrics line = getFont().getLineMetrics(s, graphics.getFontRenderContext());

			int x = inset.left;
			int y = (int) ((bound.height - r.getHeight() - inset.top - inset.bottom) / 2 + line.getAscent());

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
		
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	private static Frame getFrame(final Component comp){
		Window win = SwingUtilities.getWindowAncestor(comp);
		if(win instanceof Frame)
			return (Frame)win;
		return null;
	}
	private class PopPanel extends JPanel{
		JLabel lab = new JLabel("data:");
		JTextField tf = new JTextField(20);
		JButton sp = new JButton("多项分隔符");
		public PopPanel(final JDialog dialog) {
			super(new FlowLayout(FlowLayout.LEADING));
			add(lab);
			add(tf);
			final JButton okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(final ActionEvent e) {
					setValue(tf.getText());
					dialog.setVisible(false);
				}
			});
			sp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String s = SystemUtil.getConfByName("base.popupbrowser.separator");
					String text = tf.getText();
					tf.setText(text + s);
					
				}
			});
			add(sp);
			add(okButton);
		}
		
	}
}
