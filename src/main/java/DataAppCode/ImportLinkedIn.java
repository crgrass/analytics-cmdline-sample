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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;




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
      String campaignExpectedValue = "PDP- Broader Targeting";
      String campaign = "";
      
      if (row[4].equals(campaignExpectedValue)) {
        campaign = "FY2015_LinkedIn";
      } else {
        throw new IllegalArgumentException("The value at index 4 (" + row[4] + ")"
            + "  used to determine campaign was not expected.");
      }// end of else
      
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
  
  public static void updateLinkedIn(ArrayList<LIRecord> importData, Connection cnxn)
      throws SQLException {

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
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("LinkedIn"));
    
    //Read csv and return raw data
    //TODO: This filepathBuilder map is built every time a vendor import file is run.
    //This is wasteful and should be created once and then stored for multiple uses.
    
    
    Map<String,String> filePaths = FilePathBuilder.buildFilePathMap(); //contains all vendors and their respective import directory paths
    ArrayList<String[]> data = null;
    try {
      System.out.println("Reading LinkedIn File...\n");
      data = CSVReaders.readLICsv(filePaths.get("LinkedIn"));
      System.out.println("LinkedIn Read Complete.\n");
    } catch (IOException exception) {
      // TODO Auto-generated catch block
      exception.printStackTrace();
    }
    CSVReaders.removeHeader(data);
    CSVReaders.removeTail(data);
    
    //Now that we have the raw data in ArrayList<String[]> form
    //We need to group appropriately into a hashmap
    //We will then iterate through the HashMap and aggregate each entry
    
    System.out.println("Grouping Data by Source, Medium and Campaign...\n");
    HashMap<GroupID, ArrayList<String[]>> groupedData = groupRawData(data);
    System.out.println("Grouping Complete.\n");
    
    
    System.out.println("Aggregating LinkedIn Data...\n");
    ArrayList<LIRecord> acquisitionData = LIRecord.aggregate(groupedData);
    System.out.println("Aggregation Complete.\n");
    
    //Now that acquisition data is obtained we need
    //behavior data
    
    String startDate = guiCode.DataAppTest.startDate.toString();
    String endDate = guiCode.DataAppTest.endDate.toString();
    String[] testDates = {startDate,endDate};
    
    System.out.println("Connecting to Google Analytics API for "
        + "Behavior metrics\n");
    System.out.println("Google Analytics API messages below: \n");
    GaData behaviorResults = GACall.main(args,testDates,1);
    System.out.println("\nGoogle Analytics API Request Complete.\n");

    
    //when refactoring use the import Utils generic method
    System.out.println("Matching Acquisition Metrics to their respective behavior metrics...\n");
    for (LIRecord currRec : acquisitionData) {
      for (int i = 0 ; i < behaviorResults.getRows().size(); i ++) {
        List<String> currBehaviorRow = behaviorResults.getRows().get(i);
        if (currRec.match(currBehaviorRow)) {
          currRec.setVisits(Integer.parseInt(currBehaviorRow.get(3)));
          currRec.setPagesPerVisit(Float.parseFloat(currBehaviorRow.get(4)));
          currRec.setAvgDuration(Float.parseFloat(currBehaviorRow.get(5)));
          currRec.setPercentNewVisits(Float.parseFloat(currBehaviorRow.get(6)));
          currRec.setBounceRate(Float.parseFloat(currBehaviorRow.get(7)));
        }// end of if
      }//end of inner for
    }//end of outer for
    System.out.println("Matching Complete.\n");
    
    //Now that we have the VORecord fully loaded we will import into the database
    //Establish Connection
    Connection cnx = null;
    try {
//      cnx = DatabaseUtils.getTestDBConnection();
      cnx = DatabaseUtils.getGoogleCloudTestDBConnection();
      System.out.println("Database Connection Successful\n");
    } catch (Exception e) {
      System.out.println("There was an error establishing connection to the database");
      e.printStackTrace();
    }
    
    try{
      updateLinkedIn(acquisitionData,cnx);
    } catch (SQLException e) {
    System.out.println(e.getMessage());  
    }
    
    guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));

    DataAppTest.importActivity.reset();


    guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("LinkedIn"));

  }

}
