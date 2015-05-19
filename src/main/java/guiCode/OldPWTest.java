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

import javafx.event.EventHandler;

import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;


public class OldPWTest {


  public static void main(String[] args) {
    final Label message = new Label("");
    
    VBox vb = new VBox();
    vb.setPadding(new Insets(10,0,0,10));
    vb.setSpacing(10);
    HBox hb = new HBox();
    hb.setSpacing(10);
    hb.setAlignment(Pos.CENTER_LEFT);
    
    Label label = new Label("Password");
    PasswordField pwField = new PasswordField();
    pwField.setPromptText("Password:");
    
    pwField.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle (ActionEvent e) {
        if (!pwField.getText().equals("T2f$Ay!")) {
          message.setText("Your password is incorrect!");
          message.setTextFill(Color.rgb(210, 39, 30));
        } else {
          message.setText("Your password has been confirmed");
          message.setTextFill(Color.rgb(21,117,84));
        }
        pwField.clear();
      }
    });
    hb.getChildren().addAll(label,pwField);
    vb.getChildren().addAll(hb,message);

  }

}
