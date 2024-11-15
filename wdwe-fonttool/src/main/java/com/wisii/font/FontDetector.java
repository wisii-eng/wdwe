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
 */
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
 */

/* $Id: FontDetector.java 815383 2009-09-15 16:15:11Z maxberger $ */

package com.wisii.font;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.font.finder.FontFileFinder;

/**
 * Detector of operating system and classpath fonts
 */
public class FontDetector {
    private static Log log = LogFactory.getLog(FontDetector.class);

    private static final String[] FONT_MIMETYPES = {
        "application/x-font", "application/x-font-truetype"
    };
    
    private FontAdder fontAdder;
    private boolean strict;

    /**
     * Main constructor
     * @param manager the font manager
     * @param adder the font adder
     * @param strict true if an Exception should be thrown if an error is found.
     */
    public FontDetector(FontAdder adder, boolean strict) {
        this.fontAdder = adder;
        this.strict = strict;
    }

    /**
     * Detect installed fonts on the system
     * @param fontInfoList a list of fontinfo to populate
     * @throws FOPException thrown if a problem occurred during detection
     */
    public void detect(List/*<EmbedFontInfo>*/ fontInfoList){
        // search in font base if it is defined and
        // is a directory but don't recurse
        FontFileFinder fontFileFinder = new FontFileFinder();

        // native o/s font directory finding
        List/*<URL>*/ systemFontList;
        try {
            systemFontList = fontFileFinder.find();
            fontAdder.add(systemFontList, fontInfoList);
        } catch (IOException e) {
           e.printStackTrace();
        }

      /*  // classpath font finding
        ClasspathResource resource = ClasspathResource.getInstance();
        for (int i = 0; i < FONT_MIMETYPES.length; i++) {
            fontAdder.add(resource.listResourcesOfMimeType(FONT_MIMETYPES[i]), fontInfoList);
        }*/
    }
}
