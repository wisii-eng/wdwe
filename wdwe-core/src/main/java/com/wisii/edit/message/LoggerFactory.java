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
 * @MyLog.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.message;

/**
 * 类功能描述：
 *
 * 作者：p.x
 * 创建日期：2009-7-20
 */
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {

    private static Logger fileLogger;

    static {
        fileLogger = Logger.getLogger("com.wisii.edit.message");
        fileLogger.setLevel(Level.OFF);
        Handler[] hs = fileLogger.getHandlers();
        for (Handler h : hs) {
            h.close();
            fileLogger.removeHandler(h);
        }
        try {
        	String logPath = System.getProperty("user.home");
    		if(logPath != null){
    			logPath = logPath.trim().replace("\\", "/")+"/wdems";
    		}else{
    			logPath = "/wdems";
    		}
        	LogFileStreamHandler fh = new LogFileStreamHandler(logPath,  true); 
            fh.setEncoding("GBK");
            fh.setFormatter(new LogFormatter());
            fileLogger.setUseParentHandlers(false);  
            fileLogger.addHandler(fh);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private LoggerFactory() {
    }

    /** 
     * 返回一个文件记录实例
     */
    public static synchronized Logger getFileLogger() {
        return fileLogger;
    }
}
