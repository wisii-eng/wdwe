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
 * @WdemsCompositeComponent.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.complex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.message.StatusbarMessageHelper.LEVEL;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;

/**
 * 类功能描述：用于创建复合控件。 1、现在采用的是JTextPane，该控件可以插入JComponent控件。 
 * 作者：李晓光 创建日期：2009-6-30
 */
public class WdemsCompositeComponent implements WdemsTagComponent {
	private final JTextPane pane = new WdemsTextPane();
	private JComponent activeComponent = null;
	/*private JScrollPane scrPane = new JScrollPane(pane);*/
	 /** A list of event listeners for this component. */
    protected EventListenerList listenerList = new EventListenerList();
    private Object result=null;
	public WdemsCompositeComponent() {
		pane.setDocument(new DocumentImp());
		initStyle();
	}
	private void initStyle(){
		pane.setEditable(Boolean.FALSE);
		pane.setFocusCycleRoot(Boolean.FALSE);
		pane.setFocusable(Boolean.FALSE);
		pane.setMargin(new Insets(0, 0, 0, 0));
		
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setSpaceAbove(set, -2.8F);
		StyleConstants.setSpaceBelow(set, .0F);
		StyleConstants.setLineSpacing(set, 0F);
		
		pane.setParagraphAttributes(set, true);
		/*AttributeSet set = pane.getParagraphAttributes();
		System.out.println("space above = " + StyleConstants.getSpaceAbove(set));
		System.out.println("line space = " + StyleConstants.getLineSpacing(set));
		System.out.println("space below = " + StyleConstants.getSpaceBelow(set));*/
	}
	/* --------------------WdemsTagComponent接口实现------------------------ */
	public void addActions(final Actions action) {
		listenerList.add(ActionListener.class, action);
	}

	public void removeActionListener(final ActionListener action) {
		listenerList.remove(ActionListener.class, action);
	}

	public JComponent getComponent() {
		return pane /*scrPane*/;
	}

	public Object getValue() {
		return null;
	}
	public void setValue(Object value) {
		
	}
	public void iniValue(final Object value) {
		if (value instanceof JComponent) {
			addComponent((JComponent) value);
		} else {
			pane.setText(value + "");
		}
	}

	public void setLocation(final Point p) {
		pane.setLocation(p);
	}

	public void setMaximumSize(final Dimension maximumSize) {
		pane.setMaximumSize(maximumSize);
	}

	public void showValidationState(final ValidationMessage vAction) {
		StatusbarMessageHelper.output("复合控件", "无法通过验证", LEVEL.DEBUG);
	}
	/* --------------------WdemsTagComponent接口实现------------------------ */
	public final static Component getComponent(final int pos, JTextComponent pane) throws BadLocationException{
		if(pos < 0 || pos >= getLength(pane))
			return null;
		Rectangle r = pane.modelToView(pos);
		
		int centerX = r.x + r.width / 2;
		int centerY = r.y + r.height / 2;
		
		Component comp = pane.getComponentAt(centerX, centerY);
		return get(comp);
	}
	private final static Component get(final Component comp){
		if(!(comp instanceof Container))return comp;
		Container container = (Container)comp;
		if(container.getComponentCount() > 0)
			return container.getComponent(0);
		return null;
	}
	/**
	 * 获得当前控件的数据长度，其中一个控件、图标均看作是一个长度。
	 * @return	返回当前控件包换数据数目。
	 */
	public final static int getLength(JTextComponent pane){
		return pane.getDocument().getLength();
	}
	public void addComponent(final JComponent comp){
		if(comp == null)return;
		
		pane.insertComponent(comp);
		
		updateActive(comp);
	}
	public void addWdemsTag(final WdemsTagComponent tag){
		if(tag == null)return;
		JComponent comp = tag.getComponent();
		addComponent(comp);
	}
	public void addIcon(final Icon icon){
		if(icon == null)return;
		
		pane.insertIcon(icon);
	}
	public void insertComponent(final JComponent comp, final int index){
		if(!canInsert(index))return;
		
		int[] indexes = startInsert();
		pane.insertComponent(comp);

		updateActive(comp);
		
		endInsert(indexes);
	}
	public void insertIcon(final Icon icon, final int index){
		if(!canInsert(index))return;
		
		int[] indexes = startInsert();
		
		pane.insertIcon(icon);
		endInsert(indexes);
	}
	public final static void scrollToVisible(JComponent activeComponent){
		if(activeComponent == null)
			return;
		JViewport view = getView(activeComponent);
		
		Point p  = activeComponent.getLocation();		
		/*p.setLocation(p.x, p.y + 8);*/
		p.setLocation(p.x, p.y + 8);
		view.setViewPosition(p);
	}
	private void updateActive(JComponent comp){
		if(comp != null && activeComponent == null) {
			this.activeComponent = comp;
		}
		/*scrollToVisible(activeComponent);*/
	}
	
