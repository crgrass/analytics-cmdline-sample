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
import java.util.ArrayList;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class MultiDateImport {
  
  
  //User Interface will need to be created added
  
  //User will need to select between providing a master file for verifying that all
  //files within the date range exists
  
  //Request user input to define first and last startDates (inclusive)
  //User input will initially be provided in the testing setup methods
  
  
  //Enumerate all start dates between the two
  public static ArrayList<LocalDate> generateStartDates(LocalDate begin, LocalDate end) {
    ArrayList<LocalDate> importDates = new ArrayList<LocalDate>();
    
    //add initial startDate to array
    importDates.add(begin);
    
    //While the currentDate does not equal end date add 7 days to the date and
    //add to arraylist
    LocalDate currDate = begin;
    while (!currDate.equals(end)) {
      currDate = currDate.plusDays(7);
      importDates.add(currDate);
    }
    
    
    return importDates;
  }
  
  
  
  //For each date run import script for the date selected
  public static void runMultiImport (ArrayList<LocalDate> importDates){
    for (LocalDate currDate : importDates) {
      VendorImportMethods.importAdwords(new String[] {}, currDate, currDate.plusDays(6));
    }
  }
  
  //Execution of the multImport must stop if there is an error
  //information must be provided to the user indicating how much data was imported

}
