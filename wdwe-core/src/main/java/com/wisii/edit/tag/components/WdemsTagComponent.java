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
 */package com.wisii.edit.tag.components;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;


/**
 * 所有Wdems的实体对象对象都需要实现的接口
 * 
 * 这个接口包含了一个实体的component控件，该控件由tagObject生成
 * 
 * 1、areaTree间接通过getComponet()方法获得可以放到panel上的控件
 * 
 * 2、action直接通过addActions方法为控件添加监听器
 * 3、action直接通过getValue方法获得用户通过控件设置的值
 * 4、action通过iniValue方法更新控件的值
 * 5、action通过wrongValidation方法让控件对验证未通过的值作出反应
 * 
 * 以后需要添加的：
 * 1、把areaTree中读取过来的样式和段落属性设置到component上。
 * 
 * @author 闫舒寰
 * @version 1.0 2009/06/11
 */
public interface WdemsTagComponent {
	
	/**
	 * 获得当前控件的JComponent形式
	 * @return
	 */
	public JComponent getComponent();
	
	
	/*****************为Action所加方法*********开始*************/
	/**
	 * 添加监听器
	 * @param action
	 */
	public void addActions(Actions action);
	
	/**
	 * 验证的时候控件所对应的反应
	 * @param vAction TODO
	 */
	public void showValidationState(ValidationMessage vAction);
	
	/**
	 * 获得当前更改的值
	 * @return
	 */
	public Object getValue();
	
	/**
	 * 获得当前选中，且属于指定列索引的值。
	 * 对于下拉的控件来说，大部分数据是来自表。
	 * 1、先数据组织为DataTable→DataRow→DataCell形式。
	 * 2、
	 * @param indexes	指定列索引
	 * @return	{@link Collection}	返回获得到的数据
	 */
	/*public Collection<DataRow> getValues(int...indexes);*/
	
	/**
	 * 设置控件的初始化值而<b>不激发控件的动作</b>
	 * @param value
	 */
	public void iniValue(Object value);
	
	/**
	 * 设置控件的值而激发动作
	 * @param value
	 */
	public void setValue(Object value);
	
	/*****************为Action所加方法**********结束************/
	
	
	/*****************为样式所加方法**********开始************/
	
	public void setLocation(Point p);
	
	public void setMaximumSize(Dimension maximumSize);
	
	/*****************为样式所加方法**********结束************/
	
	/**得到Action时间的结果，只有Commonbutton可能用到*/
	public Object getActionResult();
	/**设置Action时间的结果，只有Commonbutton可能用到*/
	public void setActionResult(Object result);
	/*
	 * 设置控件的默认值,具有默认值的控件，没有值时，可通过赋默认值按钮，快捷的将默认值输入进行
	 */
	public void setDefaultValue(String value);
	/*
	 * 是否能初始化默认值，当控件为空时，且具有默认值时，可初始化
	 */
	public boolean canInitDefaultValue();
	/*
	 * 用默认值初始化控件
	 */
	public void initByDefaultValue();
}
