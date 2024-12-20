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

/* $Id: RtfGenerator.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.rtf.rtflib.rtfdoc;

import java.io.IOException;
import java.io.Writer;

/**
 * Represents a generator element which says who generated the RTF document.
 */
public class RtfGenerator extends RtfElement {

    /** Default constructor for the generator element. */
    public RtfGenerator(RtfHeader h, Writer w) throws IOException {
        super(h, w);
    }
    
    /**
     * @see com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfElement#writeRtfContent()
     */
    protected void writeRtfContent() throws IOException {
        newLine();
        writeGroupMark(true);
        writeStarControlWord("generator");
        writer.write("Apache XML Graphics RTF Library");
        writer.write(";");
        writeGroupMark(false);
    }

    /**
     * @see com.wisii.fov.render.rtf.rtflib.rtfdoc.RtfElement#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

}
