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
public class ImportDCM {


  /*
   * @param grouped - The data structure HashMap to be printed.
   * printGroupedData is a convenience method used to print out the
   * data structure passed as a parameter for debugging.
   */
  public static void printGroupedData(HashMap<GroupID, ArrayList<String[]>> grouped) {
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = grouped.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = it.next();
      ArrayList<String[]> val = pairs.getValue();
      System.out.println(pairs.getKey() + " : " + val.toString());
    }//end of record iteration
  }



  /*
   * PreCondition: Raw data is already grouped appropriately
   */
  public static ArrayList<DDRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData, LocalDate sDate,
      LocalDate eDate, String medium) {

    DataAppTest.logger.log(Level.INFO,"Aggregating rows based on Source, Network, Campaign and AdContent." +
        System.lineSeparator());


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

      for (String[] row : currList) {
        totalImpressions += Integer.parseInt(row[9]);
        totalClicks += Integer.parseInt(row[8]);
        totalSpend += Float.parseFloat(row[14]);
      }// end of outer loop

      Float aggCTR = (float)totalClicks/(float)totalImpressions;
      Float aggCPC = totalSpend/(float)totalClicks;
      Float kImpressions = (float)totalImpressions/1000;
      Float aggCPM = totalSpend/kImpressions;

      //Dates need to come from one common source
      String[] dateArray = {sDate.toString(),eDate.toString()};

      GroupID currID = pairs.getKey();

      DDRecord rec = new DDRecord(dateArray,currID.getSource(),currID.getMedium(),currID.getCampaign(),currID.getSource(),//<- This is network
          currID.getAdContent(),totalClicks,totalImpressions,aggCTR,aggCPC,aggCPM,totalSpend, totalConversions,pcConversions,piConversions);
      DDRecordCollection.add(rec);
    }

    return DDRecordCollection;
  }

  /*
   * @param importData - The arrayList of DDRecords that needs to be imported.
   * @param cnxn - The connection object used to connect to the database
   * @param medium - The medium being imported. This parameter is used to identify the
   * appropriate database to store the data.
   * 
   * updateDCMDD identifies the appropriate database for import, builds the correct update query
   * for each row and executes the query.
   */
  public static void updateDCMDD(ArrayList<DDRecord> importData, Connection cnxn, String medium) {

    PreparedStatement updateDCMDD = null;

    String tblName = "";

    //checks medium parameter to determine appropriate database for storing data.
    if (medium.equals("CrossPlatform") || medium.equals("Display")) {
      tblName = "DATESTtblDigitalDisplayMetrics";
    } else if (medium.equals( "Preroll")) {
      tblName = "DATESTtblVideoMetrics";
    } else if (medium.equals("Mobile")) {
      tblName = "DATESTtblMobileMetrics";
    } else {
      DataAppTest.logger.log(Level.INFO, "The correct table for " + medium + " could not be identified" +
          System.lineSeparator());
    }


    //Fields in database of which to load data.
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
          DataAppTest.logger.log(Level.SEVERE,e.getMessage() + System.lineSeparator());
        } catch (java.text.ParseException e) {
          DataAppTest.logger.log(Level.SEVERE,e.getMessage() + System.lineSeparator());
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
        }
        updateDCMDD.setFloat(9,currRec.getCTR());



        //MySQL databases will not accept NaN or Infinity as a float value
        if (Double.isNaN(currRec.getAvgCPC())|| 
            currRec.getAvgCPC() == Double.POSITIVE_INFINITY) {
          currRec.setAvgCPC(-1.0f);
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
      DataAppTest.logger.log(Level.SEVERE,e.getMessage() + System.lineSeparator());
    }//end of catch

  } // end of update adwords


  /*
   * @param: String[] args - Serves no purpose but exists as this method was build off of a main method
   * @param: LocalDate sDate - The startDate for the import. This value is called from a static variable in
   * the data app test method.
   * @param: LocalDate eDate - The endDate for the import. This value is called from a static variable in
   * the data app test method.
   * @param: String medium - This parameter indicates which medium the method will import. Possible values
   * are CrossPlatform, Mobile, Preroll and Display. Display really only encompasses Pandora.
   */
  public static void importDCM(String[] args, LocalDate sDate, LocalDate eDate, String medium) {

    ArrayList<String[]> data = null;
    try {

      //pull down data from Dropbox, write to file and overwrite any data files
      try {
        DropBoxConnection.pullCSV("DoubleClick", sDate, eDate);
      } catch (DbxException e) {
        DataAppTest.logger.log(Level.SEVERE,e.getMessage() + System.lineSeparator());
      } catch (IOException e) {
        DataAppTest.logger.log(Level.SEVERE,e.getMessage() + System.lineSeparator());
      }

      //Read csv generated from Dropbox and return raw data
      DataAppTest.logger.log(Level.INFO,"Reading Double Click Display File." + System.lineSeparator());
      data = CSVReaders.readCsv("retrievedDoubleClick.csv");

      //Remove header and footer and filter remove any dates that do not fall within the 
      //sDate and eDate parameters.
      CSVReaders.formatDCMData(data);
      CSVReaders.removeInvalidDates(data, "DoubleClick", sDate);


      //Now that we have the raw data in ArrayList<String[]> form
      //We need to group appropriately into a hashmap
      //We will then iterate through the HashMap and aggregate each entry

      HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupDCMRawData(data);


      DataAppTest.logger.log(Level.INFO,"Aggregating DoubleClick Digital Display Data." + System.lineSeparator());
      ArrayList<DDRecord> acquisitionData = aggregate(groupedData, sDate, eDate, medium);

      DataAppTest.logger.log(Level.INFO,"The number of " + medium + " records for import is: " +acquisitionData.size() 
          + System.lineSeparator());

      DataAppTest.logger.log(Level.INFO,"Removing all records with 0 Impressions." + System.lineSeparator());
      acquisitionData = importUtils.remove0ImpressionRecords(acquisitionData);

      //Data is now aggregated and ready for matching
      String[] testDates = {sDate.toString(),eDate.toString()};

      DataAppTest.logger.log(Level.INFO,"Connecting to Google Analytics API for "
          + "Behavior metrics." + System.lineSeparator());

      DataAppTest.logger.log(Level.INFO,"Google Analytics API messages below: " + System.lineSeparator());
      GaData behaviorResults = GACall.main(args,testDates,8);

      //match aggregated acquisition data and behavior data from Google Analytics
      DataAppTest.logger.log(Level.INFO,"Matching Acquisition Metrics to their respective behavior metrics." + System.lineSeparator());
      importUtils.matchBehaviorAcq(acquisitionData, behaviorResults);

      //Establish database connection
      Connection cnx = null;
      try {
        cnx = DatabaseUtils.getGoogleCloudDBConnection();
        DataAppTest.logger.log(Level.INFO,"Database Connection Successful." + System.lineSeparator());
      } catch (Exception e) {
        DataAppTest.logger.log(Level.INFO,"There was an error establishing connection to the database." + System.lineSeparator());
        DataAppTest.logger.log(Level.SEVERE,e.getMessage() + System.lineSeparator());
      }

      //execute query
      try{
        updateDCMDD(acquisitionData,cnx, medium);
      } catch (Exception e) {
        DataAppTest.logger.log(Level.INFO,"There was an error executing the DoubleClick query." + System.lineSeparator());
        DataAppTest.logger.log(Level.SEVERE,e.getMessage() + System.lineSeparator());
      }

    } finally {

    }

  }



  /*
   * Testing method.
   */
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
