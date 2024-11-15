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
 */package com.wisii.component.validate.validatexml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p><pre>
 * 1.概述：
 *     本类的作用是利用SAX解析xml schema文件，得到各xml数据节点的类型信息和其他的限定条件，
 * 作为客户端进行数据校验的依据。
 * 2.基本思路：
 *     在解析xml schema文件的过程中，将所有需要的信息解析出来存入相应的容器，然后在<code>
 * endDocument</code>方法中进行处理。这样做的原因是在schema中，数据节点的定义出现的位置是随机
 * 的、无序的。
 * 3.处理结果：
 *     处理的结果是allElementMap,其结构信息参见属性<code>allElementMap</code>，通过public方法
 * <code>getAllElementMap</code>得到处理结果。
 * </pre></p>
 * @author zkl.
 * @version 1.0  20007/05/15
 */
public class SchemaTreeBuilder extends DefaultHandler
{
    /**
     * <p>解析是schema文件中的所有节点的栈，在执行<code>startElement</code>的时候将节点压入，
     * 执行<code>endDoucment</code>方法的时候将节点弹出。</p>
     */
    private Stack nodeStack;

    /**
     * <p>在解析过程中对应的xml数据文件中的节点所组成的路径。
     * 解析时,组成currPath的逻辑如下：
     * 当解析schema文件遇到<b>element</b>元素时，在<code>startElement</code>方法中，取其
     * <i>name</i>或者<i>ref</i>属性，作如下运算：currPath=currPath+"/"+name；在<code>endElement</code>
     * 方法中，作如下运算：currPath=currPath.subString(0,currPath.lastIndexOf('/')).
     * </p>
     */
    private String currPath="";

    /**
     * <p>当解析schema文件遇到<b>complexType</b>元素时，将其<i>name</i>属性赋给
     * <code>currTypeName</code>.若<b>complexType</b>元素没有<i>name</i>属性，则为<code>null</code>。
     * </p>
     */
    private String currTypeName;

    /**
     *  <p>当解析schema文件遇到<b>group</b>或<b>attributeGroup</b>元素时，将其<i>name</i>属性
     *  赋给<code>currGroupName</code>.若没有此属性则赋为<code>null</code>。
     */
    private String currGroupName;

    /**
     * <p>当解析schema文件遇到<b>simpleType</b>元素时，将其<i>name</i>属性
     *  赋给<code>currSimpleTypeName</code>.若没有此属性则赋为<code>null</code>。
     */
    private String currSimpleTypeName;

    /**
     * <p>当解析schema文件遇到具有如下特点的<b>simpleType</b>元素时，将<code>inSimpleTypeFlag</code>
     * 赋为<code>true</code>：
     * <blockquote><pre>
     *   1) <b>simpleType</b>元素的<i>name</i>属性不为<code>null</code>；
     *   2) <b>simpleType</b>元素的上层元素不为<b>element</b>元素。
     * </pre></blockquote>
     * <p>注：上述第二条合适替换为：<b>simpleType</b>元素的上层元素为<b>schema</b>元素。
     */
    private boolean inSimpleTypeFlag=false;

    /**
     * <p>当解析schema文件遇到具有如下特点的<b>group</b>或<b>attributeGroup</b>元素时，执行操作
     * <code>groupFlag++</code>：
     * <blockquote><pre>
     *   1) <b>group</b>或<b>attributeGroup</b>元素的<i>name</i>属性不为<code>null</code>；
     *   2) <b>group</b>或<b>attributeGroup</b>元素的上层元素为<b>schema</b>元素。
     * </pre></blockquote>
     * <p>当到达相应的结束标签时，将其置为<b>0</b>.
     */
    private int groupFlag=0;

//	private int xpathFlag=0;

    /**<p>当解析schema文件遇到<b>complexType</b>元素时，执行操作<code>groupFlag++</code>：*/
    private int typeFlag=0;

    /**
     * <p>当解析schema文件遇到具有如下特点的<b>complexType</b>元素时，执行操作
     * <code>validType=typeFlag</code>：
     * <blockquote><pre>
     *   1) <b>simpleType</b>元素的<i>name</i>属性不为<code>null</code>；
     *   2) <b>simpleType</b>元素的上层元素不为<b>element</b>元素。
     * </pre></blockquote>
     * <p>注：上述第二条可以替换为：<b>simpleType</b>元素的上层元素为<b>schema</b>元素。
     */
    private int validType=999;

