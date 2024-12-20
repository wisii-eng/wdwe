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
 */package com.wisii.fov.fo;

// Java
import java.util.HashSet;
import java.util.Set;
import org.xml.sax.SAXException;

// Apache
import com.wisii.fov.apps.FOUserAgent;
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
import com.wisii.fov.fo.flow.PageNumber;
import com.wisii.fov.fo.flow.Table;
import com.wisii.fov.fo.flow.TableBody;
import com.wisii.fov.fo.flow.TableCell;
import com.wisii.fov.fo.flow.TableColumn;
import com.wisii.fov.fo.flow.TableRow;
import com.wisii.fov.fo.pagination.Flow;
import com.wisii.fov.fo.pagination.PageSequence;
import com.wisii.fov.fonts.FontInfo;


/**
 * Abstract class defining what should be done with SAX events that map to XSL-FO input. The events are actually
 * captured by fo/FOTreeBuilder, passed to the various fo Objects, which in turn, if needed, pass them to an instance
 * of FOEventHandler.
 * Sub-classes will generally fall into one of two categories:
 * 1) a handler that actually builds an FO Tree from the events, or
 * 2) a handler that builds a structured (as opposed to formatted) document, such as our MIF and RTF output targets.
 */
public abstract class FOEventHandler
{
    /** The FOUserAgent for this process  */
    protected FOUserAgent foUserAgent;

    /** The Font information relevant for this document     */
    protected FontInfo fontInfo;

    /** The current set of id's in the FO tree. This is used so we know if the FO tree contains duplicates.     */
    private Set idReferences = new HashSet();

    /** The property list maker.  */
    protected PropertyListMaker propertyListMaker;

    /** The XMLWhitespaceHandler for this tree   */
    protected XMLWhiteSpaceHandler whiteSpaceHandler = new XMLWhiteSpaceHandler();

    /** Indicates whether processing descendants of a marker     */
    private boolean inMarker = false;

    /**
     * Main constructor
     * @param foUserAgent the apps.FOUserAgent instance for this process
     */
    public FOEventHandler(FOUserAgent foUserAgent)
    {
        this.foUserAgent = foUserAgent;
        this.fontInfo = new FontInfo();
    }

    /**
     * Retuns the set of ID references.
     * @return the ID references
     */
    public Set getIDReferences()
    {
        return idReferences;
    }

    /**
     * Returns the User Agent object associated with this FOEventHandler.
     * @return the User Agent object
     */
    public FOUserAgent getUserAgent()
    {
        return foUserAgent;
    }

    /**
     * Retrieve the font information for this document
     * @return the FontInfo instance for this document
     */
    public FontInfo getFontInfo()
    {
        return this.fontInfo;
    }

    /** @return the propertyListMaker.    */
    public PropertyListMaker getPropertyListMaker()
    {
        return propertyListMaker;
    }

    /** Set a new propertyListMaker.     */
    public void setPropertyListMaker(PropertyListMaker propertyListMaker)
    {
        this.propertyListMaker = propertyListMaker;
    }

    /**
     * Return the XMLWhiteSpaceHandler
     * @return the whiteSpaceHandler
     */
    public XMLWhiteSpaceHandler getXMLWhiteSpaceHandler()
    {
        return whiteSpaceHandler;
    }

    /** Switch to or from marker context (used by FOTreeBuilder when processing a marker)  */
    protected void switchMarkerContext(boolean inMarker)
    {
        this.inMarker = inMarker;
    }

    /** Check whether in marker context     */
    protected boolean inMarker()
    {
        return this.inMarker;
    }

    /**
     * This method is called to indicate the start of a new document run.
     * @throws SAXException In case of a problem
     */
    public void startDocument() throws SAXException { }

    /**
     * This method is called to indicate the end of a document run.
     * @throws SAXException In case of a problem
     */
    public void endDocument() throws SAXException { }

    /** @param pageSeq PageSequence that is starting.    */
    public void startPageSequence(PageSequence pageSeq) { }

    /** @param pageSeq PageSequence that is ending.   */
    public void endPageSequence(PageSequence pageSeq) { }

    /** @param pagenum PageNumber that is starting.     */
    public void startPageNumber(PageNumber pagenum) { }

    /** @param pagenum PageNumber that is ending.  */
    public void endPageNumber(PageNumber pagenum) { }

    /**
     * This method is called to indicate the start of a new fo:flow or fo:static-content.
     * This method also handles fo:static-content tags, because the StaticContent class is derived from the Flow class.
     * @param fl Flow that is starting.
     */
    public void startFlow(Flow fl) { }

