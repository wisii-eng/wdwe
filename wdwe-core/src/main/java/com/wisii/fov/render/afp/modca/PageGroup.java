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

/* $Id: PageGroup.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.modca;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A page group is used in the data stream to define a named, logical grouping
 * of sequential pages. Page groups are delimited by begin-end structured fields
 * that carry the name of the page group. Page groups are defined so that the
 * pages that comprise the group can be referenced or processed as a single
 * entity. Page groups are often processed in stand-alone fashion; that is, they
 * are indexed, retrieved, and presented outside the context of the containing
 * document.
 *
 * @author <a href="mailto:pete@townsend.uk.com">Pete Townsend </a>
 */
public class PageGroup extends AbstractNamedAFPObject {

    /**
     * The pages contained within this group
     */
    private List _objects = new ArrayList();

    /**
     * The tag logical elements contained within this group
     */
    private List _tagLogicalElements = new ArrayList();

    /**
     * The page state
     */
    private boolean _complete = false;

    /**
     * Constructor for the PageGroup.
     *
     * @param name
     *            the name of the page group
     */
    public PageGroup(String name) {

        super(name);

    }

    /**
     * Adds a page object to the group.
     *
     * @param page
     *            the page object to add
     */
    public void addPage(PageObject page) {

        if (!_objects.contains(page)) {
            _objects.add(page);
        }

    }

    /**
     * @return the name of the page group
     */
    public String getName() {
        return _name;
    }

    /**
     * Creates a TagLogicalElement on the page.
     *
     * @param name
     *            the name of the tag
     * @param value
     *            the value of the tag
     */
    public void createTagLogicalElement(String name, String value) {

        TagLogicalElement tle = new TagLogicalElement(name, value);
        _tagLogicalElements.add(tle);

    }

    /**
     * Creates an InvokeMediaMap on the page.
     *
     * @param name
     *            the name of the media map
     */
    public void createInvokeMediumMap(String name) {

        InvokeMediumMap imm = new InvokeMediumMap(name);
        _objects.add(imm);

    }

    /**
     * Method to mark the end of the page group.
     */
    public void endPageGroup() {

        _complete = true;

    }

    /**
     * Returns an indication if the page group is complete
     */
    public boolean isComplete() {
        return _complete;
    }

   /**
     * Accessor method to write the AFP datastream for the page group.
     * @param os The stream to write to
     * @throws java.io.IOException
     */
    public void writeDataStream(OutputStream os)
        throws IOException {

        writeStart(os);

        writeObjectList(_tagLogicalElements, os);

        writeObjectList(_objects, os);

        writeEnd(os);

    }

    /**
     * Helper method to write the start of the page group.
     * @param os The stream to write to
     */
    private void writeStart(OutputStream os)
        throws IOException {

        byte[] data = new byte[17];

        data[0] = 0x5A; // Structured field identifier
        data[1] = 0x00; // Length byte 1
        data[2] = 0x10; // Length byte 2
        data[3] = (byte) 0xD3; // Structured field id byte 1
        data[4] = (byte) 0xA8; // Structured field id byte 2
        data[5] = (byte) 0xAD; // Structured field id byte 3
        data[6] = 0x00; // Flags
        data[7] = 0x00; // Reserved
        data[8] = 0x00; // Reserved

        for (int i = 0; i < _nameBytes.length; i++) {

            data[9 + i] = _nameBytes[i];

        }

        os.write(data);

    }

    /**
     * Helper method to write the end of the page group.
     * @param os The stream to write to
     */
    private void writeEnd(OutputStream os)
        throws IOException {

        byte[] data = new byte[17];

        data[0] = 0x5A; // Structured field identifier
        data[1] = 0x00; // Length byte 1
        data[2] = 0x10; // Length byte 2
        data[3] = (byte) 0xD3; // Structured field id byte 1
        data[4] = (byte) 0xA9; // Structured field id byte 2
        data[5] = (byte) 0xAD; // Structured field id byte 3
        data[6] = 0x00; // Flags
        data[7] = 0x00; // Reserved
        data[8] = 0x00; // Reserved

        for (int i = 0; i < _nameBytes.length; i++) {

            data[9 + i] = _nameBytes[i];

        }

        os.write(data);

    }

}