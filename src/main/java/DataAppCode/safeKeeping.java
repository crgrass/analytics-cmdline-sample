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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */


/*
 * The correctDate method generates a map which contains
 * the relevant indexes that contain the dates in each
 * vendors import file.
 * 
 * Method originally located in CSVReaders
 */
public static boolean correctDate(String filePath) {
  Map<String,Integer[]> dateIndexes = new HashMap<String,Integer[]>();
  //If there are two integers in the array this indicates that both the
  //start and end dates are relevant
  dateIndexes.put("Google Adwords", new Integer[] {0,0});
  
  dateIndexes.put("Centro Digital Display", new Integer[] {0,1});
  
  dateIndexes.put("Centro Mobile Display", new Integer[] {0,1});
  
  dateIndexes.put("Centro Video Display", new Integer[] {0,1});
  
  dateIndexes.put("Centro Rich Media", new Integer[] {0,1});
  
  dateIndexes.put("Facebook", new Integer[] {0,1});
  
  dateIndexes.put("Twitter", new Integer[] {5});
  
  dateIndexes.put("LinkedIn", new Integer[] {0});
  
  return true;
}



@Deprecated // Replaced by GroupCentroRawData in importUtils package 
public static HashMap<GroupID, ArrayList<String[]>> groupRawData(ArrayList<String[]> rawData) {
  /*
   * Build the dictionaries that convert the string identifying the source, medium, campaign and ad
   * content to the exact string that will match with the values from the ad units data in Google
   * Analytics. 
   * 
   * Note: This list will need to be updated as new campaigns are launched.
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
  centroCampaignMappings.put("USM010","FY2015_Courses_Summer");
  
  HashMap<String,String> centroAdContentMappings = new HashMap<String,String>();
  centroAdContentMappings.put("time","Time_Is_Now");
  centroAdContentMappings.put("summer","(not set)");
  centroAdContentMappings.put("tour","Campus_Tour");
  centroAdContentMappings.put("misc","Unknown");


  
  
  //Dictionary that will house all grouped data
  HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();

  for (String[] row : rawData) {
    //what are the key fields necessary for the groupID
    /*
     * Source: Index: 3 Values: "Collective.com", "MobileFuse.com", "Pandora.com", "Petersons.com","SparkNotes.com","YouTube.com",
     * "BrandExchange.net",
     * Medium: Index : 5 Values: Digital Display (web),Video Display, Mobile Display, Rich Media
     * Campaign: Index: 2 Values: USM001-010
     * Ad Content: Index:6
     */

    String source;
    String medium; 
    String campaign;
    String adContent;


    //Assign value to source
    if (centroSourceMappings.containsKey(row[3])) {
      source = centroSourceMappings.get(row[3]);
    } else {
      source = "Source Not Found" + row[3];
    }

    //Assign value to medium
    if (centroMediumMappings.containsKey(row[5])) {
      medium = centroMediumMappings.get(row[5]); 
    } else {
      medium = "Medium Not Found: " + row[5];
    }

    //Assign value to campaign
    if (centroCampaignMappings.containsKey(row[2])) {
      campaign = centroCampaignMappings.get(row[2]); 
    } else {
      campaign = "Campaign Not Found" + row[2];
    }
    
    

    
    //Assign value to adContent based on whether the value contains the
    //key within it. This is different then source, medium and campaign as
    //it is not looking for an exact match.
    if (row[6].contains("time")) {
      adContent = "time";
    } else if (row[6].contains("tour")) {
      adContent = "tour";
    } else if (campaign.equals("FY2015_Courses_Summer")) { //ad content not set for Summer DD
      adContent = "summer";
    } else if (row[6].equals("Tracking Pixel")) {
      adContent = "Tracking Pixel";
    } else {
      adContent = "misc";
    }
    
    
    

    if (adContent.equals("Tracking Pixel")) {
      if (campaign.equals("FY2015_Courses_Summer")) {
        adContent = "summer";
      } else {
        adContent = "time";
      }
    }
    
    
    //adContent parameter for Summer YouTube is not set
    //adContent parameter for FY2015_Undergrad YouTube is Time_Is_Now
    
    //if key is not dictionary a null value is returned?
    //this can raise a null pointer exception
    adContent = centroAdContentMappings.get(adContent);
    

    //load GroupID
    GroupID currGroupID = new GroupID(source,medium,campaign, adContent);
    
    



    boolean groupIDExists = false;
    //Iterates through hashmap and raises flag if the groupID already exists
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = groupedData.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<GroupID,ArrayList<String[]>> pairs = it.next();
      GroupID iteratedGroupID = pairs.getKey();
      if (iteratedGroupID.equals(currGroupID)) {
        groupIDExists = true;
        //add string once match is identified
        groupedData.get(iteratedGroupID).add(row);
      } //end of if
    }// end of while

    //A matching groupID was not found and a new key must be created
    if (!groupIDExists) {
      groupedData.put(currGroupID, new ArrayList<String[]>()); //create the new key value pair with empty array list
      groupedData.get(currGroupID).add(row); //add row
    }//end of if   

  }// end of for

  return groupedData;
}//end of group raw data
public class safeKeeping {

}