    // For read annotaion in Schema. ADD by rla
    static String ANN_ANNOTATION = "annotation";
    static String ANN_APPINFO = "appinfo";
    static String ANN_DOCUMENTATION = "documentation";

    /**
     * 当解析schema文件遇到annotation元素时，执行操作操作isAnnotation=1，
     * 再获得annotation内容后，还原
     */
    private int isAnnotation=0;

    private String strAnnotationName;

    private String strAnnotationContent;
    // end ADD

    /**
     * <p>用来存储外部定义simpleType的Map，其结构如下：
     * <blockquote><pre>
     *         ===========================================================
     *         =          <b>key</b>            =     <b>value</b>       =
     *         -----------------------------------------------------------
     *         =<code>typeName(name属性)</code> =<code>(Map)typeInfo</code>
     *         ===========================================================
     * </pre></blockquote>
     */
    private Map simpleTypeMap;

    /**
     * <p>用来存储所有解析处的元素和属性的Map，作为解析的结果。其结构如下：
     * <blockquote><pre>
     *         ===================================
     *         =   <b>key</b>   =  <b>value</b>  =
     *         -----------------------------------
     *         =    xpath       = (Map)element   =
     *         ===================================
     * </pre></blockquote>
     */
    private Map allElementMap;

    /**
     * <p>用来存储外部定义的<b>complexType</b>元素中的element或attribute。其结构为：
     * key：<b>complexType</b>的<i>name</i>属性； value：(Map)elements（xpath : element）
     */
    private Map elementTypeMap;

    /**
     * <p>一个全局的Map容器，没有什么特殊的意义。
     */
    private Map elementRefMap;

    /**
     * <p>处理迭代时用来存放新添进<code>allElementMap</code>中的element。
     */
    private Map branchElementMap;

    /**
     * <p>协助处理迭代。
     */
    private Map branchElementMap2;

    /**
     * <p>用来存储所有在<外部定义的Group中的element或attribute元素。
     */
    private Map allGroupMap;

    /**
     * <p>存储所有<b>group</b>或<b>attributeGroup</b>元素引用其他<b>group</b>或<b>attributeGroup</b>元素
     * 的位置。其结构为：
     * key: groupName(即ref属性的值)，value: (HashSet)引用的位置集合（集合中的元素为xpath.）。
     * value值的设置包括两种情况：
     * <pre>
     *     1) 当group引用位置处在外部定义的complexType中时，其取值为<code>"inOutterComplexType_"+currTypeName+":"+currPath</code>;
     *     2) 否则其取值为：<code>currPath</code>。
     * </pre>
     */
    private Map groupRefMap;

    /**
     * <p>开始schema文档的解析，初始化相关变量。
     */
    public void startDocument() throws SAXException
    {
        nodeStack=new Stack();
        simpleTypeMap=new HashMap();
        allElementMap=new HashMap();
        elementTypeMap=new HashMap();
        branchElementMap=new HashMap();
        branchElementMap2=new HashMap();
        elementRefMap=new HashMap();
        allGroupMap=new HashMap();
        groupRefMap=new HashMap();

    }

