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
public class ImportMob {

  /**
   * @param args
   */
  
  public static void updateCentroMob(ArrayList<MobRecord> importData, Connection cnxn) {

    PreparedStatement updateCentroMob = null;

    String tblName = "DATESTtblMobileMetrics";
    //These fields are out of order
    String fields = "(startDate,endDate,source,medium,componentName,adContent,clicks,"
        + "impressions,allCTR,averageCPC,averageCPM,spend,totalConversions,pcConversions,piConversions,"
        + "visits,pagesPerVisit,averageDuration,"
        + "percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertQuery = "INSERT INTO " + tblName + fields + " VALUES" + parameters;

    try {
      cnxn.setAutoCommit(false);

      //Need to loop through Hash Map
      for(MobRecord currRec : importData) {
        updateCentroMob = cnxn.prepareStatement(insertQuery);
        
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
   
        updateCentroMob.setDate(1,sqlFormatStartDate);
        updateCentroMob.setDate(2,sqlFormatEndDate);
        updateCentroMob.setString(3,currRec.getSource());
        updateCentroMob.setString(4,currRec.getMedium());
        updateCentroMob.setString(5,currRec.getCampaign());
        updateCentroMob.setString(6, currRec.getAdContent());
        updateCentroMob.setInt(7,currRec.getClicks());
        updateCentroMob.setInt(8,currRec.getImpressions());
       
        updateCentroMob.setFloat(9,currRec.getCTR());


        updateCentroMob.setFloat(10,currRec.getAvgCPC());

        
        updateCentroMob.setFloat(11, currRec.getAvgCPM());
        updateCentroMob.setFloat(12,currRec.getSpend());

        updateCentroMob.setFloat(13,currRec.getTotalConversions());
        updateCentroMob.setFloat(14,currRec.getPCConversions());
        updateCentroMob.setFloat(15,currRec.getPIConversions());
        updateCentroMob.setInt(16,currRec.getVisits());
        updateCentroMob.setFloat(17,currRec.getPagesPerVisit());
        updateCentroMob.setFloat(18,currRec.getAvgDuration());
        updateCentroMob.setFloat(19,currRec.getPercentNewVisits());
        updateCentroMob.setFloat(20,currRec.getBounceRate());
        updateCentroMob.setBoolean(21,currRec.getPartialWeek());
        updateCentroMob.setInt(22,currRec.getDaysActive());
        

        updateCentroMob.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());
    }//end of catch

  } // end of update adwords
  public static void main(String[] args) {
    
    
    ArrayList<String[]> data = null;
    
    //pull down data, write to file and overwrite any existing files
    try {

      DropBoxConnection.pullCSV("Centro Mobile Display", DataAppTest.startDate,DataAppTest.endDate);

    } catch (DbxException e) {
      DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());
    } catch (IOException e) {
      DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());
    }
    
    DataAppTest.logger.log(Level.INFO, "Reading Centro Mobile File." + System.lineSeparator());
    data = CSVReaders.readCsv("retrievedCentro Mobile Display.csv");
    
    CSVReaders.removeHeader(data);
    CSVReaders.removeInvalidDates(data, "Centro", DataAppTest.startDate);
    
    
    
    DataAppTest.logger.log(Level.INFO, "Grouping Data by Source, Medium and Campaign." + System.lineSeparator());
    
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupCentroRawData(data, guiCode.DataAppTest.startDate);
    
    DataAppTest.logger.log(Level.INFO, "Aggregating Centro Mobile Data." + System.lineSeparator());
    
    ArrayList<MobRecord> acquisitionData = MobRecord.aggregate(groupedData, DataAppTest.startDate,
        DataAppTest.endDate);
    
    DataAppTest.logger.log(Level.INFO, "Removing all records with 0 Impressions." + System.lineSeparator());
    
    acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);
    
    //Data is now aggregated and ready for matching
    
    String startDate = guiCode.DataAppTest.startDate.toString();
    String endDate = guiCode.DataAppTest.endDate.toString();
    String[] testDates = {startDate,endDate};
    
    DataAppTest.logger.log(Level.INFO, "Connecting to Google Analytics API for "
        + "behavior metrics." + System.lineSeparator());
    
    DataAppTest.logger.log(Level.INFO, "Google Analytics API messages below:" + System.lineSeparator());

    GaData behaviorResults = GACall.main(args,testDates,4);
    
    DataAppTest.logger.log(Level.INFO, "Matching Acquisition Metrics to their respective behavior metrics." + System.lineSeparator());
    
    //match behavior and acquisition data
    importUtils.matchBehaviorAcq(acquisitionData, behaviorResults);
    
    //Establish Connection
    Connection cnx = null;
    try {
//      cnx = DatabaseUtils.getTestDBConnection();
      cnx = DatabaseUtils.getGoogleCloudDBConnection();
      DataAppTest.logger.log(Level.INFO, "Database Connection Successful." + System.lineSeparator());
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE, "There was an error establishing connection to the database." + System.lineSeparator());
      System.out.println(e.getMessage());
    }
    
  //execute query
    try{
      updateCentroMob(acquisitionData,cnx);
    } catch (Exception e) {
    System.out.println(e.getMessage());  
    }
    

    DataAppTest.importActivity.reset();


  }//end of main

}//end of ImportMob
