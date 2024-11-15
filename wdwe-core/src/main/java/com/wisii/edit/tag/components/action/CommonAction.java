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
 * @CommonAction.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.action;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.wisii.edit.tag.components.decorative.WdemsOperationManager;
import com.wisii.edit.tag.components.select.WdemsTableCombox;
import com.wisii.fov.render.awt.viewer.date.JDatePicker;

/**
 * 类功能描述：用于编写通用的Action
 * 如：alt + ↑ 在滚动面板中移动
 * 
 * 
 * 作者：李晓光
 * 创建日期：2009-10-10
 */
public class CommonAction {
	private interface TypeInterface{
		String getContent();
	}
	private static enum FloatActionType implements TypeInterface {
		SHOW_OPTION_POP("showoption"),
		HIDE_OPTON_POP("hideoption"),
		SHOW_HINT_POP("showhint"),
		HIDE_HINT_POP("hidehint");

		private String content;
		private FloatActionType(String content){
			this.content = content;
		}
		public String getContent() {
			return this.content;
		}
	}
	private static enum TableActionType implements TypeInterface {
		NEXT_PAGE("wdems.nextPage"),
		PREVIOUS_PAGE("wdems.previousPage"),
		SELECT_NEXT_ROW("wdems.selectNextRow"),
		SELECT_PREVIOUS_ROW("wdems.selectPreviousRow"),
		SPACE_POPUP("wdems.spacePopup"),
		SUBMIT("wdems.submit");
		private String content;
		private TableActionType(String content){
			this.content = content;
		}
		public String getContent() {
			return this.content;
		}
	}
	private static enum BoxActionType implements TypeInterface{
		END_PASS_THROUGH("endPassThrough"),
		HOME_PASS_THROUGH("homePassThrough"),
		SELECT_PREVIOUS("selectPrevious"),
		SELECT_PREVIOUS2("selectPrevious2"),
		SELECT_NEXT("selectNext"),
		SELECT_NEXT2("selectNext2"),
		PAGEUP_PASS_THROUGH("pageUpPassThrough"),
		PAGEDOWN_PASS_THROUGH("pageDownPassThrough"),
		HIDE_POPUP("hidePopup"),
		SPACE_POPUP("spacePopup"),
		TOGGLE_POPUP("togglePopup"),
		ENTER_PRESSED("enterPressed");
		private String content;
		private BoxActionType(String content){
			this.content = content;
		}
		public String getContent() {
			return this.content;
		}
	}
	private static enum TextActionType implements TypeInterface{
		REDO("redo"),
		UNDO("undo"),
		SUBMIT("submit"),
		HINT("hint");

		private String content;
		private TextActionType(String content){
			this.content = content;
		}
		public String getContent() {
			return this.content;
		}
	}
	private static enum ScroActionType implements TypeInterface{
		SCROLL_UP("scrollUp"),
		SCROLL_DOWN("scrollDown"),
		SCROLL_HOME("scrollHome"),
		SCROLL_END("scrollEnd"),
		UNIT_SCROLL_UP("unitScrollUp"),
		UNIT_SCROLL_DOWN("unitScrollDown"),
		SCROLL_LEFT("scrollLeft"),
		SCROLL_RIGHT("scrollRight"),
		UNIT_SCROLL_LEFT("unitScrollLeft"),
		UNIT_SCROLL_RIGHT("unitScrollRight");
		
		private String content;
		private ScroActionType(String content){
			this.content = content;
		}
		public String getContent(){
			return this.content;
		}
	}
	@ActionOptioins("showoption")
	public void showOptionPopup(ActionEvent e){
		WdemsOperationManager.showOptionPopup(e);
	}
	@ActionOptioins("hideoption")
	public void hideOptionPopup(ActionEvent e){
		WdemsOperationManager.hideOptionPopup(e);
	}
	@ActionOptioins("nextpage")
	public void nextPage(ActionEvent e){
		process(e, TableActionType.NEXT_PAGE, WdemsTableCombox.class);		
	}
	@ActionOptioins("previouspage")
	public void previousPage(ActionEvent e){
		process(e, TableActionType.PREVIOUS_PAGE, WdemsTableCombox.class);		
	}
	@ActionOptioins("nextrow")
	public void nextRow(ActionEvent e){
		process(e, TableActionType.SELECT_NEXT_ROW, WdemsTableCombox.class);		
	}
	@ActionOptioins("previousrow")
	public void previousRow(ActionEvent e){
		process(e, TableActionType.SELECT_PREVIOUS_ROW, WdemsTableCombox.class);		
	}
	@ActionOptioins("spaceshow")
	public void showPopWithSpace(ActionEvent e){
		process(e, TableActionType.SPACE_POPUP, WdemsTableCombox.class);		
	}
	
