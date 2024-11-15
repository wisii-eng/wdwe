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
 */package com.wisii.edit.tag.action;

import java.awt.event.ActionEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.AbstractAction;
import com.wisii.component.createinitview.MessageListener;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.correlation.Correlation;
import com.wisii.edit.tag.util.WdemsTagUtil;
import com.wisii.edit.tag.util.WdemsTagUtil.ValidationType;

/**
 * 所有动作的父类
 * @author 闫舒寰
 * @version 1.0 2009/06/16
 */
public abstract class Actions extends AbstractAction implements WdemsAction, ValidationMessage, ValidationActions {
	
	//最终标签对象的总接口
	private WdemsComponent wc;

	//当前对象所对应的绝对xpath
	private String xpath;
	
	//tag对象
	private Object tagObject;
	
	//控件对象
	private WdemsTagComponent tagComponent;

	//触发当前动作的动作事件
	protected ActionEvent actionEvent;

	//错误信息
	private String wrongMessage;
	
	//是否更新成功
//	private boolean updateXMLSucess;
	
	//当前所进行的验证
	private ValidationType currentValidation;
	
	//当前控件的整体面板
	private MessageListener messageListener;
	
	/**
	 * 获得当前控件的整体面板
	 * @return
	 */
	public MessageListener getMessageListener() {
		return messageListener;
	}

