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

import com.wisii.edit.tag.factories.bar.BasicLookAndFeelAddons;
public class AquaLookAndFeelAddons extends BasicLookAndFeelAddons {

  public void initialize() {
    super.initialize();
    loadDefaults(getDefaults());
  }

  public void uninitialize() {
    super.uninitialize();
    unloadDefaults(getDefaults());
  }
  
  private Object[] getDefaults() {
    
    Object[] defaults =
      new Object[] {
      "TaskPaneGroupUI",
      "com.wisii.edit.tag.factories.bar.GlossyTaskPaneGroupUI",
    };
    return defaults;
  }
  
}
