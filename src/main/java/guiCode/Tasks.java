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

import javafx.application.Platform;

import javafx.scene.control.Button;
import javafx.concurrent.Task;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class Tasks {

  public static Task disableFullImport(Button commenceImportButton, boolean b){
    Task disableImportOnExit = new Task<Void>(){
      @Override
      public Void call(){
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            commenceImportButton.setDisable(b);
          }
        });
        return null;
      }
    };
    
    return disableImportOnExit;
  }
  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
