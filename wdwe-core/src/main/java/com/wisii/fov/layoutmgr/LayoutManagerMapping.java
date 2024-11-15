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
 */package com.wisii.fov.layoutmgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisii.fov.area.AreaTreeHandler;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FOText;
import com.wisii.fov.fo.FObjMixed;
import com.wisii.fov.fo.flow.BasicLink;
import com.wisii.fov.fo.flow.BidiOverride;
import com.wisii.fov.fo.flow.Block;
import com.wisii.fov.fo.flow.BlockContainer;
import com.wisii.fov.fo.flow.Character;
import com.wisii.fov.fo.flow.ExternalGraphic;
import com.wisii.fov.fo.flow.Footnote;
import com.wisii.fov.fo.flow.Inline;
import com.wisii.fov.fo.flow.InlineContainer;
import com.wisii.fov.fo.flow.InlineLevel;
import com.wisii.fov.fo.flow.InstreamForeignObject;
import com.wisii.fov.fo.flow.Leader;
import com.wisii.fov.fo.flow.ListBlock;
import com.wisii.fov.fo.flow.ListItem;
import com.wisii.fov.fo.flow.PageNumber;
import com.wisii.fov.fo.flow.PageNumberCitation;
import com.wisii.fov.fo.flow.PageNumberCitationLast;
import com.wisii.fov.fo.flow.RetrieveMarker;
import com.wisii.fov.fo.flow.Table;
import com.wisii.fov.fo.flow.TableBody;
import com.wisii.fov.fo.flow.TableCell;
import com.wisii.fov.fo.flow.TableColumn;
import com.wisii.fov.fo.flow.TableFooter;
import com.wisii.fov.fo.flow.TableHeader;
import com.wisii.fov.fo.flow.TableRow;
import com.wisii.fov.fo.flow.Wrapper;
import com.wisii.fov.fo.pagination.Flow;
import com.wisii.fov.fo.pagination.PageSequence;
import com.wisii.fov.fo.pagination.SideRegion;
import com.wisii.fov.fo.pagination.StaticContent;
import com.wisii.fov.fo.pagination.Title;
import com.wisii.fov.layoutmgr.inline.BasicLinkLayoutManager;
import com.wisii.fov.layoutmgr.inline.BidiLayoutManager;
import com.wisii.fov.layoutmgr.inline.CharacterLayoutManager;
import com.wisii.fov.layoutmgr.inline.ContentLayoutManager;
import com.wisii.fov.layoutmgr.inline.ExternalGraphicLayoutManager;
import com.wisii.fov.layoutmgr.inline.FootnoteLayoutManager;
import com.wisii.fov.layoutmgr.inline.ICLayoutManager;
import com.wisii.fov.layoutmgr.inline.InlineLayoutManager;
import com.wisii.fov.layoutmgr.inline.InlineLevelLayoutManager;
import com.wisii.fov.layoutmgr.inline.InstreamForeignObjectLM;
import com.wisii.fov.layoutmgr.inline.LeaderLayoutManager;
import com.wisii.fov.layoutmgr.inline.PageNumberCitationLastLayoutManager;
import com.wisii.fov.layoutmgr.inline.PageNumberCitationLayoutManager;
import com.wisii.fov.layoutmgr.inline.PageNumberLayoutManager;
import com.wisii.fov.layoutmgr.inline.QianZhangLayoutManager;
import com.wisii.fov.layoutmgr.inline.TextLayoutManager;
import com.wisii.fov.layoutmgr.inline.WrapperLayoutManager;
import com.wisii.fov.layoutmgr.list.ListBlockLayoutManager;
import com.wisii.fov.layoutmgr.list.ListItemLayoutManager;
import com.wisii.fov.layoutmgr.table.TableLayoutManager;
import com.wisii.fov.util.CharUtilities;
import com.wisii.fov.wisii.QianZhang;

/** The default LayoutManager maker class */
public class LayoutManagerMapping implements LayoutManagerMaker
{
    /** logging instance */
    protected static Log log = LogFactory.getLog(LayoutManagerMapping.class);

    /** The map of LayoutManagerMakers */
    private Map makers = new HashMap();

    public LayoutManagerMapping()
    {
        initialize();
    }

