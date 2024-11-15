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
package com.wisii.edit.tag.components.decorative;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.wisii.component.startUp.SystemUtil;

/***
 * 
 * @author 钟亚军
 * 
 */
@SuppressWarnings("serial")
public class EditPanel extends JPopupMenu implements MouseListener
{

	JComponent compont;

	FloatPanel panel;

	public static int horizontalgrap = 10;

	public static int verticalgrap = 5;

	List<VirtualButton> buttons;

	Border oldborder;

	Image imagecl = new ImageIcon(SystemUtil.getImagesPath("leftborder.gif"))
			.getImage();

	Image imagecr = new ImageIcon(SystemUtil.getImagesPath("rightborder.gif"))
			.getImage();

	Image imagectl = new ImageIcon(SystemUtil.getImagesPath("leftbottom.gif"))
			.getImage();

	Image imagectc = new ImageIcon(SystemUtil.getImagesPath("bottomborder.gif"))
			.getImage();

	Image imagectr = new ImageIcon(SystemUtil.getImagesPath("rightbottom.gif"))
			.getImage();

	Image imagepl = new ImageIcon(SystemUtil.getImagesPath("leftborder.gif"))
			.getImage();

	Image imagepr = new ImageIcon(SystemUtil.getImagesPath("rightborder.gif"))
			.getImage();

	Image imageptl = new ImageIcon(SystemUtil.getImagesPath("lefttop.gif"))
			.getImage();

	Image imageptr = new ImageIcon(SystemUtil.getImagesPath("righttop.gif"))
			.getImage();

	Image imageptc = new ImageIcon(SystemUtil.getImagesPath("topborder.gif"))
			.getImage();

	Image[] borderImg =
	{ imagecl, imagectl, imagectc, imagectr, imagecr };

	CompomtBorder border = new CompomtBorder(borderImg);

	// Border border = new LineBorder(Color.RED);

	Image[] panelborderImg =
	{ imageptl, imagepl, imageptc, imageptr, imagepr };

	PanelBorder panelborder = new PanelBorder(panelborderImg);

	// Border panelborder = new LineBorder(Color.RED);

	public EditPanel()
	{
		super();
	}

	public EditPanel(JComponent textfiled)
	{
		super();
		compont = textfiled;
		oldborder = textfiled.getBorder();
//		System.out.println("in before:" + compont.getWidth());
		compont.addMouseListener(new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
				int width = compont.getWidth();
				if (panel == null)
				{
					compont.setBorder(border);
					// Insets inset = border.getBorderInsets(e.getComponent());
					EditPanel.this.setPreferredSize(new Dimension(width,
							16 + verticalgrap));
					panel = new FloatPanel();
					EditPanel.this.add(panel);
					EditPanel.this.show(compont, 0, -(16 + verticalgrap));
					EditPanel.this.addMouseListener(EditPanel.this);
				} else
				{
					compont.setBorder(border);
					EditPanel.this.add(panel);
					EditPanel.this.show(compont, 0, -(16 + verticalgrap));
				}
				setPanelsVisible(true);
			}

			public void mouseExited(MouseEvent e)
			{
				int width = compont.getWidth();
				int height = compont.getHeight();
				Integer[] lengths = EditPanel.getWidthAndHeight(buttons, width,
						height);
				Point startpoint = compont.getLocationOnScreen();
				Point current = e.getPoint();
				SwingUtilities.convertPointToScreen(current, e.getComponent());

				if (!EditPanel.isInside(startpoint, current, lengths[0],
						lengths[1]))
				{
					setPanelsVisible(false);
				}
			}

			public void mousePressed(MouseEvent e)
			{
				setPanelsVisible(true);
				compont.requestFocus();
			}

			public void mouseReleased(MouseEvent e)
			{
			}
		});
	}

	public EditPanel(JComponent textfiled, List<VirtualButton> buttonlist)
	{
		super();
		compont = textfiled;
		oldborder = textfiled.getBorder();
		buttons = buttonlist;
		compont.addMouseListener(new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
				compont.setBorder(border);
				int width = compont.getWidth();
				int height = compont.getHeight();
				Integer[] lengths = EditPanel.getWidthAndHeight(buttons, width,
						height);
				if (panel == null)
				{

					// Insets inset = border.getBorderInsets(e.getComponent());

					EditPanel.this.setPreferredSize(new Dimension(lengths[0],
							lengths[1]));
					panel = new FloatPanel(compont, buttons);
					EditPanel.this.add(panel);
					EditPanel.this.show(compont, 0, -lengths[1]);
					EditPanel.this.addMouseListener(EditPanel.this);
				} else
				{
					EditPanel.this.add(panel);
					EditPanel.this.show(compont, 0, -lengths[1]);
				}
				setPanelsVisible(true);
			}

			public void mouseExited(MouseEvent e)
			{
				int width = compont.getWidth();
				int height = compont.getHeight();

				Integer[] lengths = EditPanel.getWidthAndHeight(buttons, width,
						height);
				Point startpoint = compont.getLocationOnScreen();
				Point current = e.getPoint();
				SwingUtilities.convertPointToScreen(current, e.getComponent());
				if (!EditPanel.isInside(startpoint, current, lengths[0],
						lengths[1]))
				{
					setPanelsVisible(false);
				}
			}

			public void mousePressed(MouseEvent e)
			{
				setPanelsVisible(true);
				compont.requestFocus();
			}

			public void mouseReleased(MouseEvent e)
			{
			}
		});
	}

	public static boolean isInside(Point start, Point current, int width,
			int height)
	{
		int startx = start.x;
		int starty = start.y;
		int currentx = current.x;
		int currenty = current.y;
		return currentx > startx && currentx < (startx + width)
				&& currenty <= starty && currenty > (starty - height);
	}

	public static Integer[] getWidthAndHeight(List<VirtualButton> buttonlist,
			int uiwidth, int uiheight)
	{
		int width = uiwidth;
		int height = 16 + verticalgrap;
		int column = 1;
		int realnumber = 1;
		int buttonnumber = buttonlist != null ? buttonlist.size() : 0;
		if (buttonnumber > 0)
		{
			int maxonelinenumber = (uiwidth + horizontalgrap)
					/ (16 + horizontalgrap);
			if (maxonelinenumber > 0)
			{
				realnumber = maxonelinenumber;
				column = buttonnumber % realnumber == 0 ? buttonnumber
						/ realnumber : (buttonnumber / realnumber) + 1;
				height = column * (16 + verticalgrap) + 8;
			}
		}
		Integer[] result =
		{ width, height, realnumber };
		return result;
	}

	public void setPanelsVisible(boolean flg)
	{
		EditPanel.this.setVisible(flg);
		if (flg)
		{
			EditPanel.this.setBorder(panelborder);
		} else
		{
			compont.setBorder(oldborder);
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		setPanelsVisible(true);
	}

	public void mouseEntered(MouseEvent e)
	{
		setPanelsVisible(true);
	}

	public void mouseExited(MouseEvent e)
	{
		if (compont != null && compont.isShowing())
		{
			Point startpoint = compont.getLocationOnScreen();
			Point current = e.getPoint();
			SwingUtilities.convertPointToScreen(current, e.getComponent());
			if (!isInside(startpoint, current, EditPanel.this.getWidth(),
					EditPanel.this.getHeight()))
			{
				setPanelsVisible(false);
			}
		} else
		{
			setPanelsVisible(false);
		}
	}

	public void mousePressed(MouseEvent e)
	{
		setPanelsVisible(true);
	}

	public void mouseReleased(MouseEvent e)
	{
	}

}
