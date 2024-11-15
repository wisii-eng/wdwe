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
 *//* $Id: TableHeader.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.flow;

// FOV
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.FONode;


/**
 * Class modelling the fo:table-header object.
 */
public class TableHeader extends TableBody {

    /**
     * @param parent FONode that is the parent of this object
     */
    public TableHeader(FONode parent) {
        super(parent);
    }

    /**
     * @see com.wisii.fov.fo.FONode#startOfNode
     */
    protected void startOfNode() throws FOVException {
        //getFOEventHandler().startHeader(this);
    }

    /**
     * @see com.wisii.fov.fo.FONode#endOfNode
     */
    protected void endOfNode() throws FOVException {
//      getFOEventHandler().endHeader(this);
        if (!(tableRowsFound || tableCellsFound)) {
            missingChildElementError("marker* (table-row+|table-cell+)");
        }
        //判断子节点的子节点是null就把子节点删掉
        if(childNodes==null)return;
        for(int i=childNodes.size()-1;i>=0;i--)

        {
        	FONode child=(FONode)childNodes.get(i);
        	if(child.getChildNodes()==null) childNodes.remove(i);
        }
        if(childNodes.size()==0)childNodes=null;
//      convertCellsToRows();
    }

    /** @see com.wisii.fov.fo.FObj#getLocalName() */
    public String getLocalName() {
        return "table-header";
    }

    /** @see com.wisii.fov.fo.FObj#getNameId() */
    public int getNameId() {
        return FO_TABLE_HEADER;
    }
}
