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
 *//* $Id: PageSequence.java,v 1.2 2007/09/19 04:58:39 lzy Exp $ */

package com.wisii.fov.fo.pagination;

// Java
import java.util.Map;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;

/**
 * Implementation of the fo:page-sequence formatting object.
 */
public class PageSequence extends FObj {

    // The value of properties relevant for fo:page-sequence.
    private String country;
    private String format;
    private String language;
    private int letterValue;
    private char groupingSeparator;
    private int groupingSize;
    private String id;
    private Numeric initialPageNumber;
    private int forcePageCount;
    private String masterReference;
    // End of property values

    /** The parent root object */
    private Root root;

    // There doesn't seem to be anything in the spec requiring flows
    // to be in the order given, only that they map to the regions
    // defined in the page sequence, so all we need is this one hashmap
    // the set of flows includes StaticContent flows also

    /** Map of flows to their flow name (flow-name, Flow) */
    private Map flowMap;

    private int startingPageNumber = 0;
    private PageNumberGenerator pageNumberGenerator;

    /**
     * The currentSimplePageMaster is either the page master for the
     * whole page sequence if master-reference refers to a simple-page-master,
     * or the simple page master produced by the page sequence master otherwise.
     * The pageSequenceMaster is null if master-reference refers to a
     * simple-page-master.
     */
    private SimplePageMaster simplePageMaster;
    private PageSequenceMaster pageSequenceMaster;

    /**
     * The fo:title object for this page-sequence.
     */
    private Title titleFO;

    /**
     * The fo:flow object for this page-sequence.
     */
    private Flow mainFlow = null;

