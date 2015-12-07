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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class ImportDD {
  
//TODO: Doc this
public static void printGroupedData(HashMap<GroupID, ArrayList<String[]>> grouped) {
  Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = grouped.entrySet().iterator();
  while (it.hasNext()) {
    Map.Entry<GroupID, ArrayList<String[]>> pairs = it.next();
    System.out.println(pairs.getKey());
  }//end of record iteration
}
  
  
  
  /*
   * PreCondition: Raw data has already been processed by the groupCentroRawData method located in
   * importUtils.
   */

 //TODO: This method can likely be modified to aggregate all Centro data at once. Currently each medium
 //within centro has it's own aggregate method located within it's respective Import class
  public static ArrayList<DDRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData, LocalDate sDate,
      LocalDate eDate) {
    
    //Iterate through HashMap and place only digital display entries into a new onlyDD HashMap
    HashMap<GroupID,ArrayList<String[]>> onlyDD = new HashMap<GroupID,ArrayList<String[]>>();
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> itr = rawData.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = itr.next();
      GroupID currID = pairs.getKey();
      ArrayList<String[]> currArray = pairs.getValue();
      //This is the sorting that should be removed
      if (currID.getMedium().equals("Display")) {
        onlyDD.put(currID, currArray);
      }//end of if
    }//end of while
    
    //Create final returned arrayList
    ArrayList<DDRecord> DDRecordCollection = new ArrayList<DDRecord>();

    //Loop through hash map aggregating values
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = onlyDD.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<GroupID,ArrayList<String[]>> pairs = it.next();
      ArrayList<String[]> currList = pairs.getValue();

      //Metrics are aggregated here
      Integer totalClicks = 0;
      Integer totalImpressions = 0;
      Float totalSpend = 0.0f;
      Integer totalConversions = 0;
      Integer pcConversions = 0;
      Integer piConversions = 0;


      for (String[] row : currList) {
        totalImpressions += Integer.parseInt(row[9]);

        //Index 12: Clicks
        totalClicks += Integer.parseInt(row[8]);
        totalSpend += Float.parseFloat(row[14]);
        totalConversions += Integer.parseInt(row[15]);
        pcConversions += Integer.parseInt(row[16]);
        piConversions += Integer.parseInt(row[17]);
      }// end of outer loop

      Float aggCTR = (float)totalClicks/(float)totalImpressions;
      Float aggCPC = totalSpend/(float)totalClicks;
      Float kImpressions = (float)totalImpressions/1000;
      Float aggCPM = totalSpend/kImpressions;
      //TODOL System.out.println("Ensure this cpm calc is correct: " + aggCPM);

      //Dates need to come from one common source

      String[] dateArray = {sDate.toString(),eDate.toString()};
      
      GroupID currID = pairs.getKey();

      DDRecord rec = new DDRecord(dateArray,currID.getSource(),currID.getMedium(),currID.getCampaign(),currID.getSource(),//<- This is network
          currID.getAdContent(),totalClicks,totalImpressions,aggCTR,aggCPC,aggCPM,totalSpend, totalConversions,pcConversions,piConversions);
      DDRecordCollection.add(rec);
    }

    return DDRecordCollection;
  }
  
  public static void updateCentroDD(ArrayList<DDRecord> importData, Connection cnxn){

    PreparedStatement updateCentroDD = null;

    String tblName = "DATESTtblDigitalDisplayMetrics";
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
      for(DDRecord currRec : importData) {
        updateCentroDD = cnxn.prepareStatement(insertQuery);
        
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
   
        updateCentroDD.setDate(1,sqlFormatStartDate);
        updateCentroDD.setDate(2,sqlFormatEndDate);
        updateCentroDD.setString(3,currRec.getSource());
        updateCentroDD.setString(4,currRec.getMedium());
        updateCentroDD.setString(5,currRec.getCampaign());
        updateCentroDD.setString(6, currRec.getAdContent());
        updateCentroDD.setInt(7,currRec.getClicks());
        updateCentroDD.setInt(8,currRec.getImpressions());

        //MySQL databases will not accept NaN or Infinity as a float value
        if (Double.isNaN(currRec.getCTR()) || 
            currRec.getCTR() == Double.POSITIVE_INFINITY) {
          currRec.setCTR(-1.0f);
        }
        updateCentroDD.setFloat(9,currRec.getCTR());
        
        //MySQL databases will not accept NaN or Infinity as a float value
        if (Double.isNaN(currRec.getAvgCPC())|| 
            currRec.getAvgCPC() == Double.POSITIVE_INFINITY) {
          currRec.setAvgCPC(-1.0f);
        }
        updateCentroDD.setFloat(10,currRec.getAvgCPC());
        
      //MySQL databases will not accept NaN or Infinity as a float value
        if (Double.isNaN(currRec.getAvgCPM()) || 
            currRec.getAvgCPM() == Double.POSITIVE_INFINITY) {
          currRec.setAvgCPM(-1.0f);
        }
        
        updateCentroDD.setFloat(11, currRec.getAvgCPM());
        updateCentroDD.setFloat(12, currRec.getSpend());
        updateCentroDD.setFloat(13,currRec.getTotalConversions());
        updateCentroDD.setFloat(14,currRec.getPCConversions());
        updateCentroDD.setFloat(15,currRec.getPIConversions());
        updateCentroDD.setInt(16,currRec.getVisits());
        updateCentroDD.setFloat(17,currRec.getPagesPerVisit());
        updateCentroDD.setFloat(18,currRec.getAvgDuration());
        updateCentroDD.setFloat(19,currRec.getPercentNewVisits());
        updateCentroDD.setFloat(20,currRec.getBounceRate());
        updateCentroDD.setBoolean(21,currRec.getPartialWeek());
        updateCentroDD.setInt(22,currRec.getDaysActive());

        updateCentroDD.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());
    }//end of catch
  } // end of updateCentroDD


  public static void main(String[] args) {
    
    

    ArrayList<String[]> data = null;
    try {
      
      //pull down data from dropbox, write to file and overwrite any data files
      try {
         DropBoxConnection.pullCSV("Centro Digital Display",DataAppTest.startDate,
             DataAppTest.endDate);
      } catch (DbxException e) {
        DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());
      } catch (IOException e) {
        DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());
      }
  
    //Read csv generated and return raw data
    DataAppTest.logger.log(Level.INFO, "Reading Centro Display File." + System.lineSeparator());
    data = CSVReaders.readCsv("retrievedCentro Digital Display.csv");

    
    CSVReaders.removeHeader(data);
    CSVReaders.removeInvalidDates(data, "Centro", DataAppTest.startDate);
    
    //Now that we have the raw data in ArrayList<String[]> form
    //We need to group appropriately into a hashmap
    //We will then iterate through the HashMap and aggregate each entry
    
    DataAppTest.logger.log(Level.INFO, "Grouping Data by Source, Medium, Campaign and AdContent." + System.lineSeparator());
    
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupCentroRawData(data, DataAppTest.startDate);

    
    DataAppTest.logger.log(Level.INFO, "Aggregating Centro Digital Display Data." + System.lineSeparator());
    ArrayList<DDRecord> acquisitionData = aggregate(groupedData, DataAppTest.startDate, DataAppTest.endDate);
    
    DataAppTest.logger.log(Level.INFO, "The number of DD records for import is: " + acquisitionData.size() + System.lineSeparator());

    DataAppTest.logger.log(Level.INFO, "Removing all records with 0 Impressions." + System.lineSeparator());
    acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);
  
    //Data is now aggregated and ready for matching
    
    String startDate = guiCode.DataAppTest.startDate.toString();
    String endDate = guiCode.DataAppTest.endDate.toString();
    String[] testDates = {startDate,endDate};
    
    DataAppTest.logger.log(Level.INFO, "Connecting to Google Analytics API for "
        + "Behavior metrics." + System.lineSeparator());
    
    DataAppTest.logger.log(Level.INFO, "Google Analytics API messages below:" + System.lineSeparator());
    
    GaData behaviorResults = GACall.main(args,testDates,2);
    
    //match aggregated acquisition data and behavior data from Google Analytics
    DataAppTest.logger.log(Level.INFO, "Matching Acquisition Metrics to their respective behavior metrics." + System.lineSeparator());
    importUtils.matchBehaviorAcq(acquisitionData, behaviorResults);
    
    //Establish database connection
    Connection cnx = null;
    try {
      cnx = DatabaseUtils.getGoogleCloudDBConnection();
      DataAppTest.logger.log(Level.INFO, "Database Connection Successful." + System.lineSeparator());
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE, "There was an error establishing connection to the database." + System.lineSeparator());
      DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());
    }
    
    //execute query
    try{
      updateCentroDD(acquisitionData,cnx);
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE, "There was an error executing the digital display query." + System.lineSeparator());
      DataAppTest.logger.log(Level.SEVERE, e.getMessage() + System.lineSeparator());  
    }
    
    DataAppTest.importActivity.reset();


  } finally {
    
  }

}
}
