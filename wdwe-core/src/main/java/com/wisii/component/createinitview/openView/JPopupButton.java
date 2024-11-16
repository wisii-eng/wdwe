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
 * 
 */
package com.wisii.component.createinitview.openView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.AbstractBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
//import com.sun.java.swing.plaf.windows.WindowsButtonUI;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.fov.apps.FOUserAgent;

/**
 * <p>
 * Title: OpenSwing
 * </p>
 * <p>
 * Description:弹出式菜单按钮
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
 * @version 1.0
 */

public class JPopupButton extends JComponent implements Serializable {
	public static final int TYPE_NORMAL = 0;

	private boolean hideText = false;

	public static final int TYPE_WITH_RIGHT_TOGGLE = 1;

	private int style = -1;

	private int actionIndex = -1;

	private JPopupMenu popup = null;

	private boolean mustRefresh = false;

	private JToggleButton btnLeft;

	private JButton bttRight;
	private String rkey;
	private String lkey;

	private PopupButtonListener listener = new PopupButtonListener();
	
	private UpBorder upBorder = new UpBorder();
	private DownBorder downBorder = new DownBorder();
	// javax.swing.plaf.BorderUIResource$EmptyBorderUIResource emptyBorder = new
	// javax.swing.plaf.BorderUIResource$EmptyBorderUIResource(new Insets(0, 0,
	// 0, 0));

