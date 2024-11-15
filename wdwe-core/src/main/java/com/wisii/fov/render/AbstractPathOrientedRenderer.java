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
 *//* $Id: AbstractPathOrientedRenderer.java,v 1.7 2008/01/08 07:29:27 lzy Exp $ */

package com.wisii.fov.render;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.wisii.fov.area.Area;
import com.wisii.fov.area.Block;
import com.wisii.fov.area.BlockViewport;
import com.wisii.fov.area.CTM;
import com.wisii.fov.area.RegionViewport;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.ForeignObject;
import com.wisii.fov.area.inline.InlineArea;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.area.inline.Viewport;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fonts.FontMetrics;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.traits.BorderProps;
/**
 * Abstract base class for renderers like PDF and PostScript where many painting operations
 * follow similar patterns which makes it possible to share some code.
 */
public abstract class AbstractPathOrientedRenderer extends PrintRenderer
{
    /**
     * Handle block traits. The block could be any sort of block with any positioning so this should render the traits
     * such as border and background in its position.
     * @param block the block to render the traits
     */
    @Override
	protected void handleBlockTraits(final Block block)
    {
        int borderPaddingStart = block.getBorderAndPaddingWidthStart();
        int borderPaddingBefore = block.getBorderAndPaddingWidthBefore();

        float startx = currentIPPosition / 1000f;
        float starty = currentBPPosition / 1000f;
        float width = block.getIPD() / 1000f;
        float height = block.getBPD() / 1000f;

        /* using start-indent now
        Integer spaceStart = (Integer) block.getTrait(Trait.SPACE_START);
        if (spaceStart != null) {
            startx += spaceStart.floatValue() / 1000f;
        }*/
        
        startx += block.getStartIndent() / 1000f;
        startx -= block.getBorderAndPaddingWidthStart() / 1000f;

        width += borderPaddingStart / 1000f;
        width += block.getBorderAndPaddingWidthEnd() / 1000f;
        height += borderPaddingBefore / 1000f;
        height += block.getBorderAndPaddingWidthAfter() / 1000f;
        //add by huangzl
//        parentBlockRecX = currentIPPosition + borderPaddingStart;
//        parentBlockRecY = currentBPPosition + borderPaddingBefore;
        //add end.
        /* 【添加：START】 by 李晓光  2009-6-9 目前这里不被执行，由Java2DRender重写了*/        
        int x = currentIPPosition;
        int y = currentBPPosition;
        x += block.getStartIndent();
        x -= block.getBorderAndPaddingWidthStart();
        
        int w = block.getAllocIPD();
        int h = block.getAllocBPD();
        Rectangle2D viewport = new Rectangle2D.Double(x, y, w, h);
        block.setViewport(viewport);
        /*System.err.println("block = " + block.getClass() + "  " + block);
        System.err.println("viewport = " + viewport);*/
        /* 【添加：END】 by 李晓光  2009-6-9 */
        drawBackAndBorders(block, startx, starty, width, height);
    }

    /**
     * Handle the traits for a region
     * This is used to draw the traits for the given page region. (See Sect. 6.4.1.2 of XSL-FO spec.)
     * @param region the RegionViewport whose region is to be drawn
     */
    @Override
	protected void handleRegionTraits(final RegionViewport region)
    {
        Rectangle2D viewArea = region.getViewArea();
        float startx = (float)(viewArea.getX() / 1000f);
        float starty = (float)(viewArea.getY() / 1000f);
        float width = (float)(viewArea.getWidth() / 1000f);
        float height = (float)(viewArea.getHeight() / 1000f);

        if (region.getRegionReference().getRegionClass() == FO_REGION_BODY)
        {
            currentBPPosition = region.getBorderAndPaddingWidthBefore();
            currentIPPosition = region.getBorderAndPaddingWidthStart();
        }
        drawBackAndBorders(region, startx, starty, width, height);
    }