	/**
	 * 设置整体面板的引用到action中
	 * @param messageListener
	 */
	public void setMessageListener(final MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	/**
	 * 默认是返回当前控件的值，但是这个值有可能用来和数据库中的数据进行比较，如果相等则不用设置到数据库中，
	 * 所以遇到控件中的值和数据库中的值不同的时候，则需要子类复写这个方法，把这个值和数据中的值相对应上，
	 * 如date中返回的值是Java的date对象，而数据库中的日期格式则在标签中定义了，所以需要在date的子类中
	 * 进行转化。
	 * @return
	 */
	public Object getValue() {
		return getTagComponent().getValue();
	}
	
	public void setWdemsComponent(final WdemsComponent wc){
		this.wc = wc;
		this.xpath = wc.getTagXPath();
		this.tagObject = wc.getTagObject();
		this.tagComponent = wc.getWdemsTagComponent();
	}
	
	public WdemsTagComponent getTagComponent() {
		return tagComponent;
	}
	
	public Object getTagObject() {
		return tagObject;
	}

	public Actions getAction() {
		return this;
	}

	public String getXPath() {
		return xpath;
	}
	
	public WdemsComponent getWdemsComponent() {
		return wc;
	}
	
	//这个还要看具体在设定初始值的时候需要多少额外的细节
//	public abstract void iniValue(Object value);

	
	/**
	 * 这里用多线程会有这样的问题，就是需要保证在各个组件调用自身的serValue方法的时候需要进入swing线程中执行，
	 * 否则边更改内容边绘制会造成死锁，若以后单线程速度有明显的影响的时候，才需要添加多线程部分的内容。
	 * 目前速度上看不出任何优势，先暂时不用多线程。
	 */
	//后台线程的线程池
	public static ExecutorService executor = Executors.newSingleThreadExecutor();
	//多线程下的任务，该多线程目前是在onBlur和OnResult失焦点之后触发
	private class AfterValidationTask implements Callable<Object> {
		public Object call() throws Exception {
			afterValidation();
			return null;
		}
	}

	public static ExecutorService doExecutor = Executors.newSingleThreadExecutor();
	//实际的动作执行方法
	public void actionPerformed(final ActionEvent e) {
		this.actionEvent = e;
		
		//在单独的线程中创建执行线程，确保脱离EDT线程
//		doExecutor.execute(new FutureTask<Object>(new DoActionTask()));
		doAction();
	}
	
	private void doAction(){
		//执行动作
		tagComponent.setActionResult(doAction(actionEvent));
		
		//FIXME 这里还需要再研究，value的含义
//		if (value == null) {
//			return;
//		}
		
		/**
		 * 这里doValidation方法可以返回boolean值，但是实际上，刘晓那头验证返回一个接口，
		 * 该接口中应该有boolean值和一个出错信息。
		 */
		doOnResultValidation();
	}
	
	/**
	 * 这个方法是用来整合做完验证之后的步骤的，因为不止在一个地方进行验证
	 * @param b 验证最终的结果
	 */
	private void afterValidation(){
		//FIXME 这个地方验证之后的流程还需要梳理一下
		//控件中有一个验证器验证不通过，则不能设置属性
		if (getWdemsComponent().getValidateState()) {
			/**
			 * 如果验证成功
			 * 1、编辑控件上显示正确信息
			 * 2、更新值到内存xml中
			 * 3、将要修改的内容放到撤销列表中，这个在updateXML中包括了
			 * 4、更新状态栏
			 * 5、触发关联域
			 */
			
			//都整合在具体的验证器中
//			tagComponent.showValidationState(this);
			
			Object vc = getValue();
			Object vi = WdemsTagUtil.getValue(getXPath());
			
//			System.out.println("set value:" + vc + " value class" + vc.getClass() + " xml data value:" + vi + " data class:" + vi.getClass());
			
			//TODO 以后这里有可能需要各个控件有个变量来记录自己的初始化的值，不通过数据库中的值进行比较
			//目前是以string形式进行比较，为了防止不同类型的数据之间进行比较，对于xml来说都是string类型
			if(!(tagObject instanceof  com.wisii.edit.tag.schema.wdems.Button))
			{
			if (vc == null && vi == null) {
				return;
			}
			if (vc != null && vi == null) {
				//区分这种情况，这种情况是数据库中为null，设置值为有效值
			} else if (vc == null && vi != null){
				//当动作中的值为null且节点中的值不为空的时候，就是把空值设置到有值的节点中
			} else if (vc.toString().equals(vi.toString())) {
				//当设置的值和数据中的值一样的时候，返回
				return;
			}
			}
			if (updateXML()) {
//				setUpdateXMLSucess(true);
//				undoList();
//				updateStateBar("更新数据成功");
				
				//设置关联属性
				fireConnection();
				
				if (isReload()) {
					//调用重排接口
					getMessageListener().refresh();
				}
				
			} else {
//				setUpdateXMLSucess(false);
//				updateStateBar("更新数据失败");
			}
			
		} else {
			
			/**
			 * 如果验证失败
			 * 1、编辑控件上显示错误信息
			 * 2、记录当前出现错误域
			 * 3、更新状态栏显示哪个域出错
			 * 
			 */
			
//			tagComponent.showValidationState(this);
			
			recodeWrongValidation();
			
//			updateStateBar("验证失败");
//			System.out.println("验证失败");
		}
	}
	
	
	/**
	 * 返回当前用户所设置的值，这里仅仅用来获得结果
	 * @param e
	 * @return 用户所设定的值
	 */
	@Action
	public abstract Object doAction(ActionEvent e);
	
	/**
	 * 把设置的值登记到undolist中
	 */
//	public void undoList(){
//		
//	}
	
	/**
	 * 更新内存中的xml数据
	 * @return
	 */
	public abstract boolean updateXML();
	
	/**
	 * 更新状态栏
	 * @param message
	 */
	public void updateStateBar(final String message){
		StatusbarMessageHelper.output(message, null, StatusbarMessageHelper.LEVEL.INFO);
	}
	
	/**
	 * 出发该控件相关联的控件
	 */
	public void fireConnection(){
		Correlation.Instance.doCorrelation(getWdemsComponent());
	}
	
	/**
	 * 记录当前出现错误域
	 */
	public void recodeWrongValidation(){
		
	}
	
	public Boolean getValidationState() {
		return getWdemsComponent().getValidateState(getCurrentValidation());
	}
	
	public Boolean getFinalValidationState() {
		return getWdemsComponent().getValidateState();
	}


	public boolean isReload(){
		
		Object tag = getTagObject();
		
		String methodName = "isIsReload";
		
		Object value = null;
		
		try {
			Method method = tag.getClass().getMethod(methodName, /*(Class<? extends Object>)*/ null);
			value = method.invoke(tag, /*(Class<? extends Object>)*/ null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
//		System.out.println("========" + value);
		
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return true;
			} else {
				return false;
			}
		}
		
		return false;
	}
	
//	protected boolean isUpdateXMLSucess() {
//		return updateXMLSucess;
//	}
//
//	protected void setUpdateXMLSucess(final boolean updateXMLSucess) {
//		this.updateXMLSucess = updateXMLSucess;
//	}
	
	public Object getRightIco() {
		return null;
	}

	public Object getWrongIco() {
		return null;
	}
	
	@Validation
	public boolean doOnBlurValidation() {
		boolean b = WdemsTagUtil.doValidation(ValidationType.onBlur, this); 
		tagComponent.showValidationState(this);
		afterValidation();
//		executor.execute(new FutureTask<Object>(new AfterValidationTask()));
		return b;
	}

	@Validation
	public boolean doOnEditValidation() {
		return WdemsTagUtil.doValidation(ValidationType.onEdit, this);
	}

	@Validation
	public boolean doOnResultValidation(){
		boolean b = WdemsTagUtil.doValidation(ValidationType.onResult, this);
//		System.err.println(b);
		afterValidation();
//		executor.execute(new FutureTask<Object>(new AfterValidationTask()));
		return b;
	}
	
	/**
	 * 设置属性的验证信息
	 * @param vType 验证类型
	 * @param state 是否验证通过
	 */
	public void setComponentValidationState(final ValidationType vType, final boolean state){
		getWdemsComponent().setValidateState(vType, state);
	}
	
	public String getWrongMessage() {
		return wrongMessage;
	}

	public void setWrongMessage(final String wrongMessage) {
		this.wrongMessage = wrongMessage;
	}
	
	public ValidationType getCurrentValidation() {
		return currentValidation;
	}


	public void setCurrentValidation(final ValidationType currentValidation) {
		this.currentValidation = currentValidation;
	}
	
	/****************下面的方法是测试用*****************/
	/**
	 * 下面的两个方法是用注释写的模板方法
	 */
	private void annotationConf(){
		for (Method m : getClass().getDeclaredMethods()) {
			Annotation[] ann = m.getAnnotations();
			for (Annotation annotation : ann) {
				if (annotation instanceof Validation) {
					Validation v = (Validation) annotation;
					System.err.println("id: " + v.id() + 
							" value: " + v.value() + " : ");
					this.invokeMethod(m);
				}
				
				if (annotation instanceof Action) {
					Action action = (Action) annotation;
					this.invokeMethod(m);
				}
			}
		}
	}
	
	private void invokeMethod(final Method m){
		try {
			m.invoke(this, null);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

}
