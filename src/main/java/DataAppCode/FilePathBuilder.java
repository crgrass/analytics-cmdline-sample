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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class FilePathBuilder {

  
  
  /*
   * The buildFilePathMapDropBox loads a map with a key for each
   * Vendor and that vendors dropbox directory housing import
   * files as the key.
   */

  public static Map<String,String> buildFilePathMapDropBox(LocalDate sDate) {
  
  Map<String,String> fileLocations = new HashMap<String,String>();
  
  //Note: Adwords does not have a vendor file as all data is pulled from the API
  fileLocations.put("Google Adwords", 
      "/Vendor Data - Import and Archive");
  
  fileLocations.put("Centro Digital Display", 
      "/Vendor Data - Import and Archive/Centro/FY15/FY15 Centro Reporting - Weekly Drop Folder");
  
  fileLocations.put("Centro Mobile Display", 
      "/Vendor Data - Import and Archive/Centro/FY15/FY15 Centro Reporting - Weekly Drop Folder");
  
  fileLocations.put("Centro Video Display", 
      "/Vendor Data - Import and Archive/Centro/FY15/FY15 Centro Reporting - Weekly Drop Folder");
  
  fileLocations.put("Centro Rich Media", 
      "/Vendor Data - Import and Archive/Centro/FY15/FY15 Centro Reporting - Weekly Drop Folder");
  
  fileLocations.put("Facebook", "/Vendor Data - Import and Archive/Facebook/FY15/FY15 Facebook Reporting - Weekly Drop Folder");
  
  fileLocations.put("Twitter", "/Vendor Data - Import and Archive/Twitter/FY15/FY15 Twitter Reporting - Weekly Drop Folder");
  
  fileLocations.put("LinkedIn", "/Vendor Data - Import and Archive/LinkedIn/FY15/FY15 LinkedIn Reporting - Weekly Drop Folder");
  
  fileLocations.put("DoubleClick", 
      "/Vendor Data - Import and Archive/DoubleClick/FY16/FY16 DoubleClick Reporting - Weekly Drop Folder");
  
  
  /*
   * The following code block determines what the filename of the correct import file is.
   * This is determined based off of the naming convention that each file will be named
   * [Vendor]_[YYYY_MM_DD].csv. 
   */
  
  Iterator<Entry<String, String>> itr = fileLocations.entrySet().iterator();
  while (itr.hasNext()) {
    Map.Entry<String,String> pairs = itr.next();
    String currVendor = pairs.getKey();
    String currFilePath = pairs.getValue();
    if (!currVendor.contains("Centro")) {
      currFilePath = currFilePath + "//"+ currVendor + "_" + sDate.toString() +".csv";

    } else {
      //TODO: Eliminate the need for a special case here.
      //A special case is required for centro as there are four different mediums that come from this
      //vendor

      currFilePath = currFilePath + "//Centro"+ "_" + sDate.toString() +".csv";
    }
    
    fileLocations.put(currVendor,currFilePath); // replace existing entries with updated entrieds
  }//end of while
  
  return fileLocations;
}

  
  public static void main(String[] args) {

  }

}
