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
 */
package com.wisii.edit.tag.components.decorative;

import javax.swing.JButton;
import javax.swing.Action;

/**
 * 
 * @author 钟亚军
 * 
 */
@SuppressWarnings("serial")
public class EditButton extends JButton
{

	public static String add = "add";

	public static String delete = "delete";

	public static String hidden = "hidden";

	public static String before = "before";

	public static String after = "after";

	String type;

	String pon;

	String text;

	public EditButton(String btype, String bpon, String xpath, Action action)
	{
		super();
		type = btype;
		pon = bpon;
		text = xpath;
		this.addActionListener(action);
	}

	public EditButton(String btype, String xpath, Action action)
	{
		super();
		type = btype;
		text = xpath;
		this.addActionListener(action);
	}
}
