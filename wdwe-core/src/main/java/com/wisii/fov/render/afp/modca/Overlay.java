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

/* $Id: Overlay.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.modca;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 */
public class Overlay extends AbstractPageObject{

    /**
     * Construct a new overlay object for the specified name argument, the overlay
     * name should be an 8 character identifier.
     *
     * @param name
     *            the name of the page.
     * @param width
     *            the width of the page.
     * @param height
     *            the height of the page.
     * @param rotation
     *            the rotation of the page.
     */
    public Overlay(String name, int width, int height, int rotation) {

        super(name, width, height, rotation);

    }

    /**
     * Accessor method to write the AFP datastream for the overlay.
     *
     * @param os The stream to write to
     * @throws java.io.IOException
     */
    public void writeDataStream(OutputStream os)
        throws IOException {

        writeStart(os);

        _activeEnvironmentGroup.writeDataStream(os);

        writeObjectList(_segments, os);

        writeObjectList(_tagLogicalElements, os);

        writeObjectList(_objects, os);

        writeEnd(os);

    }

    /**
     * Helper method to write the start of the overlay.
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
        data[5] = (byte) 0xDF; // Structured field id byte 3
        data[6] = 0x00; // Flags
        data[7] = 0x00; // Reserved
        data[8] = 0x00; // Reserved

        for (int i = 0; i < _nameBytes.length; i++) {

            data[9 + i] = _nameBytes[i];

        }

        os.write(data);

    }

    /**
     * Helper method to write the end of the overlay.
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
        data[5] = (byte) 0xDF; // Structured field id byte 3
        data[6] = 0x00; // Flags
        data[7] = 0x00; // Reserved
        data[8] = 0x00; // Reserved

        for (int i = 0; i < _nameBytes.length; i++) {

            data[9 + i] = _nameBytes[i];

        }

        os.write(data);

    }

}
