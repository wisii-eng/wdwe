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
 */package com.wisii.edit.tag.correlation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.components.select.Data;
import com.wisii.edit.tag.correlation.formula.CorrelationFormula;
import com.wisii.edit.tag.correlation.formula.CorrelationPara;
import com.wisii.edit.tag.correlation.formula.ParameterDefine.TYPE;
import com.wisii.edit.tag.schema.wdems.Connwith;
import com.wisii.edit.tag.schema.wdems.PopupBrowser;
import com.wisii.edit.tag.schema.wdems.Select;
import com.wisii.edit.tag.schema.wdems.Connwith.Formula;
import com.wisii.edit.tag.schema.wdems.Connwith.Option;
import com.wisii.edit.tag.schema.wdems.Connwith.Formula.Para;
import com.wisii.edit.tag.util.WdemsTagUtil;
import com.wisii.edit.tag.xpath.ParseXPath;

/**
 * 关联处理类
 * 
 * @author 闫舒寰
 * @version 1.0 2009/08/05
 */
public enum Correlation {

	Instance;

	private static final List<WdemsComponent> wcList = new ArrayList<WdemsComponent>();

	/**
	 * 用来获得中个关联系统的类
	 * 
	 * @param wc
	 */
	public void makeCorelation(final WdemsComponent wc) {

		String connName = wc.getConnWith();

		if (connName == null || connName.equals("") || wcList.contains(wc)) {
//			System.err.println("=====================" + wcList.size());
			// wcList.clear();
			return;
		}

		wcList.add(wc);

		List<Connwith> connList = WdemsTagUtil.getConnwithByName(connName);

		for (Connwith connwith : connList) {

			// 获得关联标签下的内容
			List<Object> objList = connwith.getOptionOrVarOrFormula();

			for (Object object : objList) {
				if (object instanceof Formula) {
					Formula fo = (Formula) object;

					List<Para> paraList = fo.getPara();

					for (Para para : paraList) {
						String paraMatch = para.getMatch();

						List<WdemsComponent> wList = WdemsTagManager.Instance
								.getWdemsComponentByName(paraMatch);

						for (WdemsComponent wdc : wList) {
							if (wdc.equals(wc)) {
								String foMatch = fo.getMatch();
								List<WdemsComponent> connwcList = WdemsTagManager.Instance
										.getWdemsComponentByName(foMatch);

								// 继续下一次的关联
								for (WdemsComponent wComponent : connwcList) {
									makeCorelation(wComponent);
								}
								// fo.getXpath();
							}
						}
					}
				}
			}
		}
	}

	private static final List<WdemsComponent> temp = new ArrayList<WdemsComponent>();

	/**
	 * 判断当前对象是否关联完毕
	 * 
	 * @param wc
	 * @return
	 */
	public boolean hasConnected(final WdemsComponent wc) {
		if (wcList.contains(wc) && !temp.contains(wc)) {
			temp.add(wc);
			if (wcList.size() == temp.size()) {
				temp.clear();
				wcList.clear();
				return false;
			}
			return true;
		} else {
			if (wcList.size() == 1) {
				// 如果是只有一次关联的时候则需要走这里
				temp.clear();
				wcList.clear();
				return true;
			}
			if (wcList.size() == (temp.size() + 1)) {
				temp.clear();
				wcList.clear();
				return false;
			}
			return false;
		}
	}

	/**
	 * 对控件进行实际的关联设置
	 * 
	 * @param wcc
	 */
	public void doCorrelation(final WdemsComponent wcc) {

		// System.out.println("input conn:" + wcc.getConnWith());

		// 获得关联名称
		String connName = wcc.getConnWith();

		if (connName == null || connName.equals(""))
			// 如果没有关联名称则返回
			return;

		// 如果有关联属性则把当前关联放到correlation中找到整体的感觉
		Correlation.Instance.makeCorelation(wcc);

		// 根据关联名称获得一组关联标签
		List<Connwith> connList = WdemsTagUtil.getConnwithByName(connName);

		for (Connwith connwith : connList) {

			// 获得关联标签下的内容
			List<Object> objList = connwith.getOptionOrVarOrFormula();

			// TODO 这里应该有个针对关联标签的分组，好随时提取var或者其他公式

			List<Option> optionList = new ArrayList<Option>();

			for (Object object : objList) {

				// 处理formula标签
				if (object instanceof Formula) {

					Formula fo = (Formula) object;

					// result1，当关联后的值需要设置到控件中
					String rName = fo.getMatch();

					// result2，当关联后的值需要直接设置到xpath中
					String rXPath = fo.getXpath();

					// result3，当关联后和var标签相关联
					String rvName = fo.getVarName();

					// 处理不同的公式设置值的情况
					if (rName != null) {
						// 找到相关联的控件
						List<WdemsComponent> wcList = WdemsTagManager.Instance
								.getWdemsComponentByName(rName);

						String result = (String) calculateResult(fo, wcc);
						if(result==null)
						{
							continue;
						}

						// 把新值设置到所有相关联节点
						for (WdemsComponent wc : wcList) {
							if (Correlation.Instance.hasConnected(wc)) {
								wc.getWdemsTagComponent().setValue(result);
							}
						}
					} else if (rXPath != null) {
						// 根据值更新内容，若先更新xpath则内容不会更新
						// 注，这里的xpath有可能是相对xpath，所以需要针对当前关联控件进行自动匹配
						WdemsTagUtil.updateXML(ParseXPath.Instance
								.transformXPath(wcc.getTagXPath(), rXPath),
								calculateResult(fo, wcc));
					} else if (rvName != null) {
						// FIXME 这个目前不推荐用，所以先不用
					} else {
						//当仅仅是做计算的情况下不需要回写结果
						calculateResult(fo, wcc);
					}
				}

				// 处理option部分
				if (wcc.getTagObject() instanceof Select) {
					if (object instanceof Option) {
						Option option = (Option) object;
						optionList.add(option);

						String value = option.getValue();
						String xpath = option.getXpath();

						// System.err.println(ParseXPath.Instance.transformXPath(tagXPath,
						// xpath) + " : " + value);

						// 这里需要根据值和所在列设置内容到数据库中
						if (wcc.getWdemsTagComponent().getValue() instanceof Data<?>) {
							Data<?> dd = (Data<?>) wcc.getWdemsTagComponent()
									.getValue();
							WdemsTagUtil.updateXML(ParseXPath.Instance
									.transformXPath(wcc.getTagXPath(), xpath),
									dd.getObject(Integer.parseInt(value)));
						}
					}
				}else if(wcc.getTagObject() instanceof PopupBrowser){
					 if(object instanceof Option) {
						Option option = (Option) object;
						optionList.add(option);

						String value = option.getValue();
						String xpath = option.getXpath();

						// System.err.println(ParseXPath.Instance.transformXPath(tagXPath,
						// xpath) + " : " + value);

						// 这里需要根据值和所在列设置内容到数据库中
						if (wcc.getWdemsTagComponent().getValue() instanceof Data<?>) {
							Data<?> dd = (Data<?>) wcc.getWdemsTagComponent()
									.getValue();
							WdemsTagUtil.updateXML(ParseXPath.Instance
									.transformXPath(wcc.getTagXPath(), xpath),
									dd.getObject(Integer.parseInt(value)));
						}
					}
				}
			}
		}
	}