    /**
     * Draw the background and borders.
     * This draws the background and border traits for an area given the position.
     * @param area the area to get the traits from
     * @param startx the start x position
     * @param starty the start y position
     * @param width the width of the area
     * @param height the height of the area
     */
    protected void drawBackAndBorders(final Area area, final float startx, final float starty, final float width, final float height)
    {
        // draw background then border

        BorderProps bpsBefore = (BorderProps)area.getTrait(Trait.BORDER_BEFORE);
        BorderProps bpsAfter = (BorderProps)area.getTrait(Trait.BORDER_AFTER);
        BorderProps bpsStart = (BorderProps)area.getTrait(Trait.BORDER_START);
        BorderProps bpsEnd = (BorderProps)area.getTrait(Trait.BORDER_END);

        Trait.Background back;
        back = (Trait.Background)area.getTrait(Trait.BACKGROUND);
        if (back != null)
        {
            endTextObject();

            //Calculate padding rectangle
            float sx = startx;
            float sy = starty;
            float paddRectWidth = width;
            float paddRectHeight = height;
            if (bpsStart != null)
            {
                sx += bpsStart.width / 1000f;
                paddRectWidth -= bpsStart.width / 1000f;
            }
            if (bpsBefore != null)
            {
                sy += bpsBefore.width / 1000f;
                paddRectHeight -= bpsBefore.width / 1000f;
            }
            if (bpsEnd != null) {
				paddRectWidth -= bpsEnd.width / 1000f;
			}
            if (bpsAfter != null) {
				paddRectHeight -= bpsAfter.width / 1000f;
			}

            if (back.getColor() != null)
            {
                updateColor(back.getColor(), true);
                fillRect(sx, sy, paddRectWidth, paddRectHeight);
            }
            if (back.getFovImage() != null)
            {
                FovImage fovimage = back.getFovImage();
                if (fovimage != null && fovimage.load(FovImage.DIMENSIONS))
                {
                    saveGraphicsState();
                    clipRect(sx, sy, paddRectWidth, paddRectHeight);
                    int horzCount = (int)((paddRectWidth * 1000 / fovimage.getIntrinsicWidth()) + 1.0f);
                    int vertCount = (int)((paddRectHeight * 1000 / fovimage.getIntrinsicHeight()) + 1.0f);
                    if (back.getRepeat() == EN_NOREPEAT)
                    {
                        horzCount = 1;
                        vertCount = 1;
                    }
                    else if (back.getRepeat() == EN_REPEATX) {
						vertCount = 1;
					} else if (back.getRepeat() == EN_REPEATY) {
						horzCount = 1;
					}
                    //change from points to millipoints
                    sx *= 1000;
                    sy *= 1000;
                    if (horzCount == 1) {
						sx += back.getHoriz();
					}
                    if (vertCount == 1) {
						sy += back.getVertical();
					}
                    for (int x = 0; x < horzCount; x++)
                    {
                        for (int y = 0; y < vertCount; y++)
                        {
                            // place once
                            Rectangle2D pos;
                            // Image positions are relative to the currentIP/BP
                            pos = new Rectangle2D.Float(sx - currentIPPosition + (x * fovimage.getIntrinsicWidth()),
                                                        sy - currentBPPosition + (y * fovimage.getIntrinsicHeight()),
                                                        fovimage.getIntrinsicWidth(),
                                                        fovimage.getIntrinsicHeight());
                            Image image = drawImage(back.getURL(), pos);
                            /* 【添加：START】 by 李晓光  2009-2-5  */
							Rectangle2D r = new Rectangle2D.Double(currentIPPosition + pos.getX(), currentBPPosition + pos.getY(), pos.getWidth(), pos.getHeight());
							drawImage(area, image, r);
							/* 【添加：END】 by 李晓光  2009-2-5  */
                        }
                    }
                    restoreGraphicsState();
                } else {
					log.warn("Can't find background image: " + back.getURL());
				}
            }
        }

        Rectangle2D.Float borderRect = new Rectangle2D.Float(startx, starty, width, height);
        drawBorders(borderRect, bpsBefore, bpsAfter, bpsStart, bpsEnd);
    }

