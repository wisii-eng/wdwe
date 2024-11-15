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

/* $Id: MapPageOverlay.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.modca;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.wisii.fov.render.afp.tools.BinaryUtils;

/**
 * The Map Page Overlay structured field maps a Resource Local ID to the name of
 * a Begin Overlay structured field. A Map Page Overlay structured field may
 * contain from one to 254 repeating groups.
 *
 */
public class MapPageOverlay extends AbstractAFPObject {

    /**
     * The collection of overlays (maximum of 254 stored as byte[])
     */
    private ArrayList _overLays = new ArrayList();

    /**
     * Constructor for the Map Page Overlay
     */
    public MapPageOverlay() {

    }

    /**
     * Add an overlay to to the map page overlay object.
     *
     * @param name
     *            The name of the overlay.
     */
    public void addOverlay(String name) throws MaximumSizeExceededException {

        if (_overLays.size() > 253) {
            throw new MaximumSizeExceededException();
        }

        if (name.length() != 8) {
            throw new IllegalArgumentException("The name of overlay " + name
                + " must be 8 characters");
        }

        if (log.isDebugEnabled()) {
            log.debug("addOverlay():: adding overlay " + name);
        }

        byte[] data;

        try {

            data = name.getBytes(AFPConstants.EBCIDIC_ENCODING);
            _overLays.add(data);

        } catch (UnsupportedEncodingException usee) {

            log
                .error("addOverlay():: UnsupportedEncodingException translating the name "
                + name);

        }

    }

    /**
     * Accessor method to write the AFP datastream for the Map Page Overlay
     * @param os The stream to write to
     * @throws java.io.IOException
     */
    public void writeDataStream(OutputStream os)
        throws IOException {


        int oLayCount = _overLays.size();
        int recordlength = oLayCount * 18;

        byte[] data = new byte[recordlength + 9];

        data[0] = 0x5A;

        // Set the total record length
        byte[] rl1 = BinaryUtils.convert(recordlength + 8, 2); //Ignore the
        // first byte in
        // the length
        data[1] = rl1[0];
        data[2] = rl1[1];

        // Structured field ID for a MPO
        data[3] = (byte) 0xD3;
        data[4] = (byte) 0xAB;
        data[5] = (byte) 0xD8;

        data[6] = 0x00; // Reserved
        data[7] = 0x00; // Reserved
        data[8] = 0x00; // Reserved

        int pos = 8;

        //For each overlay
        byte olayref = 0x00;

        for (int i = 0; i < oLayCount; i++) {

            olayref = (byte) (olayref + 1);

            data[++pos] = 0x00;
            data[++pos] = 0x12; //the length of repeating group

            data[++pos] = 0x0C; //Fully Qualified Name
            data[++pos] = 0x02;
            data[++pos] = (byte) 0x84;
            data[++pos] = 0x00;

            //now add the name
            byte[] name = (byte[]) _overLays.get(i);

            for (int j = 0; j < name.length; j++) {

                data[++pos] = name[j];

            }

            data[++pos] = 0x04; //Resource Local Identifier (RLI)
            data[++pos] = 0x24;
            data[++pos] = 0x02;

            //now add the unique id to the RLI
            data[++pos] = olayref;

        }

        os.write(data);

    }

}