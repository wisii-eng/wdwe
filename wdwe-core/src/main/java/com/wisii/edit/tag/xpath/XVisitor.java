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
 */package com.wisii.edit.tag.xpath;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.impl.xpath.XPath.Axis;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.AttributeIterator;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.axes.ChildTestIterator;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.patterns.NodeTest;

/**
 * XPath的Visitor
 * @author 闫舒寰
 * @version 1.0 2009/06/30
 */
public class XVisitor extends XPathVisitor {
	
	private static List<XPathNodes> xpathNodeList = new ArrayList<XPathNodes>();
	
	@Override
	public boolean visitNumberLiteral(final ExpressionOwner owner, final XNumber num) {
//		System.out.println(num);
		XPathNodes xpn = xpathNodeList.get(xpathNodeList.size() - 1);
		xpn.setNumber(num.toString());
		return super.visitNumberLiteral(owner, num);
		
	}
	
	@Override
	public boolean visitFunction(final ExpressionOwner owner, final Function func) {
		
		XPathNodes xpn = xpathNodeList.get(xpathNodeList.size() - 1);
		
		xpn.setFunction(func);
		
//		System.out.println("=======function===========" + func);
		return super.visitFunction(owner, func);
	}

	@Override
	public boolean visitPredicate(final ExpressionOwner owner, final Expression pred) {
		
		XPathNodes xpn = xpathNodeList.get(xpathNodeList.size() - 1);
		
//		System.err.println(pred.getClass());
		
		if (pred instanceof Operation) {
			Operation op = (Operation) pred;
			
//			System.out.println("left: " + op.getLeftOperand().getClass() + 
//					" option: " + op.getClass() + " right: "+ op.getRightOperand().getClass());
			
			if (op.getLeftOperand() instanceof ChildTestIterator) {
				ChildTestIterator ct = (ChildTestIterator) op.getLeftOperand();
				xpn.setChildren(ct.getLocalName());
			}
			
			if (op.getLeftOperand() instanceof AttributeIterator) {
				AttributeIterator ai = (AttributeIterator) op.getLeftOperand();
//				System.out.println(ai.getLocalName());
				xpn.setAttribute(ai.getLocalName());
			}
			
			if (op.getRightOperand() instanceof XObject) {
				XObject xs = (XObject) op.getRightOperand();
				xpn.setValue(xs.toString());
			}
			xpn.setExpression(pred);
		}
		
		if (pred instanceof AttributeIterator) {
			AttributeIterator ai = (AttributeIterator) pred;
			xpn.setAttribute(ai.getLocalName());
			xpn.setExpression(pred);
		}
		
		if (pred instanceof XNumber) {
			xpn.setNumber(pred.toString());
			xpn.setExpression(pred);
		}
		
		
		return super.visitPredicate(owner, pred);
	}

	@Override
	public boolean visitStep(final ExpressionOwner owner, final NodeTest step) {
		
//		System.out.println(step);
		
//		System.out.println(owner.getClass());
		
		if (step instanceof AxesWalker) {
			AxesWalker aw = (AxesWalker) step;
			
			XPathNodes xpn = new XPathNodes();
			xpn.setExpressionOwner(owner);
			
			if (Axis.SELF == aw.getAxis()) {
				xpn.setNode(step.getLocalName());
			}
			
			if (Axis.ATTRIBUTE == aw.getAxis()) {
				xpn.setAttribute(step.getLocalName());
			}
			
//			System.out.println("=====" + step.getLocalName() + " : " + aw.getAxis());
			
			xpathNodeList.add(xpn);
		}
		
		if (step instanceof AttributeIterator) {
//			AttributeIterator ai = (AttributeIterator) step;
			XPathNodes xpn = xpathNodeList.get(xpathNodeList.size() - 1);
			xpn.setAttribute(step.getLocalName());
//			System.out.println("************" + step.getLocalName());
		}
		
		return super.visitStep(owner, step);
	}
	
	/**
	 * 获得解析过的xpath列表
	 * @return xpath列表
	 */
	public List<XPathNodes> getParsedXPathNodeList(){
		List<XPathNodes> temp = new ArrayList<XPathNodes>(xpathNodeList);
		xpathNodeList.clear();
		
		if (temp.get(0).getNode() == null || temp.get(0).getNode().equals("")) {
			temp.remove(0);
		}
		
		return temp;
	}

}