    /**
     * Draws borders.
     * @param borderRect the border rectangle
     * @param bpsBefore the border specification on the before side
     * @param bpsAfter the border specification on the after side
     * @param bpsStart the border specification on the start side
     * @param bpsEnd the border specification on the end side
     */
    protected void drawBorders(final Rectangle2D.Float borderRect,
            final BorderProps bpsBefore, final BorderProps bpsAfter, final BorderProps bpsStart, final BorderProps bpsEnd)
    {
        float startx = borderRect.x;
        float starty = borderRect.y;
        float width = borderRect.width;
        float height = borderRect.height;
        boolean[] b = new boolean[] {(bpsBefore != null), (bpsEnd != null), (bpsAfter != null), (bpsStart != null)};
        if (!b[0] && !b[1] && !b[2] && !b[3])
            return;
        float[] bw = new float[]
        {
            (b[0] ? bpsBefore.width / 1000f : 0.0f), (b[1] ? bpsEnd.width / 1000f : 0.0f),
            (b[2] ? bpsAfter.width / 1000f : 0.0f),
            (b[3] ? bpsStart.width / 1000f : 0.0f)
        };
        float[] clipw = new float[]
       {
            BorderProps.getClippedWidth(bpsBefore) / 1000f,
            BorderProps.getClippedWidth(bpsEnd) / 1000f,
            BorderProps.getClippedWidth(bpsAfter) / 1000f,
            BorderProps.getClippedWidth(bpsStart) / 1000f
        };
        starty += clipw[0];
        height -= clipw[0];
        height -= clipw[2];
        startx += clipw[3];
        width -= clipw[3];
        width -= clipw[1];

        boolean[] slant = new boolean[] {(b[3] && b[0]), (b[0] && b[1]), (b[1] && b[2]), (b[2] && b[3])};
        if (bpsBefore != null)
        {
            endTextObject();
            float sx1 = startx;
            float sx2 = (slant[0] ? sx1 + bw[3] - clipw[3] : sx1);
            float ex1 = startx + width;
            float ex2 = (slant[1] ? ex1 - bw[1] + clipw[1] : ex1);
            float outery = starty - clipw[0];
            float clipy = outery + clipw[0];
            float innery = outery + bw[0];

            saveGraphicsState();
            moveTo(sx1, clipy);
            float sx1a = sx1;
            float ex1a = ex1;
            if (bpsBefore.mode == BorderProps.COLLAPSE_OUTER)
            {
                if (bpsStart != null && bpsStart.mode == BorderProps.COLLAPSE_OUTER) {
					sx1a -= clipw[3];
				}
                if (bpsEnd != null && bpsEnd.mode == BorderProps.COLLAPSE_OUTER) {
					ex1a += clipw[1];
				}
                lineTo(sx1a, outery);
                lineTo(ex1a, outery);
            }
            lineTo(ex1, clipy);
            lineTo(ex2, innery);
            lineTo(sx2, innery);
            closePath();
            clip();
            drawBorderLine(sx1a, outery, ex1a, innery, true, true, bpsBefore.style, bpsBefore.color);
            restoreGraphicsState();
        }
        if (bpsEnd != null)
        {
            endTextObject();
            float sy1 = starty;
            float sy2 = (slant[1] ? sy1 + bw[0] - clipw[0] : sy1);
            float ey1 = starty + height;
            float ey2 = (slant[2] ? ey1 - bw[2] + clipw[2] : ey1);
            float outerx = startx + width + clipw[1];
            float clipx = outerx - clipw[1];
            float innerx = outerx - bw[1];

            saveGraphicsState();
            moveTo(clipx, sy1);
            float sy1a = sy1;
            float ey1a = ey1;
            if (bpsEnd.mode == BorderProps.COLLAPSE_OUTER)
            {
                if (bpsBefore != null && bpsBefore.mode == BorderProps.COLLAPSE_OUTER) {
					sy1a -= clipw[0];
				}
                if (bpsAfter != null && bpsAfter.mode == BorderProps.COLLAPSE_OUTER) {
					ey1a += clipw[2];
				}
                lineTo(outerx, sy1a);
                lineTo(outerx, ey1a);
            }
            lineTo(clipx, ey1);
            lineTo(innerx, ey2);
            lineTo(innerx, sy2);
            closePath();
            clip();
            drawBorderLine(innerx, sy1a, outerx, ey1a, false, false, bpsEnd.style, bpsEnd.color);
            restoreGraphicsState();
        }
        if (bpsAfter != null)
        {
            endTextObject();
            float sx1 = startx;
            float sx2 = (slant[3] ? sx1 + bw[3] - clipw[3] : sx1);
            float ex1 = startx + width;
            float ex2 = (slant[2] ? ex1 - bw[1] + clipw[1] : ex1);
            float outery = starty + height + clipw[2];
            float clipy = outery - clipw[2];
            float innery = outery - bw[2];

            saveGraphicsState();
            moveTo(ex1, clipy);
            float sx1a = sx1;
            float ex1a = ex1;
            if (bpsAfter.mode == BorderProps.COLLAPSE_OUTER)
            {
                if (bpsStart != null && bpsStart.mode == BorderProps.COLLAPSE_OUTER) {
					sx1a -= clipw[3];
				}
                if (bpsEnd != null && bpsEnd.mode == BorderProps.COLLAPSE_OUTER) {
					ex1a += clipw[1];
				}
                lineTo(ex1a, outery);
                lineTo(sx1a, outery);
            }
            lineTo(sx1, clipy);
            lineTo(sx2, innery);
            lineTo(ex2, innery);
            closePath();
            clip();
            drawBorderLine(sx1a, innery, ex1a, outery, true, false, bpsAfter.style, bpsAfter.color);
            restoreGraphicsState();
        }
        if (bpsStart != null)
        {
            endTextObject();
            float sy1 = starty;
            float sy2 = (slant[0] ? sy1 + bw[0] - clipw[0] : sy1);
            float ey1 = sy1 + height;
            float ey2 = (slant[3] ? ey1 - bw[2] + clipw[2] : ey1);
            float outerx = startx - clipw[3];
            float clipx = outerx + clipw[3];
            float innerx = outerx + bw[3];

            saveGraphicsState();
            moveTo(clipx, ey1);
            float sy1a = sy1;
            float ey1a = ey1;
            if (bpsStart.mode == BorderProps.COLLAPSE_OUTER)
            {
                if (bpsBefore != null && bpsBefore.mode == BorderProps.COLLAPSE_OUTER) {
					sy1a -= clipw[0];
				}
                if (bpsAfter != null && bpsAfter.mode == BorderProps.COLLAPSE_OUTER) {
					ey1a += clipw[2];
				}
                lineTo(outerx, ey1a);
                lineTo(outerx, sy1a);
            }
            lineTo(clipx, sy1);
            lineTo(innerx, sy2);
            lineTo(innerx, ey2);
            closePath();
            clip();
            drawBorderLine(outerx, sy1a, innerx, ey1a, false, true, bpsStart.style, bpsStart.color);
            restoreGraphicsState();
        }
    }