    /**
     * <p>在每个元素的开始标签处所做的处理。
     *
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        // System.out.println("\r\nuri: "+ uri + " localName: "+ localName + "\r\nqName: "+ qName + " attributes: "+ attributes);

        if (localName.equals(ANN_ANNOTATION)||localName.equals(ANN_APPINFO)||localName.equals(ANN_DOCUMENTATION))
        {
            if (localName.equals(ANN_ANNOTATION))
            {
                isAnnotation = 0;
                strAnnotationName = ANN_ANNOTATION;
            }
            else if (localName.equals(ANN_APPINFO))
            {
                isAnnotation = 1;
                strAnnotationName = ANN_APPINFO;
            }
            else if (localName.equals(ANN_DOCUMENTATION))
            {
                isAnnotation = 2;
                strAnnotationName = ANN_DOCUMENTATION;
            }
            //return;
        }

        if (localName.equals("element")||localName.equals("attribute"))
        {
            String ref = attributes.getValue("ref");
            
            String elementName = attributes.getValue("name");
            String elementType = attributes.getValue("type");
            
            
//            System.out.println(ref +"---"+elementName+"---"+elementType);
            if(elementType!=null&&elementType.indexOf(':')!=-1)
            {
                elementType=elementType.substring(elementType.indexOf(':')+1);
            }

            if (elementName != null)
            {
                if(localName.equals("element"))
                {
                    currPath = currPath + "/" + elementName;
                }else
                {
                    currPath = currPath + "/@" + elementName;
                }
//				System.out.println("currPath= "+currPath);
                Map xmlElement = new HashMap();
                for(int i=0;i<attributes.getLength();i++)
                {
                    xmlElement.put(attributes.getQName(i), attributes.getValue(i));
                }
                xmlElement.put("xpath", currPath);
                if (elementType != null)
                {
                    xmlElement.put("type", elementType);
                }
                if(typeFlag>=validType)
                {
                    Map elementMap=(Map)elementTypeMap.get(currTypeName);
                    elementMap.put(currPath, xmlElement);
                    elementTypeMap.put(currTypeName, elementMap);
                }else if(groupFlag>0)
                {
                    Map elementmap=(Map) allGroupMap.get(currGroupName);
                    if(elementmap!=null)
                    {
                        elementmap.put(currPath, xmlElement);
                    }
                }
                else
                {
                    allElementMap.put(currPath, xmlElement);
                }


            }else if(ref != null)
            {
//            	System.out.println("isRefElement:"+ref);
                currPath = currPath + "/" + ref;
                Map xmlElement = new HashMap();
                xmlElement.put("name", ref);
                xmlElement.put("xpath", currPath);
                xmlElement.put("type", "isRefElement:"+ref);
                allElementMap.put(currPath, xmlElement);
            }
//			xpathFlag++;
        }else if(localName.equals("complexType"))
        {
            typeFlag++;
            TreeNode father=(TreeNode)nodeStack.peek();
            String fatherName=father.getName();
            if(fatherName.equals("element"))
            {}
            else
            {
            currTypeName = attributes.getValue("name");
                if (currTypeName != null)
                {
                    Map elementMap = (Map) elementTypeMap.get(currTypeName);
                    if (elementMap == null)
                    {
                        elementMap = new HashMap();
                        elementTypeMap.put(currTypeName, elementMap);
                    }
                    validType=typeFlag;
                }
            }
        }else if (localName.equals("group")||localName.equals("attributeGroup"))
        {

            String ref = attributes.getValue("ref");
            String name = attributes.getValue("name");
            String fathername=((TreeNode)nodeStack.peek()).getName();
            if(ref!=null)
            {
                Set refPositions=(Set) groupRefMap.get(ref);
                if(refPositions==null){
                    refPositions=new HashSet();
                }

                if(currTypeName!=null)
                {
                    refPositions.add("inOutterComplexType_"+currTypeName+":"+currPath);
                    groupRefMap.put(ref, refPositions);
                }else
                {
                    refPositions.add(currPath);
                    groupRefMap.put(ref, refPositions);
                }


            }else if(name!=null&&fathername.equals("schema"))
            {
                currGroupName=name;
                groupFlag++;
                if(allGroupMap.get(name)==null)
                {
                    allGroupMap.put(name, new HashMap());
                }

            }

        }
        else if(localName.equals("simpleType"))
        {
            TreeNode father=(TreeNode)nodeStack.peek();
            String fatherName=father.getName();
            currSimpleTypeName=attributes.getValue("name");
            if(fatherName.equals("element")||currSimpleTypeName==null)
            {}
            else
            {
                inSimpleTypeFlag=true;
                Map typeInfo=(Map) simpleTypeMap.get(currSimpleTypeName);
                if(typeInfo==null)
                {
                    typeInfo=new HashMap();
                    simpleTypeMap.put(currSimpleTypeName, typeInfo);
                }
            }
        }
        else if(localName.equals("restriction")||localName.equals("extension"))
        {

            String base=attributes.getValue("base");
            if(base!=null)
            {
                if(base.indexOf(':')!=-1)
                {
                    base=base.substring(base.indexOf(':')+1);
                }

                if(inSimpleTypeFlag&&currSimpleTypeName!=null)
                {
                    Map typeInfo=(Map) simpleTypeMap.get(currSimpleTypeName);
                    if(typeInfo!=null)
                    {
                        typeInfo.put("type", base);
                        typeInfo.put("base", base);
                    }
                }else
                {
                    Map currElement=(Map) allElementMap.get(currPath);
                    if(currElement==null)
                    {
                        if(elementTypeMap.get(currTypeName)==null){
                            elementTypeMap.put(currTypeName, new HashMap());
                        }

                        currElement=(Map) ((Map) elementTypeMap.get(currTypeName)).get(currPath);
                    }
                    if (currElement != null)
                    {
                        currElement.put("type", base);
                        currElement.put("base", base);
                    }else{
                        TreeNode node1=(TreeNode) nodeStack.peek();
                        if(node1.getName().equals("simpleContent")){
                            nodeStack.pop();
                            TreeNode typeNode=(TreeNode) nodeStack.peek();
                            if(typeNode.getName().equals("complexType")){
                                String typeName=typeNode.getAtt_name();
                                Map typeInfo=new HashMap();
                                typeInfo.put("type", base);
                                typeInfo.put("base", base);
                                simpleTypeMap.put(typeName, typeInfo);
                            }
                            nodeStack.push(node1);
                        }
                    }
                }
            }
        }else
        {
            if (!nodeStack.empty())
            {
                TreeNode father = (TreeNode) nodeStack.peek();
                String fatherName = father.getName();
                String value = attributes.getValue("value");
                if (fatherName.equals("restriction"))
                {
                    if(inSimpleTypeFlag&&currSimpleTypeName!=null)
                    {
                        Map typeInfo=(Map) simpleTypeMap.get(currSimpleTypeName);
                        if(typeInfo!=null)
                        {
                            if(localName.equals("enumeration"))
                            {
                                typeInfo.put(localName+"_"+value, value);
                                typeInfo.put("type","isEnumerationType");

                            } else
                            {
                                if (value != null) // Add by rla
                                    typeInfo.put(localName, value);

                            }

                        }
                    }
                    else
                    {
                        Map currElement = (Map) allElementMap.get(currPath);
//						System.out.println("currPath= "+currPath);

                        if (currElement == null)
                        {
                            Map elementMap = (Map) elementTypeMap.get(currTypeName);
                            currElement=(Map) elementMap.get(currPath);

                        }
                        if (currElement != null)
                        {
                            if(localName.equals("enumeration"))
                            {
                                currElement.put(localName+"_"+value, value);
                                currElement.put("type","isEnumerationType");
                            } else
                            {
                                currElement.put(localName, value);
                            }
                        }
                    }

                }
            }

        }

        TreeNode currTreeNode=new TreeNode(localName);
        String att_name=attributes.getValue("name");
        if(att_name!=null){
            currTreeNode.setAtt_name(att_name);
        }
        nodeStack.push(currTreeNode);
    }


    public void characters(char[] ch, int start, int length)
    throws SAXException
    {
        // Get the Annotation Content from XML Schema, ADD by rla
        if (strAnnotationName == ANN_APPINFO)
        {
            strAnnotationContent = new String(ch, start, length);
            // System.out.println(strAnnotationName + ": " + strAnnotationContent);

            if (!nodeStack.empty())
            {
                TreeNode father = (TreeNode) nodeStack.peek();
                String fatherName = father.getName();
                if (fatherName.equals(ANN_APPINFO))
                {
                    if(inSimpleTypeFlag&&currSimpleTypeName!=null)
                    {
                        Map typeInfo=(Map) simpleTypeMap.get(currSimpleTypeName);
                        if(typeInfo!=null)
                        {
                            typeInfo.put(strAnnotationName, strAnnotationContent);
                        }
                    }
                }
            }
        }
        // End ADD
    }


    /**
     * <p>在每个标签结束后，进行相应的处理。
     * 主要时对当前数据节点的路径以及一些标识变量的处理。
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        nodeStack.pop();
        if(localName.equals("element"))
        {
            int pathIndex=currPath.lastIndexOf('/');
            if(pathIndex!=-1)
            {

                currPath=currPath.substring(0,pathIndex);
//				xpathFlag--;
            }
        }else if(localName.equals("attribute"))
        {
            int attIndex=currPath.indexOf('@');
            if(attIndex!=-1)
            {
                currPath=currPath.substring(0,attIndex-1);
            }
        }
        else if(localName.equals("complexType"))
        {
            typeFlag--;
            if(typeFlag<validType)
            {
                validType=999;
            }
        }
        else if(localName.equals("simpleType"))
        {
            inSimpleTypeFlag=false;
        }
        else if(localName.equals("group")||localName.equals("attributeGroup"))
        {
            String fathername=((TreeNode)nodeStack.peek()).getName();
            if(fathername.equals("schema"))
            {
                groupFlag=0;
            }

        }

        // For read annotaion in Schema. ADD by rla
        else if (localName.equals(ANN_ANNOTATION)||localName.equals(ANN_APPINFO)||localName.equals(ANN_DOCUMENTATION))
        {
            isAnnotation = 0;
            strAnnotationName = "";
        }
        // end ADD

    }


    /**
     * <p>schema文档的解析完成，已经将各种需要的信息存入了相应的容器，最后在此方法中
     * 将element和attribute完善：
     * <li>通过type属性，引用外部定义的complexType或simpleType的，将complexType中定义的子元素和
     * 属性添加到当前引用位置。将simpleType中的类型定义添加到当前引用元素或属性的Map中。
     * <li>通过ref属性引用外部定义的元素或节点的，将完整的外部元素插入到当前引用位置。
     * <li>通过ref应用group或attributeGroup时，将group或attributeGroup中的内容添加到当前引用位置。
     */
    public void endDocument() throws SAXException
    {
    	//System.out.println(allElementMap.toString());
        completeGroup();
        getSingleComplexTypeRefElement(allElementMap, branchElementMap);
       // System.out.println(allElementMap.toString());
        completeAllComplexTypeRef(branchElementMap, branchElementMap2);
        
        branchElementMap.clear();
        branchElementMap2.clear();
        getSingleRefElement(allElementMap, branchElementMap);
       // System.out.println(allElementMap.toString());
        completeAllElementRef(branchElementMap, branchElementMap2);
       // System.out.println(allElementMap.toString());
        completeSimpleTypeRef();
       // System.out.println(allElementMap.toString());
        tidyAllElementMap();
//		System.out.println("simpleTypeMap= "+simpleTypeMap);

    }


