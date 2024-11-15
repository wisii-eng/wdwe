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
 */package com.wisii.edit.tag.factories;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.decorative.WdemsRedoUndoManager;
import com.wisii.edit.tag.components.graphic.GraphicCom;
import com.wisii.edit.tag.components.input.MultiLineInput;
import com.wisii.edit.tag.components.input.PasswordInput;
import com.wisii.edit.tag.components.input.SingleLineInput;
import com.wisii.edit.tag.schema.wdems.Graphic;

/**
 * 该工厂类专门用于构建input标签下的输入域
 * @author 闫舒寰
 * @version 1.0 2009/06/10
 */
public enum GraphicFactory implements TagFactory {
	
	
	Instance;
	

	public WdemsTagComponent makeComponent(final WdemsComponent wc) {
		
//		System.out.println("inside input factory: " + tagObject);
		
		final Object tagObject = wc.getTagObject();
		
		if (tagObject instanceof Graphic) {
			Graphic in = (Graphic) tagObject;
			
			WdemsTagComponent tag = null;
			tag = new GraphicCom(in);
			
			//为控件添加撤销、重做处理机制。
			registerRedoUndo(tag);
			
			return tag;
		} else
			return null;
		
	}
	
	private void registerRedoUndo(WdemsTagComponent tag){
		JComponent comp = tag.getComponent();
		if(!(comp instanceof JTextComponent))
			return;
		JTextComponent text = (JTextComponent)comp;
		WdemsRedoUndoManager.registerComponent(text);
	}
}
