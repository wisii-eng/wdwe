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
 */package com.wisii.fov.area;

// Java
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

// XML
import org.xml.sax.SAXException;

// Apache
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FormattingResults;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.FOEventHandler;
import com.wisii.fov.fo.extensions.ExtensionAttachment;
import com.wisii.fov.fo.pagination.PageSequence;
import com.wisii.fov.fo.pagination.Root;
import com.wisii.fov.fo.pagination.bookmarks.BookmarkTree;
import com.wisii.fov.layoutmgr.PageSequenceLayoutManager;
import com.wisii.fov.layoutmgr.LayoutManagerMaker;
import com.wisii.fov.layoutmgr.LayoutManagerMapping;

/**
 * Area tree handler for formatting objects.
 * Concepts:
 * The area tree is to be as small as possible. With minimal classes and data to fully represent an area tree for
 * formatting objects. The area tree needs to be simple to render and follow the spec closely. This area tree has the
 * concept of page sequences. Wherever possible information is discarded or optimized to keep memory use low. The data
 * is also organized to make it possible for renderers to minimize their output. A page can be saved if not fully
 * resolved and once rendered a page contains only size and id reference information. The area tree pages are organized
 * in a model that depends on the type of renderer.
 */
public class AreaTreeHandler extends FOEventHandler
{
    private static Log log = LogFactory.getLog(AreaTreeHandler.class);

    // show statistics after document complete?
    private boolean outputStatistics;

    // for statistics gathering
    private Runtime runtime;

    // heap memory allocated (for statistics)
    private long initialMemory;

    // time used in rendering (for statistics)
    private long startTime;

    /** the LayoutManager maker */
    private LayoutManagerMaker lmMaker;

    /** AreaTreeModel in use */
    protected AreaTreeModel model;

    // The fo:root node of the document
    private Root rootFObj;

    // HashMap of ID's whose area is located on one or more consecutive  PageViewports.  Each ID has an arraylist of
    // PageViewports that form the defined area of this ID
    private Map idLocations = new HashMap();

    // idref's whose target PageViewports have yet to be identified
    // Each idref has a HashSet of Resolvable objects containing that idref
    private Map unresolvedIDRefs = new HashMap();

    private Set unfinishedIDs = new HashSet();
    private Set alreadyResolvedIDs = new HashSet();

     // The formatting results to be handed back to the caller.
    private FormattingResults results = new FormattingResults();

    private PageSequenceLayoutManager prevPageSeqLM;

    private int idGen = 0;

    /**
     * Constructor.
     * @param userAgent FOUserAgent object for process
     * @param outputFormat the MIME type of the output format to use (ex. "application/pdf").
     * @param stream OutputStream
     * @throws FOVException if the RenderPagesModel cannot be created
     */
    public AreaTreeHandler (FOUserAgent userAgent, String outputFormat, OutputStream stream) throws FOVException
    {
        super(userAgent);
        setupModel(userAgent, outputFormat, stream);

        lmMaker = userAgent.getFactory().getLayoutManagerMakerOverride();
        if (lmMaker == null)
            lmMaker = new LayoutManagerMapping();

        outputStatistics = log.isDebugEnabled();
        if (outputStatistics)
            runtime = Runtime.getRuntime();
    }

    /**
     * Sets up the AreaTreeModel instance for use by the AreaTreeHandler.
     * @param userAgent FOUserAgent object for process
     * @param outputFormat the MIME type of the output format to use (ex. "application/pdf").
     * @param stream OutputStream
     * @throws FOVException if the RenderPagesModel cannot be created
     */
    protected void setupModel(FOUserAgent userAgent, String outputFormat, OutputStream stream) throws FOVException
    {
        model = new RenderPagesModel(userAgent, outputFormat, fontInfo, stream);
    }

    /**
     * Get the area tree model for this area tree.
     * @return AreaTreeModel the model being used for this area tree
     */
    public AreaTreeModel getAreaTreeModel()
    {
        return model;
    }

    /**
     * Get the LayoutManager maker for this area tree.
     * @return LayoutManagerMaker the LayoutManager maker being used for this area tree
     */
    public LayoutManagerMaker getLayoutManagerMaker()
    {
        return lmMaker;
    }

    /**
     * Tie a PageViewport with an ID found on a child area of the PV.
     * Note that an area with a given ID may be on more than one PV, hence
     * an ID may have more than one PV associated with it.
     * @param id the property ID of the area
     * @param pv a page viewport that contains the area with this ID
     */
    public void associateIDWithPageViewport(String id, PageViewport pv)
    {
        if (log.isDebugEnabled()) log.debug("associateIDWithPageViewport(" + id + ", " + pv + ")");
        List pvList = (List) idLocations.get(id);
        if (pvList == null)
        { // first time ID located
            pvList = new ArrayList();
            idLocations.put(id, pvList);
            pvList.add(pv);

            // See if this ID is in the unresolved idref list, if so resolve Resolvable objects tied to it.
            if (!unfinishedIDs.contains(id))
                tryIDResolution(id, pv, pvList);
        }
        else
            pvList.add(pv);
    }

