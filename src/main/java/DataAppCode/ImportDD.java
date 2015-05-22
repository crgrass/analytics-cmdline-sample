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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class ImportDD {
  
  
public static HashMap<GroupID, ArrayList<String[]>> groupRawData(ArrayList<String[]> rawData) {
    
    HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();
    for (String[] row : rawData) {
      //what are the key fields necessary for the groupID
      /*
       * Source: Index: 3 Values: "Collective.com", "MobileFuse.com", "Pandora.com", "Petersons.com","SparkNotes.com","YouTube.com",
       * "BrandExchange.net",
       * Medium: Index : 5 Values: Digital Display (web),Video Display, Mobile Display, Rich Media
       * Campaign: Index: 2 Values: USM001-010
       */
      
      //These mappings convert the string identifying the source, medium and campaign in the Centro Files
      //to the string that matches the corresponding term in Google Analytics
      //Note: As USM utilizes more networks through Centro this list will need to be updated.
      HashMap<String,String> centroSourceMappings = new HashMap<String,String>();
      centroSourceMappings.put("Collective.com","Collective");
      centroSourceMappings.put("MobileFuse.com","MobileFuse");
      centroSourceMappings.put("Pandora.com","Pandora");
      centroSourceMappings.put("Petersons.com","Peterson");
      centroSourceMappings.put("SparkNotes.com","Sparknotes");
      centroSourceMappings.put("YouTube.com","YouTube");
      centroSourceMappings.put("BrandExchange.net","Brand_Exchange");
      
      HashMap<String,String> centroMediumMappings = new HashMap<String,String>();
      centroMediumMappings.put("Digital Display (web)","Display");
      centroMediumMappings.put("Video Display","Preroll");
      centroMediumMappings.put("Mobile Display","Mobile");
      centroMediumMappings.put("Rich media","Rich");
      
      HashMap<String,String> centroCampaignMappings = new HashMap<String,String>();
      centroCampaignMappings.put("USM001","");
      centroCampaignMappings.put("USM002","");
      centroCampaignMappings.put("USM003","");
      centroCampaignMappings.put("USM004","");
      centroCampaignMappings.put("USM005","");
      centroCampaignMappings.put("USM006","FY2015_Undergrad");
      centroCampaignMappings.put("USM007","FY2015_Graduate");
      centroCampaignMappings.put("USM008","FY2015_Degree_Completion");
      centroCampaignMappings.put("USM009","FY2015_Transfer");
      centroCampaignMappings.put("USM010","FY2015_Courses_Fall/Spring");
      
      
      String source = "";
      String medium = ""; 
      String campaign = "";
      
      
      if (centroSourceMappings.containsKey(row[3])) {
        source = centroSourceMappings.get(row[3]);
      } else {
        source = "Source Not Found";
      }

      
      if (centroMediumMappings.containsKey(row[5])) {
        medium = centroMediumMappings.get(row[5]); 
      } else {
        medium = "Medium Not Found: " + row[5];
        System.out.println(medium);
      }
      
      if (centroCampaignMappings.containsKey(row[2])) {
        campaign = centroCampaignMappings.get(row[2]); 
      } else {
        campaign = "Campaign Not Found";
      }
      
      //load GroupID
      GroupID currGroupID = new GroupID(source,medium,campaign);
      
      
      boolean groupIDExists = false;
      //Iterates through hashmap and raised flag if the groupID already exists
      Iterator it = groupedData.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        GroupID iteratedGroupID = (GroupID)pairs.getKey();
        if (iteratedGroupID.equals(currGroupID)) {
          groupIDExists = true;
          //add string once match is identified
          groupedData.get(iteratedGroupID).add(row);
        } //end of if
      }// end of while
      
      //A matching key was not found
      if (!groupIDExists) {
        groupedData.put(currGroupID, new ArrayList<String[]>()); //create the new key value pair with empty array list
        groupedData.get(currGroupID).add(row); //add row
      }//end of if   
      
    }// end of for
    
    return groupedData;
  }//end of group raw data




