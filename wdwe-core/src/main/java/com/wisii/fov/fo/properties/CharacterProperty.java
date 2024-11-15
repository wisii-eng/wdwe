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
 *//* $Id: CharacterProperty.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.properties;

import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.PropertyList;

/**
 * Superclass for properties that wrap a character value
 */
public class CharacterProperty extends Property {

    /**
     * Inner class for creating instances of CharacterProperty
     */
    public static class Maker extends PropertyMaker {

        /**
         * @param propId the id of the property for which a Maker should be created
         */
        public Maker(int propId) {
            super(propId);
        }

        public Property make(PropertyList propertyList, String value,
                             FObj fo) {
            char c = value.charAt(0);
            return new CharacterProperty(c);
        }

    }    // end Character.Maker

    private char character;

    /**
     * @param character character value to be wrapped in this property
     */
    public CharacterProperty(char character) {
        this.character = character;
    }

    /**
     * @return this.character cast as an Object
     */
    public Object getObject() {
        return new Character(character);
    }

    /**
     * @return this.character
     */
    public char getCharacter() {
        return this.character;
    }

    /**
     * @return this.character cast as a String
     */
    public String getString() {
        return new Character(character).toString();
    }

}
