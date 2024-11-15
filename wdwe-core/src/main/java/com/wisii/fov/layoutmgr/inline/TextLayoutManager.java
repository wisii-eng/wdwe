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
 *//* $Id: TextLayoutManager.java,v 1.31 2007/11/27 03:27:51 lzy Exp $ */

package com.wisii.fov.layoutmgr.inline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.transform.TransformerException;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FOText;
import com.wisii.fov.fo.flow.Inline;
import com.wisii.fov.fonts.Font;
import com.wisii.fov.layoutmgr.InlineKnuthSequence;
import com.wisii.fov.layoutmgr.KnuthBox;
import com.wisii.fov.layoutmgr.KnuthElement;
import com.wisii.fov.layoutmgr.KnuthGlue;
import com.wisii.fov.layoutmgr.KnuthPenalty;
import com.wisii.fov.layoutmgr.KnuthSequence;
import com.wisii.fov.layoutmgr.LayoutContext;
import com.wisii.fov.layoutmgr.LeafPosition;
import com.wisii.fov.layoutmgr.Position;
import com.wisii.fov.layoutmgr.PositionIterator;
import com.wisii.fov.layoutmgr.TraitSetter;
import com.wisii.fov.traits.MinOptMax;
import com.wisii.fov.traits.SpaceVal;
import com.wisii.fov.util.CharUtilities;
import com.wisii.fov.util.SAXConversionList;

/**
 * LayoutManager for text (a sequence of characters) which generates one or more
 * inline areas.
 */
public class TextLayoutManager extends LeafNodeLayoutManager
{


    /**
     * Store information about each potential text area. Index of character
     * which ends the area, IPD of area, including any word-space and
     * letter-space. Number of word-spaces?
     */
    private class AreaInfo
    {
        private short iStartIndex;

        private short iBreakIndex;

        private short iWScount;

        private short iLScount;

        private MinOptMax ipdArea;

        private boolean bHyphenated;

        private boolean isSpace;


        public AreaInfo(short iSIndex, short iBIndex, short iWS, short iLS,
                        MinOptMax ipd, boolean bHyph, boolean isSpace)
        {
            iStartIndex = iSIndex;
            iBreakIndex = iBIndex;
            iWScount = iWS;
            iLScount = iLS;
            ipdArea = ipd;
            bHyphenated = bHyph;
            this.isSpace = isSpace;
        }

        //--------------------Add by 【李晓光】-----------------------
        private boolean isAddBlank = false;
        public boolean isAddBlank()
        {
            return isAddBlank;
        }

        public void setAddBlank(boolean isAddBlank)
        {
            this.isAddBlank = isAddBlank;
        }

        //--------------------Add by 【李晓光】-----------------------

        public String toString()
        {
            return "[ lscnt=" + iLScount + ", wscnt=" + iWScount + ", ipd="
                + ipdArea.toString() + ", sidx=" + iStartIndex + ", bidx="
                + iBreakIndex + ", hyph=" + bHyphenated + ", space="
                + isSpace + "]";
        }

    }


    // this class stores information about changes in vecAreaInfo
    // which are not yet applied
    private class PendingChange
    {
        public AreaInfo ai;

        public int index;

        public PendingChange(AreaInfo ai, int index)
        {
            this.ai = ai;
            this.index = index;
        }
    }


    // Hold all possible breaks for the text in this LM's FO.
    private ArrayList vecAreaInfo;

    /** Non-space characters on which we can end a line. */
    private static final String BREAK_CHARS = "-/,.?!:;'(){}[]\"";
//    private static final String BREAK_CHARS = "-/,.?!:;'({[\"";
    /**
     * Used to reduce instantiation of MinOptMax with zero length. Do not
     * modify!
     */
    private static final MinOptMax ZERO_MINOPTMAX = new MinOptMax(0);

    private FOText foText;

    private char[] textArray;

    /**
     * Contains an array of widths to adjust for kerning. The first entry can be
     * used to influence the start position of the first letter. The entry i+1
     * defines the cursor advancement after the character i. A null entry means
     * no special advancement.
     */
    private MinOptMax[] letterAdjustArray; // size = textArray.length + 1

    private static final char NEWLINE = '\n';

    private Font font = null;

    /** Start index of first character in this parent Area */
    private short iAreaStart = 0;

    /** Start index of next TextArea */
    private short iNextStart = 0;

    /** Size since last makeArea call, except for last break */
    private MinOptMax ipdTotal;

    /** Size including last break possibility returned */
    // private MinOptMax nextIPD = new MinOptMax(0);
    /** size of a space character (U+0020) glyph in current font */
    private int spaceCharIPD;

    private volatile MinOptMax wordSpaceIPD;
    //---------------Add by 【李晓光】-------------------
    private MinOptMax tempSpaceIPD;
    private MinOptMax tempSpaceIPDBy3;
    //---------------Add by 【李晓光】--------------------
    private MinOptMax letterSpaceIPD;

    /** size of the hyphen character glyph in current font */
    private int hyphIPD;

    /** 1/1 of word-spacing value */
    private SpaceVal ws;

    /** 1/2 of word-spacing value */
    private SpaceVal halfWS;

    /** 1/2 of letter-spacing value */
    private SpaceVal halfLS;

    /** Number of space characters after previous possible break position. */
    private int iNbSpacesPending;

    private boolean bChanged = false;

    private int iReturnedIndex = 0;

    private short iThisStart = 0;

    private short iTempStart = 0;

    private LinkedList changeList = null;

    private AlignmentContext alignmentContext = null;

    private int lineStartBAP = 0;

    private int lineEndBAP = 0;

    private static TextLayoutManager lastedManager;

    private TextLayoutManager nextManager;

    private TextLayoutManager privousManager;

    /**
     * Create a Text layout manager.
     *
     * @param node
     *            The FOText object to be rendered
     */
    public TextLayoutManager(FOText node)
    {
        super();
        foText = node;

        textArray = new char[node.endIndex - node.startIndex];

        System.arraycopy(node.ca,
                         node.startIndex, textArray, 0,
                         node.endIndex
                         - node.startIndex);

        letterAdjustArray = new MinOptMax[textArray.length + 1];

        vecAreaInfo = new java.util.ArrayList();
    }

    /** @see com.wisii.fov.layoutmgr.LayoutManager#initialize */
    public void initialize()
    {
        font = foText.getCommonFont().getFontState(
            foText.getFOEventHandler().getFontInfo(), this);
        // With CID fonts, space isn't neccesary currentFontState.width(32)
        spaceCharIPD = font.getCharWidth(' ');
        // Use hyphenationChar property
        hyphIPD = font
                  .getCharWidth(foText.getCommonHyphenation().hyphenationCharacter);

        SpaceVal ls = SpaceVal.makeLetterSpacing(foText.getLetterSpacing());
        halfLS = new SpaceVal(MinOptMax.multiply(ls.getSpace(), 0.5), ls
                              .isConditional(), ls.isForcing(), ls.getPrecedence());

        ws = SpaceVal.makeWordSpacing(foText.getWordSpacing(), ls, font);
        // Make half-space: <space> on either side of a word-space)
        halfWS = new SpaceVal(MinOptMax.multiply(ws.getSpace(), 1 / 3), ws
                              .isConditional(), ws.isForcing(), ws.getPrecedence());

        // letter space applies only to consecutive non-space characters,
        // while word space applies to space characters;
        // i.e. the spaces in the string "A SIMPLE TEST" are:
        // A<<ws>>S<ls>I<ls>M<ls>P<ls>L<ls>E<<ws>>T<ls>E<ls>S<ls>T
        // there is no letter space after the last character of a word,
        // nor after a space character
        // NOTE: The above is not quite correct. Read on in XSL 1.0, 7.16.2,
        // letter-spacing

        // set letter space and word space dimension;
        // the default value "normal" was converted into a MinOptMax value
        // in the SpaceVal.makeWordSpacing() method
        letterSpaceIPD = ls.getSpace();
        wordSpaceIPD = MinOptMax
                       .add(new MinOptMax(spaceCharIPD), ws.getSpace());

        //---------------Add by 【李晓光】-------------------
        tempSpaceIPD = MinOptMax
                       .add(new MinOptMax(spaceCharIPD), ws.getSpace());
//        wordSpaceIPD = tempSpaceIPD;

        for(int i = 0; i < textArray.length; i++)
        {
            char ch = textArray[i];
            if(isChineseCharacters(ch))
            {       	
            	wordSpaceIPD = new MinOptMax(0, 0, tempSpaceIPD.opt / 3);
                break;
            }
        }
//        if(textArray != null && textArray.length > 0 && isChineseCharacters(textArray[0]))
//        wordSpaceIPD = new MinOptMax(0, 0, tempSpaceIPD.opt / 3);
//        wordSpaceIPD = tempSpaceIPDBy3;
        //---------------Add by 【李晓光】--------------------

        //----------------Add by 【李晓光】--------------------
//        wordSpaceIPD.max = wordSpaceIPD.opt /(int) 3;
//        wordSpaceIPD.opt = 0;
//        wordSpaceIPD.min = 0;
        //----------------Add by 【李晓光】--------------------
        // if the text node is son of an inline, set vertical align
        if(foText.getParent() instanceof Inline)
        {
            Inline fobj = (Inline)foText.getParent();
        }
    }


