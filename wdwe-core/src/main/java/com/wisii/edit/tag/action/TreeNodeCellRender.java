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

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.wisii.component.startUp.SystemUtil;

@SuppressWarnings("serial")
public class TreeNodeCellRender extends DefaultTreeCellRenderer
{

	Icon iconElement = new ImageIcon(SystemUtil.getImagesPath("e.gif"));

	Icon iconAttribute = new ImageIcon(SystemUtil.getImagesPath("a.gif"));

	Icon iconText = new ImageIcon(SystemUtil.getImagesPath("t.gif"));

	private JTree tree;

	/**
	 * 初始化过程的描述
	 * 
	 * @param 初始化参数说明
	 * @exception 说明在某情况下
	 *                ,将发生什么异常}
	 */

	public TreeNodeCellRender()
	{

	}

	public Component getTreeCellRendererComponent(JTree jtree, Object obj,
			boolean flag, boolean flag1, boolean flag2, int i, boolean flag3)
	{
		this.tree = jtree;
		if (obj instanceof TreeNodeDS)
		{
			TreeNodeDS node = (TreeNodeDS) obj;
			String name = node.getName();
			int type = node.getType();
			switch (type)
			{
				case TreeDSNode.ELEMENT:
				{
					setLeafIcon(iconElement);
					setOpenIcon(iconElement);
					setClosedIcon(iconElement);
					break;
				}
				case TreeDSNode.ATTRIBUTE:
				{
					setLeafIcon(iconAttribute);
					setOpenIcon(iconAttribute);
					setClosedIcon(iconAttribute);
					break;
				}
				case TreeDSNode.TEXT:
				{
					setLeafIcon(iconText);
					setOpenIcon(iconText);
					setClosedIcon(iconText);
					break;
				}
			}
			String value = "";
			if (type == TreeNodeDS.ATTRIBUTE)
			{
				value = node.getValue();
			}
			String text = name + "  " + value;
			setText(text);
		}
		hasFocus = flag3;
		Color color = null;
//		javax.swing.JTree.DropLocation droplocation = jtree.getDropLocation();
//		if (droplocation != null && droplocation.getChildIndex() == -1
//				&& jtree.getRowForPath(droplocation.getPath()) == i)
//		{
//			Color color1 = UIManager.getColor("Tree.dropCellForeground");
//			if (color1 != null)
//			{
//				color = color1;
//			} else
//			{
//				color = getTextSelectionColor();
//			}
//		} else if (flag)
//		{
//			color = getTextSelectionColor();
//		} else
//		{
//			color = getTextNonSelectionColor();
//		}
		setForeground(color);
		if (!jtree.isEnabled())
		{
			setEnabled(false);
			if (flag2)
			{
				setDisabledIcon(getLeafIcon());
			} else if (flag1)
			{
				setDisabledIcon(getOpenIcon());
			} else
			{
				setDisabledIcon(getClosedIcon());
			}
		} else
		{
			setEnabled(true);
			if (flag2)
			{
				setIcon(getLeafIcon());
			} else if (flag1)
			{
				setIcon(getOpenIcon());
			} else
			{
				setIcon(getClosedIcon());
			}
		}
		setComponentOrientation(jtree.getComponentOrientation());
		selected = flag;
		return this;
	}

	public JTree getTree()
	{
		return tree;
	}

	public void setTree(JTree tree)
	{
		this.tree = tree;
	}

}
