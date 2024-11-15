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
 * @PROJECT.FULLNAME@ @VERSION@ License.
 *
 * Copyright @YEAR@ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wisii.edit.tag.factories.bar;

import java.util.Arrays;
import java.util.List;

/**
 * Addon for <code>JLinkButton</code>.<br>
 * 
 */
public class JLinkButtonAddon extends AbstractComponentAddon {

  public JLinkButtonAddon() {
    super("JLinkButton");
  }

  protected void addBasicDefaults(LookAndFeelAddons addon, List defaults) {
    defaults.addAll(Arrays.asList(new Object[] {JLinkButton.UI_CLASS_ID,
      "com.wisii.edit.tag.factories.bar.BasicLinkButtonUI"}));
  }

  protected void addWindowsDefaults(LookAndFeelAddons addon, List defaults) {
    defaults.addAll(Arrays.asList(new Object[] {JLinkButton.UI_CLASS_ID,
      "com.wisii.edit.tag.factories.bar.WindowsLinkButtonUI"}));
  }

}