    /**
     * Common method to render the background and borders for any inline area.
     * The all borders and padding are drawn outside the specified area.
     * @param area the inline area for which the background, border and padding is to be
     * rendered
     */
    @Override
	protected void renderInlineAreaBackAndBorders(final InlineArea area)
    {
        float x = currentIPPosition / 1000f;
        float y = (currentBPPosition + area.getOffset()) / 1000f;
        float width = area.getIPD() / 1000f;
        float height = area.getBPD() / 1000f;
        float borderPaddingStart = area.getBorderAndPaddingWidthStart() / 1000f;
        float borderPaddingBefore = area.getBorderAndPaddingWidthBefore() / 1000f;
        float bpwidth = borderPaddingStart + (area.getBorderAndPaddingWidthEnd() / 1000f);
        float bpheight = borderPaddingBefore + (area.getBorderAndPaddingWidthAfter() / 1000f);

        //这里判断可编辑的文本域，并把计算出的Rectangle2D设给文本域
        if(TextArea.class.equals(area.getClass())) {
        	if(((TextArea)area).getEditMode() != 0) {
                Rectangle2D rec = new Rectangle();
                rec.setRect(x, y - borderPaddingBefore, width + bpwidth, height + bpheight);
                ((TextArea)area).setShowRec(rec);
//                System.err.println("text rect = " + rec);
        	}
        }else{
        	Rectangle2D rect = new Rectangle();
            rect.setRect(x, y - borderPaddingBefore, width + bpwidth, height + bpheight);            
			area.setViewport(rect);
        }

        if (height != 0.0f || bpheight != 0.0f && bpwidth != 0.0f) {
			drawBackAndBorders(area, x, y - borderPaddingBefore, width + bpwidth, height + bpheight);
		}
    }

