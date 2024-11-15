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
 */package com.wisii.fov.util;

import java.io.*;

//Referenced classes of package sun.misc:
//         CharacterDecoder, CEFormatException, CEStreamExhausted

public class BASE64Decoder extends CharacterDecoder
{

 private static final char pem_array[] = {
     'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
     'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
     'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
     'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
     'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
     'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
     '8', '9', '+', '/'
 };
 private static final byte pem_convert_array[];
 byte decode_buffer[];

 public BASE64Decoder()
 {
     decode_buffer = new byte[4];
 }

 protected int bytesPerAtom()
 {
     return 4;
 }

 protected int bytesPerLine()
 {
     return 72;
 }

 protected void decodeAtom(PushbackInputStream pushbackinputstream, OutputStream outputstream, int i)
     throws IOException
 {
     byte byte0 = -1;
     byte byte1 = -1;
     byte byte2 = -1;
     byte byte3 = -1;
     if(i < 2)
     {
         throw new IOException("BASE64Decoder: Not enough bytes for an atom.");
     }
     int j;
     do
     {
         j = pushbackinputstream.read();
         if(j == -1)
         {
             throw new IOException();
         }
     } while(j == 10 || j == 13);
     decode_buffer[0] = (byte)j;
     j = readFully(pushbackinputstream, decode_buffer, 1, i - 1);
     if(j == -1)
     {
         throw new IOException();
     }
     if(i > 3 && decode_buffer[3] == 61)
     {
         i = 3;
     }
     if(i > 2 && decode_buffer[2] == 61)
     {
         i = 2;
     }
     switch(i)
     {
     case 4: // '\004'
         byte3 = pem_convert_array[decode_buffer[3] & 0xff];
         // fall through

     case 3: // '\003'
         byte2 = pem_convert_array[decode_buffer[2] & 0xff];
         // fall through

     case 2: // '\002'
         byte1 = pem_convert_array[decode_buffer[1] & 0xff];
         byte0 = pem_convert_array[decode_buffer[0] & 0xff];
         // fall through

     default:
         switch(i)
         {
         case 2: // '\002'
             outputstream.write((byte)(byte0 << 2 & 0xfc | byte1 >>> 4 & 3));
             break;

         case 3: // '\003'
             outputstream.write((byte)(byte0 << 2 & 0xfc | byte1 >>> 4 & 3));
             outputstream.write((byte)(byte1 << 4 & 0xf0 | byte2 >>> 2 & 0xf));
             break;

         case 4: // '\004'
             outputstream.write((byte)(byte0 << 2 & 0xfc | byte1 >>> 4 & 3));
             outputstream.write((byte)(byte1 << 4 & 0xf0 | byte2 >>> 2 & 0xf));
             outputstream.write((byte)(byte2 << 6 & 0xc0 | byte3 & 0x3f));
             break;
         }
         break;
     }
 }

 static
 {
     pem_convert_array = new byte[256];
     for(int i = 0; i < 255; i++)
     {
         pem_convert_array[i] = -1;
     }

     for(int j = 0; j < pem_array.length; j++)
     {
         pem_convert_array[pem_array[j]] = (byte)j;
     }

 }
}