    /** Initializes the set of maker objects associated with this LayoutManagerMapping     */
    protected void initialize()
    {
        makers.put(FOText.class, new FOTextLayoutManagerMaker());
        makers.put(FObjMixed.class, new Maker());
        makers.put(BidiOverride.class, new BidiOverrideLayoutManagerMaker());
        makers.put(Inline.class, new InlineLayoutManagerMaker());
        makers.put(Footnote.class, new FootnodeLayoutManagerMaker());
        makers.put(InlineContainer.class, new InlineContainerLayoutManagerMaker());
        makers.put(BasicLink.class, new BasicLinkLayoutManagerMaker());
        makers.put(Block.class, new BlockLayoutManagerMaker());
        makers.put(Leader.class, new LeaderLayoutManagerMaker());
        makers.put(RetrieveMarker.class, new RetrieveMarkerLayoutManagerMaker());
        makers.put(Character.class, new CharacterLayoutManagerMaker());
        makers.put(ExternalGraphic.class,  new ExternalGraphicLayoutManagerMaker());
        makers.put(BlockContainer.class,  new BlockContainerLayoutManagerMaker());
        makers.put(ListItem.class, new ListItemLayoutManagerMaker());
        makers.put(ListBlock.class, new ListBlockLayoutManagerMaker());
        makers.put(InstreamForeignObject.class, new InstreamForeignObjectLayoutManagerMaker());
        makers.put(PageNumber.class, new PageNumberLayoutManagerMaker());
        makers.put(PageNumberCitation.class,  new PageNumberCitationLayoutManagerMaker());
        makers.put(PageNumberCitationLast.class, new PageNumberCitationLastLayoutManagerMaker());
        makers.put(Table.class, new TableLayoutManagerMaker());
        makers.put(TableBody.class, new Maker());
        makers.put(TableColumn.class, new Maker());
        makers.put(TableRow.class, new Maker());
        makers.put(TableCell.class, new Maker());
        makers.put(TableFooter.class, new Maker());
        makers.put(TableHeader.class, new Maker());
        makers.put(Wrapper.class, new WrapperLayoutManagerMaker());
        makers.put(Title.class, new InlineLayoutManagerMaker());
        makers.put(QianZhang.class,  new QianZhangLayoutManagerMaker());
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManagerMaker#makeLayoutManagers(FONode, List)    */
    public void makeLayoutManagers(FONode node, List lms)
    {
        Maker maker = (Maker) makers.get(node.getClass());
        if (maker == null)
            log.error("No LayoutManager maker for class " + node.getClass());
        else
            maker.make(node, lms);
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManagerMaker#makeLayoutManager(FONode)     */
    public LayoutManager makeLayoutManager(FONode node)
    {
        List lms = new ArrayList();
        makeLayoutManagers(node, lms);
        if (lms.size() == 0)
            throw new IllegalStateException("类 " + node.getClass() + " 的布局管理丢失.");
       else if (lms.size() > 1)
            throw new IllegalStateException("重复定义LayoutMangers");
        return (LayoutManager) lms.get(0);
    }

    public PageSequenceLayoutManager makePageSequenceLayoutManager(AreaTreeHandler ath, PageSequence ps)
    {
        return new PageSequenceLayoutManager(ath, ps);
    }

    /* @see com.wisii.fov.layoutmgr.LayoutManagerMaker#makeFlowLayoutManager(PageSequenceLayoutManager, Flow)   */
    public FlowLayoutManager makeFlowLayoutManager(PageSequenceLayoutManager pslm, Flow flow)
    {
        return new FlowLayoutManager(pslm, flow);
    }

    /* @see com.wisii.fov.layoutmgr.LayoutManagerMaker#makeContentLayoutManager(PageSequenceLayoutManager, Title)     */
    public ContentLayoutManager makeContentLayoutManager(PageSequenceLayoutManager pslm, Title title)
    {
        return new ContentLayoutManager(pslm, title);
    }

    /* @see com.wisii.fov.layoutmgr.LayoutManagerMaker#makeStaticContentLayoutManager(PageSequenceLayoutManager, StaticContent, Region)     */
    public StaticContentLayoutManager makeStaticContentLayoutManager(PageSequenceLayoutManager pslm, StaticContent sc, SideRegion reg)
    {
        return new StaticContentLayoutManager(pslm, sc, reg);
    }

    /* @see com.wisii.fov.layoutmgr.LayoutManagerMaker#makeStaticContentLayoutManager(PageSequenceLayoutManager, StaticContent, Block)     */
    public StaticContentLayoutManager makeStaticContentLayoutManager(PageSequenceLayoutManager pslm, StaticContent sc, com.wisii.fov.area.Block block)
    {
        return new StaticContentLayoutManager(pslm, sc, block);
    }

    public static class Maker
    {
        public void make(FONode node, List lms)
        {
            // no layout manager
            return;
        }
    }

    public static class FOTextLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            FOText foText = (FOText) node;
            int length = foText.endIndex - foText.startIndex;
            if ( length> 0 || (length == 0 && Inline.class.equals(node.getParent().getClass())))
            {
                lms.add(new TextLayoutManager(foText));
            }
        }
    }

/*
    public static class FObjMixedLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            if (node.getChildNodes() != null)
            {
                InlineStackingLayoutManager lm;
                lm = new InlineStackingLayoutManager((FObjMixed) node);
                lms.add(lm);
            }
        }
    }
*/

