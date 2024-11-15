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
 * @LogFormatter.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.edit.message;

/**
 * 类功能描述：自定义格式化器
 *
 * 作者：p.x
 * 创建日期：2009-7-20
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    //时间
    private Date date = new Date();
    //参数
    private Object[] args = new Object[1];
    //消息格式化器
    private MessageFormat formatter;
    //时间参数
    private String format = "{0,date} {0,time}";
    //行分格符
    private String lineSeparator = "\r\n";

    /** 
     * @param 日志记录器
     * @return 返回格式化好的日志内容
     */
    public String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        date.setTime(record.getMillis());
        args[0] = date;
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        
        sb.append("[");
        sb.append(text);
        sb.append("]   ");
        String message = formatMessage(record);
        sb.append(record.getLevel().getLocalizedName());
        sb.append(": ");
        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }
        return sb.toString();
    }
}
