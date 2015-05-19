package guiCode;

import javafx.scene.control.ScrollPane;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.application.Application;
import javafx.stage.Stage;

public class TextFlowTest extends Application {

	@Override
	public void start(Stage primaryStage) {
	  String family = "Helvetica";
	  double size = 50;
	  
	  TextFlow textFlow = new TextFlow();
	  textFlow.setLayoutX(40);
	  textFlow.setLayoutY(40);
	  Text text1 = new Text("Hello ");
	  text1.setFont(Font.font(family,size));
	  text1.setFill(Color.RED);
	  Text text2 = new Text("Bold\n");
	  text2.setFill(Color.ORANGE);
	  text2.setFont(Font.font(family, FontWeight.BOLD,size));
	  Text text3 = new Text("World");
	  text3.setFill(Color.GREEN);
	  text3.setFont(Font.font(family, FontPosture.ITALIC, size));
	  textFlow.getChildren().addAll(text1,text2,text3);
	  
	
	  
	  
	  Group group = new Group(textFlow);
	  
	  primaryStage.setTitle("Hello Rich Text");
	  
	//ScrollPane
      ScrollPane s1 = new ScrollPane();
      s1.setContent(group);
      s1.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
      
      Scene scene = new Scene(s1, 500,150, Color.WHITE);
	  primaryStage.setScene(scene);
	  primaryStage.show();
	  
	  
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
