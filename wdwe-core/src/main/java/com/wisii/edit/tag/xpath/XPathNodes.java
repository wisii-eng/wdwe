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

import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.functions.Function;
import org.apache.xpath.operations.Equals;
import org.apache.xpath.operations.Gt;
import org.apache.xpath.operations.Gte;
import org.apache.xpath.operations.Lt;
import org.apache.xpath.operations.Lte;


/**
 * 存放XPath节点内容的类
 * @author 闫舒寰
 * @version 1.0 2009/06/30
 */
public class XPathNodes {
	
	private ExpressionOwner expressionOwner; 
	
	private Function function;

	private String node;
	
	private String number;
	
	private String attribute;
	
	private String value;
	
	private Expression expression;
	
	private String children;

	public String getNode() {
		return node;
	}

	public void setNode(final String node) {
		this.node = node;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(final String number) {
		this.number = number;
	}
	
	public ExpressionOwner getExpressionOwner() {
		return expressionOwner;
	}

	public void setExpressionOwner(final ExpressionOwner expressionOwner) {
		this.expressionOwner = expressionOwner;
	}
	
	public Function getFunction() {
		return function;
	}

	public void setFunction(final Function function) {
		this.function = function;
	}
	
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(final String attribute) {
		this.attribute = attribute;
	}
	
	public Expression getExpression() {
		return expression;
	}

	public void setExpression(final Expression expression) {
		this.expression = expression;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
	
	public String getChildren() {
		return children;
	}

	public void setChildren(final String children) {
		this.children = children;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		if (this.getNode() != null) {
			//正常节点
			
			//不带数字带表达式的节点
			if (this.getExpression() != null) {
				//当带[]的时候
				//当有复杂的function的时候
				String fun = getExpressionFun();
				if (this.getAttribute() != null) {
					if (this.getAttribute().equals("*")) {
						sb.append(this.getNode() + "[" + "@" + this.getAttribute() + "]");
					} else if (fun != null) {
						sb.append(this.getNode() + "[" + "@" + this.getAttribute() + fun 
								+ "'" + this.getValue() + "'" + "]");
					} else {
						System.err.println("we do not finish all expression transformation");
					}
				} else {
					//这个情况是当有[]并且属性为空的时候
					if (this.getChildren() != null) {
						sb.append(this.getNode() + "[" + this.getChildren() + fun 
								+ "'" + this.getValue() + "'" + "]");
					}
					
					//TODO 这里有点冗余，还要整理一下逻辑，但是情况是，当前的expression是XNumber的时候
					if (this.getNumber() != null) {
						//带数字的节点
						sb.append(this.getNode() + "[" + this.getNumber() + "]");
					} else {
						//光秃秃只有节点的时候
						sb.append(this.getNode());
					}
					
				}
			} else if (this.getFunction() != null) {
				//TODO 当比较值是个function的时候
				sb.append(this.getNode() + "[" + "**someFunction**" + "]");
//				System.err.println("we do not support function in xpath currently");
			} else {
				//当不带expression的时候
				if (this.getNumber() != null) {
					//带数字的节点
					sb.append(this.getNode() + "[" + this.getNumber() + "]");
				} else {
					//光秃秃只有节点的时候
					sb.append(this.getNode());
				}
			}
			
		} else {
			//属性节点
			if (this.getAttribute() != null) {
				sb.append('@');
				sb.append(this.getAttribute());
			}
		}
		
//		String temp = sb.toString();
//		System.out.println(getNumber());
//		System.out.println(temp);
		return sb.toString();
	}
	
	
	private String getExpressionFun(){
		
		String fun = null;
		
		if (this.getExpression() instanceof Equals) {
			fun = "=";
		}
		
		if (this.getExpression() instanceof Gt) {
			fun = ">";
		}
		
		if (this.getExpression() instanceof Lt) {
			fun = "<";
		}
		
		if (this.getExpression() instanceof Lte) {
			fun = "<=";
		}
		
		if (this.getExpression() instanceof Gte) {
			fun = ">=";
		}
		
		return fun;
	}

}
