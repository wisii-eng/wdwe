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
 * 
 */
package com.wisii.component.createinitview.openView;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.complex.WdemsTextPane;
import com.wisii.edit.tag.components.decorative.WdemsEditComponentManager;
import com.wisii.edit.tag.components.input.MultiLineInput;
import com.wisii.edit.tag.components.input.WdemsTextArea;
import com.wisii.fov.render.awt.viewer.Command;

/**
 * @author wisii
 *
 */
public class DefaultValueActionDoit extends Command {


	public DefaultValueActionDoit(String name, String iconName) {
		super(name,  iconName);
	}


	/**
	 * 如果没有备份文件就不回滚，如果有备份文件回滚，将回滚前的文件生成回滚文件，将xml写库之后触发重载
	 */
	public void action(ActionEvent e) {
		List<JComponent> comps=WdemsEditComponentManager.getJComponents();
		if(comps==null)
		{
			return;
		}
		for(JComponent comp:comps)
		{
			if(comp instanceof JScrollPane)
			{
				comp=(JComponent) ((JScrollPane)comp).getViewport().getView();
			}
			if(comp instanceof WdemsTagComponent)
			{
				WdemsTagComponent wcomp=(WdemsTagComponent) comp;
				wcomp.initByDefaultValue();
			}
			else if(comp instanceof WdemsTextArea)
			{
				MultiLineInput input=((WdemsTextArea) comp).getMuliline();
				input.initByDefaultValue();
			}
			else if(comp instanceof WdemsTextPane)
			{
				List<JComponent> children=((WdemsTextPane)comp).getJcomps();
				for(JComponent child:children)
				{
					if(child instanceof JScrollPane)
					{
						child=(JComponent) ((JScrollPane)child).getViewport().getView();
					}
					if(child instanceof WdemsTagComponent)
					{
						WdemsTagComponent wcomp=(WdemsTagComponent) child;
						wcomp.initByDefaultValue();
					}
					else if(child instanceof WdemsTextArea)
					{
						MultiLineInput input=((WdemsTextArea) child).getMuliline();
						input.initByDefaultValue();
					}
				}
			}
		}

	}

}