	// 根据公式和当前激发公式的控件获得攻击计算的结果
	public Object calculateResult(final Formula fo, final WdemsComponent wc) {

		// 获得公式
		String funExp = fo.getFunction().getExpression();

		// System.out.println("function:" + funExp);

		// 公式的结果
		String result = null;

		// 公式中的成员变量
		List<CorrelationPara> crList = new ArrayList<CorrelationPara>();

		List<Para> para = fo.getPara();
		for (Para pa : para) {

			CorrelationPara cp = new CorrelationPara();
			crList.add(cp);

			// 设置公式中变量的名字
			cp.setName(pa.getName());

			// 设置公式中变量的类型
			String type = pa.getType();
            if(type!=null)
            {
            	type=type.toLowerCase();
            }
			if (type == null || type.equals("string")) {
				cp.setType(TYPE.STRING);
			} else if (type.equals("boolean")) {
				cp.setType(TYPE.BOOLEAN);
			} else if (type.equals("number")) {
				cp.setType(TYPE.NUMBER);
			} else if (type.equals("regexp")) {
				cp.setType(TYPE.REGEXP);
			}

			// correlationpara中传递value的时候需要设置一组string
			List<String> sl = new ArrayList<String>();

			// 处理不同的para的参数的情况
			if (pa.getMatch() != null) {
				List<WdemsComponent> wList = WdemsTagManager.Instance
						.getWdemsComponentByName(pa.getMatch());
				// FIXME 这里涉及到一个拼接或者取对应重复节点的问题，还需要深入定义

				for (WdemsComponent wcc : wList) {
					if (WdemsTagUtil.hasEnableFunction(funExp)) {
						// 这里特殊处理了控件关联致灰
						sl.add(wcc.getTagName());
					} else {
						if (wcc.getWdemsTagComponent().getValue() instanceof Date) {
							// 这里特殊处理了控件值为日期格式
							Date dd = (Date) wcc.getWdemsTagComponent()
									.getValue();
							sl.add(dd.getTime() + "");
						} else {
							// 这是正常情况
							sl.add(wcc.getWdemsTagComponent().getValue()
									.toString());
						}
					}
				}

				cp.setValue(sl);
				// System.out.println(pa.getName() + ":" + pa.getMatch() + ":" +
				// pa.getType());
			} else if (pa.getXpath() != null) {
				// 根据相关的相对路径获得绝对路径
				String path = ParseXPath.Instance.transformXPath(
						wc.getTagXPath(), pa.getXpath());
				// 这个是获得单值的方法，这里若是取出多值就需要设置多个值
				// Object value = WdemsTagUtil.getValue(path);
				// sl.add(value.toString());
				// WdemsTagUtil.updateXML(path, value);

				// 这个是当给定xpath可以取出多个值的情况下，一一把数组中的值设置到sl中。
				String[] ss = WdemsTagUtil.getValues(path);

				if (ss != null && ss.length != 0) {
					for (String s : ss) {
						sl.add(s);
					}
					cp.setValue(sl);
					cp.setXpath(path);
				} else {
					continue;
				}

			} else if (pa.getConstance() != null) {
				Object value = pa.getConstance();
				sl.add(value.toString());
				cp.setValue(sl);
			} else if (pa.getVarName() != null) {
				// 目前这个不做处理
			}

		}

		result = CorrelationFormula.Instance.compute(funExp, crList,wc);

		return result;
	}

}
