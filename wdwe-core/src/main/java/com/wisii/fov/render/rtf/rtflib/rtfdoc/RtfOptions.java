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

/* $Id: RtfOptions.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.rtf.rtflib.rtfdoc;

/*
 * This file is part of the RTF library of the FOV project, which was originally
 * created by Bertrand Delacretaz <bdelacretaz@codeconsult.ch> and by other
 * contributors to the jfor project (www.jfor.org), who agreed to donate jfor to
 * the FOV project.
 */

/**
 * Simplistic options definitions for RTF generation
 * @author Bertrand Delacretaz bdelacretaz@codeconsult.ch
 */
public class RtfOptions {
    /**
     * If this returns true, RtfParagraphs that have no children will not
     * generate any RTF code
     * @return true
     */
    public boolean ignoreEmptyParagraphs() {
        return true;
    }

    /**
     * If this returns false, RtfContainer will not generate any RTF
     * @param c RtfContainer to be tested
     * @return true
     */
    public boolean renderContainer(RtfContainer c) {
        return true;
    }
}