	/**
	 * <p>
	 * Title: OpenSwing
	 * </p>
	 * <p>
	 * Description: 鼠标移入时的边界
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2004
	 * </p>
	 * <p>
	 * Company:
	 * </p>
	 * 
	 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
	 * @version 1.0
	 */
	public class UpBorder extends AbstractBorder {
		int thickness = 1;

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			g.setColor(Color.white);
			g.drawLine(0, 0, width - 1, 0);
			g.drawLine(0, 0, 0, height - 1);
			g.setColor(Color.gray);
			g.drawLine(width - 1, 0, width - 1, height);
			g.drawLine(0, height - 1, width, height - 1);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(thickness, thickness, thickness, thickness);
		}
	}

	/**
	 * <p>
	 * Title: OpenSwing
	 * </p>
	 * <p>
	 * Description: 鼠标按下时的边界
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2004
	 * </p>
	 * <p>
	 * Company:
	 * </p>
	 * 
	 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
	 * @version 1.0
	 */
	public class DownBorder extends AbstractBorder {
		int thickness = 1;

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			g.setColor(Color.gray);
			g.drawLine(0, 0, width - 1, 0);
			g.drawLine(0, 0, 0, height - 1);
			g.setColor(Color.white);
			g.drawLine(width - 1, 0, width - 1, height);
			g.drawLine(0, height - 1, width, height - 1);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(thickness, thickness, thickness, thickness);
		}
	}

	/**
	 * <p>
	 * Title: OpenSwing
	 * </p>
	 * <p>
	 * Description: 按钮各种动作时边界变化以及事件处理
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2004
	 * </p>
	 * <p>
	 * Company:
	 * </p>
	 * 
	 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
	 * @version 1.0
	 */
	private class PopupButtonListener implements MouseListener,
			PopupMenuListener {
		

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {

			if (!JPopupButton.this.isEnabled()) {
				return;
			}
	
			if (e.getSource() == btnLeft) {
				btnLeftDo();
			} else {
				btnRightDo();
			}

		}

		public void mouseReleased(MouseEvent e) {
			bttRight.setSelected(false);
		}

		public void mouseEntered(MouseEvent e) {
			if (!JPopupButton.this.isEnabled()) {
				return;
			}
			if (!JPopupButton.this.popup.isShowing()) {
				btnLeft.setBorder(upBorder);
				bttRight.setBorder(upBorder);
			}
		}

		public void mouseExited(MouseEvent e) {
			if (!JPopupButton.this.isEnabled()) {
				return;
			}

			if (!JPopupButton.this.popup.isShowing()) {
				btnLeft.setBorder(null);
				bttRight.setBorder(null);
			}
		}
		
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			if (!JPopupButton.this.isEnabled()) {
				return;
			}

			btnLeft.setBorder(null);
			bttRight.setBorder(null);
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}

	public JPopupButton() {
		this(TYPE_NORMAL);
	}

	public JPopupButton(int style) {
		this(style, null);
	}

	public JPopupButton(int style, String text) {
		this(style, text, (String) null);
	}
	public JPopupButton(int style, String text, String icon) {
		this(style, text, icon,(String) null);
	}

	public JPopupButton(int style, String text, String icon,String key) {
		this(style, text, icon, key,new JPopupMenu());
	}

	public JPopupButton(int style, String text, Icon icon, JPopupMenu popup) {
		createButtons();
		setIcon(icon);
		if (!hideText)
			setText(text);
		setPopup(popup);
		setStyle(style);
	}

	public JPopupButton(int style, String text, String icon,String key, JPopupMenu popup) {
		createButtons();
//		setIcon(icon);
		if (!hideText)
		{
			setText(text);
		}
		setPopup(popup);
		setStyle(style);
		setKey(key);
	}

	protected void showPopupMenu() {
		if (popup == null) {
			return;
		}
		popup.show(this, 0, this.getHeight());
	}

	protected void createButtons() {
		if (btnLeft == null) {
			btnLeft = new JToggleButton() {
				private static final long serialVersionUID = -2354623448457880135L;
				public void setUI(ButtonUI ui) {
//					if (ui instanceof WindowsButtonUI) {
//						ui = new BasicButtonUI();
//					}
					super.setUI(ui);
				}
			};
			btnLeft.setMargin(new Insets(0, 0, 0, 0));
		}
		btnLeft.setSelected(false);
		
		if (bttRight == null) {
			bttRight = new JButton() {
				public void setUI(ButtonUI ui) {
//					if (ui instanceof WindowsButtonUI) {
//						ui = new BasicButtonUI();
//					}
					super.setUI(ui);
				}

				public void paint(Graphics g) {
					super.paint(g);
					Polygon p = new Polygon();
					int w = getWidth();
					int y = (getHeight() - 4) / 2;
					int x = (w - 6) / 2;
					if (isSelected()) {
						x += 1;
					}
					p.addPoint(x, y);
					p.addPoint(x + 3, y + 3);
					p.addPoint(x + 6, y);
					g.fillPolygon(p);
					g.drawPolygon(p);
				}
			};
			bttRight.setUI(new BasicButtonUI());
			bttRight.setMargin(new Insets(0, 0, 0, 0));
		}
	}
	
	protected void refreshUI() {
		if (!mustRefresh) {
			return;
		}
		super.removeAll();
		this.setBorder(null);
		this.setLayout(new BorderLayout());
		this.add(btnLeft, BorderLayout.CENTER);
		if (style == TYPE_WITH_RIGHT_TOGGLE) {
			this.add(bttRight, BorderLayout.EAST);
		}
		btnLeft.setFocusable(false);

		btnLeft.setBorder(null);
		btnLeft.addMouseListener(listener);
		bttRight.setFocusable(false);
		bttRight.setPreferredSize(new Dimension(13, 1));
		bttRight.setBorder(null);
		bttRight.addMouseListener(listener);
		this.doLayout();
	}

	public void setStyle(int style) {
		if (this.style != style) {
			mustRefresh = true;
		}
		this.style = style;
		refreshUI();
	}

	public int getStyle() {
		return style;
	}

	public void setText(String text) {
		btnLeft.setText(text);
		btnLeft.setToolTipText("切换层");
		bttRight.setToolTipText("选择层");
	}
	public String getText() {
		return btnLeft.getText();
	}
	
	public void setKey(String key){
		lkey = "ctrl "+key;
		rkey = "ctrl shift "+key;
		lkey = SystemUtil.buildStroke(lkey);
		rkey = SystemUtil.buildStroke(rkey);
		//左快捷键
		Action  lAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				btnLeftDo();
				btnLeft.setSelected(!btnLeft.isSelected());
				bttRight.setSelected(false);
			}
		};
		btnLeft.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(lkey), "L");
		btnLeft.getActionMap().put("L",lAction);
		//右快捷键
		Action  rAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				btnRightDo();
			}
		};
		bttRight.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(rkey), "R");
		bttRight.getActionMap().put("R",rAction);

	}
	public void btnLeftDo(){
		if(btnLeft.isSelected()){
		btnLeft.setBorder(downBorder);
		bttRight.setBorder(downBorder);
		}
		else
		{
			btnLeft.setBorder(upBorder);
			bttRight.setBorder(downBorder);
		}
		FOUserAgent agent = EngineUtil.getEnginepanel().getFOUserAgent();
		agent.setViewNoBack(btnLeft.isSelected());
		EngineUtil.getEnginepanel().reload();
	}
	public void btnRightDo(){
		btnLeft.setBorder(upBorder);
		bttRight.setBorder(downBorder);
		bttRight.setSelected(true);
		WisedocPopPanel pop = new WisedocPopPanel(
				JPopupButton.this);
		setPopup(pop);
		JPopupButton.this.showPopupMenu();
	}


	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		btnLeft.setEnabled(enabled);
		bttRight.setEnabled(enabled);
	}

	public void setIcon(Icon icon) {

		btnLeft.setIcon(icon);
		hideText = true;

	}

	public void setIcon(String icon) {
		URL url = SystemUtil.getImagesPath(icon);
		if (url != null) {
			ImageIcon ss = new ImageIcon(url);
			setIcon(ss);
		}

	}

	public Icon getIcon() {
		return btnLeft.getIcon();
	}

	public void setActionSameAsPopup(int index) {
		this.actionIndex = index;
	}

	public int getActionSameAsPopup() {
		return actionIndex;
	}

	public AbstractButton getLeftButton() {
		return this.btnLeft;
	}

	public void setPopup(JPopupMenu pop) {
		if (this.popup != null) {
			popup.removePopupMenuListener(listener);
		}
		this.popup = pop;
		popup.removePopupMenuListener(listener);
		popup.addPopupMenuListener(listener);
	}

	public JPopupMenu getPopup() {
		return popup;
	}


	public boolean isLeftSelected() {
		return btnLeft.isSelected();

	}
}
