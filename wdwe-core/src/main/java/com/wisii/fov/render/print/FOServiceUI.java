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
 */package com.wisii.fov.render.print;

import javax.print.ServiceUI;
import javax.print.PrintService;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Frame;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import sun.print.ServiceDialog;
import java.awt.Window;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import java.awt.KeyboardFocusManager;
import java.awt.Dialog;
import javax.print.DocFlavor;
import java.awt.GraphicsEnvironment;
import sun.print.SunAlternateMedia;

class FOServiceUI extends ServiceUI
{
    public static PrintService printDialog(GraphicsConfiguration graphicsconfiguration, int i, int j, PrintService aprintservice[], PrintService printservice, DocFlavor docflavor, PrintRequestAttributeSet printrequestattributeset)
        throws HeadlessException
    {
        int k = -1;
        if(GraphicsEnvironment.isHeadless())
        {
            throw new HeadlessException();
        }
        if(aprintservice == null || aprintservice.length == 0)
        {
            throw new IllegalArgumentException("services must be non-null and non-empty");
        }
        if(printrequestattributeset == null)
        {
            throw new IllegalArgumentException("attributes must be non-null");
        }
        if(printservice != null)
        {
            int l = 0;
            do
            {
                if(l >= aprintservice.length)
                {
                    break;
                }
                if(aprintservice[l].equals(printservice))
                {
                    k = l;
                    break;
                }
                l++;
            } while(true);
            if(k < 0)
            {
                throw new IllegalArgumentException("services must contain defaultService");
            }
        } else
        {
            k = 0;
        }
        boolean flag = false;
        Object obj = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        if(!(obj instanceof Dialog) && !(obj instanceof Frame))
        {
            obj = new Frame();
            flag = true;
        }
        FOServiceDialog myservicedialog;
        if(obj instanceof Frame)
        {
            myservicedialog = new FOServiceDialog(graphicsconfiguration, i, j, aprintservice, k, docflavor, printrequestattributeset, (Frame)obj);
        } else
        {
            myservicedialog = new FOServiceDialog(graphicsconfiguration, i, j, aprintservice, k, docflavor, printrequestattributeset, (Dialog)obj);
        }
        myservicedialog.show();
        if(myservicedialog.getStatus() == 1)
        {
            PrintRequestAttributeSet printrequestattributeset1 = myservicedialog.getAttributes();
//            Class class1 = javax/print/attribute/standard/Destination;
//            Class class2 = sun/print/SunAlternateMedia;
//            Class class3 = javax/print/attribute/standard/Fidelity;
            Class class1 = Destination.class;
            Class class2 = SunAlternateMedia.class;
            Class class3 = Fidelity.class;

            if(printrequestattributeset.containsKey(class1) && !printrequestattributeset1.containsKey(class1))
            {
                printrequestattributeset.remove(class1);
            }
            if(printrequestattributeset.containsKey(class2) && !printrequestattributeset1.containsKey(class2))
            {
                printrequestattributeset.remove(class2);
            }
            printrequestattributeset.addAll(printrequestattributeset1);
            Fidelity fidelity = (Fidelity)printrequestattributeset.get(class3);
            if(fidelity != null && fidelity == Fidelity.FIDELITY_TRUE)
            {
                removeUnsupportedAttributes(myservicedialog.getPrintService(), docflavor, printrequestattributeset);
            }
        }
        if(flag)
        {
            ((Window) (obj)).dispose();
        }
        return myservicedialog.getPrintService();
    }

    private static void removeUnsupportedAttributes(PrintService printservice, DocFlavor docflavor, AttributeSet attributeset)
    {
        AttributeSet attributeset1 = printservice.getUnsupportedAttributes(docflavor, attributeset);
        if(attributeset1 != null)
        {
            Attribute aattribute[] = attributeset1.toArray();
            for(int i = 0; i < aattribute.length; i++)
            {
                Class class1 = aattribute[i].getCategory();
                if(printservice.isAttributeCategorySupported(class1))
                {
                    Attribute attribute = (Attribute)printservice.getDefaultAttributeValue(class1);
                    if(attribute != null)
                    {
                        attributeset.add(attribute);
                    } else
                    {
                        attributeset.remove(class1);
                    }
                } else
                {
                    attributeset.remove(class1);
                }
            }

        }
    }

}
