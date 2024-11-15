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
 *//* $Id: FOText.java,v 1.11 2007/04/28 07:53:43 zkl Exp $ */

package com.wisii.fov.fo;

// Java
import java.awt.Color;
import java.util.NoSuchElementException;

import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.datatypes.Length;
import com.wisii.fov.fo.flow.Block;
import com.wisii.fov.fo.properties.CommonFont;
import com.wisii.fov.fo.properties.CommonHyphenation;
import com.wisii.fov.fo.properties.CommonTextDecoration;
import com.wisii.fov.fo.properties.Property;
import com.wisii.fov.fo.properties.SpaceProperty;
import com.wisii.fov.layoutmgr.inline.TextLayoutManager;

/**
 * A text node (PCDATA) in the formatting object tree.
 *
 * Unfortunately the BufferManager implementatation holds
 * onto references to the character data in this object
 * longer than the lifetime of the object itself, causing
 * excessive memory consumption and OOM errors.
 *
 * @author unascribed
 * @author <a href="mailto:mark-fov@inomial.com">Mark Lillywhite</a>
 */
public class FOText extends FONode {

    /**
     * the character array containing the text
     */
    public char[] ca;

    /**
     * The starting valid index of the ca array
     * to be processed.
     *
     * This value is originally equal to 0, but becomes
     * incremented during leading whitespace removal by the flow.Block class,
     * via the TextCharIterator.remove() method below.
     */
    public int startIndex = 0;

    /**
     * The ending valid index of the ca array
     * to be processed.
     *
     * This value is originally equal to ca.length, but becomes
     * decremented during between-word whitespace removal by the flow.Block class,
     * via the TextCharIterator.remove() method below.
     */
    public int endIndex = 0;

    // The value of properties relevant for character.
    private CommonFont commonFont;
    private CommonHyphenation commonHyphenation;
    private Color color;
    private Property letterSpacing;
    private SpaceProperty lineHeight;
    private int whiteSpaceTreatment;
    private int whiteSpaceCollapse;
    private int textTransform;
    private Property wordSpacing;
    private int wrapOption;
    private Length baselineShift;
    private int editmode; //add by zkl.
    private String xpath; //add by zkl.
    private String translateurl;//add by zkl.
    private String hidename;//add by zkl.
    public boolean noText;
    // End of property values

    /**
     * Keeps track of the last FOText object created within the current
     * block. This is used to create pointers between such objects.
     * TODO: As soon as the control hierarchy is straightened out, this static
     * variable needs to become an instance variable in some parent object,
     * probably the page-sequence.
     */
    private static FOText lastFOTextProcessed = null;

    /**
     * Points to the previous FOText object created within the current
     * block. If this is "null", this is the first such object.
     */
    private FOText prevFOTextThisBlock = null;

    /**
     * Points to the next FOText object created within the current
     * block. If this is "null", this is the last such object.
     */
    private FOText nextFOTextThisBlock = null;

    /**
     * Points to the ancestor Block object. This is used to keep track of
     * which FOText nodes are descendants of the same block.
     */
    private Block ancestorBlock = null;

    /** Holds the text decoration values. May be null */
    private CommonTextDecoration textDecoration;

    private static final int IS_WORD_CHAR_FALSE = 0;
    private static final int IS_WORD_CHAR_TRUE = 1;
    private static final int IS_WORD_CHAR_MAYBE = 2;

    /**
     * Creates a now FO text node.
     * @param parent FONode that is the parent of this object
     */
    public FOText(FONode parent) {
        super(parent);
        
    }

    /** @see com.wisii.fov.fo.FONode */
    protected void addCharacters(char[] data, int start, int end,
            PropertyList list, Locator locator) throws FOVException {
        int length = end - start;                    
        int calength = 0;
        char[] nca = null;
        if (ca != null) {
            calength = ca.length;
            nca = new char[calength + length];
            System.arraycopy(ca, 0, nca, 0, calength);
        } else {       
            nca = new char[length];
        }
        System.arraycopy(data, start, nca, calength, length);
        endIndex = nca.length;
        this.ca = nca;
     }

    /**
     * @see com.wisii.fov.fo.FONode#clone(FONode, boolean)
     */
    public FONode clone(FONode parent, boolean removeChildren)
        throws FOVException {
        FOText ft = (FOText) super.clone(parent, removeChildren);
        if (removeChildren) {
            //not really removing, but just make sure the char array
            //pointed to is really a different one
            if (ca != null) {
                ft.ca = new char[ca.length];
                System.arraycopy(ca, 0, ft.ca, 0, ca.length);
            }
        }
        return ft;
    }

