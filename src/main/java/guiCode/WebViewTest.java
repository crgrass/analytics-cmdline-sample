package guiCode;

import javafx.event.EventHandler;

import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.application.Application;
import javafx.stage.Stage;

public class WebViewTest extends Application {

	@Override
	public void start(Stage primaryStage) {
	  primaryStage.setTitle("HTML");
      primaryStage.setWidth(500);
      primaryStage.setHeight(500);
      Scene scene = new Scene(new Group());
      VBox root = new VBox();    
      final WebView browser = new WebView();
      final WebEngine webEngine = browser.getEngine();
      Hyperlink hpl = new Hyperlink("java2s.com");
      hpl.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent e) {
            webEngine.load("http://java2s.com");
        }
    });
      
      root.getChildren().addAll(hpl,browser);
      scene.setRoot(root);

      primaryStage.setScene(scene);
      primaryStage.show();
	  
	}

	public static void main(String[] args) {
		launch(args);
	}
}
