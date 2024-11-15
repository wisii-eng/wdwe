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
 */package com.wisii.edit.tag.correlation.formula;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import net.sourceforge.jeval.EvaluationException;
//import net.sourceforge.jeval.Evaluator;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.data.MaintainData;
public class CommonFunction implements Function {

	public String compute(String formulaName, List<CorrelationPara> paras,WdemsComponent wc) {
		if(formulaName.contains("getYear("))
		{
			Date date=new Date(Long.parseLong(paras.get(0).getValue().get(0)));
			return ""+(date.getYear()+1900);
			
		}
		else if(formulaName.contains("getMonth(")){
			Date date=new Date(Long.parseLong(paras.get(0).getValue().get(0)));
			return ""+(date.getMonth()+1);
		}
		else if(formulaName.contains("getDate("))
		{
			Date date=new Date(Long.parseLong(paras.get(0).getValue().get(0)));
			return ""+date.getDate();
		}
		else if(formulaName.contains("getDay("))
		{
			Date date=new Date(Long.parseLong(paras.get(0).getValue().get(0)));
			return ""+date.getDay();
		}
		else if(formulaName.contains("getHours("))
		{
			Date date=new Date(Long.parseLong(paras.get(0).getValue().get(0)));
			return ""+(date.getHours()+1);
		}
		else if(formulaName.contains("getMinutes("))
		{
			Date date=new Date(Long.parseLong(paras.get(0).getValue().get(0)));
			return ""+date.getMinutes();
		}
		else if(formulaName.contains("getSeconds("))
		{
			Date date=new Date(Long.parseLong(paras.get(0).getValue().get(0)));
			return ""+date.getSeconds();
		}
//		Evaluator evaluator = new Evaluator();
//		if (paras != null) {
//			for (int i = 0; i < paras.size(); i++) {
//				dealPara(paras.get(i));
//				evaluator.putVariable(paras.get(i).getName(), paras.get(i).getValue().get(0));
//			}
//		}
//		if(formulaName.contains("?")){
//			int indexOf = formulaName.indexOf("?");
//			int indexOf2 = formulaName.indexOf(":");
//			
//			String aa = formulaName.substring(0,indexOf);
//			String bb= formulaName.substring(indexOf+1,indexOf2);
//			String cc = formulaName.substring(indexOf2+1);
//			try {
//				String result3 = evaluator.evaluate(aa);
//				String result = evaluator.evaluate(bb);
//				String resultmaohao = evaluator.evaluate(cc);
//				if("1.0".equals(result3)){
//					if(result!=null&&!result.isEmpty()&&result.charAt(0)=='\''&&result.charAt(result.length()-1)=='\'')
//					{
//						result=result.substring(1,result.length()-1);
//					}
//					else if(result!=null&&result.endsWith(".0"))
//					{
//						result=result.substring(0,result.length()-2);
//					}
//					return result;
//				}else{
//					if(resultmaohao!=null&&!resultmaohao.isEmpty()&&resultmaohao.charAt(0)=='\''&&resultmaohao.charAt(resultmaohao.length()-1)=='\'')
//					{
//						resultmaohao=resultmaohao.substring(1,resultmaohao.length()-1);
//					}
//					else if(resultmaohao!=null&&resultmaohao.endsWith(".0"))
//					{
//						resultmaohao=resultmaohao.substring(0,resultmaohao.length()-2);
//					}
//					return resultmaohao;
//				}
//			} catch (EvaluationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//		
//		try {
//			
//			String result = evaluator.evaluate(formulaName);
//			
//			if(result!=null&&!result.isEmpty()&&result.charAt(0)=='\''&&result.charAt(result.length()-1)=='\'')
//			{
//				result=result.substring(1,result.length()-1);
//			}
//			else if(result!=null&&result.endsWith(".0"))
//			{
//				result=result.substring(0,result.length()-2);
//			}
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//			StatusbarMessageHelper.output("执行"+formulaName+"出错", e.getMessage(), StatusbarMessageHelper.LEVEL.INFO);
//			return null;
//		}
		return formulaName;
		
	}

	private void dealPara(CorrelationPara para) {
		if (para == null)
			return;
		// 处理值
		List a = para.getValue();
		if (a == null ||a.size()==0|| a.get(0) == null) {
			// 没有值就从xpath中取。
			if (para.getXpath() != null || !"".equals(para.getXpath())) {
				try {
					String[] ab = MaintainData.queryValue(para.getXpath());
					if (ab == null)
						return;
					if (a == null)
						a = new ArrayList();
					for (int i = 0; i < ab.length; i++) {
						// xpath取得的值放入value中
						a.add(ab[i]);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
				return;
		}
		//处理类型
		if (para.getType() != null) {
			for (int i = 0; i < a.size(); i++) {
				String s=dealType((String) a.get(i), para.getType());
				a.set(i, s);
			}
		}
		para.setValue(a);

	}

	private String dealType(String con, ParameterDefine.TYPE type) {
		if (type == ParameterDefine.TYPE.NUMBER) {
			if (con == null)
				con = "0";

		} else if (type == ParameterDefine.TYPE.REGEXP) {
			if (con == null)
				con = "";
		} else if (type == ParameterDefine.TYPE.STRING) {
			if (con == null)
				con = "";
			con = "\'" + con + "\'";
		} else if (type == ParameterDefine.TYPE.BOOLEAN) {
			if (con == null)
				con = "false";
		}

		return con;
	}
//	public static void main(String[] args) throws EvaluationException {
//		Evaluator evaluator = new Evaluator();
//		String expr = "#{var1} + abs(sqrt(#{var2}))";
//		evaluator.putVariable("var1", "2.0");
//		evaluator.putVariable("var2", "9");
////		evaluator.putVariable("var3", "-5");
//		String result = evaluator.evaluate(expr);
//		System.out.println("VariablesSample.main()" + result);
//	}
}