    /**
     * <p>对引用group或attributeGroup的，将将group或attributeGroup中的内容添加到当前引用位置。
     * 在allElementMap中添加group或attributeGroup中的内容（根据引用位置的路径+group或attributeGroup中
     * d的路径组成完整的路径）。
     */
    private void completeGroup()
    {
        Iterator iter=groupRefMap.keySet().iterator();
        Iterator setIter;
        String key;
        String position;

        while(iter.hasNext())
        {
            key=(String) iter.next();
            Set positions=(Set) groupRefMap.get(key);
            setIter=positions.iterator();
            while(setIter.hasNext())
            {
                position=(String) setIter.next();
                if(position.startsWith("inOutterComplexType_"))
                {
                    addGroupToElementMap(key,position,elementTypeMap);
                }else
                {
                    addGroupToElementMap(key,position,allElementMap);
                }
            }

        }
    }


    /**
     * <p>将名称为参数groupName的group中的元素添加到参数elements指定的Map中，参数
     * position包涵了添加的位置信息。
     * @param groupName group或attributeGroup的名称，通过<code>(Map) allGroupMap.get(groupName)</code>
     * 得到该名称指定的group的元素集合。
     * @param position 包含位置信息的字符串，为<code>groupRefMap</code>的值。参见groupRefMap的注释。
     * @param elements 用来存储group或attributeGroup中元素的Map。
     */
    private void addGroupToElementMap(String groupName,String position,Map elements)
    {
        Map element;
        if(position.startsWith("inOutterComplexType_"))
        {
            int index1=position.indexOf('_');
            int index2=position.lastIndexOf(':');
            if(index1!=-1&&index2!=-1)
            {
                String typeName=position.substring(index1+1,index2);
                String prefix=position.substring(index2+1);
                Map typeElements=(Map) elements.get(typeName);
                Map groupElements=(Map) allGroupMap.get(groupName);
                Iterator iter=groupElements.keySet().iterator();
                String xpath;
                while(iter.hasNext()){
                    xpath=(String) iter.next();
                    element=(Map)groupElements.get(xpath);
                    element.put("xpath", prefix+xpath);
                }
                typeElements.putAll(groupElements);
            }

        }else
        {
            Map groupElements = (Map) allGroupMap.get(groupName);
            if (groupElements != null) {
                Iterator iter = groupElements.keySet().iterator();
                String xpath;
                while (iter.hasNext()) {
                    xpath = (String) iter.next();
                    element = (Map) groupElements.get(xpath);
                    element.put("xpath", position + xpath);
                }
                elements.putAll(groupElements);
            }
        }
    }



