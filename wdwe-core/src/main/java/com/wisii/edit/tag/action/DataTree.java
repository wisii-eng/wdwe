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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


@SuppressWarnings("serial")
public class DataTree extends JTree
{

	JPopupMenu popupmenuAttribute;

	JPopupMenu popupmenuText;

	TreeDSNode currentnode;

	EditXmlNodeDialog parent;

	Point currentpoint;

	JMenuItem editelementname;

	public DataTree(TreeDSModel structuremodel, EditXmlNodeDialog dia)
	{
		parent = dia;
		setModel(structuremodel);
		setDragEnabled(true);
		setTransferHandler(new TransferHandler(""));
		setSize(300, 200);
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener()
				{

					public void valueChanged(TreeSelectionEvent e)
					{
						TreePath path = e.getPath();
						currentnode = (TreeDSNode) path.getLastPathComponent();
					}
				});
		addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent mouseevent)
			{

				if (mouseevent.getButton() == MouseEvent.BUTTON3)
				{
					if (currentnode != null)
					{
						Point current = mouseevent.getPoint();
						currentpoint = new Point(current.x, current.y);
						int type = currentnode.getType();
						if (type == TreeDSNode.ATTRIBUTE)
						{
							if (currentnode != null)
							{
								final JDialog dia = new JDialog(parent, "");
								dia.setSize(new Dimension(260, 120));
								dia.setLayout(null);
								JPanel panel = new JPanel();
								panel.setLayout(null);
								panel.setPreferredSize(new Dimension(260, 120));

								JLabel name = new JLabel("属性名：");
								// name.setPreferredSize(new Dimension(60, 30));
								name.setEnabled(false);
								name.setBounds(0, 0, 60, 30);
								panel.add(name);

								JTextField text = new JTextField();
								// text.setPreferredSize(new Dimension(200,
								// 30));
								text.setText(currentnode.getName());
								text.requestFocus();
								text.addFocusListener(new FocusListener()
								{

									public void focusGained(FocusEvent e)
									{
									}

									public void focusLost(FocusEvent e)
									{
										currentnode.setName(((JTextField) e
												.getSource()).getText());
									}
								});
								text.addKeyListener(new KeyListener()
								{

									public void keyPressed(KeyEvent e)
									{
										int value = e.getKeyCode();
										if (value == 10)
										{
											currentnode.setName(((JTextField) e
													.getSource()).getText());
											dia.dispose();
											DataTree.this.updateUI();
										}
									}

									public void keyReleased(KeyEvent e)
									{
									}

									public void keyTyped(KeyEvent e)
									{
									}
								});
								text.setBounds(60, 0, 200, 30);
								panel.add(text);

								JLabel value = new JLabel("属性值：");
								// value.setPreferredSize(new Dimension(60,
								// 30));
								value.setEnabled(false);
								value.setBounds(0, 40, 60, 30);
								panel.add(value);

								JTextField textvalue = new JTextField();
								// textvalue.setPreferredSize(new Dimension(200,
								// 30));
								textvalue.setText(currentnode.getValue());
								textvalue.addFocusListener(new FocusListener()
								{

									public void focusGained(FocusEvent e)
									{
									}

									public void focusLost(FocusEvent e)
									{
										currentnode.setValue(((JTextField) e
												.getSource()).getText());
									}
								});
								textvalue.addKeyListener(new KeyListener()
								{

									public void keyPressed(KeyEvent e)
									{
										int value = e.getKeyCode();
										if (value == 10)
										{
											currentnode
													.setValue(((JTextField) e
															.getSource())
															.getText());
											dia.dispose();
											DataTree.this.updateUI();
										}
									}

									public void keyReleased(KeyEvent e)
									{
									}

									public void keyTyped(KeyEvent e)
									{
									}
								});
								textvalue.setBounds(60, 40, 200, 30);
								panel.add(textvalue);
								panel.setBounds(0, 0, 260, 120);
								dia.add(panel);
								EditXmlNodeDialog.centerOnScreen(dia);
								dia.setVisible(true);
							}

						} else if (type == TreeDSNode.TEXT)
						{

							if (currentnode != null)
							{
								TreeDSNode parentnode = currentnode.getParent();
								if (parentnode != null)
								{
									final JDialog dia = new JDialog(parent, "");
									dia.setSize(new Dimension(200, 60));
									dia.setLayout(null);
									JPanel panel = new JPanel();
									panel.setLayout(null);
									panel.setPreferredSize(new Dimension(200,
											30));
									JTextField text = new JTextField();
									text
											.setPreferredSize(new Dimension(
													200, 30));
									text.setText(currentnode.getName());
									text.requestFocus();
									text.addKeyListener(new KeyListener()
									{

										public void keyPressed(KeyEvent e)
										{
											int value = e.getKeyCode();
											if (value == 10)
											{
												currentnode
														.setName(((JTextField) e
																.getSource())
																.getText());
												dia.dispose();
												DataTree.this.updateUI();
											}
										}

										public void keyReleased(KeyEvent e)
										{
										}

										public void keyTyped(KeyEvent e)
										{
										}
									});
									text.setBounds(0, 0, 200, 30);
									panel.add(text);
									panel.setBounds(0, 0, 200, 30);
									dia.add(panel);
									EditXmlNodeDialog.centerOnScreen(dia);
									dia.setVisible(true);
								}
							}

						}
					}
				}
			}
		});
	}

	@Override
	public TreeCellRenderer getCellRenderer()
	{
		return new TreeNodeCellRender();
	}

}
