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

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ListenerTest {
  private BooleanProperty filesReady = new SimpleBooleanProperty(false);
  
//Define a getter for the property's value
  public final boolean getFilesReady(){return filesReady.get();}

  // Define a setter for the property's value
  public final void setFilesReady(boolean value){filesReady.set(value);}

   // Define a getter for the property itself
  public BooleanProperty filesReadyProperty() {return filesReady;}
  
  
  
  
  
  public static void main(String[] args) {
    
    ListenerTest test = new ListenerTest();
    
    test.filesReadyProperty().addListener(new ChangeListener() {
      @Override public void changed(ObservableValue o, Object oldVal,
                                    Object newVal) {
        System.out.println("Value Changed");
      }
    });
    
    test.setFilesReady(true);

  }

}
