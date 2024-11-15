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
 */package com.wisii.edit.view;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * @author shutu008
 *selectNode鐨勪娇鐢ㄦ柟娉�
 */
public class xpathDemo {

    public static void main(String[] args) throws Exception {

    Document doc = new SAXReader().read(new File("e:/test/contact.xml"));
        
        /**
         * @param xpath 琛ㄧずxpath璇硶鍙橀噺
         */
    String xpath="";
        
        /**
         * 1.      /      缁濆璺緞      琛ㄧず浠巟ml鐨勬牴浣嶇疆寮�濮嬫垨瀛愬厓绱狅紙涓�涓眰娆＄粨鏋勶級
         */
        xpath = "/contactList";
        xpath = "/contactList/contact";
        
        /**
         * 2. //     鐩稿璺緞       琛ㄧず涓嶅垎浠讳綍灞傛缁撴瀯鐨勯�夋嫨鍏冪礌銆�
         */
        xpath = "//contact/name";
        xpath = "//name";
        
        /**
         * 3. *      閫氶厤绗�         琛ㄧず鍖归厤鎵�鏈夊厓绱�
         */
        xpath = "/contactList/*"; //鏍规爣绛綾ontactList涓嬬殑鎵�鏈夊瓙鏍囩
        xpath = "/contactList//*";//鏍规爣绛綾ontactList涓嬬殑鎵�鏈夋爣绛撅紙涓嶅垎灞傛缁撴瀯锛�
        
        /**
         * 4. []      鏉′欢           琛ㄧず閫夋嫨浠�涔堟潯浠朵笅鐨勫厓绱�
         */
        //甯︽湁id灞炴�х殑contact鏍囩
        xpath = "//contact[@id]";
        //绗簩涓殑contact鏍囩
        xpath = "//contact[2]";
        //閫夋嫨鏈�鍚庝竴涓猚ontact鏍囩
        xpath = "//contact[last()]";
        
        /**
         * 5. @     灞炴��            琛ㄧず閫夋嫨灞炴�ц妭鐐�
         */
        xpath = "//@id"; //閫夋嫨id灞炴�ц妭鐐瑰璞★紝杩斿洖鐨勬槸Attribute瀵硅薄
        xpath = "//contact[not(@id)]";//閫夋嫨涓嶅寘鍚玦d灞炴�х殑contact鏍囩鑺傜偣
        xpath = "//contact[@id='002']";//閫夋嫨id灞炴�у�间负002鐨刢ontact鏍囩
        xpath = "//contact[@id='001' and @name='eric']";//閫夋嫨id灞炴�у�间负001锛屼笖name灞炴�т负eric鐨刢ontact鏍囩
        
        /**
         *6.  text()   琛ㄧず閫夋嫨鏂囨湰鍐呭
         */
        //閫夋嫨name鏍囩涓嬬殑鏂囨湰鍐呭锛岃繑鍥濼ext瀵硅薄
        xpath = "//name/text()";
        xpath = "//contact/name";//閫夋嫨濮撳悕涓哄紶涓夌殑name鏍囩
        
        
        List<Node> list = doc.selectNodes(xpath);
        for (Node node : list) {
            System.out.println(node);
            node.setText("aaabbb");
        }
    
        //鍐欏嚭xml鏂囦欢
        //杈撳嚭浣嶇疆
        FileOutputStream out = new FileOutputStream("e:/test/contact.xml");
        
        //鎸囧畾鏍煎紡
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(out,format);
        
        //鍐欏嚭鍐呭
        writer.write(doc);
        
        //鍏抽棴璧勬簮
        writer.close();
        
    }
    

}