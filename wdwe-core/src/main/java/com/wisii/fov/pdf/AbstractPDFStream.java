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

/* $Id: AbstractPDFStream.java 426576 2006-07-28 15:44:37Z jeremias $ */
 
package com.wisii.fov.pdf;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.CountingOutputStream;
import com.wisii.fov.util.CloseBlockerOutputStream;

/**
 * This is an abstract base class for PDF streams.
 */
public abstract class AbstractPDFStream extends PDFObject {

    /** The filters that should be applied */
    private PDFFilterList filters;

    /**
     * Constructor for AbstractPDFStream.
     */
    public AbstractPDFStream() {
        super();
    }

    /**
     * Sets up the default filters for this stream if they haven't been set 
     * from outside.
     */
    protected void setupFilterList() {
        if (!getFilterList().isInitialized()) {
            getFilterList().addDefaultFilters(
                getDocumentSafely().getFilterMap(), 
                PDFFilterList.DEFAULT_FILTER);
        }
        prepareImplicitFilters();
        getDocument().applyEncryption(this);
    }

    /**
     * Returns the associated filter list.
     * @return the filter list
     */
    public PDFFilterList getFilterList() {
        if (this.filters == null) {
            if (getDocument() == null) {
                this.filters = new PDFFilterList();
            } else {
                this.filters = new PDFFilterList(getDocument().isEncryptionActive());
                //this.filters = new PDFFilterList(false);
            }
        }
        return this.filters;
    }

    /**
     * Returns a value that hints at the size of the encoded stream. This is
     * used to optimize buffer allocation so fewer buffer reallocations are
     * necessary. 
     * @return an estimated size (0 if no hint can be given)
     * @throws IOException in case of an I/O problem
     */
    protected abstract int getSizeHint() throws IOException;
    
    /**
     * Sends the raw stream data to the target OutputStream.
     * @param out OutputStream to write to
     * @throws IOException In case of an I/O problem
     */
    protected abstract void outputRawStreamData(OutputStream out) 
            throws IOException;

    /**
     * Output just the stream data enclosed by stream/endstream markers
     * @param encodedStream already encoded/filtered stream to write
     * @param out OutputStream to write to
     * @return int number of bytes written
     * @throws IOException in case of an I/O problem
     */
    protected int outputStreamData(StreamCache encodedStream, OutputStream out) throws IOException {
        int length = 0;
        byte[] p = encode("stream\n");
        out.write(p);
        length += p.length;
        
        encodedStream.outputContents(out);
        length += encodedStream.getSize();
        
        p = encode("\nendstream");
        out.write(p);
        length += p.length;
        return length;
    }

    /**
     * Encodes the raw data stream for output to a PDF file.
     * @return the encoded stream
     * @throws IOException in case of an I/O problem
     */
    protected StreamCache encodeStream() throws IOException {
        //Allocate a temporary buffer to find out the size of the encoded stream
        final StreamCache encodedStream = StreamCacheFactory.getInstance().
                createStreamCache(getSizeHint());
        OutputStream filteredOutput = 
                getFilterList().applyFilters(encodedStream.getOutputStream());
        outputRawStreamData(filteredOutput);
        filteredOutput.flush();
        filteredOutput.close();
        return encodedStream;
    }

    /**
     * Encodes and writes a stream directly to an OutputStream. The length of
     * the stream, in this case, is set on a PDFNumber object that has to be
     * prepared beforehand.
     * @param out OutputStream to write to
     * @param refLength PDFNumber object to receive the stream length
     * @return number of bytes written (header and trailer included)
     * @throws IOException in case of an I/O problem
     */
    protected int encodeAndWriteStream(OutputStream out, PDFNumber refLength)
                throws IOException {
        int bytesWritten = 0;
        //Stream header
        byte[] buf = encode("stream\n");
        out.write(buf);
        bytesWritten += buf.length;
        
        //Stream contents
        CloseBlockerOutputStream cbout = new CloseBlockerOutputStream(out);
        CountingOutputStream cout = new CountingOutputStream(cbout);
        OutputStream filteredOutput = 
                getFilterList().applyFilters(cout);
        outputRawStreamData(filteredOutput);
        filteredOutput.close();
        refLength.setNumber(new Integer(cout.getCount()));
        bytesWritten += cout.getCount();
            
        //Stream trailer
        buf = encode("\nendstream");
        out.write(buf);
        bytesWritten += buf.length;
        
        return bytesWritten;
    }

    /**
     * Overload the base object method so we don't have to copy
     * byte arrays around so much
     * @see com.wisii.fov.pdf.PDFObject#output(OutputStream)
     */
    protected int output(OutputStream stream) throws IOException {
        int length = 0;
        setupFilterList();
        
        StreamCache encodedStream = null;
        PDFNumber refLength = null;
        final String lengthEntry;
        if (getDocument().isEncodingOnTheFly()) {
            refLength = new PDFNumber();
            getDocumentSafely().registerObject(refLength);
            lengthEntry = refLength.referencePDF();
        } else {
            encodedStream = encodeStream();
            lengthEntry = Integer.toString(encodedStream.getSize() + 1);
        }
        
        String filterEntry = getFilterList().buildFilterDictEntries();
        byte[] p = encode(buildStreamDict(lengthEntry));

        stream.write(p);
        length += p.length;
        
        //Send encoded stream to target OutputStream
        if (encodedStream == null) {
            int bytesWritten = encodeAndWriteStream(stream, refLength);
            length += bytesWritten;
        } else {
            length += outputStreamData(encodedStream, stream);
            encodedStream.clear(); //Encoded stream can now be discarded
        }
        
        p = encode("\nendobj\n");
        stream.write(p);
        length += p.length;
        return length;
    }

    /**
     * Constructs the dictionary for the stream. Override this method if you
     * need additional entries.
     * @param lengthEntry value for the /Length entry
     * @return the newly constructed dictionary
     */
    protected String buildStreamDict(String lengthEntry) {
        final String filterEntry = getFilterList().buildFilterDictEntries();
        return (getObjectID()
            + "<< /Length " + lengthEntry + "\n"
            + filterEntry
            + "\n>>\n");
    }

    /**
     * Prepares implicit filters (such as the DCTFilter for JPEG images). You
     * must make sure that the appropriate filters are in the filter list at
     * the right places.
     */
    protected void prepareImplicitFilters() {
        //nop: No default implicit filters
    }

}