    /**
     * Create a page sequence FO node.
     *
     * @param parent the parent FO node
     */
    public PageSequence(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        country = pList.get(PR_COUNTRY).getString();
        format = pList.get(PR_FORMAT).getString();
        language = pList.get(PR_LANGUAGE).getString();
        letterValue = pList.get(PR_LETTER_VALUE).getEnum();
        groupingSeparator = pList.get(PR_GROUPING_SEPARATOR).getCharacter();
        groupingSize = pList.get(PR_GROUPING_SIZE).getNumber().intValue();
        id = pList.get(PR_ID).getString();
        initialPageNumber = pList.get(PR_INITIAL_PAGE_NUMBER).getNumeric();
        forcePageCount = pList.get(PR_FORCE_PAGE_COUNT).getEnum();
        masterReference = pList.get(PR_MASTER_REFERENCE).getString();

        if (masterReference == null || masterReference.equals("")) {
            missingPropertyError("master-reference");
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode()
     */
    protected void startOfNode() throws FOVException {
        this.root = (Root) parent;
        flowMap = new java.util.HashMap();

        this.simplePageMaster = root.getLayoutMasterSet().getSimplePageMaster(masterReference);
        if (this.simplePageMaster == null) {
            this.pageSequenceMaster
                    = root.getLayoutMasterSet().getPageSequenceMaster(masterReference);
            if (this.pageSequenceMaster == null) {
                throw new ValidationException("master-reference '" + masterReference
                   + "fo:page-sequence必须为simple-page-master 或 page-sequence-master", locator);
            } else {
                pageSequenceMaster.reset();
            }
        }

        this.pageNumberGenerator = new PageNumberGenerator(
                format, groupingSeparator, groupingSize, letterValue);

        checkId(id);
        getFOEventHandler().startPageSequence(this);
    }

    /** @see com.wisii.fov.fo.FONode#endOfNode() */
    protected void endOfNode() throws FOVException {
        if (mainFlow == null) {
           missingChildElementError("(title?,static-content*,flow)");
        }

        getFOEventHandler().endPageSequence(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
        XSL Content Model: (title?,static-content*,flow)
     */
    protected void validateChildNode(Locator loc, String nsURI, String localName)
        throws ValidationException {
        if (FO_URI.equals(nsURI)) {
            if (localName.equals("title")) {
                if (titleFO != null) {
                    tooManyNodesError(loc, "fo:title");
                } else if (flowMap.size() > 0) {
                    nodesOutOfOrderError(loc, "fo:title", "fo:static-content");
                } else if (mainFlow != null) {
                    nodesOutOfOrderError(loc, "fo:title", "fo:flow");
                }
            } else if (localName.equals("static-content")) {
                if (mainFlow != null) {
                    nodesOutOfOrderError(loc, "fo:static-content", "fo:flow");
                }
            } else if (localName.equals("flow")) {
                if (mainFlow != null) {
                    tooManyNodesError(loc, "fo:flow");
                }
            } else {
                invalidChildError(loc, nsURI, localName);
            }
        } else {
            invalidChildError(loc, nsURI, localName);
        }
    }

    /**
     * @see com.wisii.fov.fo.FONode#addChildNode(FONode)
     * @todo see if addChildNode() should also be called for fo's other than
     *  fo:flow.
     */
    public void addChildNode(FONode child) throws FOVException {
        int childId = child.getNameId();

        if (childId == FO_TITLE) {
            this.titleFO = (Title) child;
        } else if (childId == FO_FLOW) {
            this.mainFlow = (Flow) child;
            addFlow(mainFlow);
        } else if (childId == FO_STATIC_CONTENT) {
            addFlow((StaticContent) child);
            String flowName = ((StaticContent) child).getFlowName();
            flowMap.put(flowName, child);
        }
    }

    /**
     * Add a flow or static content, mapped by its flow-name.
     * The flow-name is used to associate the flow with a region on a page,
     * based on the region-names given to the regions in the page-master
     * used to generate that page.
     */
     private void addFlow(Flow flow) throws ValidationException {
        String flowName = flow.getFlowName();

        if (hasFlowName(flowName)) {
            throw new ValidationException("相同的 flow-name \""
                + flowName
                + "\" 在 fo:page-sequence内部找到", flow.getLocator());
        }

        if (!root.getLayoutMasterSet().regionNameExists(flowName)
            && !flowName.equals("xsl-before-float-separator")
            && !flowName.equals("xsl-footnote-separator")) {
                throw new ValidationException("flow-name \""
                    + flowName
                    + "\" 不能映射到layout-master-set的一个region-name中", flow.getLocator());
        }
    }

    /**
     * Initialize the current page number for the start of the page sequence.
     */
    public void initPageNumber() {
        int pageNumberType = 0;

        if (initialPageNumber.getEnum() != 0) {
            // auto | auto-odd | auto-even.
            startingPageNumber = root.getEndingPageNumberOfPreviousSequence() + 1;
            pageNumberType = initialPageNumber.getEnum();
            if (pageNumberType == EN_AUTO_ODD) {
//                if (startingPageNumber % 2 == 0)
                if ((startingPageNumber & 1) == 0)
                {
                    startingPageNumber++;
                }
            } else if (pageNumberType == EN_AUTO_EVEN) {
//                if (startingPageNumber % 2 == 1)
                if ((startingPageNumber & 1) == 1)
                {
                    startingPageNumber++;
                }
            }
        } else { // <integer> for explicit page number
            int pageStart = initialPageNumber.getValue();
            startingPageNumber = (pageStart > 0) ? pageStart : 1; // spec rule
        }
    }

//     /**
//      * Returns true when there is more flow elements left to lay out.
//      */
//     private boolean flowsAreIncomplete() {
//         boolean isIncomplete = false;

//         for (Iterator e = flowMap.values().iterator(); e.hasNext(); ) {
//             Flow flow = (Flow)e.next();
//             if (flow instanceof StaticContent) {
//                 continue;
//             }

//             Status status = flow.getStatus();
//             isIncomplete |= status.isIncomplete();
//         }
//         return isIncomplete;
//     }

//     /**
//      * Returns the flow that maps to the given region class for the current
//      * page master.
//      */
//     private Flow getCurrentFlow(String regionClass) {
//         Region region = getCurrentSimplePageMaster().getRegion(regionClass);
//         if (region != null) {
//             Flow flow = (Flow)flowMap.get(region.getRegionName());
//             return flow;

//         } else {

//             getLogger().error("flow is null. regionClass = '" + regionClass
//                                + "' currentSPM = "
//                                + getCurrentSimplePageMaster());

//             return null;
//         }

//     }

//      private boolean isFlowForMasterNameDone(String masterName) {
//          // parameter is master-name of PMR; we need to locate PM
//          // referenced by this, and determine whether flow(s) are OK
//          if (isForcing)
//              return false;
//          if (masterName != null) {

//              SimplePageMaster spm =
//                  root.getLayoutMasterSet().getSimplePageMaster(masterName);
//              Region region = spm.getRegion(FO_REGION_BODY);


//              Flow flow = (Flow)flowMap.get(region.getRegionName());
//              /*if ((null == flow) || flow.getStatus().isIncomplete())
//                  return false;
//              else
//                  return true;*/
//          }
//          return false;
//      }

    /**
     * Get the starting page number for this page sequence.
     *
     * @return the starting page number
     */
    public int getStartingPageNumber() {
        return startingPageNumber;
    }

//     private void forcePage(AreaTree areaTree, int firstAvailPageNumber) {
//         boolean makePage = false;
//         if (this.forcePageCount == ForcePageCount.AUTO) {
//             PageSequence nextSequence =
//                 this.root.getSucceedingPageSequence(this);
//             if (nextSequence != null) {
//                 if (nextSequence.getIpnValue().equals("auto")) {
//                     // do nothing special
//                 }
//                 else if (nextSequence.getIpnValue().equals("auto-odd")) {
//                     if (firstAvailPageNumber % 2 == 0) {
//                         makePage = true;
//                     }
//                 } else if (nextSequence.getIpnValue().equals("auto-even")) {
//                     if (firstAvailPageNumber % 2 != 0) {
//                         makePage = true;
//                     }
//                 } else {
//                     int nextSequenceStartPageNumber =
//                         nextSequence.getCurrentPageNumber();
//                     if ((nextSequenceStartPageNumber % 2 == 0)
//                             && (firstAvailPageNumber % 2 == 0)) {
//                         makePage = true;
//                     } else if ((nextSequenceStartPageNumber % 2 != 0)
//                                && (firstAvailPageNumber % 2 != 0)) {
//                         makePage = true;
//                     }
//                 }
//             }
//         } else if ((this.forcePageCount == ForcePageCount.EVEN)
//                    && (this.pageCount % 2 != 0)) {
//             makePage = true;
//         } else if ((this.forcePageCount == ForcePageCount.ODD)
//                    && (this.pageCount % 2 == 0)) {
//             makePage = true;
//         } else if ((this.forcePageCount == ForcePageCount.END_ON_EVEN)
//                    && (firstAvailPageNumber % 2 == 0)) {
//             makePage = true;
//         } else if ((this.forcePageCount == ForcePageCount.END_ON_ODD)
//                    && (firstAvailPageNumber % 2 != 0)) {
//             makePage = true;
//         } else if (this.forcePageCount == ForcePageCount.NO_FORCE) {
//             // do nothing
//         }

//         if (makePage) {
//             try {
//                 this.isForcing = true;
//                 this.currentPageNumber++;
//                 firstAvailPageNumber = this.currentPageNumber;
//                 currentPage = makePage(areaTree, firstAvailPageNumber, false,
//                                        true);
//                 String formattedPageNumber =
//                     pageNumberGenerator.makeFormattedPageNumber(this.currentPageNumber);
//                 currentPage.setFormattedNumber(formattedPageNumber);
//                 currentPage.setPageSequence(this);
//                 formatStaticContent(areaTree);
//                 log.debug("[forced-" + firstAvailPageNumber + "]");
//                 areaTree.addPage(currentPage);
//                 this.root.setRunningPageNumberCounter(this.currentPageNumber);
//                 this.isForcing = false;
//             } catch (FOVException fovex) {
//                 log.debug("'force-page-count' failure");
//             }
//         }
//     }

    /**
     * Get the static content FO node from the flow map.
     * This gets the static content flow for the given flow name.
     *
     * @param name the flow name to find
     * @return the static content FO node
     */
    public StaticContent getStaticContent(String name) {
        return (StaticContent) flowMap.get(name);
    }

    /** @return the "id" property. */
    public String getId() {
        return id;
    }

    /**
     * Accessor method for titleFO
     * @return titleFO for this object
     */
    public Title getTitleFO() {
        return titleFO;
    }

    /**
     * Public accessor for getting the MainFlow to which this PageSequence is
     * attached.
     * @return the MainFlow object to which this PageSequence is attached.
     */
    public Flow getMainFlow() {
        return mainFlow;
    }

    /**
     * Determine if this PageSequence already has a flow with the given flow-name
     * Used for validation of incoming fo:flow or fo:static-content objects
     * @param flowName The flow-name to search for
     * @return true if flow-name already defined within this page sequence,
     *    false otherwise
     */
    public boolean hasFlowName(String flowName) {
        return flowMap.containsKey(flowName);
    }

    /** @return the flow map for this page-sequence */
    public Map getFlowMap() {
        return this.flowMap;
    }

    /**
     * Public accessor for determining the next page master to use within this page sequence.
     * @param page the page number of the page to be created
     * @param isFirstPage indicator whether this page is the first page of the
     *      page sequence
     * @param isLastPage indicator whether this page is the last page of the
     *      page sequence
     * @param isBlank indicator whether the page will be blank
     * @return the SimplePageMaster to use for this page
     * @throws FOVException if there's a problem determining the page master
     */
    public SimplePageMaster getNextSimplePageMaster(int page,
            boolean isFirstPage,
            boolean isLastPage,
            boolean isBlank) throws FOVException {

        if (pageSequenceMaster == null) {
            return simplePageMaster;
        }
//        boolean isOddPage = ((page % 2) == 1);
        boolean isOddPage = ((page & 1) == 1);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("getNextSimplePageMaster(page=" + page
                    + " isOdd=" + isOddPage
                    + " isFirst=" + isFirstPage
                    + " isLast=" + isLastPage
                    + " isBlank=" + isBlank + ")");
        }
        return pageSequenceMaster.getNextSimplePageMaster(isOddPage,
            isFirstPage, isLastPage, isBlank);
    }

    /**
     * Used to set the "cursor position" for the page masters to the previous item.
     * @return true if there is a previous item, false if the current one was the first one.
     */
    public boolean goToPreviousSimplePageMaster() {
        if (pageSequenceMaster == null) {
            return true;
        } else {
            return pageSequenceMaster.goToPreviousSimplePageMaster();
        }
    }

    /** @return true if the page-sequence has a page-master with page-position="last" */
    public boolean hasPagePositionLast() {
        if (pageSequenceMaster == null) {
            return false;
        } else {
            return pageSequenceMaster.hasPagePositionLast();
        }
    }

    /**
     * Retrieves the string representation of a page number applicable
     * for this page sequence
     * @param pageNumber the page number
     * @return string representation of the page number
     */
    public String makeFormattedPageNumber(int pageNumber) {
        return pageNumberGenerator.makeFormattedPageNumber(pageNumber);
    }

    /**
     * Public accessor for the ancestor Root.
     * @return the ancestor Root
     */
    public Root getRoot() {
        return root;
    }

    /** @return the "master-reference" property. */
    public String getMasterReference() {
        return masterReference;
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return "page-sequence";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_PAGE_SEQUENCE;
    }

    /** @return the force-page-count value */
    public int getForcePageCount() {
        return forcePageCount;
    }

    /** @return the initial-page-number property value */
    public Numeric getInitialPageNumber() {
        return initialPageNumber;
    }

    /** @return the country property value */
    public String getCountry() {
        return this.country;
    }

    /** @return the language property value */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Releases a page-sequence's children after the page-sequence has been fully processed.
     */
    public void releasePageSequence() {
        this.mainFlow = null;
        this.flowMap.clear();
    }

}
