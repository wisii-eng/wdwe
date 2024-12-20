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
 * Balloontip - Balloon tips for Java Swing applications
 * Copyright 2007, 2008 Bernhard Pauler, Tim Molderez
 * 
 * This file is part of Balloontip.
 * 
 * Balloontip is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Balloontip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Balloontip.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wisii.edit.tag.components.balloontip;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.wisii.edit.tag.components.balloontip.positioners.BalloonTipPositioner;
import com.wisii.edit.tag.components.balloontip.positioners.BasicBalloonTipPositioner;
import com.wisii.edit.tag.components.balloontip.positioners.Left_Above_Positioner;
import com.wisii.edit.tag.components.balloontip.positioners.Left_Below_Positioner;
import com.wisii.edit.tag.components.balloontip.positioners.Right_Above_Positioner;
import com.wisii.edit.tag.components.balloontip.positioners.Right_Below_Positioner;
import com.wisii.edit.tag.components.balloontip.styles.BalloonTipStyle;
import com.wisii.edit.tag.components.balloontip.styles.RoundedBalloonStyle;



/**
 * A balloon tip which can be attached to about any JComponent
 * @author Bernhard Pauler
 */
public class BalloonTip extends JPanel {
	/**
	 * Should the balloon be placed above, below, right or left of the attached component?
	 */
	public enum Orientation {LEFT_ABOVE, RIGHT_ABOVE, LEFT_BELOW, RIGHT_BELOW};
	/**
	 * Where should the balloon's tip be located, relative to the attached component
	 * ALIGNED makes sure the balloon's edge is aligned with the attached component
	 */
	public enum AttachLocation {ALIGNED, CENTER, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST};

	private static Icon defaultIcon  = new ImageIcon(BalloonTip.class.getResource("/com/wisii/edit/tag/components/balloontip/images/close_default.png"));
	private static Icon rolloverIcon = new ImageIcon(BalloonTip.class.getResource("/com/wisii/edit/tag/components/balloontip/images/close_rollover.png"));
	private static Icon pressedIcon  = new ImageIcon(BalloonTip.class.getResource("/com/wisii/edit/tag/components/balloontip/images/close_pressed.png"));

	private final JLabel label = new JLabel();
	private JButton closeButton = null;
	private boolean isVisible = true;
	private boolean clickToClose = false;
	private boolean clickToHide = false;
	