    public static class BidiOverrideLayoutManagerMaker extends Maker
    {
        // public static class BidiOverrideLayoutManagerMaker extends FObjMixedLayoutManagerMaker {
        public void make(BidiOverride node, List lms)
        {
            if (false)
            {
                // this is broken; it does nothing it should make something like an InlineStackingLM
                super.make(node, lms);
            }
            else
            {
                ArrayList childList = new ArrayList();
                // this is broken; it does nothing it should make something like an InlineStackingLM
                super.make(node, childList);
                for (int count = childList.size() - 1; count >= 0; count--)
                {
                    LayoutManager lm = (LayoutManager) childList.get(count);
                    if (lm instanceof InlineLevelLayoutManager)
                    {
                        LayoutManager blm = new BidiLayoutManager(node, (InlineLayoutManager) lm);
                        lms.add(blm);
                    }
                    else
                        lms.add(lm);
                }
            }
        }
    }

    public static class InlineLayoutManagerMaker extends Maker
    {
         public void make(FONode node, List lms)
         {
             /*if (node.getChildNodes() != null)*/
        	 //修改让带有id的inline，在tarea-tree出现，
             lms.add(new InlineLayoutManager((InlineLevel) node));
         }
    }

    public static class FootnodeLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            lms.add(new FootnoteLayoutManager((Footnote) node));
        }
    }

    public static class InlineContainerLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            ArrayList childList = new ArrayList();
            super.make(node, childList);
            lms.add(new ICLayoutManager((InlineContainer) node, childList));
        }
    }

    public static class BasicLinkLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            lms.add(new BasicLinkLayoutManager((BasicLink) node));
        }
    }

    public static class BlockLayoutManagerMaker extends Maker
    {
         public void make(FONode node, List lms)
         {
             lms.add(new BlockLayoutManager((Block) node));
         }
    }

    public static class LeaderLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            lms.add(new LeaderLayoutManager((Leader) node));
        }
    }

    public static class CharacterLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            Character foCharacter = (Character) node;
            if (foCharacter.getCharacter() != CharUtilities.CODE_EOT)
            {
                lms.add(new CharacterLayoutManager(foCharacter));
            }
        }
    }

    public static class ExternalGraphicLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            ExternalGraphic eg = (ExternalGraphic) node;
            if (!eg.getSrc().equals(""))
            {
                lms.add(new ExternalGraphicLayoutManager(eg));
            }
        }
    }
    public static class QianZhangLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
        	QianZhang eg = (QianZhang) node;
            if (!eg.getSrc().equals(""))
            {
                lms.add(new QianZhangLayoutManager(eg));
            }
        }
    }
    public static class BlockContainerLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            lms.add(new BlockContainerLayoutManager((BlockContainer) node));
         }
    }

    public static class ListItemLayoutManagerMaker extends Maker
    {
         public void make(FONode node, List lms)
         {
             lms.add(new ListItemLayoutManager((ListItem) node));
         }
    }

    public static class ListBlockLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            lms.add(new ListBlockLayoutManager((ListBlock) node));
        }
    }

    public static class InstreamForeignObjectLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            lms.add(new InstreamForeignObjectLM((InstreamForeignObject) node));
        }
    }

    public static class PageNumberLayoutManagerMaker extends Maker
    {
         public void make(FONode node, List lms)
         {
             lms.add(new PageNumberLayoutManager((PageNumber) node));
         }
    }

    public static class PageNumberCitationLayoutManagerMaker extends Maker
    {
         public void make(FONode node, List lms)
         {
            lms.add(new PageNumberCitationLayoutManager((PageNumberCitation) node));
         }
    }

    public static class PageNumberCitationLastLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
           lms.add(new PageNumberCitationLastLayoutManager((PageNumberCitationLast) node));
        }
    }

    public static class TableLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            Table table = (Table) node;
            TableLayoutManager tlm = new TableLayoutManager(table);
            lms.add(tlm);
        }
    }

    public class RetrieveMarkerLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            Iterator baseIter;
            baseIter = node.getChildNodes();
            if (baseIter == null)
                return;
            while (baseIter.hasNext())
            {
                FONode child = (FONode) baseIter.next();
                makeLayoutManagers(child, lms);
            }
        }
    }

    public class WrapperLayoutManagerMaker extends Maker
    {
        public void make(FONode node, List lms)
        {
            //We insert the wrapper LM before it's children so an ID on the node can be registered on a page.
            lms.add(new WrapperLayoutManager((Wrapper)node));
            Iterator baseIter;
            baseIter = node.getChildNodes();
            if (baseIter == null)
                return;
            while (baseIter.hasNext())
            {
                FONode child = (FONode) baseIter.next();
                makeLayoutManagers(child, lms);
            }
        }
    }

}
