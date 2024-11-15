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
 * @WdemsGroupComponent.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.group;

import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;

/**
 * 类功能描述：支持组的控件，该控件可以加入到WdemsGroup对象中。
 * 
 * 作者：李晓光
 * 创建日期：2009-8-20
 */
@SuppressWarnings("serial")
public class WdemsGroupComponent extends JCheckBox {
	public WdemsGroupComponent(){
		super();
		setModel(new WdemsButtonModel());
//		WdemsActioinHandler.bindActions(this, BindType.Checkbox);
	}
	public static class WdemsButtonModel extends JToggleButton.ToggleButtonModel{
		private SelectGroup compGroup = null;
		@Override
		public void setSelected(boolean b) {
			WdemsGroup group = (WdemsGroup)getCompGroup();
			if (group != null) {
				// use the group model instead
				group.setSelected(this, b);
				b = group.isSelected(this);
			}
			
			/*if (isSelected() == b)
				return;*/
			
			if (isSelected() == b){
				if(group == null){
					return;
				}else if(group.getMaxSelected() == 1){
					return;
				}
			}
			
			if (b) {
				stateMask |= SELECTED;
			} else {
				stateMask &= ~SELECTED;
			}
			
			// Send ChangeEvent
			fireStateChanged();
			
			// Send ItemEvent
			fireItemStateChanged(
					new ItemEvent(this,
							ItemEvent.ITEM_STATE_CHANGED,
							this,
							this.isSelected() ?  ItemEvent.SELECTED : ItemEvent.DESELECTED));
			
		}

		protected SelectGroup getCompGroup() {
			return compGroup;
		}

		protected void setCompGroup(SelectGroup compGroup) {
			this.compGroup = compGroup;
		}
	}
}
