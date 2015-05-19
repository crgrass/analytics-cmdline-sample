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

package guiCode;

import javafx.scene.text.Text;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class OutputMessages {

  public static Text startingVendorImport(String vendor) {
    Text msg = new Text("Starting " + vendor + " Import.\n\n");
    msg.setId("test");
    return msg;
  }
  
  public static Text vendorImportComplete(String vendor) {
    Text msg = new Text(vendor + " Import Complete.\n");
    msg.setId("import-complete");
    return msg;
  }
  
  public static Text divider() {
    String divider = "";
    for (int i = 0; i < 120; i++) {
      divider += "-";
    }
    divider += "\n\n";
    
    Text msg = new Text(divider);
    return msg;
  }
  
  public static Text dbConnectionSuccess(){
    Text msg = new Text("Connection to the Database Successful.\n\n");
    return msg;
    
  }
  
  public static Text errorMessage(String e) {
    Text msg = new Text(e);
    return msg;
  }
  
  public static Text importActivity(String e) {
    Text msg = new Text(e);
    return msg;
  }
  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
