package DataAppCode;


import javafx.scene.layout.Priority;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

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


/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class DropboxAuthWindow {
  static String code;
  static TextField authCode = new TextField();
  
  public static String createWindow() {
    
    
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10,10,10,10));
    grid.setVgap(5);
    grid.setHgap(5);
    ColumnConstraints column1 = new ColumnConstraints(100,100,Double.MAX_VALUE);
    column1.setHgrow(Priority.ALWAYS);
    grid.getColumnConstraints().addAll(column1);
    Stage stg = new Stage();
    stg.setTitle("Please provide Dropbox authorization code.");
    
    authCode.setPromptText("Copy and past the auth code from your browser.");
    authCode.setPrefColumnCount(10);
    
    GridPane.setConstraints(authCode, 0, 0);
    grid.getChildren().add(authCode);
    
    Button submit = new Button("Submit");
    GridPane.setConstraints(submit, 1, 0);
    grid.getChildren().add(submit);
    
    submit.setOnAction(new EventHandler<ActionEvent>() {
      
      @Override
      public void handle(ActionEvent e) {
        if ((authCode.getText() != null) && !authCode.getText().isEmpty()) {
          code = authCode.getText();
          stg.close();
        }
      }
    });
    
    stg.setScene(new Scene(grid, 400, 100));
    stg.showAndWait();
    
    
    return code;
    
  }
  
  

}