    /**
     * <p>将参数mainMap中因引用外部的complexType而丢失的节点和属性添加到参数newBranchMap中。
     * @see this{@link #endDocument()}.
     * @param mainMap
     * @param newBranchMap
     */
    private void getSingleComplexTypeRefElement(Map mainMap, Map newBranchMap)
    {
        Map node,node2;
        String dataType;
        String xpath;
        String xpathTail;
        Map elementMap;
        Iterator iter = mainMap.values().iterator();
        while (iter.hasNext())
        {
            node = (Map) iter.next();
            dataType = (String) node.get("type");
            xpath = (String) node.get("xpath");
            if (elementTypeMap.get(dataType) != null)
            {
                elementMap = (Map) elementTypeMap.get(dataType);
                Iterator it = elementMap.keySet().iterator();
                while (it.hasNext())
                {
                    xpathTail=(String)it.next();
                    node2 = (Map)elementMap.get(xpathTail);
                    node2.put("xpath", xpath + xpathTail);
                    newBranchMap.put(xpath + xpathTail, node2);
                }
            }
        }
    }


    /**
     * <p>将参数branch1中因引用外部的complexType而丢失的节点和属性添加到参数branch2中，然后将
     * branch1全部添加到<code>allElementMap</code>中。然后将branch1清空，迭代调用此方法：
     * <code>completeAllComplexTypeRef(branch2, branch1)</code>。
     * 在本类中，第一次执行时branch1为调用<code>getSingleComplexTypeRefElement(allElementMap, Map newBranchMap) </code>
     * 后的newBranchMap。
     * <p>通过迭代，最终完成了引用外部的complexType的所有丢失元素和属性的补全。
     * @see this{@link #endDocument()}.
     * @param branch1
     * @param branch2
     */
    private void completeAllComplexTypeRef(Map branch1, Map branch2)
    {
        getSingleComplexTypeRefElement(branch1, branch2);
        allElementMap.putAll(branch1);
        if (branch2 == null || branch2.size() == 0)
        {
            return;
        }
        branch1.clear();
        getSingleComplexTypeRefElement(branch2, branch1);
        completeAllComplexTypeRef(branch2, branch1); //递归。

    }


