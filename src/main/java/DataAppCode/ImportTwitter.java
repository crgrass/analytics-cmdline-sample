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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class ImportTwitter {
  
  public static void updateTW(ArrayList<TWRecord> importData, Connection cnxn){

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
    
    DataAppTest.logger.log(Level.INFO, "Starting Twitter Import." + System.lineSeparator());

    

    ArrayList<String[]> data = null;
    try {
      
      //pull down data, write to file and overwrite any existing files
      try {
         DropBoxConnection.pullCSV("Twitter", DataAppTest.startDate, DataAppTest.endDate);
      } catch (DbxException exception) {
        exception.printStackTrace();
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    
    //Read csv and return raw data
    
    try {
      DataAppTest.logger.log(Level.INFO, "Reading Twitter File." + System.lineSeparator());
      data = CSVReaders.readLICsv("retrievedTwitter.csv");
    } catch (IOException e) {
      DataAppTest.logger.log(Level.SEVERE, "There was a problem reading the Twitter file."
    + System.lineSeparator(), e);
    }
    
    
    CSVReaders.removeHeader(data);
    
    DataAppTest.logger.log(Level.INFO, "Grouping Data by Source, Medium and Campaign." 
    + System.lineSeparator());
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupTwitterRawData(data);
    
    
    DataAppTest.logger.log(Level.INFO, "Aggregating Twitter Data." + System.lineSeparator());
    ArrayList<TWRecord> acquisitionData = TWRecord.aggregate(groupedData, DataAppTest.startDate,
        DataAppTest.endDate);
    
    
    DataAppTest.logger.log(Level.INFO, "Removing all records with 0 Impressions." + System.lineSeparator());
    acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);
    
    //Data is now aggregated and ready for matching
    
    String startDate = guiCode.DataAppTest.startDate.toString();
    String endDate = guiCode.DataAppTest.endDate.toString();
    String[] testDates = {startDate,endDate};
    
    DataAppTest.logger.log(Level.INFO, "Connecting to Google Analytics API for "
        + "Behavior metrics." + System.lineSeparator());
    GaData behaviorResults = GACall.main(args,testDates,7);
    
    //match behavior and acquisition data
    DataAppTest.logger.log(Level.INFO, "Matching Acquisition Metrics to "
        + "their respective behavior metrics." + System.lineSeparator());
    importUtils.matchTWBehaviorAcq(acquisitionData, behaviorResults);
    
    //Establish Connection
    Connection cnx = null;
    DataAppTest.logger.log(Level.INFO, "Connecting to MySQL database.");
    try {
      cnx = DatabaseUtils.getGoogleCloudTestDBConnection();
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE, "There was an error establishing connection to the database.",
          e);
    }

    //execute query
    try{
      updateTW(acquisitionData,cnx);
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE, "There was an error executing the Twitter Query.",
          e);
    }


    DataAppTest.importActivity.reset();


  } finally {
    
  }

}

}