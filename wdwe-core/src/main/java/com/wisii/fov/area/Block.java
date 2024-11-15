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
 *//* $Id: Block.java,v 1.1 2007/04/12 06:41:18 cvsuser Exp $ */

package com.wisii.fov.area;

import java.util.List;

import com.wisii.fov.fo.FONode;


// block areas hold either more block areas or line
// areas can also be used as a block spacer
// a block area may have children positioned by stacking
// or by relative to the parent for floats, tables and lists
// cacheable object
// has id information

/**
 * This is the block area class.
 * It holds child block areas such as other blocks or lines.
 */
public class Block extends BlockParent {
    /**
     * Normally stacked with other blocks.
     */
    public static final int STACK = 0;

    /**
     * Placed relative to the flow position.
     * This effects the flow placement of stacking normally.
     */
    public static final int RELATIVE = 1;

    /**
     * Relative to the block parent but not effecting the stacking
     * Used for block-container, tables and lists.
     */
    public static final int ABSOLUTE = 2;

    /**
     * Relative to a viewport/page but not effecting the stacking
     * Used for block-container.
     */
    public static final int FIXED = 3;

    private int positioning = STACK;
    /* 【添加：START】 by 李晓光  2009-6-9 */
    private transient FONode source = null;
    public FONode getSource() {
    	return source;
    }
    
    public void setSource(FONode source) {
    	this.source = source;
    }
    public boolean isOverflow(){
    	List<Area> areas = getChildAreas();
    	double sum = 0;
    	if(areas == null || areas.isEmpty())
    		return Boolean.FALSE;
    	for (Area area : areas) {
			sum += area.bpd;//area.getViewport().getHeight();
		}
    	double height = getBPD();//getViewport().getHeight();
    	
    	//大于二是为了消除近似处理，而造成的偏差
    	return (sum - height) > 5000;
    }
    private void printInfo(){
    	List<Area> areas = getChildAreas();
    	int bpd = 0;
    	for (Area area : areas) {
			System.out.println("bpd = " + area.getAllocBPD());
			bpd += area.getAllocBPD();
		}
    	System.out.println("all bpd = " + bpd);
    	System.out.println("this bpd = " + getAllocBPD());
    }
//    private Rectangle2D viewport = null;
//    
//    public Rectangle2D getViewport() {
//    	if(viewport == null)
//    		return viewport;
//    	//放置被修改
//    	return (Rectangle2D)viewport.clone();
//    }
//    
//    public void setViewport(Rectangle2D viewport) {
//    	this.viewport = viewport;
//    }
//    public void clearViewport(){
//    	this.viewport = null;
//    }
    /* 【添加：END】 by 李晓光  2009-6-9 */

    // a block with may contain the dominant styling info in
    // terms of most lines or blocks with info



	/**
     * Add the block to this block area.
     *
     * @param block the block area to add
     */
    @Override
	public void addBlock(Block block) {
        addBlock(block, true);
    }

    /**
     * Add the block to this block area.
     *
     * @param block the block area to add
     * @param autoHeight increase the height of the block.
     */
    public void addBlock(Block block, boolean autoHeight) {
        if (autoHeight) {
            bpd += block.getAllocBPD();
        }
        addChildArea(block);
    }

    /**
     * Add the line area to this block area.
     *
     * @param line the line area to add
     */
    public void addLineArea(LineArea line) {
        bpd += line.getAllocBPD();
        addChildArea(line);
    }

    /**
     * Set the positioning of this area.
     *
     * @param pos the positioning to use when rendering this area
     */
    public void setPositioning(int pos) {
        positioning = pos;
    }

    /**
     * Get the positioning of this area.
     *
     * @return the positioning to use when rendering this area
     */
    public int getPositioning() {
        return positioning;
    }

    /**
     * @return the start-indent trait
     */
    public int getStartIndent() {
        Integer startIndent = (Integer)getTrait(Trait.START_INDENT);
        return (startIndent != null ? startIndent.intValue() : 0);
    }

}

