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

//import guiCode.DataAppTest;
//import guiCode.OutputMessages;

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

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class ImportDCM {


public static void printGroupedData(HashMap<GroupID, ArrayList<String[]>> grouped) {
  Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = grouped.entrySet().iterator();
  while (it.hasNext()) {
    Map.Entry<GroupID, ArrayList<String[]>> pairs = it.next();
    ArrayList<String[]> val = pairs.getValue();
    System.out.println(pairs.getKey() + " : " + val.toString()); //this should trigger the too string method
  }//end of record iteration
}
  
  
  
  /*
   * PreCondition: Raw data is already grouped appropriately
   */

 //TODO: This method can likely be modified to work aggregate all Centro data at once.
  public static ArrayList<DDRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData, LocalDate sDate,
      LocalDate eDate, String medium) {

    System.out.println("Aggregating rows based on Source, Network, Campaign and AdContent...\n");
    
    
    //Iterate through HashMap and place only digital display entries into a new onlyDD HashMap
    HashMap<GroupID,ArrayList<String[]>> filteredByMedium = new HashMap<GroupID,ArrayList<String[]>>();
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> itr = rawData.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = itr.next();
      GroupID currID = pairs.getKey();
      ArrayList<String[]> currArray = pairs.getValue();
      
      if (currID.getMedium().equals(medium)) {
        filteredByMedium.put(currID, currArray);
      }//end of if
    }//end of while
    
    //Create final returned arrayList
    ArrayList<DDRecord> DDRecordCollection = new ArrayList<DDRecord>();

    //Loop through hash map aggregating values
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = filteredByMedium.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = it.next();
      ArrayList<String[]> currList = pairs.getValue();

      //Metrics are aggregated here
      Integer totalClicks = 0;
      Integer totalImpressions = 0;
      Float totalSpend = 0.0f;
      Integer totalConversions = 0;
      Integer pcConversions = 0;
      Integer piConversions = 0;

      //TODO:Discarding last row as a band aid
      //eventually need to stop importing last row

      for (String[] row : currList) {
        totalImpressions += Integer.parseInt(row[9]);
        totalClicks += Integer.parseInt(row[8]);
        totalSpend += Float.parseFloat(row[14]);
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
  
  public static void updateDCMDD(ArrayList<DDRecord> importData, Connection cnxn, String medium) {

    PreparedStatement updateDCMDD = null;

    String tblName = "";
    
    
    if (medium.equals("CrossPlatform") || medium.equals("Display")) {
      tblName = "DATESTtblDigitalDisplayMetrics";
    } else if (medium.equals( "Preroll")) {
      tblName = "DATESTtblVideoMetrics";
    } else if (medium.equals("Mobile")) {
      tblName = "DATESTtblMobileMetrics";
    } else {
      System.out.println("The correct table for " + medium + " could not be identified");
    }
    
    
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
        updateDCMDD = cnxn.prepareStatement(insertQuery);
        
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
   
        updateDCMDD.setDate(1,sqlFormatStartDate);
        updateDCMDD.setDate(2,sqlFormatEndDate);
        updateDCMDD.setString(3,currRec.getSource());
        updateDCMDD.setString(4,currRec.getMedium());
        updateDCMDD.setString(5,currRec.getCampaign());
        updateDCMDD.setString(6, currRec.getAdContent());
        updateDCMDD.setInt(7,currRec.getClicks());
        updateDCMDD.setInt(8,currRec.getImpressions());
        
        if (Double.isNaN(currRec.getCTR()) || 
            currRec.getCTR() == Double.POSITIVE_INFINITY) {
          currRec.setCTR(-1.0f);
          System.out.println("Converted special value");
        }
        updateDCMDD.setFloat(9,currRec.getCTR());
        
        
        
        //MySQL databases will not accept NaN or Infinity as a float value
        if (Double.isNaN(currRec.getAvgCPC())|| 
            currRec.getAvgCPC() == Double.POSITIVE_INFINITY) {
          currRec.setAvgCPC(-1.0f);
          System.out.println("Converted special value");
        }
        updateDCMDD.setFloat(10,currRec.getAvgCPC());
        
        
        if (Double.isNaN(currRec.getAvgCPM()) || 
            currRec.getAvgCPM() == Double.POSITIVE_INFINITY) {
          currRec.setAvgCPM(-1.0f);
        }
        
        updateDCMDD.setFloat(11, currRec.getAvgCPM());
        updateDCMDD.setFloat(12, currRec.getSpend());
        updateDCMDD.setFloat(13,currRec.getTotalConversions());
        updateDCMDD.setFloat(14,currRec.getPCConversions());
        updateDCMDD.setFloat(15,currRec.getPIConversions());
        updateDCMDD.setInt(16,currRec.getVisits());
        updateDCMDD.setFloat(17,currRec.getPagesPerVisit());
        updateDCMDD.setFloat(18,currRec.getAvgDuration());
        updateDCMDD.setFloat(19,currRec.getPercentNewVisits());
        updateDCMDD.setFloat(20,currRec.getBounceRate());
        updateDCMDD.setBoolean(21,currRec.getPartialWeek());
        updateDCMDD.setInt(22,currRec.getDaysActive());

        updateDCMDD.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }//end of catch

  } // end of update adwords
  
  
  public static void importDCM(String[] args, LocalDate sDate, LocalDate eDate, String medium) {
    
    ArrayList<String[]> data = null;
    try {
      
      //pull down data from dropbox, write to file and overwrite any data files
      try {
        //TODO: Ensure this filpath is added to the method 
        DropBoxConnection.pullCSV("DoubleClick", sDate, eDate);
      } catch (DbxException exception) {
        exception.printStackTrace();
      } catch (IOException exception) {
        exception.printStackTrace();
      }
  
    //Read csv generated and return raw data
    System.out.println("Reading Double Click Display File... ");
    data = CSVReaders.readCsv("retrievedDoubleClick.csv");

    
    CSVReaders.formatDCMData(data);
    CSVReaders.removeInvalidDates(data, "DoubleClick", sDate);
    
    System.out.print("Complete.\n");
    
    //Now that we have the raw data in ArrayList<String[]> form
    //We need to group appropriately into a hashmap
    //We will then iterate through the HashMap and aggregate each entry
    
    System.out.println("Grouping Data by Source, Medium, Campaign and AdContent... ");
    //TODO: Create grouping method
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupDCMRawData(data);
    System.out.print("Complete.\n");

    
    System.out.println("Aggregating DoubleClick Digital Display Data... ");
    //TODO: Create Aggregate method
    ArrayList<DDRecord> acquisitionData = aggregate(groupedData, sDate, eDate, medium);
    System.out.print("Complete.\n");
    
    System.out.println("The number of " + medium + " records for import is: " + 
    acquisitionData.size());

    System.out.println("Removing all records with 0 Impressions.\n");
    acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);
  
    //Data is now aggregated and ready for matching
    

    String[] testDates = {sDate.toString(),eDate.toString()};
    
    System.out.println("Connecting to Google Analytics API for "
        + "Behavior metrics\n");
    System.out.println("Google Analytics API messages below: \n");
    GaData behaviorResults = GACall.main(args,testDates,8);
    System.out.println("\nGoogle Analytics API Request Complete.\n");
    
    //match aggregated acquisition data and behavior data from Google Analytics
    System.out.println("Matching Acquisition Metrics to their respective behavior metrics... ");
    importUtils.matchBehaviorAcq(acquisitionData, behaviorResults);
    System.out.print("Matching Complete.\n");
    
    //Establish database connection
    Connection cnx = null;
    try {
//      cnx = DatabaseUtils.getTestDBConnection();
      cnx = DatabaseUtils.getGoogleCloudDBConnection();
      System.out.println("Database Connection Successful\n");
    } catch (Exception e) {
      System.out.println("There was an error establishing connection to the database");
      System.out.println(e.getMessage());
    }
    
    //execute query
    try{
      updateDCMDD(acquisitionData,cnx, medium);
    } catch (Exception e) {
    System.out.println(e.getMessage());  
    }

  } finally {
    
  }
    
 }
  
  
  
  public static void main(String[] args) {
    
    String[] testArgs =  new String[0] ;
    
    LocalDate startDate = LocalDate.of(2015, 11, 03);
    LocalDate endDate = LocalDate.of(2015, 11, 9);
    
    //Open connection to dropbox API
    DropBoxConnection.initializeDropboxConnection();
    
//    importDCM(testArgs, startDate, endDate, "CrossPlatform");
//    importDCM(testArgs, startDate, endDate, "Mobile");
//    importDCM(testArgs, startDate, endDate, "Preroll");
    //Note this import is for Pandora which although is Audio is treated as display
    importDCM(testArgs, startDate, endDate, "Display");
    
    System.out.println("Testing Completed");
    
  }
}
