package guiCode;

import javafx.scene.web.HTMLEditor;

import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;

public class RichTextFXConsole extends OutputStream {
	
	private HTMLEditor output;
	
	//constructor
	public RichTextFXConsole(HTMLEditor html) {
		this.setOutput(html);
	}
	
	@Override
	public void write(final int i) throws IOException {
		//This has something to do with multi-threading
		Platform.runLater(new Runnable() {
			@Override
      public void run() {
				getOutput().setHtmlText(String.valueOf((char) i));
			}
		});
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

  public HTMLEditor getOutput() {
    return output;
  }

  public void setOutput(HTMLEditor output) {
    this.output = output;
  }

}