    /** @see com.wisii.fov.render.AbstractRenderer#renderBlockViewport(BlockViewport, List)    */
    @SuppressWarnings("unchecked")
	@Override
	protected void renderBlockViewport(final BlockViewport bv, final List children)
    {
        // clip and position viewport if necessary
        // save positions
        int saveIP = currentIPPosition;
        int saveBP = currentBPPosition;
        //String saveFontName = currentFontName;

        CTM ctm = bv.getCTM();
        int borderPaddingStart = bv.getBorderAndPaddingWidthStart();
        int borderPaddingBefore = bv.getBorderAndPaddingWidthBefore();
        float x, y;
        x = (bv.getXOffset() + containingIPPosition) / 1000f;
        y = (bv.getYOffset() + containingBPPosition) / 1000f;
        //This is the content-rect
        float width = bv.getIPD() / 1000f;
        float height = bv.getBPD() / 1000f;


        if (bv.getPositioning() == Block.ABSOLUTE || bv.getPositioning() == Block.FIXED)
        {
            currentIPPosition = bv.getXOffset();
            currentBPPosition = bv.getYOffset();

            //For FIXED, we need to break out of the current viewports to the
            //one established by the page. We save the state stack for restoration
            //after the block-container has been painted. See below.
            List breakOutList = null;
            if (bv.getPositioning() == Block.FIXED) {
				breakOutList = breakOutOfStateStack();
			}

            CTM tempctm = new CTM(containingIPPosition, containingBPPosition);
            ctm = tempctm.multiply(ctm);

            //Adjust for spaces (from margin or indirectly by start-indent etc.
            x += bv.getSpaceStart() / 1000f;
            currentIPPosition += bv.getSpaceStart();

            y += bv.getSpaceBefore() / 1000f;
            currentBPPosition += bv.getSpaceBefore();

            float bpwidth = (borderPaddingStart + bv.getBorderAndPaddingWidthEnd()) / 1000f;
            float bpheight = (borderPaddingBefore + bv.getBorderAndPaddingWidthAfter()) / 1000f;
            
            drawBackAndBorders(bv, x, y, width + bpwidth, height + bpheight);
            /* 【添加：START】by 李晓光	2009-6-18  */
            x += bv.getBorderAndPaddingWidthStart()/1000F;
//            x += (float)_currentViewportArea.getX() / 1000F;
            y += bv.getBorderAndPaddingWidthBefore()/1000F;
//            y += (float)_currentViewportArea.getY() / 1000;
            
            bv.setViewport(new Rectangle2D.Float(x, y, width + 1, 1 + height));
            /* 【添加：END】by 李晓光	2009-6-18  */
            
            //Now adjust for border/padding
            currentIPPosition += borderPaddingStart;
            currentBPPosition += borderPaddingBefore;

            Rectangle2D clippingRect = null;
            if (bv.getClip()) {
				clippingRect = new Rectangle(currentIPPosition, currentBPPosition, bv.getIPD(), bv.getBPD());
			}

            startVParea(ctm, clippingRect);
            currentIPPosition = 0;
            currentBPPosition = 0;
            renderBlocks(bv, children);
            endVParea();

            if (breakOutList != null) {
				restoreStateStackAfterBreakOut(breakOutList);
			}

            currentIPPosition = saveIP;
            currentBPPosition = saveBP;
        }
        else
        {
            currentBPPosition += bv.getSpaceBefore();

            //borders and background in the old coordinate system
            handleBlockTraits(bv);

            //Advance to start of content area
            currentIPPosition += bv.getStartIndent();

            CTM tempctm = new CTM(containingIPPosition, currentBPPosition);
            ctm = tempctm.multiply(ctm);

            //Now adjust for border/padding
            currentBPPosition += borderPaddingBefore;

            Rectangle2D clippingRect = null;
            if (bv.getClip()) {
				clippingRect = new Rectangle(currentIPPosition, currentBPPosition, bv.getIPD(), bv.getBPD());
			}

            startVParea(ctm, clippingRect);
            currentIPPosition = 0;
            currentBPPosition = 0;
            renderBlocks(bv, children);
            endVParea();

            currentIPPosition = saveIP;
            currentBPPosition = saveBP;

            currentBPPosition += (bv.getAllocBPD());
        }
        //currentFontName = saveFontName;
    }

