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

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class GUIJunkyard {

}



/*
 * Taken from progress bar in Data App
 */
//final Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
//@Override protected ObservableList<String> call() throws InterruptedException {
//  updateMessage("Importing Vendors...");
//  updateMessage("Finished.");
//  return FXCollections.observableArrayList("Vendors Imported");
//} // end of call
//}; //end of task
//statusLabel.textProperty().bind(task.messageProperty());
//btnStartFullImport.disableProperty().bind(task.runningProperty());
//pb.progressProperty().bind(task.progressProperty());
//
//task.stateProperty().addListener(new ChangeListener<Worker.State>() {
//@Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
//if (newState == Worker.State.SUCCEEDED) {
//  System.out.println("This is ok, this thread" + Thread.currentThread() + "is the JavaFX Application thread.");
//  btnStartFullImport.setText("Voila!");
//}
//}
//});
//stgFullImportStatus.show();
//Pausing thread to allow progress bar to load
