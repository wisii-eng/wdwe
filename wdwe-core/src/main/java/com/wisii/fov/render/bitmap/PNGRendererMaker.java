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

/* $Id: PNGRendererMaker.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.bitmap;

import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.render.AbstractRendererMaker;
import com.wisii.fov.render.Renderer;

/**
 * RendererMaker for the PNG Renderer.
 */
public class PNGRendererMaker extends AbstractRendererMaker {

    private static final String[] MIMES = new String[] {MimeConstants.MIME_PNG};
    
    
    /** @see com.wisii.fov.render.AbstractRendererMaker */
    public Renderer makeRenderer(FOUserAgent ua) {
        return new PNGRenderer();
    }

    /** @see com.wisii.fov.render.AbstractRendererMaker#needsOutputStream() */
    public boolean needsOutputStream() {
        return true;
    }

    /** @see com.wisii.fov.render.AbstractRendererMaker#getSupportedMimeTypes() */
    public String[] getSupportedMimeTypes() {
        return MIMES;
    }

}
