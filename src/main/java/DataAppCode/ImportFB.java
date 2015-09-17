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
public class ImportFB {
  
  public static void updateFB(ArrayList<FBRecord> importData, Connection cnxn) {

    PreparedStatement updateFB = null;

    String tblName = "DATESTtblFacebookMetrics";
    //TODO: Put these fields in the proper order
    String fields = "(startDate,endDate,componentName,adContent, placement,device,source,medium,reach,frequency,clicks,"
        + "uniqueClicks,websiteClicks,impressions,CTR,uniqueCTR,averageCPC,averageCPM,CP1KR,actions,PTA,spend,"
        + "likes,visits,pagesPerVisit,averageDuration,percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
        updateFB.setString(4, currRec.getAdContent());
        updateFB.setString(5,currRec.getPlacement());
        updateFB.setString(6,currRec.getDevice());
        updateFB.setString(7,currRec.getSource());
        updateFB.setString(8,currRec.getMedium());
        updateFB.setInt(9,currRec.getReach());
        updateFB.setFloat(10,currRec.getFrequency());
        updateFB.setFloat(11,currRec.getClicks());
        updateFB.setFloat(12, currRec.getUniqueClicks());
        updateFB.setFloat(13,currRec.getWebsiteClicks());
        updateFB.setFloat(14,currRec.getImpressions());
        updateFB.setFloat(15,currRec.getCTR());
        updateFB.setFloat(16,currRec.getUniqueCTR());
        updateFB.setFloat(17,currRec.getAvgCPC());
        updateFB.setFloat(18,currRec.getAvgCPM());
        updateFB.setFloat(19,currRec.getCP1KR());
        updateFB.setFloat(20,currRec.getActions());
        updateFB.setInt(21,currRec.getPTA());
        updateFB.setFloat(22,currRec.getSpend());
        updateFB.setInt(23,currRec.getLikes());
        updateFB.setInt(24,currRec.getVisits());
        updateFB.setFloat(25,currRec.getPagesPerVisit());
        updateFB.setFloat(26,currRec.getAvgDuration());
        updateFB.setFloat(27,currRec.getPercentNewVisits());
        updateFB.setFloat(28,currRec.getBounceRate());
        updateFB.setBoolean(29,currRec.getPartialWeek());
        updateFB.setInt(30,currRec.getDaysActive());

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

    DataAppTest.logger.log(Level.INFO, "Beginning Facebook Import." + System.lineSeparator());

    ArrayList<String[]> data = null; //Holds raw data
    try {
      //pull down data form Dropbox, write to file and overwrite any existing files
      try {
        DataAppTest.logger.log(Level.INFO, "Pulling Facebook data from Dropbox." + System.lineSeparator()); 
        DropBoxConnection.pullCSV("Facebook", DataAppTest.startDate, DataAppTest.endDate);
      } catch (DbxException e) {
        DataAppTest.logger.log(Level.SEVERE, "There was a Database Exception.", e);
      } catch (IOException e) {
        DataAppTest.logger.log(Level.SEVERE, "There was an IO Exception.", e);
      }


      //Read the csv created from Dropbox file and return raw data
      DataAppTest.logger.log(Level.INFO, "Reading Facebook File." + System.lineSeparator());
      data = CSVReaders.readCsv("retrievedFacebook.csv");
      
      
      CSVReaders.removeHeader(data); //remove header row
      
      DataAppTest.logger.log(Level.INFO, "Grouping Facebook Raw Data by Source, Medium, "
          + "Campaign and Placement." + System.lineSeparator());
      HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupFacebookRawData(data);

      DataAppTest.logger.log(Level.INFO, "Aggregating Facebook Data." + System.lineSeparator());
      ArrayList<FBRecord> acquisitionData = FBRecord.aggregate(groupedData, DataAppTest.startDate,
          DataAppTest.endDate);

      DataAppTest.logger.log(Level.INFO, "Removing Facebook Records"
          + "with zero impressions." + System.lineSeparator());
      acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);

      //Data is now aggregated and ready for matching

      //TODO: Consolidate these lines of code once sDate is passed in as a parameter
      //and not called from the static variable in Data App Test
      String startDate = guiCode.DataAppTest.startDate.toString();
      String endDate = guiCode.DataAppTest.endDate.toString();
      String[] testDates = {startDate,endDate};

      DataAppTest.logger.log(Level.INFO, "Connecting to Google Analytics API"
          + "for Behavior Metrics." + System.lineSeparator());
      GaData behaviorResults = GACall.main(args,testDates,6);

      //match behavior and acquisition data
      DataAppTest.logger.log(Level.INFO, "Matching acquisition and behavior metrics." + System.lineSeparator());
      importUtils.matchBehaviorAcq(acquisitionData, behaviorResults);

      
      //Establish Database Connection
      DataAppTest.logger.log(Level.INFO, "Connecting to Facebook MySQL database." 
      + System.lineSeparator());
      Connection cnx = null;
      try {
        cnx = DatabaseUtils.getGoogleCloudTestDBConnection();
      } catch (Exception e) {
        DataAppTest.logger.log(Level.SEVERE, "There was an issue connecting to the database." 
      + System.lineSeparator(), e);
      }

      //execute query
      try{
        updateFB(acquisitionData,cnx);
      } catch (Exception e) {
        DataAppTest.logger.log(Level.SEVERE, "There was an issue executing the Facebook DB Query." 
            + System.lineSeparator(), e); 
      }


      DataAppTest.importActivity.reset();

    } finally {

    } //end of try

  }//end of main
  
}//end of class
