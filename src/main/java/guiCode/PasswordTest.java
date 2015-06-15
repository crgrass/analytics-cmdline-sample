package guiCode;

import javafx.scene.control.TextField;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.stage.Stage;

public class PasswordTest extends Application {
  
  
  public void launchDataApp() {
      Platform.runLater(new Runnable() {
         public void run() {             
             String[] test = {};
             new DataAppTest().start(new Stage());
         }
      });
  }
  
  


	@Override
	public void start(Stage primaryStage) {
	  final Label message = new Label("");
	    
	    VBox vb = new VBox();
	    vb.setPadding(new Insets(10,0,0,10));
	    vb.setSpacing(10);
	    HBox hb = new HBox();
	    hb.setSpacing(10);
	    hb.setAlignment(Pos.CENTER_LEFT);
	    HBox hbUserName = new HBox();
	    hbUserName.setSpacing(10);
	    hb.setAlignment(Pos.CENTER_LEFT);
	    
	    Label label = new Label("Password  ");//TODO: use something other than spacing to align
	    PasswordField pwField = new PasswordField();
	    pwField.setPromptText("Password:");
	    Label lblUserName = new Label("User Name");
	    TextField txtUserName = new TextField();
	    txtUserName.setPromptText("User Name");
	    
	    pwField.setOnAction(new EventHandler<ActionEvent>() {
	      @Override public void handle (ActionEvent e) {
	        if (!pwField.getText().equals("usmadmin")) {
	          message.setText("Your password is incorrect!");
	          message.setTextFill(Color.rgb(210, 39, 30));
	        } else {
	          message.setText("Your password has been confirmed");
	          message.setTextFill(Color.rgb(21,117,84));
	          launchDataApp();
	          primaryStage.hide();
	          
	        }
	        pwField.clear();
	      }
	    });
	    hb.getChildren().addAll(label,pwField);
	    hbUserName.getChildren().addAll(lblUserName,txtUserName);
	    vb.getChildren().addAll(hbUserName,hb,message);
	    
	    Scene pwScene = new Scene(vb, 400,200);
	    primaryStage.setTitle("USM Marketing Data App");
	    primaryStage.setScene(pwScene);
	    primaryStage.show();
	}

	public static void main(String[] args) {
	  System.out.println("Printing at start of pwd test");
		launch(args);
	}
}