    /**
     * <p>将参数mainMap中因采用ref引用而丢失的节点和属性添加到参数newBranchMap中。
     * 该方法逻辑如下：
     * 对于通过ref引用的元素，其type键值对应的是isRefElement: <code>ref</code>.取出
     * allElementMap中的键值以/ref开头的元素，添加到参数<code>newBranchMap</code>中。
     * @param mainMap
     * @param newBranchMap
     */
    private void getSingleRefElement(Map mainMap, Map newBranchMap)
    {
        Map elementMap;
        Iterator iter = mainMap.values().iterator();
        Map node;
        String dataType;
        String xpath;
        String name;
        Iterator iter2;
        String xpathTail;
        while (iter.hasNext())
        {
            node = (Map) iter.next();
            dataType =(String) node.get("type");
            xpath =(String) node.get("xpath");
            xpath=xpath.substring(0,xpath.lastIndexOf('/'));
//			System.out.println(xpath+"!!!!!!!!!!!!!!!!!");
            name=(String) node.get("name");
           
            if(dataType!=null&&dataType.startsWith("isRefElement:"))
            {
//				if(elementRefMap.get(name)==null)
//				{
                elementMap=getElementMapByRoot(name);
//				}else
//				{
//					elementMap=(Map) elementRefMap.get(name);
//				}
                iter2=elementMap.keySet().iterator();
                while(iter2.hasNext())
                {
                    xpathTail=(String) iter2.next();
                    node=(Map) elementMap.get(xpathTail);
                    node.put("xpath", xpath+xpathTail);
                    newBranchMap.put(xpath+xpathTail, node);

                }
            }
        }
    }


    /**
     * <p>通过迭代，补全因采用ref引用而丢失的元素和属性。
     * 逻辑与<code>completeAllComplexTypeRef</code>方法相似。
     * @see this{@link #completeAllComplexTypeRef(Map, Map)}
     * @see this{@link #endDocument()}
     * @param branch1
     * @param branch2
     */
    private void completeAllElementRef(Map branch1, Map branch2)
    {
        getSingleRefElement(branch1,branch2);
        allElementMap.putAll(branch1);
        if (branch2 == null || branch2.size() == 0)
        {
            return;
        }
        branch1.clear();
        getSingleRefElement(branch2,branch1);
        completeAllElementRef(branch2,branch1);

//		do{
//			getSingleRefElement(branch1,branch2);
//			allElementMap.putAll(branch1);
//
//		}while(branch2!=null&&branch2.size() == 0);

    }


