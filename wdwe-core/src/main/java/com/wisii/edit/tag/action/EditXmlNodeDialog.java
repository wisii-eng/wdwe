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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class EditXmlNodeDialog extends JDialog
{

	DataTree tree;

	TreeDSNode root = null;

	TreeDSModel model = null;

	Element thiselement;

	String resultcode = null;

	int result = 0;

	public EditXmlNodeDialog(Element xmlnode)
	{
		super();
		thiselement = xmlnode;
		this.setTitle("编辑属性和文本");
		this.setSize(new Dimension(800, 600));
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(800, 600));
		JScrollPane treepanel = new JScrollPane();
		treepanel.setSize(new Dimension(800, 500));
		treepanel.setBounds(0, 0, 800, 500);
		root = getTreeNode(xmlnode);
		model = new TreeDSModel(root, true);
		tree = new DataTree(model, EditXmlNodeDialog.this);
		tree.setModel(model);
		treepanel.setViewportView(tree);
		JButton ok = new JButton("确定");
		JButton cancel = new JButton("取消");
		JButton reset = new JButton("重置");
		JButton clear = new JButton("置空");
		panel.add(treepanel);
		ok.setBounds(300, 520, 80, 25);
		reset.setBounds(400, 520, 80, 25);
		clear.setBounds(500, 520, 80, 25);
		cancel.setBounds(600, 520, 80, 25);
		panel.add(ok);
		panel.add(reset);
		panel.add(clear);
		panel.add(cancel);
		ok.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				result = 1;
				String code = root.getString();
				setResult(code);
				EditXmlNodeDialog.this.dispose();
			}
		});
		cancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				EditXmlNodeDialog.this.dispose();
			}
		});
		clear.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				root.clearNode();
				model = new TreeDSModel(root, true);
				tree = new DataTree(model, EditXmlNodeDialog.this);
				tree.setModel(model);
			}
		});
		reset.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				root = getTreeNode(thiselement);
				model = new TreeDSModel(root, true);
				tree = new DataTree(model, EditXmlNodeDialog.this);
				tree.setModel(model);
			}
		});
		this.add(panel);
		centerOnScreen(this);
	}

	public TreeNodeDS getTreeNode(Element element)
	{
		String name = element.getLocalName();
		TreeNodeDS newnode = new TreeNodeDS(null, 0, name);
		NodeList nodelist = element.getChildNodes();
		if (nodelist != null)
		{
			for (int i = 0; i < nodelist.getLength(); i++)
			{
				Node node = nodelist.item(i);
				if (node instanceof Element)
				{
					Element current = (Element) node;
					TreeNodeDS childnode = getTreeNode(current);
					newnode.addChild(childnode);
				}
			}
		}
		NamedNodeMap namemap = element.getAttributes();
		int length = namemap.getLength();
		for (int i = 0; i < length; i++)
		{
			Node current = namemap.item(i);
			TreeNodeDS attributenode = new TreeNodeDS(newnode, 1, current
					.getNodeName());
			attributenode.setValue(current.getNodeValue());
			newnode.addAttributeChild(attributenode);
		}
		String text = element.getTextContent();
		if (text != null)
		{
			TreeNodeDS textnode = new TreeNodeDS(newnode, 2, text);
			newnode.setText(textnode);
		}
		return newnode;
	}

	public static void centerOnScreen(final Component aComponent)
	{
		if (aComponent == null)
		{
			return;
		}
		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		final Dimension compSize = aComponent.getSize();

		compSize.width = Math.min(screenSize.width, compSize.width);
		compSize.height = Math.min(screenSize.height, compSize.height);

		aComponent.setSize(compSize);
		aComponent.setLocation((screenSize.width - compSize.width) / 2,
				(screenSize.height - compSize.height) / 2);
	}

	public static void main(String[] args)
	{
		// EditXmlNodeDialog dia = new EditXmlNodeDialog("as");
		// dia.setVisible(true);
	}

	public int showDialog()
	{
		return result;
	}

	public String getResult()
	{
		return resultcode;
	}

	public void setResult(String result)
	{
		this.resultcode = result;
	}
}