	@ActionOptioins("unitup")
	public void scroUnitUp(ActionEvent e){
		/*System.out.println("enter unit up");*/
		processForScro(e, ScroActionType.UNIT_SCROLL_UP);
	}
	@ActionOptioins("pageup")
	public void scroPageUp(ActionEvent e){
		/*System.out.println("enter page up");*/
		processForScro(e, ScroActionType.SCROLL_UP);
	}
	@ActionOptioins("unitdown")
	public void scroUnitDown(ActionEvent e){
		/*System.out.println("enter unit down");*/
		processForScro(e, ScroActionType.UNIT_SCROLL_DOWN);
	}
	@ActionOptioins("pagedown")
	public void scroPageDown(ActionEvent e){
		/*System.out.println("enter page down");*/
		processForScro(e, ScroActionType.SCROLL_DOWN);
	}
	@ActionOptioins("unitleft")
	public void scroUnitLeft(ActionEvent e){
		processForScro(e, ScroActionType.UNIT_SCROLL_LEFT);
	}
	@ActionOptioins("unitright")
	public void scroUnitRight(ActionEvent e){
		processForScro(e, ScroActionType.UNIT_SCROLL_RIGHT);
	}
	@ActionOptioins("scrohome")
	public void scroHome(ActionEvent e){
		/*System.out.println("enter home");*/
		processForScro(e, ScroActionType.SCROLL_HOME);
	}
	@ActionOptioins("scroend")
	public void scroEnd(ActionEvent e){
		/*System.out.println("enter end");*/
		processForScro(e, ScroActionType.SCROLL_END);
	}
	@ActionOptioins("redo")
	public void redo(ActionEvent e){
		process(e, TextActionType.REDO, JTextComponent.class);
	}
	@ActionOptioins("undo")
	public void undo(ActionEvent e){
		process(e, TextActionType.UNDO, JTextComponent.class);
	}
	@ActionOptioins("hint")
	public void updateHintToComponent(ActionEvent e){
		process(e, TextActionType.HINT, JTextComponent.class);
	}
	@ActionOptioins("showdate")
	public void showDatePane(ActionEvent e){
		process(e, BoxActionType.SELECT_NEXT2, JComboBox.class);
	}
	@ActionOptioins("hidedate")
	public void hidenDatePane(ActionEvent e){
		process(e, BoxActionType.HIDE_POPUP, JComboBox.class);
	}
	@ActionOptioins("nextyear")
	public void nextYear(ActionEvent e){
		/*System.out.println("enter next year");*/
		JDatePicker box = getT(e.getSource(), JDatePicker.class);
		box.nextYear();
		/*process(e, BoxActionType.SELECT_NEXT2, JComboBox.class);*/
	}
	@ActionOptioins("previousyear")
	public void previousYear(ActionEvent e){
		JDatePicker box = getT(e.getSource(), JDatePicker.class);
		box.previousYear();
	}
	@ActionOptioins("nextmouth")
	public void nextMouth(ActionEvent e){
		JDatePicker box = getT(e.getSource(), JDatePicker.class);
		box.nextMouth();
	}
	@ActionOptioins("previousmouth")
	public void previousMouth(ActionEvent e){
		JDatePicker box = getT(e.getSource(), JDatePicker.class);
		box.previousMouth();
	}
	@ActionOptioins("nextday")
	public void nextDay(ActionEvent e){
		JDatePicker box = getT(e.getSource(), JDatePicker.class);
		box.nextDay();
	}
	@ActionOptioins("previousday")
	public void previousDay(ActionEvent e){
		JDatePicker box = getT(e.getSource(), JDatePicker.class);
		box.previousDay();
	}
	@ActionOptioins("previousweek")
	public void previousWeek(ActionEvent e){
		JDatePicker box = getT(e.getSource(), JDatePicker.class);		
		box.previousWeek();
	}
	@ActionOptioins("nextweek")
	public void nextWeek(ActionEvent e){
		JDatePicker box = getT(e.getSource(), JDatePicker.class);
		if(!box.isPopupVisible())
			process(e, BoxActionType.SELECT_NEXT2, JComboBox.class);
		else
			box.nextWeek();
	}
	@ActionOptioins("nextfocus")
	public void moveToNextFocus(ActionEvent e){
		JComponent comp = getT(e.getSource(), JComponent.class);
		if(comp == null)
			return;
		comp.transferFocus();
	}
	@ActionOptioins("previoufocus")
	public void moveToPreviouFocus(ActionEvent e){
		JComponent comp = getT(e.getSource(), JComponent.class);
		if(comp == null)
			return;
		comp.transferFocusBackward();
	}
	
