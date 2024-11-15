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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import com.wisii.edit.authority.Commision;
import com.wisii.edit.tag.components.balloontip.BalloonTip;
import com.wisii.edit.tag.components.decorative.VirtualButton;
import com.wisii.edit.tag.components.decorative.WdemsOperationManager;
import com.wisii.edit.tag.components.select.datasource.DataSource;
import com.wisii.fov.apps.FOUserAgent;


/**
 * 该类是一个总的出口类，用于产生WdemsComponent的类
 * 所有外部人员对标签库的操作都应该通过这个类
 * 
 * @author 闫舒寰
 * @version 1.0 2009/06/10
 */
public enum WdemsTagManager {
	
	Instance;
	
	//当前页的所有控件的一个索引
	private static List<WdemsComponent> allComponetsList = new ArrayList<WdemsComponent>();
	//为所有控件的提示框建立一个索引
	private static Map<WdemsComponent, BalloonTip> allBallons = new HashMap<WdemsComponent, BalloonTip>();
	

	public Map<WdemsComponent, BalloonTip> getAllBallons() {
		return allBallons;
	}

	public void setAllBallons(final WdemsComponent wtc, final BalloonTip bt) {
		allBallons.put(wtc, bt);
	}

	//当前的整体的面板
//	private MessageListener messageListener;
	
	/**
	 * 根据id产生实际的控件，传递进来的id要符合规范。
	 * 当传递进来的id为name()时，视作无xpath，可以产生对象，但是对象内无xpath，主要是应对gruop标签
	 * @param id
	 * @return
	 */
	public List<WdemsComponent> getWdemsComponent(final String id,FOUserAgent userAgent){
		
		//如果传过来的id不对，则抛出异常
		if (id == null || id.equals(""))
			throw new IllegalArgumentException();
		
		List<WdemsComponent> wcList = new ArrayList<WdemsComponent>();
		
		//TODO 目前没有采用总控的方式进行设置，而是不停的遍历设置，以后需要结合总控的架构来改进
		/*if (allComponetsList != null ) {
			//如果已经创建了则不用再次创建，这里有个假设就是id中不能有同名的控件
			
			//解析id获得控件列表
			List<WdemsTagID> wtid = parserID(id);
			
			for (WdemsTagID wid : wtid) {
				for (WdemsComponent wc : allComponetsList) {
					if (wc.getTagID().equals(id) && wc.getTagName().equals(wid.getTagName())) {
						wcList.add(wc);
					}
				}
			}
			
		} else {*/
			//第一次初始化控件的时候走这里
			//这里应该拿到的是ID，需要有个解析id的过程
			/**
			 * 分以下几步：
			 * 1、解析id，生成id接口的对象(在WdemsTagIDFactory中完成)
			 * 2、由id接口对象生成WdemsComponents对象
			 * 3、这里直接返回WdemsComponents对象
			 */
			
			//解析id获得控件列表
			List<WdemsTagID> wtid = parserID(id);
			
			//根据控件列表获得实际标签列表
			List<Object> tagList = new ArrayList<Object>();
			//根据id中的名字获得tag对象
			for (WdemsTagID wdemsTagID : wtid) {
				tagList.add(getWdemsTags(wdemsTagID.getTagName()));
			}

			//根据解析后的id和tag对象获得实际的wdemsComponent对象。该方法内包含了实际的创建JComponet和相应的action的过程
//			List<WdemsComponent> wcList = new ArrayList<WdemsComponent>();
			
			for (int i = 0; i < wtid.size(); i++) {
				//这个地方首先要根据tagObject的权限属性来判断是否能需要创建对象。
				String authrity = wtid.get(i).getAuthority();
				if (!Commision.isCommision(authrity,userAgent)) {
					continue;
				}
				WdemsComponent temp = null;
				//用来判定当前控件有没有被创建过
				boolean has = false;
				//TODO 这里目前用循环来弄，用map拼接key值有风险
				for (WdemsComponent wc : allComponetsList) {
					if (wc.getTagID().equals(id) && 
							wc.getTagName().equals(wtid.get(i).getTagName()) && 
							wc.getTagXPath().equals(wtid.get(i).getTagXPath())) {
						temp = wc;
						has = true;
					}
				}
				if (!has) {
					temp = WdemsComponentFactory.Instance.createWdemsComponent(wtid.get(i), tagList.get(i));
				}
				if (temp != null) {
					wcList.add(temp);
					if (!has) {
						//把fo中唯一的id设置到标签中
						temp.setTagID(id);
						allComponetsList.addAll(wcList);
					}
					
				} else {
					//FIXME 算是严重错误了，应该处理
					System.err.println("no tag found:" + wtid.get(i).getTagName());
					continue;
				}
			}
			
//			System.out.println(allComponetsList.size());
//		}
		/* 【删除：START】 by 李晓光	2009-10-16*/
//		if (wcList.size() > 1) {
//			filterList(wcList);	
//		}
		/* 【删除：END】 by 李晓光	2009-10-16*/
		return wcList;
	}
	
