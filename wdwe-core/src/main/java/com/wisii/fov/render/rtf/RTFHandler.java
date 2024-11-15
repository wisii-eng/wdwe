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
 *//*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.wisii.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: RTFHandler.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.rtf;

// Java
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

// Libs
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

// FOV
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.datatypes.LengthBase;
import com.wisii.fov.datatypes.SimplePercentBaseContext;
import com.wisii.fov.fo.FOEventHandler;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.flow.BasicLink;
import com.wisii.fov.fo.flow.Block;
import com.wisii.fov.fo.flow.BlockContainer;
import com.wisii.fov.fo.flow.Character;
import com.wisii.fov.fo.flow.ExternalGraphic;
import com.wisii.fov.fo.flow.Footnote;
import com.wisii.fov.fo.flow.FootnoteBody;
import com.wisii.fov.fo.flow.Inline;
import com.wisii.fov.fo.flow.InstreamForeignObject;
import com.wisii.fov.fo.flow.Leader;
import com.wisii.fov.fo.flow.ListBlock;
import com.wisii.fov.fo.flow.ListItem;
import com.wisii.fov.fo.flow.ListItemBody;
import com.wisii.fov.fo.flow.ListItemLabel;
import com.wisii.fov.fo.flow.PageNumber;
import com.wisii.fov.fo.flow.Table;
import com.wisii.fov.fo.flow.TableColumn;
import com.wisii.fov.fo.flow.TableBody;
import com.wisii.fov.fo.flow.TableCell;
import com.wisii.fov.fo.flow.TableHeader;
import com.wisii.fov.fo.flow.TableRow;
import com.wisii.fov.fo.pagination.Flow;
import com.wisii.fov.fo.pagination.PageSequence;
import com.wisii.fov.fo.pagination.PageSequenceMaster;
import com.wisii.fov.fo.pagination.Region;
import com.wisii.fov.fo.pagination.SimplePageMaster;
import com.wisii.fov.fo.pagination.StaticContent;
import com.wisii.fov.fo.properties.CommonBorderPaddingBackground;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FOText;
import com.wisii.fov.render.DefaultFontResolver;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.ITableAttributes;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.IRtfAfterContainer;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.IRtfBeforeContainer;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.IRtfListContainer;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.IRtfTextrunContainer;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfAfter;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfAttributes;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfBefore;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfDocumentArea;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfElement;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfExternalGraphic;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfFile;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfFootnote;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfHyperLink;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfList;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfListItem;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfListItem.RtfListItemLabel;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfSection;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfTextrun;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfTable;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfTableRow;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfTableCell;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.IRtfTableContainer;
import com.wisii.fov.render.rtf.rtflib.tools.BuilderContext;
import com.wisii.fov.render.rtf.rtflib.tools.TableContext;
import com.wisii.fov.fonts.FontSetup;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.ImageFactory;
import com.wisii.fov.image.XMLImage;

/**
 * RTF Handler: generates RTF output using the structure events from
 * the FO Tree sent to this structure handler.
 *
 * @author Bertrand Delacretaz <bdelacretaz@codeconsult.ch>
 * @author Trembicki-Guy, Ed <GuyE@DNB.com>
 * @author Boris Poud茅rous <boris.pouderous@eads-telecom.com>
 * @author Peter Herweg <pherweg@web.de>
 * @author Andreas Putz <a.putz@skynamics.com>
 */
public class RTFHandler extends FOEventHandler {

    private RtfFile rtfFile;
    private final OutputStream os;
    private static Log log = LogFactory.getLog(RTFHandler.class);
    private RtfSection sect;
    private RtfDocumentArea docArea;
    private boolean bDefer;              //true, if each called handler shall be
                                         //processed at later time.
    private boolean bPrevHeaderSpecified = false; //true, if there has been a
                                                  //header in any page-sequence
    private boolean bPrevFooterSpecified = false; //true, if there has been a
                                                  //footer in any page-sequence
    private boolean bHeaderSpecified = false;  //true, if there is a header
                                               //in current page-sequence
    private boolean bFooterSpecified = false;  //true, if there is a footer
                                               //in current page-sequence
    private BuilderContext builderContext = new BuilderContext(null);

    private SimplePageMaster pagemaster;