    /**
     * Render an inline viewport.
     * This renders an inline viewport by clipping if necessary.
     * @param viewport the viewport to handle
     */
    @Override
	public void renderViewport(final Viewport viewport)
    {
        float x = currentIPPosition / 1000f;
        float y = (currentBPPosition + viewport.getOffset()) / 1000f;
        float width = viewport.getIPD() / 1000f;
        float height = viewport.getBPD() / 1000f;
        // TODO: Calculate the border rect correctly.
        float borderPaddingStart = viewport.getBorderAndPaddingWidthStart() / 1000f;
        float borderPaddingBefore = viewport.getBorderAndPaddingWidthBefore() / 1000f;
        float bpwidth = borderPaddingStart + (viewport.getBorderAndPaddingWidthEnd() / 1000f);
        float bpheight = borderPaddingBefore + (viewport.getBorderAndPaddingWidthAfter() / 1000f);

        drawBackAndBorders(viewport, x, y, width + bpwidth, height + bpheight);

        if (viewport.getClip())
        {
            saveGraphicsState();
            clipRect(x + borderPaddingStart, y + borderPaddingBefore, width, height);
        }
        super.renderViewport(viewport);

        if (viewport.getClip())
        {
            restoreGraphicsState();
        }
    }

    /**
     * Restores the state stack after a break out.
     * @param breakOutList the state stack to restore.
     */
    protected abstract void restoreStateStackAfterBreakOut(List breakOutList);

    /**
     * Breaks out of the state stack to handle fixed block-containers.
     * @return the saved state stack to recreate later
     */
    protected abstract List breakOutOfStateStack();

    /** Saves the graphics state of the rendering engine. */
    protected abstract void saveGraphicsState();

    /** Restores the last graphics state of the rendering engine. */
    protected abstract void restoreGraphicsState();

    /** Indicates the beginning of a text object. */
    protected abstract void beginTextObject();

    /** Indicates the end of a text object. */
    protected abstract void endTextObject();

    /**
     * Paints the text decoration marks.
     * @param fm Current typeface
     * @param fontsize Current font size
     * @param inline inline area to paint the marks for
     * @param baseline position of the baseline
     * @param startx start IPD
     */
    protected void renderTextDecoration(final FontMetrics fm, final int fontsize, final InlineArea inline, final int baseline, final int startx)
    {
        boolean hasTextDeco = inline.hasUnderline() || inline.hasOverline() || inline.hasLineThrough();
        if (hasTextDeco)
        {
            endTextObject();
            float descender = fm.getDescender(fontsize) / 1000f;
            float capHeight = fm.getCapHeight(fontsize) / 1000f;
            float halfLineWidth = (descender / -8f) / 2f;
            float endx = (startx + inline.getIPD()) / 1000f;
            if (inline.hasUnderline())
            {
                Color ct = (Color) inline.getTrait(Trait.UNDERLINE_COLOR);
                float y = baseline - descender / 2f;
                /* 【添加：START】by 李晓光 2009-2-2 */
				if(!isAvailabilityLayer(ct, userAgent.getCheckLayers()))// && userAgent.isSelected()
					return;
				/* 【添加：END】by 李晓光 2009-2-2 */
                drawBorderLine(startx / 1000f, (y - halfLineWidth) / 1000f,
                        endx, (y + halfLineWidth) / 1000f,
                        true, true, Constants.EN_SOLID, ct);
            }
            if (inline.hasOverline())
            {
                Color ct = (Color) inline.getTrait(Trait.OVERLINE_COLOR);
                float y = (float)(baseline - (1.1 * capHeight));
                /* 【添加：START】by 李晓光 2009-2-2 */
				if(!isAvailabilityLayer(ct, userAgent.getCheckLayers()))// && userAgent.isSelected()
					return;
				/* 【添加：END】by 李晓光 2009-2-2 */
                drawBorderLine(startx / 1000f, (y - halfLineWidth) / 1000f,
                        endx, (y + halfLineWidth) / 1000f,
                        true, true, Constants.EN_SOLID, ct);
            }
            if (inline.hasLineThrough())
            {
                Color ct = (Color) inline.getTrait(Trait.LINETHROUGH_COLOR);
                float y = (float)(baseline - (0.45 * capHeight));
                /* 【添加：START】by 李晓光 2009-2-2 */
				if(!isAvailabilityLayer(ct, userAgent.getCheckLayers()))// && userAgent.isSelected()
					return;
				/* 【添加：END】by 李晓光 2009-2-2 */
                drawBorderLine(startx / 1000f, (y - halfLineWidth) / 1000f,
                        endx, (y + halfLineWidth) / 1000f,
                        true, true, Constants.EN_SOLID, ct);
            }
        }
    }