    /** @param fl Flow that is ending.   */
    public void endFlow(Flow fl) { }

    /** @param bl Block that is starting.    */
    public void startBlock(Block bl) { }

    /** @param bl Block that is ending.  */
    public void endBlock(Block bl) { }

    /** @param blc BlockContainer that is starting.    */
    public void startBlockContainer(BlockContainer blc) { }

    /** @param blc BlockContainer that is ending.    */
    public void endBlockContainer(BlockContainer blc) { }

    /** @param inl Inline that is starting.     */
    public void startInline(Inline inl) { }

    /** @param inl Inline that is ending.   */
    public void endInline(Inline inl) { }

    // Tables
    /** @param tbl Table that is starting.   */
    public void startTable(Table tbl) { }

    /** @param tbl Table that is ending.   */
    public void endTable(Table tbl) { }

    /** @param tc TableColumn that is starting;     */
    public void startColumn(TableColumn tc) { }

    /** @param tc TableColumn that is ending;   */
    public void endColumn(TableColumn tc) { }

    /** @param th TableBody that is starting;  */
    public void startHeader(TableBody th) { }

    /** @param th TableBody that is ending.   */
    public void endHeader(TableBody th) {  }

    /** @param tf TableFooter that is starting.   */
    public void startFooter(TableBody tf) { }

    /** @param tf TableFooter that is ending.  */
    public void endFooter(TableBody tf) { }

    /** @param tb TableBody that is starting.   */
    public void startBody(TableBody tb) { }

    /** @param tb TableBody that is ending.     */
    public void endBody(TableBody tb) { }

    /** @param tr TableRow that is starting.   */
    public void startRow(TableRow tr) { }

    /** @param tr TableRow that is ending.     */
    public void endRow(TableRow tr) { }

    /** @param tc TableCell that is starting.   */
    public void startCell(TableCell tc) { }

    /** @param tc TableCell that is ending.     */
    public void endCell(TableCell tc) { }


    // Lists
    /** @param lb ListBlock that is starting.     */
    public void startList(ListBlock lb) { }

    /** @param lb ListBlock that is ending.     */
    public void endList(ListBlock lb) { }

    /** @param li ListItem that is starting.     */
    public void startListItem(ListItem li) { }

    /** @param li ListItem that is ending.      */
    public void endListItem(ListItem li) { }

    /** Process start of a ListLabel.  */
    public void startListLabel() { }

    /** Process end of a ListLabel.    */
    public void endListLabel() { }

    /** Process start of a ListBody.     */
    public void startListBody() { }

    /** Process end of a ListBody.      */
    public void endListBody() { }

    // Static Regions
    /** Process start of a Static.      */
    public void startStatic() { }

    /** Process end of a Static.     */
    public void endStatic() { }


    /** Process start of a Markup.  */
    public void startMarkup() { }

    /** Process end of a Markup.     */
    public void endMarkup() { }

    /**
     * Process start of a Link.
     * @param basicLink BasicLink that is ending
     */
    public void startLink(BasicLink basicLink) { }

    /** Process end of a Link.     */
    public void endLink() { }

    /**
     * Process an ExternalGraphic.
     * @param eg ExternalGraphic to process.
     */
    public void image(ExternalGraphic eg) { }

    /** Process a pageRef.     */
    public void pageRef() { }

    /**
     * Process an InstreamForeignObject.
     * @param ifo InstreamForeignObject to process.
     */
    public void foreignObject(InstreamForeignObject ifo) { }

    /**
     * Process the start of a footnote.
     * @param footnote Footnote that is starting
     */
    public void startFootnote(Footnote footnote) { }

    /**
     * Process the ending of a footnote.
     * @param footnote Footnote that is ending
     */
    public void endFootnote(Footnote footnote) { }

    /**
     * Process the start of a footnote body.
     * @param body FootnoteBody that is starting
     */
    public void startFootnoteBody(FootnoteBody body) { }

    /**
     * Process the ending of a footnote body.
     * @param body FootnoteBody that is ending
     */
    public void endFootnoteBody(FootnoteBody body) { }

    /**
     * Process a Leader.
     * @param l Leader to process.
     */
    public void leader(Leader l) { }

    /**
     * Process a Character.
     * @param c Character to process.
     */
    public void character(Character c) { }

    /**
     * Process character data.
     * @param data Array of characters to process.
     * @param start Offset for characters to process.
     * @param length Portion of array to process.
     */
    public void characters(char data[], int start, int length) { }

}

