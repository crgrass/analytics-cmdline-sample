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

import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;

import javafx.application.Platform;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */


/*
 * The purpose of the TextDisplayHandler is to create a 
 * logging handler that will push logging events to 
 * the text display
 */
public class TextDisplayHandler extends java.util.logging.Handler {
  
  DataAppTextDisplay display;
  
  
  //The text area needs to be passed to the publish method
  
  @Override
  public void publish(final LogRecord record) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        StringWriter text = new StringWriter();
        PrintWriter out = new PrintWriter(text);
        out.printf("%s" + System.lineSeparator(), record.getMessage());
        Text level = new Text(text.toString());
        display.write(level);
      }
    });
  }
  
  
  public void setTextDisplay(DataAppTextDisplay display) {
    this.display = display;
  }


  /* (non-Javadoc)
   * @see java.util.logging.Handler#close()
   */
  @Override
  public void close() throws SecurityException {
    // TODO Auto-generated method stub
    
  }


  /* (non-Javadoc)
   * @see java.util.logging.Handler#flush()
   */
  @Override
  public void flush() {
    // TODO Auto-generated method stub
    
  }
  
  

}
