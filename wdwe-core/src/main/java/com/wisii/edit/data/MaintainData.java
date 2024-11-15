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
 */package com.wisii.edit.data;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.EditStatusControl;

/**
 * 该类为写内存的接口。用于写如xml数据
 * 
 * @author liuxiao
 * 
 */
public class MaintainData {
	private static Document DOC;
	private static XPath XPATH;

	/**
	 * 做节点的更新操作,假设这个xpath所对应的节点唯一，如出现多个，则会在数据上有问题
	 * 
	 * @param shortName
	 *            本次操作的名称
	 * @param xpath
	 *            本次操作要修改的xpath
	 * @param data
	 *            本次操作的数据
	 * @para isUndo 是否进行撤销操作 true
	 *       进行撤销的记录，false，不进行撤销的记录，除了撤销的时候这个参数为false外，其他外部引用时均为ture
	 * @return 是否更新成功
	 * @throws Exception
	 * 
	 * 
	 */
	public static boolean update(String shortName, String xpath, String data,
			boolean isUndo) throws Exception {
		if (DOC == null) {
			return false;
		}
		XPathExpression expr = XPATH.compile(xpath);
		Object result = expr.evaluate(DOC, XPathConstants.NODE);
		Node node = (Node) result;
		if (node == null) {
			return false;
		}
		if (data == null) {
			data = "";
		}
		node.setTextContent(data);
		// // 查询这个xpath的原有值
		// BaseXUtil bu = new BaseXUtil(BaseXDB.XML_DATABASE_NAME);
		//
		// String[] ssa = bu.queryValue(xpath);
		//
		// String ss = null;
		// if (ssa != null && ssa.length > 0)
		// ss = ssa[0];
		// if (isUndo) {
		// // 建立一个shortName
		// if (shortName == null || "".equals(shortName))
		// shortName = "更新数据:" + data;
		//
		// try {
		// UnDoUtil.recordUndo(UndoSupportBo.OPER_UPDATE, xpath,
		// shortName, ss);
		// } catch (SQLException e) {
		// StatusbarMessageHelper.output(shortName + "记录未成功，该步不能撤销",
		// e.getMessage(), StatusbarMessageHelper.LEVEL.INFO);
		// }
		// }
		// // 更改xml
		// if (data == null) {
		// data = "";
		// }
		// // else
		// bu.updateByXPath(xpath, data);
		// bu.close();
		// 记录到更新列表 ,由于此功能较复杂，暂不实现
		// TODO
		// UpdateRecordBo upb=new UpdateRecordBo();
		// upb.setXpath(xpath);
		// upb.setContent(content)
		EditStatusControl.updataData();
		return true;

	}

	public static boolean delete(String xpath) {
		if (DOC == null) {
			return false;
		}
		XPathExpression expr;
		try {
			expr = XPATH.compile(xpath);
			Object result = expr.evaluate(DOC, XPathConstants.NODE);
			Node node = (Node) result;
			if (node == null) {
				return false;
			}
			node.getParentNode().removeChild(node);
			EditStatusControl.updataData();
			return true;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return false;
		}

		// try {
		// BaseXUtil bu = new BaseXUtil(BaseXDB.XML_DATABASE_NAME);
		// bu.delete(xpath);
		// bu.close();
		// return true;
		// } catch (Exception e) {
		// StatusbarMessageHelper.output("删除节点失败", e.getMessage(),
		// StatusbarMessageHelper.LEVEL.INFO);
		// return false;
		// }
	}

//	public static boolean addBefore(String xpath) {
//		if (DOC == null) {
//			return false;
//		}
//		XPathExpression expr;
//		try {
//			expr = XPATH.compile(xpath);
//			Object result = expr.evaluate(DOC, XPathConstants.NODE);
//			Node node = (Node) result;
//			if (node == null) {
//				return false;
//			}
//			Node newnode=node.cloneNode(true);
//			node.getParentNode().insertBefore(newnode, node);
//			EditStatusControl.updataData();
//			return true;
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			return false;
//		}
//		try {
//			bu.insertElementBeforePath(xpath, xml);
//			EditStatusControl.updataData();
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			return false;
//		}
//		return true;

//	}

