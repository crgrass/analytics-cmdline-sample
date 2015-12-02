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

package DataAppCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */


/*
 * The correctDate method generates a map which contains
 * the relevant indexes that contain the dates in each
 * vendors import file.
 * 
 * Method originally located in CSVReaders
 */
public static boolean correctDate(String filePath) {
  Map<String,Integer[]> dateIndexes = new HashMap<String,Integer[]>();
  //If there are two integers in the array this indicates that both the
  //start and end dates are relevant
  dateIndexes.put("Google Adwords", new Integer[] {0,0});
  
  dateIndexes.put("Centro Digital Display", new Integer[] {0,1});
  
  dateIndexes.put("Centro Mobile Display", new Integer[] {0,1});
  
  dateIndexes.put("Centro Video Display", new Integer[] {0,1});
  
  dateIndexes.put("Centro Rich Media", new Integer[] {0,1});
  
  dateIndexes.put("Facebook", new Integer[] {0,1});
  
  dateIndexes.put("Twitter", new Integer[] {5});
  
  dateIndexes.put("LinkedIn", new Integer[] {0});
  
  return true;
}
public class safeKeeping {

}
