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
 */package com.wisii.edit.tag.action;

import java.awt.event.ActionEvent;

/**
 * 按钮的动作类
 * @author 闫舒寰
 * @version 1.0 2009/08/05
 */
public class ButtonAction extends Actions {

	@Override
	public Object doAction(final ActionEvent e) {
		// TODO Auto-generated method stub
		
		getWdemsComponent();
		
		return null;
	}

	@Override
	public void fireConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean updateXML() {
		// TODO Auto-generated method stub
		return false;
	}

}