	//用于处理id中含有button的问题，把button摘除到list之外
	//目前的约定是，控件中有一个主控件和一组按钮，
	private void filterList(final List<WdemsComponent> wcList) {
		
//		for (WdemsComponent wc : wcList) {
//			System.out.println(wc.getTagName());
//		}
		
		//目前只有这一种逻辑，就是主控件配按钮，还需要扩展
		List<VirtualButton> vbList = new ArrayList<VirtualButton>();
		JComponent jc = null;
		for (WdemsComponent wc : wcList) {
			if (wc.getWdemsTagComponent() instanceof VirtualButton) {
				VirtualButton vb = (VirtualButton) wc.getWdemsTagComponent();
				vbList.add(vb);
				wcList.remove(wc);
			}
			
			if (wc.getWdemsTagComponent() instanceof JComponent) {
				JComponent jcc = (JComponent) wc.getWdemsTagComponent();
				jc = jcc;
			}
		}
		
		if ((jc != null) && (vbList.size() != 0)) {
			WdemsOperationManager.registerComponent(jc, vbList);
			for (VirtualButton vb : vbList) {
				wcList.remove(vb);
			}
		} 
	}
	
	public void initialTagMap(final String s) {
//		this.messageListener = messageListener;
		WdemsTagSource.Instance.initialTagMap(s);
	}
	
	/**
	 * 根据名字返回已经生成的一组控件，有可能同名的控件有一组，但一般来说只有一个
	 * @param name	控件的名称
	 * @return	一组以该名称组成的控件
	 */
	public List<WdemsComponent> getWdemsComponentByName(final String name){
		//TODO 以后发现第一次调用的时候可以构建一个map，这样不用每次都遍历一遍
		List<WdemsComponent> wcList = new ArrayList<WdemsComponent>();
		
		for (WdemsComponent com : WdemsTagManager.allComponetsList) {
			if (com.getTagName().equals(name)) {
				wcList.add(com);
			}
		}
		return wcList;
	}
	
	/**
	 * 根据数据的xpath返回一组控件，这组控件对应一个xpath
	 * @param xpath xpath值
	 * @return	一组对应该xpath的对象
	 */
	public List<WdemsComponent> getWdemsComponentByXPath(final String xpath){
		//TODO 以后发现第一次调用的时候可以构建一个map，这样不用每次都遍历一遍
		List<WdemsComponent> wcList = new ArrayList<WdemsComponent>();
		
		for (WdemsComponent com : WdemsTagManager.allComponetsList) {
			if (com.getTagXPath().equals(xpath)) {
				wcList.add(com);
			}
		}
		
		return wcList;
	}
	
	
	/**
	 * 根据名字获得控件对象
	 * @param name
	 * @return
	 */
	public Object getWdemsTags(final String name){
		return WdemsTagSource.Instance.getWdemsTag(name);
	}
	
	/**
	 * 解析读取到的id
	 * @param id
	 * @return
	 */
	public List<WdemsTagID> parserID(final String id){
		return WdemsTagIDFactory.Instance.parseWdemsTagID(id);
	}
	
	
	public void validateComponents(){
//		System.out.println(allComponetsList.size());
		for (WdemsComponent wc : allComponetsList) {
//			wc.getValidateState()
		}
		
	}
	
	/**
	 * 判断控件中是否还有验证没有通过的
	 * 返回true是有验证没通过的，返回false是没有验证没通过的
	 * @return
	 */
	public boolean hasWrongValidationComponents(){
		for (WdemsComponent wc : allComponetsList) {
			if (!wc.getValidateState())
				return true;
		}
		return false;
	}
	
	/**
	 * 设置所有验证出错的控件的值回到初始值
	 */
	public void setAllWrongValComBackIniValue(){
		for (WdemsComponent wc : allComponetsList) {
			if (!wc.getValidateState()) {
				wc.setBackIniValue();
			}
		}
	}
	
	/**
	 * 当翻页，重排等时候需要把当前页产生的编辑控件清空一下。
	 */
	public void clearCurrentPageComponents(){
		allComponetsList.clear();
		
		for (BalloonTip bt : getAllBallons().values()) {
			if (bt != null) {
				bt.closeBalloon();
			}
		}
		getAllBallons().clear();
	}
	
	//存储dataSource标签的map
	private Map<com.wisii.edit.tag.schema.wdems.DataSource, DataSource> dataSourceMap;
	
	/**
	 * 根据名字返回dataSource标签所生成的对象
	 * @param name
	 * @return
	 */
	public DataSource getDataSource(final String name) {

		if (dataSourceMap == null) {
			dataSourceMap = new HashMap<com.wisii.edit.tag.schema.wdems.DataSource, DataSource>();
		}

		Object o = getWdemsTags(name);

		com.wisii.edit.tag.schema.wdems.DataSource source = null;

		if (o instanceof com.wisii.edit.tag.schema.wdems.DataSource) {
			com.wisii.edit.tag.schema.wdems.DataSource ds = (com.wisii.edit.tag.schema.wdems.DataSource) o;
			source = ds;
		}

		if (source != null) {
			DataSource datasource = dataSourceMap.get(source);
			if (datasource == null) {
				datasource=new DataSource(source);
				dataSourceMap.put(source,datasource );
			}
			return datasource;
		}
		return null;
	}
}
