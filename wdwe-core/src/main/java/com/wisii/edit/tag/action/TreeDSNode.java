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
 */
package com.wisii.edit.tag.action;

import java.util.List;

import javax.swing.tree.TreeNode;

public interface TreeDSNode extends TreeNode
{

	// 元素节点
	public static final int ELEMENT = 0;

	// 属性节点
	public static final int ATTRIBUTE = 1;

	// 文本节点
	public static final int TEXT = 2;

	/**
	 * 
	 * 获取节点类型
	 * 
	 * @param
	 * @return
	 * @exception
	 */
	int getType();

	/**
	 * 
	 * 设置节点类型
	 * 
	 * @param
	 * @return
	 * @exception
	 */
	void setType(int type);

	/**
	 * 
	 * 获得所有的属性节点
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	List<TreeDSNode> getAttribute();

	/**
	 * 
	 * 设置属性节点
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	void setAttribute(List<TreeDSNode> value);

	/**
	 * 
	 * 获得节点名称
	 * 
	 * @param
	 * @return
	 * @exception
	 */
	String getName();

	/**
	 * 
	 * 设置元素名
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	void setName(String name);

	/**
	 * 
	 * 获得元素的文本
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	String getValue();

	/**
	 * 
	 * 设置文本
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	void setValue(String value);

	/**
	 * 
	 * 获得元素的文本节点
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	TreeDSNode getText();

	/**
	 * 
	 * 设置文本节点
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	void setText(TreeDSNode value);

	/**
	 * 
	 * 设置子元素
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public void setChildren(List<TreeDSNode> children);

	/**
	 * 
	 * 设置父元素
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public void setParent(TreeDSNode parent);

	/**
	 * 
	 * 获得父元素
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public TreeDSNode getParent();

	/**
	 * 
	 * 添加子元素
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public void addChild(TreeDSNode child);

	/**
	 * 
	 * 获得字符串
	 *
	 * @param      {引入参数名}   {引入参数说明}
	 * @return      {返回参数名}   {返回参数说明}
	 * @exception   {说明在某情况下,将发生什么异常}
	 */
	public String getString();
	/**
	 * 
	 * 清除属性值和文本
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public void clearNode();

	/**
	 * 
	 * 添加属性
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public void addAttributeChild(TreeDSNode child);

	/**
	 * 
	 * 节点克隆
	 * 
	 * @param 引入参数名
	 *            引入参数说明}
	 * @return 返回参数名} {返回参数说明}
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */
	public TreeDSNode clone();

}
