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
 * @WdemsTree.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.wisii.edit.tag.components.select.AbstractWdemsCombox.DisplayStyle;

/**
 * 类功能描述：用于创建树，方便统一定义样式。
 * 
 * 作者：李晓光
 * 创建日期：2009-6-16
 */
@SuppressWarnings("serial")
class WdemsTree extends JTree {
	private DisplayStyle type = DisplayStyle.TREE;
	WdemsTree(){
		this(DisplayStyle.TREE, null);
	}
	WdemsTree(DisplayStyle type){
		this(type, null);
	}
	WdemsTree(DisplayStyle type, TreeNode root){
		super(root);
		if((type == DisplayStyle.TABLE || type == DisplayStyle.RADIO_TABLE || type == DisplayStyle.CHECKBOX_TABLE)) {
			type = DisplayStyle.TREE;
		}
		this.type = type;
		initStyles();
	}
	private void initStyles(){
		TreeSelectionModel model = getSelectionModel();
		if(type == DisplayStyle.RADIO_TREE){
			model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			WdemsTableRender<JRadioButton> render = new WdemsTableRender<JRadioButton>(new JRadioButton());
			setCellRenderer(render);
		}else if(type == DisplayStyle.CHECKBOX_TREE){
			model.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			WdemsTableRender<JCheckBox> render = new WdemsTableRender<JCheckBox>(new JCheckBox());
			setCellRenderer(render);
		}else{
			DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
			setCellRenderer(render);			
		}
	}
}
