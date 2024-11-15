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

/* $Id: AreaTreeInputHandler.java,v 1.1 2007/07/06 07:30:56 hzl Exp $ */

package com.wisii.fov.cli;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.area.AreaTreeModel;
import com.wisii.fov.area.AreaTreeParser;
import com.wisii.fov.area.RenderPagesModel;
import com.wisii.fov.fonts.FontInfo;
import org.xml.sax.SAXException;

/**
 * InputHandler for the area tree XML (intermediate format) as input.
 */
public class AreaTreeInputHandler extends InputHandler {

//    /**
//     * Constructor for XML->XSLT->area tree XML input
//     * @param xmlfile XML file
//     * @param xsltfile XSLT file
//     * @param params Vector of command-line parameters (name, value, 
//     *      name, value, ...) for XSL stylesheet, null if none
//     */
//    public AreaTreeInputHandler(File xmlfile, File xsltfile, Vector params) {
//        super(xmlfile, xsltfile, params);
//    }

    /**
     * Constructor for area tree XML input
     * @param atfile the file to read the area tree document.
     * @throws FileNotFoundException 
     */
    public AreaTreeInputHandler(InputStream atfile){
    		super(atfile);
    }

    /** @see com.wisii.fov.cli.InputHandler */
    public void renderTo(FOUserAgent userAgent, String outputFormat, OutputStream out) 
                throws FOVException {
        FontInfo fontInfo = new FontInfo();
        AreaTreeModel treeModel = new RenderPagesModel(userAgent, 
                outputFormat, fontInfo, out);
        
        //Iterate over all intermediate files
        AreaTreeParser parser = new AreaTreeParser();
        
        // Resulting SAX events (the generated FO) must be piped through to FOV
        Result res = new SAXResult(parser.getContentHandler(treeModel, userAgent));

        transformTo(res);
        
        try {
            treeModel.endDocument();
        } catch (SAXException e) {
            throw new FOVException(e);
        }
    }

}
