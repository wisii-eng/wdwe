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
 *//* $Id: TextArea.java,v 1.10 2007/06/18 08:36:22 hzl Exp $ */

package com.wisii.fov.area.inline;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * A text inline area.
 */
public class TextArea extends AbstractTextArea
{

    // _showRec里面存放的是该TextArea的显示区域
    private Rectangle2D _showRec;

    // 编辑模式，00为不可编辑（修改，添加，删除），01为只可修改，10为只可添加，11为可修改可添加，
    // 20为只可删除，21为可修改可删除，30为可可添加可删除，31为可修改可添加可删除。
    private int _editMode;

    // 对应源XML里的哪一项
    private String _xPath;

    // 转换表(例如1对应北京，2对应上海等)。
    private List _conversion;

    // 与此关联的上一个TextArea（例如一个XML项拆分成多个TextArea,这个属性就可以把所有的关联TextArea关联起来）
    private TextArea _previous;

    // 与此关联的下一个TextArea（例如一个XML项拆分成多个TextArea,这个属性就可以把所有的关联TextArea关联起来）
    private TextArea _next;

    // 若是隐藏项，则该项为外部数据名，若不是隐藏项，为空。
    private String _hideName;

    // 表明与该TextArea相关联的隐藏项。
    private List _hideAreas;

    // 该区域的最大显示宽度，用来计算是否出现换行情况。
    private float _maxShowWidth;

    // 文本显示的字体
    private Font _font;

    // 存放当前项的文本
    private String _currentText;

    // 是否为1个Line内多个TextArea
    private boolean _multiInLine;

    // TextArea所在的Block的显示区域的左上角的X坐标
//    private float _parentBlockRecX;
    // TextArea所在的Block的显示区域的左上角的Y坐标
//    private float _parentBlockRecY;
    // TextArea所在的LineArea、Block的start-indent、text-align缩进
    private float _indent;
    // true:缩进类型是text-align
//    private boolean _isTextAlign = false; // del.因为parentBlockLength的值为Block内容的宽度，即getIPD()，而不是getAllocIPD，所以不用考虑start-indent的情况

    /**20090325 liuxiao 为修改下拉列表宽与数据字典中最宽的的数据一样宽而增加
     * 
     */
    private boolean isSetMaxHight =false;
   
    
    public boolean isSetMaxHight()
	{
		return isSetMaxHight;
	}

	public void setSetMaxHight(boolean isSetMaxHight)
	{
		this.isSetMaxHight = isSetMaxHight;
	}
  /**---------------------------------------------------------------------**/
	/**
     * Create a text inline area
     */
    public TextArea()
    {
    }

    /**
     * Constructor with extra parameters: create a TextAdjustingInfo object
     *
     * @param stretch
     *            the available stretch of the text
     * @param shrink
     *            the available shrink of the text
     * @param adj
     *            the current total adjustment
     */
    public TextArea(int stretch, int shrink, int adj)
    {
        super(stretch, shrink, adj);
    }

    /**
     * Remove the old text
     */
    public void removeText()
    {
        inlines.clear();
    }

    /**
     * Create and add a WordArea child to this TextArea.
     *
     * @param word
     *            the word string
     * @param offset
     *            the offset for the next area
     */
    public void addWord(String word, int offset)
    {
        addWord(word, offset, null);
    }

    /**
     * Create and add a WordArea child to this TextArea.
     *
     * @param word
     *            the word string
     * @param offset
     *            the offset for the next area
     */
    public void addWord(String word, int offset, int[] letterAdjust)
    {
        WordArea wordArea = new WordArea(word, offset, letterAdjust);
        addChildArea(wordArea);
        wordArea.setParentArea(this);
    }

    /**
     * Create and add a SpaceArea child to this TextArea
     *
     * @param space
     *            the space character
     * @param offset
     *            the offset for the next area
     * @param adjustable
     *            is this space adjustable?
     */
    public void addSpace(char space, int offset, boolean adjustable)
    {
        SpaceArea spaceArea = new SpaceArea(space, offset, adjustable);
        addChildArea(spaceArea);
        spaceArea.setParentArea(this);
    }

