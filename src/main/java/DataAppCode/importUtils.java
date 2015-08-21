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

import guiCode.DataAppTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class importUtils {

  
  /*
   * Remove all records from the ArrayList that have zero impressions. This generic
   * method is designed to handle arrays of VendorObjects. Sometimes external reports
   * will contain placeholder rows for ad units that will run or have run previously.
   * There is no value to storing this information in the database and for this reason
   * they are removed.
   */
  public static <E> ArrayList<E> remove0ImpressionRecords(ArrayList<E> origArray) {
    //loop iterates backwards to avoid issues around index changes on deletion of records
    for (int i = origArray.size() - 1; i >= 0; i--) {
      importRecord currRec = (importRecord)origArray.get(i);
      if (currRec.getImpressions() == 0) {
        origArray.remove(i);
      }//end of if
    }//end of for
    return origArray;
  }//end of remove0ImpressionRecords
  
  
  
  public static <E> void matchBehaviorAcq(ArrayList<E> acquisition ,GaData behavior ) {
    for (E currRec : acquisition) {
      importRecord curr = (importRecord)currRec;
      
      //Check if returned GA query is empty and exit to avoid
      //null pointer exception if so
      if (behavior.getRows() == null) {
        System.out.println("Google Analytics did not return any data in response"
            + " to the query.");
        return;
      }
      
      for (int i = 0 ; i < behavior.getRows().size(); i ++) {
        List<String> currBehaviorRow = behavior.getRows().get(i);
        if (curr.match(currBehaviorRow)) {
            curr.setVisits(Integer.parseInt(currBehaviorRow.get(4)));
            curr.setPagesPerVisit(Float.parseFloat(currBehaviorRow.get(5)));
            curr.setAvgDuration(Float.parseFloat(currBehaviorRow.get(6)));
            curr.setPercentNewVisits(Float.parseFloat(currBehaviorRow.get(7)));
            curr.setBounceRate(Float.parseFloat(currBehaviorRow.get(8)));
        }// end of if
      }//end of inner for
    }//end of outer for
  }
  
  public static <E> void matchTWBehaviorAcq(ArrayList<E> acquisition ,GaData behavior ) {
    for (E currRec : acquisition) {
      importRecord curr = (importRecord)currRec;
      
      //Check if returned GA query is empty and exit to avoid
      //null pointer exception if so
      if (behavior.getRows() == null) {
        System.out.println("Google Analytics did not return any data in response"
            + " to the query.");
        return;
      }
      
      for (int i = 0 ; i < behavior.getRows().size(); i ++) {
        List<String> currBehaviorRow = behavior.getRows().get(i);
        if (curr.match(currBehaviorRow)) {
            curr.setVisits(Integer.parseInt(currBehaviorRow.get(3)));
            curr.setPagesPerVisit(Float.parseFloat(currBehaviorRow.get(4)));
            curr.setAvgDuration(Float.parseFloat(currBehaviorRow.get(5)));
            curr.setPercentNewVisits(Float.parseFloat(currBehaviorRow.get(6)));
            curr.setBounceRate(Float.parseFloat(currBehaviorRow.get(7)));
        }// end of if
      }//end of inner for
    }//end of outer for
  }
  

  /*
   * These methods can eventually be turned multi-purpose by passing in the
   * source, medium and campaign mappings as a parameter
   */
  public static HashMap<GroupID, ArrayList<String[]>> groupCentroRawData(ArrayList<String[]> rawData, LocalDate sDate) {
    
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
      centroSourceMappings.put("Spotify.com", "Spotify");
      centroSourceMappings.put("Hulu.com", "Hulu");
      
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
      //Summer is typically run in the same campaign as individual courses
      //use date to determine which campaign to use.
      LocalDate summerCampStart = LocalDate.of(2015, Month.FEBRUARY, 1);
      if (sDate.compareTo(summerCampStart) > 0) {
        centroCampaignMappings.put("USM010","FY2015_Courses_Summer");
      } else {
        centroCampaignMappings.put("USM010","FY2015_Fall/Spring");
      }
      
      //USM011 is run through Centro by Rinck but USM Marketing is not responsible
      //for this campaign
      centroCampaignMappings.put("USM011","ERROR_FY2015_Law");
      centroCampaignMappings.put("USM012","FY2015_Umbrella");
      /*
       * USM012 is actually Umbrella
       */
      
      HashMap<String,String> centroAdContentMappings = new HashMap<String,String>();
      centroAdContentMappings.put("time","Time_Is_Now");
      centroAdContentMappings.put("summer","(not set)");
      centroAdContentMappings.put("tour","Campus_Tour");
      centroAdContentMappings.put("misc","Unknown");
      centroAdContentMappings.put("scholarship", "Scholarship");
      
      
      String source;
      String medium; 
      String campaign;
      String adContent;
      
      
      if (centroSourceMappings.containsKey(row[3])) {
        source = centroSourceMappings.get(row[3]);
      } else {
        source = "Source Not Found";
        System.out.println("Source not found for: " + row[3]);
      }

      
      if (centroMediumMappings.containsKey(row[5])) {
        medium = centroMediumMappings.get(row[5]); 
      } else {
        medium = "Medium Not Found";
        System.out.println("Source not found for: " + row[5]);
      }
      
      if (centroCampaignMappings.containsKey(row[2])) {
        campaign = centroCampaignMappings.get(row[2]);
      } else {
        campaign = "Campaign Not Found";
        System.out.println("Source not found for: " + row[2]);
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
      } else if (row[6].contains("Tracking")) {
        adContent = "Tracking Pixel";
      } else if (row[6].contains("GAME")) {
        adContent = "scholarship";
      }
        else {
        adContent = "misc";
      }
      
      //TODO: Since this is DD Tracking pixel wouldn't ap
      if (adContent.equals("Tracking Pixel")) {
        if (campaign.equals("FY2015_Courses_Summer")) {
          adContent = "summer";
        } else if (campaign.equals("FY2015_Umbrella")) {
          adContent = "scholarship";
        } else {
          adContent = "time";
        }
      }
      
      //if key is not dictionary a null value is returned?
      //this can raise a null pointer exception
      adContent = centroAdContentMappings.get(adContent);
      
      //load GroupID
      GroupID currGroupID = new GroupID(source,medium,campaign, adContent);
      
      
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




/*
 * FACEBOOK SPECIFIC
 */


public static HashMap<GroupID, ArrayList<String[]>> groupFacebookRawData(ArrayList<String[]> rawData) {
  
  HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();
  for (String[] row : rawData) {
    //TODO: Update this to include relevant fields
    
    //what are the key fields necessary for the groupID
    /*
     * Source: Hardcoded
     * Medium: Hardcoded
     * Campaign: Index: 3 Values: Campaign Naming Conventions
     * Placement: Index: 4 Values: News Feed, Right Column
     * Device: Index: 4 Values: Mobile, Desktop
     */
    
    //These mappings convert the string identifying the campaign and placement in the Centro Files
    //to the string that matches the corresponding term in Google Analytics    
    HashMap<String,String> facebookCampaignMappings = new HashMap<String,String>();
    facebookCampaignMappings.put("fy15_lg_fb_disp_hs_ug","FY2015_Undergrad");
    facebookCampaignMappings.put("fy15_lg_fb_disp_wa_gr","FY2015_Graduate");// these are not tracked separately
    facebookCampaignMappings.put("fy15_lg_fb_disp_cu_gr","FY2015_Graduate");// 
    facebookCampaignMappings.put("fy15_lg_fb_disp_wa_dc","FY2015_Degree_Completion");
    facebookCampaignMappings.put("fy15_lg_fb_disp_cu_tt","FY2015_Transfer");
    facebookCampaignMappings.put("fy15_lg_fb_disp_wa_ic","FY2015_Courses_Fall/Spring");
    facebookCampaignMappings.put("fy15_lg_fb_disp_hs_ug_tour1","FY2015_CampusTourTest1");
    facebookCampaignMappings.put("fy15_lg_fb_disp_cu_sic","FY2015_Courses_Summer");//these are not tracked separately
    facebookCampaignMappings.put("fy15_lg_fb_disp_wa_sic","FY2015_Courses_Summer");
    
   
    HashMap<String,String> facebookPlacementMappings = new HashMap<String,String>();
    facebookPlacementMappings.put("Right Column","Right_Rail");
    facebookPlacementMappings.put("News Feed","Newsfeed");
    
    HashMap<String,String> facebookAdContentMappings = new HashMap<String,String>();
    facebookAdContentMappings.put("Time","Time_Is_Now");
    facebookAdContentMappings.put("Tour","Campus_Tour");
    facebookAdContentMappings.put("Summer","Time_Is_Now");
    facebookAdContentMappings.put("Summer_Working_Adults", "Working_Adults");
    facebookAdContentMappings.put("Summer_Undergraduate", "Undergraduate");
    facebookAdContentMappings.put("WA_Time_Is_Now", "WA_Time_Is_Now");
    facebookAdContentMappings.put("UG_Time_Is_Now", "UG_Time_Is_Now");
    
    
    
    String source = "Facebook";
    String medium = ""; 
    String campaign = "";
    String adContent;
    String placement = "";
    
    if (facebookCampaignMappings.containsKey(row[4])) {
      campaign = facebookCampaignMappings.get(row[4]); 
    } else {
      campaign = "Campaign Not Found";
      System.out.println("Campaign for \"" + row[4] +"\" could not be identified");
    }
    
    if (row[1].contains("Right Column")) {
      placement = "Right_Rail";
      medium = "Right_Rail";
    } else if (row[1].contains("News Feed")) {
      placement = "Newsfeed";
      medium = "Newsfeed";
    } else {
      System.out.println("Placement for \"" + row[1] +"\" could not be identified");
    }
    
    
    
    //determine what which key is needed to access to correct adContent value
    if (row[0].contains("Tour")) {
      adContent = "Tour";
    } else if (row[0].contains("wa_gr_Time")) {
      adContent = "WA_Time_Is_Now";
    } else if (row[0].contains("cu_gr_Time")) {
      adContent = "UG_Time_Is_Now";
    } else if (row[0].contains("Time")) {
      adContent = "Time";
    } else if (row[0].contains("wa_sic_Newsfeed_Summer")) {
      adContent = "Summer_Working_Adults";
    } else if (row[0].contains("cu_sic_Newsfeed_Summer") ||row[0].contains("cu_sic_Mobile_Newsfeed_Summer")) {
      adContent = "Summer_Undergraduate";
    } else if(row[0].contains("RightRail_Summer")) {
      adContent = "Summer_Undergraduate";
      medium = "Right_Rail";
      placement = "Right_Rail";
    } else {
      adContent = "not found";
    }
    
    //get the matching ga value from the dictionary
    adContent = facebookAdContentMappings.get(adContent);
    
    //The campus tour campaign does not follow the typical
    //convention have containing either Facebook or Right
    //Rail as a medium so this needs to be adjusted based
    // on ad content
    if (adContent.equals("Campus_Tour")) {
      medium = "Social";
    }
    
    //load GroupID
    GroupID currGroupID = new GroupID(source,medium,campaign,adContent,placement);
    
    
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

public static HashMap<GroupID, ArrayList<String[]>> groupTwitterRawData(ArrayList<String[]> rawData) {
  
  HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();
  for (String[] row : rawData) {

    //what are the key fields necessary for the groupID
    /*
     * Source: Hardcoded
     * Medium: Hardcoded
     * Campaign: Index: 3 Values: Campaign Naming Conventions
     * Placement: Index: 4 Values: News Feed, Right Column
     * Device: Index: 4 Values: Mobile, Desktop
     */
    
    //These mappings convert the string identifying the campaign and placement in the Centro Files
    //to the string that matches the corresponding term in Google Analytics    
    HashMap<String,String> twitterCampaignMappings = new HashMap<String,String>();
    twitterCampaignMappings.put("USM High School Undergrad-Additional Spend ","FY2015_Undergrad");
    twitterCampaignMappings.put("USM High School Undergrad","FY2015_Undergrad");
    twitterCampaignMappings.put("USM Graduate","FY2015_Graduate");// these are not tracked separately
    twitterCampaignMappings.put("Copy of USM Graduate","FY2015_Graduate");// 
    twitterCampaignMappings.put("Degree Completer","FY2015_Degree_Completion");
    twitterCampaignMappings.put("UG_Transfer","FY2015_Transfer");
    twitterCampaignMappings.put("UG_Transfer_Additional Spend","FY2015_Transfer");
    twitterCampaignMappings.put("USM Summer Courses Working Adult","FY2015_Courses_Summer");
    twitterCampaignMappings.put("USM Summer Courses Undergrad","FY2015_Courses_Summer");
    
    
    String source = "Twitter";
    String medium = "Social"; 
    String campaign = "";
    
    if (twitterCampaignMappings.containsKey(row[1])) {
      campaign = twitterCampaignMappings.get(row[1]); 
    } else {
      campaign = "Campaign Not Found";
      System.out.println("Campaign for \"" + row[1] +"\" could not be identified");
    }
    
    //load GroupID
    GroupID currGroupID = new GroupID(source,medium,campaign);
    
    
    boolean groupIDExists = false;
    //Iterates through hashmap and raised flag if the groupID already exists
    Iterator it = groupedData.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      GroupID iteratedGroupID = (GroupID)pairs.getKey();
      if (iteratedGroupID.twitterEquals(currGroupID)) {
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
  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
