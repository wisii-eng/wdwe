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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;

public class TreeNodeDS implements TreeDSNode
{

	TreeDSNode parent = null;

	String name;

	String value;

	int type;

	List<TreeDSNode> children = null;

	List<TreeDSNode> attributes = null;

	TreeDSNode texts = null;

	public TreeNodeDS(TreeDSNode pd, int datatype, String nodename)
	{
		if (pd != null)
		{
			parent = pd;
		}
		type = datatype;
		name = nodename;
	}

	public TreeNodeDS(TreeDSNode pd, int datatype, String nodename, String value)
	{
		if (pd != null)
		{
			parent = pd;
		}
		type = datatype;
		name = nodename;
		this.value = value;
	}

	public List<TreeDSNode> getAttribute()
	{
		return attributes;
	}

	public String getName()
	{
		return name;
	}

	public TreeDSNode getParent()
	{
		return parent;
	}

	public TreeDSNode getText()
	{
		return texts;
	}

	public int getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}

	public void setAttribute(List<TreeDSNode> value)
	{
		this.attributes = value;
	}

	public void setChildren(List<TreeDSNode> children)
	{
		this.children = children;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setParent(TreeDSNode parent)
	{
		this.parent = parent;
	}

	public void setText(TreeDSNode value)
	{
		this.texts = value;
		value.setParent(this);
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public Enumeration children()
	{
		if (children != null && !children.isEmpty())
		{
			return new Vector<TreeDSNode>(children).elements();
		} else
		{
			return null;
		}
	}

	public boolean getAllowsChildren()
	{
		return true;
	}

	public TreeNode getChildAt(int childIndex)
	{
		return null;
	}

	public void addChild(TreeDSNode child)
	{
		if (children == null)
		{
			children = new ArrayList<TreeDSNode>();
		}
		children.add(child);
		child.setParent(this);
	}

	public void addAttributeChild(TreeDSNode child)
	{
		if (attributes == null)
		{
			attributes = new ArrayList<TreeDSNode>();
		}
		attributes.add(child);
		child.setParent(this);
	}

	public int getChildCount()
	{
		return children != null ? children.size() : 0;
	}

	public int getIndex(TreeNode node)
	{
		return 0;
	}

	public boolean isLeaf()
	{
		return false;
	}

	public String getString()
	{
		String code = "";
		if (name != null && !name.equals(""))
		{
			int type = this.getType();
			if (type == TreeDSNode.ELEMENT)
			{
				code = code + "<";
				code = code + name;
				if (attributes != null && !attributes.isEmpty())
				{
					for (TreeDSNode current : attributes)
					{
						code = code + current.getString();
					}
				}
				code = code + ">";
				if (texts != null)
				{
					code = code + texts.getString();
				}
				if (children != null && !children.isEmpty())
				{
					for (TreeDSNode current : children)
					{
						code = code + current.getString();
					}
				}
				code = code + "</";
				code = code + name;
				code = code + ">";
			} else if (type == TreeDSNode.ATTRIBUTE)
			{
				code = code + " ";
				code = code + name;
				code = code + "\"";
				code = code + value;
				code = code + "\"";
			} else if (type == TreeDSNode.TEXT)
			{
				code = code + name;
			}

		}
		return code;
	}

	public void clearNode()
	{
		if (children != null)
		{
			for (TreeDSNode current : children)
			{
				current.clearNode();
			}
		}
		if (attributes != null)
		{
			for (TreeDSNode current : attributes)
			{
				current.setValue("");
			}
		}
		if (texts != null)
		{
			texts.setName("");
		}
	}

	public TreeDSNode clone()
	{
		String name = new String(this.name);
		TreeNodeDS newnode = new TreeNodeDS(this.parent, this.type, name);
		if (this.value != null)
		{
			String newvalue = new String(this.value);
			newnode.setValue(newvalue);
		}
		if (children != null)
		{
			List<TreeDSNode> list = new ArrayList<TreeDSNode>();
			for (TreeDSNode current : children)
			{
				TreeDSNode clonenode = current.clone();
				list.add(clonenode);
			}
			newnode.setChildren(list);
		}
		if (attributes != null)
		{
			List<TreeDSNode> list = new ArrayList<TreeDSNode>();
			for (TreeDSNode current : attributes)
			{
				TreeDSNode clonenode = current.clone();
				list.add(clonenode);
			}
			newnode.setAttribute(list);
		}
		if (texts != null)
		{
			TreeDSNode list = texts.clone();
			newnode.setText(list);
		}
		return newnode;
	}

}
