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

/* $Id: AFPAttribute.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.extensions;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.properties.Property;
import com.wisii.fov.fo.properties.StringProperty;

/**
 * This class extends the com.wisii.fov.fo.StringProperty.Maker inner class
 * in order to provide a static property maker. The object faciliates
 * extraction of attributes from formatted objects based on the static list
 * as defined in the AFPElementMapping implementation.
 * <p/>
 */
public class AFPAttribute extends StringProperty.Maker {

    /**
     * The attribute property.
     */
    private Property _property;

    /**
     * Constructor for the AFPAttribute.
     * @param name The attribute name
     */
    protected AFPAttribute(String name) {
        super(0);
        _property = null;
    }

    /**
     * Overide the make method to return the property object
     * @param propertyList the property list from which to make the property
     * @return property The property object.
     */
    public Property make(PropertyList propertyList) {
        if (_property == null) {
            _property = make(propertyList, "", propertyList.getParentFObj());
        }
        return _property;
    }

}