	@ActionOptioins("submit")
	public void submit(ActionEvent e){
		Object source = e.getSource();
		JComponent comp = getT(source, JComponent.class);
		if(comp instanceof JTextComponent)
			submitTextComponent((JTextComponent)comp, e);
		else if(comp instanceof AbstractButton)
			submitButtonCompnent((AbstractButton)comp);
		else if(comp instanceof JDatePicker){
			submitDate((JDatePicker)comp);
		}else if(comp instanceof WdemsTableCombox){
			process(e, TableActionType.SUBMIT, WdemsTableCombox.class);
		}
	}
	private void submitDate(JDatePicker date){
		if(date.isPopupVisible())
			date.submit();
	}
	private void submitButtonCompnent(AbstractButton btn){
		btn.doClick();
	}
	private void submitTextComponent(JTextComponent comp, ActionEvent e){
		JTextField field = getT(comp, JTextField.class);
		if(field == null)
			return;
		
		ActionListener[] lises = field.getActionListeners();
		if(lises == null)
			return;
		
		for (ActionListener lis : lises) {
			process(comp, lis, e);
		}
	}
	/* 转发Action */
	private <T extends JComponent> void process(ActionEvent e, TypeInterface type, Class<T> clazz){
		T t = getT(e.getSource(), clazz);
		if(t == null)
			return;
		process(t, type, e);
	}
	private void processForScro(ActionEvent e, TypeInterface type){
		Object obj = e.getSource();
		
		JComponent comp = getT(obj, JComponent.class);
		JScrollPane pane = getScrollPane(comp);
		
		if(pane == null)
			return;
		process(pane, type, e);
	}
	/* 转发Action */
	private void process(JComponent c, ActionListener a, ActionEvent e){
		e.setSource(c);
		a.actionPerformed(e);
	}
	/* 转发Action */
	private void process(JComponent c, TypeInterface type, ActionEvent e){
		Action a = c.getActionMap().get(type.getContent());
		if(a == null)
			return;
		
		e.setSource(c);		
		a.actionPerformed(e);
	}
	private JScrollPane getScrollPane(Component comp){
		return getTParent(comp, JScrollPane.class);
	}
	/**
	 * 根据制定的Component对象，向上遍历Component-Tree，直到找到指定的类型为止，
	 * 如果没有指定的类型的对象，则返回Null。
	 * @param <T>
	 * @param comp
	 * @param clazz
	 * @return
	 */
	public final static <T extends Component> T getTParent(Component comp, Class<T> clazz){
		if(comp == null)
			return null;
		if(clazz.isAssignableFrom(comp.getClass()))
			return clazz.cast(comp);
		
		return getTParent(comp.getParent(), clazz);
	}
	/**
	 * 判断指定的对象是否是Component类型，如果是将其转换为Component类型返回
	 * @param <T>
	 * @param obj
	 * @return
	 */
	public final static <T extends Component> T getT(Object obj, Class<T> clazz){
		if(obj == null)
			return null;
		
		if(clazz.isAssignableFrom(obj.getClass())){
			return clazz.cast(obj);
		}
		return null;
	}
}
