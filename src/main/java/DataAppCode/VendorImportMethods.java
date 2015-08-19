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

import com.dropbox.core.DbxException;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.samples.analytics.cmdline.GACall;

import guiCode.DataAppTest;
import guiCode.OutputMessages;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
  
  
  //TODO: the String[] args does not serve any purpose except to initialize a required
  //parameter for the main method. This was necessary when main methods were called to
  //import vendor data but will not be necessary in the future. The last step to remove
  //this parameter is to move the GACall main code into a different method that
  //does not require this parameter.
  
  public static void importFacebook(String[] args, LocalDate sDate, LocalDate eDate) {
//    guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("Facebook"));
    
    ArrayList<String[]> data = null;
    try {
      
      //pull down data, write to file and overwrite any existing files
      try {
         DropBoxConnection.pullCSV("Facebook", sDate); //TODO: These
      } catch (DbxException exception) {
        exception.printStackTrace();
      } catch (IOException exception) {
        exception.printStackTrace();
      }
      
    
  //Read csv and return raw data
    System.out.println("Reading Facebook File...\n");
    data = CSVReaders.readCsv("retrievedFacebook.csv");
    CSVReaders.removeHeader(data);
//    CSVReaders.removeTail(data); //This likely deletes pertinent info
    System.out.println("Facebook File Read Complete.\n");
    
    System.out.println("Grouping Data by Source, Medium, Campaign and Placement...\n");
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupFacebookRawData(data);
    System.out.println("Grouping Complete.\n");
    
    System.out.println("Aggregating Facebook Data...\n");
    ArrayList<FBRecord> acquisitionData = FBRecord.aggregate(groupedData, sDate, eDate);
    System.out.println("Aggregation Complete.\n");
   
    System.out.println("Removing all records with 0 Impressions.\n");
    acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);
    
    //Data is now aggregated and ready for matching
    
    String startDate = sDate.toString();
    String endDate = eDate.toString();
    String[] testDates = {startDate,endDate};
    
    System.out.println("Connecting to Google Analytics API for "
        + "Behavior metrics\n");
    System.out.println("Google Analytics API messages below: \n");
    GaData behaviorResults = GACall.main(args,testDates,6);
    System.out.println("\nGoogle Analytics API Request Complete.\n");
    
    //match behavior and acquisition data
    System.out.println("Matching Acquisition Metrics to their respective behavior metrics...\n");
    importUtils.matchBehaviorAcq(acquisitionData, behaviorResults);
    System.out.println("Matching Complete.\n");
    
    //Establish Connection
    Connection cnx = null;
    try {
//      cnx = DatabaseUtils.getTestDBConnection();
      cnx = DatabaseUtils.getGoogleCloudTestDBConnection();
      System.out.println("Database Connection Successful\n");
    } catch (Exception e) {
      System.out.println("There was an error establishing connection to the database");
      System.out.println(e.getMessage());
    }
       
    
  //execute query
    try{
      //TODO: Move this query to FB Record enabling the eventual removal of the import
      //FB record
      ImportFB.updateFB(acquisitionData,cnx);
    } catch (SQLException e) {
    System.out.println(e.getMessage());  
    }
    
//    guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));

//    DataAppTest.importActivity.reset();
    
//    guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("Facebook"));
  } finally {
    
  }

}

  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