    /** Clip using the current path. */
    protected abstract void clip();

    /**
     * Clip using a rectangular area.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    protected abstract void clipRect(float x, float y, float width, float height);

    /**
     * Moves the current point to (x, y), omitting any connecting line segment.
     * @param x x coordinate
     * @param y y coordinate
     */
    protected abstract void moveTo(float x, float y);

    /**
     * Appends a straight line segment from the current point to (x, y). The
     * new current point is (x, y).
     * @param x x coordinate
     * @param y y coordinate
     */
    protected abstract void lineTo(float x, float y);

    /**
     * Closes the current subpath by appending a straight line segment from
     * the current point to the starting point of the subpath.
     */
    protected abstract void closePath();

    /**
     * Fill a rectangular area.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    protected abstract void fillRect(float x, float y, float width, float height);

    /**
     * Establishes a new foreground or fill color.
     * @param col the color to apply (null skips this operation)
     * @param fill true to set the fill color, false for the foreground color
     */
    protected abstract void updateColor(Color col, boolean fill);

    /**
     * Draw an image at the indicated location.
     * @param url the URI/URL of the image
     * @param pos the position of the image
     * @param foreignAttributes an optional Map with foreign attributes, may be null
     */
    protected abstract java.awt.Image drawImage(String url, Rectangle2D pos, Map foreignAttributes);

    /**
     * Draw an image at the indicated location.
     * @param url the URI/URL of the image
     * @param pos the position of the image
     */
    protected final java.awt.Image drawImage(final String url, final Rectangle2D pos)
    {
         return drawImage(url, pos, null);
    }
    /*【添加：START】 by 李晓光 2009-2-5   新加接口方法 */
	protected void drawImage(final Area area, final Image image, final Rectangle2D pos){}
	/*【添加：END】 by 李晓光 2009-2-5 */
    /**
     * Draw a border segment of an XSL-FO style border.
     * @param x1 starting x coordinate
     * @param y1 starting y coordinate
     * @param x2 ending x coordinate
     * @param y2 ending y coordinate
     * @param horz true for horizontal border segments, false for vertical border segments
     * @param startOrBefore true for border segments on the start or before edge,
     *                      false for end or after.
     * @param style the border style (one of Constants.EN_DASHED etc.)
     * @param col the color for the border segment
     */
    protected abstract void drawBorderLine(float x1, float y1, float x2, float y2,
            boolean horz, boolean startOrBefore, int style, Color col);

    /**
     * @see com.wisii.fov.render.AbstractRenderer#renderForeignObject(ForeignObject, Rectangle2D)
     */
    @Override
	public void renderForeignObject(final ForeignObject fo, final Rectangle2D pos)
    {
        endTextObject();
        Document doc = fo.getDocument();
        String ns = fo.getNameSpace();
        /* 【添加：START】 by 李晓光  2009-2-3 */
        if(!isAvailabilityLayer(fo, userAgent.getCheckLayers()))// && (userAgent.isSelected())
        	return;
        /* 【添加：END】 by 李晓光  2009-2-3 */
        	renderDocument(doc, ns, pos, fo.getForeignAttributes());
    }

}
