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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class FilePermissionsTest {
  
  public static void setStoreFilePermissions() {
    try {
      File file = new File("C:\\Users\\cgrass\\.store\\analytics_sample\\StoredCredential");
      
      if (file.exists()) {
        System.out.println("Is Execute allowed: " + file.canExecute());
        System.out.println("Is Write allowed: " + file.canWrite());
        System.out.println("Is Read allowed: " + file.canRead());
      } else {
        System.out.println("File not found");
      }
      
      System.out.println(file.setExecutable(false));
      System.out.println(file.setReadable(false));
      System.out.println(file.setWritable(false));
      
      System.out.println("Is Execute allow : " + file.canExecute());
      System.out.println("Is Write allow : " + file.canWrite());
      System.out.println("Is Read allow : " + file.canRead());
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
  public static void main(String[] args) {
    setStoreFilePermissions();

  }

}
