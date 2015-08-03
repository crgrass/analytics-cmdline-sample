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
public class ImportTwitter {
  
  public static void updateTW(ArrayList<TWRecord> importData, Connection cnxn)
      throws SQLException {

    PreparedStatement updateTW = null;

    String tblName = "DATESTtblTwitterMetrics";
    //These fields are out of order
    String fields = "(startDate,endDate,componentName,source,medium,TWclicks,clicks,impressions,"
        + "CTR,CPC,spend,engagements,billedEngagements,retweets,replies,follows,favorites,"
        + "cardEngagements,unfollows,engagementRate,CPE,visits,pagesPerVisit,averageDuration,"
        + "percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertQuery = "INSERT INTO " + tblName + fields + " VALUES" + parameters;

    try {
      cnxn.setAutoCommit(false);

      //Need to loop through Hash Map
      for(TWRecord currRec : importData) {
        updateTW = cnxn.prepareStatement(insertQuery);
        
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
   
        updateTW.setDate(1,sqlFormatStartDate);
        updateTW.setDate(2,sqlFormatEndDate);
        updateTW.setString(3,currRec.getCampaign());
        updateTW.setString(4,currRec.getSource());
        updateTW.setString(5,currRec.getMedium());
        updateTW.setInt(6,currRec.getTWclicks());
        updateTW.setInt(7,currRec.getLinkClicks());
        updateTW.setInt(8,currRec.getImpressions());
        updateTW.setFloat(9,currRec.getCTR());
        updateTW.setFloat(10,currRec.getCPC());
        updateTW.setFloat(11, currRec.getSpend());
        updateTW.setInt(12,currRec.getEngagements());
        updateTW.setInt(13,currRec.getBilledEngagements());
        updateTW.setInt(14,currRec.getRetweets());
        updateTW.setInt(15,currRec.getReplies());
        updateTW.setInt(16,currRec.getFollows());
        updateTW.setInt(17,currRec.getFavorites());
        updateTW.setInt(18,currRec.getCardEngagements());
        updateTW.setInt(19,currRec.getUnfollows());
        updateTW.setFloat(20,currRec.getEngagementRate());
        updateTW.setFloat(21,currRec.getCPE());
        updateTW.setInt(22,currRec.getVisits());
        updateTW.setFloat(23,currRec.getPagesPerVisit());
        updateTW.setFloat(24,currRec.getAvgDuration());
        updateTW.setFloat(25,currRec.getPercentNewVisits());
        updateTW.setFloat(26,currRec.getBounceRate());
        updateTW.setBoolean(27,currRec.getPartialWeek());
        updateTW.setInt(28,currRec.getDaysActive());

        updateTW.executeUpdate();
        cnxn.commit();
      }//end of loop
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }//end of catch

  } // end of updateTW


  public static void main(String[] args) {

    guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("Twitter"));
    
    Map<String,String> filePaths = FilePathBuilder.buildFilePathMapDropBox(); //contains all vendors and their respective import directory paths
    ArrayList<String[]> data = null;
    try {
      
      //pull down data, write to file and overwrite any existing files
      try {
         DropBoxConnection.pullCSV("Twitter");
      } catch (DbxException exception) {
        // TODO Auto-generated catch block
        exception.printStackTrace();
      } catch (IOException exception) {
        // TODO Auto-generated catch block
        exception.printStackTrace();
      }
    
    //Read csv and return raw data
    
    try {
      System.out.println("Reading Twitter File...\n");
      data = CSVReaders.readLICsv("retrievedTwitter.csv");
      System.out.println("Twitter File Read Complete.\n");
    } catch (IOException e) {
      System.out.println("There was a problem reading the Twitter File.");
      e.printStackTrace();
    }
    
    
    CSVReaders.removeHeader(data);
    
    System.out.println("Grouping Data by Source, Medium and Campaign...\n");
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupTwitterRawData(data);
    System.out.println("Grouping Complete.\n");
    
    System.out.println("Aggregating Twitter Data...\n");
    ArrayList<TWRecord> acquisitionData = TWRecord.aggregate(groupedData);
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
    GaData behaviorResults = GACall.main(args,testDates,7);
    System.out.println("\nGoogle Analytics API Request Complete.\n");
    
    //match behavior and acquisition data
    System.out.println("Matching Acquisition Metrics to their respective behavior metrics...\n");
    importUtils.matchTWBehaviorAcq(acquisitionData, behaviorResults);
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
      updateTW(acquisitionData,cnx);
    } catch (SQLException e) {
      System.out.println(e.getMessage());  

    }

    guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));

    DataAppTest.importActivity.reset();

    guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("Twitter"));

  } finally {
    
  }

}

}