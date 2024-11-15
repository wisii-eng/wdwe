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
 *//* $Id: CommonHyphenation.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.expr.PropertyException;

/**
 * Store all common hyphenation properties.
 * See Sec. 7.9 of the XSL-FO Standard.
 * Public "structure" allows direct member access.
 */
public class CommonHyphenation {
    /**
     * The "language" property.
     */
    public String language;

    /**
     * The "country" property.
     */
    public String country;

    /**
     * The "script" property.
     */
    public String script;

    /**
     * The "hyphenate" property.
     */
    public int hyphenate;

    /**
     * The "hyphenation-character" property.
     */
    public char hyphenationCharacter;

    /**
     * The "hyphenation-push-character" property.
     */
    public int hyphenationPushCharacterCount;

    /**
     * The "hyphenation-remain-character-count" property.
     */
    public int hyphenationRemainCharacterCount;

    /**
     * Create a CommonHyphenation object.
     * @param pList The PropertyList with propery values.
     */
    public CommonHyphenation(PropertyList pList) throws PropertyException {
        language = pList.get(Constants.PR_LANGUAGE).getString();
        country = pList.get(Constants.PR_COUNTRY).getString();
        script = pList.get(Constants.PR_SCRIPT).getString();
        hyphenate = pList.get(Constants.PR_HYPHENATE).getEnum();
        hyphenationCharacter = pList.get(Constants.PR_HYPHENATION_CHARACTER).getCharacter();
        hyphenationPushCharacterCount =
            pList.get(Constants.PR_HYPHENATION_PUSH_CHARACTER_COUNT).getNumber().intValue();
        hyphenationRemainCharacterCount =
            pList.get(Constants.PR_HYPHENATION_REMAIN_CHARACTER_COUNT).getNumber().intValue();

    }

}