    /**
     * <p>通过ref名称得到ref元素和其子元素及属性。
     * @see #getSingleRefElement(Map, Map).
     * @param rootName   ref名称
     * @return elementMap：xpath以 ref名称开头的所有节点。key：xpath； value:element.
     */
    private Map getElementMapByRoot(String rootName)
    {
        Map elementMap=new HashMap();
        Iterator iter=allElementMap.keySet().iterator();
        String xpath;
        while(iter.hasNext())
        {
            xpath=(String)iter.next();
            // 解决相同前缀元素名问题, Changed by renlian, 20080218
            // if(xpath.startsWith("/"+rootName)) // Original
            if (xpath.equals("/"+rootName) || xpath.startsWith("/"+rootName+"/"))
            {
                Map node=(Map)allElementMap.get(xpath);
                elementMap.put(xpath, node);
            }
        }
        elementRefMap.put(rootName, elementMap);
        return elementMap;
    }


    /**
     * <p>处理因引用外部定义的<b>simpleType</b>而丢失的类型信息。
     *@see this{@link #endDocument()}
     */
    private void completeSimpleTypeRef(){
        Iterator iter=allElementMap.values().iterator();
        Map element;
        String type;
        while(iter.hasNext()){
            element=(Map) iter.next();
            type=(String) element.get("type");
            if(type!=null&&simpleTypeMap.get(type)!=null)
            {
                element.putAll((Map) simpleTypeMap.get(type));
            }
        }
    }


    /**
     * <p>清理掉<code>allElementMap</code>中用作引用的元素。
     * 当<code>allElementMap</code>中的键值与其对应的元素的xpath值不相等时，将键值
     * 改为元素的xpath值。
     *@see this{@link #endDocument()}
     */
    private void tidyAllElementMap(){
        Iterator iter=elementRefMap.values().iterator();
        Map elements = new HashMap();
        while(iter.hasNext())
        {
            elements=(Map) iter.next();
            allElementMap.keySet().removeAll(elements.keySet());
        }
//       // System.out.println(allElementMap.toString());
        allElementMap.putAll(simpleTypeMap);
        elements.clear();
        Map element;
        Set removeSet =new HashSet();
        String key;
        Iterator iter2=allElementMap.keySet().iterator();
        while(iter2.hasNext())
        {
            key=(String) iter2.next();
            element=(Map) allElementMap.get(key);
            String xpath=(String) element.get("xpath");
            String type=(String) element.get("type");
            if(type!=null &&!"".equalsIgnoreCase(type)&&type.startsWith("isRefElement:"))
            {
//            	 System.out.println(element.toString());
            	
            	/**由于程序会重复的解析一些节点，到这一步骤的时候程序的节点会重负，里
            	 * 面会包含多余的未经填充的节点信息，在进行最后一次将键值不匹配的节点进行匹配的时候
            	 * 产生了重复建，所以后者把前者覆盖导致信息丢失 
            	 * 针对此bug增加这个分支将不完全信息过滤掉
            	 * 
            	 */            }
            else if(xpath!=null&&!xpath.equals(key))
            {
                elements.put(xpath, element);
                removeSet.add(key);
            }
        }
        allElementMap.putAll(elements);
//		allElementMap.keySet().removeAll(removeSet);
    }


    /**
     * <p>公共方法，得到解析的结果。
     * @return Map <code>allElementMap</code>
     */
    public Map getAllElementMap()
    {
//    	System.out.println(allElementMap.get("/Certificates/Certificate/Product/Package/PackageAmount"));
//        System.out.println(allElementMap.get("/Certificates/Certificate/Product/Weight/WeightAmount"));
    	return allElementMap;
        
    }


    /**
     * <p>用来表示解析schema文件时的元素节点。
     *@see SchemaTreeBuilder#nodeStack.
     */
    class TreeNode
    {

        private String name;
        private String att_name;

        public String getAtt_name() {
            return att_name;
        }

        public void setAtt_name(String att_name) {
            this.att_name = att_name;
        }

        public TreeNode(String name)
        {
            this.name=name;
        }

        public String getName()
        {
            return name;
        }
    }
}
