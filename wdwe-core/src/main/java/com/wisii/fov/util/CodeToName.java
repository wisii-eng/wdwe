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
 */package com.wisii.fov.util;

import java.io.Serializable;

public class CodeToName implements Serializable{
	private Object code;

	private Object name;

	public CodeToName(Object code, Object name) {
		this.code = code;
		this.name = name;

	}

	/**
	 * @return the code
	 */
	public Object getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(Object code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public Object getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(Object name) {
		this.name = name;
	}
	public String toString()
	{
		return name.toString();
	}

}
