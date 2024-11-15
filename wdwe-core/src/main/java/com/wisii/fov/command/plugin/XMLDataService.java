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
 *//**
 *    XMLDataService.java
 *    version 1.0
 *    汇智互联
 */

package com.wisii.fov.command.plugin;
import java.util.*;
import com.wisii.fov.util.XMLHelper;
/**
 * 这个类根据源xml数据文件，将客户端返回的数据组装成xml文件。
 */
public class XMLDataService
{
    public static final int TYPE_REFRESH = 1;                                   //常量，表示操作类型为更新。
    public static final int TYPE_ADD = 2;                                       //常量，表示操作类型为增加节点。
    public static final int TYPE_DELETE = 3;                                    //常量，表示操作类型为删除节点。
    protected XMLHelper _xmlHelper = new XMLHelper();                           //处理xml数据的XMLHelper对象。

    /**
     * 通过工具xmlHelper将xml数据更新,查询,增加/删除子节点。
     * 参数 xmlc    : 原xml数据内容的字符串表示.
     * 参数 dataMap : 包含处理信息的哈希表.
     * 返回值       :  返回处理完后xml文档内容的字符串表示.
     */
    public String refreshXMLDoc(String xmlc,Map dataMap)
    {
        String xmls = xmlc;                                                     //局部变量,用于返回最后处理后的结果.
        if(xmlc != null && !"".equals(xmlc) && dataMap != null && !dataMap.isEmpty())//判断哈希表是否为空或者xmlc是否为null或空字符.
        {
            try
            {
               if( _xmlHelper.load(xmlc))                                       //把此xmlc初始化成一个document文档对象.
               {
                   String type = (String)dataMap.get("editType");               //返回哈希表中由editType键标识的处理情况.
                   if(type != null)
                   {
                       boolean bool = false;                                    //标识操作是否成功.
                       int temp = Integer.parseInt(type);                       //返回对dataMap的操作类型。
                       switch(temp)
                       {
                           case TYPE_REFRESH: //值为1时，为更新。
                               ArrayList array = this.getKeys(dataMap);         //创建一个保存dataMap中所有键的数组(editType键除外).
                               for(int i = 0; i < array.size(); i++)
                               {
                                   String xpath = (String)array.get(i);         //返回数组中第i个位置的值（路径表达式）。
                                   String value = (String)dataMap.get(xpath);   //返回由xpath键对应的值.
                                   if(xpath != null && xpath.indexOf("@") > -1) //判断路径中是否包含"@"字符,包含表示是修改属性.
                                   {
                                       String tempString = xpath.substring(xpath.indexOf("@"),xpath.length());//返回xpath字符中"@"字符后的字符.
                                       if(tempString.indexOf("/") > -1) //判断xpath的"@"字符后的字符中是否含有"/",有就表示还是修改内容.没有才是修改属性.
                                       {
                                           bool = _xmlHelper.refreshElementText(xpath, value); //更新xpath指定节点的内容并返回处理是否成功的布尔值.
                                       }
                                       else
                                           bool = _xmlHelper.refreshAttributeValue(xpath, value); //更新xpath指定节点的属性并返回处理是否成功的布尔值.
                                   }
                                   else
                                   {
                                       bool = _xmlHelper.refreshElementText(xpath, value); //更新xpath指定节点的内容并返回处理是否成功的布尔值.
                                   }
                               }
                               break;

                           case TYPE_ADD: //值为2时，为增加。
                               String aXpath = (String)dataMap.get("xpath");    //返回"xpath"键对应的值(即指定的路径).
                               if(aXpath != null || !"".equals(aXpath))
                               {
                                   bool = _xmlHelper.addNode(aXpath);           //在指定位置增加一个字节点并返回处理是否成功的布尔值.
                               }
                               break;

                           case TYPE_DELETE: //值为3时，为删除。
                               String bXpath = (String)dataMap.get("xpath");    //返回"xpath"键对应的值(即指定的路径).
                               if(bXpath != null || !"".equals(bXpath))
                               {
                                   bool = _xmlHelper.deleteNode(bXpath);        //删除指定的节点并返回处理是否成功的布尔值.
                               }
                               break;
                       }
                       if(bool)
                       {
                           xmls = _xmlHelper.getStringDocument();               //返回处理后xml文件内容的字符串表示.
                       }
                   }
               }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return xmls;
    }

    /**
     * 返回给定XML文件中,指定节点的文本内容.
     * 参数 xmlc  : 给定的XML文档内容的字符串表示.
     * 参数 xpath : 指定节点的路径表达式.
     * 返回值     : 返回由xpath指定节点的文本内容.
     */
    public String getNodeText(String xmlc,String xpath)
    {
        String temp = null;                                                     //调用此方法最后返回的字符串内容。
        if(xmlc != null && xpath != null && !"".equals(xpath))
        {
            try
            {
                if(_xmlHelper.load(xmlc))                                       //初始化XML文档并验证是否初始化成功.
                {
                    org.dom4j.Node node = _xmlHelper.selectSingleNode(xpath);   //返回给定XML文件中,由xpath指定节点的文本内容.
                    if(node != null)
                    {
                        temp = node.getText();                                  //返回指定节点的文本内容.
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return temp;
    }

    /**
     * 私有方法，返回一个保存指定哈希表中所有键的数组。
     * 参数 map ：为要处理的哈希表。
     * 返回值    : 返回存放所有键的数组("editType"键除外).
     */
    private ArrayList getKeys(Map map)
    {
        ArrayList list = new ArrayList();                                       //初始化一个ArrayList数组，用于保存哈希表中所有的键。
        Iterator iter = map.keySet().iterator();                                //返回哈希表map中所有键的集合.迭代此集合.
        while(iter.hasNext())                                                   //遍厉集合.
        {
            String key = (String)iter.next();
            if(!key.equals("editType") && key.startsWith("/"))
            {
                list.add(key);                                                  //把符合条件的健存入数组.
            }
        }
        return list;
    }
}
