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
 */package com.wisii.edit.validator;

import java.util.ArrayList;
import java.util.List;

import com.wisii.edit.uiml.element.BaseElement;

public class Validation extends BaseElement implements BaseValidator {
	/* 验证器名称，这个验证器内置的验证器名称,它内置的验证器 */
	private BaseValidator validator;
	/* 这个验证器的参数 */
	private List<String> paras;
	/* 这个验证器的错误信息,按顺序匹配报错信息中的[arg] */
	private String error;

	private Validation(Builder bb) {

		setValidator(bb.validator);
		setError(bb.msg);
		setParas(bb.paras);

	}

	/**
	 * @return the validator
	 */
	public BaseValidator getValidator() {
		return validator;
	}

	/**
	 * @param validator
	 *            the validator to set
	 */
	private void setValidator(String validator) {

		this.validator = ValidatorFactory.createValidator(validator);
	}

	/**
	 * @param paras
	 *            the paras to set
	 */
	private void setParas(List<String> paras) {
		this.paras = paras;
	}

	public boolean validate(List p) {
		
		return validator.validate(this.paras);

	}

	/**
	 * 实现组装错误信息的逻辑,按顺序将信息拼装到报错信息中
	 */
	private void setError(String err) {

		this.error = validator.getError();
		if (err == null)

			return;

		String[] a = err.split(",");
		for (int i = 0; i < a.length; i++) {
			error=error.replaceFirst(Resources.MSG_REPLACEMANT, a[i]);
		}

	}

	public String getError() {
		// 得到错误信息
		return error;

	}

	public static class Builder {
		/* 验证器名称，这个验证器内置的验证器名称 */
		private String validator;
		/* 按顺序匹配报错信息中的[arg]以，分割 */
		private String msg;
		private List paras;

		public Builder(String validatorname,String xpath, String value) {
			this.validator = validatorname;
			if (paras == null)
				paras = new ArrayList();
			if(validatorname.equals("schema"))
			{
				paras.add(new ValidationPara(xpath, value));
			}
			else
			paras.add(new ValidationPara(xpath, value).getReg());
			

		}

		public Builder setMessage(String msg) {
			this.msg = msg;
			return this;
		}

		public Builder addPara(String xpath, String value) {
			
			paras.add(new ValidationPara(xpath, value).getReg());
			return this;
		}

		public Validation build() {
			return new Validation(this);
		}

	}

}