	private final static JViewport getView(Container comp){
		if(comp == null)
			return null;
		if(comp instanceof JViewport)
			return (JViewport)comp;
		return getView(comp.getParent());
	}
	private int[] startInsert(){
		int start = pane.getSelectionStart();
		int end = pane.getSelectionEnd();
		return new int[]{start, end};
	}
	private boolean canInsert(int index){
		int length = pane.getText().length();
		if(index < 0 || index >= length)
			return Boolean.FALSE;
		pane.select(index, ++index);
		return Boolean.TRUE;
	}
	private void endInsert(final int... indexes){
		pane.select(indexes[0], indexes[1]);
	}
	@SuppressWarnings("serial")
	private class DocumentImp extends DefaultStyledDocument{
		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if(StyleConstants.getComponent(a) == null)return;
			super.insertString(offs, str, a);
		}
		@Override
		public void remove(int offs, int len) throws BadLocationException {
			
		}
	}
	public static void main(final String[] args) {
		JFrame fr = new JFrame("Title");
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setLayout(new BorderLayout());
		fr.setSize(400, 300);
		final WdemsCompositeComponent comp = new WdemsCompositeComponent();
		final JTextPane pane = (JTextPane)comp.getComponent();
		/*pane.setEditable(true);
		pane.setFocusCycleRoot(Boolean.TRUE);
		pane.setFocusable(Boolean.TRUE);
		pane.setAutoscrolls(true);*/
		fr.add(pane, BorderLayout.CENTER);
		final  JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		final JTextField field = new JTextField(5);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e) {
				/*String text = field.getText();
				if(text != null && !"".equals(text)){
					int i = Integer.parseInt(text);
					pane.select(i, i + 1);
				}
				pane.selectAll();
				System.out.println("" + pane.getSelectedText());*/
				
				/*JLabel lab = new JLabel("sdfs");*/
				JTextField lab = new JTextField("abc");
				lab.setBorder(BorderFactory.createLineBorder(Color.BLUE));
				/*lab.setMargin(new Insets(0, 0, 0, 0));*/
				FontMetrics f = lab.getFontMetrics(lab.getFont());
				int line = f.getAscent() + f.getDescent();
				Dimension dim = lab.getPreferredSize();
				/*lab.setPreferredSize(new Dimension(dim.width, line));*/
				lab.setAlignmentY(0.75F);
				lab.setForeground(Color.RED);
				pane.insertComponent(lab);
			}
		});
		final JButton btnGaint = new JButton("获取控件");
		btnGaint.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e) {
				EditorKit kit = pane.getEditorKit();
				if(!(kit instanceof StyledEditorKit)){
//					System.out.println("Edi");
					return;
				}
				
				/*for (int i = 0, length = pane.getComponentCount(); i < length; i++) {
					comp = pane.getComponent(i);
					if(comp instanceof Container){
						int pos = pane.viewToModel(comp.getLocation());
						System.err.println("pos = " + pos);
						Container container = (Container)comp;
						System.out.println(container.getComponentCount());
						System.out.println(container.getComponent(0));
					}
				}*/
				try {
					System.out.println(comp.getComponent(1, pane));;
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		});
		btnPanel.add(field);
		btnPanel.add(btnAdd);
		btnPanel.add(btnGaint);
		fr.add(btnPanel, BorderLayout.SOUTH);
		
		fr.setVisible(true);
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
