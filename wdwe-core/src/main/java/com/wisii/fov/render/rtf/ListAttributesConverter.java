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

/* $Id: ListAttributesConverter.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.rtf;

//FOV
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.flow.ListBlock;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfAttributes;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfListTable;
import com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfText;

 /**
  * @autor bdelacretaz, bdelacretaz@codeconsult.ch
  * @autor Christopher Scott, scottc@westinghouse.com
  * Portions created by Christopher Scott are Coypright (C) 2001
  * Westinghouse Electric Company. All Rights Reserved.
  * @autor Peter Herweg, pherweg@web.de
  */

/**
 * Provides methods to convert list attributes to RtfAttributes.
 */
public final class ListAttributesConverter {
    
    /**
     * Constructor is private, because it's just a utility class.
     */
    private ListAttributesConverter() {
    }
    
    /**
     * Reads an FO object's properties and adds returns them as RtfAttributes.
     * @param fobj FO object
     * @return RtfAttributes object which contains the read values.
     * @throws FOVException Thrown when an IO-problem occurs.
     */
    static RtfAttributes convertAttributes(ListBlock fobj)
    throws FOVException {
        
        FOVRtfAttributes attrib = new FOVRtfAttributes();
        
        attrib.setTwips(RtfListTable.LIST_INDENT, fobj.getCommonMarginBlock().startIndent);
        attrib.setTwips(RtfText.LEFT_INDENT_BODY, fobj.getCommonMarginBlock().endIndent);
        
        /*
         * set list table defaults
         */

        //set a simple list type
        attrib.set(RtfListTable.LIST, "simple");
        //set following char as tab
        attrib.set(RtfListTable.LIST_FOLLOWING_CHAR, 0);
        
        return attrib;
    }
}