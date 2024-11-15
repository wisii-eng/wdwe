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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JTextArea;

public class UrlSessiontest extends JApplet {
	JTextArea jta = new JTextArea();
	/**
	 * Constructor of the applet.
	 *
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 */
	public UrlSessiontest() {
		super();
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it is being reclaimed and that it should destroy
	 * any resources that it has allocated. The <code>stop</code> method
	 * will always be called before <code>destroy</code>. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has any operation that it wants to perform before it is
	 * destroyed. For example, an applet with threads would use the
	 * <code>init</code> method to create the threads and the
	 * <code>destroy</code> method to kill them. <p>
	 */
	public void destroy() {
		// Put your code here
	}

	/**
	 * Returns information about this applet. An applet should override
	 * this method to return a <code>String</code> containing information
	 * about the author, version, and copyright of the applet. <p>
	 *
	 * @return  a string containing information about the author, version, and
	 * copyright of the applet.
	 */
	public String getAppletInfo() {
		return "This is my default applet created by Eclipse";
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it has been loaded into the system. It is always
	 * called before the first time that the <code>start</code> method is
	 * called. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has initialization to perform. For example, an applet with
	 * threads would use the <code>init</code> method to create the
	 * threads and the <code>destroy</code> method to kill them. <p>
	 */
	public void init() {
		// Put your code here
		jta.setText(sendPost("http://192.168.0.249:9080/test/url.jsp", ""));
		this.getContentPane().add(jta);
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it should start its execution. It is called after
	 * the <code>init</code> method and each time the applet is revisited
	 * in a Web page. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has any operation that it wants to perform each time the Web
	 * page containing it is visited. For example, an applet with
	 * animation might want to use the <code>start</code> method to
	 * resume animation, and the <code>stop</code> method to suspend the
	 * animation. <p>
	 */
	public void start() {
		// Put your code here
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it should stop its execution. It is called when
	 * the Web page that contains this applet has been replaced by
	 * another page, and also just before the applet is to be destroyed. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has any operation that it wants to perform each time the Web
	 * page containing it is no longer visible. For example, an applet
	 * with animation might want to use the <code>start</code> method to
	 * resume animation, and the <code>stop</code> method to suspend the
	 * animation. <p>
	 */
	public void stop() {
		// Put your code here
	}
	public static String sendPost(String url, String param) {
		System.err.println("applet_171_ param=" + param);
		String result = "";
		try {
			URL httpurl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) httpurl
					.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			PrintWriter out = new PrintWriter(httpConn.getOutputStream());
			out.print(param);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
		} catch (Exception e) {
			System.out.println("没有结果！" + e);
		}
		return result;
	}

}
