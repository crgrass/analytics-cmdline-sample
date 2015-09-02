/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package DataAppCode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */

//TODO: This entire class needs to be documented
public class ImportFunctionLauncher {

  
  public static Map<String,Method> generateMethodMap() throws Exception {
    Map<String,Method> methodMap = new HashMap<String,Method>();

    methodMap.put("Google Adwords", DataAppCode.ImportAdwords.class.getMethod("main", String[].class));
    methodMap.put("Centro Digital Display", DataAppCode.ImportDD.class.getMethod("main", String[].class));
    methodMap.put("Centro Mobile Display", DataAppCode.ImportMob.class.getMethod("main", String[].class));
    methodMap.put("Centro Video Display", DataAppCode.ImportCentroVid.class.getMethod("main", String[].class));
    methodMap.put("Facebook", DataAppCode.ImportFB.class.getMethod("main", String[].class));
    methodMap.put("Twitter", DataAppCode.ImportTwitter.class.getMethod("main", String[].class));
    methodMap.put("LinkedIn", DataAppCode.ImportLinkedIn.class.getMethod("main", String[].class));
    
    return methodMap;
    
  }


  public static void main(String[] args) throws Exception {

    Map<String,Method> methodMap = new HashMap<String,Method>();

    methodMap.put("Google Adwords", DataAppCode.ImportAdwords.class.getMethod("main", String[].class));
    methodMap.put("Centro Digital Display", DataAppCode.ImportDD.class.getMethod("main", String[].class));
    methodMap.put("Centro Mobile Display", DataAppCode.ImportMob.class.getMethod("main", String[].class));
    methodMap.put("Centro Video Display", DataAppCode.ImportCentroVid.class.getMethod("main", String[].class));
    methodMap.put("Facebook", DataAppCode.ImportFB.class.getMethod("main", String[].class));
    methodMap.put("Twitter", DataAppCode.ImportTwitter.class.getMethod("main", String[].class));
    methodMap.put("LinkedIn", DataAppCode.ImportLinkedIn.class.getMethod("main", String[].class));
    String[] params = null;
    methodMap.get("ImportLinkedIn").invoke(null, (Object) params);
    
  }
  


}//end of function launcher