    //----------------Add by 【李晓光】--------------------
    /**
     * Reset position for returning next BreakPossibility.
     *
     * @param prevPos
     *            the position to reset to
     */
    public void resetPosition(Position prevPos)
    {
        if(prevPos != null)
        {
            // ASSERT (prevPos.getLM() == this)
            if(prevPos.getLM() != this)
            {
                log.error("TextLayoutManager.resetPosition: "
                          + "LM mismatch!!!");
            }
            LeafPosition tbp = (LeafPosition)prevPos;
            AreaInfo ai = (AreaInfo)vecAreaInfo.get(tbp.getLeafPos());
            if(ai.iBreakIndex != iNextStart)
            {
                iNextStart = ai.iBreakIndex;
                vecAreaInfo.ensureCapacity(tbp.getLeafPos() + 1);
                // TODO: reset or recalculate total IPD = sum of all word IPD
                // up to the break position
                ipdTotal = ai.ipdArea;
                setFinished(false);
            }
        }
        else
        {
            // Reset to beginning!
            vecAreaInfo.clear();
            iNextStart = 0;
            setFinished(false);
        }
    }

    // TODO: see if we can use normal getNextBreakPoss for this with
    // extra hyphenation information in LayoutContext
    private boolean getHyphenIPD(HyphContext hc, MinOptMax hyphIPD)
    {
        // Skip leading word-space before calculating count?
        boolean bCanHyphenate = true;
        int iStopIndex = iNextStart + hc.getNextHyphPoint();

        if(textArray.length < iStopIndex)
        {
            iStopIndex = textArray.length;
            bCanHyphenate = false;
        }
        hc.updateOffset(iStopIndex - iNextStart);

        for(; iNextStart < iStopIndex; iNextStart++)
        {
            char c = textArray[iNextStart];
            hyphIPD.opt += font.getCharWidth(c);
            // letter-space?
        }
        // Need to include hyphen size too, but don't count it in the
        // stored running total, since it would be double counted
        // with later hyphenation points
        return bCanHyphenate;
    }

    /**
     * Generate and add areas to parent area. This can either generate an area
     * for each TextArea and each space, or an area containing all text with a
     * parameter controlling the size of the word space. The latter is most
     * efficient for PDF generation. Set size of each area.
     *
     * @param posIter
     *            Iterator over Position information returned by this
     *            LayoutManager.
     * @param context
     *            LayoutContext for adjustments
     */
    public void addAreas(PositionIterator posIter, LayoutContext context)
    {

        // Add word areas
        AreaInfo ai = null;
        int iWScount = 0;
        int iLScount = 0;
        int firstAreaInfoIndex = -1;
        int lastAreaInfoIndex = 0;
        MinOptMax realWidth = new MinOptMax(0);

        /*
         * On first area created, add any leading space. Calculate word-space
         * stretch value.
         */
        while(posIter.hasNext())
        {
            LeafPosition tbpNext = (LeafPosition)posIter.next();
            if(tbpNext == null)
            {
                continue; // Ignore elements without Positions
            }
            if(tbpNext.getLeafPos() != -1)
            {
                ai = (AreaInfo)vecAreaInfo.get(tbpNext.getLeafPos());
                if(firstAreaInfoIndex == -1)
                {
                    firstAreaInfoIndex = tbpNext.getLeafPos();
                }
                iWScount += ai.iWScount;
                iLScount += ai.iLScount;
                realWidth.add(ai.ipdArea);
                lastAreaInfoIndex = tbpNext.getLeafPos();
            }
        }
        if(ai == null)
        {
            return;
        }
        //------------------Add by 【李晓光】----------------
//        if(ai.isChinsesSpace())
//            wordSpaceIPD = tempSpaceIPDBy3;
//        else if(ai.isSpace)
//            wordSpaceIPD = tempSpaceIPD;
//        else
//            wordSpaceIPD = tempSpaceIPD;
        // 执行到此时为：一行中最后一个字符【当前字符】
        //------------------Add by 【李晓光】----------------
        int textLength = ai.iBreakIndex - ai.iStartIndex;
        if(ai.iLScount == textLength && context.isLastArea())
        {
            // the line ends at a character like "/" or "-";
            // remove the letter space after the last character
            realWidth.add(MinOptMax.multiply(letterSpaceIPD, -1));
            iLScount--;
        }

        for(int i = ai.iStartIndex; i < ai.iBreakIndex; i++)
        {
            MinOptMax ladj = letterAdjustArray[i + 1];
            if(ladj != null && ladj.isElastic())
            {
                iLScount++;
            }
        }

        // add hyphenation character if the last word is hyphenated
        if(context.isLastArea() && ai.bHyphenated)
        {
            realWidth.add(new MinOptMax(hyphIPD));
        }

        // Calculate adjustments
        //--------------------add by lxg ----------------
//        MinOptMax wordSpaceIPD = getWordSpace(ai);
        //--------------------add by lxg-----------------
        int iDifference = 0;
        int iTotalAdjust = 0;
        int iWordSpaceDim = wordSpaceIPD.opt;
        int iLetterSpaceDim = letterSpaceIPD.opt;
        double dIPDAdjust = context.getIPDAdjust();
        double dSpaceAdjust = context.getSpaceAdjust(); // not used

        // calculate total difference between real and available width
        if(dIPDAdjust > 0.0)
        {
            iDifference = (int)((double)(realWidth.max - realWidth.opt) * dIPDAdjust);
        }
        else
        {
            iDifference = (int)((double)(realWidth.opt - realWidth.min) * dIPDAdjust);
        }

        // set letter space adjustment
        if(dIPDAdjust > 0.0)
        {
            iLetterSpaceDim += (int)((double)(letterSpaceIPD.max - letterSpaceIPD.opt) * dIPDAdjust);
        }
        else
        {
            iLetterSpaceDim += (int)((double)(letterSpaceIPD.opt - letterSpaceIPD.min) * dIPDAdjust);
        }
        iTotalAdjust += (iLetterSpaceDim - letterSpaceIPD.opt) * iLScount;

        // set word space adjustment
        //
        if(iWScount > 0)
        {
            iWordSpaceDim += (int)((iDifference - iTotalAdjust) / iWScount);
        }
        else
        {
            // there are no word spaces in this area
        }
        iTotalAdjust += (iWordSpaceDim - wordSpaceIPD.opt) * iWScount;
        if(iTotalAdjust != iDifference)
        {
            // the applied adjustment is greater or smaller than the needed one
            log
                .trace("TextLM.addAreas: error in word / letter space adjustment = "
                       + (iTotalAdjust - iDifference));
            // set iTotalAdjust = iDifference, so that the width of the TextArea
            // will counterbalance the error and the other inline areas will be
            // placed correctly
            iTotalAdjust = iDifference;
        }

        TextArea t = createTextArea(realWidth, iTotalAdjust, context,
                                    (wordSpaceIPD.opt) - spaceCharIPD, firstAreaInfoIndex,
                                    lastAreaInfoIndex, context.isLastArea());


        // iWordSpaceDim is computed in relation to wordSpaceIPD.opt
        // but the renderer needs to know the adjustment in relation
        // to the size of the space character in the current font;
        // moreover, the pdf renderer adds the character spacing even to
        // the last character of a word and to space characters: in order
        // to avoid this, we must subtract the letter space width twice;
        // the renderer will compute the space width as:
        // space width =
        // = "normal" space width + letterSpaceAdjust + wordSpaceAdjust
        // = spaceCharIPD + letterSpaceAdjust +
        // + (iWordSpaceDim - spaceCharIPD - 2 * letterSpaceAdjust)
        // = iWordSpaceDim - letterSpaceAdjust
        t.setTextLetterSpaceAdjust(iLetterSpaceDim);
        t.setTextWordSpaceAdjust(iWordSpaceDim - spaceCharIPD - 2
                                 * t.getTextLetterSpaceAdjust());
        if(context.getIPDAdjust() != 0)
        {
            // add information about space width
            t.setSpaceDifference((wordSpaceIPD.opt) - spaceCharIPD - 2
                                 * t.getTextLetterSpaceAdjust());
        }
        parentLM.addChildArea(t);
    }

