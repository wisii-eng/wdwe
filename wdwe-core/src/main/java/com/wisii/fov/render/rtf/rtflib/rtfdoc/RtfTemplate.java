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

/* $Id: RtfTemplate.java 426576 2006-07-28 15:44:37Z jeremias $ */


/*
 * This file is part of the RTF library of the FOV project, which was originally
 * created by Bertrand Delacretaz <bdelacretaz@codeconsult.ch> and by other
 * contributors to the jfor project (www.jfor.org), who agreed to donate jfor to
 * the FOV project.
 */

package com.wisii.fov.render.rtf.rtflib.rtfdoc;

import java.io.IOException;

/**
 * Singelton of the RTF style template
 * This class belongs to the <jfor:style-template> tag processing.
 */

public class RtfTemplate  {

    /** Singelton instance */
    private static RtfTemplate instance = null;

    private String templateFilePath = null;

    /**
     * Constructor.
     */
    private RtfTemplate () {

    }


    /**
     * Singelton.
     *
     * @return The instance of RtfTemplate
     */
    public static RtfTemplate getInstance () {
        if (instance == null) {
            instance = new RtfTemplate();
        }

        return instance;
    }


    /**
     * Set the template file and adjust tha path separator
     * @param templateFilePath The full path of the template
     * @throws IOException for I/O problems
     **/
    public void setTemplateFilePath(String templateFilePath) throws IOException {
        // no validity checks here - leave this to the RTF client
        if (templateFilePath == null) {
            this.templateFilePath = null;
        } else {
            this.templateFilePath = templateFilePath.trim();
        }
    }

    /**
     * Write the rtf template
     * @param header Rtf header is the parent
     * @throws IOException On write error
     */
    public void writeTemplate (RtfHeader header) throws IOException {
        if (templateFilePath == null || templateFilePath.length() == 0) {
            return;
        }

        header.writeGroupMark (true);
        header.writeControlWord ("template");
        header.writeRtfString(this.templateFilePath);
        header.writeGroupMark (false);

        header.writeGroupMark (true);
        header.writeControlWord ("linkstyles");
        header.writeGroupMark (false);
    }
}


