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
 */package com.wisii.edit.tag;

import java.awt.Dimension;
import java.awt.Point;

import com.wisii.edit.tag.action.WdemsAction;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.util.WdemsTagUtil.ValidationType;

/**
 * 这里是最终产生的标签对象的总接口
 * 最终AreaTree需要针对这个进行操作
 * @author 闫舒寰
 * @version 1.0 2009/06/10
 *
 */
public interface WdemsComponent {
	
	//目前认可接口。。。
	/**
	 * 获得当前控件的实际id
	 * @return
	 */
	public WdemsTagID getWdemsTagID();
	
	/**
	 * 获得当前控件的解析后的标签对象
	 * @return
	 */
	public Object getTagObject();
	
	
	/**
	 * 获得该控件所对应的xpath
	 * @return
	 */
	public String getTagXPath();
	
	/**
	 * 获得标签名称
	 * @return
	 */
	public String getTagName();
	
	/**
	 * 获得该控件在fo中的id属性值
	 * 注：该属性和tagName组合使用可以确定唯一的控件，因为id在整个fo中是唯一的，这里假设每个id中的名字也是唯一的
	 * @return 返回控件在fo中id的属性值
	 */
	public String getTagID();
	
	/**
	 * 保存控件在fo中id的属性值
	 * @param tagID
	 */
	public void setTagID(String tagID);
	
	/**
	 * 把关联属性设置到控件上
	 * @param conn
	 */
	public void setConnWith(String conn);
	
	/**
	 * 获得该控件的关联属性值
	 * @return
	 */
	public String getConnWith();
	
	//结束
	
	/**
	 * 设置Wdems的实体对象对象
	 * @param wtc
	 */
	public void setWdemsTagComponent(WdemsTagComponent wtc);
	
	/**
	 * 获取WdemsTagComponent的实体对象
	 * @return WdemsTagComponent的实体对象
	 */
	public WdemsTagComponent getWdemsTagComponent();
	
	/**
	 * 设置控件动作
	 * @param wdemsAction
	 */
	public void setWdemsAction(WdemsAction wdemsAction);
	
	/**
	 * 获得控件动作
	 * @return
	 */
	public WdemsAction getWdemsAction();
	
	/**
	 * 获得该控件的整体验证状态信息，总体上该控件有没有通过验证
	 * @return
	 */
	public boolean getValidateState();
	
	/**
	 * 获得指定验证类型的验证状态
	 * @param vType
	 * @return
	 */
	public Boolean getValidateState(ValidationType vType);
	
	/**
	 * 根据验证类型，设置该控件的验证状态信息
	 * @param validationType 该验证的验证类型
	 * @param state 验证是否通过
	 */
	public void setValidateState(ValidationType validationType, boolean state);
	
	/**
	 * 设置回控件的初始值
	 */
	public void setBackIniValue();
	
	/**
	 * input域需要设置字体、字号、颜色…………
	 * 段落信息
	 */
	
	/**
	 * 获得实际的Swing控件
	 * @return
	 */
//	public JComponent getWdemsComponent();
	
	
	/******************有关控件样式设置的方法****开始*************/
	/**
	 * 设置控件的大小
	 * @param maximumSize
	 */
	public void setMaximumSize(Dimension maximumSize);
	
//	public void setContent(String content);
	
	
	/**
	 * 设置控件的位置
	 * @param maximumSize
	 */
	public void setLocation(Point p);
	
	/******************有关控件样式设置的方法****结束*************/
	
	/**
	 * 设置该控件的xpath
	 * @param xpath
	 */
//	public void setXPath(String xpath);
	
}