    /**
     * Create an inline word area. This creates a TextArea and sets up the
     * various attributes.
     *
     * @param width
     *            the MinOptMax width of the content
     * @param adjust
     *            the total ipd adjustment with respect to the optimal width
     * @param context
     *            the layout context
     * @param spaceDiff
     *            unused
     * @param firstIndex
     *            the index of the first AreaInfo used for the TextArea
     * @param lastIndex
     *            the index of the last AreaInfo used for the TextArea
     * @param isLastArea
     *            is this TextArea the last in a line?
     * @return the new text area
     */
    protected TextArea createTextArea(MinOptMax width, int adjust,
                                      LayoutContext context, int spaceDiff, int firstIndex,
                                      int lastIndex, boolean isLastArea)
    {
        TextArea textArea;
        if(context.getIPDAdjust() == 0.0)
        {
            // create just a TextArea
            textArea = new TextArea();
        }
        else
        {
            // justified area: create a TextArea with extra info
            // about potential adjustments
            textArea = new TextArea(width.max - width.opt, width.opt
                                    - width.min, adjust);
        }
        textArea.setIPD(width.opt + adjust);
        textArea.setBPD(font.getAscender() - font.getDescender());
        textArea.setBaselineOffset(font.getAscender());
        if(textArea.getBPD() == alignmentContext.getHeight())
        {
            textArea.setOffset(0);
        }
        else
        {
            textArea.setOffset(alignmentContext.getOffset());
        }

        // set the text of the TextArea, split into words and spaces
        int wordStartIndex = -1;
        AreaInfo areaInfo;
        for(int i = firstIndex; i <= lastIndex; i++)
        {
            areaInfo = (AreaInfo)vecAreaInfo.get(i);
            if(areaInfo.isSpace)
            {
                // areaInfo stores information about spaces
                // add the spaces to the TextArea
                for(int j = areaInfo.iStartIndex; j < areaInfo.iBreakIndex; j++)
                {
                    char spaceChar = textArray[j];
                    textArea.addSpace(spaceChar, 0, CharUtilities
                                      .isAdjustableSpace(spaceChar));
                }
            }
            else
            {
                // areaInfo stores information about a word fragment
                if(wordStartIndex == -1)
                {
                    // here starts a new word
                    wordStartIndex = areaInfo.iStartIndex;
                }
                if(i == lastIndex
                   || ((AreaInfo)vecAreaInfo.get(i + 1)).isSpace)
                {
                    // here ends a new word
                    // add a word to the TextArea
                    int len = areaInfo.iBreakIndex - wordStartIndex;
                    String wordChars = new String(textArray, wordStartIndex,
                                                  len);
                    if(isLastArea && i == lastIndex && areaInfo.bHyphenated)
                    {
                        // add the hyphenation character
                        wordChars += foText.getCommonHyphenation().hyphenationCharacter;
                    }
                    int[] letterAdjust = new int[wordChars.length()];
                    int lsCount = areaInfo.iLScount;
                    for(int letter = 0; letter < len; letter++)
                    {
                        MinOptMax adj = letterAdjustArray[letter
                                        + wordStartIndex];
                        if(letter > 0)
                        {
                            letterAdjust[letter] = (adj != null ? adj.opt : 0);
                        }
                        if(lsCount > 0)
                        {
                            letterAdjust[letter] += textArea
                                .getTextLetterSpaceAdjust();
                            lsCount--;
                        }
                    }
                    textArea.addWord(wordChars, 0, letterAdjust);
                    wordStartIndex = -1;
                }
            }
        }
        TraitSetter.addFontTraits(textArea, font);
        textArea.addTrait(Trait.COLOR, foText.getColor());

        TraitSetter.addTextDecoration(textArea, foText.getTextDecoration());

        /*
         * add by akl.在textArea对象中为其_editmode, _xpath, _conversionMap属性赋值。
         */
        textArea.setEditMode(foText.getEditmode());
        textArea.setXPath(foText.getXpath());
        textArea.setConversion(getConversionFromURL(foText.getTranslateurl()));

        //add by xuhao
//        if(textArea.getEditMode() % 2 != 0)
        if((textArea.getEditMode() & 1) != 0)
        { //判断当前的TextArea是否为可编辑项,为可编辑项才关联隐藏项
            // 前隐藏项计算
            TextLayoutManager tempManager = privousManager;
            TextArea parentArea = textArea;
            while(tempManager != null)
            {
                FOText tempFo = tempManager.foText;
                TextArea tempArea = new TextArea();

                tempArea.setXPath(tempFo.getXpath());
                tempArea.setHideName(tempFo.getHidename());
                tempArea.setEditMode(tempFo.getEditmode());
                tempArea.setConversion(getConversionFromURL(tempFo.getTranslateurl()));

                parentArea.setPrevious(tempArea);
                parentArea = tempArea;
                tempManager = tempManager.privousManager;
            }

            //后隐藏项计算
            tempManager = nextManager;
            parentArea = textArea;
            while(tempManager != null)
            {

                FOText tempFo = tempManager.foText;
                TextArea tempArea = new TextArea();

                tempArea.setXPath(tempFo.getXpath());
                tempArea.setHideName(tempFo.getHidename());
                tempArea.setEditMode(tempFo.getEditmode());
                tempArea.setConversion(getConversionFromURL(tempFo.getTranslateurl()));

                parentArea.setNext(tempArea);
                parentArea = tempArea;
                tempManager = tempManager.nextManager;
            }
        }
        //add end
        textArea.setHideName(foText.getHidename());

        return textArea;
    }

