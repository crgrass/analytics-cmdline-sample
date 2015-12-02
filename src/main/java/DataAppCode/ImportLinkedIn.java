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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;




public class ImportLinkedIn {
  
  public static HashMap<GroupID, ArrayList<String[]>> groupRawData(ArrayList<String[]> rawData) {
    
    HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();
    for (String[] row : rawData) {
      //what are the key fields necessary for the groupID
      /*
       * Source: Index: 5 Value: "LinkedIn Ads"
       * Medium: Hardcoded "Social"
       * Campaign: Index: 4 Value: "PDP- Broader Targeting"
       */
      String source = "LinkedIn"; //Hard coded due to this method used only to import LinkedIn
      String medium = "Social"; //Hardcoded due to LinkedIn always being social
      String campaignExpectedValueFY15 = "PDP- Broader Targeting";
      String campaignExpectedValueFY16 = "PDP- Broader Targeting_1";
      String campaign = "";
      String adContent = "(not set)";
      

      if (row[4].equals(campaignExpectedValueFY15)) {
        campaign = "FY2015_LinkedIn";
      } else if (row[4].equals(campaignExpectedValueFY16)) {
        campaign = "FY2016_PDP";
      } else {
        throw new IllegalArgumentException("The value at index 4 (" + row[4] + ")"
            + "  used to determine campaign was not expected.");
      }// end of else
      
      //load GroupID
      //TODO: Replace deprecated method
      GroupID currGroupID = new GroupID(source,medium,campaign,adContent);
      
      boolean groupIDExists = false;
      //Iterates through hashmap and raised flag if the groupID already exists
      Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = groupedData.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<GroupID, ArrayList <String[]>> pairs = it.next();
        GroupID iteratedGroupID = pairs.getKey();
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
  
  public static void updateLinkedIn(ArrayList<LIRecord> importData, Connection cnxn) {

    PreparedStatement updateLinkedIn = null;

    String tblName = "DATESTtblLinkedInMetrics";
    //These fields are out of order
    String fields = "(startDate,endDate,source,medium,componentName,clicks,"
        + "Impressions,CTR,averageCPC,spend,visits,pagesPerVisit,averageDuration,"
        + "percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertQuery = "INSERT INTO " + tblName + fields + " VALUES" + parameters;

    try {
      cnxn.setAutoCommit(false);

      //Need to loop through Hash Map
      for(LIRecord currRec : importData) {
        updateLinkedIn = cnxn.prepareStatement(insertQuery);
        
        
        Date testStart = null;
        Date testEnd = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
          testStart = sdf.parse(currRec.getStartDate());
          testEnd = sdf.parse(currRec.getEndDate());
        } catch (ParseException e) {
          e.printStackTrace();
        } catch (java.text.ParseException exception) {
          // TODO Auto-generated catch block
          exception.printStackTrace();
        }
        
        java.sql.Date sqlStart = new java.sql.Date(testStart.getTime());
        java.sql.Date sqlEnd = new java.sql.Date(testEnd.getTime());
        
   
        updateLinkedIn.setDate(1,sqlStart);
        updateLinkedIn.setDate(2,sqlEnd);
        updateLinkedIn.setString(3,currRec.getSource());
        updateLinkedIn.setString(4,currRec.getMedium());
        updateLinkedIn.setString(5,currRec.getCampaign());
        updateLinkedIn.setInt(6,currRec.getClicks());
        updateLinkedIn.setInt(7,currRec.getImpressions());
        updateLinkedIn.setFloat(8,currRec.getCTR());
        updateLinkedIn.setFloat(9,currRec.getAvgCPC());
        updateLinkedIn.setFloat(10,currRec.getSpend());
        updateLinkedIn.setInt(11,currRec.getVisits());
        updateLinkedIn.setFloat(12,currRec.getPagesPerVisit());
        updateLinkedIn.setFloat(13,currRec.getAvgDuration());
        updateLinkedIn.setFloat(14,currRec.getPercentNewVisits());
        updateLinkedIn.setFloat(15,currRec.getBounceRate());
        updateLinkedIn.setBoolean(16,currRec.getPartialWeek());
        updateLinkedIn.setInt(17,currRec.getDaysActive());

        updateLinkedIn.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }//end of catch

  } // end of update adwords
  
  public static void tryThis(){
    System.out.println("Did this work?");
  }
  


  public static void main(String[] args) {
    DataAppTest.logger.log(Level.INFO,"Importing LinkedIn." + System.lineSeparator());
    
//    guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("LinkedIn"));
    
    //Read csv and return raw data
    //TODO: This filepathBuilder map is built every time a vendor import file is run.
    //This is wasteful and should be created once and then stored for multiple uses.
    
    ArrayList<String[]> data = null;
    try {
      
      //pull down data, write to file and overwrite any existing files
      try {

         DropBoxConnection.pullCSV("LinkedIn", DataAppTest.startDate,DataAppTest.endDate);
      } catch (DbxException exception) {
        exception.printStackTrace();
      }
      
      
      DataAppTest.logger.log(Level.INFO,"Reading LinkedIn File." + System.lineSeparator()); 
      data = CSVReaders.readLICsv("retrievedLinkedIn.csv");
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    
    CSVReaders.removeHeader(data);
    
    //Now that we have the raw data in ArrayList<String[]> form
    //We need to group appropriately into a hashmap
    //We will then iterate through the HashMap and aggregate each entry
    
    DataAppTest.logger.log(Level.INFO,"Grouping Data by Source, Medium and Campaign." + System.lineSeparator()); 
    HashMap<GroupID, ArrayList<String[]>> groupedData = groupRawData(data);
    
    
    DataAppTest.logger.log(Level.INFO,"Aggregating LinkedIn Data." + System.lineSeparator());
    
    //Check start date here to run appropriate aggregate method
    //LinkedIn changed it's data extract format between the end of LinkedIn advertising
    //in FY15 and the start of LinkedInAdvertising in FY16 
    

    ArrayList<LIRecord> acquisitionData;
    if (DataAppTest.startDate.isBefore(LocalDate.of(2015,06,01))) {
      acquisitionData = LIRecord.aggregate(groupedData);
    } else {
      acquisitionData = LIRecord.newAggregate(groupedData);
    }
    
    
    //Now that acquisition data is obtained we need
    //behavior data
    
    String startDate = guiCode.DataAppTest.startDate.toString();
    String endDate = guiCode.DataAppTest.endDate.toString();
    String[] testDates = {startDate,endDate};
    
    
    
    
    DataAppTest.logger.log(Level.INFO,"Connecting to Google Analytics API for "
        + "Behavior metrics." + System.lineSeparator());
    GaData behaviorResults = GACall.main(args,testDates,1);

    

    DataAppTest.logger.log(Level.INFO,"Matching Acquisition Metrics to their respective"
        + " behavior metrics." + System.lineSeparator());
    for (LIRecord currRec : acquisitionData) {
      for (int i = 0 ; i < behaviorResults.getRows().size(); i ++) {
        List<String> currBehaviorRow = behaviorResults.getRows().get(i);
        if (currRec.match(currBehaviorRow)) {
          currRec.setVisits(Integer.parseInt(currBehaviorRow.get(4)));
          currRec.setPagesPerVisit(Float.parseFloat(currBehaviorRow.get(5)));
          currRec.setAvgDuration(Float.parseFloat(currBehaviorRow.get(6)));
          currRec.setPercentNewVisits(Float.parseFloat(currBehaviorRow.get(7)));
          currRec.setBounceRate(Float.parseFloat(currBehaviorRow.get(8)));
        }// end of if
      }//end of inner for
    }//end of outer for
    
    //Now that we have the VORecord fully loaded we will import into the database
    //Establish Connection
    
    DataAppTest.logger.log(Level.INFO,"Connecting to MySQL database." + System.lineSeparator());
    Connection cnx = null;
    try {
      cnx = DatabaseUtils.getGoogleCloudTestDBConnection();
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE,"There was an error establishing connection to the"
          + " database" + System.lineSeparator(), e);
    }
    
    try{
      updateLinkedIn(acquisitionData,cnx);
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE,"There was an error executing the LinkedIn "
          + "Query." + System.lineSeparator(), e);  
    }
    

    DataAppTest.importActivity.reset();



  }

}
