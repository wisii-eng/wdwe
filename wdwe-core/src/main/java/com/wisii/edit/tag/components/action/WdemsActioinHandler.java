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
 * @WdemsActioinHandler.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.message.StatusbarMessageHelper.LEVEL;
import com.wisii.edit.tag.components.action.schema.KeyManager;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;

/**
 * 类功能描述：用于创建快捷键用Action对象。 Action信息是来自于配置文件。 作者：李晓光 创建日期：2009-9-27
 */
public class WdemsActioinHandler {
	private final static String METHOD_NAME = ActionListener.class.getDeclaredMethods()[0].getName();
	private static enum When{
		UNDEFINED_CONDITION(-1),
		WHEN_FOCUSED(0),
		WHEN_IN_FOCUSED_WINDOW(2),
		WHEN_ANCESTOR_OF_FOCUSED_COMPONENT(1);
		
		private int value = -1;
		private When(final int value){
			this.value = value;
		}
		public int getValue(){
			return this.value;
		}
	}
	private WdemsActioinHandler() {	}
	
	public final static void bindActions(final JComponent source, final ActionItem... actionItems) {
		bindActonsByWhen(source, When.WHEN_FOCUSED, actionItems);
	}
	public final static void bindActionsWhenWindow(final JComponent source, final ActionItem... actionItems){ 
		bindActonsByWhen(source, When.WHEN_IN_FOCUSED_WINDOW, actionItems);
	}
	
	public final static void bindActionsWhenAncestor(final JComponent source, final ActionItem... actionItems){
		bindActonsByWhen(source, When.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, actionItems);
	}
	public final static void bindActions(final JComponent source, final BindType type){
		Collection<ActionItem> items = KeyManager.buildActionItems(type);
		bindAction(source, items, When.WHEN_FOCUSED);
	}
	public final static void bindActionsWhenWindow(final JComponent source, final BindType type){
		Collection<ActionItem> items = KeyManager.buildActionItems(type);
		bindAction(source, items, When.WHEN_IN_FOCUSED_WINDOW);
	}
	public final static void bindActionsWhenAncestor(final JComponent source, final BindType type){
		Collection<ActionItem> items = KeyManager.buildActionItems(type);
		bindAction(source, items, When.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}
	private final static void bindAction(final JComponent source, final Collection<ActionItem> actionItems, final When when) {
		if (source == null || actionItems == null || actionItems.isEmpty())
			return;
		ActionItem[] items = actionItems.toArray(new ActionItem[0]);
		switch (when) {
		case WHEN_FOCUSED:
			bindActions(source, items);
			break;
		case WHEN_IN_FOCUSED_WINDOW:
			bindActionsWhenWindow(source, items);
		case WHEN_ANCESTOR_OF_FOCUSED_COMPONENT:
			bindActionsWhenAncestor(source, items);
		default:
			break;
		}
	}
	private final static void bindActonsByWhen(final JComponent source, final When when, final ActionItem...actionItems){
		if (source == null || actionItems == null)
			return;
		InputMap input = source.getInputMap(when.getValue());
		ActionMap action = source.getActionMap();
		for (ActionItem item : actionItems) {
			if (item == null)
				continue;
			input.put(item.getStroke(), item.getActionKey());
			action.put(item.getActionKey(), item.getAction());
		}
	}
	public final static Action createAction(final String baseUrl, final String annotation)
			throws ClassNotFoundException {
		Action action = null;
		if (baseUrl == null || baseUrl.isEmpty())
			return action;
		Class<?> clazz = getClass(baseUrl);
		
		return createAction(clazz, annotation);
	}
	public final static Action createAction(final Class<?> clazz, final String annotation){
		Action action = null;
		if (annotation == null || "".equals(annotation)) {
			if (Action.class.isAssignableFrom(clazz)) {
				try {
					return (Action) clazz.newInstance();
				} catch (Exception e) {
					return action;
				}
			}else if(!ActionListener.class.isAssignableFrom(clazz)){
				return action;
			}
		}
		action = createAnnotationAction(clazz, annotation);

		return action;
	}
	public final static Action createAction(final Object source, final String annotation){
		Action action = null;
		final Class<?> clazz = source.getClass();
		if(annotation == null || "".equalsIgnoreCase(annotation)){
			if(!ActionListener.class.isAssignableFrom(clazz))
				return action;
		}
		InvocationHandler handler = new InvocationHandler() {

			public Object invoke(final Object proxy, final Method method, final Object[] args)
					throws Throwable {
				String name = method.getName();

				if (METHOD_NAME.equalsIgnoreCase(name)) {
					Method m = getActionMethod(clazz, annotation);
					if(m == null){
						StatusbarMessageHelper.output("无法找到您配置的Action", "class:" + clazz.getSimpleName() + "， method:" + annotation, LEVEL.INFO);
						return null;
					}
					return m.invoke(source, args);
				}
				return method.invoke(WdemsAction.shareIntance(), args);
			}
		};
		action = (Action) Proxy.newProxyInstance(null, new Class[] { Action.class }, handler);

		return action;
	}
	private final static Action createAnnotationAction(final Class<?> clazz, final String annotation) {
		try {
			return createAction(clazz.newInstance(), annotation);
		} catch (Exception e) {
			List<String> error = new ArrayList<String>();
			error.add(clazz.getSimpleName());
			error.add(annotation);
			error.add(e.getMessage());
			StatusbarMessageHelper.output("不能正确的的创建Action" , error.toString(), LEVEL.INFO);
		} 
		return null;
	}
	private final static Method getActionMethod(final Class<?> clazz, final String annotation) {
		Method method = null;
		try {
			method = getMethodWithName(clazz, annotation);
			if(method == null)
				method = getMethodWithAnnotation(clazz, annotation);
				
		} catch (NoSuchMethodException e) {
			/*StatusbarMessageHelper.output("快捷健配置错误1", e.getMessage(), LEVEL.INFO);*/
		}
		if(method != null)
			return method;
		try {
			method = getMethodWithAnnotation(clazz, annotation);
		} catch (NoSuchMethodException e) {
			StatusbarMessageHelper.output("无法找到你配置的Function。【" + annotation + "】", e.getMessage(), LEVEL.INFO);
		}
		return method;
	}
	private final static Method getActionMethod(final Class<?> clazz, final String annotation, final boolean isAnnotaiton) {
		if (!isAnnotaiton) {
			try {
				return clazz.getMethod(annotation, ActionEvent.class);
			} catch (Exception e) {
				return null;
			}
		}

		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			if (isAnnotaiton) {
				ActionOptioins options = method
						.getAnnotation(ActionOptioins.class);
				if (options == null)
					continue;
				else if (options.value().equalsIgnoreCase(annotation))
					return method;
				else
					continue;
			} else {
				if (method.getName().equalsIgnoreCase(annotation))
					return method;
			}
		}
		return null;
	}

