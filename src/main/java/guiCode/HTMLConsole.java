package guiCode;

import javafx.scene.web.HTMLEditor;

import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class HTMLConsole extends OutputStream {
	
	private HTMLEditor output;
	
	//constructor
	public HTMLConsole(HTMLEditor html) {
		this.output =html;
	}
	
	@Override
	public void write(final int i) throws IOException {
		//This has something to do with multi-threading
		Platform.runLater(new Runnable() {
		  @Override
		  public void run() {
				output.setHtmlText(String.valueOf((char) i));
			}
		});
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
