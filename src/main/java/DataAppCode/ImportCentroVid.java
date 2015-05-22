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
public class ImportCentroVid {
  
  public static void updateCentroVid(ArrayList<VidRecord> importData, Connection cnxn)
      throws SQLException {

    PreparedStatement updateCentroVid = null;

    String tblName = "DATESTtblVideoMetrics";
    //These fields are out of order
    String fields = "(startDate,endDate,source,medium,componentName,clicks,"
        + "impressions,YTViews,allCTR,averageCPC,averageCPM,spend,totalConversions,pcConversions,piConversions,"
        + "visits,pagesPerVisit,averageDuration,"
        + "percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertQuery = "INSERT INTO " + tblName + fields + " VALUES" + parameters;

    try {
      cnxn.setAutoCommit(false);

      //Need to loop through Hash Map
      for(VidRecord currRec : importData) {
        updateCentroVid = cnxn.prepareStatement(insertQuery);
        
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
   
        updateCentroVid.setDate(1,sqlFormatStartDate);
        updateCentroVid.setDate(2,sqlFormatEndDate);
        updateCentroVid.setString(3,currRec.getSource());
        updateCentroVid.setString(4,currRec.getMedium());
        updateCentroVid.setString(5,currRec.getCampaign());
        updateCentroVid.setInt(6,currRec.getClicks());
        updateCentroVid.setInt(7,currRec.getImpressions());
        updateCentroVid.setInt(8,currRec.getYTViews());
        updateCentroVid.setFloat(9,currRec.getCTR());
        updateCentroVid.setFloat(10,currRec.getAvgCPC());
        updateCentroVid.setFloat(11, currRec.getAvgCPM());
        updateCentroVid.setFloat(12,currRec.getTotalConversions());
        updateCentroVid.setFloat(13,currRec.getPCConversions());
        updateCentroVid.setFloat(14,currRec.getPIConversions());
        updateCentroVid.setFloat(15,currRec.getSpend());
        updateCentroVid.setInt(16,currRec.getVisits());
        updateCentroVid.setFloat(17,currRec.getPagesPerVisit());
        updateCentroVid.setFloat(18,currRec.getAvgDuration());
        updateCentroVid.setFloat(19,currRec.getPercentNewVisits());
        updateCentroVid.setFloat(20,currRec.getBounceRate());
        updateCentroVid.setBoolean(21,currRec.getPartialWeek());
        updateCentroVid.setInt(22,currRec.getDaysActive());

        updateCentroVid.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }//end of catch

  } // end of update adwords

  
  public static void main(String[] args) {
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("Centro Video"));
    
    Map<String,String> filePaths = FilePathBuilder.buildFilePathMap();
    
    System.out.println("Reading Centro Video File...\n");
    ArrayList<String[]> data = CSVReaders.readCsv(filePaths.get("Centro Video Display"));
    CSVReaders.removeHeader(data);
    CSVReaders.removeTail(data); //If data is missing this may be the reason why
    System.out.println("Centro Video File Read Complete.\n");
    
    System.out.println("Grouping Data by Source, Medium and Campaign...\n");
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupCentroRawData(data);
    System.out.println("Grouping Complete.\n");
    
    System.out.println("Aggregating Centro Video Data...\n");
    ArrayList<VidRecord> acquisitionData = VidRecord.aggregate(groupedData);
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
    GaData behaviorResults = GACall.main(args,testDates,3);
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
      updateCentroVid(acquisitionData,cnx);
    } catch (SQLException e) {
    System.out.println(e.getMessage()); 
    String error = e.getMessage();
    
    }
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));

    DataAppTest.importActivity.reset();

    guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("Centro Video"));

  }

}