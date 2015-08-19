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

import com.google.api.services.analytics.model.GaData;
import com.google.api.services.samples.analytics.cmdline.GACall;

import guiCode.DataAppTest;
import guiCode.OutputMessages;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * @author cgrass@google.com (Chris Grass)
 *This class is designed to replace all of the
 *individual import[Vendormethods]. This class will house one import method for each vendor additional methods
 *that are located in the current import[VendorMethods] should be moved either to the individual [vendor]Record
 *or a separate class if deemed appropriate
 */
public class VendorImportMethods {


  

  //TODO: the String[] args does not serve any purpose except to initialize a required
  //parameter for the main method. This was necessary when main methods were called to
  //import vendor data but will not be necessary in the future. The last step to remove
  //this parameter is to move the GACall main code into a different method that
  //does not require this parameter.
  
  public static void importAdwords(String[] args, LocalDate sDate, LocalDate eDate) {
    
      
      //guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("Google Adwords"));
      
      /*
       * GA Data is pulled in below this point
       * Note: Adding dimensions or metrics shifts the indexes and can lead to 
       * incorrect metrics populating in the object
       */
      
      //TODO: Incorporate Logging Functionality
      
      String startDate = sDate.toString();
      String endDate = eDate.toString();
      String[] dateArray = {startDate,endDate};
      
      System.out.println("Connecting to Google Analytics API for Acquistion"
          + " and Behavior metrics\n");
      System.out.println("Google Analytics API messages below: \n");
      GaData acquisitionResults = GACall.main(args, dateArray,0);
      System.out.println("\nGoogle Analytics API Request Complete.\n");
      
      int totalResults = acquisitionResults.getTotalResults();
      System.out.println(totalResults + " results returned.");

      
      //This loadResults should likely be moved to the vendorObject which will allow the elimination
      //of the importAdwords class
      HashMap<GroupID,AdwordsRecord> groupedAdwordsData = ImportAdwords.loadResults(acquisitionResults, dateArray);
      
      //Adwords data is now grouped and should be needs to be added to the data base
      
      //turn this into a method
      //connectToTestDatabase
      Connection cnx = null;
      
      System.out.println("Connecting to database...\n");
      try {
//      cnx = DatabaseUtils.getTestDBConnection();
        cnx = DatabaseUtils.getGoogleCloudTestDBConnection();
        System.out.println("Database Connection Successful\n");
      } catch (Exception e) {
        System.out.println("There was an error establishing connection to the database\n");
        System.out.println(e.getMessage());
      }
      
      try{
        ImportAdwords.updateAdwords(groupedAdwordsData,cnx);
      } catch (SQLException e) {
      System.out.println(e.getMessage());
      //guiCode.DataAppTest.outputDisplay.write(OutputMessages.errorMessage(e.getMessage()));
      }
      

      //guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));

      //DataAppTest.importActivity.reset(); 
      
      //guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("Google Adwords"));

  }
  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