    /**
     * @see com.wisii.fov.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) throws FOVException {
        commonFont = pList.getFontProps();
        commonHyphenation = pList.getHyphenationProps();

        color = pList.get(Constants.PR_COLOR).getColor();
        lineHeight = pList.get(Constants.PR_LINE_HEIGHT).getSpace();
        letterSpacing = pList.get(Constants.PR_LETTER_SPACING);
        whiteSpaceCollapse = pList.get(Constants.PR_WHITE_SPACE_COLLAPSE).getEnum();
        whiteSpaceTreatment = pList.get(Constants.PR_WHITE_SPACE_TREATMENT).getEnum();
        textTransform = pList.get(Constants.PR_TEXT_TRANSFORM).getEnum();
        wordSpacing = pList.get(Constants.PR_WORD_SPACING);
        wrapOption = pList.get(Constants.PR_WRAP_OPTION).getEnum();
        textDecoration = pList.getTextDecorationProps();
        baselineShift = pList.get(Constants.PR_BASELINE_SHIFT).getLength();
        
        /*
         * add by zkl.
         */
        try{
        editmode=pList.get(Constants.PR_EDITMODE).getNumber().intValue();
        }catch(Exception e){
        	editmode=0;
        }
        xpath=pList.get(Constants.PR_XPATH).getString();
        translateurl=pList.get(Constants.PR_TRANSLATEURL).getString();
        hidename=pList.get(Constants.PR_HIDENAME).getString();
    }

    /** @see com.wisii.fov.fo.FONode#endOfNode() */
    protected void endOfNode() throws FOVException {
        textTransform();
        inittext();
        getFOEventHandler().characters(ca, startIndex, endIndex);
    }
    private void inittext()
	{
		if (ca != null && ca.length > 0)
		{
			boolean ischinese = false;
			for (char c : ca)
			{

				if (TextLayoutManager.isChineseCharacters(c)
						|| Character.isDigit(c))
				{
					ischinese = true;
					break;
				}
			}
			// 处理中文断行，如果有中文，则转换空格为\u2002，中文后加空格
			if (ischinese)
			{
				StringBuffer temp = new StringBuffer();
				for (int index = 0; index < ca.length; index++)
				{

					if (ca[index] == ' ' && (ischinese || ca.length == 1))
					{// (data.length == 1) 处理xlst中添加的空格， (index != start)
						// 处理当处理的文本第一字符为空格时，不予处理。
						temp.append('\u2002');
					} 
					else
					{
						temp.append(ca[index]);
					}
					if ((TextLayoutManager.isChineseCharacters(ca[index]) && !(TextLayoutManager
							.isEndChineseSeparator(ca[(index == (ca.length - 1)) ? index
									: (index + 1)]))))
					{

						if (index != ca.length - 1)
						{
							// 为可调整汉字添加空格，来处理匀满
							temp.append(' ');
						}
					} 
					else if ((!TextLayoutManager
							.isChineseCharacters(ca[index]) && !TextLayoutManager
							.isStartChineseSeparator(ca[index]))
							&& TextLayoutManager
									.isChineseCharacters(ca[(index == (ca.length - 1)) ? index
											: (index + 1)]))
					{
						if (index != ca.length - 1)
						{
							// 为可调整汉字添加空格，来处理匀满
							temp.append(' ');
						}
					}

				}
				ca = temp.toString().toCharArray();
				endIndex = ca.length;
			}
		}
	}
    /**
     * Check if this text node will create an area.
     * This means either there is non-whitespace or it is
     * preserved whitespace.
     * Maybe this just needs to check length > 0, since char iterators
     * handle whitespace.
     *
     * @return true if this will create an area in the output
     */
    public boolean willCreateArea() {
        if (whiteSpaceCollapse == Constants.EN_FALSE
                && endIndex - startIndex > 0) {
            return true;
        }

        for (int i = startIndex; i < endIndex; i++) {
            char ch = ca[i];
            if (!((ch == ' ')
                    || (ch == '\n')
                    || (ch == '\r')
                    || (ch == '\t'))) { // whitespace
                return true;
            }
        }
        return false;
    }

    /**
     * @return a new TextCharIterator
     */
    public CharIterator charIterator() {
        return new TextCharIterator();
    }

     /**
     * This method is run as part of the ancestor Block's flushText(), to create xref pointers to
     * the previous FOText objects within the same Block
     */
    protected void createBlockPointers(Block ancestorBlock) {
        this.ancestorBlock = ancestorBlock;
        // if the last FOText is a sibling, point to it, and have it point here
        if (lastFOTextProcessed != null) {
            if (lastFOTextProcessed.ancestorBlock == this.ancestorBlock) {
                prevFOTextThisBlock = lastFOTextProcessed;
                prevFOTextThisBlock.nextFOTextThisBlock = this;
            } else {
                prevFOTextThisBlock = null;
            }
        }
        // save the current node in static field so the next guy knows where
        // to look
        lastFOTextProcessed = this;
        return;
    }

    /**
     * This method is run as part of the Constructor, to handle the
     * text-transform property.
     */
    private void textTransform() {
        if (getFOEventHandler().inMarker()
                || textTransform == Constants.EN_NONE) {
            return;
        }
        for (int i = 0; i < endIndex; i++) {
            ca[i] = charTransform(i);
        }
    }

    /**
     * Determines whether a particular location in an FOText object's text is
     * the start of a new "word". The use of "word" here is specifically for
     * the text-transform property, but may be useful for other things as
     * well, such as word-spacing. The definition of "word" is somewhat ambiguous
     * and appears to be definable by the user agent.
     *
     * @param i index into ca[]
     *
     * @return True if the character at this location is the start of a new
     * word.
     */
    private boolean isStartOfWord(int i) {
        char prevChar = getRelativeCharInBlock(i, -1);
        /* All we are really concerned about here is of what type prevChar
           is. If inputChar is not part of a word, then the Java
           conversions will (we hope) simply return inputChar.
        */
        switch (isWordChar(prevChar)) {
        case IS_WORD_CHAR_TRUE:
            return false;
        case IS_WORD_CHAR_FALSE:
            return true;
        /* "MAYBE" implies that additional context is needed. An example is a
         * single-quote, either straight or closing, which might be interpreted
         * as a possessive or a contraction, or might be a closing quote.
         */
        case IS_WORD_CHAR_MAYBE:
            char prevPrevChar = getRelativeCharInBlock(i, -2);
            switch (isWordChar(prevPrevChar)) {
            case IS_WORD_CHAR_TRUE:
                return false;
            case IS_WORD_CHAR_FALSE:
                return true;
            case IS_WORD_CHAR_MAYBE:
                return true;
            default:
                return false;
        }
        default:
            return false;
        }
    }

    /**
     * Finds a character within the current Block that is relative in location
     * to a character in the current FOText. Treats all FOText objects within a
     * block as one unit, allowing text in adjoining FOText objects to be
     * returned if the parameters are outside of the current object.
     *
     * @param i index into ca[]
     * @param offset signed integer with relative position within the
     *   block of the character to return. To return the character immediately
     *   preceding i, pass -1. To return the character immediately after i,
     *   pass 1.
     * @return the character in the offset position within the block; \u0000 if
     * the offset points to an area outside of the block.
     */
    private char getRelativeCharInBlock(int i, int offset) {
        // The easy case is where the desired character is in the same FOText
        if (((i + offset) >= 0) && ((i + offset) <= this.endIndex)) {
            return ca[i + offset];
        }
        // For now, we can't look at following FOText nodes
        if (offset > 0) {
             return '\u0000';
         }
        // Remaining case has the text in some previous FOText node
        boolean foundChar = false;
        char charToReturn = '\u0000';
        FOText nodeToTest = this;
        int remainingOffset = offset + i;
        while (!foundChar) {
            if (nodeToTest.prevFOTextThisBlock == null) {
                foundChar = true;
                break;
            }
            nodeToTest = nodeToTest.prevFOTextThisBlock;
            if ((nodeToTest.endIndex + remainingOffset) >= 0) {
                charToReturn = nodeToTest.ca[nodeToTest.endIndex + remainingOffset];
                foundChar = true;
            } else {
                remainingOffset = remainingOffset + nodeToTest.endIndex;
            }
        }
        return charToReturn;
    }

    /**
     * @return The previous FOText node in this Block; null, if this is the
     * first FOText in this Block.
     */
    public FOText getPrevFOTextThisBlock () {
        return prevFOTextThisBlock;
    }

    /**
     * @return The next FOText node in this Block; null if this is the last
     * FOText in this Block; null if subsequent FOText nodes have not yet been
     * processed.
     */
    public FOText getNextFOTextThisBlock () {
        return nextFOTextThisBlock;
    }

    /**
     * @return The nearest ancestor block object which contains this FOText.
     */
    public Block getAncestorBlock () {
        return ancestorBlock;
    }

    /**
     * Transforms one character in ca[] using the text-transform property.
     *
     * @param i the index into ca[]
     * @return char with transformed value
     */
    private char charTransform(int i) {
        switch (textTransform) {
        /* put NONE first, as this is probably the common case */
        case Constants.EN_NONE:
            return ca[i];
        case Constants.EN_UPPERCASE:
            return Character.toUpperCase(ca[i]);
        case Constants.EN_LOWERCASE:
            return Character.toLowerCase(ca[i]);
        case Constants.EN_CAPITALIZE:
            if (isStartOfWord(i)) {
                /*
                 Use toTitleCase here. Apparently, some languages use
                 a different character to represent a letter when using
                 initial caps than when all of the letters in the word
                 are capitalized. We will try to let Java handle this.
                */
                return Character.toTitleCase(ca[i]);
            } else {
                return ca[i];
            }
        default:
            getLogger().warn("Invalid text-tranform value: " + textTransform);
            return ca[i];
        }
    }

    /**
     * Determines whether the input char should be considered part of a
     * "word". This is used primarily to determine whether the character
     * immediately following starts a new word, but may have other uses.
     * We have not found a definition of "word" in the standard (1.0), so the
     * logic used here is based on the programmer's best guess.
     *
     * @param inputChar the character to be tested.
     * @return int IS_WORD_CHAR_TRUE, IS_WORD_CHAR_FALSE, or IS_WORD_CHAR_MAYBE,
     * depending on whether the character should be considered part of a word
     * or not.
     */
    public static int isWordChar(char inputChar) {
        switch (Character.getType(inputChar)) {
        case Character.COMBINING_SPACING_MARK:
            return IS_WORD_CHAR_TRUE;
        case Character.CONNECTOR_PUNCTUATION:
            return IS_WORD_CHAR_TRUE;
        case Character.CONTROL:
            return IS_WORD_CHAR_FALSE;
        case Character.CURRENCY_SYMBOL:
            return IS_WORD_CHAR_TRUE;
        case Character.DASH_PUNCTUATION:
            if (inputChar == '-') {
                return IS_WORD_CHAR_TRUE; //hyphen
            }
            return IS_WORD_CHAR_FALSE;
        case Character.DECIMAL_DIGIT_NUMBER:
            return IS_WORD_CHAR_TRUE;
        case Character.ENCLOSING_MARK:
            return IS_WORD_CHAR_FALSE;
        case Character.END_PUNCTUATION:
            if (inputChar == '\u2019') {
                return IS_WORD_CHAR_MAYBE; //apostrophe, right single quote
            }
            return IS_WORD_CHAR_FALSE;
        case Character.FORMAT:
            return IS_WORD_CHAR_FALSE;
        case Character.LETTER_NUMBER:
            return IS_WORD_CHAR_TRUE;
        case Character.LINE_SEPARATOR:
            return IS_WORD_CHAR_FALSE;
        case Character.LOWERCASE_LETTER:
            return IS_WORD_CHAR_TRUE;
        case Character.MATH_SYMBOL:
            return IS_WORD_CHAR_FALSE;
        case Character.MODIFIER_LETTER:
            return IS_WORD_CHAR_TRUE;
        case Character.MODIFIER_SYMBOL:
            return IS_WORD_CHAR_TRUE;
        case Character.NON_SPACING_MARK:
            return IS_WORD_CHAR_TRUE;
        case Character.OTHER_LETTER:
            return IS_WORD_CHAR_TRUE;
        case Character.OTHER_NUMBER:
            return IS_WORD_CHAR_TRUE;
        case Character.OTHER_PUNCTUATION:
            if (inputChar == '\'') {
                return IS_WORD_CHAR_MAYBE; //ASCII apostrophe
            }
            return IS_WORD_CHAR_FALSE;
        case Character.OTHER_SYMBOL:
            return IS_WORD_CHAR_TRUE;
        case Character.PARAGRAPH_SEPARATOR:
            return IS_WORD_CHAR_FALSE;
        case Character.PRIVATE_USE:
            return IS_WORD_CHAR_FALSE;
        case Character.SPACE_SEPARATOR:
            return IS_WORD_CHAR_FALSE;
        case Character.START_PUNCTUATION:
            return IS_WORD_CHAR_FALSE;
        case Character.SURROGATE:
            return IS_WORD_CHAR_FALSE;
        case Character.TITLECASE_LETTER:
            return IS_WORD_CHAR_TRUE;
        case Character.UNASSIGNED:
            return IS_WORD_CHAR_FALSE;
        case Character.UPPERCASE_LETTER:
            return IS_WORD_CHAR_TRUE;
        default:
            return IS_WORD_CHAR_FALSE;
        }
    }

    private class TextCharIterator extends CharIterator {
        private int curIndex = 0;

        /* Current space removal process:  just increment the startIndex
           to "remove" leading spaces from ca, until an unremoved character
           is found.  Then perform arraycopy's to remove extra spaces
           between words.  nextCharCalled is used to determine if an
           unremoved character has already been found--if its value > 2
           than it means that has occurred (it is reset to zero each time we
           remove a space via incrementing the startIndex.)  */
        private int nextCharCalled = 0;

        public boolean hasNext() {
           if (curIndex == 0) {
//             log.debug("->" + new String(ca) + "<-");
          }
           return (curIndex < endIndex);
        }

        public char nextChar() {
            if (curIndex < endIndex) {
                nextCharCalled++;
                // Just a char class? Don't actually care about the value!
                return ca[curIndex++];
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (curIndex < endIndex && nextCharCalled < 2) {
                startIndex++;
                nextCharCalled = 0;
//              log.debug("removeA: " + new String(ca, startIndex, endIndex - startIndex));
            } else if (curIndex < endIndex) {
                // copy from curIndex to end to curIndex-1
                System.arraycopy(ca, curIndex, ca, curIndex - 1,
                    endIndex - curIndex);
                endIndex--;
                curIndex--;
//              log.debug("removeB: " + new String(ca, startIndex, endIndex - startIndex));
            } else if (curIndex == endIndex) {
//              log.debug("removeC: " + new String(ca, startIndex, endIndex - startIndex));
                endIndex--;
                curIndex--;
            }
        }

        public void replaceChar(char c) {
            if (curIndex > 0 && curIndex <= endIndex) {
                ca[curIndex - 1] = c;
            }
        }

    }

    /**
     * @return the Common Font Properties.
     */
    public CommonFont getCommonFont() {
        return commonFont;
    }

    /**
     * @return the Common Hyphenation Properties.
     */
    public CommonHyphenation getCommonHyphenation() {
        return commonHyphenation;
    }

    /**
     * @return the "color" property.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the "letter-spacing" property.
     */
    public Property getLetterSpacing() {
        return letterSpacing;
    }

    /**
     * @return the "line-height" property.
     */
    public SpaceProperty getLineHeight() {
        return lineHeight;
    }

    /**
     * @return the "white-space-treatment" property
     */
    public int getWhitespaceTreatment() {
        return whiteSpaceTreatment;
    }

    /**
     * @return the "word-spacing" property.
     */
    public Property getWordSpacing() {
        return wordSpacing;
    }

    /**
     * @return the "wrap-option" property.
     */
    public int getWrapOption() {
        return wrapOption;
    }

    /** @return the "text-decoration" property. */
    public CommonTextDecoration getTextDecoration() {
        return textDecoration;
    }

    /** @return the baseline-shift property */
    public Length getBaseLineShift() {
        return baselineShift;
    }

    /** @see java.lang.Object#toString() */
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(" (").append(ca).append(")");
        return sb.toString();
    }

    /** @see com.wisii.fov.fo.FONode#getLocalName() */
    public String getLocalName() {
        return null;
    }

    /** @see com.wisii.fov.fo.FONode#getNormalNamespacePrefix() */
    public String getNormalNamespacePrefix() {
        return null;
    }

    /** @see com.wisii.fov.fo.FONode#gatherContextInfo() */
    protected String gatherContextInfo() {
        if (getLocator() != null) {
            return super.gatherContextInfo();
        } else {
            return new String(ca).trim();
        }
    }
    
    /**
     * add by zkl.
     * @return
     */

	public int getEditmode() {
		return editmode;
	}
	
	public String getXpath(){
		return xpath;
	}
	
	public String getTranslateurl(){
		return translateurl;
	}
	
	public String getHidename(){
		return hidename;
	}
    
    

}