public static void printGroupedData(HashMap<GroupID, ArrayList<String[]>> grouped) {
  Iterator it = grouped.entrySet().iterator();
  while (it.hasNext()) {
    Map.Entry pairs = (Map.Entry)it.next();
    System.out.println(pairs.getKey()); //this should trigger the too string method
    ArrayList<String[]> val = (ArrayList<String[]>)pairs.getValue();
  }//end of record iteration
}
  
  
  
  /*
   * PreCondition: Raw data is already grouped appropriately
   */
  public static ArrayList<DDRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData) {

    //TODO: all start dates should come from one place
    System.out.println("Aggregating rows based on Source, Network and Campaign...\n");
    
    
    //Iterate through HashMap and place only digial display entries into a new HashMap
    HashMap<GroupID,ArrayList<String[]>> onlyDD = new HashMap<GroupID,ArrayList<String[]>>();
    Iterator itr = rawData.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry pairs = (Map.Entry)itr.next();
      GroupID currID = (GroupID)pairs.getKey();
      ArrayList<String[]> currArray = (ArrayList<String[]>)pairs.getValue();
      if (currID.getMedium().equals("Display")) {
        onlyDD.put(currID, currArray);
      }//end of if
    }//end of while
    
    //The returned ArrayList
    ArrayList<DDRecord> DDRecordCollection = new ArrayList<DDRecord>();

    //Need to loop through Hash Map
    Iterator it = onlyDD.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      ArrayList<String[]> currList = (ArrayList<String[]>)pairs.getValue();

      //Metrics are aggregated here
      Integer totalClicks = 0;
      Integer totalImpressions = 0;
      Float totalSpend = 0.0f;
      Integer totalVisits = 0;
      Integer totalConversions = 0;
      Integer pcConversions = 0;
      Integer piConversions = 0;

      //TODO:Discarding last row as a band aid
      //eventually need to stop importing last row

      for (String[] row : currList) {
        //Index 0 : Start Date, Index 1 : End Date, !!!Index 2 : Campaign!!!
        //!!!Index 3 : Site - This is the source!!!, Index 4 : Placement, !!!Index 5 : Type This is Medium!!!
        //Index 6 : Ad, Index 7 : Size, Index: 8 Clicks
        //Index 9 : Impressions, Index 10 : Views
        //Index 11: CTR, Index 12: Average CPC, Index 13: Average CPM, Index 14: Total Media Cost
        //Index 15: Total Conversions, Index 16: PC Conversions, Index 17: PI Conversions
        totalImpressions += Integer.parseInt(row[9]);
        //TODO: Need to ensure all csvs can handle number formats with commas

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
      String startDate = guiCode.DataAppTest.startDate.toString();
      String endDate = guiCode.DataAppTest.endDate.toString();
      String[] dateArray = {startDate,endDate};
      
      GroupID currID = (GroupID)pairs.getKey();

      DDRecord rec = new DDRecord(dateArray,currID.getSource(),currID.getMedium(),currID.getCampaign(),currID.getSource(),//<- This is network
          totalClicks,totalImpressions,aggCTR,aggCPC,aggCPM,totalSpend, totalConversions,pcConversions,piConversions);
      DDRecordCollection.add(rec);
    }

    return DDRecordCollection;
  }
  
  public static void updateCentroDD(ArrayList<DDRecord> importData, Connection cnxn)
      throws SQLException {

    PreparedStatement updateCentroDD = null;

    String tblName = "DATESTtblDigitalDisplayMetrics";
    //These fields are out of order
    String fields = "(startDate,endDate,source,medium,componentName,clicks,"
        + "impressions,allCTR,averageCPC,averageCPM,spend,totalConversions,pcConversions,piConversions,"
        + "visits,pagesPerVisit,averageDuration,"
        + "percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
          // TODO Auto-generated catch block
          exception.printStackTrace();
        }
        
        java.sql.Date sqlFormatStartDate = new java.sql.Date(sDate.getTime());
        java.sql.Date sqlFormatEndDate = new java.sql.Date(eDate.getTime());
   
        updateCentroDD.setDate(1,sqlFormatStartDate);
        updateCentroDD.setDate(2,sqlFormatEndDate);
        updateCentroDD.setString(3,currRec.getSource());
        updateCentroDD.setString(4,currRec.getMedium());
        updateCentroDD.setString(5,currRec.getCampaign());
        updateCentroDD.setInt(6,currRec.getClicks());
        updateCentroDD.setInt(7,currRec.getImpressions());
        updateCentroDD.setFloat(8,currRec.getCTR());
        //MySQL databases will not accept NaN or Infinity as a float value
        if (currRec.getAvgCPC() == Double.NaN || 
            currRec.getAvgCPC() == Double.POSITIVE_INFINITY) {
          currRec.setAvgCPC(-1.0f);
          System.out.println("Converted special value");
        }
        updateCentroDD.setFloat(9,currRec.getAvgCPC());
        updateCentroDD.setFloat(10, currRec.getAvgCPM());
        updateCentroDD.setFloat(11,currRec.getTotalConversions());
        updateCentroDD.setFloat(12,currRec.getPCConversions());
        updateCentroDD.setFloat(13,currRec.getPIConversions());
        updateCentroDD.setFloat(14,currRec.getSpend());
        updateCentroDD.setInt(15,currRec.getVisits());
        updateCentroDD.setFloat(16,currRec.getPagesPerVisit());
        updateCentroDD.setFloat(17,currRec.getAvgDuration());
        updateCentroDD.setFloat(18,currRec.getPercentNewVisits());
        updateCentroDD.setFloat(19,currRec.getBounceRate());
        updateCentroDD.setBoolean(20,currRec.getPartialWeek());
        updateCentroDD.setInt(21,currRec.getDaysActive());

        updateCentroDD.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }//end of catch

  } // end of update adwords


  public static void main(String[] args) {
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("Centro Digital Display"));
    
    Map<String,String> filePaths = FilePathBuilder.buildFilePathMapDropBox(); //contains all vendors and their respective import directory paths
    ArrayList<String[]> data = null;
    try {
      
      //pull down data, write to file and overwrite any existing files
      try {
         DropBoxConnection.pullCSV("Centro Digital Display");
      } catch (DbxException exception) {
        // TODO Auto-generated catch block
        exception.printStackTrace();
      } catch (IOException exception) {
        // TODO Auto-generated catch block
        exception.printStackTrace();
      }
  
    //Read csv and return raw data
    System.out.println("Reading Centro Display File...\n");
    data = CSVReaders.readCsv("retrievedCentro Digital Display.csv");

    
    CSVReaders.removeHeader(data);
    CSVReaders.removeTail(data); //If data is missing this may be the reason why
    System.out.println("Centro Digital Display File Read Complete.\n");
    
    //Now that we have the raw data in ArrayList<String[]> form
    //We need to group appropriately into a hashmap
    //We will then iterate through the HashMap and aggregate each entry
    
    System.out.println("Grouping Data by Source, Medium and Campaign...\n");
    HashMap<GroupID, ArrayList<String[]>> groupedData = groupRawData(data);
    System.out.println("Grouping Complete.\n");

    
    System.out.println("Aggregating Centro Digital Display Data...\n");
    ArrayList<DDRecord> acquisitionData = aggregate(groupedData);
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
    GaData behaviorResults = GACall.main(args,testDates,2);
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
      updateCentroDD(acquisitionData,cnx);
    } catch (SQLException e) {
    System.out.println(e.getMessage());  
    }
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));
    DataAppTest.importActivity.reset();

    guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("Centro Digital Display"));

  } finally {
    
  }

}
}
