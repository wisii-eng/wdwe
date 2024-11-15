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
 */package com.wisii.fov.render.java2d;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wisii.component.startUp.SystemUtil;

/**
 * recode the imange data as xml form with XmlImageObj .
 *
 * creator:liuxiao date: 20080102
 */
public class XmlImageObj
{

	/**
	 * @param args
	 */
	/** 函数名 */
	private String functionName = null;

	/** XML表示的image数据类型 */
	private String dataType = null;

	/** 透明度 */
	private String alpha = null;

	/** 图片名称 */
	private String name = null;

	/** XML */
	private String str = null;

	/** 返回值类型 */
	private String returnType = null;

	/** 参数map */
	private Map para = null;

	private double high = 0;

	private double wide = 0;

	public String getAlpha()
	{
		return alpha;
	}

	public void setAlpha(String alpha)
	{
		this.alpha = alpha;
	}

	public String getDataType()
	{
		return dataType;
	}

	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}

	public String getFunctionName()
	{
		return functionName;
	}

	public void setFunctionName(String functionName)
	{
		this.functionName = functionName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map getPara()
	{
		return para;
	}

	public void setPara(Map para)
	{
		this.para = para;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}

	public void setStr(String str)
	{

		this.str = str;
	}

	public boolean tramsformImageXml()
	{
		if (name == null)
		{
			System.err.println("XmlImangeObj对象没有要寻找的图片标识");
			return false;
		}
		if (str == null)
		{
			System.err.println("XmlImangeObj对象的XML来源为null");
			return false;
		}
		SAXReader saxReader = new SAXReader();
		try
		{
			SAXReader reader = new SAXReader();
			Document document;
			try
			{

				document = (Document) reader.read(new java.io.StringReader(
						SystemUtil.getURLDecoderdecode(str)));
			}
			catch (DocumentException de)
			{
				de.printStackTrace();
				return false;
			}
			List list = document.selectNodes("/images/image[@name='" + name
					+ "']");
			if (list == null || list.size() < 1)
			{
				System.err.println("请检查imgxml文件格式是否正确，name是否存在");
				return false;
			}
			Iterator it = list.iterator();

			// 得到相关的image节点
			Element element = (Element) it.next();
			// 得到属性
			try
			{
				setDataType(element.attribute("dataType").getValue());
			}
			catch (Exception e)
			{
				System.err.println("文件没有dataType节点，请检查");
				return false;
			}
			if (dataType != null && !dataType.equalsIgnoreCase("func"))
			{
				return true;
			}
			try
			{
				setAlpha(element.attribute("alpha").getValue());
			}
			catch (Exception e)
			{
				System.out.println("文件没有alpha节点");
			}
			try
			{
				setHigh(element.attribute("high").getValue());
			}
			catch (Exception e)
			{
				System.out.println("文件没有high节点");
			}
			try
			{
				setWide(element.attribute("wide").getValue());
			}
			catch (Exception e)
			{
				System.out.println("文件没有Wide节点");
			}
			// 得到子节点值
			try
			{
				setReturnType ( element.selectSingleNode("returnType").getText());
			}
			catch (Exception e)
			{
				System.out.println("文件没有returnType节点");
			}
			try
			{
				setFunctionName (element.selectSingleNode("functionName ")
						.getText());
			}
			catch (Exception e)
			{
				System.err.println("文件没有functionName节点，请检查");
				return false;
			}
			if (functionName == null || "".equalsIgnoreCase(functionName))
			{
				System.err.println("XmlImangeObj传入的函数名为空请检查");
				return false;
			}

			functionName = functionName.trim();
			// 解析参数
			List listP = document.selectNodes("/images/image[@name='" + name
					+ "']//parameter");
			if (listP == null || listP.size() < 1)
			{
				System.out.println("没有参数");
				return true;
			}
			Iterator itP = listP.iterator();
			while (itP.hasNext())
			{

				Element elementP = (Element) itP.next();
				if (para == null)
					para = new HashMap();

				para.put(elementP.attribute("objName").getValue(), elementP
						.getTextTrim());

			}

		}
		catch (Exception e)
		{

			System.out.println("解析图片XML出错");
			e.printStackTrace();
			return false;

		}

		return true;
	}

	public Object execute() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException
	{
		String allname = this.functionName;
		String classname = allname.substring(0, allname.lastIndexOf("."));
		String methodname = allname.substring((allname.lastIndexOf(".") + 1),
				allname.length());
		Object all=null;

		Object rePara[] = new Object[1];
		rePara[0] = para;

		Class ownerClass = Class.forName(classname);

		Object remotObj = (Object) ownerClass.newInstance();

		Class[] argsClass = new Class[1];

		argsClass[0] = rePara[0].getClass();
		try{
		Method method = ownerClass.getMethod(methodname, argsClass);
		all= method.invoke(remotObj, rePara);

		}
		catch(Exception e)
		{
			System.out.println("未能找到这个以这个接口为参数的方法"+argsClass[0]);
			Class[] moreClass =   rePara[0].getClass().getInterfaces();
			for(int i=0;i<moreClass.length;i++)
			{
				try{
					Class[] sigClass = new Class[1];
					sigClass[0]=moreClass[i];
				Method method = ownerClass.getMethod(methodname, sigClass);
				all= method.invoke(remotObj, rePara);
				}
				catch(Exception ec)
				{
					System.out.println("未能找到这个以这个接口为参数的方法"+moreClass[i]);
					continue;
				}
				break;
			}


		}


		return all;

	}

	public Object testOut(HashMap ccpar)
	{

		// 读图片
		return "kao"; // 直接返回图片的流
	}

	public static void main(String[] args)
	{

//		   System.out.println("舍掉小数取整:Math.floor(2.9)=" + (int)Math.floor(2.5));

		 XmlImageObj to = new XmlImageObj();
		 to.setHigh("");
		 System.out.println(to.getHigh());

		 to.setName("test2");
		 to
		 .setStr("<images>"
		 + "<image dataType=\"func\" alpha =\"30\" name=\"test1\">"
		 + "<functionName>com.wisii.xxxxxxx.imagnfunction</functionName>"
		 + "<returnType>Stream</returnType >"
		 + "<parameter objType=\"String\" objName=\"p1\">v1</parameter>"
		 + "<parameter objType =\"String\" objName =\"p2\" >v2</parameter>"
		 + "<parameter objType =\"String\" objName =\"p3\" >v3</parameter>"
		  + "</image>"
		 + "<image dataType=\"func\" alpha =\"30\" high =\"\" wide =\"\" name=\"test2\">"
		 + "<functionName>com.XmlImageObj.testOut</functionName>"
		 + "<returnType>Stream</returnType>"
		 + "<parameter objType=\"String\" objName=\"p1\">v21</parameter>"
		 + "<parameter objType =\"String\" objName =\"p2\" >v22</parameter>"
		 + "<parameter objType =\"String\" objName =\"p3\" >v23</parameter>"
		 + "</image>"
		 + "<image dataType=\"func\" alpha =\"50\" name=\"test3\">"
		 + "<functionName >com.wisii.xxxxxxx.imagnfunction</functionName>"
		 + "<returnType>Stream</returnType >"
		 + "<parameter objType=\"String\" objName=\"p1\">v31</parameter>"
		 + "<parameter objType =\"String\" objName =\"p2\" >v32</parameter>"
		 + "<parameter objType =\"String\" objName =\"p3\" >v33</parameter>"
		 + "</image>" + "</images>");

		 if(to.tramsformImageXml()){
		 System.out.println(to.getAlpha());
		 System.out.println(to.getDataType());
		 System.out.println(to.getFunctionName());
		 System.out.println(to.getName());
		 System.out.println(to.getPara().toString());
		 System.out.println(to.getReturnType());
		 }
		 try
		 {
		 System.out.println(to.execute().toString());
		 System.out.println(to.getReturnType());
		 }
		 catch (SecurityException e)
		 {
		 // TODO 自动生成 catch 块
		 e.printStackTrace();
		 }
		 catch (IllegalArgumentException e)
		 {
		 // TODO 自动生成 catch 块
		 e.printStackTrace();
		 }
		 catch (InstantiationException e)
		 {
		 // TODO 自动生成 catch 块
		 e.printStackTrace();
		 }
		 catch (IllegalAccessException e)
		 {
		 // TODO 自动生成 catch 块
		 e.printStackTrace();
		 }
		 catch (ClassNotFoundException e)
		 {
		 // TODO 自动生成 catch 块
		 e.printStackTrace();
		 }
		 catch (NoSuchMethodException e)
		 {
		 // TODO 自动生成 catch 块
		 e.printStackTrace();
		 }
		 catch (InvocationTargetException e)
		 {
		 // TODO 自动生成 catch 块
		 e.printStackTrace();
		 }

	}

	public double getWide()
	{
		return wide;
	}

	public double getHigh()
	{
		return high;
	}

	public void setHigh(String high)
	{
		if (high == null || "".equalsIgnoreCase(high))
			high = "0";
		double h=0;
		try{

			h= Double.parseDouble(high);


		}
		catch(NumberFormatException e)
		{

		}
		 this.high=h;

	}

	public void setWide(String wide)
	{
		if (wide == null || "".equalsIgnoreCase(wide))
			wide = "0";
		double h=0;
		try{

			h= Double.parseDouble(wide);


		}
		catch(NumberFormatException e)
		{

		}
		 this.wide=h;
	}

}
