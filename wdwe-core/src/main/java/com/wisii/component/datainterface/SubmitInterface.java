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
 *//**
 * 
 */
package com.wisii.component.datainterface;

import java.util.Map;


/**该类用于提供用户一个可实现的得到提交数据的接口
 * @author liuxiao
 *
 */
public interface SubmitInterface {
/**
 * 
 * @param 提交给用户的xml
 * @return 如果出现错误信息等需要返回给WDEMS的提示用户错误信息的内容
 */
	public String  submit(String xml, Map<String, Object> info);
}