    /**
     * Creates a new RTF structure handler.
     * @param userAgent the FOUserAgent for this process
     * @param os OutputStream to write to
     */
    public RTFHandler(FOUserAgent userAgent, OutputStream os) {
        super(userAgent);
        this.os = os;
        bDefer = true;

        FontSetup.setup(fontInfo, null, new DefaultFontResolver(userAgent));
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startDocument()
     * @throws SAXException In case of a IO-problem
     */
    public void startDocument() throws SAXException {
        // TODO sections should be created
        try {
            rtfFile = new RtfFile(new OutputStreamWriter(os));
            docArea = rtfFile.startDocumentArea();
        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            throw new SAXException(ioe);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endDocument()
     * @throws SAXException In case of a IO-problem
     */
    public void endDocument() throws SAXException {
        try {
            rtfFile.flush();
        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            throw new SAXException(ioe);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler
     * @param pageSeq PageSequence that is starting
     */
    public void startPageSequence(PageSequence pageSeq)  {
        try {
            //This is needed for region handling
            if (this.pagemaster == null) {
                String reference = pageSeq.getMasterReference();
                this.pagemaster
                        = pageSeq.getRoot().getLayoutMasterSet().getSimplePageMaster(reference);
                if (this.pagemaster == null) {
                    log.warn("Only simple-page-masters are supported on page-sequences: " 
                            + reference);
                    log.warn("Using default simple-page-master from page-sequence-master...");
                    PageSequenceMaster master 
                        = pageSeq.getRoot().getLayoutMasterSet().getPageSequenceMaster(reference);
                    this.pagemaster = master.getNextSimplePageMaster(false, false, false, false);
                }
            }

            if (bDefer) {
                return;
            }

            sect = docArea.newSection();

            //read page size and margins, if specified
            //only simple-page-master supported, so pagemaster may be null
            if (pagemaster != null) {
                sect.getRtfAttributes().set(
                    PageAttributesConverter.convertPageAttributes(
                            pagemaster));
            } else {
                log.warn("No simple-page-master could be determined!");
            }

            builderContext.pushContainer(sect);

            bHeaderSpecified = false;
            bFooterSpecified = false;
        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            log.error("startPageSequence: " + ioe.getMessage(), ioe);
            //TODO throw new FOVException(ioe);
        } catch (FOVException fove) {
            // TODO could we throw Exception in all FOEventHandler events?
            log.error("startPageSequence: " + fove.getMessage(), fove);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endPageSequence(PageSequence)
     * @param pageSeq PageSequence that is ending
     */
    public void endPageSequence(PageSequence pageSeq) {
        if (bDefer) {
            //If endBlock was called while SAX parsing, and the passed FO is Block
            //nested within another Block, stop deferring.
            //Now process all deferred FOs.
            bDefer = false;
            recurseFONode(pageSeq);
            this.pagemaster = null;
            bDefer = true;

            return;
        } else {
            builderContext.popContainer();
            this.pagemaster = null;
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startFlow(Flow)
     * @param fl Flow that is starting
     */
    public void startFlow(Flow fl) {
        if (bDefer) {
            return;
        }

        try {
            log.debug("starting flow: " + fl.getFlowName());
            boolean handled = false;
            Region regionBody = pagemaster.getRegion(Constants.FO_REGION_BODY);
            Region regionBefore = pagemaster.getRegion(Constants.FO_REGION_BEFORE);
            Region regionAfter = pagemaster.getRegion(Constants.FO_REGION_AFTER);
            if (fl.getFlowName().equals(regionBody.getRegionName())) {
                // if there is no header in current page-sequence but there has been
                // a header in a previous page-sequence, insert an empty header.
                if (bPrevHeaderSpecified && !bHeaderSpecified) {
                    RtfAttributes attr = new RtfAttributes();
                    attr.set(RtfBefore.HEADER);

                    final IRtfBeforeContainer contBefore
                        = (IRtfBeforeContainer)builderContext.getContainer
                                (IRtfBeforeContainer.class, true, this);
                    contBefore.newBefore(attr);
                }

                // if there is no footer in current page-sequence but there has been
                // a footer in a previous page-sequence, insert an empty footer.
                if (bPrevFooterSpecified && !bFooterSpecified) {
                    RtfAttributes attr = new RtfAttributes();
                    attr.set(RtfAfter.FOOTER);

                    final IRtfAfterContainer contAfter
                        = (IRtfAfterContainer)builderContext.getContainer
                                (IRtfAfterContainer.class, true, this);
                    contAfter.newAfter(attr);
                }
                handled = true;
            } else if (regionBefore != null 
                    && fl.getFlowName().equals(regionBefore.getRegionName())) {
                bHeaderSpecified = true;
                bPrevHeaderSpecified = true;

                final IRtfBeforeContainer c
                    = (IRtfBeforeContainer)builderContext.getContainer(
                        IRtfBeforeContainer.class,
                        true, this);

                RtfAttributes beforeAttributes = ((RtfElement)c).getRtfAttributes();
                if (beforeAttributes == null) {
                    beforeAttributes = new RtfAttributes();
                }
                beforeAttributes.set(RtfBefore.HEADER);

                RtfBefore before = c.newBefore(beforeAttributes);
                builderContext.pushContainer(before);
                handled = true;
            } else if (regionAfter != null 
                    && fl.getFlowName().equals(regionAfter.getRegionName())) {
                bFooterSpecified = true;
                bPrevFooterSpecified = true;

                final IRtfAfterContainer c
                    = (IRtfAfterContainer)builderContext.getContainer(
                        IRtfAfterContainer.class,
                        true, this);

                RtfAttributes afterAttributes = ((RtfElement)c).getRtfAttributes();
                if (afterAttributes == null) {
                    afterAttributes = new RtfAttributes();
                }

                afterAttributes.set(RtfAfter.FOOTER);

                RtfAfter after = c.newAfter(afterAttributes);
                builderContext.pushContainer(after);
                handled = true;
            }
            if (!handled) {
                log.warn("A " + fl.getLocalName() + " has been skipped: " + fl.getFlowName());
            }
        } catch (IOException ioe) {
            log.error("startFlow: " + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("startFlow: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endFlow(Flow)
     * @param fl Flow that is ending
     */
    public void endFlow(Flow fl) {
        if (bDefer) {
            return;
        }

        try {
            Region regionBody = pagemaster.getRegion(Constants.FO_REGION_BODY);
            Region regionBefore = pagemaster.getRegion(Constants.FO_REGION_BEFORE);
            Region regionAfter = pagemaster.getRegion(Constants.FO_REGION_AFTER);
            if (fl.getFlowName().equals(regionBody.getRegionName())) {
                //just do nothing
            } else if (regionBefore != null 
                    && fl.getFlowName().equals(regionBefore.getRegionName())) {
                builderContext.popContainer();
            } else if (regionAfter != null 
                    && fl.getFlowName().equals(regionAfter.getRegionName())) {
                builderContext.popContainer();
            }
        } catch (Exception e) {
            log.error("endFlow: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startBlock(Block)
     * @param bl Block that is starting
     */
    public void startBlock(Block bl) {
        if (bDefer) {
            return;
        }

        try {
            RtfAttributes rtfAttr
                = TextAttributesConverter.convertAttributes(bl);

            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class,
                    true, this);

            RtfTextrun textrun = container.getTextrun();

            textrun.addParagraphBreak();
            textrun.pushBlockAttributes(rtfAttr);
            textrun.addBookmark(bl.getId());
        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            log.error("startBlock: " + ioe.getMessage());
            throw new RuntimeException("IOException: " + ioe);
        } catch (Exception e) {
            log.error("startBlock: " + e.getMessage());
            throw new RuntimeException("Exception: " + e);
        }
    }


    /**
     * @see com.wisii.fov.fo.FOEventHandler#endBlock(Block)
     * @param bl Block that is ending
     */
    public void endBlock(Block bl) {

        if (bDefer) {
            return;
        }

        try {
            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class,
                    true, this);

            RtfTextrun textrun = container.getTextrun();

            textrun.addParagraphBreak();
            textrun.popBlockAttributes();

        } catch (IOException ioe) {
            log.error("startBlock:" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("startBlock:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startBlockContainer(BlockContainer)
     * @param blc BlockContainer that is starting
     */
    public void startBlockContainer(BlockContainer blc) {
        if (bDefer) {
            return;
        }

        try {
            RtfAttributes rtfAttr
                = TextAttributesConverter.convertBlockContainerAttributes(blc);

            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class,
                    true, this);

            RtfTextrun textrun = container.getTextrun();

            textrun.addParagraphBreak();
            textrun.pushBlockAttributes(rtfAttr);
        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            log.error("startBlock: " + ioe.getMessage());
            throw new RuntimeException("IOException: " + ioe);
        } catch (Exception e) {
            log.error("startBlock: " + e.getMessage());
            throw new RuntimeException("Exception: " + e);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endBlockContainer(BlockContainer)
     * @param bl BlockContainer that is ending
     */
    public void endBlockContainer(BlockContainer bl) {
        if (bDefer) {
            return;
        }

        try {
            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class,
                    true, this);

            RtfTextrun textrun = container.getTextrun();

            textrun.addParagraphBreak();
            textrun.popBlockAttributes();

        } catch (IOException ioe) {
            log.error("startBlock:" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("startBlock:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startTable(Table)
     * @param tbl Table that is starting
     */
    public void startTable(Table tbl) {
        if (bDefer) {
            return;
        }

        // create an RtfTable in the current table container
        TableContext tableContext = new TableContext(builderContext);

        try {
            final IRtfTableContainer tc
                = (IRtfTableContainer)builderContext.getContainer(
                        IRtfTableContainer.class, true, null);
            
            RtfAttributes atts
                = TableAttributesConverter.convertTableAttributes(tbl);
            
            RtfTable table = tc.newTable(atts, tableContext);
            
            CommonBorderPaddingBackground border = tbl.getCommonBorderPaddingBackground();
            RtfAttributes borderAttributes = new RtfAttributes();
                    
            BorderAttributesConverter.makeBorder(border, CommonBorderPaddingBackground.BEFORE,
                    borderAttributes, ITableAttributes.CELL_BORDER_TOP);
            BorderAttributesConverter.makeBorder(border, CommonBorderPaddingBackground.AFTER,
                    borderAttributes, ITableAttributes.CELL_BORDER_BOTTOM);
            BorderAttributesConverter.makeBorder(border, CommonBorderPaddingBackground.START,
                    borderAttributes, ITableAttributes.CELL_BORDER_LEFT);
            BorderAttributesConverter.makeBorder(border, CommonBorderPaddingBackground.END,
                    borderAttributes,  ITableAttributes.CELL_BORDER_RIGHT);
            
            table.setBorderAttributes(borderAttributes);
            
            builderContext.pushContainer(table);
        } catch (Exception e) {
            log.error("startTable:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        builderContext.pushTableContext(tableContext);
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endTable(Table)
     * @param tbl Table that is ending
     */
    public void endTable(Table tbl) {
        if (bDefer) {
            return;
        }

        builderContext.popTableContext();
        builderContext.popContainer();
    }

    /**
    *
    * @param tc TableColumn that is starting;
    */

    public void startColumn(TableColumn tc) {
        if (bDefer) {
            return;
        }

        try {
            /**
             * Pass a SimplePercentBaseContext to getValue in order to
             * avoid a NullPointerException, which occurs when you use
             * proportional-column-width function in column-width attribute.
             * Of course the results won't be correct, but at least the
             * rest of the document will be rendered. Usage of the
             * TableLayoutManager is not welcome due to design reasons and
             * it also does not provide the correct values.
             * TODO: Make proportional-column-width working for rtf output 
             */
             SimplePercentBaseContext context
                = new SimplePercentBaseContext(null,
                                               LengthBase.TABLE_UNITS,
                                               100000);
            
            Integer iWidth
                = new Integer(tc.getColumnWidth().getValue(context) / 1000);
            
            String strWidth = iWidth.toString() + "pt";
            Float width = new Float(
                    FoUnitsConverter.getInstance().convertToTwips(strWidth));
            builderContext.getTableContext().setNextColumnWidth(width);
            builderContext.getTableContext().setNextColumnRowSpanning(
                    new Integer(0), null);
            builderContext.getTableContext().setNextFirstSpanningCol(false);
        } catch (Exception e) {
            log.error("startColumn: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

     /**
     *
     * @param tc TableColumn that is ending;
     */

    public void endColumn(TableColumn tc) {
        if (bDefer) {
            return;
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startHeader(TableBody)
     * @param th TableBody that is starting
     */
    public void startHeader(TableBody th) {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endHeader(TableBody)
     * @param th TableBody that is ending
     */
    public void endHeader(TableBody th) {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startFooter(TableBody)
     * @param tf TableFooter that is starting
     */
    public void startFooter(TableBody tf) {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endFooter(TableBody)
     * @param tf TableFooter that is ending
     */
    public void endFooter(TableBody tf) {
    }

    /**
     *
     * @param inl Inline that is starting.
     */
    public void startInline(Inline inl) {
        if (bDefer) {
            return;
        }

        try {
            RtfAttributes rtfAttr
                = TextAttributesConverter.convertCharacterAttributes(inl);

            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class, true, this);

            RtfTextrun textrun = container.getTextrun();
            textrun.pushInlineAttributes(rtfAttr);
            textrun.addBookmark(inl.getId());
        } catch (IOException ioe) {
            log.error("startInline:" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (FOVException fe) {
            log.error("startInline:" + fe.getMessage());
            throw new RuntimeException(fe.getMessage());
        } catch (Exception e) {
            log.error("startInline:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     *
     * @param inl Inline that is ending.
     */
    public void endInline(Inline inl) {
        if (bDefer) {
            return;
        }

        try {
            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class, true, this);

            RtfTextrun textrun = container.getTextrun();
            textrun.popInlineAttributes();
        } catch (IOException ioe) {
            log.error("startInline:" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("startInline:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

     /**
     * @see com.wisii.fov.fo.FOEventHandler#startBody(TableBody)
     * @param tb TableBody that is starting
     */
    public void startBody(TableBody tb) {
        if (bDefer) {
            return;
        }

        try {
            RtfAttributes atts = TableAttributesConverter.convertTableBodyAttributes(tb);

            RtfTable tbl = (RtfTable)builderContext.getContainer(RtfTable.class, true, this);
            tbl.setHeaderAttribs(atts);
        } catch (Exception e) {
            log.error("startBody: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endBody(TableBody)
     * @param tb TableBody that is ending
     */
    public void endBody(TableBody tb) {
        if (bDefer) {
            return;
        }

        try {
            RtfTable tbl = (RtfTable)builderContext.getContainer(RtfTable.class, true, this);
            tbl.setHeaderAttribs(null);
        } catch (Exception e) {
            log.error("endBody: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startRow(TableRow)
     * @param tr TableRow that is starting
     */
    public void startRow(TableRow tr) {
        if (bDefer) {
            return;
        }

        try {
            // create an RtfTableRow in the current RtfTable
            final RtfTable tbl = (RtfTable)builderContext.getContainer(RtfTable.class,
                    true, null);

            RtfAttributes atts = TableAttributesConverter.convertRowAttributes(tr,
                    tbl.getHeaderAttribs());

            if (tr.getParent() instanceof TableHeader) {
                atts.set(ITableAttributes.ATTR_HEADER);
            }

            builderContext.pushContainer(tbl.newTableRow(atts));

            // reset column iteration index to correctly access column widths
            builderContext.getTableContext().selectFirstColumn();
        } catch (Exception e) {
            log.error("startRow: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endRow(TableRow)
     * @param tr TableRow that is ending
     */
    public void endRow(TableRow tr) {
        if (bDefer) {
            return;
        }
        
        try {
            TableContext tctx = builderContext.getTableContext();
            final RtfTableRow row = (RtfTableRow)builderContext.getContainer(RtfTableRow.class,
                    true, null);

            //while the current column is in row-spanning, act as if
            //a vertical merged cell would have been specified.
            while (tctx.getNumberOfColumns() > tctx.getColumnIndex()
                  && tctx.getColumnRowSpanningNumber().intValue() > 0) {
                RtfTableCell vCell = row.newTableCellMergedVertically(
                        (int)tctx.getColumnWidth(),
                        tctx.getColumnRowSpanningAttrs());
                
                if (!tctx.getFirstSpanningCol()) {
                    vCell.setHMerge(RtfTableCell.MERGE_WITH_PREVIOUS);
                }
                
                tctx.selectNextColumn();
            }
        } catch (Exception e) {
            log.error("endRow: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }


        builderContext.popContainer();
        builderContext.getTableContext().decreaseRowSpannings();
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startCell(TableCell)
     * @param tc TableCell that is starting
     */
    public void startCell(TableCell tc) {
        if (bDefer) {
            return;
        }

        try {
            TableContext tctx = builderContext.getTableContext();
            final RtfTableRow row = (RtfTableRow)builderContext.getContainer(RtfTableRow.class,
                    true, null);

            int numberRowsSpanned = tc.getNumberRowsSpanned();
            int numberColumnsSpanned = tc.getNumberColumnsSpanned();

            //while the current column is in row-spanning, act as if
            //a vertical merged cell would have been specified.
            while (tctx.getNumberOfColumns() > tctx.getColumnIndex()
                  && tctx.getColumnRowSpanningNumber().intValue() > 0) {
                RtfTableCell vCell = row.newTableCellMergedVertically(
                        (int)tctx.getColumnWidth(),
                        tctx.getColumnRowSpanningAttrs());
                
                if (!tctx.getFirstSpanningCol()) {
                    vCell.setHMerge(RtfTableCell.MERGE_WITH_PREVIOUS);
                }
                
                tctx.selectNextColumn();
            }

            //get the width of the currently started cell
            float width = tctx.getColumnWidth();

            // create an RtfTableCell in the current RtfTableRow
            RtfAttributes atts = TableAttributesConverter.convertCellAttributes(tc);
            RtfTableCell cell = row.newTableCell((int)width, atts);
            
            //process number-rows-spanned attribute
            if (numberRowsSpanned > 1) {
                // Start vertical merge
                cell.setVMerge(RtfTableCell.MERGE_START);

                // set the number of rows spanned
                tctx.setCurrentColumnRowSpanning(new Integer(numberRowsSpanned), 
                        cell.getRtfAttributes());
            } else {
                tctx.setCurrentColumnRowSpanning(
                        new Integer(numberRowsSpanned), null);
            }

            //process number-columns-spanned attribute
            if (numberColumnsSpanned > 0) {
                // Get the number of columns spanned
                RtfTable table = row.getTable();
                tctx.setCurrentFirstSpanningCol(true);
                
                // We widthdraw one cell because the first cell is already created
                // (it's the current cell) !
                 for (int i = 0; i < numberColumnsSpanned - 1; ++i) {
                    tctx.selectNextColumn();
                    
                    tctx.setCurrentFirstSpanningCol(false);
                    RtfTableCell hCell = row.newTableCellMergedHorizontally(
                            0, null);
                    
                    if (numberRowsSpanned > 1) {
                        // Start vertical merge
                        hCell.setVMerge(RtfTableCell.MERGE_START);

                        // set the number of rows spanned
                        tctx.setCurrentColumnRowSpanning(
                                new Integer(numberRowsSpanned), 
                                cell.getRtfAttributes());
                    } else {
                        tctx.setCurrentColumnRowSpanning(
                                new Integer(numberRowsSpanned), null);
                    }
                }
            }
            
            builderContext.pushContainer(cell);
        } catch (Exception e) {
            log.error("startCell: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endCell(TableCell)
     * @param tc TableCell that is ending
     */
    public void endCell(TableCell tc) {
        if (bDefer) {
            return;
        }

        builderContext.popContainer();
        builderContext.getTableContext().selectNextColumn();
    }

    // Lists
    /**
     * @see com.wisii.fov.fo.FOEventHandler#startList(ListBlock)
     * @param lb ListBlock that is starting
     */
    public void startList(ListBlock lb) {
        if (bDefer) {
            return;
        }

        try  {
            // create an RtfList in the current list container
            final IRtfListContainer c
                = (IRtfListContainer)builderContext.getContainer(
                    IRtfListContainer.class, true, this);
            final RtfList newList = c.newList(
                ListAttributesConverter.convertAttributes(lb));
            builderContext.pushContainer(newList);
        } catch (IOException ioe) {
            log.error("startList: " + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (FOVException fe) {
            log.error("startList: " + fe.getMessage());
            throw new RuntimeException(fe.getMessage());
        } catch (Exception e) {
            log.error("startList: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endList(ListBlock)
     * @param lb ListBlock that is ending
     */
    public void endList(ListBlock lb) {
        if (bDefer) {
            return;
        }

        builderContext.popContainer();
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startListItem(ListItem)
     * @param li ListItem that is starting
     */
    public void startListItem(ListItem li) {
        if (bDefer) {
            return;
        }
        
        // create an RtfListItem in the current RtfList
        try {
            RtfList list = (RtfList)builderContext.getContainer(
                    RtfList.class, true, this);
            
            /**
             * If the current list already contains a list item, then close the
             * list and open a new one, so every single list item gets its own
             * list. This allows every item to have a different list label.
             * If all the items would be in the same list, they had all the
             * same label.
             */
            //TODO: do this only, if the labels content <> previous labels content
            if (list.getChildCount() > 0) {
                this.endListBody();
                this.endList((ListBlock) li.getParent());
                this.startList((ListBlock) li.getParent());
                this.startListBody();
                
                list = (RtfList)builderContext.getContainer(
                        RtfList.class, true, this);
            }            
            
            builderContext.pushContainer(list.newListItem());
        } catch (IOException ioe) {
            log.error("startList: " + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (FOVException fe) {
            log.error("startList: " + fe.getMessage());
            throw new RuntimeException(fe.getMessage());
        } catch (Exception e) {
            log.error("startList: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endListItem(ListItem)
     * @param li ListItem that is ending
     */
    public void endListItem(ListItem li) {
        if (bDefer) {
            return;
        }

        builderContext.popContainer();
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startListLabel()
     */
    public void startListLabel() {
        if (bDefer) {
            return;
        }

        try {
            RtfListItem item
                = (RtfListItem)builderContext.getContainer(RtfListItem.class, true, this);

            RtfListItemLabel label = item.new RtfListItemLabel(item);
            builderContext.pushContainer(label);
        } catch (IOException ioe) {
            log.error("startPageNumber:" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("startPageNumber: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endListLabel()
     */
    public void endListLabel() {
        if (bDefer) {
            return;
        }

        builderContext.popContainer();
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startListBody()
     */
    public void startListBody() {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endListBody()
     */
    public void endListBody() {
    }

    // Static Regions
    /**
     * @see com.wisii.fov.fo.FOEventHandler#startStatic()
     */
    public void startStatic() {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endStatic()
     */
    public void endStatic() {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startMarkup()
     */
    public void startMarkup() {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endMarkup()
     */
    public void endMarkup() {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startLink(BasicLink basicLink)
     * @param basicLink BasicLink that is starting
     */
    public void startLink(BasicLink basicLink) {
        if (bDefer) {
            return;
        }

        try {
            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class, true, this);

            RtfTextrun textrun = container.getTextrun();

            RtfHyperLink link = textrun.addHyperlink(new RtfAttributes());

            if (basicLink.getExternalDestination() != null) {
                link.setExternalURL(basicLink.getExternalDestination());
            } else {
                link.setInternalURL(basicLink.getInternalDestination());
            }

            builderContext.pushContainer(link);

        } catch (IOException ioe) {
            log.error("startLink:" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("startLink: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endLink()
     */
    public void endLink() {
        if (bDefer) {
            return;
        }

        builderContext.popContainer();
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#image(ExternalGraphic)
     * @param eg ExternalGraphic that is starting
     */
    public void image(ExternalGraphic eg) {
        if (bDefer) {
            return;
        }

        try {
            String url = eg.getURL();

            //set image data
            FOUserAgent userAgent = eg.getUserAgent();
            ImageFactory fact = userAgent.getFactory().getImageFactory();
            FovImage fovimage = fact.getImage(url, userAgent);
            if (fovimage == null) {
                log.error("Image could not be found: " + url);
                return;
            }
            fovimage.load(FovImage.ORIGINAL_DATA);

            byte[] rawData;
            if ("image/svg+xml".equals(fovimage.getMimeType())) {
                rawData = SVGConverter.convertToJPEG((XMLImage)fovimage);
            } else {
                rawData = fovimage.getRessourceBytes();
            }
            if (rawData == null) {
                log.warn(FONode.decorateWithContextInfo(
                        "Image could not be embedded: " + url, eg));
                return;
            }

            final IRtfTextrunContainer c
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class, true, this);

            final RtfExternalGraphic newGraphic = c.getTextrun().newImage();
    
            //set URL
            newGraphic.setURL(url);
            newGraphic.setImageData(rawData);

            //set scaling
            if (eg.getScaling() == Constants.EN_UNIFORM) {
                newGraphic.setScaling ("uniform");
            }

            //get width
            int width = 0;
            if (eg.getWidth().getEnum() == Constants.EN_AUTO) {
                width = fovimage.getIntrinsicWidth();
            } else {
                width = eg.getWidth().getValue();
            }

            //get height
            int height = 0;
            if (eg.getWidth().getEnum() == Constants.EN_AUTO) {
                height = fovimage.getIntrinsicHeight();
            } else {
                height = eg.getHeight().getValue();
            }

            //get content-width
            int contentwidth = 0;
            if (eg.getContentWidth().getEnum()
                    == Constants.EN_AUTO) {
                contentwidth = fovimage.getIntrinsicWidth();
            } else if (eg.getContentWidth().getEnum()
                    == Constants.EN_SCALE_TO_FIT) {
                contentwidth = width;
            } else {
                //TODO: check, if the value is a percent value
                contentwidth = eg.getContentWidth().getValue();
            }

            //get content-width
            int contentheight = 0;
            if (eg.getContentHeight().getEnum()
                    == Constants.EN_AUTO) {

                contentheight = fovimage.getIntrinsicHeight();

            } else if (eg.getContentHeight().getEnum()
                    == Constants.EN_SCALE_TO_FIT) {

                contentheight = height;
            } else {
                //TODO: check, if the value is a percent value
                contentheight = eg.getContentHeight().getValue();
            }

            //set width in rtf
            //newGraphic.setWidth((long) (contentwidth / 1000f) + "pt");
            newGraphic.setWidth((long) (contentwidth / 50f) + "twips");

            //set height in rtf
            //newGraphic.setHeight((long) (contentheight / 1000f) + "pt");
            newGraphic.setHeight((long) (contentheight / 50f) + "twips");

            //TODO: make this configurable:
            //      int compression = m_context.m_options.getRtfExternalGraphicCompressionRate ();
            int compression = 0;
            if (compression != 0) {
                if (!newGraphic.setCompressionRate(compression)) {
                    log.warn("The compression rate " + compression
                        + " is invalid. The value has to be between 1 and 100 %.");
                }
            }
        } catch (Exception e) {
            log.error("Error while handling an external-graphic: " + e.getMessage(), e);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#pageRef()
     */
    public void pageRef() {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#foreignObject(InstreamForeignObject)
     * @param ifo InstreamForeignObject that is starting
     */
    public void foreignObject(InstreamForeignObject ifo) {
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startFootnote(Footnote)
     * @param footnote Footnote that is starting
     */
    public void startFootnote(Footnote footnote) {
        if (bDefer) {
            return;
        }

        try {
            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class,
                    true, this);

            RtfTextrun textrun = container.getTextrun();
            RtfFootnote rtfFootnote = textrun.addFootnote();

            builderContext.pushContainer(rtfFootnote);

        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            log.error("startFootnote: " + ioe.getMessage());
            throw new RuntimeException("IOException: " + ioe);
        } catch (Exception e) {
            log.error("startFootnote: " + e.getMessage());
            throw new RuntimeException("Exception: " + e);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endFootnote(Footnote)
     * @param footnote Footnote that is ending
     */
    public void endFootnote(Footnote footnote) {
        if (bDefer) {
            return;
        }

        builderContext.popContainer();
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#startFootnoteBody(FootnoteBody)
     * @param body FootnoteBody that is starting
     */
    public void startFootnoteBody(FootnoteBody body) {
        if (bDefer) {
            return;
        }

        try {
            RtfFootnote rtfFootnote
                = (RtfFootnote)builderContext.getContainer(
                    RtfFootnote.class,
                    true, this);

            rtfFootnote.startBody();
        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            log.error("startFootnoteBody: " + ioe.getMessage());
            throw new RuntimeException("IOException: " + ioe);
        } catch (Exception e) {
            log.error("startFootnoteBody: " + e.getMessage());
            throw new RuntimeException("Exception: " + e);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#endFootnoteBody(FootnoteBody)
     * @param body FootnoteBody that is ending
     */
    public void endFootnoteBody(FootnoteBody body) {
        if (bDefer) {
            return;
        }

        try {
            RtfFootnote rtfFootnote
                = (RtfFootnote)builderContext.getContainer(
                    RtfFootnote.class,
                    true, this);

            rtfFootnote.endBody();
        } catch (IOException ioe) {
            // TODO could we throw Exception in all FOEventHandler events?
            log.error("endFootnoteBody: " + ioe.getMessage());
            throw new RuntimeException("IOException: " + ioe);
        } catch (Exception e) {
            log.error("endFootnoteBody: " + e.getMessage());
            throw new RuntimeException("Exception: " + e);
        }
    }

    /**
     * @see com.wisii.fov.fo.FOEventHandler#leader(Leader)
     * @param l Leader that is starting
     */
    public void leader(Leader l) {
    }

    /**
     * @param text FOText object
     * @param data Array of characters to process.
     * @param start Offset for characters to process.
     * @param length Portion of array to process.
     */
    public void text(FOText text, char[] data, int start, int length) {
        if (bDefer) {
            return;
        }

        try {
            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class, true, this);

            RtfTextrun textrun = container.getTextrun();
            RtfAttributes rtfAttr
                = TextAttributesConverter.convertCharacterAttributes(text);

            textrun.pushInlineAttributes(rtfAttr);
            textrun.addString(new String(data, start, length - start));
            textrun.popInlineAttributes();
         } catch (IOException ioe) {
            // FIXME could we throw Exception in all FOEventHandler events?
            log.error("characters: " + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("characters:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     *
     * @param pagenum PageNumber that is starting.
     */
    public void startPageNumber(PageNumber pagenum) {
        if (bDefer) {
            return;
        }

        try {
            RtfAttributes rtfAttr
                = TextAttributesConverter.convertCharacterAttributes(
                    pagenum);

            IRtfTextrunContainer container
                = (IRtfTextrunContainer)builderContext.getContainer(
                    IRtfTextrunContainer.class, true, this);

            RtfTextrun textrun = container.getTextrun();
            textrun.addPageNumber(rtfAttr);
        } catch (IOException ioe) {
            log.error("startPageNumber:" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        } catch (Exception e) {
            log.error("startPageNumber: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     *
     * @param pagenum PageNumber that is ending.
     */
    public void endPageNumber(PageNumber pagenum) {
        if (bDefer) {
            return;
        }
    }

    /**
     * Calls the appropriate event handler for the passed FObj.
     *
     * @param foNode FO node whose event is to be called
     * @param bStart TRUE calls the start handler, FALSE the end handler
     */
    private void invokeDeferredEvent(FONode foNode, boolean bStart) {
        if (foNode instanceof PageSequence) {
            if (bStart) {
                startPageSequence( (PageSequence) foNode);
            } else {
                endPageSequence( (PageSequence) foNode);
            }
        } else if (foNode instanceof Flow) {
            if (bStart) {
                startFlow( (Flow) foNode);
            } else {
                endFlow( (Flow) foNode);
            }
        } else if (foNode instanceof StaticContent) {
            if (bStart) {
                startStatic();
            } else {
                endStatic();
            }
        } else if (foNode instanceof ExternalGraphic) {
            if (bStart) {
                image( (ExternalGraphic) foNode );
            }
        } else if (foNode instanceof Block) {
            if (bStart) {
                startBlock( (Block) foNode);
            } else {
                endBlock( (Block) foNode);
            }
        } else if (foNode instanceof BlockContainer) {
            if (bStart) {
                startBlockContainer( (BlockContainer) foNode);
            } else {
                endBlockContainer( (BlockContainer) foNode);
            }
        } else if (foNode instanceof BasicLink) {
            //BasicLink must be placed before Inline
            if (bStart) {
                startLink( (BasicLink) foNode);
            } else {
                endLink();
            }
        } else if (foNode instanceof Inline) {
            if (bStart) {
                startInline( (Inline) foNode);
            } else {
                endInline( (Inline) foNode);
            }
        } else if (foNode instanceof FOText) {
            if (bStart) {
                FOText text = (FOText) foNode;
                text(text, text.ca, text.startIndex, text.endIndex);
            }
        } else if (foNode instanceof Character) {
            if (bStart) {
                Character c = (Character) foNode;
                character(c);
            }
        } else if (foNode instanceof PageNumber) {
            if (bStart) {
                startPageNumber( (PageNumber) foNode);
            } else {
                endPageNumber( (PageNumber) foNode);
            }
        } else if (foNode instanceof Footnote) {
            if (bStart) {
                startFootnote( (Footnote) foNode);
            } else {
                endFootnote( (Footnote) foNode);
            }
        } else if (foNode instanceof FootnoteBody) {
            if (bStart) {
                startFootnoteBody( (FootnoteBody) foNode);
            } else {
                endFootnoteBody( (FootnoteBody) foNode);
            }
        } else if (foNode instanceof ListBlock) {
            if (bStart) {
                startList( (ListBlock) foNode);
            } else {
                endList( (ListBlock) foNode);
            }
        } else if (foNode instanceof ListItemBody) {
            if (bStart) {
                startListBody();
            } else {
                endListBody();
            }
        } else if (foNode instanceof ListItem) {
            if (bStart) {
                startListItem( (ListItem) foNode);
            } else {
                endListItem( (ListItem) foNode);
            }
        } else if (foNode instanceof ListItemLabel) {
            if (bStart) {
                startListLabel();
            } else {
                endListLabel();
            }
        } else if (foNode instanceof Table) {
            if (bStart) {
                startTable( (Table) foNode);
            } else {
                endTable( (Table) foNode);
            }
        } else if (foNode instanceof TableBody) {
            if (bStart) {
                startBody( (TableBody) foNode);
            } else {
                endBody( (TableBody) foNode);
            }
        } else if (foNode instanceof TableColumn) {
            if (bStart) {
                startColumn( (TableColumn) foNode);
            } else {
                endColumn( (TableColumn) foNode);
            }
        } else if (foNode instanceof TableRow) {
            if (bStart) {
                startRow( (TableRow) foNode);
            } else {
                endRow( (TableRow) foNode);
            }
        } else if (foNode instanceof TableCell) {
            if (bStart) {
                startCell( (TableCell) foNode);
            } else {
                endCell( (TableCell) foNode);
            }
        } else {
            log.warn("Ignored deferred event for " + foNode);
        }
    }

    /**
     * Calls the event handlers for the passed FONode and all its elements.
     *
     * @param foNode FONode object which shall be recursed
     */
    private void recurseFONode(FONode foNode) {
        invokeDeferredEvent(foNode, true);

        if (foNode instanceof PageSequence) {
            PageSequence pageSequence = (PageSequence) foNode;

            Region regionBefore = pagemaster.getRegion(Constants.FO_REGION_BEFORE);
            if (regionBefore != null) {
                FONode staticBefore = (FONode) pageSequence.getFlowMap().get(
                        regionBefore.getRegionName());
                if (staticBefore != null) {
                    recurseFONode(staticBefore);
                }
            }
            Region regionAfter = pagemaster.getRegion(Constants.FO_REGION_AFTER);
            if (regionAfter != null) {
                FONode staticAfter = (FONode) pageSequence.getFlowMap().get(
                        regionAfter.getRegionName());
                if (staticAfter != null) {
                    recurseFONode(staticAfter);
                }
            }


            recurseFONode( pageSequence.getMainFlow() );
        } else if (foNode instanceof Table) {
            Table table = (Table) foNode;

            //recurse all table-columns
            if (table.getColumns() != null) {
                for (Iterator it = table.getColumns().iterator(); it.hasNext();) {
                    recurseFONode( (FONode) it.next() );
                }
            } else {
                //TODO Implement implicit column setup handling!
                log.warn("No table-columns found on table. RTF output requires that all"
                        + " table-columns for a table are defined. Output will be incorrect.");
            }

            //recurse table-header
            if (table.getTableHeader() != null) {
                recurseFONode( table.getTableHeader() );
            }

            //recurse table-footer
            if (table.getTableFooter() != null) {
                recurseFONode( table.getTableFooter() );
            }

            if (foNode.getChildNodes() != null) {
                for (Iterator it = foNode.getChildNodes(); it.hasNext();) {
                    recurseFONode( (FONode) it.next() );
                }
            }
        } else if (foNode instanceof ListItem) {
            ListItem item = (ListItem) foNode;

            recurseFONode(item.getLabel());
            recurseFONode(item.getBody());
        } else if (foNode instanceof Footnote) {
            Footnote fn = (Footnote)foNode;

            recurseFONode(fn.getFootnoteCitation());
            recurseFONode(fn.getFootnoteBody());
        } else {
            //Any other FO-Object: Simply recurse through all childNodes.
            if (foNode.getChildNodes() != null) {
                for (Iterator it = foNode.getChildNodes(); it.hasNext();) {
                    FONode fn = (FONode)it.next();
                    if (log.isTraceEnabled()) {
                        log.trace("  ChildNode for " + fn + " (" + fn.getName() + ")");
                    }
                    recurseFONode(fn);
                }
            }
        }

        invokeDeferredEvent(foNode, false);
    }
}
