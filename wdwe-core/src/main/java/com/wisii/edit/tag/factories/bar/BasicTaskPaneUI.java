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
 * @PROJECT.FULLNAME@ @VERSION@ License.
 *
 * Copyright @YEAR@ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wisii.edit.tag.factories.bar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

/**
 * Base implementation of the <code>JTaskPane</code> UI.
 */
public class BasicTaskPaneUI extends TaskPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new BasicTaskPaneUI();
  }
  
  protected JTaskPane taskPane;
  protected boolean useGradient;
  protected Color gradientStart;
  protected Color gradientEnd;

  public void installUI(JComponent c) {
    super.installUI(c);
    taskPane = (JTaskPane)c;
    taskPane.setLayout(new PercentLayout(PercentLayout.VERTICAL, 1));//14→1
    //删除 by 李晓光 2009-6-25
   /* taskPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));*/
    taskPane.setOpaque(true);

    if (taskPane.getBackground() == null
      || taskPane.getBackground() instanceof ColorUIResource) {
      taskPane
        .setBackground(UIManager.getColor("TaskPane.background"));
    }
    
    useGradient = UIManager.getBoolean("TaskPane.useGradient");
    if (useGradient) {
      gradientStart = UIManager
      .getColor("TaskPane.backgroundGradientStart");
      gradientEnd = UIManager
      .getColor("TaskPane.backgroundGradientEnd");
    }
  }

  public void paint(Graphics g, JComponent c) {
    Graphics2D g2d = (Graphics2D)g;
    if (useGradient) {
      Paint old = g2d.getPaint();
      GradientPaint gradient = new GradientPaint(0, 0, gradientStart, 0, c
        .getHeight(), gradientEnd);
      g2d.setPaint(gradient);
      g.fillRect(0, 0, c.getWidth(), c.getHeight());      
      g2d.setPaint(old);
    }
  }

}