    /**
     * This method tie an ID to the areaTreeHandler until this one is ready to be processed. This is used in
     * page-number-citation-last processing so we know when an id can be resolved.
     * @param id the id of the object being processed
     */
    public void signalPendingID(String id)
    {
        if (log.isDebugEnabled())
            log.debug("signalPendingID(" + id + ")");
        unfinishedIDs.add(id);
    }

    /**
     * Signals that all areas for the formatting object with the given ID have been generated.
     * This is used to determine when page-number-citation-last ref-ids can be resolved.
     * @param id the id of the formatting object which was just finished
     */
    public void signalIDProcessed(String id)
    {
        if (log.isDebugEnabled()) log.debug("signalIDProcessed(" + id + ")");

        alreadyResolvedIDs.add(id);
        if (!unfinishedIDs.contains(id))
            return;
        unfinishedIDs.remove(id);

        List pvList = (List) idLocations.get(id);
        Set todo = (Set) unresolvedIDRefs.get(id);
        if (todo != null)
        {
            for (Iterator iter = todo.iterator(); iter.hasNext();)
            {
                Resolvable res = (Resolvable) iter.next();
                res.resolveIDRef(id, pvList);
            }
            unresolvedIDRefs.remove(id);
        }
    }

    /**
     * Check if an ID has already been resolved
     * @param id the id to check
     * @return true if the ID has been resolved
     */
    public boolean alreadyResolvedID(String id)
    {
        return (alreadyResolvedIDs.contains(id));
    }

    /**
     * Tries to resolve all unresolved ID references on the given page.
     * @param id ID to resolve
     * @param pv page viewport whose ID refs to resolve
     * @param List of PageViewports
     */
    private void tryIDResolution(String id, PageViewport pv, List pvList)
    {
        Set todo = (Set) unresolvedIDRefs.get(id);
        if (todo != null)
        {
            for (Iterator iter = todo.iterator(); iter.hasNext();)
            {
                Resolvable res = (Resolvable) iter.next();
                if (!unfinishedIDs.contains(id))
                    res.resolveIDRef(id, pvList);
                else
                    return;
            }
            alreadyResolvedIDs.add(id);
            unresolvedIDRefs.remove(id);
        }
    }

    /**
     * Tries to resolve all unresolved ID references on the given page.
     * @param pv page viewport whose ID refs to resolve
     */
    public void tryIDResolution(PageViewport pv)
    {
        String[] ids = pv.getIDRefs();
        if (ids != null)
        {
            for (int i = 0; i < ids.length; i++)
            {
                List pvList = (List) idLocations.get(ids[i]);
                if (pvList != null)
                    tryIDResolution(ids[i], pv, pvList);
            }
        }
    }

    /**
     * Get the list of page viewports that have an area with a given id.
     * @param id the id to lookup
     * @return the list of PageViewports
     */
    public List getPageViewportsContainingID(String id)
    {
        return (List) idLocations.get(id);
    }

    /**
     * Get information about the rendered output, like
     * number of pages created.
     * @return the results structure
     */
    public FormattingResults getResults()
    {
        return this.results;
    }

    /**
     * Add an Resolvable object with an unresolved idref
     * @param idref the idref whose target id has not yet been located
     * @param res the Resolvable object needing the idref to be resolved
     */
    public void addUnresolvedIDRef(String idref, Resolvable res)
    {
        Set todo = (Set) unresolvedIDRefs.get(idref);
        if (todo == null)
        {
            todo = new HashSet();
            unresolvedIDRefs.put(idref, todo);
        }
        // add Resolvable object to this HashSet
        todo.add(res);
    }

