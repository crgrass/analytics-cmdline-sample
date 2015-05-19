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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class InputStreamTest {


  
  
  public static void main(String[] args) {
    
    //create a stream to hold the output
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
//    //Save the old System.out
//    PrintStream old = System.out;
//    //Tell java to use your special stream
//    System.setOut(ps);
//    System.out.println("Is this captured?");
//    //What does this do?
//    System.out.flush();
//    System.setOut(old);
//    //Grab output
////    System.out.println(baos.toString());
    
    ps.println("Hello");
    
    System.out.println("GoodBye");
    System.out.println(baos.toString());

  }

}
