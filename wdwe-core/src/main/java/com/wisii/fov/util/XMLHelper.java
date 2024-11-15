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
 */
/**
 *    XMLHelper.java
 *    version 1.0
 *    汇智互联
 */
package com.wisii.fov.util;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.jaxen.SimpleNamespaceContext;

/**
 * 这个类是一个工具类，用于更新节点和属性的值、添加子节点、删除子节点等.
 */
public class XMLHelper
{
    private org.dom4j.Document _document;                                       //要进行处理的document对象变量.

    /**
     * 从指定的路径读取一个XML文件内容.
     * 参数 filePath : 指定的XML文件路径.
     * 返回值        : 返回转换后的document对象文档的字符串表示.
     */
    public static String getXmlcByPath(String filePath)
    {
        StringBuffer docString = new StringBuffer(); //返回调用此方法最后返回的字符串内容。
        if(filePath != null && !"".equals(filePath))
        {
            InputStreamReader isr = null;
            try
            {
                isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
                int c = 0;
                while((c = isr.read()) != -1)
                {
                    docString.append((char)c);
                }
                isr.close();
            }
            catch(Exception ex)
            {
                System.err.println("读取文件失败。file: " + filePath);
            }
        }
        return docString.toString();
    }

    /**
     * 通过参数xmlStr初始化类属性document。
     * 参数  xmlStr :需要处理的xml文档内容的字符串表示.
     * 返回值   : true表示处理成功.
     */
    public boolean load(String xmlStr) throws DocumentException
    {
        boolean tag = false;                                                    //标识操作是否成功.
        if(xmlStr != null && !"".equals(xmlStr))
        {
            SAXReader reader = new SAXReader();                                 //初始化一个SAX解析器对象。
            try
            {
                reader.setIncludeExternalDTDDeclarations(true);
                _document = DocumentHelper.parseText(xmlStr);//初始化document对象变量.
                tag = true;
            }
            catch(DocumentException e)
            {
                throw new DocumentException("提供的资源文件不存在或者解析文件错误！", e);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return tag;
    }

    /**
     * 通过参数xpath得到参数doc的相应的节点。
     * 参数  doc  : org.dom4j. Document对象.
     * 参数 xpath : 指定的xml路径表达式(即指定的节点)
     * 返回值     : 返回由xpath指定的节点.
     */
    public org.dom4j.Node selectSingleNode(String xpath)throws Exception
    {
        org.dom4j.Node aNode = null;                                            //临时节点对象，返回调用此方法后的节点。
        if(xpath != null && !"".equals(xpath))
        {
            try
            {
                SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
                Node node = _document.getRootElement();
                DefaultXPath dxp = new DefaultXPath(xpath);
                dxp.setNamespaceContext(namespaceContext);
                aNode = dxp.selectSingleNode(node);
            }
            catch(Exception e)
            {
                throw new Exception("路径表达式不存在或有错误",e);
            }
        }
        return aNode;
    }

    /**
     * 以nodeText更新xpath所对应的节点的文本内容。
     * 参数 xpath    : 指定的xml路径表达式(即指定的节点).
     * 参数 nodeText : 将xpath指定节点的文本内容修改成nodeText的值.
     * 返回值        : true表示处理成功.
     */
    public boolean refreshElementText(String xpath,String nodeText)throws Exception
    {
        boolean tag = false;                                                    //标识操作是否成功.
        if(xpath != null && !"".equals(xpath) && nodeText != null)
        {
            try
            {
                Node node = _document.selectSingleNode(xpath); //得到xpath指定的节点.
                if(node != null)
                {
                    node.setText(nodeText);                                  //设置指定节点的内容.
                    tag = true;
                }
            }
            catch(Exception e)
            {
                throw new Exception("路径表达式不存在或有错误",e);
            }
        }
        return tag ;
    }

    /**
     * 以value更新xpath所对应的属性的值。
     * 参数 xpath : 指定的xml路径表达式(即指定的节点).
     * 参数 value : 将xpath指定节点的属性值修改成value的值.
     * 返回值     : true表示处理成功.
     */
    public boolean refreshAttributeValue(String xpath,String value)throws Exception
    {
        boolean tag = false;                                                    //标识操作是否成功.
        if(xpath != null && !"".equals(xpath) && value != null)
        {
            try
            {
                Attribute att = (Attribute)_document.selectNodes(xpath).get(0); //得到指定节点的属性.
                if(att != null)
                {
                    att.setValue(value);                                        //重新设置属性值.
                    tag = true;
                }
            }
            catch(Exception e)
            {
                throw new Exception("路径表达式在XML文档中不存在或有错误", e);
            }
        }
        return tag;
    }

    /**
     * 在xpath指定的节点的父节点下添加新的xpath指定节点的拷贝到xpath指定节点的后面。
     * 参数 xpath : 指定的xml路径表达式(即指定的节点).
     * 返回值     : true表示处理成功.
     */
    public boolean addNode(String xpath) throws Exception
    {
        boolean tag = false; //标识操作是否成功.
        if(xpath != null && !"".equals(xpath))
        {
            try
            {
                Attribute att = (Attribute)_document.selectNodes(xpath + "/@editmode").get(0); //得到指定节点的属性.
                if(att != null)
                {
                    Element element = (Element)_document.selectNodes(xpath).get(0); //得到xpath指定的节点.
                    if(element != null)
                    {
                        Element parent = element.getParent();                   //返回指定节点的父节点.
                        int position = -1;                                      //标识增加的节点在数组中的位置。
                        Element add = null;                                     //要增加的节点。
                        List childNodes = _document.selectNodes(parent.getUniquePath()+"/*");//返回指定节点父节点下的所有子节点的数组。
                        for(int i = 0; i < childNodes.size();i++)               //增加节点到数组中的指定位置，并删除由xpath指定节点后的所有兄弟节点。。
                        {
//add by xjw。 因为_document.selectNodes(parent.getUniquePath()+"/*")可能将parent的属性当作子节点返回，而导致获取子节点的时候类型转换失败。
                            Element e =null;
                            if(childNodes.get(i) instanceof Element)
                            {
                                e = (Element)childNodes.get(i);
                            }
                            else
                            {
                                continue;
                            }
//add end
                            if(e.equals(element))                               //判断e节点是否等于xpath指定的节点。
                            {
                                Element addNode = (Element)element.clone();
                                childNodes.add(i+1,addNode);                    //将节点增加到数组中的指定位置。
                                position = i+1;                                 //保存被增加节点在数组中的位置。
                                for(int k = position;k < childNodes.size();k++) //删除由xpath指定节点后的所有兄弟节点。
                                {
                                    Element ele = (Element)childNodes.get(k);
                                    parent.remove(ele);                         //这里删除的是XML文件下由xpath指定节点之后的所有兄弟节点，并未删除数组中的节点。
                                }
                                break;
                            }
                        }
                        for(int j = position;j<childNodes.size();j++)           //把数组中从被增加节点的位置开始，把后面的所有节点增加到xpath指定节点的后面。
                        {
                            Element dd= (Element)childNodes.get(j);
                            add = (Element)dd.clone();
                            if(j == position)
                            {
                                parent.add(add);                                //增加一个节点到xpath指定节点的后面。
                                Attribute attribute = (Attribute)_document.selectNodes(getNextPath(xpath)+ "/@editmode").get(0);//返回被增加节点的editmode属性。
                                attribute.setValue("3");                        //修改editmode属性为3。
//add by xjw
                                this.setChildEditable(add);
//add end
                             }
                            else
                                parent.add(add);
                        }
                        tag = true;

                    }
                }
            }
            catch(Exception e)
            {
                tag = false;    //mod by xjw
            }
        }
        return tag;
    }


    /**
     * 删除xpath指定的节点node。
     * 参数 xpath : 指定的xml路径表达式(即指定的节点).
     * 返回值     : true表示处理成功.
     */
    public boolean deleteNode(String xpath)throws Exception
    {
        boolean tag = false;                                                    //标识操作是否成功.
        if(xpath != null && !"".equals(xpath))
        {
            try
            {
                Element element = (Element)_document.selectNodes(xpath).get(0); //得到xpath指定的节点.
                if(element != null)
                {
                    Element parent = element.getParent();                       //返回指定节点的父节点.
                    parent.remove(element);                                     //删除指定节点.
                    tag = true;
                }
            }
            catch(Exception e)
            {
                throw new Exception("路径表达式不存在或为空", e);
            }
        }
        return tag;
    }

    /**
     * 返回document文档内容的字符串表示.
     * 返回值  : 返回document文档内容的字符串表示.
     */
    public String getStringDocument()
    {
        return _document.asXML();
    }

    /**
     * 返回指定节点的位置。
     * nodePath :节点的绝对路径。
     * 返回值：指定节点的位置。
     */
    private int getNodePosition(String nodePath)
    {
        int position = 1;
        if(nodePath.lastIndexOf("[") > -1 && nodePath.lastIndexOf("]") > -1 && nodePath.endsWith("]"))
        {
            String ss = nodePath.substring(nodePath.lastIndexOf("[") + 1, nodePath.length()); //截取nodePath中“[”的后一段字符。
            String temp = ss.substring(0, ss.lastIndexOf("]")); //截取SS中“]”的前一段字符。
            position = Integer.parseInt(temp);
        }
        return position;
    }

    /**
     * 返回path指定节点的下一个兄弟节点绝对路径表达式。
     * 参数 path  ： 指定的节点路径。
     * 返回值   : path指定节点的下一个兄弟节点绝对路径表达式。
     */
    private String getNextPath(String path)
    {
        String ppath = "";
        int position = getNodePosition(path)+1;
        if(path.lastIndexOf("[") > -1 && path.lastIndexOf("]") > -1)
        {
            String start = path.substring(0, path.lastIndexOf("[") + 1);
            String end = path.substring(path.lastIndexOf("]"));
            ppath = start + position + end;
        }
        else
            ppath = path + "[" + position+ "]";
        return ppath;
    }

    /**
     * 对传入的节点进行递归判断是否有下一级子节点，有则进入下一级子节点，修改最后一级的子节点的editable属性，有此属性则修改值为1，否则不处理。
     * 参数 childElement ： 要递归判断是否有子节点的节点。
     * 返回值： 没有
     */
    private void setChildEditable(Element childElement)
    {
        List childs = _document.selectNodes(childElement.getUniquePath()+"/*"); //返回指定节点父节点下的所有子节点的数组。
        if(childs != null)
        {
            for(int k = 0; k < childs.size(); k++) //遍厉被增加节点的所有叶子节点.
            {
//add by xjw 因为_document.selectNodes(parent.getUniquePath()+"/*")可能将parent的属性当作子节点返回，而导致获取子节点的时候类型转换失败。
                Element no =null;
                if(childs.get(k) instanceof Element)
                {
                    no = (Element)childs.get(k); //mod by xjw
                }
                else
                {
                    continue;
                }
//end add
                setChildEditable(no);             //递归判断处理每一个子节点.
                try
                {
                    Attribute attr = (Attribute)_document.selectNodes(no.getUniquePath()
                                                                      + "/@editable").get(0); //返回被增加节点的叶子节点的editable属性
                    attr.setValue("1"); //设置此叶节点的editable属性值为1.
                }
                catch(IndexOutOfBoundsException e)
                {
                    //no.addAttribute("editable","1");        //若叶子节点没有editable属性刚增加此属性,属性值为1.
                }
            }
        }
    }
}
