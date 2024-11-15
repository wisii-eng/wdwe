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
 */package com.wisii.edit.tag;

import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.action.ActionFactory;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.factories.WdemsComponentFactories;
import com.wisii.edit.tag.util.WdemsTagUtil;
import com.wisii.edit.util.EngineUtil;


/**
 * 负责创建实际可用的WdemsComponet对象
 * 
 * 应该在这里初始化WdemsComponet对象中所有常用的变量
 * 
 * @author 闫舒寰
 * @version 1.0 2009/06/15
 */
public enum WdemsComponentFactory {
	
	Instance;
	
	public WdemsComponent createWdemsComponent(final WdemsTagID tagID, final Object tagObj){
		
		if (tagObj == null) {
			return null;
		}
		//创建出通用接口的实体类
		WdemsComponent wc = new WdemsComponentImpl(tagID, tagObj);
		
		//获得该控件的关联属性
		try {
			//先反射取得方法，再反射调用该方法获得关联组件的名字
			Object connName = tagObj.getClass().getMethod("getConn", null).invoke(tagObj, null);
			wc.setConnWith((String)connName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//获得实体的tag对象
		wc.setWdemsTagComponent(WdemsComponentFactories.Instance.getWdemsComponent(wc));
		wc.getWdemsTagComponent().setDefaultValue(tagID.getDefaultvalue());
		//为控件设置原始值
		wc.getWdemsTagComponent().iniValue(WdemsTagUtil.getValue(wc.getTagXPath()));
		
		
		
		//为控件设置提示信息
		WdemsTagUtil.configTooltip(wc);//(新版本)

		//老版本
//		BalloonTip bt = WdemsTagUtil.getHintBalloon(wc.getTagObject(), wc);
//		//保存一个提示框的引用
//		if (bt != null && wc != null) {
//			WdemsTagManager.Instance.setAllBallons(wc, bt);
//		}
		
		//获得action动作接口，并把动作注册到控件中
		Actions action = ActionFactory.Instance.getAction(wc);
		if (action != null) {
			//把控件上的信息设置到action中
			action.setWdemsComponent(wc);
			action.setMessageListener(EngineUtil.getEnginepanel().getMessageListener());
			wc.setWdemsAction(action);
		} else {
			//FIXME 这里需要有异常处理
			StatusbarMessageHelper.output("没有动作", wc.getTagName() + "动作为空:", StatusbarMessageHelper.LEVEL.INFO);
		}
		
		return wc;
	}
}
