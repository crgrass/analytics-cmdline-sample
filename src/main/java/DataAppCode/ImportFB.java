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
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import guiCode.DataAppTest;
import guiCode.OutputMessages;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class ImportFB {
  
  public static void updateFB(ArrayList<FBRecord> importData, Connection cnxn)
      throws SQLException {

    PreparedStatement updateFB = null;

    String tblName = "DATESTtblFacebookMetrics";
    //These fields are out of order
    String fields = "(startDate,endDate,componentName,placement,device,source,medium,reach,frequency,clicks,"
        + "uniqueClicks,websiteClicks,impressions,CTR,uniqueCTR,averageCPC,averageCPM,CP1KR,actions,PTA,spend,"
        + "likes,visits,pagesPerVisit,averageDuration,percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertQuery = "INSERT INTO " + tblName + fields + " VALUES" + parameters;

    try {
      cnxn.setAutoCommit(false);

      //Need to loop through Hash Map
      for(FBRecord currRec : importData) {
        updateFB = cnxn.prepareStatement(insertQuery);
        
        Date sDate = null;
        Date eDate = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
          sDate = sdf.parse(currRec.getStartDate());
          eDate = sdf.parse(currRec.getEndDate());
        } catch (ParseException e) {
          e.printStackTrace();
        } catch (java.text.ParseException exception) {
          // TODO Auto-generated catch block
          exception.printStackTrace();
        }
        
        java.sql.Date sqlFormatStartDate = new java.sql.Date(sDate.getTime());
        java.sql.Date sqlFormatEndDate = new java.sql.Date(eDate.getTime());
   
        updateFB.setDate(1,sqlFormatStartDate);
        updateFB.setDate(2,sqlFormatEndDate);
        updateFB.setString(3,currRec.getCampaign());
        updateFB.setString(4,currRec.getPlacement());
        updateFB.setString(5,currRec.getDevice());
        updateFB.setString(6,currRec.getSource());
        updateFB.setString(7,currRec.getMedium());
        updateFB.setInt(8,currRec.getReach());
        updateFB.setFloat(9,currRec.getFrequency());
        updateFB.setFloat(10,currRec.getClicks());
        updateFB.setFloat(11, currRec.getUniqueClicks());
        updateFB.setFloat(12,currRec.getWebsiteClicks());
        updateFB.setFloat(13,currRec.getImpressions());
        updateFB.setFloat(14,currRec.getCTR());
        updateFB.setFloat(15,currRec.getUniqueCTR());
        updateFB.setFloat(16,currRec.getAvgCPC());
        updateFB.setFloat(17,currRec.getAvgCPM());
        updateFB.setFloat(18,currRec.getCP1KR());
        updateFB.setFloat(19,currRec.getActions());
        updateFB.setInt(20,currRec.getPTA());
        updateFB.setFloat(21,currRec.getSpend());
        updateFB.setInt(22,currRec.getLikes());
        updateFB.setInt(23,currRec.getVisits());
        updateFB.setFloat(24,currRec.getPagesPerVisit());
        updateFB.setFloat(25,currRec.getAvgDuration());
        updateFB.setFloat(26,currRec.getPercentNewVisits());
        updateFB.setFloat(27,currRec.getBounceRate());
        updateFB.setBoolean(28,currRec.getPartialWeek());
        updateFB.setInt(29,currRec.getDaysActive());

        updateFB.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }//end of catch

  } // end of update adwords

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("Facebook"));
    
    Map<String,String> filePaths = FilePathBuilder.buildFilePathMapDropBox(); //contains all vendors and their respective import directory paths
    ArrayList<String[]> data = null;
    try {
      
      //pull down data, write to file and overwrite any existing files
      try {
         DropBoxConnection.pullCSV("Facebook"); //TODO: These
      } catch (DbxException exception) {
        exception.printStackTrace();
      } catch (IOException exception) {
        exception.printStackTrace();
      }
      
    
  //Read csv and return raw data
    System.out.println("Reading Facebook File...\n");
    data = CSVReaders.readCsv("retrievedFacebook.csv");
    CSVReaders.removeHeader(data);
    CSVReaders.removeTail(data); //If data is missing this may be the reason why
    System.out.println("Facebook File Read Complete.\n");
    
    System.out.println("Grouping Data by Source, Medium, Campaign and Placement...\n");
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupFacebookRawData(data);
    System.out.println("Grouping Complete.\n");
    
    System.out.println("Aggregating Facebook Data...\n");
    ArrayList<FBRecord> acquisitionData = FBRecord.aggregate(groupedData);
    System.out.println("Aggregation Complete.\n");
   
    System.out.println("Removing all records with 0 Impressions.\n");
    acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);
    
    //Data is now aggregated and ready for matching
    
    String startDate = guiCode.DataAppTest.startDate.toString();
    String endDate = guiCode.DataAppTest.endDate.toString();
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
    
    
    for (FBRecord rec: acquisitionData) {
      System.out.println(rec + "\n");
    }
    
    
    
  //execute query
    try{
      updateFB(acquisitionData,cnx);
    } catch (SQLException e) {
    System.out.println(e.getMessage());  
    }
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));

    DataAppTest.importActivity.reset();
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("Facebook"));
  } finally {
    
  }

}
  
}
