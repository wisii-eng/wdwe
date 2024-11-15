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
 *//*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.wisii.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wisii;
import com.wisii.fov.util.Function;
import com.wisii.fov.util.Sutil;

/**
 * This class is used to evaluate the version information contained in the
 * Manifest of FOV's JAR. Note that this class can only find the version
 * information if it's in the com.wisii.fov package as this package equals the
 * one specified in the manifest.
 */
public final class Version {

	private Version() {
	}

	/**
	 * Get the version of FOV
	 * 
	 * @return the version string
	 */
	public static String getVersion() {

		Package jarinfo = Version.class.getPackage();
		StringBuilder sb = new StringBuilder();
		String version = null;
		if (jarinfo != null) {
			version = jarinfo.getImplementationVersion();
			if (version != null && !"".equalsIgnoreCase(version)) {
				sb.append("开发版本:" + version);// Implementation-Version:
				// String date=jarinfo.getImplementationTitle();
				// String[] as = date.split("-");
				// if(as[1].equalsIgnoreCase("9999"))
				// sb.append("无期限包");
				// else
				// sb.append("过期日期:" + as[1]+"-"+as[2]+"-"+as[3]);

				// if(as.length>4)
				// sb.append("用户" + as[4]);

			}

		} else {
			sb.append("请检查Jar包");

		}

		return sb.toString();
	}

	public static String getVersionDetial() {

		Package jarinfo = Version.class.getPackage();
		StringBuilder sb = new StringBuilder();
		if (jarinfo != null) {

			sb.append("开发商: www.wisii.com " + '\n');// Implementation-Vendor:
			sb.append("开发版本: " + jarinfo.getImplementationVersion() + '\n');// Implementation-Version:
			sb.append("软件名称: " + jarinfo.getSpecificationTitle() + '\n');// Specification-Title:
			if (Sutil.getF("nkksds") != null) {
				sb.append("软件授权给: " + Sutil.getF("nkksds") + '\n');// Specification-Title:
			} else
				sb.append("测试版软件，请申请正式的许可");// Specification-Title:

			if (Sutil.getF("c") != null
					&& ((Function) Sutil.getF("c")).getParms().get("c3") != null) {
				sb.append("软件授权应用: "
						+ ((Function) Sutil.getF("c")).getParms().get("c3") + '\n');// Specification-Title:
			}

		} else
			sb.append("请检查Jar包");

		return sb.toString();
	}

	public static void main(String[] arg) {
		System.out.println(Version.getVersion());
	}

}