    /**
     * 得到ConversionMap, add by zkl.
     * @param translateurl
     * @return
     */
    private List getConversionFromURL(String translateurl)
    {
        if(translateurl == null || "".equals(translateurl.trim()))
        {
            return null;
        }

        if(foText.getUserAgent().getTranlatetable() != null)
        {
            Object tempMap = foText.getUserAgent().getTranlatetable().get(translateurl);
           
            if(tempMap != null)
            {
            	 if(tempMap instanceof HashMap)
            	 {
                return SAXConversionList.getList((Map)tempMap);
            	 }
            	 if(tempMap instanceof ArrayList)
            	 {
                return (List)tempMap;
            	 }
            	 
            }
        }

        InputStream inputStream = null;

        File tempFile = new File(translateurl);
        if(tempFile.exists() && tempFile.isFile())
        {
            try
            {
                inputStream = tempFile.toURL().openStream();
            }
            catch(MalformedURLException mfue)
            {
                mfue.printStackTrace();
                return null;
            }
            catch(IOException io)
            {
                io.printStackTrace();
                return null;
            }
        }
        else
        {
            inputStream = getInputStreamFromURL(translateurl);
        }

        if(inputStream != null)
        {
            try
            {
                List result = SAXConversionList.getConversionList(inputStream);
                if(foText.getUserAgent().getTranlatetable() == null)
                {
                    foText.getUserAgent().setTranlatetable(new HashMap());
                }
                foText.getUserAgent().getTranlatetable().put(translateurl, result);
                return result;
            }
            catch(TransformerException e)
            {
                e.printStackTrace();
                return null;
            }
            finally
            {
                try
                {
                    inputStream.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * add by zkl. 2007-04-28.
     * 判断字符串是否为有效的URL并取得输入流。
     * @param urlStr
     * @return
     */
    private InputStream getInputStreamFromURL(String href)
    {
        URL absoluteURL = null;
//        String urlStr = SystemUtil.getBaseURL().trim() + SystemUtil.CONFRELATIVEPATH + href;
        String urlStr = foText.getUserAgent().getBaseURL().trim() + SystemUtil.CONFRELATIVEPATH + href;

        try
        {
            absoluteURL = new URL(urlStr);
        }
        catch(MalformedURLException mue)
        {
            try
            {
                absoluteURL = new URL("file:" + urlStr);
            }
            catch(MalformedURLException mfue)
            {
                System.out.println("new URL failed. URL is:" + urlStr);
                return null;
            }
        }

        try
        {
            return absoluteURL.openStream();
        }
        catch(IOException ex)
        {
            System.out.println("打开URL流 失败. URL is:" + absoluteURL);
            return null;
        }
    }

    private void addToLetterAdjust(int index, int width)
    {
        if(letterAdjustArray[index] == null)
        {
            letterAdjustArray[index] = new MinOptMax(width);
        }
        else
        {
            letterAdjustArray[index].add(width);
        }
    }

    private void addToLetterAdjust(int index, MinOptMax width)
    {
        if(letterAdjustArray[index] == null)
        {
            letterAdjustArray[index] = new MinOptMax(width);
        }
        else
        {
            letterAdjustArray[index].add(width);
        }
    }

    /**
     * Indicates whether a character is a space in terms of this layout manager.
     *
     * @param ch
     *            the character
     * @return true if it's a space
     */
    private static boolean isSpace(final char ch)
    {
        return ch == CharUtilities.SPACE || ch == CharUtilities.NBSPACE
            || CharUtilities.isFixedWidthSpace(ch);
    }

    //----------------------------lxg---------------------
//    private boolean flag = false;
    private boolean flag = true;
    //----------------------------lxg---------------------

    private static boolean isBreakChar(final char ch)
    {
        // return (BREAK_CHARS.indexOf(ch) >= 0);
        // modified by zq,then treate the chinese word as BreakChar
        //  && Character.getType(ch) == Character.END_PUNCTUATION
        return(BREAK_CHARS.indexOf(ch) >= 0); //|| ((ch & 0xff00) != 0); //原来的做法
//        return(BREAK_CHARS.indexOf(ch) >= 0) || isChineseCharacters(ch);
//        return (isChineseSeparator(ch)) || isChineseCharacters(ch);
    }

    private static boolean isBreakChar(final char first, final char second)
    {
        //-------------------lxg----------------------
        if(isPunctuation(first))
        {
            if(isEndChineseSeparator(second)) // isPunctuation(second)
            {
                return false;
            }
            else if(isStartChineseSeparator(first))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if(isChineseCharacters(first))
        {
            if(isEndChineseSeparator(second))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            if(isChineseCharacters(second))
            {
                return true;
            }
            else if(isStartChineseSeparator(second))
            {
                return true;
            }
            return false;
        }

        //-----------------lxg------------------------
//        return(isChineseSeparator(first) && Character.getType(first) != Character.START_PUNCTUATION
//               && Character.getType(first) != Character.INITIAL_QUOTE_PUNCTUATION)
//            || (isChineseCharacters(first) && (Character.getType(second) != Character.END_PUNCTUATION)
//                && Character.getType(second) != Character.FINAL_QUOTE_PUNCTUATION
//                && Character.getType(second) != Character.OTHER_PUNCTUATION)
//            || ((Character.getType(second) == Character.START_PUNCTUATION)
//                || (!isChineseSeparator(first) && isChineseCharacters(second))); //  && (Character.getType(first) == Character.END_PUNCTUATION)

//        return (isChineseSeparator(first) && isEndChineseSeparator(first, second)) ||
//            (isChineseSeparator(first) && isStartChineseSeparator(first, second));
    }

    private static boolean isBreakChar(int index, char[] chars)
    {
        if(chars == null || chars.length == 0)
        {
            return false;
        }
        if(isPunctuation(chars[index]))
        {
            if(isPunctuation(chars[index + 1]))
            {
                return false;
            }
            else if(isStartChineseSeparator(chars[index]))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if(isChineseCharacters(chars[index]))
        {
            if(isEndChineseSeparator(chars[index + 1]))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判定指定的字符是否为汉字【包括日文汉字，韩国的汉字】
     * @param ch char 指定被判定的字符
     * @return boolean 如果是汉字：True 否则 False
     */
    public static boolean isChineseCharacters(char ch)
    {

        return(ch > 0x4E00) && (ch < 0x9FBF);
    }

    /**
     * 判断给定的字符是不是标点符号。
     * @param ch char 指定被判定的字符
     * @return boolean 如果是标点符号：True 否则：False
     */
//    private static boolean isChineseSeparator(char ch)
//    {
//        return(ch > 0x3000 && ch < 0x303F) || BREAK_CHARS.indexOf(ch) >= 0
//            || (ch > 0xFF00 && ch < 0xFFEF);
//        return isPunctuation(ch);
//    }

    /**
     * 判断指定的符号是不是标点符号【指定所有的标点符号】
     * @param ch char  指定被检查的字符
     * @return boolean 如果指定的字符是标点符号：True 否则：Ffalse
     */
    public static boolean isPunctuation(char ch)
    {
        return(ch > 0x3000 && ch < 0x303F) ||
            isAsciiPunctuation(ch) ||
            (ch > 0x2000 && ch < 0x206F) ||
            (ch > 0xFF00 && ch < 0xFFEF) ||
            BREAK_CHARS.indexOf(ch) >= 0;

    }

    /**
     * 判断指定的字符是不是英文标点符号【即ASCII码的标点符号】
     * @param ch char  指定被检查的字符
     * @return boolean 如果指定的字符是标点符号：True 否则：False
     */
    private static boolean isAsciiPunctuation(char ch)
    {
        return(ch > 0x0020 && ch <= 0x002F) || (ch >= 0x003A && ch <= 0x003F) ||
            (ch >= 0x005B && ch <= 0x005F) || (ch >= 0x7B && ch <= 0x007F);
    }

    /**
     * 判断字符是否为表示开始的字符
     * @param ch char
     * @return boolean
     */
    public static boolean isStartChineseSeparator(char ch)
    {
        return(Character.getType(ch) == Character.START_PUNCTUATION ||
               Character.getType(ch) == Character.INITIAL_QUOTE_PUNCTUATION);
    }

    /**
     * 判断字符是否为表示结束的字符
     * @param ch char
     * @return boolean
     */
    public static boolean isEndChineseSeparator(char ch)
    {
        return((Character.getType(ch) == Character.END_PUNCTUATION ||
                Character.getType(ch) == Character.FINAL_QUOTE_PUNCTUATION)) ||
            (Character.getType(ch) == Character.OTHER_PUNCTUATION);
    }

    /**
     * @see com.wisii.fov.layoutmgr.LayoutManager#getNextKnuthElements(LayoutContext,
     *      int)
     */
    public LinkedList getNextKnuthElements(LayoutContext context, int alignment)
    {
        lineStartBAP = context.getLineStartBorderAndPaddingWidth();
        lineEndBAP = context.getLineEndBorderAndPaddingWidth();
        alignmentContext = context.getAlignmentContext();

        LinkedList returnList = new LinkedList();
        KnuthSequence sequence = new InlineKnuthSequence();
        AreaInfo ai = null;
        returnList.add(sequence);

        while(iNextStart < textArray.length)
        {
            char ch = textArray[iNextStart];
//            if(ch == 'x')
//              {
//                  textArray[iNextStart] = ' ';
//                  ch = ' ';
//                  wordSpaceIPD = getWordSpace(null);
//              }else
//                  wordSpaceIPD = tempSpaceIPD;
            //-------------------------Add by 【李晓光】---------------------
//            if((TextLayoutManager.isSpace(textArray[iNextStart])) &&
//               (TextLayoutManager.isChineseCharacters(textArray[(iNextStart == (0)) ? iNextStart
//                                                     : (iNextStart - 1)]) ||
//                (TextLayoutManager.isChineseCharacters(textArray[(iNextStart == (textArray.length - 1)) ? iNextStart
//                                                     : (iNextStart + 1)]))))
//            {
//                iThisStart = iNextStart;
//                iNextStart++;
//                ai = new AreaInfo(iThisStart, (short)(iNextStart),
//                                  (short)(iNextStart - iThisStart), (short)0,
//                                  MinOptMax.multiply(wordSpaceIPD, iNextStart // wordSpaceIPD
//                                                     - iThisStart), false, true);
//                ai.setAddBlank(true);
//                vecAreaInfo.add(ai);
//                sequence.addAll(createElementsForASpace(alignment, ai,
//                                                        vecAreaInfo.size() - 1));
//
//            }
//            else
            //-------------------------Add by 【李晓光】---------------------
            if((ch == CharUtilities.SPACE
                && foText.getWhitespaceTreatment() != Constants.EN_PRESERVE))
            {
                // normal non preserved space - collect them all
                // advance to the next character
                iThisStart = iNextStart;
                iNextStart++;
                while(iNextStart < textArray.length
                      && textArray[iNextStart] == CharUtilities.SPACE)
                {
                    iNextStart++;
                }
                // create the AreaInfo object---------------------Change by lxg---------------------
                ai = new AreaInfo(iThisStart, (short)(iNextStart),
                                  (short)(iNextStart - iThisStart), (short)0,
                                  MinOptMax.multiply(wordSpaceIPD, iNextStart // wordSpaceIPD
                                                     - iThisStart), false, true);
                // create the AreaInfo object---------------------Change by lxg---------------------
                vecAreaInfo.add(ai);

                // create the elements
                sequence.addAll(createElementsForASpace(alignment, ai,
                                                        vecAreaInfo.size() - 1));

            }
            else if(ch == CharUtilities.SPACE || ch == CharUtilities.NBSPACE)
            {
                // preserved space or non-breaking space:
                // create the AreaInfo object
                ai = new AreaInfo(iNextStart, (short)(iNextStart + 1),
                                  (short)1, (short)0, wordSpaceIPD, false, true);
                vecAreaInfo.add(ai);

                // create the elements
                sequence.addAll(createElementsForASpace(alignment, ai,
                                                        vecAreaInfo.size() - 1));

                // advance to the next character
                iNextStart++;
            }
            else if(CharUtilities.isFixedWidthSpace(ch))
            {
                // create the AreaInfo object
                MinOptMax ipd = new MinOptMax(font.getCharWidth(ch));
                ai = new AreaInfo(iNextStart, (short)(iNextStart + 1),
                                  (short)0, (short)0, ipd, false, true);
                vecAreaInfo.add(ai);

                // create the elements
                sequence.addAll(createElementsForASpace(alignment, ai,
                                                        vecAreaInfo.size() - 1));

                // advance to the next character
                iNextStart++;
            }
            else if(ch == NEWLINE)
            {
                // linefeed; this can happen when linefeed-treatment="preserve"
                // add a penalty item to the list and start a new sequence
                if(lineEndBAP != 0)
                {
                    sequence.add(new KnuthGlue(lineEndBAP, 0, 0,
                                               new LeafPosition(this, -1), true));
                }
                sequence.endSequence();
                sequence = new InlineKnuthSequence();
                returnList.add(sequence);

                // advance to the next character
                iNextStart++;
            }
            else
            {
                // the beginning of a word
                iThisStart = iNextStart;
                iTempStart = iNextStart;
                //----------------------------lxg---------------------
                if(flag)
                {
                    for(; iTempStart < textArray.length
                        && !isSpace(textArray[iTempStart])
                        && textArray[iTempStart] != NEWLINE
                        && !(iTempStart > iNextStart && isBreakChar(textArray[iTempStart - 1], textArray[iTempStart]));
                        iTempStart++)
                    {
                    }
                }
                else
                {
                    for(; iTempStart < textArray.length
                        && !isSpace(textArray[iTempStart])
                        && textArray[iTempStart] != NEWLINE
                        && !(iTempStart > iNextStart && isBreakChar(textArray[iTempStart - 1]));
                        iTempStart++)
                    {
                       
                    }

                }
                //----------------------------lxg---------------------
                // Word boundary found, process widths and kerning
                int wordLength = iTempStart - iThisStart;
                boolean kerning = font.hasKerning();
                MinOptMax wordIPD = new MinOptMax(0);
                for(int i = iThisStart; i < iTempStart; i++)
                {
                    char c = textArray[i];

                    // character width
                    int charWidth = font.getCharWidth(c);
                    wordIPD.add(charWidth);

                    // kerning // 判断是否调整字间距离
                    int kern = 0;
                    if(kerning && (i > iThisStart))
                    {
                        char previous = textArray[i - 1];
                        kern = font.getKernValue(previous, c)
                               * font.getFontSize() / 1000;
                        if(kern != 0)
                        {
                            // log.info("Kerning between " + previous + " and "
                            // + c + ": " + kern);
                            addToLetterAdjust(i, kern);
                        }
                        wordIPD.add(kern);
                    }
                }

                int iLetterSpaces = wordLength - 1;
                // if the last character is '-' or '/' and the next one
                // is not a space, it could be used as a line end;
                // add one more letter space, in case other text follows
                //----------------------------lxg---------------------
                if(flag)
                {
                    if(iTempStart < textArray.length
                       && isBreakChar(textArray[iTempStart - 1],
                                      textArray[iTempStart]) // isBreakChar(textArray[iTempStart - 1]
                       && !isSpace(textArray[iTempStart]))
                    {
                        iLetterSpaces++;
                    }
                }
                else
                {

                    if(isBreakChar(textArray[iTempStart - 1]) // isBreakChar(textArray[iTempStart - 1]
                       && iTempStart < textArray.length
                       && !isSpace(textArray[iTempStart]))
                    {
                        iLetterSpaces++;
                    }

                }
                //----------------------------lxg---------------------
                wordIPD.add(MinOptMax.multiply(letterSpaceIPD, iLetterSpaces));
//                System.err.println("iLetterSpaces = " + iLetterSpaces);
//                System.err.println("iThisStart = " + iThisStart);
                // create the AreaInfo object
                ai = new AreaInfo(iThisStart, iTempStart, (short)0,
                                  (short)iLetterSpaces, wordIPD, false, false);
                vecAreaInfo.add(ai);

                // create the elements
                sequence.addAll(createElementsForAWordFragment(alignment, ai,
                                                               vecAreaInfo.size() - 1, letterSpaceIPD));

                //------------------------------------------
//                wordSpaceIPD.opt = 0;
//                wordSpaceIPD.min = 0;
//                ai = new AreaInfo(iThisStart, (short)(iNextStart),
//                                  (short)(iNextStart - iThisStart), (short)0,
//                                  MinOptMax.multiply(wordSpaceIPD, iNextStart
//                                                     - iThisStart), false, true);
//                LinkedList spaceElements = new LinkedList();
//                LeafPosition mainPosition = new LeafPosition(this, vecAreaInfo.size() - 1);
//                spaceElements.add(new KnuthGlue(ai.ipdArea.opt,
//                                                       ai.ipdArea.max - ai.ipdArea.opt, ai.ipdArea.opt
//                                                       - ai.ipdArea.min, mainPosition, false));
//                sequence.addAll(spaceElements);
                //---------------------------------
//                ai = new AreaInfo(iThisStart, (short)(iNextStart),
//                                  (short)(iNextStart - iThisStart), (short)0, // (iNextStart - iThisStart)
//                                  MinOptMax.multiply(wordSpaceIPD, iNextStart
//                                                     - iThisStart), false, true);
//                vecAreaInfo.add(ai);
//
//                // create the elements
//                sequence.addAll(createElementsForASpace(alignment, ai,
//                                                        vecAreaInfo.size() - 1));

                //---------------------------------

                // advance to the next character
                iNextStart = iTempStart;
            }
        } // end of while
        if(((List)returnList.getLast()).size() == 0)
        {
            // Remove an empty sequence because of a trailing newline
            returnList.removeLast();
        }
        setFinished(true);
        if(returnList.size() > 0)
        {
            return returnList;
        }
        else
        {
            return null;
        }
    }

    /** @see InlineLevelLayoutManager#addALetterSpaceTo(List) */
    public List addALetterSpaceTo(List oldList)
    {
        // old list contains only a box, or the sequence: box penalty glue box;
        // look at the Position stored in the first element in oldList
        // which is always a box
        ListIterator oldListIterator = oldList.listIterator();
        KnuthElement el = (KnuthElement)oldListIterator.next();
        LeafPosition pos = (LeafPosition)((KnuthBox)el).getPosition();
        AreaInfo ai = (AreaInfo)vecAreaInfo.get(pos.getLeafPos());
        ai.iLScount++;
        ai.ipdArea.add(letterSpaceIPD);
        if(BREAK_CHARS.indexOf(textArray[iTempStart - 1]) >= 0) //  || isChineseCharacters(textArray[iTempStart - 1])
        {
            // the last character could be used as a line break
            // append new elements to oldList
            oldListIterator = oldList.listIterator(oldList.size());
            oldListIterator.add(new KnuthPenalty(0,
                                                 KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(this,
                                                                                                      -1), false));
            oldListIterator.add(new KnuthGlue(letterSpaceIPD.opt,
                                              letterSpaceIPD.max - letterSpaceIPD.opt, letterSpaceIPD.opt
                                              - letterSpaceIPD.min, new LeafPosition(this, -1),
                                              false));
        }
        else if(letterSpaceIPD.min == letterSpaceIPD.max)
        {
            // constant letter space: replace the box
            oldListIterator.set(new KnuthInlineBox(ai.ipdArea.opt,
                                                   alignmentContext, pos, false));
        }
        else
        {
            // adjustable letter space: replace the glue
            oldListIterator.next(); // this would return the penalty element
            oldListIterator.next(); // this would return the glue element
            oldListIterator.set(new KnuthGlue(ai.iLScount * letterSpaceIPD.opt,
                                              ai.iLScount * (letterSpaceIPD.max - letterSpaceIPD.opt),
                                              ai.iLScount * (letterSpaceIPD.opt - letterSpaceIPD.min),
                                              new LeafPosition(this, -1), true));
        }
        return oldList;
    }

    /**
     * remove the AreaInfo object represented by the given elements, so that it
     * won't generate any element when getChangedKnuthElements will be called
     *
     * @param oldList
     *            the elements representing the word space
     */
    public void removeWordSpace(List oldList)
    {
        // find the element storing the Position whose value
        // points to the AreaInfo object
        ListIterator oldListIterator = oldList.listIterator();
        if(((KnuthElement)((LinkedList)oldList).getFirst()).isPenalty())
        {
            // non breaking space: oldList starts with a penalty
            oldListIterator.next();
        }
        if(oldList.size() > 2)
        {
            // alignment is either center, start or end:
            // the first two elements does not store the needed Position
            oldListIterator.next();
            oldListIterator.next();
        }
        int leafValue = ((LeafPosition)((KnuthElement)oldListIterator.next())
                         .getPosition()).getLeafPos();
        // only the last word space can be a trailing space!
        if(leafValue == vecAreaInfo.size() - 1)
        {
            vecAreaInfo.remove(leafValue);
        }
        else
        {
            log.error("trying to remove a non-trailing word space");
        }
    }

    /** @see InlineLevelLayoutManager#hyphenate(Position, HyphContext) */
    public void hyphenate(Position pos, HyphContext hc)
    {
        AreaInfo ai = (AreaInfo)vecAreaInfo.get(((LeafPosition)pos)
                                                .getLeafPos());
        int iStartIndex = ai.iStartIndex;
        int iStopIndex;
        boolean bNothingChanged = true;

        while(iStartIndex < ai.iBreakIndex)
        {
            MinOptMax newIPD = new MinOptMax(0);
            boolean bHyphenFollows;

            if(hc.hasMoreHyphPoints()
               && (iStopIndex = iStartIndex + hc.getNextHyphPoint()) <= ai.iBreakIndex)
            {
                // iStopIndex is the index of the first character
                // after a hyphenation point
                bHyphenFollows = true;
            }
            else
            {
                // there are no more hyphenation points,
                // or the next one is after ai.iBreakIndex
                bHyphenFollows = false;
                iStopIndex = ai.iBreakIndex;
            }

            hc.updateOffset(iStopIndex - iStartIndex);

            // log.info("Word: " + new String(textArray, iStartIndex, iStopIndex
            // - iStartIndex));
            for(int i = iStartIndex; i < iStopIndex; i++)
            {
                char c = textArray[i];
                newIPD.add(new MinOptMax(font.getCharWidth(c)));
                // if (i > iStartIndex) {
                if(i < iStopIndex)
                {
                    MinOptMax la = this.letterAdjustArray[i + 1];
                    if((i == iStopIndex - 1) && bHyphenFollows)
                    {
                        // the letter adjust here needs to be handled further
                        // down during
                        // element generation because it depends on hyph/no-hyph
                        // condition
                        la = null;
                    }
                    if(la != null)
                    {
                        newIPD.add(la);
                    }
                }
            }
            // add letter spaces
            boolean bIsWordEnd = iStopIndex == ai.iBreakIndex
                                 && ai.iLScount < (ai.iBreakIndex - ai.iStartIndex);
            newIPD.add(MinOptMax.multiply(letterSpaceIPD,
                                          (bIsWordEnd ? (iStopIndex - iStartIndex - 1)
                                           : (iStopIndex - iStartIndex))));

            if(!(bNothingChanged && iStopIndex == ai.iBreakIndex && bHyphenFollows == false))
            {
                // the new AreaInfo object is not equal to the old one
                if(changeList == null)
                {
                    changeList = new LinkedList();
                }
                changeList.add(new PendingChange(new AreaInfo(
                    (short)iStartIndex, (short)iStopIndex, (short)0,
                    (short)(bIsWordEnd ? (iStopIndex - iStartIndex - 1)
                            : (iStopIndex - iStartIndex)), newIPD,
                    bHyphenFollows, false), ((LeafPosition)pos)
                                                 .getLeafPos()));
                bNothingChanged = false;
            }
            iStartIndex = iStopIndex;
        }
        if(!bChanged && !bNothingChanged)
        {
            bChanged = true;
        }
    }

    /** @see InlineLevelLayoutManager#applyChanges(List) */
    public boolean applyChanges(List oldList)
    {
        setFinished(false);

        if(changeList != null)
        {
            int iAddedAI = 0;
            int iRemovedAI = 0;
            int iOldIndex = -1;
            PendingChange currChange = null;
            ListIterator changeListIterator = changeList.listIterator();
            while(changeListIterator.hasNext())
            {
                currChange = (PendingChange)changeListIterator.next();
                if(currChange.index != iOldIndex)
                {
                    iRemovedAI++;
                    iAddedAI++;
                    iOldIndex = currChange.index;
                    vecAreaInfo
                        .remove(currChange.index + iAddedAI - iRemovedAI);
                    vecAreaInfo.add(currChange.index + iAddedAI - iRemovedAI,
                                    currChange.ai);
                }
                else
                {
                    iAddedAI++;
                    vecAreaInfo.add(currChange.index + iAddedAI - iRemovedAI,
                                    currChange.ai);
                }
            }
            changeList.clear();
        }

        iReturnedIndex = 0;
        return bChanged;
    }

    /**
     * @see com.wisii.fov.layoutmgr.LayoutManager#getChangedKnuthElements(List,
     *      int)
     */
    public LinkedList getChangedKnuthElements(List oldList, int alignment)
    {
        if(isFinished())
        {
            return null;
        }

        LinkedList returnList = new LinkedList();

        while(iReturnedIndex < vecAreaInfo.size())
        {
            AreaInfo ai = (AreaInfo)vecAreaInfo.get(iReturnedIndex);
            if(ai.iWScount == 0)
            {
                // ai refers either to a word or a word fragment
                returnList.addAll(createElementsForAWordFragment(alignment, ai,
                                                                 iReturnedIndex, letterSpaceIPD));
            }
            else
            {
                // ai refers to a space
                returnList.addAll(createElementsForASpace(alignment, ai,
                                                          iReturnedIndex));
            }
            iReturnedIndex++;
        } // end of while
        setFinished(true);
        // ElementListObserver.observe(returnList, "text-changed", null);
        return returnList;
    }

    /** @see InlineLevelLayoutManager#getWordChars(StringBuffer, Position) */
    public void getWordChars(StringBuffer sbChars, Position pos)
    {
        int iLeafValue = ((LeafPosition)pos).getLeafPos();
        if(iLeafValue != -1)
        {
            AreaInfo ai = (AreaInfo)vecAreaInfo.get(iLeafValue);
            sbChars.append(new String(textArray, ai.iStartIndex, ai.iBreakIndex
                                      - ai.iStartIndex));
        }
    }

    private LinkedList createElementsForASpace(int alignment, AreaInfo ai,
                                               int leafValue)
    {
        LinkedList spaceElements = new LinkedList();
        LeafPosition mainPosition = new LeafPosition(this, leafValue);

        if(textArray[ai.iStartIndex] == CharUtilities.NBSPACE)
        {
            // a non-breaking space
            // TODO: other kinds of non-breaking spaces
            if(alignment == EN_JUSTIFY)
            {
                // the space can stretch and shrink, and must be preserved
                // when starting a line
                spaceElements.add(new KnuthInlineBox(0, null,
                                                     notifyPos(new LeafPosition(this, -1)), true));
                spaceElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                   false, new LeafPosition(this, -1), false));
                spaceElements.add(new KnuthGlue(ai.ipdArea.opt, ai.ipdArea.max
                                                - ai.ipdArea.opt, ai.ipdArea.opt - ai.ipdArea.min,
                                                mainPosition, false));
            }
            else
            {
                // the space does not need to stretch or shrink, and must be
                // preserved when starting a line
                spaceElements.add(new KnuthInlineBox(ai.ipdArea.opt, null,
                                                     mainPosition, true));
            }
        }
        else if(textArray[ai.iStartIndex] == CharUtilities.SPACE
                && foText.getWhitespaceTreatment() == Constants.EN_PRESERVE)
        {
            // a breaking space that needs to be preserved
            switch(alignment)
            {
                case EN_CENTER:

                    // centered text:
                    // if the second element is chosen as a line break these
                    // elements
                    // add a constant amount of stretch at the end of a line and at
                    // the
                    // beginning of the next one, otherwise they don't add any
                    // stretch
                    spaceElements.add(new KnuthGlue(lineEndBAP,
                                                    3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    new LeafPosition(this, -1), false));
                    spaceElements
                        .add(new KnuthPenalty(
                            0,
                            (textArray[ai.iStartIndex] == CharUtilities.NBSPACE ? KnuthElement.INFINITE
                             : 0), false,
                            new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue( -(lineStartBAP + lineEndBAP),
                                                    -6 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthInlineBox(0, null,
                                                         notifyPos(new LeafPosition(this, -1)), false));
                    spaceElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                       false, new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue(ai.ipdArea.opt + lineStartBAP,
                                                    3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    mainPosition, false));
                    break;

                case EN_START: // fall through
                case EN_END:

                    // left- or right-aligned text:
                    // if the second element is chosen as a line break these
                    // elements
                    // add a constant amount of stretch at the end of a line,
                    // otherwise
                    // they don't add any stretch
                    spaceElements.add(new KnuthGlue(lineEndBAP,
                                                    3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthPenalty(0, 0, false,
                                                       new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue( -(lineStartBAP + lineEndBAP),
                                                    -3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthInlineBox(0, null,
                                                         notifyPos(new LeafPosition(this, -1)), false));
                    spaceElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                       false, new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue(ai.ipdArea.opt + lineStartBAP,
                                                    0, 0, mainPosition, false));
                    break;

                case EN_JUSTIFY:
                    // justified text:
                    // the stretch and shrink depends on the space width
                    spaceElements.add(new KnuthGlue(lineEndBAP, 0, 0,
                                                    new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthPenalty(0, 0, false,
                                                       new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue( -(lineStartBAP + lineEndBAP),
                                                    ai.ipdArea.max - ai.ipdArea.opt, ai.ipdArea.opt
                                                    - ai.ipdArea.min, new LeafPosition(this, -1),
                                                    false));
                    spaceElements.add(new KnuthInlineBox(0, null,
                                                         notifyPos(new LeafPosition(this, -1)), false));
                    spaceElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                       false, new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue(lineStartBAP + ai.ipdArea.opt,
                                                    0, 0, mainPosition, false));
                    break;

                default:

                    // last line justified, the other lines unjustified:
                    // use only the space stretch
                    spaceElements.add(new KnuthGlue(lineEndBAP, 0, 0,
                                                    new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthPenalty(0, 0, false,
                                                       new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue( -(lineStartBAP + lineEndBAP),
                                                    ai.ipdArea.max - ai.ipdArea.opt, 0, new LeafPosition(
                                                        this, -1), false));
                    spaceElements.add(new KnuthInlineBox(0, null,
                                                         notifyPos(new LeafPosition(this, -1)), false));
                    spaceElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                       false, new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue(lineStartBAP + ai.ipdArea.opt,
                                                    0, 0, mainPosition, false));
            }
        }
        else
        {
            // a (possible block) of breaking spaces
            switch(alignment)
            {
                case EN_CENTER:

                    // centered text:
                    // if the second element is chosen as a line break these
                    // elements
                    // add a constant amount of stretch at the end of a line and at
                    // the
                    // beginning of the next one, otherwise they don't add any
                    // stretch
                    spaceElements.add(new KnuthGlue(lineEndBAP,
                                                    3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthPenalty(0, 0, false,
                                                       new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue(ai.ipdArea.opt
                                                    - (lineStartBAP + lineEndBAP), -6
                                                    * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    mainPosition, false));
                    spaceElements.add(new KnuthInlineBox(0, null,
                                                         notifyPos(new LeafPosition(this, -1)), false));
                    spaceElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                       false, new LeafPosition(this, -1), false));
                    spaceElements.add(new KnuthGlue(lineStartBAP,
                                                    3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                    new LeafPosition(this, -1), false));
                    break;

                case EN_START: // fall through
                case EN_END:

                    // left- or right-aligned text:
                    // if the second element is chosen as a line break these
                    // elements
                    // add a constant amount of stretch at the end of a line,
                    // otherwise
                    // they don't add any stretch
                    if(lineStartBAP != 0 || lineEndBAP != 0)
                    {
                        spaceElements.add(new KnuthGlue(lineEndBAP,
                                                        3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                        new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthPenalty(0, 0, false,
                                                           new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthGlue(ai.ipdArea.opt
                                                        - (lineStartBAP + lineEndBAP), -3
                                                        * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                        mainPosition, false));
                        spaceElements.add(new KnuthInlineBox(0, null,
                                                             notifyPos(new LeafPosition(this, -1)), false));
                        spaceElements.add(new KnuthPenalty(0,
                                                           KnuthElement.INFINITE, false, new LeafPosition(
                                                               this, -1), false));
                        spaceElements.add(new KnuthGlue(lineStartBAP, 0, 0,
                                                        new LeafPosition(this, -1), false));
                    }
                    else
                    { // left ---> enter
                        spaceElements.add(new KnuthGlue(0,
                                                        3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                        new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthPenalty(0, 0, false,
                                                           new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthGlue(ai.ipdArea.opt, -3
                                                        * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                        mainPosition, false));
                    }
                    break;

                case EN_JUSTIFY:

                    // justified text:
                    // the stretch and shrink depends on the space width
                    if(lineStartBAP != 0 || lineEndBAP != 0)
                    {
                        spaceElements.add(new KnuthGlue(lineEndBAP, 0, 0,
                                                        new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthPenalty(0, 0, false,
                                                           new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthGlue(ai.ipdArea.opt
                                                        - (lineStartBAP + lineEndBAP), ai.ipdArea.max
                                                        - ai.ipdArea.opt, ai.ipdArea.opt - ai.ipdArea.min,
                                                        mainPosition, false));
                        spaceElements.add(new KnuthInlineBox(0, null,
                                                             notifyPos(new LeafPosition(this, -1)), false));
                        spaceElements.add(new KnuthPenalty(0,
                                                           KnuthElement.INFINITE, false, new LeafPosition(
                                                               this, -1), false));
                        spaceElements.add(new KnuthGlue(lineStartBAP, 0, 0,
                                                        new LeafPosition(this, -1), false));
                    }
                    else
                    {

                        spaceElements.add(new KnuthGlue(ai.ipdArea.opt,
                                                        ai.ipdArea.max - ai.ipdArea.opt, ai.ipdArea.opt
                                                        - ai.ipdArea.min, mainPosition, false));

                    }
                    break;

                default:

                    // last line justified, the other lines unjustified:
                    // use only the space stretch
                    if(lineStartBAP != 0 || lineEndBAP != 0)
                    {
                        spaceElements.add(new KnuthGlue(lineEndBAP, 0, 0,
                                                        new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthPenalty(0, 0, false,
                                                           new LeafPosition(this, -1), false));
                        spaceElements.add(new KnuthGlue(ai.ipdArea.opt
                                                        - (lineStartBAP + lineEndBAP), ai.ipdArea.max
                                                        - ai.ipdArea.opt, 0, mainPosition, false));
                        spaceElements.add(new KnuthInlineBox(0, null,
                                                             notifyPos(new LeafPosition(this, -1)), false));
                        spaceElements.add(new KnuthPenalty(0,
                                                           KnuthElement.INFINITE, false, new LeafPosition(
                                                               this, -1), false));
                        spaceElements.add(new KnuthGlue(lineStartBAP, 0, 0,
                                                        new LeafPosition(this, -1), false));
                    }
                    else
                    {
                        spaceElements.add(new KnuthGlue(ai.ipdArea.opt,
                                                        ai.ipdArea.max - ai.ipdArea.opt, 0, mainPosition,
                                                        false));
                    }
            }
        }

        return spaceElements;
    }

    private LinkedList createElementsForAWordFragment(int alignment,
                                                      AreaInfo ai, int leafValue, MinOptMax letterSpaceWidth)
    {
        LinkedList wordElements = new LinkedList();
        LeafPosition mainPosition = new LeafPosition(this, leafValue);

        // if the last character of the word fragment is '-' or '/',
        // the fragment could end a line; in this case, it loses one
        // of its letter spaces;
        boolean bSuppressibleLetterSpace = false; /*
        * ai.iLScount == (ai.iBreakIndex -
        * ai.iStartIndex) &&
        */
//            isBreakChar(textArray[ai.iBreakIndex - 1],
//                          textArray[ai.iBreakIndex >= textArray.length ? (textArray.length - 1) : ai.iBreakIndex]);
       // isBreakChar(textArray[ai.iBreakIndex - 1])
       //---------------------lxg----------------------
       if(flag)
       {
           bSuppressibleLetterSpace = isBreakChar(textArray[ai.iBreakIndex - 1],
                                                  textArray[ai.iBreakIndex >= textArray.length ? (textArray.length - 1)
                                                  : ai.iBreakIndex]);
       }
       else
       {
           bSuppressibleLetterSpace = isBreakChar(textArray[ai.iBreakIndex - 1]);
       }
        //---------------------lxg----------------------
        if(letterSpaceWidth.min == letterSpaceWidth.max)
        {
            // constant letter spacing
            wordElements.add(new KnuthInlineBox(
                bSuppressibleLetterSpace ? ai.ipdArea.opt - letterSpaceWidth.opt
                : ai.ipdArea.opt,
                alignmentContext, notifyPos(mainPosition), false));
        }
        else
        {
            // adjustable letter spacing
            int unsuppressibleLetterSpaces = bSuppressibleLetterSpace ? ai.iLScount - 1
                                             : ai.iLScount;
            wordElements.add(new KnuthInlineBox(ai.ipdArea.opt - ai.iLScount
                                                * letterSpaceWidth.opt, alignmentContext,
                                                notifyPos(mainPosition), false));
            wordElements.add(new KnuthPenalty(0, KnuthElement.INFINITE, false,
                                              new LeafPosition(this, -1), true));
            wordElements.add(new KnuthGlue(unsuppressibleLetterSpaces
                                           * letterSpaceWidth.opt, unsuppressibleLetterSpaces
                                           * (letterSpaceWidth.max - letterSpaceWidth.opt),
                                           unsuppressibleLetterSpaces
                                           * (letterSpaceWidth.opt - letterSpaceWidth.min),
                                           new LeafPosition(this, -1), true));
            wordElements.add(new KnuthInlineBox(0, null,
                                                notifyPos(new LeafPosition(this, -1)), true));
        }

        // extra-elements if the word fragment is the end of a syllable,
        // or it ends with a character that can be used as a line break
        if(ai.bHyphenated)
        {
            MinOptMax widthIfNoBreakOccurs = null;
            if(ai.iBreakIndex < textArray.length)
            {
                // Add in kerning in no-break condition
                widthIfNoBreakOccurs = letterAdjustArray[ai.iBreakIndex];
            }
            // if (ai.iBreakIndex)

            // the word fragment ends at the end of a syllable:
            // if a break occurs the content width increases,
            // otherwise nothing happens
            wordElements.addAll(createElementsForAHyphen(alignment, hyphIPD,
                                                         widthIfNoBreakOccurs));
        }
        else if(bSuppressibleLetterSpace)
        {
            // the word fragment ends with a character that acts as a hyphen
            // if a break occurs the width does not increase,
            // otherwise there is one more letter space
            wordElements.addAll(createElementsForAHyphen(alignment, 0,
                                                         letterSpaceWidth));
        }
        return wordElements;
    }

    private LinkedList createElementsForAHyphen(int alignment,
                                                int widthIfBreakOccurs, MinOptMax widthIfNoBreakOccurs)
    {
        if(widthIfNoBreakOccurs == null)
        {
            widthIfNoBreakOccurs = ZERO_MINOPTMAX;
        }
        LinkedList hyphenElements = new LinkedList();

        switch(alignment)
        {
            case EN_CENTER:

                // centered text:
                /*
                 * hyphenElements.add (new KnuthGlue(0, 3 *
                 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, new LeafPosition(this,
                 * -1), false)); hyphenElements.add (new KnuthPenalty(hyphIPD,
                 * KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(this, -1),
                 * false)); hyphenElements.add (new KnuthGlue(0, - 6 *
                 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, new LeafPosition(this,
                 * -1), false)); hyphenElements.add (new KnuthInlineBox(0, 0, 0, 0,
                 * new LeafPosition(this, -1), false)); hyphenElements.add (new
                 * KnuthPenalty(0, KnuthElement.INFINITE, true, new
                 * LeafPosition(this, -1), false)); hyphenElements.add (new
                 * KnuthGlue(0, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, new
                 * LeafPosition(this, -1), false));
                 */
                hyphenElements.add(new KnuthGlue(lineEndBAP,
                                                 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                 new LeafPosition(this, -1), true));
                hyphenElements.add(new KnuthPenalty(hyphIPD,
                                                    KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(this,
                                                                                                         -1), false));
                hyphenElements.add(new KnuthGlue( -(lineEndBAP + lineStartBAP), -6
                                                 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                 new LeafPosition(this, -1), false));
                hyphenElements.add(new KnuthInlineBox(0, null,
                                                      notifyPos(new LeafPosition(this, -1)), true));
                hyphenElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                    false, new LeafPosition(this, -1), true));
                hyphenElements.add(new KnuthGlue(lineStartBAP,
                                                 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                 new LeafPosition(this, -1), true));
                break;

            case EN_START: // fall through
            case EN_END:

                // left- or right-aligned text:
                /*
                 * hyphenElements.add (new KnuthGlue(0, 3 *
                 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, new LeafPosition(this,
                 * -1), false)); hyphenElements.add (new
                 * KnuthPenalty(widthIfBreakOccurs, KnuthPenalty.FLAGGED_PENALTY,
                 * true, new LeafPosition(this, -1), false)); hyphenElements.add
                 * (new KnuthGlue(widthIfNoBreakOccurs.opt, - 3 *
                 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, new LeafPosition(this,
                 * -1), false));
                 */
                if(lineStartBAP != 0 || lineEndBAP != 0)
                {
                    hyphenElements.add(new KnuthGlue(lineEndBAP,
                                                     3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                     new LeafPosition(this, -1), false));
                    hyphenElements.add(new KnuthPenalty(widthIfBreakOccurs,
                                                        KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(
                                                            this, -1), false));
                    hyphenElements.add(new KnuthGlue(widthIfNoBreakOccurs.opt
                                                     - (lineStartBAP + lineEndBAP), -3
                                                     * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                     new LeafPosition(this, -1), false));
                    hyphenElements.add(new KnuthInlineBox(0, null,
                                                          notifyPos(new LeafPosition(this, -1)), false));
                    hyphenElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                        false, new LeafPosition(this, -1), false));
                    hyphenElements.add(new KnuthGlue(lineStartBAP, 0, 0,
                                                     new LeafPosition(this, -1), false));
                }
                else
                {
                    hyphenElements.add(new KnuthGlue(0,
                                                     3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                     new LeafPosition(this, -1), false));
                    hyphenElements.add(new KnuthPenalty(widthIfBreakOccurs,
                                                        KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(
                                                            this, -1), false));
                    hyphenElements.add(new KnuthGlue(widthIfNoBreakOccurs.opt, -3
                                                     * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                     new LeafPosition(this, -1), false));
                }
                break;

            default:

                // justified text, or last line justified:
                // just a flagged penalty
                /*
                 * hyphenElements.add (new KnuthPenalty(widthIfBreakOccurs,
                 * KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(this, -1),
                 * false));
                 */
                if(lineStartBAP != 0 || lineEndBAP != 0)
                {
                    hyphenElements.add(new KnuthGlue(lineEndBAP, 0, 0,
                                                     new LeafPosition(this, -1), false));
                    hyphenElements.add(new KnuthPenalty(widthIfBreakOccurs,
                                                        KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(
                                                            this, -1), false));
                    // extra elements representing a letter space that is suppressed
                    // if a break occurs
                    if(widthIfNoBreakOccurs.min != 0
                       || widthIfNoBreakOccurs.max != 0)
                    {
                        hyphenElements
                            .add(new KnuthGlue(widthIfNoBreakOccurs.opt
                                               - (lineStartBAP + lineEndBAP),
                                               widthIfNoBreakOccurs.max
                                               - widthIfNoBreakOccurs.opt,
                                               widthIfNoBreakOccurs.opt
                                               - widthIfNoBreakOccurs.min,
                                               new LeafPosition(this, -1), false));
                    }
                    else
                    {
                        hyphenElements.add(new KnuthGlue(
                            -(lineStartBAP + lineEndBAP), 0, 0,
                            new LeafPosition(this, -1), false));
                    }
                    hyphenElements.add(new KnuthInlineBox(0, null,
                                                          notifyPos(new LeafPosition(this, -1)), false));
                    hyphenElements.add(new KnuthPenalty(0, KnuthElement.INFINITE,
                                                        false, new LeafPosition(this, -1), false));
                    hyphenElements.add(new KnuthGlue(lineStartBAP, 0, 0,
                                                     new LeafPosition(this, -1), false));
                }
                else
                {
//                    hyphenElements.add(new KnuthPenalty(widthIfBreakOccurs,
//                                                        KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(
//                                                            this, -1), false));
                    //---------------------------add by lxg 2007/11/15 start-------------------------
                    hyphenElements.add(new KnuthGlue(0,
                                                     3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                     new LeafPosition(this, -1), false));
                    hyphenElements.add(new KnuthPenalty(widthIfBreakOccurs,
                                                        KnuthPenalty.FLAGGED_PENALTY, true, new LeafPosition(
                                                            this, -1), false));

                    hyphenElements.add(new KnuthGlue(widthIfNoBreakOccurs.opt, -3
                                                     * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0,
                                                     new LeafPosition(this, -1), false));
                    //---------------------------add by lxg 2007/11/15 end------------------------
                    // extra elements representing a letter space that is suppressed
                    // if a break occurs
                    if(widthIfNoBreakOccurs.min != 0
                       || widthIfNoBreakOccurs.max != 0)
                    {
                        hyphenElements
                            .add(new KnuthGlue(widthIfNoBreakOccurs.opt,
                                               widthIfNoBreakOccurs.max
                                               - widthIfNoBreakOccurs.opt,
                                               widthIfNoBreakOccurs.opt
                                               - widthIfNoBreakOccurs.min,
                                               new LeafPosition(this, -1), false));
                    }
                }
        }

        return hyphenElements;
    }
}
