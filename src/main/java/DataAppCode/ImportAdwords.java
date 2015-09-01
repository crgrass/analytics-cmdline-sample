package DataAppCode;

import com.google.api.services.analytics.model.GaData;
import com.google.api.services.samples.analytics.cmdline.GACall;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import guiCode.DataAppTest;
import guiCode.OutputMessages;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
      throws SQLException {

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
      Iterator it = importData.entrySet().iterator();
      while (it.hasNext()) {
        updateAdwords = cnxn.prepareStatement(insertQuery);
        Map.Entry pairs = (Map.Entry)it.next();
        //pairs.getValue() is the adwords record
        
        AdwordsRecord currRec = (AdwordsRecord)pairs.getValue();
        
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
    //create set of all unique GroupIDs
    //This will be iterated through for aggregation
    //This should be a function
    //Note this may not be used
    TreeSet<GroupID> groupings = new TreeSet();
    
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
      GroupID currID = new GroupID(source, medium, campaign);

      //return Adwords records with their IDs
      data.put(currID,currRecord);
    } // end of outer loop
    

    return data;
  }//end of loadResults method
  
  
  
  
  
  

	//The date array will be passed in as a command line argument
	public static void main(String[] args) {
	  
		
		guiCode.DataAppTest.outputDisplay.write(OutputMessages.startingVendorImport("Google Adwords"));
		
		/*
		 * GA Data is pulled in below this point
    	 * Note: Adding dimensions or metrics shifts the indexes and can lead to 
    	 * incorrect metrics populating in the object
		 */
		
		//TODO: Incorporate Logging Functionality
		
		//TODO: Have dates passed in
		String startDate = guiCode.DataAppTest.startDate.toString();
		String endDate = guiCode.DataAppTest.endDate.toString();
		String[] dateArray = {startDate,endDate};
		
		System.out.println("Connecting to Google Analytics API for Acquistion"
		    + " and Behavior metrics\n");
		System.out.println("Google Analytics API messages below: \n");
		GaData acquisitionResults = GACall.main(args, dateArray,0);
		System.out.println("\nGoogle Analytics API Request Complete.\n");
		
		int totalResults = acquisitionResults.getTotalResults();
		System.out.println(totalResults + " results returned.");

		
//		System.out.println("This is the adwords acquistion data: " + adwordsAcquisition);
		
		HashMap<GroupID,AdwordsRecord> groupedAdwordsData = loadResults(acquisitionResults, dateArray);
		
		//Adwords data is now grouped and should be needs to be added to the data base
		
		//turn this into a method
		//connectToTestDatabase
		Connection cnx = null;
		
		System.out.println("Connecting to database...\n");
		try {
//		  cnx = DatabaseUtils.getTestDBConnection();
		  cnx = DatabaseUtils.getGoogleCloudTestDBConnection();
		  System.out.println("Database Connection Successful\n");
		} catch (Exception e) {
		  System.out.println("There was an error establishing connection to the database\n");
		  System.out.println(e.getMessage());
		}
		
		try{
		  updateAdwords(groupedAdwordsData,cnx);
		} catch (SQLException e) {
		System.out.println(e.getMessage());
		guiCode.DataAppTest.outputDisplay.write(OutputMessages.errorMessage(e.getMessage()));
		}
		
		

		
		
		
		
//		GACall.printGaData(adwordsAcquisition);
		
		//Each row from ga data is put into it's own adwords object
		//the adwords object is then added to a map where the groupID is the key
		//and an arrayList of Adwords objects is the value
		
		//SecondGACall here for behavior data
		


		guiCode.DataAppTest.outputDisplay.write(OutputMessages.importActivity(DataAppTest.importActivity.toString()));

		DataAppTest.importActivity.reset();
		
		guiCode.DataAppTest.outputDisplay.write(OutputMessages.vendorImportComplete("Google Adwords"));

	}

}
