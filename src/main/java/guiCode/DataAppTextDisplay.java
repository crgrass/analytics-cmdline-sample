package guiCode;

import javafx.application.Platform;



import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ScrollPane;
import javafx.application.Application;
import javafx.stage.Stage;

public class DataAppTextDisplay extends Application {
  
    public ScrollPane classSP;
    public TextFlow classTF;
  
  
    //Constructor
    public DataAppTextDisplay() {
      //create scrollpane
      classSP = new ScrollPane();
      classSP.setVbarPolicy(ScrollBarPolicy.ALWAYS);
      
      
      //create textflow
      classTF = new TextFlow();
      classTF.setPrefWidth(600);
      classTF.setPrefHeight(400);
      classSP.setContent(classTF);

      
      
    }
    
    
    public void write(Text t) {
        //This has something to do with multi-threading
        Platform.runLater(new Runnable() {
          @Override  
          public void run() {
                classTF.getChildren().add(t);
            }
        });
    }

    
    
	@Override
	public void start(Stage primaryStage) {
	  DataAppTextDisplay testDATD = new DataAppTextDisplay(); // build display
	  
	  
	  //Creat Text Object
	  Text txt1 = new Text("Hello");
	  testDATD.write(txt1);
	  
	  Text txt2 = new Text("Now");
	 
	  
	  
	  Scene scene = new Scene(testDATD.classSP, 800,600, Color.WHITE);
      primaryStage.setScene(scene);
      primaryStage.show();
      
      testDATD.write(txt2);
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
