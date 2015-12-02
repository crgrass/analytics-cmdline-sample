package DataAppCode;

import com.google.api.services.analytics.model.GaData;
import com.google.api.services.samples.analytics.cmdline.GACall;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import guiCode.DataAppTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.PrintStream;
import java.sql.*;

public class ImportAdwords {
  
  public static void printResults(GaData results) {
    //rows is the key that holds relevant data
        for (List<String> row : results.getRows()) {
          for (String column : row) {
            System.out.print(column + " ");
          }
          System.out.println();
        }
      
    }

  
  public static void updateAdwords(HashMap<GroupID, AdwordsRecord> importData, Connection cnxn)
     {

    PreparedStatement updateAdwords = null;

    String tblName = "DATESTtblSEMMetricsAdGroups";
    String fields = "(startDate,endDate,source,medium,campaign,adGroup,Clicks,"
        + "Impressions,CTR,avgCPC,spend,Visits,pagesPerVisit,averageDuration,"
        + "percentNewVisits,bounceRate,partialWeek,daysActive)";
    String parameters = "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertQuery = "INSERT INTO " + tblName + fields + " VALUES" + parameters;

    try {
      cnxn.setAutoCommit(false);

      //Need to loop through Hash Map
      Iterator<Map.Entry<GroupID,AdwordsRecord>> it = importData.entrySet().iterator();
      while (it.hasNext()) {
        updateAdwords = cnxn.prepareStatement(insertQuery);
        Map.Entry<GroupID,AdwordsRecord> pairs = it.next();
        
        AdwordsRecord currRec = pairs.getValue();
        
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
        
        
        updateAdwords.setDate(1,sqlFormatStartDate);
        updateAdwords.setDate(2,sqlFormatEndDate);
        updateAdwords.setString(3,currRec.getSource());
        updateAdwords.setString(4,currRec.getMedium());
        updateAdwords.setString(5,currRec.getCampaign());
        updateAdwords.setString(6,currRec.getAdGroup());
        updateAdwords.setInt(7,currRec.getClicks());
        updateAdwords.setInt(8,currRec.getImpressions());
        updateAdwords.setFloat(9,currRec.getCTR());
        updateAdwords.setFloat(10,currRec.getAvgCPC());
        updateAdwords.setFloat(11,currRec.getSpend());
        updateAdwords.setInt(12,currRec.getVisits());
        updateAdwords.setFloat(13,currRec.getPagesPerVisit());
        updateAdwords.setFloat(14,currRec.getAvgDuration());
        updateAdwords.setFloat(15,currRec.getPercentNewVisits());
        updateAdwords.setFloat(16,currRec.getBounceRate());
        updateAdwords.setBoolean(17,currRec.getPartialWeek());
        updateAdwords.setInt(18,currRec.getDaysActive());

        updateAdwords.executeUpdate();
        cnxn.commit();
      }//end of loop

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }//end of catch

  } // end of update adwords
  
  
  public static HashMap<GroupID,AdwordsRecord> loadResults(GaData results, String[] dates) {    
    //Create map to store data
    HashMap<GroupID,AdwordsRecord> data = new HashMap<GroupID,AdwordsRecord>();
    
    
    //TODO: This step is not necessary but does make the code more readable.
    //values could be loaded directly from the "row" into the constructor.
    for (List<String> row : results.getRows()) {
      String source = row.get(0);
      String medium = row.get(1);
      String campaign = row.get(2);
      String adGroup = row.get(3);
      Integer clicks = Integer.parseInt(row.get(4));
      Integer impressions = Integer.parseInt(row.get(5));
      Float CTR = Float.parseFloat(row.get(6));
      Float avgCPC = Float.parseFloat(row.get(7));
      Float spend = Float.parseFloat(row.get(8));
      Integer visits = Integer.parseInt(row.get(9));
      Float pagesPerVisit = Float.parseFloat(row.get(10));
      Float avgDuration = Float.parseFloat(row.get(11));
      Float percentNewVisits = Float.parseFloat(row.get(12));
      Float bounceRate = Float.parseFloat(row.get(13));
      
      // Create Adwords record and ID object
      AdwordsRecord currRecord = new AdwordsRecord(dates,source, medium,campaign,
          adGroup, clicks, impressions, CTR, avgCPC,spend,visits,pagesPerVisit,
          avgDuration,percentNewVisits,bounceRate);
      //TODO: Use non-deprecated method
      GroupID currID = new GroupID(source, medium, campaign);

      //return Adwords records with their IDs
      data.put(currID,currRecord);
    } // end of outer loop
    

    return data;
  }//end of loadResults method
  
  
  
  
  
  

  //The date array will be passed in as a command line argument
  public static void main(String[] args) {
    DataAppTest.logger.log(Level.INFO, "Beginning Adwords Import." + System.lineSeparator());




    /*
     * GA Data is pulled in below this point
     * Note: Adding dimensions or metrics shifts the indexes and can lead to 
     * incorrect metrics populating in the object
     */

    //TODO: Pass these dates in as parameters. Once dates are pulled from parameters
    //the following three lines can be reduced to one
    String startDate = guiCode.DataAppTest.startDate.toString();
    String endDate = guiCode.DataAppTest.endDate.toString();
    String[] dateArray = {startDate,endDate};

    
    DataAppTest.logger.log(Level.INFO, "Connecting to Google Analytics API for Acquistion"
        + " and Behavior metrics." + System.lineSeparator());
    GaData acquisitionResults = GACall.main(args, dateArray,0);
    DataAppTest.logger.log(Level.INFO, "Google Analytics API Request Complete." + System.lineSeparator());

    int totalResults = acquisitionResults.getTotalResults();
    
    DataAppTest.logger.log(Level.INFO, totalResults + " results returned." + System.lineSeparator());

    HashMap<GroupID,AdwordsRecord> groupedAdwordsData = loadResults(acquisitionResults, dateArray);

    //Adwords data is now grouped and should be needs to be added to the data base


    /*
     * Connect to database and execute queries
     */
    //TODO: Provide the actual database name and table name
    DataAppTest.logger.log(Level.INFO,"Connecting to MySQL Database." + System.lineSeparator());
    Connection cnx = null;
    
    try {
      cnx = DatabaseUtils.getGoogleCloudDBConnection();
      DataAppTest.logger.log(Level.INFO,"Adwords Database Connection Successful." + System.lineSeparator());
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE, "There was an error establishing connection"
          + " to the database.\n", e);
    }

    try {
      updateAdwords(groupedAdwordsData,cnx);
    } catch (Exception e) {
      DataAppTest.logger.log(Level.SEVERE, "Error executing Adwords DB Query." 
    + System.lineSeparator(), e);
    }

    //Each row from ga data is put into it's own adwords object
    //the adwords object is then added to a map where the groupID is the key
    //and an arrayList of Adwords objects is the value

    //TODO: Determine what this does and document
    DataAppTest.importActivity.reset();
    
    DataAppTest.logger.log(Level.INFO, "Adwords Import is Complete." + System.lineSeparator());

  }

}