    /**
     * Prepare AreaTreeHandler for document processing
     * This is called from FOTreeBuilder.startDocument()
     * @throws SAXException if there is an error
     */
    public void startDocument() throws SAXException
    {
        //Initialize statistics
        if (outputStatistics)
        {
            initialMemory = runtime.totalMemory() - runtime.freeMemory();
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * finish the previous pageSequence
     */
    private void finishPrevPageSequence(Numeric initialPageNumber)
    {
        if (prevPageSeqLM != null)
        {
            prevPageSeqLM.doForcePageCount(initialPageNumber);
            prevPageSeqLM.finishPageSequence();
            prevPageSeqLM = null;
        }
    }

    /** @see com.wisii.fov.fo.FOEventHandler */
    public void startPageSequence(PageSequence pageSequence)
    {
        rootFObj = pageSequence.getRoot();
        finishPrevPageSequence(pageSequence.getInitialPageNumber());
        pageSequence.initPageNumber();
        //extension attachments from fo:root
        wrapAndAddExtensionAttachments(rootFObj.getExtensionAttachments());
        //extension attachments from fo:declarations
        if (rootFObj.getDeclarations() != null)
            wrapAndAddExtensionAttachments(rootFObj.getDeclarations().getExtensionAttachments());
    }

    private void wrapAndAddExtensionAttachments(List list)
    {
        Iterator i = list.iterator();
        while (i.hasNext())
        {
            ExtensionAttachment attachment = (ExtensionAttachment)i.next();
            addOffDocumentItem(new OffDocumentExtensionAttachment(attachment));
        }
    }

    /**
     * End the PageSequence.
     * The PageSequence formats Pages and adds them to the AreaTree.
     * The area tree then handles what happens with the pages.
     * @param pageSequence the page sequence ending
     */
    public void endPageSequence(PageSequence pageSequence)
    {
        if (outputStatistics)
        {
            long memoryNow = runtime.totalMemory() - runtime.freeMemory();
            log.debug("Current heap size: " + (memoryNow / 1024L) + "Kb");
        }

        // If no main flow, nothing to layout!
        if (pageSequence.getMainFlow() != null)
        {
            PageSequenceLayoutManager pageSLM;
            pageSLM = getLayoutManagerMaker().makePageSequenceLayoutManager(this, pageSequence);
            pageSLM.activateLayout();
            // preserve the current PageSequenceLayoutManger for the
            // force-page-count check at the beginning of the next PageSequence
            prevPageSeqLM = pageSLM;
        }
    }

    /**
     * Called by the PageSequenceLayoutManager when it is finished with a page-sequence.
     * @param pageSequence the page-sequence just finished
     * @param pageCount The number of pages generated for the page-sequence
     */
    public void notifyPageSequenceFinished(PageSequence pageSequence, int pageCount)
    {
        this.results.haveFormattedPageSequence(pageSequence, pageCount);
        if (log.isDebugEnabled()) log.debug("Last page-sequence produced " + pageCount + " pages.");
    }

    /**
     * End the document.
     * @throws SAXException if there is some error
     */
    public void endDocument() throws SAXException
    {
        finishPrevPageSequence(null);
        // process fo:bookmark-tree
        BookmarkTree bookmarkTree = rootFObj.getBookmarkTree();
        if (bookmarkTree != null)
        {
            BookmarkData data = new BookmarkData(bookmarkTree);
            addOffDocumentItem(data);
            if (!data.isResolved())
            {
                //bookmarks did not fully resolve, add anyway. (hacky? yeah)
                model.handleOffDocumentItem(data);
            }
        }

        model.endDocument();

        if (outputStatistics)
        {
            long memoryNow = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = (memoryNow - initialMemory) / 1024L;
            long timeUsed = System.currentTimeMillis() - startTime;
            int pageCount = rootFObj.getTotalPagesGenerated();
            log.debug("Initial heap size: " + (initialMemory / 1024L) + "Kb");
            log.debug("Current heap size: " + (memoryNow / 1024L) + "Kb");
            log.debug("Total memory used: " + memoryUsed + "Kb");
            log.debug("Total time used: " + timeUsed + "ms");
            log.debug("Pages rendered: " + pageCount);
            if (pageCount > 0)
            {
                long perPage = (timeUsed / pageCount);
                long ppm = (timeUsed != 0 ? Math.round(60000 * pageCount / (double)timeUsed) : -1);
                log.debug("Avg render time: " + perPage + "ms/page (" + ppm + "pages/min)");
            }
        }
    }

    /**
     * Add a OffDocumentItem to the area tree model
     * This checks if the OffDocumentItem is resolvable and attempts to resolve or add the resolvable ids for later resolution.
     * @param odi the OffDocumentItem to add.
     */
    private void addOffDocumentItem(OffDocumentItem odi)
    {
        if (odi instanceof Resolvable)
        {
            Resolvable res = (Resolvable) odi;
            String[] ids = res.getIDRefs();
            for (int count = 0; count < ids.length; count++)
            {
                if (idLocations.containsKey(ids[count]))
                {
                    res.resolveIDRef(ids[count], (List) idLocations.get(ids[count]));
                }
                else
                {
                    log.warn(odi.getName() + ": Unresolved id reference \"" + ids[count] + "\" found.");
                    addUnresolvedIDRef(ids[count], res);
                }
            }
            // check to see if ODI is now fully resolved, if so process it
            if (res.isResolved())
            {
                model.handleOffDocumentItem(odi);
            }
        } else {
            model.handleOffDocumentItem(odi);
        }
    }

    /**
     * Generates and returns a unique key for a page viewport.
     * @return the generated key.
     */
    public String generatePageViewportKey()
    {
        this.idGen++;
        return "P" + this.idGen;
    }

}