    /**
     * Get the whole text string. Renderers whose space adjustment handling is not affected by
     * multi-byte characters can use this method to render the whole TextArea at once; the other
     * renderers (for example PDFRenderer) have to implement renderWord(WordArea) and
     * renderSpace(SpaceArea) in order to correctly place each text fragment.
     *
     * @return the text string
     */
    public String getText()
    {
        StringBuffer text = new StringBuffer();
        InlineArea child;
        // assemble the text
        for (int i = 0; i < inlines.size(); i++)
        {
            child = (InlineArea) inlines.get(i);
            if (child instanceof WordArea)
            {
                text.append(((WordArea) child).getWord());
            }
            else
            {
                text.append(((SpaceArea) child).getSpace());
            }
        }
        return text.toString();
    }

    /**
     * set the ipd and notify the parent area about the variation; this happens when a page-number
     * or a page-number-citation is resolved to its actual value
     *
     * @param newIPD
     *            the new ipd of the area
     */
    @Override
	public void updateIPD(int newIPD)
    {
        // remember the old ipd
        int oldIPD = getIPD();
        // set the new ipd
        setIPD(newIPD);
        // check if the line needs to be adjusted because of the ipd variation
        if (newIPD != oldIPD)
        {
            notifyIPDVariation(newIPD - oldIPD);
        }
    }

    public List getConversion()
    {
        return _conversion;
    }

    public void setConversion(List conversion)
    {
        this._conversion = conversion;
    }

    public String getCurrentText()
    {
        return _currentText;
    }

    public void setCurrentText(String currentText)
    {
        this._currentText = currentText;
    }

    public int getEditMode()
    {
        return _editMode;
    }

    public void setEditMode(int editMode)
    {
        this._editMode = editMode;
    }

	public Font getFont()
    {
        return _font;
    }

    public void setFont(Font font)
    {
        this._font = font;
    }

    public List getHideAreas()
    {
        return _hideAreas;
    }

    public void setHideAreas(List hideAreas)
    {
        this._hideAreas = hideAreas;
    }

    public String getHideName()
    {
        return _hideName;
    }

    public void setHideName(String hideName)
    {
        this._hideName = hideName;
    }

    public float getMaxShowWidth()
    {
        return _maxShowWidth;
    }

    public void setMaxShowWidth(float maxShowWidth)
    {
        this._maxShowWidth = maxShowWidth;
    }

    public TextArea getNext()
    {
        return _next;
    }

    public void setNext(TextArea next)
    {
        this._next = next;
    }

    public TextArea getPrevious()
    {
        return _previous;
    }

    public void setPrevious(TextArea previous)
    {
        this._previous = previous;
    }

    public Rectangle2D getShowRec()
    {
        return _showRec;
    }

    public void setShowRec(Rectangle2D showRec)
    {
        this._showRec = showRec;
    }

    public String getXPath()
    {
        return _xPath;
    }

    public void setXPath(String path)
    {
        _xPath = path;
    }

    public boolean isMultiInLine()
    {
        return _multiInLine;
    }

    public void setMultiInLine(boolean inLine)
    {
        _multiInLine = inLine;
    }

//    public void setParentBlockRecX(float recX)
//    {
//        _parentBlockRecX = recX;
//    }
//
//    public float getParentBlockRecX()
//    {
//        return _parentBlockRecX;
//    }
//
//    public void setParentBlockRecY(float recY)
//    {
//        _parentBlockRecY = recY;
//    }
//
//    public float getParentBlockRecY()
//    {
//        return _parentBlockRecY;
//    }

    public void setIndent(float indent)
    {
        _indent = indent;
    }

    public float getIndent()
    {
        return _indent;
    }
    // del.因为parentBlockLength的值为Block内容的宽度，即getIPD()，而不是getAllocIPD，所以不用考虑start-indent的情况
//    public void setTextAlign(boolean textAlign)
//    {
//        _isTextAlign = textAlign;
//    }
//
//    public boolean isTextAlign()
//    {
//        return _isTextAlign;
//    }
}