	private final static Method getMethodWithName(final Class<?> clazz, final String annotation) throws NoSuchMethodException {
		if(annotation == null || "".equalsIgnoreCase(annotation)){
			if(ActionListener.class.isAssignableFrom(clazz)){
				return ActionListener.class.getDeclaredMethods()[0];
			}else{
				StatusbarMessageHelper.output("快捷健配置错误", "您指定方法名称为空:【" + annotation +"】", LEVEL.INFO);
				return null;
				/*throw new IllegalArgumentException("您指定方法名称为空:【" + annotation +"】");*/
			}
		}
		try {
			return clazz.getMethod(annotation, ActionEvent.class);
		} catch (Exception e) {
			throw new NoSuchMethodException(clazz.getSimpleName() + "." + annotation);
		}
	}
	private final static Method getMethodWithAnnotation(final Class<?> clazz, final String annotation) throws NoSuchMethodException{
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			ActionOptioins options = method.getAnnotation(ActionOptioins.class);
			if (options == null)
				continue;
			else if (options.value().equalsIgnoreCase(annotation))
				return method;
		}
		throw new NoSuchMethodException(clazz.getSimpleName() + "." + annotation);
	}
	public final static Class<?> getClass(final String clazz)
			throws ClassNotFoundException {
		return Class.forName(clazz);
	}

	public static class ActionItem {
		private KeyStroke stroke = null;
		private Object actionKey = null;
		private Action action = null;

		private ActionItem(final KeyStroke stroke, final Object actionKey, final Action action) {
			this.action = action;
			this.actionKey = actionKey;
			this.stroke = stroke;
		}

		public KeyStroke getStroke() {
			return this.stroke;
		}

		public Object getActionKey() {
			return this.actionKey;
		}

		public Action getAction() {
			return this.action;
		}

		public final static ActionItem newInstance(String stroke, final Object actionKey, final Action action) {
			if (stroke == null || "".equals(stroke))
				/*throw new NullPointerException("");*/
				return null;
			if (actionKey == null)
				/*throw new NullPointerException("");*/
				return null;
			stroke = SystemUtil.buildStroke(stroke);
			KeyStroke keyStroke = KeyStroke.getKeyStroke(stroke);
			return new ActionItem(keyStroke, actionKey, action);
		}
		
	}

	@SuppressWarnings("serial")
	static class WdemsAction extends AbstractAction {
		private final static WdemsAction share = new WdemsAction();
		public void actionPerformed(final ActionEvent e) {}
		public static WdemsAction shareIntance(){
			return share;
		}
	}
}