	public static boolean addBefore(String xpath,String nodataxPaths) {
		if (DOC == null) {
			return false;
		}
		XPathExpression expr;
		try {
			expr = XPATH.compile(xpath);
			Object result = expr.evaluate(DOC, XPathConstants.NODE);
			Node node = (Node) result;
			if (node == null) {
				return false;
			}
			Node newnode = node.cloneNode(true);
			XPathExpression nodataxpath;
			Node nodatanode = null;
			List<Node> noList = new ArrayList<Node>();
			if (nodataxPaths != null && !nodataxPaths.isEmpty()) {
				String[] nodatas = nodataxPaths.split(",");
				for (int i = 0; i < nodatas.length; i++) {
					nodataxpath = XPATH.compile(nodatas[i]);
					Object res = nodataxpath.evaluate(DOC, XPathConstants.NODE);
					nodatanode = (Node) res;
					noList.add(nodatanode);
				}
			}
			Node next = node.getNextSibling();
			if (next == null) {
				node.getParentNode().appendChild(newnode);
			} else {
				NodeList childNodes = newnode.getChildNodes();
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node item = childNodes.item(i);
					boolean flag = false;
					String nodeName = item.getNodeName();
					for (Node node2 : noList) {
						String nodeName2 = node2.getNodeName();
						if (nodeName.endsWith(nodeName2)) {
							flag = true;
							continue;
						}
					}
					if (flag) {
						item.setTextContent("");
					}
				}
				node.getParentNode().insertBefore(newnode, node);
			}
			EditStatusControl.updataData();
			return true;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return false;
		}
		// try {
		// bu.insertElementAfterPath(xpath, xml);
		// EditStatusControl.updataData();
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// return false;
		// }
		// return true;
		
	}
	public static boolean addAfter(String xpath,String nodataxPaths) {
		if (DOC == null) {
			return false;
		}
		XPathExpression expr;
		try {
			expr = XPATH.compile(xpath);
			Object result = expr.evaluate(DOC, XPathConstants.NODE);
			Node node = (Node) result;
			if (node == null) {
				return false;
			}
			Node newnode = node.cloneNode(true);
			XPathExpression nodataxpath;
			Node nodatanode = null;
			List<Node> noList = new ArrayList<Node>();
			if (nodataxPaths != null && !nodataxPaths.isEmpty()) {
				String[] nodatas = nodataxPaths.split(",");
				for (int i = 0; i < nodatas.length; i++) {
					nodataxpath = XPATH.compile(nodatas[i]);
					Object res = nodataxpath.evaluate(DOC, XPathConstants.NODE);
					nodatanode = (Node) res;
					noList.add(nodatanode);
				}
			}
			Node next = node.getNextSibling();
			if (next == null) {
				node.getParentNode().appendChild(newnode);
			} else {

				NodeList childNodes = newnode.getChildNodes();
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node item = childNodes.item(i);
					boolean flag = false;
					String nodeName = item.getNodeName();
					for (Node node2 : noList) {
						String nodeName2 = node2.getNodeName();
						if (nodeName.endsWith(nodeName2)) {
							flag = true;
							continue;
						}
					}
					if (flag) {
						item.setTextContent("");
					}
				}
				node.getParentNode().insertBefore(newnode, next);
			}
			EditStatusControl.updataData();
			return true;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return false;
		}
		// try {
		// bu.insertElementAfterPath(xpath, xml);
		// EditStatusControl.updataData();
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// return false;
		// }
		// return true;

	}

	// add by px
	/**
	 * 查找值,如果得到了多个节点则会返回一个String数组，如果节点内有内容而又子节点则会返回null
	 * 
	 * @param xpath
	 *            节点xpath
	 */
	public static String[] queryValue(String xpath) {
		String[] ab=null;
		if (DOC == null) {
			return ab;
		}
		XPathExpression expr;
		try {
			expr = XPATH.compile(xpath);
			Object result = expr.evaluate(DOC, XPathConstants.NODESET);
			NodeList nodelist = (NodeList) result;
			if (nodelist == null) {
				return ab;
			}
		    int len=nodelist.getLength();
		    ab=new String[len];
		    for(int i=0;i<len;i++)
		    {
		    	ab[i]=nodelist.item(i).getTextContent();
		    }
			return ab;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return ab;
		}
//		String[] ab = null;
//		try {
//			BaseXUtil bu = new BaseXUtil(BaseXDB.XML_DATABASE_NAME);
//			ab = bu.queryValue(xpath);
//			bu.close();
//		} catch (Exception e) {
//			StatusbarMessageHelper.output("查找节点失败" + xpath, e.getMessage(),
//					StatusbarMessageHelper.LEVEL.INFO);
//		}
//		return ab;
	}

	public static String Xquery(String query) throws Exception {
		if(DOC==null)
		{
			return "";
		}
        String xmlStr = "";  
        org.dom4j.io.DOMReader xmlReader = new org.dom4j.io.DOMReader();
        org.dom4j.Document dom4j = xmlReader.read(DOC);
        xmlStr= dom4j.asXML();
        return xmlStr;  
//		BaseXUtil bu = new BaseXUtil(BaseXDB.XML_DATABASE_NAME);
//		String xml = bu.Xquery(query);
//		bu.close();
//		return xml;
	}

	public static void setDocumentbyString(String xml) {
		if (xml == null || xml.isEmpty()) {
			DOC = null;
			XPATH = null;
			return;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			ByteArrayInputStream in = new ByteArrayInputStream(
					xml.getBytes(SystemUtil.FILE_CHARSET));
			DOC = builder.parse(in);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (XPATH == null) {
			XPATH = XPathFactory.newInstance().newXPath();
		}

	}
	public static void stop()
	{
		DOC=null;
		XPATH=null;
	}
}
