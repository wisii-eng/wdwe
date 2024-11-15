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
 */package com.wisii.fov.render.awt.viewer.date;

import java.awt.*;
import javax.swing.border.*;

/**
 * <p>Title: OpenSwing</p>
 * <p>Description: TopBottomLineBorder只带上下两条线的边界Border</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
 * @version 1.0
 */
public class TopBottomLineBorder extends AbstractBorder{
    private Color lineColor;
    public TopBottomLineBorder(Color color){
        lineColor = color;
    }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height){
        g.setColor(lineColor);
        g.drawLine(0, 0, c.getWidth(), 0);
        g.drawLine(0, c.getHeight() - 1, c.getWidth(),
                   c.getHeight() - 1);
    }
}