	private final ComponentAdapter attachedComponentListener = new ComponentAdapter() {
		@Override
		public void componentMoved(final ComponentEvent e) {refreshLocation();}
		@Override
		public void componentResized(final ComponentEvent e) {refreshLocation();}
		@Override
		public void componentShown(final ComponentEvent e) {checkVisibility();}
		@Override
		public void componentHidden(final ComponentEvent e) {checkVisibility();}
	};
	private final ComponentAdapter topLevelContainerListener = new ComponentAdapter() {
		@Override
		public void componentResized(final ComponentEvent e) {
			if (attachedComponent.isShowing()) {
				refreshLocation();
			}
		}
	};
	private final MouseAdapter clickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(final MouseEvent e) {e.consume();}
		@Override
		public void mouseReleased(final MouseEvent e) {
			if (clickToHide) {
				setVisible(false);
			} else if (clickToClose) {
				closeBalloon();
			}
		}
	};
	private AncestorListener attachedComponentParentListener = null;

	protected BalloonTipStyle style;			// Determines the balloon's looks
	protected BalloonTipPositioner positioner;	// Determines the balloon's position
	
	protected JLayeredPane topLevelContainer = null;	// The balloon is drawn on this pane
	protected JComponent attachedComponent;		// The balloon is attached to this component

	/**
	 * Constructor
	 * A simple constructor, the balloon tip will get a default look
	 * @param attachedComponent		Attach the balloon tip to this component
	 * @param text					The contents of the balloon tip (may contain HTML)
	 */
	public BalloonTip(final JComponent attachedComponent, final String text) {
		this(attachedComponent, text, new RoundedBalloonStyle(5,5,Color.WHITE, Color.BLACK), true);
	}

	/**
	 * Constructor
	 * @param attachedComponent		Attach the balloon tip to this component
	 * @param text					The contents of the balloon tip (may contain HTML)
	 * @param style					The balloon tip's looks
	 * @param useCloseButton		If true, the balloon tip gets a close button
	 */
	public BalloonTip(final JComponent attachedComponent, final String text, final BalloonTipStyle style, final boolean useCloseButton) {
		this(attachedComponent, text, style, Orientation.LEFT_ABOVE, AttachLocation.ALIGNED, 16, 20, useCloseButton);
	}

	/**
	 * Constructor
	 * @param attachedComponent		Attach the balloon tip to this componen
	 * @param text					The contents of the balloon tip (may contain HTML)
	 * @param style					The balloon tip's looks
	 * @param orientation			Orientation of the balloon tip
	 * @param attachLocation		Location of the balloon's tip  within the attached component
	 * @param horizontalOffset		Horizontal offset for the balloon's tip
	 * @param verticalOffset		Vertical offset for the balloon's tip
	 * @param useCloseButton		If true, the balloon tip gets a close button
	 */
	public BalloonTip(final JComponent attachedComponent, final String text, final BalloonTipStyle style, final Orientation orientation, final AttachLocation attachLocation, 
			final int horizontalOffset, final int verticalOffset, final boolean useCloseButton) {
		// Setup the appropriate positioner
		BasicBalloonTipPositioner positioner = null;
		float attachX = 0.0f;
		float attachY = 0.0f;
		boolean fixedAttachLocation = true;
		
		switch (attachLocation) {
		case ALIGNED:
			fixedAttachLocation = false;
			break;
		case CENTER:
			attachX = 0.5f;
			attachY = 0.5f;
			break;
		case NORTH:
			attachX = 0.5f;
			break;
		case NORTHEAST:
			attachX = 1.0f;
			break;
		case EAST:
			attachX = 1.0f;
			attachY = 0.5f;
			break;
		case SOUTHEAST:
			attachX = 1.0f;
			attachY = 1.0f;
			break;
		case SOUTH:
			attachX = 0.5f;
			attachY = 1.0f;
			break;
		case SOUTHWEST:
			attachY = 1.0f;
			break;
		case WEST:
			attachY = 0.5f;
			break;
		case NORTHWEST:
			break;
		}
		
		switch (orientation) {
		case LEFT_ABOVE:
			positioner = new Left_Above_Positioner(horizontalOffset, verticalOffset);
			break;
		case LEFT_BELOW:
			positioner = new Left_Below_Positioner(horizontalOffset, verticalOffset);
			break;
		case RIGHT_ABOVE:
			positioner = new Right_Above_Positioner(horizontalOffset, verticalOffset);
			break;
		case RIGHT_BELOW:
			positioner = new Right_Below_Positioner(horizontalOffset, verticalOffset);
			break;
		}
		
		positioner.enableFixedAttachLocation(fixedAttachLocation);
		positioner.setAttachLocation(attachX, attachY);
		
		initializePhase1(attachedComponent, text, style, positioner, useCloseButton);
	}
	
	/**
	 * Constructor
	 * @param attachedComponent		Attach the balloon tip to this component
	 * @param text					The contents of the balloon tip (may contain HTML)
	 * @param style					The balloon tip's looks
	 * @param positioner			Determines the way the balloon tip is positioned
	 * @param useCloseButton		If true, the balloon tip gets a close button
	 */
	public BalloonTip(final JComponent attachedComponent, final String text, final BalloonTipStyle style, final BalloonTipPositioner positioner, final boolean useCloseButton) {
		initializePhase1(attachedComponent, text, style, positioner, useCloseButton);
	}
	
	@Override
	public void finalize() {
		closeBalloon(); // This will remove all of the listeners a balloon tip uses...
	}

	/**
	 * Set the text message that should appear in the balloon tip.
	 * HTML formatting is supported.
	 * @param text
	 */
	public void setText(final String text) {
		label.setText(text);
		refreshLocation();
	}

	/**
	 * Get the text message that appears in the balloon tip.
	 * @return The text shown in the balloon tip
	 */
	public String getText() {
		return label.getText();
	}

	/**
	 * Set the icon that should appear at the left side of the balloon tip
	 * @param icon		The icon (If it has a null-value, the balloon will not have an icon...)
	 */
	public void setIcon(final Icon icon) {
		label.setIcon(icon);
		refreshLocation();
	}

	/**
	 * Get the icon that appears at the left side of the balloon tip
	 * (Returns null if there is no such icon)
	 * @return The balloon tip's icon
	 */
	public Icon getIcon() {
		return label.getIcon();
	}

	/**
	 * Sets the distance (in px) between the icon and the text label 
	 * @param iconTextGap
	 */
	public void setIconTextGap(final int iconTextGap) {
		label.setIconTextGap(iconTextGap);
		refreshLocation();
	}

	/**
	 * Get the distance (in px) between the icon and the text label 
	 * @return The distance between the balloon tip's icon and its text
	 */
	public int getIconTextGap() {
		return label.getIconTextGap();
	}
	
	/**
	 * Set the balloon tip's style
	 * @param style
	 */
	public void setStyle(final BalloonTipStyle style) {
		// Notify property listeners that the style has changed
		firePropertyChange("style", this.style, style);
		this.style = style;
		setBorder(this.style);
		refreshLocation();
	}
	
	/**
	 * Get the balloon tip's style
	 * @return The balloon tip's style
	 */
	public BalloonTipStyle getStyle() {
		return style;
	}

	/**
	 * Set a new BalloonTipPositioner
	 * @param positioner
	 */
	public void setPositioner(final BalloonTipPositioner positioner) {
		// Notify property listeners that the positioner has changed
		firePropertyChange("positioner", this.positioner, positioner);
		
		this.positioner = positioner;
		this.positioner.setBalloonTip(this);
		refreshLocation();
	}
	
	/**
	 * Retrieve the BalloonTipPositioner that is used by this BalloonTip
	 * @return The balloon tip's positioner
	 */
	public BalloonTipPositioner getPositioner() {
		return positioner;
	}

	/**
	 * Set the amount of padding in this balloon tip
	 * @param padding
	 */
	public void setPadding(final int padding) {
		label.setBorder(new EmptyBorder(padding, padding, padding, padding));
		refreshLocation();
	}
	
	/**
	 * Get the amount of padding in this balloon tip
	 * @return The amount of padding in the balloon tip
	 */
	public int getPadding() {
		return label.getBorder().getBorderInsets(this).left;
	}
	
	/**
	 * Hide the balloon tip just by clicking anywhere on it
	 * @param enabled	if true, the balloon hides when it's clicked 
	 */
	public void enableClickToHide(final boolean enabled) {
		clickToHide = enabled;
	}
	
	/**
	 * Permanently close the balloon tip just by clicking anywhere on it
	 * @param enabled	if true, the balloon permanently closes when it's clicked 
	 */
	public void enableClickToClose(final boolean enabled) {
		clickToClose = enabled;
	}

	/**
	 * If you want to permanently close the balloon, use this method.
	 * (If you just need to hide the balloon, use setVisible(false);)
	 * You cannot use this BalloonTip-instance anymore after calling this method!
	 */
	public void closeBalloon() {
		setVisible(false);
		attachedComponent.removeComponentListener(attachedComponentListener);
		if (topLevelContainer != null) {
			topLevelContainer.remove(this);
			topLevelContainer.removeComponentListener(attachedComponentListener);
		}
		removeMouseListener(clickListener);
	}

	/**
	 * Set the close-button icons for all balloon tips 
	 * @param normal
	 * @param pressed
	 * @param rollover		If you don't want a rollover, just set this to null...
	 */
	public static void setCloseButtonIcons(final Icon normal, final Icon pressed, final Icon rollover) {
		defaultIcon  = normal;
		rolloverIcon = rollover;
		pressedIcon  = pressed;
	}

	/**
	 * Sets the border of the balloon tip's close button.
	 * If no close button is used, nothing will happen.
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	public void setCloseButtonBorder(final int top, final int left, final int bottom, final int right) {
		if (closeButton != null) {
			closeButton.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
			refreshLocation();
		}
	}

	/**
	 * Replace the close button's behaviour
	 * (The default behaviour is to permanently close the balloon...)
	 * with your own action listener
	 * @param act
	 */
	public void setCloseButtonActionListener(final ActionListener act) {
		// We assume there's one listener present...
		ActionListener[] listeners = closeButton.getActionListeners();
		closeButton.removeActionListener(listeners[0]);
		closeButton.addActionListener(act);
	}
	
	/**
	 * Retrieve the component this balloon tip is attached to
	 * @return The attached component
	 */
	public JComponent getAttachedComponent() {
		return attachedComponent;
	}
	
	/**
	 * Retrieve the container this balloon tip is drawn on
	 * If the balloon tip hasn't determined this container yet, null is returned
	 * @return The balloon tip's top level container
	 */
	public JLayeredPane getTopLevelContainer() {
		return topLevelContainer;
	}

	/**
	 * Redetermines and sets the balloon tip's location
	 */
	public void refreshLocation() {
		Point location = SwingUtilities.convertPoint(attachedComponent, getLocation(), this);
		try {
			positioner.determineAndSetLocation(new Rectangle(location.x, location.y, attachedComponent.getWidth(), attachedComponent.getHeight()));
		} catch (NullPointerException exc) {}
	}
	
	@Override
	public void setVisible(final boolean visible) {
		isVisible = visible;
		super.setVisible(visible);
	}

	/*
	 * Shows the balloon if the attached component is visible; hides the balloon if the attached component is invisible...
	 */
	private void checkVisibility() {
		// If we can see the attached component, the balloon tip is not closed AND we want it to be visible, then show it...
		if (attachedComponent.isShowing() && isVisible) {
			refreshLocation();
			super.setVisible(true);
		} else {
			super.setVisible(false);
		}
	}
	
	/*
	 * Helper method for constructing a BalloonTip
	 */
	private void initializePhase1(final JComponent attachedComponent, final String text, final BalloonTipStyle style, final BalloonTipPositioner positioner, final boolean useCloseButton) {
		this.attachedComponent = attachedComponent;
		this.style = style;
		this.positioner = positioner;

		setBorder(this.style);
		setOpaque(false);
		setLayout(new GridBagLayout());

		label.setBorder(new EmptyBorder(5, 5, 5, 5));
		label.setText(text);
		add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		if (useCloseButton) {
			closeButton = new JButton();
			closeButton.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
			closeButton.setContentAreaFilled(false);
			closeButton.setIcon(defaultIcon);
			closeButton.setRolloverIcon(rolloverIcon);
			closeButton.setPressedIcon(pressedIcon);
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					closeBalloon();
				}
			});
			add(closeButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		
		// Attempt to run initializePhase2() ...
		try {
			initializePhase2();
		} catch (NullPointerException exc) {
			/* If we failed to determine the top level container, it's because attachedComponent.getParent() returned null...
			 * We'll just have to wait until the parent is set and try again...
			 */
			attachedComponentParentListener = new AncestorListener() {
				public void ancestorAdded(final AncestorEvent event) {
					initializePhase2();
					// Remove yourself
					attachedComponent.removeAncestorListener(attachedComponentParentListener);
					refreshLocation();
					repaint();
				}
				public void ancestorMoved(final AncestorEvent event) {}
				public void ancestorRemoved(final AncestorEvent event) {}
			};
			attachedComponent.addAncestorListener(attachedComponentParentListener);
		}
	}
	
	/*
	 * Helper method for constructing a BalloonTip; when this method finishes, the balloon tip is ready for use
	 * 
	 * The main task here is to attempt to determine the top level container, which is where the balloon tip is drawn.
	 * (This is done by following the path of parent Components, starting at the attached component...)
	 */
	private void initializePhase2() {
		Container parent = attachedComponent.getParent();
		// Follow the path of parents of the attached component until you find the top level container
		while (true) {
			// If you're a top level container (JFrame, JDialog, JInternalFrame, JApplet or JWindow)
			if (parent instanceof RootPaneContainer) {
				topLevelContainer = ((RootPaneContainer)parent).getLayeredPane();
				// Exit the infinite loop
				break;
			// If you're a tab
			} else if (parent instanceof JTabbedPane) {
				/* Due to a bug in JTabbedPane, switching tabs does not cause component events
				 * that tell which components are now visible / invisible.
				 * This piece of code is a workaround. We'll check our attached component's visibility by listening to the JTabbedPane...
				 */
				((JTabbedPane)parent).addChangeListener(new ChangeListener() {
					public void stateChanged(final ChangeEvent e) {
						checkVisibility();
					}
				});
			}
			parent = parent.getParent();
		}
		
		// We use the popup layer of the top level container (frame or dialog) to show the balloon tip
		topLevelContainer.add(this, JLayeredPane.POPUP_LAYER);
		// If the attached component is moved/hidden/shown, the balloon tip should act accordingly
		attachedComponent.addComponentListener(attachedComponentListener);
		// If the window is resized, we should check if the balloon still fits
		topLevelContainer.addComponentListener(topLevelContainerListener);
		// Don't allow to click 'through' the component; will also enable to close the balloon when it's clicked
		addMouseListener(clickListener);
		// Finally pass the balloon tip to its positioner
		this.positioner.setBalloonTip(this);
		
		refreshLocation();
	}
}