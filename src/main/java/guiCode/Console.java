package guiCode;

import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Console extends OutputStream {
	
	private TextArea output;
	
	//constructor
	public Console(TextArea ta) {
		this.output =ta;
	}
	
	@Override
	public void write(final int i) throws IOException {
		//This has something to do with multi-threading
		Platform.runLater(new Runnable() {
			public void run() {
				output.appendText(String.valueOf((char) i));
			}
		});
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
